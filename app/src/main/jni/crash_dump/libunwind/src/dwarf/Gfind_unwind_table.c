/* libunwind - a platform-independent unwind library
   Copyright (C) 2003-2004 Hewlett-Packard Co
	Contributed by David Mosberger-Tang <davidm@hpl.hp.com>

This file is part of libunwind.

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.  */

#include <fcntl.h>
#include <string.h>
#include <unistd.h>

#include <sys/mman.h>

#include "libunwind_i.h"
#include "dwarf-eh.h"
#include "dwarf_i.h"

static bool get_dyn_gp(struct elf_image* ei, Elf_W(Off) dyn_phdr_offset, unw_word_t* gp) {
  Elf_W(Phdr) phdr;
  GET_PHDR_FIELD(ei, dyn_phdr_offset, &phdr, p_offset);
  Elf_W(Dyn) dyn;
  Elf_W(Off) dyn_offset = phdr.p_offset;
  unw_word_t map_size = ei->u.memory.end - ei->u.memory.start;
  while (dyn_offset + sizeof(dyn) < map_size) {
    GET_DYN_FIELD(ei, dyn_offset, &dyn, d_tag);
    if (dyn.d_tag == DT_NULL) {
      break;
    }
    if (dyn.d_tag == DT_PLTGOT) {
      // Assume that _DYNAMIC is writable and GLIBC has
      // relocated it (true for x86 at least).
      GET_DYN_FIELD(ei, dyn_offset, &dyn, d_un.d_ptr);
      *gp = dyn.d_un.d_ptr;
      return true;
    }
    dyn_offset += sizeof(dyn);
  }
  Debug(1, "DT_PLTGOT not found in dynamic header\n");
  return false;
}

static bool get_eh_frame_info(
    struct elf_image* ei, unw_word_t phdr_offset, unw_word_t load_base, unw_dyn_info_t* di_cache) {
  Elf_W(Phdr) phdr;
  GET_PHDR_FIELD(ei, phdr_offset, &phdr, p_offset);
  unw_word_t hdr_offset = phdr.p_offset;
  struct dwarf_eh_frame_hdr hdr;
  // Read the entire hdr since we are going to use every value in the struct.
  if (sizeof(hdr) != elf_w (memory_read) (ei, ei->u.memory.start + phdr.p_offset,
                            (uint8_t*) &hdr, sizeof(hdr), false)) {
    Debug(1, "Failed to read dwarf_eh_frame_hdr from in memory elf image.\n");
    return false;
  }

  if (hdr.version != DW_EH_VERSION) {
    Debug (1, "table has unexpected version %d\n", hdr.version);
    return false;
  }

  // Fill in a dummy proc_info structure.  We just need to fill in
  // enough to ensure that dwarf_read_encoded_pointer() can do its
  // job.  Since we don't have a procedure-context at this point, all
  // we have to do is fill in the global-pointer.
  unw_proc_info_t pi;
  memset (&pi, 0, sizeof (pi));
  pi.gp = di_cache->gp;

  unw_accessors_t* a = unw_get_accessors (ei->u.memory.as);
  unw_word_t addr = (unw_word_t) (uintptr_t) (hdr_offset + sizeof(struct dwarf_eh_frame_hdr));
  addr += ei->u.memory.start;

  unw_word_t eh_frame_start;
  if (dwarf_read_encoded_pointer (ei->u.memory.as, a, &addr, hdr.eh_frame_ptr_enc, &pi,
                                  &eh_frame_start, ei->u.memory.as_arg) < 0) {
    Debug(1, "Failed to read encoded frame start.\n");
    return false;
  }

  unw_word_t fde_count;
  if (dwarf_read_encoded_pointer (ei->u.memory.as, a, &addr, hdr.fde_count_enc, &pi,
                                  &fde_count, ei->u.memory.as_arg) < 0) {
    Debug(1, "Failed to read fde count.\n");
    return false;
  }

  if (hdr.table_enc != (DW_EH_PE_datarel | DW_EH_PE_sdata4)) {
    // Unsupported table format.
    Debug(1, "Unsupported header table format %d\n", hdr.table_enc);
    return false;
  }

  di_cache->u.rti.name_ptr = 0;
  // two 32-bit values (ip_offset/fde_offset) per table-entry:
  di_cache->u.rti.table_len = (fde_count * 8) / sizeof (unw_word_t);

  GET_PHDR_FIELD(ei, phdr_offset, &phdr, p_vaddr);
  GET_PHDR_FIELD(ei, phdr_offset, &phdr, p_offset);
  di_cache->u.rti.table_data =
      load_base + phdr.p_vaddr + addr - (uintptr_t) ei->u.memory.start - phdr.p_offset;

  // For the binary-search table in the eh_frame_hdr, data-relative
  // means relative to the start of that section...
  di_cache->u.rti.segbase = ((load_base + phdr.p_vaddr) + (hdr_offset - phdr.p_offset));

  return true;
}

