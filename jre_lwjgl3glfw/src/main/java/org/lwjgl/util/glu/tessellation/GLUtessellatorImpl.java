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

import org.lwjgl.util.glu.GLUtessellator;
import org.lwjgl.util.glu.GLUtessellatorCallback;
import org.lwjgl.util.glu.GLUtessellatorCallbackAdapter;

import static org.lwjgl.util.glu.GLU.*;

public class GLUtessellatorImpl implements GLUtessellator {
    public static final int TESS_MAX_CACHE = 100;

    private int state;		/* what begin/end calls have we seen? */

    private GLUhalfEdge lastEdge;	/* lastEdge->Org is the most recent vertex */
    GLUmesh mesh;		/* stores the input contours, and eventually
                                   the tessellation itself */

    /*** state needed for projecting onto the sweep plane ***/

    double[] normal = new double[3];	/* user-specified normal (if provided) */
    double[] sUnit = new double[3];	/* unit vector in s-direction (debugging) */
    double[] tUnit = new double[3];	/* unit vector in t-direction (debugging) */

    /*** state needed for the line sweep ***/

    private double relTolerance;	/* tolerance for merging features */
    int windingRule;	/* rule for determining polygon interior */
    boolean fatalError;	/* fatal error: needed combine callback */

    Dict dict;		/* edge dictionary for sweep line */
    PriorityQ pq;		/* priority queue of vertex events */
    GLUvertex event;		/* current sweep event being processed */

    /*** state needed for rendering callbacks (see render.c) ***/

    boolean flagBoundary;	/* mark boundary edges (use EdgeFlag) */
    boolean boundaryOnly;	/* Extract contours, not triangles */
    GLUface lonelyTriList;
    /* list of triangles which could not be rendered as strips or fans */



    /*** state needed to cache single-contour polygons for renderCache() */

    private boolean flushCacheOnNextVertex;		/* empty cache on next vertex() call */
    int cacheCount;		/* number of cached vertices */
    CachedVertex[] cache = new CachedVertex[TESS_MAX_CACHE];	/* the vertex data */

    /*** rendering callbacks that also pass polygon data  ***/
    private Object polygonData;		/* client data for current polygon */

    private GLUtessellatorCallback callBegin;
    private GLUtessellatorCallback callEdgeFlag;
    private GLUtessellatorCallback callVertex;
    private GLUtessellatorCallback callEnd;
//    private GLUtessellatorCallback callMesh;
    private GLUtessellatorCallback callError;
    private GLUtessellatorCallback callCombine;

    private GLUtessellatorCallback callBeginData;
    private GLUtessellatorCallback callEdgeFlagData;
    private GLUtessellatorCallback callVertexData;
    private GLUtessellatorCallback callEndData;
//    private GLUtessellatorCallback callMeshData;
    private GLUtessellatorCallback callErrorData;
    private GLUtessellatorCallback callCombineData;

    private static final double GLU_TESS_DEFAULT_TOLERANCE = 0.0;
//    private static final int GLU_TESS_MESH = 100112;	/* void (*)(GLUmesh *mesh)	    */
    private static GLUtessellatorCallback NULL_CB = new GLUtessellatorCallbackAdapter();

//    #define MAX_FAST_ALLOC	(MAX(sizeof(EdgePair), \
//                 MAX(sizeof(GLUvertex),sizeof(GLUface))))

    public GLUtessellatorImpl() {
        state = TessState.T_DORMANT;

        normal[0] = 0;
        normal[1] = 0;
        normal[2] = 0;

        relTolerance = GLU_TESS_DEFAULT_TOLERANCE;
        windingRule = GLU_TESS_WINDING_ODD;
        flagBoundary = false;
        boundaryOnly = false;

        callBegin = NULL_CB;
        callEdgeFlag = NULL_CB;
        callVertex = NULL_CB;
        callEnd = NULL_CB;
        callError = NULL_CB;
        callCombine = NULL_CB;
//        callMesh = NULL_CB;

        callBeginData = NULL_CB;
        callEdgeFlagData = NULL_CB;
        callVertexData = NULL_CB;
        callEndData = NULL_CB;
        callErrorData = NULL_CB;
        callCombineData = NULL_CB;

        polygonData = null;

        for (int i = 0; i < cache.length; i++) {
            cache[i] = new CachedVertex();
        }
    }

