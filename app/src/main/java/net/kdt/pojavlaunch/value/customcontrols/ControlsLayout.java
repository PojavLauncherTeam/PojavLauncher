package net.kdt.pojavlaunch.value.customcontrols;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.view.*;
import com.google.gson.*;
import net.kdt.pojavlaunch.*;
import android.support.v7.app.*;
import android.androidVNC.*;

public class ControlsLayout extends FrameLayout
{
	private boolean mCanModify;
	private CustomControls mLayout;
	private CustomControlsActivity mActivity;
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
		for (ControlButton button : controlLayout.button) {
			if (button.keycode < 0) {
				// Set special button
			}
			addControlView(button);
		}

		setModified(false);
	}

	public void setupKeyEvent(final ControlListener listener) {
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (v instanceof ControlView) {
				final ControlView ctrlView = (ControlView) v;
				ctrlView.setOnTouchListener(new View.OnTouchListener(){

					@Override
					public boolean onTouch(View view, MotionEvent event)
					{
						boolean isDown = false;
						switch (event.getActionMasked()) {
							case MotionEvent.ACTION_DOWN: isDown = true; break;
							case MotionEvent.ACTION_UP: isDown = false; break;
						}
						
						for (int i = 0; i < MetaKeyBean.keysByKeyCode.size(); i++) {
							MetaKeyBase key = MetaKeyBean.keysByKeyCode.valueAt(i);
							if (ctrlView.getProperties().keycode == key.keyEvent) {
								listener.onKey(key, isDown);
							}
						}
						return false;
					}
				});
			}
		}
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
		Tools.write(path, new Gson().toJson(mLayout));
		setModified(false);
	}
	
	public void setActivity(CustomControlsActivity activity) {
		mActivity = activity;
	}
	
	public void setModifiable(boolean z) {
		mCanModify = z;
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (v instanceof ControlView) {
				((ControlView) v).setModifiable(z);
			}
		}
	}
	
	private void setModified(boolean z) {
		if (mActivity != null) mActivity.isModified = z;
	}
	
	public static interface ControlListener {
		public void onKey(MetaKeyBase vncKey, boolean down);
	}
}
