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
 * PartialDisk.java
 *
 *
 * Created 23-dec-2003
 *
 * @author Erik Duijs
 */
public class PartialDisk extends Quadric {

	private static final int CACHE_SIZE = 240;

	/**
	 * Constructor for PartialDisk.
	 */
	public PartialDisk() {
		super();
	}

	/**
	 * renders a partial disk on the z=0 plane. A partial disk is similar to a
	 * full disk, except that only the subset of the disk from startAngle
	 * through startAngle + sweepAngle is included (where 0 degrees is along
	 * the +y axis, 90 degrees along the +x axis, 180 along the -y axis, and
	 * 270 along the -x axis).
	 *
	 * The partial disk has a radius of outerRadius, and contains a concentric
	 * circular hole with a radius of innerRadius. If innerRadius is zero, then
	 * no hole is generated. The partial disk is subdivided around the z axis
	 * into slices (like pizza slices), and also about the z axis into rings
	 * (as specified by slices and loops, respectively).
	 *
	 * With respect to orientation, the +z side of the partial disk is
	 * considered to be outside (see gluQuadricOrientation). This means that if
	 * the orientation is set to GLU.GLU_OUTSIDE, then any normals generated point
	 * along the +z axis. Otherwise, they point along the -z axis.
	 *
	 * If texturing is turned on (with gluQuadricTexture), texture coordinates
	 * are generated linearly such that where r=outerRadius, the value at (r, 0, 0)
	 * is (1, 0.5), at (0, r, 0) it is (0.5, 1), at (-r, 0, 0) it is (0, 0.5),
	 * and at (0, -r, 0) it is (0.5, 0).
	 */
	public void draw(
		float innerRadius,
		float outerRadius,
		int slices,
		int loops,
		float startAngle,
		float sweepAngle) {

		int i, j;
		float[] sinCache = new float[CACHE_SIZE];
		float[] cosCache = new float[CACHE_SIZE];
		float angle;
		float sintemp, costemp;
		float deltaRadius;
		float radiusLow, radiusHigh;
		float texLow = 0, texHigh = 0;
		float angleOffset;
		int slices2;
		int finish;

		if (slices >= CACHE_SIZE)
			slices = CACHE_SIZE - 1;
		if (slices < 2
			|| loops < 1
			|| outerRadius <= 0.0f
			|| innerRadius < 0.0f
			|| innerRadius > outerRadius) {
			//gluQuadricError(qobj, GLU.GLU_INVALID_VALUE);
			System.err.println("PartialDisk: GLU_INVALID_VALUE");
			return;
		}

		if (sweepAngle < -360.0f)
			sweepAngle = 360.0f;
		if (sweepAngle > 360.0f)
			sweepAngle = 360.0f;
		if (sweepAngle < 0) {
			startAngle += sweepAngle;
			sweepAngle = -sweepAngle;
		}

		if (sweepAngle == 360.0f) {
			slices2 = slices;
		} else {
			slices2 = slices + 1;
		}

		/* Compute length (needed for normal calculations) */
		deltaRadius = outerRadius - innerRadius;

		/* Cache is the vertex locations cache */

		angleOffset = startAngle / 180.0f * PI;
		for (i = 0; i <= slices; i++) {
			angle = angleOffset + ((PI * sweepAngle) / 180.0f) * i / slices;
			sinCache[i] = sin(angle);
			cosCache[i] = cos(angle);
		}

		if (sweepAngle == 360.0f) {
			sinCache[slices] = sinCache[0];
			cosCache[slices] = cosCache[0];
		}

		switch (super.normals) {
			case GLU_FLAT :
			case GLU_SMOOTH :
				if (super.orientation == GLU_OUTSIDE) {
					glNormal3f(0.0f, 0.0f, 1.0f);
				} else {
					glNormal3f(0.0f, 0.0f, -1.0f);
				}
				break;
			default :
			case GLU_NONE :
				break;
		}

		switch (super.drawStyle) {
			case GLU_FILL :
				if (innerRadius == .0f) {
					finish = loops - 1;
					/* Triangle strip for inner polygons */
					glBegin(GL_TRIANGLE_FAN);
					if (super.textureFlag) {
						glTexCoord2f(0.5f, 0.5f);
					}
					glVertex3f(0.0f, 0.0f, 0.0f);
					radiusLow = outerRadius - deltaRadius * ((float) (loops - 1) / loops);
					if (super.textureFlag) {
						texLow = radiusLow / outerRadius / 2;
					}

					if (super.orientation == GLU_OUTSIDE) {
						for (i = slices; i >= 0; i--) {
							if (super.textureFlag) {
								glTexCoord2f(
									texLow * sinCache[i] + 0.5f,
									texLow * cosCache[i] + 0.5f);
							}
							glVertex3f(radiusLow * sinCache[i], radiusLow * cosCache[i], 0.0f);
						}
					} else {
						for (i = 0; i <= slices; i++) {
							if (super.textureFlag) {
								glTexCoord2f(
									texLow * sinCache[i] + 0.5f,
									texLow * cosCache[i] + 0.5f);
							}
							glVertex3f(radiusLow * sinCache[i], radiusLow * cosCache[i], 0.0f);
						}
					}
					glEnd();
				} else {
					finish = loops;
				}
				for (j = 0; j < finish; j++) {
					radiusLow = outerRadius - deltaRadius * ((float) j / loops);
					radiusHigh = outerRadius - deltaRadius * ((float) (j + 1) / loops);
					if (super.textureFlag) {
						texLow = radiusLow / outerRadius / 2;
						texHigh = radiusHigh / outerRadius / 2;
					}

					glBegin(GL_QUAD_STRIP);
					for (i = 0; i <= slices; i++) {
						if (super.orientation == GLU_OUTSIDE) {
							if (super.textureFlag) {
								glTexCoord2f(
									texLow * sinCache[i] + 0.5f,
									texLow * cosCache[i] + 0.5f);
							}
							glVertex3f(radiusLow * sinCache[i], radiusLow * cosCache[i], 0.0f);

							if (super.textureFlag) {
								glTexCoord2f(
									texHigh * sinCache[i] + 0.5f,
									texHigh * cosCache[i] + 0.5f);
							}
							glVertex3f(
								radiusHigh * sinCache[i],
								radiusHigh * cosCache[i],
								0.0f);
						} else {
							if (super.textureFlag) {
								glTexCoord2f(
									texHigh * sinCache[i] + 0.5f,
									texHigh * cosCache[i] + 0.5f);
							}
							glVertex3f(
								radiusHigh * sinCache[i],
								radiusHigh * cosCache[i],
								0.0f);

							if (super.textureFlag) {
								glTexCoord2f(
									texLow * sinCache[i] + 0.5f,
									texLow * cosCache[i] + 0.5f);
							}
							glVertex3f(radiusLow * sinCache[i], radiusLow * cosCache[i], 0.0f);
						}
					}
					glEnd();
				}
				break;
			case GLU_POINT :
				glBegin(GL_POINTS);
				for (i = 0; i < slices2; i++) {
					sintemp = sinCache[i];
					costemp = cosCache[i];
					for (j = 0; j <= loops; j++) {
						radiusLow = outerRadius - deltaRadius * ((float) j / loops);

						if (super.textureFlag) {
							texLow = radiusLow / outerRadius / 2;

							glTexCoord2f(
								texLow * sinCache[i] + 0.5f,
								texLow * cosCache[i] + 0.5f);
						}
						glVertex3f(radiusLow * sintemp, radiusLow * costemp, 0.0f);
					}
				}
				glEnd();
				break;
			case GLU_LINE :
				if (innerRadius == outerRadius) {
					glBegin(GL_LINE_STRIP);

					for (i = 0; i <= slices; i++) {
						if (super.textureFlag) {
							glTexCoord2f(sinCache[i] / 2 + 0.5f, cosCache[i] / 2 + 0.5f);
						}
						glVertex3f(innerRadius * sinCache[i], innerRadius * cosCache[i], 0.0f);
					}
					glEnd();
					break;
				}
				for (j = 0; j <= loops; j++) {
					radiusLow = outerRadius - deltaRadius * ((float) j / loops);
					if (super.textureFlag) {
						texLow = radiusLow / outerRadius / 2;
					}

					glBegin(GL_LINE_STRIP);
					for (i = 0; i <= slices; i++) {
						if (super.textureFlag) {
							glTexCoord2f(
								texLow * sinCache[i] + 0.5f,
								texLow * cosCache[i] + 0.5f);
						}
						glVertex3f(radiusLow * sinCache[i], radiusLow * cosCache[i], 0.0f);
					}
					glEnd();
				}
				for (i = 0; i < slices2; i++) {
					sintemp = sinCache[i];
					costemp = cosCache[i];
					glBegin(GL_LINE_STRIP);
					for (j = 0; j <= loops; j++) {
						radiusLow = outerRadius - deltaRadius * ((float) j / loops);
						if (super.textureFlag) {
							texLow = radiusLow / outerRadius / 2;
						}

						if (super.textureFlag) {
							glTexCoord2f(
								texLow * sinCache[i] + 0.5f,
								texLow * cosCache[i] + 0.5f);
						}
						glVertex3f(radiusLow * sintemp, radiusLow * costemp, 0.0f);
					}
					glEnd();
				}
				break;
			case GLU_SILHOUETTE :
				if (sweepAngle < 360.0f) {
					for (i = 0; i <= slices; i += slices) {
						sintemp = sinCache[i];
						costemp = cosCache[i];
						glBegin(GL_LINE_STRIP);
						for (j = 0; j <= loops; j++) {
							radiusLow = outerRadius - deltaRadius * ((float) j / loops);

							if (super.textureFlag) {
								texLow = radiusLow / outerRadius / 2;
								glTexCoord2f(
									texLow * sinCache[i] + 0.5f,
									texLow * cosCache[i] + 0.5f);
							}
							glVertex3f(radiusLow * sintemp, radiusLow * costemp, 0.0f);
						}
						glEnd();
					}
				}
				for (j = 0; j <= loops; j += loops) {
					radiusLow = outerRadius - deltaRadius * ((float) j / loops);
					if (super.textureFlag) {
						texLow = radiusLow / outerRadius / 2;
					}

					glBegin(GL_LINE_STRIP);
					for (i = 0; i <= slices; i++) {
						if (super.textureFlag) {
							glTexCoord2f(
								texLow * sinCache[i] + 0.5f,
								texLow * cosCache[i] + 0.5f);
						}
						glVertex3f(radiusLow * sinCache[i], radiusLow * cosCache[i], 0.0f);
					}
					glEnd();
					if (innerRadius == outerRadius)
						break;
				}
				break;
			default :
				break;
		}
	}
}
