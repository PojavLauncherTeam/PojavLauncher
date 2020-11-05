package net.kdt.pojavlaunch;

import android.content.*;
import android.graphics.*;
import android.text.*;
import android.util.*;
import android.view.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import org.lwjgl.glfw.*;

public class AWTCanvasView extends View {
    private int mWidth, mHeight;
    
    private TextPaint fpsPaint = new TextPaint(Color.WHITE);
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
        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        // mRadius = (float) (Math.min(mWidth, mHeight) / 2 * 0.8);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (!attached) {
            attached = CallbackBridge.nativeAttachThreadToOther(true, MainActivity.isInputStackCall);
        }
        if (attached) {
            JREUtils.renderAWTScreenFrame(canvas, mWidth, mHeight);
        }
        canvas.drawText("FPS: " + fps(), 100, 100, fpsPaint);
    }
}
