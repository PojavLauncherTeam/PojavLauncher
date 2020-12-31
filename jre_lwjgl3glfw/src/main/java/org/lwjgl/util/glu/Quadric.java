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
import static org.lwjgl.util.glu.GLU.*;

/**
 * Quadric.java
 *
 *
 * Created 22-dec-2003
 * @author Erik Duijs
 */
public class Quadric {

	protected int drawStyle;
	protected int orientation;
	protected boolean textureFlag;
	protected int normals;

	/**
	 * Constructor for Quadric.
	 */
	public Quadric() {
		super();

		drawStyle = GLU_FILL;
		orientation = GLU_OUTSIDE;
		textureFlag = false;
		normals = GLU_SMOOTH;
	}

	/**
	 * Call glNormal3f after scaling normal to unit length.
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	protected void normal3f(float x, float y, float z) {
	   float mag;

	   mag = (float)Math.sqrt(x * x + y * y + z * z);
	   if (mag > 0.00001F) {
	      x /= mag;
	      y /= mag;
	      z /= mag;
	   }
	   glNormal3f(x, y, z);
	}

	/**
     * specifies the draw style for quadrics.
     *
     * The legal values are as follows:
     *
     * GLU.FILL:       Quadrics are rendered with polygon primitives. The polygons
     *                 are drawn in a counterclockwise fashion with respect to
     *                 their normals (as defined with glu.quadricOrientation).
     *
     * GLU.LINE:       Quadrics are rendered as a set of lines.
     *
     * GLU.SILHOUETTE: Quadrics are rendered as a set of lines, except that edges
     * 		   separating coplanar faces will not be drawn.
     *
     * GLU.POINT:       Quadrics are rendered as a set of points.
     *
	 * @param drawStyle The drawStyle to set
	 */
	public void setDrawStyle(int drawStyle) {
		this.drawStyle = drawStyle;
	}

    /**
     * specifies what kind	of normals are desired for quadrics.
     * The legal values	are as follows:
     *
     * GLU.NONE:     No normals are generated.
     *
     * GLU.FLAT:     One normal is generated for every facet of a quadric.
     *
     * GLU.SMOOTH:   One normal is generated for every vertex of a quadric.  This
     *               is the default.
     *
	 * @param normals The normals to set
	 */
	public void setNormals(int normals) {
		this.normals = normals;
	}

    /**
     * specifies what kind of orientation is desired for.
     * The orientation	values are as follows:
     *
     * GLU.OUTSIDE:  Quadrics are drawn with normals pointing outward.
     *
     * GLU.INSIDE:   Normals point inward. The default is GLU.OUTSIDE.
     *
     * Note that the interpretation of outward and inward depends on the quadric
     * being drawn.
     *
	 * @param orientation The orientation to set
	 */
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

    /**
     * specifies if texture coordinates should be generated for
     * quadrics rendered with qobj. If the value of textureCoords is true,
     * then texture coordinates are generated, and if textureCoords is false,
     * they are not.. The default is false.
     *
     * The manner in which texture coordinates are generated depends upon the
     * specific quadric rendered.
     *
	 * @param textureFlag The textureFlag to set
	 */
	public void setTextureFlag(boolean textureFlag) {
		this.textureFlag = textureFlag;
	}


	/**
	 * Returns the drawStyle.
	 * @return int
	 */
	public int getDrawStyle() {
		return drawStyle;
	}

	/**
	 * Returns the normals.
	 * @return int
	 */
	public int getNormals() {
		return normals;
	}

	/**
	 * Returns the orientation.
	 * @return int
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * Returns the textureFlag.
	 * @return boolean
	 */
	public boolean getTextureFlag() {
		return textureFlag;
	}

	protected void TXTR_COORD(float x, float y) {
		if (textureFlag) glTexCoord2f(x,y);
	}


	protected float sin(float r) {
		return (float)Math.sin(r);
	}

	protected float cos(float r) {
		return (float)Math.cos(r);
	}

}
