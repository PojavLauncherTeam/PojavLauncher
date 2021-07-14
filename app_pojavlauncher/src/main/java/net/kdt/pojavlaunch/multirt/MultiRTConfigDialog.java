package net.kdt.pojavlaunch.multirt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.kdt.pojavlaunch.BaseLauncherActivity;
import net.kdt.pojavlaunch.R;

public class MultiRTConfigDialog {
    public static final int MULTIRT_PICK_RUNTIME = 2048;
    public static final int MULTIRT_PICK_RUNTIME_NORETURN = 2049;
    public AlertDialog dialog;
    public RecyclerView dialogView;
    public void prepare(BaseLauncherActivity ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(R.string.multirt_config_title);
        dialogView = new RecyclerView(ctx);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dialogView.setLayoutManager(linearLayoutManager);
        dialogView.setAdapter(new RTRecyclerViewAdapter(this));
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.multirt_config_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /* Initialte import */
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/x-xz");
                ctx.startActivityForResult(intent,MULTIRT_PICK_RUNTIME);
            }
        });
        builder.setNegativeButton(R.string.mcn_exit_call, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog = builder.create();
    }
    public void refresh() {
        dialogView.getAdapter().notifyDataSetChanged();
    }
}
