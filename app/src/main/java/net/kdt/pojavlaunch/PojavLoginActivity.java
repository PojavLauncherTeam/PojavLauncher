package net.kdt.pojavlaunch;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.CompoundButton.*;
import androidx.annotation.*;
import androidx.core.app.*;
import androidx.core.content.*;
import com.kdt.filermod.*;
import com.kdt.mcgui.app.*;
import com.kdt.mojangauth.*;
import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.update.*;

import static android.view.ViewGroup.LayoutParams.*;

public class PojavLoginActivity extends MineActivity
{
	private EditText edit2, edit3;
	private int REQUEST_STORAGE_REQUEST_CODE = 1;
	private ProgressBar prb;
	private Switch sRemember, sOffline;
	
	private boolean isPromptingGrant = false;
	// private boolean isPermGranted = false;
	
	private SharedPreferences firstLaunchPrefs;
	private String PREF_IS_DONOTSHOWAGAIN_WARN = "isWarnDoNotShowAgain";
	private String PREF_IS_INSTALLED_LIBRARIES = "isLibrariesExtracted";
	
	private boolean isInitCalled = false;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState, false);
		if (!isInitCalled) {
			init();
			isInitCalled = true;
		}
	}
	
	private void init() {
		firstLaunchPrefs = getSharedPreferences("pojav_extract", MODE_PRIVATE);
		new File(Tools.mpProfiles).mkdir();
		
		if (isAndroid7() && !firstLaunchPrefs.getBoolean(PREF_IS_DONOTSHOWAGAIN_WARN, false)) {
			AlertDialog.Builder startDlg = new AlertDialog.Builder(PojavLoginActivity.this);
			startDlg.setTitle(R.string.warning_title);
			
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
			
			LinearLayout conLay = new LinearLayout(this);
			conLay.setLayoutParams(params);
			conLay.setOrientation(LinearLayout.VERTICAL);
			TextView conText = new TextView(this);
			conText.setText(R.string.warning_msg);
			conText.setLayoutParams(params);
			final CheckBox conCheck = new CheckBox(this);
			conCheck.setText(R.string.warning_noshowagain);
			conCheck.setLayoutParams(params);
			conLay.addView(conCheck);
			
			conLay.addView(conText);
			
			startDlg.setView(conLay);
			startDlg.setCancelable(false);
			startDlg.setPositiveButton(R.string.warning_action_install, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						setPref(PREF_IS_DONOTSHOWAGAIN_WARN, conCheck.isChecked());
						
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse("market://details?id=com.vmos.glb"));
						startActivity(intent);
					}
				});
				
			startDlg.setNegativeButton(R.string.warning_action_tryanyway, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						setPref(PREF_IS_DONOTSHOWAGAIN_WARN, conCheck.isChecked());
						
						new InitTask().execute();
					}
				});
			

			startDlg.setNeutralButton(R.string.warning_action_exit, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						finish();
					}
				});
				
			startDlg.show();
		} else {
			new InitTask().execute();
		}
	}

	private class InitTask extends AsyncTask<Void, String, Integer>{
		private AlertDialog startAle;
		private ProgressBar progress;

		private ProgressBar progressSpin;
		private EditText progressLog;
		private AlertDialog progDlg;

		@Override
		protected void onPreExecute()
		{
			LinearLayout startScr = new LinearLayout(PojavLoginActivity.this);
			LayoutInflater.from(PojavLoginActivity.this).inflate(R.layout.start_screen, startScr);

			replaceFonts(startScr);

			progress = (ProgressBar) startScr.findViewById(R.id.startscreenProgress);
			//startScr.addView(progress);

			AlertDialog.Builder startDlg = new AlertDialog.Builder(PojavLoginActivity.this, R.style.AppTheme);
			startDlg.setView(startScr);
			startDlg.setCancelable(false);

			startAle = startDlg.create();
			startAle.show();
			startAle.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT
			);
		}
		
		private int revokeCount = -1;
		
		@Override
		protected Integer doInBackground(Void[] p1)
		{
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {}

			publishProgress("visible");

			while (Build.VERSION.SDK_INT >= 23 && !isStorageAllowed()){
				try {
					revokeCount++;
					if (revokeCount >= 3) {
						Toast.makeText(PojavLoginActivity.this, R.string.toast_permission_denied, Toast.LENGTH_LONG).show();
						finish();
					}
					isPromptingGrant = true;
					requestStoragePermission();
					while (isPromptingGrant) {
						Thread.sleep(200);
					}
					
				} catch (InterruptedException e) {}
			}

			initMain();

			return 0;
		}

		@Override
		protected void onProgressUpdate(String... obj)
		{

			if (obj[0].equals("visible")) {
				progress.setVisibility(View.VISIBLE);
			} else if (obj.length == 2 && obj[1] != null) {
				progressLog.append(obj[1]);
			}
		}

		@Override
		protected void onPostExecute(Integer obj) {
			startAle.dismiss();
			if (progressSpin != null) progressSpin.setVisibility(View.GONE);
			if (obj == 0) {
				if (progDlg != null) progDlg.dismiss();
				uiInit();
			} else if (progressLog != null) {
				progressLog.setText(getResources().getString(R.string.error_checklog, "\n\n" + progressLog.getText()));
			}

		}
