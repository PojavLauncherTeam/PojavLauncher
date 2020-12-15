package com.kdt.mcgui;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.view.View.*;
import net.kdt.pojavlaunch.*;

public class MineButton extends Button
{
	private ColorDrawable left = new ColorDrawable(Color.parseColor("#80000000"));
	private ColorDrawable top = new ColorDrawable(Color.parseColor("#64FC20"));
	private ColorDrawable right = new ColorDrawable(Color.parseColor("#40000000"));
	private ColorDrawable bottom = new ColorDrawable(Color.parseColor("#80000000"));
	private ColorDrawable bgNormal = new ColorDrawable(Color.parseColor("#36b030"));

	private ColorDrawable leftFocus = new ColorDrawable(Color.parseColor("#C2000000"));
	private ColorDrawable topFocus = new ColorDrawable(Color.parseColor("#80313131"));
	private ColorDrawable rightFocus = new ColorDrawable(Color.parseColor("#C2000000"));
	private ColorDrawable bottomFocus = new ColorDrawable(Color.parseColor("#C2000000"));
	private ColorDrawable bgFocus = new ColorDrawable(Color.parseColor("#313131"));

	private boolean isUp = true;
	
	private Drawable[] DrawableArray = new Drawable[]{
		top,
		left,
		right,
		bottom,
		bgNormal
	};
	
	private Drawable[] DrawableArrayFocus = new Drawable[]{
		topFocus,
		leftFocus,
		rightFocus,
		bottomFocus,
		bgFocus
	};
	
	private LayerDrawable layerdrawable, layerdrawablefocus;
	
	public MineButton(Context ctx) {
		this(ctx, null);
	}
	
	public MineButton(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		init();
	}
	
	public void init() {
		getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					layerdrawable = new LayerDrawable(DrawableArray);
					layerdrawable.setLayerInset(0, 0, 0, 0, 0); // top
					layerdrawable.setLayerInset(1, 0, 8, getWidth() - 8,0); // left
					layerdrawable.setLayerInset(2, getWidth() - 8, 8, 0, 0); // right
					layerdrawable.setLayerInset(3, 0, getHeight() - 8, 0, 0); // bottom
					layerdrawable.setLayerInset(4, 8, 8, 8, 8); // bg

					layerdrawablefocus = new LayerDrawable(DrawableArrayFocus);
					layerdrawablefocus.setLayerInset(0, 0, 0, 0, 0); // top
					layerdrawablefocus.setLayerInset(1, 0, 8, getWidth() - 8,0); // left
					layerdrawablefocus.setLayerInset(2, getWidth() - 8, 8, 0, 0); // right
					layerdrawablefocus.setLayerInset(3, 0, getHeight() - 8, 0, 0); // bottom
					layerdrawablefocus.setLayerInset(4, 8, 8, 8, 8); // bg
					
					setBackgroundDrawable(layerdrawable);
					setTextColor(Color.WHITE);
					setPadding(10, 10, 10, 10);
					
					//setOnTouchListener(null);
				}
			});
		setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/NotoSans-Bold.ttf"));
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		setBackgroundDrawable(enabled ? layerdrawable : layerdrawablefocus);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if ((event.getAction() == event.ACTION_UP) && !isUp && isEnabled()) {
			setBackgroundDrawable(layerdrawable);
			isUp = true;
		} else if (event.getAction() == event.ACTION_DOWN && isUp) {
			setBackgroundDrawable(layerdrawablefocus);
			isUp = false;
		}
		
		return super.onTouchEvent(event);
	}
}
