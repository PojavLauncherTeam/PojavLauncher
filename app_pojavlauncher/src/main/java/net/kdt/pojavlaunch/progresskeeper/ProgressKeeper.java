package net.kdt.pojavlaunch.progresskeeper;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProgressKeeper {
    private static final ConcurrentHashMap<String, ConcurrentLinkedQueue<WeakReference<ProgressListener>>> sProgressListeners = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ProgressState> sProgressStates = new ConcurrentHashMap<>();
    private static final ArrayList<TaskCountListener> sTaskCountListeners = new ArrayList<>();

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

        Log.d("ProgressLayout", "shouldCallStarted="+shouldCallStarted+" shouldCallEnded="+shouldCallEnded);
        ConcurrentLinkedQueue<WeakReference<ProgressListener>> listenerWeakReferenceList = sProgressListeners.get(progressRecord);
        if(listenerWeakReferenceList != null) {
            Iterator<WeakReference<ProgressListener>> iterator = listenerWeakReferenceList.iterator();
            while(iterator.hasNext()) {
                ProgressListener listener = iterator.next().get();
                Log.i("ProgressLayout", listener+"");
                if(listener != null) {
                    if(shouldCallStarted) listener.onProgressStarted();
                    else if(shouldCallEnded) listener.onProgressEnded();
                    else listener.onProgressUpdated(progress, resid, va);
                } else iterator.remove();
            }
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
            Log.d("ProgressLayout", "Resubmitting UI state");
        }else{
            listener.onProgressEnded();
        }
        ConcurrentLinkedQueue<WeakReference<ProgressListener>> listenerWeakReferenceList = sProgressListeners.get(progressRecord);
        if(listenerWeakReferenceList == null) sProgressListeners.put(progressRecord, (listenerWeakReferenceList = new ConcurrentLinkedQueue<>()));
        else {
            Iterator<WeakReference<ProgressListener>> iterator = listenerWeakReferenceList.iterator();
            while(iterator.hasNext()) {
                if(iterator.next().get() == null) iterator.remove();
            }
        }
        listenerWeakReferenceList.add(new WeakReference<>(listener));
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
    public static int getTaskCount() {
        return sProgressStates.size();
    }
}
