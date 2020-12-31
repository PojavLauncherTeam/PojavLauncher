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

public class MinecraftGLView extends TextureView
{
    volatile BaseMainActivity ctx;
	// private View.OnTouchListener mTouchListener;
    public MinecraftGLView(Context context) {
        super(context);
		//setPreserveEGLContextOnPause(true);
        ctx = (BaseMainActivity) context;
    }

    public MinecraftGLView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
		//setPreserveEGLContextOnPause(true);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.inputType = EditorInfo.TYPE_NULL;
        if(!isHardKB(this.getContext())) {
            return new MinecraftInputConnection(this, false);
        }else{
            return new BaseInputConnection(this,false);
        }
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
        Log.d("EnhancedTextInput","Text committed: "+text);
        parent.sendKeyPress(text.charAt(0
        ));
        return true;
    }

}
