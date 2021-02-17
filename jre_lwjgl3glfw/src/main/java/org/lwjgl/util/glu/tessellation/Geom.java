/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
* Portions Copyright (C) 2003-2006 Sun Microsystems, Inc.
* All rights reserved.
*/

/*
** License Applicability. Except to the extent portions of this file are
** made subject to an alternative license as permitted in the SGI Free
** Software License B, Version 1.1 (the "License"), the contents of this
** file are subject only to the provisions of the License. You may not use
** this file except in compliance with the License. You may obtain a copy
** of the License at Silicon Graphics, Inc., attn: Legal Services, 1600
** Amphitheatre Parkway, Mountain View, CA 94043-1351, or at:
**
** http://oss.sgi.com/projects/FreeB
**
** Note that, as provided in the License, the Software is distributed on an
** "AS IS" basis, with ALL EXPRESS AND IMPLIED WARRANTIES AND CONDITIONS
** DISCLAIMED, INCLUDING, WITHOUT LIMITATION, ANY IMPLIED WARRANTIES AND
** CONDITIONS OF MERCHANTABILITY, SATISFACTORY QUALITY, FITNESS FOR A
** PARTICULAR PURPOSE, AND NON-INFRINGEMENT.
**
** NOTE:  The Original Code (as defined below) has been licensed to Sun
** Microsystems, Inc. ("Sun") under the SGI Free Software License B
** (Version 1.1), shown above ("SGI License").   Pursuant to Section
** 3.2(3) of the SGI License, Sun is distributing the Covered Code to
** you under an alternative license ("Alternative License").  This
** Alternative License includes all of the provisions of the SGI License
** except that Section 2.2 and 11 are omitted.  Any differences between
** the Alternative License and the SGI License are offered solely by Sun
** and not by SGI.
**
** Original Code. The Original Code is: OpenGL Sample Implementation,
** Version 1.2.1, released January 26, 2000, developed by Silicon Graphics,
** Inc. The Original Code is Copyright (c) 1991-2000 Silicon Graphics, Inc.
** Copyright in any portions created by third parties is as indicated
** elsewhere herein. All Rights Reserved.
**
** Additional Notice Provisions: The application programming interfaces
** established by SGI in conjunction with the Original Code are The
** OpenGL(R) Graphics System: A Specification (Version 1.2.1), released
** April 1, 1999; The OpenGL(R) Graphics System Utility Library (Version
** 1.3), released November 4, 1998; and OpenGL(R) Graphics with the X
** Window System(R) (Version 1.3), released October 19, 1998. This software
** was created using the OpenGL(R) version 1.2.1 Sample Implementation
** published by SGI, but has not been independently verified as being
** compliant with the OpenGL(R) version 1.2.1 Specification.
**
** Author: Eric Veach, July 1994
** Java Port: Pepijn Van Eeckhoudt, July 2003
** Java Port: Nathan Parker Burg, August 2003
*/
package org.lwjgl.util.glu.tessellation;

class Geom {
    private Geom() {
    }

    /* Given three vertices u,v,w such that VertLeq(u,v) && VertLeq(v,w),
     * evaluates the t-coord of the edge uw at the s-coord of the vertex v.
     * Returns v->t - (uw)(v->s), ie. the signed distance from uw to v.
     * If uw is vertical (and thus passes thru v), the result is zero.
     *
     * The calculation is extremely accurate and stable, even when v
     * is very close to u or w.  In particular if we set v->t = 0 and
     * let r be the negated result (this evaluates (uw)(v->s)), then
     * r is guaranteed to satisfy MIN(u->t,w->t) <= r <= MAX(u->t,w->t).
     */
    static double EdgeEval(GLUvertex u, GLUvertex v, GLUvertex w) {
        double gapL, gapR;

        assert (VertLeq(u, v) && VertLeq(v, w));

        gapL = v.s - u.s;
        gapR = w.s - v.s;

        if (gapL + gapR > 0) {
            if (gapL < gapR) {
                return (v.t - u.t) + (u.t - w.t) * (gapL / (gapL + gapR));
            } else {
                return (v.t - w.t) + (w.t - u.t) * (gapR / (gapL + gapR));
            }
        }
        /* vertical line */
        return 0;
    }

