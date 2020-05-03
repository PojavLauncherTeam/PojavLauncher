/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package com.antlersoft.android.bc;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;

/**
 * @author Michael A. MacDonald
 */
public class BCGestureDetectorDefault implements IBCGestureDetector {

	/* (non-Javadoc)
	 * @see com.antlersoft.android.bc.IBCGestureDetector#createGestureDetector(android.content.Context, android.view.GestureDetector.OnGestureListener)
	 */
	@Override
	public GestureDetector createGestureDetector(Context context,
			OnGestureListener listener) {
		return new GestureDetector(context, listener);
	}

}
