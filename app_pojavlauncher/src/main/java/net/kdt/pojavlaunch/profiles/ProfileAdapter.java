package net.kdt.pojavlaunch.profiles;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    Map<String, MinecraftProfile> profiles;
    ArrayList<DataSetObserver> observers = new ArrayList<>();
    static final Map<String, Bitmap> iconCache = new HashMap<>();
    static final String BASE64_PNG_HEADER = "data:image/png;base64,";
    public static final String CREATE_PROFILE_MAGIC = "___extra____profile-create";
    static final MinecraftProfile DUMMY = new MinecraftProfile();
    static MinecraftProfile CREATE_PROFILE;
    List<String> profileList;
    public ProfileAdapter(Context ctx) {
        LauncherProfiles.update();
        profiles = new HashMap<>(LauncherProfiles.mainProfileJson.profiles);
        if(CREATE_PROFILE == null) {
            CREATE_PROFILE = new MinecraftProfile();
            CREATE_PROFILE.name = "Create new profile";
            CREATE_PROFILE.lastVersionId = "";
        }
        profileList = new ArrayList<>(Arrays.asList(profiles.keySet().toArray(new String[0])));
        profileList.add(ProfileAdapter.CREATE_PROFILE_MAGIC);
        profiles.put(CREATE_PROFILE_MAGIC, CREATE_PROFILE);
        if(!iconCache.containsKey(null))
            iconCache.put(null,BitmapFactory.decodeResource(ctx.getResources(),R.drawable.ic_menu_java));

    }
    /*
     * Gets how much profiles are loaded in the adapter right now
     * @returns loaded profile count
     */
    @Override
    public int getCount() {
        return profileList.size();
    }

    /*
     * Gets the profile at a given index
     * @param position index to retreive
     * @returns MinecraftProfile name or null
     */
    @Override
    public Object getItem(int position) {
        //safe since the second check in the and statement will be skipped if the first one fails
        if(position < profileList.size() && profiles.containsKey(profileList.get(position))) {
            return profileList.get(position);
        }else{
            return null;
        }
    }

    public int resolveProfileIndex(String name) {
        return profileList.indexOf(name);
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
        profiles = new HashMap<>(LauncherProfiles.mainProfileJson.profiles);
        profileList = new ArrayList<>(Arrays.asList(profiles.keySet().toArray(new String[0])));
        profileList.add(ProfileAdapter.CREATE_PROFILE_MAGIC);
        profiles.put(CREATE_PROFILE_MAGIC, CREATE_PROFILE);
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) v = LayoutInflater.from(parent.getContext()).inflate(R.layout.version_profile_layout,parent,false);
        setViewProfile(v,profileList.get(position));
        return v;
    }
    public void setViewProfile(View v, String nm) {
        MinecraftProfile prof = profiles.get(nm);
        if(prof == null) prof = DUMMY;
        Bitmap cachedIcon = iconCache.get(nm);
        ImageView iconView = v.findViewById(R.id.vprof_icon_view);
        if(cachedIcon == null && prof.icon != null) {
            if (prof.icon.startsWith(BASE64_PNG_HEADER)) {
                byte[] pngBytes = Base64.decode(prof.icon.substring(BASE64_PNG_HEADER.length()), Base64.DEFAULT);
                cachedIcon = BitmapFactory.decodeByteArray(pngBytes,0,pngBytes.length);
                iconCache.put(nm,cachedIcon);
            }else{
                Log.i("IconParser","Unsupported icon: "+prof.icon);
                cachedIcon = iconCache.get(null);
            }
        }

        iconView.setImageBitmap(cachedIcon);
        if(prof.name != null && !prof.name.isEmpty())
            ((TextView)v.findViewById(R.id.vprof_profile_name_view)).setText(prof.name);
        else
            ((TextView)v.findViewById(R.id.vprof_profile_name_view)).setText(R.string.unnamed);

        TextView tv = v.findViewById(R.id.vprof_version_id_view);
        if(prof.lastVersionId != null) switch (prof.lastVersionId) {
            case "latest-release":
                tv.setText(R.string.profiles_latest_release);
            case "latest-snapshot":
                tv.setText(R.string.profiles_latest_snapshot);
            default:
                tv.setText(prof.lastVersionId);
        } else tv.setText(android.R.string.unknownName);

    }
}
