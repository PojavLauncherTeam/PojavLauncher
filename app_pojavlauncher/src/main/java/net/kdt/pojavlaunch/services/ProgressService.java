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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lazy service which allows the process not to get killed.
 * Can be created from context, can be killed statically
 */
public class ProgressService extends Service {

    private static WeakReference<Service> sProgressService = new WeakReference<>(null);
    private static final AtomicInteger sReferenceCount = new AtomicInteger(0);

    /** Simple wrapper to start the service */
    public static void startService(Context context){
        Intent intent = new Intent(context, ProgressService.class);
        if(sReferenceCount.get() < 0) sReferenceCount.set(0);
        sReferenceCount.getAndIncrement();
        ContextCompat.startForegroundService(context, intent);
    }

    /** Kill the service if it is still running */
    public static void killService(){
        Service service = sProgressService.get();
        int refcnt = sReferenceCount.decrementAndGet();
        if(service != null && refcnt <= 0) {
            service.stopSelf();
        }
    }

    private NotificationCompat.Builder mNotificationBuilder;
    public ProgressService(){
        super();
        sProgressService = new WeakReference<>(this);
    }

    @Override
    public void onCreate() {
        Tools.buildNotificationChannel(getApplicationContext());
        Intent killIntent = new Intent(getApplicationContext(), ProgressService.class);
        killIntent.putExtra("kill", true);
        PendingIntent pendingKillIntent = PendingIntent.getService(this, 0, killIntent, Build.VERSION.SDK_INT >=23 ? PendingIntent.FLAG_IMMUTABLE : 0);
        mNotificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(getString(R.string.lazy_service_default_title))
                .addAction(android.R.drawable.ic_menu_close_clear_cancel,  getString(R.string.notification_terminate), pendingKillIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && intent.getBooleanExtra("kill", false)) {
            stopSelf(); // otherwise Android tries to restart the service since it "crashed"
            Process.killProcess(Process.myPid());
            return super.onStartCommand(intent, flags, startId);
        }
        mNotificationBuilder.setContentText(getString(R.string.progresslayout_tasks_in_progress, sReferenceCount.get()));
        startForeground(1,mNotificationBuilder.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
