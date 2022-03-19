#include <jni.h>
#include <assert.h>
#include <dlfcn.h>

#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>

#include <EGL/egl.h>
#include <GL/osmesa.h>

#ifdef GLES_TEST
#include <GLES2/gl2.h>
#endif

#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <android/rect.h>
#include <string.h>
#include "utils.h"
// region OSMESA internals

struct pipe_screen;

//only get what we need to access/modify
struct st_manager
{
   struct pipe_screen *screen;
};
struct st_context_iface
{
   void *st_context_private;
};
struct zink_device_info
{
   bool have_EXT_conditional_rendering;
   bool have_EXT_transform_feedback;
};
struct zink_screen
{
   struct zink_device_info info;
};

enum st_attachment_type {
    ST_ATTACHMENT_FRONT_LEFT,
    ST_ATTACHMENT_BACK_LEFT,
    ST_ATTACHMENT_FRONT_RIGHT,
    ST_ATTACHMENT_BACK_RIGHT,
    ST_ATTACHMENT_DEPTH_STENCIL,
    ST_ATTACHMENT_ACCUM,
    ST_ATTACHMENT_SAMPLE,

    ST_ATTACHMENT_COUNT,
    ST_ATTACHMENT_INVALID = -1
};
enum pipe_format {
    PIPE_FORMAT_NONE,
    PIPE_FORMAT_B8G8R8A8_UNORM,
    PIPE_FORMAT_B8G8R8X8_UNORM,
    PIPE_FORMAT_A8R8G8B8_UNORM,
    PIPE_FORMAT_X8R8G8B8_UNORM,
    PIPE_FORMAT_B5G5R5A1_UNORM,
    PIPE_FORMAT_R4G4B4A4_UNORM,
    PIPE_FORMAT_B4G4R4A4_UNORM,
    PIPE_FORMAT_R5G6B5_UNORM,
    PIPE_FORMAT_B5G6R5_UNORM,
    PIPE_FORMAT_R10G10B10A2_UNORM,
    PIPE_FORMAT_L8_UNORM,    /**< ubyte luminance */
    PIPE_FORMAT_A8_UNORM,    /**< ubyte alpha */
    PIPE_FORMAT_I8_UNORM,    /**< ubyte intensity */
    PIPE_FORMAT_L8A8_UNORM,  /**< ubyte alpha, luminance */
    PIPE_FORMAT_L16_UNORM,   /**< ushort luminance */
    PIPE_FORMAT_UYVY,
    PIPE_FORMAT_YUYV,
    PIPE_FORMAT_Z16_UNORM,
    PIPE_FORMAT_Z16_UNORM_S8_UINT,
    PIPE_FORMAT_Z32_UNORM,
    PIPE_FORMAT_Z32_FLOAT,
    PIPE_FORMAT_Z24_UNORM_S8_UINT,
    PIPE_FORMAT_S8_UINT_Z24_UNORM,
    PIPE_FORMAT_Z24X8_UNORM,
    PIPE_FORMAT_X8Z24_UNORM,
    PIPE_FORMAT_S8_UINT,     /**< ubyte stencil */
    PIPE_FORMAT_R64_FLOAT,
    PIPE_FORMAT_R64G64_FLOAT,
    PIPE_FORMAT_R64G64B64_FLOAT,
    PIPE_FORMAT_R64G64B64A64_FLOAT,
    PIPE_FORMAT_R32_FLOAT,
    PIPE_FORMAT_R32G32_FLOAT,
    PIPE_FORMAT_R32G32B32_FLOAT,
    PIPE_FORMAT_R32G32B32A32_FLOAT,
    PIPE_FORMAT_R32_UNORM,
    PIPE_FORMAT_R32G32_UNORM,
    PIPE_FORMAT_R32G32B32_UNORM,
    PIPE_FORMAT_R32G32B32A32_UNORM,
    PIPE_FORMAT_R32_USCALED,
    PIPE_FORMAT_R32G32_USCALED,
    PIPE_FORMAT_R32G32B32_USCALED,
    PIPE_FORMAT_R32G32B32A32_USCALED,
    PIPE_FORMAT_R32_SNORM,
    PIPE_FORMAT_R32G32_SNORM,
    PIPE_FORMAT_R32G32B32_SNORM,
    PIPE_FORMAT_R32G32B32A32_SNORM,
    PIPE_FORMAT_R32_SSCALED,
    PIPE_FORMAT_R32G32_SSCALED,
    PIPE_FORMAT_R32G32B32_SSCALED,
    PIPE_FORMAT_R32G32B32A32_SSCALED,
    PIPE_FORMAT_R16_UNORM,
    PIPE_FORMAT_R16G16_UNORM,
    PIPE_FORMAT_R16G16B16_UNORM,
    PIPE_FORMAT_R16G16B16A16_UNORM,
    PIPE_FORMAT_R16_USCALED,
    PIPE_FORMAT_R16G16_USCALED,
    PIPE_FORMAT_R16G16B16_USCALED,
    PIPE_FORMAT_R16G16B16A16_USCALED,
    PIPE_FORMAT_R16_SNORM,
    PIPE_FORMAT_R16G16_SNORM,
    PIPE_FORMAT_R16G16B16_SNORM,
    PIPE_FORMAT_R16G16B16A16_SNORM,
    PIPE_FORMAT_R16_SSCALED,
    PIPE_FORMAT_R16G16_SSCALED,
    PIPE_FORMAT_R16G16B16_SSCALED,
    PIPE_FORMAT_R16G16B16A16_SSCALED,
    PIPE_FORMAT_R8_UNORM,
    PIPE_FORMAT_R8G8_UNORM,
    PIPE_FORMAT_R8G8B8_UNORM,
    PIPE_FORMAT_B8G8R8_UNORM,
    PIPE_FORMAT_R8G8B8A8_UNORM,
    PIPE_FORMAT_X8B8G8R8_UNORM,
    PIPE_FORMAT_R8_USCALED,
    PIPE_FORMAT_R8G8_USCALED,
    PIPE_FORMAT_R8G8B8_USCALED,
    PIPE_FORMAT_B8G8R8_USCALED,
    PIPE_FORMAT_R8G8B8A8_USCALED,
    PIPE_FORMAT_B8G8R8A8_USCALED,
    PIPE_FORMAT_A8B8G8R8_USCALED,
    PIPE_FORMAT_R8_SNORM,
    PIPE_FORMAT_R8G8_SNORM,
    PIPE_FORMAT_R8G8B8_SNORM,
    PIPE_FORMAT_B8G8R8_SNORM,
    PIPE_FORMAT_R8G8B8A8_SNORM,
    PIPE_FORMAT_B8G8R8A8_SNORM,
    PIPE_FORMAT_R8_SSCALED,
    PIPE_FORMAT_R8G8_SSCALED,
    PIPE_FORMAT_R8G8B8_SSCALED,
    PIPE_FORMAT_B8G8R8_SSCALED,
    PIPE_FORMAT_R8G8B8A8_SSCALED,
    PIPE_FORMAT_B8G8R8A8_SSCALED,
    PIPE_FORMAT_A8B8G8R8_SSCALED,
    PIPE_FORMAT_R32_FIXED,
    PIPE_FORMAT_R32G32_FIXED,
    PIPE_FORMAT_R32G32B32_FIXED,
    PIPE_FORMAT_R32G32B32A32_FIXED,
    PIPE_FORMAT_R16_FLOAT,
    PIPE_FORMAT_R16G16_FLOAT,
    PIPE_FORMAT_R16G16B16_FLOAT,
    PIPE_FORMAT_R16G16B16A16_FLOAT,

