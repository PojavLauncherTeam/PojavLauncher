package net.kdt.pojavlaunch;
import java.lang.reflect.*;

public class SystemCrackResolver
{
	public static void arraycopy(Object src, int srcPos, Object dst, int dstPos, int length) {
		invoke("arraycopy", src, srcPos, dst, dstPos, length);
	}
	
	public static void invoke(String method, Object... params) {
		try {
			Method methodObj = System.class.getDeclaredMethod(method, Object.class, int.class, Object.class, int.class, int.class);
			methodObj.setAccessible(true);
			methodObj.invoke(null, params);
		} catch (Throwable th) {
			throw new RuntimeException(th);
		}
	}
}
