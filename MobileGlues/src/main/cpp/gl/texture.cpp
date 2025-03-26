//
// Created by BZLZHH on 2025/1/27.
//

#include "texture.h"

#include <cstring>
#include <malloc.h>
#include <vector>
#include <unordered_map>
#include <android/log.h>

#include "gl.h"
#include "../gles/gles.h"
#include "log.h"
#include "../gles/loader.h"
#include "../includes.h"
#include "glsl/glsl_for_es.h"
#include "mg.h"
#include "framebuffer.h"
#include "pixel.h"

#define DEBUG 0

int nlevel(int size, int level) {
    if(size) {
        size>>=level;
        if(!size) size=1;
    }
    return size;
}

std::unordered_map<GLuint, texture_t> g_textures;
GLuint bound_texture = 0;

void internal_convert(GLenum* internal_format, GLenum* type, GLenum* format) {
    if (format && *format == GL_BGRA)
        *format = GL_RGBA;
    switch (*internal_format) {
        case GL_DEPTH_COMPONENT16:
            if(type)
                *type = GL_UNSIGNED_SHORT;
            break;

        case GL_DEPTH_COMPONENT24:
            if(type)
                *type = GL_UNSIGNED_INT;
            break;

        case GL_DEPTH_COMPONENT32:
            *internal_format = GL_DEPTH_COMPONENT32F;
            if(type)
                *type = GL_FLOAT;
            break;

        case GL_DEPTH_COMPONENT32F:
            if(type)
                *type = GL_FLOAT;
            break;

        case GL_DEPTH_COMPONENT:
            // TODO: Add enableCompatibleMode option
            LOG_D("Find GL_DEPTH_COMPONENT: internalFormat: %s, format: %s, type: %s", glEnumToString(*internal_format), glEnumToString(*format), glEnumToString(*type))
            if(type) {
                *type = GL_UNSIGNED_INT;
            }
            break;

        case GL_DEPTH_STENCIL:
            *internal_format = GL_DEPTH32F_STENCIL8;
            if(type)
                *type = GL_FLOAT_32_UNSIGNED_INT_24_8_REV;
            break;

        case GL_RGB10_A2:
            if(type)
                *type = GL_UNSIGNED_INT_2_10_10_10_REV;
            break;

        case GL_RGB5_A1:
            if(type)
                *type = GL_UNSIGNED_SHORT_5_5_5_1;
            break;

        case GL_COMPRESSED_RED_RGTC1:
        case GL_COMPRESSED_RG_RGTC2:
            LOG_E("GL_COMPRESSED_RED_RGTC1 or GL_COMPRESSED_RG_RGTC2 is not supported!")
            break;

        case GL_SRGB8:
            if(type)
                *type = GL_UNSIGNED_BYTE;
            break;

        case GL_RGBA32F:
        case GL_RGB32F:
        case GL_RG32F:
        case GL_R32F:
            if(type)
                *type = GL_FLOAT;
            break;

        case GL_RGB9_E5:
            if(type)
                *type = GL_UNSIGNED_INT_5_9_9_9_REV;
            break;
            
        case GL_R11F_G11F_B10F:
            if(type)
                *type = GL_UNSIGNED_INT_10F_11F_11F_REV;
            if (format)
                *format = GL_RGB;
            break;

        case GL_RGBA32UI:
        case GL_RGB32UI:
        case GL_RG32UI:
        case GL_R32UI:
            if(type)
                *type = GL_UNSIGNED_INT;
            break;

        case GL_RGBA32I:
        case GL_RGB32I:
        case GL_RG32I:
        case GL_R32I:
            if(type)
                *type = GL_INT;
            break;

        case GL_RGBA16: {
            if (g_gles_caps.GL_EXT_texture_norm16) {
                if(type)
                    *type = GL_UNSIGNED_SHORT;
            } else {
                *internal_format = GL_RGBA16F;
                if(type)
                    *type = GL_FLOAT;
            }
            break;
        }
        case GL_RGBA8:
            if(type)
                *type = GL_UNSIGNED_BYTE;
            if (format)
                *format = GL_RGBA;
            break;

        case GL_RGBA:
            if(type)
                *type = GL_UNSIGNED_BYTE;
            if (format)
                *format = GL_RGBA;
            break;
            
        case GL_RGBA16F:
        case GL_R16F:
            if(type)
                *type = GL_HALF_FLOAT;
            break;

        case GL_R16:
            *internal_format = GL_R16F;
            if(type)
                *type = GL_FLOAT;
            break;

        case GL_RGB16:
            *internal_format = GL_RGB16F;
            if(type)
                *type = GL_HALF_FLOAT;
            if(format)
                *format = GL_RGB;
            break;
            
        case GL_RGB16F:
            if(type)
                *type = GL_HALF_FLOAT;
            if(format)
                *format = GL_RGB;
            break;

        case GL_RG16:
        case GL_RG16F:
            *internal_format = GL_RG16F;
            if(type)
                *type = GL_HALF_FLOAT;
            if(format)
                *format = GL_RG;
            break;

        case GL_R8:
            if (format)
                *format = GL_RED;
            if(type)
                *type = GL_UNSIGNED_BYTE;
            break;
        case GL_R8UI:
            if (format)
                *format = GL_RED_INTEGER;
            if(type)
                *type = GL_UNSIGNED_BYTE;
            break;

        case GL_RGB8_SNORM:
        case GL_RGBA8_SNORM:
            if(type)
                *type = GL_BYTE;
            break;

        default:
            if (*internal_format == GL_RGB8) {
                if (type && *type != GL_UNSIGNED_BYTE)
                    *type = GL_UNSIGNED_BYTE;
                if (format)
                    *format = GL_RGB;
            }
            else if (type && *internal_format == GL_RGBA16_SNORM && *type != GL_SHORT) {
                *type = GL_SHORT; 
            }
            break;
    }
}

