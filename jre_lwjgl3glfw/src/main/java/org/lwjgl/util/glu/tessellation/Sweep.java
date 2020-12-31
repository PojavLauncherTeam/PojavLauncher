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

import static org.lwjgl.util.glu.GLU.*;

class Sweep {
    private Sweep() {
    }

//    #ifdef FOR_TRITE_TEST_PROGRAM
//    extern void DebugEvent( GLUtessellator *tess );
//    #else
    private static void DebugEvent(GLUtessellatorImpl tess) {

    }
//    #endif

/*
 * Invariants for the Edge Dictionary.
 * - each pair of adjacent edges e2=Succ(e1) satisfies EdgeLeq(e1,e2)
 *   at any valid location of the sweep event
 * - if EdgeLeq(e2,e1) as well (at any valid sweep event), then e1 and e2
 *   share a common endpoint
 * - for each e, e.Dst has been processed, but not e.Org
 * - each edge e satisfies VertLeq(e.Dst,event) && VertLeq(event,e.Org)
 *   where "event" is the current sweep line event.
 * - no edge e has zero length
 *
 * Invariants for the Mesh (the processed portion).
 * - the portion of the mesh left of the sweep line is a planar graph,
 *   ie. there is *some* way to embed it in the plane
 * - no processed edge has zero length
 * - no two processed vertices have identical coordinates
 * - each "inside" region is monotone, ie. can be broken into two chains
 *   of monotonically increasing vertices according to VertLeq(v1,v2)
 *   - a non-invariant: these chains may intersect (very slightly)
 *
 * Invariants for the Sweep.
 * - if none of the edges incident to the event vertex have an activeRegion
 *   (ie. none of these edges are in the edge dictionary), then the vertex
 *   has only right-going edges.
 * - if an edge is marked "fixUpperEdge" (it is a temporary edge introduced
 *   by ConnectRightVertex), then it is the only right-going edge from
 *   its associated vertex.  (This says that these edges exist only
 *   when it is necessary.)
 */

/* When we merge two edges into one, we need to compute the combined
 * winding of the new edge.
 */
    private static void AddWinding(GLUhalfEdge eDst, GLUhalfEdge eSrc) {
        eDst.winding += eSrc.winding;
        eDst.Sym.winding += eSrc.Sym.winding;
    }


    private static ActiveRegion RegionBelow(ActiveRegion r) {
        return ((ActiveRegion) Dict.dictKey(Dict.dictPred(r.nodeUp)));
    }

    private static ActiveRegion RegionAbove(ActiveRegion r) {
        return ((ActiveRegion) Dict.dictKey(Dict.dictSucc(r.nodeUp)));
    }

    static boolean EdgeLeq(GLUtessellatorImpl tess, ActiveRegion reg1, ActiveRegion reg2)
/*
 * Both edges must be directed from right to left (this is the canonical
 * direction for the upper edge of each region).
 *
 * The strategy is to evaluate a "t" value for each edge at the
 * current sweep line position, given by tess.event.  The calculations
 * are designed to be very stable, but of course they are not perfect.
 *
 * Special case: if both edge destinations are at the sweep event,
 * we sort the edges by slope (they would otherwise compare equally).
 */ {
        GLUvertex event = tess.event;
        GLUhalfEdge e1, e2;
        double t1, t2;

        e1 = reg1.eUp;
        e2 = reg2.eUp;

        if (e1.Sym.Org == event) {
            if (e2.Sym.Org == event) {
                /* Two edges right of the sweep line which meet at the sweep event.
                 * Sort them by slope.
                 */
                if (Geom.VertLeq(e1.Org, e2.Org)) {
                    return Geom.EdgeSign(e2.Sym.Org, e1.Org, e2.Org) <= 0;
                }
                return Geom.EdgeSign(e1.Sym.Org, e2.Org, e1.Org) >= 0;
            }
            return Geom.EdgeSign(e2.Sym.Org, event, e2.Org) <= 0;
        }
        if (e2.Sym.Org == event) {
            return Geom.EdgeSign(e1.Sym.Org, event, e1.Org) >= 0;
        }

        /* General case - compute signed distance *from* e1, e2 to event */
        t1 = Geom.EdgeEval(e1.Sym.Org, event, e1.Org);
        t2 = Geom.EdgeEval(e2.Sym.Org, event, e2.Org);
        return (t1 >= t2);
    }


    static void DeleteRegion(GLUtessellatorImpl tess, ActiveRegion reg) {
        if (reg.fixUpperEdge) {
            /* It was created with zero winding number, so it better be
             * deleted with zero winding number (ie. it better not get merged
             * with a real edge).
             */
            assert (reg.eUp.winding == 0);
        }
        reg.eUp.activeRegion = null;
        Dict.dictDelete(tess.dict, reg.nodeUp); /* __gl_dictListDelete */
    }


    static boolean FixUpperEdge(ActiveRegion reg, GLUhalfEdge newEdge)
/*
 * Replace an upper edge which needs fixing (see ConnectRightVertex).
 */ {
        assert (reg.fixUpperEdge);
        if (!Mesh.__gl_meshDelete(reg.eUp)) return false;
        reg.fixUpperEdge = false;
        reg.eUp = newEdge;
        newEdge.activeRegion = reg;

        return true;
    }

    static ActiveRegion TopLeftRegion(ActiveRegion reg) {
        GLUvertex org = reg.eUp.Org;
        GLUhalfEdge e;

        /* Find the region above the uppermost edge with the same origin */
        do {
            reg = RegionAbove(reg);
        } while (reg.eUp.Org == org);

        /* If the edge above was a temporary edge introduced by ConnectRightVertex,
         * now is the time to fix it.
         */
        if (reg.fixUpperEdge) {
            e = Mesh.__gl_meshConnect(RegionBelow(reg).eUp.Sym, reg.eUp.Lnext);
            if (e == null) return null;
            if (!FixUpperEdge(reg, e)) return null;
            reg = RegionAbove(reg);
        }
        return reg;
    }

    static ActiveRegion TopRightRegion(ActiveRegion reg) {
        GLUvertex dst = reg.eUp.Sym.Org;

        /* Find the region above the uppermost edge with the same destination */
        do {
            reg = RegionAbove(reg);
        } while (reg.eUp.Sym.Org == dst);
        return reg;
    }

    static ActiveRegion AddRegionBelow(GLUtessellatorImpl tess,
                                       ActiveRegion regAbove,
                                       GLUhalfEdge eNewUp)
/*
 * Add a new active region to the sweep line, *somewhere* below "regAbove"
 * (according to where the new edge belongs in the sweep-line dictionary).
 * The upper edge of the new region will be "eNewUp".
 * Winding number and "inside" flag are not updated.
 */ {
        ActiveRegion regNew = new ActiveRegion();
        //if (regNew == null) throw new RuntimeException();

        regNew.eUp = eNewUp;
        /* __gl_dictListInsertBefore */
        regNew.nodeUp = Dict.dictInsertBefore(tess.dict, regAbove.nodeUp, regNew);
        if (regNew.nodeUp == null) throw new RuntimeException();
        regNew.fixUpperEdge = false;
        regNew.sentinel = false;
        regNew.dirty = false;

        eNewUp.activeRegion = regNew;
        return regNew;
    }

