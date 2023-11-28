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

#ifndef _XA_CONTEXT_H_
#define _XA_CONTEXT_H_
#include "xa_tracker.h"
#include <stdint.h>

struct xa_context;

extern struct xa_context *xa_context_default(struct xa_tracker *xa);

extern struct xa_context *xa_context_create(struct xa_tracker *xa);

extern void xa_context_destroy(struct xa_context *r);

extern void xa_context_flush(struct xa_context *ctx);

/**
 * xa_yuv_planar_blit - 2D blit with color conversion and scaling.
 *
 * Performs a scaled blit with color conversion according to
 * (R,G,B,A)^T = (CM)^T (Y,U,V,1)^T, where @conversion_matrix or CM in the
 * formula is a four by four coefficient matrix. The input variable
 * @yuv is an array of three xa_yuv_component surfaces.
 */
extern int xa_yuv_planar_blit(struct xa_context *r,
			      int src_x,
			      int src_y,
			      int src_w,
			      int src_h,
			      int dst_x,
			      int dst_y,
			      int dst_w,
			      int dst_h,
			      struct xa_box *box,
			      unsigned int num_boxes,
			      const float conversion_matrix[],
			      struct xa_surface *dst, struct xa_surface *yuv[]);

extern int xa_copy_prepare(struct xa_context *ctx,
			   struct xa_surface *dst, struct xa_surface *src);

extern void xa_copy(struct xa_context *ctx,
		    int dx, int dy, int sx, int sy, int width, int height);

extern void xa_copy_done(struct xa_context *ctx);

extern int xa_surface_dma(struct xa_context *ctx,
			  struct xa_surface *srf,
			  void *data,
			  unsigned int byte_pitch,
			  int to_surface, struct xa_box *boxes,
			  unsigned int num_boxes);

extern void *xa_surface_map(struct xa_context *ctx,
			    struct xa_surface *srf, unsigned int usage);

extern void xa_surface_unmap(struct xa_surface *srf);

extern int
xa_solid_prepare(struct xa_context *ctx, struct xa_surface *dst,
		 uint32_t fg);
extern void
xa_solid(struct xa_context *ctx, int x, int y, int width, int height);

extern void
xa_solid_done(struct xa_context *ctx);

extern struct xa_fence *xa_fence_get(struct xa_context *ctx);

extern int xa_fence_wait(struct xa_fence *fence, uint64_t timeout);

extern void xa_fence_destroy(struct xa_fence *fence);
#endif
