/*
 * Copyright (c) 1995, 2003, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package sun.applet;
import android.media.*;
import android.net.*;
import java.applet.*;
import java.io.*;
import java.net.*;
import java.awt.mod.*;
/**
 * Applet audio clip;
 *
 * @author Arthur van Hoff, Kara Kytle
 */
public class AppletAudioClip implements AudioClip {
    // url that this AudioClip is based on
    private Uri url = null;
	boolean DEBUG = false /*true*/;
	MediaPlayer audioClip;
	
    /**
     * Constructs an AppletAudioClip from an URL.
     */
    public AppletAudioClip(URL url) {
        // store the url
        this.url = Uri.parse(url.toString());
    }
    /**
     * Constructs an AppletAudioClip from a URLConnection.
     */
    public AppletAudioClip(URLConnection uc) {
        this(uc.getURL());
    }
    /**
     * For constructing directly from Jar entries, or any other
     * raw Audio data. Note that the data provided must include the format
     * header.
     */
    public AppletAudioClip(byte[] data) {
        throw new UnsupportedOperationException("Construct (byte[])");
    }
    /*
     * Does the real work of creating an AppletAudioClip from an InputStream.
     * This function is used by both constructors.
     */
    void createAppletAudioClip(InputStream in) throws IOException {
        try {
			audioClip = new MediaPlayer();
			audioClip.setDataSource(ModdingKit.getCurrentActivity(), url);
			audioClip.prepare();
        } catch (Exception e3) {
            // no matter what happened, we throw an IOException to avoid changing the interfaces....
            throw new IOException("Failed to construct the AudioClip: " + e3);
        }
    }
    public synchronized void play() {
		if (audioClip != null)
			audioClip.start();
    }
    public synchronized void loop() {
		if (audioClip != null)
			audioClip.setLooping(true);
    }
    public synchronized void stop() {
		if (audioClip != null)
			audioClip.stop();
    }
}
