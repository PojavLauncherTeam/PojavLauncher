/**
 *  Singleton Class to hold all the configuration strings
 *
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
package com.theqvd.android.xpro;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import net.kdt.pojavlaunch.*;
/**
 * 
 * Class to hold all the configuration strings + the persistent configuration
 * of the application stored in the property files
 *
 * @author nito
 *
 */

public class Config {
	public final static String specialAndroid22Extension = ".ogg";
	public final static String assetscopydir = "xserver";
	public final static String vnccmd = "vnc://localhost:5900/C24bit/ben1to";
	public final static String x11cmd = "x11://localhost:6000";
	private static String targetdir;
	public static String xvnc;
	public static String xvnccmd;
	public static String pocketvncconfigfullpath;

//	public final static String xvnc = targetdir + "/usr/X11R6/bin/" + xvncbinary;
//	public final static String xvnccmd = xvnc + " :0 -br -localhost -nolisten local -PasswordFile="+targetdir+"/etc/vncpasswd";
	public static String xvncbinary = L.xvncbinary;
	public final static String notAllowRemoteVncConns = "-localhost";
	public final static String psxvnccmd = "/system/bin/ps "+L.xvncbinary;
	public final static String serverstartedstring = "^.*?created VNC server for screen 0";
	public final static String vncdisconnectedstring = ".*?Connections: closed: 127.0.0.1.*";
	// Connections: closed: 127.0.0.1::51506
	// Property strings in the property file
	public final static String props_hasbeencopied = "hasbeencopied";
	public final static String props_pocketconfigcopied = "pocketconfigcopied";
	public final static String props_forcexresolution = "forcexresolution";
	public final static String props_widthpixels = "widthpixels";
	public final static String props_heightpixels = "heightpixels";
	public final static String props_keep_x_running = "keepxrunning";
	public final static String props_use_android_vnc = "useandroidvnc";
	public final static String props_remote_vnc = "useremotevnc";
	public final static String props_render = "userender";
	public final static String props_xinerama = "usexinerama";
	public final static String helpurl = "http://docs.theqvd.com/";
	public final static int minPixels = 32;
	public final static int maxPixels = 10000;
	public final static boolean debug = false;
	public final static int notifycopy = 1;
	public final static int notifystartx = 2;
	public final static int notifynovncinstalled = 3;
	public final static int xvncsizerequired = 40; /* 37 MB required */
	public final static long xvncsizerequiredinkbytes = xvncsizerequired * 1024L;
	public final static String pocketvncconfig = "xvnc.vnc";
//	public static String pocketvncconfigfullpath = targetdir + Config.pocketvncconfig;
	public final static int INSTALLPACKAGE=1;
	public final static int SENDALERT=0;
	public final static int SETCOPYPROGRESS=1;
	public final static int SETPROGRESSVISIBILITY=2;
	public final static int UPDATEBUTTONS=3;
	public final static int PRERREQUISITEINSTALLED=4;
	public final static int[] messageType = {
		SENDALERT, // uses messageTitle and messageText in the setData
		SETCOPYPROGRESS, // uses progress in the setData
		SETPROGRESSVISIBILITY, // uses progressvisibility in the setData
		UPDATEBUTTONS, // no parameters
		PRERREQUISITEINSTALLED, // no parameters
	};
	public final static String messageTitle = "title";
	public final static String messageText = "text";
	public final static String copyProgress = "progress";
	public final static String progressVisibility = "progressVisibility";
	// StartActivityForResult codes
	public final static int vncActivityRequestCode = 11;
	
	
	public static String getAbout(String version) {
		return "XVnc\nLicense: Licensed under the GPLv3.\nAuthor: support@theqvd.com\nSponsored: http://theqvd.com\nVersion: "+version+"\nRevision: $Revision: 26639 $\nDate: $Date: 2015-03-31 11:51:02 +0200 (Tue, 31 Mar 2015) $";
	}
	// Class info
	static final String tag = L.xvncbinary + "-Config-" +java.util.Map.Entry.class.getSimpleName();
	private static Context context;
	private static Activity activity;
	private static boolean appConfig_force_x_geometry = false,
			appConfig_keep_x_running = false,
			appConfig_remote_vnc_allowed = false,
			appConfig_render = true,
			appConfig_xinerama = false;
	private static int appConfig_height_pixels = 0, appConfig_width_pixels = 0,
			appConfig_defaultHeightPixels = 0, appconfig_defaultWidthPixels = 0;
	private static VncViewerAndroid androidvncviewer;
	private static Handler uiHandler;
	// private Prerrequisite[] prerrequisites;
	// Set installPrerrequisitesOnStart to true if you want to finish the activity
	// after installation
	private static boolean installPrerrequisitesOnStart = false;

