package net.kdt.pojavlaunch.customcontrols;
import android.content.*;
import com.google.gson.*;

import java.io.IOException;
import java.util.*;
import net.kdt.pojavlaunch.*;
import org.lwjgl.glfw.*;

public class CustomControls {
	public int version = -1;
    public float scaledAt;
	public List<ControlData> mControlDataList;
	public List<ControlDrawerData> mDrawerDataList;
	public CustomControls() {
		this(new ArrayList<>(), new ArrayList<>());
	}



	public CustomControls(List<ControlData> mControlDataList, List<ControlDrawerData> mDrawerDataList) {
		this.mControlDataList = mControlDataList;
		this.mDrawerDataList = mDrawerDataList;
        this.scaledAt = 100f;
	}
	
	// Generate default control
	public CustomControls(Context ctx) {
		this();
		this.mControlDataList.add(ControlData.getSpecialButtons()[0].clone()); // Keyboard
		this.mControlDataList.add(ControlData.getSpecialButtons()[1].clone()); // GUI
		this.mControlDataList.add(ControlData.getSpecialButtons()[2].clone()); // Primary Mouse mControlDataList
		this.mControlDataList.add(ControlData.getSpecialButtons()[3].clone()); // Secondary Mouse mControlDataList
		this.mControlDataList.add(ControlData.getSpecialButtons()[4].clone()); // Virtual mouse toggle

		this.mControlDataList.add(new ControlData(ctx, R.string.control_debug, new int[]{LWJGLGLFWKeycode.GLFW_KEY_F3}, "${margin}", "${margin}", false));
		this.mControlDataList.add(new ControlData(ctx, R.string.control_chat, new int[]{LWJGLGLFWKeycode.GLFW_KEY_T}, "${margin} * 2 + ${width}", "${margin}", false));
		this.mControlDataList.add(new ControlData(ctx, R.string.control_listplayers, new int[]{LWJGLGLFWKeycode.GLFW_KEY_TAB}, "${margin} * 4 + ${width} * 3", "${margin}", false));
		this.mControlDataList.add(new ControlData(ctx, R.string.control_thirdperson, new int[]{LWJGLGLFWKeycode.GLFW_KEY_F5}, "${margin}", "${height} + ${margin}", false));

		this.mControlDataList.add(new ControlData(ctx, R.string.control_up, new int[]{LWJGLGLFWKeycode.GLFW_KEY_W}, "${margin} * 2 + ${width}", "${bottom} - ${margin} * 3 - ${height} * 2", true));
		this.mControlDataList.add(new ControlData(ctx, R.string.control_left, new int[]{LWJGLGLFWKeycode.GLFW_KEY_A}, "${margin}", "${bottom} - ${margin} * 2 - ${height}", true));
		this.mControlDataList.add(new ControlData(ctx, R.string.control_down, new int[]{LWJGLGLFWKeycode.GLFW_KEY_S}, "${margin} * 2 + ${width}", "${bottom} - ${margin}", true));
		this.mControlDataList.add(new ControlData(ctx, R.string.control_right, new int[]{LWJGLGLFWKeycode.GLFW_KEY_D}, "${margin} * 3 + ${width} * 2", "${bottom} - ${margin} * 2 - ${height}", true));

		this.mControlDataList.add(new ControlData(ctx, R.string.control_inventory, new int[]{LWJGLGLFWKeycode.GLFW_KEY_E}, "${margin} * 3 + ${width} * 2", "${bottom} - ${margin}", true));
        
        ControlData shiftData = new ControlData(ctx, R.string.control_shift, new int[]{LWJGLGLFWKeycode.GLFW_KEY_LEFT_SHIFT}, "${margin} * 2 + ${width}", "${screen_height} - ${margin} * 2 - ${height} * 2", true);
        shiftData.isToggle = true;
		this.mControlDataList.add(shiftData);
		this.mControlDataList.add(new ControlData(ctx, R.string.control_jump, new int[]{LWJGLGLFWKeycode.GLFW_KEY_SPACE}, "${right} - ${margin} * 2 - ${width}", "${bottom} - ${margin} * 2 - ${height}", true));

		//The default controls are conform to the V2
		version = 2;
	}
    
    public ControlData findControlData(int keycode) {
        for (ControlData data : mControlDataList) {
        	for(int dataKeycode : data.keycodes){
				if (dataKeycode == keycode) {
					return data;
				}
			}
        }
        return null;
    }
	
	public void save(String path) throws IOException {
		//Current version is the V2 so the version as to be marked as 2 !
		version = 2;

		Tools.write(path, Tools.GLOBAL_GSON.toJson(this));
	}
}
