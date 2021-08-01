package net.kdt.pojavlaunch.customcontrols.buttons;

import android.annotation.SuppressLint;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

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

        //Position
        if (!properties.isDynamicBtn) {
            properties.dynamicX = properties.x / CallbackBridge.physicalWidth + " * ${screen_width}";
            properties.dynamicY = properties.y / CallbackBridge.physicalHeight + " * ${screen_height}";
        }

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
            setX(properties.x);
            setY(properties.y);
        }

        if (properties.specialButtonListener == null) {
            // A non-special button or inside custom controls screen so skip listener
        } else if (properties.specialButtonListener instanceof View.OnClickListener) {
            setOnClickListener((View.OnClickListener) properties.specialButtonListener);
            // setOnLongClickListener(null);
            // setOnTouchListener(null);
        } else if (properties.specialButtonListener instanceof View.OnTouchListener) {
            // setOnLongClickListener(null);
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
        setX(mProperties.x);
        setY(mProperties.y);
        
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
            mProperties.x = x;
            mProperties.dynamicX = x / CallbackBridge.physicalWidth + " * ${screen_width}";
            setModified(true);
        }
    }

    @Override
    public void setY(float y) {
        super.setY(y);

        if (!mProperties.isDynamicBtn) {
            mProperties.y = y;
            mProperties.dynamicY = y / CallbackBridge.physicalHeight + " * ${screen_height}";
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
                    setX(event.getRawX() - downX);
                    setY(event.getRawY() - downY);
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

}
