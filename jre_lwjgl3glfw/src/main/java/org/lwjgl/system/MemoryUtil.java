/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system;

import org.lwjgl.*;
import org.lwjgl.system.MemoryManage.*;
import org.lwjgl.system.MemoryUtil.MemoryAllocationReport.*;
import org.lwjgl.system.jni.*;

import javax.annotation.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;

import static java.lang.Character.*;
import static java.lang.Math.*;
import static org.lwjgl.system.APIUtil.*;
import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.MathUtil.*;
import static org.lwjgl.system.MemoryUtil.LazyInit.*;
import static org.lwjgl.system.Pointer.*;
import static org.lwjgl.system.jni.JNINativeInterface.*;
import static org.lwjgl.system.libc.LibCString.*;

/**
 * This class provides functionality for managing native memory.
 *
 * <p>All methods in this class will make use of {@link sun.misc.Unsafe} if it's available, for performance. If Unsafe is not available, the fallback
 * implementations make use of reflection and, in the worst-case, JNI.</p>
 *
 * <p>Method names in this class are prefixed with {@code mem} to avoid ambiguities when used with static imports.</p>
 *
 * <h3>Text encoding/decoding</h3>
 *
 * Three codecs are available, each with a different postfix:
 * <ul>
 * <li>UTF16 - Direct mapping of 2 bytes to Java char and vice versa</li>
 * <li>UTF8 - custom UTF-8 codec without intermediate allocations</li>
 * <li>ASCII - Not the original 7bit ASCII, but any character set with a single byte encoding (ISO 8859-1, Windows-1252, etc.)</li>
 * </ul>
 *
 * <p>Methods in bindings that accept/return {@code CharSequence}/{@code String} also support {@code ByteBuffer}, so custom codecs can be used if necessary.</p>
 *
 * @see Configuration#MEMORY_ALLOCATOR
 * @see Configuration#DEBUG_MEMORY_ALLOCATOR
 */
public final class MemoryUtil {

    /** Alias for the null pointer address. */
    public static final long NULL = 0L;

    /** The memory page size, in bytes. This value is always a power-of-two. */
    public static final int PAGE_SIZE;

    /** The cache-line size, in bytes. This value is always a power-of-two. */
    public static final int CACHE_LINE_SIZE;

    static final int ARRAY_TLC_SIZE = Configuration.ARRAY_TLC_SIZE.get(8192);

    static final ThreadLocal<byte[]> ARRAY_TLC_BYTE = ThreadLocal.withInitial(() -> new byte[ARRAY_TLC_SIZE]);
    static final ThreadLocal<char[]> ARRAY_TLC_CHAR = ThreadLocal.withInitial(() -> new char[ARRAY_TLC_SIZE]);

    static final sun.misc.Unsafe UNSAFE;

    static final ByteOrder NATIVE_ORDER = ByteOrder.nativeOrder();

    private static final Charset UTF16 = NATIVE_ORDER == ByteOrder.LITTLE_ENDIAN
        ? StandardCharsets.UTF_16LE
        : StandardCharsets.UTF_16BE;

    static final Class<? extends ByteBuffer>   BUFFER_BYTE;
    static final Class<? extends ShortBuffer>  BUFFER_SHORT;
    static final Class<? extends CharBuffer>   BUFFER_CHAR;
    static final Class<? extends IntBuffer>    BUFFER_INT;
    static final Class<? extends LongBuffer>   BUFFER_LONG;
    static final Class<? extends FloatBuffer>  BUFFER_FLOAT;
    static final Class<? extends DoubleBuffer> BUFFER_DOUBLE;

    private static final long MARK;
    private static final long POSITION;
    private static final long LIMIT;
    private static final long CAPACITY;

    private static final long ADDRESS;

    private static final long PARENT_BYTE;
    private static final long PARENT_SHORT;
    private static final long PARENT_CHAR;
    private static final long PARENT_INT;
    private static final long PARENT_LONG;
    private static final long PARENT_FLOAT;
    private static final long PARENT_DOUBLE;

    static {
        Library.initialize();

        //ACCESSOR = MemoryAccess.getInstance();
        ByteBuffer bb = ByteBuffer.allocateDirect(0).order(NATIVE_ORDER);

        BUFFER_BYTE = bb.getClass();
        BUFFER_SHORT = bb.asShortBuffer().getClass();
        BUFFER_CHAR = bb.asCharBuffer().getClass();
        BUFFER_INT = bb.asIntBuffer().getClass();
        BUFFER_LONG = bb.asLongBuffer().getClass();
        BUFFER_FLOAT = bb.asFloatBuffer().getClass();
        BUFFER_DOUBLE = bb.asDoubleBuffer().getClass();

        UNSAFE = getUnsafeInstance();

        try {
            MARK = getMarkOffset();
            POSITION = getPositionOffset();
            LIMIT = getLimitOffset();
            CAPACITY = getCapacityOffset();

            ADDRESS = getAddressOffset();

            int oopSize = UNSAFE.arrayIndexScale(Object[].class);

            long offset = (max(max(max(MARK, POSITION), LIMIT), CAPACITY) + 4 + (oopSize - 1)) & ~Integer.toUnsignedLong(oopSize - 1);
            long a      = memAddress(bb);

            PARENT_BYTE = getParentOffset(offset, oopSize, bb, bb.duplicate().order(bb.order()));
            PARENT_SHORT = getParentOffset(offset, oopSize, memShortBuffer(a, 0), bb.asShortBuffer());
            PARENT_CHAR = getParentOffset(offset, oopSize, memCharBuffer(a, 0), bb.asCharBuffer());
            PARENT_INT = getParentOffset(offset, oopSize, memIntBuffer(a, 0), bb.asIntBuffer());
            PARENT_LONG = getParentOffset(offset, oopSize, memLongBuffer(a, 0), bb.asLongBuffer());
            PARENT_FLOAT = getParentOffset(offset, oopSize, memFloatBuffer(a, 0), bb.asFloatBuffer());
            PARENT_DOUBLE = getParentOffset(offset, oopSize, memDoubleBuffer(a, 0), bb.asDoubleBuffer());
        } catch (Throwable t) {
            throw new UnsupportedOperationException(t);
        }

        PAGE_SIZE = UNSAFE.pageSize();
        CACHE_LINE_SIZE = 64; // TODO: Can we do better?
    }

    static final class LazyInit {

        private LazyInit() {
        }

        static final MemoryAllocator ALLOCATOR_IMPL;
        static final MemoryAllocator ALLOCATOR;

        static {
            ALLOCATOR_IMPL = MemoryManage.getInstance();
            ALLOCATOR = Configuration.DEBUG_MEMORY_ALLOCATOR.get(false)
                ? new DebugAllocator(ALLOCATOR_IMPL)
                : ALLOCATOR_IMPL;

            apiLog("MemoryUtil allocator: " + ALLOCATOR.getClass().getSimpleName());
        }
    }

    private MemoryUtil() {
    }

    /*  -------------------------------------
        -------------------------------------
            EXPLICIT MEMORY MANAGEMENT API
        -------------------------------------
        ------------------------------------- */

    /** The interface implemented by the memory allocator used by the explicit memory management API ({@link #memAlloc}, {@link #memFree}, etc). */
    public interface MemoryAllocator {

        /** Returns a pointer to the malloc function. */
        long getMalloc();
        /** Returns a pointer to the calloc function. */
        long getCalloc();
        /** Returns a pointer to the realloc function. */
        long getRealloc();
        /** Returns a pointer to the free function. */
        long getFree();
        /** Returns a pointer to the aligned_alloc function. */
        long getAlignedAlloc();
        /** Returns a pointer to the aligned_free function. */
        long getAlignedFree();

        /** Called by {@link MemoryUtil#memAlloc}. */
        long malloc(long size);
        /** Called by {@link MemoryUtil#memCalloc}. */
        long calloc(long num, long size);
        /** Called by {@link MemoryUtil#memRealloc}. */
        long realloc(long ptr, long size);
        /** Called by {@link MemoryUtil#memFree}. */
        void free(long ptr);

        /** Called by {@link MemoryUtil#memAlignedAlloc}. */
        long aligned_alloc(long alignment, long size);
        /** Called by {@link MemoryUtil#memAlignedFree}. */
        void aligned_free(long ptr);

    }

    /**
     * Returns the {@link MemoryAllocator} instance used internally by the explicit memory management API ({@link #memAlloc}, {@link #memFree}, etc).
     *
     * <p>Allocations made through the returned instance will not be tracked for memory leaks, even if {@link Configuration#DEBUG_MEMORY_ALLOCATOR} is enabled.
     * This can be useful for {@code static final} allocations that live throughout the application's lifetime and will never be freed until the process is
     * terminated. Normally such allocations would be reported as memory leaks by the debug allocator.</p>
     *
     * <p>The expectation is that this method will rarely be used, so it does not have the {@code mem} prefix to avoid pollution of auto-complete lists.</p>
     *
     * @return the {@link MemoryAllocator} instance
     */
    public static MemoryAllocator getAllocator() {
        return getAllocator(false);
    }

    /**
     * Returns the {@link MemoryAllocator} instance used internally by the explicit memory management API ({@link #memAlloc}, {@link #memFree}, etc).
     *
     * @param tracked whether allocations will be tracked for memory leaks, if {@link Configuration#DEBUG_MEMORY_ALLOCATOR} is enabled.
     *
     * @return the {@link MemoryAllocator} instance
     */
    public static MemoryAllocator getAllocator(boolean tracked) {
        return tracked
            ? ALLOCATOR
            : ALLOCATOR_IMPL;
    }

    // --- [ memAlloc ] ---

    /** Unsafe version of {@link #memAlloc}. May return {@link #NULL} if {@code size} is zero or the allocation failed. */
    public static long nmemAlloc(long size) {
        return ALLOCATOR.malloc(size);
    }

    /**
     * Unsafe version of {@link #memAlloc} that checks the returned pointer.
     *
     * @return a pointer to the memory block allocated by the function on success. This pointer will never be {@link #NULL}, even if {@code size} is zero.
     *
     * @throws OutOfMemoryError if the function failed to allocate the requested block of memory
     */
    public static long nmemAllocChecked(long size) {
        long address = nmemAlloc(size != 0 ? size : 1L);
        if (CHECKS && address == NULL) {
            throw new OutOfMemoryError();
        }
        return address;
    }

    private static long getAllocationSize(int elements, int elementShift) {
        return apiCheckAllocation(elements, Integer.toUnsignedLong(elements) << elementShift, BITS64 ? Long.MAX_VALUE : 0xFFFF_FFFFL);
    }

    /**
     * The standard C malloc function.
     *
     * <p>Allocates a block of {@code size} bytes of memory, returning a pointer to the beginning of the block. The content of the newly allocated block of
     * memory is not initialized, remaining with indeterminate values.</p>
     *
     * <p>Memory allocated with this method must be freed with {@link #memFree}.</p>
     *
     * @param size the size of the memory block to allocate, in bytes. If {@code size} is zero, the returned pointer shall not be dereferenced.
     *
     * @return on success, a pointer to the memory block allocated by the function
     *
     * @throws OutOfMemoryError if the function failed to allocate the requested block of memory
     */
    public static ByteBuffer memAlloc(int size) {
        return wrap(BUFFER_BYTE, nmemAllocChecked(size), size).order(NATIVE_ORDER);
    }

    /**
     * ShortBuffer version of {@link #memAlloc}.
     *
     * @param size the number of short values to allocate.
     */
    public static ShortBuffer memAllocShort(int size) {
        return wrap(BUFFER_SHORT, nmemAllocChecked(getAllocationSize(size, 1)), size);
    }

    /**
     * IntBuffer version of {@link #memAlloc}.
     *
     * @param size the number of int values to allocate.
     */
    public static IntBuffer memAllocInt(int size) {
        return wrap(BUFFER_INT, nmemAllocChecked(getAllocationSize(size, 2)), size);
    }

    /**
     * FloatBuffer version of {@link #memAlloc}.
     *
     * @param size the number of float values to allocate.
     */
    public static FloatBuffer memAllocFloat(int size) {
        return wrap(BUFFER_FLOAT, nmemAllocChecked(getAllocationSize(size, 2)), size);
    }

    /**
     * LongBuffer version of {@link #memAlloc}.
     *
     * @param size the number of long values to allocate.
     */
    public static LongBuffer memAllocLong(int size) {
        return wrap(BUFFER_LONG, nmemAllocChecked(getAllocationSize(size, 3)), size);
    }

    /**
     * {@code CLongBuffer} version of {@link #memAlloc}.
     *
     * @param size the number of C long values to allocate.
     */
    public static CLongBuffer memAllocCLong(int size) {
        return Pointer.Default.wrap(CLongBuffer.class, nmemAllocChecked(getAllocationSize(size, CLONG_SHIFT)), size);
    }

    /**
     * DoubleBuffer version of {@link #memAlloc}.
     *
     * @param size the number of double values to allocate.
     */
    public static DoubleBuffer memAllocDouble(int size) {
        return wrap(BUFFER_DOUBLE, nmemAllocChecked(getAllocationSize(size, 3)), size);
    }

    /**
     * PointerBuffer version of {@link #memAlloc}.
     *
     * @param size the number of pointer values to allocate.
     */
    public static PointerBuffer memAllocPointer(int size) {
        return Pointer.Default.wrap(PointerBuffer.class, nmemAllocChecked(getAllocationSize(size, POINTER_SHIFT)), size);
    }

    /** Unsafe version of {@link #memFree}. */
    public static void nmemFree(long ptr) {
        ALLOCATOR.free(ptr);
    }

    /**
     * The standard C free function.
     *
     * <p>A block of memory previously allocated by a call to {@link #memAlloc}, {@link #memCalloc} or {@link #memRealloc} is deallocated, making it available
     * again for further allocations.</p>
     *
     * @param ptr pointer to a memory block previously allocated with {@link #memAlloc}, {@link #memCalloc} or {@link #memRealloc}. If {@code ptr} does not
     *            point to a block of memory allocated with the above functions, it causes undefined behavior. If {@code ptr} is a {@link #NULL} pointer, the
     *            function does nothing.
     */
    public static void memFree(@Nullable Buffer ptr) {
        if (ptr != null) {
            nmemFree(UNSAFE.getLong(ptr, ADDRESS));
        }
    }

    /** {@code CustomBuffer} version of {@link #memFree}. */
    public static void memFree(@Nullable CustomBuffer ptr) {
        if (ptr != null) {
            nmemFree(ptr.address);
        }
    }

