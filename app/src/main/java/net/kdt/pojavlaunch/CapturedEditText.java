package net.kdt.pojavlaunch;

import android.content.*;
import android.util.*;
import android.widget.*;
import android.view.*;

public class CapturedEditText extends TextView
{
    public CapturedEditText(Context ctx) {
        this(ctx, null);
    }
    
    public CapturedEditText(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }
    
    @Override
    public boolean getDefaultEditable() {
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AndroidLWJGLKeycode.execKey(event, keyCode, true);
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        AndroidLWJGLKeycode.execKey(event, keyCode, false);
        return true;
    }
}
