/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.vulkan;

import javax.annotation.*;

import java.nio.*;

import org.lwjgl.*;
import org.lwjgl.system.*;

import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryStack.*;

/**
 * Structure specifying parameters of a newly created Android surface object.
 * 
 * <h5>Description</h5>
 * 
 * <h5>Valid Usage (Implicit)</h5>
 * 
 * <ul>
 * <li>{@code sType} <b>must</b> be {@link KHRAndroidSurface#VK_STRUCTURE_TYPE_ANDROID_SURFACE_CREATE_INFO_KHR STRUCTURE_TYPE_ANDROID_SURFACE_CREATE_INFO_KHR}</li>
 * <li>{@code pNext} <b>must</b> be {@code NULL}</li>
 * <li>{@code flags} <b>must</b> be 0</li>
 * </ul>
 * 
 * <h5>See Also</h5>
 * 
 * <p>{@link KHRAndroidSurface#vkCreateAndroidSurfaceKHR CreateAndroidSurfaceKHR}</p>
 * 
 * <h3>Member documentation</h3>
 * 
 * <ul>
 * <li>{@code sType} &ndash; the type of this structure.</li>
 * <li>{@code pNext} &ndash; {@code NULL} or a pointer to an extension-specific structure.</li>
 * <li>{@code flags} &ndash; reserved for future use.</li>
 * <li>{@code pWindow} &ndash; a reference to a {@code ANativeWindow} object that represents a renderable surface.</li>
 * </ul>
 * 
 * <h3>Layout</h3>
 * 
 * <pre><code>
 * struct VkAndroidSurfaceCreateInfoKHR {
 *     VkStructureType sType;
 *     void const * pNext;
 *     VkAndroidSurfaceCreateFlagsKHR flags;
 *     ANativeWindow const * pWindow;
 * }</code></pre>
 */
public class VkAndroidSurfaceCreateInfoKHR extends Struct implements NativeResource {

    /** The struct size in bytes. */
    public static final int SIZEOF;

    /** The struct alignment in bytes. */
    public static final int ALIGNOF;

    /** The struct member offsets. */
    public static final int
        STYPE,
        PNEXT,
        FLAGS,
        PWINDOW;

