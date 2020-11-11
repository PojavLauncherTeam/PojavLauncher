/*
 * Copyright 2006, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <arpa/inet.h>
#include <dirent.h>
#include <elf.h>
#include <errno.h>
#include <fcntl.h>
#include <pthread.h>
#include <signal.h>
#include <stdarg.h>
#include <stdio.h>
#include <sys/poll.h>
#include <sys/prctl.h>
#include <sys/ptrace.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/un.h>
#include <time.h>

#include <memory>
#include <set>
#include <string>

#include <selinux/android.h>

#include <log/logger.h>

#include <android-base/file.h>
#include <android-base/unique_fd.h>
#include <cutils/debugger.h>
#include <cutils/properties.h>
#include <cutils/sockets.h>
#include <nativehelper/ScopedFd.h>

#include <linux/input.h>

#include <private/android_filesystem_config.h>

#include "backtrace.h"
#include "getevent.h"
#include "signal_sender.h"
#include "tombstone.h"
#include "utility.h"

// If the 32 bit executable is compiled on a 64 bit system,
// use the 32 bit socket name.
#if defined(TARGET_IS_64_BIT) && !defined(__LP64__)
#define SOCKET_NAME DEBUGGER32_SOCKET_NAME
#else
#define SOCKET_NAME DEBUGGER_SOCKET_NAME
#endif

struct debugger_request_t {
  debugger_action_t action;
  pid_t pid, tid;
  uid_t uid, gid;
  uintptr_t abort_msg_address;
  int32_t original_si_code;
};

static void wait_for_user_action(const debugger_request_t& request) {
  // Explain how to attach the debugger.
  ALOGI("***********************************************************\n"
        "* Process %d has been suspended while crashing.\n"
        "* To attach gdbserver and start gdb, run this on the host:\n"
        "*\n"
        "*     gdbclient.py -p %d\n"
        "*\n"
        "* Wait for gdb to start, then press the VOLUME DOWN key\n"
        "* to let the process continue crashing.\n"
        "***********************************************************",
        request.pid, request.tid);

  // Wait for VOLUME DOWN.
  while (true) {
    input_event e;
    if (get_event(&e, -1) == 0) {
      if (e.type == EV_KEY && e.code == KEY_VOLUMEDOWN && e.value == 0) {
        break;
      }
    }
  }

  ALOGI("debuggerd resuming process %d", request.pid);
}

static int get_process_info(pid_t tid, pid_t* out_pid, uid_t* out_uid, uid_t* out_gid) {
  char path[64];
  snprintf(path, sizeof(path), "/proc/%d/status", tid);

  FILE* fp = fopen(path, "r");
  if (!fp) {
    return -1;
  }

  int fields = 0;
  char line[1024];
  while (fgets(line, sizeof(line), fp)) {
    size_t len = strlen(line);
    if (len > 6 && !memcmp(line, "Tgid:\t", 6)) {
      *out_pid = atoi(line + 6);
      fields |= 1;
    } else if (len > 5 && !memcmp(line, "Uid:\t", 5)) {
      *out_uid = atoi(line + 5);
      fields |= 2;
    } else if (len > 5 && !memcmp(line, "Gid:\t", 5)) {
      *out_gid = atoi(line + 5);
      fields |= 4;
    }
  }
  fclose(fp);
  return fields == 7 ? 0 : -1;
}

/*
 * Corresponds with debugger_action_t enum type in
 * include/cutils/debugger.h.
 */
static const char *debuggerd_perms[] = {
  NULL, /* crash is only used on self, no check applied */
  "dump_tombstone",
  "dump_backtrace"
};

static int audit_callback(void* data, security_class_t /* cls */, char* buf, size_t len)
{
    struct debugger_request_t* req = reinterpret_cast<debugger_request_t*>(data);

    if (!req) {
        ALOGE("No debuggerd request audit data");
        return 0;
    }

    snprintf(buf, len, "pid=%d uid=%d gid=%d", req->pid, req->uid, req->gid);
    return 0;
}

