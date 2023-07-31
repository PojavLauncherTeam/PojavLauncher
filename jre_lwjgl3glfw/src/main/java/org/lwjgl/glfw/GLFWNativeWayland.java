package org.lwjgl.glfw;

import org.lwjgl.system.NativeType;

public class GLFWNativeWayland {
    @NativeType("struct wl_display *")
    public static long glfwGetWaylandDisplay() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @NativeType("struct wl_output *")
    public static long glfwGetWaylandMonitor(@NativeType("GLFWmonitor *") long monitor) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @NativeType("struct wl_surface *")
    public static long glfwGetWaylandWindow(@NativeType("GLFWwindow *") long window) {
        throw new UnsupportedOperationException("Not implemented");
    }
}