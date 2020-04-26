/**
 * Temporary use console instead of MainActivity
 * for testing in XServer XSDL.
 */

package net.kdt.pojavlaunch;

import android.app.*;
import android.os.*;
import android.util.*;
import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import android.support.v7.app.*;
import android.widget.*;
import android.graphics.*;
import android.view.*;
import android.text.method.*;

public class MainConsoleActivity extends AppCompatActivity
{
	private ScrollView mConsoleScroll;
	private TextView mConsoleView;
	
	private MCProfile.Builder mProfile;
	private JMinecraftVersionList.Version mVersionInfo;
	@Override
	protected void onCreate(Bundle b)
	{
		super.onCreate(b);
		
		mConsoleView = new TextView(this); // , null, com.android.internal.R.attr.editTextStyle);
		mConsoleView.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.WRAP_CONTENT, ScrollView.LayoutParams.WRAP_CONTENT));
		mConsoleView.setTypeface(Typeface.MONOSPACE);
		mConsoleView.setGravity(Gravity.TOP);
		mConsoleView.setTextSize(12);
		mConsoleView.setTextIsSelectable(true);
		
		mConsoleScroll = new ScrollView(this);
		mConsoleScroll.addView(mConsoleView);
		
		setContentView(mConsoleScroll);
		
		mProfile = PojavProfile.getCurrentProfileContent(this);
		mVersionInfo = Tools.getVersionInfo(mProfile.getVersion());
		
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					launchMinecraft();
				}
			}).start();
	}
	
	private void logn(String str) {
		log(str + "\n");
	}

	private void log(final String str) {
		runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					mConsoleView.append(str);
					mConsoleScroll.fullScroll(ScrollView.FOCUS_DOWN);
				}
			});
	}
	
	private void launchMinecraft() {
		try {
			List<String> mcJreArgs = new ArrayList<String>();
			mcJreArgs.add("java");
			mcJreArgs.add("-Duser.home=" + Tools.MAIN_PATH);
			mcJreArgs.add("-Xmx512M");
			mcJreArgs.add("-classpath");
			mcJreArgs.add(Tools.generate(mProfile.getVersion()));
			mcJreArgs.add(mVersionInfo.mainClass);
			mcJreArgs.addAll(Arrays.asList(getMCArgs()));
			
			SimpleShellProcess process = new SimpleShellProcess(new SimpleShellProcess.OnPrintListener(){
				@Override
				public void onPrintLine(String text) {
					log(text);
				}
			}, "sh " + Tools.homeJreDir + "/usr/bin/jre.sh");
			process.initInputStream(this);
			
			process.writeToProcess("unset LD_PRELOAD");
			process.writeToProcess("cd " + Tools.MAIN_PATH);
			process.writeToProcess(mcJreArgs.toArray(new String[0]));
		} catch (Throwable th) {
			th.printStackTrace();
			Tools.showError(this, th);
		}
	}

	private String[] getMCArgs() {
		String username = mProfile.getUsername();
		String versionName = mProfile.getVersion();
		String mcAssetsDir = Tools.ASSETS_PATH;
		String userType = "mojang";

		File gameDir = new File(Tools.MAIN_PATH);
		gameDir.mkdirs();

		Map<String, String> varArgMap = new ArrayMap<String, String>();
		varArgMap.put("auth_player_name", username);
		varArgMap.put("version_name", versionName);
		varArgMap.put("game_directory", gameDir.getAbsolutePath());
		varArgMap.put("assets_root", mcAssetsDir);
		varArgMap.put("assets_index_name", mVersionInfo.assets);
		varArgMap.put("auth_uuid", mProfile.getProfileID());
		varArgMap.put("auth_access_token", mProfile.getAccessToken());
		varArgMap.put("user_properties", "{}");
		varArgMap.put("user_type", userType);
		varArgMap.put("version_type", mVersionInfo.type);
		varArgMap.put("game_assets", Tools.ASSETS_PATH);

		String[] argsFromJson = insertVariableArgument(splitAndFilterEmpty(mVersionInfo.minecraftArguments), varArgMap);
		// Tools.dialogOnUiThread(this, "Result args", Arrays.asList(argsFromJson).toString());
		return argsFromJson;
	}

	private String[] splitAndFilterEmpty(String argStr) {
		List<String> strList = new ArrayList<String>();
		strList.add("--fullscreen");
		for (String arg : argStr.split(" ")) {
			if (!arg.isEmpty()) {
				strList.add(arg);
			}
		}
		return strList.toArray(new String[0]);
	}

	private String[] insertVariableArgument(String[] args, Map<String, String> keyValueMap) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			String argVar = null;
			if (arg.startsWith("${") && arg.endsWith("}")) {
				argVar = arg.substring(2, arg.length() - 1);
				for (Map.Entry<String, String> keyValue : keyValueMap.entrySet()) {
					if (argVar.equals(keyValue.getKey())) {
						args[i] = keyValue.getValue();
					}
				}
			}
		}
		return args;
	}
	
}