    static boolean IsWindingInside(GLUtessellatorImpl tess, int n) {
        switch (tess.windingRule) {
            case GLU_TESS_WINDING_ODD:
                return (n & 1) != 0;
            case GLU_TESS_WINDING_NONZERO:
                return (n != 0);
            case GLU_TESS_WINDING_POSITIVE:
                return (n > 0);
            case GLU_TESS_WINDING_NEGATIVE:
                return (n < 0);
            case GLU_TESS_WINDING_ABS_GEQ_TWO:
                return (n >= 2) || (n <= -2);
        }
        /*LINTED*/
//        assert (false);
        throw new InternalError();
        /*NOTREACHED*/
    }


    static void ComputeWinding(GLUtessellatorImpl tess, ActiveRegion reg) {
        reg.windingNumber = RegionAbove(reg).windingNumber + reg.eUp.winding;
        reg.inside = IsWindingInside(tess, reg.windingNumber);
    }


    static void FinishRegion(GLUtessellatorImpl tess, ActiveRegion reg)
/*
 * Delete a region from the sweep line.  This happens when the upper
 * and lower chains of a region meet (at a vertex on the sweep line).
 * The "inside" flag is copied to the appropriate mesh face (we could
 * not do this before -- since the structure of the mesh is always
 * changing, this face may not have even existed until now).
 */ {
        GLUhalfEdge e = reg.eUp;
        GLUface f = e.Lface;

        f.inside = reg.inside;
        f.anEdge = e;   /* optimization for __gl_meshTessellateMonoRegion() */
        DeleteRegion(tess, reg);
    }


    static GLUhalfEdge FinishLeftRegions(GLUtessellatorImpl tess,
                                         ActiveRegion regFirst, ActiveRegion regLast)
/*
 * We are given a vertex with one or more left-going edges.  All affected
 * edges should be in the edge dictionary.  Starting at regFirst.eUp,
 * we walk down deleting all regions where both edges have the same
 * origin vOrg.  At the same time we copy the "inside" flag from the
 * active region to the face, since at this point each face will belong
 * to at most one region (this was not necessarily true until this point
 * in the sweep).  The walk stops at the region above regLast; if regLast
 * is null we walk as far as possible.  At the same time we relink the
 * mesh if necessary, so that the ordering of edges around vOrg is the
 * same as in the dictionary.
 */ {
        ActiveRegion reg, regPrev;
        GLUhalfEdge e, ePrev;

        regPrev = regFirst;
        ePrev = regFirst.eUp;
        while (regPrev != regLast) {
            regPrev.fixUpperEdge = false;	/* placement was OK */
            reg = RegionBelow(regPrev);
            e = reg.eUp;
            if (e.Org != ePrev.Org) {
                if (!reg.fixUpperEdge) {
                    /* Remove the last left-going edge.  Even though there are no further
                     * edges in the dictionary with this origin, there may be further
                     * such edges in the mesh (if we are adding left edges to a vertex
                     * that has already been processed).  Thus it is important to call
                     * FinishRegion rather than just DeleteRegion.
                     */
                    FinishRegion(tess, regPrev);
                    break;
                }
                /* If the edge below was a temporary edge introduced by
                 * ConnectRightVertex, now is the time to fix it.
                 */
                e = Mesh.__gl_meshConnect(ePrev.Onext.Sym, e.Sym);
                if (e == null) throw new RuntimeException();
                if (!FixUpperEdge(reg, e)) throw new RuntimeException();
            }

            /* Relink edges so that ePrev.Onext == e */
            if (ePrev.Onext != e) {
                if (!Mesh.__gl_meshSplice(e.Sym.Lnext, e)) throw new RuntimeException();
                if (!Mesh.__gl_meshSplice(ePrev, e)) throw new RuntimeException();
            }
            FinishRegion(tess, regPrev);	/* may change reg.eUp */
            ePrev = reg.eUp;
            regPrev = reg;
        }
        return ePrev;
    }


    static void AddRightEdges(GLUtessellatorImpl tess, ActiveRegion regUp,
                              GLUhalfEdge eFirst, GLUhalfEdge eLast, GLUhalfEdge eTopLeft,
                              boolean cleanUp)
/*
 * Purpose: insert right-going edges into the edge dictionary, and update
 * winding numbers and mesh connectivity appropriately.  All right-going
 * edges share a common origin vOrg.  Edges are inserted CCW starting at
 * eFirst; the last edge inserted is eLast.Sym.Lnext.  If vOrg has any
 * left-going edges already processed, then eTopLeft must be the edge
 * such that an imaginary upward vertical segment from vOrg would be
 * contained between eTopLeft.Sym.Lnext and eTopLeft; otherwise eTopLeft
 * should be null.
 */ {
        ActiveRegion reg, regPrev;
        GLUhalfEdge e, ePrev;
        boolean firstTime = true;

        /* Insert the new right-going edges in the dictionary */
        e = eFirst;
        do {
            assert (Geom.VertLeq(e.Org, e.Sym.Org));
            AddRegionBelow(tess, regUp, e.Sym);
            e = e.Onext;
        } while (e != eLast);

        /* Walk *all* right-going edges from e.Org, in the dictionary order,
         * updating the winding numbers of each region, and re-linking the mesh
         * edges to match the dictionary ordering (if necessary).
         */
        if (eTopLeft == null) {
            eTopLeft = RegionBelow(regUp).eUp.Sym.Onext;
        }
        regPrev = regUp;
        ePrev = eTopLeft;
        for (; ;) {
            reg = RegionBelow(regPrev);
            e = reg.eUp.Sym;
            if (e.Org != ePrev.Org) break;

            if (e.Onext != ePrev) {
                /* Unlink e from its current position, and relink below ePrev */
                if (!Mesh.__gl_meshSplice(e.Sym.Lnext, e)) throw new RuntimeException();
                if (!Mesh.__gl_meshSplice(ePrev.Sym.Lnext, e)) throw new RuntimeException();
            }
            /* Compute the winding number and "inside" flag for the new regions */
            reg.windingNumber = regPrev.windingNumber - e.winding;
            reg.inside = IsWindingInside(tess, reg.windingNumber);

            /* Check for two outgoing edges with same slope -- process these
             * before any intersection tests (see example in __gl_computeInterior).
             */
            regPrev.dirty = true;
            if (!firstTime && CheckForRightSplice(tess, regPrev)) {
                AddWinding(e, ePrev);
                DeleteRegion(tess, regPrev);
                if (!Mesh.__gl_meshDelete(ePrev)) throw new RuntimeException();
            }
            firstTime = false;
            regPrev = reg;
            ePrev = e;
        }
        regPrev.dirty = true;
        assert (regPrev.windingNumber - e.winding == reg.windingNumber);

        if (cleanUp) {
            /* Check for intersections between newly adjacent edges. */
            WalkDirtyRegions(tess, regPrev);
        }
    }


