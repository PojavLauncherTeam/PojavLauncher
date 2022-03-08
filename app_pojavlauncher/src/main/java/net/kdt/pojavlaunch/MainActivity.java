package net.kdt.pojavlaunch;

import android.app.Activity;
import android.content.Intent;
import android.os.*;


import net.kdt.pojavlaunch.customcontrols.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.utils.MCOptionUtils;

import java.io.*;

import static net.kdt.pojavlaunch.prefs.LauncherPreferences.DEFAULT_PREF;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_SUSTAINED_PERFORMANCE;

public class MainActivity extends BaseMainActivity {
    public static ControlLayout mControlLayout;

    private MCOptionUtils.MCOptionListener optionListener;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_basemain);

        // Set the sustained performance mode for available APIs
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            getWindow().setSustainedPerformanceMode(PREF_SUSTAINED_PERFORMANCE);

        super.ingameControlsEditorListener = menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_ctrl_load:
                    CustomControlsActivity.load(mControlLayout);
                    break;
                case R.id.menu_ctrl_add:
                    mControlLayout.addControlButton(new ControlData("New"));
                    break;
                case R.id.menu_ctrl_add_drawer:
                    mControlLayout.addDrawer(new ControlDrawerData());
                    break;
                case R.id.menu_ctrl_selectdefault:
                    CustomControlsActivity.dialogSelectDefaultCtrl(mControlLayout);
                    break;
                case R.id.menu_ctrl_save:
                    CustomControlsActivity.save(true,mControlLayout);
                    break;
            }
            //Toast.makeText(MainActivity.this, menuItem.getTitle() + ":" + menuItem.getItemId(), Toast.LENGTH_SHORT).show();
            return true;
        };

        // Recompute the gui scale when options are changed
        optionListener = MCOptionUtils::getMcScale;
        MCOptionUtils.addMCOptionListener(optionListener);
        
        mControlLayout = findViewById(R.id.main_control_layout);
        mControlLayout.setModifiable(false);
        try {
            mControlLayout.loadLayout(LauncherPreferences.PREF_DEFAULTCTRL_PATH);
        } catch(IOException e) {
            try {
                mControlLayout.loadLayout(Tools.CTRLDEF_FILE);
                DEFAULT_PREF.edit().putString("defaultCtrl",Tools.CTRLDEF_FILE).commit();
            } catch (IOException ioException) {
                Tools.showError(this, ioException);
            }
        } catch (Throwable th) {
            Tools.showError(this, th);
        }
        
        // toggleGui(null);
        mControlLayout.toggleControlVisible();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // Reload PREF_DEFAULTCTRL_PATH
            LauncherPreferences.loadPreferences(getApplicationContext());
            try {
                mControlLayout.loadLayout(LauncherPreferences.PREF_DEFAULTCTRL_PATH);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //if(isInEditor) CustomControlsActivity.save(true,mControlLayout);
    }
}
