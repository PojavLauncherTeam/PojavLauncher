/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import javax.annotation.*;

import org.lwjgl.system.*;

import static org.lwjgl.system.MemoryUtil.*;

import java.io.PrintStream;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Instances of this class may be passed to the {@link GLFW#glfwSetErrorCallback SetErrorCallback} method.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * void (*) (
 *     int error,
 *     char *description
 * )</code></pre>
 *
 * @since version 3.0
 */
public abstract class GLFWErrorCallback extends Callback implements GLFWErrorCallbackI {

    /**
     * Creates a {@code GLFWErrorCallback} instance from the specified function pointer.
     *
     * @return the new {@code GLFWErrorCallback}
     */
    public static GLFWErrorCallback create(long functionPointer) {
        GLFWErrorCallbackI instance = Callback.get(functionPointer);
        return instance instanceof GLFWErrorCallback
            ? (GLFWErrorCallback)instance
            : new Container(functionPointer, instance);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code functionPointer} is {@code NULL}. */
    @Nullable
    public static GLFWErrorCallback createSafe(long functionPointer) {
        return functionPointer == NULL ? null : create(functionPointer);
    }

    /** Creates a {@code GLFWErrorCallback} instance that delegates to the specified {@code GLFWErrorCallbackI} instance. */
    public static GLFWErrorCallback create(GLFWErrorCallbackI instance) {
        return instance instanceof GLFWErrorCallback
            ? (GLFWErrorCallback)instance
            : new Container(instance.address(), instance);
    }

    protected GLFWErrorCallback() {
        super(SIGNATURE);
    }

    GLFWErrorCallback(long functionPointer) {
        super(functionPointer);
    }

    /**
     * Converts the specified {@link GLFWErrorCallback} argument to a String.
     *
     * <p>This method may only be used inside a GLFWErrorCallback invocation.</p>
     *
     * @param description pointer to the UTF-8 encoded description string
     *
     * @return the description as a String
     */
    public static String getDescription(long description) {
        try {
			return memUTF8(description);
		} catch (NullPointerException e) {
			return "null (unknown " + Long.toHexString(description) + ")";
		}
    }

    /**
     * Returns a {@link GLFWErrorCallback} instance that prints the error to the {@link APIUtil#DEBUG_STREAM}.
     *
     * @return the GLFWerrorCallback
     */
    public static GLFWErrorCallback createPrint() {
        return createPrint(APIUtil.DEBUG_STREAM);
    }

    /**
     * Returns a {@link GLFWErrorCallback} instance that prints the error in the specified {@link PrintStream}.
     *
     * @param stream the PrintStream to use
     *
     * @return the GLFWerrorCallback
     */
    public static GLFWErrorCallback createPrint(PrintStream stream) {
        return new GLFWErrorCallback() {
            private Map<Integer, String> ERROR_CODES = APIUtil.apiClassTokens((field, value) -> 0x10000 < value && value < 0x20000, null, GLFW.class);

            @Override
            public void invoke(int error, long description) {
                String msg = getDescription(description);

                stream.printf("[LWJGL] %s error\n", ERROR_CODES.get(error));
                stream.println("\tDescription : " + msg);
                stream.println("\tStacktrace  :");
                StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                for ( int i = 4; i < stack.length; i++ ) {
                    stream.print("\t\t");
                    stream.println(stack[i].toString());
                }
            }
        };
    }

    /**
     * Returns a {@link GLFWErrorCallback} instance that throws an {@link IllegalStateException} when an error occurs.
     *
     * @return the GLFWerrorCallback
     */
    public static GLFWErrorCallback createThrow() {
        return new GLFWErrorCallback() {
            @Override
            public void invoke(int error, long description) {
                throw new IllegalStateException(String.format("GLFW error [0x%X]: %s", error, getDescription(description)));
            }
        };
    }

    /** See {@link GLFW#glfwSetErrorCallback SetErrorCallback}. */
    public GLFWErrorCallback set() {
        glfwSetErrorCallback(this);
        return this;
    }

    private static final class Container extends GLFWErrorCallback {

        private final GLFWErrorCallbackI delegate;

        Container(long functionPointer, GLFWErrorCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(int error, long description) {
            delegate.invoke(error, description);
        }

    }

}
