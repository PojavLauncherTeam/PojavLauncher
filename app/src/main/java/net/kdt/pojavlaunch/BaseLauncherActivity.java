package net.kdt.pojavlaunch;

import android.support.v7.app.*;
import android.widget.*;
import net.kdt.pojavlaunch.fragments.*;
import net.kdt.pojavlaunch.tasks.*;

public abstract class BaseLauncherActivity extends BaseActivity {
	public Button mPlayButton;
	public ConsoleFragment mConsoleView;
    public CrashFragment mCrashView;
    public ProgressBar mLaunchProgress;
	public Spinner mVersionSelector;
	public TextView mLaunchTextStatus, mTextVersion;
    
    public JMinecraftVersionList mVersionList;
	public MinecraftDownloaderTask mTask;
	public MCProfile.Builder mProfile;
	public String[] mAvailableVersions;
    
	public boolean mIsAssetsProcessing = false;
    
    public abstract void statusIsLaunching(boolean isLaunching);
}
