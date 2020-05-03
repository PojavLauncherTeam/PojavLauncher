package com.theqvd.android.xpro;
/**
 * Copyright 2009-2014 by Qindel Formacion y Servicios S.L.
 * 
 * xvncpro is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * xvncpro is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 */
import android.app.Activity;
import android.app.PendingIntent;

public interface VncViewer /* extends Prerrequisite */ {

	public void launchVncViewer() throws XvncproException;
	public PendingIntent getContentVncIntent();
	public Activity getActivity();
	public void stopVncViewer();
}
