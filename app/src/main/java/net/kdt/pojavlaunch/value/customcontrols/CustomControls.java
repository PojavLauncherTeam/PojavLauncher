package net.kdt.pojavlaunch.value.customcontrols;
import java.util.*;
import net.kdt.pojavlaunch.*;
import com.google.gson.*;
import android.content.*;

public class CustomControls
{
	public List<ControlButton> button;
	public CustomControls() {
		this(new ArrayList<ControlButton>());
	}
	
	public CustomControls(List<ControlButton> button) {
		this.button = button;
	}
	
	// Generate default control
	public CustomControls(Context ctx) {
		this();
		this.button.add(ControlButton.getSpecialButtons()[0].clone()); // LWJGLGLFWKeycode
		this.button.add(ControlButton.getSpecialButtons()[1].clone()); // GUI
		this.button.add(ControlButton.getSpecialButtons()[2].clone()); // Primary Mouse button
		this.button.add(ControlButton.getSpecialButtons()[3].clone()); // Secondary Mouse button
		this.button.add(ControlButton.getSpecialButtons()[4].clone()); // Virtual mouse toggle

		this.button.add(new ControlButton(ctx, R.string.control_debug, LWJGLGLFWKeycode.GLFW_KEY_F3, ControlButton.pixelOf2dp, ControlButton.pixelOf2dp, false));
		this.button.add(new ControlButton(ctx, R.string.control_chat, LWJGLGLFWKeycode.GLFW_KEY_T, ControlButton.pixelOf2dp * 2 + ControlButton.pixelOf80dp, ControlButton.pixelOf2dp, false)); 
		this.button.add(new ControlButton(ctx, R.string.control_listplayers, LWJGLGLFWKeycode.GLFW_KEY_TAB, ControlButton.pixelOf2dp * 4 + ControlButton.pixelOf80dp * 3, ControlButton.pixelOf2dp, false));
		this.button.add(new ControlButton(ctx, R.string.control_thirdperson, LWJGLGLFWKeycode.GLFW_KEY_F5, ControlButton.pixelOf2dp, ControlButton.pixelOf30dp + ControlButton.pixelOf2dp, false));

		this.button.add(new ControlButton(ctx, R.string.control_up, LWJGLGLFWKeycode.GLFW_KEY_W, ControlButton.pixelOf2dp * 2 + ControlButton.pixelOf50dp, LWJGLInputSender.windowHeight - ControlButton.pixelOf2dp * 3 - ControlButton.pixelOf50dp * 3, true));
		this.button.add(new ControlButton(ctx, R.string.control_left, LWJGLGLFWKeycode.GLFW_KEY_A, ControlButton.pixelOf2dp, LWJGLInputSender.windowHeight - ControlButton.pixelOf2dp * 2 - ControlButton.pixelOf50dp * 2, true)); 
		this.button.add(new ControlButton(ctx, R.string.control_down, LWJGLGLFWKeycode.GLFW_KEY_S, ControlButton.pixelOf2dp * 2 + ControlButton.pixelOf50dp, LWJGLInputSender.windowHeight - ControlButton.pixelOf2dp - ControlButton.pixelOf50dp, true));
		this.button.add(new ControlButton(ctx, R.string.control_right, LWJGLGLFWKeycode.GLFW_KEY_D, ControlButton.pixelOf2dp * 3 + ControlButton.pixelOf50dp * 2, LWJGLInputSender.windowHeight - ControlButton.pixelOf2dp * 2 - ControlButton.pixelOf50dp * 2, true));

		this.button.add(new ControlButton(ctx, R.string.control_inventory, LWJGLGLFWKeycode.GLFW_KEY_E, ControlButton.pixelOf2dp * 3 + ControlButton.pixelOf50dp * 2, LWJGLInputSender.windowHeight - ControlButton.pixelOf2dp - ControlButton.pixelOf50dp, true));
		this.button.add(new ControlButton(ctx, R.string.control_shift, LWJGLGLFWKeycode.GLFW_KEY_LEFT_SHIFT, ControlButton.pixelOf2dp * 2 + ControlButton.pixelOf50dp, LWJGLInputSender.windowHeight - ControlButton.pixelOf2dp * 2 - ControlButton.pixelOf50dp * 2, true));
		this.button.add(new ControlButton(ctx, R.string.control_jump, LWJGLGLFWKeycode.GLFW_KEY_SPACE, LWJGLInputSender.windowWidth - ControlButton.pixelOf2dp * 3 - ControlButton.pixelOf50dp * 2, LWJGLInputSender.windowHeight - ControlButton.pixelOf2dp * 2 - ControlButton.pixelOf50dp * 2, true));
		
	}
	
	public void save(String path) throws Exception {
		Tools.write(path, new Gson().toJson(this));
	}
}