    /* sRGB formats */
    PIPE_FORMAT_L8_SRGB,
    PIPE_FORMAT_R8_SRGB,
    PIPE_FORMAT_L8A8_SRGB,
    PIPE_FORMAT_R8G8_SRGB,
    PIPE_FORMAT_R8G8B8_SRGB,
    PIPE_FORMAT_B8G8R8_SRGB,
    PIPE_FORMAT_A8B8G8R8_SRGB,
    PIPE_FORMAT_X8B8G8R8_SRGB,
    PIPE_FORMAT_B8G8R8A8_SRGB,
    PIPE_FORMAT_B8G8R8X8_SRGB,
    PIPE_FORMAT_A8R8G8B8_SRGB,
    PIPE_FORMAT_X8R8G8B8_SRGB,
    PIPE_FORMAT_R8G8B8A8_SRGB,

    /* compressed formats */
    PIPE_FORMAT_DXT1_RGB,
    PIPE_FORMAT_DXT1_RGBA,
    PIPE_FORMAT_DXT3_RGBA,
    PIPE_FORMAT_DXT5_RGBA,

    /* sRGB, compressed */
    PIPE_FORMAT_DXT1_SRGB,
    PIPE_FORMAT_DXT1_SRGBA,
    PIPE_FORMAT_DXT3_SRGBA,
    PIPE_FORMAT_DXT5_SRGBA,

    /* rgtc compressed */
    PIPE_FORMAT_RGTC1_UNORM,
    PIPE_FORMAT_RGTC1_SNORM,
    PIPE_FORMAT_RGTC2_UNORM,
    PIPE_FORMAT_RGTC2_SNORM,

    PIPE_FORMAT_R8G8_B8G8_UNORM,
    PIPE_FORMAT_G8R8_G8B8_UNORM,

    /* mixed formats */
    PIPE_FORMAT_R8SG8SB8UX8U_NORM,
    PIPE_FORMAT_R5SG5SB6U_NORM,

    /* TODO: re-order these */
    PIPE_FORMAT_A8B8G8R8_UNORM,
    PIPE_FORMAT_B5G5R5X1_UNORM,
    PIPE_FORMAT_R10G10B10A2_USCALED,
    PIPE_FORMAT_R11G11B10_FLOAT,
    PIPE_FORMAT_R9G9B9E5_FLOAT,
    PIPE_FORMAT_Z32_FLOAT_S8X24_UINT,
    PIPE_FORMAT_R1_UNORM,
    PIPE_FORMAT_R10G10B10X2_USCALED,
    PIPE_FORMAT_R10G10B10X2_SNORM,
    PIPE_FORMAT_L4A4_UNORM,
    PIPE_FORMAT_A2R10G10B10_UNORM,
    PIPE_FORMAT_A2B10G10R10_UNORM,
    PIPE_FORMAT_B10G10R10A2_UNORM,
    PIPE_FORMAT_R10SG10SB10SA2U_NORM,
    PIPE_FORMAT_R8G8Bx_SNORM,
    PIPE_FORMAT_R8G8B8X8_UNORM,
    PIPE_FORMAT_B4G4R4X4_UNORM,

    /* some stencil samplers formats */
    PIPE_FORMAT_X24S8_UINT,
    PIPE_FORMAT_S8X24_UINT,
    PIPE_FORMAT_X32_S8X24_UINT,

    PIPE_FORMAT_R3G3B2_UNORM,
    PIPE_FORMAT_B2G3R3_UNORM,
    PIPE_FORMAT_L16A16_UNORM,
    PIPE_FORMAT_A16_UNORM,
    PIPE_FORMAT_I16_UNORM,

    PIPE_FORMAT_LATC1_UNORM,
    PIPE_FORMAT_LATC1_SNORM,
    PIPE_FORMAT_LATC2_UNORM,
    PIPE_FORMAT_LATC2_SNORM,

    PIPE_FORMAT_A8_SNORM,
    PIPE_FORMAT_L8_SNORM,
    PIPE_FORMAT_L8A8_SNORM,
    PIPE_FORMAT_I8_SNORM,
    PIPE_FORMAT_A16_SNORM,
    PIPE_FORMAT_L16_SNORM,
    PIPE_FORMAT_L16A16_SNORM,
    PIPE_FORMAT_I16_SNORM,

    PIPE_FORMAT_A16_FLOAT,
    PIPE_FORMAT_L16_FLOAT,
    PIPE_FORMAT_L16A16_FLOAT,
    PIPE_FORMAT_I16_FLOAT,
    PIPE_FORMAT_A32_FLOAT,
    PIPE_FORMAT_L32_FLOAT,
    PIPE_FORMAT_L32A32_FLOAT,
    PIPE_FORMAT_I32_FLOAT,

    PIPE_FORMAT_YV12,
    PIPE_FORMAT_YV16,
    PIPE_FORMAT_IYUV,  /**< aka I420 */
    PIPE_FORMAT_NV12,
    PIPE_FORMAT_NV21,

    /* PIPE_FORMAT_Y8_U8_V8_420_UNORM = IYUV */
    /* PIPE_FORMAT_Y8_U8V8_420_UNORM = NV12 */
    PIPE_FORMAT_Y8_U8_V8_422_UNORM,
    PIPE_FORMAT_Y8_U8V8_422_UNORM,
    PIPE_FORMAT_Y8_U8_V8_444_UNORM,

    PIPE_FORMAT_Y16_U16_V16_420_UNORM,
    /* PIPE_FORMAT_Y16_U16V16_420_UNORM */
    PIPE_FORMAT_Y16_U16_V16_422_UNORM,
    PIPE_FORMAT_Y16_U16V16_422_UNORM,
    PIPE_FORMAT_Y16_U16_V16_444_UNORM,

