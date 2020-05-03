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
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.value.*;

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
		
		Bundle extras = getIntent().getExtras();
		final String modPath;
		
		if (extras != null) {
			modPath = extras.getString("launchJar", "");
		} else {
			modPath = null;
		}
		
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					launchJava(modPath);
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
	
	private void launchJava(String modPath) {
		try {
			/*
			 * 17w43a and above change Minecraf arguments from
			 * `minecraftArguments` to `arguments` so check if
			 * selected version requires LWJGL 3 or not is easy.
			 */
			boolean isLwjgl3 = mVersionInfo.arguments != null;
			
			List<String> mJreArgs = new ArrayList<String>();
			mJreArgs.add("java");
			mJreArgs.add("-Duser.home=" + Tools.MAIN_PATH);
			mJreArgs.add("-Xmx512M");
			
			if (modPath == null) {
				mJreArgs.add("-jar");
				mJreArgs.add(Tools.libraries + "/ClassWrapper.jar");
				mJreArgs.add(Tools.generate(mProfile.getVersion()));
				mJreArgs.add(mVersionInfo.mainClass);
				mJreArgs.addAll(Arrays.asList(getMCArgs()));
			} else {
				mJreArgs.add("-jar");
				mJreArgs.add(modPath);
			}
			
			SimpleShellProcess process = new SimpleShellProcess(new SimpleShellProcess.OnPrintListener(){
				@Override
				public void onPrintLine(String text) {
					log(text);
				}
			}, LauncherPreferences.PREF_RUNASROOT ? "su" : "sh" + " " + Tools.homeJreDir + "/usr/bin/jre.sh");
			process.initInputStream(this);
			process.writeToProcess("unset LD_PRELOAD");
			/* To prevent Permission Denied, chmod again.
			 * Useful if enable root mode */
			process.writeToProcess("chmod -R 700 " + Tools.homeJreDir);
			process.writeToProcess("cd " + Tools.MAIN_PATH);
			process.writeToProcess("export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/minecraft_lib/lwjgl" + (isLwjgl3 ? "3" : "2"));
			process.writeToProcess(mJreArgs.toArray(new String[0]));
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
		
		List<String> minecraftArgs = new ArrayList<String>();
		if (mVersionInfo.arguments != null) {
			for (Object arg : mVersionInfo.arguments.game) {
				if (arg instanceof String) {
					minecraftArgs.add((String) arg);
				} else {
					/*
					for (JMinecraftVersionList.Arguments.ArgValue.ArgRules rule : arg.rules) {
						// rule.action = allow
						// TODO implement this
					}
					*/
				}
			}
		}
	
		String[] argsFromJson = insertVariableArgument(
			splitAndFilterEmpty(
				mVersionInfo.minecraftArguments == null ?
				fromStringArray(minecraftArgs.toArray(new String[0])):
				mVersionInfo.minecraftArguments
			), varArgMap
		);
		// Tools.dialogOnUiThread(this, "Result args", Arrays.asList(argsFromJson).toString());
		return argsFromJson;
	}
	
	private String fromStringArray(String[] strArr) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < strArr.length; i++) {
			if (i > 0) builder.append(" ");
			builder.append(strArr[i]);
		}
		
		return builder.toString();
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