void glTexParameterf(GLenum target, GLenum pname, GLfloat param) {
    LOG()
    pname = pname_convert(pname);
    LOG_D("glTexParameterf, target: %d, pname: %d, param: %f",target, pname, param)

    if (pname == GL_TEXTURE_LOD_BIAS_QCOM && !g_gles_caps.GL_QCOM_texture_lod_bias) {
        LOG_D("Does not support GL_QCOM_texture_lod_bias, skipped!")
        return;
    }

    GLES.glTexParameterf(target,pname, param);
    CHECK_GL_ERROR
}

void glTexImage1D(GLenum target, GLint level, GLint internalFormat, GLsizei width, GLint border, GLenum format, GLenum type, const GLvoid* pixels) {
    LOG()
    LOG_D("glTexImage1D not implemented!")
    LOG_D("glTexImage1D, target: %d, level: %d, internalFormat: %d, width: %d, border: %d, format: %d, type: %d",
          target, level, internalFormat, width, border, format, type)
    return;
    internal_convert(reinterpret_cast<GLenum *>(&internalFormat), & type, &format);

    GLenum rtarget = map_tex_target(target);
    if (rtarget == GL_PROXY_TEXTURE_1D) {
        int max1 = 4096;
        GLES.glGetIntegerv(GL_MAX_TEXTURE_SIZE, &max1);
        set_gl_state_proxy_width(((width << level) > max1) ? 0 : width);
        set_gl_state_proxy_intformat(internalFormat);
        return;
    }

    CHECK_GL_ERROR
}

