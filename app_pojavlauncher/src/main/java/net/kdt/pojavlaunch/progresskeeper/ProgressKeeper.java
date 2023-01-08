package net.kdt.pojavlaunch.progresskeeper;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProgressKeeper {
    private static final ConcurrentHashMap<String, ConcurrentLinkedQueue<ProgressListener>> sProgressListeners = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ProgressState> sProgressStates = new ConcurrentHashMap<>();
    private static final List<TaskCountListener> sTaskCountListeners = Collections.synchronizedList(new ArrayList<>());

    public static void submitProgress(String progressRecord, int progress, int resid, Object... va) {
        ProgressState progressState = sProgressStates.get(progressRecord);
        boolean shouldCallStarted = progressState == null;
        boolean shouldCallEnded = resid == -1 && progress == -1;
        if(shouldCallEnded) {
            shouldCallStarted = false;
            sProgressStates.remove(progressRecord);
            updateTaskCount();
        }else if(shouldCallStarted){
            sProgressStates.put(progressRecord, (progressState = new ProgressState()));
            updateTaskCount();
        }
        if(progressState != null) {
            progressState.progress = progress;
            progressState.resid = resid;
            progressState.varArg = va;
        }

        ConcurrentLinkedQueue<ProgressListener> progressListeners = sProgressListeners.get(progressRecord);
        if(progressListeners != null)
            for(ProgressListener listener : progressListeners) {
                    if(shouldCallStarted) listener.onProgressStarted();
                    else if(shouldCallEnded) listener.onProgressEnded();
                    else listener.onProgressUpdated(progress, resid, va);
            }
    }

    private static void updateTaskCount() {
        int count = sProgressStates.size();
        for(TaskCountListener listener : sTaskCountListeners) {
            listener.onUpdateTaskCount(count);
        }
    }

    public static void addListener(String progressRecord, ProgressListener listener) {
        ProgressState state = sProgressStates.get(progressRecord);
        if(state != null && (state.resid != -1 || state.progress != -1)) {
            listener.onProgressStarted();
            listener.onProgressUpdated(state.progress, state.resid, state.varArg);
        }else{
            listener.onProgressEnded();
        }
       ConcurrentLinkedQueue<ProgressListener> listenerWeakReferenceList = sProgressListeners.get(progressRecord);
        if(listenerWeakReferenceList == null) sProgressListeners.put(progressRecord, (listenerWeakReferenceList = new ConcurrentLinkedQueue<>()));
        listenerWeakReferenceList.add(listener);
    }

    public static void removeListener(String progressRecord, ProgressListener listener) {
        ConcurrentLinkedQueue<ProgressListener> listenerWeakReferenceList = sProgressListeners.get(progressRecord);
        if(listenerWeakReferenceList != null) listenerWeakReferenceList.remove(listener);
    }

    public static void addTaskCountListener(TaskCountListener listener) {
        listener.onUpdateTaskCount(sProgressStates.size());
        if(!sTaskCountListeners.contains(listener)) sTaskCountListeners.add(listener);
    }
    public static void addTaskCountListener(TaskCountListener listener, boolean runUpdate) {
        if(runUpdate) listener.onUpdateTaskCount(sProgressStates.size());
        if(!sTaskCountListeners.contains(listener)) sTaskCountListeners.add(listener);
    }
    public static void removeTaskCountListener(TaskCountListener listener) {
        sTaskCountListeners.remove(listener);
    }

    /**
     * Waits until all tasks are done and runs the runnable, or if there were no pending process remaining
     * The runnable runs from the thread that updated the task count last, and it might be the UI thread,
     * so dont put long running processes in it
     * @param runnable the runnable to run when no tasks are remaining
     */
    public static void waitUntilDone(final Runnable runnable) {
        // If we do it the other way the listener would be removed before it was added, which will cause a listener object leak
        if(getTaskCount() == 0) {
            runnable.run();
            return;
        }
        TaskCountListener listener = new TaskCountListener() {
            @Override
            public void onUpdateTaskCount(int taskCount) {
                if(taskCount == 0) {
                    runnable.run();
                }
                removeTaskCountListener(this);
            }
        };
        addTaskCountListener(listener);
    }

    public static int getTaskCount() {
        return sProgressStates.size();
    }
}
