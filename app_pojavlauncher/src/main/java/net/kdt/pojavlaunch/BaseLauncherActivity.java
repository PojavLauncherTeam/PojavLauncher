package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.graphics.Bitmap;
import android.text.*;
import android.text.method.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.*;
import com.kdt.pickafile.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.kdt.pojavlaunch.fragments.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.tasks.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;

import net.kdt.pojavlaunch.value.*;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;
import net.kdt.pojavlaunch.value.launcherprofiles.VersionProfileAdapter;

public abstract class BaseLauncherActivity extends BaseActivity {
	public Button mPlayButton;
	public ConsoleFragment mConsoleView;
    public CrashFragment mCrashView;
    public ProfileEditorFragment mProfileEditView;
    public ProgressBar mLaunchProgress;
	public Spinner mVersionSelector;
	public TextView mLaunchTextStatus, mTextVersion;
    
    public JMinecraftVersionList mVersionList;
	public MinecraftDownloaderTask mTask;
	public MinecraftAccount mProfile;
	public ArrayList<String> mVersionStringList;
    public static Map<String, Bitmap> versionIcons = new HashMap<>();
	public boolean mIsAssetsProcessing = false;
    protected boolean canBack = false;
    
    public abstract void statusIsLaunching(boolean isLaunching);

    public void mcaccSwitchUser(View view) {
        showProfileInfo();
    }

    public void mcaccLogout(View view) {
        //PojavProfile.reset();
        finish();
    }

    private void showProfileInfo() {
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
    
    public void launcherMenu(View view) {
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
                            startActivity(new Intent(BaseLauncherActivity.this, CustomControlsActivity.class));
                            break;
                        case 3: { // About
                                final AlertDialog.Builder aboutB = new AlertDialog.Builder(BaseLauncherActivity.this);
                                aboutB.setTitle(R.string.mcl_option_about);
                                try {
                                    aboutB.setMessage(Html.fromHtml(String.format(Tools.read(getAssets().open("about_en.txt")),
                                                                                  Tools.APP_NAME,
                                                                                  BuildConfig.VERSION_NAME,
                                                                                  "3.2.3")
                                                                    ));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                aboutB.setPositiveButton(android.R.string.ok, null);
                                AlertDialog aboutDialog = aboutB.show();
                                TextView aboutTv = aboutDialog.findViewById(android.R.id.message);
                                aboutTv.setMovementMethod(LinkMovementMethod.getInstance());
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
                        Intent intent = new Intent(BaseLauncherActivity.this, JavaGUILauncherActivity.class);
                        intent.putExtra("skipDetectMod", true);
                        intent.putExtra("javaArgs", edit.getText().toString());
                        startActivity(intent);
                    }
                });
            dialog = builder.create();
            dialog.setView(edit);
        } else {
            dialog = builder.create();
            FileListView flv = new FileListView(dialog,"jar");
            flv.setFileSelectedListener(new FileSelectedListener(){
                    @Override
                    public void onFileSelected(File file, String path) {
                        Intent intent = new Intent(BaseLauncherActivity.this, JavaGUILauncherActivity.class);
                        intent.putExtra("modFile", file);
                        startActivity(intent);
                        dialog.dismiss();

                    }
                });
            dialog.setView(flv);
        }
        dialog.show();
    }

    public void launchGame(View v) {
        if (!canBack && mIsAssetsProcessing) {
            mIsAssetsProcessing = false;
            statusIsLaunching(false);
        } else if (canBack) {
            v.setEnabled(false);
            //Tools.setCustomGameDir(Tools.DIR_GAME_HOME+"/pojav_aux_test");
            mTask = new MinecraftDownloaderTask(this);
            mTask.execute(LauncherProfiles.mainProfileJson.profiles.get(mProfile.selectedProfile).lastVersionId);
            mCrashView.resetCrashLog = true;
        }
    }
    
    @Override
    public void onBackPressed() {
        if (canBack) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        System.out.println("call to onPostResume");
        Tools.updateWindowSize(this);
        System.out.println("call to onPostResume; E");
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        System.out.println("call to onResume");
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
        System.out.println("call to onResume; E");
    }
    SharedPreferences.OnSharedPreferenceChangeListener listRefreshListener = null;
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if(listRefreshListener == null) {
            final BaseLauncherActivity thiz = this;
            listRefreshListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if(key.startsWith("vertype_")) {
                        System.out.println("Verlist update needed!");
                        new RefreshVersionListTask(thiz).execute();
                    }
                }
            };
            LauncherPreferences.DEFAULT_PREF.registerOnSharedPreferenceChangeListener(listRefreshListener);
        }
        new RefreshVersionListTask(this).execute();
        updateProfileList();
        System.out.println("call to onResumeFragments");
        try{
            final ProgressDialog barrier = new ProgressDialog(this);
            barrier.setMessage(getString(R.string.global_waiting));
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

            File lastCrashFile = Tools.lastFileModified(Tools.DIR_HOME_CRASH);
            if(CrashFragment.isNewCrash(lastCrashFile) || !mCrashView.getLastCrash().isEmpty()){
                mCrashView.resetCrashLog = false;
                initTabs(2);

            } /*else throw new Exception();*/
        } catch(Throwable e) {
            e.printStackTrace();
        }
        System.out.println("call to onResumeFragments; E");
    }
    
    // Catching touch exception
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
    public void updateProfileList() {
        final PopupMenu popup = new PopupMenu(this, this.mVersionSelector);
        final BaseLauncherActivity thiz = this;
        popup.getMenuInflater().inflate(R.menu.menu_versionopt, popup.getMenu());
        LauncherProfiles.update();
        VersionProfileAdapter adapterVer = new VersionProfileAdapter(this);
        this.mVersionSelector.setAdapter(adapterVer);
        int selectedProfile = Arrays.binarySearch(adapterVer.profileKeys,mProfile.selectedProfile);
        if(selectedProfile > -1) {
            this.mVersionSelector.setSelection(selectedProfile);
        }

        this.mVersionSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
            {
                String version = ((MinecraftProfile)p1.getItemAtPosition(p3)).lastVersionId;
                thiz.mProfile.selectedProfile = ((VersionProfileAdapter)p1.getAdapter()).profileKeys[p3];
                PojavProfile.setCurrentProfile(thiz, thiz.mProfile);
                if (PojavProfile.isFileType(thiz)) {
                    try {
                        PojavProfile.setCurrentProfile(thiz, thiz.mProfile.save());
                    } catch (IOException e) {
                        Tools.showError(thiz, e);
                    }
                }

                thiz.mTextVersion.setText(thiz.getString(R.string.mcl_version_msg, version));
            }

            @Override
            public void onNothingSelected(AdapterView<?> p1)
            {
                // TODO: Implement this method
            }
        });
        this.mVersionSelector.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
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

        this.mTextVersion.setText(this.getString(R.string.mcl_version_msg,this.mVersionSelector.getSelectedItem()));
    }
    protected abstract void initTabs(int pageIndex);
}
