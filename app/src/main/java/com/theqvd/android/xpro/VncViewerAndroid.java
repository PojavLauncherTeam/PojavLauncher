package com.theqvd.android.xpro;
/**
 * Copyright 2009-2014 by Qindel Formacion y Servicios S.L.
 * 
 * xvncpro is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * xvncpro is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 */
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.util.*;
import net.kdt.pojavlaunch.prefs.*;
import android.support.v4.app.*;
import android.androidVNC.*;

public class VncViewerAndroid implements VncViewer {
	static final String tag = L.xvncbinary + "-VncViewerAndroid-" +java.util.Map.Entry.class.getSimpleName();
	final static String vncpackage = "android.androidVNC";
	private static Activity activity;
	private Config config;
	PendingIntent contentVncIntent;
	Intent vncIntent;
	
	VncViewerAndroid(Activity a) {
		activity = a;
		config = new Config(activity);
		String cmd = Config.vnccmd;
		vncIntent = new Intent(a, VncCanvasActivity.class);
		vncIntent.putExtra("x11", Uri.parse(cmd));
		
		// multiple tasks
		vncIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
		vncIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		
		// Remove old Create new task
		// vncIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		contentVncIntent = PendingIntent.getActivity(activity, 0, vncIntent, 0);
	}

	@Override
	public void launchVncViewer() {
		Log.i(tag, "launching vncviewer androidvnc with activity="+activity+"; vncIntent="+vncIntent);
		Intent intent = (Intent) vncIntent.clone();
		if (LauncherPreferences.PREF_FREEFORM) {
			DisplayMetrics dm = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

			ActivityOptionsCompat options = ActivityOptionsCompat.makeBasic();
			Rect freeformRect = new Rect(0, 0, dm.widthPixels / 2, dm.heightPixels / 2);
			options.setLaunchBounds(freeformRect);
			activity.startActivityForResult(intent, Config.vncActivityRequestCode, options.toBundle());
		} else {
			activity.startActivityForResult(intent, Config.vncActivityRequestCode);
		}
		// activity.startActivityForResult(intent, Config.vncActivityRequestCode);
	}
	
	@Override
	public void stopVncViewer() {
		Log.i(tag, "Stopping activity with activity code " + Config.vncActivityRequestCode);
		
		activity.finishActivity(Config.vncActivityRequestCode);
	}
	@Override
	public PendingIntent getContentVncIntent() {
		return contentVncIntent;
	}

	@Override
	public Activity getActivity() {
		return activity;
	}

}
