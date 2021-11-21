package com.kdt;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import net.kdt.pojavlaunch.Logger;
import net.kdt.pojavlaunch.R;

/**
 * A class able to display logs to the user.
 * It has support for the Logger class
 */
public class LoggerView extends ConstraintLayout {
    private Logger.eventLogListener logListener;
    private TextView log;


    public LoggerView(@NonNull Context context) {
        this(context, null);
    }

    public LoggerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Inflate the layout, and add component behaviors
     */
    private void init(){
        inflate(getContext(), R.layout.loggerview_layout, this);
        log = findViewById(R.id.content_log_view);
        log.setTypeface(Typeface.MONOSPACE);
        //TODO clamp the max text so it doesn't go oob
        log.setMaxLines(Integer.MAX_VALUE);
        log.setEllipsize(null);
        log.setVisibility(GONE);

        // Toggle log visibility
        ToggleButton toggleButton = findViewById(R.id.content_log_toggle_log);
        toggleButton.setOnCheckedChangeListener(
                (compoundButton, isChecked) -> {
                    log.setVisibility(isChecked ? VISIBLE : GONE);
                    if(!isChecked) log.setText("");
                });
        toggleButton.setChecked(false);

        // Remove the loggerView from the user View
        ImageButton cancelButton = findViewById(R.id.log_view_cancel);
        cancelButton.setOnClickListener(view -> LoggerView.this.setVisibility(GONE));

        // Listen to logs
        logListener = text -> {
            if(log.getVisibility() != VISIBLE) return;
            post(() -> log.append(text));

        };
        Logger.getInstance().setLogListener(logListener);
    }



}
