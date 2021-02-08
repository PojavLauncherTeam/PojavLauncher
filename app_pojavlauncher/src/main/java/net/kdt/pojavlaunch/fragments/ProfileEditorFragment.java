package net.kdt.pojavlaunch.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import net.kdt.pojavlaunch.BaseLauncherActivity;
import net.kdt.pojavlaunch.BaseMainActivity;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.tasks.RefreshVersionListTask;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;
import net.kdt.pojavlaunch.value.launcherprofiles.VersionProfileAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.kdt.pojavlaunch.value.launcherprofiles.VersionProfileAdapter.decodeIcon;

public class ProfileEditorFragment extends Fragment {
    String currentProfile;
    String selectedVersion;
    Map<String,MinecraftProfile> profiles;

    BaseLauncherActivity activity;
    RecyclerView versionRecyclerView;
    RecyclerView profileRecyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        new RefreshVersionListTask((BaseLauncherActivity)getActivity()).execute();
        return inflater.inflate(R.layout.lmaintab_profileeditor,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        versionRecyclerView = getView().findViewById(R.id.versionRecyclerView);
        profileRecyclerView = getView().findViewById(R.id.profileRecyclerView);
        activity = (BaseLauncherActivity) getActivity();
        versionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        profileRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getView().findViewById(R.id.mineButtonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCurrentProfile();
            }
        });
        getView().findViewById(R.id.mineButtonNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newname = "";
                boolean isUnique = false;
                while(!isUnique) {
                    newname = "pojav-"+(Math.random() *10000);
                    if(!LauncherProfiles.mainProfileJson.profiles.containsKey(newname)) isUnique = true;
                }
                MinecraftProfile prof = new MinecraftProfile();
                if(selectedVersion != null) prof.lastVersionId = selectedVersion;
                prof.name = ((EditText)getView().findViewById(R.id.mineEditTextProfileName)).getText().toString();
            profiles.put(newname,prof);
                LauncherProfiles.update();
                refreshProfiles();
                selectProfile(newname);
            }
        });
        getView().findViewById(R.id.mineButtonRemove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if(currentProfile != null) {
                     LauncherProfiles.mainProfileJson.profiles.remove(currentProfile);
                     currentProfile = null;
                     refreshProfiles();
                 }
            }
        });
        //////////////////////////////////////////
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshProfiles();
        //refreshVersions();
    }


    public void refreshVersions() {
        if(activity == null) return;

        if(!(versionRecyclerView.getAdapter() instanceof RecyclerViewVersionAdapter)){
            System.out.println("Created a new Adapter for profile version list!");
            versionRecyclerView.setAdapter(new RecyclerViewVersionAdapter(getContext(),this));
        }

        System.out.println("Created a new Adapter for profile version list!");
        ((RecyclerViewVersionAdapter)versionRecyclerView.getAdapter()).itemList = activity.mVersionStringList;
        versionRecyclerView.getAdapter().notifyDataSetChanged();
        System.gc();
    }

    public void refreshProfiles() {
        if(activity != null) {
            activity.updateProfileList();
        }
        if (!(profileRecyclerView.getAdapter() instanceof ProfileRecyclerAdapter)){
            System.out.println("Created a new Adapter for profile version list!");
            profileRecyclerView.setAdapter(new ProfileRecyclerAdapter(getContext(),this));
        }
        profiles = LauncherProfiles.mainProfileJson.profiles;
        ((ProfileRecyclerAdapter) profileRecyclerView.getAdapter()).keys = profiles.keySet().toArray(new String[0]);
        profileRecyclerView.getAdapter().notifyDataSetChanged();
        System.gc();
    }

    public void selectProfile(String profile) {
        currentProfile = profile;
        MinecraftProfile p = profiles.get(profile);
        if(p != null && p.name != null) {
            Toast.makeText(getContext(),String.format(getString(R.string.profedit_selected_profile),p.name),Toast.LENGTH_SHORT).show();
            ((EditText)getView().findViewById(R.id.mineEditTextProfileName)).setText(p.name);
            ((TextView)getView().findViewById(R.id.mineCurrentProfile)).setText(p.name);
        }else{
            Toast.makeText(getContext(),String.format(getString(R.string.profedit_selected_profile),getString(android.R.string.unknownName)),Toast.LENGTH_SHORT).show();
            ((EditText)getView().findViewById(R.id.mineEditTextProfileName)).setText(android.R.string.unknownName);
            ((TextView)getView().findViewById(R.id.mineCurrentProfile)).setText(android.R.string.unknownName);
        }
    }
    public void saveCurrentProfile() {
        //profiles[]
        if(currentProfile != null) {
            MinecraftProfile prof = profiles.get(currentProfile);
            prof.name = ((EditText) getView().findViewById(R.id.mineEditTextProfileName)).getText().toString();
            prof.lastVersionId = selectedVersion;
            profiles.put(currentProfile, prof);
            refreshProfiles();
        }
    }


    public static class RecyclerViewVersionAdapter extends RecyclerView.Adapter {
        final Context ctx;

        List<String> itemList;
        final ProfileEditorFragment host;
        public RecyclerViewVersionAdapter(Context ctx, ProfileEditorFragment host) {
            this.ctx = ctx;
            this.host = host;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.animated_text_view,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof ViewHolder) {
                ((TextView)(((ViewHolder) holder).v)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        host.selectedVersion = ((TextView)view).getText().toString();
                        Toast.makeText(host.getContext(),String.format(host.getString(R.string.profedit_selected_version),host.selectedVersion),Toast.LENGTH_SHORT).show();
                        ((TextView)host.getView().findViewById(R.id.mineCurrentVersion)).setText(host.selectedVersion);
                    }
                });
                ((TextView)(((ViewHolder) holder).v)).setText(itemList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            View v;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.v = itemView;
            }
        }


    }

    public static class ProfileRecyclerAdapter extends RecyclerView.Adapter {
        final Context ctx;
        ProfileClickListener lst;
        final ProfileEditorFragment host;
        String[] keys;

        public ProfileRecyclerAdapter(Context ctx,ProfileEditorFragment host) {
            this.ctx = ctx;
            lst = new ProfileClickListener();
            this.host = host;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerViewVersionAdapter.ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.version_profile_layout,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof RecyclerViewVersionAdapter.ViewHolder) {
                ((RecyclerViewVersionAdapter.ViewHolder) holder).v.setOnClickListener(lst);
                System.out.println("Binding to " +position +" "+keys[position]);
                MinecraftProfile profileData = host.profiles.get(keys[position]);
                if(profileData.name != null && !profileData.name.isEmpty()) {
                    ((TextView) ((RecyclerViewVersionAdapter.ViewHolder) holder).v.findViewById(R.id.vprof_profile_name_view)).setText(profileData.name);
                }else{
                    ((TextView) ((RecyclerViewVersionAdapter.ViewHolder) holder).v.findViewById(R.id.vprof_profile_name_view)).setText(android.R.string.unknownName);
                }
                if(profileData.lastVersionId != null) {if(profileData.lastVersionId.equals("latest-snapshot")) {
                    ((TextView) ((RecyclerViewVersionAdapter.ViewHolder) holder).v.findViewById(R.id.vprof_version_id_view)).setText(R.string.vp_latest_snapshot);
                }else if(profileData.lastVersionId.equals("latest-release")) {
                    ((TextView) ((RecyclerViewVersionAdapter.ViewHolder) holder).v.findViewById(R.id.vprof_version_id_view)).setText(R.string.vp_latest_release);
                }else{
                    ((TextView) ((RecyclerViewVersionAdapter.ViewHolder) holder).v.findViewById(R.id.vprof_version_id_view)).setText(profileData.lastVersionId);
                }}else {
                    ((TextView) ((RecyclerViewVersionAdapter.ViewHolder) holder).v.findViewById(R.id.vprof_version_id_view)).setText(android.R.string.unknownName);
                }
                if(profileData.icon != null && profileData.icon.startsWith("data:")) {
                    if(!BaseLauncherActivity.versionIcons.containsKey(keys[position])) {
                        BaseLauncherActivity.versionIcons.put(keys[position], decodeIcon(profileData.icon));
                    }
                    ((ImageView) ((RecyclerViewVersionAdapter.ViewHolder) holder).v.findViewById(R.id.vprof_icon_view)).setImageBitmap(BaseLauncherActivity.versionIcons.get(keys[position]));
                }else{
                    ((ImageView) ((RecyclerViewVersionAdapter.ViewHolder) holder).v.findViewById(R.id.vprof_icon_view)).setImageResource(R.drawable.ic_menu_java);
                }

            }
        }

        @Override
        public int getItemCount() {
            return keys.length;
        }
        public class ProfileClickListener implements View.OnClickListener{
            @Override
            public void onClick(View view) {
                host.selectProfile(keys[((RecyclerView)host.getView().findViewById(R.id.profileRecyclerView)).getChildLayoutPosition(view)]);
            }
        }
    }
}
