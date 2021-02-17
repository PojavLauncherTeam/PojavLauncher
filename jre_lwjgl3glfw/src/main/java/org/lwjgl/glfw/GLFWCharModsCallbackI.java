/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.dyncall.DynCallback.*;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetCharModsCallback SetCharModsCallback} method.
 * 
 * <p>Deprecared: scheduled for removal in version 4.0.</p>
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     unsigned int codepoint,
 *     int mods
 * )</code></pre>
 *
 * @since version 3.1
 */
@FunctionalInterface
@NativeType("GLFWcharmodsfun")
public interface GLFWCharModsCallbackI extends CallbackI.V {

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
     * Will be called when a Unicode character is input regardless of what modifier keys are used.
     *
     * @param window    the window that received the event
     * @param codepoint the Unicode code point of the character
     * @param mods      bitfield describing which modifier keys were held down
     */
    void invoke(@NativeType("GLFWwindow *") long window, @NativeType("unsigned int") int codepoint, int mods);

}