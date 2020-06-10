package net.kdt.pojavlaunch.value.customcontrols;
import java.util.*;
import net.kdt.pojavlaunch.*;
import com.google.gson.*;
import android.content.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.AndroidDisplay;

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
		this.button.add(ControlButton.getSpecialButtons()[0].clone()); // Keyboard
		this.button.add(ControlButton.getSpecialButtons()[1].clone()); // GUI
		this.button.add(ControlButton.getSpecialButtons()[2].clone()); // Primary Mouse button
		this.button.add(ControlButton.getSpecialButtons()[3].clone()); // Secondary Mouse button
		this.button.add(ControlButton.getSpecialButtons()[4].clone()); // Virtual mouse toggle

		this.button.add(new ControlButton(ctx, R.string.control_debug, Keyboard.KEY_F3, ControlButton.pixelOf2dp, ControlButton.pixelOf2dp, false));
		this.button.add(new ControlButton(ctx, R.string.control_chat, Keyboard.KEY_T, ControlButton.pixelOf2dp * 2 + ControlButton.pixelOf80dp, ControlButton.pixelOf2dp, false)); 
		this.button.add(new ControlButton(ctx, R.string.control_listplayers, Keyboard.KEY_TAB, ControlButton.pixelOf2dp * 4 + ControlButton.pixelOf80dp * 3, ControlButton.pixelOf2dp, false));
		this.button.add(new ControlButton(ctx, R.string.control_thirdperson, Keyboard.KEY_F5, ControlButton.pixelOf2dp, ControlButton.pixelOf30dp + ControlButton.pixelOf2dp, false));

		this.button.add(new ControlButton(ctx, R.string.control_up, Keyboard.KEY_W, ControlButton.pixelOf2dp * 2 + ControlButton.pixelOf50dp, AndroidDisplay.windowHeight - ControlButton.pixelOf2dp * 3 - ControlButton.pixelOf50dp * 3, true));
		this.button.add(new ControlButton(ctx, R.string.control_left, Keyboard.KEY_A, ControlButton.pixelOf2dp, AndroidDisplay.windowHeight - ControlButton.pixelOf2dp * 2 - ControlButton.pixelOf50dp * 2, true)); 
		this.button.add(new ControlButton(ctx, R.string.control_down, Keyboard.KEY_S, ControlButton.pixelOf2dp * 2 + ControlButton.pixelOf50dp, AndroidDisplay.windowHeight - ControlButton.pixelOf2dp - ControlButton.pixelOf50dp, true));
		this.button.add(new ControlButton(ctx, R.string.control_right, Keyboard.KEY_D, ControlButton.pixelOf2dp * 3 + ControlButton.pixelOf50dp * 2, AndroidDisplay.windowHeight - ControlButton.pixelOf2dp * 2 - ControlButton.pixelOf50dp * 2, true));

		this.button.add(new ControlButton(ctx, R.string.control_inventory, Keyboard.KEY_E, ControlButton.pixelOf2dp * 3 + ControlButton.pixelOf50dp * 2, AndroidDisplay.windowHeight - ControlButton.pixelOf2dp - ControlButton.pixelOf50dp, true));
		this.button.add(new ControlButton(ctx, R.string.control_shift, Keyboard.KEY_LSHIFT, ControlButton.pixelOf2dp * 2 + ControlButton.pixelOf50dp, AndroidDisplay.windowHeight - ControlButton.pixelOf2dp * 2 - ControlButton.pixelOf50dp * 2, true));
		this.button.add(new ControlButton(ctx, R.string.control_jump, Keyboard.KEY_SPACE, AndroidDisplay.windowWidth - ControlButton.pixelOf2dp * 3 - ControlButton.pixelOf50dp * 2, AndroidDisplay.windowHeight - ControlButton.pixelOf2dp * 2 - ControlButton.pixelOf50dp * 2, true));
		
	}
	
	public void save(String path) throws Exception {
		Tools.write(path, new Gson().toJson(this));
	}
}
