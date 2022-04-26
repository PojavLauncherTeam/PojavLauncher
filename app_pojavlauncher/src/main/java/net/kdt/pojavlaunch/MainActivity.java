package net.kdt.pojavlaunch;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;


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
        ingameControlsEditorArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.menu_customcontrol));
        ingameControlsEditorListener = (parent, view, position, id) -> {
            switch(position) {
                case 0:
                    mControlLayout.addControlButton(new ControlData("New"));
                    break;
                case 1:
                    mControlLayout.addDrawer(new ControlDrawerData());
                    break;
                case 2:
                    CustomControlsActivity.load(mControlLayout);
                    break;
                case 3:
                    CustomControlsActivity.save(true,mControlLayout);
                    break;
                case 4:
                    CustomControlsActivity.dialogSelectDefaultCtrl(mControlLayout);
                    break;
            }
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
