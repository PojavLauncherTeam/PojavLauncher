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
 * Instances of this class may be passed to the {@link GLFW#glfwSetKeyCallback SetKeyCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     GLFWwindow *window,
 *     int key,
 *     int scancode,
 *     int action,
 *     int mods
 * )</code></pre>
 */
public abstract class GLFWKeyCallback extends Callback implements GLFWKeyCallbackI {

    /**
     * Creates a {@code GLFWKeyCallback} instance from the specified function pointer.
     *
     * @return the new {@code GLFWKeyCallback}
     */
    public static GLFWKeyCallback create(long functionPointer) {
        GLFWKeyCallbackI instance = Callback.get(functionPointer);
        return instance instanceof GLFWKeyCallback
            ? (GLFWKeyCallback)instance
            : new Container(functionPointer, instance);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code functionPointer} is {@code NULL}. */
    @Nullable
    public static GLFWKeyCallback createSafe(long functionPointer) {
        return functionPointer == NULL ? null : create(functionPointer);
    }

    /** Creates a {@code GLFWKeyCallback} instance that delegates to the specified {@code GLFWKeyCallbackI} instance. */
    public static GLFWKeyCallback create(GLFWKeyCallbackI instance) {
        return instance instanceof GLFWKeyCallback
            ? (GLFWKeyCallback)instance
            : new Container(instance.address(), instance);
    }

    protected GLFWKeyCallback() {
        super(SIGNATURE);
    }

    GLFWKeyCallback(long functionPointer) {
        super(functionPointer);
    }

    /** See {@link GLFW#glfwSetKeyCallback SetKeyCallback}. */
    public GLFWKeyCallback set(long window) {
        glfwSetKeyCallback(window, this);
        return this;
    }

    private static final class Container extends GLFWKeyCallback {

        private final GLFWKeyCallbackI delegate;

        Container(long functionPointer, GLFWKeyCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            delegate.invoke(window, key, scancode, action, mods);
        }

    }

}