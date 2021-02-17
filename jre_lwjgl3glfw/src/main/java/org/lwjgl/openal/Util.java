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
 * Simple utility class for checking AL/ALC errors
 *
 * @author cix_foo <cix_foo@users.sourceforge.net>
 * @author Brian Matzon <brian@matzon.dk>
 * @version $Revision$
 */

public final class Util {
	/** No c'tor */
	private Util() {
	}

	/**
	 * Checks for any ALC errors and throws an unchecked exception on errors
	 * @param device Device for which to check ALC errors 
	 */
	public static void checkALCError(ALCdevice device) {
		int err = ALC10.alcGetError(device.device);
		if (err != ALC10.ALC_NO_ERROR)
			throw new OpenALException(ALC10.alcGetString(AL.getDevice().device, err));
	}
	
	/**
	 * Checks for any AL errors and throws an unchecked exception on errors
	 */
	public static void checkALError() {
		int err = AL10.alGetError();
		if (err != AL10.AL_NO_ERROR)
			throw new OpenALException(err);
	}

	/**
	 * Checks for a valid device
	 * @param device ALCdevice to check the validity of 
	 */
	public static void checkALCValidDevice(ALCdevice device) {
		if(!device.isValid()) {
			throw new OpenALException("Invalid device: " + device);
		}
	}

	/**
	 * Checks for a valid context
	 * @param context ALCcontext to check the validity of 
	 */
	public static void checkALCValidContext(ALCcontext context) {
		if(!context.isValid()) {
			throw new OpenALException("Invalid context: " + context);
		}
	}
}