    // from LWJGL 3.2.2
    /** {@code PointerBuffer} version of {@link #memFree}. */
    public static void memFree(@Nullable PointerBuffer ptr) {
        if (ptr != null) {
            nmemFree(ptr.address);
        }
    }

    // --- [ memCalloc ] ---

    /** Unsafe version of {@link #memCalloc}. May return {@link #NULL} if {@code num} or {@code size} are zero or the allocation failed. */
    public static long nmemCalloc(long num, long size) {
        return ALLOCATOR.calloc(num, size);
    }

    /**
     * Unsafe version of {@link #memCalloc} that checks the returned pointer.
     *
     * @return a pointer to the memory block allocated by the function on success. This pointer will never be {@link #NULL}, even if {@code num} or
     * {@code size} are zero.
     *
     * @throws OutOfMemoryError if the function failed to allocate the requested block of memory
     */
    public static long nmemCallocChecked(long num, long size) {
        if (num == 0L || size == 0L) {
            num = 1L;
            size = 1L;
        }

        long address = nmemCalloc(num, size);
        if (CHECKS && address == NULL) {
            throw new OutOfMemoryError();
        }
        return address;
    }

    /**
     * The standard C calloc function.
     *
     * <p>Allocates a block of memory for an array of {@code num} elements, each of them {@code size} bytes long, and initializes all its bits to zero. The
     * effective result is the allocation of a zero-initialized memory block of {@code (num*size)} bytes.</p>
     *
     * <p>Memory allocated with this method must be freed with {@link #memFree}.</p>
     *
     * @param num  the number of elements to allocate.
     * @param size the size of each element. If {@code size} is zero, the return value depends on the particular library implementation (it may or may not be
     *             a null pointer), but the returned pointer shall not be dereferenced.
     *
     * @return on success, a pointer to the memory block allocated by the function
     *
     * @throws OutOfMemoryError if the function failed to allocate the requested block of memory
     */
    public static ByteBuffer memCalloc(int num, int size) {
        return wrap(BUFFER_BYTE, nmemCallocChecked(num, size), num * size).order(NATIVE_ORDER);
    }

    /**
     * Alternative version of {@link #memCalloc}.
     *
     * @param num the number of bytes to allocate.
     */
    public static ByteBuffer memCalloc(int num) {
        return wrap(BUFFER_BYTE, nmemCallocChecked(num, 1), num).order(NATIVE_ORDER);
    }

    /**
     * ShortBuffer version of {@link #memCalloc}.
     *
     * @param num the number of short values to allocate.
     */
    public static ShortBuffer memCallocShort(int num) {
        return wrap(BUFFER_SHORT, nmemCallocChecked(num, 2), num);
    }

    /**
     * IntBuffer version of {@link #memCalloc}.
     *
     * @param num the number of int values to allocate.
     */
    public static IntBuffer memCallocInt(int num) {
        return wrap(BUFFER_INT, nmemCallocChecked(num, 4), num);
    }

    /**
     * FloatBuffer version of {@link #memCalloc}.
     *
     * @param num the number of float values to allocate.
     */
    public static FloatBuffer memCallocFloat(int num) {
        return wrap(BUFFER_FLOAT, nmemCallocChecked(num, 4), num);
    }

    /**
     * LongBuffer version of {@link #memCalloc}.
     *
     * @param num the number of long values to allocate.
     */
    public static LongBuffer memCallocLong(int num) {
        return wrap(BUFFER_LONG, nmemCallocChecked(num, 8), num);
    }

    /**
     * {@code CLongBuffer} version of {@link #memCalloc}.
     *
     * @param num the number of C long values to allocate.
     */
    public static CLongBuffer memCallocCLong(int num) {
        return Pointer.Default.wrap(CLongBuffer.class, nmemCallocChecked(num, CLONG_SIZE), num);
    }

    /**
     * DoubleBuffer version of {@link #memCalloc}.
     *
     * @param num the number of double values to allocate.
     */
    public static DoubleBuffer memCallocDouble(int num) {
        return wrap(BUFFER_DOUBLE, nmemCallocChecked(num, 8), num);
    }

    /**
     * PointerBuffer version of {@link #memCalloc}.
     *
     * @param num the number of pointer values to allocate.
     */
    public static PointerBuffer memCallocPointer(int num) {
        return Pointer.Default.wrap(PointerBuffer.class, nmemCallocChecked(num, POINTER_SIZE), num);
    }

    // --- [ memRealloc] ---

    /** Unsafe version of {@link #memRealloc}. May return {@link #NULL} if {@code size} is zero or the allocation failed. */
    public static long nmemRealloc(long ptr, long size) {
        return ALLOCATOR.realloc(ptr, size);
    }

    /**
     * Unsafe version of {@link #memRealloc} that checks the returned pointer.
     *
     * @return a pointer to the memory block reallocated by the function on success. This pointer will never be {@link #NULL}, even if {@code size} is zero.
     *
     * @throws OutOfMemoryError if the function failed to allocate the requested block of memory
     */
    public static long nmemReallocChecked(long ptr, long size) {
        long address = nmemRealloc(ptr, size != 0 ? size : 1L);
        if (CHECKS && address == NULL) {
            throw new OutOfMemoryError();
        }
        return address;
    }

    private static <T extends Buffer> T realloc(@Nullable T old_p, T new_p, int size) {
        if (old_p != null) {
            new_p.position(min(old_p.position(), size));
        }
        return new_p;
    }

    /**
     * The standard C realloc function.
     *
     * <p>Changes the size of the memory block pointed to by {@code ptr}. The function may move the memory block to a new location (whose address is returned
     * by the function). The content of the memory block is preserved up to the lesser of the new and old sizes, even if the block is moved to a new location.
     * If the new size is larger, the value of the newly allocated portion is indeterminate.</p>
     *
     * <p>The memory address used is always the address at the start of {@code ptr}, so the current position of {@code ptr} does not need to be set to 0 for
     * this function to work. The current position is preserved, even if the memory block is moved to a new location, unless {@code size} is less than the
     * current position in which case position will be equal to capacity. The limit is set to the capacity, and the mark is discarded.</p>
     *
     * @param ptr  a pointer to a memory block previously allocated with {@link #memAlloc}, {@link #memCalloc} or {@link #memRealloc}. Alternatively, this can
     *             be a {@link #NULL} pointer, in which case a new block is allocated (as if {@link #memAlloc} was called).
     * @param size the new size for the memory block, in bytes.
     *
     * @return a pointer to the reallocated memory block, which may be either the same as {@code ptr} or a new location
     *
     * @throws OutOfMemoryError if the function failed to allocate the requested block of memory. The memory block pointed to by argument {@code ptr} is not
     *                          deallocated (it is still valid, and with its contents unchanged).
     */
    public static ByteBuffer memRealloc(@Nullable ByteBuffer ptr, int size) {
        return realloc(ptr, memByteBuffer(nmemReallocChecked(ptr == null ? NULL : UNSAFE.getLong(ptr, ADDRESS), size), size), size);
    }

    /**
     * ShortBuffer version of {@link #memRealloc}.
     *
     * @param size the number of short values to allocate.
     */
    public static ShortBuffer memRealloc(@Nullable ShortBuffer ptr, int size) {
        return realloc(ptr, memShortBuffer(nmemReallocChecked(ptr == null ? NULL : UNSAFE.getLong(ptr, ADDRESS), getAllocationSize(size, 1)), size), size);
    }

    /**
     * IntBuffer version of {@link #memRealloc}.
     *
     * @param size the number of int values to allocate.
     */
    public static IntBuffer memRealloc(@Nullable IntBuffer ptr, int size) {
        return realloc(ptr, memIntBuffer(nmemReallocChecked(ptr == null ? NULL : UNSAFE.getLong(ptr, ADDRESS), getAllocationSize(size, 2)), size), size);
    }

    /**
     * LongBuffer version of {@link #memRealloc}.
     *
     * @param size the number of long values to allocate.
     */
    public static LongBuffer memRealloc(@Nullable LongBuffer ptr, int size) {
        return realloc(ptr, memLongBuffer(nmemReallocChecked(ptr == null ? NULL : UNSAFE.getLong(ptr, ADDRESS), getAllocationSize(size, 3)), size), size);
    }

    /**
     * {@code CLongBuffer} version of {@link #memRealloc}.
     *
     * @param size the number of C long values to allocate.
     */
    public static CLongBuffer memRealloc(@Nullable CLongBuffer ptr, int size) {
        CLongBuffer buffer = memCLongBuffer(nmemReallocChecked(ptr == null ? NULL : ptr.address, getAllocationSize(size, CLONG_SIZE)), size);
        if (ptr != null) {
            buffer.position(min(ptr.position(), size));
        }
        return buffer;
    }

    /**
     * FloatBuffer version of {@link #memRealloc}.
     *
     * @param size the number of float values to allocate.
     */
    public static FloatBuffer memRealloc(@Nullable FloatBuffer ptr, int size) {
        return realloc(ptr, memFloatBuffer(nmemReallocChecked(ptr == null ? NULL : UNSAFE.getLong(ptr, ADDRESS), getAllocationSize(size, 2)), size), size);
    }

    /**
     * DoubleBuffer version of {@link #memRealloc}.
     *
     * @param size the number of double values to allocate.
     */
    public static DoubleBuffer memRealloc(@Nullable DoubleBuffer ptr, int size) {
        return realloc(ptr, memDoubleBuffer(nmemReallocChecked(ptr == null ? NULL : UNSAFE.getLong(ptr, ADDRESS), getAllocationSize(size, 3)), size), size);
    }

    /**
     * PointerBuffer version of {@link #memRealloc}.
     *
     * @param size the number of pointer values to allocate.
     */
    public static PointerBuffer memRealloc(@Nullable PointerBuffer ptr, int size) {
        PointerBuffer buffer = memPointerBuffer(nmemReallocChecked(ptr == null ? NULL : ptr.address, getAllocationSize(size, POINTER_SHIFT)), size);
        if (ptr != null) {
            buffer.position(min(ptr.position(), size));
        }
        return buffer;
    }

    // --- [ memAlignedAlloc ] ---

    /** Unsafe version of {@link #memAlignedAlloc}. May return {@link #NULL} if {@code size} is zero or the allocation failed. */
    public static long nmemAlignedAlloc(long alignment, long size) {
        return ALLOCATOR.aligned_alloc(alignment, size);
    }

    /**
     * Unsafe version of {@link #memAlignedAlloc} that checks the returned pointer.
     *
     * @return a pointer to the memory block allocated by the function on success. This pointer will never be {@link #NULL}, even if {@code size} is zero.
     *
     * @throws OutOfMemoryError if the function failed to allocate the requested block of memory
     */
    public static long nmemAlignedAllocChecked(long alignment, long size) {
        long address = nmemAlignedAlloc(alignment, size != 0 ? size : 1L);
        if (CHECKS && address == NULL) {
            throw new OutOfMemoryError();
        }
        return address;
    }

    /**
     * The standard C aligned_alloc function.
     *
     * <p>Allocate {@code size} bytes of uninitialized storage whose alignment is specified by {@code alignment}. The size parameter must be an integral
     * multiple of alignment. Memory allocated with memAlignedAlloc() must be freed with {@link #memAlignedFree}.</p>
     *
     * @param alignment the alignment. Must be a power of two value and a multiple of {@code sizeof(void *)}.
     * @param size      the number of bytes to allocate. Must be a multiple of {@code alignment}.
     */
    public static ByteBuffer memAlignedAlloc(int alignment, int size) {
        return wrap(BUFFER_BYTE, nmemAlignedAllocChecked(alignment, size), size).order(NATIVE_ORDER);
    }

    // --- [ memAlignedFree ] ---

    /** Unsafe version of {@link #memAlignedFree}. */
    public static void nmemAlignedFree(long ptr) {
        ALLOCATOR.aligned_free(ptr);
    }

    /**
     * Frees a block of memory that was allocated with {@link #memAlignedAlloc}. If ptr is {@code NULL}, no operation is performed.
     *
     * @param ptr the aligned block of memory to free
     */
    public static void memAlignedFree(@Nullable ByteBuffer ptr) {
        if (ptr != null) {
            nmemAlignedFree(UNSAFE.getLong(ptr, ADDRESS));
        }
    }

    // --- [ DebugAllocator ] ---

    /** The memory allocation report callback. */
    public interface MemoryAllocationReport {

        /**
         * Reports allocated memory.
         *
         * @param address    the address of the memory allocated. May be {@link #NULL}.
         * @param memory     the amount of memory allocated, in bytes
         * @param threadId   id of the thread that allocated the memory. May be {@link #NULL}.
         * @param threadName name of the thread that allocated the memory. May be {@code null}.
         * @param stacktrace the allocation stacktrace. May be {@code null}.
         */
        void invoke(long address, long memory, long threadId, @Nullable String threadName, @Nullable StackTraceElement... stacktrace);

        /** Specifies how to aggregate the reported allocations. */
        enum Aggregate {
            /** Allocations are aggregated over the whole process or thread. */
            ALL,
            /**
             * Allocations are aggregated based on the first stack trace element. This will return an allocation aggregate per method/line number, regardless
             * of how many different code paths lead to that specific method and line number.
             */
            GROUP_BY_METHOD,
            /** The allocations are aggregated based on the full stack trace chain. */
            GROUP_BY_STACKTRACE
        }
    }

    /**
     * Reports all live allocations.
     *
     * <p>This method can only be used if the {@link Configuration#DEBUG_MEMORY_ALLOCATOR} option has been set to true.</p>
     *
     * @param report the report callback
     */
    public static void memReport(MemoryAllocationReport report) {
        DebugAllocator.report(report);
    }

    /**
     * Reports aggregates for the live allocations.
     *
     * <p>This method can only be used if the {@link Configuration#DEBUG_MEMORY_ALLOCATOR} option has been set to true.</p>
     *
     * @param report            the report callback
     * @param groupByStackTrace how to aggregate the reported allocations
     * @param groupByThread     if the reported allocations should be grouped by thread
     */
    public static void memReport(MemoryAllocationReport report, Aggregate groupByStackTrace, boolean groupByThread) {
        DebugAllocator.report(report, groupByStackTrace, groupByThread);
    }

    /*  -------------------------------------
        -------------------------------------
                BUFFER MANAGEMENT API
        -------------------------------------
        ------------------------------------- */

    // --- [ memAddress0 ] ---

    /**
     * Returns the memory address of the specified buffer. [INTERNAL USE ONLY]
     *
     * @param buffer the buffer
     *
     * @return the memory address
     */
    public static long memAddress0(Buffer buffer) { return UNSAFE.getLong(buffer, ADDRESS); }

    // --- [ Buffer address ] ---