static bool selinux_action_allowed(int s, debugger_request_t* request)
{
  char *scon = NULL, *tcon = NULL;
  const char *tclass = "debuggerd";
  const char *perm;
  bool allowed = false;

  if (request->action <= 0 || request->action >= (sizeof(debuggerd_perms)/sizeof(debuggerd_perms[0]))) {
    ALOGE("SELinux:  No permission defined for debugger action %d", request->action);
    return false;
  }

  perm = debuggerd_perms[request->action];

  if (getpeercon(s, &scon) < 0) {
    ALOGE("Cannot get peer context from socket\n");
    goto out;
  }

  if (getpidcon(request->tid, &tcon) < 0) {
    ALOGE("Cannot get context for tid %d\n", request->tid);
    goto out;
  }

  allowed = true; // (selinux_check_access(scon, tcon, tclass, perm, reinterpret_cast<void*>(request)) == 0);

out:
   freecon(scon);
   freecon(tcon);
   return allowed;
}

static bool pid_contains_tid(pid_t pid, pid_t tid) {
  char task_path[PATH_MAX];
  if (snprintf(task_path, PATH_MAX, "/proc/%d/task/%d", pid, tid) >= PATH_MAX) {
    ALOGE("debuggerd: task path overflow (pid = %d, tid = %d)\n", pid, tid);
    exit(1);
  }

  return access(task_path, F_OK) == 0;
}

static int read_request(int fd, debugger_request_t* out_request) {
  ucred cr;
  socklen_t len = sizeof(cr);
  int status = getsockopt(fd, SOL_SOCKET, SO_PEERCRED, &cr, &len);
  if (status != 0) {
    ALOGE("cannot get credentials");
    return -1;
  }

  ALOGV("reading tid");
  fcntl(fd, F_SETFL, O_NONBLOCK);

  pollfd pollfds[1];
  pollfds[0].fd = fd;
  pollfds[0].events = POLLIN;
  pollfds[0].revents = 0;
  status = TEMP_FAILURE_RETRY(poll(pollfds, 1, 3000));
  if (status != 1) {
    ALOGE("timed out reading tid (from pid=%d uid=%d)\n", cr.pid, cr.uid);
    return -1;
  }

  debugger_msg_t msg;
  memset(&msg, 0, sizeof(msg));
  status = TEMP_FAILURE_RETRY(read(fd, &msg, sizeof(msg)));
  if (status < 0) {
    ALOGE("read failure? %s (pid=%d uid=%d)\n", strerror(errno), cr.pid, cr.uid);
    return -1;
  }
  if (status != sizeof(debugger_msg_t)) {
    ALOGE("invalid crash request of size %d (from pid=%d uid=%d)\n", status, cr.pid, cr.uid);
    return -1;
  }

  out_request->action = static_cast<debugger_action_t>(msg.action);
  out_request->tid = msg.tid;
  out_request->pid = cr.pid;
  out_request->uid = cr.uid;
  out_request->gid = cr.gid;
  out_request->abort_msg_address = msg.abort_msg_address;
  out_request->original_si_code = msg.original_si_code;

  if (msg.action == DEBUGGER_ACTION_CRASH) {
    // Ensure that the tid reported by the crashing process is valid.
    // This check needs to happen again after ptracing the requested thread to prevent a race.
    if (!pid_contains_tid(out_request->pid, out_request->tid)) {
      ALOGE("tid %d does not exist in pid %d. ignoring debug request\n", out_request->tid,
            out_request->pid);
      return -1;
    }
  } else if (cr.uid == 0 || (cr.uid == AID_SYSTEM && msg.action == DEBUGGER_ACTION_DUMP_BACKTRACE)) {
    // Only root or system can ask us to attach to any process and dump it explicitly.
    // However, system is only allowed to collect backtraces but cannot dump tombstones.
    status = get_process_info(out_request->tid, &out_request->pid,
                              &out_request->uid, &out_request->gid);
    if (status < 0) {
      ALOGE("tid %d does not exist. ignoring explicit dump request\n", out_request->tid);
      return -1;
    }

    if (!selinux_action_allowed(fd, out_request))
      return -1;
  } else {
    // No one else is allowed to dump arbitrary processes.
    return -1;
  }
  return 0;
}

