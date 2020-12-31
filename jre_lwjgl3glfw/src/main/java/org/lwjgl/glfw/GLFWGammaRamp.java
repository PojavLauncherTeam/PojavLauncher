/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import javax.annotation.*;

import java.nio.*;

import org.lwjgl.*;
import org.lwjgl.system.*;

import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryStack.*;

/**
 * Describes the gamma ramp for a monitor.
 * 
 * <h3>Member documentation</h3>
 * 
 * <ul>
 * <li>{@code red} &ndash; an array of values describing the response of the red channel</li>
 * <li>{@code green} &ndash; an array of values describing the response of the green channel</li>
 * <li>{@code blue} &ndash; an array of values describing the response of the blue channel</li>
 * <li>{@code size} &ndash; the number of elements in each array</li>
 * </ul>
 * 
 * <h3>Layout</h3>
 * 
 * <pre><code>
 * struct GLFWgammaramp {
 *     unsigned short * red;
 *     unsigned short * green;
 *     unsigned short * blue;
 *     unsigned int size;
 * }</code></pre>
 *
 * @since version 3.0
 */
@NativeType("struct GLFWgammaramp")
public class GLFWGammaRamp extends Struct implements NativeResource {

    /** The struct size in bytes. */
    public static final int SIZEOF;

    /** The struct alignment in bytes. */
    public static final int ALIGNOF;

    /** The struct member offsets. */
    public static final int
        RED,
        GREEN,
        BLUE,
        SIZE;

