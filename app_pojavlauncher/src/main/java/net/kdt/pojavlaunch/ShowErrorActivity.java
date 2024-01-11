package net.kdt.pojavlaunch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.lifecycle.ContextExecutorTask;
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
            if(mThrowable instanceof ContextExecutorTask) {
                ((ContextExecutorTask)mThrowable).executeWithActivity(activity);
            }else {
                Tools.showError(activity, mRolledMsg, mThrowable, activity instanceof ShowErrorActivity);
            }
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

    /**
     * Install remote dialog handling onto a dialog. This should be used when the dialog is planned to be presented
     * through Tools.showError or Tools.showErrorRemote as a Throwable implementing a ContextExecutorTask.
     * @param callerActivity the activity provided by the ContextExecutorTask.executeWithActivity
     * @param builder the alert dialog builder.
     */
    public static void installRemoteDialogHandling(Activity callerActivity, @NonNull AlertDialog.Builder builder) {
        if (callerActivity instanceof ShowErrorActivity) {
            builder.setOnDismissListener(d -> callerActivity.finish());
        }
    }
}
