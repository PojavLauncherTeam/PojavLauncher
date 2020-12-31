/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl;

import org.lwjgl.system.*;

import javax.annotation.*;
import java.nio.*;

import static org.lwjgl.system.CheckIntrinsics.*;
import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.MemoryUtil.*;

/** This class is a container for architecture-independent pointer data. Its interface mirrors the {@link LongBuffer} API for convenience. */
public class PointerBuffer extends CustomBuffer<PointerBuffer> implements Comparable<PointerBuffer> {
// -- Begin LWJGL2 parts --
    public PointerBuffer(final int capacity) {
        this(allocateDirect(capacity));
	}
    
    public PointerBuffer(final ByteBuffer source) {
        this(create(source));
    }
    
    // Workaround for LWJGL2 bridge
    protected PointerBuffer(PointerBuffer copy) {
        this(copy.address0(), copy.container, copy.mark, copy.position, copy.limit, copy.capacity);
    }
    
    /**
     * Returns the ByteBuffer that backs this PointerBuffer.
     *
     * @return the pointer ByteBuffer
     */
    public ByteBuffer getBuffer() {
        return container;
    }

    /** Returns true if the underlying architecture is 64bit. */
    public static boolean is64Bit() {
        return POINTER_SIZE == 8;
    }

    /**
     * Returns the pointer size in bytes, based on the underlying architecture.
     *
     * @return The pointer size in bytes
     */
    public static int getPointerSize() {
        return POINTER_SIZE;
    }
    
    /**
     * Returns this buffer's position, in bytes. </p>
     *
     * @return The position of this buffer in bytes.
     */
    public final int positionByte() {
        return position() * getPointerSize();
	}

    /**
     * Returns the number of bytes between the current position and the
     * limit. </p>
     *
     * @return The number of bytes remaining in this buffer
     */
    public final int remainingByte() {
        return remaining() * getPointerSize();
	}
    
    /**
     * Creates a new, read-only pointer buffer that shares this buffer's
     * content.
     * <p/>
     * <p> The content of the new buffer will be that of this buffer.  Changes
     * to this buffer's content will be visible in the new buffer; the new
     * buffer itself, however, will be read-only and will not allow the shared
     * content to be modified.  The two buffers' position, limit, and mark
     * values will be independent.
     * <p/>
     * <p> The new buffer's capacity, limit and position will be
     * identical to those of this buffer.
     * <p/>
     * <p> If this buffer is itself read-only then this method behaves in
     * exactly the same way as the {@link #duplicate duplicate} method.  </p>
     *
     * @return The new, read-only pointer buffer
     */
    public PointerBuffer asReadOnlyBuffer() {
        final PointerBuffer buffer = new PointerBufferR(container);

        buffer.position(position());
        buffer.limit(limit());

        return buffer;
    }

    public boolean isReadOnly() {
        return false;
	}
    
    
    /**
     * Read-only version of PointerBuffer.
     *
     * @author Spasi
     */
    private static final class PointerBufferR extends PointerBuffer {

        PointerBufferR(final ByteBuffer source) {
            super(source);
        }

        public boolean isReadOnly() {
            return true;
        }

        protected PointerBuffer newInstance(final ByteBuffer source) {
            return new PointerBufferR(source);
        }

        public PointerBuffer asReadOnlyBuffer() {
            return duplicate();
        }

        public PointerBuffer put(final long l) {
            throw new ReadOnlyBufferException();
        }

        public PointerBuffer put(final int index, final long l) {
            throw new ReadOnlyBufferException();
        }

        public PointerBuffer put(final PointerBuffer src) {
            throw new ReadOnlyBufferException();
        }

        public PointerBuffer put(final long[] src, final int offset, final int length) {
            throw new ReadOnlyBufferException();
        }

        public PointerBuffer compact() {
            throw new ReadOnlyBufferException();
        }

	}
// -- End LWJGL2 parts --

