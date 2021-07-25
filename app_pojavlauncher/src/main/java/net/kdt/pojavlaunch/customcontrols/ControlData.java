package net.kdt.pojavlaunch.customcontrols;

import android.util.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.utils.*;
import net.objecthunter.exp4j.*;
import org.lwjgl.glfw.*;

import static net.kdt.pojavlaunch.LWJGLGLFWKeycode.GLFW_KEY_UNKNOWN;

public class ControlData implements Cloneable
{

    public static final int SPECIALBTN_KEYBOARD = -1;
    public static final int SPECIALBTN_TOGGLECTRL = -2;
    public static final int SPECIALBTN_MOUSEPRI = -3;
    public static final int SPECIALBTN_MOUSESEC = -4;
    public static final int SPECIALBTN_VIRTUALMOUSE = -5;
    public static final int SPECIALBTN_MOUSEMID = -6;
    public static final int SPECIALBTN_SCROLLUP = -7;
    public static final int SPECIALBTN_SCROLLDOWN = -8;
    
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
    public boolean isDynamicBtn, isToggle, passThruEnabled;
    
    public static ControlData[] getSpecialButtons(){
        if (SPECIAL_BUTTONS == null) {
            SPECIAL_BUTTONS = new ControlData[]{
                new ControlData("Keyboard", new int[]{SPECIALBTN_KEYBOARD}, "${margin} * 3 + ${width} * 2", "${margin}", false),
                new ControlData("GUI", new int[]{SPECIALBTN_TOGGLECTRL}, "${margin}", "${bottom} - ${margin}"),
                new ControlData("PRI", new int[]{SPECIALBTN_MOUSEPRI}, "${margin}", "${screen_height} - ${margin} * 3 - ${height} * 3"),
                new ControlData("SEC", new int[]{SPECIALBTN_MOUSESEC}, "${margin} * 3 + ${width} * 2", "${screen_height} - ${margin} * 3 - ${height} * 3"),
                new ControlData("Mouse", new int[]{SPECIALBTN_VIRTUALMOUSE}, "${right}", "${margin}", false),

                new ControlData("MID", new int[]{SPECIALBTN_MOUSEMID}, "${margin}", "${margin}"),
                new ControlData("SCROLLUP", new int[]{SPECIALBTN_SCROLLUP}, "${margin}", "${margin}"),
                new ControlData("SCROLLDOWN", new int[]{SPECIALBTN_SCROLLDOWN}, "${margin}", "${margin}")
            };
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
    private float width;         //Dp instead of Px now
    private float height;        //Dp instead of Px now
    public int[] keycodes;      //Should store up to 4 keys
    public float opacity;       //Alpha value from 0 to 1;
    public int bgColor;
    public int strokeColor;
    public int strokeWidth;     //0-100%
    public float cornerRadius;  //0-100%
    public boolean isSwipeable;

    public Object specialButtonListener;

    public ControlData() {
        this("button");
    }

    public ControlData(String name){
        this(name, new int[] {});
    }

    public ControlData(String name, int[] keycodes) {
        this(name, keycodes, Tools.currentDisplayMetrics.widthPixels/2, Tools.currentDisplayMetrics.heightPixels/2);
    }

    public ControlData(String name, int[] keycodes, float x, float y) {
        this(name, keycodes, x, y, 50, 50);
    }

    public ControlData(android.content.Context ctx, int resId, int[] keycodes, float x, float y, boolean isSquare) {
        this(ctx.getResources().getString(resId), keycodes, x, y, isSquare);
    }

    public ControlData(String name, int[] keycodes, float x, float y, boolean isSquare) {
        this(name, keycodes, x, y, isSquare ? 50 : 80, isSquare ? 50 : 30);
    }

    public ControlData(String name, int[] keycodes, float x, float y, float width, float height) {
        this(name, keycodes, Float.toString(x), Float.toString(y), width, height, false);
        this.isDynamicBtn = false;
    }

    public ControlData(String name, int[] keycodes, String dynamicX, String dynamicY) {
        this(name, keycodes, dynamicX, dynamicY, 50, 50, false);
    }

    public ControlData(android.content.Context ctx, int resId, int[] keycodes, String dynamicX, String dynamicY, boolean isSquare) {
        this(ctx.getResources().getString(resId), keycodes, dynamicX, dynamicY, isSquare);
    }

    public ControlData(String name, int[] keycodes, String dynamicX, String dynamicY, boolean isSquare) {
        this(name, keycodes, dynamicX, dynamicY, isSquare ? 50 : 80, isSquare ? 50 : 30, false);
    }

    public ControlData(String name, int[] keycodes, String dynamicX, String dynamicY, float width, float height, boolean isToggle){
        this(name, keycodes, dynamicX, dynamicY, width, height, isToggle, 1,0x4D000000, 0xFFFFFFFF,0,0);
    }

    public ControlData(String name, int[] keycodes, String dynamicX, String dynamicY, float width, float height, boolean isToggle, float opacity, int bgColor, int strokeColor, int strokeWidth, float cornerRadius) {
        this.name = name;
        this.keycodes = inflateKeycodeArray(keycodes);
        this.dynamicX = dynamicX;
        this.dynamicY = dynamicY;
        this.width = width;
        this.height = height;
        this.isDynamicBtn = true;
        this.isToggle = isToggle;
        this.opacity = opacity;
        this.bgColor = bgColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.cornerRadius = cornerRadius;
        update();
    }
    
    public void execute(boolean isDown) {
        for(int keycode : keycodes){
            BaseMainActivity.sendKeyPress(keycode, 0, isDown);
        }
    }


    public ControlData clone() {
        return new ControlData(name, keycodes, dynamicX, dynamicY, width, height, isToggle, opacity, bgColor, strokeColor,strokeWidth, cornerRadius);
    }
    
    public float insertDynamicPos(String dynamicPos) {
        // Values in the map below may be always changed
        Map<String, String> keyValueMap = new ArrayMap<>();
        keyValueMap.put("top", "0");
        keyValueMap.put("left", "0");
        keyValueMap.put("right", Float.toString(CallbackBridge.physicalWidth - getWidth()));
        keyValueMap.put("bottom", Float.toString(CallbackBridge.physicalHeight - getHeight()));
        keyValueMap.put("width", Float.toString(getWidth()));
        keyValueMap.put("height", Float.toString(getHeight()));
        keyValueMap.put("screen_width", Integer.toString(CallbackBridge.physicalWidth));
        keyValueMap.put("screen_height", Integer.toString(CallbackBridge.physicalHeight));
        keyValueMap.put("margin", Integer.toString((int) Tools.dpToPx(2)));
        
        // Insert value to ${variable}
        String insertedPos = JSONUtils.insertSingleJSONValue(dynamicPos, keyValueMap);
        
        // Calculate, because the dynamic position contains some math equations
        return calculate(insertedPos);
    }
    
    public void update() {
        if(SPECIAL_BUTTONS != null)
            for(int keycode : keycodes)
                for (ControlData data : getSpecialButtons())
                    if (keycode == data.keycodes[0])
                        specialButtonListener = data.specialButtonListener;


        if (dynamicX == null) {
            dynamicX = Float.toString(x);
        }
        if (dynamicY == null) {
            dynamicY = Float.toString(y);
        }
        
        x = insertDynamicPos(dynamicX);
        y = insertDynamicPos(dynamicY);
    }

    private static float calculate(String math) {
        return (float) new ExpressionBuilder(math).build().evaluate();
    }

    private static int[] inflateKeycodeArray(int[] keycodes){
        int[] inflatedArray = new int[]{GLFW_KEY_UNKNOWN, GLFW_KEY_UNKNOWN, GLFW_KEY_UNKNOWN, GLFW_KEY_UNKNOWN};
        System.arraycopy(keycodes, 0, inflatedArray, 0, keycodes.length);
        return inflatedArray;
    }


    public boolean containsKeycode(int keycodeToCheck){
        for(int keycode : keycodes)
            if(keycodeToCheck == keycode)
                return true;

        return false;
    }

    //Getters || setters (with conversion for ease of use)
    public float getWidth() {
        return Tools.dpToPx(width);
    }

    public float getHeight(){
        return Tools.dpToPx(height);
    }


    public void setWidth(float widthInPx){
        width = Tools.pxToDp(widthInPx);
    }

    public void setHeight(float heightInPx){
        height = Tools.pxToDp(heightInPx);
    }
}
