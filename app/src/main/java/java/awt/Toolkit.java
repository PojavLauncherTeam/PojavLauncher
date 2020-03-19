package java.awt;

import java.awt.datatransfer.*;
import java.awt.peer.*;
import javax.imageio.*;
import java.io.*;

public class Toolkit extends Object
{
	private static Toolkit toolkit = new Toolkit();
	private DesktopPeer desktopPeer = new DesktopPeer(){};
	private Clipboard clipboard = new Clipboard();

	public Image createImage(String fileStr)
	{
		try {
			return ImageIO.read(new File(fileStr));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void checkHeadless()
	{
		GraphicsEnvironment.checkHeadless();
	}

	public boolean isAlwaysOnTopSupported()
	{
		// Android implementation: NOT SUPPORTED
		return false;
	}
	
	private class AWTTreeLock {
    }

    final Object awtTreeLock = new AWTTreeLock();
	
	private Toolkit()
	{}

	public DesktopPeer createDesktopPeer()
	{
		return desktopPeer;
	}
	
	public static Toolkit getDefaultToolkit()
	{
		return toolkit;
	}
	
	public Clipboard getSystemClipboard() throws HeadlessException
	{
		return clipboard;
	}
}
