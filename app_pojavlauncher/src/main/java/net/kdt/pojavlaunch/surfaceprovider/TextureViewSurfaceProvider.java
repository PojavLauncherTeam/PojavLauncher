package net.kdt.pojavlaunch.surfaceprovider;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;

public class TextureViewSurfaceProvider implements SurfaceProvider, TextureView.SurfaceTextureListener{
    private final TextureView mTextureView;
    private SurfaceCallbacks mCallbacks;
    private Surface mSurface;


    public TextureViewSurfaceProvider(Context context) {
        mTextureView = new TextureView(context);
        mTextureView.setSurfaceTextureListener(this);
        mTextureView.setOpaque(true);
        mTextureView.setAlpha(1.0f);
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        this.mSurface = new Surface(surface);
        if(mCallbacks != null) mCallbacks.surfaceCreated(this.mSurface, width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        if(mCallbacks != null) mCallbacks.surfaceSizeChanged(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        mSurface = null;
        if(mCallbacks != null) mCallbacks.surfaceDestroyed();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {}

    @Override
    public void initialize(SurfaceCallbacks callbacks) {
        this.mCallbacks = callbacks;
    }

    @Override
    public void changeRenderSize(int width, int height) {
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        if(surfaceTexture != null) {
            surfaceTexture.setDefaultBufferSize(width, height);
        }
    }

    @Override
    public Surface getSurface() {
        return mSurface;
    }

    @Override
    public View getView() {
        return mTextureView;
    }
}
