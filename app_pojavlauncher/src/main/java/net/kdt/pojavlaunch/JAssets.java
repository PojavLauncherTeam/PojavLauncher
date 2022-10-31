package net.kdt.pojavlaunch;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

@Keep
public class JAssets {
    @SerializedName("map_to_resources") public boolean mapToResources;
    public Map<String, JAssetInfo> objects;
}

