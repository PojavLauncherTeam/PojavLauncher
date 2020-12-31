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
 * Instances of this class may be passed to the {@link GLFW#glfwSetWindowPosCallback SetWindowPosCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     int xpos,
 *     int ypos
 * )</code></pre>
 *
 * @since version 3.0
 */
public abstract class GLFWWindowPosCallback extends Callback implements GLFWWindowPosCallbackI {

    /**
     * Creates a {@code GLFWWindowPosCallback} instance from the specified function pointer.
     *
     * @return the new {@code GLFWWindowPosCallback}
     */
    public static GLFWWindowPosCallback create(long functionPointer) {
        GLFWWindowPosCallbackI instance = Callback.get(functionPointer);
        return instance instanceof GLFWWindowPosCallback
            ? (GLFWWindowPosCallback)instance
            : new Container(functionPointer, instance);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code functionPointer} is {@code NULL}. */
    @Nullable
    public static GLFWWindowPosCallback createSafe(long functionPointer) {
        return functionPointer == NULL ? null : create(functionPointer);
    }

    /** Creates a {@code GLFWWindowPosCallback} instance that delegates to the specified {@code GLFWWindowPosCallbackI} instance. */
    public static GLFWWindowPosCallback create(GLFWWindowPosCallbackI instance) {
        return instance instanceof GLFWWindowPosCallback
            ? (GLFWWindowPosCallback)instance
            : new Container(instance.address(), instance);
    }

    protected GLFWWindowPosCallback() {
        super(SIGNATURE);
    }

    GLFWWindowPosCallback(long functionPointer) {
        super(functionPointer);
    }

    /** See {@link GLFW#glfwSetWindowPosCallback SetWindowPosCallback}. */
    public GLFWWindowPosCallback set(long window) {
        glfwSetWindowPosCallback(window, this);
        return this;
    }

    private static final class Container extends GLFWWindowPosCallback {

        private final GLFWWindowPosCallbackI delegate;

        Container(long functionPointer, GLFWWindowPosCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long window, int xpos, int ypos) {
            delegate.invoke(window, xpos, ypos);
        }

    }

}