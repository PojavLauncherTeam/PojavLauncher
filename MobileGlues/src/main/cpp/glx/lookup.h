//
// Created by Administrator on 2025/1/27.
//

#ifndef MOBILEGLUES_LOOKUP_H
#define MOBILEGLUES_LOOKUP_H

#ifdef __cplusplus
extern "C" {
#endif

void *glXGetProcAddress(const char *name) __attribute__((visibility("default")));

void *glXGetProcAddressARB(const char *name) __attribute__((visibility("default")));

#ifdef __cplusplus
}
#endif

#endif //MOBILEGLUES_LOOKUP_H
