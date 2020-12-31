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

/** Native bindings to AL 1.0 functionality. */
public class AL10 {
// -- Begin LWJGL2 Bridge --
    public static void alGetDouble(int p1, DoubleBuffer p2) {
        alGetDoublev(p1, p2);
    }
/*
    public static int alGetEnumValue(String p1) {
        alGetEnumValue((CharSequence) p1);
    }
*/
    public static void alGetFloat(int p1, FloatBuffer p2) {
        alGetFloatv(p1, p2);
    }

    public static void alGetInteger(int p1, IntBuffer p2) {
        alGetIntegerv(p1, p2);
    }

    public static void alGetListener(int p1, FloatBuffer p2) {
        alGetListenerfv(p1, p2);
    }

    public static void alGetSource(int p1, int p2, FloatBuffer p3) {
        alGetSourcefv(p1, p2, p3);
    }
/*
    public static boolean alIsExtensionPresent(String p1) {
        return alIsExtensionPresent((CharSequence) p1);
    }
*/
    public static void alListener(int pname, FloatBuffer value) {
        alListenerfv(pname, value);
	}
    
    public static void alSource(int p1, int p2, FloatBuffer p3) {
        alSourcefv(p1, p2, p3);
    }

    public static void alSourcePause(IntBuffer p1) {
        alSourcePausev(p1);
    }

    public static void alSourcePlay(IntBuffer p1) {
        alSourcePlayv(p1);
    }

    public static void alSourceRewind(IntBuffer p1) {
        alSourceRewindv(p1);
    }

    public static void alSourceStop(IntBuffer p1) {
        alSourceStopv(p1);
    }
// -- End LWJGL2 Bridge --

    /** General tokens. */
    public static final int
        AL_INVALID = 0xFFFFFFFF,
        AL_NONE    = 0x0,
        AL_FALSE   = 0x0,
        AL_TRUE    = 0x1;

    /** Error conditions. */
    public static final int
        AL_NO_ERROR          = 0x0,
        AL_INVALID_NAME      = 0xA001,
        AL_INVALID_ENUM      = 0xA002,
        AL_INVALID_VALUE     = 0xA003,
        AL_INVALID_OPERATION = 0xA004,
        AL_OUT_OF_MEMORY     = 0xA005;

    /** Numerical queries. */
    public static final int
        AL_DOPPLER_FACTOR = 0xC000,
        AL_DISTANCE_MODEL = 0xD000;

    /** String queries. */
    public static final int
        AL_VENDOR     = 0xB001,
        AL_VERSION    = 0xB002,
        AL_RENDERER   = 0xB003,
        AL_EXTENSIONS = 0xB004;

    /** Distance attenuation models. */
    public static final int
        AL_INVERSE_DISTANCE         = 0xD001,
        AL_INVERSE_DISTANCE_CLAMPED = 0xD002;

    /** Source types. */
    public static final int
        AL_SOURCE_ABSOLUTE = 0x201,
        AL_SOURCE_RELATIVE = 0x202;

    /** Listener and Source attributes. */
    public static final int
        AL_POSITION = 0x1004,
        AL_VELOCITY = 0x1006,
        AL_GAIN     = 0x100A;

    /** Source attributes. */
    public static final int
        AL_CONE_INNER_ANGLE = 0x1001,
        AL_CONE_OUTER_ANGLE = 0x1002,
        AL_PITCH            = 0x1003,
        AL_DIRECTION        = 0x1005,
        AL_LOOPING          = 0x1007,
        AL_BUFFER           = 0x1009,
        AL_SOURCE_STATE     = 0x1010,
        AL_CONE_OUTER_GAIN  = 0x1022,
        AL_SOURCE_TYPE      = 0x1027;

    /** Source state. */
    public static final int
        AL_INITIAL = 0x1011,
        AL_PLAYING = 0x1012,
        AL_PAUSED  = 0x1013,
        AL_STOPPED = 0x1014;

    /** Listener attributes. */
    public static final int AL_ORIENTATION = 0x100F;

    /** Queue state. */
    public static final int
        AL_BUFFERS_QUEUED    = 0x1015,
        AL_BUFFERS_PROCESSED = 0x1016;

    /** Gain bounds. */
    public static final int
        AL_MIN_GAIN = 0x100D,
        AL_MAX_GAIN = 0x100E;

    /** Distance model attributes, */
    public static final int
        AL_REFERENCE_DISTANCE = 0x1020,
        AL_ROLLOFF_FACTOR     = 0x1021,
        AL_MAX_DISTANCE       = 0x1023;

    /** Buffer attributes, */
    public static final int
        AL_FREQUENCY = 0x2001,
        AL_BITS      = 0x2002,
        AL_CHANNELS  = 0x2003,
        AL_SIZE      = 0x2004;

    /** Buffer formats. */
    public static final int
        AL_FORMAT_MONO8    = 0x1100,
        AL_FORMAT_MONO16   = 0x1101,
        AL_FORMAT_STEREO8  = 0x1102,
        AL_FORMAT_STEREO16 = 0x1103;

    /** Buffer state. */
    public static final int
        AL_UNUSED    = 0x2010,
        AL_PENDING   = 0x2011,
        AL_PROCESSED = 0x2012;

    protected AL10() {
        throw new UnsupportedOperationException();
    }

    static boolean isAvailable(ALCapabilities caps) {
        return checkFunctions(
            caps.alGetError, caps.alEnable, caps.alDisable, caps.alIsEnabled, caps.alGetBoolean, caps.alGetInteger, caps.alGetFloat, caps.alGetDouble, 
            caps.alGetBooleanv, caps.alGetIntegerv, caps.alGetFloatv, caps.alGetDoublev, caps.alGetString, caps.alDistanceModel, caps.alDopplerFactor, 
            caps.alDopplerVelocity, caps.alListenerf, caps.alListeneri, caps.alListener3f, caps.alListenerfv, caps.alGetListenerf, caps.alGetListeneri, 
            caps.alGetListener3f, caps.alGetListenerfv, caps.alGenSources, caps.alDeleteSources, caps.alIsSource, caps.alSourcef, caps.alSource3f, 
            caps.alSourcefv, caps.alSourcei, caps.alGetSourcef, caps.alGetSource3f, caps.alGetSourcefv, caps.alGetSourcei, caps.alGetSourceiv, 
            caps.alSourceQueueBuffers, caps.alSourceUnqueueBuffers, caps.alSourcePlay, caps.alSourcePause, caps.alSourceStop, caps.alSourceRewind, 
            caps.alSourcePlayv, caps.alSourcePausev, caps.alSourceStopv, caps.alSourceRewindv, caps.alGenBuffers, caps.alDeleteBuffers, caps.alIsBuffer, 
            caps.alGetBufferf, caps.alGetBufferi, caps.alBufferData, caps.alGetEnumValue, caps.alGetProcAddress, caps.alIsExtensionPresent
        );
    }

    // --- [ alGetError ] ---

    /**
     * Obtains error information.
     * 
     * <p>Each detectable error is assigned a numeric code. When an error is detected by AL, a flag is set and the error code is recorded. Further errors, if they
     * occur, do not affect this recorded code. When alGetError is called, the code is returned and the flag is cleared, so that a further error will again
     * record its code. If a call to alGetError returns AL_NO_ERROR then there has been no detectable error since the last call to alGetError (or since the AL
     * was initialized).</p>
     * 
     * <p>Error codes can be mapped to strings. The alGetString function returns a pointer to a constant (literal) string that is identical to the identifier used
     * for the enumeration value, as defined in the specification.</p>
     */
    @NativeType("ALenum")
    public static int alGetError() {
        long __functionAddress = AL.getICD().alGetError;
        return invokeI(__functionAddress);
    }

    // --- [ alEnable ] ---

    /**
     * Enables AL capabilities.
     *
     * @param target the capability to enable
     */
    @NativeType("ALvoid")
    public static void alEnable(@NativeType("ALenum") int target) {
        long __functionAddress = AL.getICD().alEnable;
        invokeV(target, __functionAddress);
    }

    // --- [ alDisable ] ---

    /**
     * Disables AL capabilities.
     *
     * @param target the capability to disable
     */
    @NativeType("ALvoid")
    public static void alDisable(@NativeType("ALenum") int target) {
        long __functionAddress = AL.getICD().alDisable;
        invokeV(target, __functionAddress);
    }

    // --- [ alIsEnabled ] ---

    /**
     * Queries whether a given capability is currently enabled or not.
     *
     * @param target the capability to query
     */
    @NativeType("ALboolean")
    public static boolean alIsEnabled(@NativeType("ALenum") int target) {
        long __functionAddress = AL.getICD().alIsEnabled;
        return invokeZ(target, __functionAddress);
    }

    // --- [ alGetBoolean ] ---

    /**
     * Returns the boolean value of the specified parameter.
     *
     * @param paramName the parameter to query
     */
    @NativeType("ALboolean")
    public static boolean alGetBoolean(@NativeType("ALenum") int paramName) {
        long __functionAddress = AL.getICD().alGetBoolean;
        return invokeZ(paramName, __functionAddress);
    }

    // --- [ alGetInteger ] ---

    /**
     * Returns the integer value of the specified parameter.
     *
     * @param paramName the parameter to query. One of:<br><table><tr><td>{@link #AL_DOPPLER_FACTOR DOPPLER_FACTOR}</td><td>{@link #AL_DISTANCE_MODEL DISTANCE_MODEL}</td><td>{@link AL11#AL_SPEED_OF_SOUND SPEED_OF_SOUND}</td></tr></table>
     */
    @NativeType("ALint")
    public static int alGetInteger(@NativeType("ALenum") int paramName) {
        long __functionAddress = AL.getICD().alGetInteger;
        return invokeI(paramName, __functionAddress);
    }

    // --- [ alGetFloat ] ---

    /**
     * Returns the float value of the specified parameter.
     *
     * @param paramName the parameter to query. One of:<br><table><tr><td>{@link #AL_DOPPLER_FACTOR DOPPLER_FACTOR}</td><td>{@link #AL_DISTANCE_MODEL DISTANCE_MODEL}</td><td>{@link AL11#AL_SPEED_OF_SOUND SPEED_OF_SOUND}</td></tr></table>
     */
    @NativeType("ALfloat")
    public static float alGetFloat(@NativeType("ALenum") int paramName) {
        long __functionAddress = AL.getICD().alGetFloat;
        return invokeF(paramName, __functionAddress);
    }

    // --- [ alGetDouble ] ---

