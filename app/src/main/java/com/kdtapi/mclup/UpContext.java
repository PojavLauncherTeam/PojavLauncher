package com.kdtapi.mclup;

import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import java.io.*;
import net.kdt.pojavlaunch.*;

public abstract class UpContext
{
	public abstract void onCreate() throws Exception;
	public abstract void onFinish();
	public abstract void onErrorw(String message);
	
	private UpdateAppActivity activity;
	public UpContext(UpdateAppActivity activity)
	{
		this.activity = activity;
	}
	public Context getContext()
	{
		return getUpActivity();
	}
	public UpdateAppActivity getUpActivity()
	{
		return activity;
	}
	public void onRun()
	{
		new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void[] p1)
			{
				try{
					onCreate();
					onFinish();
					new File(Tools.worksDir + "/installer.jar").delete();
				}
				catch(Exception e){
					onErrorw(Log.getStackTraceString(e));
				}
				return null;
			}
		}.execute();
	}
	public ProgressBar getProgressBar()
	{
		return activity.getProgressBar();
	}
	public void log(String message)
	{
		activity.putLog(message);
	}
}
