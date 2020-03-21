package net.kdt.pojavlaunch;
import java.lang.reflect.*;

public class SystemCrackResolver
{
	public static void arraycopy(Object src, int srcPos, Object dst, int dstPos, int length) {
		invoke("arraycopy", src, srcPos, dst, dstPos, length);
	}
	
	public static void invoke(String method, Object... params) {
		Class[] classParams = new Class[params.length];
		for (int i = 0; i < params.length; i++) {
			classParams[i] = params[i].getClass();
		}
		try {
			Method methodObj = System.class.getDeclaredMethod(method, classParams);
			methodObj.setAccessible(true);
			methodObj.invoke(null, params);
		} catch (Throwable th) {
			throw new RuntimeException(th);
		}
	}
}
