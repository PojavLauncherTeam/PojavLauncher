package net.kdt.pojavlaunch.colorselector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
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

        if(mRectSeelctionListener != null) mRectSeelctionListener.onLuminosityIntensityChanged(mFingerPosY,mFingerPosX, true);
        invalidate();
        return true;
    }

    public void setLuminosityIntensity(float luminosity, float intensity) {
        mFingerPosX = intensity;
        mFingerPosY = luminosity;
        if(mRectSeelctionListener != null) mRectSeelctionListener.onLuminosityIntensityChanged(mFingerPosY,mFingerPosX, false);
        invalidate();
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
        Paint rectPaint = new Paint();
        Canvas canvas = new Canvas(mSvRectangle);
        float h2f = h/2f;
        float w2f = w/2f;
        rectPaint.setShader(new LinearGradient(0,h2f, w, h2f, Color.WHITE,0, Shader.TileMode.CLAMP));
        canvas.drawRect(mViewSize, rectPaint);
        rectPaint.setShader(new LinearGradient(w2f,0, w2f, h, Color.BLACK,0, Shader.TileMode.CLAMP));
        canvas.drawRect(mViewSize, rectPaint);

    }
}
