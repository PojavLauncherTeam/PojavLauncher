/* libunwind - a platform-independent unwind library
   Copyright (C) 2003-2005 Hewlett-Packard Co
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

#include <limits.h>
#include <stdio.h>

#include "libunwind_i.h"
#include "libunwind-ptrace.h"
#include "map_info.h"
#include "os-linux.h"

/* ANDROID support update. */
HIDDEN struct map_info *
map_create_list (int map_create_type, pid_t pid)
{
  struct map_iterator mi;
  unsigned long start, end, offset, flags;
  struct map_info *map_list = NULL;
  struct map_info *cur_map;
  unw_addr_space_t as = NULL;
  struct unw_addr_space local_as;
  void* as_arg = NULL;

  if (maps_init (&mi, pid) < 0)
    return NULL;

  while (maps_next (&mi, &start, &end, &offset, &flags))
    {
      cur_map = map_alloc_info ();
      if (cur_map == MAP_FAILED)
        break;
      cur_map->next = map_list;
      cur_map->start = start;
      cur_map->end = end;
      cur_map->offset = offset;
      cur_map->load_base = 0;
      cur_map->flags = flags;
      cur_map->path = strdup (mi.path);
      mutex_init (&cur_map->ei_lock);
      cur_map->ei.valid = false;
      cur_map->ei.load_attempted = false;
      cur_map->ei.mapped = false;
      cur_map->ei.mini_debug_info_data = NULL;
      cur_map->ei.mini_debug_info_size = 0;

      /* Indicate mapped memory of devices is special and should not
         be read or written. Use a special flag instead of zeroing the
         flags to indicate that the maps do not need to be rebuilt if
         any values ever wind up in these special maps.
         /dev/ashmem/... maps are special and don't have any restrictions,
         so don't mark them as device memory.  */
      if (strncmp ("/dev/", cur_map->path, 5) == 0
          && strncmp ("ashmem/", cur_map->path + 5, 7) != 0)
        cur_map->flags |= MAP_FLAGS_DEVICE_MEM;

      /* If this is a readable executable map, and not a stack map or an
         empty map, find the load_base.  */
      if (cur_map->path[0] != '\0' && strncmp ("[stack:", cur_map->path, 7) != 0
          && (flags & (PROT_EXEC | PROT_READ)) == (PROT_EXEC | PROT_READ)
          && !(cur_map->flags & MAP_FLAGS_DEVICE_MEM))
        {
          struct elf_image ei;
          // Do not map elf for local unwinds, it's faster to read
          // from memory directly.
          if (map_create_type == UNW_MAP_CREATE_REMOTE
              && elf_map_image (&ei, cur_map->path))
            {
              unw_word_t load_base;
              if (elf_w (get_load_base) (&ei, offset, &load_base))
                cur_map->load_base = load_base;
              munmap (ei.u.mapped.image, ei.u.mapped.size);
            }
          else
            {
              // Create an address space right here with enough initialized
              // to read data.
              if (as == NULL)
                {
                  if (map_create_type == UNW_MAP_CREATE_LOCAL)
                    {
                      as = &local_as;
                      unw_local_access_addr_space_init (as);
                    }
                  else
                    {
                      // For a remote unwind, create the address space
                      // and arg data the first time we need it.
                      // We'll reuse these values if we need to attempt
                      // to get elf data for another map.
                      as = unw_create_addr_space (&_UPT_accessors, 0);
                      if (as)
                        {
                          as_arg = (void*) _UPT_create (pid);
                          if (!as_arg)
                            {
                              unw_destroy_addr_space (as);
                              as = NULL;
                            }
                        }
                    }
                }
              if (as)
                {
                  ei.mapped = false;
                  ei.u.memory.start = cur_map->start;
                  ei.u.memory.end = cur_map->end;
                  ei.u.memory.as = as;
                  ei.u.memory.as_arg = as_arg;
                  ei.valid = elf_w (valid_object_memory) (&ei);
                  unw_word_t load_base;
                  if (ei.valid && elf_w (get_load_base) (&ei, cur_map->offset, &load_base))
                    cur_map->load_base = load_base;
                }
            }
        }

      map_list = cur_map;
    }

  maps_close (&mi);

  if (as && map_create_type == UNW_MAP_CREATE_REMOTE)
    {
      unw_destroy_addr_space (as);
      _UPT_destroy (as_arg);
    }

  return map_list;
}
/* End of ANDROID update. */
