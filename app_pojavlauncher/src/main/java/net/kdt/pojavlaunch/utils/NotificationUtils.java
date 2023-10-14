package net.kdt.pojavlaunch.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import net.kdt.pojavlaunch.R;

public class NotificationUtils {

    public static final int NOTIFICATION_ID_PROGRESS_SERVICE = 1;
    public static final int NOTIFICATION_ID_GAME_SERVICE = 2;
    public static final int NOTIFICATION_ID_DOWNLOAD_LISTENER = 3;
    public static final int NOTIFICATION_ID_SHOW_ERROR = 4;
    public static final int NOTIFICATION_ID_GAME_START = 5;
    public static final int PENDINGINTENT_CODE_KILL_PROGRESS_SERVICE = 1;
    public static final int PENDINGINTENT_CODE_KILL_GAME_SERVICE = 2;
    public static final int PENDINGINTENT_CODE_DOWNLOAD_SERVICE = 3;
    public static final int PENDINGINTENT_CODE_SHOW_ERROR = 4;
    public static final int PENDINGINTENT_CODE_GAME_START = 5;

    public static void sendBasicNotification(Context context, int contentTitle, int contentText, Intent actionIntent,
                                             int pendingIntentCode, int notificationId) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, pendingIntentCode, actionIntent,
                Build.VERSION.SDK_INT >=23 ? PendingIntent.FLAG_IMMUTABLE : 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, context.getString(R.string.notif_channel_id));
        if(contentTitle != -1) notificationBuilder.setContentTitle(context.getString(contentTitle));
        if(contentText != -1) notificationBuilder.setContentText(context.getString(contentText));
        if(actionIntent != null) notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setSmallIcon(R.drawable.notif_icon);

        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
