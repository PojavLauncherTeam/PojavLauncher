package net.kdt.pojavlaunch.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import net.kdt.pojavlaunch.R;

import java.lang.ref.WeakReference;

/**
 * Lazy service which allows the process not to get killed.
 * Can be created from context, can be killed statically
 */
public class LazyService extends Service {
    private static final String NOTIF_TITLE = "notif_title";
    private static final String NOTIF_DESC = "notif_desc";

    private static WeakReference<Service> sLazyService = new WeakReference<>(null);

    /** Simple wrapper to start the service */
    public static void startService(Context context){
        Intent intent = new Intent(context, LazyService.class);
        ContextCompat.startForegroundService(context, intent);
    }


    /** Kill the service if it is still running */
    public static void killService(){
        Service service = sLazyService.get();
        if(service != null)
            service.stopSelf();
    }

    public LazyService(){
        super();
        // TODO handle multiple service creation ?
        sLazyService = new WeakReference<>(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getBooleanExtra("kill", false)) {
            Process.killProcess(Process.myPid());
            return super.onStartCommand(intent, flags, startId);
        }
        buildNotificationChannel();
        Intent killIntent = new Intent(getApplicationContext(), LazyService.class);
        killIntent.putExtra("kill", true);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(getString(R.string.lazy_service_default_title))
                .setContentText(getString(R.string.lazy_service_default_description))
                .addAction(android.R.drawable.ic_menu_close_clear_cancel,  getString(R.string.notification_terminate), PendingIntent.getService(this, 0, killIntent, Build.VERSION.SDK_INT >=23 ? PendingIntent.FLAG_IMMUTABLE : 0))
                .setSmallIcon(R.mipmap.ic_launcher_round);
        startForeground(1,builder.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void buildNotificationChannel(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationChannel channel = new NotificationChannel(
                getResources().getString(R.string.notif_channel_id),
                getResources().getString(R.string.notif_channel_name), NotificationManager.IMPORTANCE_HIGH);
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());

        manager.createNotificationChannel(channel);

    }
}
