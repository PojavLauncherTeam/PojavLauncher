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
#define SEARCH_PATH "/system/lib64"
#define ELF_EHDR Elf64_Ehdr
#define ELF_SHDR Elf64_Shdr
#define ELF_HALF Elf64_Half
#define ELF_XWORD Elf64_Xword
#define ELF_DYN Elf64_Dyn

//#define ADRENO_POSSIBLE

typedef void* (*loader_dlopen_t)(const char* filename, int flags, const void* caller_addr);

typedef struct android_namespace_t* (*ld_android_create_namespace_t)(
        const char* name, const char* ld_library_path, const char* default_library_path, uint64_t type,
        const char* permitted_when_isolated_path, struct android_namespace_t* parent, const void* caller_addr);

typedef struct android_namespace_t* (*ld_android_get_exported_namespace_t)(const char* name, const void* caller_addr);

static ld_android_create_namespace_t android_create_namespace;
static ld_android_get_exported_namespace_t android_get_exported_namespace;
static struct android_namespace_t* namespace;

struct android_namespace_t* local_android_create_namespace(
        const char* name, const char* ld_library_path, const char* default_library_path, uint64_t type,
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
    __android_log_print(ANDROID_LOG_INFO, "nsbypass", "ld-android.so handle: %p", ld_android_handle);
    if(ld_android_handle == NULL) return false;
    android_create_namespace = dlsym(ld_android_handle, "__loader_android_create_namespace");
    android_get_exported_namespace = dlsym(ld_android_handle, "__loader_android_get_exported_namespace");
    if(android_create_namespace == NULL || android_get_exported_namespace == NULL) {
        dlclose(ld_android_handle);
        return false;
    }
    char full_path[strlen(SEARCH_PATH) + strlen(lib_search_path) + 2 + 1];
    sprintf(full_path, "%s:%s", SEARCH_PATH, lib_search_path);
    namespace = local_android_create_namespace("pojav-driver",
                                               full_path,
                                               full_path,
                                               3 /* TYPE_SHAFED | TYPE_ISOLATED */,
                                               "/system/:/data/:/vendor/:/apex/", NULL);
    //dlclose(ld_android_handle);
    return true;
}

void* linker_ns_dlopen(const char* name, int flag) {
#ifndef ADRENO_POSSIBLE
    return NULL;
#endif
    android_dlextinfo dlextinfo;
    dlextinfo.flags = ANDROID_DLEXT_USE_NAMESPACE;
    dlextinfo.library_namespace = namespace;
    return android_dlopen_ext(name, flag, &dlextinfo);
}

bool patch_elf_soname(int patchfd, int realfd, uint16_t patchid) {
    struct stat realstat;
    if(fstat(realfd, &realstat)) return false;
    if(ftruncate(patchfd, realstat.st_size) == -1) return false;
    char* target = mmap(NULL, realstat.st_size, PROT_READ | PROT_WRITE, MAP_SHARED, patchfd, 0);
    if(!target) return false;
    if(read(realfd, target, realstat.st_size) != realstat.st_size) {
        munmap(target, realstat.st_size);
        return false;
    }
    close(realfd);


    ELF_EHDR *ehdr = (ELF_EHDR*)target;
    ELF_SHDR *shdr = (ELF_SHDR*)(target + ehdr->e_shoff);
    for(ELF_HALF i = 0; i < ehdr->e_shnum; i++) {
        ELF_SHDR *hdr = &shdr[i];
        if(hdr->sh_type == SHT_DYNAMIC) {
            char* strtab = target + shdr[hdr->sh_link].sh_offset;
            ELF_DYN *dynEntries = (ELF_DYN*)(target + hdr->sh_offset);
            for(ELF_XWORD k = 0; k < (hdr->sh_size / hdr->sh_entsize);k++) {
                ELF_DYN* dynEntry = &dynEntries[k];
                if(dynEntry->d_tag == DT_SONAME) {
                    char* soname = strtab + dynEntry->d_un.d_val;
                    char sprb[4];
                    snprintf(sprb, 4, "%03x", patchid);
                    memcpy(soname, sprb, 3);
                    munmap(target, realstat.st_size);
                    return true;
                }
            }
        }
    }
    return false;
}

void* linker_ns_dlopen_unique(const char* tmpdir, const char* name, int flags) {
#ifndef ADRENO_POSSIBLE
    return NULL;
#endif
    char pathbuf[PATH_MAX];
    static uint16_t patch_id;
    int patch_fd, real_fd;
    snprintf(pathbuf,PATH_MAX,"%s/%d_p.so", tmpdir, patch_id);
    patch_fd = open(pathbuf, O_CREAT | O_RDWR, S_IRUSR | S_IWUSR);
    if(patch_fd == -1) return NULL;
    snprintf(pathbuf,PATH_MAX,"%s/%s", SEARCH_PATH, name);
    real_fd = open(pathbuf, O_RDONLY);
    if(real_fd == -1) {
        close(patch_fd);
        return NULL;
    }
    if(!patch_elf_soname(patch_fd, real_fd, patch_id)) {
        close(patch_fd);
        close(real_fd);
        return NULL;
    }
    android_dlextinfo extinfo;
    extinfo.flags = ANDROID_DLEXT_USE_NAMESPACE | ANDROID_DLEXT_USE_LIBRARY_FD;
    extinfo.library_fd = patch_fd;
    extinfo.library_namespace = namespace;
    snprintf(pathbuf, PATH_MAX, "/proc/self/fd/%d", patch_fd);
    return android_dlopen_ext(pathbuf, flags, &extinfo);
}
