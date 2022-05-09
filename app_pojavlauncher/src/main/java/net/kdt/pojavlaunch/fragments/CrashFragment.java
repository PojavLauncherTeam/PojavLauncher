package net.kdt.pojavlaunch.fragments;

import android.os.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.*;
import android.widget.*;
import java.io.*;
import net.kdt.pojavlaunch.*;

import android.graphics.*;
import androidx.fragment.app.Fragment;

public class CrashFragment extends Fragment {
	public static String LAST_CRASH_FILE = Tools.DIR_DATA + "/lastcrash.txt";

	private TextView mCrashView;
	public boolean mResetCrashLog = false;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_crash_log, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mCrashView = (TextView) view.findViewById(R.id.lmaintabconsoleLogCrashTextView);
		mCrashView.setTypeface(Typeface.MONOSPACE);
		mCrashView.setHint(this.getText(R.string.main_nocrash));
	}


	@Override
	public void onResume(){
		super.onResume();
		refreshCrashFile();
	}

	private void refreshCrashFile() {
		// Default text
		mCrashView.setText("");
		setLastCrash("");

		if(mResetCrashLog) return;

		File crashLog = Tools.lastFileModified(Tools.DIR_HOME_CRASH);
		String lastCrash = getLastCrash();
		String crashContent;
		try {
			if (isNewCrash(crashLog)) {
				crashContent = Tools.read(crashLog.getAbsolutePath());
				Tools.write(crashLog.getAbsolutePath(), "\n" + crashContent);
				setLastCrash(crashLog.getAbsolutePath());
				mCrashView.setText(crashContent);
			} else if(!lastCrash.isEmpty()) {
				crashContent = Tools.read(lastCrash);
				mCrashView.setText(crashContent);
			}
		}catch (IOException ioException){
			Log.e("CrashFragment", ioException.toString());
		}
	}

	private static boolean isNewCrash(@Nullable File crashLog) throws IOException {
		if(crashLog == null) return false;
		String content = Tools.read(crashLog.getAbsolutePath());
		return content.startsWith("---- Minecraft Crash Report ----");
	}

	private void setLastCrash(String newValue) {
		try {
			Tools.write(LAST_CRASH_FILE, newValue);
		} catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private String getLastCrash() {
		try {
			return Tools.read(LAST_CRASH_FILE);
		} catch (IOException ioException) {
			return "";
		}
	}
}
