package net.kdt.pojavlaunch;

import android.support.v7.app.*;
import android.os.*;

public class FatalErrorActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		Throwable th = (Throwable) extras.getSerializable("throwable");
		boolean isFatalError = extras.getBoolean("isFatal", false);
		
		Tools.showError(this, isFatalError ? R.string.error_fatal : R.string.global_error, th, true);
	}
}
