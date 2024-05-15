package net.kdt.pojavlaunch;

import android.view.*;
import android.view.GestureDetector.*;

import androidx.annotation.NonNull;

public class SingleTapConfirm extends SimpleOnGestureListener {
	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		System.out.println("single tap up !");
		return true;
	}

	@Override
	public boolean onDoubleTap(@NonNull MotionEvent e) {
		return super.onDoubleTap(e);
	}
}
