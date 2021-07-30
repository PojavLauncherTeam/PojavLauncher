package net.kdt.pojavlaunch.customcontrols.buttons;

import android.annotation.SuppressLint;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;



import androidx.core.math.MathUtils;

import net.kdt.pojavlaunch.customcontrols.ControlData;
import net.kdt.pojavlaunch.customcontrols.ControlLayout;
import net.kdt.pojavlaunch.customcontrols.handleview.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import org.lwjgl.glfw.*;

import static net.kdt.pojavlaunch.LWJGLGLFWKeycode.GLFW_KEY_UNKNOWN;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_BUTTONSIZE;

@SuppressLint("ViewConstructor")
public class ControlButton extends androidx.appcompat.widget.AppCompatButton implements OnLongClickListener
{
    private Paint mRectPaint;
    
    protected GestureDetector mGestureDetector;
    protected ControlData mProperties;
    protected SelectionEndHandleView mHandleView;

    protected boolean mModifiable = false;
    protected boolean mCanTriggerLongClick = true;

    protected boolean isToggled = false;
    protected boolean isPointerOutOfBounds = false;

    public ControlButton(ControlLayout layout, ControlData properties) {
        super(layout.getContext());
        setPadding(4, 4, 4, 4);

        setOnLongClickListener(this);

        //When a button is created, the width/height has yet to be processed to fit the scaling.
        setProperties(preProcessProperties(properties, layout));
        setModified(false);


        //For the toggle layer
        final TypedValue value = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorAccent, value, true);

