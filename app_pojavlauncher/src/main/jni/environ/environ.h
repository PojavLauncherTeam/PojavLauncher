//
// Created by maks on 24.09.2022.
//

#ifndef POJAVLAUNCHER_ENVIRON_H
#define POJAVLAUNCHER_ENVIRON_H

#include <ctxbridges/gl_bridge.h>

struct pojav_environ_s {
    struct ANativeWindow* pojavWindow;
    render_window_t* mainWindowBundle;
    int config_renderer;
    bool force_vsync;
};
extern struct pojav_environ_s *pojav_environ;

#endif //POJAVLAUNCHER_ENVIRON_H
