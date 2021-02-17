/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.*;
import org.lwjgl.system.macosx.*;

import static org.lwjgl.system.JNI.*;
import static org.lwjgl.system.macosx.LibC.*;
import static org.lwjgl.system.macosx.ObjCRuntime.*;

/**
 * Contains checks for the event loop issues on OS X.
 *
 * <p>On-screen GLFW windows can only be used in the main thread and only if that thread is the first thread in the process. This requires running the JVM with
 * {@code -XstartOnFirstThread}, which means that other window toolkits (AWT/Swing, JavaFX, etc.) cannot be used at the same time.</p>
 *
 * <p>Another window toolkit <em>can</em> be used if GLFW windows are never shown (created with {@link GLFW#GLFW_VISIBLE GLFW_VISIBLE} equal to
 * {@link GLFW#GLFW_FALSE GLFW_FALSE}) and only used as contexts for offscreen rendering. This is possible if the window toolkit has initialized and created
 * the shared application (NSApp) before a GLFW window is created.</p>
 */
final class EventLoop {

    static final class OffScreen {
        static {
            if (Platform.get() == Platform.MACOSX && !isMainThread()) {
                // The only way to avoid a crash is if the shared application (NSApp) has been created by something else
                throw new IllegalStateException(
                    isJavaStartedOnFirstThread()
                        ? "GLFW windows may only be created on the main thread."
                        : "GLFW windows may only be created on the main thread and that thread must be the first thread in the process. Please run " +
                          "the JVM with -XstartOnFirstThread. For offscreen rendering, make sure another window toolkit (e.g. AWT or JavaFX) is " +
                          "initialized before GLFW and Configuration.GLFW_CHECK_THREAD0 is set to false."
                );
            }
        }

        private OffScreen() {
        }

        static void check() {
            // intentionally empty to trigger the static initializer
        }
    }

    static final class OnScreen {
        static {
            if (Platform.get() == Platform.MACOSX && !isMainThread()) {
                throw new IllegalStateException(
                    "Please run the JVM with -XstartOnFirstThread and make sure a window toolkit other than GLFW (e.g. AWT or JavaFX) is not initialized."
                );
            }
        }

        private OnScreen() {
        }

        static void check() {
            // intentionally empty to trigger the static initializer
        }
    }

    private EventLoop() {
    }

    private static boolean isMainThread() {
        if (!Configuration.GLFW_CHECK_THREAD0.get(true)) {
            return true;
        }

        long objc_msgSend = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");

        long NSThread      = objc_getClass("NSThread");
        long currentThread = invokePPP(NSThread, sel_getUid("currentThread"), objc_msgSend);

        return invokePPZ(currentThread, sel_getUid("isMainThread"), objc_msgSend);
    }

    private static boolean isJavaStartedOnFirstThread() {
        return "1".equals(System.getenv().get("JAVA_STARTED_ON_FIRST_THREAD_" + getpid()));
    }

}