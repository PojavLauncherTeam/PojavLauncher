package net.kdt.pojavlaunch.progresskeeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProgressKeeper {
    private static final HashMap<String, List<ProgressListener>> sProgressListeners = new HashMap<>();
    private static final HashMap<String, ProgressState> sProgressStates = new HashMap<>();
    private static final List<TaskCountListener> sTaskCountListeners = new ArrayList<>();

    public static synchronized void submitProgress(String progressRecord, int progress, int resid, Object... va) {
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

        List<ProgressListener> progressListeners = sProgressListeners.get(progressRecord);
        if(progressListeners != null)
            for(ProgressListener listener : progressListeners) {
                    if(shouldCallStarted) listener.onProgressStarted();
                    else if(shouldCallEnded) listener.onProgressEnded();
                    else listener.onProgressUpdated(progress, resid, va);
            }
    }

    private static synchronized void updateTaskCount() {
        int count = sProgressStates.size();
        for(TaskCountListener listener : sTaskCountListeners) {
            listener.onUpdateTaskCount(count);
        }
    }

    public static synchronized void addListener(String progressRecord, ProgressListener listener) {
        ProgressState state = sProgressStates.get(progressRecord);
        if(state != null && (state.resid != -1 || state.progress != -1)) {
            listener.onProgressStarted();
            listener.onProgressUpdated(state.progress, state.resid, state.varArg);
        }else{
            listener.onProgressEnded();
        }
        List<ProgressListener> listenerWeakReferenceList = sProgressListeners.get(progressRecord);
        if(listenerWeakReferenceList == null) sProgressListeners.put(progressRecord, (listenerWeakReferenceList = new ArrayList<>()));
        listenerWeakReferenceList.add(listener);
    }

    public static synchronized void removeListener(String progressRecord, ProgressListener listener) {
        List<ProgressListener> listenerWeakReferenceList = sProgressListeners.get(progressRecord);
        if(listenerWeakReferenceList != null) listenerWeakReferenceList.remove(listener);
    }

    public static synchronized void addTaskCountListener(TaskCountListener listener) {
        listener.onUpdateTaskCount(sProgressStates.size());
        if(!sTaskCountListeners.contains(listener)) sTaskCountListeners.add(listener);
    }
    public static synchronized void addTaskCountListener(TaskCountListener listener, boolean runUpdate) {
        if(runUpdate) listener.onUpdateTaskCount(sProgressStates.size());
        if(!sTaskCountListeners.contains(listener)) sTaskCountListeners.add(listener);
    }
    public static synchronized void removeTaskCountListener(TaskCountListener listener) {
        sTaskCountListeners.remove(listener);
    }

    /**
     * Waits until all tasks are done and runs the runnable, or if there were no pending process remaining
     * The runnable runs from the thread that updated the task count last, and it might be the UI thread,
     * so don't put long running processes in it
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
                    removeTaskCountListener(this);
                }
            }
        };
        addTaskCountListener(listener);
    }

    public static synchronized int getTaskCount() {
        return sProgressStates.size();
    }

    public static boolean hasOngoingTasks() {
        return getTaskCount() > 0;
    }
}
