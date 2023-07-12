package org.lwjgl.glfw;

import org.lwjgl.system.NativeType;

public class GLFWNativeCocoa {
    @NativeType("CGDirectDisplayID")
    public static int glfwGetCocoaMonitor(@NativeType("GLFWmonitor *") long monitor) {
        throw new UnsupportedOperationException("Not implemented");
    }
    @NativeType("id")
    public static long glfwGetCocoaWindow(@NativeType("GLFWwindow *") long window) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
