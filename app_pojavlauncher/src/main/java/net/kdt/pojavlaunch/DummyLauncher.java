package net.kdt.pojavlaunch;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kdt.mcgui.ProgressLayout;

import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceFragment;
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;
import net.kdt.pojavlaunch.services.ProgressServiceKeeper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DummyLauncher extends BaseActivity {

    private FloatingActionButton fab;
    private Button playHD;
    private Button playSD;
    private static final int FILE_SELECT_CODE_JSON = 0;
    private static final int FILE_SELECT_CODE_ZIP = 1;
    private ProgressServiceKeeper mProgressServiceKeeper;
    private ProgressLayout mProgressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_launcher);
        fab = findViewById(R.id.myFab);
        playHD = findViewById(R.id.playHD);
        playSD = findViewById(R.id.playSD);
        mProgressLayout = findViewById(R.id.progress_layout);

        ProgressKeeper.addTaskCountListener((mProgressServiceKeeper = new ProgressServiceKeeper(this)));
        ProgressKeeper.addTaskCountListener(mProgressLayout);

        mProgressLayout.observe(ProgressLayout.UNPACK_RUNTIME);
        mProgressLayout.observe(ProgressLayout.INSTALL_MODPACK);

        playHD.setOnClickListener(view -> {
            Log.i("downthecrop","hello from play HD touch");
            if(mProgressLayout.hasProcesses()){
                Toast.makeText(this, R.string.tasks_ongoing, Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(DummyLauncher.this, MainActivity.class);
            startActivity(intent);
        });

        playSD.setOnClickListener(view -> {
            Log.i("downthecrop","hello from play SD touch");
            if(mProgressLayout.hasProcesses()){
                Toast.makeText(this, R.string.tasks_ongoing, Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(DummyLauncher.this, JavaGUILauncherActivity.class);
            startActivity(intent);
        });
        fab.setOnClickListener(view -> showBottomDialog());
    }



    @SuppressLint("ClickableViewAccessibility")
    private void showBottomDialog() {
        MyDialogFragment dialog = new MyDialogFragment();
        dialog.show(getSupportFragmentManager(), "tag");
    }

    private void showFileChooser(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        switch(requestCode){
            case FILE_SELECT_CODE_JSON:
                intent.setType("application/json");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                break;
            case FILE_SELECT_CODE_ZIP:
                intent.setType("application/zip");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                break;
            default:
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
        }



        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    requestCode);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT_CODE_JSON) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                // TODO: Change destination based on file type
                File config = new File(Tools.DIR_DATA, "config.json");
                try {
                    Log.d("TAG", "Starting copy: " + uri.getPath());
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    FileOutputStream fileOutputStream = new FileOutputStream(config);
                    byte buf[] = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        fileOutputStream.write(buf, 0, len);
                    }

                    Toast.makeText(this, "Config loaded. Please restart the app.",
                            Toast.LENGTH_SHORT).show();

                    fileOutputStream.close();
                    inputStream.close();

                    // TODO: unzip the plugin here
                } catch (IOException e1) {
                    Log.d("error", "Error with file " + e1);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ProgressKeeper.removeTaskCountListener(mProgressServiceKeeper);
    }
}