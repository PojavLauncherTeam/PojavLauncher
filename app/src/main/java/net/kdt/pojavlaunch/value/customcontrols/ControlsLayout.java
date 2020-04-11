package net.kdt.pojavlaunch.value.customcontrols;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.view.*;
import com.google.gson.*;
import net.kdt.pojavlaunch.*;

public class ControlsLayout extends FrameLayout
{
	private boolean mCanMove;
	private CustomControls mLayout;
	public ControlsLayout(Context ctx) {
		super(ctx);
	}
	
	public ControlsLayout(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
	}
	
	public void loadLayout(CustomControls controlLayout) {
		mLayout = controlLayout;
		for (ControlButton button : controlLayout.button) {
			ControlView view = new ControlView(getContext(), button);
			view.setCanMove(mCanMove);
			view.setLayoutParams(new LayoutParams((int) Tools.dpToPx(getContext(), 50), (int) Tools.dpToPx(getContext(), 50)));
			addView(view);
		}
	}
	
	public void refreshLayout() {
		removeAllViews();
		loadLayout(mLayout);
	}
	
	public void saveLayout(String path) throws Exception {
		Tools.write(path, new Gson().toJson(mLayout));
	}
	
	public void setCanMove(boolean z) {
		mCanMove = z;
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (v instanceof ControlView) {
				((ControlView) v).setCanMove(z);
			}
		}
	}
}
