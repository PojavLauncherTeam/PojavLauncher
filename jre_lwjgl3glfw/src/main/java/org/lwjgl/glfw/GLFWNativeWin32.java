package org.lwjgl.glfw;

import org.lwjgl.system.NativeType;

import javax.annotation.Nullable;

public class GLFWNativeWin32 {
    @Nullable
    @NativeType("char const *")
    public static String glfwGetWin32Adapter(@NativeType("GLFWmonitor *") long monitor) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Nullable
    @NativeType("char const *")
    public static String glfwGetWin32Monitor(@NativeType("GLFWmonitor *") long monitor) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @NativeType("HWND")
    public static long glfwGetWin32Window(@NativeType("GLFWwindow *") long window) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @NativeType("GLFWwindow *")
    public static long glfwAttachWin32Window(@NativeType("HWND") long handle, @NativeType("GLFWwindow *") long share) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
