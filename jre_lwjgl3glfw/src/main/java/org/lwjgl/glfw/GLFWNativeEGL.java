package org.lwjgl.glfw;

import org.lwjgl.system.NativeType;

public class GLFWNativeEGL {
    @NativeType("EGLDisplay")
    public static long glfwGetEGLDisplay() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @NativeType("EGLContext")
    public static long glfwGetEGLContext(@NativeType("GLFWwindow *") long window) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @NativeType("EGLSurface")
    public static long glfwGetEGLSurface(@NativeType("GLFWwindow *") long window) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @NativeType("EGLConfig")
    public static long glfwGetEGLConfig(@NativeType("GLFWwindow *") long window) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
