package java.awt;

/*
 * A fake GraphicsEnvironment, it was added
 * for AWT codes compatible and do nothing.
 */
public class GraphicsEnvironment extends Object
{
	private static GraphicsEnvironment localEnv = new GraphicsEnvironment();
	public static GraphicsEnvironment getLocalGraphicsEnvironment() {
		return localEnv;
	}
	
    public static String getHeadlessMessage() {
        return
            "\nNo X11 DISPLAY variable was set, " +
            "but this program performed an operation which requires it.";
    }
	
	public static void checkHeadless() {
		
	}
	
	public static boolean isHeadless() {
        return false;
    }
}
