package com.kdt.mcgui.app;

import androidx.appcompat.app.*;
import android.os.*;
import androidx.core.app.*;
import android.widget.*;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
