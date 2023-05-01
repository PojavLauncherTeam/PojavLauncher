package net.kdt.pojavlaunch.customcontrols;

import com.google.gson.JsonSyntaxException;

import net.kdt.pojavlaunch.LwjglGlfwKeycode;
import net.kdt.pojavlaunch.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.glfw.CallbackBridge;

import java.io.IOException;
import java.util.ArrayList;

public class LayoutConverter {
    public static CustomControls loadAndConvertIfNecessary(String jsonPath) throws IOException, JsonSyntaxException {

        String jsonLayoutData = Tools.read(jsonPath);
        try {
            JSONObject layoutJobj = new JSONObject(jsonLayoutData);

            if(!layoutJobj.has("version")) { //v1 layout
                CustomControls layout = LayoutConverter.convertV1Layout(layoutJobj);
                layout.save(jsonPath);
                return layout;
            }else if (layoutJobj.getInt("version") == 2) {
                CustomControls layout = LayoutConverter.convertV2Layout(layoutJobj);
                layout.save(jsonPath);
                return layout;
            }else if (layoutJobj.getInt("version") == 3 || layoutJobj.getInt("version") == 4) {
                return Tools.GLOBAL_GSON.fromJson(jsonLayoutData, CustomControls.class);
            }else{
                return null;
            }
        }catch (JSONException e) {
            throw new JsonSyntaxException("Failed to load",e);
        }
    }
    public static CustomControls convertV2Layout(JSONObject oldLayoutJson) throws JSONException {
        CustomControls layout = Tools.GLOBAL_GSON.fromJson(oldLayoutJson.toString(), CustomControls.class);
        JSONArray layoutMainArray = oldLayoutJson.getJSONArray("mControlDataList");
        layout.mControlDataList = new ArrayList<>(layoutMainArray.length());
        for(int i = 0; i < layoutMainArray.length(); i++) {
            JSONObject button = layoutMainArray.getJSONObject(i);
            ControlData n_button = Tools.GLOBAL_GSON.fromJson(button.toString(), ControlData.class);
            if(!Tools.isValidString(n_button.dynamicX) && button.has("x")) {
                double buttonC = button.getDouble("x");
                double ratio = buttonC/CallbackBridge.physicalWidth;
                n_button.dynamicX = ratio + " * ${screen_width}";
            }
            if(!Tools.isValidString(n_button.dynamicY) && button.has("y")) {
                double buttonC = button.getDouble("y");
                double ratio = buttonC/CallbackBridge.physicalHeight;
                n_button.dynamicY = ratio + " * ${screen_height}";
            }
            layout.mControlDataList.add(n_button);
        }
        JSONArray layoutDrawerArray = oldLayoutJson.getJSONArray("mDrawerDataList");
        layout.mDrawerDataList = new ArrayList<>();
        for(int i = 0; i < layoutDrawerArray.length(); i++) {
            JSONObject button = layoutDrawerArray.getJSONObject(i);
            JSONObject buttonProperties = button.getJSONObject("properties");
            ControlDrawerData n_button = Tools.GLOBAL_GSON.fromJson(button.toString(), ControlDrawerData.class);
            if(!Tools.isValidString(n_button.properties.dynamicX) && buttonProperties.has("x")) {
                double buttonC = buttonProperties.getDouble("x");
                double ratio = buttonC/CallbackBridge.physicalWidth;
                n_button.properties.dynamicX = ratio + " * ${screen_width}";
            }
            if(!Tools.isValidString(n_button.properties.dynamicY) && buttonProperties.has("y")) {
                double buttonC = buttonProperties.getDouble("y");
                double ratio = buttonC/CallbackBridge.physicalHeight;
                n_button.properties.dynamicY = ratio + " * ${screen_height}";
            }
            layout.mDrawerDataList.add(n_button);
        }
        layout.version = 3;
        return layout;
    }
    public static CustomControls convertV1Layout(JSONObject oldLayoutJson) throws JSONException {
        CustomControls empty = new CustomControls();
        JSONArray layoutMainArray = oldLayoutJson.getJSONArray("mControlDataList");
        for(int i = 0; i < layoutMainArray.length(); i++) {
            JSONObject button = layoutMainArray.getJSONObject(i);
            ControlData n_button = new ControlData();
            int[] keycodes = new int[] {LwjglGlfwKeycode.GLFW_KEY_UNKNOWN,
                    LwjglGlfwKeycode.GLFW_KEY_UNKNOWN,
                    LwjglGlfwKeycode.GLFW_KEY_UNKNOWN,
                    LwjglGlfwKeycode.GLFW_KEY_UNKNOWN};
            n_button.isDynamicBtn = button.getBoolean("isDynamicBtn");
            n_button.dynamicX = button.getString("dynamicX");
            n_button.dynamicY = button.getString("dynamicY");
            if(!Tools.isValidString(n_button.dynamicX) && button.has("x")) {
                double buttonC = button.getDouble("x");
                double ratio = buttonC/CallbackBridge.physicalWidth;
                n_button.dynamicX = ratio + " * ${screen_width}";
            }
            if(!Tools.isValidString(n_button.dynamicY) && button.has("y")) {
                double buttonC = button.getDouble("y");
                double ratio = buttonC/CallbackBridge.physicalHeight;
                n_button.dynamicY = ratio + " * ${screen_height}";
            }
            n_button.name = button.getString("name");
            n_button.opacity = ((float)((button.getInt("transparency")-100)*-1))/100f;
            n_button.passThruEnabled = button.getBoolean("passThruEnabled");
            n_button.isToggle = button.getBoolean("isToggle");
            n_button.setHeight(button.getInt("height"));
            n_button.setWidth(button.getInt("width"));
            n_button.bgColor = 0x4d000000;
            n_button.strokeWidth = 0;
            if(button.getBoolean("isRound")) { n_button.cornerRadius =  35f; }
            int next_idx = 0;
            if(button.getBoolean("holdShift")) { keycodes[next_idx] = LwjglGlfwKeycode.GLFW_KEY_LEFT_SHIFT; next_idx++; }
            if(button.getBoolean("holdCtrl")) { keycodes[next_idx] = LwjglGlfwKeycode.GLFW_KEY_LEFT_CONTROL; next_idx++; }
            if(button.getBoolean("holdAlt")) { keycodes[next_idx] = LwjglGlfwKeycode.GLFW_KEY_LEFT_ALT; next_idx++; }
            keycodes[next_idx] = button.getInt("keycode");
            n_button.keycodes = keycodes;
            empty.mControlDataList.add(n_button);
        }
        empty.scaledAt = (float)oldLayoutJson.getDouble("scaledAt");
        empty.version = 3;
        return empty;
    }
}
