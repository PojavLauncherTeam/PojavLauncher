/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.JNI.*;
import static org.lwjgl.system.MemoryUtil.*;
import java.lang.reflect.*;

/** Utility class for GLFW callbacks. */
public final class Callbacks {

    private Callbacks() {}

    /**
     * Resets all callbacks for the specified GLFW window to {@code NULL} and {@link Callback#free frees} all previously set callbacks.
     *
     * <p>This method resets only callbacks registered with a GLFW window. Non-window callbacks (registered with
     * {@link GLFW#glfwSetErrorCallback SetErrorCallback}, {@link GLFW#glfwSetMonitorCallback SetMonitorCallback}, etc.) must be reset and freed
     * separately.</p>
     *
     * <p>This method is not official GLFW API. It exists in LWJGL to simplify window callback cleanup.</p>
     *
     * @param window the GLFW window
     */
    public static void glfwFreeCallbacks(@NativeType("GLFWwindow *") long window) {
        if (Checks.CHECKS) {
            check(window);
        }

        try {
            for (Method callback : GLFW.class.getMethods()) {
                if (callback.getName().startsWith("glfwSet") && callback.getName().endsWith("Callback")) {
                    if (callback.getParameterCount() == 1) {
                        callback.invoke(null, (Object)null);
                    } else {
                        callback.invoke(null, GLFW.glfwGetCurrentContext(), null);
                    }
                }
            }
        } catch (IllegalAccessException|NullPointerException e) {
            throw new RuntimeException("org.lwjgl.GLFW.glfwSetXXXCallback() must be set to public and static", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
