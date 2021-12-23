/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.opengl;

import org.lwjgl.system.*;
import org.lwjgl.system.macosx.*;
import org.lwjgl.system.windows.*;
import org.lwjgl.glfw.*;

import javax.annotation.*;
import java.nio.*;
import java.util.*;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL32C.*;
import static org.lwjgl.opengl.GLX.*;
import static org.lwjgl.opengl.GLX11.*;
import static org.lwjgl.opengl.WGL.*;
import static org.lwjgl.system.APIUtil.*;
import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.JNI.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.linux.X11.*;
import static org.lwjgl.system.windows.GDI32.*;
import static org.lwjgl.system.windows.User32.*;
import static org.lwjgl.system.windows.WindowsUtil.*;

/**
 * This class must be used before any OpenGL function is called. It has the following responsibilities:
 * <ul>
 * <li>Loads the OpenGL native library into the JVM process.</li>
 * <li>Creates instances of {@link GLCapabilities} classes. A {@code GLCapabilities} instance contains flags for functionality that is available in an OpenGL
 * context. Internally, it also contains function pointers that are only valid in that specific OpenGL context.</li>
 * <li>Maintains thread-local state for {@code GLCapabilities} instances, corresponding to OpenGL contexts that are current in those threads.</li>
 * </ul>
 *
 * <h3>Library lifecycle</h3>
 * <p>The OpenGL library is loaded automatically when this class is initialized. Set the {@link Configuration#OPENGL_EXPLICIT_INIT} option to override this
 * behavior. Manual loading/unloading can be achieved with the {@link #create} and {@link #destroy} functions. The name of the library loaded can be overridden
 * with the {@link Configuration#OPENGL_LIBRARY_NAME} option. The maximum OpenGL version loaded can be set with the {@link Configuration#OPENGL_MAXVERSION}
 * option. This can be useful to ensure that no functionality above a specific version is used during development.</p>
 *
 * <h3>GLCapabilities creation</h3>
 * <p>Instances of {@code GLCapabilities} can be created with the {@link #createCapabilities} method. An OpenGL context must be current in the current thread
 * before it is called. Calling this method is expensive, so the {@code GLCapabilities} instance should be associated with the OpenGL context and reused as
 * necessary.</p>
 *
 * <h3>Thread-local state</h3>
 * <p>Before a function for a given OpenGL context can be called, the corresponding {@code GLCapabilities} instance must be passed to the
 * {@link #setCapabilities} method. The user is also responsible for clearing the current {@code GLCapabilities} instance when the context is destroyed or made
 * current in another thread.</p>
 *
 * <p>Note that the {@link #createCapabilities} method implicitly calls {@link #setCapabilities} with the newly created instance.</p>
 */
public final class GL {

    @Nullable
    private static final APIVersion MAX_VERSION;

    @Nullable
    private static FunctionProvider functionProvider;

    private static final ThreadLocal<GLCapabilities> capabilitiesTLS = new ThreadLocal<>();

    private static ICD icd = new ICDStatic();

    @Nullable
    private static WGLCapabilities capabilitiesWGL;

    @Nullable
    private static GLXCapabilities capabilitiesGLXClient;
    @Nullable
    private static GLXCapabilities capabilitiesGLX;

	private static final boolean isUsingRegal;

    static {
		isUsingRegal = System.getProperty("org.lwjgl.opengl.libname").contains("libRegal.so");
        // if (isUsingRegal)
            
		
        Library.loadSystem(System::load, System::loadLibrary, GL.class, "org.lwjgl.opengl", Platform.mapLibraryNameBundled("lwjgl_opengl"));

        MAX_VERSION = apiParseVersion(Configuration.OPENGL_MAXVERSION);

        if (!Configuration.OPENGL_EXPLICIT_INIT.get(false)) {
            create();
        }
    }
    
    private static native void nativeRegalMakeCurrent();

    private GL() {}

