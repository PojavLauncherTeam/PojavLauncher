#include <libunwind.h>
#include <pthread.h>
#include <stdint.h>
#include <string.h>

#include <functional>
#include <memory>
#include <string>
#include <utility>
#include <vector>

#include <backtrace/Backtrace.h>
#include <backtrace/BacktraceMap.h>
#include <cutils/threads.h>

#include <gtest/gtest.h>

extern "C" {
// Prototypes for functions in the test library.
int test_level_one(int, int, int, int, void (*)(void*), void*);
int test_level_two(int, int, int, int, void (*)(void*), void*);
int test_level_three(int, int, int, int, void (*)(void*), void*);
int test_level_four(int, int, int, int, void (*)(void*), void*);
int test_recursive_call(int, void (*)(void*), void*);
}

static volatile bool g_exit_flag = false;

static void GetContextAndExit(void* arg) {
  unw_context_t* unw_context = reinterpret_cast<unw_context_t*>(arg);
  unw_getcontext(unw_context);
  // Don't touch the stack anymore.
  while (!g_exit_flag) {
  }
}

struct OfflineThreadArg {
  unw_context_t unw_context;
  pid_t tid;
  std::function<int(void (*)(void*), void*)> function;
};

static void* OfflineThreadFunc(void* arg) {
  OfflineThreadArg* fn_arg = reinterpret_cast<OfflineThreadArg*>(arg);
  fn_arg->tid = gettid();
  fn_arg->function(GetContextAndExit, &fn_arg->unw_context);
  return nullptr;
}

static ucontext_t GetUContextFromUnwContext(const unw_context_t& unw_context) {
  ucontext_t ucontext;
  memset(&ucontext, 0, sizeof(ucontext));
#if defined(__arm__)
  ucontext.uc_mcontext.arm_r0 = unw_context.regs[0];
  ucontext.uc_mcontext.arm_r1 = unw_context.regs[1];
  ucontext.uc_mcontext.arm_r2 = unw_context.regs[2];
  ucontext.uc_mcontext.arm_r3 = unw_context.regs[3];
  ucontext.uc_mcontext.arm_r4 = unw_context.regs[4];
  ucontext.uc_mcontext.arm_r5 = unw_context.regs[5];
  ucontext.uc_mcontext.arm_r6 = unw_context.regs[6];
  ucontext.uc_mcontext.arm_r7 = unw_context.regs[7];
  ucontext.uc_mcontext.arm_r8 = unw_context.regs[8];
  ucontext.uc_mcontext.arm_r9 = unw_context.regs[9];
  ucontext.uc_mcontext.arm_r10 = unw_context.regs[10];
  ucontext.uc_mcontext.arm_fp = unw_context.regs[11];
  ucontext.uc_mcontext.arm_ip = unw_context.regs[12];
  ucontext.uc_mcontext.arm_sp = unw_context.regs[13];
  ucontext.uc_mcontext.arm_lr = unw_context.regs[14];
  ucontext.uc_mcontext.arm_pc = unw_context.regs[15];
#else
  ucontext.uc_mcontext = unw_context.uc_mcontext;
#endif
  return ucontext;
}

