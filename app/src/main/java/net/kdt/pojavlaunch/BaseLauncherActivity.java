package net.kdt.pojavlaunch;

import android.content.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import com.kdt.filerapi.*;
import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.fragments.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.tasks.*;

public abstract class BaseLauncherActivity extends BaseActivity {
	public Button mPlayButton;
	public ConsoleFragment mConsoleView;
    public CrashFragment mCrashView;
    public ProgressBar mLaunchProgress;
	public Spinner mVersionSelector;
	public TextView mLaunchTextStatus, mTextVersion;
    
    public JMinecraftVersionList mVersionList;
	public MinecraftDownloaderTask mTask;
	public MCProfile.Builder mProfile;
	public String[] mAvailableVersions;
    
	public boolean mIsAssetsProcessing = false;
    
    public abstract void statusIsLaunching(boolean isLaunching);

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
                                startActivity(new Intent(BaseLauncherActivity.this, CustomControlsActivity.class));
                            }
                            break;
                        case 3: // Settings
                            startActivity(new Intent(BaseLauncherActivity.this, LauncherPreferenceActivity.class));
                            break;
                        case 4: { // About
                                final AlertDialog.Builder aboutB = new AlertDialog.Builder(BaseLauncherActivity.this);
                                aboutB.setTitle(R.string.mcl_option_about);
                                try
                                {
                                    aboutB.setMessage(Html.fromHtml(String.format(Tools.read(getAssets().open("about_en.txt")),
                                                                                  Tools.APP_NAME,
                                                                                  Tools.usingVerName,
                                                                                  "3.2.3", getString(R.string.mcl_about_translated_by))
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
                        Intent intent = new Intent(BaseLauncherActivity.this, JavaGUILauncherActivity.class);
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
                            Intent intent = new Intent(BaseLauncherActivity.this, JavaGUILauncherActivity.class);
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
