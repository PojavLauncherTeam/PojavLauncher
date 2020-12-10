package net.kdt.pojavlaunch;

import android.os.*;
import android.support.design.widget.*;
import android.support.design.widget.VerticalTabLayout.*;
import androidx.core.view.*;
import androidx.appcompat.app.*;

import android.util.*;
import android.view.*;
import android.widget.*;

import java.io.*;
import java.util.*;

import net.kdt.pojavlaunch.fragments.*;
import net.kdt.pojavlaunch.prefs.*;

import org.lwjgl.glfw.*;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "Launcher process id: " + android.os.Process.myPid(), Toast.LENGTH_LONG).show();
        }
        
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

        tvUsernameView = (TextView) findViewById(R.id.launchermain_text_welcome);
        mTextVersion = (TextView) findViewById(R.id.launcherMainVersionView);

        try {
            profilePath = PojavProfile.getCurrentProfilePath(this);
            mProfile = PojavProfile.getCurrentProfileContent(this);

            tvUsernameView.setText(getString(R.string.main_welcome, mProfile.getUsername()));
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
                Log.w(Tools.APP_NAME, "Could not detect java crash", th);
            }
        }

        //showProfileInfo();

        final List<String> accountList = new ArrayList<String>();
        final MCProfile.Builder tempProfile = PojavProfile.getTempProfileContent(this);
        if (tempProfile != null) {
            accountList.add(tempProfile.getUsername());
        }
        accountList.addAll(Arrays.asList(new File(Tools.mpProfiles).list()));
        
        ArrayAdapter<String> adapterAcc = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accountList);
        adapterAcc.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        accountSelector = (Spinner) findViewById(R.id.launchermain_spinner_account);
        accountSelector.setAdapter(adapterAcc);
        if (tempProfile != null) {
            accountSelector.setSelection(0);
        } else {
            for (int i = 0; i < accountList.size(); i++) {
                String account = accountList.get(i);
                if (account.equals(mProfile.getUsername())) {
                    accountSelector.setSelection(i);
                }
            }
        }
        accountSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> p1, View p2, int position, long p4) {
                if (tempProfile != null && position == 0) {
                    PojavProfile.setCurrentProfile(PojavLauncherActivity.this, tempProfile);
                } else {
                    PojavProfile.setCurrentProfile(PojavLauncherActivity.this, accountList.get(position + (tempProfile != null ? 1 : 0)));
                }
                finish();
                startActivity(getIntent());
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
    protected float updateWidthHeight() {
        float leftRightWidth = (float) CallbackBridge.windowWidth / 100f * 32f;
        float mPlayButtonWidth = CallbackBridge.windowWidth - leftRightWidth * 2f;
        LinearLayout.LayoutParams leftRightParams = new LinearLayout.LayoutParams((int) leftRightWidth, (int) Tools.dpToPx(CallbackBridge.windowHeight / 9));
        LinearLayout.LayoutParams mPlayButtonParams = new LinearLayout.LayoutParams((int) mPlayButtonWidth, (int) Tools.dpToPx(CallbackBridge.windowHeight / 9));
        leftView.setLayoutParams(leftRightParams);
        rightView.setLayoutParams(leftRightParams);
        mPlayButton.setLayoutParams(mPlayButtonParams);

        return leftRightWidth;
    }

    @Override
    protected void selectTabPage(int pageIndex){
        if (tabLayout.getSelectedTabPosition() != pageIndex) {
            tabLayout.setScrollPosition(pageIndex,0f,true);
            viewPager.setCurrentItem(pageIndex);
        }
    }

    public void statusIsLaunching(boolean isLaunching) {
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
}