    /** Ensures that the lwjgl_opengl shared library has been loaded. */
    static void initialize() {
        // intentionally empty to trigger static initializer
    }

    /** Loads the OpenGL native library, using the default library name. */
    public static void create() {
        SharedLibrary GL;
        switch (Platform.get()) {
            case LINUX:
                GL = Library.loadNative(GL.class, "org.lwjgl.opengl", Configuration.OPENGL_LIBRARY_NAME, "libGL.so.1", "libGL.so");
                break;
            case MACOSX:
                String override = Configuration.OPENGL_LIBRARY_NAME.get();
                GL = override != null
                    ? Library.loadNative(GL.class, "org.lwjgl.opengl", override)
                    : MacOSXLibrary.getWithIdentifier("com.apple.opengl");
                break;
            case WINDOWS:
                GL = Library.loadNative(GL.class, "org.lwjgl.opengl", Configuration.OPENGL_LIBRARY_NAME, "opengl32");
                break;
            default:
                throw new IllegalStateException();
        }
        create(GL);
    }

    /**
     * Loads the OpenGL native library, using the specified library name.
     *
     * @param libName the native library name
     */
    public static void create(String libName) {
        create(Library.loadNative(GL.class, "org.lwjgl.opengl", libName));
    }

    private abstract static class SharedLibraryGL extends SharedLibrary.Delegate {

        SharedLibraryGL(SharedLibrary library) {
            super(library);
        }
        abstract long getExtensionAddress(long name);

        @Override
        public long getFunctionAddress(ByteBuffer functionName) {
            long address = getExtensionAddress(memAddress(functionName));
            if (address == NULL) {
                address = library.getFunctionAddress(functionName);
                if (address == NULL && DEBUG_FUNCTIONS) {
                    apiLog("Failed to locate address for GL function " + memASCII(functionName));
                }
            }

            return address;
        }

    }

    private static void create(SharedLibrary OPENGL) {
        FunctionProvider functionProvider;
        try {
            switch (Platform.get()) {
                case WINDOWS:
                    functionProvider = new SharedLibraryGL(OPENGL) {
                        private final long wglGetProcAddress = library.getFunctionAddress("wglGetProcAddress");

                        @Override
                        long getExtensionAddress(long name) {
                            return callPP(name, wglGetProcAddress);
                        }
                    };
                    break;
                case LINUX:
                    functionProvider = new SharedLibraryGL(OPENGL) {
                        private final long glXGetProcAddress;

                        {
                            long GetProcAddress = library.getFunctionAddress(isUsingRegal ? "glGetProcAddressREGAL" : "glXGetProcAddress");
                            if (GetProcAddress == NULL) {
                                GetProcAddress = library.getFunctionAddress("glXGetProcAddressARB");
                            }
							
							glXGetProcAddress = GetProcAddress;
						}

                        @Override
                        long getExtensionAddress(long name) {
                            return glXGetProcAddress == NULL ? NULL : callPP(name, glXGetProcAddress);
                        }
                    };
                    break;
                case MACOSX:
                    functionProvider = new SharedLibraryGL(OPENGL) {
                        @Override
                        long getExtensionAddress(long name) {
                            return NULL;
                        }
                    };
                    break;
                default:
                    throw new IllegalStateException();
            }
            create(functionProvider);
        } catch (RuntimeException e) {
            OPENGL.free();
            throw e;
        }
    }

    /**
     * Initializes OpenGL with the specified {@link FunctionProvider}. This method can be used to implement custom OpenGL library loading.
     *
     * @param functionProvider the provider of OpenGL function addresses
     */
    public static void create(FunctionProvider functionProvider) {
        if (GL.functionProvider != null) {
            throw new IllegalStateException("OpenGL library has already been loaded.");
        }

        GL.functionProvider = functionProvider;
        ThreadLocalUtil.setFunctionMissingAddresses(GLCapabilities.class, 3);
    }

