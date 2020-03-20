package net.kdt.pojavlaunch;
import android.util.*;

public class PojavDXManager
{
	private static Listen li;
	
	public static int maxProgress = 0;
	public static int currProgress = 0;
	
	public static interface Listen {
		public void onReceived(String msg, int max, int current);
	}
	
	public static void setListener(Listen l) {
		maxProgress = 0;
		li = l;
	}
	
	public static void call(String msg) {
		String[] splitMsg = new String[]{
			msg.substring(0, msg.indexOf(" ")),
			msg.substring(msg.indexOf(" ") + 1)
		};
		
		String firstMsg = splitMsg[0];
		if (firstMsg.startsWith("processing")) {
			currProgress++;
		} else if (firstMsg.startsWith("setmax")) {
			maxProgress = Integer.valueOf(splitMsg[1]);
		} else if (firstMsg.startsWith("warning")) {
			// Nothing
		} else if (firstMsg.startsWith("writing")) {
			// Nothing
		} else {
			Log.w("PojavDexer", "Unknown dx operation: " + firstMsg);
		}
		
		if (li != null) li.onReceived(msg, maxProgress, currProgress);
	}
}
