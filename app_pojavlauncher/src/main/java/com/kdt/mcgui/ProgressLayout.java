package com.kdt.mcgui;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.collection.ArrayMap;
import androidx.constraintlayout.widget.ConstraintLayout;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.extra.ExtraCore;

import java.util.Arrays;


/** Class staring at specific values and automatically show something if the progress is present
 * Since progress is posted in a specific way, The packing/unpacking is handheld by the class
 *
 * This class relies on ExtraCore for its behavior.
 */
public class ProgressLayout extends ConstraintLayout implements View.OnClickListener {
    public static final String UNPACK_RUNTIME = "unpack_runtime";
    public static final String DOWNLOAD_MINECRAFT = "download_minecraft";
    public static final String INSTALL_MODPACK = "install_modpack";

    public ProgressLayout(@NonNull Context context) {
        super(context);
        init();
    }
    public ProgressLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public ProgressLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public ProgressLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private int mActiveProcesses = 0;
    private final ArrayMap<String, TextProgressBar> mMap = new ArrayMap<>();
    private LinearLayout mLinearLayout;
    private TextView mTaskNumberDisplayer;
    private final Runnable mCheckProgressRunnable = new Runnable() {
        @Override
        public void run() {
            for(String progressKey : mMap.keySet()){
                if(progressKey == null) continue; //TODO check wtf does this

                Object object = ExtraCore.consumeValue(progressKey);

                if(object != null){
                    String[] progressStuff = ((String) object).split("¤");
                    int progress = Integer.parseInt(progressStuff[0]);
                    int resourceString = Integer.parseInt(progressStuff[1]);

                    // Prepare the progressbar
                    if(mMap.get(progressKey) == null){
                        TextProgressBar textView = new TextProgressBar(getContext());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen._20sdp));
                        params.bottomMargin = getResources().getDimensionPixelOffset(R.dimen._6sdp);

                        mLinearLayout.addView(textView, params);
                        mMap.put(progressKey, textView);
                        mActiveProcesses++;
                    }

                    mMap.get(progressKey).setProgress(progress);
                    if(resourceString != -1){
                        // As an optimization, copy array content back 2 indexes instead of creating another object
                        System.arraycopy(progressStuff, 2, progressStuff, 0, progressStuff.length - 2);
                        mMap.get(progressKey).setText(getResources().getString(resourceString, progressStuff));
                    }else{
                        if(progressStuff.length >= 3)
                            mMap.get(progressStuff[2]);
                    }


                    // Remove when we don't have progress
                    if(progress < 0){
                        if(progress <= -10) // Only remove the observer when it is explicitly told to do so ?
                            mMap.remove(progressKey);

                        mLinearLayout.removeView(mMap.get(progressKey));
                        mActiveProcesses--;
                    }
                }
            }

            setVisibility(hasProcesses() ? VISIBLE : GONE);

            mTaskNumberDisplayer.setText(mActiveProcesses + " tasks in progress");
            postDelayed(this, 1000);
        }
    };



    public void observe(String progressKey){
        mMap.put(progressKey, null);
    }

    public boolean hasProcesses(){
        return mActiveProcesses > 0;
    }

    public boolean hasProcess(String process){
        return mMap.get(process) != null;
    }

    private void init(){
        inflate(getContext(), R.layout.view_progress, this);
        mLinearLayout = findViewById(R.id.progress_linear_layout);
        mTaskNumberDisplayer = findViewById(R.id.progress_textview);
        postDelayed(mCheckProgressRunnable, 1000);

        setBackgroundColor(getResources().getColor(R.color.background_bottom_bar));
        setVisibility(GONE);

        setOnClickListener(this);
    }

    /** Update the progress bar content */
    public static void setProgress(String progressKey, int progress){
         ExtraCore.setValue(progressKey, progress + "¤" + "-1");
    }

    /** Update the text and progress content */
    public static void setProgress(String progressKey, int progress, @StringRes int resource, String... message){
        StringBuilder builder = new StringBuilder();
        for(String bit : message){
            builder.append(bit).append("¤");
        }

        ExtraCore.setValue(progressKey, progress + "¤" + resource + "¤" + builder);
    }

    /** Update the text and progress content */
    public static void setProgress(String progressKey, int progress, String message){
        setProgress(progressKey,progress, -1, message);
    }

    /** Update the text and progress content */
    public static void clearProgress(String progressKey){
        setProgress(progressKey, -1, -1);
    }

    @Override
    public void onClick(View v) {
        mLinearLayout.setVisibility(mLinearLayout.getVisibility() == GONE ? VISIBLE : GONE);
    }

}
