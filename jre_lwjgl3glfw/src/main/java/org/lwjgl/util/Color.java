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
import java.nio.ByteBuffer;

/**
 * A mutable Color class
 * @author $Author$
 * @version $Revision$
 * $Id$
 */
public final class Color implements ReadableColor, Serializable, WritableColor {

	static final long serialVersionUID = 1L;

	/** Color components, publicly accessible */
	private byte red, green, blue, alpha;
	
	/**
	 * Constructor for Color.
	 */
	public Color() {
		this(0, 0, 0, 255);
	}

	/**
	 * Constructor for Color. Alpha defaults to 255.
	 */
	public Color(int r, int g, int b) {
		this(r, g, b, 255);
	}
	
	/**
	 * Constructor for Color. Alpha defaults to 255.
	 */
	public Color(byte r, byte g, byte b) {
		this(r, g, b, (byte) 255);
	}
	
	/**
	 * Constructor for Color.
	 */
	public Color(int r, int g, int b, int a) {
		set(r, g, b, a);
	}
	
	/**
	 * Constructor for Color.
	 */
	public Color(byte r, byte g, byte b, byte a) {
		set(r, g, b, a);
	}
	
	/**
	 * Constructor for Color
	 */
	public Color(ReadableColor c) {
		setColor(c);
	}

	/**
	 * Set a color
	 */
	public void set(int r, int g, int b, int a) {
		red = (byte) r;
		green = (byte) g;
		blue = (byte) b;
		alpha = (byte) a;
	}

	/**
	 * Set a color
	 */
	public void set(byte r, byte g, byte b, byte a) {
		this.red = r;
		this.green = g;
		this.blue = b;
		this.alpha = a;
	}

	/**
	 * Set a color
	 */
	public void set(int r, int g, int b) {
		set(r, g, b, 255);
	}

	/**
	 * Set a color
	 */
	public void set(byte r, byte g, byte b) {
		set(r, g, b, (byte) 255);
	}
	
	/**
	 * Accessor
	 */
	public int getRed() {
		return red & 0xFF;
	}

	/**
	 * Accessor
	 */
	public int getGreen() {
		return green & 0xFF;
	}

	/**
	 * Accessor
	 */
	public int getBlue() {
		return blue & 0xFF;
	}

	/**
	 * Accessor
	 */
	public int getAlpha() {
		return alpha & 0xFF;
	}
	
	/**
	 * Set the Red component
	 */
	public void setRed(int red) {
		this.red = (byte) red;
	}

	/**
	 * Set the Green component
	 */
	public void setGreen(int green) {
		this.green = (byte) green;
	}

	/**
	 * Set the Blue component
	 */
	public void setBlue(int blue) {
		this.blue = (byte) blue;
	}

	/**
	 * Set the Alpha component
	 */
	public void setAlpha(int alpha) {
		this.alpha = (byte) alpha;
	}

	/**
	 * Set the Red component
	 */
	public void setRed(byte red) {
		this.red = red;
	}

	/**
	 * Set the Green component
	 */
	public void setGreen(byte green) {
		this.green = green;
	}

	/**
	 * Set the Blue component
	 */
	public void setBlue(byte blue) {
		this.blue = blue;
	}

	/**
	 * Set the Alpha component
	 */
	public void setAlpha(byte alpha) {
		this.alpha = alpha;
	}

	/**
	 * Stringify
	 */
	public String toString() {
		return "Color [" + getRed() + ", " + getGreen() + ", " + getBlue() + ", " + getAlpha() + "]";
	}

	/**
	 * Equals
	 */
	public boolean equals(Object o) {
		return (o != null)
			&& (o instanceof ReadableColor)
			&& (((ReadableColor) o).getRed() == this.getRed())
			&& (((ReadableColor) o).getGreen() == this.getGreen())
			&& (((ReadableColor) o).getBlue() == this.getBlue())
			&& (((ReadableColor) o).getAlpha() == this.getAlpha());
	}
	
