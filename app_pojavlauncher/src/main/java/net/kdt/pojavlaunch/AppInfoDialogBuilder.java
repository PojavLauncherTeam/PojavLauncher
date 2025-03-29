package net.kdt.pojavlaunch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AppInfoDialogBuilder extends MaterialAlertDialogBuilder {

    public AppInfoDialogBuilder(@NonNull Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_app_info, null);
        ((TextView) view.findViewById(R.id.info_version)).setText(BuildConfig.VERSION_NAME);

        setTitle(R.string.dialog_info);
        setView(view);
        setPositiveButton(R.string.dialog_positive, null);
    }
}
