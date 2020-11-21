package net.kdt.pojavlaunch;

import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import net.kdt.pojavlaunch.customcontrols.*;
import net.kdt.pojavlaunch.prefs.*;
import org.lwjgl.glfw.*;

public class CustomCtrlMainActivity extends BaseMainActivity {
    private CustomControls mControl;
    private ControlLayout mControlLayout;
    
    private View.OnClickListener mClickListener;
    private View.OnTouchListener mTouchListener;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.main_with_customctrl);

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
                    }
                }

                return false;
            }
        };
        
        ControlData[] specialButtons = ControlData.getSpecialButtons();
        specialButtons[0].specialButtonListener
            = specialButtons[1].specialButtonListener
            = specialButtons[4].specialButtonListener
            = mClickListener;

        specialButtons[2].specialButtonListener
            = specialButtons[3].specialButtonListener
            = specialButtons[5].specialButtonListener
            = mTouchListener;
        
        mControlLayout = findViewById(R.id.main_control_layout);
        mControl = new CustomControls();
        mControlLayout.setModifiable(false);
        loadControl(LauncherPreferences.PREF_DEFAULTCTRL_PATH);
        mControlLayout.loadLayout(mControl);
        
        // toggleGui(null);
        mControlLayout.toggleControlVisible();
    }

    private void loadControl(String path) {
        try {
            mControl = Tools.GLOBAL_GSON.fromJson(Tools.read(path), CustomControls.class);
            mControlLayout.loadLayout(mControl);
        } catch (Exception e) {
            Tools.showError(this, e);
        }
    }
}
