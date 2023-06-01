package net.kdt.pojavlaunch.surfaceprovider;

import android.view.Surface;
import android.view.View;

public interface SurfaceProvider {
    void initialize(SurfaceCallbacks callbacks);
    void changeRenderSize(int width, int height);
    Surface getSurface();
    View getView();
}
