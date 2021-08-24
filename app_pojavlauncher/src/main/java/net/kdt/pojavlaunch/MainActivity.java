package net.kdt.pojavlaunch;

import android.app.Activity;
import android.content.Intent;
import android.os.*;


import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.customcontrols.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.utils.MCOptionUtils;

import java.io.*;

import static net.kdt.pojavlaunch.prefs.LauncherPreferences.DEFAULT_PREF;

public class MainActivity extends BaseMainActivity {
    public static ControlLayout mControlLayout;

    private FileObserver fileObserver;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.main_with_customctrl);
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


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            fileObserver = new FileObserver(new File(Tools.DIR_GAME_NEW + "/options.txt"), FileObserver.MODIFY) {
                @Override
                public void onEvent(int i, @Nullable String s) {
                    MCOptionUtils.load();
                    getMcScale();
                }
            };
        }else{
            fileObserver = new FileObserver(Tools.DIR_GAME_NEW + "/options.txt", FileObserver.MODIFY) {
                @Override
                public void onEvent(int i, @Nullable String s) {
                    MCOptionUtils.load();
                    getMcScale();
                }
            };
        }

        fileObserver.startWatching();
        
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
