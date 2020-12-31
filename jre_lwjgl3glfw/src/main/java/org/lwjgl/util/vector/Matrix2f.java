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
package org.lwjgl.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;

/**
 *
 * Holds a 2x2 matrix
 *
 * @author cix_foo <cix_foo@users.sourceforge.net>
 * @version $Revision: 3799 $
 * $Id: Matrix2f.java 3799 2012-09-12 11:29:40Z kappa1 $
 */

public class Matrix2f extends Matrix implements Serializable {

	private static final long serialVersionUID = 1L;

	public float m00, m01, m10, m11;

	/**
	 * Constructor for Matrix2f. The matrix is initialised to the identity.
	 */
	public Matrix2f() {
		setIdentity();
	}

	/**
	 * Constructor
	 */
	public Matrix2f(Matrix2f src) {
		load(src);
	}

	/**
	 * Load from another matrix
	 * @param src The source matrix
	 * @return this
	 */
	public Matrix2f load(Matrix2f src) {
		return load(src, this);
	}

	/**
	 * Copy the source matrix to the destination matrix.
	 * @param src The source matrix
	 * @param dest The destination matrix, or null if a new one should be created.
	 * @return The copied matrix
	 */
	public static Matrix2f load(Matrix2f src, Matrix2f dest) {
		if (dest == null)
			dest = new Matrix2f();

		dest.m00 = src.m00;
		dest.m01 = src.m01;
		dest.m10 = src.m10;
		dest.m11 = src.m11;

		return dest;
	}

	/**
	 * Load from a float buffer. The buffer stores the matrix in column major
	 * (OpenGL) order.
	 *
	 * @param buf A float buffer to read from
	 * @return this
	 */
	public Matrix load(FloatBuffer buf) {

		m00 = buf.get();
		m01 = buf.get();
		m10 = buf.get();
		m11 = buf.get();

		return this;
	}

	/**
	 * Load from a float buffer. The buffer stores the matrix in row major
	 * (mathematical) order.
	 *
	 * @param buf A float buffer to read from
	 * @return this
	 */
	public Matrix loadTranspose(FloatBuffer buf) {

		m00 = buf.get();
		m10 = buf.get();
		m01 = buf.get();
		m11 = buf.get();

		return this;
	}

	/**
	 * Store this matrix in a float buffer. The matrix is stored in column
	 * major (openGL) order.
	 * @param buf The buffer to store this matrix in
	 */
	public Matrix store(FloatBuffer buf) {
		buf.put(m00);
		buf.put(m01);
		buf.put(m10);
		buf.put(m11);
		return this;
	}

	/**
	 * Store this matrix in a float buffer. The matrix is stored in row
	 * major (maths) order.
	 * @param buf The buffer to store this matrix in
	 */
	public Matrix storeTranspose(FloatBuffer buf) {
		buf.put(m00);
		buf.put(m10);
		buf.put(m01);
		buf.put(m11);
		return this;
	}



	/**
	 * Add two matrices together and place the result in a third matrix.
	 * @param left The left source matrix
	 * @param right The right source matrix
	 * @param dest The destination matrix, or null if a new one is to be created
	 * @return the destination matrix
	 */
	public static Matrix2f add(Matrix2f left, Matrix2f right, Matrix2f dest) {
		if (dest == null)
			dest = new Matrix2f();

		dest.m00 = left.m00 + right.m00;
		dest.m01 = left.m01 + right.m01;
		dest.m10 = left.m10 + right.m10;
		dest.m11 = left.m11 + right.m11;

		return dest;
	}

	/**
	 * Subtract the right matrix from the left and place the result in a third matrix.
	 * @param left The left source matrix
	 * @param right The right source matrix
	 * @param dest The destination matrix, or null if a new one is to be created
	 * @return the destination matrix
	 */
	public static Matrix2f sub(Matrix2f left, Matrix2f right, Matrix2f dest) {
		if (dest == null)
			dest = new Matrix2f();

		dest.m00 = left.m00 - right.m00;
		dest.m01 = left.m01 - right.m01;
		dest.m10 = left.m10 - right.m10;
		dest.m11 = left.m11 - right.m11;

		return dest;
	}

	/**
	 * Multiply the right matrix by the left and place the result in a third matrix.
	 * @param left The left source matrix
	 * @param right The right source matrix
	 * @param dest The destination matrix, or null if a new one is to be created
	 * @return the destination matrix
	 */
	public static Matrix2f mul(Matrix2f left, Matrix2f right, Matrix2f dest) {
		if (dest == null)
			dest = new Matrix2f();

		float m00 = left.m00 * right.m00 + left.m10 * right.m01;
		float m01 = left.m01 * right.m00 + left.m11 * right.m01;
		float m10 = left.m00 * right.m10 + left.m10 * right.m11;
		float m11 = left.m01 * right.m10 + left.m11 * right.m11;

		dest.m00 = m00;
		dest.m01 = m01;
		dest.m10 = m10;
		dest.m11 = m11;

		return dest;
	}

