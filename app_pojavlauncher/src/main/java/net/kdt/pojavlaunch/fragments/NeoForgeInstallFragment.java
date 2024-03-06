package net.kdt.pojavlaunch.fragments;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.ExpandableListAdapter;
import androidx.annotation.NonNull;
import net.kdt.pojavlaunch.JavaGUILauncherActivity;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.modloaders.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NeoForgeInstallFragment extends ModVersionListFragment<List<String>> {
    public static final String TAG = "NeoForgeInstallFragment";
    private static ModloaderListenerProxy sTaskProxy;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public int getTitleText() {
        return R.string.neoforge_dl_select_version;
    }

    @Override
    public int getNoDataMsg() {
        return R.string.neoforge_dl_no_installer;
    }

    @Override
    public ModloaderListenerProxy getTaskProxy() {
        return sTaskProxy;
    }
    @Override
    public void setTaskProxy(ModloaderListenerProxy proxy) {
        sTaskProxy = proxy;
    }

    @Override
    public List<String> loadVersionList() throws IOException {
        List<String> versions = new ArrayList<>();
        versions.addAll(NeoForgeUtils.downloadNeoForgedForgeVersions());
        versions.addAll(NeoForgeUtils.downloadNeoForgeVersions());
        return versions;
    }

    @Override
    public ExpandableListAdapter createAdapter(List<String> versionList, LayoutInflater layoutInflater) {
        return new NeoForgeVersionListAdapter(versionList, layoutInflater);
    }

    @Override
    public Runnable createDownloadTask(Object selectedVersion, ModloaderListenerProxy listenerProxy) {
        return new NeoForgeDownloadTask(listenerProxy, (String) selectedVersion);
    }

    @Override
    public void onDownloadFinished(Context context, File downloadedFile) {
        Intent modInstallerStartIntent = new Intent(context, JavaGUILauncherActivity.class);
        NeoForgeUtils.addAutoInstallArgs(modInstallerStartIntent, downloadedFile);
        context.startActivity(modInstallerStartIntent);
    }
}
