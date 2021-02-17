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
 * Instances of this class may be passed to the {@link GLFW#glfwSetCharCallback SetCharCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     unsigned int codepoint
 * )</code></pre>
 *
 * @since version 2.4
 */
public abstract class GLFWCharCallback extends Callback implements GLFWCharCallbackI {

    /**
     * Creates a {@code GLFWCharCallback} instance from the specified function pointer.
     *
     * @return the new {@code GLFWCharCallback}
     */
    public static GLFWCharCallback create(long functionPointer) {
        GLFWCharCallbackI instance = Callback.get(functionPointer);
        return instance instanceof GLFWCharCallback
            ? (GLFWCharCallback)instance
            : new Container(functionPointer, instance);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code functionPointer} is {@code NULL}. */
    @Nullable
    public static GLFWCharCallback createSafe(long functionPointer) {
        return functionPointer == NULL ? null : create(functionPointer);
    }

    /** Creates a {@code GLFWCharCallback} instance that delegates to the specified {@code GLFWCharCallbackI} instance. */
    public static GLFWCharCallback create(GLFWCharCallbackI instance) {
        return instance instanceof GLFWCharCallback
            ? (GLFWCharCallback)instance
            : new Container(instance.address(), instance);
    }

    protected GLFWCharCallback() {
        super(SIGNATURE);
    }

    GLFWCharCallback(long functionPointer) {
        super(functionPointer);
    }

    /** See {@link GLFW#glfwSetCharCallback SetCharCallback}. */
    public GLFWCharCallback set(long window) {
        glfwSetCharCallback(window, this);
        return this;
    }

    private static final class Container extends GLFWCharCallback {

        private final GLFWCharCallbackI delegate;

        Container(long functionPointer, GLFWCharCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long window, int codepoint) {
            delegate.invoke(window, codepoint);
        }

    }

}