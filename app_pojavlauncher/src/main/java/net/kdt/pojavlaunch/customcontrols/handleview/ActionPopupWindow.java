/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This class has been splited from android/widget/Editor$HandleView.java
 */
package net.kdt.pojavlaunch.customcontrols.handleview;

import android.app.Dialog;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.*;
import android.view.ViewGroup.*;
import android.widget.*;

import net.kdt.pojavlaunch.*;

import android.view.View.OnClickListener;
import net.kdt.pojavlaunch.customcontrols.*;
import androidx.appcompat.app.*;

import com.rarepebble.colorpicker.ColorPickerView;

public class ActionPopupWindow extends PinnedPopupWindow implements OnClickListener {
	private TextView mEditTextView;
	private TextView mDeleteTextView;

	private AlertDialog.Builder alertBuilder;
	private Dialog dialog;

	private EditText editName;
	private Spinner[] spinnersKeycode;

	private CheckBox checkToggle;
	private CheckBox checkPassThrough;
	private CheckBox checkDynamicPosition;
	private CheckBox checkHoldAlt;
	private CheckBox checkHoldCtrl;
	private CheckBox checkHoldShift;

	private EditText editWidth;
	private EditText editHeight;
	private EditText editDynamicX;
	private EditText editDynamicY;

	private SeekBar seekBarOpacity;
	private SeekBar seekBarCornerRadius;
	private SeekBar seekBarStrokeWidth;

	private ImageButton buttonBackgroundColor;
	private ImageButton buttonStrokeColor;

	private TextView textOpacity;
	private TextView textCornerRadius;
	private TextView textStrokeWidth;

	private ControlData properties;

	private ArrayAdapter<String> adapter;
	private String[] specialArr;




	public ActionPopupWindow(HandleView handleView) {
		super(handleView);
	}

	@Override
	protected void createPopupWindow() {
		mPopupWindow = new PopupWindow(mHandleView.getContext(), null, android.R.attr.textSelectHandleWindowStyle);
		mPopupWindow.setClippingEnabled(true);
	}

