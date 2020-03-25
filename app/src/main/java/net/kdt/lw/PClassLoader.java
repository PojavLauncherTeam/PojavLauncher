package net.kdt.lw;

import dalvik.system.*;
import java.net.*;
import net.kdt.pojavlaunch.*;

public class PClassLoader extends DexClassLoader
{
	public static String fromURL(URL[] urls) {
		StringBuilder builder = new StringBuilder();
		for (URL url : urls) {
			builder.append(url.toString().substring(5) + ":");
		}
		return builder.toString();
	}
	
	public PClassLoader(URL[] urls) {
		this(fromURL(urls));
	}
	
	public PClassLoader(String classpath) {
		super(classpath, LaunchClassLoaderAgruments.launchOptimizedDirectory, LaunchClassLoaderAgruments.launchLibrarySearchPath, PClassLoader.class.getClassLoader());
	}
}
