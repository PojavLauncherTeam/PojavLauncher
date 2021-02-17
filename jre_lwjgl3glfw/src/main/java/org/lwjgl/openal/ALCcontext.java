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

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

/**
 * The ALCcontext class represents a context opened in OpenAL space.
 *
 * All operations of the AL core API affect a current AL context. Within the scope of AL,
 * the ALC is implied - it is not visible as a handle or function parameter. Only one AL
 * Context per process can be current at a time. Applications maintaining multiple AL
 * Contexts, whether threaded or not, have to set the current context accordingly.
 * Applications can have multiple threads that share one more or contexts. In other words,
 * AL and ALC are threadsafe.
 *
 * @author Brian Matzon <brian@matzon.dk>
 * @version $Revision$
 * $Id$
 */
public final class ALCcontext {

	/** Address of actual context */
	final long context;

	/** Whether this context is valid */
	private boolean valid;

	/**
	 * Creates a new instance of ALCcontext
	 *
	 * @param context address of actual context
	 */
	ALCcontext(long context) {
		this.context = context;
		this.valid = true;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object context) {
		if(context instanceof ALCcontext) {
			return ((ALCcontext)context).context == this.context;
		}
		return super.equals(context);
	}

	/**
	 * Creates an attribute list in a ByteBuffer
	 * @param contextFrequency Frequency to add
	 * @param contextRefresh Refresh rate to add
	 * @param contextSynchronized Whether to synchronize the context
	 * @return
	 */
	static IntBuffer createAttributeList(int contextFrequency, int contextRefresh, int contextSynchronized) {
		IntBuffer attribList = BufferUtils.createIntBuffer(7);

		attribList.put(ALC10.ALC_FREQUENCY);
		attribList.put(contextFrequency);
		attribList.put(ALC10.ALC_REFRESH);
		attribList.put(contextRefresh);
		attribList.put(ALC10.ALC_SYNC);
		attribList.put(contextSynchronized);
		attribList.put(0); //terminating int

		return attribList;
	}

	/**
	 * Marks this context as invalid
	 *
	 */
	void setInvalid() {
		valid = false;
	}

	/**
	 * @return true if this context is still valid
	 */
	public boolean isValid() {
		return valid;
	}
}
