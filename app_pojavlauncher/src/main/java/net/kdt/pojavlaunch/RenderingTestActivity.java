package net.kdt.pojavlaunch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import net.kdt.pojavlaunch.utils.JREUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenderingTestActivity extends LoggableActivity {
    private final String TAG = "RenderTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TextView tv = new TextView(this);
        // tv.setText("Hello world");

        Button tv = new Button(this);
        tv.setText("Start JVM");
        tv.setOnClickListener(e -> run());

        setContentView(tv);
    }

    void run() {
        Intent i = new Intent(this, RenderingTestRunActivity.class);
        startActivity(i);

    }

    @Override
    public void appendToLog(String text, boolean checkAllow) {
        Log.i(TAG, text);
    }
}