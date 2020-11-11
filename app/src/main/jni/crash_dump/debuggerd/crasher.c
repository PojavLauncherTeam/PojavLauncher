#include <assert.h>
#include <errno.h>
#include <pthread.h>
#include <sched.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/cdefs.h>
#include <sys/mman.h>
#include <sys/ptrace.h>
#include <sys/socket.h>
#include <sys/wait.h>
#include <unistd.h>

#include <cutils/sockets.h>
#include <log/log.h>

#ifndef __unused
#define __unused __attribute__((__unused__))
#endif

extern const char* __progname;

void crash1(void);
void crashnostack(void);
static int do_action(const char* arg);

static void maybe_abort() {
    if (time(0) != 42) {
        abort();
    }
}

static char* smash_stack_dummy_buf;
__attribute__ ((noinline)) static void smash_stack_dummy_function(volatile int* plen) {
  smash_stack_dummy_buf[*plen] = 0;
}

// This must be marked with "__attribute__ ((noinline))", to ensure the
// compiler generates the proper stack guards around this function.
// Assign local array address to global variable to force stack guards.
// Use another noinline function to corrupt the stack.
__attribute__ ((noinline)) static int smash_stack(volatile int* plen) {
    printf("crasher: deliberately corrupting stack...\n");

    char buf[128];
    smash_stack_dummy_buf = buf;
    // This should corrupt stack guards and make process abort.
    smash_stack_dummy_function(plen);
    return 0;
}

#if defined(__clang__)
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Winfinite-recursion"
#endif

static void* global = 0; // So GCC doesn't optimize the tail recursion out of overflow_stack.

__attribute__((noinline)) static void overflow_stack(void* p) {
    void* buf[1];
    buf[0] = p;
    global = buf;
    overflow_stack(&buf);
}

#if defined(__clang__)
#pragma clang diagnostic pop
#endif

static void *noisy(void *x)
{
    char c = (uintptr_t) x;
    for(;;) {
        usleep(250*1000);
        write(2, &c, 1);
        if(c == 'C') *((volatile unsigned*) 0) = 42;
    }
    return NULL;
}

static int ctest()
{
    pthread_t thr;
    pthread_attr_t attr;
    pthread_attr_init(&attr);
    pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
    pthread_create(&thr, &attr, noisy, (void*) 'A');
    pthread_create(&thr, &attr, noisy, (void*) 'B');
    pthread_create(&thr, &attr, noisy, (void*) 'C');
    for(;;) ;
    return 0;
}

static void* thread_callback(void* raw_arg)
{
    return (void*) (uintptr_t) do_action((const char*) raw_arg);
}

static int do_action_on_thread(const char* arg)
{
    pthread_t t;
    pthread_create(&t, NULL, thread_callback, (void*) arg);
    void* result = NULL;
    pthread_join(t, &result);
    return (int) (uintptr_t) result;
}

__attribute__((noinline)) static int crash3(int a) {
    *((int*) 0xdead) = a;
    return a*4;
}

__attribute__((noinline)) static int crash2(int a) {
    a = crash3(a) + 2;
    return a*3;
}

__attribute__((noinline)) static int crash(int a) {
    a = crash2(a) + 1;
    return a*2;
}

static void abuse_heap() {
    char buf[16];
    free((void*) buf); // GCC is smart enough to warn about this, but we're doing it deliberately.
}

static void sigsegv_non_null() {
    int* a = (int *)(&do_action);
    *a = 42;
}

static int do_action(const char* arg)
{
    fprintf(stderr,"crasher: init pid=%d tid=%d\n", getpid(), gettid());

    if (!strncmp(arg, "thread-", strlen("thread-"))) {
        return do_action_on_thread(arg + strlen("thread-"));
    } else if (!strcmp(arg, "SIGSEGV-non-null")) {
        sigsegv_non_null();
    } else if (!strcmp(arg, "smash-stack")) {
        volatile int len = 128;
        return smash_stack(&len);
    } else if (!strcmp(arg, "stack-overflow")) {
        overflow_stack(NULL);
    } else if (!strcmp(arg, "nostack")) {
        crashnostack();
    } else if (!strcmp(arg, "ctest")) {
        return ctest();
    } else if (!strcmp(arg, "exit")) {
        exit(1);
    } else if (!strcmp(arg, "crash") || !strcmp(arg, "SIGSEGV")) {
        return crash(42);
    } else if (!strcmp(arg, "abort")) {
        maybe_abort();
    } else if (!strcmp(arg, "assert")) {
        __assert("some_file.c", 123, "false");
    } else if (!strcmp(arg, "assert2")) {
        __assert2("some_file.c", 123, "some_function", "false");
    } else if (!strcmp(arg, "LOG_ALWAYS_FATAL")) {
        LOG_ALWAYS_FATAL("hello %s", "world");
    } else if (!strcmp(arg, "LOG_ALWAYS_FATAL_IF")) {
        LOG_ALWAYS_FATAL_IF(true, "hello %s", "world");
    } else if (!strcmp(arg, "SIGFPE")) {
        raise(SIGFPE);
        return EXIT_SUCCESS;
    } else if (!strcmp(arg, "SIGTRAP")) {
        raise(SIGTRAP);
        return EXIT_SUCCESS;
    } else if (!strcmp(arg, "heap-usage")) {
        abuse_heap();
    } else if (!strcmp(arg, "SIGSEGV-unmapped")) {
        char* map = mmap(NULL, sizeof(int), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, -1, 0);
        munmap(map, sizeof(int));
        map[0] = '8';
    }

    fprintf(stderr, "%s OP\n", __progname);
    fprintf(stderr, "where OP is:\n");
    fprintf(stderr, "  smash-stack           overwrite a stack-guard canary\n");
    fprintf(stderr, "  stack-overflow        recurse until the stack overflows\n");
    fprintf(stderr, "  heap-corruption       cause a libc abort by corrupting the heap\n");
    fprintf(stderr, "  heap-usage            cause a libc abort by abusing a heap function\n");
    fprintf(stderr, "  nostack               crash with a NULL stack pointer\n");
    fprintf(stderr, "  ctest                 (obsoleted by thread-crash?)\n");
    fprintf(stderr, "  exit                  call exit(1)\n");
    fprintf(stderr, "  abort                 call abort()\n");
    fprintf(stderr, "  assert                call assert() without a function\n");
    fprintf(stderr, "  assert2               call assert() with a function\n");
    fprintf(stderr, "  LOG_ALWAYS_FATAL      call LOG_ALWAYS_FATAL\n");
    fprintf(stderr, "  LOG_ALWAYS_FATAL_IF   call LOG_ALWAYS_FATAL\n");
    fprintf(stderr, "  SIGFPE                cause a SIGFPE\n");
    fprintf(stderr, "  SIGSEGV               cause a SIGSEGV at address 0x0 (synonym: crash)\n");
    fprintf(stderr, "  SIGSEGV-non-null      cause a SIGSEGV at a non-zero address\n");
    fprintf(stderr, "  SIGSEGV-unmapped      mmap/munmap a region of memory and then attempt to access it\n");
    fprintf(stderr, "  SIGTRAP               cause a SIGTRAP\n");
    fprintf(stderr, "prefix any of the above with 'thread-' to not run\n");
    fprintf(stderr, "on the process' main thread.\n");
    return EXIT_SUCCESS;
}

int main(int argc, char **argv)
{
    fprintf(stderr,"crasher: built at " __TIME__ "!@\n");

    if(argc > 1) {
        return do_action(argv[1]);
    } else {
        crash1();
    }

    return 0;
}