void glTexImage2D(GLenum target, GLint level,GLint internalFormat,GLsizei width, GLsizei height,GLint border, GLenum format, GLenum type,const GLvoid* pixels) {
    LOG()
    auto& tex = g_textures[bound_texture];
    tex.internal_format = internalFormat;
    GLenum transfer_format = format;
//    tex.format = format;
    LOG_D("mg_glTexImage2D,target: %s,level: %d,internalFormat: %s->%s,width: %d,height: %d,border: %d,format: %s,type: %s, pixels: 0x%x",
          glEnumToString(target),level,glEnumToString(internalFormat),glEnumToString(internalFormat),
          width,height,border,glEnumToString(format),glEnumToString(type), pixels)
    internal_convert(reinterpret_cast<GLenum *>(&internalFormat), &type, &format);
    LOG_D("GLES.glTexImage2D,target: %s,level: %d,internalFormat: %s->%s,width: %d,height: %d,border: %d,format: %s,type: %s, pixels: 0x%x",
          glEnumToString(target),level,glEnumToString(internalFormat),glEnumToString(internalFormat),
          width,height,border,glEnumToString(format),glEnumToString(type), pixels)
    GLenum rtarget = map_tex_target(target);
    if(rtarget == GL_PROXY_TEXTURE_2D) {
        int max1 = 4096;
        GLES.glGetIntegerv(GL_MAX_TEXTURE_SIZE, &max1);
        set_gl_state_proxy_width(((width<<level)>max1)?0:width);
        set_gl_state_proxy_height(((height<<level)>max1)?0:height);
        set_gl_state_proxy_intformat(internalFormat);
        return;
    }

    if (transfer_format == GL_BGRA && tex.format != transfer_format && internalFormat == GL_RGBA8
                    && width <= 128 && height <= 128) {  // xaero has 64x64 tiles...hack here
        LOG_D("Detected GL_BGRA format @ tex = %d, do swizzle", bound_texture)
        if (tex.swizzle_param[0] == 0) { // assert this as never called glTexParameteri(..., GL_TEXTURE_SWIZZLE_R, ...)
            tex.swizzle_param[0] = GL_RED;
            tex.swizzle_param[1] = GL_GREEN;
            tex.swizzle_param[2] = GL_BLUE;
            tex.swizzle_param[3] = GL_ALPHA;
        }

        GLint r = tex.swizzle_param[0];
        GLint g = tex.swizzle_param[1];
        GLint b = tex.swizzle_param[2];
        GLint a = tex.swizzle_param[3];
        tex.swizzle_param[0] = g;
        tex.swizzle_param[1] = b;
        tex.swizzle_param[2] = a;
        tex.swizzle_param[3] = r;
        tex.format = transfer_format;

        GLES.glTexParameteri(target, GL_TEXTURE_SWIZZLE_R, tex.swizzle_param[0]);
        GLES.glTexParameteri(target, GL_TEXTURE_SWIZZLE_G, tex.swizzle_param[1]);
        GLES.glTexParameteri(target, GL_TEXTURE_SWIZZLE_B, tex.swizzle_param[2]);
        GLES.glTexParameteri(target, GL_TEXTURE_SWIZZLE_A, tex.swizzle_param[3]);
        CHECK_GL_ERROR
    }

    GLES.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);

    CHECK_GL_ERROR
}

void glTexImage3D(GLenum target, GLint level, GLint internalFormat, GLsizei width, GLsizei height, GLsizei depth, GLint border, GLenum format, GLenum type, const GLvoid* pixels) {
    LOG()
    LOG_D("glTexImage3D, target: 0x%x, level: %d, internalFormat: 0x%x, width: 0x%x, height: %d, depth: %d, border: %d, format: 0x%x, type: %d",
          target, level, internalFormat, width, height, depth, border, format, type)

    internal_convert(reinterpret_cast<GLenum *>(&internalFormat), &type, &format);

    GLenum rtarget = map_tex_target(target);
    if (rtarget == GL_PROXY_TEXTURE_3D) {
        int max1 = 4096;
        GLES.glGetIntegerv(GL_MAX_TEXTURE_SIZE, &max1);
        set_gl_state_proxy_width(((width << level) > max1) ? 0 : width);
        set_gl_state_proxy_height(((height << level) > max1) ? 0 : height);
        //set_gl_state_proxy_depth(((depth << level) > max1) ? 0 : depth);
        set_gl_state_proxy_intformat(internalFormat);
        return;
    }

    GLES.glTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, pixels);

    CHECK_GL_ERROR
}

void glTexStorage1D(GLenum target, GLsizei levels, GLenum internalFormat, GLsizei width) {
    LOG()
    LOG_D("glTexStorage1D not implemented!")
    LOG_D("glTexStorage1D, target: %d, levels: %d, internalFormat: %d, width: %d",
          target, levels, internalFormat, width)
    return;
    internal_convert(&internalFormat,nullptr,nullptr);

    CHECK_GL_ERROR
}

void glTexStorage2D(GLenum target, GLsizei levels, GLenum internalFormat, GLsizei width, GLsizei height) {
    LOG()
    LOG_D("glTexStorage2D, target: %d, levels: %d, internalFormat: %d, width: %d, height: %d",
          target, levels, internalFormat, width, height)

    internal_convert(&internalFormat,nullptr,nullptr);

    GLES.glTexStorage2D(target, levels, internalFormat, width, height);

    GLenum ERR = GLES.glGetError();
    if (ERR != GL_NO_ERROR)
        LOG_E("glTexStorage2D ERROR: %d", ERR)
}

void glTexStorage3D(GLenum target, GLsizei levels, GLenum internalFormat, GLsizei width, GLsizei height, GLsizei depth) {
    LOG()
    LOG_D("glTexStorage3D, target: %d, levels: %d, internalFormat: %d, width: %d, height: %d, depth: %d",
          target, levels, internalFormat, width, height, depth)

    internal_convert(&internalFormat,nullptr,nullptr);

    GLES.glTexStorage3D(target, levels, internalFormat, width, height, depth);

    CHECK_GL_ERROR
}

