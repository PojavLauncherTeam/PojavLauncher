package net.kdt.pojavlaunch;

import android.app.*;
import android.os.*;
import android.view.*;

// This is for test only! 
public class NativeMainActivity extends NativeActivity
{
	private MCProfile.Builder mProfile;
	private JMinecraftVersionList.Version mVersionInfo;
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		
		mProfile = PojavProfile.getCurrentProfileContent(this);
		mVersionInfo = Tools.getVersionInfo(mProfile.getVersion());
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		super.surfaceCreated(holder);

		new Thread() {
			@Override
			public void run() {
				try {
					JREUtils.redirectStdio(false);
					
					Tools.launchMinecraft(NativeMainActivity.this, mProfile, mVersionInfo);
				} catch (Throwable th) {
					Tools.showError(NativeMainActivity.this, th, true);
				}
			}
		}.start();
	}
}