    /** Unloads the OpenGL native library. */
    public static void destroy() {
        if (functionProvider == null) {
            return;
        }

        ThreadLocalUtil.setFunctionMissingAddresses(null, 3);

        capabilitiesWGL = null;
        capabilitiesGLX = null;

        if (functionProvider instanceof NativeResource) {
            ((NativeResource)functionProvider).free();
        }
        functionProvider = null;
    }

    /** Returns the {@link FunctionProvider} for the OpenGL native library. */
    @Nullable
    public static FunctionProvider getFunctionProvider() {
        return functionProvider;
    }

    /**
     * Sets the {@link GLCapabilities} of the OpenGL context that is current in the current thread.
     *
     * <p>This {@code GLCapabilities} instance will be used by any OpenGL call in the current thread, until {@code setCapabilities} is called again with a
     * different value.</p>
     */
    public static void setCapabilities(@Nullable GLCapabilities caps) {
        capabilitiesTLS.set(caps);
        ThreadLocalUtil.setEnv(caps == null ? NULL : memAddress(caps.addresses), 3);
        icd.set(caps);
    }

    /**
     * Returns the {@link GLCapabilities} of the OpenGL context that is current in the current thread.
     *
     * @throws IllegalStateException if {@link #setCapabilities} has never been called in the current thread or was last called with a {@code null} value
     */
    public static GLCapabilities getCapabilities() {
        return checkCapabilities(capabilitiesTLS.get());
    }

    private static GLCapabilities checkCapabilities(@Nullable GLCapabilities caps) {
        if (CHECKS && caps == null) {
            throw new IllegalStateException(
                "No GLCapabilities instance set for the current thread. Possible solutions:\n" +
                "\ta) Call GL.createCapabilities() after making a context current in the current thread.\n" +
                "\tb) Call GL.setCapabilities() if a GLCapabilities instance already exists for the current context."
            );
        }
        //noinspection ConstantConditions
        return caps;
    }

    /**
     * Returns the WGL capabilities.
     *
     * <p>This method may only be used on Windows.</p>
     */
    public static WGLCapabilities getCapabilitiesWGL() {
        if (capabilitiesWGL == null) {
            capabilitiesWGL = createCapabilitiesWGLDummy();
        }

        return capabilitiesWGL;
    }

    /** Returns the GLX client capabilities. */
    static GLXCapabilities getCapabilitiesGLXClient() {
        if (capabilitiesGLXClient == null) {
            capabilitiesGLXClient = initCapabilitiesGLX(true);
        }

        return capabilitiesGLXClient;
    }

    /**
     * Returns the GLX capabilities.
     *
     * <p>This method may only be used on Linux.</p>
     */
    public static GLXCapabilities getCapabilitiesGLX() {
        if (capabilitiesGLX == null) {
            capabilitiesGLX = initCapabilitiesGLX(false);
        }

        return capabilitiesGLX;
    }

    private static GLXCapabilities initCapabilitiesGLX(boolean client) {
        long display = nXOpenDisplay(NULL);
        try {
            return createCapabilitiesGLX(display, client ? -1 : XDefaultScreen(display));
        } finally {
            XCloseDisplay(display);
        }
    }

