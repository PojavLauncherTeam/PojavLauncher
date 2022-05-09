package net.kdt.pojavlaunch.colorselector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.Tools;



public class HueView extends View {
    private final Paint blackPaint = new Paint();
    private Bitmap mGamma;
    private HueSelectionListener mHueSelectionListener;
    private float mSelectionHue;
    private float mWidthHueRatio;
    private float mHueWidthRatio;
    private float mWidth;
    private float mHeight;
    private float mHeightThird;
    public HueView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStrokeWidth(Tools.dpToPx(3));
    }

    public void setHueSelectionListener(HueSelectionListener listener) {
        mHueSelectionListener = listener;
    }
    public void setHue(float hue) {
        mSelectionHue = hue;
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mSelectionHue = event.getX() * mWidthHueRatio;
        invalidate();
        if(mHueSelectionListener != null) mHueSelectionListener.onHueSelected(mSelectionHue);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mGamma, 0, 0 ,null);
        float linePos = mSelectionHue * mHueWidthRatio;
        canvas.drawLine(linePos, 0 ,linePos, mHeightThird, blackPaint);
        canvas.drawLine(linePos, mHeightThird * 2 ,linePos, mHeight, blackPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int old_w, int old_h) {
        mWidth = w;
        mHeight = h;
        mHeightThird = mHeight / 3;
        regenerateGammaBitmap();
    }

    protected void regenerateGammaBitmap() {
        if(mGamma != null)
           mGamma.recycle();
        mGamma = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(mGamma);
        mWidthHueRatio = 360/ mWidth;
        mHueWidthRatio = mWidth / 360;
        float[] hsvFiller = new float[] {0, 1, 1};
        for(float i = 0; i < mWidth; i++) {
            hsvFiller[0] = i * mWidthHueRatio;
            paint.setColor(Color.HSVToColor(hsvFiller));
            canvas.drawLine(i,0,i, mHeight,paint);
        }
    }
}
