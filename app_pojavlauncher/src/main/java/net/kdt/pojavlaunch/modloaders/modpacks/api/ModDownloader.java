package net.kdt.pojavlaunch.modloaders.modpacks.api;

import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.utils.DownloadUtils;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.Callable;
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
    private final boolean mUseFileCount;
    private IOException mFirstIOException;
    private long mTotalSize;

    public ModDownloader(File destinationDirectory) {
        this(destinationDirectory, false);
    }

    public ModDownloader(File destinationDirectory, boolean useFileCount) {
        this.mDownloadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        this.mDestinationDirectory = destinationDirectory;
        this.mUseFileCount = useFileCount;
    }

    public void submitDownload(int fileSize, String relativePath, @Nullable String downloadHash, String... url) {
        if(mUseFileCount) mTotalSize += 1;
        else mTotalSize += fileSize;
        mDownloadPool.execute(new DownloadTask(url, new File(mDestinationDirectory, relativePath), downloadHash));
    }

    public void submitDownload(FileInfoProvider infoProvider) {
        if(!mUseFileCount) throw new RuntimeException("This method can only be used in a file-counting ModDownloader");
        mTotalSize += 1;
        mDownloadPool.execute(new FileInfoQueryTask(infoProvider));
    }

    public void awaitFinish(Tools.DownloaderFeedback feedback) throws IOException {
        try {
            mDownloadPool.shutdown();
            while(!mDownloadPool.awaitTermination(20, TimeUnit.MILLISECONDS) && !mTerminator.get()) {
                feedback.updateProgress((int) mDownloadSize.get(), (int) mTotalSize);
            }
            if(mTerminator.get()) {
                mDownloadPool.shutdownNow();
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

    private void downloadFailed(IOException exception) {
        mTerminator.set(true);
        synchronized (mExceptionSyncPoint) {
            if(mFirstIOException == null) {
                mFirstIOException = exception;
                mExceptionSyncPoint.notify();
            }
        }
    }

    class FileInfoQueryTask implements Runnable {
        private final FileInfoProvider mFileInfoProvider;
        public FileInfoQueryTask(FileInfoProvider fileInfoProvider) {
            this.mFileInfoProvider = fileInfoProvider;
        }
        @Override
        public void run() {
            try {
                FileInfo fileInfo = mFileInfoProvider.getFileInfo();
                if(fileInfo == null) return;
                new DownloadTask(new String[]{fileInfo.url},
                        new File(mDestinationDirectory, fileInfo.relativePath), fileInfo.sha1).run();
            }catch (IOException e) {
                downloadFailed(e);
            }
        }
    }

    class DownloadTask implements Runnable, Tools.DownloaderFeedback {
        private final String[] mDownloadUrls;
        private final File mDestination;
        private final String mSha1;
        private int last = 0;

        public DownloadTask(String[] downloadurls,
                            File downloadDestination, String downloadHash) {
            this.mDownloadUrls = downloadurls;
            this.mDestination = downloadDestination;
            this.mSha1 = downloadHash;
        }

        @Override
        public void run() {
            for(String sourceUrl : mDownloadUrls) {
                try {
                    DownloadUtils.ensureSha1(mDestination, mSha1, (Callable<Void>) () -> {
                        IOException exception = tryDownload(sourceUrl);

                        if(exception != null) {
                            downloadFailed(exception);
                        }

                        return null;
                    });

                }catch (IOException e) {
                    return;
                }
            }
        }

        private IOException tryDownload(String sourceUrl) throws InterruptedException {
            IOException exception = null;
            for (int i = 0; i < 5; i++) {
                try {
                    DownloadUtils.downloadFileMonitored(sourceUrl, mDestination, getThreadLocalBuffer(), this);
                    if(mUseFileCount) mDownloadSize.addAndGet(1);
                    return null;
                } catch (InterruptedIOException e) {
                    throw new InterruptedException();
                } catch (IOException e) {
                    e.printStackTrace();
                    exception = e;
                }
                if(!mUseFileCount) {
                    mDownloadSize.addAndGet(-last);
                    last = 0;
                }
            }
            return exception;
        }

        @Override
        public void updateProgress(int curr, int max) {
            if(mUseFileCount) return;
            mDownloadSize.addAndGet(curr - last);
            last = curr;
        }
    }

    public static class FileInfo {
        public final String url;
        public final String relativePath;
        public final String sha1;

        public FileInfo(String url, String relativePath, @Nullable String sha1) {
            this.url = url;
            this.relativePath = relativePath;
            this.sha1 = sha1;
        }
    }

    public interface FileInfoProvider {
        FileInfo getFileInfo() throws IOException;
    }
}
