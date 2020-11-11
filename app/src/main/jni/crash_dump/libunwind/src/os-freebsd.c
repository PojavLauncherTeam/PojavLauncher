/* libunwind - a platform-independent unwind library
   Copyright (C) 2010 Konstantin Belousov <kib@freebsd.org>

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

#include <sys/param.h>
#include <sys/types.h>
#include <sys/mman.h>
#include <sys/sysctl.h>
#include <sys/user.h>
#include <stdio.h>
#include <errno.h>

#include "libunwind_i.h"

static void *
get_mem(size_t sz)
{
  void *res;

  res = mmap(NULL, sz, PROT_READ | PROT_WRITE, MAP_ANON | MAP_PRIVATE, -1, 0);
  if (res == MAP_FAILED)
    return (NULL);
  return (res);
}

static void
free_mem(void *ptr, size_t sz)
{
  munmap(ptr, sz);
}

static int
get_pid_by_tid(int tid)
{
  int mib[3], error;
  size_t len, len1;
  char *buf;
  struct kinfo_proc *kv;
  int i, pid;

  len = 0;
  mib[0] = CTL_KERN;
  mib[1] = KERN_PROC;
  mib[2] = KERN_PROC_ALL;

  error = sysctl(mib, 3, NULL, &len, NULL, 0);
  if (error == -1)
    return (-1);
  len1 = len * 4 / 3;
  buf = get_mem(len1);
  if (buf == NULL)
    return (-1);
  len = len1;
  error = sysctl(mib, 3, buf, &len, NULL, 0);
  if (error == -1) {
    free_mem(buf, len1);
    return (-1);
  }
  pid = -1;
  for (i = 0, kv = (struct kinfo_proc *)buf; i < len / sizeof(*kv);
   i++, kv++) {
    if (kv->ki_tid == tid) {
      pid = kv->ki_pid;
      break;
    }
  }
  free_mem(buf, len1);
  return (pid);
}

/* ANDROID support update. */
struct map_info *
map_create_list(pid_t pid)
{
  int mib[4], error, ret;
  size_t len, len1;
  char *buf, *bp, *eb;
  struct kinfo_vmentry *kv;
  struct map_info *map_list = NULL;

  len = 0;
  mib[0] = CTL_KERN;
  mib[1] = KERN_PROC;
  mib[2] = KERN_PROC_VMMAP;
  mib[3] = pid;

  error = sysctl(mib, 4, NULL, &len, NULL, 0);
  if (error == -1) {
    if (errno == ESRCH)
      {
        mib[3] = get_pid_by_tid(pid);
        if (mib[3] != -1)
          error = sysctl(mib, 4, NULL, &len, NULL, 0);
        if (error == -1)
          return (-UNW_EUNSPEC);
      }
    else
      return (-UNW_EUNSPEC);
  }
  len1 = len * 4 / 3;
  buf = get_mem(len1);
  if (buf == NULL)
    return (-UNW_EUNSPEC);
  len = len1;
  error = sysctl(mib, 4, buf, &len, NULL, 0);
  if (error == -1)
    {
      free_mem(buf, len1);
      return (-UNW_EUNSPEC);
    }
  ret = -UNW_EUNSPEC;
  for (bp = buf, eb = buf + len; bp < eb; bp += kv->kve_structsize)
    {
      kv = (struct kinfo_vmentry *)(uintptr_t)bp;
      if (kv->kve_type != KVME_TYPE_VNODE)
        continue;

      cur_map = map_alloc_info ();
      if (cur_map == NULL)
        break;
      cur_map->next = map_list;
      cur_map->start = kv->kve_start;
      cur_map->end = kv->kv_end;
      cur_map->offset = kv->kve_offset;
      cur_map->path = strdup(kv->kve_path);
      mutex_init (&cur_map->ei_lock);
      cur_map->ei.size = 0;
      cur_map->ei.image = NULL;
      cur_map->ei_shared = 0;
    }
  free_mem(buf, len1);

  return map_list;
}
/* End of ANDROID update. */
