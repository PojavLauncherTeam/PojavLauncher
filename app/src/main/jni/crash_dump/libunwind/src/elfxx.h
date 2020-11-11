/* libunwind - a platform-independent unwind library
   Copyright (C) 2003, 2005 Hewlett-Packard Co
   Copyright (C) 2007 David Mosberger-Tang
	Contributed by David Mosberger-Tang <dmosberger@gmail.com>

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
#include <stddef.h>
#include <unistd.h>

#include <sys/mman.h>
#include <sys/stat.h>

#include "libunwind_i.h"
#include "map_info.h"

#if ELF_CLASS == ELFCLASS32
# define ELF_W(x)	ELF32_##x
# define Elf_W(x)	Elf32_##x
# define elf_w(x)	_Uelf32_##x
#else
# define ELF_W(x)	ELF64_##x
# define Elf_W(x)	Elf64_##x
# define elf_w(x)	_Uelf64_##x
#endif

#define GET_FIELD(ei, offset, struct_name, elf_struct, field, check_cached) \
  { \
    if (!check_cached || (elf_struct)->field == 0) { \
      if (sizeof((elf_struct)->field) != elf_w (memory_read) ( \
          ei, ei->u.memory.start + offset + offsetof(struct_name, field), \
          (uint8_t*) &((elf_struct)->field), sizeof((elf_struct)->field), false)) { \
        return false; \
      } \
    } \
  }

#define GET_EHDR_FIELD(ei, ehdr, field, check_cached) \
  GET_FIELD(ei, 0, Elf_W(Ehdr), ehdr, field, check_cached)

#define GET_PHDR_FIELD(ei, offset, phdr, field) \
  GET_FIELD(ei, offset, Elf_W(Phdr), phdr, field, false)

#define GET_SHDR_FIELD(ei, offset, shdr, field) \
  GET_FIELD(ei, offset, Elf_W(Shdr), shdr, field, false)

#define GET_SYM_FIELD(ei, offset, sym, field) \
  GET_FIELD(ei, offset, Elf_W(Sym), sym, field, false)

#define GET_DYN_FIELD(ei, offset, dyn, field) \
  GET_FIELD(ei, offset, Elf_W(Dyn), dyn, field, false)

extern bool elf_w (get_proc_name) (
    unw_addr_space_t as, pid_t pid, unw_word_t ip, char* buf, size_t len,
    unw_word_t* offp, void* as_arg);

extern bool elf_w (get_proc_name_in_image) (
    unw_addr_space_t as, struct elf_image* ei, unsigned long segbase,
    unsigned long mapoff, unw_word_t ip, char* buf, size_t buf_len, unw_word_t* offp);

extern bool elf_w (get_load_base) (struct elf_image* ei, unw_word_t mapoff, unw_word_t* load_base);

extern size_t elf_w (memory_read) (
    struct elf_image* ei, unw_word_t addr, uint8_t* buffer, size_t bytes, bool string_read);

extern bool elf_w (xz_decompress) (uint8_t* src, size_t src_size,
                                   uint8_t** dst, size_t* dst_size);

extern bool elf_w (find_section_mapped) (struct elf_image *ei, const char* name,
                                         uint8_t** section, size_t* size, Elf_W(Addr)* vaddr);

static inline bool elf_w (valid_object_mapped) (struct elf_image* ei) {
  if (ei->u.mapped.size <= EI_VERSION) {
    return false;
  }

  uint8_t* e_ident = (uint8_t*) ei->u.mapped.image;
  return (memcmp (ei->u.mapped.image, ELFMAG, SELFMAG) == 0
          && e_ident[EI_CLASS] == ELF_CLASS && e_ident[EI_VERSION] != EV_NONE
          && e_ident[EI_VERSION] <= EV_CURRENT);
}

static inline bool elf_w (valid_object_memory) (struct elf_image* ei) {
  uint8_t e_ident[EI_NIDENT];
  uintptr_t start = ei->u.memory.start;
  if (SELFMAG != elf_w (memory_read) (ei, start, e_ident, SELFMAG, false)) {
    return false;
  }
  if (memcmp (e_ident, ELFMAG, SELFMAG) != 0) {
    return false;
  }
  // Read the rest of the ident data.
  if (EI_NIDENT - SELFMAG != elf_w (memory_read) (
      ei, start + SELFMAG, e_ident + SELFMAG, EI_NIDENT - SELFMAG, false)) {
    return false;
  }
  return e_ident[EI_CLASS] == ELF_CLASS && e_ident[EI_VERSION] != EV_NONE
         && e_ident[EI_VERSION] <= EV_CURRENT;
}

static inline bool elf_map_image (struct elf_image* ei, const char* path) {
  struct stat stat;
  int fd;

  fd = open (path, O_RDONLY);
  if (fd < 0) {
    return false;
  }

  if (fstat (fd, &stat) == -1) {
    close (fd);
    return false;
  }

  ei->u.mapped.size = stat.st_size;
  ei->u.mapped.image = mmap (NULL, ei->u.mapped.size, PROT_READ, MAP_PRIVATE, fd, 0);
  close (fd);
  if (ei->u.mapped.image == MAP_FAILED) {
    return false;
  }

  ei->valid = elf_w (valid_object_mapped) (ei);
  if (!ei->valid) {
    munmap (ei->u.mapped.image, ei->u.mapped.size);
    return false;
  }

  ei->mapped = true;
  // Set to true for cases where this is called outside of elf_map_cached.
  ei->load_attempted = true;

  return true;
}

static inline bool elf_map_cached_image (
    unw_addr_space_t as, void* as_arg, struct map_info* map, unw_word_t ip,
    bool local_unwind) {
  intrmask_t saved_mask;

  // Don't even try and cache this unless the map is readable and executable.
  if ((map->flags & (PROT_READ | PROT_EXEC)) != (PROT_READ | PROT_EXEC)) {
    return false;
  }

  // Do not try and cache the map if it's a file from /dev/ that is not
  // /dev/ashmem/.
  if (map->path != NULL && strncmp ("/dev/", map->path, 5) == 0
      && strncmp ("ashmem/", map->path + 5, 7) != 0) {
    return false;
  }

  // Lock while loading the cached elf image.
  lock_acquire (&map->ei_lock, saved_mask);
  if (!map->ei.load_attempted) {
    map->ei.load_attempted = true;

    if (!elf_map_image (&map->ei, map->path)) {
      // If the image cannot be loaded, we'll read data directly from
      // the process using the access_mem function.
      if (map->flags & PROT_READ) {
        map->ei.u.memory.start = map->start;
        map->ei.u.memory.end = map->end;
        map->ei.u.memory.as = as;
        map->ei.u.memory.as_arg = as_arg;
        map->ei.valid = elf_w (valid_object_memory) (&map->ei);
      }
    } else if (!local_unwind) {
      // Do not process the compressed section for local unwinds.
      // Uncompressing this section can consume a large amount of memory
      // and cause the unwind to take longer, which can cause problems
      // when an ANR occurs in the system. Compressed sections are
      // only used to contain java stack trace information. Since ART is
      // one of the only ways that a local trace is done, and it already
      // dumps the java stack, this information is redundant.

      // Try to cache the minidebuginfo data.
      uint8_t *compressed = NULL;
      size_t compressed_len;
      if (elf_w (find_section_mapped) (&map->ei, ".gnu_debugdata", &compressed,
          &compressed_len, NULL)) {
        if (elf_w (xz_decompress) (compressed, compressed_len,
            (uint8_t**) &map->ei.mini_debug_info_data, &map->ei.mini_debug_info_size)) {
          Debug (1, "Decompressed and cached .gnu_debugdata");
        } else {
          map->ei.mini_debug_info_data = NULL;
          map->ei.mini_debug_info_size = 0;
        }
      }
    }
    unw_word_t load_base;
    if (map->ei.valid && elf_w (get_load_base) (&map->ei, map->offset, &load_base)) {
      map->load_base = load_base;
    }
  } else if (map->ei.valid && !map->ei.mapped && map->ei.u.memory.as != as) {
    // If this map is only in memory, this might be a cached map
    // that crosses over multiple unwinds. In this case, we've detected
    // that the as is stale, so set it to a valid as.
    map->ei.u.memory.as = as;
  }
  lock_release (&map->ei_lock, saved_mask);
  return map->ei.valid;
}
