//
// Created by maks on 26.10.2024.
//
#include <string.h>
#include <stdio.h>
#include <dlfcn.h>
#include <linux/limits.h>
void* loader_dlopen(char* primaryName, char* secondaryName, int flags) {
    void* dl_handle;
    if(primaryName == NULL) goto secondary;

    dl_handle = dlopen(primaryName, flags);
    if(dl_handle != NULL) return dl_handle;
    if(secondaryName == NULL) goto dl_error;

    secondary:
    dl_handle = dlopen(secondaryName, flags);
    if(dl_handle == NULL) goto dl_error;
    return dl_handle;

    dl_error:
    printf("%s", dlerror());
    return NULL;
}