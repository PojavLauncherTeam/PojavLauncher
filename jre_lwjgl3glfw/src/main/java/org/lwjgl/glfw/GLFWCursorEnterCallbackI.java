/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.dyncall.DynCallback.*;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetCursorEnterCallback SetCursorEnterCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     int entered
 * )</code></pre>
 *
 * @since version 3.0
 */
@FunctionalInterface
@NativeType("GLFWcursorenterfun")
public interface GLFWCursorEnterCallbackI extends CallbackI.V {

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
     * Will be called when the cursor enters or leaves the client area of the window.
     *
     * @param window  the window that received the event
     * @param entered {@link GLFW#GLFW_TRUE TRUE} if the cursor entered the window's content area, or {@link GLFW#GLFW_FALSE FALSE} if it left it
     */
    void invoke(@NativeType("GLFWwindow *") long window, @NativeType("int") boolean entered);

}