    /**
     * Returns the memory address at the current position of the specified buffer. This is effectively a pointer value that can be used in native function
     * calls.
     *
     * @param buffer the buffer
     *
     * @return the memory address
     */
    public static long memAddress(ByteBuffer buffer) { return buffer.position() + memAddress0(buffer); }

    /**
     * Returns the memory address at the specified position of the specified buffer.
     *
     * @param buffer   the buffer
     * @param position the buffer position
     *
     * @return the memory address
     *
     * @see #memAddress(ByteBuffer)
     */
    public static long memAddress(ByteBuffer buffer, int position) {
        Objects.requireNonNull(buffer);
        return memAddress0(buffer) + Integer.toUnsignedLong(position);
    }

    private static long address(int position, int elementShift, long address) {
        return address + ((position & 0xFFFF_FFFFL) << elementShift);
    }

    /** ShortBuffer version of {@link #memAddress(ByteBuffer)}. */
    public static long memAddress(ShortBuffer buffer) { return address(buffer.position(), 1, memAddress0(buffer)); }
    /** ShortBuffer version of {@link #memAddress(ByteBuffer, int)}. */
    public static long memAddress(ShortBuffer buffer, int position) {
        Objects.requireNonNull(buffer);
        return address(position, 1, memAddress0(buffer));
    }

    /** CharBuffer version of {@link #memAddress(ByteBuffer)}. */
    public static long memAddress(CharBuffer buffer) { return address(buffer.position(), 1, memAddress0(buffer)); }
    /** CharBuffer version of {@link #memAddress(ByteBuffer, int)}. */
    public static long memAddress(CharBuffer buffer, int position) {
        Objects.requireNonNull(buffer);
        return address(position, 1, memAddress0(buffer));
    }

    /** IntBuffer version of {@link #memAddress(ByteBuffer)}. */
    public static long memAddress(IntBuffer buffer) { return address(buffer.position(), 2, memAddress0(buffer)); }
    /** IntBuffer version of {@link #memAddress(ByteBuffer, int)}. */
    public static long memAddress(IntBuffer buffer, int position) {
        Objects.requireNonNull(buffer);
        return address(position, 2, memAddress0(buffer));
    }

    /** FloatBuffer version of {@link #memAddress(ByteBuffer)}. */
    public static long memAddress(FloatBuffer buffer) { return address(buffer.position(), 2, memAddress0(buffer)); }
    /** FloatBuffer version of {@link #memAddress(ByteBuffer, int)}. */
    public static long memAddress(FloatBuffer buffer, int position) {
        Objects.requireNonNull(buffer);
        return address(position, 2, memAddress0(buffer));
    }

    /** LongBuffer version of {@link #memAddress(ByteBuffer)}. */
    public static long memAddress(LongBuffer buffer) { return address(buffer.position(), 3, memAddress0(buffer)); }
    /** LongBuffer version of {@link #memAddress(ByteBuffer, int)}. */
    public static long memAddress(LongBuffer buffer, int position) {
        Objects.requireNonNull(buffer);
        return address(position, 3, memAddress0(buffer));
    }

    /** DoubleBuffer version of {@link #memAddress(ByteBuffer)}. */
    public static long memAddress(DoubleBuffer buffer) { return address(buffer.position(), 3, memAddress0(buffer)); }
    /** DoubleBuffer version of {@link #memAddress(ByteBuffer, int)}. */
    public static long memAddress(DoubleBuffer buffer, int position) {
        Objects.requireNonNull(buffer);
        return address(position, 3, memAddress0(buffer));
    }

    /** Polymorphic version of {@link #memAddress(ByteBuffer)}. */
    public static long memAddress(Buffer buffer) {
        int elementShift;
        if (buffer instanceof ByteBuffer) {
            elementShift = 0;
        } else if (buffer instanceof ShortBuffer || buffer instanceof CharBuffer) {
            elementShift = 1;
        } else if (buffer instanceof IntBuffer || buffer instanceof FloatBuffer) {
            elementShift = 2;
        } else {
            elementShift = 3;
        }
        return address(buffer.position(), elementShift, memAddress0(buffer));
    }

    /** CustomBuffer version of {@link #memAddress(ByteBuffer)}. */
    public static long memAddress(CustomBuffer<?> buffer) { return buffer.address(); }
    /** CustomBuffer version of {@link #memAddress(ByteBuffer, int)}. */
    public static long memAddress(CustomBuffer<?> buffer, int position) { return buffer.address(position); }

    // --- [ Buffer address - Safe ] ---

    /** Null-safe version of {@link #memAddress(ByteBuffer)}. Returns {@link #NULL} if the specified buffer is null. */
    public static long memAddressSafe(@Nullable ByteBuffer buffer) { return buffer == null ? NULL : memAddress0(buffer) + buffer.position(); }

    /** ShortBuffer version of {@link #memAddressSafe(ByteBuffer)}. */
    public static long memAddressSafe(@Nullable ShortBuffer buffer) { return buffer == null ? NULL : address(buffer.position(), 1, memAddress0(buffer)); }

    /** CharBuffer version of {@link #memAddressSafe(ByteBuffer)}. */
    public static long memAddressSafe(@Nullable CharBuffer buffer) { return buffer == null ? NULL : address(buffer.position(), 1, memAddress0(buffer)); }

    /** IntBuffer version of {@link #memAddressSafe(ByteBuffer)}. */
    public static long memAddressSafe(@Nullable IntBuffer buffer) { return buffer == null ? NULL : address(buffer.position(), 2, memAddress0(buffer)); }

    /** FloatBuffer version of {@link #memAddressSafe(ByteBuffer)}. */
    public static long memAddressSafe(@Nullable FloatBuffer buffer) { return buffer == null ? NULL : address(buffer.position(), 2, memAddress0(buffer)); }

    /** LongBuffer version of {@link #memAddressSafe(ByteBuffer)}. */
    public static long memAddressSafe(@Nullable LongBuffer buffer) { return buffer == null ? NULL : address(buffer.position(), 3, memAddress0(buffer)); }

    /** DoubleBuffer version of {@link #memAddressSafe(ByteBuffer)}. */
    public static long memAddressSafe(@Nullable DoubleBuffer buffer) { return buffer == null ? NULL : address(buffer.position(), 3, memAddress0(buffer)); }

    /** Pointer version of {@link #memAddressSafe(ByteBuffer)}. */
    public static long memAddressSafe(@Nullable Pointer pointer) { return pointer == null ? NULL : pointer.address(); }

    // --- [ Buffer allocation ] ---

    /**
     * Creates a new direct ByteBuffer that starts at the specified memory address and has the specified capacity. The returned ByteBuffer instance will be set
     * to the native {@link ByteOrder}.
     *
     * @param address  the starting memory address
     * @param capacity the buffer capacity
     *
     * @return the new ByteBuffer
     */
    public static ByteBuffer memByteBuffer(long address, int capacity) {
        if (CHECKS) {
            check(address);
        }
        return wrap(BUFFER_BYTE, address, capacity).order(NATIVE_ORDER);
    }

    /** Like {@link #memByteBuffer}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static ByteBuffer memByteBufferSafe(long address, int capacity) {
        return address == NULL ? null : wrap(BUFFER_BYTE, address, capacity).order(NATIVE_ORDER);
    }

    /**
     * Creates a {@link ByteBuffer} instance as a view of the specified {@link ShortBuffer} between its current position and limit.
     *
     * <p>This operation is the inverse of {@link ByteBuffer#asShortBuffer()}. The returned {@code ByteBuffer} instance will be set to the native
     * {@link ByteOrder}.</p>
     *
     * @param buffer the source buffer
     *
     * @return the {@code ByteBuffer} view
     */
    public static ByteBuffer memByteBuffer(ShortBuffer buffer) {
        if (CHECKS && (Integer.MAX_VALUE >> 1) < buffer.remaining()) {
            throw new IllegalArgumentException("The source buffer range is too wide");
        }
        return wrap(BUFFER_BYTE, memAddress(buffer), buffer.remaining() << 1).order(NATIVE_ORDER);
    }

    /**
     * Creates a {@link ByteBuffer} instance as a view of the specified {@link CharBuffer} between its current position and limit.
     *
     * <p>This operation is the inverse of {@link ByteBuffer#asCharBuffer()}. The returned {@code ByteBuffer} instance will be set to the native
     * {@link ByteOrder}.</p>
     *
     * @param buffer the source buffer
     *
     * @return the {@code ByteBuffer} view
     */
    public static ByteBuffer memByteBuffer(CharBuffer buffer) {
        if (CHECKS && (Integer.MAX_VALUE >> 1) < buffer.remaining()) {
            throw new IllegalArgumentException("The source buffer range is too wide");
        }
        return wrap(BUFFER_BYTE, memAddress(buffer), buffer.remaining() << 1).order(NATIVE_ORDER);
    }

    /**
     * Creates a {@link ByteBuffer} instance as a view of the specified {@link IntBuffer} between its current position and limit.
     *
     * <p>This operation is the inverse of {@link ByteBuffer#asIntBuffer()}. The returned {@code ByteBuffer} instance will be set to the native
     * {@link ByteOrder}.</p>
     *
     * @param buffer the source buffer
     *
     * @return the {@code ByteBuffer} view
     */
    public static ByteBuffer memByteBuffer(IntBuffer buffer) {
        if (CHECKS && (Integer.MAX_VALUE >> 2) < buffer.remaining()) {
            throw new IllegalArgumentException("The source buffer range is too wide");
        }
        return wrap(BUFFER_BYTE, memAddress(buffer), buffer.remaining() << 2).order(NATIVE_ORDER);
    }

    /**
     * Creates a {@link ByteBuffer} instance as a view of the specified {@link LongBuffer} between its current position and limit.
     *
     * <p>This operation is the inverse of {@link ByteBuffer#asLongBuffer()}. The returned {@code ByteBuffer} instance will be set to the native
     * {@link ByteOrder}.</p>
     *
     * @param buffer the source buffer
     *
     * @return the {@code ByteBuffer} view
     */
    public static ByteBuffer memByteBuffer(LongBuffer buffer) {
        if (CHECKS && (Integer.MAX_VALUE >> 3) < buffer.remaining()) {
            throw new IllegalArgumentException("The source buffer range is too wide");
        }
        return wrap(BUFFER_BYTE, memAddress(buffer), buffer.remaining() << 3).order(NATIVE_ORDER);
    }

    /**
     * Creates a {@link ByteBuffer} instance as a view of the specified {@link FloatBuffer} between its current position and limit.
     *
     * <p>This operation is the inverse of {@link ByteBuffer#asFloatBuffer()}. The returned {@code ByteBuffer} instance will be set to the native
     * {@link ByteOrder}.</p>
     *
     * @param buffer the source buffer
     *
     * @return the {@code ByteBuffer} view
     */
    public static ByteBuffer memByteBuffer(FloatBuffer buffer) {
        if (CHECKS && (Integer.MAX_VALUE >> 2) < buffer.remaining()) {
            throw new IllegalArgumentException("The source buffer range is too wide");
        }
        return wrap(BUFFER_BYTE, memAddress(buffer), buffer.remaining() << 2).order(NATIVE_ORDER);
    }

    /**
     * Creates a {@link ByteBuffer} instance as a view of the specified {@link DoubleBuffer} between its current position and limit.
     *
     * <p>This operation is the inverse of {@link ByteBuffer#asDoubleBuffer()}. The returned {@code ByteBuffer} instance will be set to the native
     * {@link ByteOrder}.</p>
     *
     * @param buffer the source buffer
     *
     * @return the {@code ByteBuffer} view
     */
    public static ByteBuffer memByteBuffer(DoubleBuffer buffer) {
        if (CHECKS && (Integer.MAX_VALUE >> 3) < buffer.remaining()) {
            throw new IllegalArgumentException("The source buffer range is too wide");
        }
        return wrap(BUFFER_BYTE, memAddress(buffer), buffer.remaining() << 3).order(NATIVE_ORDER);
    }

    /**
     * Creates a {@link ByteBuffer} instance as a view of the specified {@link CustomBuffer} between its current position and limit.
     *
     * <p>The returned {@code ByteBuffer} instance will be set to the native {@link ByteOrder}.</p>
     *
     * @param buffer the source buffer
     *
     * @return the {@code ByteBuffer} view
     */
    public static ByteBuffer memByteBuffer(CustomBuffer<?> buffer) {
        if (CHECKS && (Integer.MAX_VALUE / buffer.sizeof()) < buffer.remaining()) {
            throw new IllegalArgumentException("The source buffer range is too wide");
        }
        return wrap(BUFFER_BYTE, memAddress(buffer), buffer.remaining() * buffer.sizeof()).order(NATIVE_ORDER);
    }

    /**
     * Creates a new direct ShortBuffer that starts at the specified memory address and has the specified capacity.
     *
     * <p>The {@code address} specified must be aligned to 2 bytes. If not, use {@code memByteBuffer(address, capacity * 2).asShortBuffer()}.</p>
     *
     * @param address  the starting memory address
     * @param capacity the buffer capacity
     *
     * @return the new ShortBuffer
     */
    public static ShortBuffer memShortBuffer(long address, int capacity) {
        if (CHECKS) {
            check(address);
        }
        return wrap(BUFFER_SHORT, address, capacity);
    }

    /** Like {@link #memShortBuffer}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static ShortBuffer memShortBufferSafe(long address, int capacity) {
        return address == NULL ? null : wrap(BUFFER_SHORT, address, capacity);
    }

    /**
     * Creates a new direct CharBuffer that starts at the specified memory address and has the specified capacity.
     *
     * <p>The {@code address} specified must be aligned to 2 bytes. If not, use {@code memByteBuffer(address, capacity * 2).asCharBuffer()}.</p>
     *
     * @param address  the starting memory address
     * @param capacity the buffer capacity
     *
     * @return the new CharBuffer
     */
    public static CharBuffer memCharBuffer(long address, int capacity) {
        if (CHECKS) {
            check(address);
        }
        return wrap(BUFFER_CHAR, address, capacity);
    }

    /** Like {@link #memCharBuffer}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static CharBuffer memCharBufferSafe(long address, int capacity) {
        return address == NULL ? null : wrap(BUFFER_CHAR, address, capacity);
    }

    /**
     * Creates a new direct IntBuffer that starts at the specified memory address and has the specified capacity.
     *
     * <p>The {@code address} specified must be aligned to 4 bytes. If not, use {@code memByteBuffer(address, capacity * 4).asIntBuffer()}.</p>
     *
     * @param address  the starting memory address
     * @param capacity the buffer capacity
     *
     * @return the new IntBuffer
     */
    public static IntBuffer memIntBuffer(long address, int capacity) {
        if (CHECKS) {
            check(address);
        }
        return wrap(BUFFER_INT, address, capacity);
    }

