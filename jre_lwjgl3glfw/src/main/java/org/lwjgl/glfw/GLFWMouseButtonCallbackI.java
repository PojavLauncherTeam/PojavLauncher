/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.dyncall.DynCallback.*;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetMouseButtonCallback SetMouseButtonCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     int button,
 *     int action,
 *     int mods
 * )</code></pre>
 */
@FunctionalInterface
@NativeType("GLFWmousebuttonfun")
public interface GLFWMouseButtonCallbackI extends CallbackI.V {

    String SIGNATURE = "(piii)v";

    @Override
    default String getSignature() { return SIGNATURE; }

    @Override
    default void callback(long args) {
        invoke(
            dcbArgPointer(args),
            dcbArgInt(args),
            dcbArgInt(args),
            dcbArgInt(args)
        );
    }

    /**
     * Will be called when a mouse button is pressed or released.
     *
     * @param window the window that received the event
     * @param button the mouse button that was pressed or released
     * @param action the button action. One of:<br><table><tr><td>{@link GLFW#GLFW_PRESS PRESS}</td><td>{@link GLFW#GLFW_RELEASE RELEASE}</td></tr></table>
     * @param mods   bitfield describing which modifiers keys were held down
     */
    void invoke(@NativeType("GLFWwindow *") long window, int button, int action, int mods);

}