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
 * Instances of this class may be passed to the {@link GLFW#glfwSetWindowCloseCallback SetWindowCloseCallback} method.
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
public abstract class GLFWWindowCloseCallback extends Callback implements GLFWWindowCloseCallbackI {

    /**
     * Creates a {@code GLFWWindowCloseCallback} instance from the specified function pointer.
     *
     * @return the new {@code GLFWWindowCloseCallback}
     */
    public static GLFWWindowCloseCallback create(long functionPointer) {
        GLFWWindowCloseCallbackI instance = Callback.get(functionPointer);
        return instance instanceof GLFWWindowCloseCallback
            ? (GLFWWindowCloseCallback)instance
            : new Container(functionPointer, instance);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code functionPointer} is {@code NULL}. */
    @Nullable
    public static GLFWWindowCloseCallback createSafe(long functionPointer) {
        return functionPointer == NULL ? null : create(functionPointer);
    }

    /** Creates a {@code GLFWWindowCloseCallback} instance that delegates to the specified {@code GLFWWindowCloseCallbackI} instance. */
    public static GLFWWindowCloseCallback create(GLFWWindowCloseCallbackI instance) {
        return instance instanceof GLFWWindowCloseCallback
            ? (GLFWWindowCloseCallback)instance
            : new Container(instance.address(), instance);
    }

    protected GLFWWindowCloseCallback() {
        super(SIGNATURE);
    }

    GLFWWindowCloseCallback(long functionPointer) {
        super(functionPointer);
    }

    /** See {@link GLFW#glfwSetWindowCloseCallback SetWindowCloseCallback}. */
    public GLFWWindowCloseCallback set(long window) {
        glfwSetWindowCloseCallback(window, this);
        return this;
    }

    private static final class Container extends GLFWWindowCloseCallback {

        private final GLFWWindowCloseCallbackI delegate;

        Container(long functionPointer, GLFWWindowCloseCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long window) {
            delegate.invoke(window);
        }

    }

}