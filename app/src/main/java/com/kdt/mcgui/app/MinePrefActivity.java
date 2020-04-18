package com.kdt.mcgui.app;

import android.os.*;
import android.widget.*;
import androidx.fragment.app.*;

public class MinePrefActivity extends MineActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FrameLayout fl = new FrameLayout(this);
		fl.setId(65535);
		setContentView(fl);
	}
	
	public void setFragment(Fragment fm) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(65535, fm);
		ft.commit();
	}
}
