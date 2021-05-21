package net.kdt.pojavlaunch.customcontrols;
import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
        }
        removeAllButtons();
		if(mLayout != null) {
			mLayout.mControlDataList = null;
			mLayout = null;
		}

        System.gc();

        // Cleanup buttons only when input layout is null
        if (controlLayout == null) return;
        
		mLayout = controlLayout;

		//CONTROL BUTTON
		for (ControlData button : controlLayout.mControlDataList) {
            button.isHideable = button.keycodes[0] != ControlData.SPECIALBTN_TOGGLECTRL && button.keycodes[0] != ControlData.SPECIALBTN_VIRTUALMOUSE;
            button.setWidth(button.getWidth() / controlLayout.scaledAt * LauncherPreferences.PREF_BUTTONSIZE);
            button.setHeight(button.getHeight() / controlLayout.scaledAt * LauncherPreferences.PREF_BUTTONSIZE);
            if (!button.isDynamicBtn) {
                button.dynamicX = button.x / CallbackBridge.physicalWidth + " * ${screen_width}";
                button.dynamicY = button.y / CallbackBridge.physicalHeight + " * ${screen_height}";
            }
            button.update();
			addControlView(button);
		}

		//CONTROL DRAWER
		for(ControlDrawerData drawerData : controlLayout.mDrawerDataList){
			drawerData.properties.isHideable = true;
			drawerData.properties.setWidth(drawerData.properties.getWidth() / controlLayout.scaledAt * LauncherPreferences.PREF_BUTTONSIZE);
			drawerData.properties.setHeight(drawerData.properties.getHeight() / controlLayout.scaledAt * LauncherPreferences.PREF_BUTTONSIZE);
			if (!drawerData.properties.isDynamicBtn) {
				drawerData.properties.dynamicX = drawerData.properties.x / CallbackBridge.physicalWidth + " * ${screen_width}";
				drawerData.properties.dynamicY = drawerData.properties.y / CallbackBridge.physicalHeight + " * ${screen_height}";
			}

			ControlDrawer drawer = addDrawerView(drawerData);

			//CONTROL SUB BUTTON
			for (ControlData subButton : drawerData.buttonProperties){
				subButton.isHideable = subButton.keycodes[0] != ControlData.SPECIALBTN_TOGGLECTRL && subButton.keycodes[0] != ControlData.SPECIALBTN_VIRTUALMOUSE;
				subButton.setWidth(subButton.getWidth() / controlLayout.scaledAt * LauncherPreferences.PREF_BUTTONSIZE);
				subButton.setHeight(subButton.getHeight() / controlLayout.scaledAt * LauncherPreferences.PREF_BUTTONSIZE);
				if (!subButton.isDynamicBtn) {
					subButton.dynamicX = subButton.x / CallbackBridge.physicalWidth + " * ${screen_width}";
					subButton.dynamicY = subButton.y / CallbackBridge.physicalHeight + " * ${screen_height}";
				}
				subButton.update();
				addSubView(drawer, subButton);
			}



		}


        mLayout.scaledAt = LauncherPreferences.PREF_BUTTONSIZE;

		setModified(false);
	} // loadLayout

	//CONTROL BUTTON
	public void addControlButton(ControlData controlButton) {
		mLayout.mControlDataList.add(controlButton);
		addControlView(controlButton);
	}

	private void addControlView(ControlData controlButton) {
		final ControlButton view = new ControlButton(this, controlButton);
		view.setModifiable(mModifiable);
        if (!mModifiable) {
            view.setAlpha(view.getProperties().opacity);
			view.setFocusable(false);
			view.setFocusableInTouchMode(false);
        }
		addView(view);

		setModified(true);
	}

	// CONTROL DRAWER
	public void addDrawer(ControlDrawerData drawerData){
		mLayout.mDrawerDataList.add(drawerData);
		addDrawerView();
	}

	private void addDrawerView(){
		addDrawerView(null);
	}

	private ControlDrawer addDrawerView(ControlDrawerData drawerData){

		final ControlDrawer view = new ControlDrawer(this,drawerData == null ? mLayout.mDrawerDataList.get(mLayout.mDrawerDataList.size()-1) : drawerData);
		view.setModifiable(mModifiable);
		if (!mModifiable) {
			view.setAlpha(view.getProperties().opacity);
			view.setFocusable(false);
			view.setFocusableInTouchMode(false);
		}
		addView(view);

		setModified(true);
		return view;
	}

	//CONTROL SUB-BUTTON
	public void addSubButton(ControlDrawer drawer, ControlData controlButton){
		//Yep there isn't much here
		drawer.getDrawerData().buttonProperties.add(controlButton);
		addSubView(drawer, drawer.getDrawerData().buttonProperties.get(drawer.getDrawerData().buttonProperties.size()-1 ));
	}

	public void addSubView(ControlDrawer drawer, ControlData controlButton){
		final ControlSubButton view = new ControlSubButton(this, controlButton, drawer);
		view.setModifiable(mModifiable);
		if (!mModifiable) {
			view.setAlpha(view.getProperties().opacity);
			view.setFocusable(false);
			view.setFocusableInTouchMode(false);
		}
		drawer.addButton(view);
		addView(view);

		setModified(true);
	}

    private void removeAllButtons() {
		List<View> viewList = new ArrayList<>();
		View v;
		for(int i = 0; i < getChildCount(); i++) {
			v = getChildAt(i);
			if(v instanceof ControlButton) viewList.add(v);
		}
		v = null;
		for(View v2 : viewList) {
			removeView(v2);
		}
		viewList = null;
		System.gc();
		//i wanna be sure that all the removed Views will be removed after a reload
		//because if frames will slowly go down after many control changes it will be warm and bad
	}

	public void removeControlButton(ControlButton controlButton) {
		mLayout.mControlDataList.remove(controlButton.getProperties());
		controlButton.setVisibility(View.GONE);
		removeView(controlButton);

		setModified(true);
	}

	public void removeControlDrawer(ControlDrawer controlDrawer){
		mLayout.mDrawerDataList.remove(controlDrawer.getDrawerData());
		controlDrawer.setVisibility(GONE);
		removeView(controlDrawer);

		setModified(true);
	}

	public void removeControlSubButton(ControlSubButton subButton){
		subButton.parentDrawer.drawerData.buttonProperties.remove(subButton.getProperties());
		subButton.parentDrawer.buttons.remove(subButton);

		subButton.setVisibility(GONE);
		removeView(subButton);
	}

	public void saveLayout(String path) throws Exception {
		mLayout.save(path);
		setModified(false);
	}

	public void setActivity(CustomControlsActivity activity) {
		mActivity = activity;
	}

	public void toggleControlVisible(){
		mControlVisible = !mControlVisible;
		setControlVisible(mControlVisible);
	}

	public void setControlVisible(boolean isVisible) {
		if (mModifiable) return; // Not using on custom controls activity

		mControlVisible = isVisible;
		int visibilityState = isVisible ? View.VISIBLE : View.GONE;

		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);

			if(view instanceof ControlSubButton){
				view.setVisibility(isVisible ? (((ControlSubButton)view).parentDrawer.areButtonsVisible ? VISIBLE : GONE) : View.GONE);
				continue;
			}

			if(view instanceof ControlDrawer){
				view.setVisibility(visibilityState);
				continue;
			}

			if (view instanceof ControlButton && ((ControlButton) view).getProperties().isHideable) {
				view.setVisibility(visibilityState);
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
				    cv.setAlpha(cv.getProperties().opacity);
                }
			}
		}
	}

	public boolean getModifiable(){
		return mModifiable;
	}

	protected void setModified(boolean z) {
		if (mActivity != null) mActivity.isModified = z;
	}
}
