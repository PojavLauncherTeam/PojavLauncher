package net.kdt.pojavlaunch.modloaders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import net.kdt.pojavlaunch.JavaGUILauncherActivity;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ForgeDownloaderDialog implements Runnable, ExpandableListView.OnChildClickListener, ForgeDownloadListener {
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private ExpandableListView mExpandableListView;
    private ProgressBar mProgressBar;
    private AlertDialog mAlertDialog;
    private File mDestinationFile;
    private Context mContext;
    public void show(Context context, ViewGroup root) {
        this.mContext = context;
        this.mDestinationFile = new File(context.getCacheDir(), "forge-installer.jar");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_expandable_forge_list, root, false);
        mProgressBar = dialogView.findViewById(R.id.forge_list_progress_bar);
        mExpandableListView = dialogView.findViewById(R.id.forge_expandable_version_list);
        mExpandableListView.setOnChildClickListener(this);
        dialogBuilder.setView(dialogView);
        mAlertDialog = dialogBuilder.show();
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            List<String> forgeVersions = ForgeUtils.downloadForgeVersions();
            mHandler.post(()->{
               if(forgeVersions != null) {
                   mProgressBar.setVisibility(View.GONE);
                   mExpandableListView.setAdapter(new ForgeVersionListAdapter(forgeVersions, LayoutInflater.from(mContext)));
               }else{
                   mAlertDialog.dismiss();
               }
            });
        }catch (IOException e) {
            mHandler.post(()->{
                mAlertDialog.dismiss();
                Tools.showError(mContext, e);
            });
        }
    }



    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
        String forgeVersion = (String)expandableListView.getExpandableListAdapter().getChild(i, i1);
        new Thread(new ForgeDownloadTask(this, forgeVersion, mDestinationFile)).start();
        mAlertDialog.dismiss();
        return true;
    }

    @Override
    public void onDownloadFinished() {
        Intent intent = new Intent(mContext, JavaGUILauncherActivity.class);
        ForgeUtils.addAutoInstallArgs(intent, mDestinationFile, true); // since it's a user-invoked install, we want to create a new profile
        mContext.startActivity(intent);
    }

    @Override
    public void onInstallerNotAvailable() {
        mHandler.post(()-> {
            mAlertDialog.dismiss();
            Tools.dialog(mContext,
                mContext.getString(R.string.global_error),
                mContext.getString(R.string.forge_dl_no_installer));
        });
    }

    @Override
    public void onDownloadError(Exception e) {
        mHandler.post(mAlertDialog::dismiss);
        Tools.showError(mContext, e);
    }
}
