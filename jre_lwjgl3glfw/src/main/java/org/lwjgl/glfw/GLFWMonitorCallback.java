/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import javax.annotation.*;

import org.lwjgl.system.*;

import static org.lwjgl.system.MemoryUtil.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Instances of this class may be passed to the {@link GLFW#glfwSetMonitorCallback SetMonitorCallback} method.
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
public abstract class GLFWMonitorCallback extends Callback implements GLFWMonitorCallbackI {

    /**
     * Creates a {@code GLFWMonitorCallback} instance from the specified function pointer.
     *
     * @return the new {@code GLFWMonitorCallback}
     */
    public static GLFWMonitorCallback create(long functionPointer) {
        GLFWMonitorCallbackI instance = Callback.get(functionPointer);
        return instance instanceof GLFWMonitorCallback
            ? (GLFWMonitorCallback)instance
            : new Container(functionPointer, instance);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code functionPointer} is {@code NULL}. */
    @Nullable
    public static GLFWMonitorCallback createSafe(long functionPointer) {
        return functionPointer == NULL ? null : create(functionPointer);
    }

    /** Creates a {@code GLFWMonitorCallback} instance that delegates to the specified {@code GLFWMonitorCallbackI} instance. */
    public static GLFWMonitorCallback create(GLFWMonitorCallbackI instance) {
        return instance instanceof GLFWMonitorCallback
            ? (GLFWMonitorCallback)instance
            : new Container(instance.address(), instance);
    }

    protected GLFWMonitorCallback() {
        super(SIGNATURE);
    }

    GLFWMonitorCallback(long functionPointer) {
        super(functionPointer);
    }

    /** See {@link GLFW#glfwSetMonitorCallback SetMonitorCallback}. */
    public GLFWMonitorCallback set() {
        glfwSetMonitorCallback(this);
        return this;
    }

    private static final class Container extends GLFWMonitorCallback {

        private final GLFWMonitorCallbackI delegate;

        Container(long functionPointer, GLFWMonitorCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long monitor, int event) {
            delegate.invoke(monitor, event);
        }

    }

}