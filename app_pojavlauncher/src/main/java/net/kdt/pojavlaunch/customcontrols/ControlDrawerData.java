package net.kdt.pojavlaunch.customcontrols;

import net.kdt.pojavlaunch.Tools;

import java.util.ArrayList;

import static net.kdt.pojavlaunch.customcontrols.ControlDrawerData.Orientation.DOWN;
import static net.kdt.pojavlaunch.customcontrols.ControlDrawerData.Orientation.LEFT;
import static net.kdt.pojavlaunch.customcontrols.ControlDrawerData.Orientation.RIGHT;
import static net.kdt.pojavlaunch.customcontrols.ControlDrawerData.Orientation.UP;
import static net.kdt.pojavlaunch.customcontrols.ControlDrawerData.Orientation.FREE;

@androidx.annotation.Keep
public class ControlDrawerData {

    public final ArrayList<ControlData> buttonProperties;
    public final ControlData properties;
    public Orientation orientation;

    @androidx.annotation.Keep
    public enum Orientation {
        下,
        左,
        上,
        右,
        自由
    }

    public static Orientation[] getOrientations(){
        return new Orientation[]{DOWN,LEFT,UP,RIGHT,FREE};
    }

    public static int orientationToInt(Orientation orientation){
        switch (orientation){
            case DOWN: return 0;
            case LEFT: return 1;
            case UP: return 2;
            case RIGHT: return 3;
            case FREE: return 4;
        }
        return -1;
    }

    public static Orientation intToOrientation(int by){
        switch (by){
            case 0: return DOWN;
            case 1: return LEFT;
            case 2: return UP;
            case 3: return RIGHT;
            case 4: return FREE;
        }
        return null;
    }

    public ControlDrawerData(){
        this(new ArrayList<>());
    }

    public ControlDrawerData(ArrayList<ControlData> buttonProperties){
        this(buttonProperties, new ControlData("组合键", new int[] {}, Tools.currentDisplayMetrics.widthPixels/2f, Tools.currentDisplayMetrics.heightPixels/2f));
    }

    public ControlDrawerData(ArrayList<ControlData> buttonProperties, ControlData properties){
        this(buttonProperties, properties, Orientation.LEFT);
    }


    public ControlDrawerData(ArrayList<ControlData> buttonProperties, ControlData properties, Orientation orientation){
        this.buttonProperties = buttonProperties;
        this.properties = properties;
        this.orientation = orientation;
    }

    public ControlDrawerData(ControlDrawerData drawerData){
        buttonProperties = new ArrayList<>(drawerData.buttonProperties.size());
        for(ControlData controlData : drawerData.buttonProperties){
            buttonProperties.add(new ControlData(controlData));
        }
        properties = new ControlData(drawerData.properties);
        orientation = drawerData.orientation;
    }

}