    PIPE_FORMAT_A4R4_UNORM,
    PIPE_FORMAT_R4A4_UNORM,
    PIPE_FORMAT_R8A8_UNORM,
    PIPE_FORMAT_A8R8_UNORM,

    PIPE_FORMAT_R10G10B10A2_SSCALED,
    PIPE_FORMAT_R10G10B10A2_SNORM,

    PIPE_FORMAT_B10G10R10A2_USCALED,
    PIPE_FORMAT_B10G10R10A2_SSCALED,
    PIPE_FORMAT_B10G10R10A2_SNORM,

    PIPE_FORMAT_R8_UINT,
    PIPE_FORMAT_R8G8_UINT,
    PIPE_FORMAT_R8G8B8_UINT,
    PIPE_FORMAT_R8G8B8A8_UINT,

    PIPE_FORMAT_R8_SINT,
    PIPE_FORMAT_R8G8_SINT,
    PIPE_FORMAT_R8G8B8_SINT,
    PIPE_FORMAT_R8G8B8A8_SINT,

    PIPE_FORMAT_R16_UINT,
    PIPE_FORMAT_R16G16_UINT,
    PIPE_FORMAT_R16G16B16_UINT,
    PIPE_FORMAT_R16G16B16A16_UINT,

    PIPE_FORMAT_R16_SINT,
    PIPE_FORMAT_R16G16_SINT,
    PIPE_FORMAT_R16G16B16_SINT,
    PIPE_FORMAT_R16G16B16A16_SINT,

    PIPE_FORMAT_R32_UINT,
    PIPE_FORMAT_R32G32_UINT,
    PIPE_FORMAT_R32G32B32_UINT,
    PIPE_FORMAT_R32G32B32A32_UINT,

    PIPE_FORMAT_R32_SINT,
    PIPE_FORMAT_R32G32_SINT,
    PIPE_FORMAT_R32G32B32_SINT,
    PIPE_FORMAT_R32G32B32A32_SINT,

    PIPE_FORMAT_R64_UINT,
    PIPE_FORMAT_R64_SINT,

    PIPE_FORMAT_A8_UINT,
    PIPE_FORMAT_I8_UINT,
    PIPE_FORMAT_L8_UINT,
    PIPE_FORMAT_L8A8_UINT,

    PIPE_FORMAT_A8_SINT,
    PIPE_FORMAT_I8_SINT,
    PIPE_FORMAT_L8_SINT,
    PIPE_FORMAT_L8A8_SINT,

    PIPE_FORMAT_A16_UINT,
    PIPE_FORMAT_I16_UINT,
    PIPE_FORMAT_L16_UINT,
    PIPE_FORMAT_L16A16_UINT,

    PIPE_FORMAT_A16_SINT,
    PIPE_FORMAT_I16_SINT,
    PIPE_FORMAT_L16_SINT,
    PIPE_FORMAT_L16A16_SINT,

    PIPE_FORMAT_A32_UINT,
    PIPE_FORMAT_I32_UINT,
    PIPE_FORMAT_L32_UINT,
    PIPE_FORMAT_L32A32_UINT,

    PIPE_FORMAT_A32_SINT,
    PIPE_FORMAT_I32_SINT,
    PIPE_FORMAT_L32_SINT,
    PIPE_FORMAT_L32A32_SINT,

    PIPE_FORMAT_B8G8R8_UINT,
    PIPE_FORMAT_B8G8R8A8_UINT,

    PIPE_FORMAT_B8G8R8_SINT,
    PIPE_FORMAT_B8G8R8A8_SINT,

    PIPE_FORMAT_A8R8G8B8_UINT,
    PIPE_FORMAT_A8B8G8R8_UINT,
    PIPE_FORMAT_A2R10G10B10_UINT,
    PIPE_FORMAT_A2B10G10R10_UINT,
    PIPE_FORMAT_B10G10R10A2_UINT,
    PIPE_FORMAT_B10G10R10A2_SINT,
    PIPE_FORMAT_R5G6B5_UINT,
    PIPE_FORMAT_B5G6R5_UINT,
    PIPE_FORMAT_R5G5B5A1_UINT,
    PIPE_FORMAT_B5G5R5A1_UINT,
    PIPE_FORMAT_A1R5G5B5_UINT,
    PIPE_FORMAT_A1B5G5R5_UINT,
    PIPE_FORMAT_R4G4B4A4_UINT,
    PIPE_FORMAT_B4G4R4A4_UINT,
    PIPE_FORMAT_A4R4G4B4_UINT,
    PIPE_FORMAT_A4B4G4R4_UINT,
    PIPE_FORMAT_R3G3B2_UINT,
    PIPE_FORMAT_B2G3R3_UINT,

    PIPE_FORMAT_ETC1_RGB8,

    PIPE_FORMAT_R8G8_R8B8_UNORM,
    PIPE_FORMAT_G8R8_B8R8_UNORM,

    PIPE_FORMAT_R8G8B8X8_SNORM,
    PIPE_FORMAT_R8G8B8X8_SRGB,
    PIPE_FORMAT_R8G8B8X8_UINT,
    PIPE_FORMAT_R8G8B8X8_SINT,
    PIPE_FORMAT_B10G10R10X2_UNORM,
    PIPE_FORMAT_R16G16B16X16_UNORM,
    PIPE_FORMAT_R16G16B16X16_SNORM,
    PIPE_FORMAT_R16G16B16X16_FLOAT,
    PIPE_FORMAT_R16G16B16X16_UINT,
    PIPE_FORMAT_R16G16B16X16_SINT,
    PIPE_FORMAT_R32G32B32X32_FLOAT,
    PIPE_FORMAT_R32G32B32X32_UINT,
    PIPE_FORMAT_R32G32B32X32_SINT,

    PIPE_FORMAT_R8A8_SNORM,
    PIPE_FORMAT_R16A16_UNORM,
    PIPE_FORMAT_R16A16_SNORM,
    PIPE_FORMAT_R16A16_FLOAT,
    PIPE_FORMAT_R32A32_FLOAT,
    PIPE_FORMAT_R8A8_UINT,
    PIPE_FORMAT_R8A8_SINT,
    PIPE_FORMAT_R16A16_UINT,
    PIPE_FORMAT_R16A16_SINT,
    PIPE_FORMAT_R32A32_UINT,
    PIPE_FORMAT_R32A32_SINT,
    PIPE_FORMAT_R10G10B10A2_UINT,
    PIPE_FORMAT_R10G10B10A2_SINT,

