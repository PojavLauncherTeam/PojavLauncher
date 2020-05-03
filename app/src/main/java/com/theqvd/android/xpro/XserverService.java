package com.theqvd.android.xpro;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * 
 * This class represents an XserverService
 * 
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
 * @author nito
 *
 */
public class XserverService extends Service 
implements Runnable 
{
	final static String tag = L.xvncbinary + "-XserverService-" +java.util.Map.Entry.class.getSimpleName();
    private Thread aThread;
	private static Process process;
	static private boolean xserverrunning = false;
	static private int pid = -1;
	private Config config;
	static private XserverService instance = null;

	@Override
	public void onCreate() {
		Log.i(tag, "onCreate");
		super.onCreate();
		config = new Config(this);
		instance = this;
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(tag, "Received start id " + startId + ": " + intent);
        super.onStartCommand(intent, flags, startId);

		if (isRunning()) {
			launchVNC();
			return START_STICKY;
		}
		aThread = new Thread (this);
		aThread.start();
        return START_STICKY;
    }

    private void stopXvnc() {
    	if (isRunning()) {
			Log.i(tag, "stop pid " + getPid());
			process.destroy();
			android.os.Process.killProcess(getPid());
			setRunning(false);
			setPid(-1);
			android.os.Process.sendSignal(getPid(), android.os.Process.SIGNAL_QUIT);
			updateButtons();
		}
    }
	@Override
	public void onDestroy() {
		Log.i(tag, "onDestroy");
		stopVNC();
		if (isRunning()) {
			Log.i(tag, "onDestroy xserverrunning destroy " + getPid());
			stopXvnc();
		}
		
		cancelNotify();
		updateButtons();
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		Log.i(tag, "onBind");
		return null;
	}
	
	private void launchVNC() {
		VncViewer v;
		try {
			v = config.getVncViewer();
			v.launchVncViewer();
		} catch (XvncproException e) {
			sendNotify(getString(L.r_x11_error), "Pid:"+e.toString());
		}
	}
	private void stopVNC() {
		VncViewer v;
		try {
			v = config.getVncViewer();
			v.stopVncViewer();
		} catch (XvncproException e) {
			sendNotify(getString(L.r_x11_error), "Pid:"+e.toString());
		}
	}
	@Override
	public void run() {
		String cmd = Config.xvnccmd+" -geometry "+ config.get_width_pixels() + "x"  + config.get_height_pixels();
		cmd +=  config.isAppConfig_remote_vnc_allowed() ? "" : " " + Config.notAllowRemoteVncConns;
		cmd += config.isAppConfig_render() ? " +render" : "";
		cmd += config.isAppConfig_xinerama() ? " +xinerama" : "";
		Log.i(tag, "launching:"+cmd);
		String cmdList[] = cmd.split("[ ]+");
		try {
			process = new ProcessBuilder().command(cmdList).redirectErrorStream(true).start();
			setPid(parsePid(process));
			setRunning(true);
			isRunning();
			Log.i(tag, "after launch:<"+cmd+"> = "+process.toString() + "," + process.hashCode() + "," + process.getClass());
			InputStream in = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr, 128);
			String line;
			
			while ((line = br.readLine()) != null) {
				Log.d(tag, "Read string <"+line+">");
				if (line.matches(Config.serverstartedstring)) {
					Log.i(tag, "Found string <"+line+"> launching VNC client");
					launchVNC();
					updateButtons();
				}
				// For AndroidVNC error see
				// See http://code.google.com/p/android-vnc-viewer/issues/detail?id=299
				if (line.matches(Config.vncdisconnectedstring)) {
					Log.i(tag, "Found string <"+line+">");
					stopVNC();
					if (!config.is_keep_x_running()) {
						Log.i(tag, "Stopping Xvnc service");
						stopXvnc();
						this.stopSelf();
					}
				}
			}
			process.waitFor();
			Log.i(tag, "Xvnc Process has died");
		} catch (IOException e) {
			Log.e(tag, "IOException:"+e.toString());
			sendNotify(getString(L.r_x11_error), "Pid:"+e.toString());
		} catch (InterruptedException e) {
			Log.e(tag, "InterruptedException:"+e.toString());
			sendNotify(getString(L.r_x11_error), "Pid:"+e.toString());	
		} finally {
			if (!config.is_keep_x_running()) {
				Log.i(tag, "Stopping Xvnc service (step 2)");
				stopXvnc();
				stopVNC();
				stopSelf();
			}
		}
	}
	private static int parsePid(Process p) {
		int pid;
		String s = p.toString();
		Log.d(tag, "parsePid for process String <"+s+">");
		Pattern pattern = Pattern.compile("id=([0-9]+)[^0-9].*$");
		
		Matcher m = pattern.matcher(s);
		if (m.find()) {
			Log.d(tag, "Pattern <"+pattern+"> found in string " + s + " with matching part "+m.group(1));
			pid = Integer.parseInt(m.group(1));
		} else
		{
			Log.e(tag, "Pattern <"+pattern+"> not found in string " + s + ". Trouble ahead when stopping");
			pid = -1;
		}
		return pid;
	}

	public int getPid() {
		return pid;
	}
	private void setPid(int pid) {
		Log.d(tag, "Setting pid to " + pid);
		XserverService.pid = pid;
	}
	
	private static int searchForXvncPid() {
		String psoutput = "";
		int pidfound = -1;
		String cmd = Config.psxvnccmd;
		String cmdList[] = cmd.split("[ ]+");
		try {
			process = new ProcessBuilder().command(cmdList).redirectErrorStream(true).start();
			Log.i(tag, "after launch:<"+cmd+"> = "+process.toString() + "," + process.hashCode() + "," + process.getClass());
			InputStream in = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				psoutput += line + "\n";
				Log.d(tag, "Read string <"+line+">");
			}
		} catch (IOException e) {
			Log.e(tag, "Error executing <"+cmd+"> "+e.toString());
			return pidfound;
		}

		Pattern pattern = Pattern.compile("(?m)^\\S+\\s+(\\d+)\\s+.*?"+L.xvncbinary+"$");
		
		Matcher m = pattern.matcher(psoutput);
		if (m.find()) {
			Log.d(tag, "Pattern <"+pattern+"> found in string <" + psoutput + "> with matching part "+m.group(1));
			pidfound = Integer.parseInt(m.group(1));
		} else
		{
			Log.d(tag, "Pattern <"+pattern+"> not found in string <" + psoutput + ">");
		}
		return pidfound;
	}
	
	public boolean isRunning() {
		Log.d(tag, "Running is "+xserverrunning + " pid="+pid);
		if (XserverService.xserverrunning) {
			File file=new File("/proc/" + pid);
			boolean exists = file.exists();
			if (exists) {
				sendNotify(getString(L.r_x11_is_running), "Pid:"+XserverService.pid);
				return XserverService.xserverrunning;
			}
			// Pid no longer there
			Log.i(tag, "The process was supposed to be running but pid "+pid+" is no longer there, setting running to false");
		}
		XserverService.pid=searchForXvncPid();
		XserverService.xserverrunning = (XserverService.pid != -1);
		if (XserverService.xserverrunning) {
			sendNotify(getString(L.r_x11_is_running), "Pid:"+XserverService.pid);
		} else {
			cancelNotify();
		}
		return XserverService.xserverrunning;
	}
	private void setRunning(boolean value) {
		Log.d(tag, "Setting xserverrunning to "+value);
		XserverService.xserverrunning = value;
		updateButtons();
	}
	private void updateButtons() {
		Log.d(tag, "message updateButtons");
		if (config.getUiHandler() == null) {
			Log.d(tag, "message updateButtons not sent because uiHandler is null");
			return;
		}
		Message m = config.getUiHandler().obtainMessage(Config.UPDATEBUTTONS);
		config.getUiHandler().sendMessage(m);
	}

	private void sendAlert(String title, String text) {
		Log.d(tag, "message sendAlert");
		if (config.getUiHandler() == null) {
			Log.d(tag, "message sendAlert not sent because uiHandler is null");
			return;
		}

		Message m = config.getUiHandler().obtainMessage(Config.SENDALERT);
		Bundle b = new Bundle();
		b.putString(Config.messageTitle, title);
		b.putString(Config.messageText, text);
		m.setData(b);
		config.getUiHandler().sendMessage(m);
	}
	public static XserverService getInstance() {
		return instance;
	}

