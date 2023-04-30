package net.kdt.pojavlaunch.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;

import java.lang.ref.WeakReference;

public class GameService extends Service {
    private static final WeakReference<Service> sGameService = new WeakReference<>(null);
    public static void startService(Context context) {
        Intent intent = new Intent(context, GameService.class);
        ContextCompat.startForegroundService(context, intent);
    }

    @Override
    public void onCreate() {
        Tools.buildNotificationChannel(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && intent.getBooleanExtra("kill", false)) {
            stopSelf();
            Process.killProcess(Process.myPid());
            return START_NOT_STICKY;
        }
        Intent killIntent = new Intent(getApplicationContext(), GameService.class);
        killIntent.putExtra("kill", true);
        PendingIntent pendingKillIntent = PendingIntent.getService(this, 0, killIntent, Build.VERSION.SDK_INT >=23 ? PendingIntent.FLAG_IMMUTABLE : 0);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(getString(R.string.lazy_service_default_title))
                .setContentText(getString(R.string.notification_game_runs))
                .addAction(android.R.drawable.ic_menu_close_clear_cancel,  getString(R.string.notification_terminate), pendingKillIntent)
                .setSmallIcon(R.drawable.notif_icon)
                .setNotificationSilent();
       startForeground(2, notificationBuilder.build());
        return START_NOT_STICKY; // non-sticky so android wont try restarting the game after the user uses the "Quit" button
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //At this point in time  only the game runs and the user poofed the window, time to die
        stopSelf();
        Process.killProcess(Process.myPid());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
