package net.kdt.pojavlaunch.accounts;

import android.app.Activity;
import android.app.AlertDialog;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;

public class LocalAccountUtils {
    public static void checkUsageAllowed(CheckResultListener listener) {
        if (AccountsManager.haveMicrosoftAccount()) {
            listener.onUsageAllowed();
        } else {
            listener.onUsageDenied();
        }
    }

    public static void openDialog(Activity activity, String message) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(R.string.global_yes, (dialogInterface, i) -> Tools.openURL(activity, Tools.URL_MINECRAFT))
                .setNegativeButton(R.string.global_no, null)
                .show();
    }

    public interface CheckResultListener {
        void onUsageAllowed();
        void onUsageDenied();
    }
}
