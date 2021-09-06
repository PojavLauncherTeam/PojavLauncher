package net.kdt.pojavlaunch.customcontrols.buttons;

import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import net.kdt.pojavlaunch.SingleTapConfirm;
import net.kdt.pojavlaunch.customcontrols.ControlData;
import net.kdt.pojavlaunch.customcontrols.ControlDrawerData;
import net.kdt.pojavlaunch.customcontrols.ControlLayout;

public class ControlSubButton extends ControlButton {

    public ControlDrawer parentDrawer;

    public ControlSubButton(ControlLayout layout, ControlData properties, ControlDrawer parentDrawer) {
        super(layout, properties);
        this.parentDrawer = parentDrawer;



        filterProperties();
    }

    private void filterProperties(){
        mProperties.setHeight(parentDrawer.getProperties().getHeight());
        mProperties.setWidth(parentDrawer.getProperties().getWidth());
        mProperties.isDynamicBtn = false;

        setProperties(mProperties, false);
    }

    @Override
    public void setVisible(boolean isVisible) {
        setVisibility(isVisible ? (parentDrawer.areButtonsVisible ? VISIBLE : GONE) : (!mProperties.isHideable && parentDrawer.getVisibility() == GONE) ? VISIBLE : View.GONE);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if(parentDrawer != null){
            params.width = (int)parentDrawer.mProperties.getWidth();
            params.height = (int)parentDrawer.mProperties.getHeight();
        }
        super.setLayoutParams(params);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!mModifiable || parentDrawer.drawerData.orientation == ControlDrawerData.Orientation.FREE){
            return super.onTouchEvent(event);
        }

        if(mGestureDetector == null) mGestureDetector = new GestureDetector(getContext(), new SingleTapConfirm());

        if (mGestureDetector.onTouchEvent(event)) {
            mCanTriggerLongClick = true;
            onLongClick(this);
        }
        return true;
    }
}
