package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.gson.*;
import com.kdt.filerapi.*;
import com.kdt.filermod.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import net.kdt.pojavlaunch.mcfragments.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.signer.*;
import net.kdt.pojavlaunch.util.*;
import net.kdt.pojavlaunch.value.*;
import org.lwjgl.opengl.*;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import dalvik.system.*;
import java.lang.reflect.*;
import net.kdt.pojavlaunch.patcher.*;
import android.graphics.*;
import android.content.pm.*;
import optifine.*;
//import android.support.v7.view.menu.*;
//import net.zhuoweizhang.boardwalk.downloader.*;

public class MCLauncherActivity extends AppCompatActivity
{
	//private FragmentTabHost mTabHost;
	private LinearLayout fullTab;
	private ViewPager viewPager;
	private TabLayout tabLayout;

	private TextView tvVersion, tvUsernameView;
	private Spinner versionSelector;
	private String[] availableVersions = Tools.versionList;
	private MCProfile.Builder profile;
	private String profilePath = null;
	private CrashFragment crashView;
	private ConsoleFragment consoleView;
	private ViewPagerAdapter viewPageAdapter;

	private ProgressBar launchProgress;
	private TextView launchTextStatus;
	private Button switchUsrBtn, logoutBtn; // MineButtons
	private ViewGroup leftView, rightView;
	private Button playButton;

	private Gson gson;

	private JMinecraftVersionList versionList;
	private static volatile boolean isAssetsProcessing = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		gson = new Gson();

		DisplayMetrics dm = Tools.getDisplayMetrics(this);
		AndroidDisplay.windowWidth = dm.widthPixels;
		AndroidDisplay.windowHeight = dm.heightPixels;
		viewInit();
		
