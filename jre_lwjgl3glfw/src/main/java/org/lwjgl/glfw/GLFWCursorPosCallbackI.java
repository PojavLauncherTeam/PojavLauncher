/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.dyncall.DynCallback.*;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetCursorPosCallback SetCursorPosCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     double xpos,
 *     double ypos
 * )</code></pre>
 *
 * @since version 3.0
 */
@FunctionalInterface
@NativeType("GLFWcursorposfun")
public interface GLFWCursorPosCallbackI extends CallbackI.V {

    String SIGNATURE = "(pdd)v";

    @Override
    default String getSignature() { return SIGNATURE; }

    @Override
    default void callback(long args) {
        invoke(
            dcbArgPointer(args),
            dcbArgDouble(args),
            dcbArgDouble(args)
        );
    }

    /**
     * Will be called when the cursor is moved.
     * 
     * <p>The callback function receives the cursor position, measured in screen coordinates but relative to the top-left corner of the window client area. On
     * platforms that provide it, the full sub-pixel cursor position is passed on.</p>
     *
     * @param window the window that received the event
     * @param xpos   the new cursor x-coordinate, relative to the left edge of the content area
     * @param ypos   the new cursor y-coordinate, relative to the top edge of the content area
     */
    void invoke(@NativeType("GLFWwindow *") long window, double xpos, double ypos);

}