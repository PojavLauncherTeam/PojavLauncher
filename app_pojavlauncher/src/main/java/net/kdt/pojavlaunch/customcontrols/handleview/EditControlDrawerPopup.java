package net.kdt.pojavlaunch.customcontrols.handleview;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.customcontrols.ControlData;
import net.kdt.pojavlaunch.customcontrols.buttons.ControlDrawer;
import net.kdt.pojavlaunch.customcontrols.ControlDrawerData;
import net.kdt.pojavlaunch.customcontrols.ControlLayout;

public class EditControlDrawerPopup extends EditControlButtonPopup{
    private Spinner mOrientationSpinner;


    private final ControlDrawer mDrawer;
    private final ControlDrawerData mDrawerData;

    public EditControlDrawerPopup(ControlDrawer editedButton) {
        super(editedButton);
        mDrawer = editedButton;
        mDrawerData = editedButton.getDrawerData();
    }


    @Override
    protected void hideUselessViews() {
        (mRootView.findViewById(R.id.editMapping_textView)).setVisibility(View.GONE);
        mPassthroughCheckbox.setVisibility(View.GONE);
        mToggleCheckbox.setVisibility(View.GONE);
        mSwipeableCheckbox.setVisibility(View.GONE);

        (mRootView.findViewById(R.id.editDynamicPositionX_textView)).setVisibility(View.GONE);
        (mRootView.findViewById(R.id.editDynamicPositionY_textView)).setVisibility(View.GONE);
        mDynamicXEditText.setVisibility(View.GONE);
        mDynamicYEditText.setVisibility(View.GONE);
    }

    @Override
    protected void initializeEditDialog(Context ctx) {
        super.initializeEditDialog(ctx);

        mOrientationSpinner = mRootView.findViewById(R.id.editOrientation_spinner);

        ArrayAdapter<ControlDrawerData.Orientation> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item);
        adapter.addAll(ControlDrawerData.getOrientations());
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        mOrientationSpinner.setAdapter(adapter);
    }

    @Override
    protected void setEditDialogValues() {
        super.setEditDialogValues();

        mOrientationSpinner.setSelection(ControlDrawerData.orientationToInt(mDrawerData.orientation));


        //Using the dialog to replace the button behavior allows us not to dismiss the window
        mDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(v -> {
            ControlLayout layout = (ControlLayout) mDrawer.getParent();
            ControlData controlData =  new ControlData(mDrawerData.properties);
            controlData.name = "new";
            layout.addSubButton(mDrawer, controlData);

            Context ctx = mDialog.getContext();
            Toast.makeText(ctx, ctx.getString(R.string.customctrl_add_subbutton_message,
                    mDrawer.getDrawerData().buttonProperties.size()), Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    protected void setupDialogButtons() {
        super.setupDialogButtons();

        mBuilder.setNeutralButton(mRootView.getResources().getString(R.string.customctrl_addsubbutton), null);

    }

    @Override
    protected void saveProperties() {
        mDrawerData.orientation = ControlDrawerData.intToOrientation(mOrientationSpinner.getSelectedItemPosition());
        super.saveProperties();
    }
}
