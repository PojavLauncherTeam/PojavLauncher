package net.kdt.pojavlaunch.prefs;

import net.kdt.pojavlaunch.*;
import android.os.*;
import android.support.v7.app.*;
import android.widget.*;
import android.content.*;
import com.pojavdx.dex.*;
import com.kdt.mcgui.app.*;

public class PojavPreferenceActivity extends MineActivity
{
	public static boolean PREF_FREEFORM = false;
	public static float PREF_BUTTONSIZE = 1.0f;
	
	private SeekBar viewSeekDxRef, viewSeekControlSize;
	private TextView viewSeekProgressDxRef, viewSeekProgressControl;
	private Switch viewSwitchFreeform;
	
	private SharedPreferences mainPreference;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		/* Unable to setting to PreferenceActivity:
		 *
		 * - Unable to set custom Views.
		 * - Having trouble setting.
		 */

		PojavPreferenceActivity.loadPreferences(this);
		
		mainPreference = getSharedPreferences("pojav_preferences", MODE_PRIVATE);
		final SharedPreferences.Editor mainPrefEdit = mainPreference.edit();
		
		// DX Refs
		viewSeekDxRef = (SeekBar) findViewById(R.id.settings_seekbar_setmaxdxref);
		viewSeekProgressDxRef = (TextView) findViewById(R.id.setting_progressseek_maxdxref);
		viewSeekDxRef.setMax(0xFFFF - 0xFFF);
		viewSeekDxRef.setProgress(DexFormat.MAX_MEMBER_IDX - 0xFFF);
		viewSeekDxRef.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
			private int currProgress = 0;
				@Override
				public void onProgressChanged(SeekBar bar, int progress, boolean p3) {
					currProgress = 0xFFF + progress;
					viewSeekProgressDxRef.setText(currProgress + "/" + 0xFFFF);
				}

				@Override
				public void onStartTrackingTouch(SeekBar bar) {
					// Unused
				}

				@Override
				public void onStopTrackingTouch(SeekBar bar) {
					mainPrefEdit.putInt("maxDxRefs", currProgress);
					mainPrefEdit.commit();
				}
			});
		viewSeekProgressDxRef.setText((viewSeekDxRef.getProgress() + 0xFFF) + "/" + 0xFFFF);

		// Control size
		viewSeekControlSize = (SeekBar) findViewById(R.id.settings_seekbar_controlsize);
		viewSeekProgressControl = (TextView) findViewById(R.id.setting_progressseek_control);
		viewSeekControlSize.setMax(200);
		viewSeekControlSize.setProgress((int) (PREF_BUTTONSIZE * 100));
		viewSeekControlSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
				private float currProgress = 1.0f;
				@Override
				public void onProgressChanged(SeekBar bar, int progress, boolean p3) {
					currProgress = (float) progress / 100;
					String progressStr = Float.toString(currProgress);
					if (progressStr.length() == 3) progressStr = progressStr + "0";
					viewSeekProgressControl.setText(currProgress + "/2.00");
				}

				@Override
				public void onStartTrackingTouch(SeekBar bar) {
					// Unused
				}

				@Override
				public void onStopTrackingTouch(SeekBar bar) {
					mainPrefEdit.putFloat("controlSize", currProgress);
					mainPrefEdit.commit();
				}
			});
		viewSeekProgressControl.setText(((float) viewSeekControlSize.getProgress() / 100f) + "/2");
		
		// Freeform mode
		viewSwitchFreeform = (Switch) findViewById(R.id.settings_switch_enablefreeform);
		viewSwitchFreeform.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton b, boolean z)
				{
					mainPrefEdit.putBoolean("freeform", z);
					mainPrefEdit.commit();
				}
			});
		viewSwitchFreeform.setChecked(PREF_FREEFORM);
		viewSwitchFreeform.setEnabled(Build.VERSION.SDK_INT >= 24);
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		PojavPreferenceActivity.loadPreferences(this);
	}
	
	public static void loadPreferences(Context ctx) {
		SharedPreferences mainPreference = ctx.getSharedPreferences("pojav_preferences", MODE_PRIVATE);
		int maxDxPref = mainPreference.getInt("maxDxRefs", 0xFFF);
		DexFormat.MAX_MEMBER_IDX = maxDxPref;
		DexFormat.MAX_TYPE_IDX = maxDxPref;
		
		PREF_BUTTONSIZE = mainPreference.getFloat("controlSize", 1f);
		PREF_FREEFORM = mainPreference.getBoolean("freeform", false);
	}
}
