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
package org.lwjgl.util.glu;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.util.glu.tessellation.GLUtessellatorImpl;
import org.lwjgl.opengl.Util;

import static org.lwjgl.opengl.GL11.*;

/**
 * GLU.java
 *
 *
 * Created 23-dec-2003
 * @author Erik Duijs
 */
public class GLU {
	static final float PI = (float)Math.PI;

	/* Errors: (return value 0 = no error) */
	public static final int GLU_INVALID_ENUM        = 100900;
	public static final int GLU_INVALID_VALUE       = 100901;
	public static final int GLU_OUT_OF_MEMORY       = 100902;
	public static final int GLU_INCOMPATIBLE_GL_VERSION     = 100903;

	/* StringName */
	public static final int GLU_VERSION             = 100800;
	public static final int GLU_EXTENSIONS          = 100801;

	/* Boolean */
	public static final boolean GLU_TRUE                = true;
	public static final boolean GLU_FALSE               = false;


	/****           Quadric constants               ****/

	/* QuadricNormal */
	public static final int GLU_SMOOTH              = 100000;
	public static final int GLU_FLAT                = 100001;
	public static final int GLU_NONE                = 100002;

	/* QuadricDrawStyle */
	public static final int GLU_POINT               = 100010;
	public static final int GLU_LINE                = 100011;
	public static final int GLU_FILL                = 100012;
	public static final int GLU_SILHOUETTE          = 100013;

	/* QuadricOrientation */
	public static final int GLU_OUTSIDE             = 100020;
	public static final int GLU_INSIDE              = 100021;

	/* Callback types: */
	/*      ERROR               = 100103 */


	/****           Tesselation constants           ****/

	public static final double GLU_TESS_MAX_COORD              = 1.0e150;
	public static final double TESS_MAX_COORD              = 1.0e150;

	/* TessProperty */
	public static final int GLU_TESS_WINDING_RULE           = 100140;
	public static final int GLU_TESS_BOUNDARY_ONLY          = 100141;
	public static final int GLU_TESS_TOLERANCE              = 100142;

	/* TessWinding */
	public static final int GLU_TESS_WINDING_ODD            = 100130;
	public static final int GLU_TESS_WINDING_NONZERO        = 100131;
	public static final int GLU_TESS_WINDING_POSITIVE       = 100132;
	public static final int GLU_TESS_WINDING_NEGATIVE       = 100133;
	public static final int GLU_TESS_WINDING_ABS_GEQ_TWO    = 100134;

	/* TessCallback */
	public static final int GLU_TESS_BEGIN          = 100100;  /* void (CALLBACK*)(GLenum    type)  */
	public static final int GLU_TESS_VERTEX         = 100101;  /* void (CALLBACK*)(void      *data) */
	public static final int GLU_TESS_END            = 100102;  /* void (CALLBACK*)(void)            */
	public static final int GLU_TESS_ERROR          = 100103;  /* void (CALLBACK*)(GLenum    errno) */
	public static final int GLU_TESS_EDGE_FLAG      = 100104;  /* void (CALLBACK*)(GLboolean boundaryEdge)  */
	public static final int GLU_TESS_COMBINE        = 100105;  /* void (CALLBACK*)(GLdouble  coords[3],
	                                                            void      *data[4],
	                                                            GLfloat   weight[4],
	                                                            void      **dataOut)     */
	public static final int GLU_TESS_BEGIN_DATA     = 100106;  /* void (CALLBACK*)(GLenum    type,
	                                                            void      *polygon_data) */
	public static final int GLU_TESS_VERTEX_DATA    = 100107;  /* void (CALLBACK*)(void      *data,
	                                                            void      *polygon_data) */
	public static final int GLU_TESS_END_DATA       = 100108;  /* void (CALLBACK*)(void      *polygon_data) */
	public static final int GLU_TESS_ERROR_DATA     = 100109;  /* void (CALLBACK*)(GLenum    errno,
	                                                            void      *polygon_data) */
	public static final int GLU_TESS_EDGE_FLAG_DATA = 100110;  /* void (CALLBACK*)(GLboolean boundaryEdge,
	                                                            void      *polygon_data) */
	public static final int GLU_TESS_COMBINE_DATA   = 100111;  /* void (CALLBACK*)(GLdouble  coords[3],
	                                                            void      *data[4],
	                                                            GLfloat   weight[4],
	                                                            void      **dataOut,
	                                                            void      *polygon_data) */

	/* TessError */
	public static final int GLU_TESS_ERROR1     = 100151;
	public static final int GLU_TESS_ERROR2     = 100152;
	public static final int GLU_TESS_ERROR3     = 100153;
	public static final int GLU_TESS_ERROR4     = 100154;
	public static final int GLU_TESS_ERROR5     = 100155;
	public static final int GLU_TESS_ERROR6     = 100156;
	public static final int GLU_TESS_ERROR7     = 100157;
	public static final int GLU_TESS_ERROR8     = 100158;

