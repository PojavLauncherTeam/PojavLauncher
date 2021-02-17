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

import java.nio.ByteBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;

/**
 *
 * @author elias_naur <elias_naur@users.sourceforge.net>
 * @version $Revision$
 * $Id$
 */
abstract class PeerInfo {
	private final ByteBuffer handle;
	private Thread locking_thread; // Thread that has locked this PeerInfo
	private int lock_count;

	protected PeerInfo(ByteBuffer handle) {
		this.handle = handle;
	}

	private void lockAndInitHandle() throws LWJGLException {
		doLockAndInitHandle();
	}

	public final synchronized void unlock() throws LWJGLException {
		if (lock_count <= 0)
			throw new IllegalStateException("PeerInfo not locked!");
		if (Thread.currentThread() != locking_thread)
			throw new IllegalStateException("PeerInfo already locked by " + locking_thread);
		lock_count--;
		if (lock_count == 0) {
			doUnlock();
			locking_thread = null;
			notify();
		}
	}

	protected abstract void doLockAndInitHandle() throws LWJGLException;
	protected abstract void doUnlock() throws LWJGLException;

	public final synchronized ByteBuffer lockAndGetHandle() throws LWJGLException {
		Thread this_thread = Thread.currentThread();
		while (locking_thread != null && locking_thread != this_thread) {
			try {
				wait();
			} catch (InterruptedException e) {
				LWJGLUtil.log("Interrupted while waiting for PeerInfo lock: " + e);
			}
		}
		if (lock_count == 0) {
			locking_thread = this_thread;
			doLockAndInitHandle();
		}
		lock_count++;
		return getHandle();
	}

	protected final ByteBuffer getHandle() {
		return handle;
	}

	public void destroy() {
	}
}
