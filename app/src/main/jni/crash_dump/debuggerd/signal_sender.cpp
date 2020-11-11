/*
 * Copyright (C) 2016 The Android Open Source Project
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

#include <errno.h>
#include <signal.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/syscall.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

#include <log/logger.h>

#include "signal_sender.h"

static int signal_fd = -1;
static pid_t signal_pid;
struct signal_message {
  pid_t pid;
  pid_t tid;
  int signal;
};

static void set_signal_sender_process_name() {
#if defined(__LP64__)
  static constexpr char long_process_name[] = "debuggerd64:signaller";
  static constexpr char short_process_name[] = "debuggerd64:sig";
  static_assert(sizeof(long_process_name) <= sizeof("/system/bin/debuggerd64"), "");
#else
  static constexpr char long_process_name[] = "debuggerd:signaller";
  static constexpr char short_process_name[] = "debuggerd:sig";
  static_assert(sizeof(long_process_name) <= sizeof("/system/bin/debuggerd"), "");
#endif

  // pthread_setname_np has a maximum length of 16 chars, including null terminator.
  static_assert(sizeof(short_process_name) <= 16, "");
  pthread_setname_np(pthread_self(), short_process_name);

  char* progname = const_cast<char*>(getprogname());
  if (strlen(progname) <= strlen(long_process_name)) {
    ALOGE("debuggerd: unexpected progname %s", progname);
    return;
  }

  memset(progname, 0, strlen(progname));
  strcpy(progname, long_process_name);
}

// Fork a process to send signals for the worker processes to use after they've dropped privileges.
bool start_signal_sender() {
  if (signal_pid != 0) {
    ALOGE("debuggerd: attempted to start signal sender multiple times");
    return false;
  }

  int sfd[2];
  if (socketpair(AF_UNIX, SOCK_SEQPACKET | SOCK_CLOEXEC, 0, sfd) != 0) {
    ALOGE("debuggerd: failed to create socketpair for signal sender: %s", strerror(errno));
    return false;
  }

  pid_t parent = getpid();
  pid_t fork_pid = fork();
  if (fork_pid == -1) {
    ALOGE("debuggerd: failed to initialize signal sender: fork failed: %s", strerror(errno));
    return false;
  } else if (fork_pid == 0) {
    close(sfd[1]);

    set_signal_sender_process_name();

    while (true) {
      signal_message msg;
      int rc = TEMP_FAILURE_RETRY(read(sfd[0], &msg, sizeof(msg)));
      if (rc < 0) {
        ALOGE("debuggerd: signal sender failed to read from socket");
        break;
      } else if (rc != sizeof(msg)) {
        ALOGE("debuggerd: signal sender read unexpected number of bytes: %d", rc);
        break;
      }

      // Report success after sending a signal
      int err = 0;
      if (msg.tid > 0) {
        if (syscall(SYS_tgkill, msg.pid, msg.tid, msg.signal) != 0) {
          err = errno;
        }
      } else {
        if (kill(msg.pid, msg.signal) != 0) {
          err = errno;
        }
      }

      if (TEMP_FAILURE_RETRY(write(sfd[0], &err, sizeof(err))) < 0) {
        ALOGE("debuggerd: signal sender failed to write: %s", strerror(errno));
      }
    }

    // Our parent proably died, but if not, kill them.
    if (getppid() == parent) {
      kill(parent, SIGKILL);
    }
    _exit(1);
  } else {
    close(sfd[0]);
    signal_fd = sfd[1];
    signal_pid = fork_pid;
    return true;
  }
}

bool stop_signal_sender() {
  if (signal_pid <= 0) {
    return false;
  }

  if (kill(signal_pid, SIGKILL) != 0) {
    ALOGE("debuggerd: failed to kill signal sender: %s", strerror(errno));
    return false;
  }

  close(signal_fd);
  signal_fd = -1;

  int status;
  waitpid(signal_pid, &status, 0);
  signal_pid = 0;

  return true;
}

bool send_signal(pid_t pid, pid_t tid, int signal) {
  if (signal_fd == -1) {
    ALOGE("debuggerd: attempted to send signal before signal sender was started");
    errno = EHOSTUNREACH;
    return false;
  }

  signal_message msg = {.pid = pid, .tid = tid, .signal = signal };
  if (TEMP_FAILURE_RETRY(write(signal_fd, &msg, sizeof(msg))) < 0) {
    ALOGE("debuggerd: failed to send message to signal sender: %s", strerror(errno));
    errno = EHOSTUNREACH;
    return false;
  }

  int response;
  ssize_t rc = TEMP_FAILURE_RETRY(read(signal_fd, &response, sizeof(response)));
  if (rc == 0) {
    ALOGE("debuggerd: received EOF from signal sender");
    errno = EHOSTUNREACH;
    return false;
  } else if (rc < 0) {
    ALOGE("debuggerd: failed to receive response from signal sender: %s", strerror(errno));
    errno = EHOSTUNREACH;
    return false;
  }

  if (response == 0) {
    return true;
  }

  errno = response;
  return false;
}