	   final View decorView = getWindow().getDecorView();
       decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
        @Override
        public void onSystemUiVisibilityChange(int visibility) {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
    });	
		
		
	}
	// DEBUG
	//new android.support.design.widget.NavigationView(this);

	private String getStr(int id, Object... val) {
		if (val != null && val.length > 0) {
			return getResources().getString(id, val);
		} else {
			return getResources().getString(id);
		}
	}

	private void viewInit() {
		setContentView(R.layout.launcher_main);

		fullTab = (LinearLayout) findViewById(R.id.launchermainFragmentTabView);
		tabLayout = (TabLayout) findViewById(R.id.launchermainTabLayout);
		viewPager = (ViewPager) findViewById(R.id.launchermainTabPager);

		consoleView = new ConsoleFragment();
		crashView = new CrashFragment();

		viewPageAdapter = new ViewPagerAdapter(getSupportFragmentManager());

		viewPageAdapter.addFragment(new LauncherFragment(), getStr(R.string.mcl_tab_news));
		viewPageAdapter.addFragment(consoleView, getStr(R.string.mcl_tab_console));
		viewPageAdapter.addFragment(crashView, getStr(R.string.mcl_tab_crash));

		viewPager.setAdapter(viewPageAdapter);
		tabLayout.setupWithViewPager(viewPager);

		tvUsernameView = (TextView) findId(R.id.launcherMainUsernameView);
		tvVersion = (TextView) findId(R.id.launcherMainVersionView);

		try {
			profilePath = PojavProfile.getCurrentProfilePath(this);
			profile = PojavProfile.getCurrentProfileContent(this);

			tvUsernameView.setText(profile.getUsername());
		} catch(Exception e) {
			//Tools.throwError(this, e);
			e.printStackTrace();
			toast(getStr(R.string.toast_login_error, e.getMessage()));
			finish();
		}

		//showProfileInfo();

		List<String> versions = new ArrayList<String>();
		final File fVers = new File(Tools.versnDir);

		try {
			if (fVers.listFiles().length < 1) {
				throw new Exception(getStr(R.string.error_no_version));
			}

			for (File fVer : fVers.listFiles()) {
				versions.add(fVer.getName());
			}
		} catch (Exception e) {
			versions.add(getStr(R.string.global_error) + ":");
			versions.add(e.getMessage());

		} finally {
			availableVersions = versions.toArray(new String[0]);
		}

		//availableVersions;

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, availableVersions);
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		versionSelector = (Spinner) findId(R.id.launcherMainSelectVersion);
		versionSelector.setAdapter(adapter);

		launchProgress = (ProgressBar) findId(R.id.progressDownloadBar);
		launchTextStatus = (TextView) findId(R.id.progressDownloadText);
		LinearLayout exitLayout = (LinearLayout) findId(R.id.launcherMainExitbtns);
		switchUsrBtn = (Button) exitLayout.getChildAt(0);
		logoutBtn = (Button) exitLayout.getChildAt(1);

		leftView = (LinearLayout) findId(R.id.launcherMainLeftLayout);
		playButton = (Button) findId(R.id.launcherMainPlayButton);
		rightView = (ViewGroup) findId(R.id.launcherMainRightLayout);

		statusIsLaunching(false);
	}

	public class RefreshVersionListTask extends AsyncTask<Void, Void, ArrayList<String>>{

		@Override
		protected ArrayList<String> doInBackground(Void[] p1)
		{
			try{
				versionList = gson.fromJson(DownloadUtils.downloadString("https://launchermeta.mojang.com/mc/game/version_manifest.json"), JMinecraftVersionList.class);
				ArrayList<String> versionStringList = filter(versionList.versions, new File(Tools.versnDir).listFiles());

				return versionStringList;
			} catch (Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result)
		{
			super.onPostExecute(result);

			final PopupMenu popup = new PopupMenu(MCLauncherActivity.this, versionSelector);  
			popup.getMenuInflater().inflate(R.menu.menu_versionopt, popup.getMenu());  

			if(result != null && result.size() > 0) {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(MCLauncherActivity.this, android.R.layout.simple_spinner_item, result);
				adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
				versionSelector.setAdapter(adapter);
				versionSelector.setSelection(selectAt(result.toArray(new String[0]), profile.getVersion()));
			} else {
				versionSelector.setSelection(selectAt(availableVersions, profile.getVersion()));
			}
			versionSelector.setOnItemSelectedListener(new OnItemSelectedListener(){

					@Override
					public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
					{
						String version = p1.getItemAtPosition(p3).toString();
						profile.setVersion(version);

						PojavProfile.setCurrentProfile(MCLauncherActivity.this, profile);
						if (PojavProfile.isFileType(MCLauncherActivity.this)) {
							PojavProfile.setCurrentProfile(MCLauncherActivity.this, MCProfile.build(profile));
						}

						tvVersion.setText(getStr(R.string.mcl_version_msg, version));
					}

					@Override
					public void onNothingSelected(AdapterView<?> p1)
					{
						// TODO: Implement this method
					}
				});
			versionSelector.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
					@Override
					public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
					{
						// Implement copy, remove, reinstall,...
						popup.show();
						return true;
					}
				});

			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
					public boolean onMenuItemClick(MenuItem item) {  
						return true;  
					}  
				});  

			tvVersion.setText(getStr(R.string.mcl_version_msg) + versionSelector.getSelectedItem());
		}
	}

	@Override
	protected void onPostResume()
	{
		super.onPostResume();
	}

	private float updateWidthHeight() {
		float leftRightWidth = (float) AndroidDisplay.windowWidth / 100f * 32f;
		float playButtonWidth = AndroidDisplay.windowWidth - leftRightWidth * 2f;
		LinearLayout.LayoutParams leftRightParams = new LinearLayout.LayoutParams((int) leftRightWidth, (int) Tools.dpToPx(this, AndroidDisplay.windowHeight / 9));
		LinearLayout.LayoutParams playButtonParams = new LinearLayout.LayoutParams((int) playButtonWidth, (int) Tools.dpToPx(this, AndroidDisplay.windowHeight / 9));
		leftView.setLayoutParams(leftRightParams);
		rightView.setLayoutParams(leftRightParams);
		playButton.setLayoutParams(playButtonParams);

		return leftRightWidth;
	}

	private JMinecraftVersionList.Version findVersion(String version) {
		if (versionList != null) {
			for (JMinecraftVersionList.Version valueVer: versionList.versions) {
				if (valueVer.id.equals(version)) {
					return valueVer;
				}
			}
		}

		// Custom version, inherits from base.
		return Tools.getVersionInfo(version);
	}

	private ArrayList<String> filter(JMinecraftVersionList.Version[] list1, File[] list2) {
		ArrayList<String> output = new ArrayList<String>();

		for (JMinecraftVersionList.Version value1: list1) {
			if ((value1.type.equals("release") && LauncherPreferences.PREF_VERTYPE_RELEASE) ||
				(value1.type.equals("snapshot") && LauncherPreferences.PREF_VERTYPE_SNAPSHOT) ||
				(value1.type.equals("old_alpha") && LauncherPreferences.PREF_VERTYPE_OLDALPHA) ||
				(value1.type.equals("old_beta") && LauncherPreferences.PREF_VERTYPE_OLDBETA)) {
					output.add(value1.id);
			}
		}

		for (File value2: list2) {
			if (!output.contains(value2.getName())) {
				output.add(value2.getName());
			}
		}

		return output;
	}

	private void toast(final String str) {
		runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					Toast.makeText(MCLauncherActivity.this, str, Toast.LENGTH_SHORT).show();
				}
			});
	}

	public void mcaccSwitchUser(View view)
	{
		showProfileInfo();
	}

	public void mcaccLogout(View view)
	{
		//PojavProfile.reset();
		finish();
	}

	private void showProfileInfo()
	{
		/*
		 new AlertDialog.Builder(this)
		 .setTitle("Info player")
		 .setMessage(
		 "AccessToken=" + profile.getAccessToken() + "\n" +
		 "ClientID=" + profile.getClientID() + "\n" +
		 "ProfileID=" + profile.getProfileID() + "\n" +
		 "Username=" + profile.getUsername() + "\n" +
		 "Version=" + profile.getVersion()
		 ).show();
		 */
	}

	private void selectTabPage(int pageIndex){
		if (tabLayout.getSelectedTabPosition() != pageIndex) {
			tabLayout.setScrollPosition(pageIndex,0f,true);
			viewPager.setCurrentItem(pageIndex);
		}
	}

	@Override
	protected void onResumeFragments()
	{
		super.onResumeFragments();
		new RefreshVersionListTask().execute();
		
		try{
			final ProgressDialog barrier = new ProgressDialog(this);
			barrier.setMessage("Waiting");
			barrier.setProgressStyle(barrier.STYLE_SPINNER);
			barrier.setCancelable(false);
			barrier.show();

			new Thread(new Runnable(){

					@Override
					public void run()
					{
						while (consoleView == null) {
							try {
								Thread.sleep(20);
							} catch (Throwable th) {}
						}

						try {
							Thread.sleep(100);
						} catch (Throwable th) {}

						runOnUiThread(new Runnable() {
								@Override
								public void run()
								{
									try {
										consoleView.putLog("");
										barrier.dismiss();
									} catch (Throwable th) {
										startActivity(getIntent());
										finish();
									}
								}
							});
					}
				}).start();

			File lastCrashFile = Tools.lastFileModified(Tools.crashPath);
			if(CrashFragment.isNewCrash(lastCrashFile) || !crashView.getLastCrash().isEmpty()){
				crashView.resetCrashLog = false;
				selectTabPage(2);
			} else throw new Exception();
		} catch(Throwable e){
			selectTabPage(tabLayout.getSelectedTabPosition());
		}
	}

	public int selectAt(String[] strArr, String select)
	{
		int count = 0;
		for(String str : strArr){
			if(str.equals(select)){
				return count;
			}
			else{
				count++;
			}
		}
		return -1;
	}

	@Override
	protected void onResume(){
		super.onResume();
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
	}

	private boolean canBack = false;
	private void statusIsLaunching(boolean isLaunching)
	{
		LinearLayout.LayoutParams reparam = new LinearLayout.LayoutParams((int) updateWidthHeight(), LinearLayout.LayoutParams.WRAP_CONTENT);
		ViewGroup.MarginLayoutParams lmainTabParam = (ViewGroup.MarginLayoutParams) fullTab.getLayoutParams();
		int launchVisibility = isLaunching ? View.VISIBLE : View.GONE;
		launchProgress.setVisibility(launchVisibility);
		launchTextStatus.setVisibility(launchVisibility);
		lmainTabParam.bottomMargin = reparam.height;
		leftView.setLayoutParams(reparam);

		switchUsrBtn.setEnabled(!isLaunching);
		logoutBtn.setEnabled(!isLaunching);
		versionSelector.setEnabled(!isLaunching);
		canBack = !isLaunching;
	}
	@Override
	public void onBackPressed()
	{
		if (canBack) {
			super.onBackPressed();
		}
	}
	
	// Catching touch exception
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			return super.onTouchEvent(event);
		} catch (Throwable th) {
			Tools.showError(this, th);
			return false;
		}
	}

	private GameRunnerTask mTask;

	public void launchGame(View v)
	{
		if (!canBack && isAssetsProcessing) {
			isAssetsProcessing = false;
			statusIsLaunching(false);
		} else if (canBack) {
			v.setEnabled(false);
			mTask = new GameRunnerTask();
			mTask.execute(profile.getVersion());
			crashView.resetCrashLog = true;
		}
	}

	public class GameRunnerTask extends AsyncTask<String, String, Throwable>
	{
		private String convertStr;
		private boolean launchWithError = false;

		@Override
		protected void onPreExecute()
		{
			launchProgress.setMax(39);
			statusIsLaunching(true);
		}

		private int maxSubProgress = 1;
		private int valSubProgress = 1;
		@Override
		protected Throwable doInBackground(final String[] p1)
		{
			Throwable throwable = null;
			final StringBuilder currentLog = new StringBuilder();
			LoggerJava.LoggerOutputStream logOut = new LoggerJava.LoggerOutputStream(System.out, new LoggerJava.OnStringPrintListener(){
					@Override
					public void onCharPrint(char c)
					{
						currentLog.append(c);
					}
				});
			LoggerJava.LoggerOutputStream logErr = new LoggerJava.LoggerOutputStream(System.err, new LoggerJava.OnStringPrintListener(){
					@Override
					public void onCharPrint(char c)
					{
						currentLog.append(c);
					}
				});
			System.setOut(new PrintStream(logOut));
			System.setErr(new PrintStream(logErr));

			try {
				final String downVName = "/" + p1[0] + "/" + p1[0];

				//Downloading libraries
				String inputPath = Tools.versnDir + downVName + "_orig.jar";
				String unpatchedPath = Tools.versnDir + downVName + "_unpatched.jar";
				String patchedFile = Tools.versnDir + downVName + ".jar";

				JMinecraftVersionList.Version verInfo;
				
				try {
					//com.pojavdx.dx.mod.Main.debug = true;

					String verJsonDir = Tools.versnDir + downVName + ".json";

					verInfo = findVersion(p1[0]);

					if (verInfo.url != null) {
						publishProgress("5", "Downloading " + p1[0] + " configuration...");
						Tools.downloadFile(
							verInfo.url,
							verJsonDir,
							true
						);
					}

					zeroProgress();

					verInfo = Tools.getVersionInfo(p1[0]);

					DependentLibrary[] libList = verInfo.libraries;
					setMax(libList.length * 2 + 5);

					String libPathURL;
					File outUndexLib, outDexedLib, outUnpatchedConvert;

					for (final DependentLibrary libItem: libList) {

						if (libItem.name.startsWith("com.google.code.gson:gson") ||
							libItem.name.startsWith("com.mojang:realms") ||
							libItem.name.startsWith("net.java.jinput") ||
							libItem.name.startsWith("net.minecraft.launchwrapper") ||
							libItem.name.startsWith("optifine:launchwrapper-of") ||
							// libItem.name.startsWith("org.lwjgl.lwjgl:lwjgl") ||
							libItem.name.startsWith("org.lwjgl") ||
							libItem.name.startsWith("tv.twitch")
							) { // Black list
							publishProgress("1", "Ignored " + libItem.name);
							//Thread.sleep(100);
						} else {
							currentLog.setLength(0);

							String[] libInfo = libItem.name.split(":");
							String libArtifact = Tools.artifactToPath(libInfo[0], libInfo[1], libInfo[2]);
							outUndexLib = new File(Tools.libraries + "/" + libArtifact.replace(".jar", "_orig.jar"));
							outUndexLib.getParentFile().mkdirs();

							outDexedLib = new File(Tools.libraries + "/" + libArtifact); // Don't add ".jar"
							if (!outDexedLib.exists()) {
								publishProgress("1", getStr(R.string.mcl_launch_download_lib, libItem.name));

								boolean skipIfFailed = false;

								if (libItem.downloads == null) {
									MinecraftLibraryArtifact artifact = new MinecraftLibraryArtifact();
									artifact.url = "https://libraries.minecraft.net/" + libArtifact;
									libItem.downloads = new DependentLibrary.LibraryDownloads(artifact);

									skipIfFailed = true;
								}
								try {
									libPathURL = libItem.downloads.artifact.url;
									if (!outUndexLib.exists()) {
										//toast(outUndexLib.getAbsolutePath());
										Tools.downloadFile(
											libPathURL,
											outUndexLib.getAbsolutePath(),
											true
										);
									}
								} catch (Throwable th) {
									if (!skipIfFailed) {
										throw th;
									} else {
										th.printStackTrace();
									}
								}

								convertStr = getStr(R.string.mcl_launch_convert_lib, libItem.name);
								publishProgress("1", convertStr);

								boolean isOptifine = libItem.name.startsWith(Tools.optifineLib);
								Tools.runDx(MCLauncherActivity.this, outUndexLib.getAbsolutePath(), outDexedLib.getAbsolutePath(), isOptifine , new PojavDXManager.Listen(){

										@Override
										public void onReceived(String step, int maxProg, int currProg)
										{
											maxSubProgress = maxProg;
											valSubProgress = currProg;
											publishProgress("0", convertStr + ": (" + currProg + "/" + maxProg + ") " + step, "");
										}
									});
								/*
								 if (!new File(outDexedLib + "/finish").exists()) {
								 toast("Unable to convert library " + libItem.name + " but still continue. Is it a wrong check?");
								 //throw new RuntimeException("Unable to convert library " + libItem.name);
								 }
								 */

								if (!outDexedLib.exists()) {
									RuntimeException dxError = new RuntimeException(getResources().getString(R.string.error_convert_lib, libItem.name) + "\n" + currentLog.toString());
									dxError.setStackTrace(new StackTraceElement[0]);
									throw dxError;
								}

								outUndexLib.delete();
							}
						}
					}

					publishProgress("5", getStr(R.string.mcl_launch_download_client, p1[0]));
					outUnpatchedConvert = new File(unpatchedPath);
					boolean patchedExist = new File(patchedFile).exists();
					if (!patchedExist) {
						if (!outUnpatchedConvert.exists()) {
							if (!new File(inputPath).exists()) {
								currentLog.setLength(0);

								Tools.downloadFile(
									verInfo.downloads.values().toArray(new MinecraftClientInfo[0])[0].url,
									inputPath,
									true
								);
							}

							convertStr = getStr(R.string.mcl_launch_convert_client, p1[0]);
							publishProgress("5", convertStr);
							addProgress = 0;
							Tools.runDx(MCLauncherActivity.this, inputPath, outUnpatchedConvert.getAbsolutePath(), true, new PojavDXManager.Listen(){

									@Override
									public void onReceived(String step, int maxProg, int currProg)
									{
										maxSubProgress = maxProg;
										valSubProgress = currProg;
										publishProgress("0", convertStr + " (" + currProg + "/" + maxProg + ") " + step, "");
									}
								});
							if (!outUnpatchedConvert.exists()) {
								RuntimeException dxError = new RuntimeException(getResources().getString(R.string.error_convert_client) + "\n" + currentLog.toString());
								dxError.setStackTrace(new StackTraceElement[0]);
								throw dxError;
							}

							patchAndCleanJar(p1[0], outUnpatchedConvert.getAbsolutePath(), patchedFile);
							outUnpatchedConvert.delete();
						} else {
							patchAndCleanJar(p1[0], outUnpatchedConvert.getAbsolutePath(), patchedFile);
							outUnpatchedConvert.delete();
						}
					}
				} catch (Throwable e) {
					launchWithError = true;
					throw e;
				}

				publishProgress("7", getStr(R.string.mcl_launch_cleancache));
				// new File(inputPath).delete();

				for (File f : new File(Tools.versnDir).listFiles()) {
					if(f.getName().endsWith(".part")) {
						Log.d(Tools.APP_NAME, "Cleaning cache: " + f);
						f.delete();
					}
				}

				isAssetsProcessing = true;
				playButton.post(new Runnable(){

						@Override
						public void run()
						{
							playButton.setText("Skip");
							playButton.setEnabled(true);
						}
					});
				publishProgress("9", getStr(R.string.mcl_launch_download_assets));
				try {
					downloadAssets(verInfo.assets, new File(Tools.ASSETS_PATH));
				} catch (Exception e) {
					// Ignore it
					launchWithError = false;
				} finally {
					isAssetsProcessing = false;
				}
			} catch (Throwable th){
				throwable = th;
			} finally {
				System.setErr(logErr.getRootStream());
				System.setOut(logOut.getRootStream());
				return throwable;
			}
		}
		private int addProgress = 0; // 34

		public void zeroProgress()
		{
			addProgress = 0;
		}

		public void setMax(final int value)
		{
			launchProgress.post(new Runnable(){

					@Override
					public void run()
					{
						launchProgress.setMax(value);
					}
				});
		}

		private void patchAndCleanJar(String version, String in, String out) throws Exception {
			publishProgress("1", getStr(R.string.mcl_launch_patch_client, version));
			JarSigner.sign(in, out);
			new File(in).delete();

			// Tools.clearDuplicateFiles(new File(out).getParentFile());
		}

		@Override
		protected void onProgressUpdate(String... p1)
		{
			int addedProg = Integer.parseInt(p1[0]);
			if (addedProg != -1) {
				addProgress = addProgress + addedProg;
				launchProgress.setProgress(addProgress);

				launchTextStatus.setText(p1[1]);
			}

			if (p1.length < 3) consoleView.putLog(p1[1] + (p1.length < 3 ? "\n" : ""));
		}

		@Override
		protected void onPostExecute(Throwable p1)
		{
			playButton.setText("Play");
			playButton.setEnabled(true);
			launchProgress.setMax(100);
			launchProgress.setProgress(0);
			statusIsLaunching(false);
			if(p1 != null) {
				p1.printStackTrace();
				Tools.showError(MCLauncherActivity.this, p1);
			}
			if(!launchWithError) {
				crashView.setLastCrash("");

				try {
					/*
					 List<String> jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
					 jvmArgs.add("-Xms128M");
					 jvmArgs.add("-Xmx1G");
					 */
					Intent mainIntent = new Intent(MCLauncherActivity.this, MainActivity.class);
					// mainIntent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
					mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
					mainIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					if (PojavPreferenceActivity.PREF_FREEFORM) {
						DisplayMetrics dm = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(dm);

						ActivityOptions options = (ActivityOptions) ActivityOptions.class.getMethod("makeBasic").invoke(null);
						Rect freeformRect = new Rect(0, 0, dm.widthPixels / 2, dm.heightPixels / 2);
						options.getClass().getDeclaredMethod("setLaunchBounds", Rect.class).invoke(options, freeformRect);
						startActivity(mainIntent, options.toBundle());
					} else {
						startActivity(mainIntent);
					}
				}
				catch (Throwable e) {
					Tools.showError(MCLauncherActivity.this, e);
				}

				/*
				 FloatingIntent maini = new FloatingIntent(MCLauncherActivity.this, MainActivity.class);
				 maini.startFloatingActivity();
				 */
			}

			mTask = null;
		}

		private Gson gsonss = gson;
		public static final String MINECRAFT_RES = "http://resources.download.minecraft.net/";

		public JAssets downloadIndex(String versionName, File output) throws Exception {
			String versionJson = DownloadUtils.downloadString("http://s3.amazonaws.com/Minecraft.Download/indexes/" + versionName + ".json");
			JAssets version = gsonss.fromJson(versionJson, JAssets.class);
			output.getParentFile().mkdirs();
			Tools.write(output.getAbsolutePath(), versionJson.getBytes(Charset.forName("UTF-8")));
			return version;
		}
		public void downloadAsset(JAssetInfo asset, File objectsDir) throws IOException, Throwable {
			String assetPath = asset.hash.substring(0, 2) + "/" + asset.hash;
			File outFile = new File(objectsDir, assetPath);
			if (!outFile.exists()) {
				DownloadUtils.downloadFile(MINECRAFT_RES + assetPath, outFile);
			}
		}

		public void downloadAssets(String assetsVersion, File outputDir) throws IOException, Throwable {
			File hasDownloadedFile = new File(outputDir, "downloaded/" + assetsVersion + ".downloaded");
			if (!hasDownloadedFile.exists()) {
				System.out.println("Assets begin time: " + System.currentTimeMillis());
				JAssets assets = downloadIndex(assetsVersion, new File(outputDir, "indexes/" + assetsVersion + ".json"));
				Map<String, JAssetInfo> assetsObjects = assets.objects;
				launchProgress.setMax(assetsObjects.size());
				zeroProgress();
				File objectsDir = new File(outputDir, "objects");
				int downloadedSs = 0;
				for (JAssetInfo asset : assetsObjects.values()) {
					if (!isAssetsProcessing) {
						return;
					}

					downloadAsset(asset, objectsDir);
					publishProgress("1", getStr(R.string.mcl_launch_downloading, assetsObjects.keySet().toArray(new String[0])[downloadedSs]));
					downloadedSs++;
				}
				hasDownloadedFile.getParentFile().mkdirs();
				hasDownloadedFile.createNewFile();
				System.out.println("Assets end time: " + System.currentTimeMillis());
			}
		}
	}
	public View findId(int id)
	{
		return findViewById(id);
	}
	private void mkToast(final String str)
	{
		runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(MCLauncherActivity.this, str, Toast.LENGTH_LONG).show();
				}
			});
	}

	public void launcherMenu(View view)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.mcl_options);
		builder.setItems(R.array.mcl_options, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					switch(p2){
						case 0:{ // Mods manager
								modManager();
							} break;
						case 1:{ // OptiFine installer
								installOptiFine();
							} break;
						case 2:{ // Custom controls
								if (Tools.enableDevFeatures) {
									startActivity(new Intent(MCLauncherActivity.this, CustomControlsActivity.class));
								}
							} break;
						case 3:{ // Settings
								startActivity(new Intent(MCLauncherActivity.this, LauncherPreferenceActivity.class));
							} break;
						case 4:{ // About
								final AlertDialog.Builder aboutB = new AlertDialog.Builder(MCLauncherActivity.this);
								aboutB.setTitle(R.string.mcl_option_about);
								try
								{
									aboutB.setMessage(String.format(Tools.read(getAssets().open("about_en.txt")),
																	Tools.APP_NAME,
																	Tools.usingVerName,
																	org.lwjgl.Sys.getVersion())
													  );
								} catch (Exception e) {
									throw new RuntimeException(e);
								}
								aboutB.setPositiveButton(android.R.string.ok, null);
								aboutB.show();
							} break;
					}
				}
			});
		builder.show();
	}

	public void modManager()
	{
		/*
		File file1 = new File(Tools.mpModEnable);
		File file2 = new File(Tools.mpModDisable);
		File file3 = new File(Tools.mpModAddNewMo);
		file1.mkdirs();
		file2.mkdir();
		try
		{
			file3.createNewFile();
		}
		catch (IOException e){}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Mods manager (Forge)");
		builder.setPositiveButton(android.R.string.cancel, null);

		AlertDialog dialog = builder.create();

		MFileListView flv = new MFileListView(this, dialog);
		flv.listFileAt(Tools.datapath + "/ModsManager");
		flv.setFileSelectedListener(new MFileSelectedListener(){

				@Override
				public void onFileLongClick(File file, String path, String nane, String extension)
				{
					// TODO: Implement this method
				}
				@Override
				public void onFileSelected(File file, String path, String nane, String extension)
				{
					// TODO: Implement this method
					if(extension.equals(".jar")) {

					} else {
						openSelectMod();
					}
				}
			});
		dialog.setView(flv);
		dialog.show();
		*/
		
		Tools.dialogOnUiThread(this, "Mods manager", "This feature is not yet supported!");
	}

	public void openSelectMod()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alerttitle_installmod);
		builder.setPositiveButton(android.R.string.cancel, null);

		AlertDialog dialog = builder.create();
		FileListView flv = new FileListView(this);

		dialog.setView(flv);
		dialog.show();
	}

	private void installOptiFine() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alerttitle_installoptifine);
		builder.setPositiveButton(android.R.string.cancel, null);

		final AlertDialog dialog = builder.create();
		FileListView flv = new FileListView(this);
		flv.setFileSelectedListener(new FileSelectedListener(){

				@Override
				public void onFileSelected(File file, String path, String name) {
					if (name.endsWith(".jar")) {
						doInstallOptiFine(file);
						dialog.dismiss();
					}
				}
			});
		dialog.setView(flv);
		dialog.show();
	}

	private void doInstallOptiFine(File optifineFile) {
		new OptiFineInstaller().execute(optifineFile);
	}

	private class OptiFineInstaller extends AsyncTask<File, String, Throwable>
	{
		private ProgressDialog dialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(MCLauncherActivity.this);
			dialog.setTitle("Installing OptiFine");
			dialog.setMessage("Preparing");
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setMax(5);
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected Throwable doInBackground(File[] file) {
			final StringBuilder currentLog = new StringBuilder();
			LoggerJava.LoggerOutputStream logOut = new LoggerJava.LoggerOutputStream(System.out, new LoggerJava.OnStringPrintListener(){
					@Override
					public void onCharPrint(char c)
					{
						currentLog.append(c);
					}
				});
			LoggerJava.LoggerOutputStream logErr = new LoggerJava.LoggerOutputStream(System.err, new LoggerJava.OnStringPrintListener(){
					@Override
					public void onCharPrint(char c)
					{
						currentLog.append(c);
					}
				});
			Throwable throwable = null;
			File convertedFile = null;
			try {
				publishProgress("Preparing", "5");
				
				String origMd5 = OptiFinePatcher.calculateMD5(file[0]);
				convertedFile = new File(Tools.optifineDir, origMd5 + ".jar");
				if (!convertedFile.exists()) {
					publishProgress("Patching OptiFine Installer", null, "1", "true");

					Tools.extractAssetFolder(MCLauncherActivity.this, "optifine_patch", Tools.optifineDir, true);

					new File(Tools.optifineDir + "/optifine_patch/AndroidOptiFineUtilities.class.patch").delete();

					String[] output = Tools.patchOptifineInstaller(MCLauncherActivity.this, file[0]);
					File patchedFile = new File(output[1]);

					publishProgress("Converting OptiFine", null, null, "false");

					System.setOut(new PrintStream(logOut));
					System.setErr(new PrintStream(logErr));

					Tools.runDx(MCLauncherActivity.this, patchedFile.getAbsolutePath(), convertedFile.getAbsolutePath(), true, new PojavDXManager.Listen(){
							@Override
							public void onReceived(String msg, int max, int current)
							{
								publishProgress("Converting OptiFine: " + msg, Integer.toString(max), null);
							}
					});

					if (!convertedFile.exists()) {
						RuntimeException dxError = new RuntimeException(getResources().getString(R.string.error_convert_lib, "OptiFine") + "\n" + currentLog.toString());
						dxError.setStackTrace(new StackTraceElement[0]);
						throw dxError;
					}

					patchedFile.delete();
				}

				publishProgress("Launching OptiFine installer", null, null, "true");

				File optDir = getDir("dalvik-cache", 0);
				optDir.mkdir();

				DexClassLoader loader = new DexClassLoader(convertedFile.getAbsolutePath(), optDir.getAbsolutePath(), getApplicationInfo().nativeLibraryDir, getClass().getClassLoader());
				AndroidOptiFineUtilities.originalOptifineJar = convertedFile.getAbsolutePath();

				Class installerClass = loader.loadClass("optifine.AndroidInstaller");
				Method installerMethod = installerClass.getDeclaredMethod("doInstall", File.class);
				installerMethod.invoke(null, new File(Tools.MAIN_PATH));

				publishProgress("(4/5) Patching OptiFine Tweaker", null, null);
				File optifineLibFile = new File(AndroidOptiFineUtilities.optifineOutputJar);
				if (!optifineLibFile.exists()) {
					throw new FileNotFoundException(optifineLibFile.getAbsolutePath() + "\n\n--- OptiFine installer log ---\n" + currentLog.toString());
				}
				new OptiFinePatcher(optifineLibFile).saveTweaker();
				convertedFile.delete();
				
				publishProgress("(5/5) Done!", null, null);
				Thread.sleep(500);
			} catch (Throwable th) {
				throwable = th;
			} finally {
				System.setOut(logOut.getRootStream());
				System.setErr(logErr.getRootStream());
				/*
				 if (throwable != null && convertedFile != null) {
				 convertedFile.delete();
				 }
				 */
				return throwable;
			}
		}
/*
		private Object fromConfig(DexClassLoader loader, String name) throws ReflectiveOperationException {
			Field f = loader.loadClass("Config").getDeclaredField(name);
			f.setAccessible(true);
			return f.get(null);
		}
*/
		@Override
		protected void onProgressUpdate(String[] text) {
			super.onProgressUpdate(text);
			dialog.setMessage(text[0]);
			if (text.length > 1 && text[1] != null) {
				dialog.setMax(Integer.valueOf(text[1]));
			} if (text.length > 2) {
				dialog.setProgress(dialog.getProgress() + 1);
			} if (text.length > 3 && text[3] != null) {
				dialog.setIndeterminate(Boolean.getBoolean(text[3]));
			}
		}

		@Override
		protected void onPostExecute(Throwable th) {
			super.onPostExecute(th);
			dialog.dismiss();

			new RefreshVersionListTask().execute();

			if (th == null) {
				Toast.makeText(MCLauncherActivity.this, R.string.toast_optifine_success, Toast.LENGTH_SHORT).show();
			} else {
				Tools.showError(MCLauncherActivity.this, th);
			}
		}
	}

	private class ViewPagerAdapter extends FragmentPagerAdapter {

		List<Fragment> fragmentList = new ArrayList<>();
		List<String> fragmentTitles = new ArrayList<>();

		public ViewPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentList.get(position);
		}

		@Override
		public int getCount() {
			return fragmentList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return fragmentTitles.get(position);
		}

		public void addFragment(Fragment fragment, String name) {
			fragmentList.add(fragment);
			fragmentTitles.add(name);
		}

		public void setFragment(int index, Fragment fragment, String name) {
			fragmentList.set(index, fragment);
			fragmentTitles.set(index, name);
		}

		public void removeFragment(int index) {
			fragmentList.remove(index);
			fragmentTitles.remove(index);
		}
	}
}