static int activity_manager_connect() {
  android::base::unique_fd amfd(socket(PF_UNIX, SOCK_STREAM, 0));
  if (amfd.get() < -1) {
    ALOGE("debuggerd: Unable to connect to activity manager (socket failed: %s)", strerror(errno));
    return -1;
  }

  struct sockaddr_un address;
  memset(&address, 0, sizeof(address));
  address.sun_family = AF_UNIX;
  // The path used here must match the value defined in NativeCrashListener.java.
  strncpy(address.sun_path, "/data/system/ndebugsocket", sizeof(address.sun_path));
  if (TEMP_FAILURE_RETRY(connect(amfd.get(), reinterpret_cast<struct sockaddr*>(&address),
                                 sizeof(address))) == -1) {
    ALOGE("debuggerd: Unable to connect to activity manager (connect failed: %s)", strerror(errno));
    return -1;
  }

  struct timeval tv;
  memset(&tv, 0, sizeof(tv));
  tv.tv_sec = 1;  // tight leash
  if (setsockopt(amfd.get(), SOL_SOCKET, SO_SNDTIMEO, &tv, sizeof(tv)) == -1) {
    ALOGE("debuggerd: Unable to connect to activity manager (setsockopt SO_SNDTIMEO failed: %s)",
          strerror(errno));
    return -1;
  }

  tv.tv_sec = 3;  // 3 seconds on handshake read
  if (setsockopt(amfd.get(), SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv)) == -1) {
    ALOGE("debuggerd: Unable to connect to activity manager (setsockopt SO_RCVTIMEO failed: %s)",
          strerror(errno));
    return -1;
  }

  return amfd.release();
}

static void activity_manager_write(int pid, int signal, int amfd, const std::string& amfd_data) {
  if (amfd == -1) {
    return;
  }

  // Activity Manager protocol: binary 32-bit network-byte-order ints for the
  // pid and signal number, followed by the raw text of the dump, culminating
  // in a zero byte that marks end-of-data.
  uint32_t datum = htonl(pid);
  if (!android::base::WriteFully(amfd, &datum, 4)) {
    ALOGE("AM pid write failed: %s\n", strerror(errno));
    return;
  }
  datum = htonl(signal);
  if (!android::base::WriteFully(amfd, &datum, 4)) {
    ALOGE("AM signal write failed: %s\n", strerror(errno));
    return;
  }

  if (!android::base::WriteFully(amfd, amfd_data.c_str(), amfd_data.size())) {
    ALOGE("AM data write failed: %s\n", strerror(errno));
    return;
  }

  // Send EOD to the Activity Manager, then wait for its ack to avoid racing
  // ahead and killing the target out from under it.
  uint8_t eodMarker = 0;
  if (!android::base::WriteFully(amfd, &eodMarker, 1)) {
    ALOGE("AM eod write failed: %s\n", strerror(errno));
    return;
  }
  // 3 sec timeout reading the ack; we're fine if the read fails.
  android::base::ReadFully(amfd, &eodMarker, 1);
}

static bool should_attach_gdb(const debugger_request_t& request) {
  if (request.action == DEBUGGER_ACTION_CRASH) {
    return property_get_bool("debug.debuggerd.wait_for_gdb", false);
  }
  return false;
}

#if defined(__LP64__)
static bool is32bit(pid_t tid) {
  char* exeline;
  if (asprintf(&exeline, "/proc/%d/exe", tid) == -1) {
    return false;
  }
  int fd = TEMP_FAILURE_RETRY(open(exeline, O_RDONLY | O_CLOEXEC));
  int saved_errno = errno;
  free(exeline);
  if (fd == -1) {
    ALOGW("Failed to open /proc/%d/exe %s", tid, strerror(saved_errno));
    return false;
  }

  char ehdr[EI_NIDENT];
  ssize_t bytes = TEMP_FAILURE_RETRY(read(fd, &ehdr, sizeof(ehdr)));
  close(fd);
  if (bytes != (ssize_t) sizeof(ehdr) || memcmp(ELFMAG, ehdr, SELFMAG) != 0) {
    return false;
  }
  if (ehdr[EI_CLASS] == ELFCLASS32) {
    return true;
  }
  return false;
}

