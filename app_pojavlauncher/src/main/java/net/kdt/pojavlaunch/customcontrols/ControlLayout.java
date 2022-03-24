package net.kdt.pojavlaunch.customcontrols;
import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.customcontrols.buttons.ControlButton;
import net.kdt.pojavlaunch.customcontrols.buttons.ControlDrawer;
import net.kdt.pojavlaunch.customcontrols.buttons.ControlSubButton;
import net.kdt.pojavlaunch.customcontrols.handleview.HandleView;
import net.kdt.pojavlaunch.prefs.*;

public class ControlLayout extends FrameLayout {
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
		for(ControlButton button : getButtonChildren()){
			HandleView hv = button.getHandleView();
			if(hv != null) hv.hide();
		}
	}

	public void loadLayout(String jsonPath) throws IOException, JsonSyntaxException {
		CustomControls layout = LayoutConverter.loadAndConvertIfNecessary(jsonPath);
		if(layout != null) {
			loadLayout(layout);
		}else{
			throw new IOException("Unsupported control layout version");
		}
	}

	public void loadLayout(CustomControls controlLayout) {
        if (mModifiable)
            hideAllHandleViews();

        removeAllButtons();
		if(mLayout != null) {
			mLayout.mControlDataList = null;
			mLayout = null;
		}

        System.gc();
		mapTable.clear();

        // Cleanup buttons only when input layout is null
        if (controlLayout == null) return;
        
		mLayout = controlLayout;

		//CONTROL BUTTON
		for (ControlData button : controlLayout.mControlDataList) {
			addControlView(button);
		}

		//CONTROL DRAWER
		for(ControlDrawerData drawerData : controlLayout.mDrawerDataList){
			ControlDrawer drawer = addDrawerView(drawerData);
			if(mModifiable) drawer.areButtonsVisible = true;



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
		//CONTROL SUB BUTTON
		for (ControlData subButton : view.getDrawerData().buttonProperties) {
			addSubView(view, subButton);
		}

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
		}else{
			view.setVisible(drawer.areButtonsVisible);
		}

		drawer.addButton(view);
		addView(view);

		setModified(true);
	}

    private void removeAllButtons() {
		for(View v : getButtonChildren()){
			removeView(v);
		}

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
		for(ControlSubButton subButton : controlDrawer.buttons){
			subButton.setVisibility(GONE);
			removeView(subButton);
		}
		mLayout.mDrawerDataList.remove(controlDrawer.getDrawerData());
		controlDrawer.setVisibility(GONE);
		removeView(controlDrawer);

		setModified(true);
	}

	public void removeControlSubButton(ControlSubButton subButton){
		subButton.parentDrawer.drawerData.buttonProperties.remove(subButton.getProperties());
		subButton.parentDrawer.buttons.remove(subButton);

		subButton.parentDrawer.syncButtons();

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

	public float getLayoutScale(){
		return mLayout.scaledAt;
	}

	public void setControlVisible(boolean isVisible) {
		if (mModifiable) return; // Not using on custom controls activity

		mControlVisible = isVisible;
		for(ControlButton button : getButtonChildren()){
			button.setVisible(isVisible);
		}
	}
	
	public void setModifiable(boolean isModifiable) {
		mModifiable = isModifiable;
		for(ControlButton button : getButtonChildren()){
			button.setModifiable(isModifiable);
			if (!isModifiable)
				button.setAlpha(button.getProperties().opacity);
		}
	}

	public boolean getModifiable(){
		return mModifiable;
	}

	public void setModified(boolean isModified) {
		if (mActivity != null) mActivity.isModified = isModified;

	}

	public ArrayList<ControlButton> getButtonChildren(){
		ArrayList<ControlButton> children = new ArrayList<>();
		for(int i=0; i<getChildCount(); ++i){
			View v = getChildAt(i);
			if(v instanceof ControlButton)
				children.add(((ControlButton) v));
		}
		return children;
	}

	public void refreshControlButtonPositions(){
		for(ControlButton button : getButtonChildren()){
			button.setDynamicX(button.getProperties().dynamicX);
			button.setDynamicY(button.getProperties().dynamicY);
		}
	}

	HashMap<View, ControlButton> mapTable = new HashMap<>();
	//While this is called onTouch, this should only be called from a ControlButton.
	public boolean onTouch(View v, MotionEvent ev) {
		ControlButton lastControlButton = mapTable.get(v);

		//Check if the action is cancelling, reset the lastControl button associated to the view
		if(ev.getActionMasked() == MotionEvent.ACTION_UP || ev.getActionMasked() == MotionEvent.ACTION_CANCEL){
			if(lastControlButton != null) lastControlButton.sendKeyPresses(false);
			mapTable.put(v, null);
			return true;
		}

		if(ev.getActionMasked() != MotionEvent.ACTION_MOVE) return false;

		//Optimization pass to avoid looking at all children again
		if(lastControlButton != null){
			if(	ev.getRawX() > lastControlButton.getX() && ev.getRawX() < lastControlButton.getX() + lastControlButton.getWidth() &&
				ev.getRawY() > lastControlButton.getY() && ev.getRawY() < lastControlButton.getY() + lastControlButton.getHeight()){
				return true;
			}
		}

		//Release last keys
		if (lastControlButton != null) lastControlButton.sendKeyPresses(false);
		mapTable.put(v, null);

		//Look for another SWIPEABLE button
		for(ControlButton button : getButtonChildren()){
			if(!button.getProperties().isSwipeable) continue;

			if(	ev.getRawX() > button.getX() && ev.getRawX() < button.getX() + button.getWidth() &&
				ev.getRawY() > button.getY() && ev.getRawY() < button.getY() + button.getHeight()){

				//Press the new key
				if(!button.equals(lastControlButton)){
					button.sendKeyPresses(true);

					mapTable.put(v, button);
				}
				return true;
			}
		}
		return false;
	}
}
