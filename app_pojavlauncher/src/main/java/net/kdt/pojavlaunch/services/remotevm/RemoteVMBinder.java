package net.kdt.pojavlaunch.services.remotevm;

import android.content.Context;
import android.os.Process;
import android.os.RemoteException;

import net.kdt.pojavlaunch.Logger;
import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.services.IRemoteVMLogCallback;
import net.kdt.pojavlaunch.services.IRemoteVMProcess;
import net.kdt.pojavlaunch.utils.JREUtils;

import java.util.Arrays;

public class RemoteVMBinder extends IRemoteVMProcess.Stub implements Logger.eventLogListener{
    private final Context mContext;
    private IRemoteVMLogCallback mCallback;
    RemoteVMBinder(Context context) {
        mContext = context;
    }

    @Override
    public void runJVM(IRemoteVMLogCallback logCallback, String[] commandLine) throws RemoteException {
        try {
            this.mCallback = logCallback;
            MultiRTUtils.setRuntimeNamed(LauncherPreferences.PREF_DEFAULT_RUNTIME);
            Logger.getInstance().setLogListener(this);
            JREUtils.launchJavaVM(mContext, Arrays.asList(commandLine));
        }catch (Throwable th) {
            RemoteException exception = new RemoteException("We fucked up");
            exception.initCause(th);
            throw exception;
        }
        suicide();
    }

    @Override
    public void onEventLogged(String text) {
        try {
            if (mCallback != null) mCallback.getLogLine(text);
        }catch (Exception e) {
            e.printStackTrace();
            suicide();
        }
    }
    private void suicide() {
        Process.killProcess(Process.myPid());
    }
}