    public static GLUtessellator gluNewTess()
    {
        return new GLUtessellatorImpl();
    }


    private void makeDormant() {
        /* Return the tessellator to its original dormant state. */

        if (mesh != null) {
            Mesh.__gl_meshDeleteMesh(mesh);
        }
        state = TessState.T_DORMANT;
        lastEdge = null;
        mesh = null;
    }

    private void requireState(int newState) {
        if (state != newState) gotoState(newState);
    }

    private void gotoState(int newState) {
        while (state != newState) {
            /* We change the current state one level at a time, to get to
             * the desired state.
             */
            if (state < newState) {
                if (state == TessState.T_DORMANT) {
                    callErrorOrErrorData(GLU_TESS_MISSING_BEGIN_POLYGON);
                    gluTessBeginPolygon(null);
                } else if (state == TessState.T_IN_POLYGON) {
                    callErrorOrErrorData(GLU_TESS_MISSING_BEGIN_CONTOUR);
                    gluTessBeginContour();
                }
            } else {
                if (state == TessState.T_IN_CONTOUR) {
                    callErrorOrErrorData(GLU_TESS_MISSING_END_CONTOUR);
                    gluTessEndContour();
                } else if (state == TessState.T_IN_POLYGON) {
                    callErrorOrErrorData(GLU_TESS_MISSING_END_POLYGON);
                    /* gluTessEndPolygon( tess ) is too much work! */
                    makeDormant();
                }
            }
        }
    }

    public void gluDeleteTess() {
        requireState(TessState.T_DORMANT);
    }

    public void gluTessProperty(int which, double value) {
        switch (which) {
            case GLU_TESS_TOLERANCE:
                if (value < 0.0 || value > 1.0) break;
                relTolerance = value;
                return;

            case GLU_TESS_WINDING_RULE:
                int windingRule = (int) value;
                if (windingRule != value) break;	/* not an integer */

                switch (windingRule) {
                    case GLU_TESS_WINDING_ODD:
                    case GLU_TESS_WINDING_NONZERO:
                    case GLU_TESS_WINDING_POSITIVE:
                    case GLU_TESS_WINDING_NEGATIVE:
                    case GLU_TESS_WINDING_ABS_GEQ_TWO:
                        this.windingRule = windingRule;
                        return;
                    default:
                        break;
                }

            case GLU_TESS_BOUNDARY_ONLY:
                boundaryOnly = (value != 0);
                return;

            default:
                callErrorOrErrorData(GLU_INVALID_ENUM);
                return;
        }
        callErrorOrErrorData(GLU_INVALID_VALUE);
    }

/* Returns tessellator property */
    public void gluGetTessProperty(int which, double[] value, int value_offset) {
        switch (which) {
            case GLU_TESS_TOLERANCE:
/* tolerance should be in range [0..1] */
                assert (0.0 <= relTolerance && relTolerance <= 1.0);
                value[value_offset] = relTolerance;
                break;
            case GLU_TESS_WINDING_RULE:
                assert (windingRule == GLU_TESS_WINDING_ODD ||
                        windingRule == GLU_TESS_WINDING_NONZERO ||
                        windingRule == GLU_TESS_WINDING_POSITIVE ||
                        windingRule == GLU_TESS_WINDING_NEGATIVE ||
                        windingRule == GLU_TESS_WINDING_ABS_GEQ_TWO);
                value[value_offset] = windingRule;
                break;
            case GLU_TESS_BOUNDARY_ONLY:
                assert (boundaryOnly == true || boundaryOnly == false);
                value[value_offset] = boundaryOnly ? 1 : 0;
                break;
            default:
                value[value_offset] = 0.0;
                callErrorOrErrorData(GLU_INVALID_ENUM);
                break;
        }
    } /* gluGetTessProperty() */

