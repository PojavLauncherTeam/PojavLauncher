package net.kdt.pojavlaunch.prefs;

import android.content.*;
import android.support.v7.preference.*;
import android.util.*;
import java.io.*;
import net.kdt.pojavlaunch.*;

import net.kdt.pojavlaunch.R;
import android.widget.*;

public class UninstallJREDialogPreference extends DialogPreference implements DialogInterface.OnClickListener
{
    public UninstallJREDialogPreference(Context ctx) {
        this(ctx, null);
    }
    
    public UninstallJREDialogPreference(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        setPersistent(false);
        setDialogMessage(R.string.mcl_setting_title_uninstalljre);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
    }
    
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Tools.deleteRecursive(new File(Tools.homeJreDir));
            getContext().getSharedPreferences("pojav_extract", Context.MODE_PRIVATE)
                .edit().putBoolean(PojavLoginActivity.PREF_IS_INSTALLED_JAVARUNTIME, false).commit();
            
            Toast.makeText(getContext(), R.string.toast_uninstalljre_done, Toast.LENGTH_SHORT).show();
        }
    }
}
