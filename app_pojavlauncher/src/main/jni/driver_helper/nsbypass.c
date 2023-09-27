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

typedef void* (*ld_android_link_namespaces_t)(struct android_namespace_t* namespace_from,
                                              struct android_namespace_t* namespace_to,
                                              const char* shared_libs_sonames);

static ld_android_create_namespace_t android_create_namespace;
static struct android_namespace_t* driver_namespace;

struct android_namespace_t* local_android_create_namespace(
        const char* name, const char* ld_library_path, const char* default_library_path, uint64_t type,
        const char* permitted_when_isolated_path, struct android_namespace_t* parent) {
    void* caller = __builtin_return_address(0);
    return android_create_namespace(name, ld_library_path, default_library_path, type, permitted_when_isolated_path, parent, caller);
}

// Find the first "branch to label" function in the function provided in func_start
static void* find_branch_label(void* func_start) {
    // round down the pointer to get the start of the function's page
    void* func_page_start = (void*)(((uintptr_t)func_start) & ~(PAGE_SIZE-1));
    // remap to r-x to bypass "execute only" protections on MIUI
    mprotect(func_page_start, PAGE_SIZE, PROT_READ | PROT_EXEC);
    uint32_t* bl_addr = func_start;
    // search for the "branch to label" opcode
    while((*bl_addr & OP_MS) != BL_OP) {
        bl_addr++; // walk through memory until we find it or die
    }
    // offset the address to find where the "branch to label" instrunction
    // points to.
    return ((char*)bl_addr) + (*bl_addr & BL_IM) * 4;
}

bool linker_ns_load(const char* lib_search_path) {
#ifndef ADRENO_POSSIBLE
    return false;
#else
    loader_dlopen_t loader_dlopen = find_branch_label(&dlopen);
    // reprotecting the functions removes protection from indirect jumps
    mprotect(loader_dlopen, PAGE_SIZE, PROT_WRITE | PROT_READ | PROT_EXEC);
    void* ld_android_handle = loader_dlopen("ld-android.so", RTLD_LAZY, &dlopen);
    if(ld_android_handle == NULL) {
        return false;
    }
    // load the two functions we need
    android_create_namespace = dlsym(ld_android_handle, "__loader_android_create_namespace");
    ld_android_link_namespaces_t android_link_namespaces = dlsym(ld_android_handle, "__loader_android_link_namespaces");
    __android_log_print(ANDROID_LOG_INFO, "nsbypass", "found functions at %p %p", android_create_namespace, android_link_namespaces);
    if(android_create_namespace == NULL || android_link_namespaces == NULL) {
        dlclose(ld_android_handle);
        return false;
    }
    // assemble the full path search path
    char full_path[strlen(SEARCH_PATH) + strlen(lib_search_path) + 2 + 1];
    sprintf(full_path, "%s:%s", SEARCH_PATH, lib_search_path);
    driver_namespace = local_android_create_namespace("pojav-driver",
                                                      full_path,
                                                      full_path,
                                                      3 /* TYPE_SHAFED | TYPE_ISOLATED */,
                                                      "/system/:/data/:/vendor/:/apex/", NULL);
    // THIS IS VERY IMPORTANT and how I trolled FoldCraft:
    // You need to link the new driver_namespace with NULL and and add ld-android.so
    // in the link list, to pass through the driver_namespace correctly.
    // Not doing this fucks up internal __loader symbol lookup
    // inside the new driver_namespace, thus breaking it on
    // a lot of android versions
    // FoldCraft got trolled because they copied the
    // old broken code verbatim and didn't even test it thoroughly
    android_link_namespaces(driver_namespace, NULL, "ld-android.so");
    // Also establish links to use the libnativeloader(_lazy).so libraries
    // from the global namespace. This is a workaround for an EMUI issue where
    // the newly loaded libnativeloader_lazy for some unknown reason links
    // to itself and causes a deadlock when loading the vulkan driver.
    android_link_namespaces(driver_namespace, NULL, "libnativeloader.so");
    android_link_namespaces(driver_namespace, NULL, "libnativeloader_lazy.so");
    dlclose(ld_android_handle);
    return true;
#endif
}

void* linker_ns_dlopen(const char* name, int flag) {
#ifndef ADRENO_POSSIBLE
    return NULL;
#else
    android_dlextinfo dlextinfo;
    dlextinfo.flags = ANDROID_DLEXT_USE_NAMESPACE;
    dlextinfo.library_namespace = driver_namespace;
    return android_dlopen_ext(name, flag, &dlextinfo);
#endif
}

bool patch_elf_soname(int patchfd, int realfd, uint16_t patchid) {
    struct stat realstat;
    if(fstat(realfd, &realstat)) return false;
    if(ftruncate64(patchfd, realstat.st_size) == -1) return false;
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
            // If there's a warning below, it's bogus, ignore it
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
#else
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
    extinfo.library_namespace = driver_namespace;
    snprintf(pathbuf, PATH_MAX, "/proc/self/fd/%d", patch_fd);
    return android_dlopen_ext(pathbuf, flags, &extinfo);
#endif
}
