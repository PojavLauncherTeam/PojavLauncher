// Source: https://github.com/TomazWang/TIL-today-i-learned/blob/master/Android/VerticalViewPager.md
package net.kdt.pojavlaunch.launcheruiv3;

import android.content.*;
import android.support.v4.view.*;
import android.util.*;
import android.view.*;

public class PojavLauncherViewPager extends ViewPager
{
    public PojavLauncherViewPager(Context ctx) {
        this(ctx, null);
    }
    
    public PojavLauncherViewPager(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        setPageTransformer(true, new VerticalPageTransformer());
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean interceped = super.onInterceptTouchEvent(swapXY(ev));
        swapXY(ev); // swap x,y back for other touch events.
        return interceped;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapXY(ev));
    }

    private MotionEvent swapXY(MotionEvent ev) {
        float width = getWidth();
        float height = getHeight();

        float newX = (ev.getY() / height) * width;
        float newY = (ev.getX() / width) * height;

        ev.setLocation(newX, newY);

        return ev;
    }
    
    private class VerticalPageTransformer implements PageTransformer {
        @Override
        public void transformPage(View page, float position) {
            if (position < -1) {
                page.setVisibility(View.INVISIBLE);
            } else if (position <= 1) {
                page.setVisibility(View.VISIBLE);

                page.setTranslationX(page.getWidth() * -position);
                page.setTranslationY(position * page.getHeight());
            } else {
                page.setVisibility(View.INVISIBLE);
            }
        }
    }
}
