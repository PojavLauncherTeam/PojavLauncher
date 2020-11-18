/*
 * Copyright (C) 2015 The Android Open Source Project
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

#ifdef _WIN32
#include <windows.h>
#endif

#include "android-base/logging.h"

#include <libgen.h>

// For getprogname(3) or program_invocation_short_name.
#if defined(__ANDROID__) || defined(__APPLE__)
#include <stdlib.h>
#elif defined(__GLIBC__)
#include <errno.h>
#endif

#include <iostream>
#include <limits>
#include <sstream>
#include <string>
#include <utility>
#include <vector>

#ifndef _WIN32
#include <mutex>
#endif

#include "android-base/macros.h"
#include "android-base/strings.h"
#include "cutils/threads.h"

// Headers for LogMessage::LogLine.
#ifdef __ANDROID__
#include <android/set_abort_message.h>
#include "cutils/log.h"
#else
#include <sys/types.h>
#include <unistd.h>
#endif

// For gettid.
#if defined(__APPLE__)
#include "AvailabilityMacros.h"  // For MAC_OS_X_VERSION_MAX_ALLOWED
#include <stdint.h>
#include <stdlib.h>
#include <sys/syscall.h>
#include <sys/time.h>
#include <unistd.h>
#elif defined(__linux__) && !defined(__ANDROID__)
#include <syscall.h>
#include <unistd.h>
#elif defined(_WIN32)
#include <windows.h>
#endif

#if defined(_WIN32)
typedef uint32_t thread_id;
#else
typedef pid_t thread_id;
#endif

static thread_id GetThreadId() {
#if defined(__BIONIC__)
  return gettid();
#elif defined(__APPLE__)
  return syscall(SYS_thread_selfid);
#elif defined(__linux__)
  return syscall(__NR_gettid);
#elif defined(_WIN32)
  return GetCurrentThreadId();
#endif
}

namespace {
#ifndef _WIN32
using std::mutex;
using std::lock_guard;

#if defined(__GLIBC__)
const char* getprogname() {
  return program_invocation_short_name;
}
#endif

#else
const char* getprogname() {
  static bool first = true;
  static char progname[MAX_PATH] = {};

  if (first) {
    CHAR longname[MAX_PATH];
    DWORD nchars = GetModuleFileNameA(nullptr, longname, arraysize(longname));
    if ((nchars >= arraysize(longname)) || (nchars == 0)) {
      // String truncation or some other error.
      strcpy(progname, "<unknown>");
    } else {
      strcpy(progname, basename(longname));
    }
    first = false;
  }

  return progname;
}

class mutex {
 public:
  mutex() {
    InitializeCriticalSection(&critical_section_);
  }
  ~mutex() {
    DeleteCriticalSection(&critical_section_);
  }

  void lock() {
    EnterCriticalSection(&critical_section_);
  }

  void unlock() {
    LeaveCriticalSection(&critical_section_);
  }

 private:
  CRITICAL_SECTION critical_section_;
};

template <typename LockT>
class lock_guard {
 public:
  explicit lock_guard(LockT& lock) : lock_(lock) {
    lock_.lock();
  }

  ~lock_guard() {
    lock_.unlock();
  }

 private:
  LockT& lock_;

  DISALLOW_COPY_AND_ASSIGN(lock_guard);
};
#endif
} // namespace

namespace android {
namespace base {

static auto& logging_lock = *new mutex();

#ifdef __ANDROID__
static auto& gLogger = *new LogFunction(LogdLogger());
#else
static auto& gLogger = *new LogFunction(StderrLogger);
#endif

static bool gInitialized = false;
static LogSeverity gMinimumLogSeverity = INFO;
static auto& gProgramInvocationName = *new std::unique_ptr<std::string>();

LogSeverity GetMinimumLogSeverity() {
  return gMinimumLogSeverity;
}

static const char* ProgramInvocationName() {
  if (gProgramInvocationName == nullptr) {
    gProgramInvocationName.reset(new std::string(getprogname()));
  }

  return gProgramInvocationName->c_str();
}

void StderrLogger(LogId, LogSeverity severity, const char*, const char* file,
                  unsigned int line, const char* message) {
  static const char log_characters[] = "VDIWEF";
  static_assert(arraysize(log_characters) - 1 == FATAL + 1,
                "Mismatch in size of log_characters and values in LogSeverity");
  char severity_char = log_characters[severity];
  fprintf(stderr, "%s %c %5d %5d %s:%u] %s\n", ProgramInvocationName(),
          severity_char, getpid(), GetThreadId(), file, line, message);
}


#ifdef __ANDROID__
LogdLogger::LogdLogger(LogId default_log_id) : default_log_id_(default_log_id) {
}

static const android_LogPriority kLogSeverityToAndroidLogPriority[] = {
    ANDROID_LOG_VERBOSE, ANDROID_LOG_DEBUG, ANDROID_LOG_INFO,
    ANDROID_LOG_WARN,    ANDROID_LOG_ERROR, ANDROID_LOG_FATAL,
};
static_assert(arraysize(kLogSeverityToAndroidLogPriority) == FATAL + 1,
              "Mismatch in size of kLogSeverityToAndroidLogPriority and values "
              "in LogSeverity");

static const log_id kLogIdToAndroidLogId[] = {
    LOG_ID_MAX, LOG_ID_MAIN, LOG_ID_SYSTEM,
};
static_assert(arraysize(kLogIdToAndroidLogId) == SYSTEM + 1,
              "Mismatch in size of kLogIdToAndroidLogId and values in LogId");

void LogdLogger::operator()(LogId id, LogSeverity severity, const char* tag,
                            const char* file, unsigned int line,
                            const char* message) {
  int priority = kLogSeverityToAndroidLogPriority[severity];
  if (id == DEFAULT) {
    id = default_log_id_;
  }

  log_id lg_id = kLogIdToAndroidLogId[id];

  if (priority == ANDROID_LOG_FATAL) {
    __android_log_buf_print(lg_id, priority, tag, "%s:%u] %s", file, line,
                            message);
  } else {
    __android_log_buf_print(lg_id, priority, tag, "%s", message);
  }
}
#endif

void InitLogging(char* argv[], LogFunction&& logger) {
  SetLogger(std::forward<LogFunction>(logger));
  InitLogging(argv);
}

void InitLogging(char* argv[]) {
  if (gInitialized) {
    return;
  }

  gInitialized = true;

  // Stash the command line for later use. We can use /proc/self/cmdline on
  // Linux to recover this, but we don't have that luxury on the Mac/Windows,
  // and there are a couple of argv[0] variants that are commonly used.
  if (argv != nullptr) {
    gProgramInvocationName.reset(new std::string(basename(argv[0])));
  }

  const char* tags = getenv("ANDROID_LOG_TAGS");
  if (tags == nullptr) {
    return;
  }

  std::vector<std::string> specs = Split(tags, " ");
  for (size_t i = 0; i < specs.size(); ++i) {
    // "tag-pattern:[vdiwefs]"
    std::string spec(specs[i]);
    if (spec.size() == 3 && StartsWith(spec, "*:")) {
      switch (spec[2]) {
        case 'v':
          gMinimumLogSeverity = VERBOSE;
          continue;
        case 'd':
          gMinimumLogSeverity = DEBUG;
          continue;
        case 'i':
          gMinimumLogSeverity = INFO;
          continue;
        case 'w':
          gMinimumLogSeverity = WARNING;
          continue;
        case 'e':
          gMinimumLogSeverity = ERROR;
          continue;
        case 'f':
          gMinimumLogSeverity = FATAL;
          continue;
        // liblog will even suppress FATAL if you say 's' for silent, but that's
        // crazy!
        case 's':
          gMinimumLogSeverity = FATAL;
          continue;
      }
    }
    LOG(FATAL) << "unsupported '" << spec << "' in ANDROID_LOG_TAGS (" << tags
               << ")";
  }
}

void SetLogger(LogFunction&& logger) {
  lock_guard<mutex> lock(logging_lock);
  gLogger = std::move(logger);
}

static const char* GetFileBasename(const char* file) {
  // We can't use basename(3) even on Unix because the Mac doesn't
  // have a non-modifying basename.
  const char* last_slash = strrchr(file, '/');
  if (last_slash != nullptr) {
    return last_slash + 1;
  }
#if defined(_WIN32)
  const char* last_backslash = strrchr(file, '\\');
  if (last_backslash != nullptr) {
    return last_backslash + 1;
  }
#endif
  return file;
}

// This indirection greatly reduces the stack impact of having lots of
// checks/logging in a function.
class LogMessageData {
 public:
  LogMessageData(const char* file, unsigned int line, LogId id,
                 LogSeverity severity, int error)
      : file_(GetFileBasename(file)),
        line_number_(line),
        id_(id),
        severity_(severity),
        error_(error) {
  }

  const char* GetFile() const {
    return file_;
  }

  unsigned int GetLineNumber() const {
    return line_number_;
  }

  LogSeverity GetSeverity() const {
    return severity_;
  }

  LogId GetId() const {
    return id_;
  }

  int GetError() const {
    return error_;
  }

  std::ostream& GetBuffer() {
    return buffer_;
  }

  std::string ToString() const {
    return buffer_.str();
  }

 private:
  std::ostringstream buffer_;
  const char* const file_;
  const unsigned int line_number_;
  const LogId id_;
  const LogSeverity severity_;
  const int error_;

  DISALLOW_COPY_AND_ASSIGN(LogMessageData);
};

LogMessage::LogMessage(const char* file, unsigned int line, LogId id,
                       LogSeverity severity, int error)
    : data_(new LogMessageData(file, line, id, severity, error)) {
}

LogMessage::~LogMessage() {
  // Finish constructing the message.
  if (data_->GetError() != -1) {
    data_->GetBuffer() << ": " << strerror(data_->GetError());
  }
  std::string msg(data_->ToString());

  {
    // Do the actual logging with the lock held.
    lock_guard<mutex> lock(logging_lock);
    if (msg.find('\n') == std::string::npos) {
      LogLine(data_->GetFile(), data_->GetLineNumber(), data_->GetId(),
              data_->GetSeverity(), msg.c_str());
    } else {
      msg += '\n';
      size_t i = 0;
      while (i < msg.size()) {
        size_t nl = msg.find('\n', i);
        msg[nl] = '\0';
        LogLine(data_->GetFile(), data_->GetLineNumber(), data_->GetId(),
                data_->GetSeverity(), &msg[i]);
        i = nl + 1;
      }
    }
  }

  // Abort if necessary.
  if (data_->GetSeverity() == FATAL) {
#ifdef __ANDROID__
    android_set_abort_message(msg.c_str());
#endif
    abort();
  }
}

std::ostream& LogMessage::stream() {
  return data_->GetBuffer();
}

void LogMessage::LogLine(const char* file, unsigned int line, LogId id,
                         LogSeverity severity, const char* message) {
  const char* tag = ProgramInvocationName();
  gLogger(id, severity, tag, file, line, message);
}

ScopedLogSeverity::ScopedLogSeverity(LogSeverity level) {
  old_ = gMinimumLogSeverity;
  gMinimumLogSeverity = level;
}

ScopedLogSeverity::~ScopedLogSeverity() {
  gMinimumLogSeverity = old_;
}

}  // namespace base
}  // namespace android
