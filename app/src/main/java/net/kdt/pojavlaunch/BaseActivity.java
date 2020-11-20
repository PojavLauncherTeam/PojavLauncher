package net.kdt.pojavlaunch;

import android.support.v7.app.*;
import android.os.*;

public class BaseActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.updateWindowSize(this);
    }
}
