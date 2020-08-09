package net.kdt.pojavlaunch.value.customcontrols;

import android.content.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import net.kdt.pojavlaunch.*;
import com.kdt.handleview.*;
import android.view.ViewGroup.*;

public class ControlView extends Button implements OnLongClickListener, OnTouchListener
{
	private GestureDetector mGestureDetector;
	private ControlButton mProperties;
	private SelectionEndHandleView mHandleView;
	
	private boolean mCanModify = false;
	private boolean mCanTriggerLongClick = true;
	
	public ControlView(Context ctx, ControlButton properties) {
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

	public ControlButton getProperties() {
		return mProperties;
	}
	
	public void setProperties(ControlButton properties) {
		setProperties(properties, true);
	}
	
	public void setProperties(ControlButton properties, boolean changePos) {
		mProperties = properties;
		// com.android.internal.R.string.delete
		// android.R.string.
		setText(properties.name);
		if (changePos) {
			setTranslationX(moveX = properties.x);
			setTranslationY(moveY = properties.y);
		}
		
		if (properties.specialButtonListener instanceof View.OnClickListener) {
			setOnClickListener((View.OnClickListener) properties.specialButtonListener);
		} else if (properties.specialButtonListener instanceof View.OnTouchListener) {
			setOnTouchListener((View.OnTouchListener) properties.specialButtonListener);
		} else {
			throw new IllegalArgumentException("Field " + ControlButton.class.getName() + ".specialButtonListener must be View.OnClickListener or View.OnTouchListener, but is " + properties.specialButtonListener.getClass().getName());
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
	public void setTranslationX(float x)
	{
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
	public boolean onLongClick(View p1)
	{
		if (!mCanTriggerLongClick) return false;

		if (mHandleView.isShowing()) {
			mHandleView.hide();
		} else {
			if (getParent() != null) {
				((ControlsLayout) getParent()).hideAllHandleViews();
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
			
			return false;
		}
		
		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				mCanTriggerLongClick = true;
				downX = event.getX();
				downY = event.getY();
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_MOVE:
				mCanTriggerLongClick = false;
				moveX += event.getX() - downX;
				moveY += event.getY() - downY;
				
				setTranslationX(moveX);
				setTranslationY(moveY);
				break;
		}
		
		return false;
	}
	
	public void setModifiable(boolean z) {
		mCanModify = z;
	}
}
