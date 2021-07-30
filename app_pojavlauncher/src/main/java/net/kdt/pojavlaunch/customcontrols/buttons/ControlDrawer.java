package net.kdt.pojavlaunch.customcontrols.buttons;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.customcontrols.ControlData;
import net.kdt.pojavlaunch.customcontrols.ControlDrawerData;
import net.kdt.pojavlaunch.customcontrols.ControlLayout;

import java.util.ArrayList;



@SuppressLint("ViewConstructor")
public class ControlDrawer extends ControlButton {


    public ArrayList<ControlSubButton> buttons;
    public ControlDrawerData drawerData;
    public ControlLayout mLayout;
    public boolean areButtonsVisible;


    public ControlDrawer(ControlLayout layout, ControlDrawerData drawerData) {
        super(layout, drawerData.properties);

        buttons = new ArrayList<>(drawerData.buttonProperties.size());
        mLayout = layout;
        this.drawerData = drawerData;
        areButtonsVisible = layout.getModifiable();
    }


    public void addButton(ControlData properties){
        addButton(new ControlSubButton(mLayout, properties, this));
    }

    public void addButton(ControlSubButton button){
        buttons.add(button);
        setControlButtonVisibility(button, mModifiable || areButtonsVisible);
        syncButtons();
    }

    private void setControlButtonVisibility(ControlButton button, boolean isVisible){
        button.setVisible(isVisible);
    }

    private void switchButtonVisibility(){
        areButtonsVisible = !areButtonsVisible;
        for(ControlButton button : buttons){
            button.setVisible(areButtonsVisible);
        }
    }

    //Syncing stuff
    private void alignButtons(){

        if(buttons == null) return;
        for(int i=0; i < buttons.size(); ++i){
            switch (drawerData.orientation){
                case RIGHT:
                    buttons.get(i).setX(drawerData.properties.x + (drawerData.properties.getWidth() + Tools.dpToPx(2))*(i+1) );
                    buttons.get(i).setY(drawerData.properties.y);
                    break;

                case LEFT:
                    buttons.get(i).setX(drawerData.properties.x - (drawerData.properties.getWidth() + Tools.dpToPx(2))*(i+1) );
                    buttons.get(i).setY(drawerData.properties.y);
                    break;

                case UP:
                    buttons.get(i).setY(drawerData.properties.y - (drawerData.properties.getHeight() + Tools.dpToPx(2))*(i+1) );
                    buttons.get(i).setX(drawerData.properties.x);
                    break;

                case DOWN:
                    buttons.get(i).setY(drawerData.properties.y + (drawerData.properties.getHeight() + Tools.dpToPx(2))*(i+1) );
                    buttons.get(i).setX(drawerData.properties.x);
                    break;
            }
            buttons.get(i).updateProperties();
        }
    }


    private void resizeButtons(){
        if (buttons == null) return;
        for(ControlSubButton subButton : buttons){
            subButton.mProperties.setWidth(mProperties.getWidth());
            subButton.mProperties.setHeight(mProperties.getHeight());

            subButton.updateProperties();
        }
    }

    public void syncButtons(){
        alignButtons();
        resizeButtons();
    }

    @Override
    public ControlData preProcessProperties(ControlData properties, ControlLayout layout) {
        ControlData data = super.preProcessProperties(properties, layout);
        data.isHideable = true;
        return data;
    }

    @Override
    public void setVisible(boolean isVisible) {
        //TODO replicate changes to his children ?
        setVisibility(isVisible ? VISIBLE : GONE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!mModifiable){
            switch (event.getActionMasked()){
                case MotionEvent.ACTION_UP: // 1
                case MotionEvent.ACTION_POINTER_UP: // 6
                    switchButtonVisibility();
                    break;
            }
            return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        alignButtons();
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        alignButtons();
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        syncButtons();
    }

    //Getters
    public ControlDrawerData getDrawerData() {
        return drawerData;
    }
}
