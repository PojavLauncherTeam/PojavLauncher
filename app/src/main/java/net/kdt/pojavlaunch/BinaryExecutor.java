package net.kdt.pojavlaunch;

import android.system.*;
import java.io.*;

public class BinaryExecutor
{
	private BinaryExecutor() {}
	
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
	
	public static FileDescriptor redirectStdio() throws ErrnoException {
		File logFile = new File(Tools.MAIN_PATH, "v3log.txt");

		FileDescriptor fd = Os.open(logFile.getAbsolutePath(), OsConstants.O_WRONLY | OsConstants.O_CREAT | OsConstants.O_TRUNC, 0666);
		Os.dup2(fd, OsConstants.STDERR_FILENO);
		Os.dup2(fd, OsConstants.STDOUT_FILENO);
		
		return fd;
	}

	public static native int chdir(String path);
	public static native boolean dlopen(String libPath);
	
	// Load and execute PIE binary using dlopen and dlsym("main")
	public static native int executeBinary(String[] args);

	static {
		System.loadLibrary("binexecutor");
	}
}
