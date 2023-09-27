package net.kdt.pojavlaunch.modloaders;

import org.json.JSONException;
import org.json.JSONObject;

public interface FabricMetaReader {
    boolean processMetadata(JSONObject jsonObject) throws JSONException;
}
