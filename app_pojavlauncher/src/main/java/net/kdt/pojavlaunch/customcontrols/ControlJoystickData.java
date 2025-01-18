package net.kdt.pojavlaunch.customcontrols;

public class ControlJoystickData extends ControlData {

    /* Whether the joystick can stay forward */
    public boolean forwardLock = false;
    /*
     * Whether the finger tracking is absolute (joystick jumps to where you touched)
     * or relative (joystick stays in the center)
     */
    public boolean absolute = false;

    public ControlJoystickData(){
        super();
    }

    public ControlJoystickData(ControlJoystickData properties) {
        super(properties);
        forwardLock = properties.forwardLock;
        absolute = properties.absolute;
    }
}
