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
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;

/**
 * <p/>
 * Pbuffer encapsulates an OpenGL pbuffer.
 * <p/>
 *
 * This class is thread-safe.
 *
 * @author elias_naur <elias_naur@users.sourceforge.net>
 * @version $Revision$
 * $Id$
 */
public final class Pbuffer extends DrawableGL {
	/**
	 * Indicates that Pbuffers can be created.
	 */
	public static final int PBUFFER_SUPPORTED = 0;// mark as not supported 1 << 0;

	/**
	 * Indicates that Pbuffers can be used as render-textures.
	 */
	public static final int RENDER_TEXTURE_SUPPORTED = 1 << 1;

	/**
	 * Indicates that Pbuffers can be used as non-power-of-two render-textures.
	 */
	public static final int RENDER_TEXTURE_RECTANGLE_SUPPORTED = 1 << 2;

	/**
	 * Indicates that Pbuffers can be used as depth render-textures.
	 */
	public static final int RENDER_DEPTH_TEXTURE_SUPPORTED = 1 << 3;

	/**
	 * The render-to-texture mipmap level attribute.
	 */
	public static final int MIPMAP_LEVEL = RenderTexture.WGL_MIPMAP_LEVEL_ARB;

	/**
	 * The render-to-texture cube map face attribute.
	 */
	public static final int CUBE_MAP_FACE = RenderTexture.WGL_CUBE_MAP_FACE_ARB;

	/**
	 * The render-to-texture cube map positive X face value.
	 */
	public static final int TEXTURE_CUBE_MAP_POSITIVE_X = RenderTexture.WGL_TEXTURE_CUBE_MAP_POSITIVE_X_ARB;

	/**
	 * The render-to-texture cube map negative X face value.
	 */
	public static final int TEXTURE_CUBE_MAP_NEGATIVE_X = RenderTexture.WGL_TEXTURE_CUBE_MAP_NEGATIVE_X_ARB;

	/**
	 * The render-to-texture cube map positive Y face value.
	 */
	public static final int TEXTURE_CUBE_MAP_POSITIVE_Y = RenderTexture.WGL_TEXTURE_CUBE_MAP_POSITIVE_Y_ARB;

	/**
	 * The render-to-texture cube map negative Y face value.
	 */
	public static final int TEXTURE_CUBE_MAP_NEGATIVE_Y = RenderTexture.WGL_TEXTURE_CUBE_MAP_NEGATIVE_Y_ARB;

	/**
	 * The render-to-texture cube map positive Z face value.
	 */
	public static final int TEXTURE_CUBE_MAP_POSITIVE_Z = RenderTexture.WGL_TEXTURE_CUBE_MAP_POSITIVE_Z_ARB;

	/**
	 * The render-to-texture cube map negative Z face value.
	 */
	public static final int TEXTURE_CUBE_MAP_NEGATIVE_Z = RenderTexture.WGL_TEXTURE_CUBE_MAP_NEGATIVE_Z_ARB;

	/**
	 * The Pbuffer front left buffer.
	 */
	public static final int FRONT_LEFT_BUFFER = RenderTexture.WGL_FRONT_LEFT_ARB;

	/**
	 * The Pbuffer front right buffer.
	 */
	public static final int FRONT_RIGHT_BUFFER = RenderTexture.WGL_FRONT_RIGHT_ARB;

	/**
	 * The Pbuffer back left buffer.
	 */
	public static final int BACK_LEFT_BUFFER = RenderTexture.WGL_BACK_LEFT_ARB;

	/**
	 * The Pbuffer back right buffer.
	 */
	public static final int BACK_RIGHT_BUFFER = RenderTexture.WGL_BACK_RIGHT_ARB;

	/**
	 * The Pbuffer depth buffer.
	 */
	public static final int DEPTH_BUFFER = RenderTexture.WGL_DEPTH_COMPONENT_NV;

	/**
	 * Width
	 */
	private final int width;

	/**
	 * Height
	 */
	private final int height;

	static {
		Sys.initialize();
	}

