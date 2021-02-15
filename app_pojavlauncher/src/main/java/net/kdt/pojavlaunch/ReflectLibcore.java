package net.kdt.pojavlaunch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/* This whole class is a big reflection hack to make symlinks, setenv and getenv to work on android 4.4 */
public class ReflectLibcore {
    static Method symlinkMethod;
    static Method setenvMetohd;
    static Method getenvMetohd;
    static Object os;
    public static void prepare() throws Exception{
        final Class<?> libcore = Class.forName("libcore.io.Libcore");
        final java.lang.reflect.Field fOs = libcore.getDeclaredField("os");
        fOs.setAccessible(true);
        os = fOs.get(null);
        symlinkMethod = os.getClass().getMethod("symlink", String.class, String.class);
        getenvMetohd = os.getClass().getMethod("getenv", String.class);
        setenvMetohd = os.getClass().getMethod("setenv", String.class, String.class, boolean.class);
    }
    public static void setenv(String s1, String s2, boolean b1) {
        try {
            setenvMetohd.setAccessible(true);
            setenvMetohd.invoke(os,s1,s2,b1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getenv(String s1) {
        try {
            getenvMetohd.setAccessible(true);
            return (String) getenvMetohd.invoke(os, s1);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void symlink(String s1, String s2) {
        try {
            symlinkMethod.setAccessible(true);
            symlinkMethod.invoke(os,s1,s2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

