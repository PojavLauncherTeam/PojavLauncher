/* Copyright (C) 2010 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * MODIFIED FOR ANTLERSOFT
 * 
 * Changes for antlersoft/ vnc viewer for android
 * 
 * Copyright (C) 2010 Michael A. MacDonald
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this software; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
 * USA.
 */
package com.antlersoft.android.bc;

import android.view.MotionEvent;

/**
 * Backwards-compatibility interface to the android.view.ScaleGestureDetector introduced in Android SDK 8.
 * 
 * This will be a working implementation of devices with SDK >= 5 (since I backported ScaleGestureDetector
 * to 5) and a dummy implementation for older devices.
 * 
 * @author Michael A. MacDonald
 *
 */
public interface IBCScaleGestureDetector {

	public abstract boolean onTouchEvent(MotionEvent event);

	/**
	 * Returns {@code true} if a two-finger scale gesture is in progress.
	 * @return {@code true} if a scale gesture is in progress, {@code false} otherwise.
	 */
	public abstract boolean isInProgress();

	/**
	 * Get the X coordinate of the current gesture's focal point.
	 * If a gesture is in progress, the focal point is directly between
	 * the two pointers forming the gesture.
	 * If a gesture is ending, the focal point is the location of the
	 * remaining pointer on the screen.
	 * If {@link #isInProgress()} would return false, the result of this
	 * function is undefined.
	 * 
	 * @return X coordinate of the focal point in pixels.
	 */
	public abstract float getFocusX();

	/**
	 * Get the Y coordinate of the current gesture's focal point.
	 * If a gesture is in progress, the focal point is directly between
	 * the two pointers forming the gesture.
	 * If a gesture is ending, the focal point is the location of the
	 * remaining pointer on the screen.
	 * If {@link #isInProgress()} would return false, the result of this
	 * function is undefined.
	 * 
	 * @return Y coordinate of the focal point in pixels.
	 */
	public abstract float getFocusY();

	/**
	 * Return the current distance between the two pointers forming the
	 * gesture in progress.
	 * 
	 * @return Distance between pointers in pixels.
	 */
	public abstract float getCurrentSpan();

	/**
	 * Return the previous distance between the two pointers forming the
	 * gesture in progress.
	 * 
	 * @return Previous distance between pointers in pixels.
	 */
	public abstract float getPreviousSpan();

	/**
	 * Return the scaling factor from the previous scale event to the current
	 * event. This value is defined as
	 * ({@link #getCurrentSpan()} / {@link #getPreviousSpan()}).
	 * 
	 * @return The current scaling factor.
	 */
	public abstract float getScaleFactor();

	/**
	 * Return the time difference in milliseconds between the previous
	 * accepted scaling event and the current scaling event.
	 * 
	 * @return Time difference since the last scaling event in milliseconds.
	 */
	public abstract long getTimeDelta();

	/**
	 * Return the event time of the current event being processed.
	 * 
	 * @return Current event time in milliseconds.
	 */
	public abstract long getEventTime();

}