package net.kdt.pojavlaunch.profiles;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.BaseLauncherActivity;
import net.kdt.pojavlaunch.PojavLauncherActivity;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.extra.ExtraListener;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class ProfileEditor implements ExtraListener<ArrayList<String>> {
    View mainView;
    TextView profileNameView;
    ImageView profileIconView;
    Spinner versionSpinner;
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
        profileIconView = mainView.findViewById(R.id.vprof_editor_icon);
        bldr.setPositiveButton(R.string.global_save,this::save);
        bldr.setNegativeButton(android.R.string.cancel,(dialog,which)->destroy(dialog));
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
        else if(prof.lastVersionId != null) switch (prof.lastVersionId) {
            case "latest-release":
                selectedVersionId = ((Map<String,String>)ExtraCore.getValue("release_table")).get("release");
            case "latest-snapshot":
                selectedVersionId = ((Map<String,String>)ExtraCore.getValue("release_table")).get("snapshot");
        }else{
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
        LauncherProfiles.mainProfileJson.profiles.put(editingProfile,prof);
        cb.onSave(editingProfile,isNew);
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
        void onSave(String name, boolean isNew);
    }
}
