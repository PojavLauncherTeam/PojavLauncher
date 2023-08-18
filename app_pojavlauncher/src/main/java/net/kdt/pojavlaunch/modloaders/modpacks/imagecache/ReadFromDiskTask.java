package net.kdt.pojavlaunch.modloaders.modpacks.imagecache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.kdt.pojavlaunch.Tools;

import java.io.File;

public class ReadFromDiskTask implements Runnable {
    final ModIconCache iconCache;
    final ImageReceiver imageReceiver;
    final File cacheFile;
    final String imageUrl;

    ReadFromDiskTask(ModIconCache iconCache, ImageReceiver imageReceiver, String cacheTag, String imageUrl) {
        this.iconCache = iconCache;
        this.imageReceiver = imageReceiver;
        this.cacheFile = new File(iconCache.cachePath, cacheTag+".ca");
        this.imageUrl = imageUrl;
    }

    public void runDownloadTask() {
        iconCache.cacheLoaderPool.execute(new DownloadImageTask(this));
    }

    @Override
    public void run() {
        if(cacheFile.isDirectory()) {
            return;
        }
        if(cacheFile.canRead()) {
            IconCacheJanitor.waitForJanitorToFinish();
            Bitmap bitmap = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
            if(bitmap != null) {
                Tools.runOnUiThread(()->{
                    if(taskCancelled()) {
                        bitmap.recycle(); // do not leak the bitmap if the task got cancelled right at the end
                        return;
                    }
                    imageReceiver.onImageAvailable(bitmap);
                });
                return;
            }
        }
        if(iconCache.cachePath.canWrite() &&
                !taskCancelled()) { // don't run the download task if the task got canceled
            runDownloadTask();
        }
    }
    @SuppressWarnings("BooleanMethodAlwaysInverted")
    public boolean taskCancelled() {
        return iconCache.checkCancelled(imageReceiver);
    }
}
