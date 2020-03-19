package net.kdt.pojavlaunch;
import dalvik.system.*;

public class LaunchClassLoaderAgruments
{
	public static String launchDexPath, launchOptimizedDirectory, launchLibrarySearchPath;
	
	public static void putAll(String dexPath, String optimizedDirectory, String librarySearchPath) {
		launchDexPath = dexPath;
		launchOptimizedDirectory = optimizedDirectory;
		launchLibrarySearchPath = librarySearchPath;
	}
}
