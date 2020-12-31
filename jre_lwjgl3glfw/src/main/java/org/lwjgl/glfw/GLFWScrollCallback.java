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
 * Instances of this class may be passed to the {@link GLFW#glfwSetScrollCallback SetScrollCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     double xoffset,
 *     double yoffset
 * )</code></pre>
 *
 * @since version 3.0
 */
public abstract class GLFWScrollCallback extends Callback implements GLFWScrollCallbackI {

    /**
     * Creates a {@code GLFWScrollCallback} instance from the specified function pointer.
     *
     * @return the new {@code GLFWScrollCallback}
     */
    public static GLFWScrollCallback create(long functionPointer) {
        GLFWScrollCallbackI instance = Callback.get(functionPointer);
        return instance instanceof GLFWScrollCallback
            ? (GLFWScrollCallback)instance
            : new Container(functionPointer, instance);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code functionPointer} is {@code NULL}. */
    @Nullable
    public static GLFWScrollCallback createSafe(long functionPointer) {
        return functionPointer == NULL ? null : create(functionPointer);
    }

    /** Creates a {@code GLFWScrollCallback} instance that delegates to the specified {@code GLFWScrollCallbackI} instance. */
    public static GLFWScrollCallback create(GLFWScrollCallbackI instance) {
        return instance instanceof GLFWScrollCallback
            ? (GLFWScrollCallback)instance
            : new Container(instance.address(), instance);
    }

    protected GLFWScrollCallback() {
        super(SIGNATURE);
    }

    GLFWScrollCallback(long functionPointer) {
        super(functionPointer);
    }

    /** See {@link GLFW#glfwSetScrollCallback SetScrollCallback}. */
    public GLFWScrollCallback set(long window) {
        glfwSetScrollCallback(window, this);
        return this;
    }

    private static final class Container extends GLFWScrollCallback {

        private final GLFWScrollCallbackI delegate;

        Container(long functionPointer, GLFWScrollCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long window, double xoffset, double yoffset) {
            delegate.invoke(window, xoffset, yoffset);
        }

    }

}