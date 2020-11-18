/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <dirent.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <sys/inotify.h>
#include <sys/limits.h>
#include <sys/poll.h>
#include <linux/input.h>
#include <errno.h>
#include <cutils/log.h>

static struct pollfd* ufds;
static char** device_names;
static int nfds;

static int open_device(const char* device) {
  int version;
  int fd;
  struct pollfd* new_ufds;
  char** new_device_names;
  char name[80];
  char location[80];
  char idstr[80];
  struct input_id id;

  fd = open(device, O_RDWR);
  if (fd < 0) {
    return -1;
  }

  if (ioctl(fd, EVIOCGVERSION, &version)) {
    return -1;
  }
  if (ioctl(fd, EVIOCGID, &id)) {
    return -1;
  }
  name[sizeof(name) - 1] = '\0';
  location[sizeof(location) - 1] = '\0';
  idstr[sizeof(idstr) - 1] = '\0';
  if (ioctl(fd, EVIOCGNAME(sizeof(name) - 1), &name) < 1) {
    name[0] = '\0';
  }
  if (ioctl(fd, EVIOCGPHYS(sizeof(location) - 1), &location) < 1) {
    location[0] = '\0';
  }
  if (ioctl(fd, EVIOCGUNIQ(sizeof(idstr) - 1), &idstr) < 1) {
    idstr[0] = '\0';
  }

  new_ufds = reinterpret_cast<pollfd*>(realloc(ufds, sizeof(ufds[0]) * (nfds + 1)));
  if (new_ufds == NULL) {
    fprintf(stderr, "out of memory\n");
    return -1;
  }
  ufds = new_ufds;
  new_device_names = reinterpret_cast<char**>(realloc(
      device_names, sizeof(device_names[0]) * (nfds + 1)));
  if (new_device_names == NULL) {
    fprintf(stderr, "out of memory\n");
    return -1;
  }
  device_names = new_device_names;
  ufds[nfds].fd = fd;
  ufds[nfds].events = POLLIN;
  device_names[nfds] = strdup(device);
  nfds++;

  return 0;
}

int close_device(const char* device) {
  int i;
  for (i = 1; i < nfds; i++) {
    if (strcmp(device_names[i], device) == 0) {
      int count = nfds - i - 1;
      free(device_names[i]);
      memmove(device_names + i, device_names + i + 1, sizeof(device_names[0]) * count);
      memmove(ufds + i, ufds + i + 1, sizeof(ufds[0]) * count);
      nfds--;
      return 0;
    }
  }
  return -1;
}

static int read_notify(const char* dirname, int nfd) {
  int res;
  char devname[PATH_MAX];
  char* filename;
  char event_buf[512];
  int event_size;
  int event_pos = 0;
  struct inotify_event *event;

  res = read(nfd, event_buf, sizeof(event_buf));
  if (res < (int)sizeof(*event)) {
    if (errno == EINTR)
      return 0;
    fprintf(stderr, "could not get event, %s\n", strerror(errno));
    return 1;
  }

  strcpy(devname, dirname);
  filename = devname + strlen(devname);
  *filename++ = '/';

  while (res >= (int)sizeof(*event)) {
    event = reinterpret_cast<struct inotify_event*>(event_buf + event_pos);
    if (event->len) {
      strcpy(filename, event->name);
      if (event->mask & IN_CREATE) {
        open_device(devname);
      } else {
        close_device(devname);
      }
    }
    event_size = sizeof(*event) + event->len;
    res -= event_size;
    event_pos += event_size;
  }
  return 0;
}

static int scan_dir(const char* dirname) {
  char devname[PATH_MAX];
  char* filename;
  DIR* dir;
  struct dirent* de;
  dir = opendir(dirname);
  if (dir == NULL)
    return -1;
  strcpy(devname, dirname);
  filename = devname + strlen(devname);
  *filename++ = '/';
  while ((de = readdir(dir))) {
    if ((de->d_name[0] == '.' && de->d_name[1] == '\0') ||
        (de->d_name[1] == '.' && de->d_name[2] == '\0'))
      continue;
    strcpy(filename, de->d_name);
    open_device(devname);
  }
  closedir(dir);
  return 0;
}

int init_getevent() {
  int res;
  const char* device_path = "/dev/input";

  nfds = 1;
  ufds = reinterpret_cast<pollfd*>(calloc(1, sizeof(ufds[0])));
  ufds[0].fd = inotify_init();
  ufds[0].events = POLLIN;

  res = inotify_add_watch(ufds[0].fd, device_path, IN_DELETE | IN_CREATE);
  if (res < 0) {
    return 1;
  }
  res = scan_dir(device_path);
  if (res < 0) {
    return 1;
  }
  return 0;
}

void uninit_getevent() {
  int i;
  for (i = 0; i < nfds; i++) {
    close(ufds[i].fd);
  }
  free(ufds);
  ufds = 0;
  nfds = 0;
}

int get_event(struct input_event* event, int timeout) {
  int res;
  int i;
  int pollres;
  const char* device_path = "/dev/input";
  while (1) {
    pollres = poll(ufds, nfds, timeout);
    if (pollres == 0) {
      return 1;
    }
    if (ufds[0].revents & POLLIN) {
      read_notify(device_path, ufds[0].fd);
    }
    for (i = 1; i < nfds; i++) {
      if (ufds[i].revents) {
        if (ufds[i].revents & POLLIN) {
          res = read(ufds[i].fd, event, sizeof(*event));
          if (res < static_cast<int>(sizeof(event))) {
            fprintf(stderr, "could not get event\n");
            return -1;
          }
          return 0;
        }
      }
    }
  }
  return 0;
}
