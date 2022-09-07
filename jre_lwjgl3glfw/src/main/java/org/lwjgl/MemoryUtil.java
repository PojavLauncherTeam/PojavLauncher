/*
 * Copyright (c) 2002-2011 LWJGL Project
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
package org.lwjgl;

import java.lang.reflect.Field;
import java.nio.*;
import java.nio.charset.*;

/**
 * [INTERNAL USE ONLY]
 * <p/>
 * This class provides utility methods for passing buffers to JNI API calls.
 *
 * @author Spasi
 */
public final class MemoryUtil {

	private static final Charset ascii;
	private static final Charset utf8;

	static {
		ascii = Charset.forName("ISO-8859-1");
		utf8 = Charset.forName("UTF-8");
	}

	private MemoryUtil() {
	}

	/**
	 * Returns the memory address of the specified buffer. [INTERNAL USE ONLY]
	 *
	 * @param buffer the buffer
	 *
	 * @return the memory address
	 */
	public static long getAddress0(Buffer buffer) { return org.lwjgl.system.MemoryUtil.memAddress0(buffer); }

	public static long getAddress0Safe(Buffer buffer) { return buffer == null ? 0L : getAddress0(buffer); }

	public static long getAddress0(PointerBuffer buffer) { return getAddress0(buffer.getBuffer()); }

	public static long getAddress0Safe(PointerBuffer buffer) { return buffer == null ? 0L : getAddress0(buffer.getBuffer()); }

	// --- [ API utilities ] ---

	public static long getAddress(ByteBuffer buffer) { return getAddress(buffer, buffer.position()); }

	public static long getAddress(ByteBuffer buffer, int position) { return getAddress0(buffer) + position; }

	public static long getAddress(ShortBuffer buffer) { return getAddress(buffer, buffer.position()); }

	public static long getAddress(ShortBuffer buffer, int position) { return getAddress0(buffer) + (position << 1); }

	public static long getAddress(CharBuffer buffer) { return getAddress(buffer, buffer.position()); }

	public static long getAddress(CharBuffer buffer, int position) { return getAddress0(buffer) + (position << 1); }

	public static long getAddress(IntBuffer buffer) { return getAddress(buffer, buffer.position()); }

	public static long getAddress(IntBuffer buffer, int position) { return getAddress0(buffer) + (position << 2); }

	public static long getAddress(FloatBuffer buffer) { return getAddress(buffer, buffer.position()); }

	public static long getAddress(FloatBuffer buffer, int position) { return getAddress0(buffer) + (position << 2); }

	public static long getAddress(LongBuffer buffer) { return getAddress(buffer, buffer.position()); }

	public static long getAddress(LongBuffer buffer, int position) { return getAddress0(buffer) + (position << 3); }

	public static long getAddress(DoubleBuffer buffer) { return getAddress(buffer, buffer.position()); }

	public static long getAddress(DoubleBuffer buffer, int position) { return getAddress0(buffer) + (position << 3); }

	public static long getAddress(PointerBuffer buffer) { return getAddress(buffer, buffer.position()); }

	public static long getAddress(PointerBuffer buffer, int position) { return getAddress0(buffer) + (position * PointerBuffer.getPointerSize()); }

	// --- [ API utilities - Safe ] ---

	public static long getAddressSafe(ByteBuffer buffer) { return buffer == null ? 0L : getAddress(buffer); }

	public static long getAddressSafe(ByteBuffer buffer, int position) { return buffer == null ? 0L : getAddress(buffer, position); }

	public static long getAddressSafe(ShortBuffer buffer) { return buffer == null ? 0L : getAddress(buffer); }

	public static long getAddressSafe(ShortBuffer buffer, int position) { return buffer == null ? 0L : getAddress(buffer, position); }

	public static long getAddressSafe(CharBuffer buffer) { return buffer == null ? 0L : getAddress(buffer); }

	public static long getAddressSafe(CharBuffer buffer, int position) { return buffer == null ? 0L : getAddress(buffer, position); }

