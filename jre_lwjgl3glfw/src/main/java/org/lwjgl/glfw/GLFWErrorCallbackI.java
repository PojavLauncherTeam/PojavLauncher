/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.dyncall.DynCallback.*;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetErrorCallback SetErrorCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     int error,
 *     char *description
 * )</code></pre>
 *
 * @since version 3.0
 */
@FunctionalInterface
@NativeType("GLFWerrorfun")
public interface GLFWErrorCallbackI extends CallbackI.V {

    String SIGNATURE = "(ip)v";

    @Override
    default String getSignature() { return SIGNATURE; }

    @Override
    default void callback(long args) {
        invoke(
            dcbArgInt(args),
            dcbArgPointer(args)
        );
    }

    /**
     * Will be called with an error code and a human-readable description when a GLFW error occurs.
     *
     * @param error       the error code
     * @param description a pointer to a UTF-8 encoded string describing the error
     */
    void invoke(int error, @NativeType("char *") long description);

}
