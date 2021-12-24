package net.sorenon.mcxr.play;

public class MCXRLoader {
    static {
        System.loadLibrary("mcxr_loader");
    }

    public static native void setContext(Object ctx);
    public static native void setJavaVM();
    public static native long getContextPtr();
    public static native long getJavaVMPtr();
}
