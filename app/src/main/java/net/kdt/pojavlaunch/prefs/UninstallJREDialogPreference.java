package net.kdt.pojavlaunch.prefs;

import android.content.*;
import androidx.appcompat.app.*;
import androidx.preference.*;
import android.util.*;
import android.widget.*;
import java.io.*;
import net.kdt.pojavlaunch.*;

import net.kdt.pojavlaunch.R;
import org.apache.commons.io.*;

public class UninstallJREDialogPreference extends Preference implements DialogInterface.OnClickListener
{
    private AlertDialog mDialog;
    public UninstallJREDialogPreference(Context ctx) {
        this(ctx, null);
    }
    
    public UninstallJREDialogPreference(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        setPersistent(false);
        
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setMessage(R.string.mcl_setting_title_uninstalljre);
        dialog.setPositiveButton(android.R.string.ok, this);
        dialog.setNegativeButton(android.R.string.cancel, this);
        mDialog = dialog.create();
    }

    @Override
    protected void onClick() {
        super.onClick();
        mDialog.show();
    }
    
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            try {
                FileUtils.deleteDirectory(new File(Tools.DIR_HOME_JRE));
                
                getContext().getSharedPreferences("pojav_extract", Context.MODE_PRIVATE)
                    .edit().putBoolean(PojavLoginActivity.PREF_IS_INSTALLED_JAVARUNTIME, false).commit();

                Toast.makeText(getContext(), R.string.toast_uninstalljre_done, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Tools.showError(getContext(), e);
            }
        }
    }
}
