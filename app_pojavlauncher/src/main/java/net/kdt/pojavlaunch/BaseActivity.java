package net.kdt.pojavlaunch;

import android.content.*;
import android.os.*;
import androidx.appcompat.app.*;
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

    @Override
    public void startActivity(Intent i) {
        super.startActivity(i);
        new Throwable("StartActivity").printStackTrace();
    }
}
