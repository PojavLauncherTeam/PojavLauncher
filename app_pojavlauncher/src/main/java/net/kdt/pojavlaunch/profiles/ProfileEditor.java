package net.kdt.pojavlaunch.profiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
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
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.multirt.RTSpinnerAdapter;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;
import net.kdt.pojavlaunch.multirt.Runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProfileEditor implements ExtraListener<ArrayList<String>> {
    private final View mainView;
    private final TextView profileNameView;
    private final ImageView profileIconView;
    private final Spinner versionSpinner;
    private final Spinner javaRuntimeSpinner;
    private final Spinner rendererSpinner;
    private final List<String> renderNames;
    private final AlertDialog dialog;
    private String selectedVersionId;
    private String editingProfile;
    private final EditSaveCallback editSaveCallback;
    private final Handler uiThreadHandler;
    public static MinecraftProfile generateTemplate() {
        MinecraftProfile TEMPLATE = new MinecraftProfile();
        TEMPLATE.name = "New";
        TEMPLATE.lastVersionId = "latest-release";
        return TEMPLATE;
    }
    public ProfileEditor(Context _ctx, EditSaveCallback editSaveCallback) {
        this.editSaveCallback = editSaveCallback;
        uiThreadHandler = new Handler(Looper.getMainLooper());
        LayoutInflater inflater = LayoutInflater.from(_ctx);
        mainView = inflater.inflate(R.layout.version_profile_editor,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(_ctx);
        builder.setView(mainView);
        profileNameView = mainView.findViewById(R.id.vprof_editior_profile_name);
        versionSpinner = mainView.findViewById(R.id.vprof_editor_version_spinner);
        javaRuntimeSpinner = mainView.findViewById(R.id.vprof_editor_spinner_runtime);
        rendererSpinner = mainView.findViewById(R.id.vprof_editor_profile_renderer);
        {
            Context context = rendererSpinner.getContext();
            List<String> renderList = new ArrayList<>();
            Collections.addAll(renderList, context.getResources().getStringArray(R.array.renderer));
            renderList.add("Default");
            renderNames = Arrays.asList(context.getResources().getStringArray(R.array.renderer_values));
            rendererSpinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,renderList));
        }
        profileIconView = mainView.findViewById(R.id.vprof_editor_icon);
        builder.setPositiveButton(R.string.global_save,this::save);
        builder.setNegativeButton(android.R.string.cancel,(dialog,which)->destroy(dialog));
        builder.setNeutralButton(R.string.global_delete,(dialogInterface, i) -> {
            LauncherProfiles.mainProfileJson.profiles.remove(editingProfile);
            this.editSaveCallback.onSave(editingProfile,false, true);
        });
        builder.setOnDismissListener(this::destroy);
        dialog = builder.create();
    }
    public boolean show(@NonNull String profile) {
        MinecraftProfile minecraftProfile;
        if(!ProfileAdapter.CREATE_PROFILE_MAGIC.equals(profile)) {
            minecraftProfile = LauncherProfiles.mainProfileJson.profiles.get(profile);
            if (minecraftProfile == null) return true;
            editingProfile = profile;
        }else{
            minecraftProfile = generateTemplate();
            String uuid = UUID.randomUUID().toString();
            while(LauncherProfiles.mainProfileJson.profiles.containsKey(uuid)) {
                uuid = UUID.randomUUID().toString();
            }
            editingProfile = uuid;
        }
        List<Runtime> runtimes = MultiRTUtils.getRuntimes();
        Context context = javaRuntimeSpinner.getContext();
        javaRuntimeSpinner.setAdapter(new RTSpinnerAdapter(context, runtimes));
        int jvmIndex = runtimes.indexOf(new Runtime("<Default>"));
        int rendererIndex = rendererSpinner.getAdapter().getCount()-1;
        if (minecraftProfile.javaDir != null) {
            String selectedRuntime = minecraftProfile.javaDir.substring(Tools.LAUNCHERPROFILES_RTPREFIX.length());
            int nindex = runtimes.indexOf(new Runtime(selectedRuntime));
            if (nindex != -1) jvmIndex = nindex;
        }
        if(minecraftProfile.pojavRendererName != null) {
            int nindex = renderNames.indexOf(minecraftProfile.pojavRendererName);
            if(nindex != -1) rendererIndex = nindex;
        }
        javaRuntimeSpinner.setSelection(jvmIndex);
        rendererSpinner.setSelection(rendererIndex);
        ExtraCore.addExtraListener(ExtraConstants.VERSION_LIST,this);
        profileNameView.setText(minecraftProfile.name);
        Bitmap profileIcon = ProfileIconCache.getCachedIcon(profile);
        if(profileIcon == null) {
            profileIcon = ProfileIconCache.tryResolveIcon(profile,minecraftProfile.icon);
        }
        profileIconView.setImageBitmap(profileIcon);
        if(minecraftProfile.lastVersionId != null && !"latest-release".equals(minecraftProfile.lastVersionId) && !"latest-snapshot".equals(minecraftProfile.lastVersionId))
            selectedVersionId = minecraftProfile.lastVersionId;
        else if(minecraftProfile.lastVersionId != null) {
            Map<String,String> releaseTable = (Map<String,String>)ExtraCore.getValue(ExtraConstants.RELEASE_TABLE);
            if(releaseTable != null) {
            switch (minecraftProfile.lastVersionId) {
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
        ArrayList<String> versions = (ArrayList<String>) ExtraCore.getValue(ExtraConstants.VERSION_LIST);
        BaseLauncherActivity.updateVersionSpinner(context,versions,versionSpinner, selectedVersionId);
        dialog.show();
        return true;
    }
    public void save(DialogInterface dialog, int which) {
        System.out.println(editingProfile);
        MinecraftProfile profile;
        boolean isNew;
        if(LauncherProfiles.mainProfileJson.profiles.containsKey(editingProfile)) {
            profile = LauncherProfiles.mainProfileJson.profiles.get(editingProfile);
            if(profile == null) {
                profile = new MinecraftProfile();
                isNew = true;
            }else{
                isNew = false;
            }
            LauncherProfiles.mainProfileJson.profiles.remove(editingProfile);
        }else{
            profile = new MinecraftProfile();
            isNew = true;
        }
        profile.name = profileNameView.getText().toString();
        profile.lastVersionId = (String)versionSpinner.getSelectedItem();
        Runtime selectedRuntime = (Runtime) javaRuntimeSpinner.getSelectedItem();
        if(selectedRuntime.name.equals("<Default>")) {
            profile.javaDir = null;
        }else if(selectedRuntime.versionString == null) {
            profile.javaDir = null;
        }else{
            profile.javaDir = Tools.LAUNCHERPROFILES_RTPREFIX+selectedRuntime.name;
        }
        if(rendererSpinner.getSelectedItemPosition() == renderNames.size()) profile.pojavRendererName = null;
        else profile.pojavRendererName = renderNames.get(rendererSpinner.getSelectedItemPosition());
        LauncherProfiles.mainProfileJson.profiles.put(editingProfile,profile);
        editSaveCallback.onSave(editingProfile,isNew, false);
        destroy(dialog);
    }
    public void destroy(@NonNull DialogInterface dialog) {
        ExtraCore.removeExtraListenerFromValue(ExtraConstants.VERSION_LIST,this);
        editingProfile = null;
        selectedVersionId = null;
    }
    @Override
    public boolean onValueSet(String key, @Nullable ArrayList<String> value) {
        if(value != null) {
            uiThreadHandler.post(() -> BaseLauncherActivity.updateVersionSpinner(mainView.getContext(), value, versionSpinner, selectedVersionId));
        }
        return false;
    }
    public interface EditSaveCallback {
        void onSave(String name, boolean isNew, boolean isRemoving);
    }
}
