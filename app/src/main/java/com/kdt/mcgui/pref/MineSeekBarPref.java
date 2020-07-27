/*
 * Copyright 2018 The Android Open Source Project
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

package com.kdt.mcgui.pref;

import android.content.*;
import android.content.res.*;
import android.os.*;
import android.support.v7.preference.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.SeekBar.*;
import net.kdt.pojavlaunch.*;

import android.view.View.BaseSavedState;
import net.kdt.pojavlaunch.R;

/**
 * Preference based on android.preference.SeekBarPreference but uses support preference as a base
 * . It contains a title and a {@link SeekBar} and an optional SeekBar value {@link TextView}.
 * The actual preference layout is customizable by setting {@code android:layout} on the
 * preference widget layout or {@code seekBarPreferenceStyle} attribute.
 *
 * <p>The {@link SeekBar} within the preference can be defined adjustable or not by setting {@code
 * adjustable} attribute. If adjustable, the preference will be responsive to DPAD left/right keys.
 * Otherwise, it skips those keys.
 *
 * <p>The {@link SeekBar} value view can be shown or disabled by setting {@code showSeekBarValue}
 * attribute to true or false, respectively.
 *
 * <p>Other {@link SeekBar} specific attributes (e.g. {@code title, summary, defaultValue, min,
 * max})
 * can be set directly on the preference widget layout.
 */
public class MineSeekBarPref extends Preference {

