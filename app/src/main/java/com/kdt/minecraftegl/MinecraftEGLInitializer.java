package com.kdt.minecraftegl;

import android.app.*;
import android.content.*;
import android.os.*;
import android.system.*;
import dalvik.system.*;
import java.lang.reflect.*;
import java.util.*;
import javax.microedition.khronos.egl.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.exit.*;

import static org.lwjgl.opengl.AndroidContextImplementation.*;
import android.content.pm.*;
import org.lwjgl.opengl.*;
import javax.microedition.khronos.opengles.*;
import android.util.*;

public class MinecraftEGLInitializer
{
	public static void main(final String[] args) throws Throwable {
		try {
			// long surfaceAddress = Long.parseLong(args[0]);
			
			// Disable for testing
			ExitManager.disableSystemExit();

			long lSurfaceTexture = Long.getLong(args[0]);
			long lProducer = Long.getLong(args[1]);
			long lFrameAvailableListener = Long.getLong(args[2]);

			AndroidDisplay.windowWidth = Integer.parseInt(args[3]);
			AndroidDisplay.windowHeight = Integer.parseInt(args[4]);
			
			GLTextureView gtv = new GLTextureView(lSurfaceTexture, lProducer, lFrameAvailableListener);
			gtv.setRenderer(new GLTextureView.Renderer() {
					private volatile long eglContext = 0l;
					@Override
					public void onSurfaceDestroyed(GL10 gl) {
						Log.d("PojavLauncher", "Surface destroyed.");
					}

					@Override
					public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig p2)
					{
						EGL10 egl10 = (EGL10) EGLContext.getEGL();

						AndroidContextImplementation.theEgl = egl10;
						AndroidContextImplementation.context = egl10.eglGetCurrentContext();
						AndroidContextImplementation.display = egl10.eglGetCurrentDisplay();
						AndroidContextImplementation.read = egl10.eglGetCurrentSurface(EGL10.EGL_READ);
						AndroidContextImplementation.draw = egl10.eglGetCurrentSurface(EGL10.EGL_DRAW);
						egl10.eglMakeCurrent(AndroidContextImplementation.display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
						System.out.println(new StringBuffer().append("Gave up context: ").append(AndroidContextImplementation.context).toString());

						MainActivity.launchClassPath = args[5];
						MainActivity.launchOptimizedDirectory = args[6];
						MainActivity.launchLibrarySearchPath = args[7];

						// System.out.println("Received EGL context address: " + Long.toString(eglAddress));

						String minecraftMainClass = args[8];
						List<String> minecraftArgs = new ArrayList<String>();
						// 10
						for (int i = 9; i < args.length; i++) {
							if (args[i] != null && !args[i].isEmpty())
								minecraftArgs.add(args[i]);
						}

						try {
							initEnvs();

							boolean makeCurrBool = theEgl.eglMakeCurrent(display, read, draw, context);
							System.out.println("Gave up context: " + context + ", makeCurrent: " + Boolean.toString(makeCurrBool));
							int eglGetError = egl10.eglGetError();
							System.out.println("eglGetError: " + Integer.toString(eglGetError) + ", success: " + Boolean.toString(eglGetError == EGL10.EGL_SUCCESS));
							System.out.println("user.home: " + System.getProperty("user.home"));

							// ActivityThread.currentActivityThread().getSystemContext().startActivity(new Intent().setComponent(new ComponentName("net.kdt.pojavlaunch", ".CustomControlsActivity")));

							/*
							 GL10 gl = ((GL10) context.getGL());
							 gl.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
							 gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
							 int glErr = gl.glGetError();
							 */
							DexClassLoader classLoader = new DexClassLoader(MainActivity.launchClassPath, MainActivity.launchOptimizedDirectory, MainActivity.launchLibrarySearchPath, MinecraftEGLInitializer.class.getClassLoader());
							Class minecraftClass = classLoader.loadClass(minecraftMainClass);
							Method minecraftMethod = minecraftClass.getMethod("main", String[].class);
							minecraftMethod.invoke(null, new Object[]{minecraftArgs.toArray(new String[0])});
						} catch (Throwable th) {
							th.printStackTrace();
							System.exit(1);
						}
					}
					@Override
					public void onDrawFrame(GL10 gl) {
						//mkToast("onDrawFrame");

					}
					@Override
					public void onSurfaceChanged(GL10 gl, int width, int height) {
						AndroidDisplay.windowWidth = width;
						AndroidDisplay.windowHeight = height;
					}
				});
			gtv.setPreserveEGLContextOnPause(true);
			gtv.setRenderMode(GLTextureView.RENDERMODE_CONTINUOUSLY);
			gtv.setSize(AndroidDisplay.windowWidth, AndroidDisplay.windowHeight);
		} catch (Throwable th) {
			System.err.println("UNEXCEPTED SHUTTING DOWN");
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