	public static final int GLU_TESS_MISSING_BEGIN_POLYGON  = GLU_TESS_ERROR1;
	public static final int GLU_TESS_MISSING_BEGIN_CONTOUR  = GLU_TESS_ERROR2;
	public static final int GLU_TESS_MISSING_END_POLYGON    = GLU_TESS_ERROR3;
	public static final int GLU_TESS_MISSING_END_CONTOUR    = GLU_TESS_ERROR4;
	public static final int GLU_TESS_COORD_TOO_LARGE        = GLU_TESS_ERROR5;
	public static final int GLU_TESS_NEED_COMBINE_CALLBACK  = GLU_TESS_ERROR6;

	/****           NURBS constants                 ****/

	/* NurbsProperty */
	public static final int GLU_AUTO_LOAD_MATRIX    = 100200;
	public static final int GLU_CULLING             = 100201;
	public static final int GLU_SAMPLING_TOLERANCE  = 100203;
	public static final int GLU_DISPLAY_MODE        = 100204;
	public static final int GLU_PARAMETRIC_TOLERANCE        = 100202;
	public static final int GLU_SAMPLING_METHOD             = 100205;
	public static final int GLU_U_STEP                      = 100206;
	public static final int GLU_V_STEP                      = 100207;

	/* NurbsSampling */
	public static final int GLU_PATH_LENGTH                 = 100215;
	public static final int GLU_PARAMETRIC_ERROR            = 100216;
	public static final int GLU_DOMAIN_DISTANCE             = 100217;


	/* NurbsTrim */
	public static final int GLU_MAP1_TRIM_2         = 100210;
	public static final int GLU_MAP1_TRIM_3         = 100211;

	/* NurbsDisplay */
	/*      FILL                = 100012 */
	public static final int GLU_OUTLINE_POLYGON     = 100240;
	public static final int GLU_OUTLINE_PATCH       = 100241;

	/* NurbsCallback */
	/*      ERROR               = 100103 */

	/* NurbsErrors */
	public static final int GLU_NURBS_ERROR1        = 100251;
	public static final int GLU_NURBS_ERROR2        = 100252;
	public static final int GLU_NURBS_ERROR3        = 100253;
	public static final int GLU_NURBS_ERROR4        = 100254;
	public static final int GLU_NURBS_ERROR5        = 100255;
	public static final int GLU_NURBS_ERROR6        = 100256;
	public static final int GLU_NURBS_ERROR7        = 100257;
	public static final int GLU_NURBS_ERROR8        = 100258;
	public static final int GLU_NURBS_ERROR9        = 100259;
	public static final int GLU_NURBS_ERROR10       = 100260;
	public static final int GLU_NURBS_ERROR11       = 100261;
	public static final int GLU_NURBS_ERROR12       = 100262;
	public static final int GLU_NURBS_ERROR13       = 100263;
	public static final int GLU_NURBS_ERROR14       = 100264;
	public static final int GLU_NURBS_ERROR15       = 100265;
	public static final int GLU_NURBS_ERROR16       = 100266;
	public static final int GLU_NURBS_ERROR17       = 100267;
	public static final int GLU_NURBS_ERROR18       = 100268;
	public static final int GLU_NURBS_ERROR19       = 100269;
	public static final int GLU_NURBS_ERROR20       = 100270;
	public static final int GLU_NURBS_ERROR21       = 100271;
	public static final int GLU_NURBS_ERROR22       = 100272;
	public static final int GLU_NURBS_ERROR23       = 100273;
	public static final int GLU_NURBS_ERROR24       = 100274;
	public static final int GLU_NURBS_ERROR25       = 100275;
	public static final int GLU_NURBS_ERROR26       = 100276;
	public static final int GLU_NURBS_ERROR27       = 100277;
	public static final int GLU_NURBS_ERROR28       = 100278;
	public static final int GLU_NURBS_ERROR29       = 100279;
	public static final int GLU_NURBS_ERROR30       = 100280;
	public static final int GLU_NURBS_ERROR31       = 100281;
	public static final int GLU_NURBS_ERROR32       = 100282;
	public static final int GLU_NURBS_ERROR33       = 100283;
	public static final int GLU_NURBS_ERROR34       = 100284;
	public static final int GLU_NURBS_ERROR35       = 100285;
	public static final int GLU_NURBS_ERROR36       = 100286;
	public static final int GLU_NURBS_ERROR37       = 100287;

	/* Contours types -- obsolete! */
	public static final int GLU_CW          = 100120;
	public static final int GLU_CCW         = 100121;
	public static final int GLU_INTERIOR    = 100122;
	public static final int GLU_EXTERIOR    = 100123;
	public static final int GLU_UNKNOWN     = 100124;

