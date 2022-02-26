package com.kdt.mcgui;

import android.content.*;
import android.util.*;
import android.graphics.*;

public class MineEditText extends com.google.android.material.textfield.TextInputEditText {
	public MineEditText(Context ctx) {
		super(ctx);
		init();
	}

	public MineEditText(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		init();
	}

	public void init() {
		setBackgroundColor(Color.parseColor("#131313"));
		setPadding(5, 5, 5, 5);
	}
}
