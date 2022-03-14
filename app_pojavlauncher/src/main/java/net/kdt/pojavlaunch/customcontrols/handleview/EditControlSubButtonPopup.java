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
        (mRootView.findViewById(R.id.editSize_textView)).setVisibility(View.GONE);
        (mRootView.findViewById(R.id.editOrientation_textView)).setVisibility(View.GONE);

        mDynamicPositionCheckbox.setVisibility(View.GONE);

        (mRootView.findViewById(R.id.editDynamicPositionX_textView)).setVisibility(View.GONE);
        mDynamicXEditText.setVisibility(View.GONE);

        (mRootView.findViewById(R.id.editDynamicPositionY_textView)).setVisibility(View.GONE);
        mDynamicYEditText.setVisibility(View.GONE);
    }
}