	public static long getAddressSafe(IntBuffer buffer) { return buffer == null ? 0L : getAddress(buffer); }

	public static long getAddressSafe(IntBuffer buffer, int position) { return buffer == null ? 0L : getAddress(buffer, position); }

	public static long getAddressSafe(FloatBuffer buffer) { return buffer == null ? 0L : getAddress(buffer); }

	public static long getAddressSafe(FloatBuffer buffer, int position) { return buffer == null ? 0L : getAddress(buffer, position); }

	public static long getAddressSafe(LongBuffer buffer) { return buffer == null ? 0L : getAddress(buffer); }

	public static long getAddressSafe(LongBuffer buffer, int position) { return buffer == null ? 0L : getAddress(buffer, position); }

	public static long getAddressSafe(DoubleBuffer buffer) { return buffer == null ? 0L : getAddress(buffer); }

	public static long getAddressSafe(DoubleBuffer buffer, int position) { return buffer == null ? 0L : getAddress(buffer, position); }

	public static long getAddressSafe(PointerBuffer buffer) { return buffer == null ? 0L : getAddress(buffer); }

	public static long getAddressSafe(PointerBuffer buffer, int position) { return buffer == null ? 0L : getAddress(buffer, position); }

	// --- [ String utilities ] ---

	/**
	 * Returns a ByteBuffer containing the specified text ASCII encoded and null-terminated.
	 * If text is null, null is returned.
	 *
	 * @param text the text to encode
	 *
	 * @return the encoded text or null
	 *
	 * @see String#getBytes()
	 */
	public static ByteBuffer encodeASCII(final CharSequence text) {
		return encode(text, ascii);
	}

	/**
	 * Returns a ByteBuffer containing the specified text UTF-8 encoded and null-terminated.
	 * If text is null, null is returned.
	 *
	 * @param text the text to encode
	 *
	 * @return the encoded text or null
	 *
	 * @see String#getBytes()
	 */
	public static ByteBuffer encodeUTF8(final CharSequence text) {
		return encode(text, utf8);
	}

	/**
	 * Returns a ByteBuffer containing the specified text UTF-16LE encoded and null-terminated.
	 * If text is null, null is returned.
	 *
	 * @param text the text to encode
	 *
	 * @return the encoded text
	 */
	public static ByteBuffer encodeUTF16(final CharSequence text) {
		return org.lwjgl.system.MemoryUtil.memUTF16(text);
	}

	/**
	 * Wraps the specified text in a null-terminated CharBuffer and encodes it using the specified Charset.
	 *
	 * @param text    the text to encode
	 * @param charset the charset to use for encoding
	 *
	 * @return the encoded text
	 */
	private static ByteBuffer encode(final CharSequence text, final Charset charset) {
		if ( text == null )
			return null;

		return encode(CharBuffer.wrap(new CharSequenceNT(text)), charset);
	}

	/**
	 * A {@link CharsetEncoder#encode(java.nio.CharBuffer)} implementation that uses {@link BufferUtils#createByteBuffer(int)}
	 * instead of {@link ByteBuffer#allocate(int)}.
	 *
	 * @see CharsetEncoder#encode(java.nio.CharBuffer)
	 */
	private static ByteBuffer encode(final CharBuffer in, final Charset charset) {
		final CharsetEncoder encoder = charset.newEncoder(); // encoders are not thread-safe, create a new one on every call

		int n = (int)(in.remaining() * encoder.averageBytesPerChar());
		ByteBuffer out = BufferUtils.createByteBuffer(n);

		if ( n == 0 && in.remaining() == 0 )
			return out;

		encoder.reset();
		while ( true ) {
			CoderResult cr = in.hasRemaining() ? encoder.encode(in, out, true) : CoderResult.UNDERFLOW;
			if ( cr.isUnderflow() )
				cr = encoder.flush(out);

			if ( cr.isUnderflow() )
				break;

			if ( cr.isOverflow() ) {
				n = 2 * n + 1;    // Ensure progress; n might be 0!
				ByteBuffer o = BufferUtils.createByteBuffer(n);
				out.flip();
				o.put(out);
				out = o;
				continue;
			}

			try {
				cr.throwException();
			} catch (CharacterCodingException e) {
				throw new RuntimeException(e);
			}
		}
		out.flip();
		return out;
	}

