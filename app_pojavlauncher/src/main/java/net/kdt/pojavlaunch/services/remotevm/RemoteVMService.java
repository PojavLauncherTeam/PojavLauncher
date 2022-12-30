package net.kdt.pojavlaunch.services.remotevm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

public class RemoteVMService extends Service {
    private final RemoteVMBinder mBinder = new RemoteVMBinder(this);
    @Override
    public void onCreate() {
        LauncherPreferences.loadPreferences(this);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
