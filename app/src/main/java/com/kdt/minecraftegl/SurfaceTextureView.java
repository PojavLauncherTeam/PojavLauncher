package com.kdt.minecraftegl;

import android.graphics.*;
import net.kdt.pojavlaunch.*;

public class SurfaceTextureView
{
	private SurfaceTexture mSurfaceTexture;
	private SurfaceTextureListener mListener;
	private boolean isTextureAvailable = false;
	public SurfaceTextureView(long lSurfaceTexture, long lProducer, long lFrameAvailableListener) {
		mSurfaceTexture = new SurfaceTexture(false);
		try {
			Tools.findField(mSurfaceTexture, "mSurfaceTexture").set(mSurfaceTexture, lSurfaceTexture);
			Tools.findField(mSurfaceTexture, "mProducer").set(mSurfaceTexture, lProducer);
			Tools.findField(mSurfaceTexture, "mFrameAvailableListener").set(mSurfaceTexture, lFrameAvailableListener);
		} catch (Throwable th) {
			throw new RuntimeException(th);
		}
	}
	
	public SurfaceTexture getSurfaceTexture() {
		return mSurfaceTexture;
	}
	
	public void setSize(int width, int height) {
		if (mListener != null) {
			if (isTextureAvailable) {
				mListener.onSurfaceTextureSizeChanged(mSurfaceTexture, width, height);
			} else {
				onAttachedToWindow();
				mListener.onSurfaceTextureAvailable(mSurfaceTexture, width, height);
				
				isTextureAvailable = true;
			}
		}
	}
	
	public void setSurfaceTextureListener(SurfaceTextureListener listener) {
		mListener = listener;
	}
	
	public void updateTexImage() {
		if (mListener != null) mListener.onSurfaceTextureUpdated(mSurfaceTexture);
	}
	
	protected void onAttachedToWindow() {
		// if (mListener != null) mListener.onSurfaceTextureAvailable(mSurfaceTexture);
	}
	
	protected void onDetachedFromWindow() {
		if (mListener != null) mListener.onSurfaceTextureDestroyed(mSurfaceTexture);
	}

    /**
     * This listener can be used to be notified when the surface texture
     * associated with this texture view is available.
     */
    public static interface SurfaceTextureListener {
        /**
         * Invoked when a {@link TextureView}'s SurfaceTexture is ready for use.
         *
         * @param surface The surface returned by
         *                {@link android.view.TextureView#getSurfaceTexture()}
         * @param width The width of the surface
         * @param height The height of the surface
         */
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height);
        /**
         * Invoked when the {@link SurfaceTexture}'s buffers size changed.
         *
         * @param surface The surface returned by
         *                {@link android.view.TextureView#getSurfaceTexture()}
         * @param width The new width of the surface
         * @param height The new height of the surface
         */
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height);
        /**
         * Invoked when the specified {@link SurfaceTexture} is about to be destroyed.
         * If returns true, no rendering should happen inside the surface texture after this method
         * is invoked. If returns false, the client needs to call {@link SurfaceTexture#release()}.
         * Most applications should return true.
         *
         * @param surface The surface about to be destroyed
         */
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface);
        /**
         * Invoked when the specified {@link SurfaceTexture} is updated through
         * {@link SurfaceTexture#updateTexImage()}.
         *
         * @param surface The surface just updated
         */
        public void onSurfaceTextureUpdated(SurfaceTexture surface);
    }
}
