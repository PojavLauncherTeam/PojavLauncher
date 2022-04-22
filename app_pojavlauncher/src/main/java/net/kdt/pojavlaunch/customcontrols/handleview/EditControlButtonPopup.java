package net.kdt.pojavlaunch.customcontrols.handleview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import net.kdt.pojavlaunch.EfficientAndroidLWJGLKeycode;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.colorselector.ColorSelector;
import net.kdt.pojavlaunch.customcontrols.buttons.ControlButton;
import net.kdt.pojavlaunch.customcontrols.ControlData;

import top.defaults.checkerboarddrawable.CheckerboardDrawable;

import static net.kdt.pojavlaunch.customcontrols.handleview.ActionPopupWindow.setPercentageText;

public class EditControlButtonPopup {

    protected AlertDialog mDialog;
    protected View mRootView;
    protected AlertDialog.Builder mBuilder;

    protected EditText mNameEditText;
    protected Spinner[] mKeycodeSpinners;

    protected CheckBox mToggleCheckbox;
    protected CheckBox mPassthroughCheckbox;
    protected CheckBox mSwipeableCheckbox;
    protected CheckBox mDynamicPositionCheckbox;

    protected EditText mWidthEditText;
    protected EditText mHeightEditText;
    protected EditText mDynamicXEditText;
    protected EditText mDynamicYEditText;

    protected SeekBar mOpacitySeekbar;
    protected SeekBar mCornerRadiusSeekbar;
    protected SeekBar mStrokeWidthSeekbar;

    protected ImageButton mBackgroundColorButton;
    protected ImageButton mStrokeColorButton;

    protected TextView mOpacityTextView;
    protected TextView mCornerRadiusTextView;
    protected TextView mStrokeWidthTextView;
    protected TextView mStrokeColorTextView;

    protected ColorSelector mColorSelector;
    protected ImageView mEditingView;

    protected final ControlButton mControlButton;
    protected final ControlData mProperties;

    protected ArrayAdapter<String> mAdapter;
    protected String[] mSpecialArray;


    public EditControlButtonPopup(ControlButton button){
        this.mControlButton = button;
        this.mProperties = button.getProperties();

        initializeEditDialog(button.getContext());

        //Create the finalized dialog
        mDialog = mBuilder.create();
        mDialog.setOnShowListener(dialogInterface -> setEditDialogValues());

        mDialog.show();
    }

