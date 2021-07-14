package net.kdt.pojavlaunch.prefs;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.Preference;

import net.kdt.pojavlaunch.BaseLauncherActivity;
import net.kdt.pojavlaunch.multirt.MultiRTConfigDialog;

public class RuntimeManagerPreference extends Preference{
    public RuntimeManagerPreference(Context ctx) {
        this(ctx, null);
    }

    public RuntimeManagerPreference(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        setPersistent(false);
    }
    @Override
    protected void onClick() {
        super.onClick();
        ((BaseLauncherActivity)this.getContext()).mRuntimeConfigDialog.dialog.show();
    }
}
