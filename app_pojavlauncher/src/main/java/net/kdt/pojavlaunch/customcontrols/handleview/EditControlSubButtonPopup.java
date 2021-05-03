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