    public void gluTessNormal(double x, double y, double z) {
        normal[0] = x;
        normal[1] = y;
        normal[2] = z;
    }

    public void gluTessCallback(int which, GLUtessellatorCallback aCallback) {
        switch (which) {
            case GLU_TESS_BEGIN:
                callBegin = aCallback == null ? NULL_CB : aCallback;
                return;
            case GLU_TESS_BEGIN_DATA:
                callBeginData = aCallback == null ? NULL_CB : aCallback;
                return;
            case GLU_TESS_EDGE_FLAG:
                callEdgeFlag = aCallback == null ? NULL_CB : aCallback;
/* If the client wants boundary edges to be flagged,
 * we render everything as separate triangles (no strips or fans).
 */
                flagBoundary = aCallback != null;
                return;
            case GLU_TESS_EDGE_FLAG_DATA:
                callEdgeFlagData = callBegin = aCallback == null ? NULL_CB : aCallback;
/* If the client wants boundary edges to be flagged,
 * we render everything as separate triangles (no strips or fans).
 */
                flagBoundary = (aCallback != null);
                return;
            case GLU_TESS_VERTEX:
                callVertex = aCallback == null ? NULL_CB : aCallback;
                return;
            case GLU_TESS_VERTEX_DATA:
                callVertexData = aCallback == null ? NULL_CB : aCallback;
                return;
            case GLU_TESS_END:
                callEnd = aCallback == null ? NULL_CB : aCallback;
                return;
            case GLU_TESS_END_DATA:
                callEndData = aCallback == null ? NULL_CB : aCallback;
                return;
            case GLU_TESS_ERROR:
                callError = aCallback == null ? NULL_CB : aCallback;
                return;
            case GLU_TESS_ERROR_DATA:
                callErrorData = aCallback == null ? NULL_CB : aCallback;
                return;
            case GLU_TESS_COMBINE:
                callCombine = aCallback == null ? NULL_CB : aCallback;
                return;
            case GLU_TESS_COMBINE_DATA:
                callCombineData = aCallback == null ? NULL_CB : aCallback;
                return;
//            case GLU_TESS_MESH:
//                callMesh = aCallback == null ? NULL_CB : aCallback;
//                return;
            default:
                callErrorOrErrorData(GLU_INVALID_ENUM);
                return;
        }
    }

    private boolean addVertex(double[] coords, Object vertexData) {
        GLUhalfEdge e;

        e = lastEdge;
        if (e == null) {
/* Make a self-loop (one vertex, one edge). */

            e = Mesh.__gl_meshMakeEdge(mesh);
            if (e == null) return false;
            if (!Mesh.__gl_meshSplice(e, e.Sym)) return false;
        } else {
/* Create a new vertex and edge which immediately follow e
 * in the ordering around the left face.
 */
            if (Mesh.__gl_meshSplitEdge(e) == null) return false;
            e = e.Lnext;
        }

/* The new vertex is now e.Org. */
        e.Org.data = vertexData;
        e.Org.coords[0] = coords[0];
        e.Org.coords[1] = coords[1];
        e.Org.coords[2] = coords[2];

/* The winding of an edge says how the winding number changes as we
 * cross from the edge''s right face to its left face.  We add the
 * vertices in such an order that a CCW contour will add +1 to
 * the winding number of the region inside the contour.
 */
        e.winding = 1;
        e.Sym.winding = -1;

        lastEdge = e;

        return true;
    }

