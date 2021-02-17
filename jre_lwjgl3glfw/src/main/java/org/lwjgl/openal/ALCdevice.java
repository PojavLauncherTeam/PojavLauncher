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

import java.util.HashMap;

/**
 * The ALCdevice class represents a device opened in OpenAL space.
 *
 * ALC introduces the notion of a Device. A Device can be, depending on the
 * implementation, a hardware device, or a daemon/OS service/actual server. This
 * mechanism also permits different drivers (and hardware) to coexist within the same
 * system, as well as allowing several applications to share system resources for audio,
 * including a single hardware output device. The details are left to the implementation,
 * which has to map the available backends to unique device specifiers.
 *
 * @author Brian Matzon <brian@matzon.dk>
 * @version $Revision$
 * $Id$
 */
public final class ALCdevice {

    /** Address of actual device */
    final long device;

    /** Whether this device is valid */
    private boolean valid;

	/** List of contexts belonging to the device */
	private final HashMap<Long, ALCcontext> contexts = new HashMap<Long, ALCcontext>();

    /**
     * Creates a new instance of ALCdevice
     *
     * @param device address of actual device
     */
    ALCdevice(long device) {
        this.device = device;
        this.valid = true;
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
	public boolean equals(Object device) {
		if(device instanceof ALCdevice) {
			return ((ALCdevice)device).device == this.device;
		}
		return super.equals(device);
	}

	/**
	 * Adds a context to the device
	 *
	 * @param context context to add to the list of contexts for this device
	 */
	void addContext(ALCcontext context) {
		synchronized (contexts) {
			contexts.put(context.context, context);
		}
	}

	/**
	 * Remove context associated with device
	 *
	 * @param context Context to disassociate with device
	 */
	void removeContext(ALCcontext context) {
		synchronized (contexts) {
			contexts.remove(context.context);
		}
	}

	/**
	 * Marks this device and all of its contexts invalid
	 */
	void setInvalid() {
		valid = false;
		synchronized (contexts) {
			for ( ALCcontext context : contexts.values() )
				context.setInvalid();
		}
		contexts.clear();
	}

	/**
	 * @return true if this device is still valid
	 */
	public boolean isValid() {
		return valid;
	}
}
