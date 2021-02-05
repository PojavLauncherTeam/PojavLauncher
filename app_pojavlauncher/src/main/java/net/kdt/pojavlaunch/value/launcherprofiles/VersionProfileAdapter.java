package net.kdt.pojavlaunch.value.launcherprofiles;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.R;

public class VersionProfileAdapter extends ArrayAdapter<MinecraftProfile> {
    final Context ctx;
    public VersionProfileAdapter(@NonNull Context context, int resource, @NonNull MinecraftProfile[] objects) {
        super(context, resource, objects);
        this.ctx = context;
    }
    public VersionProfileAdapter(@NonNull Context context, int resource,int r2, @NonNull MinecraftProfile[] objects) {
        super(context, resource,r2, objects);
        this.ctx = context;
    }
    private View prepareView(int pos, ViewGroup parent) {
        View layout = ((Activity)ctx).getLayoutInflater().inflate(R.layout.version_profile_layout,parent,false);
        if(getItem(pos).name != null && !getItem(pos).name.isEmpty()) ((TextView)layout.findViewById(R.id.vprof_profile_name_view)).setText(getItem(pos).name);
        if(getItem(pos).lastVersionId.equals("latest-snapshot")) {
                ((TextView) layout.findViewById(R.id.vprof_version_id_view)).setText(R.string.vp_latest_snapshot);
        }else if(getItem(pos).lastVersionId.equals("latest-release")) {
                ((TextView)layout.findViewById(R.id.vprof_version_id_view)).setText(R.string.vp_latest_release);
        }else{
                ((TextView)layout.findViewById(R.id.vprof_version_id_view)).setText(getItem(pos).lastVersionId);
        }
        if(getItem(pos).icon != null && getItem(pos).icon.startsWith("data:")) ((ImageView)layout.findViewById(R.id.vprof_icon_view)).setImageBitmap(decodeIcon(pos));
        return layout;
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
    private Bitmap decodeIcon(int verPos) {
      byte[] image = Base64.decode(getItem(verPos).icon.split(",")[1],Base64.DEFAULT);
      return BitmapFactory.decodeByteArray(image,0,image.length);
    }
}
