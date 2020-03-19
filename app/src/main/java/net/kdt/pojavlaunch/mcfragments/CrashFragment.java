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

public class CrashFragment extends Fragment
{
	public static String lastCrashSaved = "";
	private String crashContent;
	public boolean resetCrashLog = false;
	
	private TextView crashView;
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.lmaintab_crashlog, container, false);
		
		return view;
    }
	
	@Override
	public void onActivityCreated(Bundle p1)
	{
		super.onActivityCreated(p1);
		crashView = (TextView) getView().findViewById(R.id.lmaintabconsoleLogCrashTextView);
		crashView.setTypeface(Typeface.MONOSPACE);
		crashView.setHint("No crash detected.");
		
		//new File(crashPath).mkdirs();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		refreshCrashFile();
	}
	
	public static boolean isNewCrash(File crashLog) throws Exception {
		String content = Tools.read(crashLog.getAbsolutePath());
		return crashLog != null && content.startsWith("---- Minecraft Crash Report ----");
	}
	
	public void refreshCrashFile()
	{
		try {
			if(!resetCrashLog){
				File crashLog = Tools.lastFileModified(Tools.crashPath);
			
				if (isNewCrash(crashLog)) {
					crashContent = Tools.read(crashLog.getAbsolutePath());
					Tools.write(crashLog.getAbsolutePath(), "\n" + crashContent);
					lastCrashSaved = crashLog.getName();
					crashView.setText(crashContent);
				} else if(crashLog.getName().equals(lastCrashSaved)){
					crashView.setText(crashContent);
				}
				else throw new Exception();
			}
			else throw new Exception();
		} catch (Exception e) {
			// Can't find crash or no NEW crashes
			crashView.setText(""/*Log.getStackTraceString(e)*/);
		}
	}
}