static void redirect_to_32(int fd, debugger_request_t* request) {
  debugger_msg_t msg;
  memset(&msg, 0, sizeof(msg));
  msg.tid = request->tid;
  msg.action = request->action;

  int sock_fd = socket_local_client(DEBUGGER32_SOCKET_NAME, ANDROID_SOCKET_NAMESPACE_ABSTRACT,
                                    SOCK_STREAM | SOCK_CLOEXEC);
  if (sock_fd < 0) {
    ALOGE("Failed to connect to debuggerd32: %s", strerror(errno));
    return;
  }

  if (TEMP_FAILURE_RETRY(write(sock_fd, &msg, sizeof(msg))) != (ssize_t) sizeof(msg)) {
    ALOGE("Failed to write request to debuggerd32 socket: %s", strerror(errno));
    close(sock_fd);
    return;
  }

  char ack;
  if (TEMP_FAILURE_RETRY(read(sock_fd, &ack, 1)) == -1) {
    ALOGE("Failed to read ack from debuggerd32 socket: %s", strerror(errno));
    close(sock_fd);
    return;
  }

  char buffer[1024];
  ssize_t bytes_read;
  while ((bytes_read = TEMP_FAILURE_RETRY(read(sock_fd, buffer, sizeof(buffer)))) > 0) {
    ssize_t bytes_to_send = bytes_read;
    ssize_t bytes_written;
    do {
      bytes_written = TEMP_FAILURE_RETRY(write(fd, buffer + bytes_read - bytes_to_send,
                                               bytes_to_send));
      if (bytes_written == -1) {
        if (errno == EAGAIN) {
          // Retry the write.
          continue;
        }
        ALOGE("Error while writing data to fd: %s", strerror(errno));
        break;
      }
      bytes_to_send -= bytes_written;
    } while (bytes_written != 0 && bytes_to_send > 0);
    if (bytes_to_send != 0) {
        ALOGE("Failed to write all data to fd: read %zd, sent %zd", bytes_read, bytes_to_send);
        break;
    }
  }
  close(sock_fd);
}
#endif

// Attach to a thread, and verify that it's still a member of the given process
static bool ptrace_attach_thread(pid_t pid, pid_t tid) {
  if (ptrace(PTRACE_ATTACH, tid, 0, 0) != 0) {
    return false;
  }

  // Make sure that the task we attached to is actually part of the pid we're dumping.
  if (!pid_contains_tid(pid, tid)) {
    if (ptrace(PTRACE_DETACH, tid, 0, 0) != 0) {
      ALOGE("debuggerd: failed to detach from thread '%d'", tid);
      exit(1);
    }
    return false;
  }

  return true;
}

static void ptrace_siblings(pid_t pid, pid_t main_tid, std::set<pid_t>& tids) {
  char task_path[PATH_MAX];

  if (snprintf(task_path, PATH_MAX, "/proc/%d/task", pid) >= PATH_MAX) {
    ALOGE("debuggerd: task path overflow (pid = %d)\n", pid);
    abort();
  }

  std::unique_ptr<DIR, int (*)(DIR*)> d(opendir(task_path), closedir);

  // Bail early if the task directory cannot be opened.
  if (!d) {
    ALOGE("debuggerd: failed to open /proc/%d/task: %s", pid, strerror(errno));
    return;
  }

  struct dirent* de;
  while ((de = readdir(d.get())) != NULL) {
    // Ignore "." and "..".
    if (!strcmp(de->d_name, ".") || !strcmp(de->d_name, "..")) {
      continue;
    }

    char* end;
    pid_t tid = strtoul(de->d_name, &end, 10);
    if (*end) {
      continue;
    }

    if (tid == main_tid) {
      continue;
    }

    if (!ptrace_attach_thread(pid, tid)) {
      ALOGE("debuggerd: ptrace attach to %d failed: %s", tid, strerror(errno));
      continue;
    }

    tids.insert(tid);
  }
}