void glCopyTexImage1D(GLenum target, GLint level, GLenum internalFormat, GLint x, GLint y, GLsizei width, GLint border) {
    LOG()
    LOG_D("glCopyTexImage1D not implemented!")
    LOG_D("glCopyTexImage1D, target: %d, level: %d, internalFormat: %d, x: %d, y: %d, width: %d, border: %d",
          target, level, internalFormat, x, y, width, border)
    return;

    CHECK_GL_ERROR
}

static int is_depth_format(GLenum format) {
    switch(format) {
        case GL_DEPTH_COMPONENT:
        case GL_DEPTH_COMPONENT16:
        case GL_DEPTH_COMPONENT24:
        case GL_DEPTH_COMPONENT32F:
            return 1;
        default:
            return 0;
    }
}

static GLenum get_binding_for_target(GLenum target) {
    switch(target) {
        case GL_TEXTURE_2D: return GL_TEXTURE_BINDING_2D;
        case GL_TEXTURE_CUBE_MAP_POSITIVE_X:
        case GL_TEXTURE_CUBE_MAP_NEGATIVE_X:
        case GL_TEXTURE_CUBE_MAP_POSITIVE_Y:
        case GL_TEXTURE_CUBE_MAP_NEGATIVE_Y:
        case GL_TEXTURE_CUBE_MAP_POSITIVE_Z:
        case GL_TEXTURE_CUBE_MAP_NEGATIVE_Z:
            return GL_TEXTURE_BINDING_CUBE_MAP;
        default: return 0;
    }
}

void glCopyTexImage2D(GLenum target, GLint level, GLenum internalFormat, GLint x, GLint y, GLsizei width, GLsizei height, GLint border) {
    LOG()

    INIT_CHECK_GL_ERROR

    GLint realInternalFormat;
    GLES.glGetTexLevelParameteriv(target, level, GL_TEXTURE_INTERNAL_FORMAT, &realInternalFormat);
    internalFormat = (GLenum)realInternalFormat;

    LOG_D("glCopyTexImage2D, target: %d, level: %d, internalFormat: %d, x: %d, y: %d, width: %d, height: %d, border: %d",
          target, level, internalFormat, x, y, width, height, border)

    if (is_depth_format(internalFormat)) {
        GLenum format = GL_DEPTH_COMPONENT;
        GLenum type = GL_UNSIGNED_INT;
        internal_convert(&internalFormat, &type, &format);
        GLES.glTexImage2D(target, level, (GLint)internalFormat, width, height, border, format, type, nullptr);
        CHECK_GL_ERROR_NO_INIT
        GLint prevDrawFBO;
        glGetIntegerv(GL_DRAW_FRAMEBUFFER_BINDING, &prevDrawFBO);
        CHECK_GL_ERROR_NO_INIT
        GLuint tempDrawFBO;
        glGenFramebuffers(1, &tempDrawFBO);
        CHECK_GL_ERROR_NO_INIT
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, tempDrawFBO);
        CHECK_GL_ERROR_NO_INIT
        GLint currentTex;
        glGetIntegerv(get_binding_for_target(target), &currentTex);
        CHECK_GL_ERROR_NO_INIT
        glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, target, currentTex, level);
        CHECK_GL_ERROR_NO_INIT

        if (glCheckFramebufferStatus(GL_DRAW_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            CHECK_GL_ERROR_NO_INIT
            glDeleteFramebuffers(1, &tempDrawFBO);
            CHECK_GL_ERROR_NO_INIT
            glBindFramebuffer(GL_DRAW_FRAMEBUFFER, prevDrawFBO);
            CHECK_GL_ERROR_NO_INIT
            return;
        }
        CHECK_GL_ERROR_NO_INIT

        GLES.glBlitFramebuffer(x, y, x + width, y + height,
                               0, 0, width, height,
                               GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        CHECK_GL_ERROR_NO_INIT

        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, prevDrawFBO);
        CHECK_GL_ERROR_NO_INIT
        glDeleteFramebuffers(1, &tempDrawFBO);
        CHECK_GL_ERROR_NO_INIT
    } else {
        GLES.glCopyTexImage2D(target, level, internalFormat, x, y, width, height, border);
        CHECK_GL_ERROR_NO_INIT
    }

    CHECK_GL_ERROR_NO_INIT
}

