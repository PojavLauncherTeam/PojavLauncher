package net.kdt.pojavlaunch.customcontrols.handleview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import net.kdt.pojavlaunch.AndroidLWJGLKeycode;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.customcontrols.ControlButton;
import net.kdt.pojavlaunch.customcontrols.ControlData;

import static net.kdt.pojavlaunch.customcontrols.handleview.ActionPopupWindow.setPercentageText;

public class EditControlSubButtonPopup {

    private Dialog dialog;

    private EditText editName;
    private Spinner[] spinnersKeycode;

    private CheckBox checkToggle;
    private CheckBox checkPassThrough;
    private CheckBox checkHoldAlt;
    private CheckBox checkHoldCtrl;
    private CheckBox checkHoldShift;

    private SeekBar seekBarOpacity;
    private SeekBar seekBarCornerRadius;
    private SeekBar seekBarStrokeWidth;

    private ImageButton buttonBackgroundColor;
    private ImageButton buttonStrokeColor;

    private TextView textOpacity;
    private TextView textCornerRadius;
    private TextView textStrokeWidth;

    private final ControlButton button;
    private final ControlData properties;

    private ArrayAdapter<String> adapter;
    private String[] specialArr;


    public EditControlSubButtonPopup(ControlButton button){
        this.button = button;
        this.properties = button.getProperties();

        initializeEditDialog(button.getContext());
        setEditDialogValues();

        dialog.show();
    }


    public void initializeEditDialog(Context ctx){
        //Create the editing dialog
        LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.control_sub_button, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctx);
        alertBuilder.setTitle(ctx.getResources().getString(R.string.customctrl_edit, properties.name));
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


        seekBarOpacity = v.findViewById(R.id.controlsetting_seek_opacity);
        seekBarCornerRadius = v.findViewById(R.id.controlsetting_seek_corner_radius);
        seekBarStrokeWidth = v.findViewById(R.id.controlsetting_seek_stroke_width);

        //Add listeners, too bad I don't need all the methods
        seekBarOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setPercentageText(textOpacity, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //AUTO GENERATED STUB
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //AUTO GENERATED STUB
            }
        });

        seekBarCornerRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setPercentageText(textCornerRadius, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //AUTO GENERATED STUB
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //AUTO GENERATED STUB
            }
        });

        seekBarStrokeWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setPercentageText(textStrokeWidth, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //AUTO GENERATED STUB
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //AUTO GENERATED STUB
            }
        });

        buttonBackgroundColor = v.findViewById(R.id.controlsetting_background_color);
        buttonStrokeColor = v.findViewById(R.id.controlsetting_stroke_color);

        textOpacity = v.findViewById(R.id.controlsetting_text_opacity);
        textCornerRadius = v.findViewById(R.id.controlsetting_text_corner_radius);
        textStrokeWidth = v.findViewById(R.id.controlsetting_text_stroke_width);


        checkHoldAlt = v.findViewById(R.id.controlsetting_checkbox_keycombine_alt);
        checkHoldCtrl = v.findViewById(R.id.controlsetting_checkbox_keycombine_control);
        checkHoldShift = v.findViewById(R.id.controlsetting_checkbox_keycombine_shift);

        //Initialize adapter for keycodes
        adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item);
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
        buttonBackgroundColor.setOnClickListener(view -> ActionPopupWindow.showColorPicker(ctx, "Edit background color", true, ((ColorDrawable) buttonBackgroundColor.getBackground()).getColor(), buttonBackgroundColor));
        buttonStrokeColor.setOnClickListener(view -> ActionPopupWindow.showColorPicker(ctx, "Edit stroke color", false, ((ColorDrawable) buttonStrokeColor.getBackground()).getColor(), buttonStrokeColor));


        //Set dialog buttons behavior
        alertBuilder.setPositiveButton(android.R.string.ok, (dialogInterface1, i) -> {
            if(!hasPropertiesErrors(dialog.getContext())){
                saveProperties();
            }
        });
        alertBuilder.setNegativeButton(android.R.string.cancel, null);

        //Create the finalized dialog
        dialog = alertBuilder.create();

        dialog.setOnShowListener(dialogInterface -> {
            //setEditDialogValues();
        });

    }

    private void setEditDialogValues(){

        editName.setText(properties.name);

        checkToggle.setChecked(properties.isToggle);
        checkPassThrough.setChecked(properties.passThruEnabled);

        seekBarOpacity.setProgress((int) (properties.opacity*100));
        seekBarStrokeWidth.setProgress(properties.strokeWidth);
        seekBarCornerRadius.setProgress((int)properties.cornerRadius);

        buttonBackgroundColor.setBackgroundColor(properties.bgColor);
        buttonStrokeColor.setBackgroundColor(properties.strokeColor);

        setPercentageText(textCornerRadius,seekBarCornerRadius.getProgress());
        setPercentageText(textOpacity,seekBarOpacity.getProgress());
        setPercentageText(textStrokeWidth,seekBarStrokeWidth.getProgress());

        checkHoldAlt.setChecked(properties.holdAlt);
        checkHoldCtrl.setChecked(properties.holdCtrl);
        checkHoldShift.setChecked(properties.holdShift);

        for(int i=0; i< properties.keycodes.length; i++){
            if (properties.keycodes[i] < 0) {
                spinnersKeycode[i].setSelection(properties.keycodes[i] + specialArr.length);
            } else {
                spinnersKeycode[i].setSelection(AndroidLWJGLKeycode.getIndexByLWJGLKey(properties.keycodes[i]) + specialArr.length);
            }
        }
    }


    private boolean hasPropertiesErrors(Context ctx){
        if (editName.getText().toString().isEmpty()) {
            editName.setError(ctx.getResources().getString(R.string.global_error_field_empty));
            return true;
        }

        return false;
    }

    private void saveProperties(){
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

        properties.isToggle = checkToggle.isChecked();
        properties.passThruEnabled = checkPassThrough.isChecked();

        properties.holdAlt = checkHoldAlt.isChecked();
        properties.holdCtrl = checkHoldCtrl.isChecked();
        properties.holdShift = checkHoldShift.isChecked();

        button.updateProperties();
    }

}
