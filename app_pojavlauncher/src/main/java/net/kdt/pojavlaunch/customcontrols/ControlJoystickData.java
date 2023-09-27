package net.kdt.pojavlaunch.customcontrols;

public class ControlJoystickData extends ControlData {

    /* Whether the joystick can stay forward */
    public boolean forwardLock = false;

    public ControlJoystickData(){
        super();
    }

    public ControlJoystickData(ControlData properties) {
        super(properties);
    }
}
