/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.dyncall.DynCallback.*;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetWindowRefreshCallback SetWindowRefreshCallback} method.
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
@NativeType("GLFWwindowrefreshfun")
public interface GLFWWindowRefreshCallbackI extends CallbackI.V {

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
     * Will be called when the client area of the specified window needs to be redrawn, for example if the window has been exposed after having been covered by
     * another window.
     *
     * @param window the window whose content needs to be refreshed
     */
    void invoke(@NativeType("GLFWwindow *") long window);

}