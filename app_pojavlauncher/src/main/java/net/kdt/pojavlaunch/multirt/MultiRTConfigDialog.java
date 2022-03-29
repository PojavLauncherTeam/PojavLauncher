package net.kdt.pojavlaunch.multirt;

import androidx.appcompat.app.AlertDialog;

import android.app.ProgressDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.kdt.pojavlaunch.BaseLauncherActivity;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.selector.UnifiedSelector;
import net.kdt.pojavlaunch.selector.UnifiedSelectorCallback;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class MultiRTConfigDialog {
    public static final int MULTIRT_PICK_RUNTIME = 2048;
    public static final int MULTIRT_PICK_RUNTIME_STARTUP = 2049;
    public AlertDialog mDialog;
    public RecyclerView mDialogView;
    public UnifiedSelector runtimeSelector;
    public void prepare(BaseLauncherActivity activity) {
        mDialogView = new RecyclerView(activity);
        mDialogView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        mDialogView.setAdapter(new RTRecyclerViewAdapter(this));
        activity.mRuntimeSelector.setSelectionCallback(new RuntimeInstallationCallback());
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.multirt_config_title);
        builder.setView(mDialogView);
        builder.setPositiveButton(R.string.multirt_config_add, (dialog, which) -> activity.mRuntimeSelector.openSelector("xz"));
        builder.setNegativeButton(R.string.mcn_exit_call, (dialog, which) -> dialog.cancel());
        mDialog = builder.create();
    }

    public void refresh() {
        RecyclerView.Adapter adapter = mDialogView.getAdapter();
        if(adapter != null) mDialogView.getAdapter().notifyDataSetChanged();
    }

    class RuntimeInstallationCallback implements UnifiedSelectorCallback {
        @Override
        public void onSelected(InputStream stream, String name) {
            Context context = mDialog.getContext();
            ProgressDialog barrier = new ProgressDialog(context);
            barrier.setMessage(context.getString(R.string.global_waiting));
            barrier.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            barrier.setCancelable(false);
            Handler uiThreadHandler = new Handler(Looper.getMainLooper());
            Thread t = new Thread(() -> {
                try {
                    MultiRTUtils.installRuntimeNamed(stream, name,
                            (resid, stuff) ->uiThreadHandler.post(
                                    () -> barrier.setMessage(context.getString(resid, stuff))));
                    MultiRTUtils.postPrepare(context, name);
                } catch (IOException e) {
                    Tools.showError(context, e);
                }
            uiThreadHandler.post(() -> {
                    barrier.dismiss();
                    refresh();
                    mDialog.show();
                });
            });
            t.start();
        }

        @Override
        public void onError(Throwable th) {
            Tools.showError(mDialog.getContext(),th);
        }
    }
}
