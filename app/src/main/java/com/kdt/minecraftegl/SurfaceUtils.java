package com.kdt.minecraftegl;
import android.view.*;
import java.lang.reflect.*;

public class SurfaceUtils
{
	public static long getSurfaceAddress(Surface surface) {
		try {
			Field field = surface.getClass().getDeclaredField("mNativeObject");
			field.setAccessible(true);
			return (long) field.get(surface);
		} catch (Throwable th) {
			System.err.println("Unable to get surface address!");
			th.printStackTrace();
		}
		
		return -1l;
	}
	
	public static Surface createSurfaceByAddress(long surfaceAddress) {
		try {
			Constructor c = Surface.class.getDeclaredConstructor(long.class);
			c.setAccessible(true);
			return (Surface) c.newInstance(surfaceAddress);
		} catch (Throwable th) {
			System.err.println("Unable to create surface by address!");
			th.printStackTrace();
		}

		return null;
	}
}
