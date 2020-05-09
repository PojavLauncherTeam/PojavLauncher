package net.kdt.pojavlaunch.value.customcontrols;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.view.*;
import com.google.gson.*;
import net.kdt.pojavlaunch.*;
import android.support.v7.app.*;
import android.androidVNC.*;
import java.util.*;

public class ControlsLayout extends FrameLayout
{
	private boolean mCanModify;
	private CustomControls mLayout;
	private CustomControlsActivity mActivity;
	private List<ControlView> mControlViewList;
	
	public ControlsLayout(Context ctx) {
		this(ctx, null);
	}
	
	public ControlsLayout(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		mControlViewList = new ArrayList<ControlView>();
	}

	@Override
	public void addView(View view) {
		super.addView(view);
		if (view instanceof ControlView) mControlViewList.add((ControlView) view);
	}
	
	@Override
	public void removeView(View view) {
		super.removeView(view);
		if (view instanceof ControlView) mControlViewList.remove((ControlView) view);
	}
	
	public ControlView[] getControlViewArray() {
		return mControlViewList.toArray(new ControlView[0]);
	}

	public ControlView[] getSpecialControlViewArray() {
		List<ControlView> specialViewList = new ArrayList<ControlView>();
		for (ControlView view : getControlViewArray()) {
			if (view.getProperties().keycode < 0) {
				specialViewList.add(view);
			}
		}
		
		return specialViewList.toArray(new ControlView[0]);
	}
	
	public void hideAllHandleViews() {
		for (ControlView view : getControlViewArray()) {
			view.getHandleView().hide();
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
		
		// Safety remove only ControlView views
		for (ControlView view : getControlViewArray()) {
			removeView(view);
		}
		
		for (ControlButton button : controlLayout.button) {
			addControlView(button);
		}

		setModified(false);
	}

	public void setControlVisible(boolean visible) {
		for (ControlView ctrlView : getControlViewArray()) {
			if (ctrlView.getProperties().keycode == ControlButton.SPECIALBTN_TOGGLECTRL) continue;
			ctrlView.setVisibility(visible ? (
				(ctrlView.getProperties().hidden && !mCanModify) ?
					View.INVISIBLE :
					View.VISIBLE
				) :
				View.GONE
			);
		}
	}
	
	public void setupKeyEvent(final ControlListener listener) {
		for (final ControlView ctrlView : getControlViewArray()) {
			if (ctrlView.getProperties().keycode < 0) continue;
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
	
	public void addControlButton(ControlButton controlButton) {
		mLayout.button.add(controlButton);
		addControlView(controlButton);
	}
	
	private void addControlView(ControlButton controlButton) {
		final ControlView view = new ControlView(getContext(), controlButton, mCanModify);
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
		for (final ControlView view : getControlViewArray()) {
			view.setModifiable(z);
		}
	}
	
	private void setModified(boolean z) {
		if (mActivity != null && mCanModify) mActivity.isModified = z;
	}
	
	public static interface ControlListener {
		public void onKey(MetaKeyBase vncKey, boolean down);
	}
}
