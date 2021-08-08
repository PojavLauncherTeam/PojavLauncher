package net.kdt.pojavlaunch.customcontrols;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.BaseMainActivity;
import net.kdt.pojavlaunch.LWJGLGLFWKeycode;

import org.lwjgl.glfw.CallbackBridge;

/**
 * This class is intended for sending characters used in chat via the virtual keyboard
 */
public class TouchCharInput extends androidx.appcompat.widget.AppCompatEditText {
    public TouchCharInput(@NonNull Context context) {
        super(context);
        setup();
    }
    public TouchCharInput(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup();
    }
    public TouchCharInput(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }


    private boolean isDoingInternalChanges = false;

    /**
     * We take the new chars, and send them to the game.
     * If less chars are present, remove some.
     * The text is always cleaned up.
     */
    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if(isDoingInternalChanges){
            isDoingInternalChanges = false;
            return;
        }
        if(lengthAfter < lengthBefore){
            for(int i=0; i< lengthBefore-lengthAfter; ++i){
                CallbackBridge.sendKeycode(LWJGLGLFWKeycode.GLFW_KEY_BACKSPACE, '\u0008', 0, 0, true);
            }
        }else{
            for(int i=lengthBefore, index=lengthBefore+start; i < lengthAfter; ++i){
                CallbackBridge.sendChar(text.charAt(index));
            }
        }

        clear();
    }


    /**
     * When we change from app to app, the keyboard gets disabled.
     * So, we disable the object
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        disable();
    }

    /**
     * Intercepts the back key to disable focus
     * Does not affect the rest of the activity.
     */
    @Override
    public boolean onKeyPreIme(final int keyCode, final KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            disable();
        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    public void setSelection(int index) {
        super.setSelection(5);
    }

    /**
     * Clear the EditText from any leftover inputs
     * It does not affect the in-game input
     */
    public void clear(){
        isDoingInternalChanges = true;
        setText("          ");
        setSelection(5);
    }

    /**
     * Send the enter key.
     */
    private void sendEnter(){
        BaseMainActivity.sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_ENTER);
        clear();
    }

    /**
     * Regain ability to exist, take focus and have some text being input
     */
    public void enable(){
        setEnabled(true);
        setFocusable(true);
        setVisibility(VISIBLE);
        requestFocus();

    }

    /**
     * Lose ability to exist, take focus and have some text being input
     */
    public void disable(){
        clear();
        setVisibility(GONE);
        clearFocus();
        setEnabled(false);
    }



    /**
     * This function deals with anything that has to be executed when the constructor is called
     */
    private void setup(){
        setOnEditorActionListener((textView, i, keyEvent) -> {
            sendEnter();
            return false;
        });
        clear();
        disable();
    }

}
