package net.kdt.pojavlaunch.modloaders.modpacks.api;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import net.kdt.pojavlaunch.LauncherActivity;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.modloaders.ModloaderDownloadListener;
import net.kdt.pojavlaunch.modloaders.modpacks.ModloaderInstallTracker;
import net.kdt.pojavlaunch.value.NotificationConstants;

import java.io.File;

public class NotificationDownloadListener implements ModloaderDownloadListener {

    private final NotificationCompat.Builder mNotificationBuilder;
    private final NotificationManager mNotificationManager;
    private final Context mContext;
    private final ModLoader mModLoader;
    
    public NotificationDownloadListener(Context context, ModLoader modLoader) {
        mModLoader = modLoader;
        mContext = context.getApplicationContext();
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(context, "channel_id")
                .setContentTitle(context.getString(R.string.modpack_install_notification_title))
                .setSmallIcon(R.drawable.notif_icon);
    }

    @Override
    public void onDownloadFinished(File downloadedFile) {
        ModloaderInstallTracker.saveModLoader(mContext, mModLoader, downloadedFile);
        Intent mainActivityIntent = new Intent(mContext, LauncherActivity.class);
        Tools.runOnUiThread(()->sendIntentNotification(mainActivityIntent, R.string.modpack_install_notification_success));
    }

    @Override
    public void onDataNotAvailable() {
        Tools.runOnUiThread(()->sendEmptyNotification(R.string.modpack_install_notification_data_not_available));
    }

    @Override
    public void onDownloadError(Exception e) {
        Tools.showErrorRemote(mContext, R.string.modpack_install_modloader_download_failed, e);
    }
    
    private void sendIntentNotification(Intent intent, int contentText) {
        PendingIntent pendingInstallIntent =
                PendingIntent.getActivity(mContext, NotificationConstants.PENDINGINTENT_CODE_DOWNLOAD_SERVICE,
                        intent, Build.VERSION.SDK_INT >=23 ? PendingIntent.FLAG_IMMUTABLE : 0);

        mNotificationBuilder.setContentText(mContext.getText(contentText));
        mNotificationBuilder.setContentIntent(pendingInstallIntent);
        mNotificationManager.notify(NotificationConstants.NOTIFICATION_ID_DOWNLOAD_LISTENER, mNotificationBuilder.build());
    }

    private void sendEmptyNotification(int contentText) {
        mNotificationBuilder.setContentText(mContext.getText(contentText));
        mNotificationManager.notify(NotificationConstants.NOTIFICATION_ID_DOWNLOAD_LISTENER, mNotificationBuilder.build());
    }
}
