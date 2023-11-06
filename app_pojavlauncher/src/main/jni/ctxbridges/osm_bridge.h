//
// Created by maks on 18.10.2023.
//
#include <android/native_window.h>
#include <stdbool.h>
#ifndef POJAVLAUNCHER_OSM_BRIDGE_H
#define POJAVLAUNCHER_OSM_BRIDGE_H
#include "osmesa_loader.h"


typedef struct {
    char       state;
    struct ANativeWindow *nativeSurface;
    struct ANativeWindow *newNativeSurface;
    ANativeWindow_Buffer buffer;
    int32_t last_stride;
    bool disable_rendering;
    OSMesaContext context;
} osm_render_window_t;

bool osm_init();
osm_render_window_t* osm_get_current();
osm_render_window_t* osm_init_context(osm_render_window_t* share);
void osm_make_current(osm_render_window_t* bundle);
void osm_swap_buffers();
void osm_setup_window();
void osm_swap_interval(int swapInterval);

#endif //POJAVLAUNCHER_OSM_BRIDGE_H