    /**
     * Creates a new {@link GLCapabilities} instance for the OpenGL context that is current in the current thread.
     *
     * <p>Depending on the current context, the instance returned may or may not contain the deprecated functionality removed since OpenGL version 3.1.</p>
     *
     * <p>This method calls {@link #setCapabilities(GLCapabilities)} with the new instance before returning.</p>
     *
     * @return the GLCapabilities instance
     */
    public static GLCapabilities createCapabilities() {
        return createCapabilities(false);
    }
    private static native long getGraphicsBufferAddr();
    private static native int[] getNativeWidthHeight();
    /**
     * Creates a new {@link GLCapabilities} instance for the OpenGL context that is current in the current thread.
     *
     * <p>Depending on the current context, the instance returned may or may not contain the deprecated functionality removed since OpenGL version 3.1. The
     * {@code forwardCompatible} flag will force LWJGL to not load the deprecated functions, even if the current context exposes them.</p>
     *
     * <p>This method calls {@link #setCapabilities(GLCapabilities)} with the new instance before returning.</p>
     *
     * @param forwardCompatible if true, LWJGL will create forward compatible capabilities
     *
     * @return the GLCapabilities instance
     */
    @SuppressWarnings("AssignmentToMethodParameter")
    public static GLCapabilities createCapabilities(boolean forwardCompatible) {
        // System.setProperty("glfwstub.internal.glthreadid", Long.toString(Thread.currentThread().getId()));
        
        FunctionProvider functionProvider = GL.functionProvider;
        if (functionProvider == null) {
            throw new IllegalStateException("OpenGL library has not been loaded.");
        }

        GLCapabilities caps = null;

        try {
            if (System.getenv("POJAV_RENDERER").equals("opengles3_virgl") || System.getenv("POJAV_RENDERER").equals("vulkan_zink")) {
                int[] dims = getNativeWidthHeight();
                callJPI(GLFW.glfwGetCurrentContext(),getGraphicsBufferAddr(),GL_UNSIGNED_BYTE,dims[0],dims[1],functionProvider.getFunctionAddress("OSMesaMakeCurrent"));
            } else if (System.getenv("POJAV_RENDERER").startsWith("opengles")) {
                // This fixed framebuffer issue on 1.13+ 64-bit by another making current
                GLFW.glfwMakeContextCurrent(GLFW.mainContext);
                if (isUsingRegal) {
                    nativeRegalMakeCurrent();
                }
            }

            // We don't have a current ContextCapabilities when this method is called
            // so we have to use the native bindings directly.
            long GetError    = functionProvider.getFunctionAddress("glGetError");
            long GetString   = functionProvider.getFunctionAddress("glGetString");
            long GetIntegerv = functionProvider.getFunctionAddress("glGetIntegerv");

            if (GetError == NULL || GetString == NULL || GetIntegerv == NULL) {
                throw new IllegalStateException("Core OpenGL functions could not be found. Make sure that the OpenGL library has been loaded correctly.");
            }

            int errorCode = callI(GetError);
            if (errorCode != GL_NO_ERROR) {
                apiLog(String.format("An OpenGL context was in an error state before the creation of its capabilities instance. Error: 0x%X", errorCode));
            }

            int majorVersion;
            int minorVersion;

            try (MemoryStack stack = stackPush()) {
                IntBuffer version = stack.ints(0);

                // Try the 3.0+ version query first
                callPV(GL_MAJOR_VERSION, memAddress(version), GetIntegerv);
                if (callI(GetError) == GL_NO_ERROR && 3 <= (majorVersion = version.get(0))) {
                    // We're on an 3.0+ context.
                    callPV(GL_MINOR_VERSION, memAddress(version), GetIntegerv);
                    minorVersion = version.get(0);
                } else {
                    // Fallback to the string query.
                    String versionString = memUTF8Safe(callP(GL_VERSION, GetString));
                    if (versionString == null || callI(GetError) != GL_NO_ERROR) {
                        throw new IllegalStateException("There is no OpenGL context current in the current thread.");
                    }

                    APIVersion apiVersion = apiParseVersion(versionString);

                    majorVersion = apiVersion.major;
                    minorVersion = apiVersion.minor;
                }
            }

            if (majorVersion < 1 || (majorVersion == 1 && minorVersion < 1)) {
                throw new IllegalStateException("OpenGL 1.1 is required.");
            }

            int[] GL_VERSIONS = {
                5, // OpenGL 1.1 to 1.5
                1, // OpenGL 2.0 to 2.1
                3, // OpenGL 3.0 to 3.3
                6, // OpenGL 4.0 to 4.6
            };

            Set<String> supportedExtensions = new HashSet<>(512);

            int maxMajor = min(majorVersion, GL_VERSIONS.length);
            if (MAX_VERSION != null) {
                maxMajor = min(MAX_VERSION.major, maxMajor);
            }
            for (int M = 1; M <= maxMajor; M++) {
                int maxMinor = GL_VERSIONS[M - 1];
                if (M == majorVersion) {
                    maxMinor = min(minorVersion, maxMinor);
                }
                if (MAX_VERSION != null && M == MAX_VERSION.major) {
                    maxMinor = min(MAX_VERSION.minor, maxMinor);
                }

                for (int m = M == 1 ? 1 : 0; m <= maxMinor; m++) {
                    supportedExtensions.add(String.format("OpenGL%d%d", M, m));
                }
            }

            if (majorVersion < 3) {
                // Parse EXTENSIONS string
                String extensionsString = memASCIISafe(callP(GL_EXTENSIONS, GetString));
                if (extensionsString != null) {
                    StringTokenizer tokenizer = new StringTokenizer(extensionsString);
                    while (tokenizer.hasMoreTokens()) {
                        supportedExtensions.add(tokenizer.nextToken());
                    }
                }
            } else {
                // Use indexed EXTENSIONS
                try (MemoryStack stack = stackPush()) {
                    IntBuffer pi = stack.ints(0);

                    callPV(GL_NUM_EXTENSIONS, memAddress(pi), GetIntegerv);
                    int extensionCount = pi.get(0);

                    long GetStringi = apiGetFunctionAddress(functionProvider, "glGetStringi");
                    for (int i = 0; i < extensionCount; i++) {
                        supportedExtensions.add(memASCII(callP(GL_EXTENSIONS, i, GetStringi)));
                    }

                    // In real drivers, we may encounter the following weird scenarios:
                    // - 3.1 context without GL_ARB_compatibility but with deprecated functionality exposed and working.
                    // - Core or forward-compatible context with GL_ARB_compatibility exposed, but not working when used.
                    // We ignore these and go by the spec.

                    // Force forwardCompatible to true if the context is a forward-compatible context.
                    callPV(GL_CONTEXT_FLAGS, memAddress(pi), GetIntegerv);
                    if ((pi.get(0) & GL_CONTEXT_FLAG_FORWARD_COMPATIBLE_BIT) != 0) {
                        forwardCompatible = true;
                    } else {
                        // Force forwardCompatible to true if the context is a core profile context.
                        if ((3 < majorVersion || 1 <= minorVersion)) { // OpenGL 3.1+
                            if (3 < majorVersion || 2 <= minorVersion) { // OpenGL 3.2+
                                callPV(GL_CONTEXT_PROFILE_MASK, memAddress(pi), GetIntegerv);
                                if ((pi.get(0) & GL_CONTEXT_CORE_PROFILE_BIT) != 0) {
                                    forwardCompatible = true;
                                }
                            } else {
                                forwardCompatible = !supportedExtensions.contains("GL_ARB_compatibility");
                            }
                        }
                    }
                }
            }

            return caps = new GLCapabilities(functionProvider, supportedExtensions, forwardCompatible);
        } finally {
            setCapabilities(caps);
        }
    }

