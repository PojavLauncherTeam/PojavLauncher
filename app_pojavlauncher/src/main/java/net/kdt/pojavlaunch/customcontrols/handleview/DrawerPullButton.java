package net.kdt.pojavlaunch.customcontrols.handleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import net.kdt.pojavlaunch.R;

public class DrawerPullButton extends View {
    public DrawerPullButton(Context context) {super(context); init();}
    public DrawerPullButton(Context context, @Nullable AttributeSet attrs) {super(context, attrs); init();}

    private final Paint mPaint = new Paint();
    private VectorDrawableCompat mDrawable;

    private void init(){
        mDrawable = VectorDrawableCompat.create(getContext().getResources(), R.drawable.ic_sharp_settings_24, null);
        setAlpha(0.33f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        canvas.drawArc(0,-getHeight(),getWidth(), getHeight(), 0, 180, true, mPaint);

        mPaint.setColor(Color.WHITE);
        mDrawable.setBounds(0, 0, getHeight(), getHeight());
        canvas.save();
        canvas.translate((getWidth()-getHeight())/2f, 0);
        mDrawable.draw(canvas);
        canvas.restore();
    }
}
