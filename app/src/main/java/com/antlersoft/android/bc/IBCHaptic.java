/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package com.antlersoft.android.bc;

import android.view.View;

/**
 * Access the Haptic interfaces added in version 3 without breaking compatibility
 * @author Michael A. MacDonald
 */
public interface IBCHaptic {
	public boolean performLongPressHaptic(View v);
	/**
	 * Set whether haptic feedback is enabled on the view
	 * @param enabled
	 * @return Old value of setting
	 */
	//public boolean setIsHapticEnabled(View v, boolean enabled);
}
