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
package org.lwjgl.util.glu;

import static org.lwjgl.opengl.GL11.*;

/**
 * PixelStoreState.java
 *
 *
 * Created 11-jan-2004
 * @author Erik Duijs
 */
class PixelStoreState extends Util {

	public int unpackRowLength;
	public int unpackAlignment;
	public int unpackSkipRows;
	public int unpackSkipPixels;
	public int packRowLength;
	public int packAlignment;
	public int packSkipRows;
	public int packSkipPixels;

	/**
	 * Constructor for PixelStoreState.
	 */
	PixelStoreState() {
		super();
		load();
	}

	public void load() {
		unpackRowLength = glGetInteger(GL_UNPACK_ROW_LENGTH);
		unpackAlignment = glGetInteger(GL_UNPACK_ALIGNMENT);
		unpackSkipRows = glGetInteger(GL_UNPACK_SKIP_ROWS);
		unpackSkipPixels = glGetInteger(GL_UNPACK_SKIP_PIXELS);
		packRowLength = glGetInteger(GL_PACK_ROW_LENGTH);
		packAlignment = glGetInteger(GL_PACK_ALIGNMENT);
		packSkipRows = glGetInteger(GL_PACK_SKIP_ROWS);
		packSkipPixels = glGetInteger(GL_PACK_SKIP_PIXELS);
	}

	public void save() {
		glPixelStorei(GL_UNPACK_ROW_LENGTH, unpackRowLength);
		glPixelStorei(GL_UNPACK_ALIGNMENT, unpackAlignment);
		glPixelStorei(GL_UNPACK_SKIP_ROWS, unpackSkipRows);
		glPixelStorei(GL_UNPACK_SKIP_PIXELS, unpackSkipPixels);
		glPixelStorei(GL_PACK_ROW_LENGTH, packRowLength);
		glPixelStorei(GL_PACK_ALIGNMENT, packAlignment);
		glPixelStorei(GL_PACK_SKIP_ROWS, packSkipRows);
		glPixelStorei(GL_PACK_SKIP_PIXELS, packSkipPixels);
	}

}
