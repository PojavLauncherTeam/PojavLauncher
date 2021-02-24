package net.kdt.pojavlaunch;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.view.*;

import androidx.annotation.Nullable;

import com.google.android.material.navigation.NavigationView;

import net.kdt.pojavlaunch.customcontrols.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.utils.MCOptionUtils;

import org.lwjgl.glfw.*;
import java.io.*;

import static net.kdt.pojavlaunch.prefs.LauncherPreferences.DEFAULT_PREF;

public class MainActivity extends BaseMainActivity {
    public ControlLayout mControlLayout;
    
    private View.OnClickListener mClickListener;
    private View.OnTouchListener mTouchListener;
    private FileObserver fileObserver;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.main_with_customctrl);
        super.ingameControlsEditorListener = new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_ctrl_load:
                        CustomControlsActivity.load(mControlLayout);
                        break;
                    case R.id.menu_ctrl_add:
                        mControlLayout.addControlButton(new ControlData("New", LWJGLGLFWKeycode.GLFW_KEY_UNKNOWN, 100, 100));
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
            }
        };

        mClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (view instanceof ControlButton) {
                    ControlButton button = (ControlButton) view;
                    switch (button.getProperties().keycode) {
                        case ControlData.SPECIALBTN_KEYBOARD:
                            showKeyboard();
                            break;

                        case ControlData.SPECIALBTN_TOGGLECTRL:
                            mControlLayout.toggleControlVisible();
                            break;

                        case ControlData.SPECIALBTN_VIRTUALMOUSE:
                            toggleMouse(button);
                            break;
                    }
                }
            }
        };
        
        mTouchListener = new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                boolean isDown;
                switch (e.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN: // 0
                    case MotionEvent.ACTION_POINTER_DOWN: // 5
                        isDown = true;
                        break;
                    case MotionEvent.ACTION_UP: // 1
                    case MotionEvent.ACTION_CANCEL: // 3
                    case MotionEvent.ACTION_POINTER_UP: // 6
                        isDown = false;
                        break;
                    default:
                        return false;
                }

                if (view instanceof ControlButton) {
                    ControlButton button = (ControlButton) view;
                    switch (button.getProperties().keycode) {
                        case ControlData.SPECIALBTN_MOUSEPRI:
                            sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, isDown);
                            break;

                        case ControlData.SPECIALBTN_MOUSEMID:
                            sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_MIDDLE, isDown);
                            break;

                        case ControlData.SPECIALBTN_MOUSESEC:
                            if (CallbackBridge.isGrabbing()) {
                                sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, isDown);
                            } else {
                                CallbackBridge.putMouseEventWithCoords(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, isDown ? 1 : 0, CallbackBridge.mouseX, CallbackBridge.mouseY);

                                setRightOverride(isDown);
                            } 
                            break;
                            
                        case ControlData.SPECIALBTN_SCROLLDOWN:
                            if(!isDown)CallbackBridge.sendScroll(0, 1d);
                            break;
                            
                        case ControlData.SPECIALBTN_SCROLLUP:
                            if(!isDown)CallbackBridge.sendScroll(0, -1d);
                            break;
                    }
                }

                return false;
            }
        };

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            fileObserver = new FileObserver(new File(Tools.DIR_GAME_NEW + "/options.txt"), FileObserver.MODIFY) {
                @Override
                public void onEvent(int i, @Nullable String s) {
                    //FIXME Make sure the multithreading nature of this event doesn't cause any problems ?
                    MCOptionUtils.load();
                    getMcScale();
                }
            };
        }else{
            fileObserver = new FileObserver(Tools.DIR_GAME_NEW + "/options.txt", FileObserver.MODIFY) {
                @Override
                public void onEvent(int i, @Nullable String s) {
                    //FIXME Make sure the multithreading nature of this event doesn't cause any problems ?
                    MCOptionUtils.load();
                    getMcScale();
                }
            };
        }

        fileObserver.startWatching();
        
        ControlData[] specialButtons = ControlData.getSpecialButtons();
        specialButtons[0].specialButtonListener
            = specialButtons[1].specialButtonListener
            = specialButtons[4].specialButtonListener
            = mClickListener;

        specialButtons[2].specialButtonListener
            = specialButtons[3].specialButtonListener
            = specialButtons[5].specialButtonListener
            = specialButtons[6].specialButtonListener 
            = specialButtons[7].specialButtonListener
            = mTouchListener;
        
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
            LauncherPreferences.loadPreferences();
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
