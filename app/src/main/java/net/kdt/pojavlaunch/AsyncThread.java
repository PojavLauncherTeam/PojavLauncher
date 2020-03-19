package net.kdt.pojavlaunch;

import android.support.annotation.*;

public abstract class AsyncThread<Params, Progress, Result>  extends Thread
{
	private boolean isCancelled = true;
	
	@WorkerThread
    protected abstract Result doInBackground(Params... params);
	
    @MainThread
    protected void onPreExecute() {}
	
	@SuppressWarnings({"UnusedDeclaration"})
    @MainThread
    protected void onPostExecute(Result result) {}
    
    @SuppressWarnings({"UnusedDeclaration"})
    @MainThread
    protected void onProgressUpdate(Progress... values) {}
    
    @SuppressWarnings({"UnusedParameters"})
    @MainThread
    protected void onCancelled(Result result) {
        onCancelled();
    }    

    @MainThread
    protected void onCancelled() {}
    
    public final boolean isCancelled() {
        return isCancelled;
	}
    
    public final boolean cancel() {
		super.stop();
        return true;
    }
}
