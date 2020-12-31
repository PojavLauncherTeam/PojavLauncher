/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.openal;

import javax.annotation.*;

import java.nio.*;

import org.lwjgl.system.*;

import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.JNI.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/** Native bindings to ALC 1.0 functionality. */
public class ALC10 {

    /** General tokens. */
    public static final int
        ALC_INVALID = 0xFFFFFFFF,
        ALC_FALSE   = 0x0,
        ALC_TRUE    = 0x1;

    /** Context creation attributes. */
    public static final int
        ALC_FREQUENCY = 0x1007,
        ALC_REFRESH   = 0x1008,
        ALC_SYNC      = 0x1009;

    /** Error conditions. */
    public static final int
        ALC_NO_ERROR        = 0x0,
        ALC_INVALID_DEVICE  = 0xA001,
        ALC_INVALID_CONTEXT = 0xA002,
        ALC_INVALID_ENUM    = 0xA003,
        ALC_INVALID_VALUE   = 0xA004,
        ALC_OUT_OF_MEMORY   = 0xA005;

    /** String queries. */
    public static final int
        ALC_DEFAULT_DEVICE_SPECIFIER = 0x1004,
        ALC_DEVICE_SPECIFIER         = 0x1005,
        ALC_EXTENSIONS               = 0x1006;

    /** Integer queries. */
    public static final int
        ALC_MAJOR_VERSION   = 0x1000,
        ALC_MINOR_VERSION   = 0x1001,
        ALC_ATTRIBUTES_SIZE = 0x1002,
        ALC_ALL_ATTRIBUTES  = 0x1003;

    protected ALC10() {
        throw new UnsupportedOperationException();
    }

	static boolean isAvailable(ALCCapabilities caps) {
		return checkFunctions(
            caps.alcOpenDevice, caps.alcCloseDevice, caps.alcCreateContext, caps.alcMakeContextCurrent, caps.alcProcessContext, caps.alcSuspendContext, 
            caps.alcDestroyContext, caps.alcGetCurrentContext, caps.alcGetContextsDevice, caps.alcIsExtensionPresent, caps.alcGetProcAddress, 
            caps.alcGetEnumValue, caps.alcGetError, caps.alcGetString, caps.alcGetIntegerv
        );
	}
    
// -- Begin LWJGL2 --
    static ALCcontext alcContext;
    
    public static ALCcontext alcCreateContext(ALCdevice device, java.nio.IntBuffer attrList) {
        long alContextHandle = alcCreateContext(device.device, attrList);
        alcContext = new ALCcontext(alContextHandle);
        return alcContext;
    }
    
    // FIXME if Minecraft 1.12.2 and below crashes here!
/*
    public static ALCcontext alcGetCurrentContext() {
        return alcContext;
    }
*/
    public static ALCdevice alcGetContextsDevice(ALCcontext context) {
        return AL.alcDevice;
    }

    public static void alcGetInteger(ALCdevice device, int pname, java.nio.IntBuffer integerdata) {
        int res = alcGetInteger(device.device, pname);
        integerdata.put(0, res);
	}
// -- End LWJGL2 --

    // --- [ alcOpenDevice ] ---

    /** Unsafe version of: {@link #alcOpenDevice OpenDevice} */
    public static long nalcOpenDevice(long deviceSpecifier) {
		long __functionAddress = ALC.getICD().alcOpenDevice;
        return invokePP(deviceSpecifier, __functionAddress);
    }

    /**
     * Allows the application to connect to a device.
     * 
     * <p>If the function returns {@code NULL}, then no sound driver/device has been found. The argument is a null terminated string that requests a certain device or
     * device configuration. If {@code NULL} is specified, the implementation will provide an implementation specific default.</p>
     *
     * @param deviceSpecifier the requested device or device configuration
     */
    @NativeType("ALCdevice *")
    public static long alcOpenDevice(@Nullable @NativeType("ALCchar const *") ByteBuffer deviceSpecifier) {
        if (CHECKS) {
            checkNT1Safe(deviceSpecifier);
        }
        return nalcOpenDevice(memAddressSafe(deviceSpecifier));
    }

