package net.kdt.pojavlaunch;
import android.util.*;
import com.pojavdx.dx.command.dexer.*;

public class PojavDXManager
{
	private static Listen li;
	
	public static int maxProgress = 0;
	public static int currProgress = 0;
	
	public static interface Listen {
		public void onReceived(String msg, int max, int current);
	}
	
	public static void setListener(Listen l) {
		currProgress = 0;
		li = l;
	}
	
	public static void call(String msg) {
		if (li != null) li.onReceived(msg, maxProgress, currProgress);
	}
}