static bool perform_dump(const debugger_request_t& request, int fd, int tombstone_fd,
                         BacktraceMap* backtrace_map, const std::set<pid_t>& siblings,
                         int* crash_signal, std::string* amfd_data) {
  if (TEMP_FAILURE_RETRY(write(fd, "\0", 1)) != 1) {
    ALOGE("debuggerd: failed to respond to client: %s\n", strerror(errno));
    return false;
  }

  int total_sleep_time_usec = 0;
  while (true) {
    int signal = wait_for_signal(request.tid, &total_sleep_time_usec);
    switch (signal) {
      case -1:
        ALOGE("debuggerd: timed out waiting for signal");
        return false;

      case SIGSTOP:
        if (request.action == DEBUGGER_ACTION_DUMP_TOMBSTONE) {
          ALOGV("debuggerd: stopped -- dumping to tombstone");
          engrave_tombstone(tombstone_fd, backtrace_map, request.pid, request.tid, siblings, signal,
                            request.original_si_code, request.abort_msg_address, amfd_data);
        } else if (request.action == DEBUGGER_ACTION_DUMP_BACKTRACE) {
          ALOGV("debuggerd: stopped -- dumping to fd");
          dump_backtrace(fd, backtrace_map, request.pid, request.tid, siblings, nullptr);
        } else {
          ALOGV("debuggerd: stopped -- continuing");
          if (ptrace(PTRACE_CONT, request.tid, 0, 0) != 0) {
            ALOGE("debuggerd: ptrace continue failed: %s", strerror(errno));
            return false;
          }
          continue;  // loop again
        }
        break;

      case SIGABRT:
      case SIGBUS:
      case SIGFPE:
      case SIGILL:
      case SIGSEGV:
#ifdef SIGSTKFLT
      case SIGSTKFLT:
#endif
      case SIGSYS:
      case SIGTRAP:
        ALOGV("stopped -- fatal signal\n");
        *crash_signal = signal;
        engrave_tombstone(tombstone_fd, backtrace_map, request.pid, request.tid, siblings, signal,
                          request.original_si_code, request.abort_msg_address, amfd_data);
        break;

      default:
        ALOGE("debuggerd: process stopped due to unexpected signal %d\n", signal);
        break;
    }
    break;
  }

  return true;
}

static bool drop_privileges() {
  // AID_LOG: for reading the logs data associated with the crashing process.
  // AID_READPROC: for reading /proc/<PID>/{comm,cmdline}.
  gid_t groups[] = { AID_DEBUGGERD, AID_LOG, AID_READPROC };
  if (setgroups(sizeof(groups)/sizeof(groups[0]), groups) != 0) {
    ALOGE("debuggerd: failed to setgroups: %s", strerror(errno));
    return false;
  }

  if (setresgid(AID_DEBUGGERD, AID_DEBUGGERD, AID_DEBUGGERD) != 0) {
    ALOGE("debuggerd: failed to setresgid: %s", strerror(errno));
    return false;
  }

  if (setresuid(AID_DEBUGGERD, AID_DEBUGGERD, AID_DEBUGGERD) != 0) {
    ALOGE("debuggerd: failed to setresuid: %s", strerror(errno));
    return false;
  }

  return true;
}

