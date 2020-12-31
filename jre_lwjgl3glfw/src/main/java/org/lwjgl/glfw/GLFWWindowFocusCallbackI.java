/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.dyncall.DynCallback.*;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetWindowFocusCallback SetWindowFocusCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     int focused
 * )</code></pre>
 *
 * @since version 3.0
 */
@FunctionalInterface
@NativeType("GLFWwindowfocusfun")
public interface GLFWWindowFocusCallbackI extends CallbackI.V {

    String SIGNATURE = "(pi)v";

    @Override
    default String getSignature() { return SIGNATURE; }

    @Override
    default void callback(long args) {
        invoke(
            dcbArgPointer(args),
            dcbArgInt(args) != 0
        );
    }

    /**
     * Will be called when the specified window gains or loses focus.
     *
     * @param window  the window that was focused or defocused
     * @param focused {@link GLFW#GLFW_TRUE TRUE} if the window was focused, or {@link GLFW#GLFW_FALSE FALSE} if it was defocused
     */
    void invoke(@NativeType("GLFWwindow *") long window, @NativeType("int") boolean focused);

}