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
 * This is the input implementation interface. Mouse and Keyboard delegates
 * to implementors of this interface. There is one InputImplementation
 * for each supported platform.
 * @author elias_naur
 */

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;

public interface InputImplementation {
	/*
	 * Mouse methods
	 */
	/** Query of wheel support */
	boolean hasWheel();

	/** Query of button count */
	int getButtonCount();

	/**
	 * Method to create the mouse.
	 */
	void createMouse() throws LWJGLException;

	/**
	 * Method the destroy the mouse
	 */
	void destroyMouse();

	/**
	 * Method to poll the mouse
	 */
	void pollMouse(IntBuffer coord_buffer, ByteBuffer buttons);

	/**
	 * Method to read the keyboard buffer
	 */
	void readMouse(ByteBuffer buffer);

	void grabMouse(boolean grab);

	/**
	 * Function to determine native cursor support
	 */
	int getNativeCursorCapabilities();

	/** Method to set the native cursor position */
	void setCursorPosition(int x, int y);
	
	/** Method to set the native cursor */
	void setNativeCursor(Object handle) throws LWJGLException;

	/** Method returning the minimum cursor size */
	int getMinCursorSize();

	/** Method returning the maximum cursor size */
	int getMaxCursorSize();

	/*
	 * Keyboard methods
	 */

	/**
	 * Method to create the keyboard
	 */
	void createKeyboard() throws LWJGLException;

	/**
	 * Method to destroy the keyboard
	 */
	void destroyKeyboard();

	/**
	 * Method to poll the keyboard.
	 *
	 * @param keyDownBuffer the address of a 256-byte buffer to place
	 * key states in.
	 */
	void pollKeyboard(ByteBuffer keyDownBuffer);

	/**
	 * Method to read the keyboard buffer
	 */
	void readKeyboard(ByteBuffer buffer);

//	int isStateKeySet(int key);

	/** Native cursor handles */
	Object createCursor(int width, int height, int xHotspot, int yHotspot, int numImages, IntBuffer images, IntBuffer delays) throws LWJGLException;

	void destroyCursor(Object cursor_handle);

	int getWidth();

	int getHeight();

        boolean isInsideWindow();
}
