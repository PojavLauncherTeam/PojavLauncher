package net.kdt.pojavlaunch.value.customcontrols;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.view.*;
import com.google.gson.*;
import net.kdt.pojavlaunch.*;
import android.support.v7.app.*;

public class ControlsLayout extends FrameLayout
{
	private boolean mCanMove;
	private CustomControls mLayout;
	public ControlsLayout(Context ctx) {
		super(ctx);
	}
	
	public ControlsLayout(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
	}
	
	private void showCtrlOption(final ControlView view) {
		AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
		alert.setCancelable(false);
		alert.setTitle(getResources().getString(R.string.global_edit) + " " + view.getText());
		alert.setView(R.layout.control_setting);
		alert.setPositiveButton(android.R.string.ok, null);
		alert.setNegativeButton(android.R.string.cancel, null);
		String deleteText = "Delete";
		try {
			// Try to get non-public delete text, which translated to multiple languages.
			deleteText = getResources().getString((int) com.android.internal.R.string.class.getDeclaredField("deleteText").get(null));
		} catch (Throwable unused) {
		}
		
		alert.setNeutralButton(deleteText, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					removeControlButton(view);
				}
			});
		final AlertDialog dialog = alert.create();
		
		final ControlButton properties = view.getProperties();
		
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {

				@Override
				public void onShow(DialogInterface dialogInterface) {
					final LinearLayout normalBtnLayout = dialog.findViewById(R.id.controlsetting_normalbtnlayout);
					
					final EditText editName = dialog.findViewById(R.id.controlsetting_edit_name);
					editName.setText(properties.name);
					
					final Spinner spinnerKeycode = dialog.findViewById(R.id.controlsetting_spinner_lwjglkeycode);
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item);
					
					String[] oldSpecialArr = ControlButton.buildSpecialButtonArray();
					String[] specialArr = new String[oldSpecialArr.length];
					for (int i = 0; i < specialArr.length; i++) {
						specialArr[i] = "SPECIAL_" + oldSpecialArr[i];
					}
					
					adapter.addAll(specialArr);
					adapter.addAll(AndroidLWJGLKeycode.generateKeyName());
					adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
					spinnerKeycode.setAdapter(adapter);
					if (properties.lwjglKeycode < 0) {
						spinnerKeycode.setSelection(properties.lwjglKeycode + 2);
					} else {
						spinnerKeycode.setSelection(AndroidLWJGLKeycode.getIndexByLWJGLKey(properties.lwjglKeycode + 2));
					}
					spinnerKeycode.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

							@Override
							public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
								normalBtnLayout.setVisibility(id < 2 ? View.GONE : View.VISIBLE);
							}

							@Override
							public void onNothingSelected(AdapterView<?> adapter){
								// Unused
							}
						});
					
					final CheckBox checkHidden = dialog.findViewById(R.id.controlsetting_checkbox_hidden);
					checkHidden.setChecked(properties.hidden);
					
					Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
					button.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View view2) {
								if (editName.getText().toString().isEmpty()) {
									editName.setError(getResources().getString(R.string.global_error_field_empty));
								} else {
									properties.lwjglKeycode = AndroidLWJGLKeycode.getKeyIndex(spinnerKeycode.getSelectedItemPosition()) - 2;
									properties.name = editName.getText().toString();
									properties.hidden = checkHidden.isChecked();
									
									view.updateProperties();
									
									dialog.dismiss();
								}
							}
						});
				}
			});
			
		dialog.show();
	}
	
	public void loadLayout(CustomControls controlLayout) {
		mLayout = controlLayout;
		removeAllViews();
		for (ControlButton button : controlLayout.button) {
			addControlView(button);
		}
	}
	
	public void addControlButton(ControlButton controlButton) {
		mLayout.button.add(controlButton);
		addControlView(controlButton);
	}
	
	private void addControlView(ControlButton controlButton) {
		final ControlView view = new ControlView(getContext(), controlButton);
		view.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1) {
					showCtrlOption(view);
				}
			});
		view.setCanMove(mCanMove);
		addView(view);
	}
	
	public void removeControlButton(ControlView controlButton) {
		mLayout.button.remove(controlButton.getProperties());
		removeView(controlButton);
	}
	
	public void saveLayout(String path) throws Exception {
		Tools.write(path, new Gson().toJson(mLayout));
	}
	
	public void setCanMove(boolean z) {
		mCanMove = z;
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (v instanceof ControlView) {
				((ControlView) v).setCanMove(z);
			}
		}
	}
}
