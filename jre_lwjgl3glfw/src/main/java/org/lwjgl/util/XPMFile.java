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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * <p>
 * NOTE: This simple XPM reader does not support extensions nor hotspots
 * </p>
 *
 * @author Brian Matzon <brian@matzon.dk>
 * @author Jos Hirth
 * @version $Revision$
 * $Id$
 */

public class XPMFile {

	/** Array of bytes (RGBA) */
	private byte bytes[];

	private static final int WIDTH = 0;

	private static final int HEIGHT = 1;

	private static final int NUMBER_OF_COLORS = 2;

	private static final int CHARACTERS_PER_PIXEL = 3;

	private static int[] format = new int[4];

	/*
	 * Private constructor, use load(String filename)
	 */
	private XPMFile() {
	}

	/**
	 * Loads the XPM file
	 *
	 * @param file
	 *            path to file
	 * @return XPMFile loaded, or exception
	 * @throws IOException
	 *             If any IO exceptions occurs while reading file
	 */
	public static XPMFile load(String file) throws IOException {
		return load(new FileInputStream(new File(file)));
	}

	/**
	 * Loads the XPM file
	 *
	 * @param is
	 *            InputStream to read file from
	 * @return XPMFile loaded, or exception
	 */
	public static XPMFile load(InputStream is) {
		XPMFile xFile = new XPMFile();
		xFile.readImage(is);
		return xFile;
	}

	/**
	 * @return the height of the image.
	 */
	public int getHeight() {
		return format[HEIGHT];
	}

	/**
	 * @return the width of the image.
	 */
	public int getWidth() {
		return format[WIDTH];
	}

	/**
	 * @return The data of the image.
	 */
	public byte[] getBytes() {
		return bytes;
	}

	/**
	 * Read the image from the specified file.
	 */
	private void readImage(InputStream is) {
		try {
			LineNumberReader reader = new LineNumberReader(
					new InputStreamReader(is));
			HashMap<String, Integer> colors = new HashMap<String, Integer>();

			format = parseFormat(nextLineOfInterest(reader));

			// setup color mapping
			for (int i = 0; i < format[NUMBER_OF_COLORS]; i++) {
				Object[] colorDefinition = parseColor(nextLineOfInterest(reader));
				colors.put((String)colorDefinition[0], (Integer)colorDefinition[1]);
			}

			// read actual image (convert to RGBA)
			bytes = new byte[format[WIDTH] * format[HEIGHT] * 4];
			for (int i = 0; i < format[HEIGHT]; i++) {
				parseImageLine(nextLineOfInterest(reader), format, colors, i);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Unable to parse XPM File");
		}
	}

	/**
	 * Finds the next interesting line of text.
	 *
	 * @param reader
	 *            The LineNumberReader to read from
	 * @return The next interesting String (with stripped quotes)
	 * @throws IOException
	 *             If any IO exceptions occurs while reading file
	 */
	private static String nextLineOfInterest(LineNumberReader reader)
			throws IOException {
		String ret;
		do {
			ret = reader.readLine();
		} while (!ret.startsWith("\""));
		// lacks sanity check
		return ret.substring(1, ret.lastIndexOf('\"'));
	}

	/**
	 * Parses the format of the xpm file given a format string
	 *
	 * @param format
	 *            String to parse
	 * @return Array specifying width, height, colors, characters per pixel
	 */
	private static int[] parseFormat(String format) {
		// format should look like this:
		// 16 16 122 2

		// tokenize it
		StringTokenizer st = new StringTokenizer(format);

		return new int[] { Integer.parseInt(st.nextToken()), /* width */
		Integer.parseInt(st.nextToken()), /* height */
		Integer.parseInt(st.nextToken()), /* colors */
		Integer.parseInt(st.nextToken()) /* chars per pixel */
		};
	}

	/**
	 * Given a line defining a color/pixel, parses this into an array containing
	 * a key and a color
	 *
	 * @param line
	 *            Line to parse
	 * @return Array containing a key (String) and a color (Integer)
	 */
	private static Object[] parseColor(String line) {
		// line should look like this:
		// # c #0A0A0A

		// NOTE: will break if the color is something like "black" or "gray50"
		// etc (instead of #rrggbb).

		String key = line.substring(0, format[CHARACTERS_PER_PIXEL]);
		// since we always assume color as type we dont need to read it
		// String type = line.substring(format[CHARACTERS_PER_PIXEL] + 1,
		// format[CHARACTERS_PER_PIXEL] + 2);
		String color = line.substring(format[CHARACTERS_PER_PIXEL] + 4);

		// we always assume type is color, and supplied as #<r><g><b>
		return new Object[] { key, Integer.parseInt(color, 16) };
	}

	/**
	 * Parses an Image line into its byte values
	 *
	 * @param line
	 *            Line of chars to parse
	 * @param format
	 *            Format to expext it in
	 * @param colors
	 *            Colors to lookup
	 * @param index
	 *            current index into lines, we've reached
	 */
	private void parseImageLine(String line, int[] format, HashMap<String, Integer> colors,
			int index) {
		// offset for next line
		int offset = index * 4 * format[WIDTH];

		// read <format[CHARACTERS_PER_PIXEL]> characters <format[WIDTH]> times,
		// each iteration equals one pixel
		for (int i = 0; i < format[WIDTH]; i++) {
			String key = line
					.substring(
							i * format[CHARACTERS_PER_PIXEL],
							(i * format[CHARACTERS_PER_PIXEL] + format[CHARACTERS_PER_PIXEL]));
			int color = colors.get(key);
			bytes[offset + (i * 4)] = (byte) ((color & 0x00ff0000) >> 16);
			bytes[offset + ((i * 4) + 1)] = (byte) ((color & 0x0000ff00) >> 8);
			bytes[offset + ((i * 4) + 2)] = (byte) ((color & 0x000000ff) >> 0); // looks
			// better
			// :)
			bytes[offset + ((i * 4) + 3)] = (byte) 0xff; // always 0xff alpha
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("usage:\nXPMFile <file>");
		}

		try {
			String out = args[0].substring(0, args[0].indexOf(".")) + ".raw";
			XPMFile file = XPMFile.load(args[0]);
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(new File(out)));
			bos.write(file.getBytes());
			bos.close();

			// showResult(file.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	private static void showResult(byte[] bytes) {
		final BufferedImage i = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		int c = 0;
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				i.setRGB(x, y, (bytes[c] << 16) + (bytes[c + 1] << 8) + (bytes[c + 2] << 0) + (bytes[c + 3] << 24));//+(128<<24));//
				c += 4;
			}
		}

		final Frame frame = new Frame("XPM Result");
		frame.add(new Canvas() {

			public void paint(Graphics g) {
				g.drawImage(i, 0, 0, frame);
			}
		});

		frame.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}

		});

		frame.setSize(100, 100);
		frame.setVisible(true);
	}*/
}