    static {
        Layout layout = __struct(
            __member(4),
            __member(POINTER_SIZE),
            __member(4),
            __member(POINTER_SIZE)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        STYPE = layout.offsetof(0);
        PNEXT = layout.offsetof(1);
        FLAGS = layout.offsetof(2);
        PWINDOW = layout.offsetof(3);
    }

    /**
     * Creates a {@code VkAndroidSurfaceCreateInfoKHR} instance at the current position of the specified {@link ByteBuffer} container. Changes to the buffer's content will be
     * visible to the struct instance and vice versa.
     *
     * <p>The created instance holds a strong reference to the container object.</p>
     */
    public VkAndroidSurfaceCreateInfoKHR(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    /** Returns the value of the {@code sType} field. */
    @NativeType("VkStructureType")
    public int sType() { return nsType(address()); }
    /** Returns the value of the {@code pNext} field. */
    @NativeType("void const *")
    public long pNext() { return npNext(address()); }
    /** Returns the value of the {@code flags} field. */
    @NativeType("VkAndroidSurfaceCreateFlagsKHR")
    public int flags() { return nflags(address()); }
    /**
     * Returns a {@link PointerBuffer} view of the data pointed to by the {@code pWindow} field.
     *
     * @param capacity the number of elements in the returned buffer
     */
    @NativeType("ANativeWindow const *")
    public PointerBuffer pWindow(int capacity) { return npWindow(address(), capacity); }

    /** Sets the specified value to the {@code sType} field. */
    public VkAndroidSurfaceCreateInfoKHR sType(@NativeType("VkStructureType") int value) { nsType(address(), value); return this; }
    /** Sets the specified value to the {@code pNext} field. */
    public VkAndroidSurfaceCreateInfoKHR pNext(@NativeType("void const *") long value) { npNext(address(), value); return this; }
    /** Sets the specified value to the {@code flags} field. */
    public VkAndroidSurfaceCreateInfoKHR flags(@NativeType("VkAndroidSurfaceCreateFlagsKHR") int value) { nflags(address(), value); return this; }
    /** Sets the address of the specified {@link PointerBuffer} to the {@code pWindow} field. */
    public VkAndroidSurfaceCreateInfoKHR pWindow(@NativeType("ANativeWindow const *") PointerBuffer value) { npWindow(address(), value); return this; }

    /** Initializes this struct with the specified values. */
    public VkAndroidSurfaceCreateInfoKHR set(
        int sType,
        long pNext,
        int flags,
        PointerBuffer pWindow
    ) {
        sType(sType);
        pNext(pNext);
        flags(flags);
        pWindow(pWindow);

        return this;
    }

    /**
     * Copies the specified struct data to this struct.
     *
     * @param src the source struct
     *
     * @return this struct
     */
    public VkAndroidSurfaceCreateInfoKHR set(VkAndroidSurfaceCreateInfoKHR src) {
        memCopy(src.address(), address(), SIZEOF);
        return this;
    }

    // -----------------------------------

    /** Returns a new {@code VkAndroidSurfaceCreateInfoKHR} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed. */
    public static VkAndroidSurfaceCreateInfoKHR malloc() {
        return wrap(VkAndroidSurfaceCreateInfoKHR.class, nmemAllocChecked(SIZEOF));
    }

    /** Returns a new {@code VkAndroidSurfaceCreateInfoKHR} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed. */
    public static VkAndroidSurfaceCreateInfoKHR calloc() {
        return wrap(VkAndroidSurfaceCreateInfoKHR.class, nmemCallocChecked(1, SIZEOF));
    }

    /** Returns a new {@code VkAndroidSurfaceCreateInfoKHR} instance allocated with {@link BufferUtils}. */
    public static VkAndroidSurfaceCreateInfoKHR create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return wrap(VkAndroidSurfaceCreateInfoKHR.class, memAddress(container), container);
    }

    /** Returns a new {@code VkAndroidSurfaceCreateInfoKHR} instance for the specified memory address. */
    public static VkAndroidSurfaceCreateInfoKHR create(long address) {
        return wrap(VkAndroidSurfaceCreateInfoKHR.class, address);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    @Nullable
    public static VkAndroidSurfaceCreateInfoKHR createSafe(long address) {
        return address == NULL ? null : wrap(VkAndroidSurfaceCreateInfoKHR.class, address);
    }

    /**
     * Returns a new {@link VkAndroidSurfaceCreateInfoKHR.Buffer} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static VkAndroidSurfaceCreateInfoKHR.Buffer malloc(int capacity) {
        return wrap(Buffer.class, nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
    }

    /**
     * Returns a new {@link VkAndroidSurfaceCreateInfoKHR.Buffer} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static VkAndroidSurfaceCreateInfoKHR.Buffer calloc(int capacity) {
        return wrap(Buffer.class, nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    /**
     * Returns a new {@link VkAndroidSurfaceCreateInfoKHR.Buffer} instance allocated with {@link BufferUtils}.
     *
     * @param capacity the buffer capacity
     */
    public static VkAndroidSurfaceCreateInfoKHR.Buffer create(int capacity) {
        ByteBuffer container = __create(capacity, SIZEOF);
        return wrap(Buffer.class, memAddress(container), capacity, container);
    }

    /**
     * Create a {@link VkAndroidSurfaceCreateInfoKHR.Buffer} instance at the specified memory.
     *
     * @param address  the memory address
     * @param capacity the buffer capacity
     */
    public static VkAndroidSurfaceCreateInfoKHR.Buffer create(long address, int capacity) {
        return wrap(Buffer.class, address, capacity);
    }

    /** Like {@link #create(long, int) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    @Nullable
    public static VkAndroidSurfaceCreateInfoKHR.Buffer createSafe(long address, int capacity) {
        return address == NULL ? null : wrap(Buffer.class, address, capacity);
    }

    // -----------------------------------

    /** Returns a new {@code VkAndroidSurfaceCreateInfoKHR} instance allocated on the thread-local {@link MemoryStack}. */
    public static VkAndroidSurfaceCreateInfoKHR mallocStack() {
        return mallocStack(stackGet());
    }

    /** Returns a new {@code VkAndroidSurfaceCreateInfoKHR} instance allocated on the thread-local {@link MemoryStack} and initializes all its bits to zero. */
    public static VkAndroidSurfaceCreateInfoKHR callocStack() {
        return callocStack(stackGet());
    }

    /**
     * Returns a new {@code VkAndroidSurfaceCreateInfoKHR} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     */
    public static VkAndroidSurfaceCreateInfoKHR mallocStack(MemoryStack stack) {
        return wrap(VkAndroidSurfaceCreateInfoKHR.class, stack.nmalloc(ALIGNOF, SIZEOF));
    }

    /**
     * Returns a new {@code VkAndroidSurfaceCreateInfoKHR} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     */
    public static VkAndroidSurfaceCreateInfoKHR callocStack(MemoryStack stack) {
        return wrap(VkAndroidSurfaceCreateInfoKHR.class, stack.ncalloc(ALIGNOF, 1, SIZEOF));
    }

    /**
     * Returns a new {@link VkAndroidSurfaceCreateInfoKHR.Buffer} instance allocated on the thread-local {@link MemoryStack}.
     *
     * @param capacity the buffer capacity
     */
    public static VkAndroidSurfaceCreateInfoKHR.Buffer mallocStack(int capacity) {
        return mallocStack(capacity, stackGet());
    }

    /**
     * Returns a new {@link VkAndroidSurfaceCreateInfoKHR.Buffer} instance allocated on the thread-local {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param capacity the buffer capacity
     */
    public static VkAndroidSurfaceCreateInfoKHR.Buffer callocStack(int capacity) {
        return callocStack(capacity, stackGet());
    }

    /**
     * Returns a new {@link VkAndroidSurfaceCreateInfoKHR.Buffer} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static VkAndroidSurfaceCreateInfoKHR.Buffer mallocStack(int capacity, MemoryStack stack) {
        return wrap(Buffer.class, stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    /**
     * Returns a new {@link VkAndroidSurfaceCreateInfoKHR.Buffer} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static VkAndroidSurfaceCreateInfoKHR.Buffer callocStack(int capacity, MemoryStack stack) {
        return wrap(Buffer.class, stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    // -----------------------------------

    /** Unsafe version of {@link #sType}. */
    public static int nsType(long struct) { return UNSAFE.getInt(null, struct + VkAndroidSurfaceCreateInfoKHR.STYPE); }
    /** Unsafe version of {@link #pNext}. */
    public static long npNext(long struct) { return memGetAddress(struct + VkAndroidSurfaceCreateInfoKHR.PNEXT); }
    /** Unsafe version of {@link #flags}. */
    public static int nflags(long struct) { return UNSAFE.getInt(null, struct + VkAndroidSurfaceCreateInfoKHR.FLAGS); }
    /** Unsafe version of {@link #pWindow(int) pWindow}. */
    public static PointerBuffer npWindow(long struct, int capacity) { return memPointerBuffer(memGetAddress(struct + VkAndroidSurfaceCreateInfoKHR.PWINDOW), capacity); }

    /** Unsafe version of {@link #sType(int) sType}. */
    public static void nsType(long struct, int value) { UNSAFE.putInt(null, struct + VkAndroidSurfaceCreateInfoKHR.STYPE, value); }
    /** Unsafe version of {@link #pNext(long) pNext}. */
    public static void npNext(long struct, long value) { memPutAddress(struct + VkAndroidSurfaceCreateInfoKHR.PNEXT, value); }
    /** Unsafe version of {@link #flags(int) flags}. */
    public static void nflags(long struct, int value) { UNSAFE.putInt(null, struct + VkAndroidSurfaceCreateInfoKHR.FLAGS, value); }
    /** Unsafe version of {@link #pWindow(PointerBuffer) pWindow}. */
    public static void npWindow(long struct, PointerBuffer value) { memPutAddress(struct + VkAndroidSurfaceCreateInfoKHR.PWINDOW, memAddress(value)); }

    /**
     * Validates pointer members that should not be {@code NULL}.
     *
     * @param struct the struct to validate
     */
    public static void validate(long struct) {
        check(memGetAddress(struct + VkAndroidSurfaceCreateInfoKHR.PWINDOW));
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

    /** An array of {@link VkAndroidSurfaceCreateInfoKHR} structs. */
    public static class Buffer extends StructBuffer<VkAndroidSurfaceCreateInfoKHR, Buffer> implements NativeResource {

        private static final VkAndroidSurfaceCreateInfoKHR ELEMENT_FACTORY = VkAndroidSurfaceCreateInfoKHR.create(-1L);

        /**
         * Creates a new {@code VkAndroidSurfaceCreateInfoKHR.Buffer} instance backed by the specified container.
         *
         * Changes to the container's content will be visible to the struct buffer instance and vice versa. The two buffers' position, limit, and mark values
         * will be independent. The new buffer's position will be zero, its capacity and its limit will be the number of bytes remaining in this buffer divided
         * by {@link VkAndroidSurfaceCreateInfoKHR#SIZEOF}, and its mark will be undefined.
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
        protected VkAndroidSurfaceCreateInfoKHR getElementFactory() {
            return ELEMENT_FACTORY;
        }

        /** Returns the value of the {@code sType} field. */
        @NativeType("VkStructureType")
        public int sType() { return VkAndroidSurfaceCreateInfoKHR.nsType(address()); }
        /** Returns the value of the {@code pNext} field. */
        @NativeType("void const *")
        public long pNext() { return VkAndroidSurfaceCreateInfoKHR.npNext(address()); }
        /** Returns the value of the {@code flags} field. */
        @NativeType("VkAndroidSurfaceCreateFlagsKHR")
        public int flags() { return VkAndroidSurfaceCreateInfoKHR.nflags(address()); }
        /**
         * Returns a {@link PointerBuffer} view of the data pointed to by the {@code pWindow} field.
         *
         * @param capacity the number of elements in the returned buffer
         */
        @NativeType("ANativeWindow const *")
        public PointerBuffer pWindow(int capacity) { return VkAndroidSurfaceCreateInfoKHR.npWindow(address(), capacity); }

        /** Sets the specified value to the {@code sType} field. */
        public VkAndroidSurfaceCreateInfoKHR.Buffer sType(@NativeType("VkStructureType") int value) { VkAndroidSurfaceCreateInfoKHR.nsType(address(), value); return this; }
        /** Sets the specified value to the {@code pNext} field. */
        public VkAndroidSurfaceCreateInfoKHR.Buffer pNext(@NativeType("void const *") long value) { VkAndroidSurfaceCreateInfoKHR.npNext(address(), value); return this; }
        /** Sets the specified value to the {@code flags} field. */
        public VkAndroidSurfaceCreateInfoKHR.Buffer flags(@NativeType("VkAndroidSurfaceCreateFlagsKHR") int value) { VkAndroidSurfaceCreateInfoKHR.nflags(address(), value); return this; }
        /** Sets the address of the specified {@link PointerBuffer} to the {@code pWindow} field. */
        public VkAndroidSurfaceCreateInfoKHR.Buffer pWindow(@NativeType("ANativeWindow const *") PointerBuffer value) { VkAndroidSurfaceCreateInfoKHR.npWindow(address(), value); return this; }

    }

}
