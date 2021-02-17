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
package org.lwjgl.openal;

/**
 * <br>
 * Thrown by the debug build library of the LWJGL if any OpenAL operation
 * causes an error.
 * 
 * @author Brian Matzon <brian@matzon.dk>
 * @version $Revision$
 * $Id$
 */
public class OpenALException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for OpenALException.
	 */
	public OpenALException() {
		super();
	}

	/**
	  * Constructor that takes an AL error number
	  */
	public OpenALException(int error_code) {
		super("OpenAL error: " + org.lwjgl.openal.AL10.alGetString(error_code) + " (" + error_code + ")");		
	}

	/**
	 * Constructor for OpenALException.
	 * @param message
	 */
	public OpenALException(String message) {
		super(message);
	}

	/**
	 * Constructor for OpenALException.
	 * @param message
	 * @param cause
	 */
	public OpenALException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for OpenALException.
	 * @param cause
	 */
	public OpenALException(Throwable cause) {
		super(cause);
	}
}
