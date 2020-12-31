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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.DisplayMode;

/**
 * Display initialization utility, that can be used to find display modes and pick
 * one for you based on your criteria.
 * @author $Author: spasi $
 * @version $Revision: 3418 $
 * $Id: Display.java 3418 2010-09-28 21:11:35Z spasi $
 */
public final class Display {

	private static final boolean DEBUG = false;

	/**
	 * Determine the available display modes that match the specified minimum and maximum criteria.
	 * If any given criterium is specified as -1 then it is ignored.
	 *
	 * @param minWidth the minimum display resolution in pixels
	 * @param minHeight the minimum display resolution in pixels
	 * @param maxWidth the maximum display resolution in pixels
	 * @param maxHeight the maximum display resolution in pixels
	 * @param minBPP the minimum bit depth per pixel
	 * @param maxBPP the maximum bit depth per pixel
	 * @param minFreq the minimum display frequency in Hz
	 * @param maxFreq the maximum display frequency in Hz
	 * @return an array of matching display modes
	 */
	public static DisplayMode[] getAvailableDisplayModes(int minWidth, int minHeight, int maxWidth, int maxHeight, int minBPP, int maxBPP,
		int minFreq, int maxFreq) throws LWJGLException
	{
		// First get the available display modes
		DisplayMode[] modes = org.lwjgl.opengl.Display.getAvailableDisplayModes();

		if (LWJGLUtil.DEBUG || DEBUG) {
			System.out.println("Available screen modes:");
			for ( DisplayMode mode : modes ) {
				System.out.println(mode);
			}
		}

		ArrayList<DisplayMode> matches = new ArrayList<DisplayMode>(modes.length);

		for (int i = 0; i < modes.length; i ++) {
			assert modes[i] != null : ""+i+" "+modes.length;
			if (minWidth != -1 && modes[i].getWidth() < minWidth)
				continue;
			if (maxWidth != -1 && modes[i].getWidth() > maxWidth)
				continue;
			if (minHeight != -1 && modes[i].getHeight() < minHeight)
				continue;
			if (maxHeight != -1 && modes[i].getHeight() > maxHeight)
				continue;
			if (minBPP != -1 && modes[i].getBitsPerPixel() < minBPP)
				continue;
			if (maxBPP != -1 && modes[i].getBitsPerPixel() > maxBPP)
				continue;
			//if (modes[i].bpp == 24)
			//	continue;
			if (modes[i].getFrequency() != 0) {
				if (minFreq != -1 && modes[i].getFrequency() < minFreq)
					continue;
				if (maxFreq != -1 && modes[i].getFrequency() > maxFreq)
					continue;
			}
			matches.add(modes[i]);
		}

		DisplayMode[] ret = new DisplayMode[matches.size()];
		matches.toArray(ret);
		if (LWJGLUtil.DEBUG && DEBUG) {
			System.out.println("Filtered screen modes:");
			for ( DisplayMode mode : ret ) {
				System.out.println(mode);
			}
		}

		return ret;
	}

	/**
	 * Create the display by choosing from a list of display modes based on an order of preference.
	 * You must supply a list of allowable display modes, probably by calling getAvailableDisplayModes(),
	 * and an array with the order in which you would like them sorted in descending order.
	 * This method attempts to create the topmost display mode; if that fails, it will try the next one,
	 * and so on, until there are no modes left. If no mode is set at the end, an exception is thrown.
	 * @param dm a list of display modes to choose from
	 * @param param the names of the DisplayMode fields in the order in which you would like them sorted.
	 * @return the chosen display mode
	 * @throws NoSuchFieldException if one of the params is not a field in DisplayMode
	 * @throws Exception if no display mode could be set
	 * @see org.lwjgl.opengl.DisplayMode
	 */
	public static DisplayMode setDisplayMode(DisplayMode[] dm, final String[] param) throws Exception {

		class FieldAccessor {
			final String fieldName;
			final int order;
			final int preferred;
			final boolean usePreferred;
			FieldAccessor(String fieldName, int order, int preferred, boolean usePreferred) {
				this.fieldName = fieldName;
				this.order = order;
				this.preferred = preferred;
				this.usePreferred = usePreferred;
			}
			int getInt(DisplayMode mode) {
				if ("width".equals(fieldName)) {
					return mode.getWidth();
				}
				if ("height".equals(fieldName)) {
					return mode.getHeight();
				}
				if ("freq".equals(fieldName)) {
					return mode.getFrequency();
				}
				if ("bpp".equals(fieldName)) {
					return mode.getBitsPerPixel();
				}
				throw new IllegalArgumentException("Unknown field "+fieldName);
			}
		}

		class Sorter implements Comparator<DisplayMode> {

			final FieldAccessor[] accessors;

			Sorter() {
				accessors = new FieldAccessor[param.length];
				for (int i = 0; i < accessors.length; i ++) {
					int idx = param[i].indexOf('=');
					if (idx > 0) {
						accessors[i] = new FieldAccessor(param[i].substring(0, idx), 0, Integer.parseInt(param[i].substring(idx + 1, param[i].length())), true);
					} else if (param[i].charAt(0) == '-') {
						accessors[i] = new FieldAccessor(param[i].substring(1), -1, 0, false);
					} else {
						accessors[i] = new FieldAccessor(param[i], 1, 0, false);
					}
				}
			}

			/**
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			public int compare(DisplayMode dm1, DisplayMode dm2) {
				for ( FieldAccessor accessor : accessors ) {
					int f1 = accessor.getInt(dm1);
					int f2 = accessor.getInt(dm2);

					if ( accessor.usePreferred && f1 != f2 ) {
						if ( f1 == accessor.preferred )
							return -1;
						else if ( f2 == accessor.preferred )
							return 1;
						else {
							// Score according to the difference between the values
							int absf1 = Math.abs(f1 - accessor.preferred);
							int absf2 = Math.abs(f2 - accessor.preferred);
							if ( absf1 < absf2 )
								return -1;
							else if ( absf1 > absf2 )
								return 1;
							else
								continue;
						}
					} else if ( f1 < f2 )
						return accessor.order;
					else if ( f1 == f2 )
						continue;
					else
						return -accessor.order;
				}

				return 0;
			}
		}

		// Sort the display modes
		Arrays.sort(dm, new Sorter());

		// Try them out in the appropriate order
		if (LWJGLUtil.DEBUG || DEBUG) {
			System.out.println("Sorted display modes:");
			for ( DisplayMode aDm : dm ) {
				System.out.println(aDm);
			}
		}
		for ( DisplayMode aDm : dm ) {
			try {
				if ( LWJGLUtil.DEBUG || DEBUG )
					System.out.println("Attempting to set displaymode: " + aDm);
				org.lwjgl.opengl.Display.setDisplayMode(aDm);
				return aDm;
			} catch (Exception e) {
				if ( LWJGLUtil.DEBUG || DEBUG ) {
					System.out.println("Failed to set display mode to " + aDm);
					e.printStackTrace();
				}
			}
		}

		throw new Exception("Failed to set display mode.");
	}

}
