package net.kdt.pojavlaunch.customcontrols.handleview;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.customcontrols.ControlData;
import net.kdt.pojavlaunch.customcontrols.ControlDrawer;
import net.kdt.pojavlaunch.customcontrols.ControlDrawerData;
import net.kdt.pojavlaunch.customcontrols.ControlLayout;

import static net.kdt.pojavlaunch.customcontrols.handleview.ActionPopupWindow.setPercentageText;

public class EditControlDrawerPopup {
    private Dialog dialog;

    private EditText editName;
    private Spinner spinnerOrientation;

    private CheckBox checkDynamicPosition;

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

    private ControlDrawer drawer;
    private ControlDrawerData drawerData;

    public EditControlDrawerPopup(ControlDrawer editedButton) {
        drawer = editedButton;
        drawerData = editedButton.getDrawerData();

        initializeEditDialog(drawer.getContext());
        setEditDialogValues();

        dialog.show();
    }


    private void initializeEditDialog(Context ctx){
        LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.control_drawer_setting,null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctx);
        alertBuilder.setTitle(ctx.getResources().getString(R.string.customctrl_edit, drawerData.properties.name));
        alertBuilder.setView(v);

        //LINKING PHASE
        editName = v.findViewById(R.id.controlsetting_edit_name);

        spinnerOrientation = v.findViewById(R.id.controlsetting_orientation);

        editWidth = v.findViewById(R.id.controlsetting_edit_width);
        editHeight = v.findViewById(R.id.controlsetting_edit_height);

        editDynamicX = v.findViewById(R.id.controlsetting_edit_dynamicpos_x);
        editDynamicY = v.findViewById(R.id.controlsetting_edit_dynamicpos_y);

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

        checkDynamicPosition = v.findViewById(R.id.controlsetting_checkbox_dynamicpos);
        checkDynamicPosition.setOnCheckedChangeListener((btn, checked) -> {
            editDynamicX.setEnabled(checked);
            editDynamicY.setEnabled(checked);
        });

        ArrayAdapter<ControlDrawerData.Orientation> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item);
        adapter.addAll(ControlDrawerData.getOrientations());
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        spinnerOrientation.setAdapter(adapter);

        //Set color imageButton behavior
        buttonBackgroundColor.setOnClickListener(view -> ActionPopupWindow.showColorPicker(ctx, "Edit background color", true, ((ColorDrawable) buttonBackgroundColor.getBackground()).getColor(), buttonBackgroundColor));
        buttonStrokeColor.setOnClickListener(view -> ActionPopupWindow.showColorPicker(ctx, "Edit stroke color", false, ((ColorDrawable) buttonStrokeColor.getBackground()).getColor(), buttonStrokeColor));

        //Set dialog buttons behavior
        alertBuilder.setNegativeButton(android.R.string.cancel, null);
        alertBuilder.setPositiveButton(android.R.string.ok, (dialogInterface1, i) -> {
            if(!hasPropertiesErrors(dialog.getContext())){
                saveProperties();
            }
        });

        alertBuilder.setNeutralButton("Add sub-button", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO add a button;
                ControlLayout layout = (ControlLayout) drawer.getParent();
                layout.addSubButton(drawer, new ControlData());

            }
        });


        //Create the finalized dialog
        dialog = alertBuilder.create();
    } //initializeEditDialog

    private void setEditDialogValues() {
        editName.setText(drawerData.properties.name);

        spinnerOrientation.setSelection(ControlDrawerData.orientationToInt(drawerData.orientation));

        editWidth.setText(Float.toString(drawerData.properties.width));
        editHeight.setText(Float.toString(drawerData.properties.height));

        editDynamicX.setEnabled(drawerData.properties.isDynamicBtn);
        editDynamicY.setEnabled(drawerData.properties.isDynamicBtn);
        editDynamicX.setHint(Float.toString(drawerData.properties.x));
        editDynamicX.setText(drawerData.properties.dynamicX);
        editDynamicY.setHint(Float.toString(drawerData.properties.y));
        editDynamicY.setText(drawerData.properties.dynamicY);

        seekBarOpacity.setProgress((int)drawerData.properties.opacity*100);
        seekBarStrokeWidth.setProgress(drawerData.properties.strokeWidth);
        seekBarCornerRadius.setProgress((int)drawerData.properties.cornerRadius);

        buttonBackgroundColor.setBackgroundColor(drawerData.properties.bgColor);
        buttonStrokeColor.setBackgroundColor(drawerData.properties.strokeColor);

        setPercentageText(textCornerRadius,seekBarCornerRadius.getProgress());
        setPercentageText(textOpacity,seekBarOpacity.getProgress());
        setPercentageText(textStrokeWidth,seekBarStrokeWidth.getProgress());

        checkDynamicPosition.setChecked(drawerData.properties.isDynamicBtn);

    }//setEditValues


    private boolean hasPropertiesErrors(Context ctx){
        if (editName.getText().toString().isEmpty()) {
            editName.setError(ctx.getResources().getString(R.string.global_error_field_empty));
            return true;
        }

        if (drawerData.properties.isDynamicBtn) {
            int errorAt = 0;
            try {
                drawerData.properties.insertDynamicPos(editDynamicX.getText().toString());
                errorAt = 1;
                drawerData.properties.insertDynamicPos(editDynamicY.getText().toString());
            } catch (Throwable th) {
                (errorAt == 0 ? editDynamicX : editDynamicY).setError(th.getMessage());
                return true;
            }
        }
        return false;
    }//hasErrors


    private void saveProperties(){
        drawerData.properties.name = editName.getText().toString();

        drawerData.orientation = ControlDrawerData.intToOrientation(spinnerOrientation.getSelectedItemPosition());

        drawerData.properties.opacity = seekBarOpacity.getProgress()/100f;
        drawerData.properties.strokeWidth = seekBarStrokeWidth.getProgress();
        drawerData.properties.cornerRadius = seekBarCornerRadius.getProgress();

        drawerData.properties.bgColor = ((ColorDrawable) buttonBackgroundColor.getBackground()).getColor();
        drawerData.properties.strokeColor = ((ColorDrawable) buttonStrokeColor.getBackground()).getColor();

        drawerData.properties.width = Float.parseFloat(editWidth.getText().toString());
        drawerData.properties.height = Float.parseFloat(editHeight.getText().toString());

        drawerData.properties.isDynamicBtn = checkDynamicPosition.isChecked();
        drawerData.properties.dynamicX = editDynamicX.getText().toString().isEmpty() ? drawerData.properties.dynamicX = Float.toString(drawerData.properties.x) : editDynamicX.getText().toString();
        drawerData.properties.dynamicY = editDynamicY.getText().toString().isEmpty() ? drawerData.properties.dynamicY = Float.toString(drawerData.properties.y) : editDynamicY.getText().toString();



        drawer.updateProperties();
    }//saveProperties



}
