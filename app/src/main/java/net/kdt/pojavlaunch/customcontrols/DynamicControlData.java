package net.kdt.pojavlaunch.customcontrols;

import android.util.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.utils.*;
import net.objecthunter.exp4j.*;
import org.lwjgl.glfw.*;

public class DynamicControlData extends ControlData {
    /**
     * The DynamicControlData is a ControlData that uses dynamic
     * X and Y position, unlike the original one which uses fixed
     * position, so it does not provide autoscale when a control
     * is made on a small device, then import the control to a
     * bigger device or vice versa.
     */
    
    public String dynamicX, dynamicY;
    
    public DynamicControlData() {
        this("", LWJGLGLFWKeycode.GLFW_KEY_UNKNOWN);
    }

    public DynamicControlData(String name, int keycode) {
        this(name, keycode, "0", "0");
    }

    public DynamicControlData(String name, int keycode, String dynamicX, String dynamicY) {
        this(name, keycode, dynamicX, dynamicY, pixelOf50dp, pixelOf50dp);
    }

    public DynamicControlData(android.content.Context ctx, int resId, int keycode, String dynamicX, String dynamicY, boolean isSquare) {
        this(ctx.getResources().getString(resId), keycode, dynamicX, dynamicY, isSquare);
    }

    public DynamicControlData(String name, int keycode, String dynamicX, String dynamicY, boolean isSquare) {
        this(name, keycode, dynamicX, dynamicY, isSquare ? pixelOf50dp : pixelOf80dp, isSquare ? pixelOf50dp : pixelOf30dp);
    }

    public DynamicControlData(String name, int keycode, String dynamicX, String dynamicY, int width, int height) {
        super(name, keycode, 0, 0, width, height);
        this.dynamicX = dynamicX;
        this.dynamicY = dynamicY;
        update();
	}
    
    public void update() {
        // Values in the map below may be always changed
        Map<String, String> keyValueMap = new ArrayMap<>();
        keyValueMap.put("top", "0");
        keyValueMap.put("left", "0");
        keyValueMap.put("right", Integer.toString(CallbackBridge.windowWidth - width));
        keyValueMap.put("bottom", Integer.toString(CallbackBridge.windowHeight - height));
        keyValueMap.put("width", Integer.toString(width));
        keyValueMap.put("height", Integer.toString(height));
        keyValueMap.put("screen_width", Integer.toString(CallbackBridge.windowWidth));
        keyValueMap.put("screen_height", Integer.toString(CallbackBridge.windowHeight));
        keyValueMap.put("margin", Integer.toString(pixelOf2dp));
        
        // Insert JSON values to variables
        String insertedX = JSONUtils.insertSingleJSONValue(dynamicX, keyValueMap);
        String insertedY = JSONUtils.insertSingleJSONValue(dynamicY, keyValueMap);
        
        // Calculate and save, because the dynamic position contains some math equations
        x = calculate(insertedX);
        y = calculate(insertedY);
    }
    
    private static int calculate(String math) {
        // try {
            return (int) new ExpressionBuilder(math).build().evaluate();
        /* } catch (e) {
            
        } */
    }
}
