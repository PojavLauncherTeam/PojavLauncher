package net.kdt.pojavlaunch;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.extra.ExtraListener;
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceFragment;
import net.kdt.pojavlaunch.tasks.AsyncAssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyDialogFragment extends DialogFragment {

    private static final int FILE_SELECT_CODE_JSON = 0;
    private static final int FILE_SELECT_CODE_ZIP = 1;

    private static LinearLayout pluginList;
    private static File disabledPluginsDirectory;
    private static File pluginsDirectory;
    
    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        final Button loadConfig = dialog.findViewById(R.id.loadConfig);
        final Button loadPlugin = dialog.findViewById(R.id.loadPlugin);
        pluginList = dialog.findViewById(R.id.pluginsList);
        addPluginsToList();

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


        // Load the PreferenceFragment in the Dialog
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        LauncherPreferenceFragment prefFragment = new LauncherPreferenceFragment();
        ft.replace(R.id.prefContainer, prefFragment);
        ft.commit();

        ExtraCore.addExtraListener(ExtraConstants.BACK_PREFERENCE, mBackPreferenceListener);

        return dialog;
    }

    /* Listener for the back button in settings */
    private final ExtraListener<String> mBackPreferenceListener = (key, value) -> {
        if(value.equals("true")) {
            // This is a very messy way to do things but it works.
            FragmentManager fm = getChildFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            LauncherPreferenceFragment prefFragment = new LauncherPreferenceFragment();
            ft.replace(R.id.prefContainer, prefFragment);
            ft.commit();
        }
        return false;
    };

    private void addPluginsToList() {
        // Path for plugins and disabled plugins
        pluginsDirectory = new File(Tools.DIR_DATA + "/plugins/");
        disabledPluginsDirectory = new File(Tools.DIR_DATA + "/disabledPlugins/");

        // Check if disabledPluginsDirectory exists, if not, create it.
        if (!disabledPluginsDirectory.exists()) {
            boolean success = disabledPluginsDirectory.mkdirs();
            if (!success) {
                Log.e("TAG", "Failed to create directory: " + disabledPluginsDirectory.getPath());
                // If we failed to create the directory, we can return early from this method
                return;
            }
        }

        // Clear the existing list
        pluginList.removeAllViews();

        // Process the enabled plugins
        processPluginDirectory(pluginsDirectory, true);

        // Process the disabled plugins
        processPluginDirectory(disabledPluginsDirectory, false);
    }

    private void processPluginDirectory(File directory, boolean enabled) {
        File[] files = directory.listFiles();

        if (files != null) { // Make sure the directory isn't empty
            for (File file : files) {
                if (file.isDirectory()) { // This line weeds out other directories/folders
                    // Create a new LinearLayout
                    LinearLayout newLinearLayout = new LinearLayout(getContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 10, 0, 10); // Top margin is 10dp
                    newLinearLayout.setLayoutParams(layoutParams);
                    newLinearLayout.setPadding(10, 10, 10, 10); // Padding is 10dp
                    newLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

                    // Create a new TextView
                    TextView textView = new TextView(getContext());
                    LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    textParams.setMargins(30, 0, 0, 0); // Left margin is 30dp
                    textView.setLayoutParams(textParams);
                    textView.setText(file.getName());
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Text size is 16sp

                    // Create a new Switch
                    Switch pluginSwitch = new Switch(getContext());
                    pluginSwitch.setChecked(enabled); // Set the initial state based on the directory

                    // Set an onCheckedChangeListener on the Switch
                    pluginSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        File fromDir = isChecked ? disabledPluginsDirectory : pluginsDirectory;
                        File toDir = isChecked ? pluginsDirectory : disabledPluginsDirectory;

                        File fromFile = new File(fromDir, file.getName());
                        File toFile = new File(toDir, file.getName());

                        // Move the directory
                        boolean success = fromFile.renameTo(toFile);

                        if (!success) {
                            // Handle failure
                            Log.e("TAG", "Failed to move directory: " + fromFile.getPath() + " to " + toFile.getPath());
                        }
                    });

                    // Add the TextView and Switch to the LinearLayout
                    newLinearLayout.addView(textView);
                    newLinearLayout.addView(pluginSwitch);

                    // Add the LinearLayout to the parent layout
                    pluginList.addView(newLinearLayout);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT_CODE_JSON) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                File config = new File(Tools.DIR_DATA, "config.json"); // file to overwrite
                try {
                    Log.d("TAG", "Starting copy: " + uri.getPath());
                    InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                    FileOutputStream fileOutputStream = new FileOutputStream(config);
                    byte buf[] = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        fileOutputStream.write(buf, 0, len);
                    }
                    Toast.makeText(getContext(), "Config loaded. Please restart the app.",
                            Toast.LENGTH_SHORT).show();

                    fileOutputStream.close();
                    inputStream.close();
                } catch (IOException e1) {
                    Log.d("error", "Error with file " + e1);
                }
            }
        } else if(requestCode == FILE_SELECT_CODE_ZIP) {
            Uri uri = data.getData();
            // Create a temporary file in your app's cache directory
            File tempFile = new File(getContext().getCacheDir(), "temp.zip");

            try {
                // Open an InputStream to the selected file
                InputStream inputStream = getContext().getContentResolver().openInputStream(uri);

                // Open a FileOutputStream to your temporary file
                FileOutputStream fileOutputStream = new FileOutputStream(tempFile);

                // Copy the contents
                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                }

                // Close the streams
                fileOutputStream.close();
                inputStream.close();

                // Now you can pass the File object to your method
                AsyncAssetManager.extractPluginZip(tempFile);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showFileChooser(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        switch(requestCode){
            case FILE_SELECT_CODE_JSON:
                intent.setType("application/json");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                break;
            case FILE_SELECT_CODE_ZIP:
                intent.setType("*/*");
                String[] mimetypes = {"application/zip", "application/x-zip-compressed", "multipart/x-zip"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
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
            Toast.makeText(this.getContext(), "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
