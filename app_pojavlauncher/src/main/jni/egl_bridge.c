#include <jni.h>
#include <assert.h>
#include <dlfcn.h>

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>

//#include <EGL/egl.h>
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

struct PotatoBridge potatoBridge;
GLboolean (*OSMesaMakeCurrent_p) (OSMesaContext ctx, void *buffer, GLenum type,
                                  GLsizei width, GLsizei height);
OSMesaContext (*OSMesaGetCurrentContext_p) (void);
OSMesaContext  (*OSMesaCreateContext_p) (GLenum format, OSMesaContext sharelist);
void (*OSMesaPixelStore_p) ( GLint pname, GLint value );
GLubyte* (*glGetString_p) (GLenum name);
void (*glFinish_p) (void);
void (*glClearColor_p) (GLclampf red, GLclampf green, GLclampf blue, GLclampf alpha);
void (*glClear_p) (GLbitfield mask);

void pojav_openGLOnLoad() {
}
void pojav_openGLOnUnload() {

}
void* gbuffer;
int width;
int height;
void terminateEgl() {
    printf("EGLBridge: Terminating\n");
    //OSMesaDestroyContext(potatoBridge.eglContext);
}

JNIEXPORT void JNICALL Java_net_kdt_pojavlaunch_utils_JREUtils_setupBridgeWindow(JNIEnv* env, jclass clazz, jobject surface) {
    potatoBridge.androidWindow = ANativeWindow_fromSurface(env, surface);
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglGetCurrentContext(JNIEnv* env, jclass clazz) {
    return (jlong) OSMesaGetCurrentContext_p();
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglInit(JNIEnv* env, jclass clazz) {
    setenv("GALLIUM_DRIVER","zink",1);
    void* dl_handle = dlopen("libOSMesa.so.8",RTLD_NOLOAD|RTLD_NOW|RTLD_GLOBAL|RTLD_NODELETE);
    if (!dl_handle) {
        dl_handle = dlopen("libOSMesa_8.so",RTLD_NOLOAD|RTLD_NOW|RTLD_GLOBAL|RTLD_NODELETE);
        printf("OSMDroid: using built-in libOSMesa_8.so\n");
    } else {
        printf("OSMDroid: using developer libOSMesa.so.8 instead of built-in\n");
    }

    if(dl_handle == NULL) {
        printf("OSMDroid: unable to load: %s\n",dlerror());
        return JNI_FALSE;
    }

    OSMesaMakeCurrent_p = dlsym(dl_handle,"OSMesaMakeCurrent");
    OSMesaGetCurrentContext_p = dlsym(dl_handle,"OSMesaGetCurrentContext");
    OSMesaCreateContext_p = dlsym(dl_handle, "OSMesaCreateContext");
    OSMesaPixelStore_p = dlsym(dl_handle,"OSMesaPixelStore");
    glGetString_p = dlsym(dl_handle,"glGetString");
    glClearColor_p = dlsym(dl_handle, "glClearColor");
    glClear_p = dlsym(dl_handle,"glClear");
    glFinish_p = dlsym(dl_handle,"glFinish");

    if(OSMesaCreateContext_p == NULL) {
        printf("OSMDroid: %s\n",dlerror());
        return JNI_FALSE;
    }
    ANativeWindow_acquire(potatoBridge.androidWindow);
    width = ANativeWindow_getWidth(potatoBridge.androidWindow);
    height = ANativeWindow_getHeight(potatoBridge.androidWindow);
    ANativeWindow_setBuffersGeometry(potatoBridge.androidWindow,width,height,AHARDWAREBUFFER_FORMAT_R8G8B8_UNORM);
    printf("OSMDroid: width=%i;height=%i, reserving %i bytes for frame buffer\n", width, height,
           width * 4 * height);
    gbuffer = malloc(width * 4 * height+1);
    if (gbuffer) {
        printf("OSMDroid: created frame buffer\n");
        return JNI_TRUE;
    }else{
        printf("OSMDroid: can't generate frame buffer\n");
        return JNI_FALSE;
    }

}
ANativeWindow_Buffer buf;
int32_t stride;
void flipFrame() {
    ((struct osmesa_context)*OSMesaGetCurrentContext_p())
    .current_buffer->map = buf.bits;
    glFinish_p();
    ANativeWindow_unlockAndPost(potatoBridge.androidWindow);
    ANativeWindow_lock(potatoBridge.androidWindow,&buf,NULL);
}
bool stopSwapBuffers;
JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglSwapBuffers(JNIEnv *env, jclass clazz) {
    if (stopSwapBuffers) {
        return JNI_FALSE;
    }
    flipFrame();
}
bool locked = false;
JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglMakeCurrent(JNIEnv* env, jclass clazz, jlong window) {
    //if(OSMesaGetCurrentContext_p() != NULL) {
    //    printf("OSMDroid: skipped context reset\n");
    //    return JNI_TRUE;
    //}
    printf("OSMDroid: making current\n");
    OSMesaMakeCurrent_p((OSMesaContext)window,gbuffer,GL_UNSIGNED_BYTE,width,height);
    ANativeWindow_lock(potatoBridge.androidWindow,&buf,NULL);
    OSMesaPixelStore_p(OSMESA_ROW_LENGTH,buf.stride);
    stride = buf.stride;
    //ANativeWindow_unlockAndPost(potatoBridge.androidWindow);
    OSMesaPixelStore_p(OSMESA_Y_UP,0);
    printf("OSMDroid: vendor: %s\n",glGetString_p(GL_VENDOR));
    printf("OSMDroid: renderer: %s\n",glGetString_p(GL_RENDERER));
    glClear_p(GL_COLOR_BUFFER_BIT);
    glClearColor_p(0.4f, 0.4f, 0.4f, 1.0f);
    flipFrame();
    return JNI_TRUE;
}

JNIEXPORT void JNICALL
Java_org_lwjgl_glfw_GLFW_nativeEglDetachOnCurrentThread(JNIEnv *env, jclass clazz) {
    //Obstruct the context on the current thread
    //
}

JNIEXPORT jlong JNICALL
Java_org_lwjgl_glfw_GLFW_nativeEglCreateContext(JNIEnv *env, jclass clazz, jlong contextSrc) {
    printf("OSMDroid: generating context\n");
    void* ctx = OSMesaCreateContext_p(OSMESA_RGB,contextSrc);
    printf("OSMDroid: context=%p",ctx);
    return ctx;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglTerminate(JNIEnv* env, jclass clazz) {
    terminateEgl();
    return JNI_TRUE;
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_GL_nativeRegalMakeCurrent(JNIEnv *env, jclass clazz) {
    /*printf("Regal: making current");
    
    RegalMakeCurrent_func *RegalMakeCurrent = (RegalMakeCurrent_func *) dlsym(RTLD_DEFAULT, "RegalMakeCurrent");
    RegalMakeCurrent(potatoBridge.eglContext);*/
}
JNIEXPORT jlong JNICALL
Java_org_lwjgl_opengl_GL_getGraphicsBufferAddr(JNIEnv *env, jobject thiz) {
    return &gbuffer;
}
JNIEXPORT jintArray JNICALL
Java_org_lwjgl_opengl_GL_getNativeWidthHeight(JNIEnv *env, jobject thiz) {
    jintArray ret = (*env)->NewIntArray(env,2);
    jint arr[] = {width,height};
    (*env)->SetIntArrayRegion(env,ret,0,2,arr);
    return ret;
}
JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglSwapInterval(JNIEnv *env, jclass clazz, jint interval) {
    printf("eglSwapInterval: NOT IMPLEMENTED YET!\n");
    //return eglSwapInterval(potatoBridge.eglDisplay, interval);
}

