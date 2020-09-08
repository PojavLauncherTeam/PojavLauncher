package net.kdt.pojavlaunch.jrereflect;

public class JREClass extends JRENativeObject
{
	public static JREClass forName(String name) throws ClassNotFoundException {
		long nativeAddr = nativeForName(name);
		if (nativeAddr == 0) throw new ClassNotFoundException(name);
		return new JREClass(nativeAddr);
	}
	
	private JREClass(long nativeAddr) {
		super(nativeAddr);
	}
	
	private JREMethod getMethod(String name, Class... types) throws NoSuchMethodException {
		long nativeAddr = nativeForName(name);
		if (nativeAddr == 0) throw new NoSuchMethodException(name);
		return new JREMethod(nativeAddr, name, types);
	}

	public static native long nativeForName(String name);
	public static native long nativeGetMethod(String name);
}
