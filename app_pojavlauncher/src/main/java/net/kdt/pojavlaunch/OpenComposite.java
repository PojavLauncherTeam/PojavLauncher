package net.kdt.pojavlaunch;

import android.app.Activity;

public class OpenComposite {
    static {
        // Load our stub. This is certainly not a normal libopenvr_api.so file - it's named
        // that so Vivecraft picks it up, but it contains OpenComposite and some android glue.
        System.loadLibrary("openvr_api");
    }

    private OpenComposite() {
    }

    public static native void bindActivity(Activity activity);
}
