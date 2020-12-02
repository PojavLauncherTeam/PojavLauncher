package net.kdt.pojavlaunch;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import net.kdt.pojavlaunch.utils.*;

public class BaseActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.setFullscreen(this);
        Tools.updateWindowSize(this);
    }
    
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.setLocale(base));
    }
}
