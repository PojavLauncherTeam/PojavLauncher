package org.lwjgl.input;

import org.lwjgl.glfw.GLFW;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class GLFWController implements Controller{
    int jid;
    FloatBuffer axisData = FloatBuffer.allocate(8);
    ByteBuffer buttonData = ByteBuffer.allocate(8);
    @Override
    public String getName() {
        return GLFW.glfwGetJoystickName(jid);
    }

    @Override
    public int getIndex() {
        return jid;
    }

    @Override
    public int getButtonCount() {
        return 8;
    }

    @Override
    public String getButtonName(int index) {
       switch(index) {
           case 0:
               return "A";
           case 1:
               return "B";
           case 2:
               return "X";
           case 3:
               return "Y";
           case 4:
               return "DPAD LEFT";
           case 5:
               return "DPAD RIGHT";
           case 6:
               return "DPAD UP";
           case 7:
               return "DPAD DOWN";
           default:
               return null;
       }
    }

    @Override
    public boolean isButtonPressed(int index) {
        if(index < 8){
           return (buttonData.get(index) == 1);
        }else return false;
    }

    @Override
    public void poll() {
        
        //axisData = GLFW.glfwGetJoystickAxes(jid);
        //buttonData = GLFW.glfwGetJoystickButtons(jid);
    }

    @Override
    public float getPovX() {
        return axisData.get(6);
    }

    @Override
    public float getPovY() {
        return axisData.get(7);
    }

    @Override
    public float getDeadZone(int index) {
        return 0;
    }

    @Override
    public void setDeadZone(int index, float zone) {

    }

    @Override
    public int getAxisCount() {
        return 8;
    }

    @Override
    public String getAxisName(int index) {
        switch(index) {
            case 0:
                return "AXIS X";
            case 1:
                return "AXIS Y";
            case 2:
                return "AXIS Z";
            case 3:
                return "AXIS RX";
            case 4:
                return "AXIS RY";
            case 5:
                return "AXIS RZ";
            case 6:
                return "HAT/POV X";
            case 7:
                return "HAT/POV Y";
            default:
                return null;
        }

    }

    @Override
    public float getAxisValue(int index) {
        return axisData.get(index);
    }

    @Override
    public float getXAxisValue() {
        return axisData.get(0);
    }

    @Override
    public float getXAxisDeadZone() {
        return 0;
    }

    @Override
    public void setXAxisDeadZone(float zone) {

    }

    @Override
    public float getYAxisValue() {
        return axisData.get(1);
    }

    @Override
    public float getYAxisDeadZone() {
        return 0;
    }

    @Override
    public void setYAxisDeadZone(float zone) {

    }

    @Override
    public float getZAxisValue() {
        return axisData.get(2);
    }

    @Override
    public float getZAxisDeadZone() {
        return 0;
    }

    @Override
    public void setZAxisDeadZone(float zone) {

    }

    @Override
    public float getRXAxisValue() {
        return axisData.get(3);
    }

    @Override
    public float getRXAxisDeadZone() {
        return 0;
    }

    @Override
    public void setRXAxisDeadZone(float zone) {

    }

    @Override
    public float getRYAxisValue() {
        return axisData.get(4);
    }

    @Override
    public float getRYAxisDeadZone() {
        return 0;
    }

    @Override
    public void setRYAxisDeadZone(float zone) {

    }

    @Override
    public float getRZAxisValue() {
        return axisData.get(5);
    }

    @Override
    public float getRZAxisDeadZone() {
        return 0;
    }

    @Override
    public void setRZAxisDeadZone(float zone) {

    }

    @Override
    public int getRumblerCount() {
        return 0;
    }

    @Override
    public String getRumblerName(int index) {
        return null;
    }

    @Override
    public void setRumblerStrength(int index, float strength) {

    }
}