	@Override
	protected void initContentView() {
		LinearLayout linearLayout = new LinearLayout(mHandleView.getContext());
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		mContentView = linearLayout;
		mContentView.setBackgroundResource(R.drawable.control_side_action_window);

		LayoutInflater inflater = (LayoutInflater) mHandleView.getContext().
			getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LayoutParams wrapContent = new LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		mEditTextView = (TextView) inflater.inflate(R.layout.control_action_popup_text, null);
		mEditTextView.setLayoutParams(wrapContent);
		mContentView.addView(mEditTextView);
		mEditTextView.setText(R.string.global_edit);
		mEditTextView.setOnClickListener(this);

		mDeleteTextView = (TextView) inflater.inflate(R.layout.control_action_popup_text, null);
		mDeleteTextView.setLayoutParams(wrapContent);
		mContentView.addView(mDeleteTextView);
		mDeleteTextView.setText(R.string.global_remove);
		mDeleteTextView.setOnClickListener(this);
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void onClick(final View view) {
		alertBuilder = new AlertDialog.Builder(view.getContext());
		alertBuilder.setCancelable(false);




		if (view == mEditTextView) {
			properties = mHandleView.mView.getProperties();
			initializeEditDialog(view.getContext());





			dialog.show();
		} else if (view == mDeleteTextView) {
			alertBuilder.setMessage(view.getContext().getString(R.string.customctrl_remove, mHandleView.mView.getText()) + "?");

			alertBuilder.setPositiveButton(R.string.global_remove, (p1, p2) -> {
				ControlLayout layout = ((ControlLayout) mHandleView.mView.getParent());
				layout.removeControlButton(mHandleView.mView);
			});
			alertBuilder.setNegativeButton(android.R.string.cancel, null);
			alertBuilder.show();
		}
		
		hide();
	}

	@Override
	protected int getTextOffset() {
		return 0;
	}

	@Override
	protected int getVerticalLocalPosition(int line) {
		return 0;
	}

	@Override
	protected int clipVertically(int positionY) {
		return positionY;
	}

	private void initializeEditDialog(Context ctx){
		//TODO: Support the color picker, stroke width/color and corner radius
		//Create the editing dialog
		LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = layoutInflater.inflate(R.layout.control_setting_v2,null);

		alertBuilder.setTitle(ctx.getResources().getString(R.string.customctrl_edit, mHandleView.mView.getText()));
		alertBuilder.setView(v);

		//Linking a lot of stuff
		editName = v.findViewById(R.id.controlsetting_edit_name);

		spinnersKeycode = new Spinner[]{
				v.findViewById(R.id.controlsetting_spinner_lwjglkeycode),
				v.findViewById(R.id.controlsetting_spinner_lwjglkeycode2),
				v.findViewById(R.id.controlsetting_spinner_lwjglkeycode3),
				v.findViewById(R.id.controlsetting_spinner_lwjglkeycode4)
		};
		checkToggle = v.findViewById(R.id.controlsetting_checkbox_toggle);
		checkPassThrough = v.findViewById(R.id.controlsetting_checkbox_passthru);

		editWidth = v.findViewById(R.id.controlsetting_edit_width);
		editHeight = v.findViewById(R.id.controlsetting_edit_height);

		editDynamicX = v.findViewById(R.id.controlsetting_edit_dynamicpos_x);
		editDynamicY = v.findViewById(R.id.controlsetting_edit_dynamicpos_y);

		seekBarOpacity = v.findViewById(R.id.controlsetting_seek_opacity);
		seekBarCornerRadius = v.findViewById(R.id.controlsetting_seek_corner_radius);
		seekBarStrokeWidth = v.findViewById(R.id.controlsetting_seek_stroke_width);

		buttonBackgroundColor = v.findViewById(R.id.controlsetting_background_color);
		buttonStrokeColor = v.findViewById(R.id.controlsetting_stroke_color);

		textOpacity = v.findViewById(R.id.controlsetting_text_opacity);
		textCornerRadius = v.findViewById(R.id.controlsetting_text_corner_radius);
		textStrokeWidth = v.findViewById(R.id.controlsetting_text_stroke_width);

		checkDynamicPosition = v.findViewById(R.id.controlsetting_checkbox_dynamicpos);
		checkDynamicPosition.setOnCheckedChangeListener((btn, checked) -> {
			editDynamicX.setEnabled(checked);
			editDynamicY.setEnabled(checked);
		});


		checkHoldAlt = v.findViewById(R.id.controlsetting_checkbox_keycombine_alt);
		checkHoldCtrl = v.findViewById(R.id.controlsetting_checkbox_keycombine_control);
		checkHoldShift = v.findViewById(R.id.controlsetting_checkbox_keycombine_shift);

		//Initialize adapter for keycodes
		adapter= new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item);
		String[] oldSpecialArr = ControlData.buildSpecialButtonArray();
		specialArr = new String[oldSpecialArr.length];
		for (int i = 0; i < specialArr.length; i++) {
			specialArr[i] = "SPECIAL_" + oldSpecialArr[specialArr.length - i - 1];
		}
		adapter.addAll(specialArr);
		adapter.addAll(AndroidLWJGLKeycode.generateKeyName());
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

		for (Spinner spinner : spinnersKeycode) {
			spinner.setAdapter(adapter);
		}

		//Set color imageButton behavior
		buttonBackgroundColor.setOnClickListener(view -> showBackgroundColorPicker(view.getContext()));
		buttonStrokeColor.setOnClickListener(view -> showStrokeColorPicker(view.getContext()));

		//Set dialog buttons behavior
		alertBuilder.setPositiveButton(android.R.string.ok, (dialogInterface1, i) -> {
			if(!hasErrors(dialog.getContext())){
				saveProperties();
			}
		});
		alertBuilder.setNegativeButton(android.R.string.cancel, null);

		//Create the finalized dialog
		dialog = alertBuilder.create();

		dialog.setOnShowListener(dialogInterface -> {
			setEditDialogValues();
		});

	}

