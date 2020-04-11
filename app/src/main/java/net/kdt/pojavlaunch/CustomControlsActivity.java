package net.kdt.pojavlaunch;

import android.support.v7.app.*;
import android.os.*;
import net.kdt.pojavlaunch.value.customcontrols.*;

public class CustomControlsActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		CustomControls ctrl = new CustomControls();
		
		ControlButton ctrlEx = new ControlButton();
		ctrlEx.name = "Test";
		ctrlEx.x = 100;
		ctrlEx.y = 100;
		
		ctrl.button = new ControlButton[]{ctrlEx};
		
		ControlsLayout ctrlLayout = new ControlsLayout(this);
		ctrlLayout.loadLayout(ctrl);
		ctrlLayout.setCanMove(true);
		setContentView(ctrlLayout);
	}
}
