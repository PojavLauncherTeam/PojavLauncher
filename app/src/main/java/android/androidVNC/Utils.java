package android.androidVNC;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;

public class Utils {

	public static void showYesNoPrompt(Context _context, String title, String message, OnClickListener onYesListener, OnClickListener onNoListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(_context);
		builder.setTitle(title);
		builder.setIcon(android.R.drawable.ic_dialog_info); // lame icon
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", onYesListener);
		builder.setNegativeButton("No", onNoListener);
		builder.show();
	}
	
	private static final Intent docIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://code.google.com/p/android-vnc-viewer/wiki/Documentation")); 

	public static ActivityManager getActivityManager(Context context)
	{
		ActivityManager result = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		if (result == null)
			throw new UnsupportedOperationException("Could not retrieve ActivityManager");
		return result;
	}
	
	public static MemoryInfo getMemoryInfo(Context _context) {
		MemoryInfo info = new MemoryInfo();
		getActivityManager(_context).getMemoryInfo(info);
		return info;
	}
	
	public static void showDocumentation(Context c) {
		c.startActivity(docIntent);
	}

	private static int nextNoticeID = 0;
	public static int nextNoticeID() {
		nextNoticeID++;
		return nextNoticeID;
	}

	public static void showErrorMessage(Context _context, String message) {
		showMessage(_context, "Error!", message, android.R.drawable.ic_dialog_alert, null);
	}

	public static void showFatalErrorMessage(final Context _context, String message) {
		showMessage(_context, "Fatal Error!", message, android.R.drawable.ic_dialog_alert, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ((Activity) _context).finish();
			}
		});
	}
	
	public static void showMessage(Context _context, String title, String message, int icon, DialogInterface.OnClickListener ackHandler) {
		AlertDialog.Builder builder = new AlertDialog.Builder(_context);
		builder.setTitle(title);
		builder.setMessage(Html.fromHtml(message));
		builder.setCancelable(false);
		builder.setPositiveButton("Acknowledged", ackHandler);
		builder.setIcon(icon);
		builder.show();
	}
}