    private void cacheVertex(double[] coords, Object vertexData) {
        if (cache[cacheCount] == null) {
            cache[cacheCount] = new CachedVertex();
        }

        CachedVertex v = cache[cacheCount];

        v.data = vertexData;
        v.coords[0] = coords[0];
        v.coords[1] = coords[1];
        v.coords[2] = coords[2];
        ++cacheCount;
    }


    private boolean flushCache() {
        CachedVertex[] v = cache;

        mesh = Mesh.__gl_meshNewMesh();
        if (mesh == null) return false;

        for (int i = 0; i < cacheCount; i++) {
            CachedVertex vertex = v[i];
            if (!addVertex(vertex.coords, vertex.data)) return false;
        }
        cacheCount = 0;
        flushCacheOnNextVertex = false;

        return true;
    }

    public void gluTessVertex(double[] coords, int coords_offset, Object vertexData) {
        int i;
        boolean tooLarge = false;
        double x;
        double[] clamped = new double[3];

        requireState(TessState.T_IN_CONTOUR);

        if (flushCacheOnNextVertex) {
            if (!flushCache()) {
                callErrorOrErrorData(GLU_OUT_OF_MEMORY);
                return;
            }
            lastEdge = null;
        }
        for (i = 0; i < 3; ++i) {
            x = coords[i+coords_offset];
            if (x < -GLU_TESS_MAX_COORD) {
                x = -GLU_TESS_MAX_COORD;
                tooLarge = true;
            }
            if (x > GLU_TESS_MAX_COORD) {
                x = GLU_TESS_MAX_COORD;
                tooLarge = true;
            }
            clamped[i] = x;
        }
        if (tooLarge) {
            callErrorOrErrorData(GLU_TESS_COORD_TOO_LARGE);
        }

        if (mesh == null) {
            if (cacheCount < TESS_MAX_CACHE) {
                cacheVertex(clamped, vertexData);
                return;
            }
            if (!flushCache()) {
                callErrorOrErrorData(GLU_OUT_OF_MEMORY);
                return;
            }
        }

        if (!addVertex(clamped, vertexData)) {
            callErrorOrErrorData(GLU_OUT_OF_MEMORY);
        }
    }


    public void gluTessBeginPolygon(Object data) {
        requireState(TessState.T_DORMANT);

        state = TessState.T_IN_POLYGON;
        cacheCount = 0;
        flushCacheOnNextVertex = false;
        mesh = null;

        polygonData = data;
    }


    public void gluTessBeginContour() {
        requireState(TessState.T_IN_POLYGON);

        state = TessState.T_IN_CONTOUR;
        lastEdge = null;
        if (cacheCount > 0) {
/* Just set a flag so we don't get confused by empty contours
 * -- these can be generated accidentally with the obsolete
 * NextContour() interface.
 */
            flushCacheOnNextVertex = true;
        }
    }


    public void gluTessEndContour() {
        requireState(TessState.T_IN_CONTOUR);
        state = TessState.T_IN_POLYGON;
    }

