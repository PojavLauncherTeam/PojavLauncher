/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.dyncall.DynCallback.*;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetFramebufferSizeCallback SetFramebufferSizeCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     int width,
 *     int height
 * )</code></pre>
 *
 * @since version 3.0
 */
@FunctionalInterface
@NativeType("GLFWframebuffersizefun")
public interface GLFWFramebufferSizeCallbackI extends CallbackI.V {

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
     * Will be called when the framebuffer of the specified window is resized.
     *
     * @param window the window whose framebuffer was resized
     * @param width  the new width, in pixels, of the framebuffer
     * @param height the new height, in pixels, of the framebuffer
     */
    void invoke(@NativeType("GLFWwindow *") long window, int width, int height);

}