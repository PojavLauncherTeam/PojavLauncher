package net.kdt.pojavlaunch.surfaceprovider;

import android.content.Context;
import android.graphics.Rect;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

public class SurfaceViewSurfaceProvider implements SurfaceProvider, SurfaceHolder.Callback {
    private final SurfaceView mSurfaceView;
    private SurfaceCallbacks mCallbacks;


    public SurfaceViewSurfaceProvider(Context context) {
        mSurfaceView = new SurfaceView(context);
        mSurfaceView.getHolder().addCallback(this);
    }

    @Override
    public void initialize(SurfaceCallbacks callbacks) {
        this.mCallbacks = callbacks;
    }

    @Override
    public void changeRenderSize(int width, int height) {
        if(mSurfaceView != null && mSurfaceView.getHolder() != null) mSurfaceView.getHolder().setFixedSize(width, height);
    }

    @Override
    public Surface getSurface() {
        if(mSurfaceView != null && mSurfaceView.getHolder() != null) return mSurfaceView.getHolder().getSurface();
        return null;
    }

    @Override
    public View getView() {
        return mSurfaceView;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Rect surfaceRect = holder.getSurfaceFrame();
        if(mCallbacks != null) mCallbacks.surfaceCreated(holder.getSurface(), surfaceRect.right, surfaceRect.bottom);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if(mCallbacks != null) mCallbacks.surfaceSizeChanged(width, height);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if(mCallbacks != null) mCallbacks.surfaceDestroyed();
    }
}
