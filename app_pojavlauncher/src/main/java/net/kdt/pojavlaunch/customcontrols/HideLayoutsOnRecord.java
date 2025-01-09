package net.kdt.pojavlaunch.customcontrols;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.view.View;

public class HideLayoutsOnRecord {

    private ControlLayout controlLayout;

    public HideLayoutsOnRecord(ControlLayout controlLayout) {
        this.controlLayout = controlLayout;
        registerReceiver(controlLayout.getContext());
    }

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.RECORD_SOUND");
        filter.addAction("android.media.SCREEN_RECORDING");
        context.registerReceiver(recordingReceiver, filter);
    }

    private BroadcastReceiver recordingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case "android.media.RECORD_SOUND":
                    case "android.media.SCREEN_RECORDING":
                        // Hide the ControlLayout
                        hideControlLayout();
                        break;
                }
            }
        }
    };

    private void hideControlLayout() {
        if (controlLayout.getVisibility() == View.VISIBLE) {
            controlLayout.setVisibility(View.GONE);
        }
    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(recordingReceiver);
    }
}
