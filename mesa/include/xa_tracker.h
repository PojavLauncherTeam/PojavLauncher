/**********************************************************
 * Copyright 2009-2011 VMware, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * The format encoding idea is partially borrowed from libpixman, but it is not
 * considered a "substantial part of the software", so the pixman copyright
 * is left out for simplicity, and acknowledgment is instead given in this way.
 *
 *********************************************************
 * Authors:
 * Zack Rusin <zackr-at-vmware-dot-com>
 * Thomas Hellstrom <thellstrom-at-vmware-dot-com>
 */

#ifndef _XA_TRACKER_H_
#define _XA_TRACKER_H_

#include <stdint.h>

#define XA_TRACKER_VERSION_MAJOR 2
#define XA_TRACKER_VERSION_MINOR 5
#define XA_TRACKER_VERSION_PATCH 0

#define XA_FLAG_SHARED         (1 << 0)
#define XA_FLAG_RENDER_TARGET  (1 << 1)
#define XA_FLAG_SCANOUT        (1 << 2)

#define XA_MAP_READ                     (1 << 0)
#define XA_MAP_WRITE                    (1 << 1)
#define XA_MAP_MAP_DIRECTLY             (1 << 2)
#define XA_MAP_UNSYNCHRONIZED           (1 << 3)
#define XA_MAP_DONTBLOCK                (1 << 4)
#define XA_MAP_DISCARD_WHOLE_RESOURCE   (1 << 5)

#define XA_ERR_NONE            0
#define XA_ERR_NORES           1
#define XA_ERR_INVAL           2
#define XA_ERR_BUSY            3

enum xa_surface_type {
    xa_type_other,
    xa_type_a,
    xa_type_argb,
    xa_type_abgr,
    xa_type_bgra,
    xa_type_z,
    xa_type_zs,
    xa_type_sz,
    xa_type_yuv_component
};

/*
 * Note that these formats should not be assumed to be binary compatible with
 * pixman formats, but with the below macros and a format type map,
 * conversion should be simple. Macros for now. We might replace with
 * inline functions.
 */

#define xa_format(bpp,type,a,r,g,b)	(((bpp) << 24) |  \
					 ((type) << 16) | \
					 ((a) << 12) |	  \
					 ((r) << 8) |	  \
					 ((g) << 4) |	  \
					 ((b)))
/*
 *  Non-RGBA one- and two component formats.
 */

#define xa_format_c(bpp,type,c1,c2) (((bpp) << 24) |	  \
				     ((type) << 16) |	  \
				     ((c1) << 8) |	  \
				     ((c2)))
#define xa_format_bpp(f)	(((f) >> 24)       )
#define xa_format_type(f)	(((f) >> 16) & 0xff)
#define xa_format_a(f)	(((f) >> 12) & 0x0f)
#define xa_format_r(f)	(((f) >>  8) & 0x0f)
#define xa_format_g(f)	(((f) >>  4) & 0x0f)
#define xa_format_b(f)	(((f)      ) & 0x0f)
#define xa_format_rgb(f)	(((f)      ) & 0xfff)
#define xa_format_c1(f)          (((f) >> 8 ) & 0xff)
#define xa_format_c2(f)          (((f)      ) & 0xff)
#define xa_format_argb_depth(f)	(xa_format_a(f) +	\
				 xa_format_r(f) +	\
				 xa_format_g(f) +	\
				 xa_format_b(f))
#define xa_format_c_depth(f)    (xa_format_c1(f) + \
				 xa_format_c2(f))

static inline int
xa_format_type_is_color(uint32_t xa_format)
{
    return (xa_format_type(xa_format) < xa_type_z);
}

static inline unsigned int
xa_format_depth(uint32_t xa_format)
{
    return ((xa_format_type_is_color(xa_format)) ?
	    xa_format_argb_depth(xa_format) : xa_format_c_depth(xa_format));
}

