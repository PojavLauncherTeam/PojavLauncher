package net.kdt.pojavlaunch.customcontrols;

import net.kdt.pojavlaunch.Tools;

import java.util.ArrayList;

import static net.kdt.pojavlaunch.customcontrols.ControlDrawerData.Orientation.下;
import static net.kdt.pojavlaunch.customcontrols.ControlDrawerData.Orientation.左;
import static net.kdt.pojavlaunch.customcontrols.ControlDrawerData.Orientation.右;
import static net.kdt.pojavlaunch.customcontrols.ControlDrawerData.Orientation.上;
import static net.kdt.pojavlaunch.customcontrols.ControlDrawerData.Orientation.自由;

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
        return new Orientation[]{下,左,上,右,自由};
    }

    public static int orientationToInt(Orientation orientation){
        switch (orientation){
            case 下: return 0;
            case 左: return 1;
            case 上: return 2;
            case 右: return 3;
            case 自由: return 4;
        }
        return -1;
    }

    public static Orientation intToOrientation(int by){
        switch (by){
            case 0: return 下;
            case 1: return 左;
            case 2: return 上;
            case 3: return 右;
            case 4: return 自由;
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
        this(buttonProperties, properties, Orientation.左);
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
