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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import net.kdt.pojavlaunch.Tools;

import top.defaults.checkerboarddrawable.CheckerboardDrawable;

public class AlphaView extends View {
    Drawable mCheckerboardDrawable = CheckerboardDrawable.create();
    Paint mShaderPaint = new Paint();
    Paint mBlackPaint;
    RectF mViewSize = new RectF(0,0,0,0);
    AlphaSelectionListener mAlphaSelectionListener;
    int mSelectedAlpha;
    float mAlphaDiv; // for quick pos->alpha multiplication
    float mScreenDiv; // for quick alpha->pos multiplication
    float mHeightThird; // 1/3 of the view size for cursor
    public AlphaView(Context ctx, AttributeSet attrs) {
        super(ctx,attrs);
        mBlackPaint = new Paint();
        mBlackPaint.setStrokeWidth(Tools.dpToPx(3));
        mBlackPaint.setColor(Color.BLACK);
    }

    public void setAlpha(int alpha) {
        if(mAlphaSelectionListener != null) mAlphaSelectionListener.onAlphaSelected(mSelectedAlpha,false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mSelectedAlpha = (int) (mAlphaDiv * event.getX());
        if(mAlphaSelectionListener != null) mAlphaSelectionListener.onAlphaSelected(mSelectedAlpha,true);
        invalidate();
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mViewSize.right = w;
        mViewSize.bottom = h;
        float h2 = mViewSize.bottom / 2f;
        mShaderPaint.setShader(new LinearGradient(0,h2,w,h2, Color.BLACK, 0, Shader.TileMode.REPEAT));
        mAlphaDiv = 255f / mViewSize.right;
        mScreenDiv = mViewSize.right / 255f;
        mHeightThird = mViewSize.bottom / 3f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i("onDraw","onDraw");
        mCheckerboardDrawable.draw(canvas);
        canvas.drawRect(mViewSize, mShaderPaint);
        float linePos = mSelectedAlpha * mScreenDiv;
        Log.i("onDraw",linePos+"");
        canvas.drawLine(linePos, 0 ,linePos, mHeightThird, mBlackPaint);
        canvas.drawLine(linePos, mHeightThird * 2 ,linePos, mViewSize.bottom, mBlackPaint);
    }
}