    /**
     * Allows the application to connect to a device.
     * 
     * <p>If the function returns {@code NULL}, then no sound driver/device has been found. The argument is a null terminated string that requests a certain device or
     * device configuration. If {@code NULL} is specified, the implementation will provide an implementation specific default.</p>
     *
     * @param deviceSpecifier the requested device or device configuration
     */
    @NativeType("ALCdevice *")
    public static long alcOpenDevice(@Nullable @NativeType("ALCchar const *") CharSequence deviceSpecifier) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nUTF8Safe(deviceSpecifier, true);
            long deviceSpecifierEncoded = deviceSpecifier == null ? NULL : stack.getPointerAddress();
            return nalcOpenDevice(deviceSpecifierEncoded);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alcCloseDevice ] ---

    /**
     * Allows the application to disconnect from a device.
     * 
     * <p>The return code will be ALC_TRUE or ALC_FALSE, indicating success or failure. Failure will occur if all the device's contexts and buffers have not been
     * destroyed. Once closed, the {@code deviceHandle} is invalid.</p>
     *
     * @param deviceHandle the device to close
     */
    @NativeType("ALCboolean")
    public static boolean alcCloseDevice(@NativeType("ALCdevice const *") long deviceHandle) {
		long __functionAddress = ALC.getICD().alcCloseDevice;
        if (CHECKS) {
            check(deviceHandle);
        }
        return invokePZ(deviceHandle, __functionAddress);
    }

    // --- [ alcCreateContext ] ---

    /** Unsafe version of: {@link #alcCreateContext CreateContext} */
    public static long nalcCreateContext(long deviceHandle, long attrList) {
		long __functionAddress = ALC.getICD().alcCreateContext;
        if (CHECKS) {
            check(deviceHandle);
        }
        return invokePPP(deviceHandle, attrList, __functionAddress);
    }

    /**
     * Creates an AL context.
     *
     * @param deviceHandle a valid device
     * @param attrList     null or a zero terminated list of integer pairs composed of valid ALC attribute tokens and requested values. One of:<br><table><tr><td>{@link #ALC_FREQUENCY FREQUENCY}</td><td>{@link #ALC_REFRESH REFRESH}</td><td>{@link #ALC_SYNC SYNC}</td><td>{@link ALC11#ALC_MONO_SOURCES MONO_SOURCES}</td><td>{@link ALC11#ALC_STEREO_SOURCES STEREO_SOURCES}</td></tr></table>
     */
    @NativeType("ALCcontext *")
    public static long alcCreateContext(@NativeType("ALCdevice const *") long deviceHandle, @Nullable @NativeType("ALCint const *") IntBuffer attrList) {
        if (CHECKS) {
            checkNTSafe(attrList);
        }
        return nalcCreateContext(deviceHandle, memAddressSafe(attrList));
    }

    // --- [ alcMakeContextCurrent ] ---

    /**
     * Makes a context current with respect to OpenAL operation.
     * 
     * <p>The context parameter can be {@code NULL} or a valid context pointer. Using {@code NULL} results in no context being current, which is useful when shutting OpenAL down.
     * The operation will apply to the device that the context was created for.</p>
     * 
     * <p>For each OS process (usually this means for each application), only one context can be current at any given time. All AL commands apply to the current
     * context. Commands that affect objects shared among contexts (e.g. buffers) have side effects on other contexts.</p>
     *
     * @param context the context to make current
     */
    @NativeType("ALCboolean")
    public static boolean alcMakeContextCurrent(@NativeType("ALCcontext *") long context) {
		long __functionAddress = ALC.getICD().alcMakeContextCurrent;
        return invokePZ(context, __functionAddress);
    }

    // --- [ alcProcessContext ] ---

    /**
     * The current context is the only context accessible to state changes by AL commands (aside from state changes affecting shared objects). However,
     * multiple contexts can be processed at the same time. To indicate that a context should be processed (i.e. that internal execution state such as the
     * offset increments are to be performed), the application uses {@code alcProcessContext}.
     * 
     * <p>Repeated calls to alcProcessContext are legal, and do not affect a context that is already marked as processing. The default state of a context created
     * by alcCreateContext is that it is processing.</p>
     *
     * @param context the context to mark for processing
     */
    @NativeType("ALCvoid")
    public static void alcProcessContext(@NativeType("ALCcontext *") long context) {
		long __functionAddress = ALC.getICD().alcProcessContext;
        if (CHECKS) {
            check(context);
        }
        invokePV(context, __functionAddress);
    }

    // --- [ alcSuspendContext ] ---

    /**
     * The application can suspend any context from processing (including the current one). To indicate that a context should be suspended from processing
     * (i.e. that internal execution state such as offset increments are not to be changed), the application uses {@code alcSuspendContext}.
     * 
     * <p>Repeated calls to alcSuspendContext are legal, and do not affect a context that is already marked as suspended.</p>
     *
     * @param context the context to mark as suspended
     */
    @NativeType("ALCvoid")
    public static void alcSuspendContext(@NativeType("ALCcontext *") long context) {
		long __functionAddress = ALC.getICD().alcSuspendContext;
        if (CHECKS) {
            check(context);
        }
        invokePV(context, __functionAddress);
    }

    // --- [ alcDestroyContext ] ---

    /**
     * Destroys a context.
     * 
     * <p>The correct way to destroy a context is to first release it using alcMakeCurrent with a {@code NULL} context. Applications should not attempt to destroy a
     * current context – doing so will not work and will result in an ALC_INVALID_OPERATION error. All sources within a context will automatically be deleted
     * during context destruction.</p>
     *
     * @param context the context to destroy
     */
    @NativeType("ALCvoid")
    public static void alcDestroyContext(@NativeType("ALCcontext *") long context) {
		long __functionAddress = ALC.getICD().alcDestroyContext;
        if (CHECKS) {
            check(context);
        }
        invokePV(context, __functionAddress);
    }

    // --- [ alcGetCurrentContext ] ---

    /** Queries for, and obtains a handle to, the current context for the application. If there is no current context, {@code NULL} is returned. */
    @NativeType("ALCcontext *")
    public static long alcGetCurrentContext() {
		long __functionAddress = ALC.getICD().alcGetCurrentContext;
        return invokeP(__functionAddress);
    }

    // --- [ alcGetContextsDevice ] ---

    /**
     * Queries for, and obtains a handle to, the device of a given context.
     *
     * @param context the context to query
     */
    @NativeType("ALCdevice *")
    public static long alcGetContextsDevice(@NativeType("ALCcontext *") long context) {
		long __functionAddress = ALC.getICD().alcGetContextsDevice;
        if (CHECKS) {
            check(context);
        }
        return invokePP(context, __functionAddress);
    }

    // --- [ alcIsExtensionPresent ] ---

    /** Unsafe version of: {@link #alcIsExtensionPresent IsExtensionPresent} */
    public static boolean nalcIsExtensionPresent(long deviceHandle, long extName) {
		long __functionAddress = ALC.getICD().alcIsExtensionPresent;
        return invokePPZ(deviceHandle, extName, __functionAddress);
    }

    /**
     * Verifies that a given extension is available for the current context and the device it is associated with.
     * 
     * <p>Invalid and unsupported string tokens return ALC_FALSE. A {@code NULL} deviceHandle is acceptable. {@code extName} is not case sensitive – the implementation
     * will convert the name to all upper-case internally (and will express extension names in upper-case).</p>
     *
     * @param deviceHandle the device to query
     * @param extName      the extension name
     */
    @NativeType("ALCboolean")
    public static boolean alcIsExtensionPresent(@NativeType("ALCdevice const *") long deviceHandle, @NativeType("ALCchar const *") ByteBuffer extName) {
        if (CHECKS) {
            checkNT1(extName);
        }
        return nalcIsExtensionPresent(deviceHandle, memAddress(extName));
    }

    /**
     * Verifies that a given extension is available for the current context and the device it is associated with.
     * 
     * <p>Invalid and unsupported string tokens return ALC_FALSE. A {@code NULL} deviceHandle is acceptable. {@code extName} is not case sensitive – the implementation
     * will convert the name to all upper-case internally (and will express extension names in upper-case).</p>
     *
     * @param deviceHandle the device to query
     * @param extName      the extension name
     */
    @NativeType("ALCboolean")
    public static boolean alcIsExtensionPresent(@NativeType("ALCdevice const *") long deviceHandle, @NativeType("ALCchar const *") CharSequence extName) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nASCII(extName, true);
            long extNameEncoded = stack.getPointerAddress();
            return nalcIsExtensionPresent(deviceHandle, extNameEncoded);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alcGetProcAddress ] ---

    /** Unsafe version of: {@link #alcGetProcAddress GetProcAddress} */
    public static long nalcGetProcAddress(long deviceHandle, long funcName) {
		long __functionAddress = ALC.getICD().alcGetProcAddress;
        return invokePPP(deviceHandle, funcName, __functionAddress);
    }

    /**
     * Retrieves extension entry points.
     * 
     * <p>The application is expected to verify the applicability of an extension or core function entry point before requesting it by name, by use of
     * {@link #alcIsExtensionPresent IsExtensionPresent}.</p>
     * 
     * <p>Entry points can be device specific, but are not context specific. Using a {@code NULL} device handle does not guarantee that the entry point is returned,
     * even if available for one of the available devices.</p>
     *
     * @param deviceHandle the device to query
     * @param funcName     the function name
     */
    @NativeType("void *")
    public static long alcGetProcAddress(@NativeType("ALCdevice const *") long deviceHandle, @NativeType("ALchar const *") ByteBuffer funcName) {
        if (CHECKS) {
            checkNT1(funcName);
        }
        return nalcGetProcAddress(deviceHandle, memAddress(funcName));
    }

    /**
     * Retrieves extension entry points.
     * 
     * <p>The application is expected to verify the applicability of an extension or core function entry point before requesting it by name, by use of
     * {@link #alcIsExtensionPresent IsExtensionPresent}.</p>
     * 
     * <p>Entry points can be device specific, but are not context specific. Using a {@code NULL} device handle does not guarantee that the entry point is returned,
     * even if available for one of the available devices.</p>
     *
     * @param deviceHandle the device to query
     * @param funcName     the function name
     */
    @NativeType("void *")
    public static long alcGetProcAddress(@NativeType("ALCdevice const *") long deviceHandle, @NativeType("ALchar const *") CharSequence funcName) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nASCII(funcName, true);
            long funcNameEncoded = stack.getPointerAddress();
            return nalcGetProcAddress(deviceHandle, funcNameEncoded);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alcGetEnumValue ] ---

    /** Unsafe version of: {@link #alcGetEnumValue GetEnumValue} */
    public static int nalcGetEnumValue(long deviceHandle, long enumName) {
		long __functionAddress = ALC.getICD().alcGetEnumValue;
        return invokePPI(deviceHandle, enumName, __functionAddress);
    }

    /**
     * Returns extension enum values.
     * 
     * <p>Enumeration/token values are device independent, but tokens defined for extensions might not be present for a given device. Using a {@code NULL} handle is
     * legal, but only the tokens defined by the AL core are guaranteed. Availability of extension tokens depends on the ALC extension.</p>
     *
     * @param deviceHandle the device to query
     * @param enumName     the enum name
     */
    @NativeType("ALCenum")
    public static int alcGetEnumValue(@NativeType("ALCdevice const *") long deviceHandle, @NativeType("ALCchar const *") ByteBuffer enumName) {
        if (CHECKS) {
            checkNT1(enumName);
        }
        return nalcGetEnumValue(deviceHandle, memAddress(enumName));
    }

    /**
     * Returns extension enum values.
     * 
     * <p>Enumeration/token values are device independent, but tokens defined for extensions might not be present for a given device. Using a {@code NULL} handle is
     * legal, but only the tokens defined by the AL core are guaranteed. Availability of extension tokens depends on the ALC extension.</p>
     *
     * @param deviceHandle the device to query
     * @param enumName     the enum name
     */
    @NativeType("ALCenum")
    public static int alcGetEnumValue(@NativeType("ALCdevice const *") long deviceHandle, @NativeType("ALCchar const *") CharSequence enumName) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nASCII(enumName, true);
            long enumNameEncoded = stack.getPointerAddress();
            return nalcGetEnumValue(deviceHandle, enumNameEncoded);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alcGetError ] ---

    /**
     * Queries ALC errors.
     * 
     * <p>ALC uses the same conventions and mechanisms as AL for error handling. In particular, ALC does not use conventions derived from X11 (GLX) or Windows
     * (WGL).</p>
     * 
     * <p>Error conditions are specific to the device, and (like AL) a call to alcGetError resets the error state.</p>
     *
     * @param deviceHandle the device to query
     */
    @NativeType("ALCenum")
    public static int alcGetError(@NativeType("ALCdevice *") long deviceHandle) {
		long __functionAddress = ALC.getICD().alcGetError;
        return invokePI(deviceHandle, __functionAddress);
    }

    // --- [ alcGetString ] ---

    /** Unsafe version of: {@link #alcGetString GetString} */
    public static long nalcGetString(long deviceHandle, int token) {
		long __functionAddress = ALC.getICD().alcGetString;
        return invokePP(deviceHandle, token, __functionAddress);
    }

    /**
     * Obtains string value(s) from ALC.
     * 
     * <p><b>LWJGL note</b>: Use {@link ALUtil#getStringList} for those tokens that return multiple values.</p>
     *
     * @param deviceHandle the device to query
     * @param token        the information to query. One of:<br><table><tr><td>{@link #ALC_DEFAULT_DEVICE_SPECIFIER DEFAULT_DEVICE_SPECIFIER}</td><td>{@link #ALC_DEVICE_SPECIFIER DEVICE_SPECIFIER}</td><td>{@link #ALC_EXTENSIONS EXTENSIONS}</td></tr><tr><td>{@link ALC11#ALC_CAPTURE_DEFAULT_DEVICE_SPECIFIER CAPTURE_DEFAULT_DEVICE_SPECIFIER}</td><td>{@link ALC11#ALC_CAPTURE_DEVICE_SPECIFIER CAPTURE_DEVICE_SPECIFIER}</td></tr></table>
     */
    @Nullable
    @NativeType("ALCchar const *")
    public static String alcGetString(@NativeType("ALCdevice *") long deviceHandle, @NativeType("ALCenum") int token) {
        long __result = nalcGetString(deviceHandle, token);
        return memUTF8Safe(__result);
    }

    // --- [ alcGetIntegerv ] ---

    /**
     * Unsafe version of: {@link #alcGetIntegerv GetIntegerv}
     *
     * @param size the size of the {@code dest} buffer
     */
    public static void nalcGetIntegerv(long deviceHandle, int token, int size, long dest) {
		long __functionAddress = ALC.getICD().alcGetIntegerv;
        invokePPV(deviceHandle, token, size, dest, __functionAddress);
    }

    /**
     * Obtains integer value(s) from ALC.
     *
     * @param deviceHandle the device to query
     * @param token        the information to query. One of:<br><table><tr><td>{@link #ALC_MAJOR_VERSION MAJOR_VERSION}</td><td>{@link #ALC_MINOR_VERSION MINOR_VERSION}</td><td>{@link #ALC_ATTRIBUTES_SIZE ATTRIBUTES_SIZE}</td><td>{@link #ALC_ALL_ATTRIBUTES ALL_ATTRIBUTES}</td><td>{@link ALC11#ALC_CAPTURE_SAMPLES CAPTURE_SAMPLES}</td></tr></table>
     * @param dest         the destination buffer
     */
    @NativeType("ALCvoid")
    public static void alcGetIntegerv(@NativeType("ALCdevice *") long deviceHandle, @NativeType("ALCenum") int token, @NativeType("ALCint *") IntBuffer dest) {
        nalcGetIntegerv(deviceHandle, token, dest.remaining(), memAddress(dest));
    }

    /**
     * Obtains integer value(s) from ALC.
     *
     * @param deviceHandle the device to query
     * @param token        the information to query. One of:<br><table><tr><td>{@link #ALC_MAJOR_VERSION MAJOR_VERSION}</td><td>{@link #ALC_MINOR_VERSION MINOR_VERSION}</td><td>{@link #ALC_ATTRIBUTES_SIZE ATTRIBUTES_SIZE}</td><td>{@link #ALC_ALL_ATTRIBUTES ALL_ATTRIBUTES}</td><td>{@link ALC11#ALC_CAPTURE_SAMPLES CAPTURE_SAMPLES}</td></tr></table>
     */
    @NativeType("ALCvoid")
    public static int alcGetInteger(@NativeType("ALCdevice *") long deviceHandle, @NativeType("ALCenum") int token) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            IntBuffer dest = stack.callocInt(1);
            nalcGetIntegerv(deviceHandle, token, 1, memAddress(dest));
            return dest.get(0);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    /** Array version of: {@link #alcCreateContext CreateContext} */
    @NativeType("ALCcontext *")
    public static long alcCreateContext(@NativeType("ALCdevice const *") long deviceHandle, @Nullable @NativeType("ALCint const *") int[] attrList) {
		long __functionAddress = ALC.getICD().alcCreateContext;
        if (CHECKS) {
            check(deviceHandle);
            checkNTSafe(attrList);
        }
        return invokePPP(deviceHandle, attrList, __functionAddress);
    }

    /** Array version of: {@link #alcGetIntegerv GetIntegerv} */
    @NativeType("ALCvoid")
    public static void alcGetIntegerv(@NativeType("ALCdevice *") long deviceHandle, @NativeType("ALCenum") int token, @NativeType("ALCint *") int[] dest) {
		long __functionAddress = ALC.getICD().alcGetIntegerv;
        invokePPV(deviceHandle, token, dest.length, dest, __functionAddress);
    }

}