void glCopyTexSubImage2D(GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint x, GLint y, GLsizei width, GLsizei height) {
    LOG()
    GLint internalFormat;
    GLES.glGetTexLevelParameteriv(target, level, GL_TEXTURE_INTERNAL_FORMAT, &internalFormat);

    LOG_D("glCopyTexSubImage2D, target: %d, level: %d, ......, internalFormat: %d", target, level, internalFormat)

    if (is_depth_format((GLenum)internalFormat)) {
        GLint prevReadFBO, prevDrawFBO;
        glGetIntegerv(GL_READ_FRAMEBUFFER_BINDING, &prevReadFBO);
        glGetIntegerv(GL_DRAW_FRAMEBUFFER_BINDING, &prevDrawFBO);

        GLuint tempDrawFBO;
        glGenFramebuffers(1, &tempDrawFBO);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, tempDrawFBO);

        GLint currentTex;
        glGetIntegerv(get_binding_for_target(target), &currentTex);
        glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, target, currentTex, level);

        if (glCheckFramebufferStatus(GL_DRAW_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            glDeleteFramebuffers(1, &tempDrawFBO);
            glBindFramebuffer(GL_DRAW_FRAMEBUFFER, prevDrawFBO);
            return;
        }

        GLES.glBlitFramebuffer(x, y, x + width, y + height,
                               xoffset, yoffset, xoffset + width, yoffset + height,
                               GL_DEPTH_BUFFER_BIT, GL_NEAREST);

        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, prevDrawFBO);
        glDeleteFramebuffers(1, &tempDrawFBO);
    } else {
        GLES.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
    }

    CHECK_GL_ERROR
}

void glRenderbufferStorage(GLenum target, GLenum internalFormat, GLsizei width, GLsizei height) {
    LOG()

    INIT_CHECK_GL_ERROR_FORCE

    CLEAR_GL_ERROR_NO_INIT

    LOG_D("mg.glRenderbufferStorage, target: %s, internalFormat: %s, width: %d, height: %d",
          glEnumToString(target), glEnumToString(internalFormat), width, height)

    GLint realInternalFormat;
    GLES.glGetTexLevelParameteriv(target, 0, GL_TEXTURE_INTERNAL_FORMAT, &realInternalFormat);
    ERR = GLES.glGetError();
    if (realInternalFormat != 0 && ERR == GL_NO_ERROR)
        internalFormat = (GLenum)realInternalFormat;
    else
        internalFormat = GL_DEPTH_COMPONENT24;

    CLEAR_GL_ERROR_NO_INIT

    LOG_D("es.glRenderbufferStorage, target: %s, internalFormat: %s, width: %d, height: %d",
          glEnumToString(target), glEnumToString(internalFormat), width, height)

    GLES.glRenderbufferStorage(target, internalFormat, width, height);

    CHECK_GL_ERROR_NO_INIT
}

void glRenderbufferStorageMultisample(GLenum target, GLsizei samples, GLenum internalFormat, GLsizei width, GLsizei height) {
    LOG()

    INIT_CHECK_GL_ERROR_FORCE

    CLEAR_GL_ERROR_NO_INIT

    GLint realInternalFormat;
    GLES.glGetTexLevelParameteriv(target, 0, GL_TEXTURE_INTERNAL_FORMAT, &realInternalFormat);
    ERR = GLES.glGetError();
    if (realInternalFormat != 0 && ERR == GL_NO_ERROR)
        internalFormat = (GLenum)realInternalFormat;
    else
        internalFormat = GL_DEPTH_COMPONENT24;


    LOG_D("glRenderbufferStorageMultisample, target: %d, samples: %d, internalFormat: %d, width: %d, height: %d",
          target, samples, internalFormat, width, height)

    GLES.glRenderbufferStorageMultisample(target, samples, internalFormat, width, height);

    CHECK_GL_ERROR_NO_INIT
}

void glGetTexLevelParameterfv(GLenum target, GLint level,GLenum pname, GLfloat *params) {
    LOG()
    LOG_D("glGetTexLevelParameterfv,target: %d, level: %d, pname: %d",target,level,pname)
    GLenum rtarget = map_tex_target(target);
    if (rtarget==GL_PROXY_TEXTURE_2D) {
        switch (pname) {
            case GL_TEXTURE_WIDTH:
                (*params) = (float)nlevel(gl_state->proxy_width, level);
                break;
            case GL_TEXTURE_HEIGHT:
                (*params) = (float)nlevel(gl_state->proxy_height, level);
                break;
            case GL_TEXTURE_INTERNAL_FORMAT:
                (*params) = (float)gl_state->proxy_intformat;
                break;
            default:
                return;
        }
    }
    GLES.glGetTexLevelParameterfv(target,level,pname,params);
    CHECK_GL_ERROR
}