    protected PointerBuffer(long address, @Nullable ByteBuffer container, int mark, int position, int limit, int capacity) {
        super(address, container, mark, position, limit, capacity);
    }

    /**
     * Allocates a new pointer buffer.
     *
     * <p>The new buffer's position will be zero, its limit will be its capacity, and its mark will be undefined.</p>
     *
     * @param capacity the new buffer's capacity, in pointers
     *
     * @return the new pointer buffer
     *
     * @throws IllegalArgumentException If the {@code capacity} is a negative integer
     */
    public static PointerBuffer allocateDirect(int capacity) {
        ByteBuffer source = BufferUtils.createByteBuffer(BufferUtils.getAllocationSize(capacity, POINTER_SHIFT));
        return wrap(PointerBuffer.class, memAddress(source), capacity, source);
    }

    /**
     * Creates a new PointerBuffer that starts at the specified memory address and has the specified capacity.
     *
     * @param address  the starting memory address
     * @param capacity the buffer capacity, in number of pointers
     */
    public static PointerBuffer create(long address, int capacity) {
        return wrap(PointerBuffer.class, address, capacity);
    }

    /**
     * Creates a new PointerBuffer using the specified ByteBuffer as its pointer data source.
     *
     * @param source the source buffer
     */
    public static PointerBuffer create(ByteBuffer source) {
        int capacity = source.remaining() >> POINTER_SHIFT;
        return wrap(PointerBuffer.class, memAddress(source), capacity, source);
    }

    @Override
    protected PointerBuffer self() {
        return this;
    }
    
    @Override
    public int sizeof() {
        return POINTER_SIZE;
    }
    
    /**
     * Relative <i>get</i> method. Reads the pointer at this buffer's current position, and then increments the position.
     *
     * @return the pointer at the buffer's current position
     *
     * @throws BufferUnderflowException If the buffer's current position is not smaller than its limit
     */
    public long get() {
        return memGetAddress(address + Integer.toUnsignedLong(nextGetIndex()) * POINTER_SIZE);
    }

    /**
     * Convenience relative get from a source ByteBuffer.
     *
     * @param source the source ByteBuffer
     */
    public static long get(ByteBuffer source) {
        if (source.remaining() < POINTER_SIZE) {
            throw new BufferUnderflowException();
        }

        try {
            return memGetAddress(memAddress(source));
        } finally {
            source.position(source.position() + POINTER_SIZE);
        }
    }

    /**
     * Relative <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p>Writes the specified pointer into this buffer at the current position, and then increments the position.</p>
     *
     * @param p the pointer to be written
     *
     * @return This buffer
     *
     * @throws BufferOverflowException If this buffer's current position is not smaller than its limit
     */
    public PointerBuffer put(long p) {
        memPutAddress(address + Integer.toUnsignedLong(nextPutIndex()) * POINTER_SIZE, p);
        return this;
    }

    /**
     * Convenience relative put on a target ByteBuffer.
     *
     * @param target the target ByteBuffer
     * @param p      the pointer value to be written
     */
    public static void put(ByteBuffer target, long p) {
        if (target.remaining() < POINTER_SIZE) {
            throw new BufferOverflowException();
        }

        try {
            memPutAddress(memAddress(target), p);
        } finally {
            target.position(target.position() + POINTER_SIZE);
        }
    }

    /**
     * Absolute <i>get</i> method. Reads the pointer at the specified {@code index}.
     *
     * @param index the index from which the pointer will be read
     *
     * @return the pointer at the specified {@code index}
     *
     * @throws IndexOutOfBoundsException If {@code index} is negative or not smaller than the buffer's limit
     */
    public long get(int index) {
        return memGetAddress(address + check(index, limit) * POINTER_SIZE);
    }