    static void CallCombine(GLUtessellatorImpl tess, GLUvertex isect,
                            Object[] data, float[] weights, boolean needed) {
        double[] coords = new double[3];

        /* Copy coord data in case the callback changes it. */
        coords[0] = isect.coords[0];
        coords[1] = isect.coords[1];
        coords[2] = isect.coords[2];

        Object[] outData = new Object[1];
        tess.callCombineOrCombineData(coords, data, weights, outData);
        isect.data = outData[0];
        if (isect.data == null) {
            if (!needed) {
                isect.data = data[0];
            } else if (!tess.fatalError) {
                /* The only way fatal error is when two edges are found to intersect,
                 * but the user has not provided the callback necessary to handle
                 * generated intersection points.
                 */
                tess.callErrorOrErrorData(GLU_TESS_NEED_COMBINE_CALLBACK);
                tess.fatalError = true;
            }
        }
    }

    static void SpliceMergeVertices(GLUtessellatorImpl tess, GLUhalfEdge e1,
                                    GLUhalfEdge e2)
/*
 * Two vertices with idential coordinates are combined into one.
 * e1.Org is kept, while e2.Org is discarded.
 */ {
        Object[] data = new Object[4];
        float[] weights = new float[]{0.5f, 0.5f, 0.0f, 0.0f};

        data[0] = e1.Org.data;
        data[1] = e2.Org.data;
        CallCombine(tess, e1.Org, data, weights, false);
        if (!Mesh.__gl_meshSplice(e1, e2)) throw new RuntimeException();
    }

    static void VertexWeights(GLUvertex isect, GLUvertex org, GLUvertex dst,
                              float[] weights)
/*
 * Find some weights which describe how the intersection vertex is
 * a linear combination of "org" and "dest".  Each of the two edges
 * which generated "isect" is allocated 50% of the weight; each edge
 * splits the weight between its org and dst according to the
 * relative distance to "isect".
 */ {
        double t1 = Geom.VertL1dist(org, isect);
        double t2 = Geom.VertL1dist(dst, isect);

        weights[0] = (float) (0.5 * t2 / (t1 + t2));
        weights[1] = (float) (0.5 * t1 / (t1 + t2));
        isect.coords[0] += weights[0] * org.coords[0] + weights[1] * dst.coords[0];
        isect.coords[1] += weights[0] * org.coords[1] + weights[1] * dst.coords[1];
        isect.coords[2] += weights[0] * org.coords[2] + weights[1] * dst.coords[2];
    }


    static void GetIntersectData(GLUtessellatorImpl tess, GLUvertex isect,
                                 GLUvertex orgUp, GLUvertex dstUp,
                                 GLUvertex orgLo, GLUvertex dstLo)
/*
 * We've computed a new intersection point, now we need a "data" pointer
 * from the user so that we can refer to this new vertex in the
 * rendering callbacks.
 */ {
        Object[] data = new Object[4];
        float[] weights = new float[4];
        float[] weights1 = new float[2];
        float[] weights2 = new float[2];

        data[0] = orgUp.data;
        data[1] = dstUp.data;
        data[2] = orgLo.data;
        data[3] = dstLo.data;

        isect.coords[0] = isect.coords[1] = isect.coords[2] = 0;
        VertexWeights(isect, orgUp, dstUp, weights1);
        VertexWeights(isect, orgLo, dstLo, weights2);
        System.arraycopy(weights1, 0, weights, 0, 2);
        System.arraycopy(weights2, 0, weights, 2, 2);

        CallCombine(tess, isect, data, weights, true);
    }

    static boolean CheckForRightSplice(GLUtessellatorImpl tess, ActiveRegion regUp)
/*
 * Check the upper and lower edge of "regUp", to make sure that the
 * eUp.Org is above eLo, or eLo.Org is below eUp (depending on which
 * origin is leftmost).
 *
 * The main purpose is to splice right-going edges with the same
 * dest vertex and nearly identical slopes (ie. we can't distinguish
 * the slopes numerically).  However the splicing can also help us
 * to recover from numerical errors.  For example, suppose at one
 * point we checked eUp and eLo, and decided that eUp.Org is barely
 * above eLo.  Then later, we split eLo into two edges (eg. from
 * a splice operation like this one).  This can change the result of
 * our test so that now eUp.Org is incident to eLo, or barely below it.
 * We must correct this condition to maintain the dictionary invariants.
 *
 * One possibility is to check these edges for intersection again
 * (ie. CheckForIntersect).  This is what we do if possible.  However
 * CheckForIntersect requires that tess.event lies between eUp and eLo,
 * so that it has something to fall back on when the intersection
 * calculation gives us an unusable answer.  So, for those cases where
 * we can't check for intersection, this routine fixes the problem
 * by just splicing the offending vertex into the other edge.
 * This is a guaranteed solution, no matter how degenerate things get.
 * Basically this is a combinatorial solution to a numerical problem.
 */ {
        ActiveRegion regLo = RegionBelow(regUp);
        GLUhalfEdge eUp = regUp.eUp;
        GLUhalfEdge eLo = regLo.eUp;

        if (Geom.VertLeq(eUp.Org, eLo.Org)) {
            if (Geom.EdgeSign(eLo.Sym.Org, eUp.Org, eLo.Org) > 0) return false;

            /* eUp.Org appears to be below eLo */
            if (!Geom.VertEq(eUp.Org, eLo.Org)) {
                /* Splice eUp.Org into eLo */
                if (Mesh.__gl_meshSplitEdge(eLo.Sym) == null) throw new RuntimeException();
                if (!Mesh.__gl_meshSplice(eUp, eLo.Sym.Lnext)) throw new RuntimeException();
                regUp.dirty = regLo.dirty = true;

            } else if (eUp.Org != eLo.Org) {
                /* merge the two vertices, discarding eUp.Org */
                tess.pq.pqDelete(eUp.Org.pqHandle); /* __gl_pqSortDelete */
                SpliceMergeVertices(tess, eLo.Sym.Lnext, eUp);
            }
        } else {
            if (Geom.EdgeSign(eUp.Sym.Org, eLo.Org, eUp.Org) < 0) return false;

            /* eLo.Org appears to be above eUp, so splice eLo.Org into eUp */
            RegionAbove(regUp).dirty = regUp.dirty = true;
            if (Mesh.__gl_meshSplitEdge(eUp.Sym) == null) throw new RuntimeException();
            if (!Mesh.__gl_meshSplice(eLo.Sym.Lnext, eUp)) throw new RuntimeException();
        }
        return true;
    }

