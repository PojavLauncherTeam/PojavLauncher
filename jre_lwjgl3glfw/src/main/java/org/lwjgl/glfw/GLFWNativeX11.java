package org.lwjgl.glfw;

import org.lwjgl.system.NativeType;

import java.nio.ByteBuffer;

import javax.annotation.Nullable;

public class GLFWNativeX11 {
    @NativeType("Display *")
    public static long glfwGetX11Display() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @NativeType("RRCrtc")
    public static long glfwGetX11Adapter(@NativeType("GLFWmonitor *") long monitor) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @NativeType("RROutput")
    public static long glfwGetX11Monitor(@NativeType("GLFWmonitor *") long monitor) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @NativeType("Window")
    public static long glfwGetX11Window(@NativeType("GLFWwindow *") long window) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static void glfwSetX11SelectionString(@NativeType("char const *") ByteBuffer string) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static void glfwSetX11SelectionString(@NativeType("char const *") CharSequence string) {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Nullable
    @NativeType("char const *")
    public static String glfwGetX11SelectionString() {
        throw new UnsupportedOperationException("Not implemented");
    }
}