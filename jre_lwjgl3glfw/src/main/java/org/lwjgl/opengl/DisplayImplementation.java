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
 * This is the Display implementation interface. Display delegates
 * to implementors of this interface. There is one DisplayImplementation
 * for each supported platform.
 * @author elias_naur
 */

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.awt.Canvas;

import org.lwjgl.LWJGLException;

interface DisplayImplementation extends InputImplementation {

	void createWindow(DrawableLWJGL drawable, DisplayMode mode, Canvas parent, int x, int y) throws LWJGLException;

	void destroyWindow();

	void switchDisplayMode(DisplayMode mode) throws LWJGLException;

	/**
	 * Reset the display mode to whatever it was when LWJGL was initialized.
	 * Fails silently.
	 */
	void resetDisplayMode();

	/**
	 * Return the length of the gamma ramp arrays. Returns 0 if gamma settings are
	 * unsupported.
	 *
	 * @return the length of each gamma ramp array, or 0 if gamma settings are unsupported.
	 */
	int getGammaRampLength();

	/**
	 * Method to set the gamma ramp.
	 */
	void setGammaRamp(FloatBuffer gammaRamp) throws LWJGLException;

	/**
	 * Get the driver adapter string. This is a unique string describing the actual card's hardware, eg. "Geforce2", "PS2",
	 * "Radeon9700". If the adapter cannot be determined, this function returns null.
	 * @return a String
	 */
	String getAdapter();

	/**
	 * Get the driver version. This is a vendor/adapter specific version string. If the version cannot be determined,
	 * this function returns null.
	 * @return a String
	 */
	String getVersion();

	/**
	 * Initialize and return the current display mode.
	 */
	DisplayMode init() throws LWJGLException;

	/**
	 * Implementation of setTitle(). This will read the window's title member
	 * and stash it in the native title of the window.
	 */
	void setTitle(String title);

	boolean isCloseRequested();

	boolean isVisible();
	boolean isActive();

	boolean isDirty();

	/**
	 * Create the native PeerInfo.
	 * @throws LWJGLException
	 */
	PeerInfo createPeerInfo(PixelFormat pixel_format, ContextAttribs attribs) throws LWJGLException;

//	void destroyPeerInfo();

	/**
	 * Updates the windows internal state. This must be called at least once per video frame
	 * to handle window close requests, moves, paints, etc.
	 */
	void update();

	void reshape(int x, int y, int width, int height);

	/**
	 * Method for getting displaymodes
	 */
	DisplayMode[] getAvailableDisplayModes() throws LWJGLException;

	/* Pbuffer */
	int getPbufferCapabilities();

	/**
	 * Method to test for buffer integrity
	 */
	boolean isBufferLost(PeerInfo handle);

	/**
	 * Method to create a Pbuffer
	 */
	PeerInfo createPbuffer(int width, int height, PixelFormat pixel_format, ContextAttribs attribs,
			IntBuffer pixelFormatCaps,
			IntBuffer pBufferAttribs) throws LWJGLException;

	void setPbufferAttrib(PeerInfo handle, int attrib, int value);

	void bindTexImageToPbuffer(PeerInfo handle, int buffer);

	void releaseTexImageFromPbuffer(PeerInfo handle, int buffer);

	/**
	 * Sets one or more icons for the Display.
	 * <ul>
	 * <li>On Windows you should supply at least one 16x16 icon and one 32x32.</li>
	 * <li>Linux (and similar platforms) expect one 32x32 icon.</li>
	 * <li>Mac OS X should be supplied one 128x128 icon</li>
	 * </ul>
	 * The implementation will use the supplied ByteBuffers with image data in RGBA and perform any conversions nescesarry for the specific platform.
	 *
	 * @param icons Array of icons in RGBA mode
	 * @return number of icons used.
	 */
	int setIcon(ByteBuffer[] icons);

	/**
	 * Enable or disable the Display window to be resized.
	 *
	 * @param resizable set to true to make the Display window resizable;
	 * false to disable resizing on the Display window.
	 */
	void setResizable(boolean resizable);

	/**
	 * @return true if the Display window has been resized since this method was last called.
	 */
	boolean wasResized();

	/**
	 * @return this method will return the width of the Display window.
	 */
	int getWidth();

	/**
	 * @return this method will return the height of the Display window.
	 */
	int getHeight();
	
	/**
	 * @return this method will return the top-left x position of the Display window.
	 */
	int getX();

	/**
	 * @return this method will return the top-left y position of the Display window.
	 */
	int getY();
	
	/**
	 * @return this method will return the pixel scale factor of the Display window useful for high resolution modes.
	 */
	float getPixelScaleFactor();
}
