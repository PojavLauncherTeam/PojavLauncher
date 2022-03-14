package net.kdt.pojavlaunch.prefs;

import static net.kdt.pojavlaunch.prefs.LauncherPreferences.DEFAULT_PREF;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_CONTROL_BOTTOM_OFFSET;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_CONTROL_LEFT_OFFSET;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_CONTROL_RIGHT_OFFSET;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_CONTROL_TOP_OFFSET;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.preference.Preference;

import net.kdt.pojavlaunch.R;

/** Custom preference class displaying a dialog */
public class ControlOffsetPreference extends Preference {

    private AlertDialog mPreferenceDialog;

    public ControlOffsetPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ControlOffsetPreference(Context context) {
        super(context);
        init();
    }

    @Override
    protected void onClick() {
        mPreferenceDialog.show();

        SeekBar topOffsetSeekbar = mPreferenceDialog.findViewById(R.id.control_offset_top_seekbar);
        SeekBar rightOffsetSeekbar = mPreferenceDialog.findViewById(R.id.control_offset_right_seekbar);
        SeekBar bottomOffsetSeekbar = mPreferenceDialog.findViewById(R.id.control_offset_bottom_seekbar);
        SeekBar leftOffsetSeekbar = mPreferenceDialog.findViewById(R.id.control_offset_left_seekbar);

        TextView topOffsetTextView = mPreferenceDialog.findViewById(R.id.control_offset_top_textview);
        TextView rightOffsetTextView = mPreferenceDialog.findViewById(R.id.control_offset_right_textview);
        TextView bottomOffsetTextView = mPreferenceDialog.findViewById(R.id.control_offset_bottom_textview);
        TextView leftOffsetTextView = mPreferenceDialog.findViewById(R.id.control_offset_left_textview);

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(seekBar == topOffsetSeekbar){
                    String text = String.format("%s %d%s", getContext().getString(R.string.control_top_offset), i, " px");
                    topOffsetTextView.setText(text);
                    return;
                }
                if(seekBar == rightOffsetSeekbar){
                    String text = String.format("%s %d%s", getContext().getString(R.string.control_right_offset), i, " px");
                    rightOffsetTextView.setText(text);
                    return;
                }
                if(seekBar == bottomOffsetSeekbar){
                    String text = String.format("%s %d%s", getContext().getString(R.string.control_bottom_offset), i, " px");
                    bottomOffsetTextView.setText(text);
                    return;
                }
                if(seekBar == leftOffsetSeekbar){
                    String text = String.format("%s %d%s", getContext().getString(R.string.control_left_offset), i, " px");
                    leftOffsetTextView.setText(text);
                    return;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        topOffsetSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);
        rightOffsetSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);
        bottomOffsetSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);
        leftOffsetSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);

        topOffsetSeekbar.setProgress(PREF_CONTROL_TOP_OFFSET);
        rightOffsetSeekbar.setProgress(PREF_CONTROL_RIGHT_OFFSET);
        bottomOffsetSeekbar.setProgress(PREF_CONTROL_BOTTOM_OFFSET);
        leftOffsetSeekbar.setProgress(PREF_CONTROL_LEFT_OFFSET);

        seekBarChangeListener.onProgressChanged(topOffsetSeekbar, PREF_CONTROL_TOP_OFFSET, false);
        seekBarChangeListener.onProgressChanged(rightOffsetSeekbar, PREF_CONTROL_RIGHT_OFFSET, false);
        seekBarChangeListener.onProgressChanged(bottomOffsetSeekbar, PREF_CONTROL_BOTTOM_OFFSET, false);
        seekBarChangeListener.onProgressChanged(leftOffsetSeekbar, PREF_CONTROL_LEFT_OFFSET, false);

        // Custom writing to preferences
        mPreferenceDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view -> {
            DEFAULT_PREF.edit().putInt("controlTopOffset", topOffsetSeekbar.getProgress()).apply();
            DEFAULT_PREF.edit().putInt("controlRightOffset", rightOffsetSeekbar.getProgress()).apply();
            DEFAULT_PREF.edit().putInt("controlBottomOffset", bottomOffsetSeekbar.getProgress()).apply();
            DEFAULT_PREF.edit().putInt("controlLeftOffset", leftOffsetSeekbar.getProgress()).apply();


            mPreferenceDialog.dismiss();
        });
    }

    private void init(){
        // Setup visual values
        if(getTitle() == null){
            setTitle(R.string.preference_control_offset_title);
            setSummary(R.string.preference_control_offset_description);
        }
        if(getIcon() == null){
            setIcon(android.R.drawable.radiobutton_off_background);
        }

        // Prepare Alert dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(R.layout.dialog_control_offset_preference);
        dialogBuilder.setTitle(getContext().getString(R.string.control_offset_title));

        dialogBuilder.setPositiveButton(android.R.string.ok, null);
        dialogBuilder.setNegativeButton(android.R.string.cancel, null);

        mPreferenceDialog = dialogBuilder.create();
    }

}