    private static final String TAG = "SeekBarPreference";
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    int mSeekBarValue;
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    int mMin;
    private int mMax;
    private int mSeekBarIncrement;
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    boolean mTrackingTouch;
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    SeekBar mSeekBar;
    private TextView mSeekBarValueTextView;
    // Whether the SeekBar should respond to the left/right keys
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    boolean mAdjustable;
    // Whether to show the SeekBar value TextView next to the bar
    private boolean mShowSeekBarValue;
    // Whether the SeekBarPreference should continuously save the Seekbar value while it is being
    // dragged.
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    boolean mUpdatesContinuously;
    /**
     * Listener reacting to the {@link SeekBar} changing value by the user
     */
    private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser && (mUpdatesContinuously || !mTrackingTouch)) {
                syncValueInternal(seekBar);
            } else {
                // We always want to update the text while the seekbar is being dragged
                updateLabelValue(progress + mMin);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mTrackingTouch = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mTrackingTouch = false;
            if (seekBar.getProgress() + mMin != mSeekBarValue) {
                syncValueInternal(seekBar);
            }
        }
    };

    /**
     * Listener reacting to the user pressing DPAD left/right keys if {@code
     * adjustable} attribute is set to true; it transfers the key presses to the {@link SeekBar}
     * to be handled accordingly.
     */
    private View.OnKeyListener mSeekBarKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() != KeyEvent.ACTION_DOWN) {
                return false;
            }

            if (!mAdjustable && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                    || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                // Right or left keys are pressed when in non-adjustable mode; Skip the keys.
                return false;
            }

            // We don't want to propagate the click keys down to the SeekBar view since it will
            // create the ripple effect for the thumb.
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                return false;
            }

            if (mSeekBar == null) {
                Log.e(TAG, "SeekBar view is null and hence cannot be adjusted.");
                return false;
            }
            return mSeekBar.onKeyDown(keyCode, event);
        }
    };

    public MineSeekBarPref(
            Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.SeekBarPreference, defStyleAttr, defStyleRes);

        // The ordering of these two statements are important. If we want to set max first, we need
        // to perform the same steps by changing min/max to max/min as following:
        // mMax = a.getInt(...) and setMin(...).
        mMin = a.getInt(R.styleable.SeekBarPreference_min, 0);
        setMax(a.getInt(R.styleable.SeekBarPreference_android_max, 100));
        setSeekBarIncrement(a.getInt(R.styleable.SeekBarPreference_seekBarIncrement, 0));
        mAdjustable = a.getBoolean(R.styleable.SeekBarPreference_adjustable, true);
        mShowSeekBarValue = a.getBoolean(R.styleable.SeekBarPreference_showSeekBarValue, false);
        // mUpdatesContinuously = a.getBoolean(R.styleable.SeekBarPreference_updatesContinuously, false);
        a.recycle();
    }

    public MineSeekBarPref(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MineSeekBarPref(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.seekBarPreferenceStyle);
    }

    public MineSeekBarPref(Context context) {
        this(context, null);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        view.itemView.setOnKeyListener(mSeekBarKeyListener);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        mSeekBarValueTextView = (TextView) view.findViewById(R.id.seekbar_value);
		mSeekBarValueTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        if (mShowSeekBarValue) {
            mSeekBarValueTextView.setVisibility(View.VISIBLE);
        } else {
            mSeekBarValueTextView.setVisibility(View.GONE);
            mSeekBarValueTextView = null;
        }

        if (mSeekBar == null) {
            Log.e(TAG, "SeekBar view is null in onBindViewHolder.");
            return;
        }
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mSeekBar.setMax(mMax - mMin);
        // If the increment is not zero, use that. Otherwise, use the default mKeyProgressIncrement
        // in AbsSeekBar when it's zero. This default increment value is set by AbsSeekBar
        // after calling setMax. That's why it's important to call setKeyProgressIncrement after
        // calling setMax() since setMax() can change the increment value.
        if (mSeekBarIncrement != 0) {
            mSeekBar.setKeyProgressIncrement(mSeekBarIncrement);
        } else {
            mSeekBarIncrement = mSeekBar.getKeyProgressIncrement();
        }

        mSeekBar.setProgress(mSeekBarValue - mMin);
        updateLabelValue(mSeekBarValue);
        mSeekBar.setEnabled(isEnabled());
    }

    // @Override
    protected void onSetInitialValue(Object defaultValue) {
        if (defaultValue == null) {
            defaultValue = 0;
        }
        setValue(getPersistedInt((Integer) defaultValue));
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    /**
     * Gets the lower bound set on the {@link SeekBar}.
     *
     * @return The lower bound set
     */
    public int getMin() {
        return mMin;
    }

    /**
     * Sets the lower bound on the {@link SeekBar}.
     *
     * @param min The lower bound to set
     */
    public void setMin(int min) {
        if (min > mMax) {
            min = mMax;
        }
        if (min != mMin) {
            mMin = min;
            notifyChanged();
        }
    }

    /**
     * Returns the amount of increment change via each arrow key click. This value is derived from
     * user's specified increment value if it's not zero. Otherwise, the default value is picked
     * from the default mKeyProgressIncrement value in {@link android.widget.AbsSeekBar}.
     *
     * @return The amount of increment on the {@link SeekBar} performed after each user's arrow
     * key press
     */
    public final int getSeekBarIncrement() {
        return mSeekBarIncrement;
    }

    /**
     * Sets the increment amount on the {@link SeekBar} for each arrow key press.
     *
     * @param seekBarIncrement The amount to increment or decrement when the user presses an
     *                         arrow key.
     */
    public final void setSeekBarIncrement(int seekBarIncrement) {
        if (seekBarIncrement != mSeekBarIncrement) {
            mSeekBarIncrement = Math.min(mMax - mMin, Math.abs(seekBarIncrement));
            notifyChanged();
        }
    }

    /**
     * Gets the upper bound set on the {@link SeekBar}.
     *
     * @return The upper bound set
     */
    public int getMax() {
        return mMax;
    }

    /**
     * Sets the upper bound on the {@link SeekBar}.
     *
     * @param max The upper bound to set
     */
    public final void setMax(int max) {
        if (max < mMin) {
            max = mMin;
        }
        if (max != mMax) {
            mMax = max;
            notifyChanged();
        }
    }

    /**
     * Gets whether the {@link SeekBar} should respond to the left/right keys.
     *
     * @return Whether the {@link SeekBar} should respond to the left/right keys
     */
    public boolean isAdjustable() {
        return mAdjustable;
    }

    /**
     * Sets whether the {@link SeekBar} should respond to the left/right keys.
     *
     * @param adjustable Whether the {@link SeekBar} should respond to the left/right keys
     */
    public void setAdjustable(boolean adjustable) {
        mAdjustable = adjustable;
    }

    /**
     * Gets whether the {@link SeekBarPreference} should continuously save the {@link SeekBar} value
     * while it is being dragged. Note that when the value is true,
     * {@link Preference.OnPreferenceChangeListener} will be called continuously as well.
     *
     * @return Whether the {@link SeekBarPreference} should continuously save the {@link SeekBar}
     * value while it is being dragged
     * @see #setUpdatesContinuously(boolean)
     */
    public boolean getUpdatesContinuously() {
        return mUpdatesContinuously;
    }

    /**
     * Sets whether the {@link SeekBarPreference} should continuously save the {@link SeekBar} value
     * while it is being dragged.
     *
     * @param updatesContinuously Whether the {@link SeekBarPreference} should continuously save
     *                            the {@link SeekBar} value while it is being dragged
     * @see #getUpdatesContinuously()
     */
    public void setUpdatesContinuously(boolean updatesContinuously) {
        mUpdatesContinuously = updatesContinuously;
    }

    /**
     * Gets whether the current {@link SeekBar} value is displayed to the user.
     *
     * @return Whether the current {@link SeekBar} value is displayed to the user
     * @see #setShowSeekBarValue(boolean)
     */
    public boolean getShowSeekBarValue() {
        return mShowSeekBarValue;
    }

    /**
     * Sets whether the current {@link SeekBar} value is displayed to the user.
     *
     * @param showSeekBarValue Whether the current {@link SeekBar} value is displayed to the user
     * @see #getShowSeekBarValue()
     */
    public void setShowSeekBarValue(boolean showSeekBarValue) {
        mShowSeekBarValue = showSeekBarValue;
        notifyChanged();
    }

    private void setValueInternal(int seekBarValue, boolean notifyChanged) {
        if (seekBarValue < mMin) {
            seekBarValue = mMin;
        }
        if (seekBarValue > mMax) {
            seekBarValue = mMax;
        }

        if (seekBarValue != mSeekBarValue) {
            mSeekBarValue = seekBarValue;
            updateLabelValue(mSeekBarValue);
            persistInt(seekBarValue);
            if (notifyChanged) {
                notifyChanged();
            }
        }
    }

    /**
     * Gets the current progress of the {@link SeekBar}.
     *
     * @return The current progress of the {@link SeekBar}
     */
    public int getValue() {
        return mSeekBarValue;
    }

    /**
     * Sets the current progress of the {@link SeekBar}.
     *
     * @param seekBarValue The current progress of the {@link SeekBar}
     */
    public void setValue(int seekBarValue) {
        setValueInternal(seekBarValue, true);
    }

    /**
     * Persist the {@link SeekBar}'s SeekBar value if callChangeListener returns true, otherwise
     * set the {@link SeekBar}'s value to the stored value.
     */
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    void syncValueInternal(SeekBar seekBar) {
        int seekBarValue = mMin + seekBar.getProgress();
        if (seekBarValue != mSeekBarValue) {
            if (callChangeListener(seekBarValue)) {
                setValueInternal(seekBarValue, false);
            } else {
                seekBar.setProgress(mSeekBarValue - mMin);
                updateLabelValue(mSeekBarValue);
            }
        }
    }

    /**
     * Attempts to update the TextView label that displays the current value.
     *
     * @param value the value to display next to the {@link SeekBar}
     */
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    void updateLabelValue(int value) {
        if (mSeekBarValueTextView != null) {
            mSeekBarValueTextView.setText(String.valueOf(value));
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        // Save the instance state
        final SavedState myState = new SavedState(superState);
        myState.mSeekBarValue = mSeekBarValue;
        myState.mMin = mMin;
        myState.mMax = mMax;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        // Restore the instance state
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        mSeekBarValue = myState.mSeekBarValue;
        mMin = myState.mMin;
        mMax = myState.mMax;
        notifyChanged();
    }

    /**
     * SavedState, a subclass of {@link BaseSavedState}, will store the state of this preference.
     *
     * <p>It is important to always call through to super methods.
     */
    private static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

        int mSeekBarValue;
        int mMin;
        int mMax;

        SavedState(Parcel source) {
            super(source);

            // Restore the click counter
            mSeekBarValue = source.readInt();
            mMin = source.readInt();
            mMax = source.readInt();
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            // Save the click counter
            dest.writeInt(mSeekBarValue);
            dest.writeInt(mMin);
            dest.writeInt(mMax);
        }
    }
}
