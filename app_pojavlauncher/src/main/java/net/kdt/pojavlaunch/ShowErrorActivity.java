package net.kdt.pojavlaunch;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import net.kdt.pojavlaunch.contextexecutor.ContextExecutorTask;
import net.kdt.pojavlaunch.utils.NotificationUtils;
import net.kdt.pojavlaunch.utils.NotificationUtils;

import java.io.Serializable;

public class ShowErrorActivity extends Activity {

    private static final String ERROR_ACTIVITY_REMOTE_TASK = "remoteTask";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent == null) {
            finish();
            return;
        }
        RemoteErrorTask remoteErrorTask = (RemoteErrorTask) intent.getSerializableExtra(ERROR_ACTIVITY_REMOTE_TASK);
        if(remoteErrorTask == null) {
            finish();
            return;
        }
        remoteErrorTask.executeWithActivity(this);
    }


    public static class RemoteErrorTask implements ContextExecutorTask, Serializable {
        private final Throwable mThrowable;
        private final String mRolledMsg;

        public RemoteErrorTask(Throwable mThrowable, String mRolledMsg) {
            this.mThrowable = mThrowable;
            this.mRolledMsg = mRolledMsg;
        }

        @Override
        public void executeWithActivity(Activity activity) {
            Tools.showError(activity, mRolledMsg, mThrowable, true);
        }

        @Override
        public void executeWithApplication(Context context) {
            Intent showErrorIntent = new Intent(context, ShowErrorActivity.class);
            showErrorIntent.putExtra(ERROR_ACTIVITY_REMOTE_TASK, this);
            NotificationUtils.sendBasicNotification(context,
                    R.string.notif_error_occured,
                    R.string.notif_error_occured_desc,
                    showErrorIntent,
                    NotificationUtils.PENDINGINTENT_CODE_SHOW_ERROR,
                    NotificationUtils.NOTIFICATION_ID_SHOW_ERROR
            );
        }
    }
}
