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

    private AlertDialog preferenceDialog;



    public ControlOffsetPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ControlOffsetPreference(Context context) {
        super(context);
        init();
    }


    private void init(){
        // Setup visual values
        if(getTitle() == null){
            setTitle("Control side offsets");
        }
        if(getIcon() == null){
            setIcon(android.R.drawable.radiobutton_off_background);
        }

        // Prepare Alert dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(R.layout.control_offset_preference_dialog);
        dialogBuilder.setTitle("Control offset");

        dialogBuilder.setPositiveButton(android.R.string.ok, null);
        dialogBuilder.setNegativeButton(android.R.string.cancel, null);

        preferenceDialog = dialogBuilder.create();


    }

    @Override
    protected void onClick() {
        preferenceDialog.show();

        SeekBar topOffsetSeekbar = preferenceDialog.findViewById(R.id.control_offset_top_seekbar);
        SeekBar rightOffsetSeekbar = preferenceDialog.findViewById(R.id.control_offset_right_seekbar);
        SeekBar bottomOffsetSeekbar = preferenceDialog.findViewById(R.id.control_offset_bottom_seekbar);
        SeekBar leftOffsetSeekbar = preferenceDialog.findViewById(R.id.control_offset_left_seekbar);

        TextView topOffsetTextView = preferenceDialog.findViewById(R.id.control_offset_top_textview);
        TextView rightOffsetTextView = preferenceDialog.findViewById(R.id.control_offset_right_textview);
        TextView bottomOffsetTextView = preferenceDialog.findViewById(R.id.control_offset_bottom_textview);
        TextView leftOffsetTextView = preferenceDialog.findViewById(R.id.control_offset_left_textview);

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(seekBar == topOffsetSeekbar){
                    topOffsetTextView.setText("Top offset: " + i);
                    return;
                }
                if(seekBar == rightOffsetSeekbar){
                    rightOffsetTextView.setText("Right offset: " + i);
                    return;
                }
                if(seekBar == bottomOffsetSeekbar){
                    bottomOffsetTextView.setText("Bottom offset: " + i);
                    return;
                }
                if(seekBar == leftOffsetSeekbar){
                    leftOffsetTextView.setText("Left offset: " + i);
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

        // Custom writing to preferences
        preferenceDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view -> {
            DEFAULT_PREF.edit().putInt("controlTopOffset", topOffsetSeekbar.getProgress()).apply();
            DEFAULT_PREF.edit().putInt("controlRightOffset", rightOffsetSeekbar.getProgress()).apply();
            DEFAULT_PREF.edit().putInt("controlBottomOffset", bottomOffsetSeekbar.getProgress()).apply();
            DEFAULT_PREF.edit().putInt("controlLeftOffset", leftOffsetSeekbar.getProgress()).apply();


            preferenceDialog.dismiss();
        });
    }

}
