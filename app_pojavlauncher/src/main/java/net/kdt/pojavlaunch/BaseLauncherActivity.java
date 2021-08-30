package net.kdt.pojavlaunch;

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
import net.kdt.pojavlaunch.fragments.*;
import net.kdt.pojavlaunch.multirt.MultiRTConfigDialog;
import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.tasks.*;

import androidx.appcompat.app.AlertDialog;
import net.kdt.pojavlaunch.value.*;

import org.apache.commons.io.IOUtils;

public abstract class BaseLauncherActivity extends BaseActivity {
	public Button mPlayButton;
	public ConsoleFragment mConsoleView;
    public CrashFragment mCrashView;
    public ProgressBar mLaunchProgress;
	public Spinner mVersionSelector;
	public MultiRTConfigDialog mRuntimeConfigDialog;
	public TextView mLaunchTextStatus, mTextVersion;
    
    public JMinecraftVersionList mVersionList;
	public MinecraftDownloaderTask mTask;
	public MinecraftAccount mProfile;
	public String[] mAvailableVersions;
    
	public boolean mIsAssetsProcessing = false;
    protected boolean canBack = false;
    
    public abstract void statusIsLaunching(boolean isLaunching);

    public void mcaccLogout(View view) {
        //PojavProfile.reset();
        finish();
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
            mTask.execute(mProfile.selectedVersion);
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
        }
        LauncherPreferences.DEFAULT_PREF.registerOnSharedPreferenceChangeListener(listRefreshListener);
        new RefreshVersionListTask(this).execute();
        System.out.println("call to onResumeFragments");
        mRuntimeConfigDialog = new MultiRTConfigDialog();
        mRuntimeConfigDialog.prepare(this);
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
    public static String getFileName(Context ctx, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = ctx.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
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
            if (requestCode == MultiRTConfigDialog.MULTIRT_PICK_RUNTIME) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Thread t = new Thread(() -> {
                        try {
                            String name = getFileName(this, uri);
                            MultiRTUtils.installRuntimeNamed(getContentResolver().openInputStream(uri), name,
                                    (resid, stuff) -> BaseLauncherActivity.this.runOnUiThread(
                                            () -> barrier.setMessage(BaseLauncherActivity.this.getString(resid, stuff))));
                            MultiRTUtils.postPrepare(BaseLauncherActivity.this, name);
                        } catch (IOException e) {
                            Tools.showError(BaseLauncherActivity.this
                                    , e);
                        }
                        BaseLauncherActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                barrier.dismiss();
                                mRuntimeConfigDialog.refresh();
                                mRuntimeConfigDialog.dialog.show();
                            }
                        });
                    });
                    t.start();
                }
            } else if (requestCode == RUN_MOD_INSTALLER) {
                if (data != null) {
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

    // Catching touch exception
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    protected abstract void initTabs(int pageIndex);
}
