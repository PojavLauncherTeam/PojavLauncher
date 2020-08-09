package com.android.internal.awt;

import java.awt.*;

public class AndroidGraphicsDevice extends GraphicsDevice
{
	public GraphicsConfiguration[] mConfigurations = new GraphicsConfiguration[]{new AndroidGraphicsConfiguration()};
	
	@Override
	public GraphicsConfiguration[] getConfigurations() {
		return mConfigurations;
	}

	@Override
	public GraphicsConfiguration getDefaultConfiguration() {
		return mConfigurations[0];
	}

	@Override
	public String getIDstring() {
		return "Android";
	}

	@Override
	public int getType() {
		return TYPE_IMAGE_BUFFER;
	}
}

