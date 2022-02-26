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
    private final int MAX_SIZE = 100;
    private final double NANOS = 1000000000.0;

    private int mScaleFactor;
    private int[] mScales;

    private int mWidth, mHeight;
    private boolean mIsDestroyed = false;
    private final TextPaint mFpsPaint;
    private boolean mAttached = false;
    private boolean mDrawing;

    // Temporary count fps https://stackoverflow.com/a/13729241
    private final LinkedList<Long> mTimes = new LinkedList<Long>(){{add(System.nanoTime());}};
    
    public AWTCanvasView(Context ctx) {
        this(ctx, null);
    }
    
    public AWTCanvasView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        
        mFpsPaint = new TextPaint();
        mFpsPaint.setColor(Color.WHITE);
        mFpsPaint.setTextSize(20);
        
        setSurfaceTextureListener(this);
        initScaleFactors();
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

    @Override
    public void run() {
        Canvas canvas;
        Surface surface = new Surface(getSurfaceTexture());

        try {
            while (!mIsDestroyed && surface.isValid()) {
                canvas = surface.lockCanvas(null);
                canvas.drawRGB(0, 0, 0);

                if (!mAttached) {
                    mAttached = CallbackBridge.nativeAttachThreadToOther(true, BaseMainActivity.isInputStackCall);
                } else {
                    int[] rgbArray = JREUtils.renderAWTScreenFrame(/* canvas, mWidth, mHeight */);
                    mDrawing = rgbArray != null;
                    if (rgbArray != null) {

                        canvas.save();
                        canvas.scale(mScaleFactor, mScaleFactor);
                        canvas.translate(-mScales[0], -mScales[1]);

                        canvas.drawBitmap(rgbArray, 0, CallbackBridge.physicalWidth, 0, 0, CallbackBridge.physicalWidth, CallbackBridge.physicalHeight, true, null);
                        canvas.restore();
                    }
                }
                canvas.drawText("FPS: " + (Math.round(fps() * 10) / 10) + ", attached=" + mAttached + ", drawing=" + mDrawing, 50, 50, mFpsPaint);
                surface.unlockCanvasAndPost(canvas);
            }
        } catch (Throwable throwable) {
            Tools.showError(getContext(), throwable);
        }
        surface.release();
    }

    /** Computes the scale to better fit the screen */
    void initScaleFactors(){
        initScaleFactors(0);
    }

    void initScaleFactors(int forcedScale){
        //Could be optimized
        if(forcedScale < 1) { //Auto scale
            int minDimension = Math.min(CallbackBridge.physicalHeight, CallbackBridge.physicalWidth);
            mScaleFactor = Math.max(((3 * minDimension) / 1080) - 1, 1);
        }else{
            mScaleFactor = forcedScale;
        }

        int[] scales = new int[2]; //Left, Top

        scales[0] = (CallbackBridge.physicalWidth/2);
        scales[0] -= scales[0]/mScaleFactor;

        scales[1] = (CallbackBridge.physicalHeight/2);
        scales[1] -= scales[1]/mScaleFactor;

        mScales = scales;
    }

    /** Calculates and returns frames per second */
    private double fps() {
        long lastTime = System.nanoTime();
        double difference = (lastTime - mTimes.getFirst()) / NANOS;
        mTimes.addLast(lastTime);
        int size = mTimes.size();
        if (size > MAX_SIZE) {
            mTimes.removeFirst();
        }
        return difference > 0 ? mTimes.size() / difference : 0.0;
    }
}