    static boolean CheckForLeftSplice(GLUtessellatorImpl tess, ActiveRegion regUp)
/*
 * Check the upper and lower edge of "regUp", to make sure that the
 * eUp.Sym.Org is above eLo, or eLo.Sym.Org is below eUp (depending on which
 * destination is rightmost).
 *
 * Theoretically, this should always be true.  However, splitting an edge
 * into two pieces can change the results of previous tests.  For example,
 * suppose at one point we checked eUp and eLo, and decided that eUp.Sym.Org
 * is barely above eLo.  Then later, we split eLo into two edges (eg. from
 * a splice operation like this one).  This can change the result of
 * the test so that now eUp.Sym.Org is incident to eLo, or barely below it.
 * We must correct this condition to maintain the dictionary invariants
 * (otherwise new edges might get inserted in the wrong place in the
 * dictionary, and bad stuff will happen).
 *
 * We fix the problem by just splicing the offending vertex into the
 * other edge.
 */ {
        ActiveRegion regLo = RegionBelow(regUp);
        GLUhalfEdge eUp = regUp.eUp;
        GLUhalfEdge eLo = regLo.eUp;
        GLUhalfEdge e;

        assert (!Geom.VertEq(eUp.Sym.Org, eLo.Sym.Org));

        if (Geom.VertLeq(eUp.Sym.Org, eLo.Sym.Org)) {
            if (Geom.EdgeSign(eUp.Sym.Org, eLo.Sym.Org, eUp.Org) < 0) return false;

            /* eLo.Sym.Org is above eUp, so splice eLo.Sym.Org into eUp */
            RegionAbove(regUp).dirty = regUp.dirty = true;
            e = Mesh.__gl_meshSplitEdge(eUp);
            if (e == null) throw new RuntimeException();
            if (!Mesh.__gl_meshSplice(eLo.Sym, e)) throw new RuntimeException();
            e.Lface.inside = regUp.inside;
        } else {
            if (Geom.EdgeSign(eLo.Sym.Org, eUp.Sym.Org, eLo.Org) > 0) return false;

            /* eUp.Sym.Org is below eLo, so splice eUp.Sym.Org into eLo */
            regUp.dirty = regLo.dirty = true;
            e = Mesh.__gl_meshSplitEdge(eLo);
            if (e == null) throw new RuntimeException();
            if (!Mesh.__gl_meshSplice(eUp.Lnext, eLo.Sym)) throw new RuntimeException();
            e.Sym.Lface.inside = regUp.inside;
        }
        return true;
    }


    static boolean CheckForIntersect(GLUtessellatorImpl tess, ActiveRegion regUp)
/*
 * Check the upper and lower edges of the given region to see if
 * they intersect.  If so, create the intersection and add it
 * to the data structures.
 *
 * Returns true if adding the new intersection resulted in a recursive
 * call to AddRightEdges(); in this case all "dirty" regions have been
 * checked for intersections, and possibly regUp has been deleted.
 */ {
        ActiveRegion regLo = RegionBelow(regUp);
        GLUhalfEdge eUp = regUp.eUp;
        GLUhalfEdge eLo = regLo.eUp;
        GLUvertex orgUp = eUp.Org;
        GLUvertex orgLo = eLo.Org;
        GLUvertex dstUp = eUp.Sym.Org;
        GLUvertex dstLo = eLo.Sym.Org;
        double tMinUp, tMaxLo;
        GLUvertex isect = new GLUvertex();
        GLUvertex orgMin;
        GLUhalfEdge e;

        assert (!Geom.VertEq(dstLo, dstUp));
        assert (Geom.EdgeSign(dstUp, tess.event, orgUp) <= 0);
        assert (Geom.EdgeSign(dstLo, tess.event, orgLo) >= 0);
        assert (orgUp != tess.event && orgLo != tess.event);
        assert (!regUp.fixUpperEdge && !regLo.fixUpperEdge);

        if (orgUp == orgLo) return false;	/* right endpoints are the same */

        tMinUp = Math.min(orgUp.t, dstUp.t);
        tMaxLo = Math.max(orgLo.t, dstLo.t);
        if (tMinUp > tMaxLo) return false;	/* t ranges do not overlap */

        if (Geom.VertLeq(orgUp, orgLo)) {
            if (Geom.EdgeSign(dstLo, orgUp, orgLo) > 0) return false;
        } else {
            if (Geom.EdgeSign(dstUp, orgLo, orgUp) < 0) return false;
        }

        /* At this point the edges intersect, at least marginally */
        DebugEvent(tess);

        Geom.EdgeIntersect(dstUp, orgUp, dstLo, orgLo, isect);
        /* The following properties are guaranteed: */
        assert (Math.min(orgUp.t, dstUp.t) <= isect.t);
        assert (isect.t <= Math.max(orgLo.t, dstLo.t));
        assert (Math.min(dstLo.s, dstUp.s) <= isect.s);
        assert (isect.s <= Math.max(orgLo.s, orgUp.s));

        if (Geom.VertLeq(isect, tess.event)) {
            /* The intersection point lies slightly to the left of the sweep line,
             * so move it until it''s slightly to the right of the sweep line.
             * (If we had perfect numerical precision, this would never happen
             * in the first place).  The easiest and safest thing to do is
             * replace the intersection by tess.event.
             */
            isect.s = tess.event.s;
            isect.t = tess.event.t;
        }
        /* Similarly, if the computed intersection lies to the right of the
         * rightmost origin (which should rarely happen), it can cause
         * unbelievable inefficiency on sufficiently degenerate inputs.
         * (If you have the test program, try running test54.d with the
         * "X zoom" option turned on).
         */
        orgMin = Geom.VertLeq(orgUp, orgLo) ? orgUp : orgLo;
        if (Geom.VertLeq(orgMin, isect)) {
            isect.s = orgMin.s;
            isect.t = orgMin.t;
        }

        if (Geom.VertEq(isect, orgUp) || Geom.VertEq(isect, orgLo)) {
            /* Easy case -- intersection at one of the right endpoints */
            CheckForRightSplice(tess, regUp);
            return false;
        }

        if ((!Geom.VertEq(dstUp, tess.event)
                && Geom.EdgeSign(dstUp, tess.event, isect) >= 0)
                || (!Geom.VertEq(dstLo, tess.event)
                && Geom.EdgeSign(dstLo, tess.event, isect) <= 0)) {
            /* Very unusual -- the new upper or lower edge would pass on the
             * wrong side of the sweep event, or through it.  This can happen
             * due to very small numerical errors in the intersection calculation.
             */
            if (dstLo == tess.event) {
                /* Splice dstLo into eUp, and process the new region(s) */
                if (Mesh.__gl_meshSplitEdge(eUp.Sym) == null) throw new RuntimeException();
                if (!Mesh.__gl_meshSplice(eLo.Sym, eUp)) throw new RuntimeException();
                regUp = TopLeftRegion(regUp);
                if (regUp == null) throw new RuntimeException();
                eUp = RegionBelow(regUp).eUp;
                FinishLeftRegions(tess, RegionBelow(regUp), regLo);
                AddRightEdges(tess, regUp, eUp.Sym.Lnext, eUp, eUp, true);
                return true;
            }
            if (dstUp == tess.event) {
                /* Splice dstUp into eLo, and process the new region(s) */
                if (Mesh.__gl_meshSplitEdge(eLo.Sym) == null) throw new RuntimeException();
                if (!Mesh.__gl_meshSplice(eUp.Lnext, eLo.Sym.Lnext)) throw new RuntimeException();
                regLo = regUp;
                regUp = TopRightRegion(regUp);
                e = RegionBelow(regUp).eUp.Sym.Onext;
                regLo.eUp = eLo.Sym.Lnext;
                eLo = FinishLeftRegions(tess, regLo, null);
                AddRightEdges(tess, regUp, eLo.Onext, eUp.Sym.Onext, e, true);
                return true;
            }
            /* Special case: called from ConnectRightVertex.  If either
             * edge passes on the wrong side of tess.event, split it
             * (and wait for ConnectRightVertex to splice it appropriately).
             */
            if (Geom.EdgeSign(dstUp, tess.event, isect) >= 0) {
                RegionAbove(regUp).dirty = regUp.dirty = true;
                if (Mesh.__gl_meshSplitEdge(eUp.Sym) == null) throw new RuntimeException();
                eUp.Org.s = tess.event.s;
                eUp.Org.t = tess.event.t;
            }
            if (Geom.EdgeSign(dstLo, tess.event, isect) <= 0) {
                regUp.dirty = regLo.dirty = true;
                if (Mesh.__gl_meshSplitEdge(eLo.Sym) == null) throw new RuntimeException();
                eLo.Org.s = tess.event.s;
                eLo.Org.t = tess.event.t;
            }
            /* leave the rest for ConnectRightVertex */
            return false;
        }

        /* General case -- split both edges, splice into new vertex.
         * When we do the splice operation, the order of the arguments is
         * arbitrary as far as correctness goes.  However, when the operation
         * creates a new face, the work done is proportional to the size of
         * the new face.  We expect the faces in the processed part of
         * the mesh (ie. eUp.Lface) to be smaller than the faces in the
         * unprocessed original contours (which will be eLo.Sym.Lnext.Lface).
         */
        if (Mesh.__gl_meshSplitEdge(eUp.Sym) == null) throw new RuntimeException();
        if (Mesh.__gl_meshSplitEdge(eLo.Sym) == null) throw new RuntimeException();
        if (!Mesh.__gl_meshSplice(eLo.Sym.Lnext, eUp)) throw new RuntimeException();
        eUp.Org.s = isect.s;
        eUp.Org.t = isect.t;
        eUp.Org.pqHandle = tess.pq.pqInsert(eUp.Org); /* __gl_pqSortInsert */
        if (eUp.Org.pqHandle == Long.MAX_VALUE) {
            tess.pq.pqDeletePriorityQ();	/* __gl_pqSortDeletePriorityQ */
            tess.pq = null;
            throw new RuntimeException();
        }
        GetIntersectData(tess, eUp.Org, orgUp, dstUp, orgLo, dstLo);
        RegionAbove(regUp).dirty = regUp.dirty = regLo.dirty = true;
        return false;
    }

