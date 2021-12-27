package net.kdt.pojavlaunch;

public class MCXRLoader {
    public static native void setContext(Object ctx);
    public static native long getContextPtr();
    public static native long getJavaVMPtr();

    static {
        System.loadLibrary("mcxr_loader");
    }
}