    /**
     * Returns the double value of the specified parameter.
     *
     * @param paramName the parameter to query. One of:<br><table><tr><td>{@link #AL_DOPPLER_FACTOR DOPPLER_FACTOR}</td><td>{@link #AL_DISTANCE_MODEL DISTANCE_MODEL}</td><td>{@link AL11#AL_SPEED_OF_SOUND SPEED_OF_SOUND}</td></tr></table>
     */
    @NativeType("ALdouble")
    public static double alGetDouble(@NativeType("ALenum") int paramName) {
        long __functionAddress = AL.getICD().alGetDouble;
        return invokeD(paramName, __functionAddress);
    }

    // --- [ alGetBooleanv ] ---

    /** Unsafe version of: {@link #alGetBooleanv GetBooleanv} */
    public static void nalGetBooleanv(int paramName, long dest) {
        long __functionAddress = AL.getICD().alGetBooleanv;
        invokePV(paramName, dest, __functionAddress);
    }

    /**
     * Pointer version of {@link #alGetBoolean GetBoolean}.
     *
     * @param paramName the parameter to query
     * @param dest      a buffer that will receive the parameter values
     */
    @NativeType("ALvoid")
    public static void alGetBooleanv(@NativeType("ALenum") int paramName, @NativeType("ALboolean *") ByteBuffer dest) {
        if (CHECKS) {
            check(dest, 1);
        }
        nalGetBooleanv(paramName, memAddress(dest));
    }

    // --- [ alGetIntegerv ] ---

    /** Unsafe version of: {@link #alGetIntegerv GetIntegerv} */
    public static void nalGetIntegerv(int paramName, long dest) {
        long __functionAddress = AL.getICD().alGetIntegerv;
        invokePV(paramName, dest, __functionAddress);
    }

    /**
     * Pointer version of {@link #alGetInteger GetInteger}.
     *
     * @param paramName the parameter to query
     * @param dest      a buffer that will receive the parameter values
     */
    @NativeType("ALvoid")
    public static void alGetIntegerv(@NativeType("ALenum") int paramName, @NativeType("ALint *") IntBuffer dest) {
        if (CHECKS) {
            check(dest, 1);
        }
        nalGetIntegerv(paramName, memAddress(dest));
    }

    // --- [ alGetFloatv ] ---

    /** Unsafe version of: {@link #alGetFloatv GetFloatv} */
    public static void nalGetFloatv(int paramName, long dest) {
        long __functionAddress = AL.getICD().alGetFloatv;
        invokePV(paramName, dest, __functionAddress);
    }

    /**
     * Pointer version of {@link #alGetFloat GetFloat}.
     *
     * @param paramName the parameter to query
     * @param dest      a buffer that will receive the parameter values
     */
    @NativeType("ALvoid")
    public static void alGetFloatv(@NativeType("ALenum") int paramName, @NativeType("ALfloat *") FloatBuffer dest) {
        if (CHECKS) {
            check(dest, 1);
        }
        nalGetFloatv(paramName, memAddress(dest));
    }

    // --- [ alGetDoublev ] ---

    /** Unsafe version of: {@link #alGetDoublev GetDoublev} */
    public static void nalGetDoublev(int paramName, long dest) {
        long __functionAddress = AL.getICD().alGetDoublev;
        invokePV(paramName, dest, __functionAddress);
    }

    /**
     * Pointer version of {@link #alGetDouble GetDouble}.
     *
     * @param paramName the parameter to query
     * @param dest      a buffer that will receive the parameter values
     */
    @NativeType("ALvoid")
    public static void alGetDoublev(@NativeType("ALenum") int paramName, @NativeType("ALdouble *") DoubleBuffer dest) {
        if (CHECKS) {
            check(dest, 1);
        }
        nalGetDoublev(paramName, memAddress(dest));
    }

    // --- [ alGetString ] ---

    /** Unsafe version of: {@link #alGetString GetString} */
    public static long nalGetString(int paramName) {
        long __functionAddress = AL.getICD().alGetString;
        return invokeP(paramName, __functionAddress);
    }

    /**
     * Returns the string value of the specified parameter
     *
     * @param paramName the parameter to query. One of:<br><table><tr><td>{@link #AL_VENDOR VENDOR}</td><td>{@link #AL_VERSION VERSION}</td><td>{@link #AL_RENDERER RENDERER}</td><td>{@link #AL_EXTENSIONS EXTENSIONS}</td></tr></table>
     */
    @Nullable
    @NativeType("ALchar const *")
    public static String alGetString(@NativeType("ALenum") int paramName) {
        long __result = nalGetString(paramName);
        return memUTF8Safe(__result);
    }

    // --- [ alDistanceModel ] ---

    /**
     * Sets the distance attenuation model.
     * 
     * <p>Samples usually use the entire dynamic range of the chosen format/encoding, independent of their real world intensity. For example, a jet engine and a
     * clockwork both will have samples with full amplitude. The application will then have to adjust source gain accordingly to account for relative differences.</p>
     * 
     * <p>Source gain is then attenuated by distance. The effective attenuation of a source depends on many factors, among which distance attenuation and source
     * and listener gain are only some of the contributing factors. Even if the source and listener gain exceed 1.0 (amplification beyond the guaranteed
     * dynamic range), distance and other attenuation might ultimately limit the overall gain to a value below 1.0.</p>
     * 
     * <p>OpenAL currently supports three modes of operation with respect to distance attenuation, including one that is similar to the IASIG I3DL2 model. The
     * application can choose one of these models (or chooses to disable distance-dependent attenuation) on a per-context basis.</p>
     *
     * @param modelName the distance attenuation model to set. One of:<br><table><tr><td>{@link #AL_INVERSE_DISTANCE INVERSE_DISTANCE}</td><td>{@link #AL_INVERSE_DISTANCE_CLAMPED INVERSE_DISTANCE_CLAMPED}</td><td>{@link AL11#AL_LINEAR_DISTANCE LINEAR_DISTANCE}</td><td>{@link AL11#AL_LINEAR_DISTANCE_CLAMPED LINEAR_DISTANCE_CLAMPED}</td></tr><tr><td>{@link AL11#AL_EXPONENT_DISTANCE EXPONENT_DISTANCE}</td><td>{@link AL11#AL_EXPONENT_DISTANCE_CLAMPED EXPONENT_DISTANCE_CLAMPED}</td><td>{@link #AL_NONE NONE}</td></tr></table>
     */
    @NativeType("ALvoid")
    public static void alDistanceModel(@NativeType("ALenum") int modelName) {
        long __functionAddress = AL.getICD().alDistanceModel;
        invokeV(modelName, __functionAddress);
    }

    // --- [ alDopplerFactor ] ---

    /**
     * Sets the doppler effect factor.
     * 
     * <p>The Doppler Effect depends on the velocities of source and listener relative to the medium, and the propagation speed of sound in that medium. The
     * application might want to emphasize or de-emphasize the Doppler Effect as physically accurate calculation might not give the desired results. The amount
     * of frequency shift (pitch change) is proportional to the speed of listener and source along their line of sight. The Doppler Effect as implemented by
     * OpenAL is described by the formula below. Effects of the medium (air, water) moving with respect to listener and source are ignored.</p>
     * 
     * <pre><code>
     * SS: AL_SPEED_OF_SOUND = speed of sound (default value 343.3)
     * DF: AL_DOPPLER_FACTOR = Doppler factor (default 1.0)
     * vls: Listener velocity scalar (scalar, projected on source-to-listener vector)
     * vss: Source velocity scalar (scalar, projected on source-to-listener vector)
     * f: Frequency of sample
     * f': effective Doppler shifted frequency
     * 
     * 3D Mathematical representation of vls and vss:
     * 
     * Mag(vector) = sqrt(vector.x * vector.x + vector.y * vector.y + vector.z * vector.z)
     * DotProduct(v1, v2) = (v1.x * v2.x + v1.y * v2.y + v1.z * v2.z)
     * 
     * SL = source to listener vector
     * SV = Source velocity vector
     * LV = Listener velocity vector
     * 
     * vls = DotProduct(SL, LV) / Mag(SL)
     * vss = DotProduct(SL, SV) / Mag(SL)
     * 
     * Dopper Calculation:
     * 
     * vss = min(vss, SS / DF)
     * vls = min(vls, SS / DF)
     * 
     * f' = f * (SS - DF * vls) / (SS - DF * vss)</code></pre>
     * 
     * <p>The {@code dopplerFactor} is a simple scaling of source and listener velocities to exaggerate or deemphasize the Doppler (pitch) shift resulting from
     * the calculation.</p>
     *
     * @param dopplerFactor the doppler factor
     */
    @NativeType("ALvoid")
    public static void alDopplerFactor(@NativeType("ALfloat") float dopplerFactor) {
        long __functionAddress = AL.getICD().alDopplerFactor;
        invokeV(dopplerFactor, __functionAddress);
    }

    // --- [ alDopplerVelocity ] ---

    /**
     * Sets the doppler effect propagation velocity.
     * 
     * <p>The OpenAL 1.1 Doppler implementation is different than that of OpenAL 1.0, because the older implementation was confusing and not implemented
     * consistently. The new "speed of sound" property makes the 1.1 implementation more intuitive than the old implementation. If your implementation wants to
     * support the AL_DOPPLER_VELOCITY parameter (the alDopplerVelocity call will remain as an entry point so that 1.0 applications can link with a 1.1
     * library), the above formula can be changed to the following:</p>
     * 
     * <pre><code>
     * vss = min(vss, (SS * DV)/DF)
     * vls = min(vls, (SS * DV)/DF)
     * 
     * f' = f * (SS * DV - DF*vls) / (SS * DV - DF * vss)</code></pre>
     * 
     * <p>OpenAL 1.1 programmers would never use AL_DOPPLER_VELOCITY (which defaults to 1.0).</p>
     *
     * @param dopplerVelocity the doppler velocity
     */
    @NativeType("ALvoid")
    public static void alDopplerVelocity(@NativeType("ALfloat") float dopplerVelocity) {
        long __functionAddress = AL.getICD().alDopplerVelocity;
        invokeV(dopplerVelocity, __functionAddress);
    }

    // --- [ alListenerf ] ---

    /**
     * Sets the float value of a listener parameter.
     *
     * @param paramName the parameter to modify. One of:<br><table><tr><td>{@link #AL_ORIENTATION ORIENTATION}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td></tr></table>
     * @param value     the parameter value
     */
    @NativeType("ALvoid")
    public static void alListenerf(@NativeType("ALenum") int paramName, @NativeType("ALfloat") float value) {
        long __functionAddress = AL.getICD().alListenerf;
        invokeV(paramName, value, __functionAddress);
    }

    // --- [ alListeneri ] ---

    /**
     * Integer version of {@link #alListenerf Listenerf}.
     *
     * @param paramName the parameter to modify. One of:<br><table><tr><td>{@link #AL_ORIENTATION ORIENTATION}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td></tr></table>
     * @param values    the parameter value
     */
    @NativeType("ALvoid")
    public static void alListeneri(@NativeType("ALenum") int paramName, @NativeType("ALint") int values) {
        long __functionAddress = AL.getICD().alListeneri;
        invokeV(paramName, values, __functionAddress);
    }

