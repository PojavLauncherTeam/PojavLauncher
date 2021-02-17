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

/**
 * <p/>
 * Thrown by the debug build library of the LWJGL if any OpenGL operation causes an error.
 *
 * @author cix_foo <cix_foo@users.sourceforge.net>
 * @version $Revision$
 * $Id$
 */
public class OpenGLException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/** Constructor for OpenGLException. */
	public OpenGLException(int gl_error_code) {
		this(createErrorMessage(gl_error_code));
	}

	private static String createErrorMessage(int gl_error_code) {
		String error_string = Util.translateGLErrorString(gl_error_code);
		return error_string + " (" + gl_error_code + ")";
	}

	/** Constructor for OpenGLException. */
	public OpenGLException() {
		super();
	}

	/**
	 * Constructor for OpenGLException.
	 *
	 * @param message
	 */
	public OpenGLException(String message) {
		super(message);
	}

	/**
	 * Constructor for OpenGLException.
	 *
	 * @param message
	 * @param cause
	 */
	public OpenGLException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for OpenGLException.
	 *
	 * @param cause
	 */
	public OpenGLException(Throwable cause) {
		super(cause);
	}

}
