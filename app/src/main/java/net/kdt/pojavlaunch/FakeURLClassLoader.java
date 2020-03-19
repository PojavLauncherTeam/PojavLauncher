package net.kdt.pojavlaunch;
import java.net.*;
import java.io.*;
import dalvik.system.*;

public class FakeURLClassLoader extends URLClassLoader
{
	private DexClassLoader m;
	public FakeURLClassLoader(java.lang.String dexPath, java.lang.String optimizedDirectory, java.lang.String librarySearchPath, java.lang.ClassLoader parent) throws MalformedURLException {
		super(new URL[] { new File("test.jar").toURI().toURL() }, null);
		
		m = new DexClassLoader(dexPath, optimizedDirectory, librarySearchPath, parent);
	}
	
	@Override
    public java.lang.String findLibrary(String name) {
		return m.findLibrary(name);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		return m.loadClass(name);
	}
}
