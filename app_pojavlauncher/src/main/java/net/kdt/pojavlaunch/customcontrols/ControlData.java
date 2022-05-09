package net.kdt.pojavlaunch.customcontrols;

import android.util.*;

import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;
import java.util.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.utils.*;
import net.objecthunter.exp4j.*;
import net.objecthunter.exp4j.function.Function;

import org.lwjgl.glfw.*;

import static net.kdt.pojavlaunch.LwjglGlfwKeycode.GLFW_KEY_UNKNOWN;
import static org.lwjgl.glfw.CallbackBridge.sendKeyPress;

import androidx.annotation.Keep;

@Keep
public class ControlData {

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

    private static WeakReference<ExpressionBuilder> builder = new WeakReference<>(null);
    private static WeakReference<ArrayMap<String , String>> conversionMap = new WeakReference<>(null);
    static {
        buildExpressionBuilder();
        buildConversionMap();
    }


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
    private float width;         //Dp instead of Px now
    private float height;        //Dp instead of Px now
    public int[] keycodes;      //Should store up to 4 keys
    public float opacity;       //Alpha value from 0 to 1;
    public int bgColor;
    public int strokeColor;
    public int strokeWidth;     //0-100%
    public float cornerRadius;  //0-100%
    public boolean isSwipeable;

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
        this.isDynamicBtn = false;
        this.isToggle = isToggle;
        this.opacity = opacity;
        this.bgColor = bgColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.cornerRadius = cornerRadius;
    }

    //Deep copy constructor
    public ControlData(ControlData controlData){
        this(
                controlData.name,
                controlData.keycodes,
                controlData.dynamicX,
                controlData.dynamicY,
                controlData.width,
                controlData.height,
                controlData.isToggle,
                controlData.opacity,
                controlData.bgColor,
                controlData.strokeColor,
                controlData.strokeWidth,
                controlData.cornerRadius
        );
    }
    
    public void execute(boolean isDown) {
        for(int keycode : keycodes){
            sendKeyPress(keycode, 0, isDown);
        }
    }

    
    public float insertDynamicPos(String dynamicPos) {
        // Insert value to ${variable}
        String insertedPos = JSONUtils.insertSingleJSONValue(dynamicPos, fillConversionMap());
        
        // Calculate, because the dynamic position contains some math equations
        return calculate(insertedPos);
    }

    private static float calculate(String math) {
        setExpression(math);
        return (float) builder.get().build().evaluate();
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

    /**
     * Create a builder, keep a weak reference to it to use it with all views on first inflation
     */
    private static void buildExpressionBuilder(){
        ExpressionBuilder expressionBuilder = new ExpressionBuilder("1 + 1")
                .function(new Function("dp", 1) {
                    @Override
                    public double apply(double... args) {
                        return Tools.pxToDp((float) args[0]);
                    }
                })
                .function(new Function("px", 1) {
                    @Override
                    public double apply(double... args) {
                        return Tools.dpToPx((float) args[0]);
                    }
                });
        builder = new WeakReference<>(expressionBuilder);
    }

    /**
     * wrapper for the WeakReference to the expressionField.
     * @param stringExpression the expression to set.
     */
    private static void setExpression(String stringExpression){
        if(builder.get() == null) buildExpressionBuilder();
        builder.get().expression(stringExpression);
    }

    /**
     * Build a shared conversion map without the ControlData dependent values
     * You need to set the view dependent values before using it.
     */
    private static void buildConversionMap() {
        // Values in the map below may be always changed
        ArrayMap<String, String> keyValueMap = new ArrayMap<>(10);
        keyValueMap.put("top", "0");
        keyValueMap.put("left", "0");
        keyValueMap.put("right", "DUMMY_RIGHT");
        keyValueMap.put("bottom", "DUMMY_BOTTOM");
        keyValueMap.put("width", "DUMMY_WIDTH");
        keyValueMap.put("height", "DUMMY_HEIGHT");
        keyValueMap.put("screen_width", "DUMMY_DATA" );
        keyValueMap.put("screen_height", "DUMMY_DATA");
        keyValueMap.put("margin", Integer.toString((int) Tools.dpToPx(2)));
        keyValueMap.put("preferred_scale", Float.toString(LauncherPreferences.PREF_BUTTONSIZE));

        conversionMap = new WeakReference<>(keyValueMap);
    }

    /**
     * Fill the conversionMap with controlData dependent values.
     * The returned valueMap should NOT be kept in memory.
     * @return the valueMap to use.
     */
    private Map<String, String> fillConversionMap(){
        ArrayMap<String, String> valueMap = conversionMap.get();
        if (valueMap == null){
            buildConversionMap();
            valueMap = conversionMap.get();
        }

        valueMap.put("right", Float.toString(CallbackBridge.physicalWidth - getWidth()));
        valueMap.put("bottom", Float.toString(CallbackBridge.physicalHeight - getHeight()));
        valueMap.put("width", Float.toString(getWidth()));
        valueMap.put("height", Float.toString(getHeight()));
        valueMap.put("screen_width",Integer.toString(CallbackBridge.physicalWidth));
        valueMap.put("screen_height",Integer.toString(CallbackBridge.physicalHeight));

        return valueMap;
    }

}