    /**
     * Convenience absolute get from a source ByteBuffer.
     *
     * @param source the source ByteBuffer
     * @param index  the index at which the pointer will be read
     */
    public static long get(ByteBuffer source, int index) {
        checkFromIndexSize(index, POINTER_SIZE, source.limit());
        return memGetAddress(memAddress0(source) + index);
    }

    /**
     * Absolute <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p>Writes the specified pointer into this buffer at the specified {@code index}.</p>
     *
     * @param index the index at which the pointer will be written
     * @param p     the pointer value to be written
     *
     * @return This buffer
     *
     * @throws IndexOutOfBoundsException If {@code index} is negative or not smaller than the buffer's limit
     */
    public PointerBuffer put(int index, long p) {
        memPutAddress(address + check(index, limit) * POINTER_SIZE, p);
        return this;
    }

    /**
     * Convenience absolute put on a target ByteBuffer.
     *
     * @param target the target ByteBuffer
     * @param index  the index at which the pointer will be written
     * @param p      the pointer value to be written
     */
    public static void put(ByteBuffer target, int index, long p) {
        checkFromIndexSize(index, POINTER_SIZE, target.limit());
        memPutAddress(memAddress0(target) + index, p);
    }

    // -- PointerWrapper operations --

    /** Puts the pointer value of the specified {@link Pointer} at the current position and then increments the position. */
    public PointerBuffer put(Pointer pointer) {
        put(pointer.address());
        return this;
    }

    /** Puts the pointer value of the specified {@link Pointer} at the specified {@code index}. */
    public PointerBuffer put(int index, Pointer pointer) {
        put(index, pointer.address());
        return this;
    }

    // -- Buffer address operations --

    /**
     * <p>Writes the address of the specified {@code buffer} into this buffer at the current position, and then increments the position.</p>
     *
     * @param buffer the pointer to be written
     *
     * @return this buffer
     *
     * @throws BufferOverflowException If this buffer's current position is not smaller than its limit
     */
    public PointerBuffer put(ByteBuffer buffer) {
        put(memAddress(buffer));
        return this;
    }

    /**
     * <p>Writes the address of the specified {@code buffer} into this buffer at the current position, and then increments the position.</p>
     *
     * @param buffer the pointer to be written
     *
     * @return this buffer
     *
     * @throws BufferOverflowException If this buffer's current position is not smaller than its limit
     */
    public PointerBuffer put(ShortBuffer buffer) {
        put(memAddress(buffer));
        return this;
    }

    /**
     * <p>Writes the address of the specified {@code buffer} into this buffer at the current position, and then increments the position.</p>
     *
     * @param buffer the pointer to be written
     *
     * @return this buffer
     *
     * @throws BufferOverflowException If this buffer's current position is not smaller than its limit
     */
    public PointerBuffer put(IntBuffer buffer) {
        put(memAddress(buffer));
        return this;
    }

    /**
     * <p>Writes the address of the specified {@code buffer} into this buffer at the current position, and then increments the position.</p>
     *
     * @param buffer the pointer to be written
     *
     * @return this buffer
     *
     * @throws BufferOverflowException If this buffer's current position is not smaller than its limit
     */
    public PointerBuffer put(LongBuffer buffer) {
        put(memAddress(buffer));
        return this;
    }

    /**
     * <p>Writes the address of the specified {@code buffer} into this buffer at the current position, and then increments the position.</p>
     *
     * @param buffer the pointer to be written
     *
     * @return this buffer
     *
     * @throws BufferOverflowException If this buffer's current position is not smaller than its limit
     */
    public PointerBuffer put(FloatBuffer buffer) {
        put(memAddress(buffer));
        return this;
    }

    /**
     * <p>Writes the address of the specified {@code buffer} into this buffer at the current position, and then increments the position.</p>
     *
     * @param buffer the pointer to be written
     *
     * @return this buffer
     *
     * @throws BufferOverflowException If this buffer's current position is not smaller than its limit
     */
    public PointerBuffer put(DoubleBuffer buffer) {
        put(memAddress(buffer));
        return this;
    }

