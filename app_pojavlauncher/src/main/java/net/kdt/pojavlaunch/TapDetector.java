package net.kdt.pojavlaunch;

import android.view.MotionEvent;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_POINTER_DOWN;
import static android.view.MotionEvent.ACTION_POINTER_UP;
import static android.view.MotionEvent.ACTION_UP;

/**
 * Class aiming at better detecting X-tap events regardless of the POINTERS
 * Only uses the least amount of events possible,
 * since we aren't guaranteed to have all events in order
 */
public class TapDetector {

    public final static int DETECTION_METHOD_DOWN = 0x1;
    public final static int DETECTION_METHOD_UP = 0x2;
    public final static int DETECTION_METHOD_BOTH = 0x3; //Unused for now

    private final static int DOUBLE_TAP_MIN_DELTA_MS = 50;
    private final static int DOUBLE_TAP_MAX_DELTA_MS = 300;
    private final static int DOUBLE_TAP_SLOP_SQUARE_PX = (int) Math.pow(Tools.dpToPx(100), 2);

    private final int tapNumberToDetect;
    private int currentTapNumber = 0;

    private final int detectionMethod;

    private long mLastEventTime = 0;
    private float mLastX = 9999;
    private float mLastY = 9999;

    /**
     * @param tapNumberToDetect How many taps are needed before onTouchEvent returns True.
     * @param detectionMethod Method used to detect touches. See DETECTION_METHOD constants above.
     */
    public TapDetector(int tapNumberToDetect, int detectionMethod){
        this.tapNumberToDetect = tapNumberToDetect;
        this.detectionMethod = detectionMethod;
    }

    /**
     * A function to call when you have a touch event.
     * @param e The MotionEvent to inspect
     * @return whether or not a X-tap happened for a pointer
     */
    public boolean onTouchEvent(MotionEvent e){
        int eventAction = e.getActionMasked();
        int pointerIndex;
        int base_action, alternate_action;

        //Get the event to look forward
        if(detectDownTouch()){
           base_action = ACTION_DOWN;
           alternate_action = ACTION_POINTER_DOWN;
        }else if(detectUpTouch()){
           base_action = ACTION_UP;
           alternate_action = ACTION_POINTER_UP;
        }else return false;

        //Get the pointer index we want to look at
        if(eventAction == base_action) pointerIndex = 0;
        else if(eventAction == alternate_action) pointerIndex = e.getActionIndex();
        else return false;

        //Store current event info
        float eventX = e.getX(pointerIndex);
        float eventY = e.getY(pointerIndex);
        long eventTime = e.getEventTime();

        //Compute deltas
        long deltaTime = eventTime - mLastEventTime;
        int deltaX = (int) mLastX - (int) eventX;
        int deltaY = (int) mLastY - (int) eventY;

        //Store current event info to persist on next event
        mLastEventTime = eventTime;
        mLastX = eventX;
        mLastY = eventY;

        //Check for high enough speed and precision
        if(currentTapNumber > 0){
            if  ((deltaTime < DOUBLE_TAP_MIN_DELTA_MS || deltaTime > DOUBLE_TAP_MAX_DELTA_MS) ||
                ((deltaX*deltaX + deltaY*deltaY) > DOUBLE_TAP_SLOP_SQUARE_PX)) {
                currentTapNumber = 0;
                return false;
            }
        }

        //A worthy tap happened
        currentTapNumber += 1;
        if(currentTapNumber == tapNumberToDetect){
           resetTapDetectionState();
           return true;
        }

        //If not enough taps are reached
        return false;
    }

    /**
     * Reset the double tap values.
     */
   private void resetTapDetectionState(){
       currentTapNumber = 0;
       mLastEventTime = 0;
       mLastX = 9999;
       mLastY = 9999;
   }


   private boolean detectDownTouch(){
       return (detectionMethod & DETECTION_METHOD_DOWN) == DETECTION_METHOD_DOWN;
   }

   private boolean detectUpTouch(){
       return (detectionMethod & DETECTION_METHOD_UP) == DETECTION_METHOD_UP;
   }
}
