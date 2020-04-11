package net.kdt.pojavlaunch.value.customcontrols;
import android.widget.*;
import android.content.*;
import android.util.*;

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
}