    /**
     * <p>Writes the address of the specified {@code buffer} into this buffer at the current position, and then increments the position.</p>
     *
     * @param buffer the pointer to be written
     *
     * @return this buffer
     *
     * @throws BufferOverflowException If this buffer's current position is not smaller than its limit
     */
    public PointerBuffer putAddressOf(CustomBuffer<?> buffer) {
        put(memAddress(buffer));
        return this;
    }

    // ---

    /** Puts the address of the specified {@code buffer} at the specified {@code index}. */
    public PointerBuffer put(int index, ByteBuffer buffer) {
        put(index, memAddress(buffer));
        return this;
    }

    /** Puts the address of the specified {@code buffer} at the specified {@code index}. */
    public PointerBuffer put(int index, ShortBuffer buffer) {
        put(index, memAddress(buffer));
        return this;
    }

    /** Puts the address of the specified {@code buffer} at the specified {@code index}. */
    public PointerBuffer put(int index, IntBuffer buffer) {
        put(index, memAddress(buffer));
        return this;
    }

    /** Puts the address of the specified {@code buffer} at the specified {@code index}. */
    public PointerBuffer put(int index, LongBuffer buffer) {
        put(index, memAddress(buffer));
        return this;
    }

    /** Puts the address of the specified {@code buffer} at the specified {@code index}. */
    public PointerBuffer put(int index, FloatBuffer buffer) {
        put(index, memAddress(buffer));
        return this;
    }

    /** Puts the address of the specified {@code buffer} at the specified {@code index}. */
    public PointerBuffer put(int index, DoubleBuffer buffer) {
        put(index, memAddress(buffer));
        return this;
    }

    /** Puts the address of the specified {@code buffer} at the specified {@code index}. */
    public PointerBuffer putAddressOf(int index, CustomBuffer<?> buffer) {
        put(index, memAddress(buffer));
        return this;
    }

    // ---

    /**
     * Reads the pointer at this buffer's current position, and then increments the position. The pointer is returned as a {@link ByteBuffer} instance that
     * starts at the pointer address and has capacity equal to the specified {@code size}.
     *
     * @throws BufferUnderflowException If the buffer's current position is not smaller than its limit
     */
    public ByteBuffer getByteBuffer(int size) { return memByteBuffer(get(), size); }

    /**
     * Reads the pointer at this buffer's current position, and then increments the position. The pointer is returned as a {@link ShortBuffer} instance that
     * starts at the pointer address and has capacity equal to the specified {@code size}.
     *
     * @throws BufferUnderflowException If the buffer's current position is not smaller than its limit
     */
    public ShortBuffer getShortBuffer(int size) { return memShortBuffer(get(), size); }

    /**
     * Reads the pointer at this buffer's current position, and then increments the position. The pointer is returned as a {@link IntBuffer} instance that
     * starts at the pointer address and has capacity equal to the specified {@code size}.
     *
     * @throws BufferUnderflowException If the buffer's current position is not smaller than its limit
     */
    public IntBuffer getIntBuffer(int size) { return memIntBuffer(get(), size); }

    /**
     * Reads the pointer at this buffer's current position, and then increments the position. The pointer is returned as a {@link LongBuffer} instance that
     * starts at the pointer address and has capacity equal to the specified {@code size}.
     *
     * @throws BufferUnderflowException If the buffer's current position is not smaller than its limit
     */
    public LongBuffer getLongBuffer(int size) { return memLongBuffer(get(), size); }

    /**
     * Reads the pointer at this buffer's current position, and then increments the position. The pointer is returned as a {@link FloatBuffer} instance that
     * starts at the pointer address and has capacity equal to the specified {@code size}.
     *
     * @throws BufferUnderflowException If the buffer's current position is not smaller than its limit
     */
    public FloatBuffer getFloatBuffer(int size) { return memFloatBuffer(get(), size); }