static void OfflineBacktraceFunctionCall(std::function<int(void (*)(void*), void*)> function,
                                         std::vector<uintptr_t>* pc_values) {
  // Create a thread to generate the needed stack and registers information.
  g_exit_flag = false;
  const size_t stack_size = 1024 * 1024;
  void* stack = mmap(NULL, stack_size, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
  ASSERT_NE(MAP_FAILED, stack);
  uintptr_t stack_addr = reinterpret_cast<uintptr_t>(stack);
  pthread_attr_t attr;
  ASSERT_EQ(0, pthread_attr_init(&attr));
  ASSERT_EQ(0, pthread_attr_setstack(&attr, reinterpret_cast<void*>(stack), stack_size));
  pthread_t thread;
  OfflineThreadArg arg;
  arg.function = function;
  ASSERT_EQ(0, pthread_create(&thread, &attr, OfflineThreadFunc, &arg));
  // Wait for the offline thread to generate the stack and unw_context information.
  sleep(1);
  // Copy the stack information.
  std::vector<uint8_t> stack_data(reinterpret_cast<uint8_t*>(stack),
                                  reinterpret_cast<uint8_t*>(stack) + stack_size);
  g_exit_flag = true;
  ASSERT_EQ(0, pthread_join(thread, nullptr));
  ASSERT_EQ(0, munmap(stack, stack_size));

  // Do offline backtrace.
  std::unique_ptr<BacktraceMap> map(BacktraceMap::Create(getpid()));
  ASSERT_TRUE(map != nullptr);

  backtrace_stackinfo_t stack_info;
  stack_info.start = stack_addr;
  stack_info.end = stack_addr + stack_size;
  stack_info.data = stack_data.data();

  std::unique_ptr<Backtrace> backtrace(
      Backtrace::CreateOffline(getpid(), arg.tid, map.get(), stack_info));
  ASSERT_TRUE(backtrace != nullptr);

  ucontext_t ucontext = GetUContextFromUnwContext(arg.unw_context);
  ASSERT_TRUE(backtrace->Unwind(0, &ucontext));

  // Collect pc values of the call stack frames.
  for (size_t i = 0; i < backtrace->NumFrames(); ++i) {
    pc_values->push_back(backtrace->GetFrame(i)->pc);
  }
}

// Return the name of the function which matches the address. Although we don't know the
// exact end of each function, it is accurate enough for the tests.
static std::string FunctionNameForAddress(uintptr_t addr) {
  struct FunctionSymbol {
    std::string name;
    uintptr_t start;
    uintptr_t end;
  };

  static std::vector<FunctionSymbol> symbols;
  if (symbols.empty()) {
    symbols = std::vector<FunctionSymbol>{
        {"unknown_start", 0, 0},
        {"test_level_one", reinterpret_cast<uintptr_t>(&test_level_one), 0},
        {"test_level_two", reinterpret_cast<uintptr_t>(&test_level_two), 0},
        {"test_level_three", reinterpret_cast<uintptr_t>(&test_level_three), 0},
        {"test_level_four", reinterpret_cast<uintptr_t>(&test_level_four), 0},
        {"test_recursive_call", reinterpret_cast<uintptr_t>(&test_recursive_call), 0},
        {"GetContextAndExit", reinterpret_cast<uintptr_t>(&GetContextAndExit), 0},
        {"unknown_end", static_cast<uintptr_t>(-1), static_cast<uintptr_t>(-1)},
    };
    std::sort(
        symbols.begin(), symbols.end(),
        [](const FunctionSymbol& s1, const FunctionSymbol& s2) { return s1.start < s2.start; });
    for (size_t i = 0; i + 1 < symbols.size(); ++i) {
      symbols[i].end = symbols[i + 1].start;
    }
  }
  for (auto& symbol : symbols) {
    if (addr >= symbol.start && addr < symbol.end) {
      return symbol.name;
    }
  }
  return "";
}

TEST(libbacktrace, offline) {
  std::function<int(void (*)(void*), void*)> function =
      std::bind(test_level_one, 1, 2, 3, 4, std::placeholders::_1, std::placeholders::_2);
  std::vector<uintptr_t> pc_values;
  OfflineBacktraceFunctionCall(function, &pc_values);
  ASSERT_FALSE(pc_values.empty());
  ASSERT_LE(pc_values.size(), static_cast<size_t>(MAX_BACKTRACE_FRAMES));

  size_t test_one_index = 0;
  for (size_t i = 0; i < pc_values.size(); ++i) {
    if (FunctionNameForAddress(pc_values[i]) == "test_level_one") {
      test_one_index = i;
      break;
    }
  }

  ASSERT_GE(test_one_index, 3u);
  ASSERT_EQ("test_level_one", FunctionNameForAddress(pc_values[test_one_index]));
  ASSERT_EQ("test_level_two", FunctionNameForAddress(pc_values[test_one_index - 1]));
  ASSERT_EQ("test_level_three", FunctionNameForAddress(pc_values[test_one_index - 2]));
  ASSERT_EQ("test_level_four", FunctionNameForAddress(pc_values[test_one_index - 3]));
}

TEST(libbacktrace, offline_max_trace) {
  std::function<int(void (*)(void*), void*)> function = std::bind(
      test_recursive_call, MAX_BACKTRACE_FRAMES + 10, std::placeholders::_1, std::placeholders::_2);
  std::vector<uintptr_t> pc_values;
  OfflineBacktraceFunctionCall(function, &pc_values);
  ASSERT_FALSE(pc_values.empty());
  ASSERT_EQ(static_cast<size_t>(MAX_BACKTRACE_FRAMES), pc_values.size());
  ASSERT_EQ("test_recursive_call", FunctionNameForAddress(pc_values.back()));
}
