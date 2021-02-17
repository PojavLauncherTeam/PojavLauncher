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
 * Instances of this class may be passed to the {@link GLFW#glfwSetWindowContentScaleCallback SetWindowContentScaleCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     float xscale,
 *     float yscale
 * )</code></pre>
 *
 * @since version 3.3
 */
public abstract class GLFWWindowContentScaleCallback extends Callback implements GLFWWindowContentScaleCallbackI {

    /**
     * Creates a {@code GLFWWindowContentScaleCallback} instance from the specified function pointer.
     *
     * @return the new {@code GLFWWindowContentScaleCallback}
     */
    public static GLFWWindowContentScaleCallback create(long functionPointer) {
        GLFWWindowContentScaleCallbackI instance = Callback.get(functionPointer);
        return instance instanceof GLFWWindowContentScaleCallback
            ? (GLFWWindowContentScaleCallback)instance
            : new Container(functionPointer, instance);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code functionPointer} is {@code NULL}. */
    @Nullable
    public static GLFWWindowContentScaleCallback createSafe(long functionPointer) {
        return functionPointer == NULL ? null : create(functionPointer);
    }

    /** Creates a {@code GLFWWindowContentScaleCallback} instance that delegates to the specified {@code GLFWWindowContentScaleCallbackI} instance. */
    public static GLFWWindowContentScaleCallback create(GLFWWindowContentScaleCallbackI instance) {
        return instance instanceof GLFWWindowContentScaleCallback
            ? (GLFWWindowContentScaleCallback)instance
            : new Container(instance.address(), instance);
    }

    protected GLFWWindowContentScaleCallback() {
        super(SIGNATURE);
    }

    GLFWWindowContentScaleCallback(long functionPointer) {
        super(functionPointer);
    }

    /** See {@link GLFW#glfwSetWindowContentScaleCallback SetWindowContentScaleCallback}. */
    public GLFWWindowContentScaleCallback set(long window) {
        glfwSetWindowContentScaleCallback(window, this);
        return this;
    }

    private static final class Container extends GLFWWindowContentScaleCallback {

        private final GLFWWindowContentScaleCallbackI delegate;

        Container(long functionPointer, GLFWWindowContentScaleCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long window, float xscale, float yscale) {
            delegate.invoke(window, xscale, yscale);
        }

    }

}