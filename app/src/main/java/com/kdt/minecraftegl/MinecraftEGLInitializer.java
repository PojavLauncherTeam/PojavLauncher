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
import static org.lwjgl.opengl.AndroidContextImplementation.*;
import android.view.*;
import net.kdt.pojavlaunch.exit.*;

public class MinecraftEGLInitializer
{
	public static void main(String[] args) throws Throwable {
		try {
			// long surfaceAddress = Long.parseLong(args[0]);
			
			// Disable for testing
			// ExitManager.disableSystemExit();
			
			MainActivity.launchClassPath = args[0];
			MainActivity.launchOptimizedDirectory = args[1];
			MainActivity.launchLibrarySearchPath = args[2];
			
			// System.out.println("Received EGL context address: " + Long.toString(eglAddress));

			String minecraftMainClass = args[3];
			List<String> minecraftArgs = new ArrayList<String>();
			for (int i = 5; i < args.length; i++) {
				if (args[i] != null && !args[i].isEmpty())
					minecraftArgs.add(args[i]);
			}

			initEnvs();

			EGL10 egl;
			theEgl = egl = (EGL10) EGLContext.getEGL(); // context.getEGL();
			context = egl.eglGetCurrentContext(); // new EGLContextImpl(eglAddress);
			display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
			read = egl.eglGetCurrentSurface(EGL10.EGL_READ);
			draw = egl.eglGetCurrentSurface(EGL10.EGL_DRAW);
			
			int[] attribs = {
				EGL10.EGL_RED_SIZE,   8,
				EGL10.EGL_GREEN_SIZE, 8,
				EGL10.EGL_BLUE_SIZE,  8,
				EGL10.EGL_DEPTH_SIZE, 16, // 0
				EGL10.EGL_NONE
			};
			
			egl.eglInitialize(display, new int[]{0, 0});
			EGLConfig config = getEglConfig(egl, display, attribs);
			// egl.eglCreateWindowSurface(display, config, SurfaceUtils.createSurfaceByAddress(surfaceAddress), null);
			
			boolean makeCurrBool = theEgl.eglMakeCurrent(display, read, draw, context);
			System.out.println("Gave up context: " + context + ", makeCurrent: " + Boolean.toString(makeCurrBool));
			int eglGetError = egl.eglGetError();
			System.out.println("eglGetError: " + Integer.toString(eglGetError) + ", success: " + Boolean.toString(eglGetError == EGL10.EGL_SUCCESS));
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
	
	private static EGLConfig getEglConfig(EGL10 egl, EGLDisplay eglDisplay, int[] configAttributes) {
		EGLConfig[] configs = new EGLConfig[1];
		int[] numConfigs = new int[1];
		if (!egl.eglChooseConfig(eglDisplay, configAttributes, configs, configs.length, numConfigs)) {
			throw new RuntimeException(
				"eglChooseConfig failed: 0x" + Integer.toHexString(egl.eglGetError()));
		}
		if (numConfigs[0] <= 0) {
			throw new RuntimeException("Unable to find any matching EGL config");
		}
		final EGLConfig eglConfig = configs[0];
		if (eglConfig == null) {
			throw new RuntimeException("eglChooseConfig returned null");
		}
		return eglConfig;
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
