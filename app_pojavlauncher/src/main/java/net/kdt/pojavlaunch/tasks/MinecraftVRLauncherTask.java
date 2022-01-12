package net.kdt.pojavlaunch.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class MinecraftVRLauncherTask extends AsyncTask<String, String, Throwable> {
    private final Context activity;

    public MinecraftVRLauncherTask(Context activity) {
        this.activity = activity;
    }

    @Override
    protected Throwable doInBackground(String... strings) {
        try {
            Intent launchIntent = activity.getPackageManager()
                    .getLaunchIntentForPackage("me.maniac.mcxrlauncher");
            activity.startActivity(launchIntent);
        } catch(Exception e) {
            return e;
        }
        return null;
    }
}