	/**
	 * Create an instance of a Pbuffer with a unique OpenGL context. The buffer is single-buffered.
	 * <p/>
	 * NOTE: The Pbuffer will have its own context that shares display lists and textures with <code>shared_context</code>,
	 * or, if <code>shared_context</code> is <code>null</code>, the Display context if it is created. The Pbuffer
	 * will have its own OpenGL state. Therefore, state changes to a pbuffer will not be seen in the window context and vice versa.
	 * <p/>
	 *
	 * @param width         Pbuffer width
	 * @param height        Pbuffer height
	 * @param pixel_format  Minimum Pbuffer context properties
	 * @param shared_drawable If non-null the Pbuffer will share display lists and textures with it. Otherwise, the Pbuffer will share
	 * 						 with the Display context (if created).
	 */
	public Pbuffer(int width, int height, PixelFormat pixel_format, Drawable shared_drawable) throws LWJGLException {
		this(width, height, pixel_format, null, shared_drawable);
	}

	/**
	 * Create an instance of a Pbuffer with a unique OpenGL context. The buffer is single-buffered.
	 * <p/>
	 * NOTE: The Pbuffer will have its own context that shares display lists and textures with <code>shared_context</code>,
	 * or, if <code>shared_context</code> is <code>null</code>, the Display context if it is created. The Pbuffer
	 * will have its own OpenGL state. Therefore, state changes to a pbuffer will not be seen in the window context and vice versa.
	 * <p/>
	 * The renderTexture parameter defines the necessary state for enabling render-to-texture. When this parameter is null,
	 * render-to-texture is not available. Before using render-to-texture, the Pbuffer capabilities must be queried to ensure that
	 * it is supported. Currently only windows platform can support this feature, so it is recommended that EXT_framebuffer_object
	 * or similar is used if available, for maximum portability.
	 * <p/>
	 *
	 * @param width         Pbuffer width
	 * @param height        Pbuffer height
	 * @param pixel_format  Minimum Pbuffer context properties
	 * @param renderTexture
	 * @param shared_drawable If non-null the Pbuffer will share display lists and textures with it. Otherwise, the Pbuffer will share
	 * 						 with the Display context (if created).
	 */
	public Pbuffer(int width, int height, PixelFormat pixel_format, RenderTexture renderTexture, Drawable shared_drawable) throws LWJGLException {
		this(width, height, pixel_format, renderTexture, shared_drawable, null);
	}

	/**
	 * Create an instance of a Pbuffer with a unique OpenGL context. The buffer is single-buffered.
	 * <p/>
	 * NOTE: The Pbuffer will have its own context that shares display lists and textures with <code>shared_context</code>,
	 * or, if <code>shared_context</code> is <code>null</code>, the Display context if it is created. The Pbuffer
	 * will have its own OpenGL state. Therefore, state changes to a pbuffer will not be seen in the window context and vice versa.
	 * <p/>
	 * The renderTexture parameter defines the necessary state for enabling render-to-texture. When this parameter is null,
	 * render-to-texture is not available. Before using render-to-texture, the Pbuffer capabilities must be queried to ensure that
	 * it is supported. Currently only windows platform can support this feature, so it is recommended that EXT_framebuffer_object
	 * or similar is used if available, for maximum portability.
	 * <p/>
	 *
	 * @param width         Pbuffer width
	 * @param height        Pbuffer height
	 * @param pixel_format  Minimum Pbuffer context properties
	 * @param renderTexture
	 * @param shared_drawable If non-null the Pbuffer will share display lists and textures with it. Otherwise, the Pbuffer will share
	 * 						 with the Display context (if created).
	 * @param attribs      The ContextAttribs to use when creating the context. (optional, may be null)
	 */
	public Pbuffer(int width, int height, PixelFormat pixel_format, RenderTexture renderTexture, Drawable shared_drawable, ContextAttribs attribs) throws LWJGLException {
		if (pixel_format == null)
			throw new NullPointerException("Pixel format must be non-null");
		this.width = width;
		this.height = height;
		this.peer_info = createPbuffer(width, height, pixel_format, attribs, renderTexture);
		Context shared_context = null;
		if ( shared_drawable == null )
			shared_drawable = Display.getDrawable(); // May be null
		if (shared_drawable != null)
			shared_context = ((DrawableLWJGL)shared_drawable).getContext();
		//this.context = new ContextGL(peer_info, attribs, (ContextGL)shared_context);
	}

