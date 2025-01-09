package net.kdt.pojavlaunch;

import androidx.annotation.Keep;

import dalvik.annotation.optimization.CriticalNative;

@Keep
public class CriticalNativeTest {
    @CriticalNative
    public static native void testCriticalNative(int arg0, int arg1);
    public static void invokeTest() {
        testCriticalNative(0, 0);
    }
}
