package net.kdt.pojavlaunch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ExitActivity extends AppCompatActivity {
    public static void showExitMessage(Context ctx, int code) {
        Intent i = new Intent(ctx,ExitActivity.class);
        i.putExtra("code",code);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int code = -1;
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            code = extras.getInt("code",-1);
        }
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.mcn_exit_title,code))
                .setPositiveButton(android.R.string.ok,(dialog,which)->{
                    dialog.dismiss();
                    ExitActivity.this.finish();
                }).setOnCancelListener((z)->{
                    ExitActivity.this.finish();
                })
                .show();
    }
}
