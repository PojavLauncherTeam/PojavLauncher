package net.kdt.pojavlaunch.utils.interfaces;

import android.widget.SeekBar;

/**
 * Most interfaces implementations of {@link SeekBar.OnSeekBarChangeListener}
 * only implement the onProgressChanged method. This class provides a default for other methods.
 */
public interface SimpleSeekBarListener extends SeekBar.OnSeekBarChangeListener {
    @Override
    default void onStartTrackingTouch(android.widget.SeekBar seekBar) {
    }

    @Override
    default void onStopTrackingTouch(android.widget.SeekBar seekBar) {
    }
}
