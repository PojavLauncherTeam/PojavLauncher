package net.kdt.pojavlaunch.customcontrols;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.view.*;
import com.google.gson.*;
import net.kdt.pojavlaunch.*;
import android.support.v7.app.*;

public class ControlLayout extends FrameLayout
{
	private boolean mCanModify;
	private CustomControls mLayout;
	private CustomControlsActivity mActivity;
	private boolean mControlVisible = false;
	public ControlLayout(Context ctx) {
		super(ctx);
	}

	public ControlLayout(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
	}

	public void hideAllHandleViews() {
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			if (view instanceof ControlButton) {
				((ControlButton) view).getHandleView().hide();
			}
		}
	}

	public void loadLayout(String jsonPath) {
		try {
			loadLayout(new Gson().fromJson(Tools.read(jsonPath), CustomControls.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadLayout(CustomControls controlLayout) {
		mLayout = controlLayout;
		removeAllViews();
		for (ControlData button : controlLayout.button) {
			addControlView(button);
		}

		setModified(false);
	}

	public void addControlButton(ControlData controlButton) {
		mLayout.button.add(controlButton);
		addControlView(controlButton);
	}

	private void addControlView(ControlData controlButton) {
		final ControlButton view = new ControlButton(getContext(), controlButton);
		view.setModifiable(mCanModify);
		addView(view);

		setModified(true);
	}

	public void removeControlButton(ControlButton controlButton) {
		mLayout.button.remove(controlButton.getProperties());
		controlButton.setVisibility(View.GONE);
		removeView(controlButton);

		setModified(true);
	}

	public void saveLayout(String path) throws Exception {
		mLayout.save(path);
		setModified(false);
	}

	public void setActivity(CustomControlsActivity activity) {
		mActivity = activity;
	}
	
	public void toggleControlVisible() {
		if (mCanModify) return; // Not using on custom controls activity
		
		mControlVisible = !mControlVisible;
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			if (view instanceof ControlButton && ((ControlButton) view).getProperties().keycode != ControlData.SPECIALBTN_TOGGLECTRL) {
				((ControlButton) view).setVisibility(mControlVisible ? (((ControlButton) view).getProperties().hidden ? View.INVISIBLE : View.VISIBLE) : View.GONE);
			}
		}
	}
	
	public void setModifiable(boolean z) {
		mCanModify = z;
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (v instanceof ControlButton) {
				ControlButton cv = ((ControlButton) v);
				cv.setModifiable(z);
				// cv.setVisibility(cv.getProperties().hidden ? View.INVISIBLE : View.VISIBLE);
			}
		}
	}

	private void setModified(boolean z) {
		if (mActivity != null) mActivity.isModified = z;
	}
}