    protected void initializeEditDialog(Context ctx){
        //Create the editing dialog
        LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = layoutInflater.inflate(R.layout.dialog_control_button_setting,null);

        mBuilder = new AlertDialog.Builder(ctx);
        mBuilder.setTitle(ctx.getResources().getString(R.string.customctrl_edit, mProperties.name));
        mBuilder.setView(mRootView);

        //Linking a lot of stuff
        mNameEditText = mRootView.findViewById(R.id.editName_editText);

        mKeycodeSpinners = new Spinner[]{
                mRootView.findViewById(R.id.editMapping_spinner_1),
                mRootView.findViewById(R.id.editMapping_spinner_2),
                mRootView.findViewById(R.id.editMapping_spinner_3),
                mRootView.findViewById(R.id.editMapping_spinner_4)
        };

        mToggleCheckbox = mRootView.findViewById(R.id.checkboxToggle);
        mPassthroughCheckbox = mRootView.findViewById(R.id.checkboxPassThrough);
        mSwipeableCheckbox = mRootView.findViewById(R.id.checkboxSwipeable);

        mWidthEditText = mRootView.findViewById(R.id.editSize_editTextX);
        mHeightEditText = mRootView.findViewById(R.id.editSize_editTextY);

        mDynamicXEditText = mRootView.findViewById(R.id.editDynamicPositionX_editText);
        mDynamicYEditText = mRootView.findViewById(R.id.editDynamicPositionY_editText);

        mOpacitySeekbar = mRootView.findViewById(R.id.editButtonOpacity_seekbar);
        mCornerRadiusSeekbar = mRootView.findViewById(R.id.editCornerRadius_seekbar);
        mStrokeWidthSeekbar = mRootView.findViewById(R.id.editStrokeWidth_seekbar);

        SeekBar.OnSeekBarChangeListener changeListener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(seekBar.equals(mCornerRadiusSeekbar)) {
                    setPercentageText(mCornerRadiusTextView, i);
                    return;
                }
                if(seekBar.equals(mOpacitySeekbar)) {
                    setPercentageText(mOpacityTextView, i);
                    return;
                }
                if(seekBar.equals(mStrokeWidthSeekbar)) {
                    setPercentageText(mStrokeWidthTextView, i);
                    mStrokeColorTextView.setVisibility(i == 0 ? View.GONE : View.VISIBLE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {/*STUB*/}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {/*STUB*/}
        };

        //Add listeners, too bad I don't need all the methods
        mOpacitySeekbar.setOnSeekBarChangeListener(changeListener);
        mCornerRadiusSeekbar.setOnSeekBarChangeListener(changeListener);
        mStrokeWidthSeekbar.setOnSeekBarChangeListener(changeListener);

        mBackgroundColorButton = mRootView.findViewById(R.id.editBackgroundColor_imageButton);
        mStrokeColorButton = mRootView.findViewById(R.id.editStrokeColor_imageButton);

        mOpacityTextView = mRootView.findViewById(R.id.editButtonOpacity_textView_percent);
        mCornerRadiusTextView = mRootView.findViewById(R.id.editCornerRadius_textView_percent);
        mStrokeWidthTextView = mRootView.findViewById(R.id.editStrokeWidth_textView_percent);
        mStrokeColorTextView = mRootView.findViewById(R.id.editStrokeColor_textView);

        mDynamicPositionCheckbox = mRootView.findViewById(R.id.checkboxDynamicPosition);
        mDynamicPositionCheckbox.setOnCheckedChangeListener((btn, checked) -> {
            mDynamicXEditText.setEnabled(checked);
            mDynamicYEditText.setEnabled(checked);
        });


        //Initialize adapter for keycodes
        mAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item);
        String[] oldSpecialArr = ControlData.buildSpecialButtonArray();
        mSpecialArray = new String[oldSpecialArr.length];
        for (int i = 0; i < mSpecialArray.length; i++) {
            mSpecialArray[i] = "SPECIAL_" + oldSpecialArr[mSpecialArray.length - i - 1];
        }
        mAdapter.addAll(mSpecialArray);
        mAdapter.addAll(EfficientAndroidLWJGLKeycode.generateKeyName());
        mAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        for (Spinner spinner : mKeycodeSpinners) {
            spinner.setAdapter(mAdapter);
        }

        mColorSelector = new ColorSelector(ctx,color -> mEditingView.setImageDrawable(new ColorDrawable(color)));

        //Set color imageButton behavior
        mBackgroundColorButton.setOnClickListener(view -> showColorEditor((ImageView) view));
        mStrokeColorButton.setOnClickListener(view -> showColorEditor((ImageView) view));


        //Set dialog buttons behavior
        setupDialogButtons();

        hideUselessViews();

        defineDynamicCheckChange();

        setupCheckerboards();
    }

    protected void showColorEditor(ImageView imgView) {
        mEditingView = imgView;
        mColorSelector.show(((ColorDrawable)(imgView.getDrawable())).getColor());
    }

    protected void setupDialogButtons(){
        //Set dialog buttons behavior
        mBuilder.setPositiveButton(android.R.string.ok, (dialogInterface1, i) -> {
            if(!hasPropertiesErrors(mDialog.getContext())){
                saveProperties();
            }
        });
        mBuilder.setNegativeButton(android.R.string.cancel, null);
    }

    protected void hideUselessViews(){
        (mRootView.findViewById(R.id.editOrientation_textView)).setVisibility(View.GONE);

        (mRootView.findViewById(R.id.editDynamicPositionX_textView)).setVisibility(View.GONE);
        (mRootView.findViewById(R.id.editDynamicPositionY_textView)).setVisibility(View.GONE);
        mDynamicXEditText.setVisibility(View.GONE);
        mDynamicYEditText.setVisibility(View.GONE);

        //Hide the color choice if the width is 0.
        mStrokeColorTextView.setVisibility(mProperties.strokeWidth == 0 ? View.GONE : View.VISIBLE);
    }

    protected void defineDynamicCheckChange(){
        mDynamicPositionCheckbox.setOnCheckedChangeListener((compoundButton, b) -> {
            int visibility = b ? View.VISIBLE : View.GONE;

            (mRootView.findViewById(R.id.editDynamicPositionX_textView)).setVisibility(visibility);
            (mRootView.findViewById(R.id.editDynamicPositionY_textView)).setVisibility(visibility);
            mDynamicXEditText.setVisibility(visibility);
            mDynamicYEditText.setVisibility(visibility);
        });

    }

    private void setupCheckerboards(){
        CheckerboardDrawable drawable = new CheckerboardDrawable.Builder()
                .colorEven(Color.LTGRAY)
                .colorOdd(Color.WHITE)
                .size((int) Tools.dpToPx(20))
                .build();

        mBackgroundColorButton.setBackground(drawable);
        mStrokeColorButton.setBackground(drawable);
    }

