package net.kdt.pojavlaunch.modloaders.modpacks.api;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.utils.DownloadUtils;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ModDownloader {
    private static final ThreadLocal<byte[]> sThreadLocalBuffer = new ThreadLocal<>();
    private final ThreadPoolExecutor mDownloadPool = new ThreadPoolExecutor(4,4,100, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());
    private final AtomicBoolean mTerminator = new AtomicBoolean(false);
    private final AtomicLong mDownloadSize = new AtomicLong(0);
    private final Object mExceptionSyncPoint = new Object();
    private final File mDestinationDirectory;
    private IOException mFirstIOException;
    private long mTotalSize;

    public ModDownloader(File destinationDirectory) {
        this.mDestinationDirectory = destinationDirectory;
    }

    public void submitDownload(int fileSize, String relativePath, String... url) {
        mTotalSize += fileSize;
        mDownloadPool.execute(new DownloadTask(url, new File(mDestinationDirectory, relativePath)));
    }

    public void awaitFinish(Tools.DownloaderFeedback feedback) throws IOException{
        try {
            mDownloadPool.shutdown();
            while(!mDownloadPool.awaitTermination(20, TimeUnit.MILLISECONDS) && !mTerminator.get()) {
                feedback.updateProgress((int) mDownloadSize.get(), (int) mTotalSize);
            }

            if(mTerminator.get()) {
                synchronized (mExceptionSyncPoint) {
                    if(mFirstIOException == null) mExceptionSyncPoint.wait();
                    throw mFirstIOException;
                }
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static byte[] getThreadLocalBuffer() {
        byte[] buffer = sThreadLocalBuffer.get();
        if(buffer != null) return buffer;
        buffer = new byte[8192];
        sThreadLocalBuffer.set(buffer);
        return buffer;
    }

    class DownloadTask implements Runnable, Tools.DownloaderFeedback {
        private final String[] mDownloadUrls;
        private final File mDestination;
        private int last = 0;

        public DownloadTask(String[] downloadurls,
                            File downloadDestination) {
            this.mDownloadUrls = downloadurls;
            this.mDestination = downloadDestination;
        }

        @Override
        public void run() {
            IOException exception = null;
            for(String sourceUrl : mDownloadUrls) {
                try {
                    exception = tryDownload(sourceUrl);
                    if(exception == null) return;
                }catch (InterruptedException e) {
                    return;
                }
            }
            if(exception != null) {
                synchronized (mExceptionSyncPoint) {
                    if(mFirstIOException == null) {
                        mFirstIOException = exception;
                        mExceptionSyncPoint.notify();
                    }
                }
            }
        }

        private IOException tryDownload(String sourceUrl) throws InterruptedException {
            IOException exception = null;
            for (int i = 0; i < 5; i++) {
                try {
                    DownloadUtils.downloadFileMonitored(sourceUrl, mDestination, getThreadLocalBuffer(), this);
                    return null;
                } catch (InterruptedIOException e) {
                    throw new InterruptedException();
                } catch (IOException e) {
                    e.printStackTrace();
                    exception = e;
                }
                mDownloadSize.addAndGet(-last);
                last = 0;
            }
            return exception;
        }

        @Override
        public void updateProgress(int curr, int max) {
            mDownloadSize.addAndGet(curr - last);
            last = curr;
        }
    }
}
