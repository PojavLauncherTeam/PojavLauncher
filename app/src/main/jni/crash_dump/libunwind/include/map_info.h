/* libunwind - a platform-independent unwind library
   Copyright (C) 2013 The Android Open Source Project

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

#ifndef map_info_h
#define map_info_h

/* Must not conflict with PROT_{NONE,READ,WRITE}. */
#define MAP_FLAGS_DEVICE_MEM  0x8000

enum map_create_type
  {
    UNW_MAP_CREATE_REMOTE,
    UNW_MAP_CREATE_LOCAL,
  };

struct map_info
  {
    uintptr_t start;
    uintptr_t end;
    uintptr_t offset;
    uintptr_t load_base;
    int flags;
    char *path;

    lock_var (ei_lock);
    struct elf_image ei;

    struct map_info *next;
  };

extern struct mempool map_pool;
extern struct map_info *local_map_list;

void map_local_init (void);

int map_local_is_readable (unw_word_t, size_t);

int map_local_is_writable (unw_word_t, size_t);

char *map_local_get_image_name (unw_word_t);

struct map_info *map_alloc_info (void);

void map_free_info (struct map_info *);

struct map_info *map_find_from_addr (struct map_info *, unw_word_t);

struct map_info *map_create_list (int, pid_t);

void map_destroy_list (struct map_info *);

#endif /* map_info_h */
