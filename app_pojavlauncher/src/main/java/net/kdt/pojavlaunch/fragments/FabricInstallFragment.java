package net.kdt.pojavlaunch.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import net.kdt.pojavlaunch.JavaGUILauncherActivity;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.modloaders.FabricDownloadTask;
import net.kdt.pojavlaunch.modloaders.FabricUtils;
import net.kdt.pojavlaunch.modloaders.ModloaderDownloadListener;
import net.kdt.pojavlaunch.modloaders.ModloaderListenerProxy;
import net.kdt.pojavlaunch.profiles.VersionSelectorDialog;
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FabricInstallFragment extends Fragment implements AdapterView.OnItemSelectedListener, ModloaderDownloadListener, Runnable {
    public static final String TAG = "FabricInstallTarget";
    private static ModloaderListenerProxy sTaskProxy;
    private TextView mSelectedVersionLabel;
    private String mSelectedLoaderVersion;
    private Spinner mLoaderVersionSpinner;
    private String mSelectedGameVersion;
    private boolean mSelectedSnapshot;
    private ProgressBar mProgressBar;
    private File mDestinationDir;
    private Button mStartButton;
    private View mRetryView;
    public FabricInstallFragment() {
        super(R.layout.fragment_fabric_install);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mDestinationDir = new File(Tools.DIR_CACHE, "fabric-installer");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStartButton = view.findViewById(R.id.fabric_installer_start_button);
        mStartButton.setOnClickListener(this::onClickStart);
        mSelectedVersionLabel = view.findViewById(R.id.fabric_installer_version_select_label);
        view.findViewById(R.id.fabric_installer_game_version_change).setOnClickListener(this::onClickSelect);
        mLoaderVersionSpinner = view.findViewById(R.id.fabric_installer_loader_ver_spinner);
        mLoaderVersionSpinner.setOnItemSelectedListener(this);
        mProgressBar = view.findViewById(R.id.fabric_installer_progress_bar);
        mRetryView = view.findViewById(R.id.fabric_installer_retry_layout);
        view.findViewById(R.id.fabric_installer_retry_button).setOnClickListener(this::onClickRetry);
        if(sTaskProxy != null) {
            mStartButton.setEnabled(false);
            sTaskProxy.attachListener(this);
        }
        new Thread(this).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(sTaskProxy != null) {
            sTaskProxy.detachListener();
        }
    }

    private void onClickStart(View v) {
        if(ProgressKeeper.hasOngoingTasks()) {
            Toast.makeText(v.getContext(), R.string.tasks_ongoing, Toast.LENGTH_LONG).show();
            return;
        }
        sTaskProxy = new ModloaderListenerProxy();
        FabricDownloadTask fabricDownloadTask = new FabricDownloadTask(sTaskProxy, mDestinationDir);
        sTaskProxy.attachListener(this);
        mStartButton.setEnabled(false);
        new Thread(fabricDownloadTask).start();
    }

    private void onClickSelect(View v) {
        VersionSelectorDialog.open(v.getContext(), true, (id, snapshot)->{
            mSelectedGameVersion = id;
            mSelectedVersionLabel.setText(mSelectedGameVersion);
            mSelectedSnapshot = snapshot;
            if(mSelectedLoaderVersion != null && sTaskProxy == null) mStartButton.setEnabled(true);
        });
    }

    private void onClickRetry(View v) {
        mLoaderVersionSpinner.setAdapter(null);
        mStartButton.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
        mRetryView.setVisibility(View.GONE);
        new Thread(this).start();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Adapter adapter = adapterView.getAdapter();
        mSelectedLoaderVersion = (String) adapter.getItem(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        mSelectedLoaderVersion = null;
    }

    @Override
    public void onDownloadFinished(File downloadedFile) {
        Tools.runOnUiThread(()->{
            Context context = requireContext();
            sTaskProxy.detachListener();
            sTaskProxy = null;
            mStartButton.setEnabled(true);
            // This works because the due to the fact that we have transitioned here
            // without adding a transaction to the back stack, which caused the previous
            // transaction to be amended (i guess?? thats how the back stack dump looks like)
            // we can get back to the main fragment with just one back stack pop.
            // For some reason that amendment causes the transaction to lose its tag
            // so we cant use the tag here.
            getParentFragmentManager().popBackStackImmediate();
            Intent intent = new Intent(context, JavaGUILauncherActivity.class);
            FabricUtils.addAutoInstallArgs(intent, downloadedFile, mSelectedGameVersion, mSelectedLoaderVersion, mSelectedSnapshot, true);
            context.startActivity(intent);
        });
    }

    @Override
    public void onDataNotAvailable() {
        Tools.runOnUiThread(()->{
            Context context = requireContext();
            sTaskProxy.detachListener();
            sTaskProxy = null;
            mStartButton.setEnabled(true);
            Tools.dialog(context,
                    context.getString(R.string.global_error),
                    context.getString(R.string.fabric_dl_cant_read_meta));
        });
    }

    @Override
    public void onDownloadError(Exception e) {
        Tools.runOnUiThread(()-> {
            Context context = requireContext();
            sTaskProxy.detachListener();
            sTaskProxy = null;
            mStartButton.setEnabled(true);
            Tools.showError(context, e);
        });
    }

    @Override
    public void run() {
        try {
            List<String> mLoaderVersions = FabricUtils.downloadLoaderVersionList(false);
            if (mLoaderVersions != null) {
                Tools.runOnUiThread(()->{
                    Context context = getContext();
                    if(context == null) return;
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, mLoaderVersions);
                    mLoaderVersionSpinner.setAdapter(arrayAdapter);
                    mProgressBar.setVisibility(View.GONE);
                });
            }else{
                Tools.runOnUiThread(()-> {
                    mRetryView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                });
            }
        }catch (IOException e) {
            Tools.runOnUiThread(()-> {
                if(getContext() != null) Tools.showError(getContext(), e);
                mRetryView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            });
        }
    }
}
