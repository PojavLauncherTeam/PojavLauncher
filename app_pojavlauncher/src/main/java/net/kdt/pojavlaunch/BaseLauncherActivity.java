package net.kdt.pojavlaunch;

import static net.kdt.pojavlaunch.Tools.getFileName;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.view.*;
import android.webkit.MimeTypeMap;
import android.widget.*;

import androidx.annotation.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.multirt.MultiRTConfigDialog;
import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.tasks.*;

import androidx.appcompat.app.AlertDialog;

import net.kdt.pojavlaunch.value.*;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import org.apache.commons.io.IOUtils;

public abstract class BaseLauncherActivity extends BaseActivity {
	public Button mPlayButton;
    public ProgressBar mLaunchProgress;
	public Spinner mVersionSelector;
	public MultiRTConfigDialog mRuntimeConfigDialog;
	public TextView mLaunchTextStatus;
    
    public JMinecraftVersionList mVersionList;
	public MinecraftDownloaderTask mTask;
	public MinecraftAccount mProfile;
	//public String[] mAvailableVersions;
    
	public boolean mIsAssetsProcessing = false;
    protected boolean canBack = false;
    
    public abstract void statusIsLaunching(boolean isLaunching);

    public static final int RUN_MOD_INSTALLER = 2050;

    public void launchGame(View v) {
        if (!canBack && mIsAssetsProcessing) {
            mIsAssetsProcessing = false;
            statusIsLaunching(false);
        } else if (canBack) {
            v.setEnabled(false);
            mTask = new MinecraftDownloaderTask(this);
                LauncherProfiles.update();
                String selectedProfile = LauncherPreferences.DEFAULT_PREF.getString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE,"");
                if (LauncherProfiles.mainProfileJson != null && LauncherProfiles.mainProfileJson.profiles != null && LauncherProfiles.mainProfileJson.profiles.containsKey(selectedProfile)) {
                    MinecraftProfile prof = LauncherProfiles.mainProfileJson.profiles.get(selectedProfile);
                    if (prof != null && prof.lastVersionId != null) {
                        if (mProfile.accessToken.equals("0")) {
                            String versionId = getVersionId(prof.lastVersionId);
                            File verJsonFile = new File(Tools.DIR_HOME_VERSION,
                                versionId + "/" + versionId + ".json");
                            if (verJsonFile.exists()) {
                                mTask.onPostExecute(null);
                                return;
                            }
                            Tools.dialogOnUiThread(this,
                                    getString(R.string.global_error),
                                    getString(R.string.mcl_launch_error_localmode)
                            );
                        }else {
                            mTask.execute(getVersionId(prof.lastVersionId));
                        }
                    }
                }
        }
    }

    public static String getVersionId(String input) {
        Map<String,String> releaseTable = (Map<String,String>)ExtraCore.getValue(ExtraConstants.RELEASE_TABLE);
        if(releaseTable == null || releaseTable.isEmpty()) return input;
        if("latest-release".equals(input)) return releaseTable.get("release");
        if("latest-snapshot".equals(input)) return releaseTable.get("snapshot");
        return input;
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

    public static void updateVersionSpinner(Context ctx, ArrayList<String> value, Spinner mVersionSelector, String defaultSelection) {
        if(value != null && value.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item, value);
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
            mVersionSelector.setAdapter(adapter);
            mVersionSelector.setSelection(RefreshVersionListTask.selectAt(value, defaultSelection));
            return;
        }
        mVersionSelector.setSelection(RefreshVersionListTask.selectAt(PojavLauncherActivity.basicVersionList, defaultSelection));
    }
    @Override
    protected void onResume(){
        super.onResume();
        new RefreshVersionListTask(this).execute();
        if(listRefreshListener != null) {
            LauncherPreferences.DEFAULT_PREF.unregisterOnSharedPreferenceChangeListener(listRefreshListener);
        }
        listRefreshListener = (sharedPreferences, key) -> {
            if(key.startsWith("vertype_")) {
                System.out.println("Verlist update needed!");
                LauncherPreferences.PREF_VERTYPE_RELEASE = sharedPreferences.getBoolean("vertype_release",true);
                LauncherPreferences.PREF_VERTYPE_SNAPSHOT = sharedPreferences.getBoolean("vertype_snapshot",false);
                LauncherPreferences.PREF_VERTYPE_OLDALPHA = sharedPreferences.getBoolean("vertype_oldalpha",false);
                LauncherPreferences.PREF_VERTYPE_OLDBETA = sharedPreferences.getBoolean("vertype_oldbeta",false);
                new RefreshVersionListTask(this).execute();
            }
        };
        LauncherPreferences.DEFAULT_PREF.registerOnSharedPreferenceChangeListener(listRefreshListener);
        System.out.println("call to onResume");
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
        System.out.println("call to onResume; E");
    }

    SharedPreferences.OnSharedPreferenceChangeListener listRefreshListener = null;
    SharedPreferences.OnSharedPreferenceChangeListener profileEnableListener = null;
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        System.out.println("call to onResumeFragments");
        mRuntimeConfigDialog = new MultiRTConfigDialog();
        mRuntimeConfigDialog.prepare(this);
        //TODO ADD CRASH CHECK AND FOCUS
        System.out.println("call to onResumeFragments; E");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == Activity.RESULT_OK) {
            final ProgressDialog barrier = new ProgressDialog(this);
            barrier.setMessage(getString(R.string.global_waiting));
            barrier.setProgressStyle(barrier.STYLE_SPINNER);
            barrier.setCancelable(false);
            barrier.show();

            // Install the runtime
            if (requestCode == MultiRTConfigDialog.MULTIRT_PICK_RUNTIME) {
                if (data == null) return;

                final Uri uri = data.getData();
                Thread t = new Thread(() -> {
                    try {
                        String name = getFileName(this, uri);
                        MultiRTUtils.installRuntimeNamed(getContentResolver().openInputStream(uri), name,
                                (resid, stuff) -> BaseLauncherActivity.this.runOnUiThread(
                                        () -> barrier.setMessage(BaseLauncherActivity.this.getString(resid, stuff))));
                        MultiRTUtils.postPrepare(BaseLauncherActivity.this, name);
                    } catch (IOException e) {
                        Tools.showError(BaseLauncherActivity.this, e);
                    }
                    BaseLauncherActivity.this.runOnUiThread(() -> {
                        barrier.dismiss();
                        mRuntimeConfigDialog.refresh();
                        mRuntimeConfigDialog.mDialog.show();
                    });
                });
                t.start();
            }

            // Run a mod installer
            if (requestCode == RUN_MOD_INSTALLER) {
                if (data == null) return;

                final Uri uri = data.getData();
                barrier.setMessage(BaseLauncherActivity.this.getString(R.string.multirt_progress_caching));
                Thread t = new Thread(()->{
                    try {
                        final String name = getFileName(this, uri);
                        final File modInstallerFile = new File(getCacheDir(), name);
                        FileOutputStream fos = new FileOutputStream(modInstallerFile);
                        IOUtils.copy(getContentResolver().openInputStream(uri), fos);
                        fos.close();
                        BaseLauncherActivity.this.runOnUiThread(() -> {
                            barrier.dismiss();
                            Intent intent = new Intent(BaseLauncherActivity.this, JavaGUILauncherActivity.class);
                            intent.putExtra("modFile", modInstallerFile);
                            startActivity(intent);
                        });
                    }catch(IOException e) {
                        Tools.showError(BaseLauncherActivity.this,e);
                    }
                });
                t.start();
            }

        }
    }
}
