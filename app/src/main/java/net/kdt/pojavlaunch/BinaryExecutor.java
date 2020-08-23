package net.kdt.pojavlaunch;

// This class load and execute PIE binary using dlopen and dlsym("main")
public class BinaryExecutor
{
	public static native int executeBinary(String ldLibraryPath, String[] args);
	
	static {
		System.loadLibrary("binexecutor");
	}
}
