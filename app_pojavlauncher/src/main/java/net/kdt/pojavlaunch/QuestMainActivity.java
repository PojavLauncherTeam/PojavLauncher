package net.kdt.pojavlaunch;

import android.os.Bundle;
import android.util.Log;

public class QuestMainActivity extends LoggableActivity {
    private static final String TAG = "QuestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Oculus says to do it and we're getting mysterious errors, maybe this will help:
        // https://developer.oculus.com/downloads/package/oculus-openxr-mobile-sdk/
        System.loadLibrary("openxr_loader");

        Log.i(TAG, "Start activity bind");
        OpenComposite.bindActivity(this);

        Thread thread = new Thread(() -> RenderingTestRunActivity.runJava(this));
        thread.setName("minecraft-java-start-thread");
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "Quest activity was destroyed, this may be bad news if the runtime tries to use a GC'd handle to us");
    }

    @Override
    public void appendToLog(String text, boolean checkAllow) {
        Log.i(TAG, text);
    }
}
