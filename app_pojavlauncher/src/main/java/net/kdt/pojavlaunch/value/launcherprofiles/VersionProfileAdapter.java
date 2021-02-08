package net.kdt.pojavlaunch.value.launcherprofiles;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.BaseLauncherActivity;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.fragments.ProfileEditorFragment;

public class VersionProfileAdapter extends BaseAdapter {
    final Context ctx;
    public String[] profileKeys;
    public VersionProfileAdapter(Context ctx) {
        this.ctx = ctx;
        LauncherProfiles.update();
        profileKeys = LauncherProfiles.mainProfileJson.profiles.keySet().toArray(new String[0]);
    }

    private View prepareView(int pos, ViewGroup parent) {
        View layout = ((Activity)ctx).getLayoutInflater().inflate(R.layout.version_profile_layout,parent,false);
        MinecraftProfile prof = LauncherProfiles.mainProfileJson.profiles.get(profileKeys[pos]);
        if(prof.name != null && !prof.name.isEmpty()) ((TextView)layout.findViewById(R.id.vprof_profile_name_view)).setText(prof.name);
        if(prof.lastVersionId != null) {if(prof.lastVersionId.equals("latest-snapshot")) {
                ((TextView) layout.findViewById(R.id.vprof_version_id_view)).setText(R.string.vp_latest_snapshot);
        }else if(prof.lastVersionId.equals("latest-release")) {
                ((TextView)layout.findViewById(R.id.vprof_version_id_view)).setText(R.string.vp_latest_release);
        }else{
                ((TextView)layout.findViewById(R.id.vprof_version_id_view)).setText(prof.lastVersionId);
        }}else{
            ((TextView) (layout.findViewById(R.id.vprof_version_id_view))).setText(android.R.string.unknownName);
        }
        if(prof.icon != null && prof.icon.startsWith("data:")){
            if(!BaseLauncherActivity.versionIcons.containsKey(profileKeys[pos])) {
                BaseLauncherActivity.versionIcons.put(profileKeys[pos], decodeIcon(prof.icon));
            }
            ((ImageView)layout.findViewById(R.id.vprof_icon_view)).setImageBitmap(BaseLauncherActivity.versionIcons.get(profileKeys[pos]));
        }
        return layout;
    }

    @Override
    public int getCount() {
        return profileKeys.length;
    }

    @Override
    public Object getItem(int i) {
        return LauncherProfiles.mainProfileJson.profiles.get(profileKeys[i]);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return prepareView(position,parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return prepareView(position,parent);
    }
    public static Bitmap decodeIcon(String icon) {
      byte[] image = Base64.decode(icon.split(",")[1],Base64.DEFAULT);
      return BitmapFactory.decodeByteArray(image,0,image.length);
    }
    public void update() {
        notifyDataSetChanged();
    }
}
