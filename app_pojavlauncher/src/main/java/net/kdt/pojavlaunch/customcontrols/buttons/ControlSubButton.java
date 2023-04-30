package net.kdt.pojavlaunch.customcontrols.buttons;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import net.kdt.pojavlaunch.customcontrols.ControlData;
import net.kdt.pojavlaunch.customcontrols.ControlDrawerData;
import net.kdt.pojavlaunch.customcontrols.ControlLayout;

@SuppressLint("ViewConstructor")
public class ControlSubButton extends ControlButton {

    public final ControlDrawer parentDrawer;

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!getControlLayoutParent().getModifiable() || parentDrawer.drawerData.orientation == ControlDrawerData.Orientation.FREE){
            return super.onTouchEvent(event);
        }

        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            //mCanTriggerLongClick = true;
            onLongClick(this);
        }
        return true;
    }

    @Override
    public void cloneButton() {
        ControlData cloneData = new ControlData(getProperties());
        cloneData.dynamicX = "0.5 * ${screen_width}";
        cloneData.dynamicY = "0.5 * ${screen_height}";
        ((ControlLayout) getParent()).addSubButton(parentDrawer, cloneData);
    }

    @Override
    public void removeButton() {
        parentDrawer.drawerData.buttonProperties.remove(getProperties());
        parentDrawer.drawerData.buttonProperties.remove(getProperties());
        parentDrawer.buttons.remove(this);

        parentDrawer.syncButtons();

        super.removeButton();
    }

    @Override
    public void snapAndAlign(float x, float y) {
        if(parentDrawer.drawerData.orientation == ControlDrawerData.Orientation.FREE)
            super.snapAndAlign(x, y);
        // Else the button is forced into place
    }
}
