package net.kdt.pojavlaunch.profiles;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.BaseLauncherActivity;
import net.kdt.pojavlaunch.PojavLauncherActivity;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.extra.ExtraListener;
import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.multirt.RTSpinnerAdapter;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProfileEditor implements ExtraListener<ArrayList<String>> {
    View mainView;
    TextView profileNameView;
    ImageView profileIconView;
    Spinner versionSpinner;
    Spinner javaRuntimeSpinner;
    Spinner rendererSpinner;
    List<MultiRTUtils.Runtime> runtimes;
    List<String> renderNames;
    AlertDialog dialog;
    Context context;
    String selectedVersionId;
    String editingProfile;
    EditSaveCallback cb;
    public static MinecraftProfile generateTemplate() {
        MinecraftProfile TEMPLATE = new MinecraftProfile();
        TEMPLATE.name = "New";
        TEMPLATE.lastVersionId = "latest-release";
        return TEMPLATE;
    }
    public ProfileEditor(Context _ctx, EditSaveCallback cb) {
        context = _ctx;
        this.cb = cb;
        LayoutInflater infl = LayoutInflater.from(_ctx);
        mainView = infl.inflate(R.layout.version_profile_editor,null);
        AlertDialog.Builder bldr = new AlertDialog.Builder(_ctx);
        bldr.setView(mainView);
        profileNameView = mainView.findViewById(R.id.vprof_editior_profile_name);
        versionSpinner = mainView.findViewById(R.id.vprof_editor_version_spinner);
        javaRuntimeSpinner = mainView.findViewById(R.id.vprof_editor_spinner_runtime);
        rendererSpinner = mainView.findViewById(R.id.vprof_editor_profile_renderer);
        {
            List<String> renderList = new ArrayList<>();
            Collections.addAll(renderList, context.getResources().getStringArray(R.array.renderer));
            renderList.add("Default");
            renderNames = Arrays.asList(context.getResources().getStringArray(R.array.renderer_values));
            rendererSpinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,renderList));
        }
        profileIconView = mainView.findViewById(R.id.vprof_editor_icon);
        bldr.setPositiveButton(R.string.global_save,this::save);
        bldr.setNegativeButton(android.R.string.cancel,(dialog,which)->destroy(dialog));
        bldr.setNeutralButton(R.string.global_delete,(dialogInterface, i) -> {
            LauncherProfiles.mainProfileJson.profiles.remove(editingProfile);
            this.cb.onSave(editingProfile,false, true);
        });
        bldr.setOnDismissListener(this::destroy);
        dialog = bldr.create();
    }
    public boolean show(@NonNull String profile) {
        MinecraftProfile prof;
        if(!ProfileAdapter.CREATE_PROFILE_MAGIC.equals(profile)) {
            prof = LauncherProfiles.mainProfileJson.profiles.get(profile);
            if (prof == null) return true;
            editingProfile = profile;
        }else{
            prof = generateTemplate();
            String uuid = UUID.randomUUID().toString();
            while(LauncherProfiles.mainProfileJson.profiles.containsKey(uuid)) {
                uuid = UUID.randomUUID().toString();
            }
            editingProfile = uuid;
        }
        runtimes = MultiRTUtils.getRuntimes();
        javaRuntimeSpinner.setAdapter(new RTSpinnerAdapter(context, runtimes));
        int jvm_index = runtimes.indexOf(new MultiRTUtils.Runtime("<Default>"));
        int rnd_index = rendererSpinner.getAdapter().getCount()-1;
        if (prof.javaDir != null) {
            String selectedRuntime = prof.javaDir.substring(Tools.LAUNCHERPROFILES_RTPREFIX.length());
            int nindex = runtimes.indexOf(new MultiRTUtils.Runtime(selectedRuntime));
            if (nindex != -1) jvm_index = nindex;
        }
        if(prof.__P_renderer_name != null) {
            int nindex = renderNames.indexOf(prof.__P_renderer_name);
            if(nindex != -1) rnd_index = nindex;
        }
        javaRuntimeSpinner.setSelection(jvm_index);
        rendererSpinner.setSelection(rnd_index);
        ExtraCore.addExtraListener("lac_version_list",this);
        profileNameView.setText(prof.name);
        if(ProfileAdapter.iconCache.containsKey(profile)) {
            Log.i("ProfileEditor","Icon resolved!");
            profileIconView.setImageBitmap(ProfileAdapter.iconCache.get(profile));
        }else {
            Log.i("ProfileEditor","No resolved icon.");
            Log.i("ProfileEditor", ProfileAdapter.iconCache.keySet().toString());
            profileIconView.setImageBitmap(ProfileAdapter.iconCache.get(null));
        }
        if(prof.lastVersionId != null && !"latest-release".equals(prof.lastVersionId) && !"latest-snapshot".equals(prof.lastVersionId))
            selectedVersionId = prof.lastVersionId;
        else if(prof.lastVersionId != null) {
            Map<String,String> releaseTable = (Map<String,String>)ExtraCore.getValue("release_table");
            if(releaseTable != null) {
            switch (prof.lastVersionId) {
                case "latest-release":
                    selectedVersionId = releaseTable.get("release");
                case "latest-snapshot":
                    selectedVersionId = releaseTable.get("snapshot");
            }
        }else{
                selectedVersionId = null;
            }
        }
        else{
            if(PojavLauncherActivity.basicVersionList.length > 0) {
                selectedVersionId = PojavLauncherActivity.basicVersionList[0];
            }
        }
        ArrayList<String> versions = (ArrayList<String>) ExtraCore.getValue("lac_version_list");
        BaseLauncherActivity.updateVersionSpinner(context,versions,versionSpinner, selectedVersionId);
        dialog.show();
        return true;
    }
    public void save(DialogInterface dialog, int which) {

        System.out.println(editingProfile);
        MinecraftProfile prof;
        boolean isNew;
        if(LauncherProfiles.mainProfileJson.profiles.containsKey(editingProfile)) {
            prof = LauncherProfiles.mainProfileJson.profiles.get(editingProfile);
            LauncherProfiles.mainProfileJson.profiles.remove(editingProfile);
            isNew = false;
        }else{
            prof = new MinecraftProfile();
            isNew = true;
        }
        prof.name = profileNameView.getText().toString();
        prof.lastVersionId = (String)versionSpinner.getSelectedItem();
        MultiRTUtils.Runtime selectedRuntime = (MultiRTUtils.Runtime) javaRuntimeSpinner.getSelectedItem();
        if(selectedRuntime.name.equals("<Default>")) {
            prof.javaDir = null;
        }else if(selectedRuntime.versionString == null) {
            prof.javaDir = null;
        }else{
            prof.javaDir = Tools.LAUNCHERPROFILES_RTPREFIX+selectedRuntime.name;
        }
        if(rendererSpinner.getSelectedItemPosition() == renderNames.size()) prof.__P_renderer_name = null;
        else prof.__P_renderer_name = renderNames.get(rendererSpinner.getSelectedItemPosition());
        LauncherProfiles.mainProfileJson.profiles.put(editingProfile,prof);
        cb.onSave(editingProfile,isNew, false);
        destroy(dialog);
    }
    public void destroy(@NonNull DialogInterface dialog) {
        ExtraCore.removeExtraListenerFromValue("lac_version_list",this);
        editingProfile = null;
        selectedVersionId = null;
    }
    @Override
    public boolean onValueSet(String key, @Nullable ArrayList<String> value) {
        if(value != null) ((Activity)context).runOnUiThread(()->{
            BaseLauncherActivity.updateVersionSpinner(context,value,versionSpinner, selectedVersionId);
        });
        return false;
    }
    public interface EditSaveCallback {
        void onSave(String name, boolean isNew, boolean isRemoving);
    }
}
