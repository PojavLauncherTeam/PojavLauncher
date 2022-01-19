package net.kdt.pojavlaunch;

import android.content.Context;

public class MCXRLoader {
    public static native void setContext(Context ctx);

    public static native long getContextPtr();

    public static native void setApplicationPtr(MainActivity activity);

    static {
        System.loadLibrary("mcxr_loader");
    }

    public static native void launch(MainActivity activity);
}
