/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.dyncall.DynCallback.*;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetDropCallback SetDropCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     int count,
 *     char const **names
 * )</code></pre>
 *
 * @since version 3.1
 */
@FunctionalInterface
@NativeType("GLFWdropfun")
public interface GLFWDropCallbackI extends CallbackI.V {

    String SIGNATURE = "(pip)v";

    @Override
    default String getSignature() { return SIGNATURE; }

    @Override
    default void callback(long args) {
        invoke(
            dcbArgPointer(args),
            dcbArgInt(args),
            dcbArgPointer(args)
        );
    }

    /**
     * Will be called when one or more dragged files are dropped on the window.
     *
     * @param window the window that received the event
     * @param count  the number of dropped files
     * @param names  pointer to the array of UTF-8 encoded path names of the dropped files
     */
    void invoke(@NativeType("GLFWwindow *") long window, int count, @NativeType("char const **") long names);

}