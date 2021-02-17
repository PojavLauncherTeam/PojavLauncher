/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */

/**
 * Contains bindings to the <a target="_blank" href="http://www.glfw.org/">GLFW</a> library.
 * 
 * <p>GLFW comes with extensive documentation, which you can read online <a target="_blank" href="http://www.glfw.org/docs/latest/">here</a>. The
 * <a target="_blank" href="http://www.glfw.org/faq.html">Frequently Asked Questions</a> are also useful.</p>
 * 
 * <p>On macOS the JVM must be started with the {@code -XstartOnFirstThread} argument for GLFW to work. This is necessary because most GLFW functions must be
 * called on the main thread and the Cocoa API on macOS requires that thread to be the first thread in the process. For this reason, on-screen GLFW
 * windows and the GLFW event loop are incompatible with other window toolkits (such as AWT/Swing or JavaFX) on macOS. Off-screen GLFW windows can be used
 * with other window toolkits, but only if the window toolkit is initialized before GLFW.</p>
 */
@org.lwjgl.system.NonnullDefault
package org.lwjgl.glfw;

