package org.lwjgl.glfw;

import org.lwjgl.system.NativeType;

public class GLFWNativeWGL {
    @NativeType("HGLRC")
    public static long glfwGetWGLContext(@NativeType("GLFWwindow *") long window) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
