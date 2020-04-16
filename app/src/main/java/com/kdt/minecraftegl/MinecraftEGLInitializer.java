package com.kdt.minecraftegl;

import android.os.*;
import android.system.*;
import com.google.android.gles_jni.*;
import java.lang.reflect.*;
import java.util.*;
import javax.microedition.khronos.egl.*;
import net.kdt.pojavlaunch.*;
import org.lwjgl.opengl.*;
import dalvik.system.*;

public class MinecraftEGLInitializer
{
	public static void main(String[] args) throws Throwable {
		try {
			// long eglAddress = Long.parseLong(args[0]);

			MainActivity.launchClassPath = args[0];
			MainActivity.launchOptimizedDirectory = args[1];
			MainActivity.launchLibrarySearchPath = args[2];
			
			// System.out.println("Received EGL context address: " + Long.toString(eglAddress));

			String minecraftMainClass = args[3];
			List<String> minecraftArgs = new ArrayList<String>();
			for (int i = 4; i < args.length; i++) {
				if (args[i] != null && !args[i].isEmpty())
					minecraftArgs.add(args[i]);
			}

			initEnvs();

			EGL10 egl;
			AndroidContextImplementation.theEgl = egl = (EGL10) EGLContext.getEGL(); // AndroidContextImplementation.context.getEGL();
			AndroidContextImplementation.context = egl.eglGetCurrentContext(); // new EGLContextImpl(eglAddress);
			AndroidContextImplementation.display = egl.eglGetCurrentDisplay();
			AndroidContextImplementation.read = egl.eglGetCurrentSurface(EGL10.EGL_READ);
			AndroidContextImplementation.draw = egl.eglGetCurrentSurface(EGL10.EGL_DRAW);
			
			boolean makeCurrBool = AndroidContextImplementation.theEgl.eglMakeCurrent(AndroidContextImplementation.display, AndroidContextImplementation.read, AndroidContextImplementation.draw, AndroidContextImplementation.context);
			System.out.println("Gave up context: " + AndroidContextImplementation.context + ", makeCurrent: " + Boolean.toString(makeCurrBool));
			System.out.println("eglGetError: " + Integer.toString(egl.eglGetError()));
			System.out.println("user.home: " + System.getProperty("user.home"));
			
			DexClassLoader classLoader = new DexClassLoader(args[0], args[1], args[2], MinecraftEGLInitializer.class.getClassLoader());
			Class minecraftClass = classLoader.loadClass(minecraftMainClass);
			Method minecraftMethod = minecraftClass.getMethod("main", String[].class);
			minecraftMethod.invoke(null, new Object[]{minecraftArgs.toArray(new String[0])});
		} catch (Throwable th) {
			th.printStackTrace();
			System.exit(1);
		}
	}
	
	
    public static void forceUserHome(String s) throws Exception {
        Properties props = System.getProperties();
        Class clazz = props.getClass();
        Field f = null;
        while (clazz != null) {
            try {
                f = clazz.getDeclaredField("defaults");
                break;
            } catch (Exception e) {
                clazz = clazz.getSuperclass();
            }
        }
        if (f != null) {
            f.setAccessible(true);
            Properties p = ((Properties) f.get(props));
			p.put("user.home", s);
			// p.put("user.dir", s);
        }
    }

    public static void initEnvs() throws Exception {
        Os.setenv("LIBGL_MIPMAP", "3", true);
		System.setProperty("user.home", Tools.MAIN_PATH);
		if (!System.getProperty("user.home", "/").equals(Tools.MAIN_PATH)) {
			forceUserHome(Tools.MAIN_PATH);
		}
		System.setProperty("org.apache.logging.log4j.level", "INFO");
		System.setProperty("org.apache.logging.log4j.simplelog.level", "INFO");

		// Disable javax management for smaller launcher.
		System.setProperty("log4j2.disable.jmx", "true");

		//System.setProperty("net.zhuoweizhang.boardwalk.org.apache.logging.log4j.level", "INFO");
		//System.setProperty("net.zhuoweizhang.boardwalk.org.apache.logging.log4j.simplelog.level", "INFO");

		// Change info for useful dump
		System.setProperty("java.vm.info", Build.MANUFACTURER + " " + Build.MODEL + ", Android " + Build.VERSION.RELEASE);
    }
	
}
