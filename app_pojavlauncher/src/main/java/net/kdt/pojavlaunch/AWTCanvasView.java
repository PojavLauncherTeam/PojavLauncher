package net.kdt.pojavlaunch;

import android.content.*;
import android.graphics.*;
import android.text.*;
import android.util.*;
import android.view.*;
import java.util.*;
import net.kdt.pojavlaunch.utils.*;
import org.lwjgl.glfw.*;

public class AWTCanvasView extends TextureView implements TextureView.SurfaceTextureListener, Runnable {
    private int mWidth, mHeight;
    private boolean mIsDestroyed = false;
    
    private TextPaint fpsPaint;
    private boolean attached = false;

    // Temporary count fps https://stackoverflow.com/a/13729241
    private LinkedList<Long> times = new LinkedList<Long>(){{add(System.nanoTime());}};
    private final int MAX_SIZE = 100;
    private final double NANOS = 1000000000.0;

    /** Calculates and returns frames per second */
    private double fps() {
        long lastTime = System.nanoTime();
        double difference = (lastTime - times.getFirst()) / NANOS;
        times.addLast(lastTime);
        int size = times.size();
        if (size > MAX_SIZE) {
            times.removeFirst();
        }
        return difference > 0 ? times.size() / difference : 0.0;
    }
    
    public AWTCanvasView(Context ctx) {
        this(ctx, null);
    }
    
    public AWTCanvasView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        // setWillNotDraw(false);
        
        fpsPaint = new TextPaint();
        fpsPaint.setColor(Color.WHITE);
        fpsPaint.setTextSize(20);
        
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture texture, int w, int h) {
        mWidth = w;
        mHeight = h;
        
        mIsDestroyed = false;
        new Thread(this, "AndroidAWTRenderer").start();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
        mIsDestroyed = true;
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int w, int h) {
        mWidth = w;
        mHeight = h;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture texture) {
    }
    
    private boolean mDrawing;
    private Surface mSurface;
    @Override
    public void run() {
        Canvas canvas;
        mSurface = new Surface(getSurfaceTexture());

        try {
            while (!mIsDestroyed && mSurface.isValid()) {
                canvas = mSurface.lockCanvas(null);
                canvas.drawRGB(0, 0, 0);

                if (!attached) {
                    attached = CallbackBridge.nativeAttachThreadToOther(true, BaseMainActivity.isInputStackCall);
                } else {
                    int[] rgbArray = JREUtils.renderAWTScreenFrame(/* canvas, mWidth, mHeight */);
                    mDrawing = rgbArray != null;
                    if (rgbArray != null) {
                        canvas.drawBitmap(rgbArray, 0, CallbackBridge.physicalWidth, 0, 0, CallbackBridge.physicalWidth, CallbackBridge.physicalHeight, true, null);
                    }
                    rgbArray = null;
                    // System.gc();
                }
                canvas.drawText("FPS: " + (Math.round(fps() * 10) / 10) + ", attached=" + attached + ", drawing=" + mDrawing, 50, 50, fpsPaint);

                mSurface.unlockCanvasAndPost(canvas);
            }
        } catch (Throwable th) {
            Tools.showError(getContext(), th);
        }
    }
}
