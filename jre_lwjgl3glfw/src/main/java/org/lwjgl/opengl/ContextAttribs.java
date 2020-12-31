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

public final class ContextAttribs {

	public ContextAttribs() {
		
	}

	public ContextAttribs(final int majorVersion, final int minorVersion) {
		
	}

	public int getMajorVersion() {
		return 0;
	}

	public int getMinorVersion() {
		return 0;
	}

	public int getLayerPlane() {
		return 0;
	}

	public boolean isDebug() {
		return false;
	}

	public boolean isForwardCompatible() {
		return false;
	}

	public boolean isProfileCore() {
		return false;
	}

	public boolean isProfileCompatibility() {
		return false;
	}

	public boolean isProfileES() {
		return false;
	}

	public ContextAttribs withLayer(final int layerPlane) {
		return null;
	}

	public ContextAttribs withDebug(final boolean debug) {
		return null;
	}

	public ContextAttribs withForwardCompatible(final boolean forwardCompatible) {
		return null;
	}

	public ContextAttribs withProfileCore(final boolean profileCore) {
		return null;
	}

	public ContextAttribs withProfileCompatibility(final boolean profileCompatibility) {
		return null;
	}

	public ContextAttribs withProfileES(final boolean profileES) {
		return null;
	}

	public ContextAttribs withLoseContextOnReset(final boolean loseContextOnReset) {
		return null;
	}

	public ContextAttribs withContextResetIsolation(final boolean contextResetIsolation) {
		return null;
	}

	public String toString() {
		return null;
	}

}