static bool dwarf_find_unwind_table_memory (
    struct elf_dyn_info *edi, struct elf_image *ei, unw_addr_space_t as, char *path,
    unw_word_t segbase, unw_word_t mapoff, unw_word_t ip) {
  Elf_W(Ehdr) ehdr;
  GET_EHDR_FIELD(ei, &ehdr, e_phoff, false);
  GET_EHDR_FIELD(ei, &ehdr, e_phnum, false);

  Elf_W(Off) offset = ehdr.e_phoff;
  Elf_W(Off) txt_phdr_offset = 0;
  Elf_W(Addr) txt_pvaddr = 0;
  Elf_W(Off) dyn_phdr_offset = 0;
#if UNW_TARGET_ARM
  Elf_W(Off) arm_exidx_phdr_offset = 0;
#endif
  int i;
  unw_word_t start_ip = (unw_word_t) -1;
  unw_word_t end_ip = 0;
  Elf_W(Off) eh_frame_phdr_offset = 0;
  for (i = 0; i < ehdr.e_phnum; ++i) {
    Elf_W(Phdr) phdr;
    GET_PHDR_FIELD(ei, offset, &phdr, p_type);
    switch (phdr.p_type) {
      case PT_LOAD:
        GET_PHDR_FIELD(ei, offset, &phdr, p_vaddr);
        if (phdr.p_vaddr < start_ip) {
          start_ip = phdr.p_vaddr;
        }

        GET_PHDR_FIELD(ei, offset, &phdr, p_memsz);
        if (phdr.p_vaddr + phdr.p_memsz > end_ip) {
          end_ip = phdr.p_vaddr + phdr.p_memsz;
        }

        GET_PHDR_FIELD(ei, offset, &phdr, p_offset);
        if (phdr.p_offset == mapoff) {
          txt_phdr_offset = offset;
          txt_pvaddr = phdr.p_vaddr;
        }
        break;

      case PT_GNU_EH_FRAME:
        eh_frame_phdr_offset = offset;
        break;

      case PT_DYNAMIC:
        dyn_phdr_offset = offset;
        break;

#if UNW_TARGET_ARM
      case PT_ARM_EXIDX:
        arm_exidx_phdr_offset = offset;
        break;
#endif

      default:
        break;
    }
    offset += sizeof(phdr);
  }

  if (txt_phdr_offset == 0) {
    Debug(1, "PT_LOAD section not found.\n");
    return false;
  }

  unw_word_t load_base = segbase - txt_pvaddr;
  start_ip += load_base;
  end_ip += load_base;

  bool found = false;
  if (eh_frame_phdr_offset) {
    // For dynamicly linked executables and shared libraries,
    // DT_PLTGOT is the value that data-relative addresses are
    // relative to for that object.  We call this the "gp".
    // Otherwise this is a static executable with no _DYNAMIC.  Assume
    // that data-relative addresses are relative to 0, i.e.,
    // absolute.
    edi->di_cache.gp = 0;
    if (dyn_phdr_offset) {
      // Ignore failures, we'll attempt to keep going with a zero gp.
      get_dyn_gp(ei, dyn_phdr_offset, &edi->di_cache.gp);
    }

    found = get_eh_frame_info(ei, eh_frame_phdr_offset, load_base, &edi->di_cache);
    if (found) {
      edi->di_cache.start_ip = start_ip;
      edi->di_cache.end_ip = end_ip;
      edi->di_cache.format = UNW_INFO_FORMAT_REMOTE_TABLE;
    }
  }

#if UNW_TARGET_ARM
  // Verify that the map contains enough space for the arm unwind data.
  if (arm_exidx_phdr_offset &&
    arm_exidx_phdr_offset + sizeof(Elf_W(Phdr)) < ei->u.memory.end - ei->u.memory.start) {
    Elf_W(Phdr) phdr;
    GET_PHDR_FIELD(ei, arm_exidx_phdr_offset, &phdr, p_vaddr);
    GET_PHDR_FIELD(ei, arm_exidx_phdr_offset, &phdr, p_memsz);
    edi->di_arm.u.rti.table_data = load_base + phdr.p_vaddr;
    edi->di_arm.u.rti.table_len = phdr.p_memsz;

    edi->di_arm.format = UNW_INFO_FORMAT_ARM_EXIDX;
    edi->di_arm.start_ip = start_ip;
    edi->di_arm.end_ip = end_ip;
    edi->di_arm.u.rti.name_ptr = (unw_word_t) path;
    found = true;
  }
#endif

  return found;
}