//@SuppressWarnings("deprecation")
private void sendNotify(CharSequence title, CharSequence text) {
		Context c = this.getApplicationContext();
		// TODO try to set the DummyActivity as the intent
//		Intent dummyactivity = new Intent(this, DummyActivity.class);
//		dummyactivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		PendingIntent i = PendingIntent.getActivity(c, 0, new Intent(), 0);
		// End of TODO
		PendingIntent i = null;
		if (config == null) {
			Log.i(tag, "Not sending notify because config is null. This should not happen");
			return;
		}
		try {
			i = config.getVncViewer().getContentVncIntent(); 
			Log.i(tag, "activity is "+c+" intent is "+i);
		} catch (XvncproException e) {
			Log.e(tag, "Vnc intent error "+e.toString());
		}
		
		if (config == null) {
			Log.i(tag, "Not sending notify because config, vncViewer, or context is null");
			return;
		}
		if (i == null) {
			Log.e(tag, "PendingIntent is null creating empty PendingIntent");
			i = PendingIntent.getActivity(c, 0, new Intent(), 0);
		}
		Notification notification = new Notification(L.r_ic_xvnc, title, System.currentTimeMillis());
		notification.setLatestEventInfo(c, title, text, i);
		NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(Config.notifystartx, notification);
		Log.i(tag, "Sent notify with id <"+Config.notifystartx+">title <" +title+"> and text <" + text +">");
	}
	public void cancelNotify() {
		NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(Config.notifystartx);
		Log.i(tag, "Cancelled notify with id <" +Config.notifystartx+">");
	}
}