	private void setEditDialogValues(){

		editName.setText(properties.name);

		checkToggle.setChecked(properties.isToggle);
		checkPassThrough.setChecked(properties.passThruEnabled);

		editWidth.setText(Float.toString(properties.width));
		editHeight.setText(Float.toString(properties.height));

		editDynamicX.setEnabled(properties.isDynamicBtn);
		editDynamicY.setEnabled(properties.isDynamicBtn);
		editDynamicX.setHint(Float.toString(properties.x));
		editDynamicX.setText(properties.dynamicX);
		editDynamicY.setHint(Float.toString(properties.y));
		editDynamicY.setText(properties.dynamicY);

		seekBarOpacity.setProgress(properties.hidden ? 0 : (int)properties.opacity*100);
		seekBarStrokeWidth.setProgress(properties.strokeWidth);
		seekBarCornerRadius.setProgress((int)properties.cornerRadius);

		buttonBackgroundColor.setBackgroundColor(properties.bgColor);
		buttonStrokeColor.setBackgroundColor(properties.strokeColor);

		checkHoldAlt.setChecked(properties.holdAlt);
		checkHoldCtrl.setChecked(properties.holdCtrl);
		checkHoldShift.setChecked(properties.holdShift);
		checkDynamicPosition.setChecked(properties.isDynamicBtn);

		for(int i=0; i< properties.keycodes.length; i++){
			if (properties.keycodes[i] < 0) {
				spinnersKeycode[i].setSelection(properties.keycodes[i] + specialArr.length);
			} else {
				spinnersKeycode[i].setSelection(AndroidLWJGLKeycode.getIndexByLWJGLKey(properties.keycodes[i]) + specialArr.length);
			}
		}
	}

	private void saveProperties(){
		//TODO save new properties
		//This method assumes there are no error.
		properties.name = editName.getText().toString();

		//Keycodes
		for(int i=0; i<spinnersKeycode.length; ++i){
			if (spinnersKeycode[i].getSelectedItemPosition() < specialArr.length) {
				properties.keycodes[i] = spinnersKeycode[i].getSelectedItemPosition() - specialArr.length;
			} else {
				properties.keycodes[i] = AndroidLWJGLKeycode.getKeyByIndex(spinnersKeycode[i].getSelectedItemPosition() - specialArr.length);
			}
		}

		properties.opacity = seekBarOpacity.getProgress()/100f;
		properties.strokeWidth = seekBarStrokeWidth.getProgress();
		properties.cornerRadius = seekBarCornerRadius.getProgress();

		properties.bgColor = ((ColorDrawable) buttonBackgroundColor.getBackground()).getColor();
		properties.strokeColor = ((ColorDrawable) buttonStrokeColor.getBackground()).getColor();

		properties.hidden = false;
		properties.isToggle = checkToggle.isChecked();
		properties.passThruEnabled = checkPassThrough.isChecked();

		properties.width = Float.parseFloat(editWidth.getText().toString());
		properties.height = Float.parseFloat(editHeight.getText().toString());

		properties.isDynamicBtn = checkDynamicPosition.isChecked();
		properties.dynamicX = editDynamicX.getText().toString().isEmpty() ? properties.dynamicX = Float.toString(properties.x) : editDynamicX.getText().toString();
		properties.dynamicY = editDynamicY.getText().toString().isEmpty() ? properties.dynamicY = Float.toString(properties.y) : editDynamicY.getText().toString();

		properties.holdAlt = checkHoldAlt.isChecked();
		properties.holdCtrl = checkHoldCtrl.isChecked();
		properties.holdShift = checkHoldShift.isChecked();

		mHandleView.mView.updateProperties();
	}

	private boolean hasErrors(Context ctx){
		if (editName.getText().toString().isEmpty()) {
			editName.setError(ctx.getResources().getString(R.string.global_error_field_empty));
			return true;
		}

		if (properties.isDynamicBtn) {

			int errorAt = 0;
			try {
				properties.insertDynamicPos(editDynamicX.getText().toString());
				errorAt = 1;
				properties.insertDynamicPos(editDynamicY.getText().toString());
			} catch (Throwable th) {
				(errorAt == 0 ? editDynamicX : editDynamicY).setError(th.getMessage());

				return true;
			}
		}

		return false;
	}


	private void showBackgroundColorPicker(Context ctx){
		ColorPickerView picker = new ColorPickerView(ctx);
		picker.setColor(((ColorDrawable)buttonBackgroundColor.getBackground()).getColor());

		AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
		dialog.setTitle("Edit background color");
		dialog.setView(picker);
		dialog.setNegativeButton(android.R.string.cancel, null);
		dialog.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> buttonBackgroundColor.setBackgroundColor(picker.getColor()));

		dialog.show();
	}

	private void showStrokeColorPicker(Context ctx){
		ColorPickerView picker = new ColorPickerView(ctx);
		picker.setColor(((ColorDrawable)buttonStrokeColor.getBackground()).getColor());
		picker.showAlpha(false);

		AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
		dialog.setTitle("Edit stroke color");
		dialog.setView(picker);
		dialog.setNegativeButton(android.R.string.cancel, null);
		dialog.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> buttonStrokeColor.setBackgroundColor(picker.getColor()));

		dialog.show();
	}

}