    /** Like {@link #memIntBuffer}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static IntBuffer memIntBufferSafe(long address, int capacity) {
        return address == NULL ? null : wrap(BUFFER_INT, address, capacity);
    }

    /**
     * Creates a new direct LongBuffer that starts at the specified memory address and has the specified capacity.
     *
     * <p>The {@code address} specified must be aligned to 8 bytes. If not, use {@code memByteBuffer(address, capacity * 8).asLongBuffer()}.</p>
     *
     * @param address  the starting memory address
     * @param capacity the buffer capacity
     *
     * @return the new LongBuffer
     */
    public static LongBuffer memLongBuffer(long address, int capacity) {
        if (CHECKS) {
            check(address);
        }
        return wrap(BUFFER_LONG, address, capacity);
    }

    /** Like {@link #memLongBuffer}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static LongBuffer memLongBufferSafe(long address, int capacity) {
        return address == NULL ? null : wrap(BUFFER_LONG, address, capacity);
    }

    /**
     * Creates a new direct {@code CLongBuffer} that starts at the specified memory address and has the specified capacity.
     *
     * <p>The {@code address} specified must be aligned to 8 bytes. If not, use {@code memByteBuffer(address, capacity * 8).asLongBuffer()}.</p>
     *
     * @param address  the starting memory address
     * @param capacity the buffer capacity
     *
     * @return the new {@code CLongBuffer}
     */
    public static CLongBuffer memCLongBuffer(long address, int capacity) {
        if (CHECKS) {
            check(address);
        }
        return Pointer.Default.wrap(CLongBuffer.class, address, capacity);
    }

    /** Like {@link #memCLongBuffer}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static CLongBuffer memCLongBufferSafe(long address, int capacity) {
        return address == NULL ? null : Pointer.Default.wrap(CLongBuffer.class, address, capacity);
    }

    /**
     * Creates a new direct FloatBuffer that starts at the specified memory address and has the specified capacity.
     *
     * <p>The {@code address} specified must be aligned to 4 bytes. If not, use {@code memByteBuffer(address, capacity * 4).asFloatBuffer()}.</p>
     *
     * @param address  the starting memory address
     * @param capacity the buffer capacity
     *
     * @return the new FloatBuffer
     */
    public static FloatBuffer memFloatBuffer(long address, int capacity) {
        if (CHECKS) {
            check(address);
        }
        return wrap(BUFFER_FLOAT, address, capacity);
    }

    /** Like {@link #memFloatBuffer}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static FloatBuffer memFloatBufferSafe(long address, int capacity) {
        return address == NULL ? null : wrap(BUFFER_FLOAT, address, capacity);
    }

    /**
     * Creates a new direct DoubleBuffer that starts at the specified memory address and has the specified capacity.
     *
     * <p>The {@code address} specified must be aligned to 8 bytes. If not, use {@code memByteBuffer(address, capacity * 8).asDoubleBuffer()}.</p>
     *
     * @param address  the starting memory address
     * @param capacity the buffer capacity
     *
     * @return the new DoubleBuffer
     */
    public static DoubleBuffer memDoubleBuffer(long address, int capacity) {
        if (CHECKS) {
            check(address);
        }
        return wrap(BUFFER_DOUBLE, address, capacity);
    }

    /** Like {@link #memDoubleBuffer}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static DoubleBuffer memDoubleBufferSafe(long address, int capacity) {
        return address == NULL ? null : wrap(BUFFER_DOUBLE, address, capacity);
    }

    /**
     * Creates a new PointerBuffer that starts at the specified memory address and has the specified capacity.
     *
     * <p>The {@code address} specified must be aligned to the pointer size. If not, use {@code PointerBuffer.create(memByteBuffer(address, capacity *
     * POINTER_SIZE))}.</p>
     *
     * @param address  the starting memory address
     * @param capacity the buffer capacity
     *
     * @return the new PointerBuffer
     */
    public static PointerBuffer memPointerBuffer(long address, int capacity) {
        if (CHECKS) {
            check(address);
        }
        return Pointer.Default.wrap(PointerBuffer.class, address, capacity);
    }

    /** Like {@link #memPointerBuffer}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static PointerBuffer memPointerBufferSafe(long address, int capacity) {
        return address == NULL ? null : Pointer.Default.wrap(PointerBuffer.class, address, capacity);
    }

    // --- [ Buffer duplication ] ---

    /**
     * Duplicates the specified buffer. The returned buffer will have the same {@link ByteOrder} as the source buffer.
     *
     * <p>This method should be preferred over {@link ByteBuffer#duplicate} because it has a much shorter call chain. Long call chains may fail to inline due
     * to JVM limits, disabling important optimizations (e.g. scalar replacement via Escape Analysis).</p>
     *
     * @param buffer the buffer to duplicate
     *
     * @return the duplicated buffer
     */
    public static ByteBuffer memDuplicate(ByteBuffer buffer) {
        ByteBuffer target;
        try {
            target = (ByteBuffer)UNSAFE.allocateInstance(BUFFER_BYTE);
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }

        UNSAFE.putLong(target, ADDRESS, UNSAFE.getLong(buffer, ADDRESS));
        UNSAFE.putInt(target, MARK, UNSAFE.getInt(buffer, MARK));
        UNSAFE.putInt(target, POSITION, UNSAFE.getInt(buffer, POSITION));
        UNSAFE.putInt(target, LIMIT, UNSAFE.getInt(buffer, LIMIT));
        UNSAFE.putInt(target, CAPACITY, UNSAFE.getInt(buffer, CAPACITY));

        Object attachment = UNSAFE.getObject(buffer, PARENT_BYTE);
        UNSAFE.putObject(target, PARENT_BYTE, attachment == null ? buffer : attachment);

        return target.order(buffer.order());
    }

    /**
     * Duplicates the specified buffer.
     *
     * <p>This method should be preferred over {@link ShortBuffer#duplicate} because it has a much shorter call chain. Long call chains may fail to inline due
     * to JVM limits, disabling important optimizations (e.g. scalar replacement via Escape Analysis).</p>
     *
     * @param buffer the buffer to duplicate
     *
     * @return the duplicated buffer
     */
    public static ShortBuffer memDuplicate(ShortBuffer buffer) { return duplicate(BUFFER_SHORT, buffer, PARENT_SHORT); }

    /**
     * Duplicates the specified buffer.
     *
     * <p>This method should be preferred over {@link CharBuffer#duplicate} because it has a much shorter call chain. Long call chains may fail to inline due
     * to JVM limits, disabling important optimizations (e.g. scalar replacement via Escape Analysis).</p>
     *
     * @param buffer the buffer to duplicate
     *
     * @return the duplicated buffer
     */
    public static CharBuffer memDuplicate(CharBuffer buffer) { return duplicate(BUFFER_CHAR, buffer, PARENT_CHAR); }

    /**
     * Duplicates the specified buffer.
     *
     * <p>This method should be preferred over {@link IntBuffer#duplicate} because it has a much shorter call chain. Long call chains may fail to inline due
     * to JVM limits, disabling important optimizations (e.g. scalar replacement via Escape Analysis).</p>
     *
     * @param buffer the buffer to duplicate
     *
     * @return the duplicated buffer
     */
    public static IntBuffer memDuplicate(IntBuffer buffer) { return duplicate(BUFFER_INT, buffer, PARENT_INT); }

    /**
     * Duplicates the specified buffer.
     *
     * <p>This method should be preferred over {@link LongBuffer#duplicate} because it has a much shorter call chain. Long call chains may fail to inline due
     * to JVM limits, disabling important optimizations (e.g. scalar replacement via Escape Analysis).</p>
     *
     * @param buffer the buffer to duplicate
     *
     * @return the duplicated buffer
     */
    public static LongBuffer memDuplicate(LongBuffer buffer) { return duplicate(BUFFER_LONG, buffer, PARENT_LONG); }

    /**
     * Duplicates the specified buffer.
     *
     * <p>This method should be preferred over {@link FloatBuffer#duplicate} because it has a much shorter call chain. Long call chains may fail to inline due
     * to JVM limits, disabling important optimizations (e.g. scalar replacement via Escape Analysis).</p>
     *
     * @param buffer the buffer to duplicate
     *
     * @return the duplicated buffer
     */
    public static FloatBuffer memDuplicate(FloatBuffer buffer) { return duplicate(BUFFER_FLOAT, buffer, PARENT_FLOAT); }

    /**
     * Duplicates the specified buffer.
     *
     * <p>This method should be preferred over {@link DoubleBuffer#duplicate} because it has a much shorter call chain. Long call chains may fail to inline due
     * to JVM limits, disabling important optimizations (e.g. scalar replacement via Escape Analysis).</p>
     *
     * @param buffer the buffer to duplicate
     *
     * @return the duplicated buffer
     */
    public static DoubleBuffer memDuplicate(DoubleBuffer buffer) { return duplicate(BUFFER_DOUBLE, buffer, PARENT_DOUBLE); }

    // --- [ Buffer slicing ] ---

    /**
     * Slices the specified buffer. The returned buffer will have the same {@link ByteOrder} as the source buffer.
     *
     * <p>This method should be preferred over {@link ByteBuffer#slice} because it has a much shorter call chain. Long call chains may fail to inline due to
     * JVM limits, disabling important optimizations (e.g. scalar replacement via Escape Analysis).</p>
     *
     * @param buffer the buffer to slice
     *
     * @return the sliced buffer
     */
    public static ByteBuffer memSlice(ByteBuffer buffer) {
        return slice(buffer, memAddress0(buffer) + buffer.position(), buffer.remaining());
    }

    /**
     * Slices the specified buffer.
     *
     * <p>This method should be preferred over {@link ShortBuffer#slice} because it has a much shorter call chain. Long call chains may fail to inline due to
     * JVM limits, disabling important optimizations (e.g. scalar replacement via Escape Analysis).</p>
     *
     * @param buffer the buffer to slice
     *
     * @return the sliced buffer
     */
    public static ShortBuffer memSlice(ShortBuffer buffer) {
        return slice(BUFFER_SHORT, buffer, address(buffer.position(), 1, memAddress0(buffer)), buffer.remaining(), PARENT_SHORT);
    }

    /**
     * Slices the specified buffer.
     *
     * <p>This method should be preferred over {@link CharBuffer#slice} because it has a much shorter call chain. Long call chains may fail to inline due to
     * JVM limits, disabling important optimizations (e.g. scalar replacement via Escape Analysis).</p>
     *
     * @param buffer the buffer to slice
     *
     * @return the sliced buffer
     */
    public static CharBuffer memSlice(CharBuffer buffer) {
        return slice(BUFFER_CHAR, buffer, address(buffer.position(), 1, memAddress0(buffer)), buffer.remaining(), PARENT_CHAR);
    }

    /**
     * Slices the specified buffer.
     *
     * <p>This method should be preferred over {@link IntBuffer#slice} because it has a much shorter call chain. Long call chains may fail to inline due to
     * JVM limits, disabling important optimizations (e.g. scalar replacement via Escape Analysis).</p>
     *
     * @param buffer the buffer to slice
     *
     * @return the sliced buffer
     */
    public static IntBuffer memSlice(IntBuffer buffer) {
        return slice(BUFFER_INT, buffer, address(buffer.position(), 2, memAddress0(buffer)), buffer.remaining(), PARENT_INT);
    }

    /**
     * Slices the specified buffer.
     *
     * <p>This method should be preferred over {@link LongBuffer#slice} because it has a much shorter call chain. Long call chains may fail to inline due to
     * JVM limits, disabling important optimizations (e.g. scalar replacement via Escape Analysis).</p>
     *
     * @param buffer the buffer to slice
     *
     * @return the sliced buffer
     */
    public static LongBuffer memSlice(LongBuffer buffer) {
        return slice(BUFFER_LONG, buffer, address(buffer.position(), 3, memAddress0(buffer)), buffer.remaining(), PARENT_LONG);
    }

    /**
     * Slices the specified buffer.
     *
     * <p>This method should be preferred over {@link FloatBuffer#slice} because it has a much shorter call chain. Long call chains may fail to inline due to
     * JVM limits, disabling important optimizations (e.g. scalar replacement via Escape Analysis).</p>
     *
     * @param buffer the buffer to slice
     *
     * @return the sliced buffer
     */
    public static FloatBuffer memSlice(FloatBuffer buffer) {
        return slice(BUFFER_FLOAT, buffer, address(buffer.position(), 2, memAddress0(buffer)), buffer.remaining(), PARENT_FLOAT);
    }

    /**
     * Slices the specified buffer.
     *
     * <p>This method should be preferred over {@link DoubleBuffer#slice} because it has a much shorter call chain. Long call chains may fail to inline due to
     * JVM limits, disabling important optimizations (e.g. scalar replacement via Escape Analysis).</p>
     *
     * @param buffer the buffer to slice
     *
     * @return the sliced buffer
     */
    public static DoubleBuffer memSlice(DoubleBuffer buffer) {
        return slice(BUFFER_DOUBLE, buffer, address(buffer.position(), 3, memAddress0(buffer)), buffer.remaining(), PARENT_DOUBLE);
    }
    /**
     * Returns a slice of the specified buffer between {@code (buffer.position() + offset)} and {@code (buffer.position() + offset + capacity)}. The returned
     * buffer will have the same {@link ByteOrder} as the original buffer.
     *
     * <p>The position and limit of the original buffer are preserved after a call to this method.</p>
     *
     * @param buffer   the buffer to slice
     * @param offset   the slice offset, it must be &le; {@code buffer.remaining()}
     * @param capacity the slice length, it must be &le; {@code buffer.capacity() - (buffer.position() + offset)}
     *
     * @return the sliced buffer
     */
    public static ByteBuffer memSlice(ByteBuffer buffer, int offset, int capacity) {
        int position = buffer.position() + offset;
        if (offset < 0 || buffer.limit() < position) {
            throw new IllegalArgumentException();
        }
        if (capacity < 0 || buffer.capacity() - position < capacity) {
            throw new IllegalArgumentException();
        }
        return slice(buffer, memAddress0(buffer) + position, capacity);
    }

