package net.kdt.pojavlaunch.modloaders.modpacks.api;

import android.content.Context;
import android.content.Intent;

import net.kdt.pojavlaunch.LauncherActivity;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.modloaders.ModloaderDownloadListener;
import net.kdt.pojavlaunch.modloaders.modpacks.ModloaderInstallTracker;
import net.kdt.pojavlaunch.utils.NotificationUtils;

import java.io.File;

public class NotificationDownloadListener implements ModloaderDownloadListener {
    private final Context mContext;
    private final ModLoader mModLoader;
    
    public NotificationDownloadListener(Context context, ModLoader modLoader) {
        mModLoader = modLoader;
        mContext = context.getApplicationContext();
    }

    @Override
    public void onDownloadFinished(File downloadedFile) {
        if(mModLoader.requiresGuiInstallation()) {
            ModloaderInstallTracker.saveModLoader(mContext, mModLoader, downloadedFile);
            Intent mainActivityIntent = new Intent(mContext, LauncherActivity.class);
            Tools.runOnUiThread(() -> NotificationUtils.sendBasicNotification(mContext,
                    R.string.modpack_install_notification_title,
                    R.string.modpack_install_notification_success,
                    mainActivityIntent,
                    NotificationUtils.PENDINGINTENT_CODE_DOWNLOAD_SERVICE,
                    NotificationUtils.NOTIFICATION_ID_DOWNLOAD_LISTENER
            ));
        }
    }

    @Override
    public void onDataNotAvailable() {
        Tools.runOnUiThread(()->NotificationUtils.sendBasicNotification(mContext,
                R.string.modpack_install_notification_title,
                R.string.modpack_install_notification_success,
                null,
                NotificationUtils.PENDINGINTENT_CODE_DOWNLOAD_SERVICE,
                NotificationUtils.NOTIFICATION_ID_DOWNLOAD_LISTENER
        ));
    }

    @Override
    public void onDownloadError(Exception e) {
        Tools.showErrorRemote(mContext, R.string.modpack_install_modloader_download_failed, e);
    }
}
