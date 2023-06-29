package net.kdt.pojavlaunch.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.kdt.pojavlaunch.JavaGUILauncherActivity;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.modloaders.ForgeDownloadTask;
import net.kdt.pojavlaunch.modloaders.ForgeUtils;
import net.kdt.pojavlaunch.modloaders.ForgeVersionListAdapter;
import net.kdt.pojavlaunch.modloaders.ModloaderDownloadListener;
import net.kdt.pojavlaunch.modloaders.ModloaderListenerProxy;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ForgeInstallFragment extends Fragment implements Runnable, View.OnClickListener, ExpandableListView.OnChildClickListener, ModloaderDownloadListener {
    public static final String TAG = "ForgeInstallFragment";
    private static ModloaderListenerProxy sTaskProxy;
    private ExpandableListView mExpandableListView;
    private ProgressBar mProgressBar;
    private File mDestinationFile;
    private LayoutInflater mInflater;
    private View mRetryView;

    public ForgeInstallFragment() {
        super(R.layout.fragment_forge_installer);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mInflater = LayoutInflater.from(context);
        this.mDestinationFile = new File(Tools.DIR_CACHE, "forge-installer.jar");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = view.findViewById(R.id.forge_list_progress_bar);
        mExpandableListView = view.findViewById(R.id.forge_expandable_version_list);
        mExpandableListView.setOnChildClickListener(this);
        mRetryView = view.findViewById(R.id.forge_installer_retry_layout);
        view.findViewById(R.id.forge_installer_retry_button).setOnClickListener(this);
        if(sTaskProxy != null) {
            mExpandableListView.setEnabled(false);
            sTaskProxy.attachListener(this);
        }
        new Thread(this).start();
    }

    @Override
    public void onDestroyView() {
        if(sTaskProxy != null) sTaskProxy.detachListener();
        super.onDestroyView();
    }

    @Override
    public void run() {
        try {
            List<String> forgeVersions = ForgeUtils.downloadForgeVersions();
            Tools.runOnUiThread(()->{
                if(forgeVersions != null) {
                    mExpandableListView.setAdapter(new ForgeVersionListAdapter(forgeVersions, mInflater));
                }else{
                    mRetryView.setVisibility(View.VISIBLE);
                }
                mProgressBar.setVisibility(View.GONE);
            });
        }catch (IOException e) {
            Tools.runOnUiThread(()-> {
                if (getContext() != null) {
                    Tools.showError(getContext(), e);
                    mRetryView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        mRetryView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        new Thread(this).start();
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
        String forgeVersion = (String)expandableListView.getExpandableListAdapter().getChild(i, i1);
        sTaskProxy = new ModloaderListenerProxy();
        ForgeDownloadTask downloadTask = new ForgeDownloadTask(sTaskProxy, forgeVersion, mDestinationFile);
        sTaskProxy.attachListener(this);
        mExpandableListView.setEnabled(false);
        new Thread(downloadTask).start();
        return true;
    }

    @Override
    public void onDownloadFinished(File downloadedFile) {
        Tools.runOnUiThread(()->{
            Context context = requireContext();
            sTaskProxy.detachListener();
            sTaskProxy = null;
            mExpandableListView.setEnabled(true);
            Intent modInstallerStartIntent = new Intent(context, JavaGUILauncherActivity.class);
            ForgeUtils.addAutoInstallArgs(modInstallerStartIntent, downloadedFile, true);
            context.startActivity(modInstallerStartIntent);
        });
    }

    @Override
    public void onDataNotAvailable() {
        Tools.runOnUiThread(()->{
            Context context = requireContext();
            sTaskProxy.detachListener();
            sTaskProxy = null;
            mExpandableListView.setEnabled(true);
            Tools.dialog(context,
                    context.getString(R.string.global_error),
                    context.getString(R.string.forge_dl_no_installer));
        });
    }

    @Override
    public void onDownloadError(Exception e) {
        Tools.runOnUiThread(()->{
            Context context = requireContext();
            sTaskProxy.detachListener();
            sTaskProxy = null;
            mExpandableListView.setEnabled(true);
            Tools.showError(context, e);
        });
    }
}