    static void WalkDirtyRegions(GLUtessellatorImpl tess, ActiveRegion regUp)
/*
 * When the upper or lower edge of any region changes, the region is
 * marked "dirty".  This routine walks through all the dirty regions
 * and makes sure that the dictionary invariants are satisfied
 * (see the comments at the beginning of this file).  Of course
 * new dirty regions can be created as we make changes to restore
 * the invariants.
 */ {
        ActiveRegion regLo = RegionBelow(regUp);
        GLUhalfEdge eUp, eLo;

        for (; ;) {
            /* Find the lowest dirty region (we walk from the bottom up). */
            while (regLo.dirty) {
                regUp = regLo;
                regLo = RegionBelow(regLo);
            }
            if (!regUp.dirty) {
                regLo = regUp;
                regUp = RegionAbove(regUp);
                if (regUp == null || !regUp.dirty) {
                    /* We've walked all the dirty regions */
                    return;
                }
            }
            regUp.dirty = false;
            eUp = regUp.eUp;
            eLo = regLo.eUp;

            if (eUp.Sym.Org != eLo.Sym.Org) {
                /* Check that the edge ordering is obeyed at the Dst vertices. */
                if (CheckForLeftSplice(tess, regUp)) {

                    /* If the upper or lower edge was marked fixUpperEdge, then
                     * we no longer need it (since these edges are needed only for
                     * vertices which otherwise have no right-going edges).
                     */
                    if (regLo.fixUpperEdge) {
                        DeleteRegion(tess, regLo);
                        if (!Mesh.__gl_meshDelete(eLo)) throw new RuntimeException();
                        regLo = RegionBelow(regUp);
                        eLo = regLo.eUp;
                    } else if (regUp.fixUpperEdge) {
                        DeleteRegion(tess, regUp);
                        if (!Mesh.__gl_meshDelete(eUp)) throw new RuntimeException();
                        regUp = RegionAbove(regLo);
                        eUp = regUp.eUp;
                    }
                }
            }
            if (eUp.Org != eLo.Org) {
                if (eUp.Sym.Org != eLo.Sym.Org
                        && !regUp.fixUpperEdge && !regLo.fixUpperEdge
                        && (eUp.Sym.Org == tess.event || eLo.Sym.Org == tess.event)) {
                    /* When all else fails in CheckForIntersect(), it uses tess.event
                     * as the intersection location.  To make this possible, it requires
                     * that tess.event lie between the upper and lower edges, and also
                     * that neither of these is marked fixUpperEdge (since in the worst
                     * case it might splice one of these edges into tess.event, and
                     * violate the invariant that fixable edges are the only right-going
                     * edge from their associated vertex).
                         */
                    if (CheckForIntersect(tess, regUp)) {
                        /* WalkDirtyRegions() was called recursively; we're done */
                        return;
                    }
                } else {
                    /* Even though we can't use CheckForIntersect(), the Org vertices
                     * may violate the dictionary edge ordering.  Check and correct this.
                     */
                    CheckForRightSplice(tess, regUp);
                }
            }
            if (eUp.Org == eLo.Org && eUp.Sym.Org == eLo.Sym.Org) {
                /* A degenerate loop consisting of only two edges -- delete it. */
                AddWinding(eLo, eUp);
                DeleteRegion(tess, regUp);
                if (!Mesh.__gl_meshDelete(eUp)) throw new RuntimeException();
                regUp = RegionAbove(regLo);
            }
        }
    }


