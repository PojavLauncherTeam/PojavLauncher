package net.kdt.pojavlaunch;

import android.os.*;

import androidx.core.app.*;
import androidx.core.view.*;
import androidx.appcompat.app.*;

import android.view.*;
import android.widget.*;

import com.google.android.material.tabs.TabLayout;

import java.io.*;
import java.util.*;

import net.kdt.pojavlaunch.fragments.*;

import org.lwjgl.glfw.*;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;

import android.support.design.widget.VerticalTabLayout.*;

//import android.support.v7.view.menu.*;
//import net.zhuoweizhang.boardwalk.downloader.*;

public class MCLauncherActivity extends BaseLauncherActivity
{
    //private FragmentTabHost mTabHost;
    private LinearLayout fullTab;
    /*
     private PojavLauncherViewPager viewPager;
     private VerticalTabLayout tabLayout;
     */

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private TextView tvUsernameView;
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
        setContentView(R.layout.launcher_main);

        fullTab = findViewById(R.id.launchermainFragmentTabView);
        tabLayout = findViewById(R.id.launchermainTabLayout);
        viewPager = findViewById(R.id.launchermainTabPager);

        mConsoleView = new ConsoleFragment();
        mCrashView = new CrashFragment();

        viewPageAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPageAdapter.addFragment(new LauncherFragment(), 0, getStr(R.string.mcl_tab_news));
        viewPageAdapter.addFragment(mConsoleView, 0, getStr(R.string.mcl_tab_console));
        viewPageAdapter.addFragment(mCrashView, 0, getStr(R.string.mcl_tab_crash));

        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tvUsernameView = (TextView) findViewById(R.id.launchermain_text_welcome);
        mTextVersion = (TextView) findViewById(R.id.launcherMainVersionView);

        try {
            profilePath = PojavProfile.getCurrentProfilePath(this);
            mProfile = PojavProfile.getCurrentProfileContent(this);

            tvUsernameView.setText(getString(R.string.main_welcome, mProfile.getUsername()));
        } catch(Exception e) {
            //Tools.throwError(this, e);
            e.printStackTrace();
            Toast.makeText(this, getStr(R.string.toast_login_error, e.getMessage()), Toast.LENGTH_LONG).show();
            finish();
        }
        
        //showProfileInfo();

        List<String> versions = new ArrayList<String>();
        final File fVers = new File(Tools.DIR_HOME_VERSION);

        try {
            if (fVers.listFiles().length < 1) {
                throw new Exception(getStr(R.string.error_no_version));
            }

            for (File fVer : fVers.listFiles()) {
                if (fVer.isDirectory())
                    versions.add(fVer.getName());
            }
        } catch (Exception e) {
            versions.add(getStr(R.string.global_error) + ":");
            versions.add(e.getMessage());

        } finally {
            mAvailableVersions = versions.toArray(new String[0]);
        }

        //availableVersions;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mAvailableVersions);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mVersionSelector = (Spinner) findViewById(R.id.launcherMainSelectVersion);
        mVersionSelector.setAdapter(adapter);

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
    // DEBUG
    //new android.support.design.widget.NavigationView(this);

    private String getStr(int id, Object... val) {
        if (val != null && val.length > 0) {
            return getResources().getString(id, val);
        } else {
            return getResources().getString(id);
        }
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
    protected void selectTabPage(int pageIndex) {
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