/*
		private void appendlnToLog(String txt) {
			publishProgress("", txt + "\n");
		}
		
		 private void execCmd(String cmd) throws Exception {
		 appendlnToLog("> " + cmd);
		 ShellProcessOperation mainProcess = new ShellProcessOperation(new ShellProcessOperation.OnPrintListener(){

		 @Override
		 public void onPrintLine(String text)
		 {
		 publishProgress(text);
		 }
		 }, cmd);
		 mainProcess.initInputStream(MCLoginActivity.this);
		 String msgExit = cmd.split(" ")[0] + " has exited with code " + mainProcess.waitFor();
		 if (mainProcess.exitCode() != 0) {
		 throw new Error("(ERROR) " + msgExit);
		 } else {
		 appendlnToLog("(SUCCESS) " + msgExit);
		 }
		 }
		 */
	}
	
	private void uiInit() {
		setContentView(R.layout.launcher_login);

		edit2 = (EditText) findViewById(R.id.launcherAccEmail);
		edit3 = (EditText) findViewById(R.id.launcherAccPassword);
		if(prb == null) prb = (ProgressBar) findViewById(R.id.launcherAccProgress);
		
		sRemember = (Switch) findViewById(R.id.launcherAccRememberSwitch);
		sOffline  = (Switch) findViewById(R.id.launcherAccOffSwitch);
		sOffline.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton p1, boolean p2)
				{
					// May delete later
					edit3.setEnabled(!p2);
				}
			});
	}
	
	private boolean isAndroid7()
	{
		return Build.VERSION.SDK_INT >= 24;
	}
	
	/*
	
	long lastTime = System.currentTimeMillis();
	long lastDel = 0;
	
	
	private void deAnr(String msg) {
		long currt = System.currentTimeMillis();
		lastDel = currt - lastTime;
		lastTime = currt;
		System.out.println("Time:" + lastDel + "ms||" + (lastDel / 1000) + "s: " + msg);
	}
	*/
	
	@Override
	public void onResume() {
		super.onResume();
		
		// Clear current profile
		PojavProfile.setCurrentProfile(this, null);
	}
