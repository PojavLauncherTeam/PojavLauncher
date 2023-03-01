package net.kdt.pojavlaunch.customcontrols.handleview;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static net.kdt.pojavlaunch.Tools.currentDisplayMetrics;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.kdt.DefocusableScrollView;

import net.kdt.pojavlaunch.EfficientAndroidLWJGLKeycode;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.colorselector.ColorSelectionListener;
import net.kdt.pojavlaunch.colorselector.ColorSelector;
import net.kdt.pojavlaunch.customcontrols.ControlData;
import net.kdt.pojavlaunch.customcontrols.ControlDrawerData;
import net.kdt.pojavlaunch.customcontrols.buttons.ControlButton;
import net.kdt.pojavlaunch.customcontrols.buttons.ControlDrawer;
import net.kdt.pojavlaunch.customcontrols.buttons.ControlInterface;

import java.util.Arrays;
import java.util.Collections;

/**
 * Class providing a sort of popup on top of a Layout, allowing to edit a given ControlButton
 */
public class EditControlPopup {
    private final DefocusableScrollView mScrollView;
    private ConstraintLayout mRootView;
    private final ColorSelector mColorSelector;

    private final ObjectAnimator mEditPopupAnimator;
    private final ObjectAnimator mColorEditorAnimator;
    private boolean mDisplaying = false;
    private boolean mDisplayingColor = false;
    public boolean internalChanges = false; // True when we programmatically change stuff.
    private ControlInterface mCurrentlyEditedButton;
    private final int mMargin;
    private final View.OnLayoutChangeListener mLayoutChangedListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if(internalChanges) return;

            internalChanges = true;
            int width = (int)(safeParseFloat(mWidthEditText.getText().toString()));

            if(width >= 0 && Math.abs(right - width) > 1){
                mWidthEditText.setText(String.valueOf(right - left));
            }
            int height = (int)(safeParseFloat(mHeightEditText.getText().toString()));
            if(height >= 0 && Math.abs(bottom - height) > 1){
                mHeightEditText.setText(String.valueOf(bottom - top));
            }

