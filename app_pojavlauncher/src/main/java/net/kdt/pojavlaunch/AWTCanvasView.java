package net.kdt.pojavlaunch;

import android.content.*;
import android.graphics.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.RelativeLayout;

import java.util.*;
import net.kdt.pojavlaunch.utils.*;
import org.lwjgl.glfw.*;

public class AWTCanvasView extends TextureView implements TextureView.SurfaceTextureListener, Runnable {
    public static final int AWT_CANVAS_WIDTH = 720;
    public static final int AWT_CANVAS_HEIGHT = 600;
    private final int MAX_SIZE = 100;
    private final double NANOS = 1000000000.0;

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

        post(this::refreshSize);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture texture, int w, int h) {
        getSurfaceTexture().setDefaultBufferSize(AWT_CANVAS_WIDTH, AWT_CANVAS_HEIGHT);
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
        getSurfaceTexture().setDefaultBufferSize(AWT_CANVAS_WIDTH, AWT_CANVAS_HEIGHT);
        mWidth = w;
        mHeight = h;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        getSurfaceTexture().setDefaultBufferSize(AWT_CANVAS_WIDTH, AWT_CANVAS_HEIGHT);
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

                        canvas.drawBitmap(rgbArray, 0, AWT_CANVAS_WIDTH, 0, 0, AWT_CANVAS_WIDTH, AWT_CANVAS_HEIGHT, true, null);
                        canvas.restore();
                    }
                }
                canvas.drawText("FPS: " + (Math.round(fps() * 10) / 10) + ", attached=" + mAttached + ", drawing=" + mDrawing, 0, 20, mFpsPaint);
                surface.unlockCanvasAndPost(canvas);
            }
        } catch (Throwable throwable) {
            Tools.showError(getContext(), throwable);
        }
        surface.release();
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

    /** Make the view fit the proper aspect ratio of the surface */
    private void refreshSize(){
        ViewGroup.LayoutParams layoutParams = getLayoutParams();

        if(getHeight() < getWidth()){
            layoutParams.width = AWT_CANVAS_WIDTH * getHeight() / AWT_CANVAS_HEIGHT;
        }else{
            layoutParams.height = AWT_CANVAS_HEIGHT * getWidth() / AWT_CANVAS_WIDTH;
        }

        setLayoutParams(layoutParams);
    }

}
