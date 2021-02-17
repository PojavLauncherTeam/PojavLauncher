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
 * Disk.java
 *
 *
 * Created 23-dec-2003
 * @author Erik Duijs
 */
public class Disk extends Quadric {

	/**
	 * Constructor for Disk.
	 */
	public Disk() {
		super();
	}

    /**
     * renders a disk on the z = 0  plane.  The disk has a radius of
     * outerRadius, and contains a concentric circular hole with a radius of
     * innerRadius. If innerRadius is 0, then no hole is generated. The disk is
     * subdivided around the z axis into slices (like pizza slices), and also
     * about the z axis into rings (as specified by slices and loops,
     * respectively).
     *
     * With respect to orientation, the +z side of the disk is considered to be
     * "outside" (see glu.quadricOrientation).  This means that if the orientation
     * is set to GLU.OUTSIDE, then any normals generated point along the +z axis.
     * Otherwise, they point along the -z axis.
     *
     * If texturing is turned on (with glu.quadricTexture), texture coordinates are
     * generated linearly such that where r=outerRadius, the value at (r, 0, 0) is
     * (1, 0.5), at (0, r, 0) it is (0.5, 1), at (-r, 0, 0) it is (0, 0.5), and at
     * (0, -r, 0) it is (0.5, 0).
     */
	public void draw(float innerRadius, float outerRadius, int slices, int loops)
	{
	   float da, dr;

	   /* Normal vectors */
	   if (super.normals != GLU_NONE) {
	      if (super.orientation == GLU_OUTSIDE) {
		 glNormal3f(0.0f, 0.0f, +1.0f);
	      }
	      else {
		 glNormal3f(0.0f, 0.0f, -1.0f);
	      }
	   }

	   da = 2.0f * PI / slices;
	   dr = (outerRadius - innerRadius) /  loops;

	   switch (super.drawStyle) {
	   case GLU_FILL:
	      {
		 /* texture of a gluDisk is a cut out of the texture unit square
		  * x, y in [-outerRadius, +outerRadius]; s, t in [0, 1]
		  * (linear mapping)
		  */
		 float dtc = 2.0f * outerRadius;
		 float sa, ca;
		 float r1 = innerRadius;
		 int l;
		 for (l = 0; l < loops; l++) {
		    float r2 = r1 + dr;
		    if (super.orientation == GLU_OUTSIDE) {
		       int s;
		       glBegin(GL_QUAD_STRIP);
		       for (s = 0; s <= slices; s++) {
			  float a;
			  if (s == slices)
			     a = 0.0f;
			  else
			     a = s * da;
			  sa = sin(a);
			  ca = cos(a);
			  TXTR_COORD(0.5f + sa * r2 / dtc, 0.5f + ca * r2 / dtc);
			  glVertex2f(r2 * sa, r2 * ca);
			  TXTR_COORD(0.5f + sa * r1 / dtc, 0.5f + ca * r1 / dtc);
			  glVertex2f(r1 * sa, r1 * ca);
		       }
		       glEnd();
		    }
		    else {
		       int s;
		       glBegin(GL_QUAD_STRIP);
		       for (s = slices; s >= 0; s--) {
			  float a;
			  if (s == slices)
			     a = 0.0f;
			  else
			     a = s * da;
			  sa = sin(a);
			  ca = cos(a);
			  TXTR_COORD(0.5f - sa * r2 / dtc, 0.5f + ca * r2 / dtc);
			  glVertex2f(r2 * sa, r2 * ca);
			  TXTR_COORD(0.5f - sa * r1 / dtc, 0.5f + ca * r1 / dtc);
			  glVertex2f(r1 * sa, r1 * ca);
		       }
		       glEnd();
		    }
		    r1 = r2;
		 }
		 break;
	      }
	   case GLU_LINE:
	      {
		 int l, s;
		 /* draw loops */
		 for (l = 0; l <= loops; l++) {
		    float r = innerRadius + l * dr;
		    glBegin(GL_LINE_LOOP);
		    for (s = 0; s < slices; s++) {
		       float a = s * da;
		       glVertex2f(r * sin(a), r * cos(a));
		    }
		    glEnd();
		 }
		 /* draw spokes */
		 for (s = 0; s < slices; s++) {
		    float a = s * da;
		    float x = sin(a);
		    float y = cos(a);
		    glBegin(GL_LINE_STRIP);
		    for (l = 0; l <= loops; l++) {
		       float r = innerRadius + l * dr;
		       glVertex2f(r * x, r * y);
		    }
		    glEnd();
		 }
		 break;
	      }
	   case GLU_POINT:
	      {
		 int s;
		 glBegin(GL_POINTS);
		 for (s = 0; s < slices; s++) {
		    float a = s * da;
		    float x = sin(a);
		    float y = cos(a);
		    int l;
		    for (l = 0; l <= loops; l++) {
		       float r = innerRadius * l * dr;
		       glVertex2f(r * x, r * y);
		    }
		 }
		 glEnd();
		 break;
	      }
	   case GLU_SILHOUETTE:
	      {
		 if (innerRadius != 0.0) {
		    float a;
		    glBegin(GL_LINE_LOOP);
		    for (a = 0.0f; a < 2.0 * PI; a += da) {
		       float x = innerRadius * sin(a);
		       float y = innerRadius * cos(a);
		       glVertex2f(x, y);
		    }
		    glEnd();
		 }
		 {
		    float a;
		    glBegin(GL_LINE_LOOP);
		    for (a = 0; a < 2.0f * PI; a += da) {
		       float x = outerRadius * sin(a);
		       float y = outerRadius * cos(a);
		       glVertex2f(x, y);
		    }
		    glEnd();
		 }
		 break;
	      }
	   default:
	      return;
	   }
	}

}
