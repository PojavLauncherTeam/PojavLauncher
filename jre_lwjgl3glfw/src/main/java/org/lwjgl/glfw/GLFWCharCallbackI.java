/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.dyncall.DynCallback.*;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetCharCallback SetCharCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     unsigned int codepoint
 * )</code></pre>
 *
 * @since version 2.4
 */
@FunctionalInterface
@NativeType("GLFWcharfun")
public interface GLFWCharCallbackI extends CallbackI.V {

    String SIGNATURE = "(pi)v";

    @Override
    default String getSignature() { return SIGNATURE; }

    @Override
    default void callback(long args) {
        invoke(
            dcbArgPointer(args),
            dcbArgInt(args)
        );
    }

    /**
     * Will be called when a Unicode character is input.
     *
     * @param window    the window that received the event
     * @param codepoint the Unicode code point of the character
     */
    void invoke(@NativeType("GLFWwindow *") long window, @NativeType("unsigned int") int codepoint);

}