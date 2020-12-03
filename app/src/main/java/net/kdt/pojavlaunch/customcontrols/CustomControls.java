package net.kdt.pojavlaunch.customcontrols;
import android.content.*;
import com.google.gson.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import org.lwjgl.glfw.*;

public class CustomControls
{
    public float scaledAt;
	public List<ControlData> mControlDataList;
	public CustomControls() {
		this(new ArrayList<ControlData>());
	}
	
	public CustomControls(List<ControlData> mControlDataList) {
		this.mControlDataList = mControlDataList;
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

		this.mControlDataList.add(new ControlData(ctx, R.string.control_debug, LWJGLGLFWKeycode.GLFW_KEY_F3, "${margin}", "${margin}", false));
		this.mControlDataList.add(new ControlData(ctx, R.string.control_chat, LWJGLGLFWKeycode.GLFW_KEY_T, "${margin} * 2 + ${width}", "${margin}", false)); 
		this.mControlDataList.add(new ControlData(ctx, R.string.control_listplayers, LWJGLGLFWKeycode.GLFW_KEY_TAB, "${margin} * 4 + ${width} * 3", "${margin}", false));
		this.mControlDataList.add(new ControlData(ctx, R.string.control_thirdperson, LWJGLGLFWKeycode.GLFW_KEY_F5, "${margin}", "${height} + ${margin}", false));

		this.mControlDataList.add(new ControlData(ctx, R.string.control_up, LWJGLGLFWKeycode.GLFW_KEY_W, "${margin} * 2 + ${width}", "${bottom} - ${margin} * 3 - ${height} * 2", true));
		this.mControlDataList.add(new ControlData(ctx, R.string.control_left, LWJGLGLFWKeycode.GLFW_KEY_A, "${margin}", "${bottom} - ${margin} * 2 - ${height}", true)); 
		this.mControlDataList.add(new ControlData(ctx, R.string.control_down, LWJGLGLFWKeycode.GLFW_KEY_S, "${margin} * 2 + ${width}", "${bottom} - ${margin}", true));
		this.mControlDataList.add(new ControlData(ctx, R.string.control_right, LWJGLGLFWKeycode.GLFW_KEY_D, "${margin} * 3 + ${width} * 2", "${bottom} - ${margin} * 2 - ${height}", true));

		this.mControlDataList.add(new ControlData(ctx, R.string.control_inventory, LWJGLGLFWKeycode.GLFW_KEY_E, "${margin} * 3 + ${width} * 2", "${bottom} - ${margin}", true));
        
        ControlData shiftData = new ControlData(ctx, R.string.control_shift, LWJGLGLFWKeycode.GLFW_KEY_LEFT_SHIFT, "${margin} * 2 + ${width}", "${screen_height} - ${margin} * 2 - ${height} * 2", true);
        shiftData.isToggle = true;
		this.mControlDataList.add(shiftData);
		this.mControlDataList.add(new ControlData(ctx, R.string.control_jump, LWJGLGLFWKeycode.GLFW_KEY_SPACE, "${right} - ${margin} * 2 - ${width}", "${bottom} - ${margin} * 2 - ${height}", true));
		
	}
    
    public ControlData findControlData(int keycode) {
        for (ControlData data : mControlDataList) {
            if (data.keycode == keycode) {
                return data;
            }
        }
        return null;
    }
	
	public void save(String path) throws Exception {
		Tools.write(path, Tools.GLOBAL_GSON.toJson(this));
	}
}
