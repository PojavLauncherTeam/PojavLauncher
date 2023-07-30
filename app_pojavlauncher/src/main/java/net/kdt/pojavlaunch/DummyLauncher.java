package net.kdt.pojavlaunch;

import android.annotation.SuppressLint;
import android.app.Dialog;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_launcher);
        fab = findViewById(R.id.myFab);
        playHD = findViewById(R.id.playHD);
        playSD = findViewById(R.id.playSD);

        playHD.setOnClickListener(view -> {
            Log.i("downthecrop","hello from play HD touch");
            // Launch MainActivity.java here and call runCraft()
            Intent intent = new Intent(DummyLauncher.this, MainActivity.class);
            startActivity(intent);
        });

        playSD.setOnClickListener(view -> {
            Log.i("downthecrop","hello from play SD touch");
            Intent intent = new Intent(DummyLauncher.this, JavaGUILauncherActivity.class);
            startActivity(intent);
        });
        fab.setOnClickListener(view -> showBottomDialog());
    }



    @SuppressLint("ClickableViewAccessibility")
    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        final Button loadConfig = dialog.findViewById(R.id.loadConfig);
        final Button loadPlugin = dialog.findViewById(R.id.loadPlugin);

        loadConfig.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showFileChooser(FILE_SELECT_CODE_JSON);
                return true;
            }
            return false;
        });

        loadPlugin.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showFileChooser(FILE_SELECT_CODE_ZIP);
                return true;
            }
            return false;
        });

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

}