	/* Names without "TESS_" prefix */
	public static final int GLU_BEGIN       = GLU_TESS_BEGIN;
	public static final int GLU_VERTEX      = GLU_TESS_VERTEX;
	public static final int GLU_END         = GLU_TESS_END;
	public static final int GLU_ERROR       = GLU_TESS_ERROR;
	public static final int GLU_EDGE_FLAG   = GLU_TESS_EDGE_FLAG;

	/**
	 * Method gluLookAt
	 * @param eyex
	 * @param eyey
	 * @param eyez
	 * @param centerx
	 * @param centery
	 * @param centerz
	 * @param upx
	 * @param upy
	 * @param upz
	 */
	public static void gluLookAt(
			float eyex,
			float eyey,
			float eyez,
			float centerx,
			float centery,
			float centerz,
			float upx,
			float upy,
			float upz) {

		Project.gluLookAt(eyex, eyey, eyez, centerx, centery, centerz, upx, upy, upz);
	}

	/**
	 * Method gluOrtho2D
	 * @param left
	 * @param right
	 * @param bottom
	 * @param top
	 */
	public static void gluOrtho2D(
			float left,
			float right,
			float bottom,
			float top) {

		glOrtho(left, right, bottom, top, -1.0, 1.0);
	}

	/**
	 * Method gluPerspective
	 * @param fovy
	 * @param aspect
	 * @param zNear
	 * @param zFar
	 */
	public static void gluPerspective(
			float fovy,
			float aspect,
			float zNear,
			float zFar) {

		Project.gluPerspective(fovy, aspect, zNear, zFar);
	}

	/**
	 * Method gluProject
	 * @param objx
	 * @param objy
	 * @param objz
	 * @param modelMatrix
	 * @param projMatrix
	 * @param viewport
	 * @param win_pos
	 */
	public static boolean gluProject(float objx, float objy, float objz,
			FloatBuffer modelMatrix,
			FloatBuffer projMatrix,
			IntBuffer viewport,
			FloatBuffer win_pos)
	{
		return Project.gluProject(objx, objy, objz, modelMatrix, projMatrix, viewport, win_pos);
	}

	/**
	 * Method gluUnproject
	 * @param winx
	 * @param winy
	 * @param winz
	 * @param modelMatrix
	 * @param projMatrix
	 * @param viewport
	 * @param obj_pos
	 */
	public static boolean gluUnProject(float winx, float winy, float winz,
			FloatBuffer modelMatrix,
			FloatBuffer projMatrix,
			IntBuffer viewport,
			FloatBuffer obj_pos)
	{
		return Project.gluUnProject(winx, winy, winz, modelMatrix, projMatrix, viewport, obj_pos);
	}

	/**
	 * Method gluPickMatrix
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param viewport
	 */
	public static void gluPickMatrix(
			float x,
			float y,
			float width,
			float height,
			IntBuffer viewport) {

		Project.gluPickMatrix(x, y, width, height, viewport);
	}

	/**
	 * Method gluGetString.
	 * @param name
	 * @return String
	 */
	public static String gluGetString(int name) {
		return Registry.gluGetString(name);
	}

	/**
	 * Method gluCheckExtension.
	 * @param extName
	 * @param extString
	 * @return boolean
	 */
	public static boolean gluCheckExtension(String extName, String extString) {
		return Registry.gluCheckExtension(extName, extString);
	}

	/**
	 * Method gluBuild2DMipmaps
	 * @param target
	 * @param components
	 * @param width
	 * @param height
	 * @param format
	 * @param type
	 * @param data
	 * @return int
	 */
	public static int gluBuild2DMipmaps(
			int target,
			int components,
			int width,
			int height,
			int format,
			int type,
			ByteBuffer data) {

		return MipMap.gluBuild2DMipmaps(target, components, width, height, format, type, data);
	}

	/**
	 * Method gluScaleImage.
	 * @param format
	 * @param widthIn
	 * @param heightIn
	 * @param typeIn
	 * @param dataIn
	 * @param widthOut
	 * @param heightOut
	 * @param typeOut
	 * @param dataOut
	 * @return int
	 */
	public static int gluScaleImage(
			int format,
			int widthIn,
			int heightIn,
			int typeIn,
			ByteBuffer dataIn,
			int widthOut,
			int heightOut,
			int typeOut,
			ByteBuffer dataOut) {

		return MipMap.gluScaleImage(format, widthIn, heightIn, typeIn, dataIn, widthOut, heightOut, typeOut, dataOut);
	}

	public static String gluErrorString(int error_code) {
		switch (error_code) {
			case GLU_INVALID_ENUM:
				return "Invalid enum (glu)";
			case GLU_INVALID_VALUE:
				return "Invalid value (glu)";
			case GLU_OUT_OF_MEMORY:
				return "Out of memory (glu)";
			default:
				return Util.translateGLErrorString(error_code);
		}
	}

	public static GLUtessellator gluNewTess() {
		return new GLUtessellatorImpl();
	}
}
