/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.dyncall.DynCallback.*;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetKeyCallback SetKeyCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     int key,
 *     int scancode,
 *     int action,
 *     int mods
 * )</code></pre>
 */
@FunctionalInterface
@NativeType("GLFWkeyfun")
public interface GLFWKeyCallbackI extends CallbackI.V {

    String SIGNATURE = "(piiii)v";

    @Override
    default String getSignature() { return SIGNATURE; }

    @Override
    default void callback(long args) {
        invoke(
            dcbArgPointer(args),
            dcbArgInt(args),
            dcbArgInt(args),
            dcbArgInt(args),
            dcbArgInt(args)
        );
    }

    /**
     * Will be called when a key is pressed, repeated or released.
     *
     * @param window   the window that received the event
     * @param key      the keyboard key that was pressed or released
     * @param scancode the system-specific scancode of the key
     * @param action   the key action. One of:<br><table><tr><td>{@link GLFW#GLFW_PRESS PRESS}</td><td>{@link GLFW#GLFW_RELEASE RELEASE}</td><td>{@link GLFW#GLFW_REPEAT REPEAT}</td></tr></table>
     * @param mods     bitfield describing which modifiers keys were held down
     */
    void invoke(@NativeType("GLFWwindow *") long window, int key, int scancode, int action, int mods);

}