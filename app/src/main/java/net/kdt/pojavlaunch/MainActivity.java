package net.kdt.pojavlaunch;

import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import net.kdt.pojavlaunch.customcontrols.*;

public class MainActivity extends BaseMainActivity implements OnClickListener {
    private Button upButton,
    downButton, leftButton,
    rightButton, jumpButton,
    primaryButton, secondaryButton,
    debugButton, shiftButton,
    keyboardButton, inventoryButton,
    talkButton, thirdPersonButton,
    zoomButton, listPlayersButton,
	toggleControlButton;

    private Button[] controlButtons;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.upButton = findButton(R.id.control_up);
        this.downButton = findButton(R.id.control_down);
        this.leftButton = findButton(R.id.control_left);
        this.rightButton = findButton(R.id.control_right);
        this.jumpButton = findButton(R.id.control_jump);
        this.primaryButton = findButton(R.id.control_primary);
        this.secondaryButton = findButton(R.id.control_secondary);
        this.debugButton = findButton(R.id.control_debug);
        this.shiftButton = findButton(R.id.control_shift);
        this.keyboardButton = findButton(R.id.control_keyboard);
        this.inventoryButton = findButton(R.id.control_inventory);
        this.talkButton = findButton(R.id.control_talk);
        this.thirdPersonButton = findButton(R.id.control_thirdperson);
        this.zoomButton = findButton(R.id.control_zoom);
        this.listPlayersButton = findButton(R.id.control_listplayers);
        this.toggleControlButton = findButton(R.id.control_togglecontrol);
        this.controlButtons = new Button[]{
            upButton, downButton, leftButton, rightButton,
            jumpButton, primaryButton, secondaryButton,
            debugButton, shiftButton, keyboardButton,
            inventoryButton, talkButton, thirdPersonButton,
            listPlayersButton
        };
        this.toggleControlButton.setOnClickListener(this);
        this.zoomButton.setVisibility(mVersionInfo.optifineLib == null ? View.GONE : View.VISIBLE);
        
        ControlData[] specialButtons = ControlData.getSpecialButtons();
        specialButtons[1].specialButtonListener = this;

        // toggleGui(null);
        onClick(toggleControlButton);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.control_togglecontrol: {
                    /*
                     switch(overlayView.getVisibility()){
                     case View.VISIBLE: overlayView.setVisibility(View.GONE);
                     break;
                     case View.GONE: overlayView.setVisibility(View.VISIBLE);
                     }
                     */

                    for (Button button : controlButtons) {
                        button.setVisibility(button.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    }

                    zoomButton.setVisibility((zoomButton.getVisibility() == View.GONE && mVersionInfo.optifineLib != null) ? View.VISIBLE : View.GONE);
                }
        }
    }
}
