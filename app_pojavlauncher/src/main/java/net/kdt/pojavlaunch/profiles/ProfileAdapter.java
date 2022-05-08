package net.kdt.pojavlaunch.profiles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            mCreateProfile.name = "Create new profile";
            mCreateProfile.lastVersionId = "";
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

    public void fireProfileEdit() {
        notifyDataSetChanged();
    }
    /*
     * Gets the item ID (crc64 hash of the profile name) for a given index
     * @param position index to get the hash for
     * @returns ID (crc64 of a profile name string) or -1 if the index is out of bounds
     */
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
        if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.version_profile_layout, parent,false);
        setViewProfile(convertView, mProfileList.get(position));
        return convertView;
    }

    public void setViewProfile(View view, String nm) {
        MinecraftProfile minecraftProfile = mProfiles.get(nm);
        if(minecraftProfile == null) minecraftProfile = dummy;
        BitmapDrawable cachedIcon = ProfileIconCache.getCachedIcon(nm);
        if(cachedIcon == null) {
            cachedIcon = ProfileIconCache.tryResolveIcon(nm, minecraftProfile.icon);
        }

        TextView textView = (TextView) view;

        textView.setCompoundDrawablesRelative(cachedIcon, null, null, null);
        if(minecraftProfile.name != null && !minecraftProfile.name.isEmpty())
            ((TextView)view).setText(minecraftProfile.name);
        else
            ((TextView)view).setText(R.string.unnamed);


        if(minecraftProfile.lastVersionId != null) switch (minecraftProfile.lastVersionId) {
            case "latest-release":
                textView.setText(R.string.profiles_latest_release);
            case "latest-snapshot":
                textView.setText(R.string.profiles_latest_snapshot);
            default:
                textView.setText(minecraftProfile.lastVersionId);
        } else textView.setText(android.R.string.unknownName);

    }
}
