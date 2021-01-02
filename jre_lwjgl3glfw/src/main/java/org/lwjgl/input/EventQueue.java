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
package org.lwjgl.input;

/**
 * A java implementation of a LWJGL compatible event queue.
 * @author elias_naur
 */

import java.nio.ByteBuffer;

class EventQueue {
	private static final int QUEUE_SIZE = 200;

	private final int event_size;

	private final ByteBuffer queue;

	protected EventQueue(int event_size) {
		this.event_size = event_size;
		this.queue = ByteBuffer.allocate(QUEUE_SIZE*event_size);
	}

	protected synchronized void clearEvents() {
		queue.clear();
	}

	/**
	 * Copy available events into the specified buffer.
	 */
	public synchronized void copyEvents(ByteBuffer dest) {
		queue.flip();
		int old_limit = queue.limit();
		if (dest.remaining() < queue.remaining())
			queue.limit(dest.remaining() + queue.position());
		dest.put(queue);
		queue.limit(old_limit);
		queue.compact();
	}

	/**
	 * Put an event into the queue.
	 * @return true if the event fitted into the queue, false otherwise
	 */
	public synchronized boolean putEvent(ByteBuffer event) {
		if (event.remaining() != event_size)
			throw new IllegalArgumentException("Internal error: event size " + event_size + " does not equal the given event size " + event.remaining());
		if (queue.remaining() >= event.remaining()) {
			queue.put(event);
			return true;
		} else
			return false;
	}
}