    /** Creates a dummy context and retrieves the WGL capabilities. */
    private static WGLCapabilities createCapabilitiesWGLDummy() {
        long hdc = wglGetCurrentDC(); // just use the current context if one exists
        if (hdc != NULL) {
            return createCapabilitiesWGL(hdc);
        }

        short classAtom = 0;
        long  hwnd      = NULL;
        long  hglrc     = NULL;
        try (MemoryStack stack = stackPush()) {
            WNDCLASSEX wc = WNDCLASSEX.callocStack(stack)
                .cbSize(WNDCLASSEX.SIZEOF)
                .style(CS_HREDRAW | CS_VREDRAW)
                .hInstance(WindowsLibrary.HINSTANCE)
                .lpszClassName(stack.UTF16("WGL"));

            memPutAddress(
                wc.address() + WNDCLASSEX.LPFNWNDPROC,
                User32.Functions.DefWindowProc
            );

            classAtom = RegisterClassEx(wc);
            if (classAtom == 0) {
                throw new IllegalStateException("Failed to register WGL window class");
            }

            hwnd = check(nCreateWindowEx(
                0, classAtom & 0xFFFF, NULL,
                WS_OVERLAPPEDWINDOW | WS_CLIPCHILDREN | WS_CLIPSIBLINGS,
                0, 0, 1, 1,
                NULL, NULL, NULL, NULL
            ));

            hdc = check(GetDC(hwnd));

            PIXELFORMATDESCRIPTOR pfd = PIXELFORMATDESCRIPTOR.callocStack(stack)
                .nSize((short)PIXELFORMATDESCRIPTOR.SIZEOF)
                .nVersion((short)1)
                .dwFlags(PFD_SUPPORT_OPENGL); // we don't care about anything else

            int pixelFormat = ChoosePixelFormat(hdc, pfd);
            if (pixelFormat == 0) {
                windowsThrowException("Failed to choose an OpenGL-compatible pixel format");
            }

            if (DescribePixelFormat(hdc, pixelFormat, pfd) == 0) {
                windowsThrowException("Failed to obtain pixel format information");
            }

            if (!SetPixelFormat(hdc, pixelFormat, pfd)) {
                windowsThrowException("Failed to set the pixel format");
            }

            hglrc = check(wglCreateContext(hdc));
            wglMakeCurrent(hdc, hglrc);

            return createCapabilitiesWGL(hdc);
        } finally {
            if (hglrc != NULL) {
                wglMakeCurrent(NULL, NULL);
                wglDeleteContext(hglrc);
            }

            if (hwnd != NULL) {
                DestroyWindow(hwnd);
            }

            if (classAtom != 0) {
                nUnregisterClass(classAtom & 0xFFFF, WindowsLibrary.HINSTANCE);
            }
        }
    }

