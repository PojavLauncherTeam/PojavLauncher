package net.kdt.pojavlaunch;

import android.content.*;
import android.os.*;
import androidx.appcompat.app.*;
import net.kdt.pojavlaunch.utils.*;

import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_IGNORE_NOTCH;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.setLocale(this);
        Tools.setFullscreen(this);
        Tools.updateWindowSize(this);
    }


    @Override
    public void startActivity(Intent i) {
        super.startActivity(i);
        //new Throwable("StartActivity").printStackTrace();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Tools.ignoreNotch(PREF_IGNORE_NOTCH,this);
    }
}
