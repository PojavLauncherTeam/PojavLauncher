package com.oracle.dalvik;

public final class VMLauncher {
	private VMLauncher() {
	}
	public static native void launchJVM(String jvmPath, String[] vmArgs, String classpath, String mainClass, String[] applicationArgs);
}
