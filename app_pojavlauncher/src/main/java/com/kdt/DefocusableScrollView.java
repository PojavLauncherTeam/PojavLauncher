package com.kdt;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
    Class allowing to ignore the focusing from an item such an EditText within it.
    Ignoring it will stop the scrollView from refocusing on the view
*/
public class DefocusableScrollView extends ScrollView {



    private boolean mKeepFocusing = false;


    public DefocusableScrollView(Context context) {
        super(context);
    }

    public DefocusableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefocusableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DefocusableScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setKeepFocusing(boolean shouldKeepFocusing){
        mKeepFocusing = shouldKeepFocusing;
    }

    public boolean isKeepFocusing(){
        return mKeepFocusing;
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        if(!mKeepFocusing) return 0;
        return super.computeScrollDeltaToGetChildRectOnScreen(rect);
    }


}
