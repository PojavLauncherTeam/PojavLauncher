package net.kdt.pojavlaunch;

public class MultidojManager
{
	private static Listen li;
	
	public static interface Listen {
		public void onReceived(String msg, int max, int current);
	}
	
	public static void setListener(Listen l) {
		li = l;
	}
	
	public static void call(String msg, int max, int current) {
		if (li != null) li.onReceived(msg, max, current);
	}
}
