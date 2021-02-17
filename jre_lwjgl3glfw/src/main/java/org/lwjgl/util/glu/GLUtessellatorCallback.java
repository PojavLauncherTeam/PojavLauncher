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
package org.lwjgl.util.glu;

/**
 * <b>GLUtessellatorCallback</b> interface provides methods that the user will
 * override to define the callbacks for a tessellation object.
 *
 * @author Eric Veach, July 1994
 * @author Java Port: Pepijn Van Eeckhoudt, July 2003
 * @author Java Port: Nathan Parker Burg, August 2003
 */
public interface GLUtessellatorCallback {
  /**
   * The <b>begin</b> callback method is invoked like
   * {@link javax.media.opengl.GL#glBegin glBegin} to indicate the start of a
   * (triangle) primitive. The method takes a single argument of type int. If
   * the <b>GLU_TESS_BOUNDARY_ONLY</b> property is set to <b>GL_FALSE</b>, then
   * the argument is set to either <b>GL_TRIANGLE_FAN</b>,
   * <b>GL_TRIANGLE_STRIP</b>, or <b>GL_TRIANGLES</b>. If the
   * <b>GLU_TESS_BOUNDARY_ONLY</b> property is set to <b>GL_TRUE</b>, then the
   * argument will be set to <b>GL_LINE_LOOP</b>.
   *
   * @param type
   *        Specifics the type of begin/end pair being defined.  The following
   *        values are valid:  <b>GL_TRIANGLE_FAN</b>, <b>GL_TRIANGLE_STRIP</b>,
   *        <b>GL_TRIANGLES</b> or <b>GL_LINE_LOOP</b>.
   *
   * @see GLU#gluTessCallback           gluTessCallback
   * @see #end     end
   * @see #begin   begin
   */
  void begin(int type);

  /**
   * The same as the {@link #begin begin} callback method except that
   * it takes an additional reference argument. This reference is
   * identical to the opaque reference provided when {@link
   * GLU#gluTessBeginPolygon gluTessBeginPolygon} was called.
   *
   * @param type
   *        Specifics the type of begin/end pair being defined.  The following
   *        values are valid:  <b>GL_TRIANGLE_FAN</b>, <b>GL_TRIANGLE_STRIP</b>,
   *        <b>GL_TRIANGLES</b> or <b>GL_LINE_LOOP</b>.
   * @param polygonData
   *        Specifics a reference to user-defined data.
   *
   * @see GLU#gluTessCallback           gluTessCallback
   * @see #endData endData
   * @see #begin   begin
   */
  void beginData(int type, Object polygonData);


  /**
   * The <b>edgeFlag</b> callback method is similar to
   * {@link javax.media.opengl.GL#glEdgeFlag glEdgeFlag}. The method takes
   * a single boolean boundaryEdge that indicates which edges lie on the
   * polygon boundary. If the boundaryEdge is <b>GL_TRUE</b>, then each vertex
   * that follows begins an edge that lies on the polygon boundary, that is,
   * an edge that separates an interior region from an exterior one. If the
   * boundaryEdge is <b>GL_FALSE</b>, then each vertex that follows begins an
   * edge that lies in the polygon interior. The edge flag callback (if
   * defined) is invoked before the first vertex callback.<P>
   *
   * Since triangle fans and triangle strips do not support edge flags, the
   * begin callback is not called with <b>GL_TRIANGLE_FAN</b> or
   * <b>GL_TRIANGLE_STRIP</b> if a non-null edge flag callback is provided.
   * (If the callback is initialized to null, there is no impact on
   * performance). Instead, the fans and strips are converted to independent
   * triangles.
   *
   * @param boundaryEdge
   *        Specifics which edges lie on the polygon boundary.
   *
   * @see GLU#gluTessCallback gluTessCallback
   * @see #edgeFlagData edgeFlagData
   */
  void edgeFlag(boolean boundaryEdge);


  /**
   * The same as the {@link #edgeFlag edgeFlage} callback method
   * except that it takes an additional reference argument. This
   * reference is identical to the opaque reference provided when
   * {@link GLU#gluTessBeginPolygon gluTessBeginPolygon} was called.
   *
   * @param boundaryEdge
   *        Specifics which edges lie on the polygon boundary.
   * @param polygonData
   *        Specifics a reference to user-defined data.
   *
   * @see GLU#gluTessCallback            gluTessCallback
   * @see #edgeFlag edgeFlag
   */
  void edgeFlagData(boolean boundaryEdge, Object polygonData);


