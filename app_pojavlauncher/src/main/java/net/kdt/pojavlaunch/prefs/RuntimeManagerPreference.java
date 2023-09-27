package net.kdt.pojavlaunch.prefs;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.Preference;


import net.kdt.pojavlaunch.multirt.MultiRTConfigDialog;

public class RuntimeManagerPreference extends Preference{
    private MultiRTConfigDialog mDialogScreen;

    @SuppressWarnings("unused") public RuntimeManagerPreference(Context ctx) {
        this(ctx, null);
    }

    public RuntimeManagerPreference(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        setPersistent(false);
    }

    @Override
    protected void onClick() {
        if(mDialogScreen == null){
            mDialogScreen = new MultiRTConfigDialog();
            mDialogScreen.prepare(((Activity) getContext()));
        }
        mDialogScreen.show();
    }
}
