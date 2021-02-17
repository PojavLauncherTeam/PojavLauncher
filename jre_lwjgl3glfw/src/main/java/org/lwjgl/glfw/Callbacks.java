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
			for (Field callback : GLFW.class.getFields()) {
				if (callback.getName().startsWith("mGLFW") && callback.getName().endsWith("Callback")) {
					callback.set(null, null);
				}
			}
		} catch (IllegalAccessException|NullPointerException e) {
			throw new RuntimeException("org.lwjgl.GLFW.mGLFWxxxCallbacks must be set to public and static", e);
		}

/*
        for (long callback : new long[] {
            GLFW.Functions.SetWindowPosCallback,
            GLFW.Functions.SetWindowSizeCallback,
            GLFW.Functions.SetWindowCloseCallback,
            GLFW.Functions.SetWindowRefreshCallback,
            GLFW.Functions.SetWindowFocusCallback,
            GLFW.Functions.SetWindowIconifyCallback,
            GLFW.Functions.SetWindowMaximizeCallback,
            GLFW.Functions.SetFramebufferSizeCallback,
            GLFW.Functions.SetWindowContentScaleCallback,
            GLFW.Functions.SetKeyCallback,
            GLFW.Functions.SetCharCallback,
            GLFW.Functions.SetCharModsCallback,
            GLFW.Functions.SetMouseButtonCallback,
            GLFW.Functions.SetCursorPosCallback,
            GLFW.Functions.SetCursorEnterCallback,
            GLFW.Functions.SetScrollCallback,
            GLFW.Functions.SetDropCallback
        }) {
            long prevCB = invokePPP(window, NULL, callback);
            if (prevCB != NULL) {
                Callback.free(prevCB);
            }
        }
*/
    }
}
