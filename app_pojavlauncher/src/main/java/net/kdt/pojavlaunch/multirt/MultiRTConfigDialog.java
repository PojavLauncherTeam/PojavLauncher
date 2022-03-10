package net.kdt.pojavlaunch.multirt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.webkit.MimeTypeMap;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.kdt.pojavlaunch.BaseLauncherActivity;
import net.kdt.pojavlaunch.R;

public class MultiRTConfigDialog {
    public static final int MULTIRT_PICK_RUNTIME = 2048;
    public static final int MULTIRT_PICK_RUNTIME_STARTUP = 2049;
    public AlertDialog mDialog;
    public RecyclerView mDialogView;

    public void prepare(BaseLauncherActivity activity) {
        mDialogView = new RecyclerView(activity);
        mDialogView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        mDialogView.setAdapter(new RTRecyclerViewAdapter(this));

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.multirt_config_title);
        builder.setView(mDialogView);
        builder.setPositiveButton(R.string.multirt_config_add, (dialog, which) -> openRuntimeSelector(activity,MULTIRT_PICK_RUNTIME));
        builder.setNegativeButton(R.string.mcn_exit_call, (dialog, which) -> dialog.cancel());
        mDialog = builder.create();
    }

    public void refresh() {
        RecyclerView.Adapter adapter = mDialogView.getAdapter();
        if(adapter != null) mDialogView.getAdapter().notifyDataSetChanged();
    }

    public static void openRuntimeSelector(Activity activity, int code) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xz");
        if(mimeType == null) mimeType = "*/*";
        intent.setType(mimeType);
        activity.startActivityForResult(intent,code);
    }
}
