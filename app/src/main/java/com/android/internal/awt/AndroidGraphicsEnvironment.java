/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * @author Alexey A. Petrenko, Oleg V. Khaschansky
 * @version $Revision$
 */
 
package com.android.internal.awt;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.harmony.awt.gl.CommonGraphicsEnvironment;
import java.awt.*;

/**
 * AndroidGraphicsEnvironment implementation
 *
 */
public class AndroidGraphicsEnvironment extends CommonGraphicsEnvironment
{
	public GraphicsDevice[] mGraphicsDevices = new GraphicsDevice[]{new AndroidGraphicsDevice()};
	
	@Override
	public GraphicsDevice getDefaultScreenDevice() throws HeadlessException {
		return mGraphicsDevices[0];
	}

	@Override
	public GraphicsDevice[] getScreenDevices() throws HeadlessException {
		return mGraphicsDevices;
	}
}
