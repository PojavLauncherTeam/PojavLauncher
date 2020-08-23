package net.kdt.pojavlaunch;

// This class load and execute PIE binary using dlopen and dlsym("main")
public class BinaryExecutor
{
	public static void initJavaRuntime() {
		chdir(Tools.MAIN_PATH);
		
		dlopen(Tools.homeJreDir + "/lib/jli/libjli.so");
		dlopen(Tools.homeJreDir + "/lib/server/libjvm.so");
		dlopen(Tools.homeJreDir + "/lib/libverify.so");
		dlopen(Tools.homeJreDir + "/lib/libjava.so");
		dlopen(Tools.homeJreDir + "/lib/libnet.so");
		dlopen(Tools.homeJreDir + "/lib/libnio.so");
		dlopen(Tools.homeJreDir + "/lib/libawt.so");
		dlopen(Tools.homeJreDir + "/lib/libawt_headless.so");
	}
	
	public static native int chdir(String path);
	public static native boolean dlopen(String libPath);
	public static native int executeBinary(String[] args);
	
	static {
		System.loadLibrary("binexecutor");
	}
}
