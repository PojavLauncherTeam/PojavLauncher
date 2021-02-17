package org.lwjgl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.glfw.GLFW;

import java.awt.Desktop;
import java.net.URI;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Sys {
    
    /**
     * No constructor for Sys.
     */
    private Sys() {
    }

    /** Returns the LWJGL version. */
    public static String getVersion() {
        return org.lwjgl.Version.getVersion();
    }
    
	public static void initialize() {
		if (!GLFW.glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
	}

	/**
	 * GLFW automatically recomputes the time via
	 * {@link GLFW#glfwGetTimerValue()}, no need to divide the frequency
	 * 
	 * @return 1
	 */
	public static long getTimerResolution() {
		return 1000;
	}

	/**
	 * Gets the current value of the hires timer, in ticks. When the Sys class
	 * is first loaded the hi-res timer is reset to 0. If no hi-res timer is
	 * present then this method will always return 0.
	 * <p>
	 * <strong>PLEASE NOTE: </strong> the hi-res timer WILL wrap around.
	 *
	 * @return the current hi-res time, in ticks (always >= 0)
	 */
	public static long getTime() {
		return GLFW.glfwGetTimerValue();
	}

	public static long getNanoTime() {
        return System.nanoTime();
		// return getTime() * 1000L * 1000L;
	}

	public static boolean openURL(String url) {
		if (!Desktop.isDesktopSupported())
			return false;

		Desktop desktop = Desktop.getDesktop();
		if (!desktop.isSupported(Desktop.Action.BROWSE))
			return false;

		try {
			desktop.browse(new URI(url));
			return true;
		} catch (Exception ex) {
            ex.printStackTrace();
			return false;
		}
	}

	public static void alert(String title, String message) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LWJGLUtil.log("Caught exception while setting Look-and-Feel: " + e);
		}
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
	}

	public static String getClipboard() {
		return GLFW.glfwGetClipboardString(GLFW.glfwGetPrimaryMonitor());
	}
}