/*
	private boolean isOpenJDKInstalled() {
		return firstLaunchPrefs.getBoolean(PREF_IS_INSTALLED_OPENJDK, false);
	}
*/
	private boolean isLibrariesExtracted() {
		return firstLaunchPrefs.getBoolean(PREF_IS_INSTALLED_LIBRARIES, false);
	}
	
	private boolean setPref(String prefName, boolean value) {
		return firstLaunchPrefs.edit().putBoolean(prefName, value).commit();
	}
	
	private void initMain()
	{
		mkdirs(Tools.worksDir);
		mkdirs(Tools.versnDir);
		mkdirs(Tools.libraries);
		
		File file0 = new File(Tools.mpProfiles);
		File file1 = new File(Tools.mpModEnable);
		File file2 = new File(Tools.mpModDisable);
		File file3 = new File(Tools.mpModAddNewMo);
		
		file0.mkdir();
		file1.mkdirs();
		file2.mkdir();
		try {
			file3.createNewFile();
		} catch (IOException e){}
		
		try {
			mkdirs(Tools.MAIN_PATH);
			
			Tools.copyAssetFile(this, "options.txt", Tools.MAIN_PATH, false);
			
			// Extract launcher_profiles.json
			// TODO: Remove after implement.
			Tools.copyAssetFile(this, "launcher_profiles.json", Tools.MAIN_PATH, false);
			
			// Yep, the codebase from v1.0.3:
			//FileAccess.copyAssetToFolderIfNonExist(this, "1.0.jar", Tools.versnDir + "/1.0");
			//FileAccess.copyAssetToFolderIfNonExist(this, "1.7.3.jar", Tools.versnDir + "/1.7.3");
			//FileAccess.copyAssetToFolderIfNonExist(this, "1.7.10.jar", Tools.versnDir + "/1.7.10");
			
			// Extract libraries
			if (!isLibrariesExtracted()) {
				Tools.extractAssetFolder(this, "libraries", Tools.MAIN_PATH);
				setPref(PREF_IS_INSTALLED_LIBRARIES, true);
			}

			UpdateDataChanger.changeDataAuto("2.4", "2.4.2");
		}
		catch(Exception e){
			Tools.showError(this, e);
		}
	}
	
	private boolean mkdirs(String path)
	{
		File mFileeee = new File(path);
		if(mFileeee.getParentFile().exists())
			 return mFileeee.mkdir();
		else return mFileeee.mkdirs();
	}
	
	/*
	public void loginUsername(View view)
	{
		LinearLayout mainLaun = new LinearLayout(this);
		LayoutInflater.from(this).inflate(R.layout.launcher_user, mainLaun, true);
		replaceFonts(mainLaun);
		
		//edit1 = mainLaun.findViewById(R.id.launcherAccUsername);
		
		new AlertDialog.Builder(this)
			.setTitle("Register with username")
			.setView(mainLaun)
			.show();
		
	}
	*/
	
	// developer methods
	// end dev methods
	public void loginSavedAcc(View view)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.login_select_account);

		if (Tools.enableDevFeatures) {
			/*
			builder.setNegativeButton("Toggle v2", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						int ver = PojavV2ActivityManager.getLauncherRemakeInt(MCLoginActivity.this) == 0 ? 1 : 0;
						PojavV2ActivityManager.setLauncherRemakeVer(MCLoginActivity.this, ver);
						Toast.makeText(MCLoginActivity.this, "Changed to use v" + (ver + 1), Toast.LENGTH_SHORT).show();
					}
				});
				*/		
		}
		
		builder.setPositiveButton(android.R.string.cancel, null);

		final AlertDialog dialog = builder.create();

		/*
		LinearLayout.LayoutParams lpHint, lpFlv;
		
		lpHint = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lpFlv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lpHint.weight = 1;
		lpFlv.weight = 1;
		*/
		LinearLayout dialay = new LinearLayout(this);
		dialay.setOrientation(LinearLayout.VERTICAL);
		TextView fhint = new TextView(this);
		fhint.setText(R.string.hint_select_account);
		// fhint.setLayoutParams(lpHint);
		
		final MFileListView flv = new MFileListView(this, dialog);
		// flv.setLayoutParams(lpFlv);
		
		flv.listFileAt(Tools.mpProfiles);
		flv.setFileSelectedListener(new MFileSelectedListener(){

				@Override
				public void onFileLongClick(final File file, String path, String name, String extension)
				{
					AlertDialog.Builder builder2 = new AlertDialog.Builder(PojavLoginActivity.this);
					builder2.setTitle(name);
					builder2.setMessage(R.string.warning_remove_account);
					builder2.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								// TODO: Implement this method
								file.delete();
								flv.refreshPath();
							}
						});
					builder2.setNegativeButton(android.R.string.cancel, null);
					builder2.show();
				}
				@Override
				public void onFileSelected(File file, final String path, String nane, String extension)
				{
					try
					{
						if(MCProfile.load(path).isMojangAccount()){
							MCProfile.updateTokens(PojavLoginActivity.this, path, new RefreshListener(){

									@Override
									public void onFailed(Throwable e)
									{
										Tools.showError(PojavLoginActivity.this, e);
									}

									@Override
									public void onSuccess()
									{
										MCProfile.launch(PojavLoginActivity.this, path);
									}
								});
						} else {
							MCProfile.launch(PojavLoginActivity.this, path);
						}
						
						dialog.hide();
						//Tools.throwError(MCLoginActivity.this, new Exception(builder.getAccessToken() + "," + builder.getUUID() + "," + builder.getNickname() + "," + builder.getEmail() + "," + builder.getPassword()));
					}
					catch (Exception e)
					{
						Tools.showError(PojavLoginActivity.this, e);
					}
				}
			});
		dialay.addView(fhint);
		dialay.addView(flv);
		
		dialog.setView(dialay);
		dialog.show();
	}
	
	private MCProfile.Builder loginOffline() {
		new File(Tools.mpProfiles).mkdir();
		
		String text = edit2.getText().toString();
		if(text.isEmpty()){
			edit2.setError(getResources().getString(R.string.global_error_field_empty));
		} else if(text.length() <= 2){
			edit2.setError(getResources().getString(R.string.login_error_short_username));
		} else if(new File(Tools.mpProfiles + "/" + text).exists()){
			edit2.setError(getResources().getString(R.string.login_error_exist_username));
		} else{
			MCProfile.Builder builder = new MCProfile.Builder();
			builder.setIsMojangAccount(false);
			builder.setUsername(text);
			
			return builder;
		}
		return null;
	}
	
	private MCProfile.Builder mProfile = null;
	private AlertDialog warning;
	public void loginMC(final View v)
	{
		/*skip it

		String proFilePath = MCProfile.build(builder);
		MCProfile.launchWithProfile(this, proFilePath);
		end skip*/
		
		if (sOffline.isChecked()) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(R.string.warning_title);
			alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						sRemember.setChecked(true);
						mProfile = loginOffline();
						playProfile();
					}
				});
			
			alert.setNegativeButton(R.string.login_offline_alert_skip, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						mProfile = loginOffline();
						playProfile();
					}
				});
			
			alert.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						mProfile = null;
					}
				});
			
			if (!sRemember.isChecked()) {
				alert.setMessage(R.string.login_offline_warning_1);
				warning = alert.show();
			} else {
				mProfile = loginOffline();
				playProfile();
			}
			
			/*
			while (warning != null && warning.isShowing()) {
			}
			*/
		} else {
			new LoginTask().setLoginListener(new LoginListener(){

					@Override
					public void onBeforeLogin()
					{
						// TODO: Implement this method
						v.setEnabled(false);
						prb.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoginDone(String[] result)
					{
						// TODO: Implement this method
						if(result[0].equals("ERROR")){
							Tools.dialogOnUiThread(PojavLoginActivity.this, getResources().getString(R.string.error_title), strArrToString(result));
						} else{
							MCProfile.Builder builder = new MCProfile.Builder();
							builder.setAccessToken(result[1]);
							builder.setClientID(result[2]);
							builder.setProfileID(result[3]);
							builder.setUsername(result[4]);
							builder.setVersion("1.7.10");

							mProfile = builder;
						}
						v.setEnabled(true);
						prb.setVisibility(View.GONE);
						
						playProfile();
					}
				}).execute(edit2.getText().toString(), edit3.getText().toString());
		}
	}
	
	private void playProfile() {
		if (mProfile != null) {
			String profilePath = null;
			if (sRemember.isChecked()) {
				profilePath = MCProfile.build(mProfile);
			}
			
			MCProfile.launch(PojavLoginActivity.this, profilePath == null ? mProfile : profilePath);
		}
	}
	
	public static String strArrToString(String[] strArr)
	{
		String[] strArrEdit = strArr;
		strArrEdit[0] = "";
		
		String str = Arrays.toString(strArrEdit);
		str = str.substring(1, str.length() - 1).replace(",", "\n");
		
		return str;
	}
    //We are calling this method to check the permission status
    private boolean isStorageAllowed() {
		//Getting the permission status
		int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

		//If permission is granted returning true
		return result1 == PackageManager.PERMISSION_GRANTED &&
			result2 == PackageManager.PERMISSION_GRANTED;
    }

    //Requesting permission
    private void requestStoragePermission()
	{
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_REQUEST_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == REQUEST_STORAGE_REQUEST_CODE){
			isPromptingGrant = false;
            // isPermGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED;
        }
    }
}
