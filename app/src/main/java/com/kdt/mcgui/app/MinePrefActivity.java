package com.kdt.mcgui.app;

import android.support.v7.app.*;
import android.os.*;
import android.support.v4.app.*;
import android.widget.*;

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