    PIPE_FORMAT_B5G6R5_SRGB,

    PIPE_FORMAT_BPTC_RGBA_UNORM,
    PIPE_FORMAT_BPTC_SRGBA,
    PIPE_FORMAT_BPTC_RGB_FLOAT,
    PIPE_FORMAT_BPTC_RGB_UFLOAT,

    PIPE_FORMAT_G8R8_UNORM,
    PIPE_FORMAT_G8R8_SNORM,
    PIPE_FORMAT_G16R16_UNORM,
    PIPE_FORMAT_G16R16_SNORM,

    PIPE_FORMAT_A8B8G8R8_SNORM,
    PIPE_FORMAT_X8B8G8R8_SNORM,

    PIPE_FORMAT_ETC2_RGB8,
    PIPE_FORMAT_ETC2_SRGB8,
    PIPE_FORMAT_ETC2_RGB8A1,
    PIPE_FORMAT_ETC2_SRGB8A1,
    PIPE_FORMAT_ETC2_RGBA8,
    PIPE_FORMAT_ETC2_SRGBA8,
    PIPE_FORMAT_ETC2_R11_UNORM,
    PIPE_FORMAT_ETC2_R11_SNORM,
    PIPE_FORMAT_ETC2_RG11_UNORM,
    PIPE_FORMAT_ETC2_RG11_SNORM,

    PIPE_FORMAT_ASTC_4x4,
    PIPE_FORMAT_ASTC_5x4,
    PIPE_FORMAT_ASTC_5x5,
    PIPE_FORMAT_ASTC_6x5,
    PIPE_FORMAT_ASTC_6x6,
    PIPE_FORMAT_ASTC_8x5,
    PIPE_FORMAT_ASTC_8x6,
    PIPE_FORMAT_ASTC_8x8,
    PIPE_FORMAT_ASTC_10x5,
    PIPE_FORMAT_ASTC_10x6,
    PIPE_FORMAT_ASTC_10x8,
    PIPE_FORMAT_ASTC_10x10,
    PIPE_FORMAT_ASTC_12x10,
    PIPE_FORMAT_ASTC_12x12,

    PIPE_FORMAT_ASTC_4x4_SRGB,
    PIPE_FORMAT_ASTC_5x4_SRGB,
    PIPE_FORMAT_ASTC_5x5_SRGB,
    PIPE_FORMAT_ASTC_6x5_SRGB,
    PIPE_FORMAT_ASTC_6x6_SRGB,
    PIPE_FORMAT_ASTC_8x5_SRGB,
    PIPE_FORMAT_ASTC_8x6_SRGB,
    PIPE_FORMAT_ASTC_8x8_SRGB,
    PIPE_FORMAT_ASTC_10x5_SRGB,
    PIPE_FORMAT_ASTC_10x6_SRGB,
    PIPE_FORMAT_ASTC_10x8_SRGB,
    PIPE_FORMAT_ASTC_10x10_SRGB,
    PIPE_FORMAT_ASTC_12x10_SRGB,
    PIPE_FORMAT_ASTC_12x12_SRGB,

    PIPE_FORMAT_ASTC_3x3x3,
    PIPE_FORMAT_ASTC_4x3x3,
    PIPE_FORMAT_ASTC_4x4x3,
    PIPE_FORMAT_ASTC_4x4x4,
    PIPE_FORMAT_ASTC_5x4x4,
    PIPE_FORMAT_ASTC_5x5x4,
    PIPE_FORMAT_ASTC_5x5x5,
    PIPE_FORMAT_ASTC_6x5x5,
    PIPE_FORMAT_ASTC_6x6x5,
    PIPE_FORMAT_ASTC_6x6x6,

    PIPE_FORMAT_ASTC_3x3x3_SRGB,
    PIPE_FORMAT_ASTC_4x3x3_SRGB,
    PIPE_FORMAT_ASTC_4x4x3_SRGB,
    PIPE_FORMAT_ASTC_4x4x4_SRGB,
    PIPE_FORMAT_ASTC_5x4x4_SRGB,
    PIPE_FORMAT_ASTC_5x5x4_SRGB,
    PIPE_FORMAT_ASTC_5x5x5_SRGB,
    PIPE_FORMAT_ASTC_6x5x5_SRGB,
    PIPE_FORMAT_ASTC_6x6x5_SRGB,
    PIPE_FORMAT_ASTC_6x6x6_SRGB,

    PIPE_FORMAT_FXT1_RGB,
    PIPE_FORMAT_FXT1_RGBA,

    PIPE_FORMAT_P010,
    PIPE_FORMAT_P012,
    PIPE_FORMAT_P016,

    PIPE_FORMAT_R10G10B10X2_UNORM,
    PIPE_FORMAT_A1R5G5B5_UNORM,
    PIPE_FORMAT_A1B5G5R5_UNORM,
    PIPE_FORMAT_X1B5G5R5_UNORM,
    PIPE_FORMAT_R5G5B5A1_UNORM,
    PIPE_FORMAT_A4R4G4B4_UNORM,
    PIPE_FORMAT_A4B4G4R4_UNORM,

    PIPE_FORMAT_G8R8_SINT,
    PIPE_FORMAT_A8B8G8R8_SINT,
    PIPE_FORMAT_X8B8G8R8_SINT,

    PIPE_FORMAT_ATC_RGB,
    PIPE_FORMAT_ATC_RGBA_EXPLICIT,
    PIPE_FORMAT_ATC_RGBA_INTERPOLATED,

    PIPE_FORMAT_Z24_UNORM_S8_UINT_AS_R8G8B8A8,

    PIPE_FORMAT_AYUV,
    PIPE_FORMAT_XYUV,

    PIPE_FORMAT_R8_G8B8_420_UNORM,

    PIPE_FORMAT_COUNT
};

struct st_visual
{
    bool no_config;

    /**
     * Available buffers.  Bitfield of ST_ATTACHMENT_*_MASK bits.
     */
    unsigned buffer_mask;

    /**
     * Buffer formats.  The formats are always set even when the buffer is
     * not available.
     */
    enum pipe_format color_format;
    enum pipe_format depth_stencil_format;
    enum pipe_format accum_format;
    unsigned samples;

    /**
     * Desired render buffer.
     */
    enum st_attachment_type render_buffer;
};
typedef struct osmesa_buffer
{
    struct st_framebuffer_iface *stfb;
    struct st_visual visual;
    unsigned width, height;

    struct pipe_resource *textures[ST_ATTACHMENT_COUNT];

    void *map;

    struct osmesa_buffer *next;  /**< next in linked list */
};