static void worker_process(int fd, debugger_request_t& request) {
  // Open the tombstone file if we need it.
  std::string tombstone_path;
  int tombstone_fd = -1;
  switch (request.action) {
    case DEBUGGER_ACTION_DUMP_TOMBSTONE:
    case DEBUGGER_ACTION_CRASH:
      tombstone_fd = open_tombstone(&tombstone_path);
      if (tombstone_fd == -1) {
        ALOGE("debuggerd: failed to open tombstone file: %s\n", strerror(errno));
        exit(1);
      }
      break;

    case DEBUGGER_ACTION_DUMP_BACKTRACE:
      break;

    default:
      ALOGE("debuggerd: unexpected request action: %d", request.action);
      exit(1);
  }

  // At this point, the thread that made the request is blocked in
  // a read() call.  If the thread has crashed, then this gives us
  // time to PTRACE_ATTACH to it before it has a chance to really fault.
  //
  // The PTRACE_ATTACH sends a SIGSTOP to the target process, but it
  // won't necessarily have stopped by the time ptrace() returns.  (We
  // currently assume it does.)  We write to the file descriptor to
  // ensure that it can run as soon as we call PTRACE_CONT below.
  // See details in bionic/libc/linker/debugger.c, in function
  // debugger_signal_handler().

  // Attach to the target process.
  if (!ptrace_attach_thread(request.pid, request.tid)) {
    ALOGE("debuggerd: ptrace attach failed: %s", strerror(errno));
    exit(1);
  }

  // DEBUGGER_ACTION_CRASH requests can come from arbitrary processes and the tid field in the
  // request is sent from the other side. If an attacker can cause a process to be spawned with the
  // pid of their process, they could trick debuggerd into dumping that process by exiting after
  // sending the request. Validate the trusted request.uid/gid to defend against this.
  if (request.action == DEBUGGER_ACTION_CRASH) {
    pid_t pid;
    uid_t uid;
    gid_t gid;
    if (get_process_info(request.tid, &pid, &uid, &gid) != 0) {
      ALOGE("debuggerd: failed to get process info for tid '%d'", request.tid);
      exit(1);
    }

    if (pid != request.pid || uid != request.uid || gid != request.gid) {
      ALOGE(
        "debuggerd: attached task %d does not match request: "
        "expected pid=%d,uid=%d,gid=%d, actual pid=%d,uid=%d,gid=%d",
        request.tid, request.pid, request.uid, request.gid, pid, uid, gid);
      exit(1);
    }
  }

  // Don't attach to the sibling threads if we want to attach gdb.
  // Supposedly, it makes the process less reliable.
  bool attach_gdb = should_attach_gdb(request);
  if (attach_gdb) {
    // Open all of the input devices we need to listen for VOLUMEDOWN before dropping privileges.
    if (init_getevent() != 0) {
      ALOGE("debuggerd: failed to initialize input device, not waiting for gdb");
      attach_gdb = false;
    }

  }

  std::set<pid_t> siblings;
  if (!attach_gdb) {
    ptrace_siblings(request.pid, request.tid, siblings);
  }

  // Generate the backtrace map before dropping privileges.
  std::unique_ptr<BacktraceMap> backtrace_map(BacktraceMap::Create(request.pid));

  int amfd = -1;
  std::unique_ptr<std::string> amfd_data;
  if (request.action == DEBUGGER_ACTION_CRASH) {
    // Connect to the activity manager before dropping privileges.
    amfd = activity_manager_connect();
    amfd_data.reset(new std::string);
  }

  bool succeeded = false;

  // Now that we've done everything that requires privileges, we can drop them.
  if (!drop_privileges()) {
    ALOGE("debuggerd: failed to drop privileges, exiting");
    _exit(1);
  }

  int crash_signal = SIGKILL;
  succeeded = perform_dump(request, fd, tombstone_fd, backtrace_map.get(), siblings,
                           &crash_signal, amfd_data.get());
  if (succeeded) {
    if (request.action == DEBUGGER_ACTION_DUMP_TOMBSTONE) {
      if (!tombstone_path.empty()) {
        android::base::WriteFully(fd, tombstone_path.c_str(), tombstone_path.length());
      }
    }
  }

  if (attach_gdb) {
    // Tell the signal process to send SIGSTOP to the target.
    if (!send_signal(request.pid, 0, SIGSTOP)) {
      ALOGE("debuggerd: failed to stop process for gdb attach: %s", strerror(errno));
      attach_gdb = false;
    }
  }

  if (!attach_gdb) {
    // Tell the Activity Manager about the crashing process. If we are
    // waiting for gdb to attach, do not send this or Activity Manager
    // might kill the process before anyone can attach.
    activity_manager_write(request.pid, crash_signal, amfd, *amfd_data.get());
  }

  if (ptrace(PTRACE_DETACH, request.tid, 0, 0) != 0) {
    ALOGE("debuggerd: ptrace detach from %d failed: %s", request.tid, strerror(errno));
  }

  for (pid_t sibling : siblings) {
    ptrace(PTRACE_DETACH, sibling, 0, 0);
  }

  // Send the signal back to the process if it crashed and we're not waiting for gdb.
  if (!attach_gdb && request.action == DEBUGGER_ACTION_CRASH) {
    if (!send_signal(request.pid, request.tid, crash_signal)) {
      ALOGE("debuggerd: failed to kill process %d: %s", request.pid, strerror(errno));
    }
  }

  // Wait for gdb, if requested.
  if (attach_gdb) {
    wait_for_user_action(request);

    // Now tell the activity manager about this process.
    activity_manager_write(request.pid, crash_signal, amfd, *amfd_data.get());

    // Tell the signal process to send SIGCONT to the target.
    if (!send_signal(request.pid, 0, SIGCONT)) {
      ALOGE("debuggerd: failed to resume process %d: %s", request.pid, strerror(errno));
    }

    uninit_getevent();
  }

  close(amfd);

  exit(!succeeded);
}

