/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.dyncall.DynCallback.*;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetWindowContentScaleCallback SetWindowContentScaleCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     float xscale,
 *     float yscale
 * )</code></pre>
 *
 * @since version 3.3
 */
@FunctionalInterface
@NativeType("GLFWwindowcontentscalefun")
public interface GLFWWindowContentScaleCallbackI extends CallbackI.V {

    String SIGNATURE = "(pff)v";

    @Override
    default String getSignature() { return SIGNATURE; }

    @Override
    default void callback(long args) {
        invoke(
            dcbArgPointer(args),
            dcbArgFloat(args),
            dcbArgFloat(args)
        );
    }

    /**
     * Will be called when the window content scale changes.
     *
     * @param window the window whose content scale changed
     * @param xscale the new x-axis content scale of the window
     * @param yscale the new y-axis content scale of the window
     */
    void invoke(@NativeType("GLFWwindow *") long window, float xscale, float yscale);

}