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
 *
 * This class encapsulates the properties for a given display mode.
 * This class is not instantiable, and is aquired from the <code>Display.
 * getAvailableDisplayModes()</code> method.
 *
 * @author cix_foo <cix_foo@users.sourceforge.net>
 * @version $Revision$
 * $Id$
 */

public final class DisplayMode {

	/** properties of the display mode */
	private final int width, height, bpp, freq;
	/** If true, this instance can be used for fullscreen modes */
	private final boolean fullscreen;

	/**
	 * Construct a display mode. DisplayModes constructed through the
	 * public constructor can only be used to specify the dimensions of
	 * the Display in windowed mode. To get the available DisplayModes for
	 * fullscreen modes, use Display.getAvailableDisplayModes().
	 *
	 * @param width The Display width.
	 * @param height The Display height.
	 * @see Display
	 */
	public DisplayMode(int width, int height) {
		this(width, height, 0, 0, false);
	}

	DisplayMode(int width, int height, int bpp, int freq) {
		this(width, height, bpp, freq, true);
	}

	private DisplayMode(int width, int height, int bpp, int freq, boolean fullscreen) {
		this.width = width;
		this.height = height;
		this.bpp = bpp;
		this.freq = freq;
		this.fullscreen = fullscreen;
	}

	/** True if this instance can be used for fullscreen modes */
	public boolean isFullscreenCapable() {
		return fullscreen;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getBitsPerPixel() {
		return bpp;
	}

	public int getFrequency() {
		return freq;
	}

	/**
	 * Tests for <code>DisplayMode</code> equality
	 *
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof DisplayMode)) {
			return false;
		}

		DisplayMode dm = (DisplayMode) obj;
		return dm.width == width
			&& dm.height == height
			&& dm.bpp == bpp
			&& dm.freq == freq;
	}

	/**
	 * Retrieves the hashcode for this object
	 *
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return width ^ height ^ freq ^ bpp;
	}

	/**
	 * Retrieves a String representation of this <code>DisplayMode</code>
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder(32);
		sb.append(width);
		sb.append(" x ");
		sb.append(height);
		sb.append(" x ");
		sb.append(bpp);
		sb.append(" @");
		sb.append(freq);
		sb.append("Hz");
		return sb.toString();
	}
}