	private void init() {
		Log.i(tag, "The CPU type from CPU_ABI is "+android.os.Build.CPU_ABI);
		setTargetdir(Tools.datapath + "/xvncfiles");
		pocketvncconfigfullpath = getTargetdir() + "/" + Config.pocketvncconfig;
		xvnc = getTargetdir() + "/usr/X11R6/bin/" + L.xvncbinary;
		if (android.os.Build.CPU_ABI.equals("x86")) {
			xvnc += "i386";
		} else if (android.os.Build.CPU_ABI.startsWith("arm")) {
			// do not do anything
		} else {
			Log.e(tag, "Unknown CPU_ABI is neither x86 and not arm*");
			// TODO throw error here?
		}
		//xvnc += specialAndroid22Extension;
		
		
		xvnccmd = xvnc + " :0 -br -nolisten local  -pixelformat rgb888 -pixdepths 1 4 8 15 16 24 32 -PasswordFile="+getTargetdir()+"/etc/vncpasswd";
		setHeightAndWidth();
		load_properties();
	}

	public Config(Context c) {
		context = c;
		init();
	}
	public Config(Activity a) {
		context = a;
		activity = a;
		init();
	}

	private void setHeightAndWidth() {
		// Set height and width	
		WindowManager w = activity.getWindowManager();
		Display d = w.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		d.getMetrics(metrics);
		// since SDK_INT = 1;
		int widthPixels = metrics.widthPixels;
		int heightPixels = metrics.heightPixels;
		Log.d(tag, "setHeightAndWidth:The Build.VERSION is:"+Build.VERSION.SDK_INT+
	    		" and the initial width and height is:"+widthPixels+","+heightPixels);
		
		// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 17)
		try {
			Log.d(tag, "setHeightAndWidth:The Build.VERSION is greater than 17:"+Build.VERSION.SDK_INT);
		    Point realSize = new Point();
		    Display.class.getMethod("getSize", Point.class).invoke(d, realSize);   //getRealSize gets full screen without decorations
//		    Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
		    widthPixels = realSize.x;
		    heightPixels = realSize.y;
		    Log.d(tag, "setHeightAndWidth:The Build.VERSION is greater than 17:"+Build.VERSION.SDK_INT+
		    		" and the width and height is:"+widthPixels+","+heightPixels);
		} catch (Exception ignored) {
		}
		
		// force landscape hack
		appConfig_height_pixels = appConfig_defaultHeightPixels = (heightPixels > widthPixels) ? widthPixels : heightPixels;
		appConfig_width_pixels = appconfig_defaultWidthPixels = (heightPixels > widthPixels) ? heightPixels : widthPixels;
		Log.d(tag, "setHeightAndWidth: The final and the end width and height is:"+
				appconfig_defaultWidthPixels+","+appConfig_defaultHeightPixels);
	}
	

