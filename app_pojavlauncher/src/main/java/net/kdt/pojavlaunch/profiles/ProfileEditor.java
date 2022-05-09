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
import android.widget.EditText;
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
    private final View mMainView;
    private final TextView mProfileNameTextView;
    private final ImageView mProfileIconImageView;
    private final Spinner mVersionSpinner;
    private final Spinner mJavaRuntimeSpinner;
    private final Spinner mRendererSpinner;
    private final EditText mPathSelectionEditText;
    private final EditText mArgChangerEditText;
    private final List<String> mRenderNames;
    private final AlertDialog mDialog;
    private String mSelectedVersionId;
    private String mEditingProfile;
    private final EditSaveCallback mEditSaveCallback;
    private final Handler mUiThreadHandler;
  
    public static MinecraftProfile generateTemplate() {
        MinecraftProfile TEMPLATE = new MinecraftProfile();
        TEMPLATE.name = "New";
        TEMPLATE.lastVersionId = "latest-release";
        return TEMPLATE;
    }
    public ProfileEditor(Context ctx, EditSaveCallback editSaveCallback) {
        this.mEditSaveCallback = editSaveCallback;
        mUiThreadHandler = new Handler(Looper.getMainLooper());
        LayoutInflater inflater = LayoutInflater.from(ctx);
        mMainView = inflater.inflate(R.layout.version_profile_editor,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setView(mMainView);
        mProfileNameTextView = mMainView.findViewById(R.id.vprof_editior_profile_name);
        mVersionSpinner = mMainView.findViewById(R.id.vprof_editor_version_spinner);
        mJavaRuntimeSpinner = mMainView.findViewById(R.id.vprof_editor_spinner_runtime);
        mRendererSpinner = mMainView.findViewById(R.id.vprof_editor_profile_renderer);
        {
            Context context = mRendererSpinner.getContext();
            List<String> renderList = new ArrayList<>();
            Collections.addAll(renderList, context.getResources().getStringArray(R.array.renderer));
            renderList.add("Default");
            mRenderNames = Arrays.asList(context.getResources().getStringArray(R.array.renderer_values));
            mRendererSpinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,renderList));
        }
        mProfileIconImageView = mMainView.findViewById(R.id.vprof_editor_icon);
        ((TextView)mMainView.findViewById(R.id.vprof_editor_beginPathView)).setText(Tools.DIR_GAME_HOME+"/");
        mPathSelectionEditText = mMainView.findViewById(R.id.vprof_editor_path);
        mArgChangerEditText = mMainView.findViewById(R.id.vprof_editor_jre_args);

        builder.setPositiveButton(R.string.global_save,this::save);
        builder.setNegativeButton(android.R.string.cancel,(dialog,which)->destroy(dialog));
        builder.setNeutralButton(R.string.global_delete,(dialogInterface, i) -> {
            LauncherProfiles.mainProfileJson.profiles.remove(mEditingProfile);
            this.mEditSaveCallback.onSave(mEditingProfile,false, true);
        });
        builder.setOnDismissListener(this::destroy);
        mDialog = builder.create();
    }

    public boolean show(@NonNull String profile) {
        MinecraftProfile minecraftProfile;
        if(!ProfileAdapter.CREATE_PROFILE_MAGIC.equals(profile)) {
            minecraftProfile = LauncherProfiles.mainProfileJson.profiles.get(profile);
            if (minecraftProfile == null) return true;
            mEditingProfile = profile;
        }else{
            minecraftProfile = generateTemplate();
            String uuid = UUID.randomUUID().toString();
            while(LauncherProfiles.mainProfileJson.profiles.containsKey(uuid)) {
                uuid = UUID.randomUUID().toString();
            }
            mEditingProfile = uuid;
        }

        List<Runtime> runtimes = MultiRTUtils.getRuntimes();
        Context context = mJavaRuntimeSpinner.getContext();
        mJavaRuntimeSpinner.setAdapter(new RTSpinnerAdapter(context, runtimes));
        int jvmIndex = runtimes.indexOf(new Runtime("<Default>"));
        int rendererIndex = mRendererSpinner.getAdapter().getCount()-1;
        if (minecraftProfile.javaDir != null) {
            String selectedRuntime = minecraftProfile.javaDir.substring(Tools.LAUNCHERPROFILES_RTPREFIX.length());
            int nindex = runtimes.indexOf(new Runtime(selectedRuntime));
            if (nindex != -1) jvmIndex = nindex;
        }
        if(minecraftProfile.pojavRendererName != null) {
            int nindex = mRenderNames.indexOf(minecraftProfile.pojavRendererName);
            if(nindex != -1) rendererIndex = nindex;
        }
        mJavaRuntimeSpinner.setSelection(jvmIndex);
        mRendererSpinner.setSelection(rendererIndex);
        ExtraCore.addExtraListener(ExtraConstants.VERSION_LIST,this);
        mProfileNameTextView.setText(minecraftProfile.name);
        Bitmap profileIcon = ProfileIconCache.getCachedIcon(profile);
        if(profileIcon == null) {
            profileIcon = ProfileIconCache.tryResolveIcon(profile,minecraftProfile.icon);
        }
        mProfileIconImageView.setImageBitmap(profileIcon);
        if(minecraftProfile.lastVersionId != null && !"latest-release".equals(minecraftProfile.lastVersionId) && !"latest-snapshot".equals(minecraftProfile.lastVersionId))
            mSelectedVersionId = minecraftProfile.lastVersionId;
        else if(minecraftProfile.lastVersionId != null) {
            Map<String,String> releaseTable = (Map<String,String>)ExtraCore.getValue(ExtraConstants.RELEASE_TABLE);
            if(releaseTable != null) {
            switch (minecraftProfile.lastVersionId) {
                case "latest-release":
                    mSelectedVersionId = releaseTable.get("release");
                case "latest-snapshot":
                    mSelectedVersionId = releaseTable.get("snapshot");
            }
        }else{
                mSelectedVersionId = null;
            }
        }
        else{
            if(PojavLauncherActivity.basicVersionList.length > 0) {
                mSelectedVersionId = PojavLauncherActivity.basicVersionList[0];
            }
        }
        ArrayList<String> versions = (ArrayList<String>) ExtraCore.getValue(ExtraConstants.VERSION_LIST);

        BaseLauncherActivity.updateVersionSpinner(context,versions,mVersionSpinner, mSelectedVersionId);
        if(minecraftProfile.gameDir != null && minecraftProfile.gameDir.startsWith(Tools.LAUNCHERPROFILES_RTPREFIX))
            mPathSelectionEditText.setText(minecraftProfile.gameDir.substring(Tools.LAUNCHERPROFILES_RTPREFIX.length()));
        else mPathSelectionEditText.setText("");

        if(minecraftProfile.javaArgs != null) mArgChangerEditText.setText(minecraftProfile.javaArgs);
        else mArgChangerEditText.setText("");
        mDialog.show();
        return true;
    }
    public void save(DialogInterface dialog, int which) {
        System.out.println(mEditingProfile);
        MinecraftProfile profile;
        boolean isNew;
        if(LauncherProfiles.mainProfileJson.profiles.containsKey(mEditingProfile)) {
            profile = LauncherProfiles.mainProfileJson.profiles.get(mEditingProfile);
            if(profile == null) {
                profile = new MinecraftProfile();
                isNew = true;
            }else{
                isNew = false;
            }
            LauncherProfiles.mainProfileJson.profiles.remove(mEditingProfile);
        }else{
            profile = new MinecraftProfile();
            isNew = true;
        }
        profile.name = mProfileNameTextView.getText().toString();
        profile.lastVersionId = (String) mVersionSpinner.getSelectedItem();
        Runtime selectedRuntime = (Runtime) mJavaRuntimeSpinner.getSelectedItem();
        if(selectedRuntime.name.equals("<Default>")) {
            profile.javaDir = null;
        }else if(selectedRuntime.versionString == null) {
            profile.javaDir = null;
        }else{
            profile.javaDir = Tools.LAUNCHERPROFILES_RTPREFIX+selectedRuntime.name;
        }

        if(mRendererSpinner.getSelectedItemPosition() == mRenderNames.size()) profile.pojavRendererName = null;
        else profile.pojavRendererName = mRenderNames.get(mRendererSpinner.getSelectedItemPosition());
        String selectedPath = mPathSelectionEditText.getText().toString();
        String arguments = mArgChangerEditText.getText().toString();
        if(!selectedPath.isEmpty()) profile.gameDir = Tools.LAUNCHERPROFILES_RTPREFIX+selectedPath;
        else profile.gameDir = null;
        if(!arguments.isEmpty()) profile.javaArgs = arguments;
        else profile.javaArgs = null;
        LauncherProfiles.mainProfileJson.profiles.put(mEditingProfile,profile);
        mEditSaveCallback.onSave(mEditingProfile,isNew, false);
        destroy(dialog);
    }
    public void destroy(@NonNull DialogInterface dialog) {
        ExtraCore.removeExtraListenerFromValue(ExtraConstants.VERSION_LIST,this);
        mEditingProfile = null;
        mSelectedVersionId = null;
    }
    @Override
    public boolean onValueSet(String key, @Nullable ArrayList<String> value) {
        if(value != null) {
            mUiThreadHandler.post(() -> BaseLauncherActivity.updateVersionSpinner(mMainView.getContext(), value, mVersionSpinner, mSelectedVersionId));
        }
        return false;
    }
    public interface EditSaveCallback {
        void onSave(String name, boolean isNew, boolean isRemoving);
    }
}