    protected void setEditDialogValues(){

        mNameEditText.setText(mProperties.name);

        mToggleCheckbox.setChecked(mProperties.isToggle);
        mPassthroughCheckbox.setChecked(mProperties.passThruEnabled);
        mSwipeableCheckbox.setChecked(mProperties.isSwipeable);

        mWidthEditText.setText(Float.toString(mProperties.getWidth()));
        mHeightEditText.setText(Float.toString(mProperties.getHeight()));

        mDynamicXEditText.setEnabled(mProperties.isDynamicBtn);
        mDynamicYEditText.setEnabled(mProperties.isDynamicBtn);
        mDynamicXEditText.setText(mProperties.dynamicX);

        mDynamicYEditText.setText(mProperties.dynamicY);

        mOpacitySeekbar.setProgress((int) (mProperties.opacity*100));
        mStrokeWidthSeekbar.setProgress(mProperties.strokeWidth);
        mCornerRadiusSeekbar.setProgress((int) mProperties.cornerRadius);

        mBackgroundColorButton.setImageDrawable(new ColorDrawable(mProperties.bgColor));
        mStrokeColorButton.setImageDrawable(new ColorDrawable(mProperties.strokeColor));

        setPercentageText(mCornerRadiusTextView, mCornerRadiusSeekbar.getProgress());
        setPercentageText(mOpacityTextView, mOpacitySeekbar.getProgress());
        setPercentageText(mStrokeWidthTextView, mStrokeWidthSeekbar.getProgress());

        mDynamicPositionCheckbox.setChecked(mProperties.isDynamicBtn);

        for(int i = 0; i< mProperties.keycodes.length; i++){
            if (mProperties.keycodes[i] < 0) {
                mKeycodeSpinners[i].setSelection(mProperties.keycodes[i] + mSpecialArray.length);
            } else {
                mKeycodeSpinners[i].setSelection(EfficientAndroidLWJGLKeycode.getIndexByValue(mProperties.keycodes[i]) + mSpecialArray.length);
            }
        }
    }


    protected boolean hasPropertiesErrors(Context ctx){
        if (mNameEditText.getText().toString().isEmpty()) {
            mNameEditText.setError(ctx.getResources().getString(R.string.global_error_field_empty));
            return true;
        }

        if (mProperties.isDynamicBtn) {

            int errorAt = 0;
            try {
                mProperties.insertDynamicPos(mDynamicXEditText.getText().toString());
                errorAt = 1;
                mProperties.insertDynamicPos(mDynamicYEditText.getText().toString());
            } catch (Throwable th) {
                (errorAt == 0 ? mDynamicXEditText : mDynamicYEditText).setError(th.getMessage());

                return true;
            }
        }

        return false;
    }

    protected void saveProperties(){
        //This method assumes there are no error.
        mProperties.name = mNameEditText.getText().toString();

        //Keycodes
        for(int i = 0; i< mKeycodeSpinners.length; ++i){
            if (mKeycodeSpinners[i].getSelectedItemPosition() < mSpecialArray.length) {
                mProperties.keycodes[i] = mKeycodeSpinners[i].getSelectedItemPosition() - mSpecialArray.length;
            } else {
                mProperties.keycodes[i] = EfficientAndroidLWJGLKeycode.getValueByIndex(mKeycodeSpinners[i].getSelectedItemPosition() - mSpecialArray.length);
            }
        }

        mProperties.opacity = mOpacitySeekbar.getProgress()/100f;
        mProperties.strokeWidth = mStrokeWidthSeekbar.getProgress();
        mProperties.cornerRadius = mCornerRadiusSeekbar.getProgress();

        mProperties.bgColor = ((ColorDrawable) mBackgroundColorButton.getDrawable()).getColor();
        mProperties.strokeColor = ((ColorDrawable) mStrokeColorButton.getDrawable()).getColor();

        mProperties.isToggle = mToggleCheckbox.isChecked();
        mProperties.passThruEnabled = mPassthroughCheckbox.isChecked();
        mProperties.isSwipeable = mSwipeableCheckbox.isChecked();

        mProperties.setWidth(Float.parseFloat(mWidthEditText.getText().toString()));
        mProperties.setHeight(Float.parseFloat(mHeightEditText.getText().toString()));

        mProperties.isDynamicBtn = mDynamicPositionCheckbox.isChecked();
        if(!mDynamicXEditText.getText().toString().isEmpty()) mProperties.dynamicX = mDynamicXEditText.getText().toString();
        if(!mDynamicYEditText.getText().toString().isEmpty()) mProperties.dynamicY = mDynamicYEditText.getText().toString();

        mControlButton.updateProperties();
    }

}
