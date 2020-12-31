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
 * Sphere.java
 *
 *
 * Created 23-dec-2003
 * @author Erik Duijs
 */
public class Sphere extends Quadric {

	/**
	 * Constructor
	 */
	public Sphere() {
		super();
	}

	/**
	 * draws a sphere of the given	radius centered	around the origin.
	 * The sphere is subdivided around the z axis into slices and along the z axis
	 * into stacks (similar to lines of longitude and latitude).
	 *
	 * If the orientation is set to GLU.OUTSIDE (with glu.quadricOrientation), then
	 * any normals generated point away from the center of the sphere. Otherwise,
	 * they point toward the center of the sphere.

	 * If texturing is turned on (with glu.quadricTexture), then texture
	 * coordinates are generated so that t ranges from 0.0 at z=-radius to 1.0 at
	 * z=radius (t increases linearly along longitudinal lines), and s ranges from
	 * 0.0 at the +y axis, to 0.25 at the +x axis, to 0.5 at the -y axis, to 0.75
	 * at the -x axis, and back to 1.0 at the +y axis.
	 */
	public void draw(float radius, int slices, int stacks) {
		// TODO

		float rho, drho, theta, dtheta;
		float x, y, z;
		float s, t, ds, dt;
		int i, j, imin, imax;
		boolean normals;
		float nsign;

		normals = super.normals != GLU_NONE;

		if (super.orientation == GLU_INSIDE) {
			nsign = -1.0f;
		} else {
			nsign = 1.0f;
		}

		drho = PI / stacks;
		dtheta = 2.0f * PI / slices;

		if (super.drawStyle == GLU_FILL) {
			if (!super.textureFlag) {
				// draw +Z end as a triangle fan
				glBegin(GL_TRIANGLE_FAN);
				glNormal3f(0.0f, 0.0f, 1.0f);
				glVertex3f(0.0f, 0.0f, nsign * radius);
				for (j = 0; j <= slices; j++) {
					theta = (j == slices) ? 0.0f : j * dtheta;
					x = -sin(theta) * sin(drho);
					y = cos(theta) * sin(drho);
					z = nsign * cos(drho);
					if (normals) {
						glNormal3f(x * nsign, y * nsign, z * nsign);
					}
					glVertex3f(x * radius, y * radius, z * radius);
				}
				glEnd();
			}

			ds = 1.0f / slices;
			dt = 1.0f / stacks;
			t = 1.0f; // because loop now runs from 0
			if (super.textureFlag) {
				imin = 0;
				imax = stacks;
			} else {
				imin = 1;
				imax = stacks - 1;
			}

			// draw intermediate stacks as quad strips
			for (i = imin; i < imax; i++) {
				rho = i * drho;
				glBegin(GL_QUAD_STRIP);
				s = 0.0f;
				for (j = 0; j <= slices; j++) {
					theta = (j == slices) ? 0.0f : j * dtheta;
					x = -sin(theta) * sin(rho);
					y = cos(theta) * sin(rho);
					z = nsign * cos(rho);
					if (normals) {
						glNormal3f(x * nsign, y * nsign, z * nsign);
					}
					TXTR_COORD(s, t);
					glVertex3f(x * radius, y * radius, z * radius);
					x = -sin(theta) * sin(rho + drho);
					y = cos(theta) * sin(rho + drho);
					z = nsign * cos(rho + drho);
					if (normals) {
						glNormal3f(x * nsign, y * nsign, z * nsign);
					}
					TXTR_COORD(s, t - dt);
					s += ds;
					glVertex3f(x * radius, y * radius, z * radius);
				}
				glEnd();
				t -= dt;
			}

			if (!super.textureFlag) {
				// draw -Z end as a triangle fan
				glBegin(GL_TRIANGLE_FAN);
				glNormal3f(0.0f, 0.0f, -1.0f);
				glVertex3f(0.0f, 0.0f, -radius * nsign);
				rho = PI - drho;
				s = 1.0f;
				for (j = slices; j >= 0; j--) {
					theta = (j == slices) ? 0.0f : j * dtheta;
					x = -sin(theta) * sin(rho);
					y = cos(theta) * sin(rho);
					z = nsign * cos(rho);
					if (normals)
						glNormal3f(x * nsign, y * nsign, z * nsign);
					s -= ds;
					glVertex3f(x * radius, y * radius, z * radius);
				}
				glEnd();
			}
		} else if (
			super.drawStyle == GLU_LINE
				|| super.drawStyle == GLU_SILHOUETTE) {
			// draw stack lines
			for (i = 1;
				i < stacks;
				i++) { // stack line at i==stacks-1 was missing here
				rho = i * drho;
				glBegin(GL_LINE_LOOP);
				for (j = 0; j < slices; j++) {
					theta = j * dtheta;
					x = cos(theta) * sin(rho);
					y = sin(theta) * sin(rho);
					z = cos(rho);
					if (normals)
						glNormal3f(x * nsign, y * nsign, z * nsign);
					glVertex3f(x * radius, y * radius, z * radius);
				}
				glEnd();
			}
			// draw slice lines
			for (j = 0; j < slices; j++) {
				theta = j * dtheta;
				glBegin(GL_LINE_STRIP);
				for (i = 0; i <= stacks; i++) {
					rho = i * drho;
					x = cos(theta) * sin(rho);
					y = sin(theta) * sin(rho);
					z = cos(rho);
					if (normals)
						glNormal3f(x * nsign, y * nsign, z * nsign);
					glVertex3f(x * radius, y * radius, z * radius);
				}
				glEnd();
			}
		} else if (super.drawStyle == GLU_POINT) {
			// top and bottom-most points
			glBegin(GL_POINTS);
			if (normals)
				glNormal3f(0.0f, 0.0f, nsign);
			glVertex3f(0.0f, 0.0f, radius);
			if (normals)
				glNormal3f(0.0f, 0.0f, -nsign);
			glVertex3f(0.0f, 0.0f, -radius);

			// loop over stacks
			for (i = 1; i < stacks - 1; i++) {
				rho = i * drho;
				for (j = 0; j < slices; j++) {
					theta = j * dtheta;
					x = cos(theta) * sin(rho);
					y = sin(theta) * sin(rho);
					z = cos(rho);
					if (normals)
						glNormal3f(x * nsign, y * nsign, z * nsign);
					glVertex3f(x * radius, y * radius, z * radius);
				}
			}
			glEnd();
		}
	}

}
