package net.kdt.pojavlaunch.prefs;

import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.CompoundButton.*;
import android.widget.SeekBar.*;
import com.kdt.mcgui.app.*;
import com.pojavdx.dex.*;
import java.lang.reflect.*;
import net.kdt.pojavlaunch.*;

public class PojavPreferenceActivity extends MineActivity implements OnCheckedChangeListener, OnSeekBarChangeListener
{
	public static boolean PREF_FREEFORM = false;
	public static boolean PREF_FORGETOF = false;
	public static float PREF_BUTTONSIZE = 1.0f;
	
	private SeekBar viewSeekDxRef, viewSeekControlSize;
	private TextView viewSeekProgressDxRef, viewSeekProgressControl;
	private Switch viewSwitchFreeform, viewSwitchForgetOF;
	private CheckBox viewCheckVTypeRelease, viewCheckVTypeSnapshot, viewCheckVTypeOldAlpha, viewCheckVTypeOldBeta;
	
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

		LauncherPreferences.loadPreferences(this);
		
		mainPreference = getSharedPreferences("pojav_preferences", MODE_PRIVATE);
		final SharedPreferences.Editor mainPrefEdit = mainPreference.edit();
		
		// DX Refs
		viewSeekDxRef = (SeekBar) findView(R.id.settings_seekbar_setmaxdxref);
		viewSeekProgressDxRef = (TextView) findView(R.id.setting_progressseek_maxdxref);
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
		viewSeekControlSize = (SeekBar) findView(R.id.settings_seekbar_controlsize);
		viewSeekProgressControl = (TextView) findView(R.id.setting_progressseek_control);
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
		viewSwitchFreeform = (Switch) findView(R.id.settings_switch_enablefreeform);
		viewSwitchFreeform.setChecked(PREF_FREEFORM);
		viewSwitchFreeform.setEnabled(Build.VERSION.SDK_INT >= 24);
		
		// Forget OptiFine path
		viewSwitchForgetOF = (Switch) findView(R.id.settings_switch_forgetoptifpath);
		viewSwitchForgetOF.setChecked(PREF_FORGETOF);
		
		viewCheckVTypeRelease = (CheckBox) findView(R.id.settings_checkbox_vertype_release);
		viewCheckVTypeSnapshot = (CheckBox) findView(R.id.settings_checkbox_vertype_snapshot);
		viewCheckVTypeOldAlpha = (CheckBox) findView(R.id.settings_checkbox_vertype_oldalpha);
		viewCheckVTypeOldBeta = (CheckBox) findView(R.id.settings_checkbox_vertype_oldbeta);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		LauncherPreferences.loadPreferences(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
		String prefName = null;
		switch (btn.getId()) {
			case R.id.settings_switch_enablefreeform:
				
				prefName = "freeform";
				break;
			case R.id.settings_switch_forgetoptifpath:
				prefName = "forgetOptifinePath";
				break;
			case R.id.settings_checkbox_vertype_release:
				prefName = "vertype_release";
				break;
			case R.id.settings_checkbox_vertype_snapshot:
				prefName = "vertype_snapshot";
				break;
			case R.id.settings_checkbox_vertype_oldalpha:
				prefName = "vertype_oldalpha";
				break;
			case R.id.settings_checkbox_vertype_oldbeta:
				prefName = "vertype_oldbeta";
				break;
		}
		
		mainPreference.edit()
			.putBoolean(prefName, isChecked)
			.commit();
	}

	@Override
	public void onProgressChanged(SeekBar p1, int p2, boolean p3) {
		// Unused
	}

	@Override
	public void onStartTrackingTouch(SeekBar bar) {
		// Unused
	}

	@Override
	public void onStopTrackingTouch(SeekBar bar) {
		float currProgress = (float) bar.getProgress() / 100;
		String progressStr = Float.toString(currProgress);
		if (progressStr.length() == 3) progressStr = progressStr + "0";
		
		try {
			Field field = R.id.class.getDeclaredField(getId(bar.getId()).replace("seekbar", "progressseek"));
			((TextView) findViewById((Integer) field.get(null))).setText(currProgress + "/" + bar.getMax());
		} catch (Throwable th) {
			throw new RuntimeException(th);
		}
	}
	
	public View findView(int id) {
		View view = findView(id);
		if (view instanceof CompoundButton) {
			((CompoundButton) view).setOnCheckedChangeListener(this);
		}
		return view;
	}
	
	private String getId(int id) {
		if (id == View.NO_ID) return "unknown";
		else return getResources().getResourceEntryName(id);
	}
	
}