	/**
	 * Transform a Vector by a matrix and return the result in a destination
	 * vector.
	 * @param left The left matrix
	 * @param right The right vector
	 * @param dest The destination vector, or null if a new one is to be created
	 * @return the destination vector
	 */
	public static Vector2f transform(Matrix2f left, Vector2f right, Vector2f dest) {
		if (dest == null)
			dest = new Vector2f();

		float x = left.m00 * right.x + left.m10 * right.y;
		float y = left.m01 * right.x + left.m11 * right.y;

		dest.x = x;
		dest.y = y;

		return dest;
	}

	/**
	 * Transpose this matrix
	 * @return this
	 */
	public Matrix transpose() {
		return transpose(this);
	}

	/**
	 * Transpose this matrix and place the result in another matrix.
	 * @param dest The destination matrix or null if a new matrix is to be created
	 * @return the transposed matrix
	 */
	public Matrix2f transpose(Matrix2f dest) {
		return transpose(this, dest);
	}

	/**
	 * Transpose the source matrix and place the result in the destination matrix.
	 * @param src The source matrix or null if a new matrix is to be created
	 * @param dest The destination matrix or null if a new matrix is to be created
	 * @return the transposed matrix
	 */
	public static Matrix2f transpose(Matrix2f src, Matrix2f dest) {
		if (dest == null)
			dest = new Matrix2f();

		float m01 = src.m10;
		float m10 = src.m01;

		dest.m01 = m01;
		dest.m10 = m10;

		return dest;
	}

	/**
	 * Invert this matrix
	 * @return this if successful, null otherwise
	 */
	public Matrix invert() {
		return invert(this, this);
	}

	/**
	 * Invert the source matrix and place the result in the destination matrix.
	 * @param src The source matrix to be inverted
	 * @param dest The destination matrix or null if a new matrix is to be created
	 * @return The inverted matrix, or null if source can't be reverted.
	 */
	public static Matrix2f invert(Matrix2f src, Matrix2f dest) {
		/*
		 *inv(A) = 1/det(A) * adj(A);
		 */

		float determinant = src.determinant();
		if (determinant != 0) {
			if (dest == null)
				dest = new Matrix2f();
			float determinant_inv = 1f/determinant;
			float t00 =  src.m11*determinant_inv;
			float t01 = -src.m01*determinant_inv;
			float t11 =  src.m00*determinant_inv;
			float t10 = -src.m10*determinant_inv;

			dest.m00 = t00;
			dest.m01 = t01;
			dest.m10 = t10;
			dest.m11 = t11;
			return dest;
		} else
			return null;
	}

	/**
	 * Returns a string representation of this matrix
	 */
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(m00).append(' ').append(m10).append(' ').append('\n');
		buf.append(m01).append(' ').append(m11).append(' ').append('\n');
		return buf.toString();
	}

	/**
	 * Negate this matrix
	 * @return this
	 */
	public Matrix negate() {
		return negate(this);
	}

	/**
	 * Negate this matrix and stash the result in another matrix.
	 * @param dest The destination matrix, or null if a new matrix is to be created
	 * @return the negated matrix
	 */
	public Matrix2f negate(Matrix2f dest) {
		return negate(this, dest);
	}

	/**
	 * Negate the source matrix and stash the result in the destination matrix.
	 * @param src The source matrix to be negated
	 * @param dest The destination matrix, or null if a new matrix is to be created
	 * @return the negated matrix
	 */
	public static Matrix2f negate(Matrix2f src, Matrix2f dest) {
		if (dest == null)
			dest = new Matrix2f();

		dest.m00 = -src.m00;
		dest.m01 = -src.m01;
		dest.m10 = -src.m10;
		dest.m11 = -src.m11;

		return dest;
	}

	/**
	 * Set this matrix to be the identity matrix.
	 * @return this
	 */
	public Matrix setIdentity() {
		return setIdentity(this);
	}

	/**
	 * Set the source matrix to be the identity matrix.
	 * @param src The matrix to set to the identity.
	 * @return The source matrix
	 */
	public static Matrix2f setIdentity(Matrix2f src) {
		src.m00 = 1.0f;
		src.m01 = 0.0f;
		src.m10 = 0.0f;
		src.m11 = 1.0f;
		return src;
	}

	/**
	 * Set this matrix to 0.
	 * @return this
	 */
	public Matrix setZero() {
		return setZero(this);
	}

	public static Matrix2f setZero(Matrix2f src) {
		src.m00 = 0.0f;
		src.m01 = 0.0f;
		src.m10 = 0.0f;
		src.m11 = 0.0f;
		return src;
	}

	/* (non-Javadoc)
	 * @see org.lwjgl.vector.Matrix#determinant()
	 */
	public float determinant() {
		return m00 * m11 - m01*m10;
	}
}
