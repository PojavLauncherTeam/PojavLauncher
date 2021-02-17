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
package org.lwjgl.opengl;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;

/** This class represents the state necessary for render-to-texture. */
public final class RenderTexture {

	// ----------------------------------------------------------------------------------
	// ----------------------------- WGL_ARB_render_texture -----------------------------
	// ----------------------------------------------------------------------------------

	/*
	Accepted by the <piAttributes> parameter of wglGetPixelFormatAttribivARB,
	wglGetPixelFormatAttribfvARB, and the <piAttribIList> and <pfAttribIList>
	parameters of wglChoosePixelFormatARB:
	*/
	private static final int WGL_BIND_TO_TEXTURE_RGB_ARB = 0x2070;
	private static final int WGL_BIND_TO_TEXTURE_RGBA_ARB = 0x2071;

	/*
	Accepted by the <piAttribList> parameter of wglCreatePbufferARB and
	by the <iAttribute> parameter of wglQueryPbufferARB:
	*/
	private static final int WGL_TEXTURE_FORMAT_ARB = 0x2072;
	private static final int WGL_TEXTURE_TARGET_ARB = 0x2073;
	private static final int WGL_MIPMAP_TEXTURE_ARB = 0x2074;

	/*
	Accepted as a value in the <piAttribList> parameter of
	wglCreatePbufferARB and returned in the value parameter of
	wglQueryPbufferARB when <iAttribute> is WGL_TEXTURE_FORMAT_ARB:
	*/
	private static final int WGL_TEXTURE_RGB_ARB = 0x2075;
	private static final int WGL_TEXTURE_RGBA_ARB = 0x2076;

	/*
	Accepted as a value in the <piAttribList> parameter of
	wglCreatePbufferARB and returned in the value parameter of
	wglQueryPbufferARB when <iAttribute> is WGL_TEXTURE_TARGET_ARB:
	*/
	private static final int WGL_TEXTURE_CUBE_MAP_ARB = 0x2078;
	private static final int WGL_TEXTURE_1D_ARB = 0x2079;
	private static final int WGL_TEXTURE_2D_ARB = 0x207A;
	private static final int WGL_NO_TEXTURE_ARB = 0x2077;

	/*
	Accepted by the <piAttribList> parameter of wglSetPbufferAttribARB and
	by the <iAttribute> parameter of wglQueryPbufferARB:
	*/
	static final int WGL_MIPMAP_LEVEL_ARB = 0x207B;
	static final int WGL_CUBE_MAP_FACE_ARB = 0x207C;

	/*
	Accepted as a value in the <piAttribList> parameter of
	wglSetPbufferAttribARB and returned in the value parameter of
	wglQueryPbufferARB when <iAttribute> is WGL_CUBE_MAP_FACE_ARB:
	*/
	static final int WGL_TEXTURE_CUBE_MAP_POSITIVE_X_ARB = 0x207D;
	static final int WGL_TEXTURE_CUBE_MAP_NEGATIVE_X_ARB = 0x207E;
	static final int WGL_TEXTURE_CUBE_MAP_POSITIVE_Y_ARB = 0x207F;
	static final int WGL_TEXTURE_CUBE_MAP_NEGATIVE_Y_ARB = 0x2080;
	static final int WGL_TEXTURE_CUBE_MAP_POSITIVE_Z_ARB = 0x2081;
	static final int WGL_TEXTURE_CUBE_MAP_NEGATIVE_Z_ARB = 0x2082;

	/*
	Accepted by the <iBuffer> parameter of wglBindTexImageARB and
	wglReleaseTexImageARB:
	*/
	static final int WGL_FRONT_LEFT_ARB = 0x2083;
	static final int WGL_FRONT_RIGHT_ARB = 0x2084;
	static final int WGL_BACK_LEFT_ARB = 0x2085;
	static final int WGL_BACK_RIGHT_ARB = 0x2086;

	/*
	private static final int WGL_AUX0_ARB = 0x2087;
	private static final int WGL_AUX1_ARB = 0x2088;
	private static final int WGL_AUX2_ARB = 0x2089;
	private static final int WGL_AUX3_ARB = 0x208A;
	private static final int WGL_AUX4_ARB = 0x208B;
	private static final int WGL_AUX5_ARB = 0x208C;
	private static final int WGL_AUX6_ARB = 0x208D;
	private static final int WGL_AUX7_ARB = 0x208E;
	private static final int WGL_AUX8_ARB = 0x208F;
	private static final int WGL_AUX9_ARB = 0x2090;
	*/

	// -------------------------------------------------------------------------------------------
	// ----------------------------- WGL_NV_render_texture_rectangle -----------------------------
	// -------------------------------------------------------------------------------------------

	/*
	Accepted by the <piAttributes> parameter of wglGetPixelFormatAttribivARB,
	wglGetPixelFormatAttribfvARB, and the <piAttribIList> and <pfAttribIList>
	parameters of wglChoosePixelFormatARB:
	*/
	private static final int WGL_BIND_TO_TEXTURE_RECTANGLE_RGB_NV = 0x20A0;
	private static final int WGL_BIND_TO_TEXTURE_RECTANGLE_RGBA_NV = 0x20A1;

	/*
	Accepted as a value in the <piAttribList> parameter of wglCreatePbufferARB
	and returned in the value parameter of wglQueryPbufferARB when
	<iAttribute> is WGL_TEXTURE_TARGET_ARB:
	*/
	private static final int WGL_TEXTURE_RECTANGLE_NV = 0x20A2;

	// ---------------------------------------------------------------------------------------
	// ----------------------------- WGL_NV_render_depth_texture -----------------------------
	// ---------------------------------------------------------------------------------------