	public static String decodeASCII(final ByteBuffer buffer) {
		return decode(buffer, ascii);
	}

	public static String decodeUTF8(final ByteBuffer buffer) {
		return decode(buffer, utf8);
	}

	public static String decodeUTF16(final ByteBuffer buffer) {
		return org.lwjgl.system.MemoryUtil.memUTF16(buffer);
	}

	private static String decode(final ByteBuffer buffer, final Charset charset) {
		if ( buffer == null )
			return null;

		return decodeImpl(buffer, charset);
	}

	private static String decodeImpl(final ByteBuffer in, final Charset charset) {
		final CharsetDecoder decoder = charset.newDecoder(); // decoders are not thread-safe, create a new one on every call

		int n = (int)(in.remaining() * decoder.averageCharsPerByte());
		CharBuffer out = BufferUtils.createCharBuffer(n);

		if ( (n == 0) && (in.remaining() == 0) )
			return "";

		decoder.reset();
		for (; ; ) {
			CoderResult cr = in.hasRemaining() ? decoder.decode(in, out, true) : CoderResult.UNDERFLOW;
			if ( cr.isUnderflow() )
				cr = decoder.flush(out);

			if ( cr.isUnderflow() )
				break;
			if ( cr.isOverflow() ) {
				n = 2 * n + 1;    // Ensure progress; n might be 0!
				CharBuffer o = BufferUtils.createCharBuffer(n);
				out.flip();
				o.put(out);
				out = o;
				continue;
			}
			try {
				cr.throwException();
			} catch (CharacterCodingException e) {
				throw new RuntimeException(e);
			}
		}
		out.flip();
		return out.toString();
	}

	/** A null-terminated CharSequence. */
	private static class CharSequenceNT implements CharSequence {

		final CharSequence source;

		CharSequenceNT(CharSequence source) {
			this.source = source;
		}

		public int length() {
			return source.length() + 1;

		}

		public char charAt(final int index) {
			return index == source.length() ? '\0' : source.charAt(index);

		}

		public CharSequence subSequence(final int start, final int end) {
			return new CharSequenceNT(source.subSequence(start, Math.min(end, source.length())));
		}

	}

	interface Accessor {

		long getAddress(Buffer buffer);

	}

	private static Accessor loadAccessor(final String className) throws Exception {
		return (Accessor)Class.forName(className).newInstance();
	}

	/** Default implementation. */
	private static class AccessorJNI implements Accessor {

		public long getAddress(final Buffer buffer) {
			return BufferUtils.getBufferAddress(buffer);
		}

	}

	/** Implementation using reflection on ByteBuffer. */
	private static class AccessorReflect implements Accessor {

		private final Field address;

		AccessorReflect() {
			try {
				address = getAddressField();
			} catch (NoSuchFieldException e) {
				throw new UnsupportedOperationException(e);
			}
			address.setAccessible(true);
		}

		public long getAddress(final Buffer buffer) {
			try {
				return address.getLong(buffer);
			} catch (IllegalAccessException e) {
				// cannot happen
				return 0L;
			}
		}

	}

	static Field getAddressField() throws NoSuchFieldException {
		return getDeclaredFieldRecursive(ByteBuffer.class, "address");
	}

	private static Field getDeclaredFieldRecursive(final Class<?> root, final String fieldName) throws NoSuchFieldException {
		Class<?> type = root;

		do {
			try {
				return type.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				type = type.getSuperclass();
			}
		} while ( type != null );

		throw new NoSuchFieldException(fieldName + " does not exist in " + root.getSimpleName() + " or any of its superclasses.");
	}

}
