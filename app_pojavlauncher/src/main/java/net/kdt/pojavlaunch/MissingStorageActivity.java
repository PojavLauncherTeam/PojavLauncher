package net.kdt.pojavlaunch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MissingStorageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_test_no_sdcard);
    }
}