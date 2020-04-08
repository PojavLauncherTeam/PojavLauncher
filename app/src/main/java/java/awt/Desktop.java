package java.awt;

import android.content.*;
import android.net.*;
import android.util.*;
import java.awt.peer.*;
import java.io.*;
import java.net.*;
import net.kdt.pojavlaunch.MainActivity;
import android.app.*;
import net.kdt.pojavlaunch.*;
import java.awt.mod.*;

public class Desktop
{
	private Activity currentActivity;
	public enum Action
	{
		BROWSE, EDIT, MAIL, OPEN, PRINT
	}
	// private DesktopPeer peer;
	public Desktop()
	{
		// peer = Toolkit.getDefaultToolkit().createDesktopPeer();
		try {
			if (currentActivity == null) currentActivity = ModdingKit.getCurrentActivity();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Desktop getDesktop() {
		return new Desktop();
	}
	
	public static boolean isDesktopSupported() {
		return true;
	}
	
	public boolean isSupported(Action action) {
        return true;
    }
	
	public void browse(URI uri) {
        try {
            URL url = uri.toURL();
			if(url.toString().startsWith("file:")){
				String fPath = url.toString().replace("file:", "");
				System.out.println("PojavLauncher:java.awt.Desktop: Browse folder: " + fPath);
				
				// Current not implemented
			}
			else{
				System.out.println("PojavLauncher:java.awt.Desktop: Browse URL: " + url.toString());
				if (!url.toString().startsWith("http://") && !url.toString().startsWith("https://")){
					url = new URL("http://" + url.toString());
				}
				currentActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString())));
			}
        } catch (Exception e) {
            throw new RuntimeException(e);
		}
	}
}