    /**
     * Creates a {@link WGLCapabilities} instance for the context that is current in the current thread.
     *
     * <p>This method may only be used on Windows.</p>
     */
    public static WGLCapabilities createCapabilitiesWGL() {
        long hdc = wglGetCurrentDC();
        if (hdc == NULL) {
            throw new IllegalStateException("Failed to retrieve the device context of the current OpenGL context");
        }

        return createCapabilitiesWGL(hdc);
    }

    /**
     * Creates a {@link WGLCapabilities} instance for the specified device context.
     *
     * @param hdc the device context handle ({@code HDC})
     */
    private static WGLCapabilities createCapabilitiesWGL(long hdc) {
        FunctionProvider functionProvider = GL.functionProvider;
        if (functionProvider == null) {
            throw new IllegalStateException("OpenGL library has not been loaded.");
        }

        String extensionsString = null;

        long wglGetExtensionsString = functionProvider.getFunctionAddress("wglGetExtensionsStringARB");
        if (wglGetExtensionsString != NULL) {
            extensionsString = memASCII(callPP(hdc, wglGetExtensionsString));
        } else {
            wglGetExtensionsString = functionProvider.getFunctionAddress("wglGetExtensionsStringEXT");
            if (wglGetExtensionsString != NULL) {
                extensionsString = memASCII(callP(wglGetExtensionsString));
            }
        }

        Set<String> supportedExtensions = new HashSet<>(32);

        if (extensionsString != null) {
            StringTokenizer tokenizer = new StringTokenizer(extensionsString);
            while (tokenizer.hasMoreTokens()) {
                supportedExtensions.add(tokenizer.nextToken());
            }
        }

        return new WGLCapabilities(functionProvider, supportedExtensions);
    }

    /**
     * Creates a {@link GLXCapabilities} instance for the default screen of the specified X connection.
     *
     * <p>This method may only be used on Linux.</p>
     *
     * @param display the X connection handle ({@code DISPLAY})
     */
    public static GLXCapabilities createCapabilitiesGLX(long display) {
        return createCapabilitiesGLX(display, XDefaultScreen(display));
    }

