package net.kdt.pojavlaunch;

public class NativeOs {
    static {
        System.loadLibrary("nativeos");
    }
    public static native void symlink(String name1, String name2);
    public static native String getenv(String name);
    public static native void setenv(String name, String value, boolean force);
}
