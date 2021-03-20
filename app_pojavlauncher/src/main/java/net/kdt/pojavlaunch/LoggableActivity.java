package net.kdt.pojavlaunch;


import android.app.Activity;

public abstract class LoggableActivity extends BaseActivity implements ILoggableActivity {
    @Override
    public Activity asActivity() {
        return this;
    }
}
