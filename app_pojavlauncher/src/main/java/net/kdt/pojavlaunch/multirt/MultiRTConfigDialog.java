package net.kdt.pojavlaunch.multirt;

import android.app.Activity;
import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kdt.pickafile.FileListView;
import com.kdt.pickafile.FileSelectedListener;

import net.kdt.pojavlaunch.BaseLauncherActivity;
import net.kdt.pojavlaunch.BuildConfig;
import net.kdt.pojavlaunch.R;

import java.io.File;
import java.lang.reflect.Method;

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
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xz");
            if (mimeType == null) mimeType = "*/*";
            intent.setType(mimeType);
            activity.startActivityForResult(intent, code);
        }else{
            activity.runOnUiThread(()->{
                AlertDialog.Builder bldr = new AlertDialog.Builder(activity);
                bldr.setCancelable(false);
                final AlertDialog dialog = bldr.create();
                FileListView view = new FileListView(dialog,"tar.xz");

                view.setFileSelectedListener(new FileSelectedListener() {

                    @Override
                    public void onFileSelected(File file, String path) {
                        dialog.dismiss();
                        try {
                            Class<Activity> clazz = Activity.class;
                            Method m = clazz.getDeclaredMethod("onActivityResult", int.class, int.class, Intent.class);
                            m.setAccessible(true);
                            Intent intent = new Intent();
                            intent.setData(
                                    Uri.fromFile(file)
                            );
                            m.invoke(activity,
                                    code,
                                    Activity.RESULT_OK,
                                    intent
                            );
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                view.listFileAt(Environment.getExternalStorageDirectory().getAbsolutePath());
                dialog.setView(view);
                dialog.show();
            });
        }
    }
}