    /**
     * Returns a slice of the specified buffer between {@code (buffer.position() + offset)} and {@code (buffer.position() + offset + capacity)}.
     *
     * <p>The position and limit of the original buffer are preserved after a call to this method.</p>
     *
     * @param buffer   the buffer to slice
     * @param offset   the slice offset, it must be &le; {@code buffer.remaining()}
     * @param capacity the slice length, it must be &le; {@code buffer.capacity() - (buffer.position() + offset)}
     *
     * @return the sliced buffer
     */
    public static ShortBuffer memSlice(ShortBuffer buffer, int offset, int capacity) {
        int position = buffer.position() + offset;
        if (offset < 0 || buffer.limit() < position) {
            throw new IllegalArgumentException();
        }
        if (capacity < 0 || buffer.capacity() - position < capacity) {
            throw new IllegalArgumentException();
        }
        return slice(BUFFER_SHORT, buffer, address(position, 1, memAddress0(buffer)), capacity, PARENT_SHORT);
    }

    /**
     * Returns a slice of the specified buffer between {@code (buffer.position() + offset)} and {@code (buffer.position() + offset + capacity)}.
     *
     * <p>The position and limit of the original buffer are preserved after a call to this method.</p>
     *
     * @param buffer   the buffer to slice
     * @param offset   the slice offset, it must be &le; {@code buffer.remaining()}
     * @param capacity the slice length, it must be &le; {@code buffer.capacity() - (buffer.position() + offset)}
     *
     * @return the sliced buffer
     */
    public static CharBuffer memSlice(CharBuffer buffer, int offset, int capacity) {
        int position = buffer.position() + offset;
        if (offset < 0 || buffer.limit() < position) {
            throw new IllegalArgumentException();
        }
        if (capacity < 0 || buffer.capacity() - position < capacity) {
            throw new IllegalArgumentException();
        }
        return slice(BUFFER_CHAR, buffer, address(position, 1, memAddress0(buffer)), capacity, PARENT_CHAR);
    }

    /**
     * Returns a slice of the specified buffer between {@code (buffer.position() + offset)} and {@code (buffer.position() + offset + capacity)}.
     *
     * <p>The position and limit of the original buffer are preserved after a call to this method.</p>
     *
     * @param buffer   the buffer to slice
     * @param offset   the slice offset, it must be &le; {@code buffer.remaining()}
     * @param capacity the slice length, it must be &le; {@code buffer.capacity() - (buffer.position() + offset)}
     *
     * @return the sliced buffer
     */
    public static IntBuffer memSlice(IntBuffer buffer, int offset, int capacity) {
        int position = buffer.position() + offset;
        if (offset < 0 || buffer.limit() < position) {
            throw new IllegalArgumentException();
        }
        if (capacity < 0 || buffer.capacity() - position < capacity) {
            throw new IllegalArgumentException();
        }
        return slice(BUFFER_INT, buffer, address(position, 2, memAddress0(buffer)), capacity, PARENT_INT);
    }

    /**
     * Returns a slice of the specified buffer between {@code (buffer.position() + offset)} and {@code (buffer.position() + offset + capacity)}.
     *
     * <p>The position and limit of the original buffer are preserved after a call to this method.</p>
     *
     * @param buffer   the buffer to slice
     * @param offset   the slice offset, it must be &le; {@code buffer.remaining()}
     * @param capacity the slice length, it must be &le; {@code buffer.capacity() - (buffer.position() + offset)}
     *
     * @return the sliced buffer
     */
    public static LongBuffer memSlice(LongBuffer buffer, int offset, int capacity) {
        int position = buffer.position() + offset;
        if (offset < 0 || buffer.limit() < position) {
            throw new IllegalArgumentException();
        }
        if (capacity < 0 || buffer.capacity() - position < capacity) {
            throw new IllegalArgumentException();
        }
        return slice(BUFFER_LONG, buffer, address(position, 3, memAddress0(buffer)), capacity, PARENT_LONG);
    }

    /**
     * Returns a slice of the specified buffer between {@code (buffer.position() + offset)} and {@code (buffer.position() + offset + capacity)}.
     *
     * <p>The position and limit of the original buffer are preserved after a call to this method.</p>
     *
     * @param buffer   the buffer to slice
     * @param offset   the slice offset, it must be &le; {@code buffer.remaining()}
     * @param capacity the slice length, it must be &le; {@code buffer.capacity() - (buffer.position() + offset)}
     *
     * @return the sliced buffer
     */
    public static FloatBuffer memSlice(FloatBuffer buffer, int offset, int capacity) {
        int position = buffer.position() + offset;
        if (offset < 0 || buffer.limit() < position) {
            throw new IllegalArgumentException();
        }
        if (capacity < 0 || buffer.capacity() - position < capacity) {
            throw new IllegalArgumentException();
        }
        return slice(BUFFER_FLOAT, buffer, address(position, 2, memAddress0(buffer)), capacity, PARENT_FLOAT);
    }

    /**
     * Returns a slice of the specified buffer between {@code (buffer.position() + offset)} and {@code (buffer.position() + offset + capacity)}.
     *
     * <p>The position and limit of the original buffer are preserved after a call to this method.</p>
     *
     * @param buffer   the buffer to slice
     * @param offset   the slice offset, it must be &le; {@code buffer.remaining()}
     * @param capacity the slice length, it must be &le; {@code buffer.capacity() - (buffer.position() + offset)}
     *
     * @return the sliced buffer
     */
    public static DoubleBuffer memSlice(DoubleBuffer buffer, int offset, int capacity) {
        int position = buffer.position() + offset;
        if (offset < 0 || buffer.limit() < position) {
            throw new IllegalArgumentException();
        }
        if (capacity < 0 || buffer.capacity() - position < capacity) {
            throw new IllegalArgumentException();
        }
        return slice(BUFFER_DOUBLE, buffer, address(position, 3, memAddress0(buffer)), capacity, PARENT_DOUBLE);
    }

    /**
     * Returns a slice of the specified buffer between {@code (buffer.position() + offset)} and {@code (buffer.position() + offset + capacity)}.
     *
     * <p>The position and limit of the original buffer are preserved after a call to this method.</p>
     *
     * @param buffer   the buffer to slice
     * @param offset   the slice offset, it must be &le; {@code buffer.remaining()}
     * @param capacity the slice length, it must be &le; {@code buffer.capacity() - (buffer.position() + offset)}
     *
     * @return the sliced buffer
     */
    public static <T extends CustomBuffer<T>> T memSlice(T buffer, int offset, int capacity) { return buffer.slice(offset, capacity); }

    // --- [ memset ] ---

    /**
     * Sets all bytes in a specified block of memory to a fixed value (usually zero).
     *
     * @param ptr   the starting memory address
     * @param value the value to set (memSet will convert it to unsigned byte)
     */
    public static void memSet(ByteBuffer ptr, int value) { memSet(memAddress(ptr), value, ptr.remaining()); }

    /**
     * Sets all bytes in a specified block of memory to a fixed value (usually zero).
     *
     * @param ptr   the starting memory address
     * @param value the value to set (memSet will convert it to unsigned byte)
     */
    public static void memSet(ShortBuffer ptr, int value) { memSet(memAddress(ptr), value, apiGetBytes(ptr.remaining(), 1)); }

    /**
     * Sets all bytes in a specified block of memory to a fixed value (usually zero).
     *
     * @param ptr   the starting memory address
     * @param value the value to set (memSet will convert it to unsigned byte)
     */
    public static void memSet(CharBuffer ptr, int value) { memSet(memAddress(ptr), value, apiGetBytes(ptr.remaining(), 1)); }

    /**
     * Sets all bytes in a specified block of memory to a fixed value (usually zero).
     *
     * @param ptr   the starting memory address
     * @param value the value to set (memSet will convert it to unsigned byte)
     */
    public static void memSet(IntBuffer ptr, int value) { memSet(memAddress(ptr), value, apiGetBytes(ptr.remaining(), 2)); }

    /**
     * Sets all bytes in a specified block of memory to a fixed value (usually zero).
     *
     * @param ptr   the starting memory address
     * @param value the value to set (memSet will convert it to unsigned byte)
     */
    public static void memSet(LongBuffer ptr, int value) { memSet(memAddress(ptr), value, apiGetBytes(ptr.remaining(), 3)); }

    /**
     * Sets all bytes in a specified block of memory to a fixed value (usually zero).
     *
     * @param ptr   the starting memory address
     * @param value the value to set (memSet will convert it to unsigned byte)
     */
    public static void memSet(FloatBuffer ptr, int value) { memSet(memAddress(ptr), value, apiGetBytes(ptr.remaining(), 2)); }

    /**
     * Sets all bytes in a specified block of memory to a fixed value (usually zero).
     *
     * @param ptr   the starting memory address
     * @param value the value to set (memSet will convert it to unsigned byte)
     */
    public static void memSet(DoubleBuffer ptr, int value) { memSet(memAddress(ptr), value, apiGetBytes(ptr.remaining(), 3)); }

    /**
     * Sets all bytes in a specified block of memory to a fixed value (usually zero).
     *
     * @param ptr   the starting memory address
     * @param value the value to set (memSet will convert it to unsigned byte)
     * @param <T>   the buffer type
     */
    public static <T extends CustomBuffer<T>> void memSet(T ptr, int value) { memSet(memAddress(ptr), value, Integer.toUnsignedLong(ptr.remaining()) * ptr.sizeof()); }

    /**
     * Sets all bytes in a specified block of memory to a fixed value (usually zero).
     *
     * @param ptr   the starting memory address
     * @param value the value to set (memSet will convert it to unsigned byte)
     * @param <T>   the struct type
     */
    public static <T extends Struct> void memSet(T ptr, int value) { memSet(ptr.address, value, ptr.sizeof()); }

    // --- [ memcpy ] ---

    /**
     * Sets all bytes in a specified block of memory to a copy of another block.
     *
     * @param src the source memory address
     * @param dst the destination memory address
     */
    public static void memCopy(ByteBuffer src, ByteBuffer dst) {
        if (CHECKS) {
            check(dst, src.remaining());
        }
        MultiReleaseMemCopy.copy(memAddress(src), memAddress(dst), src.remaining());
    }

    /**
     * Sets all bytes in a specified block of memory to a copy of another block.
     *
     * @param src the source memory address
     * @param dst the destination memory address
     */
    public static void memCopy(ShortBuffer src, ShortBuffer dst) {
        if (CHECKS) {
            check(dst, src.remaining());
        }
        MultiReleaseMemCopy.copy(memAddress(src), memAddress(dst), apiGetBytes(src.remaining(), 1));
    }

    /**
     * Sets all bytes in a specified block of memory to a copy of another block.
     *
     * @param src the source memory address
     * @param dst the destination memory address
     */
    public static void memCopy(CharBuffer src, CharBuffer dst) {
        if (CHECKS) {
            check((Buffer)dst, src.remaining());
        }
        MultiReleaseMemCopy.copy(memAddress(src), memAddress(dst), apiGetBytes(src.remaining(), 1));
    }

    /**
     * Sets all bytes in a specified block of memory to a copy of another block.
     *
     * @param src the source memory address
     * @param dst the destination memory address
     */
    public static void memCopy(IntBuffer src, IntBuffer dst) {
        if (CHECKS) {
            check(dst, src.remaining());
        }
        MultiReleaseMemCopy.copy(memAddress(src), memAddress(dst), apiGetBytes(src.remaining(), 2));
    }

    /**
     * Sets all bytes in a specified block of memory to a copy of another block.
     *
     * @param src the source memory address
     * @param dst the destination memory address
     */
    public static void memCopy(LongBuffer src, LongBuffer dst) {
        if (CHECKS) {
            check(dst, src.remaining());
        }
        MultiReleaseMemCopy.copy(memAddress(src), memAddress(dst), apiGetBytes(src.remaining(), 3));
    }

    /**
     * Sets all bytes in a specified block of memory to a copy of another block.
     *
     * @param src the source memory address
     * @param dst the destination memory address
     */
    public static void memCopy(FloatBuffer src, FloatBuffer dst) {
        if (CHECKS) {
            check(dst, src.remaining());
        }
        MultiReleaseMemCopy.copy(memAddress(src), memAddress(dst), apiGetBytes(src.remaining(), 2));
    }

    /**
     * Sets all bytes in a specified block of memory to a copy of another block.
     *
     * @param src the source memory address
     * @param dst the destination memory address
     */
    public static void memCopy(DoubleBuffer src, DoubleBuffer dst) {
        if (CHECKS) {
            check(dst, src.remaining());
        }
        MultiReleaseMemCopy.copy(memAddress(src), memAddress(dst), apiGetBytes(src.remaining(), 3));
    }

    /**
     * Sets all bytes in a specified block of memory to a copy of another block.
     *
     * @param src the source memory address
     * @param dst the destination memory address
     * @param <T> the buffer type
     */
    public static <T extends CustomBuffer<T>> void memCopy(T src, T dst) {
        if (CHECKS) {
            check(dst, src.remaining());
        }
        MultiReleaseMemCopy.copy(memAddress(src), memAddress(dst), Integer.toUnsignedLong(src.remaining()) * src.sizeof());
    }

    /**
     * Sets all bytes of a struct to a copy of another struct.
     *
     * @param src the source struct
     * @param dst the destination struct
     * @param <T> the struct type
     */
    public static <T extends Struct> void memCopy(T src, T dst) {
        MultiReleaseMemCopy.copy(src.address, dst.address, src.sizeof());
    }

    /*  -------------------------------------
        -------------------------------------
               UNSAFE MEMORY ACCESS API
        -------------------------------------
        ------------------------------------- */

    private interface NativeShift {
        long left(long value, int bytes);
        long right(long value, int bytes);
    }

    private static final NativeShift SHIFT = NATIVE_ORDER == ByteOrder.BIG_ENDIAN ?
        new NativeShift() {
            @Override public long left(long value, int bytes) { return value << (bytes << 3); }
            @Override public long right(long value, int bytes) { return value >>> (bytes << 3); }
        } :
        new NativeShift() {
            @Override public long left(long value, int bytes) { return value >>> (bytes << 3); }
            @Override public long right(long value, int bytes) { return value << (bytes << 3); }
        };

    private static final long FILL_PATTERN = Long.divideUnsigned(-1L, 255L);

