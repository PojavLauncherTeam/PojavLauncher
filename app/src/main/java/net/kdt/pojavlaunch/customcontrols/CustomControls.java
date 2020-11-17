package net.kdt.pojavlaunch.customcontrols;
import android.content.*;
import com.google.gson.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import org.lwjgl.glfw.*;

public class CustomControls
{
	public List<ControlData> button;
	public CustomControls() {
		this(new ArrayList<ControlData>());
	}
	
	public CustomControls(List<ControlData> button) {
		this.button = button;
	}
	
	// Generate default control
	public CustomControls(Context ctx) {
		this();
		this.button.add(ControlData.getSpecialButtons()[0].clone()); // Keyboard
		this.button.add(ControlData.getSpecialButtons()[1].clone()); // GUI
		this.button.add(ControlData.getSpecialButtons()[2].clone()); // Primary Mouse button
		this.button.add(ControlData.getSpecialButtons()[3].clone()); // Secondary Mouse button
		this.button.add(ControlData.getSpecialButtons()[4].clone()); // Virtual mouse toggle

		this.button.add(new DynamicControlData(ctx, R.string.control_debug, LWJGLGLFWKeycode.GLFW_KEY_F3, "${margin}", "${margin}", false));
		this.button.add(new DynamicControlData(ctx, R.string.control_chat, LWJGLGLFWKeycode.GLFW_KEY_T, "${margin} * 2 + ${width}", "${margin}", false)); 
		this.button.add(new DynamicControlData(ctx, R.string.control_listplayers, LWJGLGLFWKeycode.GLFW_KEY_TAB, "${margin} * 4 + ${width} * 3", "${margin}", false));
		this.button.add(new DynamicControlData(ctx, R.string.control_thirdperson, LWJGLGLFWKeycode.GLFW_KEY_F5, "${margin}", "${height} + ${margin}", false));

		this.button.add(new DynamicControlData(ctx, R.string.control_up, LWJGLGLFWKeycode.GLFW_KEY_W, "${margin} * 2 + ${width}", "${screen_height} - ${margin} * 3 - ${height} * 3", true));
		this.button.add(new DynamicControlData(ctx, R.string.control_left, LWJGLGLFWKeycode.GLFW_KEY_A, "${margin}", "${screen_height} - ${margin} * 2 - ${height} * 2", true)); 
		this.button.add(new DynamicControlData(ctx, R.string.control_down, LWJGLGLFWKeycode.GLFW_KEY_S, "${margin} * 2 + ${width}", "${screen_height} - ${margin} - ${width}", true));
		this.button.add(new DynamicControlData(ctx, R.string.control_right, LWJGLGLFWKeycode.GLFW_KEY_D, "${margin} * 3 + ${width} * 2", "${screen_height} - ${margin} * 2 - ${width} * 2", true));

		this.button.add(new DynamicControlData(ctx, R.string.control_inventory, LWJGLGLFWKeycode.GLFW_KEY_E, "${margin} * 3 + ${width} * 2", "${screen_height} - ${margin} - ${width}", true));
		this.button.add(new DynamicControlData(ctx, R.string.control_shift, LWJGLGLFWKeycode.GLFW_KEY_LEFT_SHIFT, "${margin} * 2 + ${width}", "${screen_height} - ${margin} * 2 - ${width} * 2", true));
		this.button.add(new DynamicControlData(ctx, R.string.control_jump, LWJGLGLFWKeycode.GLFW_KEY_SPACE, "${screen_width} - ${margin} * 3 - ${width} * 2", "${screen_height} - ${margin} * 2 - ${width} * 2", true));
		
	}
	
	public void save(String path) throws Exception {
		Tools.write(path, Tools.GLOBAL_GSON.toJson(this));
	}
}