static void monitor_worker_process(int child_pid, const debugger_request_t& request) {
  struct timespec timeout = {.tv_sec = 10, .tv_nsec = 0 };
  if (should_attach_gdb(request)) {
    // If wait_for_gdb is enabled, set the timeout to something large.
    timeout.tv_sec = INT_MAX;
  }

  sigset_t signal_set;
  sigemptyset(&signal_set);
  sigaddset(&signal_set, SIGCHLD);

  bool kill_worker = false;
  bool kill_target = false;
  bool kill_self = false;

  int status;
  siginfo_t siginfo;
  int signal = TEMP_FAILURE_RETRY(sigtimedwait(&signal_set, &siginfo, &timeout));
  if (signal == SIGCHLD) {
    pid_t rc = waitpid(-1, &status, WNOHANG | WUNTRACED);
    if (rc != child_pid) {
      ALOGE("debuggerd: waitpid returned unexpected pid (%d), committing murder-suicide", rc);

      if (WIFEXITED(status)) {
        ALOGW("debuggerd: pid %d exited with status %d", rc, WEXITSTATUS(status));
      } else if (WIFSIGNALED(status)) {
        ALOGW("debuggerd: pid %d received signal %d", rc, WTERMSIG(status));
      } else if (WIFSTOPPED(status)) {
        ALOGW("debuggerd: pid %d stopped by signal %d", rc, WSTOPSIG(status));
      } else if (WIFCONTINUED(status)) {
        ALOGW("debuggerd: pid %d continued", rc);
      }

      kill_worker = true;
      kill_target = true;
      kill_self = true;
    } else if (WIFSIGNALED(status)) {
      ALOGE("debuggerd: worker process %d terminated due to signal %d", child_pid, WTERMSIG(status));
      kill_worker = false;
      kill_target = true;
    } else if (WIFSTOPPED(status)) {
      ALOGE("debuggerd: worker process %d stopped due to signal %d", child_pid, WSTOPSIG(status));
      kill_worker = true;
      kill_target = true;
    }
  } else {
    ALOGE("debuggerd: worker process %d timed out", child_pid);
    kill_worker = true;
    kill_target = true;
  }

  if (kill_worker) {
    // Something bad happened, kill the worker.
    if (kill(child_pid, SIGKILL) != 0) {
      ALOGE("debuggerd: failed to kill worker process %d: %s", child_pid, strerror(errno));
    } else {
      waitpid(child_pid, &status, 0);
    }
  }

  int exit_signal = SIGCONT;
  if (kill_target && request.action == DEBUGGER_ACTION_CRASH) {
    ALOGE("debuggerd: killing target %d", request.pid);
    exit_signal = SIGKILL;
  } else {
    ALOGW("debuggerd: resuming target %d", request.pid);
  }

  if (kill(request.pid, exit_signal) != 0) {
    ALOGE("debuggerd: failed to send signal %d to target: %s", exit_signal, strerror(errno));
  }

  if (kill_self) {
    stop_signal_sender();
    _exit(1);
  }
}

static void handle_request(int fd) {
  ALOGV("handle_request(%d)\n", fd);

  ScopedFd closer(fd);
  debugger_request_t request;
  memset(&request, 0, sizeof(request));
  int status = read_request(fd, &request);
  if (status != 0) {
    return;
  }

  ALOGW("debuggerd: handling request: pid=%d uid=%d gid=%d tid=%d\n", request.pid, request.uid,
        request.gid, request.tid);

#if defined(__LP64__)
  // On 64 bit systems, requests to dump 32 bit and 64 bit tids come
  // to the 64 bit debuggerd. If the process is a 32 bit executable,
  // redirect the request to the 32 bit debuggerd.
  if (is32bit(request.tid)) {
    // Only dump backtrace and dump tombstone requests can be redirected.
    if (request.action == DEBUGGER_ACTION_DUMP_BACKTRACE ||
        request.action == DEBUGGER_ACTION_DUMP_TOMBSTONE) {
      redirect_to_32(fd, &request);
    } else {
      ALOGE("debuggerd: Not allowed to redirect action %d to 32 bit debuggerd\n", request.action);
    }
    return;
  }
#endif

  // Fork a child to handle the rest of the request.
  pid_t fork_pid = fork();
  if (fork_pid == -1) {
    ALOGE("debuggerd: failed to fork: %s\n", strerror(errno));
  } else if (fork_pid == 0) {
    worker_process(fd, request);
  } else {
    monitor_worker_process(fork_pid, request);
  }
}

