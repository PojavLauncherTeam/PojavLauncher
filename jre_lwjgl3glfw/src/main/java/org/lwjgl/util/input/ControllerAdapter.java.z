/* 
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are 
 * met:
 * 
 * * Redistributions of source code must retain the above copyright 
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of 
 *   its contributors may be used to endorse or promote products derived 
 *   from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.lwjgl.util.input;

import org.lwjgl.input.Controller;

/**
 * Adapter for the Controller interface. It can be used as placeholder
 * Controller, which doesn't do anything (eg if Controllers.create() fails and
 * you don't want to take care of that).
 * 
 * @author Onyx, Aho and all the other aliases...
 */
public class ControllerAdapter implements Controller {

	/**
	 * Get the name assigned to this controller.
	 * 
	 * @return The name assigned to this controller
	 */
	public String getName() {
		return "Dummy Controller";
	}

	/**
	 * Get the index of this controller in the collection
	 * 
	 * @return The index of this controller in the collection
	 */
	public int getIndex() {
		return 0; //-1 maybe?
	}

	/**
	 * Retrieve the number of buttons available on this controller
	 * 
	 * @return The number of butotns available on this controller
	 */
	public int getButtonCount() {
		return 0;
	}

	/**
	 * Get the name of the specified button. Be warned, often this is as
	 * exciting as "Button X"
	 * 
	 * @param index The index of the button whose name should be retrieved
	 * @return The name of the button requested
	 */
	public String getButtonName(int index) {
		return "button n/a";
	}

	/**
	 * Check if a button is currently pressed
	 * 
	 * @param index The button to check
	 * @return True if the button is currently pressed
	 */
	public boolean isButtonPressed(int index) {
		return false;
	}

	/**
	 * Poll the controller for new data. This will also update events
	 */
	public void poll() {
	}

	/**
	 * Get the X-Axis value of the POV on this controller
	 * 
	 * @return The X-Axis value of the POV on this controller
	 */
	public float getPovX() {
		return 0f;
	}

	/**
	 * Get the Y-Axis value of the POV on this controller
	 * 
	 * @return The Y-Axis value of the POV on this controller
	 */
	public float getPovY() {
		return 0f;
	}

	/**
	 * Get the dead zone for a specified axis
	 * 
	 * @param index The index of the axis for which to retrieve the dead zone
	 * @return The dead zone for the specified axis
	 */
	public float getDeadZone(int index) {
		return 0f;
	}

	/**
	 * Set the dead zone for the specified axis
	 * 
	 * @param index The index of hte axis for which to set the dead zone
	 * @param zone The dead zone to use for the specified axis
	 */
	public void setDeadZone(int index, float zone) {
	}

	/**
	 * Retrieve the number of axes available on this controller.
	 * 
	 * @return The number of axes available on this controller.
	 */
	public int getAxisCount() {
		return 0;
	}

	/**
	 * Get the name that's given to the specified axis
	 * 
	 * @param index The index of the axis whose name should be retrieved
	 * @return The name of the specified axis.
	 */
	public String getAxisName(int index) {
		return "axis n/a";
	}

	/**
	 * Retrieve the value thats currently available on a specified axis. The
	 * value will always be between 1.0 and -1.0 and will calibrate as values
	 * are passed read. It may be useful to get the player to wiggle the
	 * joystick from side to side to get the calibration right.
	 * 
	 * @param index The index of axis to be read
	 * @return The value from the specified axis.
	 */
	public float getAxisValue(int index) {
		return 0f;
	}

	/**
	 * Get the value from the X axis if there is one. If no X axis is defined a
	 * zero value will be returned.
	 * 
	 * @return The value from the X axis
	 */
	public float getXAxisValue() {
		return 0f;
	}

	/**
	 * Get the dead zone for the X axis.
	 * 
	 * @return The dead zone for the X axis
	 */
	public float getXAxisDeadZone() {
		return 0f;
	}

	/**
	 * Set the dead zone for the X axis
	 * 
	 * @param zone The dead zone to use for the X axis
	 */
	public void setXAxisDeadZone(float zone) {
	}

	/**
	 * Get the value from the Y axis if there is one. If no Y axis is defined a
	 * zero value will be returned.
	 * 
	 * @return The value from the Y axis
	 */
	public float getYAxisValue() {
		return 0f;
	}

	/**
	 * Get the dead zone for the Y axis.
	 * 
	 * @return The dead zone for the Y axis
	 */
	public float getYAxisDeadZone() {
		return 0f;
	}

	/**
	 * Set the dead zone for the Y axis
	 * 
	 * @param zone The dead zone to use for the Y axis
	 */
	public void setYAxisDeadZone(float zone) {
	}

	/**
	 * Get the value from the Z axis if there is one. If no Z axis is defined a
	 * zero value will be returned.
	 * 
	 * @return The value from the Z axis
	 */
	public float getZAxisValue() {
		return 0f;
	}

	/**
	 * Get the dead zone for the Z axis.
	 * 
	 * @return The dead zone for the Z axis
	 */
	public float getZAxisDeadZone() {
		return 0f;
	}

	/**
	 * Set the dead zone for the Z axis
	 * 
	 * @param zone The dead zone to use for the Z axis
	 */
	public void setZAxisDeadZone(float zone) {
	}

	/**
	 * Get the value from the RX axis if there is one. If no RX axis is defined
	 * a zero value will be returned.
	 * 
	 * @return The value from the RX axis
	 */
	public float getRXAxisValue() {
		return 0f;
	}

	/**
	 * Get the dead zone for the RX axis.
	 * 
	 * @return The dead zone for the RX axis
	 */
	public float getRXAxisDeadZone() {
		return 0f;
	}

	/**
	 * Set the dead zone for the RX axis
	 * 
	 * @param zone The dead zone to use for the RX axis
	 */
	public void setRXAxisDeadZone(float zone) {
	}

	/**
	 * Get the value from the RY axis if there is one. If no RY axis is defined
	 * a zero value will be returned.
	 * 
	 * @return The value from the RY axis
	 */
	public float getRYAxisValue() {
		return 0f;
	}

	/**
	 * Get the dead zone for the RY axis.
	 * 
	 * @return The dead zone for the RY axis
	 */
	public float getRYAxisDeadZone() {
		return 0f;
	}

	/**
	 * Set the dead zone for the RY axis
	 * 
	 * @param zone The dead zone to use for the RY axis
	 */
	public void setRYAxisDeadZone(float zone) {
	}

	/**
	 * Get the value from the RZ axis if there is one. If no RZ axis is defined
	 * a zero value will be returned.
	 * 
	 * @return The value from the RZ axis
	 */
	public float getRZAxisValue() {
		return 0f;
	}

	/**
	 * Get the dead zone for the RZ axis.
	 * 
	 * @return The dead zone for the RZ axis
	 */
	public float getRZAxisDeadZone() {
		return 0f;
	}

	/**
	 * Set the dead zone for the RZ axis
	 * 
	 * @param zone The dead zone to use for the RZ axis
	 */
	public void setRZAxisDeadZone(float zone) {
	}
	
	public int getRumblerCount() {
		return 0;
	}
	
	public String getRumblerName(int index) {
		return "rumber n/a";
	}
	
	public void setRumblerStrength(int index, float strength) {
	}
}