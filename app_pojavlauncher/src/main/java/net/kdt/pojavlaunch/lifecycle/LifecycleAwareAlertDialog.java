package net.kdt.pojavlaunch.lifecycle;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * A class that implements a form of lifecycle awareness for AlertDialog
 */
public abstract class LifecycleAwareAlertDialog implements LifecycleEventObserver {
    private Lifecycle mLifecycle;
    private AlertDialog mDialog;
    private boolean mLifecycleEnded = false;

    /**
     * Show the lifecycle-aware dialog.
     * @param lifecycle the lifecycle to follow
     * @param context the context for the dialog
     */
    public void show(Lifecycle lifecycle, Context context) {
        this.mLifecycleEnded = false;
        this.mLifecycle = lifecycle;
        if(mLifecycle.getCurrentState().equals(Lifecycle.State.DESTROYED)) {
            this.mLifecycleEnded = true;
            dialogHidden(mLifecycleEnded);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Install the default cancel/dismiss handling
        builder.setOnDismissListener(wrapDismissListener(null));
        createDialog(builder);
        mLifecycle.addObserver(this);
        mDialog = builder.show();
    }

    /**
     * Invoked by the show() method to create the dialog. Note that any dismiss listeners
     * added to the dialog must be wrapped with wrapDismissLisener.
     * @param dialogBuilder the dialog builder used for building the dialog
     */
    abstract protected void createDialog(AlertDialog.Builder dialogBuilder);

    /**
     * Invoked when the dialog gets hidden either by cancel()/dismiss(), or if a lifecycle event
     * happens.
     * @param lifecycleEnded if the dialog was hidden due to a lifecycle event
     */
    abstract protected void dialogHidden(boolean lifecycleEnded);

    protected void dispatchDialogHidden() {
        new Exception().printStackTrace();
        dialogHidden(mLifecycleEnded);
        mLifecycle.removeObserver(this);
    }

    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if(event.equals(Lifecycle.Event.ON_DESTROY)) {
            mDialog.dismiss();
            mLifecycleEnded = true;
        }
    }

    /**
     * Wrap an OnDismissListener for use with this LifecycleAwareAlertDialog. Pass null to only invoke the
     * default dialog hidden handling.
     * @param listener your listener
     * @return the wrapped listener
     */
    protected DialogInterface.OnDismissListener wrapDismissListener(DialogInterface.OnCancelListener listener) {
        return dialog -> {
            dispatchDialogHidden();
            if(listener != null) listener.onCancel(dialog);
        };
    }
}
