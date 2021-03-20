package net.kdt.pojavlaunch;

import android.app.Activity;

public interface ILoggableActivity {
    Activity asActivity();

    default void appendToLog(String text) {
        appendToLog(text, true);
    }

    default void appendlnToLog(String text) {
        appendlnToLog(text, true);
    }

    default void appendlnToLog(String text, boolean checkAllow) {
        appendToLog(text + "\n", checkAllow);
    }

    void appendToLog(final String text, boolean checkAllow);
}