	/**
	 * Hashcode
	 */
	public int hashCode() {
		return (red << 24) | (green << 16) | (blue << 8) | alpha;
	}
	
	/* (Overrides)
	 * @see com.shavenpuppy.jglib.ReadableColor#getAlphaByte()
	 */
	public byte getAlphaByte() {
		return alpha;
	}

	/* (Overrides)
	 * @see com.shavenpuppy.jglib.ReadableColor#getBlueByte()
	 */
	public byte getBlueByte() {
		return blue;
	}

	/* (Overrides)
	 * @see com.shavenpuppy.jglib.ReadableColor#getGreenByte()
	 */
	public byte getGreenByte() {
		return green;
	}

	/* (Overrides)
	 * @see com.shavenpuppy.jglib.ReadableColor#getRedByte()
	 */
	public byte getRedByte() {
		return red;
	}

	/* (Overrides)
	 * @see com.shavenpuppy.jglib.ReadableColor#writeRGBA(java.nio.ByteBuffer)
	 */
	public void writeRGBA(ByteBuffer dest) {
		dest.put(red);
		dest.put(green);
		dest.put(blue);
		dest.put(alpha);
	}

	/* (Overrides)
	 * @see com.shavenpuppy.jglib.ReadableColor#writeRGB(java.nio.ByteBuffer)
	 */
	public void writeRGB(ByteBuffer dest) {
		dest.put(red);
		dest.put(green);
		dest.put(blue);
	}

	/* (Overrides)
	 * @see com.shavenpuppy.jglib.ReadableColor#writeABGR(java.nio.ByteBuffer)
	 */
	public void writeABGR(ByteBuffer dest) {
		dest.put(alpha);
		dest.put(blue);
		dest.put(green);
		dest.put(red);
	}

	/* (Overrides)
	 * @see com.shavenpuppy.jglib.ReadableColor#writeARGB(java.nio.ByteBuffer)
	 */
	public void writeARGB(ByteBuffer dest) {
		dest.put(alpha);
		dest.put(red);
		dest.put(green);
		dest.put(blue);
	}
	
	/* (Overrides)
	 * @see com.shavenpuppy.jglib.ReadableColor#writeBGR(java.nio.ByteBuffer)
	 */
	public void writeBGR(ByteBuffer dest) {
		dest.put(blue);
		dest.put(green);
		dest.put(red);
	}
	
	/* (Overrides)
	 * @see com.shavenpuppy.jglib.ReadableColor#writeBGRA(java.nio.ByteBuffer)
	 */
	public void writeBGRA(ByteBuffer dest) {
		dest.put(blue);
		dest.put(green);
		dest.put(red);
		dest.put(alpha);
	}
	
	/**
	 * Read a color from a byte buffer
	 * @param src The source buffer
	 */
	public void readRGBA(ByteBuffer src) {
		red = src.get();
		green = src.get();
		blue = src.get();
		alpha = src.get();
	}

	/**
	 * Read a color from a byte buffer
	 * @param src The source buffer
	 */
	public void readRGB(ByteBuffer src) {
		red = src.get();
		green = src.get();
		blue = src.get();
	}

	/**
	 * Read a color from a byte buffer
	 * @param src The source buffer
	 */
	public void readARGB(ByteBuffer src) {
		alpha = src.get();
		red = src.get();
		green = src.get();
		blue = src.get();
	}

	/**
	 * Read a color from a byte buffer
	 * @param src The source buffer
	 */
	public void readBGRA(ByteBuffer src) {
		blue = src.get();
		green = src.get();
		red = src.get();
		alpha = src.get();
	}

	/**
	 * Read a color from a byte buffer
	 * @param src The source buffer
	 */
	public void readBGR(ByteBuffer src) {
		blue = src.get();
		green = src.get();
		red = src.get();
	}

