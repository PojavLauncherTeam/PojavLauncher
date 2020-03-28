package javax.swing;

import android.app.*;
import android.util.*;
import java.awt.*;
import java.awt.mod.*;
import android.content.*;

public class JOptionPane
{
	private static boolean isOk;
	public static void showMessageDialog(Component parentComponent, final Object title, final String message, int messageType) {
		Log.w("JOptionPane", "[" + message + "] " + title.toString());

		isOk = false;
		final Activity act = ModdingKit.getCurrentActivity();
		act.runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					AlertDialog.Builder dialog = new AlertDialog.Builder(act);
					dialog.setTitle(title.toString());
					dialog.setMessage(message);
					dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								isOk = true;
							}
						});
					dialog.show();
				}
			});
		while (!isOk) {}
	}
}

