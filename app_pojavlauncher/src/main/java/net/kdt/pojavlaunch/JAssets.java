package net.kdt.pojavlaunch;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

@Keep
public class JAssets {
    /* Used by older versions of mc, when the files were named and under .minecraft/resources  */
    @SerializedName("map_to_resources") public boolean mapToResources;
    public Map<String, JAssetInfo> objects;

    /* Used by the legacy.json (~1.6.X) asset file, used for paths at the root of the .minecraft/assets folder */
    public boolean virtual;
}