    static void ConnectRightVertex(GLUtessellatorImpl tess, ActiveRegion regUp,
                                   GLUhalfEdge eBottomLeft)
/*
 * Purpose: connect a "right" vertex vEvent (one where all edges go left)
 * to the unprocessed portion of the mesh.  Since there are no right-going
 * edges, two regions (one above vEvent and one below) are being merged
 * into one.  "regUp" is the upper of these two regions.
 *
 * There are two reasons for doing this (adding a right-going edge):
 *  - if the two regions being merged are "inside", we must add an edge
 *    to keep them separated (the combined region would not be monotone).
 *  - in any case, we must leave some record of vEvent in the dictionary,
 *    so that we can merge vEvent with features that we have not seen yet.
 *    For example, maybe there is a vertical edge which passes just to
 *    the right of vEvent; we would like to splice vEvent into this edge.
 *
 * However, we don't want to connect vEvent to just any vertex.  We don''t
 * want the new edge to cross any other edges; otherwise we will create
 * intersection vertices even when the input data had no self-intersections.
 * (This is a bad thing; if the user's input data has no intersections,
 * we don't want to generate any false intersections ourselves.)
 *
 * Our eventual goal is to connect vEvent to the leftmost unprocessed
 * vertex of the combined region (the union of regUp and regLo).
 * But because of unseen vertices with all right-going edges, and also
 * new vertices which may be created by edge intersections, we don''t
 * know where that leftmost unprocessed vertex is.  In the meantime, we
 * connect vEvent to the closest vertex of either chain, and mark the region
 * as "fixUpperEdge".  This flag says to delete and reconnect this edge
 * to the next processed vertex on the boundary of the combined region.
 * Quite possibly the vertex we connected to will turn out to be the
 * closest one, in which case we won''t need to make any changes.
 */ {
        GLUhalfEdge eNew;
        GLUhalfEdge eTopLeft = eBottomLeft.Onext;
        ActiveRegion regLo = RegionBelow(regUp);
        GLUhalfEdge eUp = regUp.eUp;
        GLUhalfEdge eLo = regLo.eUp;
        boolean degenerate = false;

        if (eUp.Sym.Org != eLo.Sym.Org) {
            CheckForIntersect(tess, regUp);
        }

        /* Possible new degeneracies: upper or lower edge of regUp may pass
         * through vEvent, or may coincide with new intersection vertex
         */
        if (Geom.VertEq(eUp.Org, tess.event)) {
            if (!Mesh.__gl_meshSplice(eTopLeft.Sym.Lnext, eUp)) throw new RuntimeException();
            regUp = TopLeftRegion(regUp);
            if (regUp == null) throw new RuntimeException();
            eTopLeft = RegionBelow(regUp).eUp;
            FinishLeftRegions(tess, RegionBelow(regUp), regLo);
            degenerate = true;
        }
        if (Geom.VertEq(eLo.Org, tess.event)) {
            if (!Mesh.__gl_meshSplice(eBottomLeft, eLo.Sym.Lnext)) throw new RuntimeException();
            eBottomLeft = FinishLeftRegions(tess, regLo, null);
            degenerate = true;
        }
        if (degenerate) {
            AddRightEdges(tess, regUp, eBottomLeft.Onext, eTopLeft, eTopLeft, true);
            return;
        }

        /* Non-degenerate situation -- need to add a temporary, fixable edge.
         * Connect to the closer of eLo.Org, eUp.Org.
         */
        if (Geom.VertLeq(eLo.Org, eUp.Org)) {
            eNew = eLo.Sym.Lnext;
        } else {
            eNew = eUp;
        }
        eNew = Mesh.__gl_meshConnect(eBottomLeft.Onext.Sym, eNew);
        if (eNew == null) throw new RuntimeException();

        /* Prevent cleanup, otherwise eNew might disappear before we've even
         * had a chance to mark it as a temporary edge.
         */
        AddRightEdges(tess, regUp, eNew, eNew.Onext, eNew.Onext, false);
        eNew.Sym.activeRegion.fixUpperEdge = true;
        WalkDirtyRegions(tess, regUp);
    }

/* Because vertices at exactly the same location are merged together
 * before we process the sweep event, some degenerate cases can't occur.
 * However if someone eventually makes the modifications required to
 * merge features which are close together, the cases below marked
 * TOLERANCE_NONZERO will be useful.  They were debugged before the
 * code to merge identical vertices in the main loop was added.
 */
    private static final boolean TOLERANCE_NONZERO = false;

    static void ConnectLeftDegenerate(GLUtessellatorImpl tess,
                                      ActiveRegion regUp, GLUvertex vEvent)
/*
 * The event vertex lies exacty on an already-processed edge or vertex.
 * Adding the new vertex involves splicing it into the already-processed
 * part of the mesh.
 */ {
        GLUhalfEdge e, eTopLeft, eTopRight, eLast;
        ActiveRegion reg;

        e = regUp.eUp;
        if (Geom.VertEq(e.Org, vEvent)) {
            /* e.Org is an unprocessed vertex - just combine them, and wait
             * for e.Org to be pulled from the queue
             */
            assert (TOLERANCE_NONZERO);
            SpliceMergeVertices(tess, e, vEvent.anEdge);
            return;
        }

        if (!Geom.VertEq(e.Sym.Org, vEvent)) {
            /* General case -- splice vEvent into edge e which passes through it */
            if (Mesh.__gl_meshSplitEdge(e.Sym) == null) throw new RuntimeException();
            if (regUp.fixUpperEdge) {
                /* This edge was fixable -- delete unused portion of original edge */
                if (!Mesh.__gl_meshDelete(e.Onext)) throw new RuntimeException();
                regUp.fixUpperEdge = false;
            }
            if (!Mesh.__gl_meshSplice(vEvent.anEdge, e)) throw new RuntimeException();
            SweepEvent(tess, vEvent);	/* recurse */
            return;
        }

        /* vEvent coincides with e.Sym.Org, which has already been processed.
         * Splice in the additional right-going edges.
         */
        assert (TOLERANCE_NONZERO);
        regUp = TopRightRegion(regUp);
        reg = RegionBelow(regUp);
        eTopRight = reg.eUp.Sym;
        eTopLeft = eLast = eTopRight.Onext;
        if (reg.fixUpperEdge) {
            /* Here e.Sym.Org has only a single fixable edge going right.
             * We can delete it since now we have some real right-going edges.
             */
            assert (eTopLeft != eTopRight);   /* there are some left edges too */
            DeleteRegion(tess, reg);
            if (!Mesh.__gl_meshDelete(eTopRight)) throw new RuntimeException();
            eTopRight = eTopLeft.Sym.Lnext;
        }
        if (!Mesh.__gl_meshSplice(vEvent.anEdge, eTopRight)) throw new RuntimeException();
        if (!Geom.EdgeGoesLeft(eTopLeft)) {
            /* e.Sym.Org had no left-going edges -- indicate this to AddRightEdges() */
            eTopLeft = null;
        }
        AddRightEdges(tess, regUp, eTopRight.Onext, eLast, eTopLeft, true);
    }