    static {
        Layout layout = __struct(
            __member(POINTER_SIZE),
            __member(POINTER_SIZE),
            __member(POINTER_SIZE),
            __member(4)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        RED = layout.offsetof(0);
        GREEN = layout.offsetof(1);
        BLUE = layout.offsetof(2);
        SIZE = layout.offsetof(3);
    }

    /**
     * Creates a {@code GLFWGammaRamp} instance at the current position of the specified {@link ByteBuffer} container. Changes to the buffer's content will be
     * visible to the struct instance and vice versa.
     *
     * <p>The created instance holds a strong reference to the container object.</p>
     */
    public GLFWGammaRamp(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    /** Returns a {@link ShortBuffer} view of the data pointed to by the {@code red} field. */
    @NativeType("unsigned short *")
    public ShortBuffer red() { return nred(address()); }
    /** Returns a {@link ShortBuffer} view of the data pointed to by the {@code green} field. */
    @NativeType("unsigned short *")
    public ShortBuffer green() { return ngreen(address()); }
    /** Returns a {@link ShortBuffer} view of the data pointed to by the {@code blue} field. */
    @NativeType("unsigned short *")
    public ShortBuffer blue() { return nblue(address()); }
    /** Returns the value of the {@code size} field. */
    @NativeType("unsigned int")
    public int size() { return nsize(address()); }

    /** Sets the address of the specified {@link ShortBuffer} to the {@code red} field. */
    public GLFWGammaRamp red(@NativeType("unsigned short *") ShortBuffer value) { nred(address(), value); return this; }
    /** Sets the address of the specified {@link ShortBuffer} to the {@code green} field. */
    public GLFWGammaRamp green(@NativeType("unsigned short *") ShortBuffer value) { ngreen(address(), value); return this; }
    /** Sets the address of the specified {@link ShortBuffer} to the {@code blue} field. */
    public GLFWGammaRamp blue(@NativeType("unsigned short *") ShortBuffer value) { nblue(address(), value); return this; }
    /** Sets the specified value to the {@code size} field. */
    public GLFWGammaRamp size(@NativeType("unsigned int") int value) { nsize(address(), value); return this; }

    /** Initializes this struct with the specified values. */
    public GLFWGammaRamp set(
        ShortBuffer red,
        ShortBuffer green,
        ShortBuffer blue,
        int size
    ) {
        red(red);
        green(green);
        blue(blue);
        size(size);

        return this;
    }

    /**
     * Copies the specified struct data to this struct.
     *
     * @param src the source struct
     *
     * @return this struct
     */
    public GLFWGammaRamp set(GLFWGammaRamp src) {
        memCopy(src.address(), address(), SIZEOF);
        return this;
    }

    // -----------------------------------

    /** Returns a new {@code GLFWGammaRamp} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed. */
    public static GLFWGammaRamp malloc() {
        return wrap(GLFWGammaRamp.class, nmemAllocChecked(SIZEOF));
    }

    /** Returns a new {@code GLFWGammaRamp} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed. */
    public static GLFWGammaRamp calloc() {
        return wrap(GLFWGammaRamp.class, nmemCallocChecked(1, SIZEOF));
    }

    /** Returns a new {@code GLFWGammaRamp} instance allocated with {@link BufferUtils}. */
    public static GLFWGammaRamp create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return wrap(GLFWGammaRamp.class, memAddress(container), container);
    }

    /** Returns a new {@code GLFWGammaRamp} instance for the specified memory address. */
    public static GLFWGammaRamp create(long address) {
        return wrap(GLFWGammaRamp.class, address);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    @Nullable
    public static GLFWGammaRamp createSafe(long address) {
        return address == NULL ? null : wrap(GLFWGammaRamp.class, address);
    }

    /**
     * Returns a new {@link GLFWGammaRamp.Buffer} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static GLFWGammaRamp.Buffer malloc(int capacity) {
        return wrap(Buffer.class, nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
    }

    /**
     * Returns a new {@link GLFWGammaRamp.Buffer} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static GLFWGammaRamp.Buffer calloc(int capacity) {
        return wrap(Buffer.class, nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    /**
     * Returns a new {@link GLFWGammaRamp.Buffer} instance allocated with {@link BufferUtils}.
     *
     * @param capacity the buffer capacity
     */
    public static GLFWGammaRamp.Buffer create(int capacity) {
        ByteBuffer container = __create(capacity, SIZEOF);
        return wrap(Buffer.class, memAddress(container), capacity, container);
    }

    /**
     * Create a {@link GLFWGammaRamp.Buffer} instance at the specified memory.
     *
     * @param address  the memory address
     * @param capacity the buffer capacity
     */
    public static GLFWGammaRamp.Buffer create(long address, int capacity) {
        return wrap(Buffer.class, address, capacity);
    }

    /** Like {@link #create(long, int) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    @Nullable
    public static GLFWGammaRamp.Buffer createSafe(long address, int capacity) {
        return address == NULL ? null : wrap(Buffer.class, address, capacity);
    }

    // -----------------------------------

    /** Returns a new {@code GLFWGammaRamp} instance allocated on the thread-local {@link MemoryStack}. */
    public static GLFWGammaRamp mallocStack() {
        return mallocStack(stackGet());
    }

    /** Returns a new {@code GLFWGammaRamp} instance allocated on the thread-local {@link MemoryStack} and initializes all its bits to zero. */
    public static GLFWGammaRamp callocStack() {
        return callocStack(stackGet());
    }

    /**
     * Returns a new {@code GLFWGammaRamp} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     */
    public static GLFWGammaRamp mallocStack(MemoryStack stack) {
        return wrap(GLFWGammaRamp.class, stack.nmalloc(ALIGNOF, SIZEOF));
    }

    /**
     * Returns a new {@code GLFWGammaRamp} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     */
    public static GLFWGammaRamp callocStack(MemoryStack stack) {
        return wrap(GLFWGammaRamp.class, stack.ncalloc(ALIGNOF, 1, SIZEOF));
    }

    /**
     * Returns a new {@link GLFWGammaRamp.Buffer} instance allocated on the thread-local {@link MemoryStack}.
     *
     * @param capacity the buffer capacity
     */
    public static GLFWGammaRamp.Buffer mallocStack(int capacity) {
        return mallocStack(capacity, stackGet());
    }

    /**
     * Returns a new {@link GLFWGammaRamp.Buffer} instance allocated on the thread-local {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param capacity the buffer capacity
     */
    public static GLFWGammaRamp.Buffer callocStack(int capacity) {
        return callocStack(capacity, stackGet());
    }

    /**
     * Returns a new {@link GLFWGammaRamp.Buffer} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static GLFWGammaRamp.Buffer mallocStack(int capacity, MemoryStack stack) {
        return wrap(Buffer.class, stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    /**
     * Returns a new {@link GLFWGammaRamp.Buffer} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static GLFWGammaRamp.Buffer callocStack(int capacity, MemoryStack stack) {
        return wrap(Buffer.class, stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    // -----------------------------------

    /** Unsafe version of {@link #red() red}. */
    public static ShortBuffer nred(long struct) { return memShortBuffer(memGetAddress(struct + GLFWGammaRamp.RED), nsize(struct)); }
    /** Unsafe version of {@link #green() green}. */
    public static ShortBuffer ngreen(long struct) { return memShortBuffer(memGetAddress(struct + GLFWGammaRamp.GREEN), nsize(struct)); }
    /** Unsafe version of {@link #blue() blue}. */
    public static ShortBuffer nblue(long struct) { return memShortBuffer(memGetAddress(struct + GLFWGammaRamp.BLUE), nsize(struct)); }
    /** Unsafe version of {@link #size}. */
    public static int nsize(long struct) { return UNSAFE.getInt(null, struct + GLFWGammaRamp.SIZE); }

    /** Unsafe version of {@link #red(ShortBuffer) red}. */
    public static void nred(long struct, ShortBuffer value) { memPutAddress(struct + GLFWGammaRamp.RED, memAddress(value)); }
    /** Unsafe version of {@link #green(ShortBuffer) green}. */
    public static void ngreen(long struct, ShortBuffer value) { memPutAddress(struct + GLFWGammaRamp.GREEN, memAddress(value)); }
    /** Unsafe version of {@link #blue(ShortBuffer) blue}. */
    public static void nblue(long struct, ShortBuffer value) { memPutAddress(struct + GLFWGammaRamp.BLUE, memAddress(value)); }
    /** Sets the specified value to the {@code size} field of the specified {@code struct}. */
    public static void nsize(long struct, int value) { UNSAFE.putInt(null, struct + GLFWGammaRamp.SIZE, value); }

    /**
     * Validates pointer members that should not be {@code NULL}.
     *
     * @param struct the struct to validate
     */
    public static void validate(long struct) {
        check(memGetAddress(struct + GLFWGammaRamp.RED));
        check(memGetAddress(struct + GLFWGammaRamp.GREEN));
        check(memGetAddress(struct + GLFWGammaRamp.BLUE));
    }

    /**
     * Calls {@link #validate(long)} for each struct contained in the specified struct array.
     *
     * @param array the struct array to validate
     * @param count the number of structs in {@code array}
     */
    public static void validate(long array, int count) {
        for (int i = 0; i < count; i++) {
            validate(array + Integer.toUnsignedLong(i) * SIZEOF);
        }
    }

    // -----------------------------------

    /** An array of {@link GLFWGammaRamp} structs. */
    public static class Buffer extends StructBuffer<GLFWGammaRamp, Buffer> implements NativeResource {

        private static final GLFWGammaRamp ELEMENT_FACTORY = GLFWGammaRamp.create(-1L);

        /**
         * Creates a new {@code GLFWGammaRamp.Buffer} instance backed by the specified container.
         *
         * Changes to the container's content will be visible to the struct buffer instance and vice versa. The two buffers' position, limit, and mark values
         * will be independent. The new buffer's position will be zero, its capacity and its limit will be the number of bytes remaining in this buffer divided
         * by {@link GLFWGammaRamp#SIZEOF}, and its mark will be undefined.
         *
         * <p>The created buffer instance holds a strong reference to the container object.</p>
         */
        public Buffer(ByteBuffer container) {
            super(container, container.remaining() / SIZEOF);
        }

        public Buffer(long address, int cap) {
            super(address, null, -1, 0, cap, cap);
        }

        Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
            super(address, container, mark, pos, lim, cap);
        }

        @Override
        protected Buffer self() {
            return this;
        }

        @Override
        protected GLFWGammaRamp getElementFactory() {
            return ELEMENT_FACTORY;
        }

        /** Returns a {@link ShortBuffer} view of the data pointed to by the {@code red} field. */
        @NativeType("unsigned short *")
        public ShortBuffer red() { return GLFWGammaRamp.nred(address()); }
        /** Returns a {@link ShortBuffer} view of the data pointed to by the {@code green} field. */
        @NativeType("unsigned short *")
        public ShortBuffer green() { return GLFWGammaRamp.ngreen(address()); }
        /** Returns a {@link ShortBuffer} view of the data pointed to by the {@code blue} field. */
        @NativeType("unsigned short *")
        public ShortBuffer blue() { return GLFWGammaRamp.nblue(address()); }
        /** Returns the value of the {@code size} field. */
        @NativeType("unsigned int")
        public int size() { return GLFWGammaRamp.nsize(address()); }

        /** Sets the address of the specified {@link ShortBuffer} to the {@code red} field. */
        public GLFWGammaRamp.Buffer red(@NativeType("unsigned short *") ShortBuffer value) { GLFWGammaRamp.nred(address(), value); return this; }
        /** Sets the address of the specified {@link ShortBuffer} to the {@code green} field. */
        public GLFWGammaRamp.Buffer green(@NativeType("unsigned short *") ShortBuffer value) { GLFWGammaRamp.ngreen(address(), value); return this; }
        /** Sets the address of the specified {@link ShortBuffer} to the {@code blue} field. */
        public GLFWGammaRamp.Buffer blue(@NativeType("unsigned short *") ShortBuffer value) { GLFWGammaRamp.nblue(address(), value); return this; }
        /** Sets the specified value to the {@code size} field. */
        public GLFWGammaRamp.Buffer size(@NativeType("unsigned int") int value) { GLFWGammaRamp.nsize(address(), value); return this; }

    }

}