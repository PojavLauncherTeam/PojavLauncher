package java.awt;

import java.util.*;
import java.awt.peer.*;
import sun.security.util.*;
import java.awt.mod.*;

public class Window extends Container
{
    private boolean alwaysOnTop;
	
	private static List<Window> windows;
	private String title = "Untitled";
	
	static {
		if (windows == null) {
			windows = new ArrayList<Window>();
		}
	}
	
	public static Window[] getWindows() {
		return windows.toArray(new Window[0]);
	}
	
	public Window() {
		this("");
	}
	
	public Window(String title) {
		this.title = title;
		
		// Don't add WindowManager
		// if (!(this instanceof AndroidWindowManager)) {
			windows.add(this);
		// }
    }
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public final void setAlwaysOnTop(boolean alwaysOnTop) throws SecurityException {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(SecurityConstants.AWT.SET_WINDOW_ALWAYS_ON_TOP_PERMISSION);
        }

        boolean oldAlwaysOnTop;
        synchronized(this) {
            oldAlwaysOnTop = this.alwaysOnTop;
            this.alwaysOnTop = alwaysOnTop;
        }
        if (oldAlwaysOnTop != alwaysOnTop ) {
            if (isAlwaysOnTopSupported()) {
				
                WindowPeer peer = (WindowPeer)this.peer;
                synchronized(getTreeLock()) {
                    if (peer != null) {
                        // peer.setAlwaysOnTop(alwaysOnTop);
                    }
                }
            }
            firePropertyChange("alwaysOnTop", oldAlwaysOnTop, alwaysOnTop);
        }
    }

    public boolean isAlwaysOnTopSupported() {
        return Toolkit.getDefaultToolkit().isAlwaysOnTopSupported();
    }
	
	public final boolean isAlwaysOnTop() {
        return alwaysOnTop;
    }
	
	public void pack() {
		//super.pack();
    }
	
	public void setIconImages(List<Image> icons)
	{
		// TODO: Implement this method
	}
}
