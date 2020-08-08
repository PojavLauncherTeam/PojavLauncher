package net.kdt.pojavlaunch;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;

public class FatalErrorActivity extends AppCompatActivity
{
	public static void showError(Context ctx, boolean isFatalErr, Throwable th) {
		Intent ferrorIntent = new Intent(ctx, FatalErrorActivity.class);
		ferrorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		ferrorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ferrorIntent.putExtra("throwable", th);
		ferrorIntent.putExtra("isFatal", isFatalErr);
		ctx.startActivity(ferrorIntent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		Throwable th = (Throwable) extras.getSerializable("throwable");
		boolean isFatalError = extras.getBoolean("isFatal", false);
		
		Tools.showError(this, isFatalError ? R.string.error_fatal : R.string.global_error, th, true);
	}
}