static int do_server() {
  // debuggerd crashes can't be reported to debuggerd.
  // Reset all of the crash handlers.
  signal(SIGABRT, SIG_DFL);
  signal(SIGBUS, SIG_DFL);
  signal(SIGFPE, SIG_DFL);
  signal(SIGILL, SIG_DFL);
  signal(SIGSEGV, SIG_DFL);
#ifdef SIGSTKFLT
  signal(SIGSTKFLT, SIG_DFL);
#endif
  signal(SIGTRAP, SIG_DFL);

  // Ignore failed writes to closed sockets
  signal(SIGPIPE, SIG_IGN);

  // Block SIGCHLD so we can sigtimedwait for it.
  sigset_t sigchld;
  sigemptyset(&sigchld);
  sigaddset(&sigchld, SIGCHLD);
  sigprocmask(SIG_SETMASK, &sigchld, nullptr);

  int s = socket_local_server(SOCKET_NAME, ANDROID_SOCKET_NAMESPACE_ABSTRACT,
                              SOCK_STREAM | SOCK_CLOEXEC);
  if (s == -1) return 1;

  // Fork a process that stays root, and listens on a pipe to pause and resume the target.
  if (!start_signal_sender()) {
    ALOGE("debuggerd: failed to fork signal sender");
    return 1;
  }

  ALOGI("debuggerd: starting\n");

  for (;;) {
    sockaddr_storage ss;
    sockaddr* addrp = reinterpret_cast<sockaddr*>(&ss);
    socklen_t alen = sizeof(ss);

    ALOGV("waiting for connection\n");
    int fd = accept4(s, addrp, &alen, SOCK_CLOEXEC);
    if (fd == -1) {
      ALOGE("accept failed: %s\n", strerror(errno));
      continue;
    }

    handle_request(fd);
  }
  return 0;
}

static int do_explicit_dump(pid_t tid, bool dump_backtrace) {
  fprintf(stdout, "Sending request to dump task %d.\n", tid);

  if (dump_backtrace) {
    fflush(stdout);
    if (dump_backtrace_to_file(tid, fileno(stdout)) < 0) {
      fputs("Error dumping backtrace.\n", stderr);
      return 1;
    }
  } else {
    char tombstone_path[PATH_MAX];
    if (dump_tombstone(tid, tombstone_path, sizeof(tombstone_path)) < 0) {
      fputs("Error dumping tombstone.\n", stderr);
      return 1;
    }
    fprintf(stderr, "Tombstone written to: %s\n", tombstone_path);
  }
  return 0;
}

static void usage() {
  fputs("Usage: -b [<tid>]\n"
        "  -b dump backtrace to console, otherwise dump full tombstone file\n"
        "\n"
        "If tid specified, sends a request to debuggerd to dump that task.\n"
        "Otherwise, starts the debuggerd server.\n", stderr);
}

int main(int argc, char** argv) {
  union selinux_callback cb;
  if (argc == 1) {
    cb.func_audit = audit_callback;
    selinux_set_callback(SELINUX_CB_AUDIT, cb);
    cb.func_log = selinux_log_callback;
    selinux_set_callback(SELINUX_CB_LOG, cb);
    return do_server();
  }

  bool dump_backtrace = false;
  bool have_tid = false;
  pid_t tid = 0;
  for (int i = 1; i < argc; i++) {
    if (!strcmp(argv[i], "-b")) {
      dump_backtrace = true;
    } else if (!have_tid) {
      tid = atoi(argv[i]);
      have_tid = true;
    } else {
      usage();
      return 1;
    }
  }
  if (!have_tid) {
    usage();
    return 1;
  }
  return do_explicit_dump(tid, dump_backtrace);
}