	/**
	 * Read a color from a byte buffer
	 * @param src The source buffer
	 */
	public void readABGR(ByteBuffer src) {
		alpha = src.get();
		blue = src.get();
		green = src.get();
		red = src.get();
	}

	/**
	 * Set this color's color by copying another color
	 * @param src The source color
	 */
	public void setColor(ReadableColor src) {
		red = src.getRedByte();
		green = src.getGreenByte();
		blue = src.getBlueByte();
		alpha = src.getAlphaByte();
	}
	
	/**
	 * HSB to RGB conversion, pinched from java.awt.Color.
	 * @param hue (0..1.0f)
	 * @param saturation (0..1.0f)
	 * @param brightness (0..1.0f)
	 */
    public void fromHSB(float hue, float saturation, float brightness) {
		if (saturation == 0.0F) {
			red = green = blue = (byte) (brightness * 255F + 0.5F);
		} else {
			float f3 = (hue - (float) Math.floor(hue)) * 6F;
			float f4 = f3 - (float) Math.floor(f3);
			float f5 = brightness * (1.0F - saturation);
			float f6 = brightness * (1.0F - saturation * f4);
			float f7 = brightness * (1.0F - saturation * (1.0F - f4));
			switch ((int) f3) {
				case 0 :
					red = (byte) (brightness * 255F + 0.5F);
					green = (byte) (f7 * 255F + 0.5F);
					blue = (byte) (f5 * 255F + 0.5F);
					break;
				case 1 :
					red = (byte) (f6 * 255F + 0.5F);
					green = (byte) (brightness * 255F + 0.5F);
					blue = (byte) (f5 * 255F + 0.5F);
					break;
				case 2 :
					red = (byte) (f5 * 255F + 0.5F);
					green = (byte) (brightness * 255F + 0.5F);
					blue = (byte) (f7 * 255F + 0.5F);
					break;
				case 3 :
					red = (byte) (f5 * 255F + 0.5F);
					green = (byte) (f6 * 255F + 0.5F);
					blue = (byte) (brightness * 255F + 0.5F);
					break;
				case 4 :
					red = (byte) (f7 * 255F + 0.5F);
					green = (byte) (f5 * 255F + 0.5F);
					blue = (byte) (brightness * 255F + 0.5F);
					break;
				case 5 :
					red = (byte) (brightness * 255F + 0.5F);
					green = (byte) (f5 * 255F + 0.5F);
					blue = (byte) (f6 * 255F + 0.5F);
					break;
			}
		}
	}

	/**
	 * RGB to HSB conversion, pinched from java.awt.Color.
	 * The HSB value is returned in dest[] if dest[] is supplied.
	 * Values range from 0..1
	 * @param dest Destination floats, or null
	 * @return dest, or a new float array
	 */
	public float[] toHSB(float dest[]) {
		int r = getRed();
		int g = getGreen();
		int b = getBlue();
		if (dest == null)
			dest = new float[3];
		int l = r <= g ? g : r;
		if (b > l)
			l = b;
		int i1 = r >= g ? g : r;
		if (b < i1)
			i1 = b;
		float brightness = l / 255F;
		float saturation;
		if (l != 0)
			saturation = (float) (l - i1) / (float) l;
		else
			saturation = 0.0F;
		float hue;
		if (saturation == 0.0F) {
			hue = 0.0F;
		} else {
			float f3 = (float) (l - r) / (float) (l - i1);
			float f4 = (float) (l - g) / (float) (l - i1);
			float f5 = (float) (l - b) / (float) (l - i1);
			if (r == l)
				hue = f5 - f4;
			else if (g == l)
				hue = (2.0F + f3) - f5;
			else
				hue = (4F + f4) - f3;
			hue /= 6F;
			if (hue < 0.0F)
				hue++;
		}
		dest[0] = hue;
		dest[1] = saturation;
		dest[2] = brightness;
		return dest;
	}

}
