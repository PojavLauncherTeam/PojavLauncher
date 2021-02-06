package net.kdt.pojavlaunch.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.kdt.pojavlaunch.BaseLauncherActivity;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import java.util.List;
import java.util.Map;

import static net.kdt.pojavlaunch.value.launcherprofiles.VersionProfileAdapter.decodeIcon;

public class ProfileEditorFragment extends Fragment {
    String currentProfile;
    String selectedVersion;
    Map<String,MinecraftProfile> profiles;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lmaintab_profileeditor,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((RecyclerView)getView().findViewById(R.id.versionRecyclerView)).setLayoutManager(new LinearLayoutManager(getContext()));
        ((RecyclerView)getView().findViewById(R.id.profileRecyclerView)).setLayoutManager(new LinearLayoutManager(getContext()));
        getView().findViewById(R.id.mineButtonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCurrentProfile();
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
        if(getActivity() instanceof BaseLauncherActivity) {
            BaseLauncherActivity activity = (BaseLauncherActivity) getActivity();
            RecyclerView v = getView().findViewById(R.id.versionRecyclerView);
            if(!(v.getAdapter() instanceof RecyclerViewVersionAdapter)) {
                System.out.println("Created a new Adapter for profile version list!");
                v.setAdapter(new RecyclerViewVersionAdapter(getContext(),this));
            }
            System.out.println("Updating...");
            ((RecyclerViewVersionAdapter) v.getAdapter()).itemList = activity.mVersionStringList;
            v.getAdapter().notifyDataSetChanged();
        }
    }
    public void refreshProfiles() {
        LauncherProfiles.update();
        RecyclerView v = getView().findViewById(R.id.profileRecyclerView);
        if(!(v.getAdapter() instanceof ProfileRecyclerAdapter)) {
            System.out.println("Created a new Adapter for profile version list!");
            v.setAdapter(new ProfileRecyclerAdapter(getContext(),this));
        }
        profiles = LauncherProfiles.mainProfileJson.profiles;
        //System.out.println(LauncherProfiles.mainProfileJson.profiles.values().getClass().getName());
        ((ProfileRecyclerAdapter) v.getAdapter()).keys = profiles.keySet().toArray(new String[0]);
        v.getAdapter().notifyDataSetChanged();
    }
    public void selectProfile(String profile) {
        currentProfile = profile;
        MinecraftProfile p = profiles.get(profile);
        if(p.name != null && !p.name.isEmpty()) {
            ((EditText)getView().findViewById(R.id.mineEditTextProfileName)).setText(p.name);
            ((TextView)getView().findViewById(R.id.mineCurrentProfile)).setText(p.name);
        }else{
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
    static TypedValue selectableItemBackground;
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
            return new ViewHolder(LayoutInflater.from(ctx).inflate(android.R.layout.simple_list_item_1,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof ViewHolder) {
                ((TextView)(((ViewHolder) holder).v)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        host.selectedVersion = ((TextView)view).getText().toString();
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
                if(selectableItemBackground == null) {
                    selectableItemBackground = new TypedValue();
                    itemView.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, selectableItemBackground, true);
                }
                itemView.setBackground(itemView.getContext().getDrawable(selectableItemBackground.resourceId));
                //itemView.setBackgroundResource(android.R.attr.selectableItemBackground);
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
                if(profileData.lastVersionId.equals("latest-snapshot")) {
                    ((TextView) ((RecyclerViewVersionAdapter.ViewHolder) holder).v.findViewById(R.id.vprof_version_id_view)).setText(R.string.vp_latest_snapshot);
                }else if(profileData.lastVersionId.equals("latest-release")) {
                    ((TextView) ((RecyclerViewVersionAdapter.ViewHolder) holder).v.findViewById(R.id.vprof_version_id_view)).setText(R.string.vp_latest_release);
                }else{
                    ((TextView) ((RecyclerViewVersionAdapter.ViewHolder) holder).v.findViewById(R.id.vprof_version_id_view)).setText(profileData.lastVersionId);
                }
                if(profileData.icon != null && profileData.icon.startsWith("data:")) {
                    ((ImageView) ((RecyclerViewVersionAdapter.ViewHolder) holder).v.findViewById(R.id.vprof_icon_view)).setImageBitmap(decodeIcon(profileData.icon));
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
