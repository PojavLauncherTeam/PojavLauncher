/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.openal;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.system.*;

import javax.annotation.*;
import java.nio.*;
import java.util.*;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.EXTThreadLocalContext.*;
import static org.lwjgl.system.APIUtil.*;
import static org.lwjgl.system.JNI.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * This class must be used before any OpenAL function is called. It has the following responsibilities:
 * <ul>
 * <li>Creates instances of {@link ALCapabilities} classes. An {@code ALCapabilities} instance contains flags for functionality that is available in an OpenAL
 * context. Internally, it also contains function pointers that are only valid in that specific OpenAL context.</li>
 * <li>Maintains thread-local and global state for {@code ALCapabilities} instances, corresponding to OpenAL contexts that are current in those threads and the
 * entire process, respectively.</li>
 * </ul>
 *
 * <h3>ALCapabilities creation</h3>
 * <p>Instances of {@code ALCapabilities} can be created with the {@link #createCapabilities} method. An OpenAL context must be current in the current thread
 * or process before it is called. Calling this method is expensive, so {@code ALCapabilities} instances should be cached in user code.</p>
 *
 * <h3>Thread-local state</h3>
 * <p>Before a function for a given OpenAL context can be called, the corresponding {@code ALCapabilities} instance must be made current in the current
 * thread or process. The user is also responsible for clearing the current {@code ALCapabilities} instance when the context is destroyed or made current in
 * another thread.</p>
 *
 * <p>Note that OpenAL contexts are made current process-wide by default. Current thread-local contexts are only available if the
 * {@link EXTThreadLocalContext ALC_EXT_thread_local_context} extension is supported by the OpenAL implementation. <em>OpenAL Soft</em>, the implementation
 * that LWJGL ships with, supports this extension and performs better when it is used.</p>
 *
 * @see ALC
 */
public final class AL {
// -- Begin LWJGL2 part --
    static {
        // FIXME should be?
        // Sys.initialize(); // init using dummy sys method
	}
    static long alContext;
    static ALCdevice alcDevice;
    static ALCCapabilities alContextCaps;
	static ALCapabilities alCaps;
    
    private static boolean created_lwjgl2 = false;

    /**
     * Creates an OpenAL instance. Using this constructor will cause OpenAL to
     * open the device using supplied device argument, and create a context using the context values
     * supplied.
     *
     * @param deviceArguments Arguments supplied to native device
     * @param contextFrequency Frequency for mixing output buffer, in units of Hz (Common values include 11025, 22050, and 44100).
     * @param contextRefresh Refresh intervalls, in units of Hz.
     * @param contextSynchronized Flag, indicating a synchronous context.*
     */
    public static void create(String deviceArguments, int contextFrequency, int contextRefresh, boolean contextSynchronized)
        throws LWJGLException {
        create(deviceArguments, contextFrequency, contextRefresh, contextSynchronized, true);
    }

    /**
     * @param openDevice Whether to automatically open the device
     * @see #create(String, int, int, boolean)
     */
    public static void create(String deviceArguments, int contextFrequency, int contextRefresh, boolean contextSynchronized, boolean openDevice)
        throws LWJGLException {
        if (alContext == MemoryUtil.NULL && openDevice) {
            //ALDevice alDevice = ALDevice.create();
            long alDevice = ALC10.alcOpenDevice(deviceArguments);
            if(alDevice == MemoryUtil.NULL){
                throw new LWJGLException("Cannot open the device");
            }

            IntBuffer attribs = BufferUtils.createIntBuffer(16);

            attribs.put(ALC10.ALC_FREQUENCY);
            attribs.put(contextFrequency);

            attribs.put(ALC10.ALC_REFRESH);
            attribs.put(contextRefresh);

            attribs.put(ALC10.ALC_SYNC);
            attribs.put(contextSynchronized ? ALC10.ALC_TRUE : ALC10.ALC_FALSE);

            attribs.put(0);
            attribs.flip();

            long contextHandle = ALC10.alcCreateContext(alDevice, attribs);
            ALC10.alcMakeContextCurrent(contextHandle);
            //alContext = new ALContext(alDevice, contextHandle);
            alContext = ALC10.alcCreateContext(contextHandle, (IntBuffer)null);
            alContextCaps = ALC.createCapabilities(alContext);

            alCaps = AL.createCapabilities(alContextCaps);

            alcDevice = new ALCdevice(alDevice);
            created_lwjgl2 = true;
        }
    }

    public static void create() throws LWJGLException {
        if (alContext == MemoryUtil.NULL) {
            //ALDevice alDevice = ALDevice.create();
            long alDevice = ALC10.alcOpenDevice((ByteBuffer)null);
            if(alDevice == MemoryUtil.NULL){
                throw new LWJGLException("Cannot open the device");
            }

            IntBuffer attribs = BufferUtils.createIntBuffer(16);

            attribs.put(ALC10.ALC_FREQUENCY);
            attribs.put(44100);

            attribs.put(ALC10.ALC_REFRESH);
            attribs.put(60);

            attribs.put(ALC10.ALC_SYNC);
            attribs.put(ALC10.ALC_FALSE);

            attribs.put(0);
            attribs.flip();

            long contextHandle = ALC10.alcCreateContext(alDevice, attribs);
            ALC10.alcMakeContextCurrent(contextHandle);
            //alContext = new ALContext(alDevice, contextHandle);
            alContext = ALC10.alcCreateContext(contextHandle, (IntBuffer)null);
            alContextCaps = ALC.createCapabilities(alContext);

            alCaps = AL.createCapabilities(alContextCaps);

            alcDevice = new ALCdevice(alDevice);
            created_lwjgl2 = true;
        }
	}
    
    public static boolean isCreated() {
        return created_lwjgl2;
	}
    
    public static ALCdevice getDevice() {
        return alcDevice;
	}
// -- End LWJGL2 part
    
    @Nullable
    private static FunctionProvider functionProvider;

    @Nullable
    private static ALCapabilities processCaps;

    private static final ThreadLocal<ALCapabilities> capabilitiesTLS = new ThreadLocal<>();

    private static ICD icd = new ICDStatic();

    private AL() {}

    static void init() {
        functionProvider = new FunctionProvider() {
            // We'll use alGetProcAddress for both core and extension entry points.
            // To do that, we need to first grab the alGetProcAddress function from
            // the OpenAL native library.
            private final long alGetProcAddress = ALC.getFunctionProvider().getFunctionAddress("alGetProcAddress");

            @Override
            public long getFunctionAddress(ByteBuffer functionName) {
                long address = invokePP(memAddress(functionName), alGetProcAddress);
                if (address == NULL && Checks.DEBUG_FUNCTIONS) {
                    apiLog("Failed to locate address for AL function " + memASCII(functionName));
                }
                return address;
            }
        };
    }

    public static void destroy() {
        if (functionProvider == null) {
            return;
        }
        
        // LWJGL2 code
        if (created_lwjgl2) {
            ALC10.alcMakeContextCurrent(MemoryUtil.NULL);
            ALC10.alcDestroyContext(alContext);
            ALC10.alcCloseDevice(alcDevice.device);
            alContext = -1;
            alcDevice = null;
            created_lwjgl2 = false;
        }

        setCurrentProcess(null);

        functionProvider = null;
    }

    /**
     * Sets the specified {@link ALCapabilities} for the current process-wide OpenAL context.
     *
     * <p>If the current thread had a context current (see {@link #setCurrentThread}), those {@code ALCapabilities} are cleared. Any OpenAL functions called in
     * the current thread, or any threads that have no context current, will use the specified {@code ALCapabilities}.</p>
     *
     * @param caps the {@link ALCapabilities} to make current, or null
     */
    public static void setCurrentProcess(@Nullable ALCapabilities caps) {
        processCaps = caps;
        capabilitiesTLS.set(null); // See EXT_thread_local_context, second Q.
        icd.set(caps);
    }

    /**
     * Sets the specified {@link ALCapabilities} for the current OpenAL context in the current thread.
     *
     * <p>Any OpenAL functions called in the current thread will use the specified {@code ALCapabilities}.</p>
     *
     * @param caps the {@link ALCapabilities} to make current, or null
     */
    public static void setCurrentThread(@Nullable ALCapabilities caps) {
        capabilitiesTLS.set(caps);
        icd.set(caps);
    }

    /**
     * Returns the {@link ALCapabilities} for the OpenAL context that is current in the current thread or process.
     *
     * @throws IllegalStateException if no OpenAL context is current in the current thread or process
     */
    public static ALCapabilities getCapabilities() {
        ALCapabilities caps = capabilitiesTLS.get();
        if (caps == null) {
            caps = processCaps;
        }

        return checkCapabilities(caps);
    }

    private static ALCapabilities checkCapabilities(@Nullable ALCapabilities caps) {
        if (caps == null) {
            throw new IllegalStateException(
                "No ALCapabilities instance set for the current thread or process. Possible solutions:\n" +
                "\ta) Call AL.createCapabilities() after making a context current.\n" +
                "\tb) Call AL.setCurrentProcess() or AL.setCurrentThread() if an ALCapabilities instance already exists."
            );
        }
        return caps;
    }

    /**
     * Creates a new {@link ALCapabilities} instance for the OpenAL context that is current in the current thread or process.
     *
     * @param alcCaps the {@link ALCCapabilities} of the device associated with the current context
     *
     * @return the ALCapabilities instance
     */
    public static ALCapabilities createCapabilities(ALCCapabilities alcCaps) {
        FunctionProvider functionProvider = ALC.check(AL.functionProvider);

        ALCapabilities caps = null;

        try {
            long GetString          = functionProvider.getFunctionAddress("alGetString");
            long GetError           = functionProvider.getFunctionAddress("alGetError");
            long IsExtensionPresent = functionProvider.getFunctionAddress("alIsExtensionPresent");
            if (GetString == NULL || GetError == NULL || IsExtensionPresent == NULL) {
                throw new IllegalStateException("Core OpenAL functions could not be found. Make sure that the OpenAL library has been loaded correctly.");
            }

            String versionString = memASCIISafe(invokeP(AL_VERSION, GetString));
            if (versionString == null || invokeI(GetError) != AL_NO_ERROR) {
                throw new IllegalStateException("There is no OpenAL context current in the current thread or process.");
            }

            APIVersion apiVersion = apiParseVersion(versionString);

            int majorVersion = apiVersion.major;
            int minorVersion = apiVersion.minor;

            int[][] AL_VERSIONS = {
                {0, 1}  // OpenAL 1
            };

            Set<String> supportedExtensions = new HashSet<>(32);

            for (int major = 1; major <= AL_VERSIONS.length; major++) {
                int[] minors = AL_VERSIONS[major - 1];
                for (int minor : minors) {
                    if (major < majorVersion || (major == majorVersion && minor <= minorVersion)) {
                        supportedExtensions.add("OpenAL" + major + minor);
                    }
                }
            }

            // Parse EXTENSIONS string
            String extensionsString = memASCIISafe(invokeP(AL_EXTENSIONS, GetString));
            if (extensionsString != null) {
                MemoryStack stack = stackGet();

                StringTokenizer tokenizer = new StringTokenizer(extensionsString);
                while (tokenizer.hasMoreTokens()) {
                    String extName = tokenizer.nextToken();
                    try (MemoryStack frame = stack.push()) {
                        if (invokePZ(memAddress(frame.ASCII(extName, true)), IsExtensionPresent)) {
                            supportedExtensions.add(extName);
                        }
                    }
                }
            }

            if (alcCaps.ALC_EXT_EFX) {
                supportedExtensions.add("ALC_EXT_EFX");
            }

            return caps = new ALCapabilities(functionProvider, supportedExtensions);
        } finally {
            if (alcCaps.ALC_EXT_thread_local_context && alcGetThreadContext() != NULL) {
                setCurrentThread(caps);
            } else {
                setCurrentProcess(caps);
            }
        }
    }

    static ALCapabilities getICD() {
        return ALC.check(icd.get());
    }

    /** Function pointer provider. */
    private interface ICD {
        default void set(@Nullable ALCapabilities caps) {}
        @Nullable ALCapabilities get();
    }

    /**
     * Write-once {@link ICD}.
     *
     * <p>This is the default implementation that skips the thread/process lookup. When a new ALCapabilities is set, we compare it to the write-once
     * capabilities. If different function pointers are found, we fall back to the expensive lookup. This will never happen with the OpenAL-Soft
     * implementation.</p>
     */
    private static class ICDStatic implements ICD {

        @Nullable
        private static ALCapabilities tempCaps;

        @Override
        public void set(@Nullable ALCapabilities caps) {
            if (tempCaps == null) {
                tempCaps = caps;
            } else if (caps != null && caps != tempCaps && ThreadLocalUtil.areCapabilitiesDifferent(tempCaps.addresses, caps.addresses)) {
                apiLog("[WARNING] Incompatible context detected. Falling back to thread/process lookup for AL contexts.");
                icd = AL::getCapabilities; // fall back to thread/process lookup
            }
        }

        @Override
        @Nullable
        public ALCapabilities get() {
            return WriteOnce.caps;
        }

        private static final class WriteOnce {
            // This will be initialized the first time get() above is called
            @Nullable
            static final ALCapabilities caps = ICDStatic.tempCaps;

            static {
                if (caps == null) {
                    throw new IllegalStateException("No ALCapabilities instance has been set");
                }
            }
        }

    }

}
