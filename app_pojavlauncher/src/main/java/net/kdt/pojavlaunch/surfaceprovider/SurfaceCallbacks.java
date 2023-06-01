package net.kdt.pojavlaunch.surfaceprovider;

import android.view.Surface;

public interface SurfaceCallbacks {
    void surfaceCreated(Surface surface, int width, int height);
    void surfaceSizeChanged(int width, int height);
    void surfaceDestroyed();
}
