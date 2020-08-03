package net.kdt.pojavlaunch.mcfragments;

import android.os.*;
import android.support.annotation.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import net.kdt.pojavlaunch.*;
import android.util.*;
import android.graphics.*;
import android.content.*;
import android.support.v4.os.*;

public class CrashFragment extends Fragment
{
	public static String lastCrashFile = Tools.worksDir + "/lastcrash.txt";
	
	private String crashContent;
	private TextView crashView;
	
	public boolean resetCrashLog = false;
	
	public static boolean isNewCrash(File crashLog) throws Exception {
		String content = Tools.read(crashLog.getAbsolutePath());
		return crashLog != null && content.startsWith("---- Minecraft Crash Report ----");
	}
	
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.lmaintab_crashlog, container, false);

		return view;
    }
	
	@Override
	public void onActivityCreated(Bundle b)
	{
		super.onActivityCreated(b);
		
		crashView = (TextView) getView().findViewById(R.id.lmaintabconsoleLogCrashTextView);
		crashView.setTypeface(Typeface.MONOSPACE);
		crashView.setHint(this.getText(R.string.main_nocrash));
		
		//new File(crashPath).mkdirs();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		refreshCrashFile();
	}
	
	public void refreshCrashFile()
	{
		try {
			if(!resetCrashLog){
				File crashLog = Tools.lastFileModified(Tools.crashPath);
				String lastCrash = getLastCrash();
				if (isNewCrash(crashLog)) {
					crashContent = Tools.read(crashLog.getAbsolutePath());
					Tools.write(crashLog.getAbsolutePath(), "\n" + crashContent);
					setLastCrash(crashLog.getAbsolutePath());
					crashView.setText(crashContent);
				} else if(!lastCrash.isEmpty()) {
					crashContent = Tools.read(lastCrash);
					crashView.setText(crashContent);
				} else throw new Exception();
			} else throw new Exception();
		} catch (Exception e) {
			// Can't find crash or no NEW crashes
			crashView.setText(""/*Log.getStackTraceString(e)*/);
			setLastCrash("");
		}
	}
	
	public void setLastCrash(String newValue) {
		try {
			Tools.write(lastCrashFile, newValue);
		} catch (Throwable th) {
			throw new RuntimeException(th);
		}
	}
	
	public String getLastCrash() {
		try {
			return Tools.read(lastCrashFile);
		} catch (Throwable th) {
			return "";
		}
	}
}
