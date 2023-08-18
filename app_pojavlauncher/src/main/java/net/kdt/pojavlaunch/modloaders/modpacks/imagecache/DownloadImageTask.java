package net.kdt.pojavlaunch.modloaders.modpacks.imagecache;

import net.kdt.pojavlaunch.utils.DownloadUtils;

import java.io.IOException;

class DownloadImageTask implements Runnable {
    private final ReadFromDiskTask mParentTask;
    private int mRetryCount;
    DownloadImageTask(ReadFromDiskTask parentTask) {
        this.mParentTask = parentTask;
        this.mRetryCount = 0;
    }

    @Override
    public void run() {
        boolean wasSuccessful = false;
        while(mRetryCount < 5 && !(wasSuccessful = runCatching())) {
            mRetryCount++;
        }
        // restart the parent task to read the image and send it to the receiver
        // if it wasn't cancelled. If it was, then we just die here
        if(wasSuccessful && !mParentTask.taskCancelled())
            mParentTask.iconCache.cacheLoaderPool.execute(mParentTask);
    }

    public boolean runCatching() {
        try {
            IconCacheJanitor.waitForJanitorToFinish();
            DownloadUtils.downloadFile(mParentTask.imageUrl, mParentTask.cacheFile);
            return true;
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
