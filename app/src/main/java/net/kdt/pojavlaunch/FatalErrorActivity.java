package net.kdt.pojavlaunch;

import android.support.v7.app.*;
import android.os.*;

public class FatalErrorActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Throwable th = (Throwable) getIntent().getExtras().getSerializable("throwable");
		Tools.showError(this, R.string.error_fatal, th, true);
	}
}
