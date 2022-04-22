package net.kdt.pojavlaunch.colorselector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import net.kdt.pojavlaunch.Tools;

import top.defaults.checkerboarddrawable.CheckerboardDrawable;

public class AlphaView extends View {
    private final Drawable mCheckerboardDrawable = CheckerboardDrawable.create();
    private final Paint mShaderPaint = new Paint();
    private final Paint mBlackPaint;
    private final RectF mViewSize = new RectF(0,0,0,0);
    private AlphaSelectionListener mAlphaSelectionListener;
    private int mSelectedAlpha;
    private float mAlphaDiv; // for quick pos->alpha multiplication
    private float mScreenDiv; // for quick alpha->pos multiplication
    private float mHeightThird; // 1/3 of the view size for cursor
    public AlphaView(Context ctx, AttributeSet attrs) {
        super(ctx,attrs);
        mBlackPaint = new Paint();
        mBlackPaint.setStrokeWidth(Tools.dpToPx(3));
        mBlackPaint.setColor(Color.BLACK);
    }

    public void setAlphaSelectionListener(AlphaSelectionListener alphaSelectionListener) {
        mAlphaSelectionListener = alphaSelectionListener;
    }

    public void setAlpha(int alpha) {
        mSelectedAlpha = alpha;
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mSelectedAlpha = (int) (mAlphaDiv * event.getX());
        if(mSelectedAlpha < 0) mSelectedAlpha = 0;
        if(mSelectedAlpha > 0xff) mSelectedAlpha = 0xff;
        if(mAlphaSelectionListener != null) mAlphaSelectionListener.onAlphaSelected(mSelectedAlpha);
        invalidate();
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int old_w, int old_h) {
        mViewSize.right = w;
        mViewSize.bottom = h;
        float h2 = mViewSize.bottom / 2f;
        mShaderPaint.setShader(new LinearGradient(0,h2,w,h2, 0, Color.BLACK, Shader.TileMode.REPEAT));
        mAlphaDiv = 255f / mViewSize.right;
        mScreenDiv = mViewSize.right / 255f;
        mHeightThird = mViewSize.bottom / 3f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCheckerboardDrawable.draw(canvas);
        canvas.drawRect(mViewSize, mShaderPaint);
        float linePos = mSelectedAlpha * mScreenDiv;
        canvas.drawLine(linePos, 0 ,linePos, mHeightThird, mBlackPaint);
        canvas.drawLine(linePos, mHeightThird * 2 ,linePos, mViewSize.bottom, mBlackPaint);
    }
}
