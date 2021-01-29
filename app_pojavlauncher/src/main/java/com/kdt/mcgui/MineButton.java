package com.kdt.mcgui;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.view.View.*;
import net.kdt.pojavlaunch.*;

public class MineButton extends androidx.appcompat.widget.AppCompatButton
{
	
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
					setTextColor(Color.WHITE);
					setPadding(10,10,10,10);
					
					//setOnTouchListener(null);
				}
			});
		setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/NotoSans-Bold.ttf"));
	}

}