typedef struct osmesa_context
{
    struct st_context_iface *stctx;

    bool ever_used;     /*< Has this context ever been current? */

    struct osmesa_buffer *current_buffer;

    /* Storage for depth/stencil, if the user has requested access.  The backing
     * driver always has its own storage for the actual depth/stencil, which we
     * have to transfer in and out.
     */
    void *zs;
    unsigned zs_stride;

    enum pipe_format depth_stencil_format, accum_format;

    GLenum format;         /*< User-specified context format */
    GLenum type;           /*< Buffer's data type */
    GLint user_row_length; /*< user-specified number of pixels per row */
    GLboolean y_up;        /*< TRUE  -> Y increases upward */
    /*< FALSE -> Y increases downward */

    /** Which postprocessing filters are enabled. */
    //safe to remove
};
// endregion OSMESA internals
struct PotatoBridge {
     /*ANativeWindow */ void* androidWindow;
        
    /* EGLContext */ void* eglContextOld;
    /* EGLContext */ void* eglContext;
    /* EGLDisplay */ void* eglDisplay;
    /* EGLSurface */ void* eglSurface;
/*
    void* eglSurfaceRead;
    void* eglSurfaceDraw;
*/
};
EGLConfig config;
struct PotatoBridge potatoBridge;

/* OSMesa functions */
GLboolean (*OSMesaMakeCurrent_p) (OSMesaContext ctx, void *buffer, GLenum type,
                                  GLsizei width, GLsizei height);
OSMesaContext (*OSMesaGetCurrentContext_p) (void);
OSMesaContext  (*OSMesaCreateContext_p) (GLenum format, OSMesaContext sharelist);
void (*OSMesaDestroyContext_p) (OSMesaContext ctx);
void (*OSMesaPixelStore_p) ( GLint pname, GLint value );
GLubyte* (*glGetString_p) (GLenum name);
void (*glFinish_p) (void);
void (*glClearColor_p) (GLclampf red, GLclampf green, GLclampf blue, GLclampf alpha);
void (*glClear_p) (GLbitfield mask);
void (*glReadPixels_p) (GLint x, GLint y, GLsizei width, GLsizei height, GLenum format, GLenum type, void * data);

/*EGL functions */
EGLBoolean (*eglMakeCurrent_p) (EGLDisplay dpy, EGLSurface draw, EGLSurface read, EGLContext ctx);
EGLBoolean (*eglDestroyContext_p) (EGLDisplay dpy, EGLContext ctx);
EGLBoolean (*eglDestroySurface_p) (EGLDisplay dpy, EGLSurface surface);
EGLBoolean (*eglTerminate_p) (EGLDisplay dpy);
EGLBoolean (*eglReleaseThread_p) (void);
EGLContext (*eglGetCurrentContext_p) (void);
EGLDisplay (*eglGetDisplay_p) (NativeDisplayType display);
EGLBoolean (*eglInitialize_p) (EGLDisplay dpy, EGLint *major, EGLint *minor);
EGLBoolean (*eglChooseConfig_p) (EGLDisplay dpy, const EGLint *attrib_list, EGLConfig *configs, EGLint config_size, EGLint *num_config);
EGLBoolean (*eglGetConfigAttrib_p) (EGLDisplay dpy, EGLConfig config, EGLint attribute, EGLint *value);
EGLBoolean (*eglBindAPI_p) (EGLenum api);
EGLSurface (*eglCreatePbufferSurface_p) (EGLDisplay dpy, EGLConfig config, const EGLint *attrib_list);
EGLSurface (*eglCreateWindowSurface_p) (EGLDisplay dpy, EGLConfig config, NativeWindowType window, const EGLint *attrib_list);
EGLBoolean (*eglSwapBuffers_p) (EGLDisplay dpy, EGLSurface draw);
EGLint (*eglGetError_p) (void);
EGLContext (*eglCreateContext_p) (EGLDisplay dpy, EGLConfig config, EGLContext share_list, const EGLint *attrib_list);
EGLBoolean (*eglSwapInterval_p) (EGLDisplay dpy, EGLint interval);
EGLSurface (*eglGetCurrentSurface_p) (EGLint readdraw);
int (*vtest_main_p) (int argc, char** argv);
void (*vtest_swap_buffers_p) (void);

#define RENDERER_GL4ES 1
#define RENDERER_VK_ZINK 2
#define RENDERER_VIRGL 3

int config_renderer;
void* gbuffer;

void* egl_make_current(void* window);

void pojav_openGLOnLoad() {
}
void pojav_openGLOnUnload() {

}

