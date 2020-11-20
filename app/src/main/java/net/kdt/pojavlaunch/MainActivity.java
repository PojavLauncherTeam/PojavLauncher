package net.kdt.pojavlaunch;

import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import net.kdt.pojavlaunch.customcontrols.*;
import net.kdt.pojavlaunch.prefs.*;
import org.lwjgl.glfw.*;

public class MainActivity extends BaseMainActivity implements OnClickListener, OnTouchListener {
    private Button upButton,
    downButton, leftButton,
    rightButton, jumpButton,
    primaryButton, secondaryButton,
    debugButton, shiftButton,
    keyboardButton, inventoryButton,
    talkButton, thirdPersonButton,
    zoomButton, listPlayersButton,
	toggleControlButton;

    private Button[] controlButtons;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.main);
        
        this.upButton = findButton(R.id.control_up);
        this.downButton = findButton(R.id.control_down);
        this.leftButton = findButton(R.id.control_left);
        this.rightButton = findButton(R.id.control_right);
        this.jumpButton = findButton(R.id.control_jump);
        this.primaryButton = findButton(R.id.control_primary);
        this.secondaryButton = findButton(R.id.control_secondary);
        this.debugButton = findButton(R.id.control_debug);
        this.shiftButton = findButton(R.id.control_shift);
        this.keyboardButton = findButton(R.id.control_keyboard);
        this.inventoryButton = findButton(R.id.control_inventory);
        this.talkButton = findButton(R.id.control_talk);
        this.thirdPersonButton = findButton(R.id.control_thirdperson);
        this.zoomButton = findButton(R.id.control_zoom);
        this.listPlayersButton = findButton(R.id.control_listplayers);
        this.toggleControlButton = findButton(R.id.control_togglecontrol);
        this.controlButtons = new Button[]{
            upButton, downButton, leftButton, rightButton,
            jumpButton, primaryButton, secondaryButton,
            debugButton, shiftButton, keyboardButton,
            inventoryButton, talkButton, thirdPersonButton,
            listPlayersButton
        };
        this.toggleControlButton.setOnClickListener(this);
        this.zoomButton.setVisibility(mVersionInfo.optifineLib == null ? View.GONE : View.VISIBLE);
        
        ControlData[] specialButtons = ControlData.getSpecialButtons();
        specialButtons[1].specialButtonListener = this;

        // toggleGui(null);
        onClick(toggleControlButton);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.control_togglecontrol: {
                    /*
                     switch(overlayView.getVisibility()){
                     case View.VISIBLE: overlayView.setVisibility(View.GONE);
                     break;
                     case View.GONE: overlayView.setVisibility(View.VISIBLE);
                     }
                     */

                    for (Button button : controlButtons) {
                        button.setVisibility(button.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    }

                    zoomButton.setVisibility((zoomButton.getVisibility() == View.GONE && mVersionInfo.optifineLib != null) ? View.VISIBLE : View.GONE);
                }
        }
    }

    public boolean onTouch(View v, MotionEvent e) {
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

        switch (v.getId()) {
            case R.id.control_up: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_W, 0, isDown); break;
            case R.id.control_left: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_A, 0, isDown); break;
            case R.id.control_down: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_S, 0, isDown); break;
            case R.id.control_right: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_D, 0, isDown); break;
            case R.id.control_jump: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_SPACE, 0, isDown); break;
            case R.id.control_primary: sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, isDown); break;
            case R.id.control_secondary:
                if (CallbackBridge.isGrabbing()) {
                    sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, isDown);
                } else {
                    /*
                     if (!isDown) {
                     CallbackBridge.putMouseEventWithCoords(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, CallbackBridge.mouseX, CallbackBridge.mouseY);
                     }
                     */

                    CallbackBridge.putMouseEventWithCoords(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, isDown ? 1 : 0, CallbackBridge.mouseX, CallbackBridge.mouseY);

                    setRightOverride(isDown);
                } break;
            case R.id.control_debug: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_F3, 0, isDown); break;
            case R.id.control_shift: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_LEFT_SHIFT, 0, isDown); break;
            case R.id.control_inventory: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_E, 0, isDown); break;
            case R.id.control_talk: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_T, 0, isDown); break;
            case R.id.control_keyboard: showKeyboard(); break;
            case R.id.control_thirdperson: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_F5, 0, isDown); break;
            case R.id.control_zoom: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_C, 0, isDown); break;
            case R.id.control_listplayers: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_TAB, 0, isDown); break;
        }

        return false;
    }
    
    private Button findButton(int id) {
        Button button = (Button) findViewById(id);
        button.setWidth((int) (button.getWidth() * Tools.currentDisplayMetrics.scaledDensity));
        button.setHeight((int) (button.getHeight() * LauncherPreferences.PREF_BUTTONSIZE));
        button.setOnTouchListener(this);
        button.setFocusable(false);
        button.setFocusableInTouchMode(false);
        return button;
    }
}
