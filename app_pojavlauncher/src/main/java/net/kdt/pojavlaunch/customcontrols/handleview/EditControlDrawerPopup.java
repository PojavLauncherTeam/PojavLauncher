package net.kdt.pojavlaunch.customcontrols.handleview;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.customcontrols.ControlData;
import net.kdt.pojavlaunch.customcontrols.buttons.ControlDrawer;
import net.kdt.pojavlaunch.customcontrols.ControlDrawerData;
import net.kdt.pojavlaunch.customcontrols.ControlLayout;

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
        checkBoxSwipeable.setVisibility(View.GONE);
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

        builder.setNeutralButton(v.getResources().getString(R.string.customctrl_addsubbutton), (dialogInterface, i) -> {
            ControlLayout layout = (ControlLayout) drawer.getParent();
            ControlData controlData =  drawerData.properties.clone();
            controlData.name = "new";
            layout.addSubButton(drawer, controlData);
        });

    }

    @Override
    protected void saveProperties() {
        drawerData.orientation = ControlDrawerData.intToOrientation(spinnerOrientation.getSelectedItemPosition());
        super.saveProperties();
    }
}
