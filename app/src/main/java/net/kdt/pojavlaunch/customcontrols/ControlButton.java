package net.kdt.pojavlaunch.customcontrols;

import android.content.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import net.kdt.pojavlaunch.*;
import com.kdt.handleview.*;
import android.view.ViewGroup.*;

public class ControlButton extends Button implements OnLongClickListener, OnTouchListener
{
    private GestureDetector mGestureDetector;
    private ControlData mProperties;
    private SelectionEndHandleView mHandleView;

    private boolean mCanModify = false;
    private boolean mCanTriggerLongClick = true;

    public ControlButton(Context ctx, ControlData properties) {
        super(ctx);

        mGestureDetector = new GestureDetector(ctx, new SingleTapConfirm());

        setBackgroundResource(R.drawable.control_button);

        setOnLongClickListener(this);
        setOnTouchListener(this);

        setProperties(properties);

        mHandleView = new SelectionEndHandleView(this);
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
        } else if (properties.specialButtonListener instanceof View.OnTouchListener) {
            setOnTouchListener((View.OnTouchListener) properties.specialButtonListener);
        } else {
            throw new IllegalArgumentException("Field " + ControlData.class.getName() + ".specialButtonListener must be View.OnClickListener or View.OnTouchListener, but is " + properties.specialButtonListener.getClass().getName());
        }

        setLayoutParams(new FrameLayout.LayoutParams(properties.width, properties.height));
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params)
    {
        super.setLayoutParams(params);

        mProperties.width = params.width;
        mProperties.height = params.height;
    }

    @Override
    public void setTranslationX(float x) {
        super.setTranslationX(x);
        mProperties.x = x;
    }

    @Override
    public void setTranslationY(float y) {
        super.setTranslationY(y);
        mProperties.y = y;
    }

    public void updateProperties() {
        setProperties(mProperties);
    }

    @Override
    public boolean onLongClick(View p1) {
        if (!mCanTriggerLongClick) return false;

        if (mHandleView.isShowing()) {
            mHandleView.hide();
        } else {
            if (getParent() != null) {
                ((ControlLayout) getParent()).hideAllHandleViews();
            }
            mHandleView.show();
        }
        return true;
    }

    private float moveX, moveY;
    private float downX, downY;
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (!mCanModify) {
            mCanTriggerLongClick = false;
            
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
                MainActivity.sendKeyPress(mProperties.keycode, 0, isDown);

                return true;
            }
            
            return false;
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

        return false;
    }

    public void setModifiable(boolean z) {
        mCanModify = z;
    }
}