void glGetTexLevelParameteriv(GLenum target, GLint level,GLenum pname, GLint *params) {
    LOG()
    LOG_D("glGetTexLevelParameteriv,target: %s, level: %d, pname: %s",glEnumToString(target),level,glEnumToString(pname))
    GLenum rtarget = map_tex_target(target);
    if (rtarget==GL_PROXY_TEXTURE_2D) {
        switch (pname) {
            case GL_TEXTURE_WIDTH:
                (*params) = nlevel(gl_state->proxy_width, level);
                break;
            case GL_TEXTURE_HEIGHT:
                (*params) = nlevel(gl_state->proxy_height, level);
                break;
            case GL_TEXTURE_INTERNAL_FORMAT:
                (*params) = (GLint)gl_state->proxy_intformat;
                break;
            default:
                return;
        }
    }
    LOG_D("es.glGetTexLevelParameteriv,target: %s, level: %d, pname: %s",glEnumToString(target),level,glEnumToString(pname))
    GLES.glGetTexLevelParameteriv(target,level,pname,params);
    CHECK_GL_ERROR
}

void glTexParameteriv(GLenum target, GLenum pname, const GLint* params) {
    LOG_D("glTexParameteriv, target: %s, pname: %s, params[0]: %s",
          params, glEnumToString(pname), params ? glEnumToString(params[0]) : "0")

    if (pname == GL_TEXTURE_SWIZZLE_RGBA) {
        LOG_D("find GL_TEXTURE_SWIZZLE_RGBA, now use glTexParameteri")
        if (params) {
            // deferred those call to draw call?
            GLES.glTexParameteri(target, GL_TEXTURE_SWIZZLE_R, params[0]);
            GLES.glTexParameteri(target, GL_TEXTURE_SWIZZLE_G, params[1]);
            GLES.glTexParameteri(target, GL_TEXTURE_SWIZZLE_B, params[2]);
            GLES.glTexParameteri(target, GL_TEXTURE_SWIZZLE_A, params[3]);

            // save states for now
            texture_t& tex = g_textures[bound_texture];
            tex.swizzle_param[0] = params[0];
            tex.swizzle_param[1] = params[1];
            tex.swizzle_param[2] = params[2];
            tex.swizzle_param[3] = params[3];
        } else {
            LOG_E("glTexParameteriv: params is nullptr for GL_TEXTURE_SWIZZLE_RGBA")
        }
    } else {
        GLES.glTexParameteriv(target, pname, params);
    }

    CHECK_GL_ERROR
}

void glTexSubImage2D(GLenum target, GLint level, GLint xoffset, GLint yoffset, GLsizei width, GLsizei height, GLenum format, GLenum type, const void *pixels) {
    LOG()

    LOG_D("glTexSubImage2D, target = %s, level = %d, xoffset = %d, yoffset = %d, width = %d, height = %d, format = %s, type = %s, pixels = 0x%x",
            glEnumToString(target), level, xoffset, yoffset, width, height, glEnumToString(format),
          glEnumToString(type), pixels)

    if (format == GL_BGRA && type == GL_UNSIGNED_INT_8_8_8_8) {
        format = GL_RGBA;
        type = GL_UNSIGNED_BYTE;
    }

    GLES.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);

    CHECK_GL_ERROR
}

void glBindTexture(GLenum target, GLuint texture) {
    LOG()
    LOG_D("glBindTexture(%s, %d)", glEnumToString(target), texture)
    INIT_CHECK_GL_ERROR
    GLES.glBindTexture(target, texture);
    CHECK_GL_ERROR_NO_INIT

    g_textures[texture] = {
            .target = target,
            .texture = texture,
            .format = 0,
            .swizzle_param = {0}
    };
    bound_texture = texture;
}

void glDeleteTextures(GLsizei n, const GLuint *textures) {
    LOG()
    INIT_CHECK_GL_ERROR
    GLES.glDeleteTextures(n, textures);
    CHECK_GL_ERROR_NO_INIT

    for (GLsizei i = 0; i < n; ++i) {
        g_textures.erase(textures[i]);
        if (bound_texture == textures[i])
            bound_texture = 0;
    }
}

