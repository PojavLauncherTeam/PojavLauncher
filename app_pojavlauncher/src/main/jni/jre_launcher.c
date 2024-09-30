/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
#include <android/log.h>
#include <dlfcn.h>
#include <errno.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
// Boardwalk: missing include
#include <string.h>

#include "log.h"
#include "utils.h"
#include "environ/environ.h"

// Uncomment to try redirect signal handling to JVM
// #define TRY_SIG2JVM

// PojavLancher: fixme: are these wrong?
#define FULL_VERSION "1.8.0-internal"
#define DOT_VERSION "1.8"

static const char* const_progname = "java";
static const char* const_launcher = "openjdk";
static const char** const_jargs = NULL;
static const char** const_appclasspath = NULL;
static const jboolean const_javaw = JNI_FALSE;
static const jboolean const_cpwildcard = JNI_TRUE;
static const jint const_ergo_class = 0; // DEFAULT_POLICY

typedef jint JLI_Launch_func(int argc, char ** argv, /* main argc, argc */
        int jargc, const char** jargv,          /* java args */
        int appclassc, const char** appclassv,  /* app classpath */
        const char* fullversion,                /* full version defined */
        const char* dotversion,                 /* dot version defined */
        const char* pname,                      /* program name */
        const char* lname,                      /* launcher name */
        jboolean javaargs,                      /* JAVA_ARGS */
        jboolean cpwildcard,                    /* classpath wildcard*/
        jboolean javaw,                         /* windows-only javaw */
        jint ergo                               /* ergonomics class policy */
);

struct {
    sigset_t tracked_sigset;
    int pipe[2];
} abrt_waiter_data;

_Noreturn extern void nominal_exit(int code, bool is_signal);

_Noreturn static void* abrt_waiter_thread(void* extraArg) {
    // Don't allow this thread to receive signals this thread is tracking.
    // We should only receive them externally.
    pthread_sigmask(SIG_BLOCK, &abrt_waiter_data.tracked_sigset, NULL);
    int signal;
    // Block for reading the signal ID until it arrives
    read(abrt_waiter_data.pipe[0], &signal, sizeof(int));
    // Die
    nominal_exit(signal, true);
}

_Noreturn static void abrt_waiter_handler(int signal) {
    // Write the final signal into the pipe and block forever.
    write(abrt_waiter_data.pipe[1], &signal, sizeof(int));
    while(1) {};
};

static void abrt_waiter_setup() {
    // Only abort on SIGABRT as the JVM either emits SIGABRT or SIGKILL (which we can't catch)
    // when a fatal crash occurs. Still, keep expandability if we would want to add more
    // user-friendly fatal signals in the future.
    const static int tracked_signals[] = {SIGABRT};
    const static int ntracked = (sizeof(tracked_signals) / sizeof(tracked_signals[0]));
    struct sigaction sigactions[ntracked];
    sigemptyset(&abrt_waiter_data.tracked_sigset);
    for(size_t i = 0; i < ntracked; i++) {
        sigaddset(&abrt_waiter_data.tracked_sigset, tracked_signals[i]);
        sigactions[i].sa_handler = abrt_waiter_handler;
    }
    if(pipe(abrt_waiter_data.pipe) != 0) {
        printf("Failed to set up aborter pipe: %s\n", strerror(errno));
        return;
    }
    pthread_t waiter_thread; int result;
    if((result = pthread_create(&waiter_thread, NULL, abrt_waiter_thread, NULL)) != 0) {
        printf("Failed to start up waiter thread: %s", strerror(result));
        for(int i = 0; i < 2; i++) close(abrt_waiter_data.pipe[i]);
        return;
    }
    // Only set the sigactions *after* we have already set up the pipe and the thread.
    for(size_t i = 0; i < ntracked; i++) {
        if(sigaction(tracked_signals[i], &sigactions[i], NULL) != 0) {
            // Not returning here because we may have set some handlers successfully.
            // Some handling is better than no handling.
            printf("Failed to set signal hander for signal %i: %s", i, strerror(errno));
        }
    }
}

static jint launchJVM(int margc, char** margv) {
   void* libjli = dlopen("libjli.so", RTLD_LAZY | RTLD_GLOBAL);

   // Unset all signal handlers to create a good slate for JVM signal detection.
   struct sigaction clean_sa;
   memset(&clean_sa, 0, sizeof (struct sigaction));
   for(int sigid = SIGHUP; sigid < NSIG; sigid++) {
       // For some reason Android specifically checks if you set SIGSEGV to SIG_DFL.
       // There's probably a good reason for that but the signal handler here is
       // temporary and will be replaced by the Java VM's signal/crash handler.
       // Work around the warning by using SIG_IGN for SIGSEGV
       if(sigid == SIGSEGV) clean_sa.sa_handler = SIG_IGN;
       else clean_sa.sa_handler = SIG_DFL;
       sigaction(sigid, &clean_sa, NULL);
   }
   // Set up the thread that will abort the launcher with an user-facing dialog on a signal.
   abrt_waiter_setup();

   // Boardwalk: silence
   // LOGD("JLI lib = %x", (int)libjli);
   if (NULL == libjli) {
       LOGE("JLI lib = NULL: %s", dlerror());
       return -1;
   }
   LOGD("Found JLI lib");

   JLI_Launch_func *pJLI_Launch =
          (JLI_Launch_func *)dlsym(libjli, "JLI_Launch");
    // Boardwalk: silence
    // LOGD("JLI_Launch = 0x%x", *(int*)&pJLI_Launch);

   if (NULL == pJLI_Launch) {
       LOGE("JLI_Launch = NULL");
       return -1;
   }

   LOGD("Calling JLI_Launch");

   return pJLI_Launch(margc, margv,
                   0, NULL, // sizeof(const_jargs) / sizeof(char *), const_jargs,
                   0, NULL, // sizeof(const_appclasspath) / sizeof(char *), const_appclasspath,
                   FULL_VERSION,
                   DOT_VERSION,
                   *margv, // (const_progname != NULL) ? const_progname : *margv,
                   *margv, // (const_launcher != NULL) ? const_launcher : *margv,
                   (const_jargs != NULL) ? JNI_TRUE : JNI_FALSE,
                   const_cpwildcard, const_javaw, const_ergo_class);
/*
   return pJLI_Launch(argc, argv, 
       0, NULL, 0, NULL, FULL_VERSION,
       DOT_VERSION, *margv, *margv, // "java", "openjdk",
       JNI_FALSE, JNI_TRUE, JNI_FALSE, 0);
*/
}

/*
 * Class:     com_oracle_dalvik_VMLauncher
 * Method:    launchJVM
 * Signature: ([Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_oracle_dalvik_VMLauncher_launchJVM(JNIEnv *env, jclass clazz, jobjectArray argsArray) {

   jint res = 0;

    // Save dalvik JNIEnv pointer for JVM launch thread
    pojav_environ->dalvikJNIEnvPtr_ANDROID = env;

    if (argsArray == NULL) {
        LOGE("Args array null, returning");
        //handle error
        return 0;
    }

    int argc = (*env)->GetArrayLength(env, argsArray);
    char **argv = convert_to_char_array(env, argsArray);

    LOGD("Done processing args");

    res = launchJVM(argc, argv);

    LOGD("Going to free args");
    free_char_array(env, argsArray, argv);

    LOGD("Free done");

    return res;
}
