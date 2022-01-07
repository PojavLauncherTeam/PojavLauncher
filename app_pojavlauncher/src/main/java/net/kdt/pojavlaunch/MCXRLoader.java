package net.kdt.pojavlaunch;

import android.app.Activity;

public class MCXRLoader {
    public static native void setContext(Activity ctx);
    public static native long getContextPtr();

    static {
        System.loadLibrary("mcxr_loader");
    }
}
