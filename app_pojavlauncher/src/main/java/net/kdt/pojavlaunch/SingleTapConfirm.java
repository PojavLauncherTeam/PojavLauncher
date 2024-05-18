package net.kdt.pojavlaunch;

import android.view.*;
import android.view.GestureDetector.*;

import androidx.annotation.NonNull;

public class SingleTapConfirm extends SimpleOnGestureListener {
	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		return true;
	}
}
