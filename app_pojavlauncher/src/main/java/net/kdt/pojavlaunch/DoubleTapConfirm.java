package net.kdt.pojavlaunch;

import android.view.GestureDetector;
import android.view.GestureDetector.*;
import android.view.MotionEvent;

public class DoubleTapConfirm extends SimpleOnGestureListener {
    @Override
    public boolean onDoubleTap(MotionEvent e) {return true;}

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {return false;}
}
