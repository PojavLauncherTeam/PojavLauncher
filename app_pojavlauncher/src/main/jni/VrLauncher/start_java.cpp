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

// Copied from jre_launcher.c

#include "start_java.h"

#include <signal.h>
#include <unistd.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <dlfcn.h>
#include <fcntl.h>
#include <errno.h>

#include "log.h"

#define TAG "start_java"

// PojavLancher: fixme: are these wrong?
#define FULL_VERSION "1.8.0-internal"
#define DOT_VERSION "1.8"

static const char *const_progname = "java";
static const char *const_launcher = "openjdk";
static const char **const_jargs = NULL;
static const char **const_appclasspath = NULL;
static const jboolean const_javaw = JNI_FALSE;
static const jboolean const_cpwildcard = JNI_TRUE;
static const jint const_ergo_class = 0; // DEFAULT_POLICY
static struct sigaction old_sa[NSIG];

void (*old_sa_func)(int signal, siginfo_t *info, void *reserved);

void android_sigaction(int signal, siginfo_t *info, void *reserved) {
    printf("process killed with signal %d code %d addr %p", signal, info->si_code, info->si_addr);
    old_sa_func = old_sa[signal].sa_sigaction;
    old_sa_func(signal, info, reserved);
    exit(1);
}

typedef jint JNI_CreateJavaVM_func(JavaVM **pvm, void **penv, void *args);

typedef jint JLI_Launch_func(int argc, char **argv, /* main argc, argc */
                             int jargc, const char **jargv,          /* java args */
                             int appclassc, const char **appclassv,  /* app classpath */
                             const char *fullversion,                /* full version defined */
                             const char *dotversion,                 /* dot version defined */
                             const char *pname,                      /* program name */
                             const char *lname,                      /* launcher name */
                             jboolean javaargs,                      /* JAVA_ARGS */
                             jboolean cpwildcard,                    /* classpath wildcard*/
                             jboolean javaw,                         /* windows-only javaw */
                             jint ergo                               /* ergonomics class policy */
);

static jint launchJVMRaw(int margc, char **margv) {
    void *libjli = dlopen("libjli.so", RTLD_LAZY | RTLD_GLOBAL);

    // Boardwalk: silence
    // LOGD("JLI lib = %x", (int)libjli);
    if (nullptr == libjli) {
        LOGE("JLI lib = NULL: %s", dlerror());
        return -1;
    }
    LOGD("Found JLI lib");

    auto *pJLI_Launch = (JLI_Launch_func *) dlsym(libjli, "JLI_Launch");
    // Boardwalk: silence
    // LOGD("JLI_Launch = 0x%x", *(int*)&pJLI_Launch);

    if (nullptr == pJLI_Launch) {
        LOGE("JLI_Launch = NULL");
        return -1;
    }

    // send stderr and stdout to a log
    // FIXME test code, remove for prod - the non-test mode captures this output properly
    int fd = open("/storage/emulated/0/games/PojavLauncher/logout", O_CREAT | O_TRUNC | O_RDWR,
                  0666);
    if (fd == -1) {
        LOGE("open failed!");
    } else {
        int res;
        if ((res = dup2(fd, 1)) == -1)
            LOGE("dup2 stdout failed %d %d", res, errno);
        if ((res = dup2(fd, 2)) == -1)
            LOGE("dup2 stderr failed %d %d", res, errno);
    }

    printf("Testing!\n");
    fflush(stdout);
    write(1, "hi:\n", 4);

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
int start_java(int argc, char **argv) {
    jint res = 0;
    // int i;
    //Prepare the signal trapper
    struct sigaction catcher = {};
    catcher.sa_sigaction = android_sigaction;
    catcher.sa_flags = SA_RESETHAND;
#define CATCHSIG(X) sigaction(X, &catcher, &old_sa[X])
    CATCHSIG(SIGILL);
    CATCHSIG(SIGABRT);
    CATCHSIG(SIGBUS);
    CATCHSIG(SIGFPE);
    //CATCHSIG(SIGSEGV);
    CATCHSIG(SIGSTKFLT);
    CATCHSIG(SIGPIPE);
    //Signal trapper ready

    return launchJVMRaw(argc, argv);
}

