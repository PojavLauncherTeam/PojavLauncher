package net.kdt.pojavlaunch;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

public class MainEditText extends EditText {
    public MainKeyWatcher _MainKeyWatcher;
    public final int allowedLength;
    public final boolean limitInput;

    private class MainInputConnection extends InputConnectionWrapper {
        MainEditText textbox;

        public MainInputConnection(InputConnection target, boolean mutable, MainEditText textbox2) {
            super(target, mutable);
            this.textbox = textbox2;
        }

        public boolean sendKeyEvent(KeyEvent event) {
            if (this.textbox.getText().length() != 0 || event.getAction() != 0 || event.getKeyCode() != 67) {
                return super.sendKeyEvent(event);
            }
            if (MainEditText.this._MainKeyWatcher != null) {
                MainEditText.this._MainKeyWatcher.onDeleteKeyPressed();
            }
            return false;
        }
    }

    public interface MainKeyWatcher {
        void onBackKeyPressed();

        void onDeleteKeyPressed();
    }

    public MainEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this._MainKeyWatcher = null;
        this.allowedLength = 160;
        this.limitInput = false;
    }

    public MainEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._MainKeyWatcher = null;
        this.allowedLength = 160;
        this.limitInput = false;
    }

    public MainEditText(Context context, int allowedLength2, boolean limitInput2) {
        super(context);
        this._MainKeyWatcher = null;
        this.allowedLength = allowedLength2;
        this.limitInput = limitInput2;
        setFilters(limitInput2 ? new InputFilter[]{new LengthFilter(this.allowedLength), new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                return (!source.equals("") && !source.toString().matches("^[a-zA-Z0-9_ -^~'.,;!#&()=`{}]*")) ? "" : source;
            }
        }} : new InputFilter[]{new LengthFilter(this.allowedLength)});
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new MainInputConnection(super.onCreateInputConnection(outAttrs), true, this);
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK || event.getAction() != KeyEvent.ACTION_UP) {
            return super.dispatchKeyEvent(event);
        }
        if (this._MainKeyWatcher != null) {
            this._MainKeyWatcher.onBackKeyPressed();
        }
        return false;
    }

    public void setOnMainKeyWatcher(MainKeyWatcher MainKeyWatcher) {
        this._MainKeyWatcher = MainKeyWatcher;
    }
}
