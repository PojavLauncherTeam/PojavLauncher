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

#define UNW_LOCAL_ONLY
#include <libunwind.h>
#include "libunwind_i.h"

/* Globals to hold the map data for local unwinds. */
HIDDEN struct map_info *local_map_list = NULL;
HIDDEN int local_map_list_refs = 0;
HIDDEN lock_rdwr_var (local_rdwr_lock);

PROTECTED void
unw_map_local_cursor_get (unw_map_cursor_t *map_cursor)
{
  intrmask_t saved_mask;

  /* This function can be called before any other unwind code, so make sure
     the lock has been initialized.  */
  map_local_init ();

  lock_rdwr_wr_acquire (&local_rdwr_lock, saved_mask);
  map_cursor->map_list = local_map_list;
  map_cursor->cur_map = local_map_list;
  lock_rdwr_release (&local_rdwr_lock, saved_mask);
}

PROTECTED int
unw_map_local_cursor_valid (unw_map_cursor_t *map_cursor)
{
  if (map_cursor->map_list == local_map_list)
    return 0;
  return -1;
}

PROTECTED int
unw_map_local_create (void)
{
  intrmask_t saved_mask;
  int ret_value = 0;

  /* This function can be called before any other unwind code, so make sure
     the lock has been initialized.  */
  map_local_init ();

  lock_rdwr_wr_acquire (&local_rdwr_lock, saved_mask);
  if (local_map_list_refs == 0)
    {
      local_map_list = map_create_list (UNW_MAP_CREATE_LOCAL, getpid());
      if (local_map_list != NULL)
        local_map_list_refs = 1;
      else
        ret_value = -1;
    }
  else
    local_map_list_refs++;
  lock_rdwr_release (&local_rdwr_lock, saved_mask);
  return ret_value;
}

PROTECTED void
unw_map_local_destroy (void)
{
  intrmask_t saved_mask;

  /* This function can be called before any other unwind code, so make sure
     the lock has been initialized.  */
  map_local_init ();

  lock_rdwr_wr_acquire (&local_rdwr_lock, saved_mask);
  if (local_map_list != NULL && --local_map_list_refs == 0)
    {
      map_destroy_list (local_map_list);
      local_map_list = NULL;
    }
  lock_rdwr_release (&local_rdwr_lock, saved_mask);
}

PROTECTED int
unw_map_local_cursor_get_next (unw_map_cursor_t *map_cursor, unw_map_t *unw_map)
{
  struct map_info *map_info = map_cursor->cur_map;
  intrmask_t saved_mask;
  int ret = 1;

  if (map_info == NULL)
    return 0;

  /* This function can be called before any other unwind code, so make sure
     the lock has been initialized.  */
  map_local_init ();

  lock_rdwr_rd_acquire (&local_rdwr_lock, saved_mask);
  if (map_cursor->map_list != local_map_list)
    {
      map_cursor->map_list = local_map_list;
      ret = -UNW_EINVAL;
    }
  else
    {
      unw_map->start = map_info->start;
      unw_map->end = map_info->end;
      unw_map->offset = map_info->offset;
      unw_map->load_base = map_info->load_base;
      unw_map->flags = map_info->flags;
      if (map_info->path)
        unw_map->path = strdup (map_info->path);
      else
        unw_map->path = NULL;

      map_cursor->cur_map = map_info->next;
    }
  lock_rdwr_release (&local_rdwr_lock, saved_mask);

  return ret;
}
