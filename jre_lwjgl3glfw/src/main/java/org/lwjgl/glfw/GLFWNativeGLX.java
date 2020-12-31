/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.APIUtil.*;
import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.JNI.*;

import javax.annotation.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.system.MemoryUtil.*;

/** Native bindings to the GLFW library's GLX native access functions. */
public class GLFWNativeGLX {

    protected GLFWNativeGLX() {
        throw new UnsupportedOperationException();
    }

    /** Contains the function pointers loaded from {@code GLFW.getLibrary()}. */
    public static final class Functions {

        private Functions() {}

        /** Function address. */
        public static final long
            GetGLXContext = apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetGLXContext"),
            GetGLXWindow  = apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetGLXWindow");

    }

    // --- [ glfwGetGLXContext ] ---

    /**
     * Returns the {@code GLXContext} of the specified window.
     * 
     * <p>This function may be called from any thread. Access is not synchronized.</p>
     *
     * @param window a GLFW window
     *
     * @return the {@code GLXContext} of the specified window, or {@code NULL} if an error occurred.
     *
     * @since version 3.0
     */
    @NativeType("GLXContext")
    public static long glfwGetGLXContext(@NativeType("GLFWwindow *") long window) {
        long __functionAddress = Functions.GetGLXContext;
        if (CHECKS) {
            check(window);
        }
        return invokePP(window, __functionAddress);
    }

    // --- [ glfwGetGLXWindow ] ---

    /**
     * Returns the {@code GLXWindow} of the specified window.
     * 
     * <p>This function may be called from any thread. Access is not synchronized.</p>
     *
     * @param window a GLFW window
     *
     * @return the {@code GLXWindow} of the specified window, or {@code None} if an error occurred.
     *
     * @since version 3.2
     */
    @NativeType("GLXWindow")
    public static long glfwGetGLXWindow(@NativeType("GLFWwindow *") long window) {
        long __functionAddress = Functions.GetGLXWindow;
        if (CHECKS) {
            check(window);
        }
        return invokePP(window, __functionAddress);
    }

    /** Calls {@link #setPath(String)} with the path of the OpenGL shared library loaded by LWJGL. */
    public static void setPathLWJGL() {
        FunctionProvider fp = GL.getFunctionProvider();
        if (!(fp instanceof SharedLibrary)) {
            apiLog("GLFW OpenGL path override not set: OpenGL function provider is not a shared library.");
            return;

        }

        String path = ((SharedLibrary)fp).getPath();
        if (path == null) {
            apiLog("GLFW OpenGL path override not set: Could not resolve the OpenGL shared library path.");
            return;

        }

        setPath(path);
    }

    /**
     * Overrides the OpenGL shared library that GLFW loads internally.
     *
     * <p>This is useful when there's a mismatch between the shared libraries loaded by LWJGL and GLFW.</p>
     *
     * <p>This method must be called before GLFW initializes OpenGL. The override is available only in the default GLFW build bundled with LWJGL. Using the
     * override with a custom GLFW build will produce a warning in {@code DEBUG} mode (but not an error).</p>
     *
     * @param path the OpenGL shared library path, or {@code null} to remove the override.
     */
    public static void setPath(@Nullable String path) {
        long override = GLFW.getLibrary().getFunctionAddress("_glfw_opengl_library");
        if (override == NULL) {
            apiLog("GLFW OpenGL path override not set: Could not resolve override symbol.");
            return;
        }

        long a = memGetAddress(override);
        if (a != NULL) {
            nmemFree(a);
        }
        memPutAddress(override, path == null ? NULL : memAddress(memUTF8(path)));
    }

}