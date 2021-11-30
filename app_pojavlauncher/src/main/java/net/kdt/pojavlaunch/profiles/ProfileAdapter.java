package net.kdt.pojavlaunch.profiles;

import android.content.Context;
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

import java.util.HashMap;
import java.util.Map;
/*
 * Adapter for listing launcher profiles in a Spinner
 */
public class ProfileAdapter extends BaseAdapter {
    Map<String, MinecraftProfile> profiles;
    Map<String, Bitmap> iconCache;
    static final String BASE64_PNG_HEADER = "data:image/png;base64,";
    static final MinecraftProfile DUMMY = new MinecraftProfile();
    String[] profileArray;
    public ProfileAdapter(Context ctx) {
        LauncherProfiles.update();
        profiles = LauncherProfiles.mainProfileJson.profiles;
        profileArray = profiles.keySet().toArray(new String[0]);
        iconCache = new HashMap<>();
        iconCache.put(null,BitmapFactory.decodeResource(ctx.getResources(),R.drawable.ic_menu_java));
    }
    /*
     * Gets how much profiles are loaded in the adapter right now
     * @returns loaded profile count
     */
    @Override
    public int getCount() {
        return profileArray.length;
    }

    /*
     * Gets the profile at a given index
     * @param position index to retreive
     * @returns MinecraftProfile or null if the index is out of bounds
     */
    @Override
    public Object getItem(int position) {
        //safe since the second check in the and statement will be skipped if the first one fails
        if(position < profileArray.length && profiles.containsKey(profileArray[position])) {
            return profiles.get(profileArray[position]);
        }else{
            return null;
        }
    }

    /*
     * Gets the item ID (crc64 hash of the profile name) for a given index
     * @param position index to get the hash for
     * @returns ID (crc64 of a profile name string) or -1 if the index is out of bounds
     */
    @Override
    public long getItemId(int position) {
        if(position < profileArray.length) {
            return StringCRC64.strhash(profileArray[position]);
        }else{
            return -1L;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) v = LayoutInflater.from(parent.getContext()).inflate(R.layout.version_profile_layout,parent,false);
        setViewProfile(v,profileArray[position]);
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
