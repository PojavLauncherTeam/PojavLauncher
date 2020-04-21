package net.kdt.pojavlaunch.value.customcontrols;

import android.content.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import net.kdt.pojavlaunch.*;

public class ControlView extends Button implements OnTouchListener
{
	private GestureDetector mGestureDetector;
	private View.OnClickListener mClickListener;
	private ControlButton mProperties;
	private boolean mCanMove = false;
	
	public ControlView(Context ctx, ControlButton properties) {
		super(ctx);
		
		setBackgroundResource(R.drawable.control_button);
		setOnTouchListener(this);
		
		mGestureDetector = new GestureDetector(ctx, new SingleTapConfirm());
		
		setProperties(properties);
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
		
		setLayoutParams(new FrameLayout.LayoutParams(properties.width, properties.height));
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
	
	private float moveX, moveY;
	private float downX, downY;
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event)) {
			if (mClickListener != null) mClickListener.onClick(view);
			return true;
		} else if (!mCanMove) {
			return false;
		}
		
		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				downX = event.getX();
				downY = event.getY();
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_MOVE:
				moveX += event.getX() - downX;
				moveY += event.getY() - downY;
				
				setTranslationX(moveX);
				setTranslationY(moveY);
				break;
		}
		
		return false;
	}
	
	public View.OnClickListener getOnClickListener() {
		return mClickListener;
	}

	@Override
	public void setOnClickListener(View.OnClickListener l) {
		// super.setOnClickListener(p1);
		
		mClickListener = l;
	}
	
	public void setCanMove(boolean z) {
		mCanMove = z;
	}
}
