package net.kdt.pojavlaunch;

import android.app.Activity;
import android.content.Context;

public class MCXRLoader {
    public static native void setContext(Context ctx);

    public static native void setApplicationActivity(Activity activity);

    static {
        System.loadLibrary("mcxr_loader");
    }
}