    // --- [ alListener3f ] ---

    /**
     * Sets the 3 dimensional float values of a listener parameter.
     *
     * @param paramName the parameter to modify. One of:<br><table><tr><td>{@link #AL_ORIENTATION ORIENTATION}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td></tr></table>
     * @param value1    the first value
     * @param value2    the second value
     * @param value3    the third value
     */
    @NativeType("ALvoid")
    public static void alListener3f(@NativeType("ALenum") int paramName, @NativeType("ALfloat") float value1, @NativeType("ALfloat") float value2, @NativeType("ALfloat") float value3) {
        long __functionAddress = AL.getICD().alListener3f;
        invokeV(paramName, value1, value2, value3, __functionAddress);
    }

    // --- [ alListenerfv ] ---

    /** Unsafe version of: {@link #alListenerfv Listenerfv} */
    public static void nalListenerfv(int paramName, long values) {
        long __functionAddress = AL.getICD().alListenerfv;
        invokePV(paramName, values, __functionAddress);
    }

    /**
     * Pointer version of {@link #alListenerf Listenerf}.
     *
     * @param paramName the parameter to modify
     * @param values    the parameter values
     */
    @NativeType("ALvoid")
    public static void alListenerfv(@NativeType("ALenum") int paramName, @NativeType("ALfloat const *") FloatBuffer values) {
        if (CHECKS) {
            check(values, 1);
        }
        nalListenerfv(paramName, memAddress(values));
    }

    // --- [ alGetListenerf ] ---

    /** Unsafe version of: {@link #alGetListenerf GetListenerf} */
    public static void nalGetListenerf(int paramName, long value) {
        long __functionAddress = AL.getICD().alGetListenerf;
        invokePV(paramName, value, __functionAddress);
    }

    /**
     * Returns the float value of a listener parameter.
     *
     * @param paramName the parameter to query. One of:<br><table><tr><td>{@link #AL_ORIENTATION ORIENTATION}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td></tr></table>
     * @param value     the parameter value
     */
    @NativeType("ALvoid")
    public static void alGetListenerf(@NativeType("ALenum") int paramName, @NativeType("ALfloat *") FloatBuffer value) {
        if (CHECKS) {
            check(value, 1);
        }
        nalGetListenerf(paramName, memAddress(value));
    }

