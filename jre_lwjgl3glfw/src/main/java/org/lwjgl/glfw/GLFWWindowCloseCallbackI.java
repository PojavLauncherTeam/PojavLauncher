/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.dyncall.DynCallback.*;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetWindowCloseCallback SetWindowCloseCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window
 * )</code></pre>
 *
 * @since version 2.5
 */
@FunctionalInterface
@NativeType("GLFWwindowclosefun")
public interface GLFWWindowCloseCallbackI extends CallbackI.V {

    String SIGNATURE = "(p)v";

    @Override
    default String getSignature() { return SIGNATURE; }

    @Override
    default void callback(long args) {
        invoke(
            dcbArgPointer(args)
        );
    }

    /**
     * Will be called when the user attempts to close the specified window, for example by clicking the close widget in the title bar.
     *
     * @param window the window that the user attempted to close
     */
    void invoke(@NativeType("GLFWwindow *") long window);

}