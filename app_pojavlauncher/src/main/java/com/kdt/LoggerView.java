package com.kdt;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

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
    private Logger logger = null;
    private TextView log;


    public LoggerView(@NonNull Context context) {
        this(context, null);
    }

    public LoggerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.loggerview_layout, this);
        log = findViewById(R.id.content_log_view);

    }

    /** Create the logger */
    public void setLogFileName(String fileName){
        if(logger != null){
            logger.shutdown();
        }
        logger = new Logger(fileName);
    }

    /** Indirect Wrapper for the logger object */
    public void appendToLog(String text){
        log.append(text);
        logger.appendToLog(text);
    }

    /** Indirect wrapper for the shutdown system */
    public void shutdown(){
        logger.shutdown();
    }

}
