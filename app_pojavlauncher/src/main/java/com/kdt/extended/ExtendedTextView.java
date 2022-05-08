package com.kdt.extended;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class ExtendedTextView extends AppCompatTextView implements ExtendedView {
    public ExtendedTextView(@NonNull Context context) {
        super(context);
        initExtendedView(context, null, this);
    }

    public ExtendedTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initExtendedView(context,  attrs, this);
    }

    public ExtendedTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initExtendedView(context,  attrs, this);
    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        super.setCompoundDrawablesRelative(left, top, right, bottom);
        postProcessDrawables(this);
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(int left, int top, int right, int bottom) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(left, top, right, bottom);
        postProcessDrawables(this);
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        postProcessDrawables(this);
    }

    @Override
    public void setCompoundDrawablesRelative(@Nullable Drawable start, @Nullable Drawable top, @Nullable Drawable end, @Nullable Drawable bottom) {
        super.setCompoundDrawablesRelative(start, top, end, bottom);
        postProcessDrawables(this);
    }

    @Override
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(int start, int top, int end, int bottom) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        postProcessDrawables(this);
    }

    @Override
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(@Nullable Drawable start, @Nullable Drawable top, @Nullable Drawable end, @Nullable Drawable bottom) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        postProcessDrawables(this);
    }
}
