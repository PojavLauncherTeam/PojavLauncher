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
import net.kdt.pojavlaunch.tasks.RefreshVersionListTask;
import net.kdt.pojavlaunch.value.PerVersionConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PerVersionConfigDialog{
    final Context ctx;
    final AlertDialog dialog;
    final View v;
    List<MultiRTUtils.Runtime> runtimes;
    final Spinner javaVMSpinner;
    final Spinner rendererSpinner;
    final EditText customDirText;
    final EditText jvmArgsEditText;
    final List<String> renderNames;
    String selectedGameVersion = null;
    public PerVersionConfigDialog(Context _ctx) {
        ctx = _ctx;
        v = LayoutInflater.from(ctx).inflate(R.layout.pvc_popup,null);
        javaVMSpinner = v.findViewById(R.id.pvc_javaVm);
        rendererSpinner = v.findViewById(R.id.pvc_renderer);
        {
            List<String> renderList = new ArrayList<>();
            Collections.addAll(renderList, ctx.getResources().getStringArray(R.array.renderer));
            renderList.add("Default");
            renderNames = Arrays.asList(ctx.getResources().getStringArray(R.array.renderer_values));
            rendererSpinner.setAdapter(new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item,renderList));
        }
        customDirText = v.findViewById(R.id.pvc_customDir);
        jvmArgsEditText = v.findViewById(R.id.pvc_jvmArgs);
        AlertDialog.Builder builder =   new AlertDialog.Builder(ctx);
        builder.setView(v);
        builder.setTitle("Per-version settings");
        builder.setNegativeButton(android.R.string.cancel,(dialogInterface,i)->dialogInterface.dismiss());
        builder.setPositiveButton(android.R.string.ok,this::save);
        dialog = builder.create();
    }
    public void refreshRuntimes() {
        if(runtimes!=null)runtimes.clear();
        runtimes = MultiRTUtils.getRuntimes();
        //runtimes.add(new MultiRTUtils.Runtime("<Default>"));
    }
    private void save(DialogInterface i, int which) {
        if(selectedGameVersion == null) {
            i.dismiss();
            return;
        }
        PerVersionConfig.VersionConfig conf1 = PerVersionConfig.configMap.get(selectedGameVersion);
        if(conf1==null){
            conf1=new PerVersionConfig.VersionConfig();
        }
        conf1.jvmArgs=jvmArgsEditText.getText().toString();
        conf1.gamePath=customDirText.getText().toString();

        if(rendererSpinner.getSelectedItemPosition() == renderNames.size()) conf1.renderer = null;
        else conf1.renderer = renderNames.get(rendererSpinner.getSelectedItemPosition());

        String runtime=((MultiRTUtils.Runtime)javaVMSpinner.getSelectedItem()).name;;
        if(!runtime.equals("<Default>"))conf1.selectedRuntime=runtime;
        else conf1.selectedRuntime=null;

        PerVersionConfig.configMap.put(selectedGameVersion,conf1);
        try{
            PerVersionConfig.update();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public boolean openConfig(String selectedVersion) {
        selectedGameVersion = selectedVersion;
        try{
            PerVersionConfig.update();
        }catch(IOException e){
            e.printStackTrace();
        }
        PerVersionConfig.VersionConfig conf=PerVersionConfig.configMap.get(selectedGameVersion);
        refreshRuntimes();
        javaVMSpinner.setAdapter(new RTSpinnerAdapter(ctx,runtimes));
        {
            int jvm_index = runtimes.indexOf(new MultiRTUtils.Runtime("<Default>"));
            int rnd_index = rendererSpinner.getAdapter().getCount()-1;
            if (conf != null) {
                customDirText.setText(conf.gamePath);
                jvmArgsEditText.setText(conf.jvmArgs);
                if (conf.selectedRuntime != null) {
                    int nindex = runtimes.indexOf(new MultiRTUtils.Runtime(conf.selectedRuntime));
                    if (nindex != -1) jvm_index = nindex;
                }
                if(conf.renderer != null) {
                    int nindex = renderNames.indexOf(conf.renderer);
                    if (nindex != -1) rnd_index = nindex;
                }
            }
            javaVMSpinner.setSelection(jvm_index);
            rendererSpinner.setSelection(rnd_index);
        }
        dialog.show();
        return true;
        }
}
