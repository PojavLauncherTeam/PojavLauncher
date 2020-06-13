package net.kdt.pojavlaunch.value.customcontrols;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.view.*;
import com.google.gson.*;
import net.kdt.pojavlaunch.*;
import android.support.v7.app.*;

public class ControlsLayout extends FrameLayout
{
	private boolean mCanModify;
	private CustomControls mLayout;
	private CustomControlsActivity mActivity;
	private boolean mControlVisible = false;
	public ControlsLayout(Context ctx) {
		super(ctx);
	}

	public ControlsLayout(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
	}

	public void hideAllHandleViews() {
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			if (view instanceof ControlView) {
				((ControlView) view).getHandleView().hide();
			}
		}
	}

	public boolean loadLayout(String jsonPath) {
		try {
			return loadLayout(new Gson().fromJson(Tools.read(jsonPath), CustomControls.class));
		} catch (Throwable th) {
			Tools.showError(getContext(), th);
		}
		return false;
	}

	public boolean loadLayout(CustomControls controlLayout) {
		try {
			mLayout = controlLayout;
			removeAllViews();
			for (ControlButton button : controlLayout.button) {
				addControlView(button);
			}

			setModified(false);
			
			return true;
		} catch (Throwable th) {
			Tools.showError(getContext(), th);
		}
		return false;
	}

	public void addControlButton(ControlButton controlButton) {
		mLayout.button.add(controlButton);
		addControlView(controlButton);
	}

	private void addControlView(ControlButton controlButton) {
		final ControlView view = new ControlView(getContext(), controlButton);
		view.setModifiable(mCanModify);
		addView(view);

		setModified(true);
	}

	public void removeControlButton(ControlView controlButton) {
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
	
	/*
	 * Setting non-special buttons listener
	 *
	 * @param listener, the touch listener to initialize.
	 */
	public void setNonspecBtnsListener(View.OnTouchListener listener) {
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			if (view instanceof ControlView && ((ControlView) view).getProperties().keycode < 0) {
				ControlView currView = ((ControlView) view);
				currView.getProperties().specialButtonListener = listener;
				currView.setOnTouchListener(listener);
			}
		}
	}
	
	public void toggleControlVisible() {
		if (mCanModify) return; // Not using on custom controls activity
		
		mControlVisible = !mControlVisible;
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			if (view instanceof ControlView && ((ControlView) view).getProperties().keycode != ControlButton.SPECIALBTN_TOGGLECTRL) {
				((ControlView) view).setVisibility(mControlVisible ? (((ControlView) view).getProperties().hidden ? View.INVISIBLE : View.VISIBLE) : View.GONE);
			}
		}
	}
	
	public void setModifiable(boolean z) {
		mCanModify = z;
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (v instanceof ControlView) {
				ControlView cv = ((ControlView) v);
				cv.setModifiable(z);
				// cv.setVisibility(cv.getProperties().hidden ? View.INVISIBLE : View.VISIBLE);
			}
		}
	}

	private void setModified(boolean z) {
		if (mActivity != null) mActivity.isModified = z;
	}
}
