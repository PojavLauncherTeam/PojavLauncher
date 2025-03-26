//
// Created by BZLZHH on 2025/1/27.
//

#ifndef MOBILEGLUES_MG_H
#define MOBILEGLUES_MG_H

#include <cstring>
#include <malloc.h>
#include <cstdlib>
#include <android/log.h>

#include "gl.h"
#include "../gles/gles.h"
#include "log.h"
#include "../gles/loader.h"
#include "../includes.h"
#include "glsl/glsl_for_es.h"
#include "../config/config.h"

#ifdef __cplusplus
extern "C" {
#endif

#define FUNC_GL_STATE_SIZEI(name) \
void set_gl_state_##name (GLsizei value) { \
    gl_state->name=value; \
    LOG_D(" -> gl_state: %s is %d",#name,value); \
}
#define FUNC_GL_STATE_ENUM(name) \
void set_gl_state_##name (GLenum value) { \
    gl_state->name=value; \
    LOG_D(" -> gl_state: %s is %d",#name,value); \
}
#define FUNC_GL_STATE_SIZEI_DECLARATION(name) void set_gl_state_##name (GLsizei value);
#define FUNC_GL_STATE_ENUM_DECLARATION(name) void set_gl_state_##name (GLenum value);

FUNC_GL_STATE_SIZEI_DECLARATION(proxy_width)

FUNC_GL_STATE_SIZEI_DECLARATION(proxy_height)

FUNC_GL_STATE_ENUM_DECLARATION(proxy_intformat)

struct hardware_s {
    unsigned int es_version;
};
typedef struct hardware_s *hardware_t;
extern hardware_t hardware;

struct gl_state_s {
    GLsizei proxy_width;
    GLsizei proxy_height;
    GLenum proxy_intformat;
};
typedef struct gl_state_s *gl_state_t;
extern gl_state_t gl_state;

GLenum pname_convert(GLenum pname);

GLenum map_tex_target(GLenum target);

void start_log();

void write_log(const char *format, ...);

void clear_log();

#ifdef __cplusplus
}
#endif

#endif //MOBILEGLUES_MG_H
