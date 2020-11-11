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

#include <dlfcn.h>
#include <string.h>
#include <unistd.h>

#include "libunwind_i.h"

#include "elf64.h"

/* ANDROID support update. */
extern struct map_info *local_map_list;
HIDDEN define_lock(os_map_lock);

HIDDEN struct map_info *
maps_create_list (pid_t pid)
{
  return NULL;
}

PROTECTED int
tdep_get_elf_image (unw_addr_space_t as, struct elf_image **ei,
                    pid_t pid, unw_word_t ip,
                    unsigned long *segbase, unsigned long *mapoff, char **path)
{
  struct load_module_desc lmd;
  const char *path;

  if (pid != getpid ())
    {
      printf ("%s: remote case not implemented yet\n", __FUNCTION__);
      return -UNW_ENOINFO;
    }

  /* First check to see if this ip is in our cache. */
  map = map_find_from_addr(as->map_list, ip);
  if (map)
    goto finish;

  /* Lock while we update the list. */
  lock_acquire (&os_map_lock, saved_mask);

  /* Check again if ip is in the map. */
  map = map_find_from_addr(as->map_list, ip);
  if (map)
    goto release_lock;

  /* Not in the cache, try and find the data. */
  if (!dlmodinfo (ip, &lmd, sizeof (lmd), NULL, 0, 0))
    goto release_lock;

  path = dlgetname (&lmd, sizeof (lmd), NULL, 0, 0);
  if (!path)
    goto release_lock;

  map = mempool_alloc (&map_pool);
  if (!map)
    goto release_lock;

  map->start = lmd.text_base;
  map->end = cur_map->start + lmd.text_size;
  map->offset = 0;			/* XXX fix me? */
  map->flags = ;
  map->path = strdup(path2);
  mutex_init (&cur_map->ei_lock);
  map->ei.size = 0;
  map->ei.image = NULL;
  map->ei_shared = 0;
  Debug(1, "segbase=%lx, mapoff=%lx, path=%s\n", map->start, map->offset, map->path);

  if (elf_map_cached_image (map, ip) < 0)
    {
      free(map);
      map = NULL;
    }
  else
    {
      /* Add this element into list in descending order by start. */
      struct map_info *map_list = as->map_list;
      if (as->map_list == NULL || map->start > as->map_list->start)
        {
          map->next = as->map_list;
          as->map_list = map;
        }
      else
        {
          while (map_list->next != NULL && map->start <= map_list->next->start)
            map_list = map_list->next;
          map->next = map_list->next;
          map_list->next = map;
        }
    }
release_lock:
  lock_release (&os_map_lock, saved_mask);

finish:
  if (map)
    {
      *ei = &map->ei;
      *segbase = map->start;
      *mapoff = map->offset;
      if (path != NULL)
        {
          *path = strdup (map->path);
        }
    }
  return 0;
}

PROTECTED int
maps_is_local_readable(struct map_info *map_list, unw_word_t addr)
{
  return 1;
}

PROTECTED int
maps_is_local_writable(struct map_info *map_list, unw_word_t addr)
{
  return 1;
}
/* End of ANDROID update. */
