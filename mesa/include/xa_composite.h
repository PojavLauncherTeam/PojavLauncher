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
 *********************************************************
 * Authors:
 * Zack Rusin <zackr-at-vmware-dot-com>
 * Thomas Hellstrom <thellstrom-at-vmware-dot-com>
 */

#ifndef _XA_COMPOSITE_H_
#define _XA_COMPOSITE_H_

#include "xa_tracker.h"
#include "xa_context.h"

/*
 * Supported composite ops.
 */
enum xa_composite_op {
    xa_op_clear,
    xa_op_src,
    xa_op_dst,
    xa_op_over,
    xa_op_over_reverse,
    xa_op_in,
    xa_op_in_reverse,
    xa_op_out,
    xa_op_out_reverse,
    xa_op_atop,
    xa_op_atop_reverse,
    xa_op_xor,
    xa_op_add
};

/*
 * Supported filters.
 */
enum xa_composite_filter {
    xa_filter_nearest,
    xa_filter_linear
};

/*
 * Supported clamp methods.
 */
enum xa_composite_wrap {
    xa_wrap_clamp_to_border,
    xa_wrap_repeat,
    xa_wrap_mirror_repeat,
    xa_wrap_clamp_to_edge
};

/*
 * Src picture types.
 */
enum xa_composite_src_pict_type {
    xa_src_pict_solid_fill,
    xa_src_pict_float_solid_fill
};


/*
 * struct xa_pict_solid_fill - Description of a solid_fill picture
 * Deprecated. Use struct xa_pict_float_solid_fill instead.
 */
struct xa_pict_solid_fill {
    enum xa_composite_src_pict_type type;
    unsigned int class;
    uint32_t color;
};

/*
 * struct xa_pict_solid_fill - Description of a solid_fill picture
 * with color channels represented by floats.
 */
struct xa_pict_float_solid_fill {
    enum xa_composite_src_pict_type type;
    float color[4]; /* R, G, B, A */
};

union xa_source_pict {
    enum xa_composite_src_pict_type type;
    struct xa_pict_solid_fill solid_fill;
    struct xa_pict_float_solid_fill float_solid_fill;
};

struct xa_picture {
    enum xa_formats pict_format;
    struct xa_surface *srf;
    struct xa_surface *alpha_map;
    float transform[9];
    int has_transform;
    int component_alpha;
    enum xa_composite_wrap wrap;
    enum xa_composite_filter filter;
    union xa_source_pict *src_pict;
};

struct xa_composite {
    struct xa_picture *src, *mask, *dst;
    int op;
    int no_solid;
};

struct xa_composite_allocation {
    unsigned int xa_composite_size;
    unsigned int xa_picture_size;
    unsigned int xa_source_pict_size;
};

/*
 * Get allocation sizes for minor bump compatibility.
 */

extern const struct xa_composite_allocation *
xa_composite_allocation(void);

/*
 * This function checks most things except the format of the hardware
 * surfaces, since they are generally not available at the time this
 * function is called. Returns usual XA error codes.
 */
extern int
xa_composite_check_accelerated(const struct xa_composite *comp);

extern int
xa_composite_prepare(struct xa_context *ctx, const struct xa_composite *comp);

extern void
xa_composite_rect(struct xa_context *ctx,
		  int srcX, int srcY, int maskX, int maskY,
		  int dstX, int dstY, int width, int height);
extern void
xa_composite_done(struct xa_context *ctx);

#endif
