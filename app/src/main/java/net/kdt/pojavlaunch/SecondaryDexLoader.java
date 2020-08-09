package net.kdt.pojavlaunch;

import android.util.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/*
 * All methods there are from
 * https://android.googlesource.com/platform/frameworks/multidex/+/refs/heads/master/library/src/androidx/multidex/MultiDex.java
 */
public class SecondaryDexLoader
{
	public static String TAG = "SecondaryDexLoader";

	private static int ADDITIONAL_CLASSES = 0;
	
	public static void install(ClassLoader loader, List<File> additionalClassPathEntries, File optimizedDirectory) throws Throwable {
		ADDITIONAL_CLASSES = additionalClassPathEntries.size();
		
            /* The patched class loader is expected to be a descendant of
             * dalvik.system.BaseDexClassLoader. We modify its
             * dalvik.system.DexPathList pathList field to append additional DEX
             * file entries.
             */
            
            Object dexPathList = getDexPathList(loader);
            ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
            expandFieldArray(dexPathList, "dexElements", makeDexElements(dexPathList,
                    new ArrayList<File>(additionalClassPathEntries), optimizedDirectory,
                    suppressedExceptions));
            if (suppressedExceptions.size() > 0) {
                for (IOException e : suppressedExceptions) {
                    Log.w(TAG, "Exception in makeDexElement", e);
                }
                Field suppressedExceptionsField =
                        findField(loader, "dexElementsSuppressedExceptions");
                IOException[] dexElementsSuppressedExceptions =
                        (IOException[]) suppressedExceptionsField.get(loader);
                if (dexElementsSuppressedExceptions == null) {
                    dexElementsSuppressedExceptions =
                            suppressedExceptions.toArray(
                                    new IOException[suppressedExceptions.size()]);
                } else {
                    IOException[] combined =
                            new IOException[suppressedExceptions.size() +
                                            dexElementsSuppressedExceptions.length];
                    suppressedExceptions.toArray(combined);
                    System.arraycopy(dexElementsSuppressedExceptions, 0, combined,
                            suppressedExceptions.size(), dexElementsSuppressedExceptions.length);
                    dexElementsSuppressedExceptions = combined;
                }
                suppressedExceptionsField.set(loader, dexElementsSuppressedExceptions);
            }
	}
	
	private static /* DexPathList */ Object getDexPathList(ClassLoader loader) throws Throwable {
		Field pathListField = findField(loader, "pathList");
		return pathListField.get(loader);
	}
	
	/**
	 * A wrapper around
	 * {@code private static final dalvik.system.DexPathList#makeDexElements}.
	 */
	private static Object[] makeDexElements(Object dexPathList, ArrayList<File> files, File optimizedDirectory, ArrayList<IOException> suppressedExceptions) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Method makeDexElements = findMethod(dexPathList, "makeDexElements", ArrayList.class, File.class, ArrayList.class);
		return (Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory, suppressedExceptions);
    }
		

    /**
     * Locates a given field anywhere in the class inheritance hierarchy.
     *
     * @param instance an object to search the field into.
     * @param name field name
     * @return a field object
     * @throws NoSuchFieldException if the field cannot be located
     */
    private static Field findField(Object instance, String name) throws NoSuchFieldException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field;
            } catch (NoSuchFieldException e) {
                // ignore and search next
            }
        }
        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }
    /**
     * Locates a given method anywhere in the class inheritance hierarchy.
     *
     * @param instance an object to search the method into.
     * @param name method name
     * @param parameterTypes method parameter types
     * @return a method object
     * @throws NoSuchMethodException if the method cannot be located
     */
    private static Method findMethod(Object instance, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                return method;
            } catch (NoSuchMethodException e) {
                // ignore and search next
            }
        }
        throw new NoSuchMethodException("Method " + name + " with parameters " +
										Arrays.asList(parameterTypes) + " not found in " + instance.getClass());
    }
    /**
     * Replace the value of a field containing a non null array, by a new array containing the
     * elements of the original array plus the elements of extraElements.
     * @param instance the instance whose field is to be modified.
     * @param fieldName the field to modify.
     * @param extraElements elements to append at the end of the array.
     */
	private static Object[] originalDex;
    private static void expandFieldArray(Object instance, String fieldName, Object[] extraElements) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field jlrField = findField(instance, fieldName);
        originalDex = (Object[]) jlrField.get(instance);
        Object[] combined = (Object[]) Array.newInstance(
			originalDex.getClass().getComponentType(), originalDex.length + extraElements.length);
        System.arraycopy(originalDex, 0, combined, 0, originalDex.length);
        System.arraycopy(extraElements, 0, combined, originalDex.length, extraElements.length);
        jlrField.set(instance, combined);
    }
	
	public static void resetFieldArray(ClassLoader loader) throws Throwable {
		if (originalDex == null) return;
		
		Object instance = getDexPathList(loader);
		
		Field jlrField = findField(instance, "dexElements");
		jlrField.set(instance, originalDex);
		
		originalDex = null;
	}
}
