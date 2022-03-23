package net.kdt.pojavlaunch.customcontrols.keyboard;


import static android.content.Context.INPUT_METHOD_SERVICE;

import static org.lwjgl.glfw.CallbackBridge.sendKeyPress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.LwjglGlfwKeycode;
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


    private boolean mIsDoingInternalChanges = false;
    private CharacterSenderStrategy mCharacterSender;

    /**
     * We take the new chars, and send them to the game.
     * If less chars are present, remove some.
     * The text is always cleaned up.
     */
    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if(mIsDoingInternalChanges)return;
        if(mCharacterSender != null){
            for(int i=0; i < lengthBefore; ++i){
                mCharacterSender.sendBackspace();
            }

            for(int i=start, count = 0; count < lengthAfter; ++i){
                mCharacterSender.sendChar(text.charAt(i));
                ++count;
            }
        }

        //Reset the keyboard state
        if(text.length() < 1) clear();
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
        mIsDoingInternalChanges = true;
        //Braille space, doesn't trigger keyboard auto-complete
        //replacing directly the text without though setText avoids notifying changes
        setText("                              ");
        setSelection(getText().length());
        mIsDoingInternalChanges = false;
    }

    /** Regain ability to exist, take focus and have some text being input */
    public void enable(){
        setEnabled(true);
        setFocusable(true);
        setVisibility(VISIBLE);
        requestFocus();

    }

    /** Lose ability to exist, take focus and have some text being input */
    public void disable(){
        clear();
        setVisibility(GONE);
        clearFocus();
        setEnabled(false);
    }

    /** Send the enter key. */
    private void sendEnter(){
        mCharacterSender.sendEnter();
        clear();
    }

    /** Just sets the char sender that should be used. */
    public void setCharacterSender(CharacterSenderStrategy characterSender){
        mCharacterSender = characterSender;
    }

    /** This function deals with anything that has to be executed when the constructor is called */
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
