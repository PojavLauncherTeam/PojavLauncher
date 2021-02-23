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

import android.content.*;
import android.view.*;
import android.view.ViewGroup.*;
import android.widget.*;

import net.kdt.pojavlaunch.*;

import android.view.View.OnClickListener;
import net.kdt.pojavlaunch.customcontrols.*;
import androidx.appcompat.app.*;

public class ActionPopupWindow extends PinnedPopupWindow implements OnClickListener {
	private TextView mEditTextView;
	private TextView mDeleteTextView;
	
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
		AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
		alert.setCancelable(false);
		if (view == mEditTextView) {
			alert.setTitle(view.getResources().getString(R.string.customctrl_edit, mHandleView.mView.getText()));
			alert.setView(R.layout.control_setting);
			alert.setPositiveButton(android.R.string.ok, null);
			alert.setNegativeButton(android.R.string.cancel, null);
			final AlertDialog dialog = alert.create();
			final ControlData properties = mHandleView.mView.getProperties();

			dialog.setOnShowListener(new DialogInterface.OnShowListener() {

					@Override
					public void onShow(DialogInterface dialogInterface) {
						final EditText editName = dialog.findViewById(R.id.controlsetting_edit_name);
						editName.setText(properties.name);

						final Spinner spinnerKeycode = dialog.findViewById(R.id.controlsetting_spinner_lwjglkeycode);
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item);

						String[] oldSpecialArr = ControlData.buildSpecialButtonArray();
						final String[] specialArr = new String[oldSpecialArr.length];
						for (int i = 0; i < specialArr.length; i++) {
							specialArr[i] = "SPECIAL_" + oldSpecialArr[specialArr.length - i - 1];
						}

						adapter.addAll(specialArr);
						adapter.addAll(AndroidLWJGLKeycode.generateKeyName());
						adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
						spinnerKeycode.setAdapter(adapter);
						if (properties.keycode < 0) {
							spinnerKeycode.setSelection(properties.keycode + specialArr.length);
						} else {
							spinnerKeycode.setSelection(AndroidLWJGLKeycode.getIndexByLWJGLKey(properties.keycode) + specialArr.length);
						}

                        final CheckBox checkToggle = dialog.findViewById(R.id.controlsetting_checkbox_toggle);
                        checkToggle.setChecked(properties.isToggle);
                        final CheckBox checkPassthru = dialog.findViewById(R.id.controlsetting_checkbox_passthru);
                        checkPassthru.setChecked(properties.passThruEnabled);
                        final CheckBox checkRound = dialog.findViewById(R.id.controlsetting_checkbox_round);
                        checkRound.setChecked(properties.isRound);
                        final EditText editWidth = dialog.findViewById(R.id.controlsetting_edit_width);
                        final EditText editHeight = dialog.findViewById(R.id.controlsetting_edit_height);
                        editWidth.setText(Float.toString(properties.width));
                        editHeight.setText(Float.toString(properties.height));
                        
                        final EditText editDynamicX = dialog.findViewById(R.id.controlsetting_edit_dynamicpos_x);
                        final EditText editDynamicY = dialog.findViewById(R.id.controlsetting_edit_dynamicpos_y);
                        editDynamicX.setEnabled(properties.isDynamicBtn);
                        editDynamicY.setEnabled(properties.isDynamicBtn);
                        
                        final SeekBar seekTransparency = dialog.findViewById(R.id.controlsetting_seek_transparency);
                        seekTransparency.setMax(100);
                        seekTransparency.setProgress(properties.hidden ? 100 : properties.transparency);
                        
                        final CheckBox checkDynamicPos = dialog.findViewById(R.id.controlsetting_checkbox_dynamicpos);
                        checkDynamicPos.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

                                @Override
                                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                                    editDynamicX.setEnabled(checked);
                                    editDynamicY.setEnabled(checked);
                                }
                            });
                        checkDynamicPos.setChecked(properties.isDynamicBtn);
                        
                        editDynamicX.setHint(Float.toString(properties.x));
                        editDynamicX.setText(properties.dynamicX);
                        
                        editDynamicY.setHint(Float.toString(properties.y));
                        editDynamicY.setText(properties.dynamicY);
                        
                        final CheckBox checkHoldAlt = dialog.findViewById(R.id.controlsetting_checkbox_keycombine_alt);
                        checkHoldAlt.setChecked(properties.holdAlt);

                        final CheckBox checkHoldControl = dialog.findViewById(R.id.controlsetting_checkbox_keycombine_control);
                        checkHoldControl.setChecked(properties.holdCtrl);

