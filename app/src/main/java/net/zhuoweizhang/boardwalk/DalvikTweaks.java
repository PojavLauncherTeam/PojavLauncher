package net.zhuoweizhang.boardwalk;

import android.os.Build.VERSION;

public class DalvikTweaks {
    public static native void crashTheLogger();

    public static native void nativeSetDefaultStackSize(int i, int i2);

    public static native void nativeSetHeapMaxFree(long j, int i);

    public static native void nativeSetHeapMinFree(long j, int i);

    public static native void setenv(String str, String str2, boolean z);

    public static void setDefaultStackSize(int i) {
        nativeSetDefaultStackSize(i, VERSION.SDK_INT);
    }

    public static void setHeapMaxFree(long j) {
        nativeSetHeapMaxFree(j, VERSION.SDK_INT);
    }

    public static void setHeapMinFree(long j) {
        nativeSetHeapMinFree(j, VERSION.SDK_INT);
    }

    public static boolean isDalvik() {
        String property = System.getProperty("java.vm.version");
        return property != null && property.startsWith("1");
    }

    static {
        System.loadLibrary("boardwalk");
    }
}