    /**
     * Creates a {@link GLXCapabilities} instance for the specified screen of the specified X connection.
     *
     * <p>This method may only be used on Linux.</p>
     *
     * @param display the X connection handle ({@code DISPLAY})
     * @param screen  the screen index
     */
    public static GLXCapabilities createCapabilitiesGLX(long display, int screen) {
        FunctionProvider functionProvider = GL.functionProvider;
        if (functionProvider == null) {
            throw new IllegalStateException("OpenGL library has not been loaded.");
        }

        int majorVersion = 1;
        int minorVersion = 4;

		Set<String> supportedExtensions = new HashSet<>(32);
		
		int[][] GLX_VERSIONS = {
			{1, 2, 3, 4}
		};

        try (MemoryStack stack = stackPush()) {
            IntBuffer piMajor = stack.ints(0);
            IntBuffer piMinor = stack.ints(0);

            if (!glXQueryVersion(display, piMajor, piMinor)) {
                throw new IllegalStateException("Failed to query GLX version");
            }

            majorVersion = piMajor.get(0);
            minorVersion = piMinor.get(0);
            if (majorVersion != 1) {
                throw new IllegalStateException("Invalid GLX major version: " + majorVersion);
            }
        }
		
		for (int major = 1; major <= GLX_VERSIONS.length; major++) {
			int[] minors = GLX_VERSIONS[major - 1];
			for (int minor : minors) {
				if (major < majorVersion || (major == majorVersion && minor <= minorVersion)) {
					supportedExtensions.add("GLX" + major + minor);
				}
			}
		}

		if (1 <= minorVersion) {
            String extensionsString;

            if (screen == -1) {
                long glXGetClientString = functionProvider.getFunctionAddress("glXGetClientString");
                extensionsString = memASCIISafe(callPP(display, GLX_EXTENSIONS, glXGetClientString));
            } else {
                long glXQueryExtensionsString = functionProvider.getFunctionAddress("glXQueryExtensionsString");
                extensionsString = memASCIISafe(callPP(display, screen, glXQueryExtensionsString));
            }

            if (extensionsString != null) {
                StringTokenizer tokenizer = new StringTokenizer(extensionsString);
                while (tokenizer.hasMoreTokens()) {
                    supportedExtensions.add(tokenizer.nextToken());
                }
            }
        }

        return new GLXCapabilities(functionProvider, supportedExtensions);
    }

    // Only used by array overloads
    static GLCapabilities getICD() {
        return checkCapabilities(icd.get());
    }

    /** Function pointer provider. */
    private interface ICD {
        default void set(@Nullable GLCapabilities caps) {}
        @Nullable GLCapabilities get();
    }

    /**
     * Write-once {@link ICD}.
     *
     * <p>This is the default implementation that skips the thread-local lookup. When a new GLCapabilities is set, we compare it to the write-once capabilities.
     * If different function pointers are found, we fall back to the expensive lookup.</p>
     */
    private static class ICDStatic implements ICD {

        @Nullable
        private static GLCapabilities tempCaps;

        @Override
        public void set(@Nullable GLCapabilities caps) {
            if (tempCaps == null) {
                tempCaps = caps;
            } else if (caps != null && caps != tempCaps && ThreadLocalUtil.areCapabilitiesDifferent(tempCaps.addresses, caps.addresses)) {
                apiLog("[WARNING] Incompatible context detected. Falling back to thread-local lookup for GL contexts.");
                icd = GL::getCapabilities; // fall back to thread/process lookup
            }
        }

        @Override
        public GLCapabilities get() {
            return WriteOnce.caps;
        }

        private static final class WriteOnce {
            // This will be initialized the first time get() above is called
            @Nullable
            static final GLCapabilities caps = ICDStatic.tempCaps;

            static {
                if (caps == null) {
                    throw new IllegalStateException("No GLCapabilities instance has been set");
                }
            }
        }

    }

}
