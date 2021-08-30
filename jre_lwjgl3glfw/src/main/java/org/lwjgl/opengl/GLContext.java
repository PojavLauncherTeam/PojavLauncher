package org.lwjgl.opengl;


public class GLContext {
	
	private static ThreadLocal<ContextCapabilities> contextCapabilities = new ThreadLocal<>();
	
	public static GLContext createFromCurrent() {
		return new GLContext();
	}
	public static void initCapabilities() {
		if(contextCapabilities.get() == null) {
			System.out.println("LWJGLX: GL caps init");
			contextCapabilities.set(new ContextCapabilities());
		}
	}
	public static ContextCapabilities getCapabilities() {
		return contextCapabilities.get();
	}
}