int
dwarf_find_unwind_table (struct elf_dyn_info *edi, struct elf_image *ei,
			 unw_addr_space_t as, char *path,
			 unw_word_t segbase, unw_word_t mapoff, unw_word_t ip)
{
  Elf_W(Phdr) *phdr, *ptxt = NULL, *peh_hdr = NULL, *pdyn = NULL;
  unw_word_t addr, eh_frame_start, fde_count, load_base;
#if 0
  // Not currently used.
  unw_word_t max_load_addr = 0;
#endif
  unw_word_t start_ip = (unw_word_t) -1;
  unw_word_t end_ip = 0;
  struct dwarf_eh_frame_hdr *hdr;
  unw_proc_info_t pi;
  unw_accessors_t *a;
  Elf_W(Ehdr) *ehdr;
#if UNW_TARGET_ARM
  const Elf_W(Phdr) *parm_exidx = NULL;
#endif
  int i, ret, found = 0;

  /* XXX: Much of this code is Linux/LSB-specific.  */

  if (!ei->valid)
    return -UNW_ENOINFO;

  if (!ei->mapped) {
    if (dwarf_find_unwind_table_memory (edi, ei, as, path, segbase, mapoff, ip)) {
      return 1;
    }
    return -UNW_ENOINFO;
  }

  /* ANDROID support update. */
  ehdr = ei->u.mapped.image;
  phdr = (Elf_W(Phdr) *) ((char *) ei->u.mapped.image + ehdr->e_phoff);
  /* End of ANDROID update. */

  for (i = 0; i < ehdr->e_phnum; ++i)
    {
      switch (phdr[i].p_type)
	{
	case PT_LOAD:
	  if (phdr[i].p_vaddr < start_ip)
	    start_ip = phdr[i].p_vaddr;

	  if (phdr[i].p_vaddr + phdr[i].p_memsz > end_ip)
	    end_ip = phdr[i].p_vaddr + phdr[i].p_memsz;

	  if (phdr[i].p_offset == mapoff)
	    ptxt = phdr + i;

#if 0
	  // Not currently used.
	  if ((uintptr_t) ei->u.mapped.image + phdr->p_filesz > max_load_addr)
	    max_load_addr = (uintptr_t) ei->u.mapped.image + phdr->p_filesz;
#endif
	  break;

	case PT_GNU_EH_FRAME:
	  peh_hdr = phdr + i;
	  break;

	case PT_DYNAMIC:
	  pdyn = phdr + i;
	  break;

#if UNW_TARGET_ARM
	case PT_ARM_EXIDX:
	  parm_exidx = phdr + i;
	  break;
#endif

	default:
	  break;
	}
    }

  if (!ptxt)
    return 0;

  load_base = segbase - ptxt->p_vaddr;
  start_ip += load_base;
  end_ip += load_base;

  if (peh_hdr)
    {
      // For dynamicly linked executables and shared libraries,
      // DT_PLTGOT is the value that data-relative addresses are
      // relative to for that object.  We call this the "gp".
      // Otherwise this is a static executable with no _DYNAMIC.  Assume
      // that data-relative addresses are relative to 0, i.e.,
      // absolute.
      edi->di_cache.gp = 0;
      if (pdyn) {
        Elf_W(Dyn) *dyn = (Elf_W(Dyn) *)(pdyn->p_offset + (char *) ei->u.mapped.image);
        while ((char*) dyn - (char*) ei->u.mapped.image + sizeof(Elf_W(Dyn)) < ei->u.mapped.size
               && dyn->d_tag != DT_NULL) {
          if (dyn->d_tag == DT_PLTGOT) {
            // Assume that _DYNAMIC is writable and GLIBC has
            // relocated it (true for x86 at least).
            edi->di_cache.gp = dyn->d_un.d_ptr;
            break;
          }
          dyn++;
        }
      }

      /* ANDROID support update. */
      hdr = (struct dwarf_eh_frame_hdr *) (peh_hdr->p_offset
					   + (char *) ei->u.mapped.image);
      /* End of ANDROID update. */
      if (hdr->version != DW_EH_VERSION)
	{
	  Debug (1, "table `%s' has unexpected version %d\n",
		 path, hdr->version);
	  return -UNW_ENOINFO;
	}

      a = unw_get_accessors (unw_local_addr_space);
      /* ANDROID support update. */
      addr = (unw_word_t) (uintptr_t) (hdr + 1);
      /* End of ANDROID update. */

      /* Fill in a dummy proc_info structure.  We just need to fill in
	 enough to ensure that dwarf_read_encoded_pointer() can do its
	 job.  Since we don't have a procedure-context at this point, all
	 we have to do is fill in the global-pointer.  */
      memset (&pi, 0, sizeof (pi));
      pi.gp = edi->di_cache.gp;

      if ((ret = dwarf_read_encoded_pointer (unw_local_addr_space, a,
					     &addr, hdr->eh_frame_ptr_enc, &pi,
					     &eh_frame_start, NULL)) < 0)
	return -UNW_ENOINFO;

      if ((ret = dwarf_read_encoded_pointer (unw_local_addr_space, a,
					     &addr, hdr->fde_count_enc, &pi,
					     &fde_count, NULL)) < 0)
	return -UNW_ENOINFO;

      if (hdr->table_enc != (DW_EH_PE_datarel | DW_EH_PE_sdata4))
	{
#if 1
          // Right now do nothing.
	  //abort ();
#else
	  unw_word_t eh_frame_end;

	  /* If there is no search table or it has an unsupported
	     encoding, fall back on linear search.  */
	  if (hdr->table_enc == DW_EH_PE_omit)
	    Debug (4, "EH lacks search table; doing linear search\n");
	  else
	    Debug (4, "EH table has encoding 0x%x; doing linear search\n",
		   hdr->table_enc);

	  eh_frame_end = max_load_addr;	/* XXX can we do better? */

	  if (hdr->fde_count_enc == DW_EH_PE_omit)
	    fde_count = ~0UL;
	  if (hdr->eh_frame_ptr_enc == DW_EH_PE_omit)
	    abort ();

	  return linear_search (unw_local_addr_space, ip,
				eh_frame_start, eh_frame_end, fde_count,
				pi, need_unwind_info, NULL);
#endif
	}
      else
        {
          edi->di_cache.start_ip = start_ip;
          edi->di_cache.end_ip = end_ip;
          edi->di_cache.format = UNW_INFO_FORMAT_REMOTE_TABLE;
          edi->di_cache.u.rti.name_ptr = 0;
          /* two 32-bit values (ip_offset/fde_offset) per table-entry: */
          edi->di_cache.u.rti.table_len = (fde_count * 8) / sizeof (unw_word_t);
          /* ANDROID support update. */
          edi->di_cache.u.rti.table_data = ((load_base + peh_hdr->p_vaddr)
                                           + (addr - (uintptr_t) ei->u.mapped.image
                                           - peh_hdr->p_offset));
          /* End of ANDROID update. */

          /* For the binary-search table in the eh_frame_hdr, data-relative
             means relative to the start of that section... */

          /* ANDROID support update. */
          edi->di_cache.u.rti.segbase = ((load_base + peh_hdr->p_vaddr)
                                        + ((uintptr_t) hdr - (uintptr_t) ei->u.mapped.image
                                        - peh_hdr->p_offset));
          /* End of ANDROID update. */
          found = 1;
        }
    }

#if UNW_TARGET_ARM
  if (parm_exidx)
    {
      edi->di_arm.format = UNW_INFO_FORMAT_ARM_EXIDX;
      edi->di_arm.start_ip = start_ip;
      edi->di_arm.end_ip = end_ip;
      edi->di_arm.u.rti.name_ptr = (unw_word_t) path;
      edi->di_arm.u.rti.table_data = load_base + parm_exidx->p_vaddr;
      edi->di_arm.u.rti.table_len = parm_exidx->p_memsz;
      found = 1;
    }
#endif

#ifdef CONFIG_DEBUG_FRAME
  /* Try .debug_frame. */
  found = dwarf_find_debug_frame (found, &edi->di_debug, ip, load_base, path,
				  start_ip, end_ip);
#endif

  return found;
}