            internalChanges = false;
        }
    };

    protected EditText mNameEditText, mWidthEditText, mHeightEditText;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    protected Switch mToggleSwitch, mPassthroughSwitch, mSwipeableSwitch;
    protected Spinner mOrientationSpinner;
    protected Spinner[] mKeycodeSpinners = new Spinner[4];
    protected SeekBar mStrokeWidthSeekbar, mCornerRadiusSeekbar, mAlphaSeekbar;
    protected TextView mStrokePercentTextView, mCornerRadiusPercentTextView, mAlphaPercentTextView;
    protected TextView mSelectBackgroundColor, mSelectStrokeColor;
    protected ArrayAdapter<String> mAdapter;
    protected String[] mSpecialArray;

    // Decorative textviews
    private TextView mOrientationTextView, mMappingTextView, mNameTextView, mCornerRadiusTextView;



    public EditControlPopup(Context context, ViewGroup parent){
        mScrollView = (DefocusableScrollView) LayoutInflater.from(context).inflate(R.layout.dialog_control_button_setting, parent, false);
        parent.addView(mScrollView);

        mMargin = context.getResources().getDimensionPixelOffset(R.dimen._20sdp);

        mColorSelector = new ColorSelector(context, parent, null);
        mColorSelector.getRootView().setElevation(11);
        mColorSelector.getRootView().setTranslationZ(11);
        mColorSelector.getRootView().setX(-context.getResources().getDimensionPixelOffset(R.dimen._280sdp));

        mEditPopupAnimator = ObjectAnimator.ofFloat(mScrollView, "x", 0).setDuration(1000);
        mColorEditorAnimator = ObjectAnimator.ofFloat(mColorSelector.getRootView(), "x", 0).setDuration(1000);
        Interpolator decelerate = new AccelerateDecelerateInterpolator();
        mEditPopupAnimator.setInterpolator(decelerate);
        mColorEditorAnimator.setInterpolator(decelerate);

        mScrollView.setElevation(10);
        mScrollView.setTranslationZ(10);
        mScrollView.setX(-context.getResources().getDimensionPixelOffset(R.dimen._280sdp));

        bindLayout();
        loadAdapter();

        setupRealTimeListeners();
    }


    /** Slide the layout into the visible screen area */
    public void appear(boolean fromRight){
        disappearColor(); // When someone jumps from a button to another

        if(fromRight){
            if(!mDisplaying || !isAtRight()){
                mEditPopupAnimator.setFloatValues(currentDisplayMetrics.widthPixels, currentDisplayMetrics.widthPixels - mScrollView.getWidth() - mMargin);
                mEditPopupAnimator.start();
            }
        }else{
            if (!mDisplaying || isAtRight()){
                mEditPopupAnimator.setFloatValues(-mScrollView.getWidth(), mMargin);
                mEditPopupAnimator.start();
            }
        }

        mDisplaying = true;
    }

    /** Slide out the layout */
    public void disappear(){
        if(!mDisplaying) return;

        mDisplaying = false;
        if(isAtRight())
            mEditPopupAnimator.setFloatValues(currentDisplayMetrics.widthPixels - mScrollView.getWidth() - mMargin, currentDisplayMetrics.widthPixels);
        else
            mEditPopupAnimator.setFloatValues(mMargin, -mScrollView.getWidth());

        mEditPopupAnimator.start();
    }

    /** Slide the layout into the visible screen area */
    public void appearColor(boolean fromRight, int color){
        if(fromRight){
            if(!mDisplayingColor || !isAtRight()){
                mColorEditorAnimator.setFloatValues(currentDisplayMetrics.widthPixels, currentDisplayMetrics.widthPixels - mScrollView.getWidth() - mMargin);
                mColorEditorAnimator.start();
            }
        }else{
            if (!mDisplayingColor || isAtRight()){
                mColorEditorAnimator.setFloatValues(-mScrollView.getWidth(), mMargin);
                mColorEditorAnimator.start();
            }
        }

        mDisplayingColor = true;
        mColorSelector.show(color == -1 ? Color.WHITE : color);
    }

    /** Slide out the layout */
    public void disappearColor(){
        if(!mDisplayingColor) return;

        mDisplayingColor = false;
        if(isAtRight())
            mColorEditorAnimator.setFloatValues(currentDisplayMetrics.widthPixels - mScrollView.getWidth() - mMargin, currentDisplayMetrics.widthPixels);
        else
            mColorEditorAnimator.setFloatValues(mMargin, -mScrollView.getWidth());

        mColorEditorAnimator.start();
    }

    /** Slide out the first visible layer.
     * @return True if the last layer is disappearing */
    public boolean disappearLayer(){
        if(mDisplayingColor){
            disappearColor();
            return false;
        }else{
            disappear();
            return true;
        }
    }

    /** Switch the panels position if needed */
    public void adaptPanelPosition(){
        if(mDisplaying){
            boolean isAtRight = mCurrentlyEditedButton.getControlView().getX() + mCurrentlyEditedButton.getControlView().getWidth()/2f < currentDisplayMetrics.widthPixels/2f;
            appear(isAtRight);
        }
    }


    public void destroy(){
        ((ViewGroup) mScrollView.getParent()).removeView(mColorSelector.getRootView());
        ((ViewGroup) mScrollView.getParent()).removeView(mScrollView);
    }

    private void loadAdapter(){
        //Initialize adapter for keycodes
        mAdapter = new ArrayAdapter<>(mRootView.getContext(), R.layout.item_centered_textview);
        mSpecialArray = ControlData.buildSpecialButtonArray();
        for (int i = 0; i < mSpecialArray.length; i++) {
            mSpecialArray[i] = "SPECIAL_" + mSpecialArray[i];
        }
        Collections.reverse(Arrays.asList(mSpecialArray));
        mAdapter.addAll(mSpecialArray);
        mAdapter.addAll(EfficientAndroidLWJGLKeycode.generateKeyName());
        mAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        for (Spinner spinner : mKeycodeSpinners) {
            spinner.setAdapter(mAdapter);
        }

        // Orientation spinner
        ArrayAdapter<ControlDrawerData.Orientation> adapter = new ArrayAdapter<>(mScrollView.getContext(), android.R.layout.simple_spinner_item);
        adapter.addAll(ControlDrawerData.getOrientations());
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        mOrientationSpinner.setAdapter(adapter);
    }



    private void setDefaultVisibilitySetting(){
        for(int i=0; i<mRootView.getChildCount(); ++i){
            mRootView.getChildAt(i).setVisibility(VISIBLE);
        }
    }

    private boolean isAtRight(){
        return mScrollView.getX() > currentDisplayMetrics.widthPixels/2f;
    }


    public static void setPercentageText(TextView textView, int progress){
        textView.setText(progress + " %");
    }

    /* LOADING VALUES */

    /** Load values for basic control data */
    public void loadValues(ControlData data){
        setDefaultVisibilitySetting();
        mOrientationTextView.setVisibility(GONE);
        mOrientationSpinner.setVisibility(GONE);

        mNameEditText.setText(data.name);
        mWidthEditText.setText(String.valueOf(data.getWidth()));
        mHeightEditText.setText(String.valueOf(data.getHeight()));

        mAlphaSeekbar.setProgress((int) (data.opacity*100));
        mStrokeWidthSeekbar.setProgress(data.strokeWidth);
        mCornerRadiusSeekbar.setProgress((int) data.cornerRadius);

        setPercentageText(mAlphaPercentTextView, (int) (data.opacity*100));
        setPercentageText(mStrokePercentTextView, data.strokeWidth);
        setPercentageText(mCornerRadiusPercentTextView, (int) data.cornerRadius);

        mToggleSwitch.setChecked(data.isToggle);
        mPassthroughSwitch.setChecked(data.passThruEnabled);
        mSwipeableSwitch.setChecked(data.isSwipeable);

        for(int i = 0; i< data.keycodes.length; i++){
            if (data.keycodes[i] < 0) {
                mKeycodeSpinners[i].setSelection(data.keycodes[i] + mSpecialArray.length);
            } else {
                mKeycodeSpinners[i].setSelection(EfficientAndroidLWJGLKeycode.getIndexByValue(data.keycodes[i]) + mSpecialArray.length);
            }
        }
    }

    /** Load values for extended control data */
    public void loadValues(ControlDrawerData data){
        loadValues(data.properties);

        mOrientationSpinner.setSelection(
                ControlDrawerData.orientationToInt(data.orientation));

        mMappingTextView.setVisibility(GONE);
        mKeycodeSpinners[0].setVisibility(GONE);
        mKeycodeSpinners[1].setVisibility(GONE);
        mKeycodeSpinners[2].setVisibility(GONE);
        mKeycodeSpinners[3].setVisibility(GONE);

        mOrientationTextView.setVisibility(VISIBLE);
        mOrientationSpinner.setVisibility(VISIBLE);

        mSwipeableSwitch.setVisibility(View.GONE);
        mPassthroughSwitch.setVisibility(View.GONE);
        mToggleSwitch.setVisibility(View.GONE);
    }

    /** Load values for the joystick */
    public void loadJoystickValues(ControlData data){
        loadValues(data);

        mMappingTextView.setVisibility(GONE);
        mKeycodeSpinners[0].setVisibility(GONE);
        mKeycodeSpinners[1].setVisibility(GONE);
        mKeycodeSpinners[2].setVisibility(GONE);
        mKeycodeSpinners[3].setVisibility(GONE);

        mNameTextView.setVisibility(GONE);
        mNameEditText.setVisibility(GONE);

        mCornerRadiusTextView.setVisibility(GONE);
        mCornerRadiusSeekbar.setVisibility(GONE);
        mCornerRadiusPercentTextView.setVisibility(GONE);

        mSwipeableSwitch.setVisibility(View.GONE);
        mPassthroughSwitch.setVisibility(View.GONE);
        mToggleSwitch.setVisibility(View.GONE);
    }


    private void bindLayout(){
        mRootView = mScrollView.findViewById(R.id.edit_layout);
        mNameEditText = mScrollView.findViewById(R.id.editName_editText);
        mWidthEditText = mScrollView.findViewById(R.id.editSize_editTextX);
        mHeightEditText = mScrollView.findViewById(R.id.editSize_editTextY);
        mToggleSwitch = mScrollView.findViewById(R.id.checkboxToggle);
        mPassthroughSwitch = mScrollView.findViewById(R.id.checkboxPassThrough);
        mSwipeableSwitch = mScrollView.findViewById(R.id.checkboxSwipeable);
        mKeycodeSpinners[0] = mScrollView.findViewById(R.id.editMapping_spinner_1);
        mKeycodeSpinners[1] = mScrollView.findViewById(R.id.editMapping_spinner_2);
        mKeycodeSpinners[2] = mScrollView.findViewById(R.id.editMapping_spinner_3);
        mKeycodeSpinners[3] = mScrollView.findViewById(R.id.editMapping_spinner_4);
        mOrientationSpinner = mScrollView.findViewById(R.id.editOrientation_spinner);
        mStrokeWidthSeekbar = mScrollView.findViewById(R.id.editStrokeWidth_seekbar);
        mCornerRadiusSeekbar = mScrollView.findViewById(R.id.editCornerRadius_seekbar);
        mAlphaSeekbar = mScrollView.findViewById(R.id.editButtonOpacity_seekbar);
        mSelectBackgroundColor = mScrollView.findViewById(R.id.editBackgroundColor_textView);
        mSelectStrokeColor = mScrollView.findViewById(R.id.editStrokeColor_textView);
        mStrokePercentTextView = mScrollView.findViewById(R.id.editStrokeWidth_textView_percent);
        mAlphaPercentTextView = mScrollView.findViewById(R.id.editButtonOpacity_textView_percent);
        mCornerRadiusPercentTextView = mScrollView.findViewById(R.id.editCornerRadius_textView_percent);

        //Decorative stuff
        mMappingTextView = mScrollView.findViewById(R.id.editMapping_textView);
        mOrientationTextView = mScrollView.findViewById(R.id.editOrientation_textView);
        mNameTextView = mScrollView.findViewById(R.id.editName_textView);
        mCornerRadiusTextView = mScrollView.findViewById(R.id.editCornerRadius_textView);
    }

    /**
     * A long function linking all the displayed data on the popup and,
     * the currently edited mCurrentlyEditedButton
     */
    public void setupRealTimeListeners(){
        mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(internalChanges) return;

                mCurrentlyEditedButton.getProperties().name = s.toString();

                // Cheap and unoptimized, doesn't break the abstraction layer
                mCurrentlyEditedButton.setProperties(mCurrentlyEditedButton.getProperties(), false);
            }
        });

        mWidthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(internalChanges) return;

                float width = safeParseFloat(s.toString());
                if(width >= 0){
                    mCurrentlyEditedButton.getProperties().setWidth(width);
                    mCurrentlyEditedButton.updateProperties();
                }
            }
        });

        mHeightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(internalChanges) return;

                float height = safeParseFloat(s.toString());
                if(height >= 0){
                    mCurrentlyEditedButton.getProperties().setHeight(height);
                    mCurrentlyEditedButton.updateProperties();
                }
            }
        });

        mSwipeableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(internalChanges) return;
            mCurrentlyEditedButton.getProperties().isSwipeable = isChecked;
        });
        mToggleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(internalChanges) return;
            mCurrentlyEditedButton.getProperties().isToggle = isChecked;
        });
        mPassthroughSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(internalChanges) return;
            mCurrentlyEditedButton.getProperties().passThruEnabled = isChecked;
        });

        mAlphaSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(internalChanges) return;
                mCurrentlyEditedButton.getProperties().opacity = mAlphaSeekbar.getProgress()/100f;
                mCurrentlyEditedButton.getControlView().setAlpha(mAlphaSeekbar.getProgress()/100f);
                setPercentageText(mAlphaPercentTextView, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mStrokeWidthSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(internalChanges) return;
                mCurrentlyEditedButton.getProperties().strokeWidth = mStrokeWidthSeekbar.getProgress();
                mCurrentlyEditedButton.setBackground();
                setPercentageText(mStrokePercentTextView, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mCornerRadiusSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(internalChanges) return;
                mCurrentlyEditedButton.getProperties().cornerRadius = mCornerRadiusSeekbar.getProgress();
                mCurrentlyEditedButton.setBackground();
                setPercentageText(mCornerRadiusPercentTextView, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        for(int i = 0; i< mKeycodeSpinners.length; ++i){
            int finalI = i;
            mKeycodeSpinners[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Side note, spinner listeners are fired later than all the other ones.
                    // Meaning the internalChanges bool is useless here.
                    if (position < mSpecialArray.length) {
                        mCurrentlyEditedButton.getProperties().keycodes[finalI] = mKeycodeSpinners[finalI].getSelectedItemPosition() - mSpecialArray.length;
                    } else {
                        mCurrentlyEditedButton.getProperties().keycodes[finalI] = EfficientAndroidLWJGLKeycode.getValueByIndex(mKeycodeSpinners[finalI].getSelectedItemPosition() - mSpecialArray.length);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }


        mOrientationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Side note, spinner listeners are fired later than all the other ones.
                // Meaning the internalChanges bool is useless here.

                if(mCurrentlyEditedButton instanceof ControlDrawer){
                    ((ControlDrawer)mCurrentlyEditedButton).drawerData.orientation = ControlDrawerData.intToOrientation(mOrientationSpinner.getSelectedItemPosition());
                    ((ControlDrawer)mCurrentlyEditedButton).syncButtons();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        mSelectStrokeColor.setOnClickListener(v -> {
            mColorSelector.setAlphaEnabled(false);
            mColorSelector.setColorSelectionListener(color -> {
                mCurrentlyEditedButton.getProperties().strokeColor = color;
                mCurrentlyEditedButton.setBackground();
            });
            appearColor(isAtRight(), mCurrentlyEditedButton.getProperties().strokeColor);
        });

        mSelectBackgroundColor.setOnClickListener(v -> {
            mColorSelector.setAlphaEnabled(true);
            mColorSelector.setColorSelectionListener(color -> {
                mCurrentlyEditedButton.getProperties().bgColor = color;
                mCurrentlyEditedButton.setBackground();
            });
            appearColor(isAtRight(), mCurrentlyEditedButton.getProperties().bgColor);
        });
    }

    private float safeParseFloat(String string){
        float out = -1; // -1
        try {
            out = Float.parseFloat(string);
        }catch (NumberFormatException e){
            Log.e("EditControlPopup", e.toString());
        }
        return out;
    }

    public void setCurrentlyEditedButton(ControlInterface button){
        if(mCurrentlyEditedButton != null)
            mCurrentlyEditedButton.getControlView().removeOnLayoutChangeListener(mLayoutChangedListener);
        mCurrentlyEditedButton = button;
        mCurrentlyEditedButton.getControlView().addOnLayoutChangeListener(mLayoutChangedListener);
    }
}