	private static PeerInfo createPbuffer(int width, int height, PixelFormat pixel_format, ContextAttribs attribs, RenderTexture renderTexture) throws LWJGLException {
		if ( renderTexture == null ) {
			// Though null is a perfectly valid argument, Matrox Parhelia drivers expect
			// a 0 terminated list, or else they crash. Supplying NULL or 0, should
			// cause the drivers to use default settings
			IntBuffer defaultAttribs = BufferUtils.createIntBuffer(1);
			return Display.getImplementation().createPbuffer(width, height, pixel_format, attribs, null, defaultAttribs);
		} else
			return Display.getImplementation().createPbuffer(width, height, pixel_format, attribs,
					renderTexture.pixelFormatCaps,
					renderTexture.pBufferAttribs);
	}

	/**
	 * Method to test for validity of the buffer. If this function returns true, the buffer contents is lost. The buffer can still
	 * be used, but the results are undefined. The application is expected to release the buffer if needed, destroy it and recreate
	 * a new buffer.
	 *
	 * @return true if the buffer is lost and destroyed, false if the buffer is valid.
	 */
	public synchronized boolean isBufferLost() {
		checkDestroyed();
		return Display.getImplementation().isBufferLost(peer_info);
	}

	/**
	 * Gets the Pbuffer capabilities.
	 *
	 * @return a bitmask of Pbuffer capabilities.
	 */
	public static int getCapabilities() {
		return Display.getImplementation().getPbufferCapabilities();
	}

	// -----------------------------------------------------------------------------------------
	// ------------------------------- Render-to-Texture Methods -------------------------------
	// -----------------------------------------------------------------------------------------

	/**
	 * Sets a render-to-texture attribute.
	 * <p/>
	 * The attrib parameter can be one of MIPMAP_LEVEL and CUBE_MAP_FACE. When the attrib parameter is CUBE_MAP_FACE then the value
	 * parameter can be on of the following:
	 * <p/>
	 * TEXTURE_CUBE_MAP_POSITIVE_X TEXTURE_CUBE_MAP_NEGATIVE_X TEXTURE_CUBE_MAP_POSITIVE_Y TEXTURE_CUBE_MAP_NEGATIVE_Y
	 * TEXTURE_CUBE_MAP_POSITIVE_Z TEXTURE_CUBE_MAP_NEGATIVE_Z
	 *
	 * @param attrib
	 * @param value
	 */
	public synchronized void setAttrib(int attrib, int value) {
		checkDestroyed();
		Display.getImplementation().setPbufferAttrib(peer_info, attrib, value);
	}

	/**
	 * Binds the currently bound texture to the buffer specified. The buffer can be one of the following:
	 * <p/>
	 * FRONT_LEFT_BUFFER FRONT_RIGHT_BUFFER BACK_LEFT_BUFFER BACK_RIGHT_BUFFER DEPTH_BUFFER
	 *
	 * @param buffer
	 */
	public synchronized void bindTexImage(int buffer) {
		checkDestroyed();
		Display.getImplementation().bindTexImageToPbuffer(peer_info, buffer);
	}

	/**
	 * Releases the currently bound texture from the buffer specified.
	 *
	 * @param buffer
	 */
	public synchronized void releaseTexImage(int buffer) {
		checkDestroyed();
		Display.getImplementation().releaseTexImageFromPbuffer(peer_info, buffer);
	}

	/**
	 * @return Returns the height.
	 */
	public synchronized int getHeight() {
		checkDestroyed();
		return height;
	}

	/**
	 * @return Returns the width.
	 */
	public synchronized int getWidth() {
		checkDestroyed();
		return width;
	}
}
