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
package org.lwjgl.util;

import org.lwjgl.Sys;

/**
 *
 * A hires timer. This measures time in seconds as floating point values.
 * All Timers created are updated simultaneously by calling the static method
 * tick(). This ensures that within a single iteration of a game loop that
 * all timers are updated consistently with each other.
 *
 * @author cix_foo <cix_foo@users.sourceforge.net>
 * @version $Revision$
 * $Id$
 */
public class Timer {

	// Record the timer resolution on classload
	private static long resolution = Sys.getTimerResolution();

	// Every so often we will re-query the timer resolution
	private static final int QUERY_INTERVAL = 50; // in calls to tick()
	private static int queryCount;

	// Globally keeps track of time for all instances of Timer
	private static long currentTime;

	// When the timer was started
	private long startTime;

	// The last time recorded by getTime()
	private long lastTime;

	// Whether the timer is paused
	private boolean paused;

	static {
		tick();
	}

	/**
	 * Constructs a timer. The timer will be reset to 0.0 and resumed immediately.
	 */
	public Timer() {
		reset();
		resume();
	}

	/**
	 * @return the time in seconds, as a float
	 */
	public float getTime() {
		if (!paused) {
			lastTime = currentTime - startTime;
		}

		return (float) ((double) lastTime / (double) resolution);
	}
	/**
	 * @return whether this timer is paused
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Pause the timer. Whilst paused the time will not change for this timer
	 * when tick() is called.
	 *
	 * @see #resume()
	 */
	public void pause() {
		paused = true;
	}

	/**
	 * Reset the timer. Equivalent to set(0.0f);
	 * @see #set(float)
	 */
	public void reset() {
		set(0.0f);
	}

	/**
	 * Resume the timer.
	 * @see #pause()
	 */
	public void resume() {
		paused = false;
		startTime = currentTime - lastTime;
	}

	/**
	 * Set the time of this timer
	 * @param newTime the new time, in seconds
	 */
	public void set(float newTime) {
		long newTimeInTicks = (long) ((double) newTime * (double) resolution);
		startTime = currentTime - newTimeInTicks;
		lastTime = newTimeInTicks;
	}

	/**
	 * Get the next time update from the system's hires timer. This method should
	 * be called once per main loop iteration; all timers are updated simultaneously
	 * from it.
	 */
	public static void tick() {
		currentTime = Sys.getTime();

		// Periodically refresh the timer resolution:
		queryCount ++;
		if (queryCount > QUERY_INTERVAL) {
			queryCount = 0;
			resolution = Sys.getTimerResolution();
		}
	}

	/**
	 * Debug output.
	 */
	public String toString() {
		return "Timer[Time=" + getTime() + ", Paused=" + paused + "]";
	}
}