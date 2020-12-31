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
 * Instances of this class may be passed to the {@link GLFW#glfwSetWindowRefreshCallback SetWindowRefreshCallback} method.
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
public abstract class GLFWWindowRefreshCallback extends Callback implements GLFWWindowRefreshCallbackI {

    /**
     * Creates a {@code GLFWWindowRefreshCallback} instance from the specified function pointer.
     *
     * @return the new {@code GLFWWindowRefreshCallback}
     */
    public static GLFWWindowRefreshCallback create(long functionPointer) {
        GLFWWindowRefreshCallbackI instance = Callback.get(functionPointer);
        return instance instanceof GLFWWindowRefreshCallback
            ? (GLFWWindowRefreshCallback)instance
            : new Container(functionPointer, instance);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code functionPointer} is {@code NULL}. */
    @Nullable
    public static GLFWWindowRefreshCallback createSafe(long functionPointer) {
        return functionPointer == NULL ? null : create(functionPointer);
    }

    /** Creates a {@code GLFWWindowRefreshCallback} instance that delegates to the specified {@code GLFWWindowRefreshCallbackI} instance. */
    public static GLFWWindowRefreshCallback create(GLFWWindowRefreshCallbackI instance) {
        return instance instanceof GLFWWindowRefreshCallback
            ? (GLFWWindowRefreshCallback)instance
            : new Container(instance.address(), instance);
    }

    protected GLFWWindowRefreshCallback() {
        super(SIGNATURE);
    }

    GLFWWindowRefreshCallback(long functionPointer) {
        super(functionPointer);
    }

    /** See {@link GLFW#glfwSetWindowRefreshCallback SetWindowRefreshCallback}. */
    public GLFWWindowRefreshCallback set(long window) {
        glfwSetWindowRefreshCallback(window, this);
        return this;
    }

    private static final class Container extends GLFWWindowRefreshCallback {

        private final GLFWWindowRefreshCallbackI delegate;

        Container(long functionPointer, GLFWWindowRefreshCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long window) {
            delegate.invoke(window);
        }

    }

}