void glGenerateTextureMipmap(GLuint texture) {
    LOG()
    GLint currentTexture;
    auto& tex = g_textures[bound_texture];
    GLenum target = tex.target;
    GLenum binding = get_binding_for_target(target);
    if (binding == 0) return;
    glGetIntegerv(binding, &currentTexture);
    glBindTexture(target, texture);
    glGenerateMipmap(target);
    glBindTexture(target, currentTexture);
}

void glGetTexImage(GLenum target, GLint level, GLenum format, GLenum type, void* pixels) {
    LOG()
    LOG_D("glGetTexImage, target = %s, level = %d, format = %s, type = %s, pixel = 0x%x",
          glEnumToString(target), level, glEnumToString(format), glEnumToString(type), pixels)
          
    GLint prevFBO;
    GLES.glGetIntegerv(GL_FRAMEBUFFER_BINDING, &prevFBO);
    GLenum bindingTarget = get_binding_for_target(target);
    if (bindingTarget == 0) return;
    GLint oldTexBinding;
    GLES.glActiveTexture(GL_TEXTURE0);
    GLES.glGetIntegerv(bindingTarget, &oldTexBinding);
    auto texture = static_cast<GLuint>(oldTexBinding);
    if (texture == 0) return;
    GLint width, height;
    GLES.glBindTexture(target, texture);
    GLES.glGetTexLevelParameteriv(target, level, GL_TEXTURE_WIDTH, &width);
    GLES.glGetTexLevelParameteriv(target, level, GL_TEXTURE_HEIGHT, &height);
    GLES.glBindTexture(target, oldTexBinding);
    if (width <= 0 || height <= 0) return;
    GLuint fbo;
    glGenFramebuffers(1, &fbo);
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    if (target == GL_TEXTURE_2D || (target >= GL_TEXTURE_CUBE_MAP_POSITIVE_X && target <= GL_TEXTURE_CUBE_MAP_NEGATIVE_Z)) {
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, target, texture, level);
    } else {
        glDeleteFramebuffers(1, &fbo);
        glBindFramebuffer(GL_FRAMEBUFFER, prevFBO);
        return;
    }
    if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
        glDeleteFramebuffers(1, &fbo);
        glBindFramebuffer(GL_FRAMEBUFFER, prevFBO);
        return;
    }
    GLint oldViewport[4];
    GLES.glGetIntegerv(GL_VIEWPORT, oldViewport);
    GLES.glViewport(0, 0, width, height);
    GLint oldPackAlignment;
    glGetIntegerv(GL_PACK_ALIGNMENT, &oldPackAlignment);
    glPixelStorei(GL_PACK_ALIGNMENT, 1);
    glReadBuffer(GL_COLOR_ATTACHMENT0);

    if (pixels != NULL && format == GL_BGRA && (type == GL_UNSIGNED_INT_8_8_8_8 || type == GL_UNSIGNED_INT_8_8_8_8_REV)) {
        void *read_pixels = malloc(width * height * 4);
        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, read_pixels);
        pixel_convert(read_pixels, &pixels, width, height, GL_RGBA, GL_UNSIGNED_BYTE, format, GL_UNSIGNED_BYTE, 0, 1);
        free(read_pixels);
    } else {
        glReadPixels(0, 0, width, height, format, type, pixels);
    }


//    glReadPixels(0, 0, width, height, format, type, pixels);

    glPixelStorei(GL_PACK_ALIGNMENT, oldPackAlignment);
    GLES.glViewport(oldViewport[0], oldViewport[1], oldViewport[2], oldViewport[3]);
    glDeleteFramebuffers(1, &fbo);
    glBindFramebuffer(GL_FRAMEBUFFER, prevFBO);
}

#if GLOBAL_DEBUG || DEBUG
#include <fstream>
#include "../config/config.h"
#endif

void glReadPixels(GLint x, GLint y, GLsizei width, GLsizei height, GLenum format, GLenum type, void *pixels) {
    LOG()
    LOG_D("glReadPixels, x=%d, y=%d, width=%d, height=%d, format=0x%x, type=0x%x, pixels=0x%x",
          x, y, width, height, format, type, pixels)
          
    static int count = 0;
    GLenum prevFormat = format;
    
    if (format == GL_BGRA && type == GL_UNSIGNED_INT_8_8_8_8) {
        format = GL_RGBA;
        type = GL_UNSIGNED_BYTE;
    }
    LOG_D("glReadPixels converted, x=%d, y=%d, width=%d, height=%d, format=0x%x, type=0x%x, pixels=0x%x",
          x, y, width, height, format, type, pixels)
    GLES.glReadPixels(x, y, width, height, format, type, pixels);

#if GLOBAL_DEBUG || DEBUG
    if (prevFormat == GL_BGRA && type == GL_UNSIGNED_BYTE) {
        std::vector<uint8_t> px(width * height * sizeof(uint8_t) * 4, 0);
        GLES.glBindBuffer(GL_PIXEL_PACK_BUFFER, 0);
        GLES.glReadPixels(x, y, width, height, format, type, px.data());

        std::fstream fs(std::string(concatenate(mg_directory_path, "/readpixels/")) + std::to_string(count++) + ".bin", std::ios::out | std::ios::binary | std::ios::trunc);
        fs.write((const char*)px.data(), px.size());
        fs.close();
    }
#endif
    CHECK_GL_ERROR
}

