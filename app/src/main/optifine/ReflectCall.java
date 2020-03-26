package ../optifine;
import java.lang.reflect.*;

public class ReflectCall
{

	private static void invokeMethodStatic(String className, String method, Object... params) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?>[] paramClass = new Class<?>[params.length];
		for (int i = 0; i < params.length; i++) {
			paramClass[i] = params[i].getClass();
		}

		Method m = null;
		try {
			m = Class.forName(className).getDeclaredMethod(method, paramClass);
			m.setAccessible(true);
			m.invoke(null, params);
		} catch (Throwable th) {
			try {
				for (Method m2: Class.forName(className).getDeclaredMethods()) {
					if (m2.getName().equals(method)) {
						m = m2;
						break;
					}
				}
			} catch (Throwable th2) {
				th2.addSuppressed(th);
				throw new RuntimeException(th2);
			}

			m.setAccessible(true);
			m.invoke(null, params);
		}
	}
}
