package com.kdt.mcgui;

import android.content.*;
import android.graphics.*;
import android.util.*;

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
		setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/NotoSans-Bold.ttf"));
	}

}
