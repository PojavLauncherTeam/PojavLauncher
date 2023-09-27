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

import androidx.core.math.MathUtils;

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
    private float mWidthThird; // 1/3 of the view size for cursor
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
        mSelectedAlpha = (int) MathUtils.clamp(mAlphaDiv * event.getY(), 0, 0xff);
        if(mAlphaSelectionListener != null) mAlphaSelectionListener.onAlphaSelected(mSelectedAlpha);
        invalidate();
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int old_w, int old_h) {
        mViewSize.right = w;
        mViewSize.bottom = h;
        mShaderPaint.setShader(new LinearGradient(0,0,0,h, 0, Color.WHITE, Shader.TileMode.REPEAT));
        mAlphaDiv = 255f / mViewSize.bottom;
        mScreenDiv = mViewSize.bottom / 255f;
        mWidthThird = mViewSize.right / 3f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCheckerboardDrawable.draw(canvas);
        canvas.drawRect(mViewSize, mShaderPaint);
        float linePos = mSelectedAlpha * mScreenDiv;
        canvas.drawLine(0, linePos , mWidthThird, linePos, mBlackPaint);
        canvas.drawLine(mWidthThird * 2, linePos, getRight(),linePos, mBlackPaint);
    }
}
