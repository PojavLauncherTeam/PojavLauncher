package net.kdt.pojavlaunch.prefs;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.Preference;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;

public class BackButtonPreference extends Preference {
    public BackButtonPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BackButtonPreference(Context context) {
        this(context, null);
    }

    private void init(){
        if(getTitle() == null){
            setTitle(R.string.preference_back_title);
        }
        if(getIcon() == null){
            setIcon(R.drawable.ic_arrow_back_white);
        }
    }


    @Override
    protected void onClick() {
        // It is caught by an ExtraListener in the LauncherActivity
        ExtraCore.setValue(ExtraConstants.BACK_PREFERENCE, "true");
    }
}