  /**
   * The <b>vertex</b> callback method is invoked between the {@link
   * #begin begin} and {@link #end end} callback methods.  It is
   * similar to {@link javax.media.opengl.GL#glVertex3f glVertex3f},
   * and it defines the vertices of the triangles created by the
   * tessellation process.  The method takes a reference as its only
   * argument. This reference is identical to the opaque reference
   * provided by the user when the vertex was described (see {@link
   * GLU#gluTessVertex gluTessVertex}).
   *
   * @param vertexData
   *        Specifics a reference to the vertices of the triangles created
   *        byt the tessellatin process.
   *
   * @see GLU#gluTessCallback              gluTessCallback
   * @see #vertexData vertexData
   */
  void vertex(Object vertexData);


  /**
   * The same as the {@link #vertex vertex} callback method except
   * that it takes an additional reference argument. This reference is
   * identical to the opaque reference provided when {@link
   * GLU#gluTessBeginPolygon gluTessBeginPolygon} was called.
   *
   * @param vertexData
   *        Specifics a reference to the vertices of the triangles created
   *        byt the tessellatin process.
   * @param polygonData
   *        Specifics a reference to user-defined data.
   *
   * @see GLU#gluTessCallback          gluTessCallback
   * @see #vertex vertex
   */
  void vertexData(Object vertexData, Object polygonData);


  /**
   * The end callback serves the same purpose as
   * {@link javax.media.opengl.GL#glEnd glEnd}. It indicates the end of a
   * primitive and it takes no arguments.
   *
   * @see GLU#gluTessCallback           gluTessCallback
   * @see #begin   begin
   * @see #endData endData
   */
  void end();


  /**
   * The same as the {@link #end end} callback method except that it
   * takes an additional reference argument. This reference is
   * identical to the opaque reference provided when {@link
   * GLU#gluTessBeginPolygon gluTessBeginPolygon} was called.
   *
   * @param polygonData
   *        Specifics a reference to user-defined data.
   *
   * @see GLU#gluTessCallback             gluTessCallback
   * @see #beginData beginData
   * @see #end       end
   */
  void endData(Object polygonData);


  /**
   * The <b>combine</b> callback method is called to create a new vertex when
   * the tessellation detects an intersection, or wishes to merge features. The
   * method takes four arguments: an array of three elements each of type
   * double, an array of four references, an array of four elements each of
   * type float, and a reference to a reference.<P>
   *
   * The vertex is defined as a linear combination of up to four existing
   * vertices, stored in <i>data</i>. The coefficients of the linear combination
   * are given by <i>weight</i>; these weights always add up to 1. All vertex
   * pointers are valid even when some of the weights are 0. <i>coords</i> gives
   * the location of the new vertex.<P>
   *
   * The user must allocate another vertex, interpolate parameters using
   * <i>data</i> and <i>weight</i>, and return the new vertex pointer in
   * <i>outData</i>. This handle is supplied during rendering callbacks. The
   * user is responsible for freeing the memory some time after
   * {@link GLU#gluTessEndPolygon gluTessEndPolygon} is
   * called.<P>
   *
   * For example, if the polygon lies in an arbitrary plane in 3-space, and a
   * color is associated with each vertex, the <b>GLU_TESS_COMBINE</b>
   * callback might look like this:
   * </UL>
   * <PRE>
   *         void myCombine(double[] coords, Object[] data,
   *                        float[] weight, Object[] outData)
   *         {
   *            MyVertex newVertex = new MyVertex();
   *
   *            newVertex.x = coords[0];
   *            newVertex.y = coords[1];
   *            newVertex.z = coords[2];
   *            newVertex.r = weight[0]*data[0].r +
   *                          weight[1]*data[1].r +
   *                          weight[2]*data[2].r +
   *                          weight[3]*data[3].r;
   *            newVertex.g = weight[0]*data[0].g +
   *                          weight[1]*data[1].g +
   *                          weight[2]*data[2].g +
   *                          weight[3]*data[3].g;
   *            newVertex.b = weight[0]*data[0].b +
   *                          weight[1]*data[1].b +
   *                          weight[2]*data[2].b +
   *                          weight[3]*data[3].b;
   *            newVertex.a = weight[0]*data[0].a +
   *                          weight[1]*data[1].a +
   *                          weight[2]*data[2].a +
   *                          weight[3]*data[3].a;
   *            outData = newVertex;
   *         }</PRE>
   *
   * @param coords
   *        Specifics the location of the new vertex.
   * @param data
   *        Specifics the vertices used to create the new vertex.
   * @param weight
   *        Specifics the weights used to create the new vertex.
   * @param outData
   *        Reference user the put the coodinates of the new vertex.
   *
   * @see GLU#gluTessCallback               gluTessCallback
   * @see #combineData combineData
   */
  void combine(double[] coords, Object[] data,
               float[] weight, Object[] outData);


