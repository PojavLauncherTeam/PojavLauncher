package net.kdt.pojavlaunch;

import android.view.MotionEvent;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_POINTER_DOWN;

/**
 * Class aiming at better detecting double tap events for EVERY POINTER
 * Only uses the least amount of events possible,
 * since we aren't guaranteed to have all events in order
 */
public class DoubleTapDetector {

    private final static int DOUBLE_TAP_MIN_DELTA_MS = 50;
    private final static int DOUBLE_TAP_MAX_DELTA_MS = 300;
    private final static int DOUBLE_TAP_SLOP_SQUARE_PX = (int) Math.pow(Tools.dpToPx(100), 2);

    private long mLastEventTime = 0;
    private float mLastX = 9999;
    private float mLastY = 9999;

    /**
     * A function to call when you have a touch event.
     * @param e The MotionEvent to inspect
     * @return whether or not a double tap happened for a pointer
     */
   public boolean onTouchEvent(MotionEvent e){
       int eventAction = e.getActionMasked();
       int pointerIndex;

       //Get the pointer index we want to look at
       if(eventAction == ACTION_DOWN) pointerIndex = 0;
       else if(eventAction == ACTION_POINTER_DOWN) pointerIndex = e.getActionIndex();
       else return false;

       float eventX = e.getX(pointerIndex);
       float eventY = e.getY(pointerIndex);
       long eventTime = e.getEventTime();

       long deltaTime = eventTime - mLastEventTime;
        if(deltaTime > DOUBLE_TAP_MIN_DELTA_MS && deltaTime < DOUBLE_TAP_MAX_DELTA_MS){
           int deltaX = (int) mLastX - (int) eventX;
           int deltaY = (int) mLastY - (int) eventY;
            if((deltaX*deltaX + deltaY*deltaY) < DOUBLE_TAP_SLOP_SQUARE_PX){
                //Then I guess there is a double tap :thonk:
                resetDoubleTapState();
                return true;
            }
        }

       mLastEventTime = eventTime;
       mLastX = eventX;
       mLastY = eventY;
       return false;
   }

    /**
     * Reset the double tap values.
     */
   private void resetDoubleTapState(){
       mLastEventTime = 0;
       mLastX = 9999;
       mLastY = 9999;
   }

}
