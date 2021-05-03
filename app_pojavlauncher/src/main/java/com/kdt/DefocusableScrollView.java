package com.kdt;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class DefocusableScrollView extends ScrollView {

    /*
        What is this class for ?
        It allows to ignore the focusing from an item such an EditText.
        Ignoring it will stop the scrollView from refocusing on the view
     */

    private boolean keepFocusing = false;


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
        keepFocusing = shouldKeepFocusing;
    }

    public boolean isKeepFocusing(){
        return keepFocusing;
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        if(!keepFocusing) return 0;
        return super.computeScrollDeltaToGetChildRectOnScreen(rect);
    }


}
