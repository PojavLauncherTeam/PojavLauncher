package net.kdt.pojavlaunch.customcontrols;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.kdt.handleview.*;
import net.kdt.pojavlaunch.*;
import org.lwjgl.glfw.*;

public class ControlButton extends Button implements OnLongClickListener, OnTouchListener
{
    private Paint mRectPaint;
    
    private GestureDetector mGestureDetector;
    private ControlData mProperties;
    private SelectionEndHandleView mHandleView;

    private boolean mModifiable = false;
    private boolean mCanTriggerLongClick = true;
    
    private boolean mChecked = false;
    
    private float mScaleAt;
    private int mMods;

    public ControlButton(ControlLayout layout, ControlData properties) {
        super(layout.getContext());
        setWillNotDraw(false);

        mScaleAt = layout.mLayout.scaledAt;
        
        mGestureDetector = new GestureDetector(getContext(), new SingleTapConfirm());

        setBackgroundResource(R.drawable.control_button);
        setOnLongClickListener(this);
        setOnTouchListener(this);

        setProperties(properties);
        setModified(false);

        mHandleView = new SelectionEndHandleView(this);
        
        final TypedValue value = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        
        mRectPaint = new Paint();
        mRectPaint.setColor(value.data);
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

    public void setProperties(ControlData properties, boolean changePos) {
        mProperties = properties;
        properties.update();
        
        if (properties.holdAlt) {
            mMods &= LWJGLGLFWKeycode.GLFW_MOD_ALT;
        } if (properties.holdCtrl) {
            mMods &= LWJGLGLFWKeycode.GLFW_MOD_CONTROL;
        } if (properties.holdShift) {
            mMods &= LWJGLGLFWKeycode.GLFW_MOD_SHIFT;
        }

        // com.android.internal.R.string.delete
        // android.R.string.
        setText(properties.name);
        if (changePos) {
            setTranslationX(moveX = properties.x);
            setTranslationY(moveY = properties.y);
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
            throw new IllegalArgumentException("Field " + ControlData.class.getName() + ".specialButtonListener must be View.OnClickListener or View.OnTouchListener, but is " + properties.specialButtonListener.getClass().getName());
        }

        setLayoutParams(new FrameLayout.LayoutParams((int) properties.width, (int) properties.height));
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);

        mProperties.width = params.width;
        mProperties.height = params.height;
        
        // Re-calculate position
        mProperties.update();
        setTranslationX(mProperties.x);
        setTranslationY(mProperties.y);
        
        setModified(true);
    }

    @Override
    public void setTranslationX(float x) {
        super.setTranslationX(x);

        if (!mProperties.isDynamicBtn) {
            mProperties.x = x;
            mProperties.dynamicX = Float.toString(x / CallbackBridge.windowWidth) + " * ${screen_width}";
            setModified(true);
        }
    }

    @Override
    public void setTranslationY(float y) {
        super.setTranslationY(y);

        if (!mProperties.isDynamicBtn) {
            mProperties.y = y;
            mProperties.dynamicY = Float.toString(y / CallbackBridge.windowHeight) + " * ${screen_height}";
            setModified(true);
        }
    }
    
    public void updateProperties() {
        setProperties(mProperties);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mChecked) {
            canvas.drawRect(0, getHeight() - 10 * mScaleAt, getWidth(), getHeight(), mRectPaint);
        }
    }

    @Override
    public boolean onLongClick(View p1) {
        if (mCanTriggerLongClick && mModifiable) {
            if (mHandleView.isShowing()) {
                mHandleView.hide();
            } else {
                if (getParent() != null) {
                    ((ControlLayout) getParent()).hideAllHandleViews();
                }
                
                try {
                    mHandleView.show();
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        }
        
        return mCanTriggerLongClick;
    }

    private float moveX, moveY;
    private float downX, downY;
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (!mModifiable) {
            mCanTriggerLongClick = false;
            
            // if (!mProperties.isToggle) {
                if (mProperties.keycode >= 0) {
                    boolean isDown;
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN: // 0
                        case MotionEvent.ACTION_POINTER_DOWN: // 5
                            isDown = true;
                            break;
                        case MotionEvent.ACTION_UP: // 1
                        case MotionEvent.ACTION_CANCEL: // 3
                        case MotionEvent.ACTION_POINTER_UP: // 6
                            isDown = false;
                            break;
                        default:
                            return false;
                    }
                    MainActivity.sendKeyPress(mProperties.keycode, mMods, isDown);
                    return true;
                }
            /* } else if (mGestureDetector.onTouchEvent(event)) {
                mChecked = !mChecked;
                MainActivity.sendKeyPress(mProperties.keycode, mMods, mChecked);
            } */

        } else {
            if (mGestureDetector.onTouchEvent(event)) {
                mCanTriggerLongClick = true;
                onLongClick(this);
            }
            
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_DOWN:
                    mCanTriggerLongClick = true;
                    downX = event.getX();
                    downY = event.getY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    mCanTriggerLongClick = false;
                    moveX += event.getX() - downX;
                    moveY += event.getY() - downY;

                    if (!mProperties.isDynamicBtn) {
                        setTranslationX(moveX);
                        setTranslationY(moveY);
                    }

                    break;
            }
        }
        
        return false;
    }

    public void setModifiable(boolean z) {
        mModifiable = z;
    }
    
    private void setModified(boolean modified) {
        if (getParent() != null) {
            ((ControlLayout) getParent()).setModified(modified);
        }
    }
}