    static void ConnectLeftVertex(GLUtessellatorImpl tess, GLUvertex vEvent)
/*
 * Purpose: connect a "left" vertex (one where both edges go right)
 * to the processed portion of the mesh.  Let R be the active region
 * containing vEvent, and let U and L be the upper and lower edge
 * chains of R.  There are two possibilities:
 *
 * - the normal case: split R into two regions, by connecting vEvent to
 *   the rightmost vertex of U or L lying to the left of the sweep line
 *
 * - the degenerate case: if vEvent is close enough to U or L, we
 *   merge vEvent into that edge chain.  The subcases are:
 *	- merging with the rightmost vertex of U or L
 *	- merging with the active edge of U or L
 *	- merging with an already-processed portion of U or L
 */ {
        ActiveRegion regUp, regLo, reg;
        GLUhalfEdge eUp, eLo, eNew;
        ActiveRegion tmp = new ActiveRegion();

        /* assert ( vEvent.anEdge.Onext.Onext == vEvent.anEdge ); */

        /* Get a pointer to the active region containing vEvent */
        tmp.eUp = vEvent.anEdge.Sym;
        /* __GL_DICTLISTKEY */ /* __gl_dictListSearch */
        regUp = (ActiveRegion) Dict.dictKey(Dict.dictSearch(tess.dict, tmp));
        regLo = RegionBelow(regUp);
        eUp = regUp.eUp;
        eLo = regLo.eUp;

        /* Try merging with U or L first */
        if (Geom.EdgeSign(eUp.Sym.Org, vEvent, eUp.Org) == 0) {
            ConnectLeftDegenerate(tess, regUp, vEvent);
            return;
        }

        /* Connect vEvent to rightmost processed vertex of either chain.
         * e.Sym.Org is the vertex that we will connect to vEvent.
         */
        reg = Geom.VertLeq(eLo.Sym.Org, eUp.Sym.Org) ? regUp : regLo;

        if (regUp.inside || reg.fixUpperEdge) {
            if (reg == regUp) {
                eNew = Mesh.__gl_meshConnect(vEvent.anEdge.Sym, eUp.Lnext);
                if (eNew == null) throw new RuntimeException();
            } else {
                GLUhalfEdge tempHalfEdge = Mesh.__gl_meshConnect(eLo.Sym.Onext.Sym, vEvent.anEdge);
                if (tempHalfEdge == null) throw new RuntimeException();

                eNew = tempHalfEdge.Sym;
            }
            if (reg.fixUpperEdge) {
                if (!FixUpperEdge(reg, eNew)) throw new RuntimeException();
            } else {
                ComputeWinding(tess, AddRegionBelow(tess, regUp, eNew));
            }
            SweepEvent(tess, vEvent);
        } else {
            /* The new vertex is in a region which does not belong to the polygon.
             * We don''t need to connect this vertex to the rest of the mesh.
             */
            AddRightEdges(tess, regUp, vEvent.anEdge, vEvent.anEdge, null, true);
        }
    }


    static void SweepEvent(GLUtessellatorImpl tess, GLUvertex vEvent)
/*
 * Does everything necessary when the sweep line crosses a vertex.
 * Updates the mesh and the edge dictionary.
 */ {
        ActiveRegion regUp, reg;
        GLUhalfEdge e, eTopLeft, eBottomLeft;

        tess.event = vEvent;		/* for access in EdgeLeq() */
        DebugEvent(tess);

        /* Check if this vertex is the right endpoint of an edge that is
         * already in the dictionary.  In this case we don't need to waste
         * time searching for the location to insert new edges.
         */
        e = vEvent.anEdge;
        while (e.activeRegion == null) {
            e = e.Onext;
            if (e == vEvent.anEdge) {
                /* All edges go right -- not incident to any processed edges */
                ConnectLeftVertex(tess, vEvent);
                return;
            }
        }

        /* Processing consists of two phases: first we "finish" all the
         * active regions where both the upper and lower edges terminate
         * at vEvent (ie. vEvent is closing off these regions).
         * We mark these faces "inside" or "outside" the polygon according
         * to their winding number, and delete the edges from the dictionary.
         * This takes care of all the left-going edges from vEvent.
         */
        regUp = TopLeftRegion(e.activeRegion);
        if (regUp == null) throw new RuntimeException();
        reg = RegionBelow(regUp);
        eTopLeft = reg.eUp;
        eBottomLeft = FinishLeftRegions(tess, reg, null);

        /* Next we process all the right-going edges from vEvent.  This
         * involves adding the edges to the dictionary, and creating the
         * associated "active regions" which record information about the
         * regions between adjacent dictionary edges.
         */
        if (eBottomLeft.Onext == eTopLeft) {
            /* No right-going edges -- add a temporary "fixable" edge */
            ConnectRightVertex(tess, regUp, eBottomLeft);
        } else {
            AddRightEdges(tess, regUp, eBottomLeft.Onext, eTopLeft, eTopLeft, true);
        }
    }


/* Make the sentinel coordinates big enough that they will never be
 * merged with real input features.  (Even with the largest possible
 * input contour and the maximum tolerance of 1.0, no merging will be
 * done with coordinates larger than 3 * GLU_TESS_MAX_COORD).
 */
    private static final double SENTINEL_COORD = (4.0 * GLU_TESS_MAX_COORD);

    static void AddSentinel(GLUtessellatorImpl tess, double t)
/*
 * We add two sentinel edges above and below all other edges,
 * to avoid special cases at the top and bottom.
 */ {
        GLUhalfEdge e;
        ActiveRegion reg = new ActiveRegion();
        //if (reg == null) throw new RuntimeException();

        e = Mesh.__gl_meshMakeEdge(tess.mesh);
        if (e == null) throw new RuntimeException();

        e.Org.s = SENTINEL_COORD;
        e.Org.t = t;
        e.Sym.Org.s = -SENTINEL_COORD;
        e.Sym.Org.t = t;
        tess.event = e.Sym.Org;		/* initialize it */

        reg.eUp = e;
        reg.windingNumber = 0;
        reg.inside = false;
        reg.fixUpperEdge = false;
        reg.sentinel = true;
        reg.dirty = false;
        reg.nodeUp = Dict.dictInsert(tess.dict, reg); /* __gl_dictListInsertBefore */
        if (reg.nodeUp == null) throw new RuntimeException();
    }


    static void InitEdgeDict(final GLUtessellatorImpl tess)
/*
 * We maintain an ordering of edge intersections with the sweep line.
 * This order is maintained in a dynamic dictionary.
 */ {
        /* __gl_dictListNewDict */
        tess.dict = Dict.dictNewDict(tess, new Dict.DictLeq() {
            public boolean leq(Object frame, Object key1, Object key2) {
                return EdgeLeq(tess, (ActiveRegion) key1, (ActiveRegion) key2);
            }
        });
        if (tess.dict == null) throw new RuntimeException();

        AddSentinel(tess, -SENTINEL_COORD);
        AddSentinel(tess, SENTINEL_COORD);
    }


    static void DoneEdgeDict(GLUtessellatorImpl tess) {
        ActiveRegion reg;
        int fixedEdges = 0;

        /* __GL_DICTLISTKEY */ /* __GL_DICTLISTMIN */
        while ((reg = (ActiveRegion) Dict.dictKey(Dict.dictMin(tess.dict))) != null) {
            /*
             * At the end of all processing, the dictionary should contain
             * only the two sentinel edges, plus at most one "fixable" edge
             * created by ConnectRightVertex().
             */
            if (!reg.sentinel) {
                assert (reg.fixUpperEdge);
                assert (++fixedEdges == 1);
            }
            assert (reg.windingNumber == 0);
            DeleteRegion(tess, reg);
/*    __gl_meshDelete( reg.eUp );*/
        }
        Dict.dictDeleteDict(tess.dict);	/* __gl_dictListDeleteDict */
    }