  /**
   * The same as the {@link #combine combine} callback method except
   * that it takes an additional reference argument. This reference is
   * identical to the opaque reference provided when {@link
   * GLU#gluTessBeginPolygon gluTessBeginPolygon} was called.
   *
   * @param coords
   *        Specifics the location of the new vertex.
   * @param data
   *        Specifics the vertices used to create the new vertex.
   * @param weight
   *        Specifics the weights used to create the new vertex.
   * @param outData
   *        Reference user the put the coodinates of the new vertex.
   * @param polygonData
   *        Specifics a reference to user-defined data.
   *
   * @see GLU#gluTessCallback           gluTessCallback
   * @see #combine combine
   */
  void combineData(double[] coords, Object[] data,
                   float[] weight, Object[] outData,
                   Object polygonData);


  /**
   * The <b>error</b> callback method is called when an error is encountered.
   * The one argument is of type int; it indicates the specific error that
   * occurred and will be set to one of <b>GLU_TESS_MISSING_BEGIN_POLYGON</b>,
   * <b>GLU_TESS_MISSING_END_POLYGON</b>, <b>GLU_TESS_MISSING_BEGIN_CONTOUR</b>,
   * <b>GLU_TESS_MISSING_END_CONTOUR</b>, <b>GLU_TESS_COORD_TOO_LARGE</b>,
   * <b>GLU_TESS_NEED_COMBINE_CALLBACK</b> or <b>GLU_OUT_OF_MEMORY</b>.
   * Character strings describing these errors can be retrieved with the
   * {@link GLU#gluErrorString gluErrorString} call.<P>
   *
   * The GLU library will recover from the first four errors by inserting the
   * missing call(s). <b>GLU_TESS_COORD_TOO_LARGE</b> indicates that some
   * vertex coordinate exceeded the predefined constant
   * <b>GLU_TESS_MAX_COORD</b> in absolute value, and that the value has been
   * clamped. (Coordinate values must be small enough so that two can be
   * multiplied together without overflow.)
   * <b>GLU_TESS_NEED_COMBINE_CALLBACK</b> indicates that the tessellation
   * detected an intersection between two edges in the input data, and the
   * <b>GLU_TESS_COMBINE</b> or <b>GLU_TESS_COMBINE_DATA</b> callback was not
   * provided. No output is generated. <b>GLU_OUT_OF_MEMORY</b> indicates that
   * there is not enough memory so no output is generated.
   *
   * @param errnum
   *        Specifics the error number code.
   *
   * @see GLU#gluTessCallback             gluTessCallback
   * @see #errorData errorData
   */
  void error(int errnum);


  /**
   * The same as the {@link #error error} callback method except that
   * it takes an additional reference argument. This reference is
   * identical to the opaque reference provided when {@link
   * GLU#gluTessBeginPolygon gluTessBeginPolygon} was called.
   *
   * @param errnum
   *        Specifics the error number code.
   * @param polygonData
   *        Specifics a reference to user-defined data.
   *
   * @see GLU#gluTessCallback         gluTessCallback
   * @see #error error
   */
  void errorData(int errnum, Object polygonData);

  //void mesh(com.sun.opengl.impl.tessellator.GLUmesh mesh);
}
