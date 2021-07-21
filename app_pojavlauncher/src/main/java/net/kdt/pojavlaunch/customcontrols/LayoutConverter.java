package net.kdt.pojavlaunch.customcontrols;

import com.google.gson.JsonSyntaxException;

import net.kdt.pojavlaunch.LWJGLGLFWKeycode;
import net.kdt.pojavlaunch.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LayoutConverter {
    public static boolean convertLookType = false; //false = flat; true = classic
    public static CustomControls loadAndConvertIfNecessary(String jsonPath) throws IOException, JsonSyntaxException {
        String jsonLayoutData = Tools.read(jsonPath);
        try {
            JSONObject layoutJobj = new JSONObject(jsonLayoutData);

            if(!layoutJobj.has("version")) { //v1 layout
                CustomControls layout = LayoutConverter.convertV1Layout(layoutJobj);
                layout.save(jsonPath);
                return layout;
            }else if (layoutJobj.getInt("version") == 2) {
                return Tools.GLOBAL_GSON.fromJson(jsonLayoutData, CustomControls.class);
            }else{
                return null;
            }
        }catch (JSONException e) {
            throw new JsonSyntaxException("Failed to load",e);
        }
    }
    public static CustomControls convertV1Layout(JSONObject oldLayoutJson) throws JSONException {
        CustomControls empty = new CustomControls();
        JSONArray layoutMainArray = oldLayoutJson.getJSONArray("mControlDataList");
        for(int i = 0; i < layoutMainArray.length(); i++) {
            JSONObject button = layoutMainArray.getJSONObject(i);
            ControlData n_button = new ControlData();
            int[] keycodes = new int[] {LWJGLGLFWKeycode.GLFW_KEY_UNKNOWN,
                    LWJGLGLFWKeycode.GLFW_KEY_UNKNOWN,
                    LWJGLGLFWKeycode.GLFW_KEY_UNKNOWN,
                    LWJGLGLFWKeycode.GLFW_KEY_UNKNOWN};
            n_button.dynamicX = button.getString("dynamicX");
            n_button.dynamicY = button.getString("dynamicY");
            n_button.isDynamicBtn = button.getBoolean("isDynamicBtn");
            n_button.name = button.getString("name");
            n_button.opacity = ((float)button.getInt("transparency"))/100f;
            n_button.passThruEnabled = button.getBoolean("passThruEnabled");
            n_button.isToggle = button.getBoolean("isToggle");
            n_button.x = button.getInt("x");
            n_button.y = button.getInt("y");
            n_button.setHeight(button.getInt("height"));
            n_button.setWidth(button.getInt("width"));
            if(convertLookType) {
                n_button.strokeColor = 0xdd7f7f7f;
                n_button.bgColor = 0x807f7f7f;
                n_button.strokeWidth = 10;
            }else{
                n_button.bgColor = 0x4d000000;
                n_button.strokeWidth = 0;
            }
            if(button.getBoolean("isRound")) { n_button.cornerRadius =  35f; }
            int next_idx = 0;
            if(button.getBoolean("holdShift")) { keycodes[next_idx] = LWJGLGLFWKeycode.GLFW_KEY_LEFT_SHIFT; next_idx++; }
            if(button.getBoolean("holdCtrl")) { keycodes[next_idx] = LWJGLGLFWKeycode.GLFW_KEY_LEFT_CONTROL; next_idx++; }
            if(button.getBoolean("holdAlt")) { keycodes[next_idx] = LWJGLGLFWKeycode.GLFW_KEY_LEFT_ALT; next_idx++; }
            keycodes[next_idx] = button.getInt("keycode");
            n_button.keycodes = keycodes;
            n_button.update();
            empty.mControlDataList.add(n_button);
        }
        empty.scaledAt = (float)oldLayoutJson.getDouble("scaledAt");
        empty.version = 2;
        return empty;
    }
}
