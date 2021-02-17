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
 * Instances of this class may be passed to the {@link GLFW#glfwSetWindowMaximizeCallback SetWindowMaximizeCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     int maximized
 * )</code></pre>
 *
 * @since version 3.3
 */
public abstract class GLFWWindowMaximizeCallback extends Callback implements GLFWWindowMaximizeCallbackI {

    /**
     * Creates a {@code GLFWWindowMaximizeCallback} instance from the specified function pointer.
     *
     * @return the new {@code GLFWWindowMaximizeCallback}
     */
    public static GLFWWindowMaximizeCallback create(long functionPointer) {
        GLFWWindowMaximizeCallbackI instance = Callback.get(functionPointer);
        return instance instanceof GLFWWindowMaximizeCallback
            ? (GLFWWindowMaximizeCallback)instance
            : new Container(functionPointer, instance);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code functionPointer} is {@code NULL}. */
    @Nullable
    public static GLFWWindowMaximizeCallback createSafe(long functionPointer) {
        return functionPointer == NULL ? null : create(functionPointer);
    }

    /** Creates a {@code GLFWWindowMaximizeCallback} instance that delegates to the specified {@code GLFWWindowMaximizeCallbackI} instance. */
    public static GLFWWindowMaximizeCallback create(GLFWWindowMaximizeCallbackI instance) {
        return instance instanceof GLFWWindowMaximizeCallback
            ? (GLFWWindowMaximizeCallback)instance
            : new Container(instance.address(), instance);
    }

    protected GLFWWindowMaximizeCallback() {
        super(SIGNATURE);
    }

    GLFWWindowMaximizeCallback(long functionPointer) {
        super(functionPointer);
    }

    /** See {@link GLFW#glfwSetWindowMaximizeCallback SetWindowMaximizeCallback}. */
    public GLFWWindowMaximizeCallback set(long window) {
        glfwSetWindowMaximizeCallback(window, this);
        return this;
    }

    private static final class Container extends GLFWWindowMaximizeCallback {

        private final GLFWWindowMaximizeCallbackI delegate;

        Container(long functionPointer, GLFWWindowMaximizeCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long window, boolean maximized) {
            delegate.invoke(window, maximized);
        }

    }

}