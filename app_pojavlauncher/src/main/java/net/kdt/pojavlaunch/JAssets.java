package net.kdt.pojavlaunch;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class JAssets {
    @SerializedName("map_to_resources") public boolean mapToResources;
    public Map<String, JAssetInfo> objects;
}