    static void RemoveDegenerateEdges(GLUtessellatorImpl tess)
/*
 * Remove zero-length edges, and contours with fewer than 3 vertices.
 */ {
        GLUhalfEdge e, eNext, eLnext;
        GLUhalfEdge eHead = tess.mesh.eHead;

        /*LINTED*/
        for (e = eHead.next; e != eHead; e = eNext) {
            eNext = e.next;
            eLnext = e.Lnext;

            if (Geom.VertEq(e.Org, e.Sym.Org) && e.Lnext.Lnext != e) {
                /* Zero-length edge, contour has at least 3 edges */

                SpliceMergeVertices(tess, eLnext, e);	/* deletes e.Org */
                if (!Mesh.__gl_meshDelete(e)) throw new RuntimeException(); /* e is a self-loop */
                e = eLnext;
                eLnext = e.Lnext;
            }
            if (eLnext.Lnext == e) {
                /* Degenerate contour (one or two edges) */

                if (eLnext != e) {
                    if (eLnext == eNext || eLnext == eNext.Sym) {
                        eNext = eNext.next;
                    }
                    if (!Mesh.__gl_meshDelete(eLnext)) throw new RuntimeException();
                }
                if (e == eNext || e == eNext.Sym) {
                    eNext = eNext.next;
                }
                if (!Mesh.__gl_meshDelete(e)) throw new RuntimeException();
            }
        }
    }

    static boolean InitPriorityQ(GLUtessellatorImpl tess)
/*
 * Insert all vertices into the priority queue which determines the
 * order in which vertices cross the sweep line.
 */ {
        PriorityQ pq;
        GLUvertex v, vHead;

        /* __gl_pqSortNewPriorityQ */
        pq = tess.pq = PriorityQ.pqNewPriorityQ(new PriorityQ.Leq() {
            public boolean leq(Object key1, Object key2) {
                return Geom.VertLeq(((GLUvertex) key1), (GLUvertex) key2);
            }
        });
        if (pq == null) return false;

        vHead = tess.mesh.vHead;
        for (v = vHead.next; v != vHead; v = v.next) {
            v.pqHandle = pq.pqInsert(v); /* __gl_pqSortInsert */
            if (v.pqHandle == Long.MAX_VALUE) break;
        }
        if (v != vHead || !pq.pqInit()) { /* __gl_pqSortInit */
            tess.pq.pqDeletePriorityQ();	/* __gl_pqSortDeletePriorityQ */
            tess.pq = null;
            return false;
        }

        return true;
    }


    static void DonePriorityQ(GLUtessellatorImpl tess) {
        tess.pq.pqDeletePriorityQ(); /* __gl_pqSortDeletePriorityQ */
    }


    static boolean RemoveDegenerateFaces(GLUmesh mesh)
/*
 * Delete any degenerate faces with only two edges.  WalkDirtyRegions()
 * will catch almost all of these, but it won't catch degenerate faces
 * produced by splice operations on already-processed edges.
 * The two places this can happen are in FinishLeftRegions(), when
 * we splice in a "temporary" edge produced by ConnectRightVertex(),
 * and in CheckForLeftSplice(), where we splice already-processed
 * edges to ensure that our dictionary invariants are not violated
 * by numerical errors.
 *
 * In both these cases it is *very* dangerous to delete the offending
 * edge at the time, since one of the routines further up the stack
 * will sometimes be keeping a pointer to that edge.
 */ {
        GLUface f, fNext;
        GLUhalfEdge e;

        /*LINTED*/
        for (f = mesh.fHead.next; f != mesh.fHead; f = fNext) {
            fNext = f.next;
            e = f.anEdge;
            assert (e.Lnext != e);

            if (e.Lnext.Lnext == e) {
                /* A face with only two edges */
                AddWinding(e.Onext, e);
                if (!Mesh.__gl_meshDelete(e)) return false;
            }
        }
        return true;
    }

    public static boolean __gl_computeInterior(GLUtessellatorImpl tess)
/*
 * __gl_computeInterior( tess ) computes the planar arrangement specified
 * by the given contours, and further subdivides this arrangement
 * into regions.  Each region is marked "inside" if it belongs
 * to the polygon, according to the rule given by tess.windingRule.
 * Each interior region is guaranteed be monotone.
 */ {
        GLUvertex v, vNext;

        tess.fatalError = false;

        /* Each vertex defines an event for our sweep line.  Start by inserting
         * all the vertices in a priority queue.  Events are processed in
         * lexicographic order, ie.
         *
         *	e1 < e2  iff  e1.x < e2.x || (e1.x == e2.x && e1.y < e2.y)
         */
        RemoveDegenerateEdges(tess);
        if (!InitPriorityQ(tess)) return false; /* if error */
        InitEdgeDict(tess);

        /* __gl_pqSortExtractMin */
        while ((v = (GLUvertex) tess.pq.pqExtractMin()) != null) {
            for (; ;) {
                vNext = (GLUvertex) tess.pq.pqMinimum(); /* __gl_pqSortMinimum */
                if (vNext == null || !Geom.VertEq(vNext, v)) break;

                /* Merge together all vertices at exactly the same location.
                 * This is more efficient than processing them one at a time,
                 * simplifies the code (see ConnectLeftDegenerate), and is also
                 * important for correct handling of certain degenerate cases.
                 * For example, suppose there are two identical edges A and B
                 * that belong to different contours (so without this code they would
                 * be processed by separate sweep events).  Suppose another edge C
                 * crosses A and B from above.  When A is processed, we split it
                 * at its intersection point with C.  However this also splits C,
                 * so when we insert B we may compute a slightly different
                 * intersection point.  This might leave two edges with a small
                 * gap between them.  This kind of error is especially obvious
                 * when using boundary extraction (GLU_TESS_BOUNDARY_ONLY).
                 */
                vNext = (GLUvertex) tess.pq.pqExtractMin(); /* __gl_pqSortExtractMin*/
                SpliceMergeVertices(tess, v.anEdge, vNext.anEdge);
            }
            SweepEvent(tess, v);
        }

        /* Set tess.event for debugging purposes */
        /* __GL_DICTLISTKEY */ /* __GL_DICTLISTMIN */
        tess.event = ((ActiveRegion) Dict.dictKey(Dict.dictMin(tess.dict))).eUp.Org;
        DebugEvent(tess);
        DoneEdgeDict(tess);
        DonePriorityQ(tess);

        if (!RemoveDegenerateFaces(tess.mesh)) return false;
        Mesh.__gl_meshCheckMesh(tess.mesh);

        return true;
    }
}
