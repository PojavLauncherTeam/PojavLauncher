//
// Created by maks on 18.10.2023.
//

#ifndef POJAVLAUNCHER_BRIDGE_TBL_H
#define POJAVLAUNCHER_BRIDGE_TBL_H

#include <ctxbridges/common.h>
#include <ctxbridges/gl_bridge.h>
#include <ctxbridges/osm_bridge.h>

typedef basic_render_window_t* (*br_init_context_t)(basic_render_window_t* share);
typedef void (*br_make_current_t)(basic_render_window_t* bundle);
typedef basic_render_window_t* (*br_get_current_t)();

bool (*br_init)() = NULL;
br_init_context_t br_init_context = NULL;
br_make_current_t br_make_current = NULL;
br_get_current_t br_get_current = NULL;
void (*br_swap_buffers)() = NULL;
void (*br_setup_window)() = NULL;
void (*br_swap_interval)(int swapInterval) = NULL;


void set_osm_bridge_tbl() {
    br_init = osm_init;
    br_init_context = (br_init_context_t) osm_init_context;
    br_make_current = (br_make_current_t) osm_make_current;
    br_get_current = (br_get_current_t) osm_get_current;
    br_swap_buffers = osm_swap_buffers;
    br_setup_window = osm_setup_window;
    br_swap_interval = osm_swap_interval;
}

void set_gl_bridge_tbl() {
    br_init = gl_init;
    br_init_context = (br_init_context_t) gl_init_context;
    br_make_current = (br_make_current_t) gl_make_current;
    br_swap_buffers = gl_swap_buffers;
    br_setup_window = gl_setup_window;
    br_swap_interval = gl_swap_interval;
}

#endif //POJAVLAUNCHER_BRIDGE_TBL_H
