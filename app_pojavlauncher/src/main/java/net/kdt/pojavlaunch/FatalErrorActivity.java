package net.kdt.pojavlaunch;

import android.content.*;
import android.os.*;
import androidx.appcompat.app.*;
import android.util.*;
import androidx.appcompat.app.AlertDialog;

public class FatalErrorActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		boolean storageAllow = extras.getBoolean("storageAllow");
		final String stackTrace = Log.getStackTraceString((Throwable) extras.getSerializable("throwable"));
		String strSavePath = extras.getString("savePath");
		String errHeader = storageAllow ?
			"Crash stack trace saved to " + strSavePath + "." :
			"Storage permission is required to save crash stack trace!";
		
		new AlertDialog.Builder(this)
			.setTitle(R.string.error_fatal)
			.setMessage(errHeader + "\n\n" + stackTrace)
			.setPositiveButton(android.R.string.ok, (p1, p2) -> finish())
			.setNegativeButton(R.string.global_restart, (p1, p2) -> startActivity(new Intent(FatalErrorActivity.this, PojavLoginActivity.class)))
			.setNeutralButton(android.R.string.copy, (p1, p2) -> {
				ClipboardManager mgr = (ClipboardManager) FatalErrorActivity.this.getSystemService(CLIPBOARD_SERVICE);
				mgr.setPrimaryClip(ClipData.newPlainText("error", stackTrace));

				finish();
			})
			.setCancelable(false)
			.show();
	}

	public static void showError(Context ctx, String savePath, boolean storageAllow, Throwable th) {
		Intent ferrorIntent = new Intent(ctx, FatalErrorActivity.class);
		ferrorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		ferrorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ferrorIntent.putExtra("throwable", th);
		ferrorIntent.putExtra("savePath", savePath);
		ferrorIntent.putExtra("storageAllow", storageAllow);
		ctx.startActivity(ferrorIntent);
	}
}
