package net.kdt.pojavlaunch.utils.interfaces;

import android.view.View;
import android.widget.AdapterView;

/**
 * Most interfaces implementations of {@link AdapterView.OnItemSelectedListener}
 * only implement the {@link AdapterView.OnItemSelectedListener#onItemSelected(AdapterView, View, int, long)} onItemClick method.
 * This class provides a default for other methods.
 */
public interface SimpleItemSelectedListener extends AdapterView.OnItemSelectedListener {
    @Override
    default void onNothingSelected(AdapterView<?> parent) {
    }
}