    static double EdgeSign(GLUvertex u, GLUvertex v, GLUvertex w) {
        double gapL, gapR;

        assert (VertLeq(u, v) && VertLeq(v, w));

        gapL = v.s - u.s;
        gapR = w.s - v.s;

        if (gapL + gapR > 0) {
            return (v.t - w.t) * gapL + (v.t - u.t) * gapR;
        }
        /* vertical line */
        return 0;
    }


    /***********************************************************************
     * Define versions of EdgeSign, EdgeEval with s and t transposed.
     */

    static double TransEval(GLUvertex u, GLUvertex v, GLUvertex w) {
        /* Given three vertices u,v,w such that TransLeq(u,v) && TransLeq(v,w),
         * evaluates the t-coord of the edge uw at the s-coord of the vertex v.
         * Returns v->s - (uw)(v->t), ie. the signed distance from uw to v.
         * If uw is vertical (and thus passes thru v), the result is zero.
         *
         * The calculation is extremely accurate and stable, even when v
         * is very close to u or w.  In particular if we set v->s = 0 and
         * let r be the negated result (this evaluates (uw)(v->t)), then
         * r is guaranteed to satisfy MIN(u->s,w->s) <= r <= MAX(u->s,w->s).
         */
        double gapL, gapR;

        assert (TransLeq(u, v) && TransLeq(v, w));

        gapL = v.t - u.t;
        gapR = w.t - v.t;

        if (gapL + gapR > 0) {
            if (gapL < gapR) {
                return (v.s - u.s) + (u.s - w.s) * (gapL / (gapL + gapR));
            } else {
                return (v.s - w.s) + (w.s - u.s) * (gapR / (gapL + gapR));
            }
        }
        /* vertical line */
        return 0;
    }

    static double TransSign(GLUvertex u, GLUvertex v, GLUvertex w) {
        /* Returns a number whose sign matches TransEval(u,v,w) but which
         * is cheaper to evaluate.  Returns > 0, == 0 , or < 0
         * as v is above, on, or below the edge uw.
         */
        double gapL, gapR;

        assert (TransLeq(u, v) && TransLeq(v, w));

        gapL = v.t - u.t;
        gapR = w.t - v.t;

        if (gapL + gapR > 0) {
            return (v.s - w.s) * gapL + (v.s - u.s) * gapR;
        }
        /* vertical line */
        return 0;
    }


    static boolean VertCCW(GLUvertex u, GLUvertex v, GLUvertex w) {
        /* For almost-degenerate situations, the results are not reliable.
         * Unless the floating-point arithmetic can be performed without
         * rounding errors, *any* implementation will give incorrect results
         * on some degenerate inputs, so the client must have some way to
         * handle this situation.
         */
        return (u.s * (v.t - w.t) + v.s * (w.t - u.t) + w.s * (u.t - v.t)) >= 0;
    }

/* Given parameters a,x,b,y returns the value (b*x+a*y)/(a+b),
 * or (x+y)/2 if a==b==0.  It requires that a,b >= 0, and enforces
 * this in the rare case that one argument is slightly negative.
 * The implementation is extremely stable numerically.
 * In particular it guarantees that the result r satisfies
 * MIN(x,y) <= r <= MAX(x,y), and the results are very accurate
 * even when a and b differ greatly in magnitude.
 */
    static double Interpolate(double a, double x, double b, double y) {
        a = (a < 0) ? 0 : a;
        b = (b < 0) ? 0 : b;
        if (a <= b) {
            if (b == 0) {
                return (x + y) / 2.0;
            } else {
                return (x + (y - x) * (a / (a + b)));
            }
        } else {
            return (y + (x - y) * (b / (a + b)));
        }
    }

