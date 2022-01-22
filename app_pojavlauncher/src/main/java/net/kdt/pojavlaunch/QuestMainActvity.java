package net.kdt.pojavlaunch;

import android.app.NativeActivity;
import android.os.Bundle;

public class QuestMainActvity extends NativeActivity {
    @Override
    public void onCreate(Bundle savedBundleState) {
        MCXRLoader.setContext(getApplication());
        MCXRLoader.launch(MainActivity.getInstance());
        super.onCreate(savedBundleState);
    }
}
