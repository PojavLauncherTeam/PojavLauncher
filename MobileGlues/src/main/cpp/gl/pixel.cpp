//
// Created by Swung 0x48 on 2025/2/27.
//

#include "pixel.h"

#define DEBUG 0

GLsizei gl_sizeof(GLenum type) {
    // types
    switch (type) {
        case GL_DOUBLE:
            return 8;
        case GL_FLOAT:
        case GL_INT:
        case GL_UNSIGNED_INT:
        case GL_UNSIGNED_INT_10_10_10_2:
        case GL_UNSIGNED_INT_2_10_10_10_REV:
        case GL_UNSIGNED_INT_8_8_8_8:
        case GL_UNSIGNED_INT_8_8_8_8_REV:
        case GL_UNSIGNED_INT_24_8:
        case GL_4_BYTES:
            return 4;
        case GL_3_BYTES:
            return 3;
        case GL_LUMINANCE_ALPHA:
        case GL_SHORT:
        case GL_HALF_FLOAT:
        case GL_UNSIGNED_SHORT:
        case GL_UNSIGNED_SHORT_1_5_5_5_REV:
        case GL_UNSIGNED_SHORT_4_4_4_4:
        case GL_UNSIGNED_SHORT_4_4_4_4_REV:
        case GL_UNSIGNED_SHORT_5_5_5_1:
        case GL_UNSIGNED_SHORT_5_6_5:
        case GL_UNSIGNED_SHORT_5_6_5_REV:
        case GL_2_BYTES:
            return 2;
        case GL_ALPHA:
        case GL_LUMINANCE:
        case GL_BYTE:
        case GL_UNSIGNED_BYTE:
        case GL_UNSIGNED_BYTE_2_3_3_REV:
        case GL_UNSIGNED_BYTE_3_3_2:
        case GL_DEPTH_COMPONENT:
        case GL_COLOR_INDEX:
            return 1;
        default:
            LOG_D("Unsupported pixel data type: %s\n", glEnumToString(type))
            return 0;
    }
}

GLboolean is_type_packed(GLenum type) {
    switch (type) {
        case GL_4_BYTES:
        case GL_3_BYTES:
        case GL_2_BYTES:
        case GL_UNSIGNED_BYTE_2_3_3_REV:
        case GL_UNSIGNED_BYTE_3_3_2:
        case GL_UNSIGNED_INT_10_10_10_2:
        case GL_UNSIGNED_INT_2_10_10_10_REV:
        case GL_UNSIGNED_INT_8_8_8_8:
        case GL_UNSIGNED_INT_8_8_8_8_REV:
        case GL_UNSIGNED_SHORT_1_5_5_5_REV:
        case GL_UNSIGNED_SHORT_4_4_4_4:
        case GL_UNSIGNED_SHORT_4_4_4_4_REV:
        case GL_UNSIGNED_SHORT_5_5_5_1:
        case GL_UNSIGNED_SHORT_5_6_5:
        case GL_UNSIGNED_SHORT_5_6_5_REV:
        case GL_DEPTH_STENCIL:
            return true;
        default:
            return false;
    }
}

GLsizei pixel_sizeof(GLenum format, GLenum type) {
    GLsizei width = 0;
    switch (format) {
        case GL_R:
        case GL_RED:
        case GL_ALPHA:
        case GL_LUMINANCE:
        case GL_DEPTH_COMPONENT:
        case GL_DEPTH_STENCIL:
        case GL_COLOR_INDEX:
            width = 1;
            break;
        case GL_RG:
        case GL_LUMINANCE_ALPHA:
            width = 2;
            break;
        case GL_RGB:
        case GL_BGR:
        case GL_RGB8:
            width = 3;
            break;
        case GL_RGBA:
        case GL_BGRA:
        case GL_RGBA8:
        case GL_R11F_G11F_B10F:
        case GL_R32F:
            width = 4;
            break;
        default:
            LOG_D("unsupported pixel format %s\n", glEnumToString(format))
            return 0;
    }

    if (is_type_packed(type))
        width = 1;

    return width * gl_sizeof(type);
}

