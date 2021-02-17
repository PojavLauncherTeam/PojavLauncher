/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.dyncall.DynCallback.*;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetWindowSizeCallback SetWindowSizeCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     int width,
 *     int height
 * )</code></pre>
 */
@FunctionalInterface
@NativeType("GLFWwindowsizefun")
public interface GLFWWindowSizeCallbackI extends CallbackI.V {

    String SIGNATURE = "(pii)v";

    @Override
    default String getSignature() { return SIGNATURE; }

    @Override
    default void callback(long args) {
        invoke(
            dcbArgPointer(args),
            dcbArgInt(args),
            dcbArgInt(args)
        );
    }

    /**
     * Will be called when the specified window is resized.
     *
     * @param window the window that was resized
     * @param width  the new width, in screen coordinates, of the window
     * @param height the new height, in screen coordinates, of the window
     */
    void invoke(@NativeType("GLFWwindow *") long window, int width, int height);

}