/**
 * Copyright (c) 2010 Michael A. MacDonald
 */
package com.antlersoft.android.bc;

import java.io.File;

import android.content.Context;

/**
 * @author Michael A. MacDonald
 *
 */
class BCStorageContext8 implements IBCStorageContext {

	/* (non-Javadoc)
	 * @see com.antlersoft.android.bc.IBCStorageContext#getExternalStorageDir(android.content.Context, java.lang.String)
	 */
	@Override
	public File getExternalStorageDir(Context context, String type) {
		return context.getExternalFilesDir(type);
	}

}
