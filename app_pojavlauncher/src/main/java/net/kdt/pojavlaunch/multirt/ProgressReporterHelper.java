package net.kdt.pojavlaunch.multirt;

import android.util.Log;

public class ProgressReporterHelper implements Runnable, MultiRTUtils.RuntimeProgressReporter{
    final MultiRTUtils.RuntimeProgressReporter reporter;
    int resid;
    Object[] stuff;
    boolean run = true;
    public ProgressReporterHelper(MultiRTUtils.RuntimeProgressReporter reporter) {
        this.reporter = reporter;
        new Thread(this).start();
    }

    @Override
    public void run() {
        Log.i("ReporterHelper","ReporterHelper started");
        while(run) {
            try {
                Thread.sleep(15);
            } catch (InterruptedException ignored) {
            }
            reporter.reportStringProgress(resid, stuff);
        }
        Log.i("ReporterHelper","ReporterHelper stopped");
    }
    public void stop() {
        run = false;
    }
    @Override
    public void reportStringProgress(int resId, Object... stuff) {
        this.resid = resId;
        this.stuff = stuff;
    }
}
