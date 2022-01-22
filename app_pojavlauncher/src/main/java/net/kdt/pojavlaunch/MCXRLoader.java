package net.kdt.pojavlaunch;

import android.app.Activity;

public class MCXRLoader {
    public static native void setActivity(Activity ctx);

    static {
        System.loadLibrary("mcxr_loader");
    }

    public static native void launch(MainActivity activity);
}