    /**
     * Reads the pointer at this buffer's current position, and then increments the position. The pointer is returned as a {@link DoubleBuffer} instance that
     * starts at the pointer address and has capacity equal to the specified {@code size}.
     *
     * @throws BufferUnderflowException If the buffer's current position is not smaller than its limit
     */
    public DoubleBuffer getDoubleBuffer(int size) { return memDoubleBuffer(get(), size); }

    /**
     * Reads the pointer at this buffer's current position, and then increments the position. The pointer is returned as a {@code PointerBuffer} instance that
     * starts at the pointer address and has capacity equal to the specified {@code size}.
     *
     * @throws BufferUnderflowException If the buffer's current position is not smaller than its limit
     */
    public PointerBuffer getPointerBuffer(int size) { return memPointerBuffer(get(), size); }

    /**
     * Reads the pointer at this buffer's current position, and then increments the position. The pointer is evaluated as a null-terminated ASCII string, which
     * is decoded and returned as a {@link String} instance.
     *
     * @throws BufferUnderflowException If the buffer's current position is not smaller than its limit
     */
    public String getStringASCII() { return memASCII(get()); }

    /**
     * Reads the pointer at this buffer's current position, and then increments the position. The pointer is evaluated as a null-terminated UTF-8 string, which
     * is decoded and returned as a {@link String} instance.
     *
     * @throws BufferUnderflowException If the buffer's current position is not smaller than its limit
     */
    public String getStringUTF8() { return memUTF8(get()); }

    /**
     * Reads the pointer at this buffer's current position, and then increments the position. The pointer is evaluated as a null-terminated UTF-16 string,
     * which is decoded and returned as a {@link String} instance.
     *
     * @throws BufferUnderflowException If the buffer's current position is not smaller than its limit
     */
    public String getStringUTF16() { return memUTF16(get()); }

    // ---

    /** Returns a {@link ByteBuffer} instance that starts at the address found at the specified {@code index} and has capacity equal to the specified size. */
    public ByteBuffer getByteBuffer(int index, int size) { return memByteBuffer(get(index), size); }

    /** Returns a {@link ShortBuffer} instance that starts at the address found at the specified {@code index} and has capacity equal to the specified size. */
    public ShortBuffer getShortBuffer(int index, int size) { return memShortBuffer(get(index), size); }

    /** Returns a {@link IntBuffer} instance that starts at the address found at the specified {@code index} and has capacity equal to the specified size. */
    public IntBuffer getIntBuffer(int index, int size) { return memIntBuffer(get(index), size); }

    /** Returns a {@link LongBuffer} instance that starts at the address found at the specified {@code index} and has capacity equal to the specified size. */
    public LongBuffer getLongBuffer(int index, int size) { return memLongBuffer(get(index), size); }

    /** Returns a {@link FloatBuffer} instance that starts at the address found at the specified {@code index} and has capacity equal to the specified size. */
    public FloatBuffer getFloatBuffer(int index, int size) { return memFloatBuffer(get(index), size); }

    /** Returns a {@link DoubleBuffer} instance that starts at the address found at the specified {@code index} and has capacity equal to the specified size. */
    public DoubleBuffer getDoubleBuffer(int index, int size) { return memDoubleBuffer(get(index), size); }

    /** Returns a {@code PointerBuffer} instance that starts at the address found at the specified {@code index} and has capacity equal to the specified size. */
    public PointerBuffer getPointerBuffer(int index, int size) { return memPointerBuffer(get(index), size); }

    /** Decodes the ASCII string that starts at the address found at the specified {@code index}. */
    public String getStringASCII(int index) { return memASCII(get(index)); }

    /** Decodes the UTF-8 string that starts at the address found at the specified {@code index}. */
    public String getStringUTF8(int index) { return memUTF8(get(index)); }

    /** Decodes the UTF-16 string that starts at the address found at the specified {@code index}. */
    public String getStringUTF16(int index) { return memUTF16(get(index)); }

