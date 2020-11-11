/* libunwind - a platform-independent unwind library
   Copyright (C) 2014 The Android Open Source Project

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


#include "libunwind_i.h"
#include "map_info.h"

extern int local_get_elf_image (unw_addr_space_t as, struct elf_image *,
                                unw_word_t, unsigned long *, unsigned long *,
                                char **, void *);

PROTECTED int
tdep_get_elf_image (unw_addr_space_t as, struct elf_image *ei,
                    pid_t pid, unw_word_t ip,
                    unsigned long *segbase, unsigned long *mapoff, char **path,
                    void *as_arg)
{
  struct map_info *map;

  if (pid == getpid())
    return local_get_elf_image (as, ei, ip, segbase, mapoff, path, as_arg);

  map = map_find_from_addr (as->map_list, ip);
  if (!map)
    return -UNW_ENOINFO;

  if (!elf_map_cached_image (as, as_arg, map, ip, false))
    return -UNW_ENOINFO;

  *ei = map->ei;
  *segbase = map->start;
  if (ei->mapped)
    *mapoff = map->offset;
  else
    /* Always use zero as the map offset for in memory maps. The
     * dlopen of a shared library from an APK will result in a
     * non-zero offset so it won't match the elf data and cause
     * unwinds to fail. Currently, only in memory unwinds of an APK
     * are possible, so only modify this path.
     */
    *mapoff = 0;

  if (path != NULL)
    {
      *path = strdup (map->path);
    }
  return 0;
}