    /**
     * Sets all bytes in a specified block of memory to a fixed value (usually zero).
     *
     * @param ptr   the starting memory address
     * @param value the value to set (memSet will convert it to unsigned byte)
     * @param bytes the number of bytes to set
     */
    public static void memSet(long ptr, int value, long bytes) {
        if (DEBUG && (ptr == NULL || bytes < 0)) {
            throw new IllegalArgumentException();
        }

        /*
        - Unsafe.setMemory is very slow.
        - A custom Java loop is fastest at small sizes, approximately up to 256 bytes.
        - The native memset becomes fastest at bigger sizes, when the JNI overhead becomes negligible.
         */

        //UNSAFE.setMemory(dst, bytes, (byte)(value & 0xFF));
        if (256L <= bytes) {
            nmemset(ptr, value, bytes);
            return;
        }

        long fill = (value & 0xFF) * FILL_PATTERN;

        int i = 0,
            length = (int)bytes & 0xFF;

        if (length != 0) {
            int misalignment = (int)ptr & 7;
            if (misalignment != 0) {
                long aligned = ptr - misalignment;
                UNSAFE.putLong(null, aligned, merge(
                    UNSAFE.getLong(null, aligned),
                    fill,
                    SHIFT.right(SHIFT.left(-1L, max(0, 8 - length)), misalignment) // 0x0000FFFFFFFF0000
                ));
                i += 8 - misalignment;
            }
        }

        // Aligned longs for performance
        for (; i <= length - 8; i += 8) {
            UNSAFE.putLong(null, ptr + i, fill);
        }

        int tail = length - i;
        if (0 < tail) {
            // Aligned tail
            UNSAFE.putLong(null, ptr + i, merge(
                fill,
                UNSAFE.getLong(null, ptr + i),
                SHIFT.right(-1L, tail) // 0x00000000FFFFFFFF
            ));
        }
    }

    // Bit from a where mask bit is 0, bit from b where mask bit is 1.
    static long merge(long a, long b, long mask) {
        return a ^ ((a ^ b) & mask);
    }

    /**
     * Sets all bytes in a specified block of memory to a copy of another block.
     *
     * @param src   the source memory address
     * @param dst   the destination memory address
     * @param bytes the number of bytes to copy
     */
    // from LWJGL 3.1.2
    public static void memCopy(long src, long dst, int bytes) {
        memCopy(src, dst, (long)bytes);
    }

    /**
     * Sets all bytes in a specified block of memory to a copy of another block.
     *
     * @param src   the source memory address
     * @param dst   the destination memory address
     * @param bytes the number of bytes to copy
     */
    public static void memCopy(long src, long dst, long bytes) {
        if (DEBUG && (src == NULL || dst == NULL || bytes < 0)) {
            throw new IllegalArgumentException();
        }

        MultiReleaseMemCopy.copy(src, dst, bytes);
    }

    static void memCopyAligned(long src, long dst, int bytes) {
        int i = 0;

        // Aligned longs for performance
        for (; i <= bytes - 8; i += 8) {
            UNSAFE.putLong(null, dst + i, UNSAFE.getLong(null, src + i));
        }

        // Aligned tail
        if (i < bytes) {
            UNSAFE.putLong(null, dst + i, merge(
                UNSAFE.getLong(null, src + i),
                UNSAFE.getLong(null, dst + i),
                SHIFT.right(-1L, bytes - i)
            ));
        }
    }

    public static boolean memGetBoolean(long ptr) { return UNSAFE.getByte(null, ptr) != 0; }
    public static byte memGetByte(long ptr)       { return UNSAFE.getByte(null, ptr); }
    public static short memGetShort(long ptr)     { return UNSAFE.getShort(null, ptr); }
    public static int memGetInt(long ptr)         { return UNSAFE.getInt(null, ptr); }
    public static long memGetLong(long ptr)       { return UNSAFE.getLong(null, ptr); }
    public static float memGetFloat(long ptr)     { return UNSAFE.getFloat(null, ptr); }
    public static double memGetDouble(long ptr)   { return UNSAFE.getDouble(null, ptr); }
    public static long memGetCLong(long ptr) {
        return CLONG_SIZE == 8
            ? UNSAFE.getLong(null, ptr)
            : UNSAFE.getInt(null, ptr);
    }

    public static long memGetAddress(long ptr) {
        return BITS64
            ? UNSAFE.getLong(null, ptr)
            : UNSAFE.getInt(null, ptr) & 0xFFFF_FFFFL;
    }

    public static void memPutByte(long ptr, byte value)     { UNSAFE.putByte(null, ptr, value); }
    public static void memPutShort(long ptr, short value)   { UNSAFE.putShort(null, ptr, value); }
    public static void memPutInt(long ptr, int value)       { UNSAFE.putInt(null, ptr, value); }
    public static void memPutLong(long ptr, long value)     { UNSAFE.putLong(null, ptr, value); }
    public static void memPutFloat(long ptr, float value)   { UNSAFE.putFloat(null, ptr, value); }
    public static void memPutDouble(long ptr, double value) { UNSAFE.putDouble(null, ptr, value); }
    public static void memPutCLong(long ptr, long value) {
        if (CLONG_SIZE == 8) {
            UNSAFE.putLong(null, ptr, value);
        } else {
            UNSAFE.putInt(null, ptr, (int)value);
        }
    }

    public static void memPutAddress(long ptr, long value) {
        if (BITS64) {
            UNSAFE.putLong(null, ptr, value);
        } else {
            UNSAFE.putInt(null, ptr, (int)value);
        }
    }

    /*  -------------------------------------
        -------------------------------------
                  JNI UTILITIES API
        -------------------------------------
        ------------------------------------- */

    /**
     * Returns the object that the specified global reference points to.
     *
     * @param globalRef the global reference
     * @param <T>       the object type
     *
     * @return the object pointed to by {@code globalRef}
     */
    public static native <T> T memGlobalRefToObject(long globalRef);

    /** Deprecated, use {@link JNINativeInterface#NewGlobalRef} instead. */
    @Deprecated public static long memNewGlobalRef(Object obj) { return NewGlobalRef(obj); }

    /** Deprecated, use {@link JNINativeInterface#DeleteGlobalRef} instead. */
    @Deprecated public static void memDeleteGlobalRef(long globalRef) { DeleteGlobalRef(globalRef); }

    /** Deprecated, use {@link JNINativeInterface#NewWeakGlobalRef} instead. */
    @Deprecated public static long memNewWeakGlobalRef(Object obj) { return NewWeakGlobalRef(obj); }

    /** Deprecated, use {@link JNINativeInterface#DeleteWeakGlobalRef} instead. */
    @Deprecated public static void memDeleteWeakGlobalRef(long globalRef) { DeleteWeakGlobalRef(globalRef);}

    /*  -------------------------------------
        -------------------------------------
                  TEXT ENCODING API
        -------------------------------------
        ------------------------------------- */

    /**
     * Returns a ByteBuffer containing the specified text ASCII encoded and null-terminated.
     *
     * @param text the text to encode
     *
     * @return the encoded text. The returned buffer must be deallocated manually with {@link #memFree}.
     */
    public static ByteBuffer memASCII(CharSequence text) {
        return memASCII(text, true);
    }

    /** Like {@link #memASCII(CharSequence) memASCII}, but returns {@code null} if {@code text} is {@code null}. */
    @Nullable
    public static ByteBuffer memASCIISafe(@Nullable CharSequence text) {
        return text == null ? null : memASCII(text, true);
    }

    /**
     * Returns a ByteBuffer containing the specified text ASCII encoded and optionally null-terminated.
     *
     * @param text           the text to encode
     * @param nullTerminated if true, the text will be terminated with a '\0'.
     *
     * @return the encoded text. The returned buffer must be deallocated manually with {@link #memFree}.
     */
    public static ByteBuffer memASCII(CharSequence text, boolean nullTerminated) {
        int  length = memLengthASCII(text, nullTerminated);
        long target = nmemAlloc(length);
        encodeASCII(text, nullTerminated, target);
        return wrap(BUFFER_BYTE, target, length).order(NATIVE_ORDER);
    }

    /** Like {@link #memASCII(CharSequence, boolean) memASCII}, but returns {@code null} if {@code text} is {@code null}. */
    @Nullable
    public static ByteBuffer memASCIISafe(@Nullable CharSequence text, boolean nullTerminated) {
        return text == null ? null : memASCII(text, nullTerminated);
    }

    /**
     * Encodes and optionally null-terminates the specified text using ASCII encoding. The encoded text is stored in the specified {@link ByteBuffer}, at the
     * current buffer position. The current buffer position is not modified by this operation. The {@code target} buffer is assumed to have enough remaining
     * space to store the encoded text.
     *
     * @param text           the text to encode
     * @param nullTerminated if true, the text will be terminated with a '\0'.
     *
     * @return the number of bytes of the encoded string
     */
    public static int memASCII(CharSequence text, boolean nullTerminated, ByteBuffer target) {
        return encodeASCII(text, nullTerminated, memAddress(target));
    }

    /**
     * Encodes and optionally null-terminates the specified text using ASCII encoding. The encoded text is stored in the specified {@link ByteBuffer} at the
     * specified {@code position} offset. The current buffer position is not modified by this operation. The {@code target} buffer is assumed to have enough
     * remaining space to store the encoded text.
     *
     * @param text           the text to encode
     * @param nullTerminated if true, the text will be terminated with a '\0'.
     * @param offset         the buffer position to which the string will be encoded
     *
     * @return the number of bytes of the encoded string
     */
    public static int memASCII(CharSequence text, boolean nullTerminated, ByteBuffer target, int offset) {
        return encodeASCII(text, nullTerminated, memAddress(target, offset));
    }

    static int encodeASCII(CharSequence text, boolean nullTerminated, long target) {
        int len = text.length();
        for (int p = 0; p < len; p++) {
            UNSAFE.putByte(target + p, (byte)text.charAt(p));
        }
        if (nullTerminated) {
            UNSAFE.putByte(target + len++, (byte)0);
        }
        return len;
    }

    /**
     * Returns the number of bytes required to encode the specified text in the ASCII encoding.
     *
     * @param value          the text to encode
     * @param nullTerminated if true, add the number of bytes required for null-termination
     *
     * @return the number of bytes
     */
    public static int memLengthASCII(CharSequence value, boolean nullTerminated) {
        return value.length() + (nullTerminated ? 1 : 0);
    }

    /**
     * Returns a ByteBuffer containing the specified text UTF-8 encoded and null-terminated.
     *
     * @param text the text to encode
     *
     * @return the encoded text. The returned buffer must be deallocated manually with {@link #memFree}.
     */
    public static ByteBuffer memUTF8(CharSequence text) {
        return memUTF8(text, true);
    }

    /** Like {@link #memUTF8(CharSequence) memASCII}, but returns {@code null} if {@code text} is {@code null}. */
    @Nullable
    public static ByteBuffer memUTF8Safe(@Nullable CharSequence text) {
        return text == null ? null : memUTF8(text, true);
    }

    /**
     * Returns a ByteBuffer containing the specified text UTF-8 encoded and optionally null-terminated.
     *
     * @param text           the text to encode
     * @param nullTerminated if true, the text will be terminated with a '\0'.
     *
     * @return the encoded text. The returned buffer must be deallocated manually with {@link #memFree}.
     */
    public static ByteBuffer memUTF8(CharSequence text, boolean nullTerminated) {
        int  length = memLengthUTF8(text, nullTerminated);
        long target = nmemAlloc(length);
        encodeUTF8(text, nullTerminated, target);
        return wrap(BUFFER_BYTE, target, length).order(NATIVE_ORDER);
    }

    /** Like {@link #memUTF8(CharSequence, boolean) memASCII}, but returns {@code null} if {@code text} is {@code null}. */
    @Nullable
    public static ByteBuffer memUTF8Safe(@Nullable CharSequence text, boolean nullTerminated) {
        return text == null ? null : memUTF8(text, nullTerminated);
    }

    /**
     * Encodes and optionally null-terminates the specified text using UTF-8 encoding. The encoded text is stored in the specified {@link ByteBuffer}, at the
     * current buffer position. The current buffer position is not modified by this operation. The {@code target} buffer is assumed to have enough remaining
     * space to store the encoded text. The specified text is assumed to be a valid UTF-16 string.
     *
     * @param text           the text to encode
     * @param nullTerminated if true, the text will be terminated with a '\0'.
     * @param target         the buffer in which to store the encoded text
     *
     * @return the number of bytes of the encoded string
     */
    public static int memUTF8(CharSequence text, boolean nullTerminated, ByteBuffer target) {
        return encodeUTF8(text, nullTerminated, memAddress(target));
    }

    /**
     * Encodes and optionally null-terminates the specified text using UTF-8 encoding. The encoded text is stored in the specified {@link ByteBuffer}, at the
     * specified {@code position} offset. The current buffer position is not modified by this operation. The {@code target} buffer is assumed to have enough
     * remaining space to store the encoded text. The specified text is assumed to be a valid UTF-16 string.
     *
     * @param text           the text to encode
     * @param nullTerminated if true, the text will be terminated with a '\0'.
     * @param target         the buffer in which to store the encoded text
     * @param offset         the buffer position to which the string will be encoded
     *
     * @return the number of bytes of the encoded string
     */
    public static int memUTF8(CharSequence text, boolean nullTerminated, ByteBuffer target, int offset) {
        return encodeUTF8(text, nullTerminated, memAddress(target, offset));
    }

    static int encodeUTF8(CharSequence text, boolean nullTerminated, long target) {
        int i = 0, len = text.length(), p = 0;

        char c;

        // ASCII fast path
        while (i < len && (c = text.charAt(i)) < 0x80) {
            UNSAFE.putByte(target + p++, (byte)c);
            i++;
        }

        // Slow path
        while (i < len) {
            c = text.charAt(i++);
            if (c < 0x80) {
                UNSAFE.putByte(target + p++, (byte)c);
            } else {
                int cp = c;
                if (c < 0x800) {
                    UNSAFE.putByte(target + p++, (byte)(0xC0 | cp >> 6));
                } else {
                    if (!isHighSurrogate(c)) {
                        UNSAFE.putByte(target + p++, (byte)(0xE0 | cp >> 12));
                    } else {
                        cp = toCodePoint(c, text.charAt(i++));

                        UNSAFE.putByte(target + p++, (byte)(0xF0 | cp >> 18));
                        UNSAFE.putByte(target + p++, (byte)(0x80 | cp >> 12 & 0x3F));
                    }
                    UNSAFE.putByte(target + p++, (byte)(0x80 | cp >> 6 & 0x3F));
                }
                UNSAFE.putByte(target + p++, (byte)(0x80 | cp & 0x3F));
            }
        }

        if (nullTerminated) {
            UNSAFE.putByte(target + p++, (byte)0); // TODO: did we have a bug here?
        }

        return p;
    }

