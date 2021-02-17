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

import java.io.Serializable;

/**
 * A 2D integer Dimension class, which looks remarkably like an AWT one.
 * @author $Author$
 * @version $Revision$
 * $Id$
 */
public final class Dimension implements Serializable, ReadableDimension, WritableDimension {
	
	static final long serialVersionUID = 1L;
	
	/** The dimensions! */
	private int width, height;

	/**
	 * Constructor for Dimension.
	 */
	public Dimension() {
		super();
	}

	/**
	 * Constructor for Dimension.
	 */
	public Dimension(int w, int h) {
		this.width = w;
		this.height = h;
	}

	/**
	 * Constructor for Dimension.
	 */
	public Dimension(ReadableDimension d) {
		setSize(d);
	}

	public void setSize(int w, int h) {
		this.width = w;
		this.height = h;
	}

	public void setSize(ReadableDimension d) {
		this.width = d.getWidth();
		this.height = d.getHeight();
	}
	
	/* (Overrides)
	 * @see com.shavenpuppy.jglib.ReadableDimension#getSize(com.shavenpuppy.jglib.Dimension)
	 */
	public void getSize(WritableDimension dest) {
		dest.setSize(this);
	}
	
	/**
	 * Checks whether two dimension objects have equal values.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof ReadableDimension) {
			ReadableDimension d = (ReadableDimension) obj;
			return (width == d.getWidth()) && (height == d.getHeight());
		}
		return false;
	}

	/**
	 * Returns the hash code for this <code>Dimension</code>.
	 *
	 * @return    a hash code for this <code>Dimension</code>
	 */
	public int hashCode() {
		int sum = width + height;
		return sum * (sum + 1) / 2 + width;
	}

	/**
	 * Returns a string representation of the values of this 
	 * <code>Dimension</code> object's <code>height</code> and 
	 * <code>width</code> fields. This method is intended to be used only 
	 * for debugging purposes, and the content and format of the returned 
	 * string may vary between implementations. The returned string may be 
	 * empty but may not be <code>null</code>.
	 * 
	 * @return  a string representation of this <code>Dimension</code> 
	 *          object
	 */
	public String toString() {
		return getClass().getName() + "[width=" + width + ",height=" + height + "]";
	}

	/**
	 * Gets the height.
	 * @return Returns a int
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the height.
	 * @param height The height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Gets the width.
	 * @return Returns a int
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the width.
	 * @param width The width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

}
