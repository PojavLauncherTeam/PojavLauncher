package net.kdt.pojavlaunch;

import android.content.*;
import android.util.*;
import android.widget.*;
import android.view.*;

public class CapturedEditText extends EditText
{
    public CapturedEditText(Context ctx) {
        this(ctx, null);
    }
    
    public CapturedEditText(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event){
        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                AndroidLWJGLKeycode.execKey(event, keyCode, true);
                break;
                
            case KeyEvent.ACTION_UP:
                AndroidLWJGLKeycode.execKey(event, keyCode, false);
                break;
        }
        return false;
    }
}
