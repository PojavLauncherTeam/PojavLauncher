package net.kdt.pojavlaunch.imgcropper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.Tools;

import top.defaults.checkerboarddrawable.CheckerboardDrawable;

public class CropperView extends View {
    private final RectF mSelectionHighlight = new RectF();
    protected final Rect mSelectionRect = new Rect();
    public boolean horizontalLock, verticalLock;
    private float mLastTouchX, mLastTouchY;
    private float mHighlightThickness;
    private float mLastDistance = -1f;
    private float mSelectionPadding;
    private int mLastTrackedPointer;
    private Paint mSelectionPaint;
    public CropperBehaviour cropperBehaviour = CropperBehaviour.DUMMY;

    public CropperView(Context context) {
        super(context);
        init();
    }

    public CropperView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CropperView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    protected void init() {
        setBackground(new CheckerboardDrawable.Builder().build());
        mSelectionPadding = Tools.dpToPx(24);
        mHighlightThickness = Tools.dpToPx(3);
        mSelectionPaint = new Paint();
        mSelectionPaint.setColor(Color.DKGRAY);
        mSelectionPaint.setStrokeWidth(mHighlightThickness);
        // Divide the thickness by 2 since we will be needing only half of it for
        // rect highlight correction.
        mHighlightThickness /= 2;
        mSelectionPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        float x1 = event.getX(0);
        float y1 = event.getY(0);
        if(event.getPointerCount() > 1) {
            // More than 1 pointer = pinching
            // Compute the distance and zoom the image with it
            float x2 = event.getX(1);
            float y2 = event.getY(1);
            float deltaXSquared = (x2 - x1) * (x2 - x1);
            float deltaYSquared = (y2 - y1) * (y2 - y1);
            float distance = (float) Math.sqrt(deltaXSquared + deltaYSquared);
            if(mLastDistance != -1) {
                float distanceDelta = distance - mLastDistance;
                float multiplier = 0.005f;
                float midpointX = (x1 + x2) / 2;
                float midpointY = (y1 + y2) / 2;
                cropperBehaviour.zoom(1 + distanceDelta * multiplier, midpointX, midpointY);
            }
            mLastDistance = distance;
            return true;
        } else {
            // Reset lastDistance as it's fairly reliable to assume that when
            // there's less than 2 pointers on the screen, the zoom gesture is over
            mLastDistance = -1f;
        }

        // When not pinching, pan around. Simultaneous panning and zooming proved to be confusing in my testing.
        // Lots of code there to allow seamless finger changing while panning.
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchX = x1;
                mLastTouchY = y1;
                // Remember the pointer index from the start of the gesture.
                // We will be tracking it for the rest of the gesture unless it gets released.
                mLastTrackedPointer = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                // Fond the pointer we should be tracking
                int trackedIndex = findPointerIndex(event, mLastTrackedPointer);
                // By default, we query the X/Y coordinates of pointer index 0. If our tracked
                // pointer is no longer at index 0 and is still tracked, overwrite the coordinates
                // with the expected ones
                if(trackedIndex > 0) {
                    x1 = event.getX(trackedIndex);
                    y1 = event.getY(trackedIndex);
                }
                if(trackedIndex != -1) {
                    // If we still track out current pointer, pan the image by the movement delta
                    cropperBehaviour.pan(x1 - mLastTouchX, y1 - mLastTouchY);
                } else {
                    // Otherwise, mark the new tracked pointer without panning.
                    mLastTrackedPointer = event.getPointerId(0);
                }
                mLastTouchX = x1;
                mLastTouchY = y1;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        cropperBehaviour.drawPreHighlight(canvas);
        canvas.restore();
        canvas.drawRect(mSelectionHighlight, mSelectionPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return dispatchGenericMotionEvent(event);
    }

    private int findPointerIndex(MotionEvent event, int id)  {
        for(int i = 0; i < event.getPointerCount(); i++) {
            if(event.getPointerId(i) == id) return i;
        }
        return -1;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        int lesserDimension = (int)(Math.min(w, h) - mSelectionPadding);
        // Calculate the corners of the new selection frame. It should always appear at the center of the view.
        int centerShiftX = (w - lesserDimension) / 2;
        int centerShiftY = (h - lesserDimension) / 2;
        mSelectionRect.left = centerShiftX;
        mSelectionRect.top = centerShiftY;
        mSelectionRect.right = centerShiftX + lesserDimension;
        mSelectionRect.bottom = centerShiftY + lesserDimension;
        cropperBehaviour.onSelectionRectUpdated();
        // Adjust the selection highlight rectangle to be bigger than the selection area
        // by the highlight thickness, to make sure that the entire inside of the selection highlight
        // will fit into the image
        mSelectionHighlight.left = mSelectionRect.left - mHighlightThickness;
        mSelectionHighlight.top = mSelectionRect.top + mHighlightThickness;
        mSelectionHighlight.right = mSelectionRect.right + mHighlightThickness;
        mSelectionHighlight.bottom = mSelectionRect.bottom - mHighlightThickness;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int widthMode = MeasureSpec.getMode(widthSpec), widthSize = MeasureSpec.getSize(widthSpec);
        int heightMode = MeasureSpec.getMode(heightSpec), heightSize = MeasureSpec.getSize(heightSpec);
        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            // No leeway. Size to spec.
            setMeasuredDimension(widthSize, heightSize);
            return;
        }
        int biggestAllowedDimension = cropperBehaviour.getLargestImageSide();
        if(widthMode == MeasureSpec.EXACTLY) biggestAllowedDimension = widthSize;
        if(heightMode == MeasureSpec.EXACTLY) biggestAllowedDimension = heightSize;
        setMeasuredDimension(
                pickDesiredDimension(widthMode, widthSize, biggestAllowedDimension),
                pickDesiredDimension(heightMode, heightSize, biggestAllowedDimension)
        );

    }

    private int pickDesiredDimension(int mode, int size, int desired) {
        switch (mode) {
            case MeasureSpec.EXACTLY:
                return size;
            case MeasureSpec.AT_MOST:
                return Math.min(size, desired);
            case MeasureSpec.UNSPECIFIED:
                return desired;
        }
        return desired;
    }


    @CallSuper
    protected void reset() {
        mLastDistance = -1;
    }
    public Bitmap crop(int targetMaxSide) {
        return cropperBehaviour.crop(targetMaxSide);
    }
}
