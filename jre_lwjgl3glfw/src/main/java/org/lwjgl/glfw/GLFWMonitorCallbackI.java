/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;

import static org.lwjgl.system.dyncall.DynCallback.*;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetMonitorCallback SetMonitorCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWmonitor *monitor,
 *     int event
 * )</code></pre>
 *
 * @since version 3.0
 */
@FunctionalInterface
@NativeType("GLFWmonitorfun")
public interface GLFWMonitorCallbackI extends CallbackI.V {

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
     * Will be called when a monitor is connected to or disconnected from the system.
     *
     * @param monitor the monitor that was connected or disconnected
     * @param event   one of {@link GLFW#GLFW_CONNECTED CONNECTED} or {@link GLFW#GLFW_DISCONNECTED DISCONNECTED}. Remaining values reserved for future use.
     */
    void invoke(@NativeType("GLFWmonitor *") long monitor, int event);

}