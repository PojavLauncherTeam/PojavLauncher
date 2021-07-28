package net.kdt.pojavlaunch.customcontrols.buttons;

import android.annotation.SuppressLint;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import com.google.android.material.math.MathUtils;

import net.kdt.pojavlaunch.customcontrols.ControlData;
import net.kdt.pojavlaunch.customcontrols.ControlLayout;
import net.kdt.pojavlaunch.customcontrols.handleview.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import org.lwjgl.glfw.*;

import static net.kdt.pojavlaunch.LWJGLGLFWKeycode.GLFW_KEY_UNKNOWN;

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
        properties.setWidth(properties.getWidth() / layout.getLayoutScale() * LauncherPreferences.PREF_BUTTONSIZE);
        properties.setHeight(properties.getHeight() / layout.getLayoutScale() * LauncherPreferences.PREF_BUTTONSIZE);

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
        setX(mProperties.insertDynamicPos(mProperties.dynamicX));
        setY(mProperties.insertDynamicPos(mProperties.dynamicY));
        
        setModified(true);
    }

    public void setVisible(boolean isVisible){
        if(mProperties.isHideable)
            setVisibility(isVisible ? VISIBLE : GONE);
    }

    @Override
    public void setX(float x) {
        super.setX(x);

        if (!mProperties.isDynamicBtn) {

            if(x + (mProperties.getWidth()/2f) > CallbackBridge.physicalWidth/2f){
                System.out.println(mProperties.getWidth());
                mProperties.dynamicX = (x + mProperties.getWidth()) / CallbackBridge.physicalWidth + " * ${screen_width} - ${width}";
            }else{
                mProperties.dynamicX = x / CallbackBridge.physicalWidth + " * ${screen_width}";
            }

            setModified(true);
        }
    }

    @Override
    public void setY(float y) {
        super.setY(y);

        if (!mProperties.isDynamicBtn) {

            if(y + (mProperties.getHeight()/2f) > CallbackBridge.physicalHeight/2f){
                System.out.println(mProperties.getHeight());
                mProperties.dynamicY = (y + mProperties.getHeight()) / CallbackBridge.physicalHeight + " * ${screen_height} - ${height}";
            }else{
                mProperties.dynamicY = y / CallbackBridge.physicalHeight + " * ${screen_height}";
            }

            setModified(true);
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
                    //Time to snap !
                    float MIN_DISTANCE = Tools.dpToPx(10);
                    float currentX = event.getRawX() - downX;
                    float currentY = event.getRawY() - downY;

                    setX(currentX);
                    setY(currentY);

                    ControlButton nearButton;
                    /*
                    for(ControlButton button : ((ControlLayout) getParent()).getButtonChildren()){
                        if(button == ControlButton.this){
                            continue;
                        }
                        if(distanceBetweenViews(ControlButton.this, button) < MIN_DISTANCE){
                            if(Math.abs(getTop() - button.getBottom()) < MIN_DISTANCE){ // Bottom snap
                                currentY = button.getBottom();
                            }
                            //System.out.println(button.getTop() - getBottom());
                            if(Math.abs(button.getTop() - getBottom()) < MIN_DISTANCE){ //Top snap
                                currentY = button.getTop() - getHeight();
                            }
                            if(Math.abs(button.getLeft() - getRight()) < MIN_DISTANCE){ //Left snap
                                currentX = button.getLeft() - getWidth();
                            }
                            if(Math.abs(getLeft() - button.getRight()) < MIN_DISTANCE){ //Right snap
                                currentX = button.getRight();
                            }
                        }
                     */
                    for(ControlButton button :  ((ControlLayout) getParent()).getButtonChildren()){
                        if(button == this) continue;
                        if(MathUtils.dist(button.getX() + button.getProperties().getWidth()/2,
                                button.getY() + button.getProperties().getHeight()/2,
                                getX() + getProperties().getWidth()/2,
                                getY() + getProperties().getHeight()/2) > Math.max(button.getProperties().getWidth()/2 + getProperties().getWidth()/2, button.getProperties().getHeight()/2 + getProperties().getHeight()/2) + MIN_DISTANCE) continue;

                        float button_top = button.getY();
                        float button_bottom = button.getY() + button.getProperties().getHeight();
                        float button_left = button.getX();
                        float button_right = button.getX() + button.getProperties().getWidth();

                        float top = getY();
                        float bottom = getY() + getProperties().getHeight();
                        float left = getX();
                        float right = getX() + getProperties().getWidth();



                        /*
                        if(MathUtils.dist(button.getX(), button.getY(), currentX, currentY) < MIN_DISTANCE){
                            currentX = button.getX();
                            currentY = button.getY();
                        }
                        */

                        if(Math.abs(top - button_bottom) < MIN_DISTANCE){ // Bottom snap
                            currentY = button_bottom;
                        }
                        //System.out.println(button.getTop() - getBottom());
                        if(Math.abs(button_top - bottom) < MIN_DISTANCE){ //Top snap
                            currentY = button_top - getProperties().getHeight();
                        }
                        if(currentY != getY()){ //If we snapped
                            if(Math.abs(button_left - left) < MIN_DISTANCE){ //Left align snap
                                currentX = button_left;

                            }
                            if(Math.abs(button_right - right) < MIN_DISTANCE){ //Right align snap
                                currentX = button_right - getProperties().getWidth();

                            }
                        }

                        if(Math.abs(button_left - right) < MIN_DISTANCE){ //Left snap
                            currentX = button_left - getProperties().getWidth();
                        }
                        if(Math.abs(left - button_right) < MIN_DISTANCE){ //Right snap
                            currentX = button_right;
                        }
                        if(currentX != getX()){
                            if(Math.abs(button_top - top) < MIN_DISTANCE){
                                currentY = button_top;

                            }
                            if(Math.abs(button_bottom - bottom) < MIN_DISTANCE){
                                currentY = button_bottom - getProperties().getHeight();

                            }
                        }

                    }

                    setX(currentX);
                    setY(currentY);


                }
                break;
        }

        return super.onTouchEvent(event);
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

    /*
     * Returns the distance based on the bounding boxes
     */
    private static float distanceBetweenViews(View v1, View v2){
        float distX, distY;
        if(v1.getPivotX() < v2.getPivotX()){
            distX = v2.getLeft() - v1.getRight();
        }else{
            distX = v1.getLeft() - v2.getRight();
        }
        //if(distX < 0) distX = 0;

        if(v1.getPivotY() < v2.getPivotY()){
            distY = v2.getTop() - v1.getBottom();
        }else{
            distY = v1.getTop() - v2.getBottom();
        }
        //if(distY < 0) distY = 0;
        //System.out.println("DISTX: " + distX);
        //System.out.println("DISTY: " + distY);

        return distX + distY;
    }

    private static boolean isViewOverlapping(View firstView, View secondView) {
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];

        firstView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        firstView.getLocationOnScreen(firstPosition);
        secondView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        secondView.getLocationOnScreen(secondPosition);

        return firstPosition[0] < secondPosition[0] + secondView.getMeasuredWidth()
                && firstPosition[0] + firstView.getMeasuredWidth() > secondPosition[0]
                && firstPosition[1] < secondPosition[1] + secondView.getMeasuredHeight()
                && firstPosition[1] + firstView.getMeasuredHeight() > secondPosition[1];
    }

}