enum xa_formats {
    xa_format_unknown = 0,
    xa_format_a8 = xa_format(8, xa_type_a, 8, 0, 0, 0),

    xa_format_a8r8g8b8 = xa_format(32, xa_type_argb, 8, 8, 8, 8),
    xa_format_x8r8g8b8 = xa_format(32, xa_type_argb, 0, 8, 8, 8),
    xa_format_r5g6b5 = xa_format(16, xa_type_argb, 0, 5, 6, 5),
    xa_format_x1r5g5b5 = xa_format(16, xa_type_argb, 0, 5, 5, 5),
    xa_format_a4r4g4b4 = xa_format(16, xa_type_argb, 4, 4, 4, 4),
    xa_format_a2b10g10r10 = xa_format(32, xa_type_abgr, 2, 10, 10, 10),
    xa_format_x2b10g10r10 = xa_format(32, xa_type_abgr, 0, 10, 10, 10),
    xa_format_b8g8r8a8 = xa_format(32, xa_type_bgra, 8, 8, 8, 8),
    xa_format_b8g8r8x8 = xa_format(32, xa_type_bgra, 0, 8, 8, 8),

    xa_format_z16 = xa_format_c(16, xa_type_z, 16, 0),
    xa_format_z32 = xa_format_c(32, xa_type_z, 32, 0),
    xa_format_z24 = xa_format_c(32, xa_type_z, 24, 0),

    xa_format_x8z24 = xa_format_c(32, xa_type_sz, 24, 0),
    xa_format_s8z24 = xa_format_c(32, xa_type_sz, 24, 8),
    xa_format_z24x8 = xa_format_c(32, xa_type_zs, 24, 0),
    xa_format_z24s8 = xa_format_c(32, xa_type_zs, 24, 8),

    xa_format_yuv8 = xa_format_c(8, xa_type_yuv_component, 8, 0)
};

struct xa_tracker;
struct xa_surface;

struct xa_box {
    uint16_t x1, y1, x2, y2;
};

enum xa_handle_type {
    xa_handle_type_shared,
    xa_handle_type_kms,
    xa_handle_type_fd,
};

extern void xa_tracker_version(int *major, int *minor, int *patch);

extern struct xa_tracker *xa_tracker_create(int drm_fd);

extern void xa_tracker_destroy(struct xa_tracker *xa);

extern int xa_format_check_supported(struct xa_tracker *xa,
				     enum xa_formats xa_format,
				     unsigned int flags);

extern struct xa_surface *xa_surface_create(struct xa_tracker *xa,
					    int width,
					    int height,
					    int depth,
					    enum xa_surface_type stype,
					    enum xa_formats pform,
					    unsigned int flags);

extern struct xa_surface * xa_surface_from_handle(struct xa_tracker *xa,
					    int width,
					    int height,
					    int depth,
					    enum xa_surface_type stype,
					    enum xa_formats pform,
					    unsigned int flags,
					    uint32_t handle, uint32_t stride);
extern struct xa_surface *
xa_surface_from_handle2(struct xa_tracker *xa,
                        int width,
                        int height,
                        int depth,
                        enum xa_surface_type stype,
                        enum xa_formats xa_format,
                        unsigned int flags,
                        enum xa_handle_type type,
                        uint32_t handle,
                        uint32_t stride);

enum xa_formats xa_surface_format(const struct xa_surface *srf);

extern struct xa_surface *xa_surface_ref(struct xa_surface *srf);
extern void xa_surface_unref(struct xa_surface *srf);

extern int xa_surface_redefine(struct xa_surface *srf,
			       int width,
			       int height,
			       int depth,
			       enum xa_surface_type stype,
			       enum xa_formats rgb_format,
			       unsigned int new_flags,
			       int copy_contents);

extern int xa_surface_handle(struct xa_surface *srf,
			     enum xa_handle_type type,
			     uint32_t * handle,
			     unsigned int *byte_stride);

#endif
