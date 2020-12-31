/*
 * Copyright (c) 2002-2011 LWJGL Project
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

import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.PointerBuffer;

import static org.lwjgl.opengl.GL11.*;

/** @author Spasi */
abstract class DrawableGL implements DrawableLWJGL {

	/** The PixelFormat used to create the drawable. */
	protected PixelFormat pixel_format;

	/** Handle to the native GL rendering context */
	protected PeerInfo peer_info;

	/** The OpenGL Context. */
	protected ContextGL context;

	protected DrawableGL() {
	}

	public void setPixelFormat(final PixelFormatLWJGL pf) throws LWJGLException {
		throw new UnsupportedOperationException();
	}

	public void setPixelFormat(final PixelFormatLWJGL pf, final ContextAttribs attribs) throws LWJGLException {
		this.pixel_format = (PixelFormat)pf;
		this.peer_info = Display.getImplementation().createPeerInfo(pixel_format, attribs);
	}

	public PixelFormatLWJGL getPixelFormat() {
		return pixel_format;
	}

	public ContextGL getContext() {
		synchronized ( GlobalLock.lock ) {
			return context;
		}
	}

	public ContextGL createSharedContext() throws LWJGLException {
		synchronized ( GlobalLock.lock ) {
			checkDestroyed();
			return new ContextGL(peer_info, context.getContextAttribs(), context);
			// return null;
		}
	}

	public void checkGLError() {
		Util.checkGLError();
	}

	public void setSwapInterval(final int swap_interval) {
		ContextGL.setSwapInterval(swap_interval);
	}

	public void swapBuffers() throws LWJGLException {
		ContextGL.swapBuffers();
	}

	public void initContext(final float r, final float g, final float b) {
		// set background clear color
		glClearColor(r, g, b, 0.0f);
		// Clear window to avoid the desktop "showing through"
		glClear(GL_COLOR_BUFFER_BIT);
	}

	public boolean isCurrent() throws LWJGLException {
		synchronized ( GlobalLock.lock ) {
			checkDestroyed();
			return context.isCurrent();
		}
	}

	public void makeCurrent() throws LWJGLException {
		synchronized ( GlobalLock.lock ) {
			checkDestroyed();
			context.makeCurrent();
		}
	}

	public void releaseContext() throws LWJGLException {
		synchronized ( GlobalLock.lock ) {
			checkDestroyed();
			if ( context.isCurrent() )
				context.releaseCurrent();
		}
	}

	public void destroy() {
		synchronized ( GlobalLock.lock ) {
			if ( context == null )
				return;

			try {
				releaseContext();

				context.forceDestroy();
				context = null;

				if ( peer_info != null ) {
					peer_info.destroy();
					peer_info = null;
				}
			} catch (LWJGLException e) {
				LWJGLUtil.log("Exception occurred while destroying Drawable: " + e);
			}
		}
	}

	public void setCLSharingProperties(final PointerBuffer properties) throws LWJGLException {
		synchronized ( GlobalLock.lock ) {
			checkDestroyed();
			context.setCLSharingProperties(properties);
		}
	}

	protected final void checkDestroyed() {
		if ( context == null )
			throw new IllegalStateException("The Drawable has no context available.");
	}

}
