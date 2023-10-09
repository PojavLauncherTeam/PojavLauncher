package net.kdt.pojavlaunch.mirrors;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.ShowErrorActivity;
import net.kdt.pojavlaunch.contextexecutor.ContextExecutorTask;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

public class MirrorTamperedException extends Exception implements ContextExecutorTask {
    // Do not change. Android really hates when this value changes for some reason.
    private static final long serialVersionUID = -7482301619612640658L;
    @Override
    public void executeWithActivity(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.dl_tampered_manifest_title);
        builder.setMessage(Html.fromHtml(activity.getString(R.string.dl_tampered_manifest)));
        builder.setPositiveButton(R.string.dl_switch_to_official_site,(d,w)->{
            LauncherPreferences.DEFAULT_PREF.edit().putString("downloadSource", "default").apply();
            LauncherPreferences.PREF_DOWNLOAD_SOURCE = "default";

        });
        builder.setNegativeButton(R.string.dl_turn_off_manifest_checks,(d,w)->{
            LauncherPreferences.DEFAULT_PREF.edit().putBoolean("verifyManifest", false).apply();
            LauncherPreferences.PREF_VERIFY_MANIFEST = false;
        });
        builder.setNeutralButton(android.R.string.cancel, (d,w)->{});
        ShowErrorActivity.installRemoteDialogHandling(activity, builder);
        builder.show();
    }

    @Override
    public void executeWithApplication(Context context) {}
}