	private void load_properties() {
		// Use default settings
		// appConfig_force_x_geometry = true;
		// appConfig_keep_x_running = true;
		
		/*
		SharedPreferences prefsPrivate;
		prefsPrivate = context.getSharedPreferences("PREFS_PRIVATE", Context.MODE_PRIVATE);
		appConfig_force_x_geometry = prefsPrivate.getBoolean(Config.props_forcexresolution, appConfig_force_x_geometry);
		appConfig_keep_x_running = prefsPrivate.getBoolean(Config.props_keep_x_running, appConfig_keep_x_running);
		appConfig_run_androidvnc_client = prefsPrivate.getBoolean(Config.props_use_android_vnc, appConfig_run_androidvnc_client);
		appConfig_xvncbinary_copied = prefsPrivate.getBoolean(Config.props_hasbeencopied, appConfig_xvncbinary_copied);
		appConfig_height_pixels = prefsPrivate.getInt(Config.props_heightpixels, appConfig_defaultHeightPixels);
		appConfig_width_pixels = prefsPrivate.getInt(Config.props_widthpixels, appconfig_defaultWidthPixels);
		appConfig_pocketconfig_copied = prefsPrivate.getBoolean(Config.props_pocketconfigcopied, appConfig_pocketconfig_copied);
		appConfig_remote_vnc_allowed =  prefsPrivate.getBoolean(Config.props_remote_vnc, appConfig_remote_vnc_allowed);
		appConfig_render =  prefsPrivate.getBoolean(Config.props_render, appConfig_render);
		appConfig_xinerama =  prefsPrivate.getBoolean(Config.props_xinerama, appConfig_xinerama);
		*/
	}
	private void save_properties() {
		/*
		SharedPreferences prefsPrivate;
		prefsPrivate = context.getSharedPreferences("PREFS_PRIVATE", Context.MODE_PRIVATE);
		Editor prefsPrivateEditor = prefsPrivate.edit();
		prefsPrivateEditor.putBoolean(Config.props_forcexresolution, appConfig_force_x_geometry);
		prefsPrivateEditor.putBoolean(Config.props_keep_x_running, appConfig_keep_x_running);
		prefsPrivateEditor.putBoolean(Config.props_use_android_vnc, appConfig_run_androidvnc_client);
		prefsPrivateEditor.putBoolean(Config.props_hasbeencopied, appConfig_xvncbinary_copied);
		prefsPrivateEditor.putBoolean(Config.props_pocketconfigcopied, appConfig_pocketconfig_copied);
		prefsPrivateEditor.putBoolean(Config.props_remote_vnc, appConfig_remote_vnc_allowed);
		prefsPrivateEditor.putBoolean(Config.props_render, appConfig_render);
		prefsPrivateEditor.putBoolean(Config.props_xinerama, appConfig_xinerama);
		prefsPrivateEditor.putInt(Config.props_heightpixels, appConfig_height_pixels);
		prefsPrivateEditor.putInt(Config.props_widthpixels, appConfig_width_pixels);
		prefsPrivateEditor.commit();
		*/
	}
	public VncViewer getVncViewer() throws XvncproException {
		return this.getAndroidvncviewer(); // : this.getPocketcloudvncviewer();
	}
	public boolean is_force_x_geometry() {
		return appConfig_force_x_geometry;
	}
	public void set_force_x_geometry(
			boolean appConfig_force_x_geometry) {
		Config.appConfig_force_x_geometry = appConfig_force_x_geometry;
		save_properties();
	}
	public boolean is_keep_x_running() {
		return appConfig_keep_x_running;
	}
	public void set_keep_x_running(boolean appConfig_keep_x_running) {
		Config.appConfig_keep_x_running = appConfig_keep_x_running;
		save_properties();
	}
	public boolean isAppConfig_remote_vnc_allowed() {
		return appConfig_remote_vnc_allowed;
	}
	public void setAppConfig_remote_vnc_allowed(
			boolean appConfig_remote_vnc_allowed) {
		Config.appConfig_remote_vnc_allowed = appConfig_remote_vnc_allowed;
		save_properties();
	}
	public int get_height_pixels() {
		return appConfig_height_pixels;
	}
	public void set_height_pixels(int appConfig_height_pixels) {
		Config.appConfig_height_pixels = appConfig_height_pixels;
		save_properties();
	}
	public int get_width_pixels() {
		return appConfig_width_pixels;
	}
	public void set_width_pixels(int appConfig_width_pixels) {
		Config.appConfig_width_pixels = appConfig_width_pixels;
		save_properties();
	}
    public int getAppConfig_defaultHeightPixels() {
		return appConfig_defaultHeightPixels;
	}
	public int getAppconfig_defaultWidthPixels() {
		return appconfig_defaultWidthPixels;
	}
	public boolean isAppConfig_render() {
		return appConfig_render;
	}
	public void setAppConfig_render(boolean appConfig_render) {
		Config.appConfig_render = appConfig_render;
		save_properties();
	}
	public boolean isAppConfig_xinerama() {
		return appConfig_xinerama;
	}
	public void setAppConfig_xinerama(boolean appConfig_xinerama) {
		Config.appConfig_xinerama = appConfig_xinerama;
		save_properties();
	}
	public VncViewerAndroid getAndroidvncviewer() throws XvncproException {
		if (activity == null) {
			throw new XvncproException(context.getString(L.r_xvncpro_activity_notdefined));
		}
    	androidvncviewer = (androidvncviewer == null) ? new VncViewerAndroid(activity) : androidvncviewer;
		
		return androidvncviewer;
	}
	
	public Handler getUiHandler() {
		return uiHandler;
	}
	public void setUiHandler(Handler mHandler) {
		Config.uiHandler = mHandler;
		// getXvnccopy().setuiHandler(mHandler);
	}
	public boolean packageInstalled(String packagename) {
		ApplicationInfo info;
		try{
			info = context.getPackageManager().getApplicationInfo(packagename, 0);
		} catch( PackageManager.NameNotFoundException e ){
			Log.i(tag, packagename + " is not installed");
			return false;
		}
		Log.i(tag, packagename+" is already installed" + info);
		return true;
	}
    public void installPackage(String packagename) {
    	Log.i(tag, "Requesting installation of "+packagename);
    	Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id="+packagename));
//    	goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	if (activity == null) {
    		Log.i(tag, "Calling installPackage withouth startActivityForResult because activity is null");
    		context.startActivity(goToMarket);
    	} else {
    		Log.d(tag, "Calling installPackage with startActivityForResult because activity is non null");
    		activity.startActivityForResult(goToMarket, INSTALLPACKAGE);
    	}
		Log.d(tag, "package is installed sending prerrequisite installed for " + packagename);
		Message m = getUiHandler().obtainMessage(Config.PRERREQUISITEINSTALLED);
		getUiHandler().sendMessage(m);
    }
	public String getTargetdir() {
		return targetdir;
	}
	public void setTargetdir(String targetdir) {
		Config.targetdir = targetdir;
	}
	public static Activity getActivity() {
		return activity;
	}
	public static void setActivity(Activity activity) {
		Config.activity = activity;
	}
	public static boolean isInstallPrerrequisitesOnStart() {
		return installPrerrequisitesOnStart;
	}
	public static void setInstallPrerrequisitesOnStart(
			boolean finishAfterInstallingPrerrequisites) {
		Config.installPrerrequisitesOnStart = finishAfterInstallingPrerrequisites;
	}
}