    // -- Bulk get operations --

    /**
     * Relative bulk <i>get</i> method.
     *
     * <p>This method transfers pointers from this buffer into the specified destination array. An invocation of this method of the form {@code src.get(a)}
     * behaves in exactly the same way as the invocation
     *
     * <pre>
     *     src.get(a, 0, a.length) </pre>
     *
     * @return This buffer
     *
     * @throws BufferUnderflowException If there are fewer than {@code length} pointers remaining in this buffer
     */
    public PointerBuffer get(long[] dst) {
        return get(dst, 0, dst.length);
    }

    /**
     * Relative bulk <i>get</i> method.
     *
     * <p>This method transfers pointers from this buffer into the specified destination array. If there are fewer pointers remaining in the buffer than are
     * required to satisfy the request, that is, if {@code length}&nbsp;{@code &gt;}&nbsp;{@code remaining()}, then no pointers are transferred and a
     * {@link BufferUnderflowException} is thrown.
     *
     * <p>Otherwise, this method copies {@code length} pointers from this buffer into the specified array, starting at the current position of this buffer and
     * at the specified offset in the array. The position of this buffer is then incremented by {@code length}.
     *
     * <p>In other words, an invocation of this method of the form {@code src.get(dst,&nbsp;off,&nbsp;len)} has exactly the same effect as the loop</p>
     *
     * <pre>
     *     for (int i = off; i &lt; off + len; i++)
     *         dst[i] = src.get(); </pre>
     *
     * <p>except that it first checks that there are sufficient pointers in this buffer and it is potentially much more efficient. </p>
     *
     * @param dst    the array into which pointers are to be written
     * @param offset the offset within the array of the first pointer to be written; must be non-negative and no larger than {@code dst.length}
     * @param length the maximum number of pointers to be written to the specified array; must be non-negative and no larger than {@code dst.length - offset}
     *
     * @return This buffer
     *
     * @throws BufferUnderflowException  If there are fewer than {@code length} pointers remaining in this buffer
     * @throws IndexOutOfBoundsException If the preconditions on the {@code offset} and {@code length} parameters do not hold
     */
    public PointerBuffer get(long[] dst, int offset, int length) {
        if (BITS64) {
            memLongBuffer(address(), remaining()).get(dst, offset, length);
            position(position() + length);
        } else {
            get32(dst, offset, length);
        }

        return this;
    }

    private void get32(long[] dst, int offset, int length) {
        checkFromIndexSize(offset, length, dst.length);
        if (remaining() < length) {
            throw new BufferUnderflowException();
        }
        for (int i = offset, end = offset + length; i < end; i++) {
            dst[i] = get();
        }
    }

    /**
     * Relative bulk <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p>This method transfers the entire content of the specified source pointer array into this buffer. An invocation of this method of the form
     * {@code dst.put(a)} behaves in exactly the same way as the invocation</p>
     *
     * <pre>
     *     dst.put(a, 0, a.length) </pre>
     *
     * @return This buffer
     *
     * @throws BufferOverflowException If there is insufficient space in this buffer
     */
    public PointerBuffer put(long[] src) {
        return put(src, 0, src.length);
    }

