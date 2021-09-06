package net.kdt.pojavlaunch.customcontrols;


import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.BaseMainActivity;
import net.kdt.pojavlaunch.LWJGLGLFWKeycode;
import net.kdt.pojavlaunch.R;

import org.lwjgl.glfw.CallbackBridge;

/**
 * This class is intended for sending characters used in chat via the virtual keyboard
 */
public class TouchCharInput extends androidx.appcompat.widget.AppCompatEditText {
    public TouchCharInput(@NonNull Context context) {
        this(context, null);
    }
    public TouchCharInput(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
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
                //I didn't know F25 existed before that. I just need a full fat keycode for mc 1.13+
                CallbackBridge.sendKeycode(LWJGLGLFWKeycode.GLFW_KEY_F25, text.charAt(index), 0, 0, true);
                index ++;
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
     * Toggle on and off the soft keyboard, depending of the state
     *
     * @return if the keyboard is set to be shown.
     */
    public boolean switchKeyboardState(){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
        //If an hard keyboard is present, never trigger the soft one
        if(hasFocus()
                || (getResources().getConfiguration().keyboard == Configuration.KEYBOARD_QWERTY
                && getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES)){
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
            clear();
            disable();
            return false;
        }else{
            enable();
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
            return true;
        }
    }


    /**
     * Clear the EditText from any leftover inputs
     * It does not affect the in-game input
     */
    @SuppressLint("SetTextI18n")
    public void clear(){
        isDoingInternalChanges = true;
        //Braille space, doesn't trigger keyboard auto-complete
        //replacing directly the text without though setText avoids notifying changes
        getText().replace(0, getText().length(),"\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800\u2800");
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
            clear();
            disable();
            return false;
        });
        clear();
        disable();
    }

}
