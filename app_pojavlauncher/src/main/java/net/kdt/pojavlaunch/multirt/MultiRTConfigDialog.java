package net.kdt.pojavlaunch.multirt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import net.kdt.pojavlaunch.R;

public class MultiRTConfigDialog {
    public static final int MULTIRT_PICK_RUNTIME = 2048;
    private AlertDialog mDialog;
    private RecyclerView mDialogView;

    /** Show the dialog, refreshes the adapter data before showing it */
    public void show(){
        refresh();
        mDialog.show();
    }

    @SuppressLint("NotifyDataSetChanged") //only used to completely refresh the list, it is necessary
    public void refresh() {
        RecyclerView.Adapter<?> adapter = mDialogView.getAdapter();
        if(adapter != null) adapter.notifyDataSetChanged();
    }

    /** Build the dialog behavior and style */
    public void prepare(Activity activity) {
        mDialogView = new RecyclerView(activity);
        mDialogView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        RTRecyclerViewAdapter adapter = new RTRecyclerViewAdapter();
        mDialogView.setAdapter(adapter);

        mDialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.multirt_config_title)
                .setView(mDialogView)
                .setPositiveButton(R.string.multirt_config_add, (dialog, which) -> openRuntimeSelector(activity,MULTIRT_PICK_RUNTIME))
                .setNeutralButton(R.string.multirt_delete_runtime, null)
                .create();

        // Custom button behavior without dismiss
        mDialog.setOnShowListener(dialog -> {
            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
            button.setOnClickListener(view -> {
                boolean isEditing = !adapter.getIsEditing();
                adapter.setIsEditing(isEditing);
                button.setText(isEditing ? R.string.multirt_config_setdefault : R.string.multirt_delete_runtime);
            });
        });
    }

    public static void openRuntimeSelector(Activity activity, int code) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xz");
        if (mimeType == null) mimeType = "*/*";
        intent.setType(mimeType);
        activity.startActivityForResult(intent, code);
    }
}