        mRectPaint = new Paint();
        mRectPaint.setColor(value.data);
        mRectPaint.setAlpha(128);
    }

    public HandleView getHandleView() {
        return mHandleView;
    }

    public ControlData getProperties() {
        return mProperties;
    }

    public void setProperties(ControlData properties) {
        setProperties(properties, true);
    }

    public ControlData preProcessProperties(ControlData properties, ControlLayout layout){
        //When a button is created, properties have to be modified to fit the screen.
        //Size
        properties.setWidth(properties.getWidth() / layout.getLayoutScale() * PREF_BUTTONSIZE);
        properties.setHeight(properties.getHeight() / layout.getLayoutScale() * PREF_BUTTONSIZE);

        //Visibility
        properties.isHideable = !properties.containsKeycode(ControlData.SPECIALBTN_TOGGLECTRL) && !properties.containsKeycode(ControlData.SPECIALBTN_VIRTUALMOUSE);

        properties.update();
        return properties;
    }

    public void setProperties(ControlData properties, boolean changePos) {
        mProperties = properties;

        properties.update();

        // com.android.internal.R.string.delete
        // android.R.string.
        setText(properties.name);

        if (changePos) {
            setX(properties.insertDynamicPos(mProperties.dynamicX));
            setY(properties.insertDynamicPos(mProperties.dynamicY));
        }

        if (properties.specialButtonListener == null) {
            // A non-special button or inside custom controls screen so skip listener
        } else if (properties.specialButtonListener instanceof View.OnClickListener) {
            setOnClickListener((View.OnClickListener) properties.specialButtonListener);
        } else if (properties.specialButtonListener instanceof View.OnTouchListener) {
            setOnTouchListener((View.OnTouchListener) properties.specialButtonListener);
        } else {
            throw new IllegalArgumentException("Field " + ControlData.class.getName() + ".specialButtonListener must be View.OnClickListener or View.OnTouchListener, but was " +
                properties.specialButtonListener.getClass().getName());
        }

        setLayoutParams(new FrameLayout.LayoutParams((int) properties.getWidth(), (int) properties.getHeight() ));
    }

    public void setBackground(){
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(mProperties.bgColor);
        gd.setStroke(computeStrokeWidth(mProperties.strokeWidth), mProperties.strokeColor);
        gd.setCornerRadius(computeCornerRadius(mProperties.cornerRadius));

        setBackground(gd);
    }

    public void setModifiable(boolean isModifiable) {
        mModifiable = isModifiable;
    }

    private void setModified(boolean modified) {
        if (getParent() != null)
            ((ControlLayout) getParent()).setModified(modified);
    }



    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);

        mProperties.setWidth(params.width);
        mProperties.setHeight(params.height);
        setBackground();
        
        // Re-calculate position
        mProperties.update();
        if(!mProperties.isDynamicBtn){
            setX(getX());
            setY(getY());
        }else {
            setX(mProperties.insertDynamicPos(mProperties.dynamicX));
            setY(mProperties.insertDynamicPos(mProperties.dynamicY));
        }

        
        setModified(true);
    }

    public void setVisible(boolean isVisible){
        if(mProperties.isHideable)
            setVisibility(isVisible ? VISIBLE : GONE);
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        setModified(true);
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        setModified(true);
    }

    /**
     * Apply the dynamic equation on the x axis.
     * @param dynamicX The equation to compute the position from
     */
    public void setDynamicX(String dynamicX){
        mProperties.dynamicX = dynamicX;
        setX(mProperties.insertDynamicPos(dynamicX));
    }

    /**
     * Apply the dynamic equation on the y axis.
     * @param dynamicY The equation to compute the position from
     */
    public void setDynamicY(String dynamicY){
        mProperties.dynamicY = dynamicY;
        setY(mProperties.insertDynamicPos(dynamicY));
    }

    /**
     * Generate a dynamic equation from an absolute position, used to scale properly across devices
     * @param x The absolute position on the horizontal axis
     * @return The equation as a String
     */
    public String generateDynamicX(float x){
        if(x + (mProperties.getWidth()/2f) > CallbackBridge.physicalWidth/2f){
            return (x + mProperties.getWidth()) / CallbackBridge.physicalWidth + " * ${screen_width} - ${width}";
        }else{
            return x / CallbackBridge.physicalWidth + " * ${screen_width}";
        }
    }

    /**
     * Generate a dynamic equation from an absolute position, used to scale properly across devices
     * @param y The absolute position on the vertical axis
     * @return The equation as a String
     */
    public String generateDynamicY(float y){
        if(y + (mProperties.getHeight()/2f) > CallbackBridge.physicalHeight/2f){
            return  (y + mProperties.getHeight()) / CallbackBridge.physicalHeight + " * ${screen_height} - ${height}";
        }else{
            return y / CallbackBridge.physicalHeight + " * ${screen_height}";
        }
    }

    public void updateProperties() {
        setProperties(mProperties);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isToggled)
            canvas.drawRoundRect(0, 0, getWidth(), getHeight(), mProperties.cornerRadius, mProperties.cornerRadius, mRectPaint);
    }

    @Override
    public boolean onLongClick(View v) {
        if (mCanTriggerLongClick && mModifiable) {
            //Instantiate on need only
            if(mHandleView == null) mHandleView = new SelectionEndHandleView(this);

            if (mHandleView.isShowing()) {
                mHandleView.hide();
            } else {
                if (getParent() != null) {
                    ((ControlLayout) getParent()).hideAllHandleViews();
                }
                
                try {
                    mHandleView.show(this);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        }
        
        return mCanTriggerLongClick;
    }

    protected float downX, downY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!mModifiable){
            mCanTriggerLongClick = false;

            switch (event.getActionMasked()){
                case MotionEvent.ACTION_MOVE:
                    //Send the event to be taken as a mouse action
                    if(mProperties.passThruEnabled && CallbackBridge.isGrabbing()){
                        MinecraftGLView v = ((ControlLayout) this.getParent()).findViewById(R.id.main_game_render_view);
                        if (v != null) v.dispatchTouchEvent(event);
                    }

                    //If out of bounds
                    if(event.getX() < getLeft() || event.getX() > getRight() ||
                       event.getY() < getTop()  || event.getY() > getBottom()){
                        if(mProperties.isSwipeable && !isPointerOutOfBounds){
                            //Remove keys
                            if(!triggerToggle(event)) {
                                sendKeyPresses(event, false);
                            }
                        }
                        isPointerOutOfBounds = true;
                        ((ControlLayout) getParent()).onTouch(this, event);
                        break;
                    }

                    //Else if we now are in bounds
                    if(isPointerOutOfBounds) {
                        ((ControlLayout) getParent()).onTouch(this, event);
                        //RE-press the button
                        if(mProperties.isSwipeable && !mProperties.isToggle){
                            sendKeyPresses(event, true);
                        }
                    }
                    isPointerOutOfBounds = false;
                    break;

                case MotionEvent.ACTION_DOWN: // 0
                case MotionEvent.ACTION_POINTER_DOWN: // 5
                    if(!mProperties.isToggle){
                        sendKeyPresses(event, true);
                    }
                    break;

                case MotionEvent.ACTION_UP: // 1
                case MotionEvent.ACTION_CANCEL: // 3
                case MotionEvent.ACTION_POINTER_UP: // 6
                    if(mProperties.passThruEnabled){
                        MinecraftGLView v = ((ControlLayout) this.getParent()).findViewById(R.id.main_game_render_view);
                        if (v != null) v.dispatchTouchEvent(event);
                    }
                    if(isPointerOutOfBounds) ((ControlLayout) getParent()).onTouch(this, event);
                    isPointerOutOfBounds = false;

                    if(!triggerToggle(event)) {
                        sendKeyPresses(event, false);
                    }
                    break;

                default:
                    return false;
            }
            return true;
        }

        /* If the button can be modified/moved */
        //Instantiate the gesture detector only when needed
        if(mGestureDetector == null) mGestureDetector = new GestureDetector(getContext(), new SingleTapConfirm());

        if (mGestureDetector.onTouchEvent(event)) {
            mCanTriggerLongClick = true;
            onLongClick(this);
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_DOWN:
                mCanTriggerLongClick = true;
                downX = event.getRawX() - getX();
                downY = event.getRawY() - getY();
                break;

            case MotionEvent.ACTION_MOVE:
                mCanTriggerLongClick = false;

                if (!mProperties.isDynamicBtn) {
                    snapAndAlign(
                        MathUtils.clamp(event.getRawX() - downX, 0, CallbackBridge.physicalWidth - getWidth()),
                        MathUtils.clamp(event.getRawY() - downY, 0, CallbackBridge.physicalHeight - getHeight())
                    );
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * Passe a series of checks to determine if the ControlButton is available to be snapped on.
     *
     * @param button The button to check
     * @return whether or not the button
     */
    protected boolean canSnap(ControlButton button){
        float MIN_DISTANCE = Tools.dpToPx(8);

        if(button == this) return false;
        if(com.google.android.material.math.MathUtils.dist(
                button.getX() + button.getWidth()/2f,
                button.getY() + button.getHeight()/2f,
                getX() + getWidth()/2f,
                getY() + getHeight()/2f) > Math.max(button.getWidth()/2f + getWidth()/2f, button.getHeight()/2f + getHeight()/2f) + MIN_DISTANCE) return false;

        return true;
    }

    /**
     * Try to snap, then align to neighboring buttons, given the provided coordinates.
     * The new position is automatically applied to the View,
     * regardless of if the View snapped or not.
     *
     * The new position is always dynamic, thus replacing previous dynamic positions
     *
     * @param x Coordinate on the x axis
     * @param y Coordinate on the y axis
     */
    protected void snapAndAlign(float x, float y){
        float MIN_DISTANCE = Tools.dpToPx(8);
        String dynamicX = generateDynamicX(x);
        String dynamicY = generateDynamicY(y);

        setX(x);
        setY(y);

        for(ControlButton button :  ((ControlLayout) getParent()).getButtonChildren()){
            //Step 1: Filter unwanted buttons
            if(!canSnap(button)) continue;

            //Step 2: Get Coordinates
            float button_top = button.getY();
            float button_bottom = button_top + button.getHeight();
            float button_left = button.getX();
            float button_right = button_left + button.getWidth();

            float top = getY();
            float bottom = getY() + getHeight();
            float left = getX();
            float right = getX() + getWidth();

            //Step 3: For each axis, we try to snap to the nearest
            if(Math.abs(top - button_bottom) < MIN_DISTANCE){ // Bottom snap
                dynamicY = applySize(button.getProperties().dynamicY, button) + applySize(" + ${height}", button) ;
            }else if(Math.abs(button_top - bottom) < MIN_DISTANCE){ //Top snap
                dynamicY = applySize(button.getProperties().dynamicY, button) + " - ${height}";
            }
            if(!dynamicY.equals(generateDynamicY(getY()))){ //If we snapped
                if(Math.abs(button_left - left) < MIN_DISTANCE){ //Left align snap
                    dynamicX = applySize(button.getProperties().dynamicX, button);
                }else if(Math.abs(button_right - right) < MIN_DISTANCE){ //Right align snap
                    dynamicX = applySize(button.getProperties().dynamicX, button) + applySize(" + ${width}", button) + " - ${width}";
                }
            }

            if(Math.abs(button_left - right) < MIN_DISTANCE){ //Left snap
                dynamicX = applySize(button.getProperties().dynamicX, button) + " - ${width}";
            }else if(Math.abs(left - button_right) < MIN_DISTANCE){ //Right snap
                dynamicX = applySize(button.getProperties().dynamicX, button) + applySize(" + ${width}", button);
            }
            if(!dynamicX.equals(generateDynamicX(getX()))){ //If we snapped
                if(Math.abs(button_top - top) < MIN_DISTANCE){ //Top align snap
                    dynamicY = applySize(button.getProperties().dynamicY, button);
                }else if(Math.abs(button_bottom - bottom) < MIN_DISTANCE){ //Bottom align snap
                    dynamicY = applySize(button.getProperties().dynamicY, button) + applySize(" + ${height}", button) + " - ${height}";
                }
            }

        }

        setDynamicX(dynamicX);
        setDynamicY(dynamicY);
    }

    /**
     *
     * @param equation
     * @param button
     * @return
     */
    private static String applySize(String equation, ControlButton button){
        return equation.replace("${height}", " px(" + Tools.pxToDp(button.getProperties().getHeight()) + ") /" + PREF_BUTTONSIZE + " * ${preferred_scale}")
                .replace("${width}", " px(" + Tools.pxToDp(button.getProperties().getWidth()) + ") / " + PREF_BUTTONSIZE + " * ${preferred_scale}");
    }

    public int computeStrokeWidth(float widthInPercent){
        float maxSize = Math.max(mProperties.getWidth(), mProperties.getHeight());
        return (int)((maxSize/2) * (widthInPercent/100));
    }

    public float computeCornerRadius(float radiusInPercent){
        float minSize = Math.min(mProperties.getWidth(), mProperties.getHeight());
        return (minSize/2) * (radiusInPercent/100);
    }

    public boolean triggerToggle(MotionEvent event){
        //returns true a the toggle system is triggered
        if(mProperties.isToggle){
            isToggled = !isToggled;
            invalidate();
            sendKeyPresses(event, isToggled);
            return true;
        }
        return false;
    }

    public void sendKeyPresses(MotionEvent event, boolean isDown){
        for(int keycode : mProperties.keycodes){
            if(keycode >= GLFW_KEY_UNKNOWN){
                MainActivity.sendKeyPress(keycode, CallbackBridge.getCurrentMods(), isDown);
                CallbackBridge.setModifiers(keycode, isDown);
            }else {
                super.onTouchEvent(event);
            }
        }
    }

}
