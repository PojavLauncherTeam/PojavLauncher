package net.kdt.pojavlaunch;

import static net.kdt.pojavlaunch.Tools.getFileName;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.text.*;
import android.text.method.*;
import android.view.*;
import android.webkit.MimeTypeMap;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.*;
import com.kdt.pickafile.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;

import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.extra.ExtraListener;
import net.kdt.pojavlaunch.fragments.*;
import net.kdt.pojavlaunch.multirt.MultiRTConfigDialog;
import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.tasks.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;

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


    /**
     * Used by the custom control button from the layout_main_v4
     * @param view The view triggering the function
     */
    public void launchCustomControlsActivity(View view){
        startActivity(new Intent(BaseLauncherActivity.this, CustomControlsActivity.class));
    }

    /**
     * Used by the install button from the layout_main_v4
     * @param view The view triggering the function
     */
    public void installJarFile(View view){
        installMod(false);
    }


    public static final int RUN_MOD_INSTALLER = 2050;
    private void installMod(boolean customJavaArgs) {
        if (customJavaArgs) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.alerttitle_installmod);
            builder.setNegativeButton(android.R.string.cancel, null);
            final AlertDialog dialog;
            final EditText edit = new EditText(this);
            edit.setSingleLine();
            edit.setHint("-jar/-cp /path/to/file.jar ...");
            builder.setPositiveButton(android.R.string.ok, (di, i) -> {
                Intent intent = new Intent(BaseLauncherActivity.this, JavaGUILauncherActivity.class);
                intent.putExtra("skipDetectMod", true);
                intent.putExtra("javaArgs", edit.getText().toString());
                startActivity(intent);
            });
            dialog = builder.create();
            dialog.setView(edit);
            dialog.show();
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("jar");
            if(mimeType == null) mimeType = "*/*";
            intent.setType(mimeType);
            startActivityForResult(intent,RUN_MOD_INSTALLER);
        }

    }

    public void launchGame(View v) {
        if (!canBack && mIsAssetsProcessing) {
            mIsAssetsProcessing = false;
            statusIsLaunching(false);
        } else if (canBack) {
            v.setEnabled(false);
            mTask = new MinecraftDownloaderTask(this);
            if(LauncherPreferences.PREF_ENABLE_PROFILES) {
                LauncherProfiles.update();
                if (LauncherProfiles.mainProfileJson != null && LauncherProfiles.mainProfileJson.profiles != null && LauncherProfiles.mainProfileJson.profiles.containsKey(mProfile.selectedProfile + "")) {
                    MinecraftProfile prof = LauncherProfiles.mainProfileJson.profiles.get(mProfile.selectedProfile + "");
                    if (prof != null && prof.lastVersionId != null) {
                        mTask.execute(getVersionId(prof.lastVersionId));
                    }
                }
            }else{
                mTask.execute(mProfile.selectedProfile);
            }

        }
    }

    public static String getVersionId(String input) {
        Map<String,String> lReleaseMaps = (Map<String,String>)ExtraCore.getValue("release_table");
        if(lReleaseMaps == null || lReleaseMaps.isEmpty()) return input;
        switch(input) {
            case "latest-release":
                return lReleaseMaps.get("release");
            case "latest-snapshot":
                return lReleaseMaps.get("snapshot");
            default:
                return input;
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
    public void setupVersionSelector() {
        final androidx.appcompat.widget.PopupMenu popup = new PopupMenu(this, mVersionSelector);
        popup.getMenuInflater().inflate(R.menu.menu_versionopt, popup.getMenu());
        PerVersionConfigDialog dialog = new PerVersionConfigDialog(this);
        this.mVersionSelector.setOnLongClickListener((v)->dialog.openConfig(this.mProfile.selectedVersion));
        this.mVersionSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
            {
                mProfile.selectedVersion = p1.getItemAtPosition(p3).toString();

                PojavProfile.setCurrentProfile(BaseLauncherActivity.this, mProfile);
                if (PojavProfile.isFileType(BaseLauncherActivity.this)) {
                    try {
                        PojavProfile.setCurrentProfile(BaseLauncherActivity.this, mProfile.save());
                    } catch (IOException e) {
                        Tools.showError(BaseLauncherActivity.this, e);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> p1)
            {
                // TODO: Implement this method
            }
        });
        popup.setOnMenuItemClickListener(item -> true);
    }
    ExtraListener<ArrayList<String>> versionListener;
    @Override
    protected void onPause() {
        super.onPause();
        if((!LauncherPreferences.PREF_ENABLE_PROFILES) && versionListener != null) ExtraCore.removeExtraListenerFromValue("lac_version_list",versionListener);
    }

    public static void updateVersionSpinner(Context ctx, ArrayList<String> value, Spinner mVersionSelector, String defaultSelection) {
        if(value != null && value.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item, value);
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
            mVersionSelector.setAdapter(adapter);
            mVersionSelector.setSelection(RefreshVersionListTask.selectAt(value, defaultSelection));
        } else {
            mVersionSelector.setSelection(RefreshVersionListTask.selectAt(PojavLauncherActivity.basicVersionList, defaultSelection));
        }
    }
    @Override
    protected void onResume(){
        super.onResume();

        if(!LauncherPreferences.PREF_ENABLE_PROFILES) {

            ArrayList<String> vlst = (ArrayList<String>) ExtraCore.getValue("lac_version_list");
            if(vlst != null) {
                setupVersionSelector();
                updateVersionSpinner(this, vlst, mVersionSelector, mProfile.selectedVersion);
            }
            versionListener = (key, value) -> {
                if(value != null) {
                    setupVersionSelector();
                    updateVersionSpinner(this, value, mVersionSelector, mProfile.selectedVersion);
                }
                return false;
            };
            ExtraCore.addExtraListener("lac_version_list",versionListener);
        }
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
            listRefreshListener = (sharedPreferences, key) -> {
                if(key.startsWith("vertype_")) {
                    System.out.println("Verlist update needed!");
                    new RefreshVersionListTask(thiz).execute();
                }
            };
        }
        LauncherPreferences.DEFAULT_PREF.registerOnSharedPreferenceChangeListener(listRefreshListener);
        new RefreshVersionListTask(this).execute();
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
                        mRuntimeConfigDialog.dialog.show();
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

    protected abstract void initTabs(int pageIndex);
}
