package net.kdt.pojavlaunch.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

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

    /** Simple wrappers to start the service */
    public static void startService(Context context){
        startService(context, R.string.lazy_service_default_title, R.string.lazy_service_default_description);
    }

    public static void startService(Context context, int titleID, int descriptionID){
        Intent intent = new Intent(context, LazyService.class);
        intent.putExtra(NOTIF_TITLE, titleID);
        intent.putExtra(NOTIF_DESC, descriptionID);
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
        buildNotificationChannel();
        //TODO custom strings ?
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(getString(intent.getIntExtra(NOTIF_TITLE, R.string.lazy_service_default_title)))
                .setContentText(getString(intent.getIntExtra(NOTIF_DESC ,R.string.lazy_service_default_description)))
                .setSmallIcon(R.mipmap.ic_launcher_round);

        startForeground(1, builder.build());
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