    /**
     * Returns the float value of a listener parameter.
     *
     * @param paramName the parameter to query. One of:<br><table><tr><td>{@link #AL_ORIENTATION ORIENTATION}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td></tr></table>
     */
    @NativeType("ALvoid")
    public static float alGetListenerf(@NativeType("ALenum") int paramName) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            FloatBuffer value = stack.callocFloat(1);
            nalGetListenerf(paramName, memAddress(value));
            return value.get(0);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alGetListeneri ] ---

    /** Unsafe version of: {@link #alGetListeneri GetListeneri} */
    public static void nalGetListeneri(int paramName, long value) {
        long __functionAddress = AL.getICD().alGetListeneri;
        invokePV(paramName, value, __functionAddress);
    }

    /**
     * Returns the integer value of a listener parameter.
     *
     * @param paramName the parameter to query. One of:<br><table><tr><td>{@link #AL_ORIENTATION ORIENTATION}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td></tr></table>
     * @param value     the parameter value
     */
    @NativeType("ALvoid")
    public static void alGetListeneri(@NativeType("ALenum") int paramName, @NativeType("ALint *") IntBuffer value) {
        if (CHECKS) {
            check(value, 1);
        }
        nalGetListeneri(paramName, memAddress(value));
    }

    /**
     * Returns the integer value of a listener parameter.
     *
     * @param paramName the parameter to query. One of:<br><table><tr><td>{@link #AL_ORIENTATION ORIENTATION}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td></tr></table>
     */
    @NativeType("ALvoid")
    public static int alGetListeneri(@NativeType("ALenum") int paramName) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            IntBuffer value = stack.callocInt(1);
            nalGetListeneri(paramName, memAddress(value));
            return value.get(0);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alGetListener3f ] ---

    /** Unsafe version of: {@link #alGetListener3f GetListener3f} */
    public static void nalGetListener3f(int paramName, long value1, long value2, long value3) {
        long __functionAddress = AL.getICD().alGetListener3f;
        invokePPPV(paramName, value1, value2, value3, __functionAddress);
    }

    /**
     * Returns the 3 dimensional values of a listener parameter.
     *
     * @param paramName the parameter to query. One of:<br><table><tr><td>{@link #AL_ORIENTATION ORIENTATION}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td></tr></table>
     * @param value1    the first parameter value
     * @param value2    the second parameter value
     * @param value3    the third parameter value
     */
    @NativeType("ALvoid")
    public static void alGetListener3f(@NativeType("ALenum") int paramName, @NativeType("ALfloat *") FloatBuffer value1, @NativeType("ALfloat *") FloatBuffer value2, @NativeType("ALfloat *") FloatBuffer value3) {
        if (CHECKS) {
            check(value1, 1);
            check(value2, 1);
            check(value3, 1);
        }
        nalGetListener3f(paramName, memAddress(value1), memAddress(value2), memAddress(value3));
    }

    // --- [ alGetListenerfv ] ---

    /** Unsafe version of: {@link #alGetListenerfv GetListenerfv} */
    public static void nalGetListenerfv(int paramName, long values) {
        long __functionAddress = AL.getICD().alGetListenerfv;
        invokePV(paramName, values, __functionAddress);
    }

    /**
     * Returns float values of a listener parameter.
     *
     * @param paramName the parameter to query. One of:<br><table><tr><td>{@link #AL_ORIENTATION ORIENTATION}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td></tr></table>
     * @param values    the parameter values
     */
    @NativeType("ALvoid")
    public static void alGetListenerfv(@NativeType("ALenum") int paramName, @NativeType("ALfloat *") FloatBuffer values) {
        if (CHECKS) {
            check(values, 1);
        }
        nalGetListenerfv(paramName, memAddress(values));
    }

    // --- [ alGenSources ] ---

    /**
     * Unsafe version of: {@link #alGenSources GenSources}
     *
     * @param n the number of source names to generated
     */
    public static void nalGenSources(int n, long srcNames) {
        long __functionAddress = AL.getICD().alGenSources;
        invokePV(n, srcNames, __functionAddress);
    }

    /**
     * Requests a number of source names.
     *
     * @param srcNames the buffer that will receive the source names
     */
    @NativeType("ALvoid")
    public static void alGenSources(@NativeType("ALuint *") IntBuffer srcNames) {
        nalGenSources(srcNames.remaining(), memAddress(srcNames));
    }

    /** Requests a number of source names. */
    @NativeType("ALvoid")
    public static int alGenSources() {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            IntBuffer srcNames = stack.callocInt(1);
            nalGenSources(1, memAddress(srcNames));
            return srcNames.get(0);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alDeleteSources ] ---

    /**
     * Unsafe version of: {@link #alDeleteSources DeleteSources}
     *
     * @param n the number of sources to delete
     */
    public static void nalDeleteSources(int n, long sources) {
        long __functionAddress = AL.getICD().alDeleteSources;
        invokePV(n, sources, __functionAddress);
    }

    /**
     * Requests the deletion of a number of sources.
     *
     * @param sources the sources to delete
     */
    @NativeType("ALvoid")
    public static void alDeleteSources(@NativeType("ALuint *") IntBuffer sources) {
        nalDeleteSources(sources.remaining(), memAddress(sources));
    }

    /** Requests the deletion of a number of sources. */
    @NativeType("ALvoid")
    public static void alDeleteSources(@NativeType("ALuint *") int source) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            IntBuffer sources = stack.ints(source);
            nalDeleteSources(1, memAddress(sources));
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alIsSource ] ---

    /**
     * Verifies whether the specified object name is a source name.
     *
     * @param sourceName a value that may be a source name
     */
    @NativeType("ALboolean")
    public static boolean alIsSource(@NativeType("ALuint") int sourceName) {
        long __functionAddress = AL.getICD().alIsSource;
        return invokeZ(sourceName, __functionAddress);
    }

    // --- [ alSourcef ] ---

    /**
     * Sets the float value of a source parameter.
     *
     * @param source the source to modify
     * @param param  the parameter to modify. One of:<br><table><tr><td>{@link #AL_CONE_INNER_ANGLE CONE_INNER_ANGLE}</td><td>{@link #AL_CONE_OUTER_ANGLE CONE_OUTER_ANGLE}</td><td>{@link #AL_PITCH PITCH}</td><td>{@link #AL_DIRECTION DIRECTION}</td><td>{@link #AL_LOOPING LOOPING}</td><td>{@link #AL_BUFFER BUFFER}</td><td>{@link #AL_SOURCE_STATE SOURCE_STATE}</td></tr><tr><td>{@link #AL_CONE_OUTER_GAIN CONE_OUTER_GAIN}</td><td>{@link #AL_SOURCE_TYPE SOURCE_TYPE}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td><td>{@link #AL_REFERENCE_DISTANCE REFERENCE_DISTANCE}</td><td>{@link #AL_ROLLOFF_FACTOR ROLLOFF_FACTOR}</td></tr><tr><td>{@link #AL_MAX_DISTANCE MAX_DISTANCE}</td></tr></table>
     * @param value  the parameter value
     */
    @NativeType("ALvoid")
    public static void alSourcef(@NativeType("ALuint") int source, @NativeType("ALenum") int param, @NativeType("ALfloat") float value) {
        long __functionAddress = AL.getICD().alSourcef;
        invokeV(source, param, value, __functionAddress);
    }

    // --- [ alSource3f ] ---

    /**
     * Sets the 3 dimensional values of a source parameter.
     *
     * @param source the source to modify
     * @param param  the parameter to modify. One of:<br><table><tr><td>{@link #AL_CONE_INNER_ANGLE CONE_INNER_ANGLE}</td><td>{@link #AL_CONE_OUTER_ANGLE CONE_OUTER_ANGLE}</td><td>{@link #AL_PITCH PITCH}</td><td>{@link #AL_DIRECTION DIRECTION}</td><td>{@link #AL_LOOPING LOOPING}</td><td>{@link #AL_BUFFER BUFFER}</td><td>{@link #AL_SOURCE_STATE SOURCE_STATE}</td></tr><tr><td>{@link #AL_CONE_OUTER_GAIN CONE_OUTER_GAIN}</td><td>{@link #AL_SOURCE_TYPE SOURCE_TYPE}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td><td>{@link #AL_REFERENCE_DISTANCE REFERENCE_DISTANCE}</td><td>{@link #AL_ROLLOFF_FACTOR ROLLOFF_FACTOR}</td></tr><tr><td>{@link #AL_MAX_DISTANCE MAX_DISTANCE}</td></tr></table>
     * @param v1     the first parameter value
     * @param v2     the second parameter value
     * @param v3     the third parameter value
     */
    @NativeType("ALvoid")
    public static void alSource3f(@NativeType("ALuint") int source, @NativeType("ALenum") int param, @NativeType("ALfloat") float v1, @NativeType("ALfloat") float v2, @NativeType("ALfloat") float v3) {
        long __functionAddress = AL.getICD().alSource3f;
        invokeV(source, param, v1, v2, v3, __functionAddress);
    }

    // --- [ alSourcefv ] ---

    /** Unsafe version of: {@link #alSourcefv Sourcefv} */
    public static void nalSourcefv(int source, int param, long values) {
        long __functionAddress = AL.getICD().alSourcefv;
        invokePV(source, param, values, __functionAddress);
    }

    /**
     * Pointer version of {@link #alSourcef Sourcef}.
     *
     * @param source the source to modify
     * @param param  the parameter to modify
     * @param values the parameter values
     */
    @NativeType("ALvoid")
    public static void alSourcefv(@NativeType("ALuint") int source, @NativeType("ALenum") int param, @NativeType("ALfloat const *") FloatBuffer values) {
        if (CHECKS) {
            check(values, 1);
        }
        nalSourcefv(source, param, memAddress(values));
    }

    // --- [ alSourcei ] ---

    /**
     * Integer version of {@link #alSourcef Sourcef}.
     *
     * @param source the source to modify
     * @param param  the parameter to modify
     * @param value  the parameter value
     */
    @NativeType("ALvoid")
    public static void alSourcei(@NativeType("ALuint") int source, @NativeType("ALenum") int param, @NativeType("ALint") int value) {
        long __functionAddress = AL.getICD().alSourcei;
        invokeV(source, param, value, __functionAddress);
    }

    // --- [ alGetSourcef ] ---

    /** Unsafe version of: {@link #alGetSourcef GetSourcef} */
    public static void nalGetSourcef(int source, int param, long value) {
        long __functionAddress = AL.getICD().alGetSourcef;
        invokePV(source, param, value, __functionAddress);
    }

    /**
     * Returns the float value of the specified source parameter.
     *
     * @param source the source to query
     * @param param  the parameter to query. One of:<br><table><tr><td>{@link #AL_CONE_INNER_ANGLE CONE_INNER_ANGLE}</td><td>{@link #AL_CONE_OUTER_ANGLE CONE_OUTER_ANGLE}</td><td>{@link #AL_PITCH PITCH}</td><td>{@link #AL_DIRECTION DIRECTION}</td><td>{@link #AL_LOOPING LOOPING}</td><td>{@link #AL_BUFFER BUFFER}</td><td>{@link #AL_SOURCE_STATE SOURCE_STATE}</td></tr><tr><td>{@link #AL_CONE_OUTER_GAIN CONE_OUTER_GAIN}</td><td>{@link #AL_SOURCE_TYPE SOURCE_TYPE}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td><td>{@link #AL_REFERENCE_DISTANCE REFERENCE_DISTANCE}</td><td>{@link #AL_ROLLOFF_FACTOR ROLLOFF_FACTOR}</td></tr><tr><td>{@link #AL_MAX_DISTANCE MAX_DISTANCE}</td></tr></table>
     * @param value  the parameter value
     */
    @NativeType("ALvoid")
    public static void alGetSourcef(@NativeType("ALuint") int source, @NativeType("ALenum") int param, @NativeType("ALfloat *") FloatBuffer value) {
        if (CHECKS) {
            check(value, 1);
        }
        nalGetSourcef(source, param, memAddress(value));
    }

    /**
     * Returns the float value of the specified source parameter.
     *
     * @param source the source to query
     * @param param  the parameter to query. One of:<br><table><tr><td>{@link #AL_CONE_INNER_ANGLE CONE_INNER_ANGLE}</td><td>{@link #AL_CONE_OUTER_ANGLE CONE_OUTER_ANGLE}</td><td>{@link #AL_PITCH PITCH}</td><td>{@link #AL_DIRECTION DIRECTION}</td><td>{@link #AL_LOOPING LOOPING}</td><td>{@link #AL_BUFFER BUFFER}</td><td>{@link #AL_SOURCE_STATE SOURCE_STATE}</td></tr><tr><td>{@link #AL_CONE_OUTER_GAIN CONE_OUTER_GAIN}</td><td>{@link #AL_SOURCE_TYPE SOURCE_TYPE}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td><td>{@link #AL_REFERENCE_DISTANCE REFERENCE_DISTANCE}</td><td>{@link #AL_ROLLOFF_FACTOR ROLLOFF_FACTOR}</td></tr><tr><td>{@link #AL_MAX_DISTANCE MAX_DISTANCE}</td></tr></table>
     */
    @NativeType("ALvoid")
    public static float alGetSourcef(@NativeType("ALuint") int source, @NativeType("ALenum") int param) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            FloatBuffer value = stack.callocFloat(1);
            nalGetSourcef(source, param, memAddress(value));
            return value.get(0);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alGetSource3f ] ---

    /** Unsafe version of: {@link #alGetSource3f GetSource3f} */
    public static void nalGetSource3f(int source, int param, long v1, long v2, long v3) {
        long __functionAddress = AL.getICD().alGetSource3f;
        invokePPPV(source, param, v1, v2, v3, __functionAddress);
    }

    /**
     * Returns the 3 dimensional values of the specified source parameter.
     *
     * @param source the source to query
     * @param param  the parameter to query. One of:<br><table><tr><td>{@link #AL_CONE_INNER_ANGLE CONE_INNER_ANGLE}</td><td>{@link #AL_CONE_OUTER_ANGLE CONE_OUTER_ANGLE}</td><td>{@link #AL_PITCH PITCH}</td><td>{@link #AL_DIRECTION DIRECTION}</td><td>{@link #AL_LOOPING LOOPING}</td><td>{@link #AL_BUFFER BUFFER}</td><td>{@link #AL_SOURCE_STATE SOURCE_STATE}</td></tr><tr><td>{@link #AL_CONE_OUTER_GAIN CONE_OUTER_GAIN}</td><td>{@link #AL_SOURCE_TYPE SOURCE_TYPE}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td><td>{@link #AL_REFERENCE_DISTANCE REFERENCE_DISTANCE}</td><td>{@link #AL_ROLLOFF_FACTOR ROLLOFF_FACTOR}</td></tr><tr><td>{@link #AL_MAX_DISTANCE MAX_DISTANCE}</td></tr></table>
     * @param v1     the first parameter value
     * @param v2     the second parameter value
     * @param v3     the third parameter value
     */
    @NativeType("ALvoid")
    public static void alGetSource3f(@NativeType("ALuint") int source, @NativeType("ALenum") int param, @NativeType("ALfloat *") FloatBuffer v1, @NativeType("ALfloat *") FloatBuffer v2, @NativeType("ALfloat *") FloatBuffer v3) {
        if (CHECKS) {
            check(v1, 1);
            check(v2, 1);
            check(v3, 1);
        }
        nalGetSource3f(source, param, memAddress(v1), memAddress(v2), memAddress(v3));
    }

    // --- [ alGetSourcefv ] ---

    /** Unsafe version of: {@link #alGetSourcefv GetSourcefv} */
    public static void nalGetSourcefv(int source, int param, long values) {
        long __functionAddress = AL.getICD().alGetSourcefv;
        invokePV(source, param, values, __functionAddress);
    }

    /**
     * Returns the float values of the specified source parameter.
     *
     * @param source the source to query
     * @param param  the parameter to query. One of:<br><table><tr><td>{@link #AL_CONE_INNER_ANGLE CONE_INNER_ANGLE}</td><td>{@link #AL_CONE_OUTER_ANGLE CONE_OUTER_ANGLE}</td><td>{@link #AL_PITCH PITCH}</td><td>{@link #AL_DIRECTION DIRECTION}</td><td>{@link #AL_LOOPING LOOPING}</td><td>{@link #AL_BUFFER BUFFER}</td><td>{@link #AL_SOURCE_STATE SOURCE_STATE}</td></tr><tr><td>{@link #AL_CONE_OUTER_GAIN CONE_OUTER_GAIN}</td><td>{@link #AL_SOURCE_TYPE SOURCE_TYPE}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td><td>{@link #AL_REFERENCE_DISTANCE REFERENCE_DISTANCE}</td><td>{@link #AL_ROLLOFF_FACTOR ROLLOFF_FACTOR}</td></tr><tr><td>{@link #AL_MAX_DISTANCE MAX_DISTANCE}</td></tr></table>
     * @param values the parameter values
     */
    @NativeType("ALvoid")
    public static void alGetSourcefv(@NativeType("ALuint") int source, @NativeType("ALenum") int param, @NativeType("ALfloat *") FloatBuffer values) {
        if (CHECKS) {
            check(values, 1);
        }
        nalGetSourcefv(source, param, memAddress(values));
    }

    // --- [ alGetSourcei ] ---

    /** Unsafe version of: {@link #alGetSourcei GetSourcei} */
    public static void nalGetSourcei(int source, int param, long value) {
        long __functionAddress = AL.getICD().alGetSourcei;
        invokePV(source, param, value, __functionAddress);
    }

    /**
     * Returns the integer value of the specified source parameter.
     *
     * @param source the source to query
     * @param param  the parameter to query. One of:<br><table><tr><td>{@link #AL_CONE_INNER_ANGLE CONE_INNER_ANGLE}</td><td>{@link #AL_CONE_OUTER_ANGLE CONE_OUTER_ANGLE}</td><td>{@link #AL_PITCH PITCH}</td><td>{@link #AL_DIRECTION DIRECTION}</td><td>{@link #AL_LOOPING LOOPING}</td><td>{@link #AL_BUFFER BUFFER}</td><td>{@link #AL_SOURCE_STATE SOURCE_STATE}</td></tr><tr><td>{@link #AL_CONE_OUTER_GAIN CONE_OUTER_GAIN}</td><td>{@link #AL_SOURCE_TYPE SOURCE_TYPE}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td><td>{@link #AL_REFERENCE_DISTANCE REFERENCE_DISTANCE}</td><td>{@link #AL_ROLLOFF_FACTOR ROLLOFF_FACTOR}</td></tr><tr><td>{@link #AL_MAX_DISTANCE MAX_DISTANCE}</td></tr></table>
     * @param value  the parameter value
     */
    @NativeType("ALvoid")
    public static void alGetSourcei(@NativeType("ALuint") int source, @NativeType("ALenum") int param, @NativeType("ALint *") IntBuffer value) {
        if (CHECKS) {
            check(value, 1);
        }
        nalGetSourcei(source, param, memAddress(value));
    }

    /**
     * Returns the integer value of the specified source parameter.
     *
     * @param source the source to query
     * @param param  the parameter to query. One of:<br><table><tr><td>{@link #AL_CONE_INNER_ANGLE CONE_INNER_ANGLE}</td><td>{@link #AL_CONE_OUTER_ANGLE CONE_OUTER_ANGLE}</td><td>{@link #AL_PITCH PITCH}</td><td>{@link #AL_DIRECTION DIRECTION}</td><td>{@link #AL_LOOPING LOOPING}</td><td>{@link #AL_BUFFER BUFFER}</td><td>{@link #AL_SOURCE_STATE SOURCE_STATE}</td></tr><tr><td>{@link #AL_CONE_OUTER_GAIN CONE_OUTER_GAIN}</td><td>{@link #AL_SOURCE_TYPE SOURCE_TYPE}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td><td>{@link #AL_REFERENCE_DISTANCE REFERENCE_DISTANCE}</td><td>{@link #AL_ROLLOFF_FACTOR ROLLOFF_FACTOR}</td></tr><tr><td>{@link #AL_MAX_DISTANCE MAX_DISTANCE}</td></tr></table>
     */
    @NativeType("ALvoid")
    public static int alGetSourcei(@NativeType("ALuint") int source, @NativeType("ALenum") int param) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            IntBuffer value = stack.callocInt(1);
            nalGetSourcei(source, param, memAddress(value));
            return value.get(0);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alGetSourceiv ] ---

    /** Unsafe version of: {@link #alGetSourceiv GetSourceiv} */
    public static void nalGetSourceiv(int source, int param, long values) {
        long __functionAddress = AL.getICD().alGetSourceiv;
        invokePV(source, param, values, __functionAddress);
    }

    /**
     * Returns the integer values of the specified source parameter.
     *
     * @param source the source to query
     * @param param  the parameter to query. One of:<br><table><tr><td>{@link #AL_CONE_INNER_ANGLE CONE_INNER_ANGLE}</td><td>{@link #AL_CONE_OUTER_ANGLE CONE_OUTER_ANGLE}</td><td>{@link #AL_PITCH PITCH}</td><td>{@link #AL_DIRECTION DIRECTION}</td><td>{@link #AL_LOOPING LOOPING}</td><td>{@link #AL_BUFFER BUFFER}</td><td>{@link #AL_SOURCE_STATE SOURCE_STATE}</td></tr><tr><td>{@link #AL_CONE_OUTER_GAIN CONE_OUTER_GAIN}</td><td>{@link #AL_SOURCE_TYPE SOURCE_TYPE}</td><td>{@link #AL_POSITION POSITION}</td><td>{@link #AL_VELOCITY VELOCITY}</td><td>{@link #AL_GAIN GAIN}</td><td>{@link #AL_REFERENCE_DISTANCE REFERENCE_DISTANCE}</td><td>{@link #AL_ROLLOFF_FACTOR ROLLOFF_FACTOR}</td></tr><tr><td>{@link #AL_MAX_DISTANCE MAX_DISTANCE}</td></tr></table>
     * @param values the parameter values
     */
    @NativeType("ALvoid")
    public static void alGetSourceiv(@NativeType("ALuint") int source, @NativeType("ALenum") int param, @NativeType("ALint *") IntBuffer values) {
        if (CHECKS) {
            check(values, 1);
        }
        nalGetSourceiv(source, param, memAddress(values));
    }

    // --- [ alSourceQueueBuffers ] ---

    /**
     * Unsafe version of: {@link #alSourceQueueBuffers SourceQueueBuffers}
     *
     * @param numBuffers the number of buffers to queue
     */
    public static void nalSourceQueueBuffers(int sourceName, int numBuffers, long bufferNames) {
        long __functionAddress = AL.getICD().alSourceQueueBuffers;
        invokePV(sourceName, numBuffers, bufferNames, __functionAddress);
    }

    /**
     * Queues up one or multiple buffer names to the specified source.
     * 
     * <p>The buffers will be queued in the sequence in which they appear in the array. This command is legal on a source in any playback state (to allow for
     * streaming, queuing has to be possible on a AL_PLAYING source). All buffers in a queue must have the same format and attributes, with the exception of
     * the {@code NULL} buffer (i.e., 0) which can always be queued.</p>
     *
     * @param sourceName  the target source
     * @param bufferNames the buffer names
     */
    @NativeType("ALvoid")
    public static void alSourceQueueBuffers(@NativeType("ALuint") int sourceName, @NativeType("ALuint *") IntBuffer bufferNames) {
        nalSourceQueueBuffers(sourceName, bufferNames.remaining(), memAddress(bufferNames));
    }

    /**
     * Queues up one or multiple buffer names to the specified source.
     * 
     * <p>The buffers will be queued in the sequence in which they appear in the array. This command is legal on a source in any playback state (to allow for
     * streaming, queuing has to be possible on a AL_PLAYING source). All buffers in a queue must have the same format and attributes, with the exception of
     * the {@code NULL} buffer (i.e., 0) which can always be queued.</p>
     *
     * @param sourceName the target source
     */
    @NativeType("ALvoid")
    public static void alSourceQueueBuffers(@NativeType("ALuint") int sourceName, @NativeType("ALuint *") int bufferName) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            IntBuffer bufferNames = stack.ints(bufferName);
            nalSourceQueueBuffers(sourceName, 1, memAddress(bufferNames));
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alSourceUnqueueBuffers ] ---

    /**
     * Unsafe version of: {@link #alSourceUnqueueBuffers SourceUnqueueBuffers}
     *
     * @param numEntries the number of buffers to unqueue
     */
    public static void nalSourceUnqueueBuffers(int sourceName, int numEntries, long bufferNames) {
        long __functionAddress = AL.getICD().alSourceUnqueueBuffers;
        invokePV(sourceName, numEntries, bufferNames, __functionAddress);
    }

    /**
     * Removes a number of buffer entries that have finished processing, in the order of apperance, from the queue of the specified source.
     * 
     * <p>Once a queue entry for a buffer has been appended to a queue and is pending processing, it should not be changed. Removal of a given queue entry is not
     * possible unless either the source is stopped (in which case then entire queue is considered processed), or if the queue entry has already been processed
     * (AL_PLAYING or AL_PAUSED source). A playing source will enter the AL_STOPPED state if it completes playback of the last buffer in its queue (the same
     * behavior as when a single buffer has been attached to a source and has finished playback).</p>
     *
     * @param sourceName  the target source
     * @param bufferNames the buffer names
     */
    @NativeType("ALvoid")
    public static void alSourceUnqueueBuffers(@NativeType("ALuint") int sourceName, @NativeType("ALuint *") IntBuffer bufferNames) {
        nalSourceUnqueueBuffers(sourceName, bufferNames.remaining(), memAddress(bufferNames));
    }

    /**
     * Removes a number of buffer entries that have finished processing, in the order of apperance, from the queue of the specified source.
     * 
     * <p>Once a queue entry for a buffer has been appended to a queue and is pending processing, it should not be changed. Removal of a given queue entry is not
     * possible unless either the source is stopped (in which case then entire queue is considered processed), or if the queue entry has already been processed
     * (AL_PLAYING or AL_PAUSED source). A playing source will enter the AL_STOPPED state if it completes playback of the last buffer in its queue (the same
     * behavior as when a single buffer has been attached to a source and has finished playback).</p>
     *
     * @param sourceName the target source
     */
    @NativeType("ALvoid")
    public static int alSourceUnqueueBuffers(@NativeType("ALuint") int sourceName) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            IntBuffer bufferNames = stack.callocInt(1);
            nalSourceUnqueueBuffers(sourceName, 1, memAddress(bufferNames));
            return bufferNames.get(0);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alSourcePlay ] ---

    /**
     * Sets the source state to AL_PLAYING.
     * 
     * <p>alSourcePlay applied to an AL_INITIAL source will promote the source to AL_PLAYING, thus the data found in the buffer will be fed into the processing,
     * starting at the beginning. alSourcePlay applied to a AL_PLAYING source will restart the source from the beginning. It will not affect the configuration,
     * and will leave the source in AL_PLAYING state, but reset the sampling offset to the beginning. alSourcePlay applied to a AL_PAUSED source will resume
     * processing using the source state as preserved at the alSourcePause operation. alSourcePlay applied to a AL_STOPPED source will propagate it to
     * AL_INITIAL then to AL_PLAYING immediately.</p>
     *
     * @param source the source to play
     */
    @NativeType("ALvoid")
    public static void alSourcePlay(@NativeType("ALuint") int source) {
        long __functionAddress = AL.getICD().alSourcePlay;
        invokeV(source, __functionAddress);
    }

    // --- [ alSourcePause ] ---

    /**
     * Sets the source state to AL_PAUSED.
     * 
     * <p>alSourcePause applied to an AL_INITIAL source is a legal NOP. alSourcePause applied to a AL_PLAYING source will change its state to AL_PAUSED. The
     * source is exempt from processing, its current state is preserved. alSourcePause applied to a AL_PAUSED source is a legal NOP. alSourcePause applied to a
     * AL_STOPPED source is a legal NOP.</p>
     *
     * @param source the source to pause
     */
    @NativeType("ALvoid")
    public static void alSourcePause(@NativeType("ALuint") int source) {
        long __functionAddress = AL.getICD().alSourcePause;
        invokeV(source, __functionAddress);
    }

    // --- [ alSourceStop ] ---

    /**
     * Sets the source state to AL_STOPPED.
     * 
     * <p>alSourceStop applied to an AL_INITIAL source is a legal NOP. alSourceStop applied to a AL_PLAYING source will change its state to AL_STOPPED. The source
     * is exempt from processing, its current state is preserved. alSourceStop applied to a AL_PAUSED source will change its state to AL_STOPPED, with the same
     * consequences as on a AL_PLAYING source. alSourceStop applied to a AL_STOPPED source is a legal NOP.</p>
     *
     * @param source the source to stop
     */
    @NativeType("ALvoid")
    public static void alSourceStop(@NativeType("ALuint") int source) {
        long __functionAddress = AL.getICD().alSourceStop;
        invokeV(source, __functionAddress);
    }

    // --- [ alSourceRewind ] ---

    /**
     * Sets the source state to AL_INITIAL.
     * 
     * <p>alSourceRewind applied to an AL_INITIAL source is a legal NOP. alSourceRewind applied to a AL_PLAYING source will change its state to AL_STOPPED then
     * AL_INITIAL. The source is exempt from processing: its current state is preserved, with the exception of the sampling offset, which is reset to the
     * beginning. alSourceRewind applied to a AL_PAUSED source will change its state to AL_INITIAL, with the same consequences as on a AL_PLAYING source.
     * alSourceRewind applied to an AL_STOPPED source promotes the source to AL_INITIAL, resetting the sampling offset to the beginning.</p>
     *
     * @param source the source to rewind
     */
    @NativeType("ALvoid")
    public static void alSourceRewind(@NativeType("ALuint") int source) {
        long __functionAddress = AL.getICD().alSourceRewind;
        invokeV(source, __functionAddress);
    }

    // --- [ alSourcePlayv ] ---

    /**
     * Unsafe version of: {@link #alSourcePlayv SourcePlayv}
     *
     * @param n the number of sources to play
     */
    public static void nalSourcePlayv(int n, long sources) {
        long __functionAddress = AL.getICD().alSourcePlayv;
        invokePV(n, sources, __functionAddress);
    }

    /**
     * Pointer version of {@link #alSourcePlay SourcePlay}.
     *
     * @param sources the sources to play
     */
    @NativeType("ALvoid")
    public static void alSourcePlayv(@NativeType("ALuint const *") IntBuffer sources) {
        nalSourcePlayv(sources.remaining(), memAddress(sources));
    }

    // --- [ alSourcePausev ] ---

    /**
     * Unsafe version of: {@link #alSourcePausev SourcePausev}
     *
     * @param n the number of sources to pause
     */
    public static void nalSourcePausev(int n, long sources) {
        long __functionAddress = AL.getICD().alSourcePausev;
        invokePV(n, sources, __functionAddress);
    }

    /**
     * Pointer version of {@link #alSourcePause SourcePause}.
     *
     * @param sources the sources to pause
     */
    @NativeType("ALvoid")
    public static void alSourcePausev(@NativeType("ALuint const *") IntBuffer sources) {
        nalSourcePausev(sources.remaining(), memAddress(sources));
    }

    // --- [ alSourceStopv ] ---

    /**
     * Unsafe version of: {@link #alSourceStopv SourceStopv}
     *
     * @param n the number of sources to stop
     */
    public static void nalSourceStopv(int n, long sources) {
        long __functionAddress = AL.getICD().alSourceStopv;
        invokePV(n, sources, __functionAddress);
    }

    /**
     * Pointer version of {@link #alSourceStop SourceStop}.
     *
     * @param sources the sources to stop
     */
    @NativeType("ALvoid")
    public static void alSourceStopv(@NativeType("ALuint const *") IntBuffer sources) {
        nalSourceStopv(sources.remaining(), memAddress(sources));
    }

    // --- [ alSourceRewindv ] ---

    /**
     * Unsafe version of: {@link #alSourceRewindv SourceRewindv}
     *
     * @param n the number of sources to rewind
     */
    public static void nalSourceRewindv(int n, long sources) {
        long __functionAddress = AL.getICD().alSourceRewindv;
        invokePV(n, sources, __functionAddress);
    }

    /**
     * Pointer version of {@link #alSourceRewind SourceRewind}.
     *
     * @param sources the sources to rewind
     */
    @NativeType("ALvoid")
    public static void alSourceRewindv(@NativeType("ALuint const *") IntBuffer sources) {
        nalSourceRewindv(sources.remaining(), memAddress(sources));
    }

    // --- [ alGenBuffers ] ---

    /**
     * Unsafe version of: {@link #alGenBuffers GenBuffers}
     *
     * @param n the number of buffer names to generate
     */
    public static void nalGenBuffers(int n, long bufferNames) {
        long __functionAddress = AL.getICD().alGenBuffers;
        invokePV(n, bufferNames, __functionAddress);
    }

    /**
     * Requests a number of buffer names.
     *
     * @param bufferNames the buffer that will receive the buffer names
     */
    @NativeType("ALvoid")
    public static void alGenBuffers(@NativeType("ALuint *") IntBuffer bufferNames) {
        nalGenBuffers(bufferNames.remaining(), memAddress(bufferNames));
    }

    /** Requests a number of buffer names. */
    @NativeType("ALvoid")
    public static int alGenBuffers() {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            IntBuffer bufferNames = stack.callocInt(1);
            nalGenBuffers(1, memAddress(bufferNames));
            return bufferNames.get(0);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alDeleteBuffers ] ---

    /**
     * Unsafe version of: {@link #alDeleteBuffers DeleteBuffers}
     *
     * @param n the number of buffers to delete
     */
    public static void nalDeleteBuffers(int n, long bufferNames) {
        long __functionAddress = AL.getICD().alDeleteBuffers;
        invokePV(n, bufferNames, __functionAddress);
    }

    /**
     * Requests the deletion of a number of buffers.
     *
     * @param bufferNames the buffers to delete
     */
    @NativeType("ALvoid")
    public static void alDeleteBuffers(@NativeType("ALuint const *") IntBuffer bufferNames) {
        nalDeleteBuffers(bufferNames.remaining(), memAddress(bufferNames));
    }

    /** Requests the deletion of a number of buffers. */
    @NativeType("ALvoid")
    public static void alDeleteBuffers(@NativeType("ALuint const *") int bufferName) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            IntBuffer bufferNames = stack.ints(bufferName);
            nalDeleteBuffers(1, memAddress(bufferNames));
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alIsBuffer ] ---

    /**
     * Verifies whether the specified object name is a buffer name.
     *
     * @param bufferName a value that may be a buffer name
     */
    @NativeType("ALboolean")
    public static boolean alIsBuffer(@NativeType("ALuint") int bufferName) {
        long __functionAddress = AL.getICD().alIsBuffer;
        return invokeZ(bufferName, __functionAddress);
    }

    // --- [ alGetBufferf ] ---

    /** Unsafe version of: {@link #alGetBufferf GetBufferf} */
    public static void nalGetBufferf(int bufferName, int paramName, long value) {
        long __functionAddress = AL.getICD().alGetBufferf;
        invokePV(bufferName, paramName, value, __functionAddress);
    }

    /**
     * Returns the float value of the specified buffer parameter.
     *
     * @param bufferName the buffer to query
     * @param paramName  the parameter to query. One of:<br><table><tr><td>{@link #AL_FREQUENCY FREQUENCY}</td><td>{@link #AL_BITS BITS}</td><td>{@link #AL_CHANNELS CHANNELS}</td><td>{@link #AL_SIZE SIZE}</td></tr></table>
     * @param value      the parameter value
     */
    @NativeType("ALvoid")
    public static void alGetBufferf(@NativeType("ALuint") int bufferName, @NativeType("ALenum") int paramName, @NativeType("ALfloat *") FloatBuffer value) {
        if (CHECKS) {
            check(value, 1);
        }
        nalGetBufferf(bufferName, paramName, memAddress(value));
    }

    /**
     * Returns the float value of the specified buffer parameter.
     *
     * @param bufferName the buffer to query
     * @param paramName  the parameter to query. One of:<br><table><tr><td>{@link #AL_FREQUENCY FREQUENCY}</td><td>{@link #AL_BITS BITS}</td><td>{@link #AL_CHANNELS CHANNELS}</td><td>{@link #AL_SIZE SIZE}</td></tr></table>
     */
    @NativeType("ALvoid")
    public static float alGetBufferf(@NativeType("ALuint") int bufferName, @NativeType("ALenum") int paramName) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            FloatBuffer value = stack.callocFloat(1);
            nalGetBufferf(bufferName, paramName, memAddress(value));
            return value.get(0);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alGetBufferi ] ---

    /** Unsafe version of: {@link #alGetBufferi GetBufferi} */
    public static void nalGetBufferi(int bufferName, int paramName, long value) {
        long __functionAddress = AL.getICD().alGetBufferi;
        invokePV(bufferName, paramName, value, __functionAddress);
    }

    /**
     * Returns the integer value of the specified buffer parameter.
     *
     * @param bufferName the buffer to query
     * @param paramName  the parameter to query. One of:<br><table><tr><td>{@link #AL_FREQUENCY FREQUENCY}</td><td>{@link #AL_BITS BITS}</td><td>{@link #AL_CHANNELS CHANNELS}</td><td>{@link #AL_SIZE SIZE}</td></tr></table>
     * @param value      the parameter value
     */
    @NativeType("ALvoid")
    public static void alGetBufferi(@NativeType("ALuint") int bufferName, @NativeType("ALenum") int paramName, @NativeType("ALint *") IntBuffer value) {
        if (CHECKS) {
            check(value, 1);
        }
        nalGetBufferi(bufferName, paramName, memAddress(value));
    }

    /**
     * Returns the integer value of the specified buffer parameter.
     *
     * @param bufferName the buffer to query
     * @param paramName  the parameter to query. One of:<br><table><tr><td>{@link #AL_FREQUENCY FREQUENCY}</td><td>{@link #AL_BITS BITS}</td><td>{@link #AL_CHANNELS CHANNELS}</td><td>{@link #AL_SIZE SIZE}</td></tr></table>
     */
    @NativeType("ALvoid")
    public static int alGetBufferi(@NativeType("ALuint") int bufferName, @NativeType("ALenum") int paramName) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            IntBuffer value = stack.callocInt(1);
            nalGetBufferi(bufferName, paramName, memAddress(value));
            return value.get(0);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alBufferData ] ---

    /**
     * Unsafe version of: {@link #alBufferData BufferData}
     *
     * @param size the data buffer size, in bytes
     */
    public static void nalBufferData(int bufferName, int format, long data, int size, int frequency) {
        long __functionAddress = AL.getICD().alBufferData;
        invokePV(bufferName, format, data, size, frequency, __functionAddress);
    }

    /**
     * Sets the sample data of the specified buffer.
     * 
     * <p>The data specified is copied to an internal software, or if possible, hardware buffer. The implementation is free to apply decompression, conversion,
     * resampling, and filtering as needed.</p>
     * 
     * <p>8-bit data is expressed as an unsigned value over the range 0 to 255, 128 being an audio output level of zero.</p>
     * 
     * <p>16-bit data is expressed as a signed value over the range -32768 to 32767, 0 being an audio output level of zero. Byte order for 16-bit values is
     * determined by the native format of the CPU.</p>
     * 
     * <p>Stereo data is expressed in an interleaved format, left channel sample followed by the right channel sample.</p>
     * 
     * <p>Buffers containing audio data with more than one channel will be played without 3D spatialization features – these formats are normally used for
     * background music.</p>
     *
     * @param bufferName the buffer to modify
     * @param format     the data format. One of:<br><table><tr><td>{@link #AL_FORMAT_MONO8 FORMAT_MONO8}</td><td>{@link #AL_FORMAT_MONO16 FORMAT_MONO16}</td><td>{@link #AL_FORMAT_STEREO8 FORMAT_STEREO8}</td><td>{@link #AL_FORMAT_STEREO16 FORMAT_STEREO16}</td></tr></table>
     * @param data       the sample data
     * @param frequency  the data frequency
     */
    @NativeType("ALvoid")
    public static void alBufferData(@NativeType("ALuint") int bufferName, @NativeType("ALenum") int format, @NativeType("ALvoid const *") ByteBuffer data, @NativeType("ALsizei") int frequency) {
        nalBufferData(bufferName, format, memAddress(data), data.remaining(), frequency);
    }

    /**
     * Sets the sample data of the specified buffer.
     * 
     * <p>The data specified is copied to an internal software, or if possible, hardware buffer. The implementation is free to apply decompression, conversion,
     * resampling, and filtering as needed.</p>
     * 
     * <p>8-bit data is expressed as an unsigned value over the range 0 to 255, 128 being an audio output level of zero.</p>
     * 
     * <p>16-bit data is expressed as a signed value over the range -32768 to 32767, 0 being an audio output level of zero. Byte order for 16-bit values is
     * determined by the native format of the CPU.</p>
     * 
     * <p>Stereo data is expressed in an interleaved format, left channel sample followed by the right channel sample.</p>
     * 
     * <p>Buffers containing audio data with more than one channel will be played without 3D spatialization features – these formats are normally used for
     * background music.</p>
     *
     * @param bufferName the buffer to modify
     * @param format     the data format. One of:<br><table><tr><td>{@link #AL_FORMAT_MONO8 FORMAT_MONO8}</td><td>{@link #AL_FORMAT_MONO16 FORMAT_MONO16}</td><td>{@link #AL_FORMAT_STEREO8 FORMAT_STEREO8}</td><td>{@link #AL_FORMAT_STEREO16 FORMAT_STEREO16}</td></tr></table>
     * @param data       the sample data
     * @param frequency  the data frequency
     */
    @NativeType("ALvoid")
    public static void alBufferData(@NativeType("ALuint") int bufferName, @NativeType("ALenum") int format, @NativeType("ALvoid const *") ShortBuffer data, @NativeType("ALsizei") int frequency) {
        nalBufferData(bufferName, format, memAddress(data), data.remaining() << 1, frequency);
    }

    /**
     * Sets the sample data of the specified buffer.
     * 
     * <p>The data specified is copied to an internal software, or if possible, hardware buffer. The implementation is free to apply decompression, conversion,
     * resampling, and filtering as needed.</p>
     * 
     * <p>8-bit data is expressed as an unsigned value over the range 0 to 255, 128 being an audio output level of zero.</p>
     * 
     * <p>16-bit data is expressed as a signed value over the range -32768 to 32767, 0 being an audio output level of zero. Byte order for 16-bit values is
     * determined by the native format of the CPU.</p>
     * 
     * <p>Stereo data is expressed in an interleaved format, left channel sample followed by the right channel sample.</p>
     * 
     * <p>Buffers containing audio data with more than one channel will be played without 3D spatialization features – these formats are normally used for
     * background music.</p>
     *
     * @param bufferName the buffer to modify
     * @param format     the data format. One of:<br><table><tr><td>{@link #AL_FORMAT_MONO8 FORMAT_MONO8}</td><td>{@link #AL_FORMAT_MONO16 FORMAT_MONO16}</td><td>{@link #AL_FORMAT_STEREO8 FORMAT_STEREO8}</td><td>{@link #AL_FORMAT_STEREO16 FORMAT_STEREO16}</td></tr></table>
     * @param data       the sample data
     * @param frequency  the data frequency
     */
    @NativeType("ALvoid")
    public static void alBufferData(@NativeType("ALuint") int bufferName, @NativeType("ALenum") int format, @NativeType("ALvoid const *") IntBuffer data, @NativeType("ALsizei") int frequency) {
        nalBufferData(bufferName, format, memAddress(data), data.remaining() << 2, frequency);
    }

    /**
     * Sets the sample data of the specified buffer.
     * 
     * <p>The data specified is copied to an internal software, or if possible, hardware buffer. The implementation is free to apply decompression, conversion,
     * resampling, and filtering as needed.</p>
     * 
     * <p>8-bit data is expressed as an unsigned value over the range 0 to 255, 128 being an audio output level of zero.</p>
     * 
     * <p>16-bit data is expressed as a signed value over the range -32768 to 32767, 0 being an audio output level of zero. Byte order for 16-bit values is
     * determined by the native format of the CPU.</p>
     * 
     * <p>Stereo data is expressed in an interleaved format, left channel sample followed by the right channel sample.</p>
     * 
     * <p>Buffers containing audio data with more than one channel will be played without 3D spatialization features – these formats are normally used for
     * background music.</p>
     *
     * @param bufferName the buffer to modify
     * @param format     the data format. One of:<br><table><tr><td>{@link #AL_FORMAT_MONO8 FORMAT_MONO8}</td><td>{@link #AL_FORMAT_MONO16 FORMAT_MONO16}</td><td>{@link #AL_FORMAT_STEREO8 FORMAT_STEREO8}</td><td>{@link #AL_FORMAT_STEREO16 FORMAT_STEREO16}</td></tr></table>
     * @param data       the sample data
     * @param frequency  the data frequency
     */
    @NativeType("ALvoid")
    public static void alBufferData(@NativeType("ALuint") int bufferName, @NativeType("ALenum") int format, @NativeType("ALvoid const *") FloatBuffer data, @NativeType("ALsizei") int frequency) {
        nalBufferData(bufferName, format, memAddress(data), data.remaining() << 2, frequency);
    }

    // --- [ alGetEnumValue ] ---

    /** Unsafe version of: {@link #alGetEnumValue GetEnumValue} */
    public static int nalGetEnumValue(long enumName) {
        long __functionAddress = AL.getICD().alGetEnumValue;
        return invokePI(enumName, __functionAddress);
    }

    /**
     * Returns the enumeration value of the specified enum.
     *
     * @param enumName the enum name
     */
    @NativeType("ALuint")
    public static int alGetEnumValue(@NativeType("ALchar const *") ByteBuffer enumName) {
        if (CHECKS) {
            checkNT1(enumName);
        }
        return nalGetEnumValue(memAddress(enumName));
    }

    /**
     * Returns the enumeration value of the specified enum.
     *
     * @param enumName the enum name
     */
    @NativeType("ALuint")
    public static int alGetEnumValue(@NativeType("ALchar const *") CharSequence enumName) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nASCII(enumName, true);
            long enumNameEncoded = stack.getPointerAddress();
            return nalGetEnumValue(enumNameEncoded);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alGetProcAddress ] ---

    /** Unsafe version of: {@link #alGetProcAddress GetProcAddress} */
    public static long nalGetProcAddress(long funcName) {
        long __functionAddress = AL.getICD().alGetProcAddress;
        return invokePP(funcName, __functionAddress);
    }

    /**
     * Retrieves extension entry points.
     * 
     * <p>Returns {@code NULL} if no entry point with the name funcName can be found. Implementations are free to return {@code NULL} if an entry point is present, but not
     * applicable for the current context. However the specification does not guarantee this behavior.</p>
     * 
     * <p>Applications can use alGetProcAddress to obtain core API entry points, not just extensions. This is the recommended way to dynamically load and unload
     * OpenAL DLL's as sound drivers.</p>
     *
     * @param funcName the function name
     */
    @NativeType("void *")
    public static long alGetProcAddress(@NativeType("ALchar const *") ByteBuffer funcName) {
        if (CHECKS) {
            checkNT1(funcName);
        }
        return nalGetProcAddress(memAddress(funcName));
    }

    /**
     * Retrieves extension entry points.
     * 
     * <p>Returns {@code NULL} if no entry point with the name funcName can be found. Implementations are free to return {@code NULL} if an entry point is present, but not
     * applicable for the current context. However the specification does not guarantee this behavior.</p>
     * 
     * <p>Applications can use alGetProcAddress to obtain core API entry points, not just extensions. This is the recommended way to dynamically load and unload
     * OpenAL DLL's as sound drivers.</p>
     *
     * @param funcName the function name
     */
    @NativeType("void *")
    public static long alGetProcAddress(@NativeType("ALchar const *") CharSequence funcName) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nASCII(funcName, true);
            long funcNameEncoded = stack.getPointerAddress();
            return nalGetProcAddress(funcNameEncoded);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ alIsExtensionPresent ] ---

    /** Unsafe version of: {@link #alIsExtensionPresent IsExtensionPresent} */
    public static boolean nalIsExtensionPresent(long extName) {
        long __functionAddress = AL.getICD().alIsExtensionPresent;
        return invokePZ(extName, __functionAddress);
    }

    /**
     * Verifies that a given extension is available for the current context and the device it is associated with.
     * 
     * <p>Invalid and unsupported string tokens return ALC_FALSE. {@code extName} is not case sensitive – the implementation will convert the name to all
     * upper-case internally (and will express extension names in upper-case).</p>
     *
     * @param extName the extension name
     */
    @NativeType("ALCboolean")
    public static boolean alIsExtensionPresent(@NativeType("ALchar const *") ByteBuffer extName) {
        if (CHECKS) {
            checkNT1(extName);
        }
        return nalIsExtensionPresent(memAddress(extName));
    }

    /**
     * Verifies that a given extension is available for the current context and the device it is associated with.
     * 
     * <p>Invalid and unsupported string tokens return ALC_FALSE. {@code extName} is not case sensitive – the implementation will convert the name to all
     * upper-case internally (and will express extension names in upper-case).</p>
     *
     * @param extName the extension name
     */
    @NativeType("ALCboolean")
    public static boolean alIsExtensionPresent(@NativeType("ALchar const *") CharSequence extName) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nASCII(extName, true);
            long extNameEncoded = stack.getPointerAddress();
            return nalIsExtensionPresent(extNameEncoded);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    /** Array version of: {@link #alGetIntegerv GetIntegerv} */
    @NativeType("ALvoid")
    public static void alGetIntegerv(@NativeType("ALenum") int paramName, @NativeType("ALint *") int[] dest) {
        long __functionAddress = AL.getICD().alGetIntegerv;
        if (CHECKS) {
            check(dest, 1);
        }
        invokePV(paramName, dest, __functionAddress);
    }

    /** Array version of: {@link #alGetFloatv GetFloatv} */
    @NativeType("ALvoid")
    public static void alGetFloatv(@NativeType("ALenum") int paramName, @NativeType("ALfloat *") float[] dest) {
        long __functionAddress = AL.getICD().alGetFloatv;
        if (CHECKS) {
            check(dest, 1);
        }
        invokePV(paramName, dest, __functionAddress);
    }

    /** Array version of: {@link #alGetDoublev GetDoublev} */
    @NativeType("ALvoid")
    public static void alGetDoublev(@NativeType("ALenum") int paramName, @NativeType("ALdouble *") double[] dest) {
        long __functionAddress = AL.getICD().alGetDoublev;
        if (CHECKS) {
            check(dest, 1);
        }
        invokePV(paramName, dest, __functionAddress);
    }

    /** Array version of: {@link #alListenerfv Listenerfv} */
    @NativeType("ALvoid")
    public static void alListenerfv(@NativeType("ALenum") int paramName, @NativeType("ALfloat const *") float[] values) {
        long __functionAddress = AL.getICD().alListenerfv;
        if (CHECKS) {
            check(values, 1);
        }
        invokePV(paramName, values, __functionAddress);
    }

    /** Array version of: {@link #alGetListenerf GetListenerf} */
    @NativeType("ALvoid")
    public static void alGetListenerf(@NativeType("ALenum") int paramName, @NativeType("ALfloat *") float[] value) {
        long __functionAddress = AL.getICD().alGetListenerf;
        if (CHECKS) {
            check(value, 1);
        }
        invokePV(paramName, value, __functionAddress);
    }

    /** Array version of: {@link #alGetListeneri GetListeneri} */
    @NativeType("ALvoid")
    public static void alGetListeneri(@NativeType("ALenum") int paramName, @NativeType("ALint *") int[] value) {
        long __functionAddress = AL.getICD().alGetListeneri;
        if (CHECKS) {
            check(value, 1);
        }
        invokePV(paramName, value, __functionAddress);
    }

    /** Array version of: {@link #alGetListener3f GetListener3f} */
    @NativeType("ALvoid")
    public static void alGetListener3f(@NativeType("ALenum") int paramName, @NativeType("ALfloat *") float[] value1, @NativeType("ALfloat *") float[] value2, @NativeType("ALfloat *") float[] value3) {
        long __functionAddress = AL.getICD().alGetListener3f;
        if (CHECKS) {
            check(value1, 1);
            check(value2, 1);
            check(value3, 1);
        }
        invokePPPV(paramName, value1, value2, value3, __functionAddress);
    }

    /** Array version of: {@link #alGetListenerfv GetListenerfv} */
    @NativeType("ALvoid")
    public static void alGetListenerfv(@NativeType("ALenum") int paramName, @NativeType("ALfloat *") float[] values) {
        long __functionAddress = AL.getICD().alGetListenerfv;
        if (CHECKS) {
            check(values, 1);
        }
        invokePV(paramName, values, __functionAddress);
    }

    /** Array version of: {@link #alGenSources GenSources} */
    @NativeType("ALvoid")
    public static void alGenSources(@NativeType("ALuint *") int[] srcNames) {
        long __functionAddress = AL.getICD().alGenSources;
        invokePV(srcNames.length, srcNames, __functionAddress);
    }

    /** Array version of: {@link #alDeleteSources DeleteSources} */
    @NativeType("ALvoid")
    public static void alDeleteSources(@NativeType("ALuint *") int[] sources) {
        long __functionAddress = AL.getICD().alDeleteSources;
        invokePV(sources.length, sources, __functionAddress);
    }

    /** Array version of: {@link #alSourcefv Sourcefv} */
    @NativeType("ALvoid")
    public static void alSourcefv(@NativeType("ALuint") int source, @NativeType("ALenum") int param, @NativeType("ALfloat const *") float[] values) {
        long __functionAddress = AL.getICD().alSourcefv;
        if (CHECKS) {
            check(values, 1);
        }
        invokePV(source, param, values, __functionAddress);
    }

    /** Array version of: {@link #alGetSourcef GetSourcef} */
    @NativeType("ALvoid")
    public static void alGetSourcef(@NativeType("ALuint") int source, @NativeType("ALenum") int param, @NativeType("ALfloat *") float[] value) {
        long __functionAddress = AL.getICD().alGetSourcef;
        if (CHECKS) {
            check(value, 1);
        }
        invokePV(source, param, value, __functionAddress);
    }

    /** Array version of: {@link #alGetSource3f GetSource3f} */
    @NativeType("ALvoid")
    public static void alGetSource3f(@NativeType("ALuint") int source, @NativeType("ALenum") int param, @NativeType("ALfloat *") float[] v1, @NativeType("ALfloat *") float[] v2, @NativeType("ALfloat *") float[] v3) {
        long __functionAddress = AL.getICD().alGetSource3f;
        if (CHECKS) {
            check(v1, 1);
            check(v2, 1);
            check(v3, 1);
        }
        invokePPPV(source, param, v1, v2, v3, __functionAddress);
    }

    /** Array version of: {@link #alGetSourcefv GetSourcefv} */
    @NativeType("ALvoid")
    public static void alGetSourcefv(@NativeType("ALuint") int source, @NativeType("ALenum") int param, @NativeType("ALfloat *") float[] values) {
        long __functionAddress = AL.getICD().alGetSourcefv;
        if (CHECKS) {
            check(values, 1);
        }
        invokePV(source, param, values, __functionAddress);
    }

    /** Array version of: {@link #alGetSourcei GetSourcei} */
    @NativeType("ALvoid")
    public static void alGetSourcei(@NativeType("ALuint") int source, @NativeType("ALenum") int param, @NativeType("ALint *") int[] value) {
        long __functionAddress = AL.getICD().alGetSourcei;
        if (CHECKS) {
            check(value, 1);
        }
        invokePV(source, param, value, __functionAddress);
    }

    /** Array version of: {@link #alGetSourceiv GetSourceiv} */
    @NativeType("ALvoid")
    public static void alGetSourceiv(@NativeType("ALuint") int source, @NativeType("ALenum") int param, @NativeType("ALint *") int[] values) {
        long __functionAddress = AL.getICD().alGetSourceiv;
        if (CHECKS) {
            check(values, 1);
        }
        invokePV(source, param, values, __functionAddress);
    }

    /** Array version of: {@link #alSourceQueueBuffers SourceQueueBuffers} */
    @NativeType("ALvoid")
    public static void alSourceQueueBuffers(@NativeType("ALuint") int sourceName, @NativeType("ALuint *") int[] bufferNames) {
        long __functionAddress = AL.getICD().alSourceQueueBuffers;
        invokePV(sourceName, bufferNames.length, bufferNames, __functionAddress);
    }

    /** Array version of: {@link #alSourceUnqueueBuffers SourceUnqueueBuffers} */
    @NativeType("ALvoid")
    public static void alSourceUnqueueBuffers(@NativeType("ALuint") int sourceName, @NativeType("ALuint *") int[] bufferNames) {
        long __functionAddress = AL.getICD().alSourceUnqueueBuffers;
        invokePV(sourceName, bufferNames.length, bufferNames, __functionAddress);
    }

    /** Array version of: {@link #alSourcePlayv SourcePlayv} */
    @NativeType("ALvoid")
    public static void alSourcePlayv(@NativeType("ALuint const *") int[] sources) {
        long __functionAddress = AL.getICD().alSourcePlayv;
        invokePV(sources.length, sources, __functionAddress);
    }

    /** Array version of: {@link #alSourcePausev SourcePausev} */
    @NativeType("ALvoid")
    public static void alSourcePausev(@NativeType("ALuint const *") int[] sources) {
        long __functionAddress = AL.getICD().alSourcePausev;
        invokePV(sources.length, sources, __functionAddress);
    }

    /** Array version of: {@link #alSourceStopv SourceStopv} */
    @NativeType("ALvoid")
    public static void alSourceStopv(@NativeType("ALuint const *") int[] sources) {
        long __functionAddress = AL.getICD().alSourceStopv;
        invokePV(sources.length, sources, __functionAddress);
    }

    /** Array version of: {@link #alSourceRewindv SourceRewindv} */
    @NativeType("ALvoid")
    public static void alSourceRewindv(@NativeType("ALuint const *") int[] sources) {
        long __functionAddress = AL.getICD().alSourceRewindv;
        invokePV(sources.length, sources, __functionAddress);
    }

    /** Array version of: {@link #alGenBuffers GenBuffers} */
    @NativeType("ALvoid")
    public static void alGenBuffers(@NativeType("ALuint *") int[] bufferNames) {
        long __functionAddress = AL.getICD().alGenBuffers;
        invokePV(bufferNames.length, bufferNames, __functionAddress);
    }

    /** Array version of: {@link #alDeleteBuffers DeleteBuffers} */
    @NativeType("ALvoid")
    public static void alDeleteBuffers(@NativeType("ALuint const *") int[] bufferNames) {
        long __functionAddress = AL.getICD().alDeleteBuffers;
        invokePV(bufferNames.length, bufferNames, __functionAddress);
    }

    /** Array version of: {@link #alGetBufferf GetBufferf} */
    @NativeType("ALvoid")
    public static void alGetBufferf(@NativeType("ALuint") int bufferName, @NativeType("ALenum") int paramName, @NativeType("ALfloat *") float[] value) {
        long __functionAddress = AL.getICD().alGetBufferf;
        if (CHECKS) {
            check(value, 1);
        }
        invokePV(bufferName, paramName, value, __functionAddress);
    }

    /** Array version of: {@link #alGetBufferi GetBufferi} */
    @NativeType("ALvoid")
    public static void alGetBufferi(@NativeType("ALuint") int bufferName, @NativeType("ALenum") int paramName, @NativeType("ALint *") int[] value) {
        long __functionAddress = AL.getICD().alGetBufferi;
        if (CHECKS) {
            check(value, 1);
        }
        invokePV(bufferName, paramName, value, __functionAddress);
    }

    /** Array version of: {@link #alBufferData BufferData} */
    @NativeType("ALvoid")
    public static void alBufferData(@NativeType("ALuint") int bufferName, @NativeType("ALenum") int format, @NativeType("ALvoid const *") short[] data, @NativeType("ALsizei") int frequency) {
        long __functionAddress = AL.getICD().alBufferData;
        invokePV(bufferName, format, data, data.length << 1, frequency, __functionAddress);
    }

    /** Array version of: {@link #alBufferData BufferData} */
    @NativeType("ALvoid")
    public static void alBufferData(@NativeType("ALuint") int bufferName, @NativeType("ALenum") int format, @NativeType("ALvoid const *") int[] data, @NativeType("ALsizei") int frequency) {
        long __functionAddress = AL.getICD().alBufferData;
        invokePV(bufferName, format, data, data.length << 2, frequency, __functionAddress);
    }

    /** Array version of: {@link #alBufferData BufferData} */
    @NativeType("ALvoid")
    public static void alBufferData(@NativeType("ALuint") int bufferName, @NativeType("ALenum") int format, @NativeType("ALvoid const *") float[] data, @NativeType("ALsizei") int frequency) {
        long __functionAddress = AL.getICD().alBufferData;
        invokePV(bufferName, format, data, data.length << 2, frequency, __functionAddress);
    }

}
