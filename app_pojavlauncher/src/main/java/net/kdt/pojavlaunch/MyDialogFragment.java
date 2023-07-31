package net.kdt.pojavlaunch;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.extra.ExtraListener;
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceFragment;

public class MyDialogFragment extends DialogFragment {

    private static final int FILE_SELECT_CODE_JSON = 0;
    private static final int FILE_SELECT_CODE_ZIP = 1;

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
            Toast.makeText(this.getContext(), "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