    static void EdgeIntersect(GLUvertex o1, GLUvertex d1,
                              GLUvertex o2, GLUvertex d2,
                              GLUvertex v)
/* Given edges (o1,d1) and (o2,d2), compute their point of intersection.
 * The computed point is guaranteed to lie in the intersection of the
 * bounding rectangles defined by each edge.
 */ {
        double z1, z2;

        /* This is certainly not the most efficient way to find the intersection
         * of two line segments, but it is very numerically stable.
         *
         * Strategy: find the two middle vertices in the VertLeq ordering,
         * and interpolate the intersection s-value from these.  Then repeat
         * using the TransLeq ordering to find the intersection t-value.
         */

        if (!VertLeq(o1, d1)) {
            GLUvertex temp = o1;
            o1 = d1;
            d1 = temp;
        }
        if (!VertLeq(o2, d2)) {
            GLUvertex temp = o2;
            o2 = d2;
            d2 = temp;
        }
        if (!VertLeq(o1, o2)) {
            GLUvertex temp = o1;
            o1 = o2;
            o2 = temp;
            temp = d1;
            d1 = d2;
            d2 = temp;
        }

        if (!VertLeq(o2, d1)) {
            /* Technically, no intersection -- do our best */
            v.s = (o2.s + d1.s) / 2.0;
        } else if (VertLeq(d1, d2)) {
            /* Interpolate between o2 and d1 */
            z1 = EdgeEval(o1, o2, d1);
            z2 = EdgeEval(o2, d1, d2);
            if (z1 + z2 < 0) {
                z1 = -z1;
                z2 = -z2;
            }
            v.s = Interpolate(z1, o2.s, z2, d1.s);
        } else {
            /* Interpolate between o2 and d2 */
            z1 = EdgeSign(o1, o2, d1);
            z2 = -EdgeSign(o1, d2, d1);
            if (z1 + z2 < 0) {
                z1 = -z1;
                z2 = -z2;
            }
            v.s = Interpolate(z1, o2.s, z2, d2.s);
        }

        /* Now repeat the process for t */

        if (!TransLeq(o1, d1)) {
            GLUvertex temp = o1;
            o1 = d1;
            d1 = temp;
        }
        if (!TransLeq(o2, d2)) {
            GLUvertex temp = o2;
            o2 = d2;
            d2 = temp;
        }
        if (!TransLeq(o1, o2)) {
            GLUvertex temp = o2;
            o2 = o1;
            o1 = temp;
            temp = d2;
            d2 = d1;
            d1 = temp;
        }

        if (!TransLeq(o2, d1)) {
            /* Technically, no intersection -- do our best */
            v.t = (o2.t + d1.t) / 2.0;
        } else if (TransLeq(d1, d2)) {
            /* Interpolate between o2 and d1 */
            z1 = TransEval(o1, o2, d1);
            z2 = TransEval(o2, d1, d2);
            if (z1 + z2 < 0) {
                z1 = -z1;
                z2 = -z2;
            }
            v.t = Interpolate(z1, o2.t, z2, d1.t);
        } else {
            /* Interpolate between o2 and d2 */
            z1 = TransSign(o1, o2, d1);
            z2 = -TransSign(o1, d2, d1);
            if (z1 + z2 < 0) {
                z1 = -z1;
                z2 = -z2;
            }
            v.t = Interpolate(z1, o2.t, z2, d2.t);
        }
    }

    static boolean VertEq(GLUvertex u, GLUvertex v) {
        return u.s == v.s && u.t == v.t;
    }

    static boolean VertLeq(GLUvertex u, GLUvertex v) {
        return u.s < v.s || (u.s == v.s && u.t <= v.t);
    }

/* Versions of VertLeq, EdgeSign, EdgeEval with s and t transposed. */

    static boolean TransLeq(GLUvertex u, GLUvertex v) {
        return u.t < v.t || (u.t == v.t && u.s <= v.s);
    }

    static boolean EdgeGoesLeft(GLUhalfEdge e) {
        return VertLeq(e.Sym.Org, e.Org);
    }

    static boolean EdgeGoesRight(GLUhalfEdge e) {
        return VertLeq(e.Org, e.Sym.Org);
    }

    static double VertL1dist(GLUvertex u, GLUvertex v) {
        return Math.abs(u.s - v.s) + Math.abs(u.t - v.t);
    }
}
