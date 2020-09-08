package net.kdt.pojavlaunch.jrereflect;

public class JRENativeObject
{
	protected long mNativeAddress;
	protected JRENativeObject(long nativeAddress) {
		mNativeAddress = nativeAddress;
	}
}
