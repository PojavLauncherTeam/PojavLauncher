package net.kdt.pojavlaunch.exit;

import java.lang.reflect.*;

public class ExitManager {
	private static boolean stopLoop = true;
	
	private static ExitTrappedListener listener;
	private static Thread exitTrappedHook = new Thread(new Runnable(){
		
		@Override
		public void run()
		{
			if (listener != null) listener.onExitTrapped();
			
			stopLoop = false;
			
			while (!stopLoop) {
				// Make Thread hook never stop, then System.exit() never continue!
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
			}
		}
	});
	
	public static boolean isExiting() {
		return !stopLoop;
	}
	
	public static void setExitTrappedListener(ExitTrappedListener l) {
		listener = l;
	}
	
	public static void stopExitLoop() {
		stopLoop = true;
	}
	
	public static void disableSystemExit() // throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException
	{
		// changeRuntimeExitDisabled(true);
		
		Runtime.getRuntime().addShutdownHook(exitTrappedHook);
	}

	public static void enableSystemExit() // throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException
	{
		// changeRuntimeExitDisabled(false);
		
		
		Runtime.getRuntime().removeShutdownHook(exitTrappedHook);
	}
	
	// It's not safe. Add/Remove shutdown hooks will cause error.
	private static void changeRuntimeExitDisabled(boolean value) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
		Runtime run = Runtime.getRuntime();
		Field shutdownField = run.getClass().getDeclaredField("shuttingDown");
		shutdownField.setAccessible(true);
		shutdownField.set(run, value);
	}

	
	public static interface ExitTrappedListener {
		public void onExitTrapped();
	}
}
