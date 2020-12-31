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

import org.lwjgl.LWJGLException;
import org.lwjgl.PointerBuffer;

/**
 * The Drawable interface describes an OpenGL drawable with an associated
 * Context.
 *
 * @author elias_naur
 */

public interface Drawable {

	/** Returns true if the Drawable's context is current in the current thread. */
	boolean isCurrent() throws LWJGLException;

	/**
	 * Makes the Drawable's context current in the current thread.
	 *
	 * @throws LWJGLException
	 */
	void makeCurrent() throws LWJGLException;

	/**
	 * If the Drawable's context is current in the current thread, no context will be current after a call to this method.
	 *
	 * @throws LWJGLException
	 */
	void releaseContext() throws LWJGLException;

	/** Destroys the Drawable. */
	void destroy();

	/**
	 * Sets the appropriate khr_gl_sharing properties in the target <code>PointerBuffer</code>,
	 * so that if it is used in a <code>clCreateContext(FromType)</code> call, the created CL
	 * context will be sharing objects with this <code>Drawable</code>'s GL context. After a
	 * call to this method, the target buffer position will have advanced by 2 to 4 positions,
	 * depending on the implementation.
	 *
	 * @param properties The target properties buffer. It must have at least 4 positions remaining.
	 */
	void setCLSharingProperties(PointerBuffer properties) throws LWJGLException;

}