    /**
     * Returns the number of bytes required to encode the specified text in the UTF-8 encoding.
     *
     * @param value          the text to encode
     * @param nullTerminated if true, add the number of bytes required for null-termination
     *
     * @return the number of bytes
     */
    public static int memLengthUTF8(CharSequence value, boolean nullTerminated) {
        int i, len = value.length(), bytes = len; // start with 1:1

        // ASCII fast path
        for (i = 0; i < len; i++) {
            if (0x80 <= value.charAt(i)) {
                break;
            }
        }

        // 1 or 2 bytes fast path
        for (; i < len; i++) {
            char c = value.charAt(i);

            // fallback to slow path
            if (0x800 <= c) {
                bytes += encodeUTF8LengthSlow(value, i, len);
                break;
            }

            // c <= 127: 0
            // c >= 128: 1
            bytes += (0x7F - c) >>> 31;
        }

        return bytes + (nullTerminated ? 1 : 0);
    }

    private static int encodeUTF8LengthSlow(CharSequence value, int offset, int len) {
        int bytes = 0;

        for (int i = offset; i < len; i++) {
            char c = value.charAt(i);
            if (c < 0x800) {
                bytes += (0x7F - c) >>> 31;
            } else if (c < MIN_SURROGATE || MAX_SURROGATE < c) {
                bytes += 2;
            } else {
                bytes += 2; // the byte count already includes 2 bytes for the surrogate pair, add 2 more
                i++;
            }
        }

        return bytes;
    }

    /**
     * Returns a ByteBuffer containing the specified text UTF-16 encoded and null-terminated.
     *
     * @param text the text to encode
     *
     * @return the encoded text. The returned buffer must be deallocated manually with {@link #memFree}.
     */
    public static ByteBuffer memUTF16(CharSequence text) {
        return memUTF16(text, true);
    }

    /** Like {@link #memUTF16(CharSequence) memASCII}, but returns {@code null} if {@code text} is {@code null}. */
    @Nullable
    public static ByteBuffer memUTF16Safe(@Nullable CharSequence text) {
        return text == null ? null : memUTF16(text, true);
    }

    /**
     * Returns a ByteBuffer containing the specified text UTF-16 encoded and optionally null-terminated.
     *
     * @param text           the text to encode
     * @param nullTerminated if true, the text will be terminated with a '\0'.
     *
     * @return the encoded text. The returned buffer must be deallocated manually with {@link #memFree}.
     */
    public static ByteBuffer memUTF16(CharSequence text, boolean nullTerminated) {
        int  length = memLengthUTF16(text, nullTerminated);
        long target = nmemAlloc(length);
        encodeUTF16(text, nullTerminated, target);
        return wrap(BUFFER_BYTE, target, length).order(NATIVE_ORDER);
    }

    /** Like {@link #memUTF16(CharSequence, boolean) memASCII}, but returns {@code null} if {@code text} is {@code null}. */
    @Nullable
    public static ByteBuffer memUTF16Safe(@Nullable CharSequence text, boolean nullTerminated) {
        return text == null ? null : memUTF16(text, nullTerminated);
    }

    /**
     * Encodes and optionally null-terminates the specified text using UTF-16 encoding. The encoded text is stored in the specified {@link ByteBuffer}, at the
     * current buffer position. The current buffer position is not modified by this operation. The {@code target} buffer is assumed to have enough remaining
     * space to store the encoded text.
     *
     * @param text           the text to encode
     * @param nullTerminated if true, the text will be terminated with a '\0'.
     * @param target         the buffer in which to store the encoded text
     *
     * @return the number of bytes of the encoded string
     */
    public static int memUTF16(CharSequence text, boolean nullTerminated, ByteBuffer target) {
        return encodeUTF16(text, nullTerminated, memAddress(target));
    }

    /**
     * Encodes and optionally null-terminates the specified text using UTF-16 encoding. The encoded text is stored in the specified {@link ByteBuffer} at the
     * specified {@code position} offset. The current buffer position is not modified by this operation. The {@code target} buffer is assumed to have enough
     * remaining space to store the encoded text.
     *
     * @param text           the text to encode
     * @param nullTerminated if true, the text will be terminated with a '\0'.
     * @param target         the buffer in which to store the encoded text
     * @param offset         the buffer position to which the string will be encoded
     *
     * @return the number of bytes of the encoded string
     */
    public static int memUTF16(CharSequence text, boolean nullTerminated, ByteBuffer target, int offset) {
        return encodeUTF16(text, nullTerminated, memAddress(target, offset));
    }

    static int encodeUTF16(CharSequence text, boolean nullTerminated, long target) {
        int len = text.length();
        for (int i = 0; i < len; i++) {
            UNSAFE.putShort(target + Integer.toUnsignedLong(i) * 2, (short)text.charAt(i));
        }
        if (nullTerminated) {
            UNSAFE.putShort(target + Integer.toUnsignedLong(len++) * 2, (short)0);
        }
        return 2 * len;
    }

    /**
     * Returns the number of bytes required to encode the specified text in the UTF-16 encoding.
     *
     * @param value          the text to encode
     * @param nullTerminated if true, add the number of bytes required for null-termination
     *
     * @return the number of bytes
     */
    public static int memLengthUTF16(CharSequence value, boolean nullTerminated) {
        return (value.length() + (nullTerminated ? 1 : 0)) << 1;
    }

    /*  -------------------------------------
        -------------------------------------
                  TEXT DECODING API
        -------------------------------------
        ------------------------------------- */

    private static int memLengthNT1(long address, int maxLength) {
        if (CHECKS) {
            check(address);
        }
        return BITS64
            ? strlen64NT1(address, maxLength)
            : strlen32NT1(address, maxLength);
    }

    private static int strlen64NT1(long address, int maxLength) {
        int i = 0;

        if (8 <= maxLength) {
            int misalignment = (int)address & 7;
            if (misalignment != 0) {
                // Align to 8 bytes
                for (int len = 8 - misalignment; i < len; i++) {
                    if (UNSAFE.getByte(null, address + i) == 0) {
                        return i;
                    }
                }
            }

            // Aligned longs for performance
            while (i <= maxLength - 8) {
                if (mathHasZeroByte(UNSAFE.getLong(null, address + i))) {
                    break;
                }
                i += 8;
            }
        }

        // Tail
        for (; i < maxLength; i++) {
            if (UNSAFE.getByte(null, address + i) == 0) {
                break;
            }
        }

        return i;
    }

    private static int strlen32NT1(long address, int maxLength) {
        int i = 0;

        if (4 <= maxLength) {
            int misalignment = (int)address & 3;
            if (misalignment != 0) {
                // Align to 4 bytes
                for (int len = 4 - misalignment; i < len; i++) {
                    if (UNSAFE.getByte(null, address + i) == 0) {
                        return i;
                    }
                }
            }

            // Aligned ints for performance
            while (i <= maxLength - 4) {
                if (mathHasZeroByte(UNSAFE.getInt(null, address + i))) {
                    break;
                }
                i += 4;
            }
        }

        // Tail
        for (; i < maxLength; i++) {
            if (UNSAFE.getByte(null, address + i) == 0) {
                break;
            }
        }

        return i;
    }

    /**
     * Calculates the length, in bytes, of the null-terminated string that starts at the current position of the specified buffer. A single \0 character will
     * terminate the string. The returned length will NOT include the \0 byte.
     *
     * <p>This method is useful for reading ASCII and UTF8 encoded text.</p>
     *
     * @param buffer the buffer containing the null-terminated string
     *
     * @return the string length, in bytes
     */
    public static int memLengthNT1(ByteBuffer buffer) {
        return memLengthNT1(memAddress(buffer), buffer.remaining());
    }

    private static int memLengthNT2(long address, int maxLength) {
        if (CHECKS) {
            check(address);
        }
        return BITS64
            ? strlen64NT2(address, maxLength)
            : strlen32NT2(address, maxLength);
    }

    private static int strlen64NT2(long address, int maxLength) {
        int i = 0;

        if (8 <= maxLength) {
            int misalignment = (int)address & 7;
            if (misalignment != 0) {
                // Align to 8 bytes
                for (int len = 8 - misalignment; i < len; i += 2) {
                    if (UNSAFE.getShort(null, address + i) == 0) {
                        return i;
                    }
                }
            }

            // Aligned longs for performance
            while (i <= maxLength - 8) {
                if (mathHasZeroShort(UNSAFE.getLong(null, address + i))) {
                    break;
                }
                i += 8;
            }
        }

        // Tail
        for (; i < maxLength; i += 2) {
            if (UNSAFE.getShort(null, address + i) == 0) {
                break;
            }
        }

        return i;
    }

    private static int strlen32NT2(long address, int maxLength) {
        int i = 0;

        if (4 <= maxLength) {
            int misalignment = (int)address & 3;
            if (misalignment != 0) {
                // Align to 4 bytes
                for (int len = 4 - misalignment; i < len; i += 2) {
                    if (UNSAFE.getShort(null, address + i) == 0) {
                        return i;
                    }
                }
            }

            // Aligned longs for performance
            while (i <= maxLength - 4) {
                if (mathHasZeroShort(UNSAFE.getInt(null, address + i))) {
                    break;
                }
                i += 4;
            }
        }

        // Tail
        for (; i < maxLength; i += 2) {
            if (UNSAFE.getShort(null, address + i) == 0) {
                break;
            }
        }

        return i;
    }

    /**
     * Calculates the length, in bytes, of the null-terminated string that starts at the current position of the specified buffer. Two \0 characters will
     * terminate the string. The returned buffer will NOT include the \0 bytes.
     *
     * <p>This method is useful for reading UTF16 encoded text.</p>
     *
     * @param buffer the buffer containing the null-terminated string
     *
     * @return the string length, in bytes
     */
    public static int memLengthNT2(ByteBuffer buffer) {
        return memLengthNT2(memAddress(buffer), buffer.remaining());
    }

    /**
     * Creates a new direct ByteBuffer that starts at the specified memory address and has capacity equal to the null-terminated string starting at that
     * address. A single \0 character will terminate the string. The returned buffer will NOT include the \0 byte.
     *
     * <p>This method is useful for reading ASCII and UTF8 encoded text.</p>
     *
     * @param address the starting memory address
     *
     * @return the new ByteBuffer
     */
    public static ByteBuffer memByteBufferNT1(long address) {
        return memByteBuffer(address, memLengthNT1(address, Integer.MAX_VALUE));
    }

    /**
     * Creates a new direct ByteBuffer that starts at the specified memory address and has capacity equal to the null-terminated string starting at that
     * address, up to a maximum of {@code maxLength} bytes. A single \0 character will terminate the string. The returned buffer will NOT include the \0 byte.
     *
     * <p>This method is useful for reading ASCII and UTF8 encoded text.</p>
     *
     * @param address   the starting memory address
     * @param maxLength the maximum string length, in bytes
     *
     * @return the new ByteBuffer
     */
    public static ByteBuffer memByteBufferNT1(long address, int maxLength) {
        return memByteBuffer(address, memLengthNT1(address, maxLength));
    }

    /** Like {@link #memByteBufferNT1(long) memByteBufferNT1}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static ByteBuffer memByteBufferNT1Safe(long address) {
        return address == NULL ? null : memByteBuffer(address, memLengthNT1(address, Integer.MAX_VALUE));
    }

    /** Like {@link #memByteBufferNT1(long, int) memByteBufferNT1}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static ByteBuffer memByteBufferNT1Safe(long address, int maxLength) {
        return address == NULL ? null : memByteBuffer(address, memLengthNT1(address, maxLength));
    }

    /**
     * Creates a new direct ByteBuffer that starts at the specified memory address and has capacity equal to the null-terminated string starting at that
     * address. Two \0 characters will terminate the string. The returned buffer will NOT include the \0 bytes.
     *
     * <p>This method is useful for reading UTF16 encoded text.</p>
     *
     * @param address the starting memory address
     *
     * @return the new ByteBuffer
     */
    public static ByteBuffer memByteBufferNT2(long address) {
        return memByteBufferNT2(address, Integer.MAX_VALUE - 1);
    }

    /**
     * Creates a new direct ByteBuffer that starts at the specified memory address and has capacity equal to the null-terminated string starting at that
     * address, up to a maximum of {@code maxLength} bytes. Two \0 characters will terminate the string. The returned buffer will NOT include the \0 bytes.
     *
     * <p>This method is useful for reading UTF16 encoded text.</p>
     *
     * @param address the starting memory address
     *
     * @return the new ByteBuffer
     */
    public static ByteBuffer memByteBufferNT2(long address, int maxLength) {
        if (DEBUG) {
            if ((maxLength & 1) != 0) {
                throw new IllegalArgumentException("The maximum length must be an even number.");
            }
        }
        return memByteBuffer(address, memLengthNT2(address, maxLength));
    }

    /** Like {@link #memByteBufferNT2(long) memByteBufferNT2}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static ByteBuffer memByteBufferNT2Safe(long address) {
        return address == NULL ? null : memByteBufferNT2(address, Integer.MAX_VALUE - 1);
    }

    /** Like {@link #memByteBufferNT2(long, int) memByteBufferNT2}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static ByteBuffer memByteBufferNT2Safe(long address, int maxLength) {
        return address == NULL ? null : memByteBufferNT2(address, maxLength);
    }

    /**
     * Converts the null-terminated ASCII encoded string at the specified memory address to a {@link String}.
     *
     * @param address the string memory address
     *
     * @return the decoded {@link String}
     */
    public static String memASCII(long address) {
        return memASCII(address, memLengthNT1(address, Integer.MAX_VALUE));
    }

    /**
     * Converts the ASCII encoded string at the specified memory address to a {@link String}.
     *
     * @param address the string memory address
     * @param length  the number of bytes to decode
     *
     * @return the decoded {@link String}
     */
    @SuppressWarnings("deprecation")
    public static String memASCII(long address, int length) {
        if (length <= 0) {
            return "";
        }

        byte[] ascii = length <= ARRAY_TLC_SIZE ? ARRAY_TLC_BYTE.get() : new byte[length];
        memByteBuffer(address, length).get(ascii, 0, length);
        return new String(ascii, 0, 0, length);
    }

    /**
     * Decodes the bytes with index {@code [position(), position()+remaining()}) in {@code buffer}, as an ASCII string.
     *
     * <p>The current {@code position} and {@code limit} of the specified {@code buffer} are not affected by this operation.</p>
     *
     * @param buffer the {@link ByteBuffer} to decode
     *
     * @return the decoded {@link String}
     */
    public static String memASCII(ByteBuffer buffer) {
        return memASCII(memAddress(buffer), buffer.remaining());
    }

    /** Like {@link #memASCII(long) memASCII}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static String memASCIISafe(long address) {
        return address == NULL ? null : memASCII(address, memLengthNT1(address, Integer.MAX_VALUE));
    }

    /** Like {@link #memASCII(long, int) memASCII}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static String memASCIISafe(long address, int length) {
        return address == NULL ? null : memASCII(address, length);
    }

    /** Like {@link #memASCII(ByteBuffer) memASCII}, but returns {@code null} if {@code buffer} is {@code null}. */
    @Nullable
    public static String memASCIISafe(@Nullable ByteBuffer buffer) {
        return buffer == null ? null : memASCII(memAddress(buffer), buffer.remaining());
    }

