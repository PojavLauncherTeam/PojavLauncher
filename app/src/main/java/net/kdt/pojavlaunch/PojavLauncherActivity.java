package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.design.widget.VerticalTabLayout.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.gson.*;
import com.kdt.filerapi.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.fragments.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.utils.*;
import net.kdt.pojavlaunch.value.*;
import org.apache.commons.io.*;
import org.lwjgl.glfw.*;

import android.support.v7.app.AlertDialog;
import net.kdt.pojavlaunch.tasks.*;
//import android.support.v7.view.menu.*;
//import net.zhuoweizhang.boardwalk.downloader.*;

public class PojavLauncherActivity extends BaseLauncherActivity
{
    //private FragmentTabHost mTabHost;
    private LinearLayout fullTab, leftTab;
    /*
     private PojavLauncherViewPager viewPager;
     private VerticalTabLayout tabLayout;
     */

    private ViewPager viewPager;
    private VerticalTabLayout tabLayout;

    private TextView tvUsernameView;
    private Spinner accountSelector;
    private String profilePath = null;
    private ViewPagerAdapter viewPageAdapter;

    private Button switchUsrBtn, logoutBtn; // MineButtons
    private ViewGroup leftView, rightView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        viewInit();

        Tools.setFullscreen(this);

        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "Launcher process id: " + android.os.Process.myPid(), Toast.LENGTH_LONG).show();
        }
    }
    // DEBUG
    //new android.support.design.widget.NavigationView(this);

    private void viewInit() {
        setContentView(R.layout.launcher_main_v3);
        // setContentView(R.layout.launcher_main);

        leftTab = findViewById(R.id.launchermain_layout_leftmenu);
        leftTab.setLayoutParams(new LinearLayout.LayoutParams(
            CallbackBridge.windowWidth / 4,
            LinearLayout.LayoutParams.MATCH_PARENT));
        
        fullTab = findViewById(R.id.launchermain_layout_viewpager);
        tabLayout = findViewById(R.id.launchermainTabLayout);
        viewPager = findViewById(R.id.launchermainTabPager);

        mConsoleView = new ConsoleFragment();
        mCrashView = new CrashFragment();

        viewPageAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPageAdapter.addFragment(new LauncherFragment(), R.drawable.ic_menu_news, getString(R.string.mcl_tab_news));
        viewPageAdapter.addFragment(mConsoleView, R.drawable.ic_menu_java, getString(R.string.mcl_tab_console));
        viewPageAdapter.addFragment(mCrashView, 0, getString(R.string.mcl_tab_crash));
        viewPageAdapter.addFragment(new LauncherPreferenceFragment(), R.drawable.ic_menu_settings, getString(R.string.mcl_option_settings));
        
        viewPager.setAdapter(viewPageAdapter);
        // tabLayout.setTabMode(VerticalTabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setLastTabAsBottom();

        tvUsernameView = (TextView) findViewById(R.id.launcherMainUsernameView);
        mTextVersion = (TextView) findViewById(R.id.launcherMainVersionView);

        try {
            profilePath = PojavProfile.getCurrentProfilePath(this);
            mProfile = PojavProfile.getCurrentProfileContent(this);

            tvUsernameView.setText(mProfile.getUsername());
        } catch(Exception e) {
            //Tools.throwError(this, e);
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.toast_login_error, e.getMessage()), Toast.LENGTH_LONG).show();
            finish();
        }

        File logFile = new File(Tools.MAIN_PATH, "latestlog.txt");
        if (logFile.exists() && logFile.length() < 20480) {
            String errMsg = "Error occurred during initialization of ";
            try {
                String logContent = Tools.read(logFile.getAbsolutePath());
                if (logContent.contains(errMsg + "VM") && 
                    logContent.contains("Could not reserve enough space for")) {
                    OutOfMemoryError ex = new OutOfMemoryError("Java error: " + logContent);
                    ex.setStackTrace(null);
                    Tools.showError(PojavLauncherActivity.this, ex);

                    // Do it so dialog will not shown for second time
                    Tools.write(logFile.getAbsolutePath(), logContent.replace(errMsg + "VM", errMsg + "JVM"));
                }
            } catch (Throwable th) {
                System.err.println("Could not detect java crash");
                th.printStackTrace();
            }
        }

        //showProfileInfo();

        final String[] accountList = new File(Tools.mpProfiles).list();
        
        ArrayAdapter<String> adapterAcc = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accountList);
        adapterAcc.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        accountSelector = (Spinner) findViewById(R.id.launchermain_spinner_account);
        accountSelector.setAdapter(adapterAcc);
        accountSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> p1, View p2, int position, long p4) {
                PojavProfile.setCurrentProfile(PojavLauncherActivity.this, accountList[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> p1) {
                // TODO: Implement this method
            }
        });
        
        List<String> versions = new ArrayList<String>();
        final File fVers = new File(Tools.versnDir);

        try {
            if (fVers.listFiles().length < 1) {
                throw new Exception(getString(R.string.error_no_version));
            }

            for (File fVer : fVers.listFiles()) {
                if (fVer.isDirectory())
                    versions.add(fVer.getName());
            }
        } catch (Exception e) {
            versions.add(getString(R.string.global_error) + ":");
            versions.add(e.getMessage());

        } finally {
            mAvailableVersions = versions.toArray(new String[0]);
        }

        //mAvailableVersions;

        ArrayAdapter<String> adapterVer = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mAvailableVersions);
        adapterVer.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mVersionSelector = (Spinner) findViewById(R.id.launchermain_spinner_version);
        mVersionSelector.setAdapter(adapterVer);

        mLaunchProgress = (ProgressBar) findViewById(R.id.progressDownloadBar);
        mLaunchTextStatus = (TextView) findViewById(R.id.progressDownloadText);
        LinearLayout exitLayout = (LinearLayout) findViewById(R.id.launcherMainExitbtns);
        switchUsrBtn = (Button) exitLayout.getChildAt(0);
        logoutBtn = (Button) exitLayout.getChildAt(1);

        leftView = (LinearLayout) findViewById(R.id.launcherMainLeftLayout);
        mPlayButton = (Button) findViewById(R.id.launcherMainPlayButton);
        rightView = (ViewGroup) findViewById(R.id.launcherMainRightLayout);

        statusIsLaunching(false);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Tools.updateWindowSize(this);
    }

    private float updateWidthHeight() {
        float leftRightWidth = (float) CallbackBridge.windowWidth / 100f * 32f;
        float mPlayButtonWidth = CallbackBridge.windowWidth - leftRightWidth * 2f;
        LinearLayout.LayoutParams leftRightParams = new LinearLayout.LayoutParams((int) leftRightWidth, (int) Tools.dpToPx(this, CallbackBridge.windowHeight / 9));
        LinearLayout.LayoutParams mPlayButtonParams = new LinearLayout.LayoutParams((int) mPlayButtonWidth, (int) Tools.dpToPx(this, CallbackBridge.windowHeight / 9));
        leftView.setLayoutParams(leftRightParams);
        rightView.setLayoutParams(leftRightParams);
        mPlayButton.setLayoutParams(mPlayButtonParams);

        return leftRightWidth;
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
        new RefreshVersionListTask(this).execute();

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
                        while (mConsoleView == null) {
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
                                        mConsoleView.putLog("");
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
            if(CrashFragment.isNewCrash(lastCrashFile) || !mCrashView.getLastCrash().isEmpty()){
                mCrashView.resetCrashLog = false;
                selectTabPage(2);
            } else throw new Exception();
        } catch(Throwable e){
            selectTabPage(tabLayout.getSelectedTabPosition());
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
    }

    private boolean canBack = false;
    public void statusIsLaunching(boolean isLaunching) {
        // As preference fragment put to tab, changes without notice, so need re-load pref
        if (isLaunching) LauncherPreferences.loadPreferences();
        
        LinearLayout.LayoutParams reparam = new LinearLayout.LayoutParams((int) updateWidthHeight(), LinearLayout.LayoutParams.WRAP_CONTENT);
        ViewGroup.MarginLayoutParams lmainTabParam = (ViewGroup.MarginLayoutParams) fullTab.getLayoutParams();
        int launchVisibility = isLaunching ? View.VISIBLE : View.GONE;
        mLaunchProgress.setVisibility(launchVisibility);
        mLaunchTextStatus.setVisibility(launchVisibility);
        lmainTabParam.bottomMargin = reparam.height;
        leftView.setLayoutParams(reparam);

        switchUsrBtn.setEnabled(!isLaunching);
        logoutBtn.setEnabled(!isLaunching);
        mVersionSelector.setEnabled(!isLaunching);
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

    public void launchGame(View v)
    {
        if (!canBack && mIsAssetsProcessing) {
            mIsAssetsProcessing = false;
            statusIsLaunching(false);
        } else if (canBack) {
            v.setEnabled(false);
            mTask = new MinecraftDownloaderTask(this);
            mTask.execute(mProfile.getVersion());
            mCrashView.resetCrashLog = true;
        }
    }

    public void launcherMenu(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.mcl_options);
        builder.setItems(R.array.mcl_options, new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    switch (p2) {
                        case 0: // Mod installer
                            installMod(false);
                            break;
                        case 1: // Mod installer with java args 
                            installMod(true);
                            break;
                        case 2: // Custom controls
                            if (Tools.enableDevFeatures) {
                                startActivity(new Intent(PojavLauncherActivity.this, CustomControlsActivity.class));
                            }
                            break;
                        case 3: // Settings
                            startActivity(new Intent(PojavLauncherActivity.this, LauncherPreferenceActivity.class));
                            break;
                        case 4: { // About
                                final AlertDialog.Builder aboutB = new AlertDialog.Builder(PojavLauncherActivity.this);
                                aboutB.setTitle(R.string.mcl_option_about);
                                try
                                {
                                    aboutB.setMessage(Html.fromHtml(String.format(Tools.read(getAssets().open("about_en.txt")),
                                                                                  Tools.APP_NAME,
                                                                                  Tools.usingVerName,
                                                                                  "3.2.3")
                                                                    ));
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

    private void installMod(boolean customJavaArgs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alerttitle_installmod);
        builder.setNegativeButton(android.R.string.cancel, null);

        final AlertDialog dialog;
        if (customJavaArgs) {
            final EditText edit = new EditText(this);
            edit.setSingleLine();
            edit.setHint("-jar/-cp /path/to/file.jar ...");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface di, int i) {
                        Intent intent = new Intent(PojavLauncherActivity.this, JavaGUILauncherActivity.class);
                        intent.putExtra("javaArgs", edit.getText().toString());
                        startActivity(intent);
                    }
                });
            dialog = builder.create();
            dialog.setView(edit);
        } else {
            dialog = builder.create();
            FileListView flv = new FileListView(dialog);
            flv.setFileSelectedListener(new FileSelectedListener(){

                    @Override
                    public void onFileSelected(File file, String path) {
                        if (file.getName().endsWith(".jar")) {
                            Intent intent = new Intent(PojavLauncherActivity.this, JavaGUILauncherActivity.class);
                            intent.putExtra("modFile", file);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    }
                });
            dialog.setView(flv);
        }
        dialog.show();
    }
}
