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
package com.kdt.handleview;

import android.content.*;
import android.view.*;
import android.view.View.*;
import android.view.ViewGroup.*;
import android.widget.*;
import java.lang.reflect.*;
import net.kdt.pojavlaunch.*;

import android.view.View.OnClickListener;
import net.kdt.pojavlaunch.customcontrols.*;
import android.support.v7.app.*;
import android.content.res.*;

public class ActionPopupWindow extends PinnedPopupWindow implements OnClickListener {
	private final int POPUP_TEXT_LAYOUT = getInternalId("layout", "text_edit_action_popup_text");
	private TextView mEditTextView;
	private TextView mDeleteTextView;
	
	public ActionPopupWindow(HandleView handleView) {
		super(handleView);
	}

	private int getInternalId(String type, String name) {
        int id = Resources.getSystem().getIdentifier(name, type, "com.android.internal");
        if (id == 0) {
            mHandleView.getContext().getResources().getIdentifier(name, type, "android");
        }
		return id;
	}
	
	@Override
	protected void createPopupWindow() {
		mPopupWindow = new PopupWindow(mHandleView.getContext(), null, getInternalId("attr", "textSelectHandleWindowStyle"));
		mPopupWindow.setClippingEnabled(true);
	}

	@Override
	protected void initContentView() {
		LinearLayout linearLayout = new LinearLayout(mHandleView.getContext());
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		mContentView = linearLayout;
		mContentView.setBackgroundResource(getInternalId("drawable", "text_edit_paste_window"));

		LayoutInflater inflater = (LayoutInflater) mHandleView.getContext().
			getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LayoutParams wrapContent = new LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		mEditTextView = (TextView) inflater.inflate(POPUP_TEXT_LAYOUT, null);
		mEditTextView.setLayoutParams(wrapContent);
		mContentView.addView(mEditTextView);
		mEditTextView.setText(R.string.global_edit);
		mEditTextView.setOnClickListener(this);

		mDeleteTextView = (TextView) inflater.inflate(POPUP_TEXT_LAYOUT, null);
		mDeleteTextView.setLayoutParams(wrapContent);
		mContentView.addView(mDeleteTextView);
		mDeleteTextView.setText(getInternalId("string", "delete"));
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
			alert.setTitle(view.getResources().getString(R.string.global_edit) + " " + mHandleView.mView.getText());
			alert.setView(R.layout.control_setting);
			alert.setPositiveButton(android.R.string.ok, null);
			alert.setNegativeButton(android.R.string.cancel, null);
			final AlertDialog dialog = alert.create();
			final ControlData properties = mHandleView.mView.getProperties();

			dialog.setOnShowListener(new DialogInterface.OnShowListener() {

					@Override
					public void onShow(DialogInterface dialogInterface) {
						final LinearLayout normalBtnLayout = dialog.findViewById(R.id.controlsetting_normalbtnlayout);

						final EditText editName = dialog.findViewById(R.id.controlsetting_edit_name);
						editName.setText(properties.name);

						final Spinner spinnerKeycode = dialog.findViewById(R.id.controlsetting_spinner_lwjglkeycode);
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item);

						String[] oldSpecialArr = ControlData.buildSpecialButtonArray();
						final String[] specialArr = new String[oldSpecialArr.length];
						for (int i = 0; i < specialArr.length; i++) {
							specialArr[i] = "SPECIAL_" + oldSpecialArr[i];
						}

						adapter.addAll(specialArr);
						adapter.addAll(AndroidLWJGLKeycode.generateKeyName());
						adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
						spinnerKeycode.setAdapter(adapter);
						if (properties.keycode < 0) {
							spinnerKeycode.setSelection(properties.keycode + specialArr.length);
						} else {
							spinnerKeycode.setSelection(AndroidLWJGLKeycode.getIndexByLWJGLKey(properties.keycode));
						}
						spinnerKeycode.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

								@Override
								public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
									normalBtnLayout.setVisibility(id < specialArr.length ? View.GONE : View.VISIBLE);
								}

								@Override
								public void onNothingSelected(AdapterView<?> adapter){
									// Unused
								}
							});

						final CheckBox checkHidden = dialog.findViewById(R.id.controlsetting_checkbox_hidden);
						checkHidden.setChecked(properties.hidden);
                        
                        final CheckBox checkDynamicPos = dialog.findViewById(R.id.controlsetting_checkbox_hidden);
                        checkDynamicPos.setChecked(properties.isDynamicBtn);

                        final LinearLayout layoutDynamicBtn = dialog.findViewById(R.id.controlsetting_dynamicbtnlayout);
                        
                        final EditText editDynamicX = dialog.findViewById(R.id.controlsetting_edit_dynamicpos_x);
                        final EditText editDynamicY = dialog.findViewById(R.id.controlsetting_edit_dynamicpos_y);
                        
                        editDynamicX.setHint(Float.toString(properties.x));
                        editDynamicX.setText(properties.dynamicX);
                        
                        editDynamicY.setHint(Float.toString(properties.y));
                        editDynamicY.setText(properties.dynamicY);
                        
                        checkDynamicPos.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

                                @Override
                                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                                    layoutDynamicBtn.setVisibility(checked ? View.VISIBLE : View.GONE);
                                }
                            });
                        
						Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
						button.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View view2) {
									if (editName.getText().toString().isEmpty()) {
										editName.setError(view.getResources().getString(R.string.global_error_field_empty));
									} else {
                                        if (spinnerKeycode.getSelectedItemPosition() < specialArr.length) {
										    properties.keycode = spinnerKeycode.getSelectedItemPosition() - specialArr.length;
                                        } else {
                                            properties.keycode = AndroidLWJGLKeycode.getKeyByIndex(spinnerKeycode.getSelectedItemPosition() - specialArr.length);
                                        }
										properties.name = editName.getText().toString();
										properties.hidden = checkHidden.isChecked();
                                        properties.isDynamicBtn = checkDynamicPos.isChecked();
                                        properties.dynamicX = editDynamicX.getText().toString();
                                        properties.dynamicY = editDynamicY.getText().toString();
                                        
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
			alert.setMessage(view.getResources().getString(getInternalId("string", "delete")) + " " + mHandleView.mView.getText() + "?");
			alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

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
