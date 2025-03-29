package net.kdt,.pojavlaunch.utils;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.Nullable;

public class ResultListener {

    private static Listener listener;

    public static void registerListener(Listener listener) {
        ResultListener.listener = listener;
    }

    public static void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (listener != null) {
            listener.onActivityResult(requestCode, resultCode, data);
            listener = null;
        }
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode, Listener listener) {
        registerListener(listener);
        activity.startActivityForResult(intent, requestCode);
    }

    public interface Listener {
        void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
    }

}
