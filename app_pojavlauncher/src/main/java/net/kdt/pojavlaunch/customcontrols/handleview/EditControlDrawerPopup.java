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

public class EditControlDrawerPopup extends EditControlButtonPopup{
    private Spinner spinnerOrientation;


    private ControlDrawer drawer;
    private ControlDrawerData drawerData;

    public EditControlDrawerPopup(ControlDrawer editedButton) {
        super(editedButton);
        drawer = editedButton;
        drawerData = editedButton.getDrawerData();
    }


    @Override
    protected void hideUselessViews() {
        (v.findViewById(R.id.editMapping_textView)).setVisibility(View.GONE);
        checkPassThrough.setVisibility(View.GONE);
        checkToggle.setVisibility(View.GONE);
    }

    @Override
    protected void initializeEditDialog(Context ctx) {
        super.initializeEditDialog(ctx);

        spinnerOrientation = v.findViewById(R.id.editOrientation_spinner);

        ArrayAdapter<ControlDrawerData.Orientation> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item);
        adapter.addAll(ControlDrawerData.getOrientations());
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        spinnerOrientation.setAdapter(adapter);
    }

    @Override
    protected void setEditDialogValues() {
        super.setEditDialogValues();

        spinnerOrientation.setSelection(ControlDrawerData.orientationToInt(drawerData.orientation));
    }

    @Override
    protected void setupDialogButtons() {
        super.setupDialogButtons();

        builder.setNeutralButton("Add sub-button", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ControlLayout layout = (ControlLayout) drawer.getParent();
                layout.addSubButton(drawer, new ControlData());

            }
        });

    }

    @Override
    protected void saveProperties() {
        drawerData.orientation = ControlDrawerData.intToOrientation(spinnerOrientation.getSelectedItemPosition());
        super.saveProperties();
    }
}
