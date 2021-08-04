package net.kdt.pojavlaunch.customcontrols;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;

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

    private boolean isClearingText = false;

    TextWatcher mTextWatcher = new TextWatcher() {
        //TODO Engineer a more performant system
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(isClearingText) return;

            for(int j=0; j<charSequence.length(); ++j){
                CallbackBridge.sendKeycode(LWJGLGLFWKeycode.GLFW_KEY_BACKSPACE, '\u0008', 0, 0, true);
            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int lengthBefore, int lengthAfter) {
            if(isClearingText) return;

            for (int i=0; i<charSequence.length(); ++i){
                CallbackBridge.sendChar(charSequence.charAt(i));
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(isClearingText){
                isClearingText = false;
                editable.clear();
            }
        }
    };


    /**
     * Clear the EditText from any leftover inputs
     * It does not affect the in-game input
     */
    public void clear(){
        isClearingText = true;
        setText(" ");
    }

    /**
     * Send the text stored to the game
     */
    private void send(){
        //TODO proper focus removal ?
        BaseMainActivity.sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_ENTER);
        clear();
    }

    /**
     * This function deals with anything that has to be executed when the constructor is called
     */
    private void setup(){
        //The text watcher used to look and send text
        addTextChangedListener(mTextWatcher);

        setOnEditorActionListener((textView, i, keyEvent) -> {
            //TODO remove the focus from the EditText ?
            send();
            return false;
        });
    }

}