                        final CheckBox checkHoldShift = dialog.findViewById(R.id.controlsetting_checkbox_keycombine_shift);
                        checkHoldShift.setChecked(properties.holdShift);
                            
						Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
						button.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View view2) {
									if (editName.getText().toString().isEmpty()) {
										editName.setError(view.getResources().getString(R.string.global_error_field_empty));
									} else {
                                        /*
                                        String errorAt = null;
                                        try {
                                            errorAt = "DynamicX";
                                            properties.insertDynamicPos(editDynamicX.getText().toString());
                                            errorAt = "DynamicY";
                                            properties.insertDynamicPos(editDynamicY.getText().toString());
                                        } catch (Throwable th) {
                                            Error e = new Error(errorAt, th);
                                            e.setStackTrace(null);
                                            Tools.showError(view.getContext(), e);
                                            return;
                                        }
                                        errorAt = null;
                                        */
                                        
                                        if (properties.isDynamicBtn) {
                                            int errorAt = 0;
                                            try {
                                                properties.insertDynamicPos(editDynamicX.getText().toString());
                                                errorAt = 1;
                                                properties.insertDynamicPos(editDynamicY.getText().toString());
                                            } catch (Throwable th) {
                                                (errorAt == 0 ? editDynamicX : editDynamicY)
                                                    .setError(th.getMessage());

                                                return;
                                            }
                                        }
                                        
                                        if (spinnerKeycode.getSelectedItemPosition() < specialArr.length) {
										    properties.keycode = spinnerKeycode.getSelectedItemPosition() - specialArr.length;
                                        } else {
                                            properties.keycode = AndroidLWJGLKeycode.getKeyByIndex(spinnerKeycode.getSelectedItemPosition() - specialArr.length);
                                        }
										properties.name = editName.getText().toString();
                                        
                                        properties.transparency = seekTransparency.getProgress();
                                        
										properties.hidden = false;
                                        properties.isToggle = checkToggle.isChecked();
                                        properties.passThruEnabled = checkPassthru.isChecked();
                                        properties.isRound = checkRound.isChecked();
                                        properties.isDynamicBtn = checkDynamicPos.isChecked();
                                        properties.width = Float.parseFloat(editWidth.getText().toString());
                                        properties.height = Float.parseFloat(editHeight.getText().toString());
                                        properties.dynamicX = editDynamicX.getText().toString();    
                                        properties.dynamicY = editDynamicY.getText().toString();

                                        properties.holdAlt = checkHoldAlt.isChecked();
                                        properties.holdCtrl = checkHoldControl.isChecked();
                                        properties.holdShift = checkHoldShift.isChecked();
                                        
                                        if (properties.dynamicX.isEmpty()) {
                                            properties.dynamicX = Float.toString(properties.x);
                                        } if (properties.dynamicY.isEmpty()) {
                                            properties.dynamicY = Float.toString(properties.y);
                                        }

										mHandleView.mView.updateProperties();

										dialog.dismiss();
									}
								}
							});
					}
				});

			dialog.show();
		} else if (view == mDeleteTextView) {
			alert.setMessage(view.getContext().getString(R.string.customctrl_remove, mHandleView.mView.getText()) + "?");
			alert.setPositiveButton(R.string.global_remove, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						ControlLayout layout = ((ControlLayout) mHandleView.mView.getParent());
						layout.removeControlButton(mHandleView.mView);
					}
				});
			alert.setNegativeButton(android.R.string.cancel, null);
			alert.show();
		}
		
		hide();
	}

	@Override
	protected int getTextOffset() {
		return 0;
		// return (mTextView.getSelectionStart() + mTextView.getSelectionEnd()) / 2;
	}

	@Override
	protected int getVerticalLocalPosition(int line) {
		// return mTextView.getLayout().getLineTop(line) - mContentView.getMeasuredHeight();
		return 0;
	}

	@Override
	protected int clipVertically(int positionY) {
		/*
		if (positionY < 0) {
			final int offset = getTextOffset();
			final Layout layout = mTextView.getLayout();
			final int line = layout.getLineForOffset(offset);
			positionY += layout.getLineBottom(line) - layout.getLineTop(line);
			positionY += mContentView.getMeasuredHeight();

			// Assumes insertion and selection handles share the same height
			final Drawable handle = mHandleView.getContext().getDrawable(
				mTextView.mTextSelectHandleRes);
			positionY += handle.getIntrinsicHeight();
		}
		*/

		return positionY;
	}
}
