package net.kdt.pojavlaunch.customcontrols;
import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import java.io.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.prefs.*;
import org.lwjgl.glfw.*;

public class ControlLayout extends FrameLayout
{
	protected CustomControls mLayout;
	private boolean mModifiable;
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

	public void loadLayout(String jsonPath) throws IOException, JsonSyntaxException {
		loadLayout(Tools.GLOBAL_GSON.fromJson(Tools.read(jsonPath), CustomControls.class));
	}

	public void loadLayout(CustomControls controlLayout) {
        if (mModifiable) {
            hideAllHandleViews();
            removeAllViews();
        }

		mLayout = controlLayout;
        
		for (ControlData button : controlLayout.mControlDataList) {
            button.isHideable = button.keycode != ControlData.SPECIALBTN_TOGGLECTRL && button.keycode != ControlData.SPECIALBTN_VIRTUALMOUSE;
            button.width = button.width / controlLayout.scaledAt * LauncherPreferences.PREF_BUTTONSIZE;
            button.height = button.height / controlLayout.scaledAt * LauncherPreferences.PREF_BUTTONSIZE;
            if (!button.isDynamicBtn) {
                button.dynamicX = Float.toString(button.x / CallbackBridge.physicalWidth) + " * ${screen_width}";
                button.dynamicY = Float.toString(button.y / CallbackBridge.physicalHeight) + " * ${screen_height}";
            }
            button.update();
			addControlView(button);
		}
        mLayout.scaledAt = LauncherPreferences.PREF_BUTTONSIZE;

		setModified(false);
	}

	public void addControlButton(ControlData controlButton) {
		mLayout.mControlDataList.add(controlButton);
		addControlView(controlButton);
	}

	private void addControlView(ControlData controlButton) {
		final ControlButton view = new ControlButton(this, controlButton);
		view.setModifiable(mModifiable);
        if (!mModifiable) {
            view.setAlpha(1f - view.getProperties().transparency / 100f);
			view.setFocusable(false);
			view.setFocusableInTouchMode(false);
        }
		addView(view);

		setModified(true);
	}

	public void removeControlButton(ControlButton controlButton) {
		mLayout.mControlDataList.remove(controlButton.getProperties());
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
		if (mModifiable) return; // Not using on custom controls activity
		
		mControlVisible = !mControlVisible;
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			if (view instanceof ControlButton && ((ControlButton) view).getProperties().isHideable) {
				((ControlButton) view).setVisibility(mControlVisible ? View.VISIBLE : View.GONE);
			}
		}
	}
	
	public void setModifiable(boolean z) {
		mModifiable = z;
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (v instanceof ControlButton) {
				ControlButton cv = ((ControlButton) v);
				cv.setModifiable(z);
                if (!z) {
				    cv.setAlpha(1f - cv.getProperties().transparency / 100f);
                }
			}
		}
	}

	protected void setModified(boolean z) {
		if (mActivity != null) mActivity.isModified = z;
	}
}
