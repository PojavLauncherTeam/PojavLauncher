package net.kdt.pojavlaunch.customcontrols;

import android.util.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.utils.*;
import net.objecthunter.exp4j.*;
import org.lwjgl.glfw.*;

public class ControlData implements Cloneable
{
	public static int pixelOf2dp;
	public static int pixelOf30dp;
	public static int pixelOf50dp;
	public static int pixelOf80dp;

	public static final int SPECIALBTN_KEYBOARD = -1;
	public static final int SPECIALBTN_TOGGLECTRL = -2;
	public static final int SPECIALBTN_MOUSEPRI = -3;
	public static final int SPECIALBTN_MOUSESEC = -4;
	public static final int SPECIALBTN_VIRTUALMOUSE = -5;
	public static final int SPECIALBTN_MOUSEMID = -6;
	
	private static ControlData[] SPECIAL_BUTTONS;
	private static String[] SPECIAL_BUTTON_NAME_ARRAY;

    // Internal usage only
    public boolean isHideable;
    
    /**
     * Both fields below are dynamic position data, auto updates
     * X and Y position, unlike the original one which uses fixed
     * position, so it does not provide auto-location when a control
     * is made on a small device, then import the control to a
     * bigger device or vice versa.
     */
    public String dynamicX, dynamicY;
    public boolean isDynamicBtn;
    
	public static ControlData[] getSpecialButtons(){
		if (SPECIAL_BUTTONS == null) {
			ControlData[] specialButtons = new ControlData[]{
				new ControlData("Keyboard", SPECIALBTN_KEYBOARD, "${margin} * 3 + ${width} * 2", "${margin}", false),
				new ControlData("GUI", SPECIALBTN_TOGGLECTRL, "${margin}", "${bottom} - ${margin}"),
				new ControlData("PRI", SPECIALBTN_MOUSEPRI, "${margin}", "${screen_height} - ${margin} * 3 - ${height} * 3"),
				new ControlData("SEC", SPECIALBTN_MOUSESEC, "${margin} * 3 + ${width} * 2", "${screen_height} - ${margin} * 3 - ${height} * 3"),
				new ControlData("Mouse", SPECIALBTN_VIRTUALMOUSE, "${right}", "${margin}", false),
                
                new ControlData("MID", SPECIALBTN_MOUSEMID, "${margin}", "${margin}")
			};
            SPECIAL_BUTTONS = specialButtons;
		}

		return SPECIAL_BUTTONS;
	}

	public static String[] buildSpecialButtonArray() {
		if (SPECIAL_BUTTON_NAME_ARRAY == null) {
			List<String> nameList = new ArrayList<String>();
			for (ControlData btn : getSpecialButtons()) {
				nameList.add(btn.name);
			}
			SPECIAL_BUTTON_NAME_ARRAY = nameList.toArray(new String[0]);
		}

		return SPECIAL_BUTTON_NAME_ARRAY;
	}

	public String name;
	public float x;
	public float y;
	public float width = pixelOf50dp;
	public float height = pixelOf50dp;
	public int keycode;
	public boolean hidden;
	public boolean holdCtrl;
	public boolean holdAlt;
	public boolean holdShift;
	public /* View.OnClickListener */ Object specialButtonListener;
	// public boolean hold

	public ControlData() {
		this("", LWJGLGLFWKeycode.GLFW_KEY_UNKNOWN, 0, 0);
	}

	public ControlData(String name, int keycode) {
		this(name, keycode, 0, 0);
	}

	public ControlData(String name, int keycode, float x, float y) {
		this(name, keycode, x, y, pixelOf50dp, pixelOf50dp);
	}

	public ControlData(android.content.Context ctx, int resId, int keycode, float x, float y, boolean isSquare) {
		this(ctx.getResources().getString(resId), keycode, x, y, isSquare);
	}

	public ControlData(String name, int keycode, float x, float y, boolean isSquare) {
		this(name, keycode, x, y, isSquare ? pixelOf50dp : pixelOf80dp, isSquare ? pixelOf50dp : pixelOf30dp);
	}

	public ControlData(String name, int keycode, float x, float y, float width, float height) {
		this.name = name;
		this.keycode = keycode;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
        this.isDynamicBtn = false;
	}

    public ControlData(String name, int keycode, String dynamicX, String dynamicY) {
        this(name, keycode, dynamicX, dynamicY, pixelOf50dp, pixelOf50dp);
    }

    public ControlData(android.content.Context ctx, int resId, int keycode, String dynamicX, String dynamicY, boolean isSquare) {
        this(ctx.getResources().getString(resId), keycode, dynamicX, dynamicY, isSquare);
    }

    public ControlData(String name, int keycode, String dynamicX, String dynamicY, boolean isSquare) {
        this(name, keycode, dynamicX, dynamicY, isSquare ? pixelOf50dp : pixelOf80dp, isSquare ? pixelOf50dp : pixelOf30dp);
    }

    public ControlData(String name, int keycode, String dynamicX, String dynamicY, float width, float height) {
        this(name, keycode, 0, 0, width, height);
        this.dynamicX = dynamicX;
        this.dynamicY = dynamicY;
        this.isDynamicBtn = true;
        update();
    }
    
	public void execute(BaseMainActivity act, boolean isDown) {
		act.sendKeyPress(keycode, 0, isDown);
	}

	public ControlData clone() {
        if (this instanceof ControlData) {
            return new ControlData(name, keycode, ((ControlData) this).dynamicX, ((ControlData) this).dynamicY, width, height);
        } else {
            return new ControlData(name, keycode, x, y, width, height);
        }
	}
    
    public float insertDynamicPos(String dynamicPos) {
        // Values in the map below may be always changed
        Map<String, String> keyValueMap = new ArrayMap<>();
        keyValueMap.put("top", "0");
        keyValueMap.put("left", "0");
        keyValueMap.put("right", Float.toString(CallbackBridge.windowWidth - width));
        keyValueMap.put("bottom", Float.toString(CallbackBridge.windowHeight - height));
        keyValueMap.put("width", Float.toString(width));
        keyValueMap.put("height", Float.toString(height));
        keyValueMap.put("screen_width", Integer.toString(CallbackBridge.windowWidth));
        keyValueMap.put("screen_height", Integer.toString(CallbackBridge.windowHeight));
        keyValueMap.put("margin", Integer.toString(pixelOf2dp));
        
        // Insert value to ${variable}
        String insertedPos = JSONUtils.insertSingleJSONValue(dynamicPos, keyValueMap);
        
        // Calculate, because the dynamic position contains some math equations
        return calculate(insertedPos);
    }
    
    public void update() {
        if (keycode < 0 && SPECIAL_BUTTONS != null) {
            for (ControlData data : getSpecialButtons()) {
                if (keycode == data.keycode) {
                    specialButtonListener = data.specialButtonListener;
                }
            }
        } if (!isDynamicBtn) {
            return;
        }
        
        x = insertDynamicPos(dynamicX);
        y = insertDynamicPos(dynamicY);
    }

    private static float calculate(String math) {
        return (float) new ExpressionBuilder(math).build().evaluate();
    }
}
