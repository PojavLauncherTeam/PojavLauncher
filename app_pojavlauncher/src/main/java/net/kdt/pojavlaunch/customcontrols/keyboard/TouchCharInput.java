package net.kdt.pojavlaunch.customcontrols.keyboard;


import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.R;

/**
 * This class is intended for sending characters used in chat via the virtual keyboard
 */
public class TouchCharInput extends androidx.appcompat.widget.AppCompatEditText {
    public static final String TEXT_FILLER = "                              ";
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
     */
    public void switchKeyboardState(){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
        // Allow, regardless of whether or not a hardware keyboard is declared
        if(hasFocus()){
            clear();
            disable();
        }else{
            enable();
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
        }
    }


    /**
     * Clear the EditText from any leftover inputs
     * It does not affect the in-game input
     */
    public void clear(){
        mIsDoingInternalChanges = true;
        // Edit the Editable directly as it doesn't affect the state
        // of the TextView.
        Editable editable = getEditableText();
        editable.clear();
        //Braille space, doesn't trigger keyboard auto-complete
        editable.append(TEXT_FILLER);
        Selection.setSelection(editable, TEXT_FILLER.length());
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
        //setFocusable(false);
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
        // Using TextWatcher instead of overriding onTextChanged because some Huawei firmware
        // calls setText in constructor, causing havoc for our listener
        addTextChangedListener(new InputTextWatcher());
        setOnEditorActionListener((textView, i, keyEvent) -> {
            sendEnter();
            clear();
            disable();
            return false;
        });
        clear();
        disable();
    }
    private class InputTextWatcher implements android.text.TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        /**
         * We take the new chars, and send them to the game.
         * If less chars are present, remove some.
         * The text is always cleaned up.
         */
        @Override
        public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
            if(mIsDoingInternalChanges) return;
            if(mCharacterSender != null){
                for(int i=0; i < lengthBefore; ++i){
                    mCharacterSender.sendBackspace();
                }

                for(int i=start, count = 0; count < lengthAfter; ++i){
                    mCharacterSender.sendChar(text.charAt(i));
                    ++count;
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(mIsDoingInternalChanges) return;
            // Moved from onTextChanged because "It is an error to attempt to make changes to s from this callback."
            // reference: https://developer.android.com/reference/android/text/TextWatcher#onTextChanged(java.lang.CharSequence,%20int,%20int,%20int)
            if(editable.length() < 1) clear();
        }
    }
}
