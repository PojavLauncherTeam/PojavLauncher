package net.kdt.pojavlaunch;

import android.system.*;
import java.io.*;

// This class should be named as 'LoadMe' as original
public class BinaryExecutor
{
	private BinaryExecutor() {}
	
	public static void initJavaRuntime() {
		dlopen(Tools.homeJreDir + "/lib/jli/libjli.so");
		dlopen(Tools.homeJreDir + "/lib/server/libjvm.so");
		dlopen(Tools.homeJreDir + "/lib/libverify.so");
		dlopen(Tools.homeJreDir + "/lib/libjava.so");
		dlopen(Tools.homeJreDir + "/lib/libnet.so");
		dlopen(Tools.homeJreDir + "/lib/libnio.so");
		dlopen(Tools.homeJreDir + "/lib/libawt.so");
		dlopen(Tools.homeJreDir + "/lib/libawt_headless.so");
	}

	public static void redirectStdio() throws ErrnoException {
		File logFile = new File(Tools.MAIN_PATH, "latestlog.txt");

		FileDescriptor fd = Os.open(logFile.getAbsolutePath(), OsConstants.O_WRONLY | OsConstants.O_CREAT | OsConstants.O_TRUNC, 0666);
		Os.dup2(fd, OsConstants.STDERR_FILENO);
		Os.dup2(fd, OsConstants.STDOUT_FILENO);
		
		// return fd;
	}

	public static native int chdir(String path);
	public static native boolean dlopen(String libPath);
	public static native void setLdLibraryPath(String ldLibraryPath);
	public static native void setupBridgeEGL();
	
	// BEFORE Load and execute PIE binary using dlopen and dlsym("main")
	// AFTER: Execute a binary in forked process
	public static native int executeBinary(String[] args);

	static {
		System.loadLibrary("binexecutor");
	}
}
