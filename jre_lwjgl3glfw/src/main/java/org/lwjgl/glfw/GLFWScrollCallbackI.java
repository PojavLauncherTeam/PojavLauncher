/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.dyncall.DynCallback.*;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetScrollCallback SetScrollCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     double xoffset,
 *     double yoffset
 * )</code></pre>
 *
 * @since version 3.0
 */
@FunctionalInterface
@NativeType("GLFWscrollfun")
public interface GLFWScrollCallbackI extends CallbackI.V {

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
     * Will be called when a scrolling device is used, such as a mouse wheel or scrolling area of a touchpad.
     *
     * @param window  the window that received the event
     * @param xoffset the scroll offset along the x-axis
     * @param yoffset the scroll offset along the y-axis
     */
    void invoke(@NativeType("GLFWwindow *") long window, double xoffset, double yoffset);

}