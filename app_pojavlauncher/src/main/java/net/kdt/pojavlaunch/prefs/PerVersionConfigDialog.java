package net.kdt.pojavlaunch.prefs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.multirt.RTSpinnerAdapter;
import net.kdt.pojavlaunch.multirt.Runtime;
import net.kdt.pojavlaunch.value.PerVersionConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PerVersionConfigDialog{
    final Context mContext;
    final AlertDialog mDialog;
    final View mRootView;
    List<Runtime> mRuntimes;
    final Spinner mJvmSpinner;
    final Spinner mRendererSpinner;
    final EditText mCustomDirEditText;
    final EditText mJvmArgsEditText;
    final List<String> mRendererNames;
    String mSelectedGameVersion = null;

    public PerVersionConfigDialog(Context ctx) {
        mContext = ctx;
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_per_version_control,null);
        mJvmSpinner = mRootView.findViewById(R.id.pvc_javaVm);
        mRendererSpinner = mRootView.findViewById(R.id.pvc_renderer);

        ArrayList<String> renderList = new ArrayList<>(5);
        Collections.addAll(renderList, mContext.getResources().getStringArray(R.array.renderer));
        renderList.add("Default");
        mRendererNames = Arrays.asList(mContext.getResources().getStringArray(R.array.renderer_values));
        mRendererSpinner.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, renderList));

        mCustomDirEditText = mRootView.findViewById(R.id.pvc_customDir);
        mJvmArgsEditText = mRootView.findViewById(R.id.pvc_jvmArgs);
        AlertDialog.Builder builder =   new AlertDialog.Builder(mContext);
        builder.setView(mRootView);
        builder.setTitle(R.string.pvc_title);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, this::save);
        mDialog = builder.create();
    }

    public void refreshRuntimes() {
        if(mRuntimes !=null) mRuntimes.clear();
        mRuntimes = MultiRTUtils.getRuntimes();
        //runtimes.add(new Runtime("<Default>"));
    }

    private void save(DialogInterface dialogInterface, int which) {
        if(mSelectedGameVersion == null) {
            dialogInterface.dismiss();
            return;
        }

        PerVersionConfig.VersionConfig versionConfig = PerVersionConfig.configMap.get(mSelectedGameVersion);
        if(versionConfig == null){
            versionConfig = new PerVersionConfig.VersionConfig();
        }
        versionConfig.jvmArgs= mJvmArgsEditText.getText().toString();
        versionConfig.gamePath= mCustomDirEditText.getText().toString();

        if(mRendererSpinner.getSelectedItemPosition() == mRendererNames.size()) versionConfig.renderer = null;
        else versionConfig.renderer = mRendererNames.get(mRendererSpinner.getSelectedItemPosition());

        String runtime=((Runtime) mJvmSpinner.getSelectedItem()).name;;
        if(!runtime.equals("<Default>"))versionConfig.selectedRuntime=runtime;
        else versionConfig.selectedRuntime = null;

        PerVersionConfig.configMap.put(mSelectedGameVersion, versionConfig);
        try{
            PerVersionConfig.update();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public boolean openConfig(String selectedVersion) {
        mSelectedGameVersion = selectedVersion;
        try{
            PerVersionConfig.update();
        }catch(IOException e){
            e.printStackTrace();
        }
        PerVersionConfig.VersionConfig versionConfig = PerVersionConfig.configMap.get(mSelectedGameVersion);
        refreshRuntimes();
        mJvmSpinner.setAdapter(new RTSpinnerAdapter(mContext, mRuntimes));

        int jvmIndex = mRuntimes.indexOf(new Runtime("<Default>"));
        int rendererIndex = mRendererSpinner.getAdapter().getCount()-1;
        if (versionConfig != null) {
            mCustomDirEditText.setText(versionConfig.gamePath);
            mJvmArgsEditText.setText(versionConfig.jvmArgs);
            if (versionConfig.selectedRuntime != null) {
                int nIndex = mRuntimes.indexOf(new Runtime(versionConfig.selectedRuntime));
                if (nIndex != -1) jvmIndex = nIndex;
            }
            if(versionConfig.renderer != null) {
                int nIndex = mRendererNames.indexOf(versionConfig.renderer);
                if (nIndex != -1) rendererIndex = nIndex;
            }
        }
        mJvmSpinner.setSelection(jvmIndex);
        mRendererSpinner.setSelection(rendererIndex);

        mDialog.show();
        return true;
    }
}
