//
// Created by maks on 05.06.2023.
//
#include "nsbypass.h"
#include <dlfcn.h>
#include <android/dlext.h>
#include <android/log.h>
#include <sys/mman.h>
#include <sys/user.h>
#include <string.h>
#include <stdio.h>
#include <linux/limits.h>
#include <errno.h>
#include <unistd.h>
#include <asm/unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <elf.h>

/* upper 6 bits of an ARM64 instruction are the instruction name */
#define OP_MS 0b11111100000000000000000000000000
/* Branch Label instruction opcode and immediate mask */
#define BL_OP 0b10010100000000000000000000000000
#define BL_IM 0b00000011111111111111111111111111
/* Library search path */
#define SEARCH_PATH "/system/lib"
#define ELF_EHDR Elf32_Ehdr
#define ELF_SHDR Elf32_Shdr
#define ELF_HALF Elf32_Half
#define ELF_XWORD Elf32_Word
#define ELF_DYN Elf32_Dyn

//#define ADRENO_POSSIBLE

typedef void* (*loader_dlopen_t)(const char* filename, int flags, const void* caller_addr);

typedef struct android_namespace_t* (*ld_android_create_namespace_t)(
        const char* name, const char* ld_library_path, const char* default_library_path, uint32_t type,
        const char* permitted_when_isolated_path, struct android_namespace_t* parent, const void* caller_addr);

typedef struct android_namespace_t* (*ld_android_get_exported_namespace_t)(const char* name, const void* caller_addr);

static ld_android_create_namespace_t android_create_namespace;
static ld_android_get_exported_namespace_t android_get_exported_namespace;
static struct android_namespace_t* namespace;

struct android_namespace_t* local_android_create_namespace(
        const char* name, const char* ld_library_path, const char* default_library_path, uint32_t type,
        const char* permitted_when_isolated_path, struct android_namespace_t* parent) {
    void* caller = __builtin_return_address(0);
    return android_create_namespace(name, ld_library_path, default_library_path, type, permitted_when_isolated_path, parent, caller);
}

struct android_namespace_t* ns_android_get_exported_namespace(
        const char* name) {
    void* caller = __builtin_return_address(0);
    return android_get_exported_namespace(name, caller);
}


bool linker_ns_load(const char* lib_search_path) {
#ifndef ADRENO_POSSIBLE
    return false;
#endif
    uint32_t *dlext_bl_addr = (uint32_t*)&dlopen;
    while((*dlext_bl_addr & OP_MS) !=
                                   BL_OP) dlext_bl_addr++; //walk through the function until we find the label that we need to go to
    __android_log_print(ANDROID_LOG_INFO, "nsbypass", "found branch label: %u", *dlext_bl_addr);
    loader_dlopen_t loader_dlopen;
    loader_dlopen = (loader_dlopen_t)(((char *) dlext_bl_addr) + (*dlext_bl_addr & BL_IM)*4);
    mprotect(loader_dlopen, PAGE_SIZE, PROT_WRITE | PROT_READ | PROT_EXEC); // reprotecting the function removes protection from indirect jumps
    void* ld_android_handle = loader_dlopen("ld-android.so", RTLD_LAZY, &dlopen);
    __android_log_print(ANDROID_LOG_INFO, "nsbypass", "ld-android.so