    /**
     * Relative bulk <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p>This method transfers pointers into this buffer from the specified source array. If there are more pointers to be copied from the array than remain
     * in this buffer, that is, if {@code length}&nbsp;{@code &gt;}&nbsp;{@code remaining()}, then no pointers are transferred and a
     * {@link BufferOverflowException} is thrown.
     *
     * <p>Otherwise, this method copies {@code length} pointers from the specified array into this buffer, starting at the specified offset in the array and
     * at the current position of this buffer. The position of this buffer is then incremented by {@code length}.</p>
     *
     * <p>In other words, an invocation of this method of the form {@code dst.put(src,&nbsp;off,&nbsp;len)} has exactly the same effect as the loop</p>
     *
     * <pre>
     *     for (int i = off; i &lt; off + len; i++)
     *         dst.put(a[i]); </pre>
     *
     * <p>except that it first checks that there is sufficient space in this buffer and it is potentially much more efficient.</p>
     *
     * @param src    the array from which pointers are to be read
     * @param offset the offset within the array of the first pointer to be read; must be non-negative and no larger than {@code array.length}
     * @param length the number of pointers to be read from the specified array; must be non-negative and no larger than {@code array.length - offset}
     *
     * @return This buffer
     *
     * @throws BufferOverflowException   If there is insufficient space in this buffer
     * @throws IndexOutOfBoundsException If the preconditions on the {@code offset} and {@code length} parameters do not hold
     */
    public PointerBuffer put(long[] src, int offset, int length) {
        if (BITS64) {
            memLongBuffer(address(), remaining()).put(src, offset, length);
            position(position() + length);
        } else {
            put32(src, offset, length);
        }

        return this;
    }

    private void put32(long[] src, int offset, int length) {
        checkFromIndexSize(offset, length, src.length);
        if (remaining() < length) {
            throw new BufferOverflowException();
        }
        int end = offset + length;
        for (int i = offset; i < end; i++) {
            put(src[i]);
        }
    }

    /**
     * Returns the current hash code of this buffer.
     *
     * <p>The hash code of a pointer buffer depends only upon its remaining elements; that is, upon the elements from {@code position()} up to, and including,
     * the element at {@code limit()}&nbsp;-&nbsp;{@code 1}.</p>
     *
     * <p>Because buffer hash codes are content-dependent, it is inadvisable to use buffers as keys in hash maps or similar data structures unless it is known
     * that their contents will not change.</p>
     *
     * @return the current hash code of this buffer
     */
    public int hashCode() {
        int h = 1;
        int p = position();
        for (int i = limit() - 1; i >= p; i--) {
            h = 31 * h + (int)get(i);
        }
        return h;
    }

    /**
     * Tells whether or not this buffer is equal to another object.
     *
     * <p>Two pointer buffers are equal if, and only if,</p>
     *
     * <ol>
     * <li>They have the same element type,</li>
     * <li>They have the same number of remaining elements, and</li>
     * <li>The two sequences of remaining elements, considered
     * independently of their starting positions, are pointwise equal.</li>
     * </ol>
     *
     * <p>A pointer buffer is not equal to any other type of object.</p>
     *
     * @param ob the object to which this buffer is to be compared
     *
     * @return {@code true} if, and only if, this buffer is equal to the
     * given object
     */
    public boolean equals(Object ob) {
        if (!(ob instanceof PointerBuffer)) {
            return false;
        }
        PointerBuffer that = (PointerBuffer)ob;
        if (this.remaining() != that.remaining()) {
            return false;
        }
        int p = this.position();
        for (int i = this.limit() - 1, j = that.limit() - 1; i >= p; i--, j--) {
            long v1 = this.get(i);
            long v2 = that.get(j);
            if (v1 != v2) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares this buffer to another.
     *
     * <p>Two pointer buffers are compared by comparing their sequences of remaining elements lexicographically, without regard to the starting position of
     * each sequence within its corresponding buffer.</p>
     *
     * <p>A pointer buffer is not comparable to any other type of object.</p>
     *
     * @return A negative integer, zero, or a positive integer as this buffer is less than, equal to, or greater than the specified buffer
     */
    @Override
    public int compareTo(PointerBuffer that) {
        int n = this.position() + Math.min(this.remaining(), that.remaining());
        for (int i = this.position(), j = that.position(); i < n; i++, j++) {
            long v1 = this.get(i);
            long v2 = that.get(j);
            if (v1 == v2) {
                continue;
            }
            if (v1 < v2) {
                return -1;
            }
            return +1;
        }
        return this.remaining() - that.remaining();
    }

}
