package com.kdt.mcgui;

import android.content.*;
import android.util.*;
import android.widget.*;
import net.kdt.pojavlaunch.*;

public class MineEditText extends EditText
{
	public MineEditText(Context ctx)
	{
		super(ctx);
		init();
	}

	public MineEditText(Context ctx, AttributeSet attrs)
	{
		super(ctx, attrs);
		init();
	}

	public void init()
	{
		setBackgroundResource(R.drawable.border_edittext);
		setPadding(5, 5, 5, 5);
	}
}
