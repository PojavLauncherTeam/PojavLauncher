package net.kdt.pojavlaunch.profiles;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.spse.extended_view.ExtendedTextView;

/*
 * Adapter for listing launcher profiles in a Spinner
 */
public class ProfileAdapter extends BaseAdapter {
    private Map<String, MinecraftProfile> mProfiles;
    public static final String CREATE_PROFILE_MAGIC = "___extra____profile-create";
    private final MinecraftProfile dummy = new MinecraftProfile();
    private MinecraftProfile mCreateProfile;
    private List<String> mProfileList;

    public ProfileAdapter(Context context, boolean enableCreateButton) {
        ProfileIconCache.initDefault(context);
        LauncherProfiles.update();
        mProfiles = new HashMap<>(LauncherProfiles.mainProfileJson.profiles);
        if(enableCreateButton) {
            mCreateProfile = new MinecraftProfile();
            mCreateProfile.name = context.getString(R.string.create_profile);
            mCreateProfile.lastVersionId = null;
        }
        mProfileList = new ArrayList<>(Arrays.asList(mProfiles.keySet().toArray(new String[0])));
        if(enableCreateButton) {
            mProfileList.add(ProfileAdapter.CREATE_PROFILE_MAGIC);
            mProfiles.put(CREATE_PROFILE_MAGIC, mCreateProfile);
        }
    }
    /*
     * Gets how much profiles are loaded in the adapter right now
     * @returns loaded profile count
     */
    @Override
    public int getCount() {
        return mProfileList.size();
    }
    /*
     * Gets the profile at a given index
     * @param position index to retreive
     * @returns MinecraftProfile name or null
     */
    @Override
    public Object getItem(int position) {
        //safe since the second check in the and statement will be skipped if the first one fails
        if(position < mProfileList.size() && mProfiles.containsKey(mProfileList.get(position))) {
            return mProfileList.get(position);
        }else{
            return null;
        }
    }

    public int resolveProfileIndex(String name) {
        return mProfileList.indexOf(name);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        mProfiles = new HashMap<>(LauncherProfiles.mainProfileJson.profiles);
        mProfileList = new ArrayList<>(Arrays.asList(mProfiles.keySet().toArray(new String[0])));
        mProfileList.add(ProfileAdapter.CREATE_PROFILE_MAGIC);
        mProfiles.put(CREATE_PROFILE_MAGIC, mCreateProfile);
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_version_profile_layout,parent,false);
        setViewProfile(v,mProfileList.get(position));
        return v;
    }

    public void setViewProfile(View v, String nm) {
        ExtendedTextView extendedTextView = (ExtendedTextView) v;

        MinecraftProfile minecraftProfile = mProfiles.get(nm);
        if(minecraftProfile == null) minecraftProfile = dummy;
        Drawable cachedIcon = ProfileIconCache.getCachedIcon(nm);

        if(cachedIcon == null) {
            cachedIcon = ProfileIconCache.tryResolveIcon(v.getResources(), nm, minecraftProfile.icon);
        }
        extendedTextView.setCompoundDrawablesRelative(cachedIcon, null, extendedTextView.getCompoundsDrawables()[2], null);

        if(Tools.isValidString(minecraftProfile.name))
            extendedTextView.setText(minecraftProfile.name);
        else
            extendedTextView.setText(R.string.unnamed);

        if(minecraftProfile.lastVersionId != null){
            if(minecraftProfile.lastVersionId.equalsIgnoreCase("latest-release")){
                extendedTextView.setText( String.format("%s - %s", extendedTextView.getText(), v.getContext().getText(R.string.profiles_latest_release)));
            } else if(minecraftProfile.lastVersionId.equalsIgnoreCase("latest-snapshot")){
                extendedTextView.setText( String.format("%s - %s", extendedTextView.getText(), v.getContext().getText(R.string.profiles_latest_snapshot)));
            } else {
                extendedTextView.setText( String.format("%s - %s", extendedTextView.getText(), minecraftProfile.lastVersionId));
            }

        } else extendedTextView.setText(extendedTextView.getText());

    }
}
