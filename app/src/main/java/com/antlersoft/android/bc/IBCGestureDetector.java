/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package com.antlersoft.android.bc;

import android.content.Context;
import android.view.GestureDetector;

/**
 * Create a gesture detector in a version friendly way, avoiding incompatible API on older version
 * and deprecated API on newer version
 * 
 * @author Michael A. MacDonald
 */
public interface IBCGestureDetector {
	public GestureDetector createGestureDetector(Context context, GestureDetector.OnGestureListener listener);
}