static const colorlayout_t *get_color_map(GLenum format) {
#define map(fmt, ...)                               \
        case fmt: {                                     \
        static colorlayout_t layout = {fmt, __VA_ARGS__}; \
        return &layout; }
    switch (format) {
        map(GL_RED, 0, -1, -1, -1, 0)
        map(GL_R,   0, -1, -1, -1, 0)
        map(GL_RG,  0,  1, -1, -1, 0)
        map(GL_RGBA,0,  1, 2, 3, 3)
        map(GL_RGB, 0,  1, 2, -1, 2)
        map(GL_BGRA,2,  1, 0, 3, 3)
        map(GL_BGR, 2,  1, 0, -1, 2)
        map(GL_LUMINANCE_ALPHA, 0, 0, 0, 1, 1)
        map(GL_LUMINANCE, 0, 0, 0, -1, 0)
        map(GL_ALPHA,-1, -1, -1, 0, 0)
        map(GL_DEPTH_COMPONENT, 0, -1, -1, -1, 0)
        map(GL_COLOR_INDEX, 0, 1, 2, 3, 3)
        default:
            LOG_D("get_color_map: unknown pixel format %s\n", glEnumToString(format))
            break;
    }
    static colorlayout_t null = {0};
    return &null;
#undef map
}

bool pixel_convert(const GLvoid *src, GLvoid **dst,
                   GLuint width, GLuint height,
                   GLenum src_format, GLenum src_type,
                   GLenum dst_format, GLenum dst_type, GLuint stride, GLuint align) {
    const colorlayout_t *src_color, *dst_color;
    GLuint pixels = width * height;
    if(src_type==GL_INT8_REV) src_type=GL_UNSIGNED_BYTE;
    if(dst_type==GL_INT8_REV) dst_type=GL_UNSIGNED_BYTE;
    GLuint dst_size = height * widthalign(width * pixel_sizeof(dst_format, dst_type), align);
    GLuint dst_width2 = widthalign((stride?stride:width) * pixel_sizeof(dst_format, dst_type), align);
    GLuint dst_width = dst_width2 - (width * pixel_sizeof(dst_format, dst_type));
    GLuint src_width = widthalign(width * pixel_sizeof(src_format, src_type), align);
    GLuint src_widthadj = src_width -(width * pixel_sizeof(src_format, src_type));

    //printf("pixel conversion: %ix%i - %s, %s (%d) ==> %s, %s (%d), transform=%i, align=%d, src_width=%d(%d), dst_width=%d(%d)\n", width, height, PrintEnum(src_format), PrintEnum(src_type),pixel_sizeof(src_format, src_type), PrintEnum(dst_format), PrintEnum(dst_type), pixel_sizeof(dst_format, dst_type), raster_need_transform(), align, src_width, src_widthadj, dst_width2, dst_width);

    if ((src_type == dst_type) && (dst_format == src_format)) {
        if (*dst == src)
            return true;
        if (!dst_size || !pixel_sizeof(src_format, src_type)) {
            LOG_D("pixel_convert: pixel conversion, unknown format size, anticipated abort\n")
            return false;
        }
        if (*dst == nullptr)        // alloc dst only if dst==NULL
            *dst = malloc(dst_size);
        if (stride)	// for in-place conversion
            for (int yy=0; yy<height; yy++)
                memcpy((char*)(*dst)+yy*dst_width2, (char*)src+yy*src_width, src_width);
        else
            memcpy(*dst, src, dst_size);
        return true;
    }
    src_color = get_color_map(src_format);
    dst_color = get_color_map(dst_format);
    if (!dst_size || !pixel_sizeof(src_format, src_type)
        || !src_color->type || !dst_color->type) {
        LOG_D("pixel_convert: pixel conversion, anticipated abort\n")
        return false;
    }
    GLsizei src_stride = pixel_sizeof(src_format, src_type);
    GLsizei dst_stride = pixel_sizeof(dst_format, dst_type);
    if (*dst == src || *dst == nullptr)
        *dst = malloc(dst_size);
    uintptr_t src_pos = widthalign((uintptr_t)src, align);
    uintptr_t dst_pos = widthalign((uintptr_t)*dst, align);
    // fast optimized loop for common conversion cases first...
    // TODO: Rewrite that with some Macro, it's obviously doable to simplify the reading (and writing) of all this
    // simple BGRA <-> RGBA / UNSIGNED_BYTE
    if ((((src_format == GL_BGRA) && (dst_format == GL_RGBA)) || ((src_format == GL_RGBA) && (dst_format == GL_BGRA)))
        && (dst_type == GL_UNSIGNED_BYTE) && ((src_type == GL_UNSIGNED_BYTE))) {
        GLuint tmp;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tmp = *(const GLuint*)src_pos;
#ifdef __BIG_ENDIAN__
                *(GLuint*)dst_pos = (tmp&0x00ff00ff) | ((tmp&0x0000ff00)<<16) | ((tmp&0xff000000)>>16);
#else
                *(GLuint*)dst_pos = (tmp&0xff00ff00) | ((tmp&0x00ff0000)>>16) | ((tmp&0x000000ff)<<16);
#endif
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // RGBA or BGRA with GL_INT_8_8_8_8 <-> GL_INT_8_8_8_8_REV
    if((src_format==dst_format) && (src_format==GL_RGBA || src_format==GL_BGRA) && ((src_type==GL_INT8 && dst_type==GL_INT8_REV) || (src_type==GL_INT8_REV && dst_type==GL_INT8))) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                ((char*)dst_pos)[0] = ((char*)src_pos)[3];
                ((char*)dst_pos)[1] = ((char*)src_pos)[2];
                ((char*)dst_pos)[2] = ((char*)src_pos)[1];
                ((char*)dst_pos)[3] = ((char*)src_pos)[0];
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
#ifdef __BIG_ENDIAN__
    // RGBA or BGRA with GL_UNSIGNED_INT_8_8_8_8_REV <-> GL_UNSIGNED_BYTE
    if((src_format==dst_format) && (src_format==GL_RGBA || src_format==GL_BGRA) && ((src_type==GL_UNSIGNED_INT_8_8_8_8_REV && dst_type==GL_UNSIGNED_BYTE) || (src_type==GL_UNSIGNED_BYTE && dst_type==GL_UNSIGNED_INT_8_8_8_8_REV))) {
        for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				((char*)dst_pos)[0] = ((char*)src_pos)[3];
				((char*)dst_pos)[1] = ((char*)src_pos)[2];
				((char*)dst_pos)[2] = ((char*)src_pos)[1];
                ((char*)dst_pos)[3] = ((char*)src_pos)[0];
				src_pos += src_stride;
				dst_pos += dst_stride;
			}
			dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
#endif
    // BGRA1555 -> RGBA5551
    if ((src_format == GL_BGRA) && (dst_format == GL_RGBA) && (dst_type == GL_UNSIGNED_SHORT_5_5_5_1) && (src_type == GL_UNSIGNED_SHORT_1_5_5_5_REV)) {
        GLushort tmp;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // invert 1555/BGRA to 5551/RGBA (0x1f / 0x3e0 / 7c00)
                tmp=*(GLushort*)src_pos;
                *(GLushort*)dst_pos = ((tmp&0x8000)>>15) | ((tmp&0x7fff)<<1);
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // L -> RGBA
    if ((src_format == GL_LUMINANCE) && (dst_format == GL_RGBA) && (dst_type == GL_UNSIGNED_BYTE) && ((src_type == GL_UNSIGNED_BYTE))) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //tmp = *(const GLuint*)src_pos;
                auto* byte_dst = (unsigned char*)dst_pos;
#ifdef __BIG_ENDIAN__
                byte_dst[1] = byte_dst[2] = byte_dst[3] = *(GLubyte*)src_pos;
                byte_dst[0] = 255;
#else
                byte_dst[0] = byte_dst[1] = byte_dst[2] = *(GLubyte*)src_pos;
                byte_dst[3] = 255;
#endif
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // L -> RGB
    if ((src_format == GL_LUMINANCE) && (dst_format == GL_RGB) && (dst_type == GL_UNSIGNED_BYTE) && ((src_type == GL_UNSIGNED_BYTE))) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //tmp = *(const GLuint*)src_pos;
                auto* byte_dst = (unsigned char*)dst_pos;
                byte_dst[0] = byte_dst[1] = byte_dst[2] = *(GLubyte*)src_pos;
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // RGBA -> LA
    if ((src_format == GL_RGBA) && (dst_format == GL_LUMINANCE_ALPHA) && (dst_type == GL_UNSIGNED_BYTE) && ((src_type == GL_UNSIGNED_BYTE))) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //tmp = *(const GLuint*)src_pos;
                auto* byte_src = (unsigned char*)src_pos;
#ifdef __BIG_ENDIAN__
                *(GLushort*)dst_pos = ((((int)byte_src[3])*77 + ((int)byte_src[2])*151 + ((int)byte_src[1])*28)&0xff00)>>8 | (byte_src[0]<<8);
#else
                *(GLushort*)dst_pos = ((((int)byte_src[0])*77 + ((int)byte_src[1])*151 + ((int)byte_src[2])*28)&0xff00)>>8 | (byte_src[3]<<8);
#endif
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // BGRA -> LA
    if ((src_format == GL_BGRA) && (dst_format == GL_LUMINANCE_ALPHA) && (dst_type == GL_UNSIGNED_BYTE) && ((src_type == GL_UNSIGNED_BYTE))) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //tmp = *(const GLuint*)src_pos;
                auto* byte_src = (unsigned char*)src_pos;
#ifdef __BIG_ENDIAN__
                *(GLushort*)dst_pos = ((((int)byte_src[1])*77 + ((int)byte_src[2])*151 + ((int)byte_src[3])*28)&0xff00)>>8 | (byte_src[0]<<8);
#else
                *(GLushort*)dst_pos = ((((int)byte_src[2])*77 + ((int)byte_src[1])*151 + ((int)byte_src[0])*28)&0xff00)>>8 | (byte_src[3]<<8);
#endif
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // RGB(A) -> L
    if (((src_format == GL_RGBA)||(src_format == GL_RGB)) && (dst_format == GL_LUMINANCE) && (dst_type == GL_UNSIGNED_BYTE) && ((src_type == GL_UNSIGNED_BYTE))) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //tmp = *(const GLuint*)src_pos;
                auto* byte_src = (unsigned char*)src_pos;
#ifdef __BIG_ENDIAN__
                *(unsigned char*)dst_pos = (((int)byte_src[3])*77 + ((int)byte_src[2])*151 + ((int)byte_src[1])*28)>>8;
#else
                *(unsigned char*)dst_pos = (((int)byte_src[0])*77 + ((int)byte_src[1])*151 + ((int)byte_src[2])*28)>>8;
#endif
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // BGR(A) -> L
    if (((src_format == GL_BGRA)||(src_format == GL_BGR)) && (dst_format == GL_LUMINANCE) && (dst_type == GL_UNSIGNED_BYTE) && ((src_type == GL_UNSIGNED_BYTE))) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //tmp = *(const GLuint*)src_pos;
                auto* byte_src = (unsigned char*)src_pos;
#ifdef __BIG_ENDIAN__
                *(unsigned char*)dst_pos = (((int)byte_src[1])*77 + ((int)byte_src[2])*151 + ((int)byte_src[3])*28)>>8;
#else
                *(unsigned char*)dst_pos = (((int)byte_src[2])*77 + ((int)byte_src[1])*151 + ((int)byte_src[0])*28)>>8;
#endif
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // BGR(A) -> RGB
    if (((src_format == GL_BGR)||(src_format == GL_BGRA)) && (dst_format == GL_RGB) && (dst_type == GL_UNSIGNED_BYTE) && ((src_type == GL_UNSIGNED_BYTE))) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                ((char*)dst_pos)[0] = ((char*)src_pos)[2];
                ((char*)dst_pos)[1] = ((char*)src_pos)[1];
                ((char*)dst_pos)[2] = ((char*)src_pos)[0];
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // BGR -> RGBA
    if (((src_format == GL_BGR)) && (dst_format == GL_RGBA) && (dst_type == GL_UNSIGNED_BYTE) && ((src_type == GL_UNSIGNED_BYTE))) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                ((unsigned char*)dst_pos)[0] = ((unsigned char*)src_pos)[2];
                ((unsigned char*)dst_pos)[1] = ((unsigned char*)src_pos)[1];
                ((unsigned char*)dst_pos)[2] = ((unsigned char*)src_pos)[0];
                ((unsigned char*)dst_pos)[3] = 255;
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // RGBA -> RGB
    if ((src_format == GL_RGBA) && (dst_format == GL_RGB) && (dst_type == GL_UNSIGNED_BYTE) && ((src_type == GL_UNSIGNED_BYTE))) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                ((char*)dst_pos)[0] = ((char*)src_pos)[0];
                ((char*)dst_pos)[1] = ((char*)src_pos)[1];
                ((char*)dst_pos)[2] = ((char*)src_pos)[2];
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // RGB(A) -> RGB565
    if (((src_format == GL_RGB)||(src_format == GL_RGBA)) && (dst_format == GL_RGB) && (dst_type == GL_UNSIGNED_SHORT_5_6_5) && ((src_type == GL_UNSIGNED_BYTE))) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                *(GLushort*)dst_pos = ((GLushort)(((char*)src_pos)[2]&0xf8)>>(3)) | ((GLushort)(((char*)src_pos)[1]&0xfc)<<(5-2)) | ((GLushort)(((char*)src_pos)[0]&0xf8)<<(11-3));
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // BGR(A) -> RGB565
    if (((src_format == GL_BGR) || (src_format == GL_BGRA)) && (dst_format == GL_RGB) && (dst_type == GL_UNSIGNED_SHORT_5_6_5) && ((src_type == GL_UNSIGNED_BYTE))) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                *(GLushort*)dst_pos = ((GLushort)(((char*)src_pos)[0]&0xf8)>>(3)) | ((GLushort)(((char*)src_pos)[1]&0xfc)<<(5-2)) | ((GLushort)(((char*)src_pos)[2]&0xf8)<<(11-3));
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // RGBA -> RGBA5551
    if ((src_format == GL_RGBA) && (dst_format == GL_RGBA) && (dst_type == GL_UNSIGNED_SHORT_5_5_5_1) && ((src_type == GL_UNSIGNED_BYTE))) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                *(GLushort*)dst_pos = ((GLushort)(((char*)src_pos)[2]&0xf8)>>(3-1)) | ((GLushort)(((char*)src_pos)[1]&0xf8)<<(5-2)) | ((GLushort)(((char*)src_pos)[0]&0xf8)<<(10-2)) | ((GLushort)(((char*)src_pos)[3])?1:0);
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // BGRA -> RGBA5551
    if ((src_format == GL_BGRA) && (dst_format == GL_RGBA) && (dst_type == GL_UNSIGNED_SHORT_5_5_5_1) && ((src_type == GL_UNSIGNED_BYTE))) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                *(GLushort*)dst_pos = ((GLushort)(((char*)src_pos)[0]&0xf8)>>(3-1)) | ((GLushort)(((char*)src_pos)[1]&0xf8)<<(5-2)) | ((GLushort)(((char*)src_pos)[2]&0xf8)<<(10-2)) | ((GLushort)(((char*)src_pos)[3])?1:0);
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // RGBA -> RGBA4444
    if ((src_format == GL_RGBA) && (dst_format == GL_RGBA) && (dst_type == GL_UNSIGNED_SHORT_4_4_4_4) && ((src_type == GL_UNSIGNED_BYTE))) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                *(GLushort*)dst_pos = ((GLushort)(((char*)src_pos)[3]&0xf0))>>(4) | ((GLushort)(((char*)src_pos)[2]&0xf0)) | ((GLushort)(((char*)src_pos)[1]&0xf0))<<(4) | ((GLushort)(((char*)src_pos)[0]&0xf0))<<(8);
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // BGRA -> RGBA4444
    if ((src_format == GL_BGRA) && (dst_format == GL_RGBA) && (dst_type == GL_UNSIGNED_SHORT_4_4_4_4) && ((src_type == GL_UNSIGNED_BYTE))) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                *(GLushort*)dst_pos = ((GLushort)(((char*)src_pos)[3]&0xf0)>>(4)) | ((GLushort)(((char*)src_pos)[0]&0xf0)) | ((GLushort)(((char*)src_pos)[1]&0xf0)<<(4)) | ((GLushort)(((char*)src_pos)[2]&0xf0)<<(8));
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // BGRA4444 -> RGBA
    if ((src_format == GL_BGRA) && (dst_format == GL_RGBA) && (dst_type == GL_UNSIGNED_BYTE) && (src_type == GL_UNSIGNED_SHORT_4_4_4_4_REV)) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                const GLushort pix = *(GLushort*)src_pos;
                ((char*)dst_pos)[3] = ((pix>>12)&0x0f)<<4;
                ((char*)dst_pos)[2] = ((pix>>8)&0x0f)<<4;
                ((char*)dst_pos)[1] = ((pix>>4)&0x0f)<<4;
                ((char*)dst_pos)[0] = ((pix)&0x0f)<<4;
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    // RGBA5551 -> RGBA
    if ((src_format == GL_RGBA) && (dst_format == GL_RGBA) && (dst_type == GL_UNSIGNED_BYTE) && (src_type == GL_UNSIGNED_SHORT_5_5_5_1)) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                const GLushort pix = *(GLushort*)src_pos;
                ((unsigned char*)dst_pos)[0] = ((pix>>11)&0x1f)<<3;
                ((unsigned char*)dst_pos)[1] = ((pix>>6)&0x1f)<<3;
                ((unsigned char*)dst_pos)[2] = ((pix>>1)&0x1f)<<3;
                ((unsigned char*)dst_pos)[3] = ((pix)&0x01)?255:0;
                src_pos += src_stride;
                dst_pos += dst_stride;
            }
            dst_pos += dst_width;
            src_pos += src_widthadj;
        }
        return true;
    }
    return true;
}
