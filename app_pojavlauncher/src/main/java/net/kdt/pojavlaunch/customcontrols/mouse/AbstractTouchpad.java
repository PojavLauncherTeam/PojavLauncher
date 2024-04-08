package net.kdt.pojavlaunch.customcontrols.mouse;

public interface AbstractTouchpad {
    boolean getDisplayState();
    void applyMotionVector(float[] vector);
}
