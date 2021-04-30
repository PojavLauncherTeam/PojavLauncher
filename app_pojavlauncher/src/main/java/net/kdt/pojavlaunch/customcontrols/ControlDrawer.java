package net.kdt.pojavlaunch.customcontrols;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;



public class ControlDrawer extends ControlButton {


    public ArrayList<ControlSubButton> buttons;
    public ControlDrawerData drawerData;
    public ControlLayout mLayout;
    private boolean areButtonsVisible = false;


    public ControlDrawer(ControlLayout layout, ControlDrawerData drawerData) {
        super(layout, drawerData.properties);

        buttons = new ArrayList<>(/*drawerData.buttonProperties.size()*/);
        mLayout = layout;
        this.drawerData = drawerData;


        //Filter unwanted values before instantiating the button
        for(int i=0; i < buttons.size(); ++i){
            drawerData.buttonProperties.set(i, filterProperties(drawerData.buttonProperties.get(i)));

            addButton(drawerData.buttonProperties.get(i));
        }

    }



    private ControlData filterProperties(ControlData properties){
        properties.isDynamicBtn = false;
        properties.width = drawerData.properties.width;
        properties.height = drawerData.properties.height;

        return properties;
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
        button.setVisibility(isVisible ? VISIBLE : GONE);
    }

    private void switchButtonVisibility(){
        areButtonsVisible = !areButtonsVisible;
        int visibility = areButtonsVisible ? View.VISIBLE : View.GONE;
        for(ControlButton button : buttons){
            button.setVisibility(visibility);
        }
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

        boolean isHandled = super.onTouchEvent(event);
        //syncButtons();
        return isHandled;
    }

    //Syncing stuff
    private void alignButtons(){

        if(buttons == null) return;
        for(int i=0; i < buttons.size(); ++i){
            switch (drawerData.orientation){
                case RIGHT:
                    buttons.get(i).setTranslationX( (drawerData.properties.x + drawerData.properties.width) + drawerData.properties.width*i );
                    buttons.get(i).setTranslationY(drawerData.properties.y);
                    break;

                case LEFT:
                    buttons.get(i).setTranslationX( (drawerData.properties.x - drawerData.properties.width) - drawerData.properties.width*i );
                    buttons.get(i).setTranslationY(drawerData.properties.y);
                    break;

                case UP:
                    buttons.get(i).setTranslationY( (drawerData.properties.y - drawerData.properties.height) - drawerData.properties.height*i );
                    buttons.get(i).setTranslationX(drawerData.properties.x);
                    break;

                case DOWN:
                    buttons.get(i).setTranslationY( (drawerData.properties.y + drawerData.properties.height) + drawerData.properties.height*i );
                    buttons.get(i).setTranslationX(drawerData.properties.x);
                    break;
            }
            buttons.get(i).updateProperties();
        }
    }


    private void resizeButtons(){
        if (buttons == null) return;
        for(ControlSubButton subButton : buttons){
            subButton.mProperties.width = mProperties.width;
            subButton.mProperties.height = mProperties.height;

            subButton.updateProperties();
        }
    }

    private void syncButtons(){
        alignButtons();
        resizeButtons();
    }

    @Override
    public void setTranslationX(float x) {
        super.setTranslationX(x);
        alignButtons();
    }

    @Override
    public void setTranslationY(float y) {
        super.setTranslationY(y);
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