void pojavTerminate() {
    printf("EGLBridge: Terminating\n");
    
    switch (config_renderer) {
        case RENDERER_GL4ES: {
            eglMakeCurrent_p(potatoBridge.eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
            eglDestroySurface_p(potatoBridge.eglDisplay, potatoBridge.eglSurface);
            eglDestroyContext_p(potatoBridge.eglDisplay, potatoBridge.eglContext);
            eglTerminate_p(potatoBridge.eglDisplay);
            eglReleaseThread_p();
    
            potatoBridge.eglContext = EGL_NO_CONTEXT;
            potatoBridge.eglDisplay = EGL_NO_DISPLAY;
            potatoBridge.eglSurface = EGL_NO_SURFACE;
        } break;
        
        //case RENDERER_VIRGL:
        case RENDERER_VK_ZINK: {
            // Nothing to do here
        } break;
    }
}

JNIEXPORT void JNICALL Java_net_kdt_pojavlaunch_utils_JREUtils_setupBridgeWindow(JNIEnv* env, jclass clazz, jobject surface) {
    potatoBridge.androidWindow = ANativeWindow_fromSurface(env, surface);
    char *ptrStr;
    asprintf(&ptrStr, "%ld", (long) potatoBridge.androidWindow);
    setenv("POJAV_WINDOW_PTR", ptrStr, 1);
    free(ptrStr);
}


JNIEXPORT void JNICALL
Java_net_kdt_pojavlaunch_utils_JREUtils_releaseBridgeWindow(JNIEnv *env, jclass clazz) {
    ANativeWindow_release(potatoBridge.androidWindow);
}

void* pojavGetCurrentContext() {
    switch (config_renderer) {
        case RENDERER_GL4ES:
            return (void *)eglGetCurrentContext_p();
        case RENDERER_VIRGL:
        case RENDERER_VK_ZINK:
            return (void *)OSMesaGetCurrentContext_p();

        default: return NULL;
    }
}

void dlsym_EGL(void* dl_handle) {
    eglBindAPI_p = dlsym(dl_handle,"eglBindAPI");
    eglChooseConfig_p = dlsym(dl_handle, "eglChooseConfig");
    eglCreateContext_p = dlsym(dl_handle, "eglCreateContext");
    eglCreatePbufferSurface_p = dlsym(dl_handle, "eglCreatePbufferSurface");
    eglCreateWindowSurface_p = dlsym(dl_handle, "eglCreateWindowSurface");
    eglDestroyContext_p = dlsym(dl_handle, "eglDestroyContext");
    eglDestroySurface_p = dlsym(dl_handle, "eglDestroySurface");
    eglGetConfigAttrib_p = dlsym(dl_handle, "eglGetConfigAttrib");
    eglGetCurrentContext_p = dlsym(dl_handle, "eglGetCurrentContext");
    eglGetDisplay_p = dlsym(dl_handle, "eglGetDisplay");
    eglGetError_p = dlsym(dl_handle, "eglGetError");
    eglInitialize_p = dlsym(dl_handle, "eglInitialize");
    eglMakeCurrent_p = dlsym(dl_handle, "eglMakeCurrent");
    eglSwapBuffers_p = dlsym(dl_handle, "eglSwapBuffers");
    eglReleaseThread_p = dlsym(dl_handle, "eglReleaseThread");
    eglSwapInterval_p = dlsym(dl_handle, "eglSwapInterval");
    eglTerminate_p = dlsym(dl_handle, "eglTerminate");
    eglGetCurrentSurface_p = dlsym(dl_handle,"eglGetCurrentSurface");
}

void dlsym_OSMesa(void* dl_handle) {
    OSMesaMakeCurrent_p = dlsym(dl_handle,"OSMesaMakeCurrent");
    OSMesaGetCurrentContext_p = dlsym(dl_handle,"OSMesaGetCurrentContext");
    OSMesaCreateContext_p = dlsym(dl_handle, "OSMesaCreateContext");
    OSMesaDestroyContext_p = dlsym(dl_handle, "OSMesaDestroyContext");
    OSMesaPixelStore_p = dlsym(dl_handle,"OSMesaPixelStore");
    glGetString_p = dlsym(dl_handle,"glGetString");
    glClearColor_p = dlsym(dl_handle, "glClearColor");
    glClear_p = dlsym(dl_handle,"glClear");
    glFinish_p = dlsym(dl_handle,"glFinish");
    glReadPixels_p = dlsym(dl_handle,"glReadPixels");
}

bool loadSymbols() {
    char* fileName = calloc(1, 1024);
    char* fileNameExt = calloc(1, 1024);
    switch (config_renderer) {
        case RENDERER_VK_ZINK:
            sprintf(fileName, "%s/libOSMesa_8.so", getenv("POJAV_NATIVEDIR"));
            sprintf(fileNameExt, "%s/libOSMesa.so.8", getenv("POJAV_NATIVEDIR"));
            break;
        case RENDERER_GL4ES:
            sprintf(fileName, "libEGL.so");
            char* eglLib = getenv("POJAVEXEC_EGL");
            if (eglLib) {
                sprintf(fileNameExt, "%s", eglLib);
            }
            break;
    }
    void* dl_handle = dlopen(fileNameExt,RTLD_NOW|RTLD_GLOBAL|RTLD_NODELETE);
    if (!dl_handle) {
        dl_handle = dlopen(fileNameExt,RTLD_NOW|RTLD_GLOBAL);
    }
    if (!dl_handle) {
        dl_handle = dlopen(fileName,RTLD_NOW|RTLD_GLOBAL|RTLD_NODELETE);
        if (!dl_handle) {
            dl_handle = dlopen(fileName,RTLD_NOW|RTLD_GLOBAL);
        }
        printf("DlLoader: using default %s\n", fileName);
    } else {
        printf("DlLoader: using external %s\n", fileNameExt);
    }

    if(dl_handle == NULL) {
        printf("DlLoader: unable to load: %s\n",dlerror());
        return 0;
    }
    switch(config_renderer) {
        case RENDERER_VK_ZINK:
            dlsym_OSMesa(dl_handle);
            break;
        case RENDERER_GL4ES:
            dlsym_EGL(dl_handle);
            break;
    }

    free(fileName);
    free(fileNameExt);
}

bool loadSymbolsVirGL() {
    config_renderer = RENDERER_GL4ES;
    loadSymbols();
    config_renderer = RENDERER_VK_ZINK;
    loadSymbols();
    config_renderer = RENDERER_VIRGL;

    char* fileName = calloc(1, 1024);

    sprintf(fileName, "%s/libvirgl_test_server.so", getenv("POJAV_NATIVEDIR"));
    void *handle = dlopen(fileName, RTLD_LAZY);
    printf("VirGL: libvirgl_test_server = %p\n", handle);
    if (!handle) {
        printf("VirGL: %s\n", dlerror());
    }
    vtest_main_p = dlsym(handle, "vtest_main");
    vtest_swap_buffers_p = dlsym(handle, "vtest_swap_buffers");

    free(fileName);
}

int pojavInit() {
    potatoBridge.androidWindow = (void *)atol(getenv("POJAV_WINDOW_PTR"));
    ANativeWindow_acquire(potatoBridge.androidWindow);
    savedWidth = ANativeWindow_getWidth(potatoBridge.androidWindow);
    savedHeight = ANativeWindow_getHeight(potatoBridge.androidWindow);
    ANativeWindow_setBuffersGeometry(potatoBridge.androidWindow,savedWidth,savedHeight,AHARDWAREBUFFER_FORMAT_R8G8B8X8_UNORM);

    // NOTE: Override for now.
    const char *renderer = getenv("POJAV_RENDERER");
    if (strncmp("opengles3_virgl", renderer, 15) == 0) {
        config_renderer = RENDERER_VIRGL;
        setenv("GALLIUM_DRIVER","virpipe",1);
        setenv("OSMESA_NO_FLUSH_FRONTBUFFER","1",false);
        if(strcmp(getenv("OSMESA_NO_FLUSH_FRONTBUFFER"),"1") == 0) {
            printf("VirGL: OSMesa buffer flush is DISABLED!\n");
        }
        loadSymbolsVirGL();
    } else if (strncmp("opengles", renderer, 8) == 0) {
        config_renderer = RENDERER_GL4ES;
        loadSymbols();
    } else if (strcmp(renderer, "vulkan_zink") == 0) {
        config_renderer = RENDERER_VK_ZINK;
        setenv("GALLIUM_DRIVER","zink",1);
        loadSymbols();
    }

    if (config_renderer == RENDERER_GL4ES || config_renderer == RENDERER_VIRGL) {
        if (potatoBridge.eglDisplay == NULL || potatoBridge.eglDisplay == EGL_NO_DISPLAY) {
            potatoBridge.eglDisplay = eglGetDisplay_p(EGL_DEFAULT_DISPLAY);
            if (potatoBridge.eglDisplay == EGL_NO_DISPLAY) {
                printf("EGLBridge: Error eglGetDefaultDisplay() failed: %p\n", eglGetError_p());
                return 0;
            }
        }

        printf("EGLBridge: Initializing\n");
        // printf("EGLBridge: ANativeWindow pointer = %p\n", potatoBridge.androidWindow);
        //(*env)->ThrowNew(env,(*env)->FindClass(env,"java/lang/Exception"),"Trace exception");
        if (!eglInitialize_p(potatoBridge.eglDisplay, NULL, NULL)) {
            printf("EGLBridge: Error eglInitialize() failed: %s\n", eglGetError_p());
            return 0;
        }

        static const EGLint attribs[] = {
                EGL_RED_SIZE, 8,
                EGL_GREEN_SIZE, 8,
                EGL_BLUE_SIZE, 8,
                EGL_ALPHA_SIZE, 8,
                // Minecraft required on initial 24
                EGL_DEPTH_SIZE, 24,
                EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                EGL_NONE
        };

        EGLint num_configs;
        EGLint vid;

        if (!eglChooseConfig_p(potatoBridge.eglDisplay, attribs, &config, 1, &num_configs)) {
            printf("EGLBridge: Error couldn't get an EGL visual config: %s\n", eglGetError_p());
            return 0;
        }

        assert(config);
        assert(num_configs > 0);

        if (!eglGetConfigAttrib_p(potatoBridge.eglDisplay, config, EGL_NATIVE_VISUAL_ID, &vid)) {
            printf("EGLBridge: Error eglGetConfigAttrib() failed: %s\n", eglGetError_p());
            return 0;
        }

        ANativeWindow_setBuffersGeometry(potatoBridge.androidWindow, 0, 0, vid);

        eglBindAPI_p(EGL_OPENGL_ES_API);

        potatoBridge.eglSurface = eglCreateWindowSurface_p(potatoBridge.eglDisplay, config, potatoBridge.androidWindow, NULL);

        if (!potatoBridge.eglSurface) {
            printf("EGLBridge: Error eglCreateWindowSurface failed: %p\n", eglGetError_p());
            //(*env)->ThrowNew(env,(*env)->FindClass(env,"java/lang/Exception"),"Trace exception");
            return 0;
        }

        // sanity checks
        {
            EGLint val;
            assert(eglGetConfigAttrib_p(potatoBridge.eglDisplay, config, EGL_SURFACE_TYPE, &val));
            assert(val & EGL_WINDOW_BIT);
        }

        printf("EGLBridge: Initialized!\n");
        printf("EGLBridge: ThreadID=%d\n", gettid());
        printf("EGLBridge: EGLDisplay=%p, EGLSurface=%p\n",
/* window==0 ? EGL_NO_CONTEXT : */
               potatoBridge.eglDisplay,
               potatoBridge.eglSurface
        );
        if (config_renderer != RENDERER_VIRGL) {
            return 1;
        }
    }

    if (config_renderer == RENDERER_VIRGL) {
        // Init EGL context and vtest server
        const EGLint ctx_attribs[] = {
            EGL_CONTEXT_CLIENT_VERSION, 3,
            EGL_NONE
        };
        EGLContext* ctx = eglCreateContext_p(potatoBridge.eglDisplay, config, NULL, ctx_attribs);
        printf("VirGL: created EGL context %p\n", ctx);

        pthread_t t;
        pthread_create(&t, NULL, egl_make_current, (void *)ctx);
        usleep(100*1000); // need enough time for the server to init
    }

    if (config_renderer == RENDERER_VK_ZINK || config_renderer == RENDERER_VIRGL) {
        if(OSMesaCreateContext_p == NULL) {
            printf("OSMDroid: %s\n",dlerror());
            return 0;
        }
        
        printf("OSMDroid: width=%i;height=%i, reserving %i bytes for frame buffer\n", savedWidth, savedHeight,
             savedWidth * 4 * savedHeight);
        gbuffer = malloc(savedWidth * 4 * savedHeight+1);
        if (gbuffer) {
            printf("OSMDroid: created frame buffer\n");
            return 1;
        } else {
            printf("OSMDroid: can't generate frame buffer\n");
            return 0;
        }
    }
    
    return 0;
}
ANativeWindow_Buffer buf;
int32_t stride;
bool stopSwapBuffers;
void pojavSwapBuffers() {
    if (stopSwapBuffers) {
        return;
    }
    switch (config_renderer) {
        case RENDERER_GL4ES: {
            if (!eglSwapBuffers_p(potatoBridge.eglDisplay, eglGetCurrentSurface_p(EGL_DRAW))) {
                if (eglGetError_p() == EGL_BAD_SURFACE) {
                    stopSwapBuffers = true;
                    closeGLFWWindow();
                }
            }
        } break;
        
        case RENDERER_VIRGL: {
            glFinish_p();
            vtest_swap_buffers_p();
        } break;

        case RENDERER_VK_ZINK: {
            OSMesaContext ctx = OSMesaGetCurrentContext_p();
            if(ctx == NULL) {
                printf("Zink: attempted to swap buffers without context!");
                break;
            }
            OSMesaMakeCurrent_p(ctx,buf.bits,GL_UNSIGNED_BYTE,savedWidth,savedHeight);
            glFinish_p();
            ANativeWindow_unlockAndPost(potatoBridge.androidWindow);
            //OSMesaMakeCurrent_p(ctx,gbuffer,GL_UNSIGNED_BYTE,savedWidth,savedHeight);
            ANativeWindow_lock(potatoBridge.androidWindow,&buf,NULL);
        } break;
    }
}

void* egl_make_current(void* window) {
    EGLBoolean success = eglMakeCurrent_p(
        potatoBridge.eglDisplay,
        window==0 ? (EGLSurface *) 0 : potatoBridge.eglSurface,
        window==0 ? (EGLSurface *) 0 : potatoBridge.eglSurface,
        /* window==0 ? EGL_NO_CONTEXT : */ (EGLContext *) window
    );

    if (success == EGL_FALSE) {
        printf("EGLBridge: Error: eglMakeCurrent() failed: %p\n", eglGetError_p());
    } else {
        printf("EGLBridge: eglMakeCurrent() succeed!\n");
    }

    if (config_renderer == RENDERER_VIRGL) {
        printf("VirGL: vtest_main = %p\n", vtest_main_p);
        printf("VirGL: Calling VTest server's main function\n");
        vtest_main_p(3, (const char*[]){"vtest", "--no-loop-or-fork", "--use-gles", NULL, NULL});
    }
}

bool locked = false;
void pojavMakeCurrent(void* window) {
    //if(OSMesaGetCurrentContext_p() != NULL) {
    //    printf("OSMDroid: skipped context reset\n");
    //    return JNI_TRUE;
    //}
    
    if (config_renderer == RENDERER_GL4ES) {
            EGLContext *currCtx = eglGetCurrentContext_p();
            printf("EGLBridge: Comparing: thr=%d, this=%p, curr=%p\n", gettid(), window, currCtx);
            if (currCtx == NULL || window == 0) {
        /*if (window != 0x0 && potatoBridge.eglContextOld != NULL && potatoBridge.eglContextOld != (void *) window) {
            // Create new pbuffer per thread
            // TODO get window size for 2nd+ window!
            int surfaceWidth, surfaceHeight;
            eglQuerySurface(potatoBridge.eglDisplay, potatoBridge.eglSurface, EGL_WIDTH, &surfaceWidth);
            eglQuerySurface(potatoBridge.eglDisplay, potatoBridge.eglSurface, EGL_HEIGHT, &surfaceHeight);
            int surfaceAttr[] = {
                EGL_WIDTH, surfaceWidth,
                EGL_HEIGHT, surfaceHeight,
                EGL_NONE
            };
            potatoBridge.eglSurface = eglCreatePbufferSurface(potatoBridge.eglDisplay, config, surfaceAttr);
            printf("EGLBridge: created pbuffer surface %p for context %p\n", potatoBridge.eglSurface, window);
        }*/
        //potatoBridge.eglContextOld = (void *) window;
        // eglMakeCurrent(potatoBridge.eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
                printf("EGLBridge: Making current on window %p on thread %d\n", window, gettid());
                egl_make_current((void *)window);

                // Test
#ifdef GLES_TEST
                glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
                glClear(GL_COLOR_BUFFER_BIT);
                eglSwapBuffers(potatoBridge.eglDisplay, potatoBridge.eglSurface);
                printf("First frame error: %p\n", eglGetError());
#endif

                // idk this should convert or just `return success;`...
                return; //success == EGL_TRUE ? JNI_TRUE : JNI_FALSE;
            } else {
                // (*env)->ThrowNew(env,(*env)->FindClass(env,"java/lang/Exception"),"Trace exception");
                return;
            }
    }

    if (config_renderer == RENDERER_VK_ZINK || config_renderer == RENDERER_VIRGL) {
            printf("OSMDroid: making current\n");
            OSMesaMakeCurrent_p((OSMesaContext)window,gbuffer,GL_UNSIGNED_BYTE,savedWidth,savedHeight);
            if (config_renderer == RENDERER_VK_ZINK) {
                ANativeWindow_lock(potatoBridge.androidWindow,&buf,NULL);
                OSMesaPixelStore_p(OSMESA_ROW_LENGTH,buf.stride);
                stride = buf.stride;
                //ANativeWindow_unlockAndPost(potatoBridge.androidWindow);
                OSMesaPixelStore_p(OSMESA_Y_UP,0);
            }

            printf("OSMDroid: vendor: %s\n",glGetString_p(GL_VENDOR));
            printf("OSMDroid: renderer: %s\n",glGetString_p(GL_RENDERER));
            glClear_p(GL_COLOR_BUFFER_BIT);
            glClearColor_p(0.4f, 0.4f, 0.4f, 1.0f);

            // Trigger a texture creation, which then set VIRGL_TEXTURE_ID
            int pixelsArr[4];
            glReadPixels_p(0, 0, 1, 1, GL_RGB, GL_INT, &pixelsArr);

            pojavSwapBuffers();
            return;
    }
}

/*
JNIEXPORT void JNICALL
Java_org_lwjgl_glfw_GLFW_nativeEglDetachOnCurrentThread(JNIEnv *env, jclass clazz) {
    //Obstruct the context on the current thread
    
    switch (config_renderer) {
        case RENDERER_GL4ES: {
            eglMakeCurrent_p(potatoBridge.eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
        } break;

        case RENDERER_VIRGL:
        case RENDERER_VK_ZINK: {
            // Nothing to do here
        } break;
    }
}
*/

void* pojavCreateContext(void* contextSrc) {
    if (config_renderer == RENDERER_GL4ES) {
            const EGLint ctx_attribs[] = {
                EGL_CONTEXT_CLIENT_VERSION, atoi(getenv("LIBGL_ES")),
                EGL_NONE
            };
            EGLContext* ctx = eglCreateContext_p(potatoBridge.eglDisplay, config, (void*)contextSrc, ctx_attribs);
            potatoBridge.eglContext = ctx;
            printf("EGLBridge: Created CTX pointer = %p\n",ctx);
            //(*env)->ThrowNew(env,(*env)->FindClass(env,"java/lang/Exception"),"Trace exception");
            return (long)ctx;
    }

    if (config_renderer == RENDERER_VK_ZINK || config_renderer == RENDERER_VIRGL) {
            printf("OSMDroid: generating context\n");
            void* ctx = OSMesaCreateContext_p(OSMESA_RGBA,contextSrc);
            printf("OSMDroid: context=%p\n",ctx);
            return ctx;
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_GL_nativeRegalMakeCurrent(JNIEnv *env, jclass clazz) {
    /*printf("Regal: making current");
    
    RegalMakeCurrent_func *RegalMakeCurrent = (RegalMakeCurrent_func *) dlsym(RTLD_DEFAULT, "RegalMakeCurrent");
    RegalMakeCurrent(potatoBridge.eglContext);*/
    
    printf("regal removed\n");
    abort();
}
JNIEXPORT jlong JNICALL
Java_org_lwjgl_opengl_GL_getGraphicsBufferAddr(JNIEnv *env, jobject thiz) {
    return &gbuffer;
}
JNIEXPORT jintArray JNICALL
Java_org_lwjgl_opengl_GL_getNativeWidthHeight(JNIEnv *env, jobject thiz) {
    jintArray ret = (*env)->NewIntArray(env,2);
    jint arr[] = {savedWidth, savedHeight};
    (*env)->SetIntArrayRegion(env,ret,0,2,arr);
    return ret;
}
void pojavSwapInterval(int interval) {
    switch (config_renderer) {
        case RENDERER_GL4ES:
        case RENDERER_VIRGL: {
            eglSwapInterval_p(potatoBridge.eglDisplay, interval);
        } break;

        case RENDERER_VK_ZINK: {
            printf("eglSwapInterval: NOT IMPLEMENTED YET!\n");
            // Nothing to do here
        } break;
    }
}

