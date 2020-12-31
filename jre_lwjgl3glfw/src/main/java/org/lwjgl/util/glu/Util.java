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

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

/**
 * Util.java
 * <p/>
 * <p/>
 * Created 7-jan-2004
 *
 * @author Erik Duijs
 */
public class Util {
	
	/**
	 * Return ceiling of integer division
	 *
	 * @param a
	 * @param b
	 *
	 * @return int
	 */
	protected static int ceil(int a, int b) {
		return (a % b == 0 ? a / b : a / b + 1);
	}

	/**
	 * Normalize vector
	 *
	 * @param v
	 *
	 * @return float[]
	 */
	protected static float[] normalize(float[] v) {
		float r;

		r = (float)Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
		if ( r == 0.0 )
			return v;

		r = 1.0f / r;

		v[0] *= r;
		v[1] *= r;
		v[2] *= r;

		return v;
	}

	/**
	 * Calculate cross-product
	 *
	 * @param v1
	 * @param v2
	 * @param result
	 */
	protected static void cross(float[] v1, float[] v2, float[] result) {
		result[0] = v1[1] * v2[2] - v1[2] * v2[1];
		result[1] = v1[2] * v2[0] - v1[0] * v2[2];
		result[2] = v1[0] * v2[1] - v1[1] * v2[0];
	}

	/**
	 * Method compPerPix.
	 *
	 * @param format
	 *
	 * @return int
	 */
	protected static int compPerPix(int format) {
		/* Determine number of components per pixel */
		switch ( format ) {
			case GL_COLOR_INDEX:
			case GL_STENCIL_INDEX:
			case GL_DEPTH_COMPONENT:
			case GL_RED:
			case GL_GREEN:
			case GL_BLUE:
			case GL_ALPHA:
			case GL_LUMINANCE:
				return 1;
			case GL_LUMINANCE_ALPHA:
				return 2;
			case GL_RGB:
			case GL_BGR:
				return 3;
			case GL_RGBA:
			case GL_BGRA:
				return 4;
			default :
				return -1;
		}
	}

	/**
	 * Method nearestPower.
	 * <p/>
	 * Compute the nearest power of 2 number.  This algorithm is a little strange, but it works quite well.
	 *
	 * @param value
	 *
	 * @return int
	 */
	protected static int nearestPower(int value) {
		int i;

		i = 1;

		/* Error! */
		if ( value == 0 )
			return -1;

		for ( ; ; ) {
			if ( value == 1 ) {
				return i;
			} else if ( value == 3 ) {
				return i << 2;
			}
			value >>= 1;
			i <<= 1;
		}
	}

	/**
	 * Method bytesPerPixel.
	 *
	 * @param format
	 * @param type
	 *
	 * @return int
	 */
	protected static int bytesPerPixel(int format, int type) {
		int n, m;

		switch ( format ) {
			case GL_COLOR_INDEX:
			case GL_STENCIL_INDEX:
			case GL_DEPTH_COMPONENT:
			case GL_RED:
			case GL_GREEN:
			case GL_BLUE:
			case GL_ALPHA:
			case GL_LUMINANCE:
				n = 1;
				break;
			case GL_LUMINANCE_ALPHA:
				n = 2;
				break;
			case GL_RGB:
			case GL_BGR:
				n = 3;
				break;
			case GL_RGBA:
			case GL_BGRA:
				n = 4;
				break;
			default :
				n = 0;
		}

		switch ( type ) {
			case GL_UNSIGNED_BYTE:
				m = 1;
				break;
			case GL_BYTE:
				m = 1;
				break;
			case GL_BITMAP:
				m = 1;
				break;
			case GL_UNSIGNED_SHORT:
				m = 2;
				break;
			case GL_SHORT:
				m = 2;
				break;
			case GL_UNSIGNED_INT:
				m = 4;
				break;
			case GL_INT:
				m = 4;
				break;
			case GL_FLOAT:
				m = 4;
				break;
			default :
				m = 0;
		}

		return n * m;
	}

}
