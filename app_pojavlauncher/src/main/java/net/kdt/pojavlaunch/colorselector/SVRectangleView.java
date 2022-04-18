package net.kdt.pojavlaunch.colorselector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.Tools;

public class SVRectangleView extends View {
    Bitmap mSvRectangle;
    Paint mColorPaint = new Paint();
    Paint mCrosshairPaint = new Paint();
    RectF mViewSize;
    float mHeightInverted;
    float mWidthInverted;
    float mCrosshairSize;
    float mFingerPosX;
    float mFingerPosY;
    RectangleSelectionListener mRectSeelctionListener;
    public SVRectangleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mColorPaint.setColor(Color.BLACK);
        mColorPaint.setStyle(Paint.Style.FILL);
        mCrosshairSize = Tools.dpToPx(6);
        mCrosshairPaint.setColor(Color.BLACK);
        mCrosshairPaint.setStrokeWidth(Tools.dpToPx(3));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        mFingerPosX = x * mWidthInverted;
        mFingerPosY = y * mHeightInverted;

        if(mFingerPosX < 0) mFingerPosX = 0;
        else if(mFingerPosX > 1) mFingerPosX = 1;

        if(mFingerPosY < 0) mFingerPosY = 0;
        else if(mFingerPosY > 1) mFingerPosY = 1;

        if(mRectSeelctionListener != null) mRectSeelctionListener.onLuminosityIntensityChanged(mFingerPosY,mFingerPosX);
        invalidate();
        return true;
    }

    public void setColor(int color) {
        mColorPaint.setColor(color);
        invalidate();
    }

    public void setRectSelectionListener(RectangleSelectionListener listener) {
        mRectSeelctionListener = listener;
    }

    protected void drawCrosshair(Canvas canvas, float x, float y) {
        canvas.drawLine(mCrosshairSize * 2 + x, y, mCrosshairSize + x, y, mCrosshairPaint);
        canvas.drawLine(x - mCrosshairSize * 2, y, x - mCrosshairSize, y, mCrosshairPaint);
        canvas.drawLine(x, mCrosshairSize * 2 + y, x, mCrosshairSize + y, mCrosshairPaint);
        canvas.drawLine(x, y - mCrosshairSize * 2, x, y - mCrosshairSize, mCrosshairPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(mViewSize, mColorPaint);
        drawCrosshair(canvas, mViewSize.right * mFingerPosX, mViewSize.bottom * mFingerPosY);
        if(mSvRectangle != null)
        canvas.drawBitmap(mSvRectangle, 0,0, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mViewSize = new RectF(0,0, w,h);
        mWidthInverted = 1/mViewSize.right;
        mHeightInverted = 1/mViewSize.bottom;
        if(w > 0 && h > 0)
        regenerateRectangle();
    }

    protected void regenerateRectangle() {
        int w = getWidth();
        int h = getHeight();
        if(mSvRectangle != null)
            mSvRectangle.recycle();
        mSvRectangle = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        float intensityRatio = 255f / h;
        float luminosityRatio = 255f / w;
        for(int x = 0; x < w; x++) {
            for(int y = 0; y < h; y++) {
                int intensity = (int)(y * intensityRatio);
                mSvRectangle.setPixel(x,y,Color.argb((int)(x * luminosityRatio), intensity, intensity, intensity));
            }
        }
    }
}