void glTexParameteri(GLenum target, GLenum pname, GLint param) {
    LOG()
    pname = pname_convert(pname);
    LOG_D("glTexParameteri, pname: 0x%x", pname)

    if (pname == GL_TEXTURE_LOD_BIAS_QCOM && !g_gles_caps.GL_QCOM_texture_lod_bias) {
        LOG_D("Does not support GL_QCOM_texture_lod_bias, skipped!")
        return;
    }

    GLES.glTexParameteri(target,pname,param);
    CHECK_GL_ERROR
}

void glClearTexImage(GLuint texture, GLint level, GLenum format, GLenum type, const void *data)
{
    LOG()
    LOG_D("glClearTexImage, texture: %d, level: %d, format: %d, type: %d", texture, level, format, type)
    INIT_CHECK_GL_ERROR_FORCE
    GLuint fbo;
    glGenFramebuffers(1, &fbo);
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    CHECK_GL_ERROR_NO_INIT

    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, level);

    CHECK_GL_ERROR_NO_INIT
    if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
        LOG_D("  -> exit")
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glDeleteFramebuffers(1, &fbo);
        CHECK_GL_ERROR_NO_INIT
        return;
    }
    
    GLES.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    CHECK_GL_ERROR_NO_INIT

    if (data != nullptr) {
        if (format == GL_RGBA && type == GL_UNSIGNED_BYTE) {
            auto* byteData = static_cast<const GLubyte*>(data);
            GLES.glClearColor((float)byteData[0] / 255.0f, (float)byteData[1] / 255.0f, (float)byteData[2] / 255.0f, (float)byteData[3] / 255.0f);
        }
        else if (format == GL_RGB && type == GL_UNSIGNED_BYTE) {
            auto* byteData = static_cast<const GLubyte*>(data);
            GLES.glClearColor((float)byteData[0] / 255.0f, (float)byteData[1] / 255.0f, (float)byteData[2] / 255.0f, 1.0f);
        }
        else if (format == GL_RGBA && type == GL_FLOAT) {
            auto* floatData = static_cast<const GLfloat*>(data);
            GLES.glClearColor(floatData[0], floatData[1], floatData[2], floatData[3]);
        }
        else if (format == GL_RGB && type == GL_FLOAT) {
            auto* floatData = static_cast<const GLfloat*>(data);
            GLES.glClearColor(floatData[0], floatData[1], floatData[2], 1.0f);
        }
        else if (format == GL_DEPTH_COMPONENT && type == GL_FLOAT) {
            auto* depthData = static_cast<const GLfloat*>(data);
            GLES.glClearDepthf(depthData[0]);
            GLES.glClear(GL_DEPTH_BUFFER_BIT);
        }
        else if (format == GL_STENCIL_INDEX && type == GL_UNSIGNED_BYTE) {
            auto* stencilData = static_cast<const GLubyte*>(data);
            GLES.glClearStencil(stencilData[0]);
            GLES.glClear(GL_STENCIL_BUFFER_BIT);
        }
    }
    CHECK_GL_ERROR_NO_INIT

    if (format == GL_DEPTH_COMPONENT || format == GL_STENCIL_INDEX) {
        GLES.glClear(GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        CHECK_GL_ERROR_NO_INIT
    } else {
        GLES.glClear(GL_COLOR_BUFFER_BIT);
        CHECK_GL_ERROR_NO_INIT
    }

    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    glDeleteFramebuffers(1, &fbo);
    CHECK_GL_ERROR_NO_INIT
}

void glPixelStorei(GLenum pname, GLint param) {
    LOG_D("glPixelStorei, pname = %s, param = %d", glEnumToString(pname), param)
    GLES.glPixelStorei(pname, param);
    CHECK_GL_ERROR
}