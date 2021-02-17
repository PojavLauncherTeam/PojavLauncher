package net.kdt.pojavlaunch;

import android.content.*;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.*;
import android.view.*;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

public class MinecraftGLView extends TextureView
{
    public MinecraftGLView(Context context) {
        this(context, null);
    }

    public MinecraftGLView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    ((InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(),0);
                }
            }
        });
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.inputType = EditorInfo.TYPE_NULL;
        Log.d("TypeableGLView","onCreateInputConnection");
            return new MinecraftInputConnection(this, false);
    }
    @Override
    public boolean onCheckIsTextEditor() {
        return false;
    }
    public static boolean isHardKB(Context ctx) {
        return ctx.getResources().getConfiguration().keyboard == Configuration.KEYBOARD_QWERTY;
    }
    
    
}
class MinecraftInputConnection extends BaseInputConnection {
    private SpannableStringBuilder _editable;
    BaseMainActivity parent;
    public MinecraftInputConnection(View targetView, boolean fullEditor) {
        super(targetView, fullEditor);

        parent = (BaseMainActivity)targetView.getContext();
    }

    public Editable getEditable() {
        if (_editable == null) {
            _editable = (SpannableStringBuilder) Editable.Factory.getInstance()
                    .newEditable("Placeholder");
        }
        return _editable;
    }

    public boolean commitText(CharSequence text, int newCursorPosition) {
        for(int i = 0; i < text.length(); i++) parent.sendKeyPress(text.charAt(i));
        return true;
    }

    @Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        for(int i = 0; i < beforeLength; i++) {
            parent.sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_BACKSPACE);
        }
        return true;
    }
}
