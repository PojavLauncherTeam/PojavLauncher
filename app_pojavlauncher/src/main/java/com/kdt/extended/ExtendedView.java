package com.kdt.extended;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.R;

public interface ExtendedView {

    /* Whether compound drawables should be scaled with NN (start, top, end, bottom) */
    boolean[] mIntegerCompounds = new boolean[]{false, false, false, false};
    /* Square bounds of drawables, overriding the ones from the drawables themselves */
    int[] mSizeCompounds = new int[]{-1, -1, -1, -1};



    /** Initialize stuff specific to this view */
    default void initExtendedView(@NonNull Context context, @Nullable AttributeSet set, TextView textView){
        getAttributes(context, set);
        postProcessDrawables(textView);
    }

    /** Get extended view attribute */
    default void getAttributes(@NonNull Context context, @Nullable AttributeSet set){
        if(set == null) return;
        TypedArray values = context.getTheme().obtainStyledAttributes(set, R.styleable.ExtendedView, 0, 0);
        try {
            mIntegerCompounds[0] = values.getBoolean(R.styleable.ExtendedView_drawableStartIntegerScaling, false);
            mIntegerCompounds[1] = values.getBoolean(R.styleable.ExtendedView_drawableTopIntegerScaling, false);
            mIntegerCompounds[2] = values.getBoolean(R.styleable.ExtendedView_drawableEndIntegerScaling, false);
            mIntegerCompounds[3] = values.getBoolean(R.styleable.ExtendedView_drawableBottomIntegerScaling, false);

            mSizeCompounds[0] = values.getDimensionPixelSize(R.styleable.ExtendedView_drawableStartSize, -1);
            mSizeCompounds[1] = values.getDimensionPixelSize(R.styleable.ExtendedView_drawableTopSize, -1);
            mSizeCompounds[2] = values.getDimensionPixelSize(R.styleable.ExtendedView_drawableEndSize, -1);
            mSizeCompounds[3] = values.getDimensionPixelSize(R.styleable.ExtendedView_drawableBottomSize, -1);
        }finally {
            values.recycle();
        }
    }

    default void postProcessDrawables(TextView textView){
        if(mIntegerCompounds == null) return;
        makeDrawablesIntegerScaled(textView);
        scaleDrawablesToDesiredSize(textView);
    }

    default void scaleDrawablesToDesiredSize(TextView textView){
        Drawable[] drawables = textView.getCompoundDrawablesRelative();
        int index = -1;
        boolean shouldUpdate = false;
        Rect bounds = new Rect();
        for(Drawable drawable : drawables){
            index++;
            if(mSizeCompounds[index] == -1 || drawable == null) continue;
            drawable.copyBounds(bounds);
            if(bounds.right != mSizeCompounds[index] || bounds.bottom != mSizeCompounds[index]){
                drawable.setBounds(0,0, mSizeCompounds[index], mSizeCompounds[index]);
                shouldUpdate = true;
            }
        }
        if(shouldUpdate)
            textView.setCompoundDrawablesRelative(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    /** Makes all the compound drawables scaled with NN */
    default void makeDrawablesIntegerScaled(TextView textView){
        int index = 0;
        for(Drawable compoundDrawable : textView.getCompoundDrawablesRelative()){
            if(mIntegerCompounds[index]) makeDrawableIntegerScaled(compoundDrawable);
            index++;
        }
    }

    /** Make a single drawable scaled with NN */
    default void makeDrawableIntegerScaled(Drawable drawable){
        if(drawable == null) return;
        drawable.setDither(false);
        drawable.setFilterBitmap(false);
    }
}
