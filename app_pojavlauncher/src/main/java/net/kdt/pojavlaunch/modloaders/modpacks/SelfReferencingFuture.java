package net.kdt.pojavlaunch.modloaders.modpacks;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class SelfReferencingFuture {
    private final Object mFutureLock = new Object();
    private final FutureInterface mFutureInterface;
    private Future<?> mMyFuture;

    public SelfReferencingFuture(FutureInterface futureInterface) {
        this.mFutureInterface = futureInterface;
    }

    public Future<?> startOnExecutor(ExecutorService executorService) {
        Future<?> future = executorService.submit(this::run);
        synchronized (mFutureLock) {
            mMyFuture = future;
            mFutureLock.notify();
        }
        return future;
    }

    private void run() {
        try {
            synchronized (mFutureLock) {
                if (mMyFuture == null) mFutureLock.wait();
            }
            mFutureInterface.run(mMyFuture);
        }catch (InterruptedException e) {
            Log.i("SelfReferencingFuture", "Interrupted while acquiring own Future");
        }
    }

    public interface FutureInterface {
        void run(Future<?> myFuture);
    }
}
