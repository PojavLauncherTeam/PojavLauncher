package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.os.*;
import androidx.appcompat.app.*;
import android.util.*;
import androidx.appcompat.app.AlertDialog;

public class FatalErrorActivity extends BaseActivity
{
	public static void showError(Context ctx, String savePath, boolean storageAllow, /* boolean isFatalErr, */ Throwable th) {
		Intent ferrorIntent = new Intent(ctx, FatalErrorActivity.class);
		ferrorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		ferrorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ferrorIntent.putExtra("throwable", th);
		ferrorIntent.putExtra("savePath", savePath);
		ferrorIntent.putExtra("storageAllow", storageAllow);
		// ferrorIntent.putExtra("isFatal", isFatalErr);
		ctx.startActivity(ferrorIntent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		boolean storageAllow = extras.getBoolean("storageAllow");
		final String strStackTrace = Log.getStackTraceString((Throwable) extras.getSerializable("throwable"));
		String strSavePath = extras.getString("savePath");
		String errHeader = storageAllow ?
			"Crash stack trace saved to " + strSavePath + "." :
			"Storage permission is required to save crash stack trace!";
		
		// boolean isFatalError = extras.getBoolean("isFatal", false);
		
		new AlertDialog.Builder(this)
			.setTitle(R.string.error_fatal)
			.setMessage(errHeader + "\n\n" + strStackTrace)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2) {
					finish();
				}
			})
			.setNegativeButton(R.string.global_restart, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2) {
					startActivity(new Intent(FatalErrorActivity.this, PojavLoginActivity.class));
				}
			})
			.setNeutralButton(android.R.string.copy, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2) {
					ClipboardManager mgr = (ClipboardManager) FatalErrorActivity.this.getSystemService(CLIPBOARD_SERVICE);
					mgr.setPrimaryClip(ClipData.newPlainText("error", strStackTrace));

					finish();
				}
			})
			//.setNegativeButton("Report (not available)", null)
			.setCancelable(false)
			.show();
		
		// Tools.showError(this, isFatalError ? R.string.error_fatal : R.string.global_error, th, true);
	}
}
