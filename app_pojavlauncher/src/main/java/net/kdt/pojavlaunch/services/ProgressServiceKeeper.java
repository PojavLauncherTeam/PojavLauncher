package net.kdt.pojavlaunch.services;

import android.content.Context;
import android.util.Log;

import net.kdt.pojavlaunch.progresskeeper.TaskCountListener;

public class ProgressServiceKeeper implements TaskCountListener {
    private final Context context;
    public ProgressServiceKeeper(Context ctx) {
        this.context = ctx;
    }

    @Override
    public void onUpdateTaskCount(int taskCount) {
        if(taskCount > 0) ProgressService.startService(context);
    }
}
