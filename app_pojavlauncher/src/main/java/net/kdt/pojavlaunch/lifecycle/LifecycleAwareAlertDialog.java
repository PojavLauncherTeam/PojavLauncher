package net.kdt.pojavlaunch.lifecycle;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import net.kdt.pojavlaunch.Tools;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class that implements a form of lifecycle awareness for AlertDialog
 */
public abstract class LifecycleAwareAlertDialog implements LifecycleEventObserver {
    private Lifecycle mLifecycle;
    private AlertDialog mDialog;
    private boolean mLifecycleEnded = false;

    /**
     * Show the lifecycle-aware dialog.
     * Note that the DialogCreator may not be always invoked.
     * @param lifecycle the lifecycle to follow
     * @param context the context for the dialog
     * @param dialogCreator an interface used to create the dialog.
     *                      Note that any dismiss listeners added to the dialog must be wrapped
     *                      with wrapDismissListener().
     */
    public void show(Lifecycle lifecycle, Context context, DialogCreator dialogCreator) {
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
        dialogCreator.createDialog(this, builder);
        mLifecycle.addObserver(this);
        mDialog = builder.show();
    }

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
    public DialogInterface.OnDismissListener wrapDismissListener(DialogInterface.OnCancelListener listener) {
        return dialog -> {
            dispatchDialogHidden();
            if(listener != null) listener.onCancel(dialog);
        };
    }
    
    public interface DialogCreator {
        /**
         * This methods is called when the LifecycleAwareAlertDialog needs to set up its dialog.
         * @param alertDialog an instance of LifecycleAwareAlertDialog for wrapping listeners
         * @param dialogBuilder the AlertDialog builder
         */
        void createDialog(LifecycleAwareAlertDialog alertDialog, AlertDialog.Builder dialogBuilder);
    }

    /**
     * Show a dialog and halt the current thread until the dialog gets closed either due to user action or a lifecycle event.
     * @param lifecycle the Lifecycle object that this dialog will track to automatically close upon destruction
     * @param context the context used to show the dialog
     * @param dialogCreator a DialogCreator that creates the dialog
     * @return true if the dialog was automatically dismissed due to a lifecycle event. This may happen
     *              before the dialog creator is used, so make sure to to handle the return value of the function.
     *         false otherwise
     * @throws InterruptedException if the thread was interrupted while waiting for the dialog
     */

    public static boolean haltOnDialog(Lifecycle lifecycle, Context context, DialogCreator dialogCreator) throws InterruptedException {
        Object waitLock = new Object();
        AtomicBoolean hasLifecycleEnded = new AtomicBoolean(false);
        // This runnable is moved here in order to reduce bracket/lambda hell
        Runnable showDialogRunnable = () -> {
            LifecycleAwareAlertDialog lifecycleAwareDialog = new LifecycleAwareAlertDialog() {
                @Override
                protected void dialogHidden(boolean lifecycleEnded) {
                    hasLifecycleEnded.set(lifecycleEnded);
                    synchronized(waitLock){waitLock.notifyAll();}
                }
            };
            lifecycleAwareDialog.show(lifecycle, context, dialogCreator);
        };
        synchronized (waitLock) {
            Tools.runOnUiThread(showDialogRunnable);
            // the wait() method makes the thread wait on the end of the synchronized block.
            // so we put it here to make sure that the thread won't get notified before wait()
            // is called
            waitLock.wait();
        }
        return hasLifecycleEnded.get();
    }
}
