package net.kdt.pojavlaunch;

import static net.kdt.pojavlaunch.prefs.LauncherPreferences.DEFAULT_PREF;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_SUSTAINED_PERFORMANCE;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import net.kdt.pojavlaunch.customcontrols.ControlData;
import net.kdt.pojavlaunch.customcontrols.ControlDrawerData;
import net.kdt.pojavlaunch.customcontrols.ControlLayout;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.utils.MCOptionUtils;

import java.io.IOException;

public class MainActivity extends BaseMainActivity {
    public static ControlLayout mControlLayout;

    private MCOptionUtils.MCOptionListener optionListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.main_with_customctrl);

        // Set the sustained performance mode for available APIs
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            getWindow().setSustainedPerformanceMode(PREF_SUSTAINED_PERFORMANCE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // Reload PREF_DEFAULTCTRL_PATH
            LauncherPreferences.loadPreferences(getApplicationContext());
        }
    }

    @Override
    public void onBackPressed() {
        //if(isInEditor) CustomControlsActivity.save(true,mControlLayout);
    }
}
