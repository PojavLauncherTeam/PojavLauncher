package net.kdt.pojavlaunch.modloaders.modpacks.imagecache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.kdt.pojavlaunch.utils.DownloadUtils;

import java.io.FileOutputStream;
import java.io.IOException;

class DownloadImageTask implements Runnable {
    private static final float BITMAP_FINAL_DIMENSION = 256f;
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
            Bitmap bitmap = BitmapFactory.decodeFile(mParentTask.cacheFile.getAbsolutePath());
            if(bitmap == null) return false;
            int bitmapWidth = bitmap.getWidth(), bitmapHeight = bitmap.getHeight();
            if(bitmapWidth <= BITMAP_FINAL_DIMENSION && bitmapHeight <= BITMAP_FINAL_DIMENSION) {
                bitmap.recycle();
                return true;
            }
            float imageRescaleRatio = Math.min(BITMAP_FINAL_DIMENSION/bitmapWidth, BITMAP_FINAL_DIMENSION/bitmapHeight);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap,
                    (int)(bitmapWidth * imageRescaleRatio),
                    (int)(bitmapHeight * imageRescaleRatio),
                    true);
            bitmap.recycle();
            if(resizedBitmap == bitmap) return true;
            try (FileOutputStream fileOutputStream = new FileOutputStream(mParentTask.cacheFile)) {
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
            } finally {
                resizedBitmap.recycle();
            }
            return true;
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
