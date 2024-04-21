package net.kdt.pojavlaunch.customcontrols.mouse;

import android.view.MotionEvent;

public interface TouchEventProcessor {
    boolean processTouchEvent(MotionEvent motionEvent);
    void cancelPendingActions();
}
