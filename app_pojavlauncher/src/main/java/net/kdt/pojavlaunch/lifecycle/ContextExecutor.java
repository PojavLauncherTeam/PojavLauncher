package net.kdt.pojavlaunch.lifecycle;

import android.app.Activity;
import android.app.Application;

import net.kdt.pojavlaunch.Tools;

import java.lang.ref.WeakReference;

public class ContextExecutor {
    private static WeakReference<Application> sApplication;
    private static WeakReference<Activity> sActivity;


    /**
     * Schedules a ContextExecutorTask to be executed. For more info on tasks, please read
     * ContextExecutorTask.java
     * @param contextExecutorTask the task to be executed
     */
    public static void execute(ContextExecutorTask contextExecutorTask) {
        Tools.runOnUiThread(()->executeOnUiThread(contextExecutorTask));
    }

    private static void executeOnUiThread(ContextExecutorTask contextExecutorTask) {
        Activity activity = Tools.getWeakReference(sActivity);
        if(activity != null) {
            contextExecutorTask.executeWithActivity(activity);
            return;
        }
        Application application = Tools.getWeakReference(sApplication);
        if(application != null) {
            contextExecutorTask.executeWithApplication(application);
        }else {
            throw new RuntimeException("ContextExecutor.execute() called before Application.onCreate!");
        }
    }

    /**
     * Set the Activity that this ContextExecutor will use for executing tasks
     * @param activity the activity to be used
     */
    public static void setActivity(Activity activity) {
        sActivity = new WeakReference<>(activity);
    }

    /**
     * Clear the Activity previously set, so thet ContextExecutor won't use it to execute tasks.
     */
    public static void clearActivity() {
        if(sActivity != null)
            sActivity.clear();
    }

    /**
     * Set the Application that will be used to execute tasks if the Activity won't be available.
     * @param application the application to use as the fallback
     */
    public static void setApplication(Application application) {
        sApplication = new WeakReference<>(application);
    }

    /**
     * Clear the Application previously set, so that ContextExecutor will notify the user of a critical error
     * that is executing code after the application is ended by the system.
     */
    public static void clearApplication() {
        if(sApplication != null)
            sApplication.clear();
    }


}
