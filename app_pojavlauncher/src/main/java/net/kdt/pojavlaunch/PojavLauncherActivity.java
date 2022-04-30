package net.kdt.pojavlaunch;

import static android.os.Build.VERSION_CODES.P;
import static net.kdt.pojavlaunch.Tools.ignoreNotch;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_HIDE_SIDEBAR;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_NOTCH_SIZE;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.extra.ExtraListener;
import net.kdt.pojavlaunch.fragments.ConsoleFragment;
import net.kdt.pojavlaunch.fragments.CrashFragment;
import net.kdt.pojavlaunch.fragments.LauncherFragment;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceFragment;
import net.kdt.pojavlaunch.profiles.ProfileAdapter;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.profiles.ProfileEditor;
import net.kdt.pojavlaunch.profiles.ProfileIconCache;
import net.kdt.pojavlaunch.value.MinecraftAccount;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PojavLauncherActivity extends BaseLauncherActivity
{


    private TextView tvConnectStatus;
    private Spinner accountSelector;
    private ImageView accountFaceImageView;

    private Button logoutBtn; // MineButtons
    private ExtraListener backPreferenceListener;

    public PojavLauncherActivity() {
    }

    @Override
    protected void onDestroy() {
        ExtraCore.removeExtraListenerFromValue(ExtraConstants.BACK_PREFERENCE, backPreferenceListener);
        super.onDestroy();
        ProfileIconCache.clearIconCache();
        Log.i("LauncherActivity","Destroyed!");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pojav_launcher);

        //Boilerplate linking/initialisation
        tvConnectStatus = findViewById(R.id.launchermain_text_accountstatus);
        accountFaceImageView = findViewById(R.id.launchermain_account_image);
        accountSelector = findViewById(R.id.launchermain_spinner_account);
        mVersionSelector = findViewById(R.id.launchermain_spinner_version);
        mLaunchProgress = findViewById(R.id.progressDownloadBar);
        mLaunchTextStatus = findViewById(R.id.progressDownloadText);
        mPlayButton = findViewById(R.id.launchermainPlayButton);


        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "Launcher process id: " + android.os.Process.myPid(), Toast.LENGTH_LONG).show();
        }

        //Setup listener to the backPreference system
        backPreferenceListener = (key, value) -> {
            if(value.equals("true")){
                onBackPressed();
                ExtraCore.setValue(key, "false");
            }
            return false;
        };
        ExtraCore.addExtraListener(ExtraConstants.BACK_PREFERENCE, backPreferenceListener);


        // Try to load the temporary account
        final List<String> accountList = new ArrayList<>();
        final MinecraftAccount tempProfile = PojavProfile.getTempProfileContent();
        if (tempProfile != null) {
            accountList.add(tempProfile.username);
        }
        for (String s : new File(Tools.DIR_ACCOUNT_NEW).list()) {
            accountList.add(s.substring(0, s.length() - 5));
        }

        // Setup account spinner
        pickAccount();
        ArrayAdapter<String> adapterAcc = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accountList);
        adapterAcc.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        accountSelector.setAdapter(adapterAcc);

        if (tempProfile != null) {
            accountSelector.setSelection(0);
        } else {
            for (int i = 0; i < accountList.size(); i++) {
                String account = accountList.get(i);
                if (account.equals(mProfile.username)) {
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
                    PojavProfile.setCurrentProfile(PojavLauncherActivity.this,
                        accountList.get(position + (tempProfile != null ? 1 : 0)));
                }
                pickAccount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> p1) {
                // TODO: Implement this method
            }
        });

        // Setup the minecraft version list
        setupBasicList(this);

        //mAvailableVersions;
            ProfileAdapter profileAdapter = new ProfileAdapter(this, true);
            ProfileEditor profileEditor = new ProfileEditor(this,(name, isNew, deleting)->{
                LauncherProfiles.update();
                if(isNew) {
                    mVersionSelector.setSelection(profileAdapter.resolveProfileIndex(name));
                }
                if(deleting) {
                    mVersionSelector.setSelection(0);
                }
                profileAdapter.notifyDataSetChanged();
            });
            mVersionSelector.setOnLongClickListener((v)->profileEditor.show(LauncherPreferences.DEFAULT_PREF.getString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE,"")));
            mVersionSelector.setAdapter(profileAdapter);
            mVersionSelector.setSelection(profileAdapter.resolveProfileIndex(LauncherPreferences.DEFAULT_PREF.getString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE,"")));
            mVersionSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
                {
                    String profileName = p1.getItemAtPosition(p3).toString();
                    if(profileName.equals(ProfileAdapter.CREATE_PROFILE_MAGIC)) {
                        profileEditor.show(profileName);
                        mVersionSelector.setSelection(0);
                        return;
                    }
                    LauncherPreferences.DEFAULT_PREF.edit()
                            .putString(
                                    LauncherPreferences.PREF_KEY_CURRENT_PROFILE,
                                    p1.getItemAtPosition(p3).toString())
                            .commit();
                }
                @Override
                public void onNothingSelected(AdapterView<?> p1)
                {
                    // TODO: Implement this method
                }
            });
        //
        statusIsLaunching(false);


        //Add the preference changed listener
        LauncherPreferences.DEFAULT_PREF.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if(key.equals("hideSidebar")){
                //changeLookAndFeel(sharedPreferences.getBoolean("hideSidebar",false));
                return;
            }

            if(key.equals("ignoreNotch")){
                ignoreNotch(sharedPreferences.getBoolean("ignoreNotch", true), PojavLauncherActivity.this);
                return;
            }
        });
    }

    public static String[] basicVersionList;
    public static void setupBasicList(Context ctx) {
        List<String> versions = new ArrayList<>();
        final File fVers = new File(Tools.DIR_HOME_VERSION);

        try {
            if (fVers.listFiles().length < 1) {
                throw new Exception(ctx.getString(R.string.error_no_version));
            }

            for (File fVer : fVers.listFiles()) {
                if (fVer.isDirectory())
                    versions.add(fVer.getName());
            }
        } catch (Exception e) {
            versions.add(ctx.getString(R.string.global_error) + ":");
            versions.add(e.getMessage());

        } finally {
            basicVersionList = versions.toArray(new String[0]);
            ExtraCore.setValue(ExtraConstants.VERSION_LIST,versions);
        }
    }
    private void pickAccount() {
        try {
            mProfile = PojavProfile.getCurrentProfileContent(this);
            accountFaceImageView.setImageBitmap(mProfile.getSkinFace());

            //TODO FULL BACKGROUND LOGIN
            tvConnectStatus.setText(mProfile.accessToken.equals("0") ? R.string.mcl_account_local : R.string.mcl_account_connected);
        } catch(Exception e) {
            mProfile = new MinecraftAccount();
            Tools.showError(this, e, true);
        }
    }

    public void statusIsLaunching(boolean isLaunching) {
        int launchVisibility = isLaunching ? View.VISIBLE : View.GONE;
        mLaunchProgress.setVisibility(launchVisibility);
        mLaunchTextStatus.setVisibility(launchVisibility);


        //logoutBtn.setEnabled(!isLaunching);
        mVersionSelector.setEnabled(!isLaunching);
        canBack = !isLaunching;
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //Try to get the notch so it can be taken into account in settings
        if (Build.VERSION.SDK_INT >= P){
            try {
                PREF_NOTCH_SIZE = getWindow().getDecorView().getRootWindowInsets().getDisplayCutout().getBoundingRects().get(0).width();
            }catch (Exception e){
                Log.i("NOTCH DETECTION", "No notch detected, or the device if in split screen mode");
                PREF_NOTCH_SIZE = -1;
            }
            Tools.updateWindowSize(this);
        }
    }

    /**
     * Custom back stack system. Use the classic backstack when the focus is on the setting screen,
     * finish the activity and remove the back_preference listener otherwise
     */
    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if(count > 0){
            getSupportFragmentManager().popBackStack();
        }else{
            super.onBackPressed();
            //additional code
            ExtraCore.removeExtraListenerFromValue(ExtraConstants.BACK_PREFERENCE, backPreferenceListener);
            finish();
        }
    }
}

