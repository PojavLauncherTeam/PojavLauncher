package net.kdt.pojavlaunch.customcontrols.handleview;

import android.view.View;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.customcontrols.buttons.ControlButton;

public class EditControlSubButtonPopup extends EditControlButtonPopup{


    public EditControlSubButtonPopup(ControlButton button){
        super(button);
    }

    @Override
    protected void hideUselessViews() {
        (v.findViewById(R.id.editSize_textView)).setVisibility(View.GONE);
        (v.findViewById(R.id.editOrientation_textView)).setVisibility(View.GONE);

        checkDynamicPosition.setVisibility(View.GONE);

        (v.findViewById(R.id.editDynamicPositionX_textView)).setVisibility(View.GONE);
        editDynamicX.setVisibility(View.GONE);

        (v.findViewById(R.id.editDynamicPositionY_textView)).setVisibility(View.GONE);
        editDynamicY.setVisibility(View.GONE);
    }
}
