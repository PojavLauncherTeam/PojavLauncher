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
 * A 2D integer Rectangle class which looks remarkably like an AWT one.
 * @author $Author$
 * @version $Revision$
 * $Id$
 */
public final class Rectangle implements ReadableRectangle, WritableRectangle, Serializable {

	static final long serialVersionUID = 1L;

	/** Rectangle's bounds */
	private int x, y, width, height;

	/**
	 * Constructor for Rectangle.
	 */
	public Rectangle() {
		super();
	}
	/**
	 * Constructor for Rectangle.
	 */
	public Rectangle(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}
	/**
	 * Constructor for Rectangle.
	 */
	public Rectangle(ReadablePoint p, ReadableDimension d) {
		x = p.getX();
		y = p.getY();
		width = d.getWidth();
		height = d.getHeight();
	}
	/**
	 * Constructor for Rectangle.
	 */
	public Rectangle(ReadableRectangle r) {
		x = r.getX();
		y = r.getY();
		width = r.getWidth();
		height = r.getHeight();
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setLocation(ReadablePoint p) {
		this.x = p.getX();
		this.y = p.getY();
	}

	public void setSize(int w, int h) {
		this.width = w;
		this.height = h;
	}

	public void setSize(ReadableDimension d) {
		this.width = d.getWidth();
		this.height = d.getHeight();
	}

	public void setBounds(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	public void setBounds(ReadablePoint p, ReadableDimension d) {
		x = p.getX();
		y = p.getY();
		width = d.getWidth();
		height = d.getHeight();
	}

	public void setBounds(ReadableRectangle r) {
		x = r.getX();
		y = r.getY();
		width = r.getWidth();
		height = r.getHeight();
	}
	
	/* (Overrides)
	 * @see com.shavenpuppy.jglib.ReadableRectangle#getBounds(com.shavenpuppy.jglib.Rectangle)
	 */
	public void getBounds(WritableRectangle dest) {
		dest.setBounds(x, y, width, height);
	}
	
	/* (Overrides)
	 * @see com.shavenpuppy.jglib.ReadablePoint#getLocation(com.shavenpuppy.jglib.Point)
	 */
	public void getLocation(WritablePoint dest) {
		dest.setLocation(x, y);
	}
	
	/* (Overrides)
	 * @see com.shavenpuppy.jglib.ReadableDimension#getSize(com.shavenpuppy.jglib.Dimension)
	 */
	public void getSize(WritableDimension dest) {
		dest.setSize(width, height);
	}

	/**
	 * Translate the rectangle by an amount.
	 * @param x The translation amount on the x axis
   * @param y The translation amount on the y axis
	 */
	public void translate(int x, int y) {
		this.x += x;
		this.y += y;
	}

	/**
	 * Translate the rectangle by an amount.
	 * @param point The translation amount
	 */
	public void translate(ReadablePoint point) {
		this.x += point.getX();
		this.y += point.getY();
	}

	/**
	 * Un-translate the rectangle by an amount.
	 * @param point The translation amount
	 */
	public void untranslate(ReadablePoint point) {
		this.x -= point.getX();
		this.y -= point.getY();
	}

	/**
	 * Checks whether or not this <code>Rectangle</code> contains the 
	 * specified <code>Point</code>.
	 * @param p the <code>Point</code> to test
	 * @return    <code>true</code> if the <code>Point</code> 
	 *            (<i>x</i>,&nbsp;<i>y</i>) is inside this 
	 * 		  <code>Rectangle</code>; 
	 *            <code>false</code> otherwise.
	 */
	public boolean contains(ReadablePoint p) {
		return contains(p.getX(), p.getY());
	}

	/**
	 * Checks whether or not this <code>Rectangle</code> contains the 
	 * point at the specified location
	 * (<i>x</i>,&nbsp;<i>y</i>).
	 * @param X the specified x coordinate
   * @param Y the specified y coordinate
	 * @return    <code>true</code> if the point 
	 *            (<i>x</i>,&nbsp;<i>y</i>) is inside this 
	 *		  <code>Rectangle</code>; 
	 *            <code>false</code> otherwise.
	 */
	public boolean contains(int X, int Y) {
		int w = this.width;
		int h = this.height;
		if ((w | h) < 0) {
			// At least one of the dimensions is negative...
			return false;
		}
		// Note: if either dimension is zero, tests below must return false...
		int x = this.x;
		int y = this.y;
		if (X < x || Y < y) {
			return false;
		}
		w += x;
		h += y;
		//    overflow || intersect
		return ((w < x || w > X) && (h < y || h > Y));
	}

	/**
	 * Checks whether or not this <code>Rectangle</code> entirely contains 
	 * the specified <code>Rectangle</code>.
	 * @param     r   the specified <code>Rectangle</code>
	 * @return    <code>true</code> if the <code>Rectangle</code> 
	 *            is contained entirely inside this <code>Rectangle</code>; 
	 *            <code>false</code> otherwise.
	 */
	public boolean contains(ReadableRectangle r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * Checks whether this <code>Rectangle</code> entirely contains 
	 * the <code>Rectangle</code>
	 * at the specified location (<i>X</i>,&nbsp;<i>Y</i>) with the
	 * specified dimensions (<i>W</i>,&nbsp;<i>H</i>).
   * @param X the specified x coordinate
   * @param Y the specified y coordinate
	 * @param     W   the width of the <code>Rectangle</code>
	 * @param     H   the height of the <code>Rectangle</code>
	 * @return    <code>true</code> if the <code>Rectangle</code> specified by
	 *            (<i>X</i>,&nbsp;<i>Y</i>,&nbsp;<i>W</i>,&nbsp;<i>H</i>)
	 *            is entirely enclosed inside this <code>Rectangle</code>; 
	 *            <code>false</code> otherwise.
	 */
	public boolean contains(int X, int Y, int W, int H) {
		int w = this.width;
		int h = this.height;
		if ((w | h | W | H) < 0) {
			// At least one of the dimensions is negative...
			return false;
		}
		// Note: if any dimension is zero, tests below must return false...
		int x = this.x;
		int y = this.y;
		if (X < x || Y < y) {
			return false;
		}
		w += x;
		W += X;
		if (W <= X) {
			// X+W overflowed or W was zero, return false if...
			// either original w or W was zero or
			// x+w did not overflow or
			// the overflowed x+w is smaller than the overflowed X+W
			if (w >= x || W > w)
				return false;
		} else {
			// X+W did not overflow and W was not zero, return false if...
			// original w was zero or
			// x+w did not overflow and x+w is smaller than X+W
			if (w >= x && W > w)
				return false;
		}
		h += y;
		H += Y;
		if (H <= Y) {
			if (h >= y || H > h)
				return false;
		} else {
			if (h >= y && H > h)
				return false;
		}
		return true;
	}

	/**
	 * Determines whether or not this <code>Rectangle</code> and the specified 
	 * <code>Rectangle</code> intersect. Two rectangles intersect if 
	 * their intersection is nonempty. 
	 *
	 * @param r the specified <code>Rectangle</code>
	 * @return    <code>true</code> if the specified <code>Rectangle</code> 
	 *            and this <code>Rectangle</code> intersect; 
	 *            <code>false</code> otherwise.
	 */
	public boolean intersects(ReadableRectangle r) {
		int tw = this.width;
		int th = this.height;
		int rw = r.getWidth();
		int rh = r.getHeight();
		if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
			return false;
		}
		int tx = this.x;
		int ty = this.y;
		int rx = r.getX();
		int ry = r.getY();
		rw += rx;
		rh += ry;
		tw += tx;
		th += ty;
		//      overflow || intersect
		return ((rw < rx || rw > tx) && (rh < ry || rh > ty) && (tw < tx || tw > rx) && (th < ty || th > ry));
	}

	/**
	 * Computes the intersection of this <code>Rectangle</code> with the 
	 * specified <code>Rectangle</code>. Returns a new <code>Rectangle</code> 
	 * that represents the intersection of the two rectangles.
	 * If the two rectangles do not intersect, the result will be
	 * an empty rectangle.
	 *
	 * @param     r   the specified <code>Rectangle</code>
	 * @return    the largest <code>Rectangle</code> contained in both the 
	 *            specified <code>Rectangle</code> and in 
	 *		  this <code>Rectangle</code>; or if the rectangles
	 *            do not intersect, an empty rectangle.
	 */
	public Rectangle intersection(ReadableRectangle r, Rectangle dest) {
		int tx1 = this.x;
		int ty1 = this.y;
		int rx1 = r.getX();
		int ry1 = r.getY();
		long tx2 = tx1;
		tx2 += this.width;
		long ty2 = ty1;
		ty2 += this.height;
		long rx2 = rx1;
		rx2 += r.getWidth();
		long ry2 = ry1;
		ry2 += r.getHeight();
		if (tx1 < rx1)
			tx1 = rx1;
		if (ty1 < ry1)
			ty1 = ry1;
		if (tx2 > rx2)
			tx2 = rx2;
		if (ty2 > ry2)
			ty2 = ry2;
		tx2 -= tx1;
		ty2 -= ty1;
		// tx2,ty2 will never overflow (they will never be
		// larger than the smallest of the two source w,h)
		// they might underflow, though...
		if (tx2 < Integer.MIN_VALUE)
			tx2 = Integer.MIN_VALUE;
		if (ty2 < Integer.MIN_VALUE)
			ty2 = Integer.MIN_VALUE;
		if (dest == null)
			dest = new Rectangle(tx1, ty1, (int) tx2, (int) ty2);
		else
			dest.setBounds(tx1, ty1, (int) tx2, (int) ty2);
		return dest;
		
	}

	/**
	 * Computes the union of this <code>Rectangle</code> with the 
	 * specified <code>Rectangle</code>. Returns a new 
	 * <code>Rectangle</code> that 
	 * represents the union of the two rectangles
	 * @param r the specified <code>Rectangle</code>
	 * @return    the smallest <code>Rectangle</code> containing both 
	 *		  the specified <code>Rectangle</code> and this 
	 *		  <code>Rectangle</code>.
	 */
	public WritableRectangle union(ReadableRectangle r, WritableRectangle dest) {
		int x1 = Math.min(x, r.getX());
		int x2 = Math.max(x + width, r.getX() + r.getWidth());
		int y1 = Math.min(y, r.getY());
		int y2 = Math.max(y + height, r.getY() + r.getHeight());
		dest.setBounds(x1, y1, x2 - x1, y2 - y1);
		return dest;
	}

	/**
	 * Adds a point, specified by the integer arguments <code>newx</code>
	 * and <code>newy</code>, to this <code>Rectangle</code>. The 
	 * resulting <code>Rectangle</code> is
	 * the smallest <code>Rectangle</code> that contains both the 
	 * original <code>Rectangle</code> and the specified point.
	 * <p>
	 * After adding a point, a call to <code>contains</code> with the 
	 * added point as an argument does not necessarily return
	 * <code>true</code>. The <code>contains</code> method does not 
	 * return <code>true</code> for points on the right or bottom 
	 * edges of a <code>Rectangle</code>. Therefore, if the added point 
	 * falls on the right or bottom edge of the enlarged 
	 * <code>Rectangle</code>, <code>contains</code> returns 
	 * <code>false</code> for that point.
	 * @param newx the x coordinates of the new point
   * @param newy the y coordinates of the new point
	 */
	public void add(int newx, int newy) {
		int x1 = Math.min(x, newx);
		int x2 = Math.max(x + width, newx);
		int y1 = Math.min(y, newy);
		int y2 = Math.max(y + height, newy);
		x = x1;
		y = y1;
		width = x2 - x1;
		height = y2 - y1;
	}

	/**
	 * Adds the specified <code>Point</code> to this 
	 * <code>Rectangle</code>. The resulting <code>Rectangle</code> 
	 * is the smallest <code>Rectangle</code> that contains both the 
	 * original <code>Rectangle</code> and the specified 
	 * <code>Point</code>.
	 * <p>
	 * After adding a <code>Point</code>, a call to <code>contains</code> 
	 * with the added <code>Point</code> as an argument does not 
	 * necessarily return <code>true</code>. The <code>contains</code> 
	 * method does not return <code>true</code> for points on the right 
	 * or bottom edges of a <code>Rectangle</code>. Therefore if the added 
	 * <code>Point</code> falls on the right or bottom edge of the 
	 * enlarged <code>Rectangle</code>, <code>contains</code> returns 
	 * <code>false</code> for that <code>Point</code>.
	 * @param pt the new <code>Point</code> to add to this 
	 *           <code>Rectangle</code>
	 */
	public void add(ReadablePoint pt) {
		add(pt.getX(), pt.getY());
	}

	/**
	 * Adds a <code>Rectangle</code> to this <code>Rectangle</code>. 
	 * The resulting <code>Rectangle</code> is the union of the two
	 * rectangles. 
	 * @param  r the specified <code>Rectangle</code>
	 */
	public void add(ReadableRectangle r) {
		int x1 = Math.min(x, r.getX());
		int x2 = Math.max(x + width, r.getX() + r.getWidth());
		int y1 = Math.min(y, r.getY());
		int y2 = Math.max(y + height, r.getY() + r.getHeight());
		x = x1;
		y = y1;
		width = x2 - x1;
		height = y2 - y1;
	}

	/**
	 * Resizes the <code>Rectangle</code> both horizontally and vertically.
	 * <p>
	 * This method modifies the <code>Rectangle</code> so that it is 
	 * <code>h</code> units larger on both the left and right side, 
	 * and <code>v</code> units larger at both the top and bottom. 
	 * <p>
	 * The new <code>Rectangle</code> has (<code>x&nbsp;-&nbsp;h</code>, 
	 * <code>y&nbsp;-&nbsp;v</code>) as its top-left corner, a 
	 * width of 
	 * <code>width</code>&nbsp;<code>+</code>&nbsp;<code>2h</code>, 
	 * and a height of 
	 * <code>height</code>&nbsp;<code>+</code>&nbsp;<code>2v</code>. 
	 * <p>
	 * If negative values are supplied for <code>h</code> and 
	 * <code>v</code>, the size of the <code>Rectangle</code> 
	 * decreases accordingly. 
	 * The <code>grow</code> method does not check whether the resulting 
	 * values of <code>width</code> and <code>height</code> are 
	 * non-negative. 
	 * @param h the horizontal expansion
	 * @param v the vertical expansion
	 */
	public void grow(int h, int v) {
		x -= h;
		y -= v;
		width += h * 2;
		height += v * 2;
	}

	/**
	 * Determines whether or not this <code>Rectangle</code> is empty. A 
	 * <code>Rectangle</code> is empty if its width or its height is less 
	 * than or equal to zero. 
	 * @return     <code>true</code> if this <code>Rectangle</code> is empty; 
	 *             <code>false</code> otherwise.
	 */
	public boolean isEmpty() {
		return (width <= 0) || (height <= 0);
	}
	/**
	 * Checks whether two rectangles are equal.
	 * <p>
	 * The result is <code>true</code> if and only if the argument is not 
	 * <code>null</code> and is a <code>Rectangle</code> object that has the 
	 * same top-left corner, width, and height as this <code>Rectangle</code>. 
	 * @param obj the <code>Object</code> to compare with
	 *                this <code>Rectangle</code>
	 * @return    <code>true</code> if the objects are equal; 
	 *            <code>false</code> otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Rectangle) {
			Rectangle r = (Rectangle) obj;
			return ((x == r.x) && (y == r.y) && (width == r.width) && (height == r.height));
		}
		return super.equals(obj);
	}

	/**
	 * Debugging
	 * @return a String
	 */
	public String toString() {
		return getClass().getName() + "[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height + "]";
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

	/**
	 * Gets the x.
	 * @return Returns a int
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the x.
	 * @param x The x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Gets the y.
	 * @return Returns a int
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the y.
	 * @param y The y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

}