    /**
     * Decodes the bytes with index {@code [position(), position()+length}) in {@code buffer}, as an ASCII string.
     *
     * <p>The current {@code position} and {@code limit} of the specified {@code buffer} are not affected by this operation.</p>
     *
     * @param buffer the {@link ByteBuffer} to decode
     * @param length the number of bytes to decode
     *
     * @return the decoded {@link String}
     */
    public static String memASCII(ByteBuffer buffer, int length) {
        return memASCII(memAddress(buffer), length);
    }

    /**
     * Decodes the bytes with index {@code [offset, offset+length}) in {@code buffer}, as an ASCII string.
     *
     * <p>The current {@code position} and {@code limit} of the specified {@code buffer} are not affected by this operation.</p>
     *
     * @param buffer the {@link ByteBuffer} to decode
     * @param length the number of bytes to decode
     * @param offset the offset at which to start decoding.
     *
     * @return the decoded {@link String}
     */
    public static String memASCII(ByteBuffer buffer, int length, int offset) {
        return memASCII(memAddress(buffer, offset), length);
    }

    /**
     * Converts the null-terminated UTF-8 encoded string at the specified memory address to a {@link String}.
     *
     * @param address the string memory address
     *
     * @return the decoded {@link String}
     */
    public static String memUTF8(long address) {
        return MultiReleaseTextDecoding.decodeUTF8(address, memLengthNT1(address, Integer.MAX_VALUE));
    }

    /**
     * Converts the UTF-8 encoded string at the specified memory address to a {@link String}.
     *
     * @param address the string memory address
     * @param length  the number of bytes to decode
     *
     * @return the decoded {@link String}
     */
    public static String memUTF8(long address, int length) {
        return MultiReleaseTextDecoding.decodeUTF8(address, length);
    }

    /**
     * Decodes the bytes with index {@code [position(), position()+remaining()}) in {@code buffer}, as a UTF-8 string.
     *
     * <p>The current {@code position} and {@code limit} of the specified {@code buffer} are not affected by this operation.</p>
     *
     * @param buffer the {@link ByteBuffer} to decode
     *
     * @return the decoded {@link String}
     */
    public static String memUTF8(ByteBuffer buffer) {
        return MultiReleaseTextDecoding.decodeUTF8(memAddress(buffer), buffer.remaining());
    }

    /** Like {@link #memUTF8(long) memUTF8}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static String memUTF8Safe(long address) {
        return address == NULL ? null : MultiReleaseTextDecoding.decodeUTF8(address, memLengthNT1(address, Integer.MAX_VALUE));
    }

    /** Like {@link #memUTF8(long, int) memUTF8}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static String memUTF8Safe(long address, int length) {
        return address == NULL ? null : MultiReleaseTextDecoding.decodeUTF8(address, length);
    }

    /** Like {@link #memUTF8(ByteBuffer) memUTF8}, but returns {@code null} if {@code buffer} is {@code null}. */
    @Nullable
    public static String memUTF8Safe(@Nullable ByteBuffer buffer) {
        return buffer == null ? null : MultiReleaseTextDecoding.decodeUTF8(memAddress(buffer), buffer.remaining());
    }

    /**
     * Decodes the bytes with index {@code [position(), position()+length}) in {@code buffer}, as a UTF-8 string.
     *
     * <p>The current {@code position} and {@code limit} of the specified {@code buffer} are not affected by this operation.</p>
     *
     * @param buffer the {@link ByteBuffer} to decode
     * @param length the number of bytes to decode
     *
     * @return the decoded {@link String}
     */
    public static String memUTF8(ByteBuffer buffer, int length) {
        return MultiReleaseTextDecoding.decodeUTF8(memAddress(buffer), length);
    }

    /**
     * Decodes the bytes with index {@code [offset, offset+length}) in {@code buffer}, as a UTF-8 string.
     *
     * <p>The current {@code position} and {@code limit} of the specified {@code buffer} are not affected by this operation.</p>
     *
     * @param buffer the {@link ByteBuffer} to decode
     * @param length the number of bytes to decode
     * @param offset the offset at which to start decoding.
     *
     * @return the decoded {@link String}
     */
    public static String memUTF8(ByteBuffer buffer, int length, int offset) {
        return MultiReleaseTextDecoding.decodeUTF8(memAddress(buffer, offset), length);
    }

    /**
     * Converts the null-terminated UTF-16 encoded string at the specified memory address to a {@link String}.
     *
     * @param address the string memory address
     *
     * @return the decoded {@link String}
     */
    public static String memUTF16(long address) {
        return memUTF16(address, memLengthNT2(address, Integer.MAX_VALUE - 1) >> 1);
    }

    /**
     * Converts the UTF-16 encoded string at the specified memory address to a {@link String}.
     *
     * @param address the string memory address
     * @param length  the number of characters to decode
     *
     * @return the decoded {@link String}
     */
    public static String memUTF16(long address, int length) {
        if (length <= 0) {
            return "";
        }

        if (DEBUG) {
            // The implementation below does no codepoint validation.
            int    len   = length << 1;
            byte[] bytes = len <= ARRAY_TLC_SIZE ? ARRAY_TLC_BYTE.get() : new byte[len];
            memByteBuffer(address, len).get(bytes, 0, len);
            return new String(bytes, 0, len, UTF16);
        }

        char[] chars = length <= ARRAY_TLC_SIZE ? ARRAY_TLC_CHAR.get() : new char[length];
        memCharBuffer(address, length).get(chars, 0, length);
        return new String(chars, 0, length);
    }

    /**
     * Decodes the bytes with index {@code [position(), position()+remaining()}) in {@code buffer}, as a UTF-16 string.
     *
     * <p>The current {@code position} and {@code limit} of the specified {@code buffer} are not affected by this operation.</p>
     *
     * @param buffer the {@link ByteBuffer} to decode
     *
     * @return the decoded {@link String}
     */
    public static String memUTF16(ByteBuffer buffer) {
        return memUTF16(memAddress(buffer), buffer.remaining() >> 1);
    }

    /** Like {@link #memUTF16(long) memUTF16}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static String memUTF16Safe(long address) {
        return address == NULL ? null : memUTF16(address, memLengthNT2(address, Integer.MAX_VALUE - 1) >> 1);
    }

    /** Like {@link #memUTF16(long, int) memUTF16}, but returns {@code null} if {@code address} is {@link #NULL}. */
    @Nullable
    public static String memUTF16Safe(long address, int length) {
        return address == NULL ? null : memUTF16(address, length);
    }

    /** Like {@link #memUTF16(ByteBuffer) memUTF16}, but returns {@code null} if {@code buffer} is {@code null}. */
    @Nullable
    public static String memUTF16Safe(@Nullable ByteBuffer buffer) {
        return buffer == null ? null : memUTF16(memAddress(buffer), buffer.remaining() >> 1);
    }

    /**
     * Decodes the bytes with index {@code [position(), position()+(length*2)}) in {@code buffer}, as a UTF-16 string.
     *
     * <p>The current {@code position} and {@code limit} of the specified {@code buffer} are not affected by this operation.</p>
     *
     * @param buffer the {@link ByteBuffer} to decode
     * @param length the number of characters to decode
     *
     * @return the decoded {@link String}
     */
    public static String memUTF16(ByteBuffer buffer, int length) {
        return memUTF16(memAddress(buffer), length);
    }

    /**
     * Decodes the bytes with index {@code [offset, offset+(length*2)}) in {@code buffer}, as a UTF-16 string.
     *
     * <p>The current {@code position} and {@code limit} of the specified {@code buffer} are not affected by this operation.</p>
     *
     * @param buffer the {@link ByteBuffer} to decode
     * @param length the number of characters to decode
     * @param offset the offset at which to start decoding, in bytes.
     *
     * @return the decoded {@link String}
     */
    public static String memUTF16(ByteBuffer buffer, int length, int offset) {
        return memUTF16(memAddress(buffer, offset), length);
    }

    // -------------------------------------------------
    // -------------------------------------------------
    // -------------------------------------------------

    private static sun.misc.Unsafe getUnsafeInstance() {
        java.lang.reflect.Field[] fields = sun.misc.Unsafe.class.getDeclaredFields();

        /*
        Different runtimes use different names for the Unsafe singleton,
        so we cannot use .getDeclaredField and we scan instead. For example:

        Oracle: theUnsafe
        PERC : m_unsafe_instance
        Android: THE_ONE
        */
        for (java.lang.reflect.Field field : fields) {
            if (!field.getType().equals(sun.misc.Unsafe.class)) {
                continue;
            }

            int modifiers = field.getModifiers();
            if (!(java.lang.reflect.Modifier.isStatic(modifiers) && java.lang.reflect.Modifier.isFinal(modifiers))) {
                continue;
            }

            try {
                field.setAccessible(true);
                return (sun.misc.Unsafe)field.get(null);
            } catch (Exception ignored) {
            }
            break;
        }

        throw new UnsupportedOperationException("LWJGL requires sun.misc.Unsafe to be available.");
    }

    private static long getAddressOffset() {
        long MAGIC_ADDRESS = 0xDEADBEEF8BADF00DL;
        if (BITS32) {
            MAGIC_ADDRESS &= 0xFFFFFFFFL;
        }

        ByteBuffer bb = Objects.requireNonNull(NewDirectByteBuffer(MAGIC_ADDRESS, 0));

        long offset = 8L; // 8 byte aligned, cannot be at 0
        while (true) {
            if (UNSAFE.getLong(bb, offset) == MAGIC_ADDRESS) {
                return offset;
            }
            offset += 8L;
        }
    }

    private static final int MAGIC_CAPACITY = 0x0D15EA5E;
    private static final int MAGIC_POSITION = 0x00FACADE;

    private static long getIntFieldOffset(ByteBuffer bb, int magicValue) {
        long offset = 4L; // 4 byte aligned, cannot be at 0
        while (true) {
            if (UNSAFE.getInt(bb, offset) == magicValue) {
                return offset;
            }
            offset += 4L;
        }
    }

    private static long getMarkOffset() {
        ByteBuffer bb = Objects.requireNonNull(NewDirectByteBuffer(1L, 0));
        return getIntFieldOffset(bb, -1);
    }

    private static long getPositionOffset() {
        ByteBuffer bb = Objects.requireNonNull(NewDirectByteBuffer(-1L, MAGIC_CAPACITY));
        bb.position(MAGIC_POSITION);
        return getIntFieldOffset(bb, MAGIC_POSITION);
    }

    private static long getLimitOffset() {
        ByteBuffer bb = Objects.requireNonNull(NewDirectByteBuffer(-1L, MAGIC_CAPACITY));
        bb.limit(MAGIC_POSITION);
        return getIntFieldOffset(bb, MAGIC_POSITION);
    }

    private static long getCapacityOffset() {
        ByteBuffer bb = Objects.requireNonNull(NewDirectByteBuffer(-1L, MAGIC_CAPACITY));
        bb.limit(0);
        return getIntFieldOffset(bb, MAGIC_CAPACITY);
    }

    private static <T extends Buffer> long getParentOffset(long offset, int oopSize, T buffer, T bufferWithAttachment) {
        switch (oopSize) {
            case Integer.BYTES: // 32-bit or 64-bit with compressed oops
                while (true) {
                    if (UNSAFE.getInt(buffer, offset) != UNSAFE.getInt(bufferWithAttachment, offset)) {
                        return offset;
                    }
                    offset += oopSize;
                }
            case Long.BYTES: // 64-bit with uncompressed oops
                while (true) {
                    if (UNSAFE.getLong(buffer, offset) != UNSAFE.getLong(bufferWithAttachment, offset)) {
                        return offset;
                    }
                    offset += oopSize;
                }
            default:
                throw new IllegalStateException();
        }
    }

    @SuppressWarnings("unchecked")
    static <T extends Buffer> T wrap(Class<? extends T> clazz, long address, int capacity) {
        T buffer;
        try {
            buffer = (T)UNSAFE.allocateInstance(clazz);
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }

        UNSAFE.putLong(buffer, ADDRESS, address);
        UNSAFE.putInt(buffer, MARK, -1);
        UNSAFE.putInt(buffer, LIMIT, capacity);
        UNSAFE.putInt(buffer, CAPACITY, capacity);

        return buffer;
    }

    static ByteBuffer slice(ByteBuffer source, long address, int capacity) {
        ByteBuffer target;
        try {
            target = (ByteBuffer)UNSAFE.allocateInstance(BUFFER_BYTE);
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }

        UNSAFE.putLong(target, ADDRESS, address);
        UNSAFE.putInt(target, MARK, -1);
        UNSAFE.putInt(target, LIMIT, capacity);
        UNSAFE.putInt(target, CAPACITY, capacity);

        Object attachment = UNSAFE.getObject(source, PARENT_BYTE);
        UNSAFE.putObject(target, PARENT_BYTE, attachment == null ? source : attachment);

        return target.order(source.order());
    }

    @SuppressWarnings("unchecked")
    static <T extends Buffer> T slice(Class<? extends T> clazz, T source, long address, int capacity, long attachmentOffset) {
        T target;
        try {
            target = (T)UNSAFE.allocateInstance(clazz);
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }

        UNSAFE.putLong(target, ADDRESS, address);
        UNSAFE.putInt(target, MARK, -1);
        UNSAFE.putInt(target, LIMIT, capacity);
        UNSAFE.putInt(target, CAPACITY, capacity);

        UNSAFE.putObject(target, attachmentOffset, UNSAFE.getObject(source, attachmentOffset));

        return target;
    }

    @SuppressWarnings("unchecked")
    static <T extends Buffer> T duplicate(Class<? extends T> clazz, T source, long attachmentOffset) {
        T target;
        try {
            target = (T)UNSAFE.allocateInstance(clazz);
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }

        UNSAFE.putLong(target, ADDRESS, UNSAFE.getLong(source, ADDRESS));
        UNSAFE.putInt(target, MARK, UNSAFE.getInt(source, MARK));
        UNSAFE.putInt(target, POSITION, UNSAFE.getInt(source, POSITION));
        UNSAFE.putInt(target, LIMIT, UNSAFE.getInt(source, LIMIT));
        UNSAFE.putInt(target, CAPACITY, UNSAFE.getInt(source, CAPACITY));

        UNSAFE.putObject(target, attachmentOffset, UNSAFE.getObject(source, attachmentOffset));

        return target;
    }

}
