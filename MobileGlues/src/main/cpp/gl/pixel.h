//
// Created by Swung 0x48 on 2025/2/27.
//

#ifndef MOBILEGLUES_PIXEL_H
#define MOBILEGLUES_PIXEL_H

#include "gl.h"
#include "../gles/gles.h"
#include "log.h"

#ifdef __BIG_ENDIAN__
#define GL_INT8_REV     GL_UNSIGNED_INT_8_8_8_8
#define GL_INT8         GL_UNSIGNED_INT_8_8_8_8_REV
#else
#define GL_INT8_REV     GL_UNSIGNED_INT_8_8_8_8_REV
#define GL_INT8         GL_UNSIGNED_INT_8_8_8_8
#endif

typedef struct {
    GLenum type;
    GLint red, green, blue, alpha;
    int maxv;
} colorlayout_t;

typedef struct {
    GLfloat r, g, b, a;
} pixel_t;

#define widthalign(width, align) ((((uintptr_t)(width))+((uintptr_t)(align)-1))&(~((uintptr_t)(align)-1)))

GLsizei gl_sizeof(GLenum type);

GLsizei pixel_sizeof(GLenum format, GLenum type);

GLboolean is_type_packed(GLenum type);

bool pixel_convert(const GLvoid *src, GLvoid **dst,
                   GLuint width, GLuint height,
                   GLenum src_format, GLenum src_type,
                   GLenum dst_format, GLenum dst_type, GLuint stride, GLuint align);

#endif //MOBILEGLUES_PIXEL_H
