package net.kdt.pojavlaunch.value.customcontrols;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.view.*;

public class ControlsLayout extends FrameLayout
{
	public ControlsLayout(Context ctx) {
		super(ctx);
	}
	
	public ControlsLayout(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
	}
	
	public void loadLayout(CustomControls controlLayout) {
		for (ControlButton button : controlLayout.button) {
			addView(new ControlView(getContext(), button));
		}
	}
	
	public void setCanMove(boolean z) {
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (v instanceof ControlView) {
				((ControlView) v).setCanMove(z);
			}
		}
	}
}