    public void gluTessEndPolygon() {
        GLUmesh mesh;

        try {
            requireState(TessState.T_IN_POLYGON);
            state = TessState.T_DORMANT;

            if (this.mesh == null) {
                if (!flagBoundary /*&& callMesh == NULL_CB*/) {

/* Try some special code to make the easy cases go quickly
 * (eg. convex polygons).  This code does NOT handle multiple contours,
 * intersections, edge flags, and of course it does not generate
 * an explicit mesh either.
 */
                    if (Render.__gl_renderCache(this)) {
                        polygonData = null;
                        return;
                    }
                }
                if (!flushCache()) throw new RuntimeException(); /* could've used a label*/
            }

/* Determine the polygon normal and project vertices onto the plane
         * of the polygon.
         */
            Normal.__gl_projectPolygon(this);

/* __gl_computeInterior( tess ) computes the planar arrangement specified
 * by the given contours, and further subdivides this arrangement
 * into regions.  Each region is marked "inside" if it belongs
 * to the polygon, according to the rule given by windingRule.
 * Each interior region is guaranteed be monotone.
 */
            if (!Sweep.__gl_computeInterior(this)) {
                throw new RuntimeException();	/* could've used a label */
            }

            mesh = this.mesh;
            if (!fatalError) {
                boolean rc = true;

/* If the user wants only the boundary contours, we throw away all edges
 * except those which separate the interior from the exterior.
 * Otherwise we tessellate all the regions marked "inside".
 */
                if (boundaryOnly) {
                    rc = TessMono.__gl_meshSetWindingNumber(mesh, 1, true);
                } else {
                    rc = TessMono.__gl_meshTessellateInterior(mesh);
                }
                if (!rc) throw new RuntimeException();	/* could've used a label */

                Mesh.__gl_meshCheckMesh(mesh);

                if (callBegin != NULL_CB || callEnd != NULL_CB
                        || callVertex != NULL_CB || callEdgeFlag != NULL_CB
                        || callBeginData != NULL_CB
                        || callEndData != NULL_CB
                        || callVertexData != NULL_CB
                        || callEdgeFlagData != NULL_CB) {
                    if (boundaryOnly) {
                        Render.__gl_renderBoundary(this, mesh);  /* output boundary contours */
                    } else {
                        Render.__gl_renderMesh(this, mesh);	   /* output strips and fans */
                    }
                }
//                if (callMesh != NULL_CB) {
//
///* Throw away the exterior faces, so that all faces are interior.
//                 * This way the user doesn't have to check the "inside" flag,
//                 * and we don't need to even reveal its existence.  It also leaves
//                 * the freedom for an implementation to not generate the exterior
//                 * faces in the first place.
//                 */
//                    TessMono.__gl_meshDiscardExterior(mesh);
//                    callMesh.mesh(mesh);		/* user wants the mesh itself */
//                    mesh = null;
//                    polygonData = null;
//                    return;
//                }
            }
            Mesh.__gl_meshDeleteMesh(mesh);
            polygonData = null;
            mesh = null;
        } catch (Exception e) {
            e.printStackTrace();
            callErrorOrErrorData(GLU_OUT_OF_MEMORY);
        }
    }

    /*******************************************************/

/* Obsolete calls -- for backward compatibility */

    public void gluBeginPolygon() {
        gluTessBeginPolygon(null);
        gluTessBeginContour();
    }


/*ARGSUSED*/
    public void gluNextContour(int type) {
        gluTessEndContour();
        gluTessBeginContour();
    }


    public void gluEndPolygon() {
        gluTessEndContour();
        gluTessEndPolygon();
    }

    void callBeginOrBeginData(int a) {
        if (callBeginData != NULL_CB)
            callBeginData.beginData(a, polygonData);
        else
            callBegin.begin(a);
    }

    void callVertexOrVertexData(Object a) {
        if (callVertexData != NULL_CB)
            callVertexData.vertexData(a, polygonData);
        else
            callVertex.vertex(a);
    }

    void callEdgeFlagOrEdgeFlagData(boolean a) {
        if (callEdgeFlagData != NULL_CB)
            callEdgeFlagData.edgeFlagData(a, polygonData);
        else
            callEdgeFlag.edgeFlag(a);
    }

    void callEndOrEndData() {
        if (callEndData != NULL_CB)
            callEndData.endData(polygonData);
        else
            callEnd.end();
    }

    void callCombineOrCombineData(double[] coords, Object[] vertexData, float[] weights, Object[] outData) {
        if (callCombineData != NULL_CB)
            callCombineData.combineData(coords, vertexData, weights, outData, polygonData);
        else
            callCombine.combine(coords, vertexData, weights, outData);
    }

    void callErrorOrErrorData(int a) {
        if (callErrorData != NULL_CB)
            callErrorData.errorData(a, polygonData);
        else
            callError.error(a);
    }

}
