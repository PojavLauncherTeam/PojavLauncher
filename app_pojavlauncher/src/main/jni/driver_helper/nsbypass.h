
//
// Created by maks on 05.06.2023.
//

#ifndef POJAVLAUNCHER_NSBYPASS_H
#define POJAVLAUNCHER_NSBYPASS_H

#include <stdbool.h>

bool linker_ns_load(const char* lib_search_path);
void* linker_ns_dlopen(const char* name, int flag);
void* linker_ns_dlopen_unique(const char* tmpdir, const char* name, int flag);
struct android_namespace_t* ns_android_get_exported_namespace(const char*);

#endif //POJAVLAUNCHER_NSBYPASS_H
