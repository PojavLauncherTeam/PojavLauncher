/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.dyncall.DynCallback.*;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetWindowPosCallback SetWindowPosCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     int xpos,
 *     int ypos
 * )</code></pre>
 *
 * @since version 3.0
 */
@FunctionalInterface
@NativeType("GLFWwindowposfun")
public interface GLFWWindowPosCallbackI extends CallbackI.V {

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
     * Will be called when the specified window moves.
     *
     * @param window the window that was moved
     * @param xpos   the new x-coordinate, in screen coordinates, of the upper-left corner of the content area of the window
     * @param ypos   the new y-coordinate, in screen coordinates, of the upper-left corner of the content area of the window
     */
    void invoke(@NativeType("GLFWwindow *") long window, int xpos, int ypos);

}