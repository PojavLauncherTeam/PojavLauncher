package com.kdt.minecraftegl;

import java.lang.reflect.*;
import com.google.android.gles_jni.*;
import javax.microedition.khronos.egl.*;
import org.lwjgl.opengl.*;

public class MinecraftEGLInitializer
{
	static {
		System.loadLibrary("android_runtime");
	}
	
	public static void main(String[] args) throws Throwable {
		long eglAddress = Long.parseLong(args[0]);

		System.out.println("Received EGL context address: " + Long.toString(eglAddress));
		
		String minecraftMainClass = args[1];
		String[] minecraftArgs = new String[args.length - 1];
		for (int i = 2; i < args.length; i++) {
			minecraftArgs[i - 2] = args[i];
		}

		AndroidContextImplementation.context = new EGLContextImpl(eglAddress);
		AndroidContextImplementation.theEgl = (EGL10) AndroidContextImplementation.context.getEGL();
		AndroidContextImplementation.display = new EGLDisplayImpl(eglAddress);
		AndroidContextImplementation.read = new EGLSurfaceImpl(eglAddress);
		AndroidContextImplementation.draw = new EGLSurfaceImpl(eglAddress);
		AndroidContextImplementation.theEgl.eglMakeCurrent(AndroidContextImplementation.display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);

		System.out.println(new StringBuffer().append("Gave up context: ").append(AndroidContextImplementation.context).toString());

		Class minecraftClass = Class.forName(minecraftMainClass);
		Method minecraftMethod = minecraftClass.getMethod("main", String[].class);
		minecraftMethod.invoke(null, new Object[]{minecraftArgs});
	}
}
