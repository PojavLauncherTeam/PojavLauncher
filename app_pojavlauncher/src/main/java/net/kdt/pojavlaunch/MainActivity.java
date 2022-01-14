package net.kdt.pojavlaunch;

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
        MCXRLoader.setContext(this.getApplicationContext());
        MCXRLoader.setApplicationActivity(this);

        initLayout(R.layout.main_with_customctrl);

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

        // toggleGui(null);
        super.onCreate(savedInstanceState);
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
