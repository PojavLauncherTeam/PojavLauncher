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

/**
 * [INTERNAL USE ONLY]
 *
 * @author Spasi
 */
interface DrawableLWJGL extends Drawable {

	void setPixelFormat(PixelFormatLWJGL pf) throws LWJGLException;

	void setPixelFormat(PixelFormatLWJGL pf, ContextAttribs attribs) throws LWJGLException;

	PixelFormatLWJGL getPixelFormat();

	/**
	 * [INTERNAL USE ONLY] Returns the Drawable's Context.
	 *
	 * @return the Drawable's Context
	 */
	Context getContext();

	/**
	 * [INTERNAL USE ONLY] Creates a new Context that is shared with the Drawable's Context.
	 *
	 * @return a Context shared with the Drawable's Context.
	 */
	Context createSharedContext() throws LWJGLException;

	void checkGLError();

	void setSwapInterval(int swap_interval);

	void swapBuffers() throws LWJGLException;

	void initContext(final float r, final float g, final float b);

}
