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
import android.graphics.drawable.ColorDrawable;
import android.view.*;
import android.view.ViewGroup.*;
import android.widget.*;

import net.kdt.pojavlaunch.*;

import android.view.View.OnClickListener;
import net.kdt.pojavlaunch.customcontrols.*;
import net.kdt.pojavlaunch.customcontrols.buttons.ControlButton;
import net.kdt.pojavlaunch.customcontrols.buttons.ControlDrawer;
import net.kdt.pojavlaunch.customcontrols.buttons.ControlSubButton;

import androidx.appcompat.app.*;

import com.rarepebble.colorpicker.ColorPickerView;

public class ActionPopupWindow extends PinnedPopupWindow implements OnClickListener {

	private TextView mEditTextView;
	private TextView mDeleteTextView;
	private TextView mCloneTextView;

	private ControlButton editedButton;

	public ActionPopupWindow(HandleView handleView, ControlButton button){
		super(handleView);
		this.editedButton = button;
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

		mCloneTextView = (TextView) inflater.inflate(R.layout.control_action_popup_text, null);
		mCloneTextView.setLayoutParams(wrapContent);
		mContentView.addView(mCloneTextView);
		mCloneTextView.setText(R.string.global_clone);
		mCloneTextView.setOnClickListener(this);
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void onClick(final View view) {
		if (view == mEditTextView) {

			if(editedButton instanceof ControlSubButton){
				new EditControlSubButtonPopup((ControlSubButton) editedButton);
				return;
			}

			if(editedButton instanceof ControlDrawer){
				new EditControlDrawerPopup((ControlDrawer) editedButton);
				return;
			}

			if(editedButton instanceof ControlButton){
				new EditControlButtonPopup((ControlButton) editedButton);
				return;
			}


		} else if (view == mDeleteTextView) {
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(view.getContext());
			alertBuilder.setCancelable(false);
			alertBuilder.setMessage(view.getContext().getString(R.string.customctrl_remove, mHandleView.mView.getText()) + "?");

			alertBuilder.setPositiveButton(R.string.global_remove, (p1, p2) -> {
				ControlLayout layout = ((ControlLayout) mHandleView.mView.getParent());

				if(editedButton instanceof ControlSubButton){
					layout.removeControlSubButton((ControlSubButton) editedButton);
					return;
				}

				if(editedButton instanceof ControlDrawer){
					layout.removeControlDrawer((ControlDrawer) editedButton);
					return;
				}

				if(editedButton instanceof ControlButton){
					layout.removeControlButton((ControlButton) editedButton);
				}

				layout.removeControlButton(mHandleView.mView);
			});
			alertBuilder.setNegativeButton(android.R.string.cancel, null);
			alertBuilder.show();
		}else if(view == mCloneTextView) {
			if(editedButton instanceof ControlDrawer){
				ControlDrawerData cloneData = new ControlDrawerData(((ControlDrawer)editedButton).getDrawerData());
				cloneData.properties.dynamicX = "0.5 * ${screen_width}";
				cloneData.properties.dynamicY = "0.5 * ${screen_height}";
				((ControlLayout) mHandleView.mView.getParent()).addDrawer(cloneData);
			}else{
				ControlData cloneData = new ControlData(editedButton.getProperties());
				cloneData.dynamicX = "0.5 * ${screen_width}";
				cloneData.dynamicY = "0.5 * ${screen_height}";
				((ControlLayout) mHandleView.mView.getParent()).addControlButton(cloneData);
			}

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


	public static void showColorPicker(Context ctx,String title, boolean showAlpha, ImageView v){
		int startColor = ((ColorDrawable)v.getDrawable()).getColor();

		ColorPickerView picker = new ColorPickerView(ctx);
		picker.setColor(startColor);
		picker.showAlpha(showAlpha);

		AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
		dialog.setTitle(title);
		dialog.setView(picker);
		dialog.setNegativeButton(android.R.string.cancel, null);
		dialog.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> v.setImageDrawable(new ColorDrawable(picker.getColor())));

		dialog.show();
	}

	public static void setPercentageText(TextView textView, int progress){
		textView.setText(progress + " %");
	}

}
