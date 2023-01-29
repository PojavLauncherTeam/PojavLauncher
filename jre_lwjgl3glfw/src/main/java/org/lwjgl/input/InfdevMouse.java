package org.lwjgl.input;

import net.java.openjdk.cacio.ctc.ExternalMouseReader;
import net.java.openjdk.cacio.ctc.InfdevGrabHandler;

public class InfdevMouse implements ExternalMouseReader, Mouse.EmptyCursorGrabListener {
    static {
        InfdevGrabHandler.setMouseReader(new InfdevMouse());
    }

    @Override
    public int getX() {
        return Mouse.getAbsoluteX();
    }

    @Override
    public int getY() {
        return Mouse.getAbsoluteY();
    }

    @Override
    public void onGrab(boolean grabbing) {
        InfdevGrabHandler.setGrabbed(grabbing);
    }
}