	/*
	Accepted by the <piAttributes> parameter of wglGetPixelFormatAttribivARB,
	wglGetPixelFormatAttribfvARB, and the <piAttribIList> and <pfAttribIList>
	parameters of wglChoosePixelFormatARB:
	*/
	private static final int WGL_BIND_TO_TEXTURE_DEPTH_NV = 0x20A3;
	private static final int WGL_BIND_TO_TEXTURE_RECTANGLE_DEPTH_NV = 0x20A4;

	/*
	Accepted by the <piAttribList> parameter of wglCreatePbufferARB and
	by the <iAttribute> parameter of wglQueryPbufferARB:
	*/
	private static final int WGL_DEPTH_TEXTURE_FORMAT_NV = 0x20A5;

	/*
	Accepted as a value in the <piAttribList> parameter of wglCreatePbufferARB
	and returned in the value parameter of wglQueryPbufferARB when
	<iAttribute> is WGL_DEPTH_TEXTURE_FORMAT_NV:
	*/
	private static final int WGL_TEXTURE_DEPTH_COMPONENT_NV = 0x20A6;

	/*
	Accepted by the <iBuffer> parameter of wglBindTexImageARB:
	*/
	static final int WGL_DEPTH_COMPONENT_NV = 0x20A7;

	/** The TEXTURE_1D target. */
	public static final int RENDER_TEXTURE_1D = WGL_TEXTURE_1D_ARB;

	/** The TEXTURE_2D target. */
	public static final int RENDER_TEXTURE_2D = WGL_TEXTURE_2D_ARB;

	/** The TEXTURE_RECTANGLE target. */
	public static final int RENDER_TEXTURE_RECTANGLE = WGL_TEXTURE_RECTANGLE_NV;

	/** The TEXTURE_CUBE_MAP target. */
	public static final int RENDER_TEXTURE_CUBE_MAP = WGL_TEXTURE_CUBE_MAP_ARB;

	IntBuffer pixelFormatCaps;
	IntBuffer pBufferAttribs;

	/**
	 * Creates a RenderTexture object for enabling render-to-texture on a P-buffer.
	 * <p/>
	 * NOTE: Only one of useRGB and useRGBA can be true at the same time.
	 * <p/>
	 * NOTE: useRGB(A) and useDepth can be true at the same time, thus allowing two different render textures.
	 * <p/>
	 * NOTE: The target parameter can be one of the following:
	 * <p/>
	 * RENDER_TEXTURE_1D RENDER_TEXTURE_2D RENDER_TEXTURE_RECTANGLE RENDER_TEXTURE_CUBE_MAP
	 *
	 * @param useRGB      - When true the P-buffer can be used as an RGB render texture.
	 * @param useRGBA     - When true the P-buffer can be used as an RGBA render texture.
	 * @param useDepth    - When true the P-buffer can be used as a depth render texture.
	 * @param isRectangle - When true rectangle textures will be allowed on the P-buffer.
	 * @param target      - The texture target of the render texture.
	 * @param mipmaps     - How many mipmap levels to allocate on the P-buffer.
	 */
	public RenderTexture(boolean useRGB, boolean useRGBA, boolean useDepth, boolean isRectangle, int target, int mipmaps) {
		if ( useRGB && useRGBA )
			throw new IllegalArgumentException("A RenderTexture can't be both RGB and RGBA.");

		if ( mipmaps < 0 )
			throw new IllegalArgumentException("The mipmap levels can't be negative.");

		if ( isRectangle && target != RENDER_TEXTURE_RECTANGLE )
			throw new IllegalArgumentException("When the RenderTexture is rectangle the target must be RENDER_TEXTURE_RECTANGLE.");

		pixelFormatCaps = BufferUtils.createIntBuffer(4);
		pBufferAttribs = BufferUtils.createIntBuffer(8);

		if ( useRGB ) {
			pixelFormatCaps.put(isRectangle ? WGL_BIND_TO_TEXTURE_RECTANGLE_RGB_NV : WGL_BIND_TO_TEXTURE_RGB_ARB);
			pixelFormatCaps.put(GL_TRUE);

			pBufferAttribs.put(WGL_TEXTURE_FORMAT_ARB);
			pBufferAttribs.put(WGL_TEXTURE_RGB_ARB);
		} else if ( useRGBA ) {
			pixelFormatCaps.put(isRectangle ? WGL_BIND_TO_TEXTURE_RECTANGLE_RGBA_NV : WGL_BIND_TO_TEXTURE_RGBA_ARB);
			pixelFormatCaps.put(GL_TRUE);

			pBufferAttribs.put(WGL_TEXTURE_FORMAT_ARB);
			pBufferAttribs.put(WGL_TEXTURE_RGBA_ARB);
		}

		if ( useDepth ) {
			pixelFormatCaps.put(isRectangle ? WGL_BIND_TO_TEXTURE_RECTANGLE_DEPTH_NV : WGL_BIND_TO_TEXTURE_DEPTH_NV);
			pixelFormatCaps.put(GL_TRUE);

			pBufferAttribs.put(WGL_DEPTH_TEXTURE_FORMAT_NV);
			pBufferAttribs.put(WGL_TEXTURE_DEPTH_COMPONENT_NV);
		}

		pBufferAttribs.put(WGL_TEXTURE_TARGET_ARB);
		pBufferAttribs.put(target);

		if ( mipmaps != 0 ) {
			pBufferAttribs.put(WGL_MIPMAP_TEXTURE_ARB);
			pBufferAttribs.put(mipmaps);
		}

		pixelFormatCaps.flip();
		pBufferAttribs.flip();
	}

}
