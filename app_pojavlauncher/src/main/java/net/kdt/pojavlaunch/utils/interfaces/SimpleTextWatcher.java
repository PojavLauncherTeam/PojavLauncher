package net.kdt.pojavlaunch.utils.interfaces;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Most interfaces implementations of {@link TextWatcher} only implement the afterTextChanged method.
 * This class provides a default for other methods.
 */
public interface SimpleTextWatcher extends TextWatcher {
    @Override
    default void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    default void onTextChanged(CharSequence s, int start, int before, int count) {}
}
