package net.kdt.pojavlaunch.modloaders.modpacks.imagecache;

import android.util.Log;

import net.kdt.pojavlaunch.PojavApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This image is intended to keep the mod icon cache tidy (aka under 100 megabytes)
 */
public class IconCacheJanitor implements Runnable{
    public static final long CACHE_SIZE_LIMIT = 104857600; // The cache size limit, 100 megabytes
    public static final long CACHE_BRINGDOWN = 52428800; // The size to which the cache should be brought
    // in case of an overflow, 50 mb
    private static Future<?> sJanitorFuture;
    private static boolean sJanitorRan = false;
    private IconCacheJanitor() {
        // don't allow others to create this
    }
    @Override
    public void run() {
        File modIconCachePath = ModIconCache.getImageCachePath();
        if(!modIconCachePath.isDirectory() || !modIconCachePath.canRead()) return;
        File[] modIconFiles = modIconCachePath.listFiles();
        if(modIconFiles == null) return;
        ArrayList<File> writableModIconFiles = new ArrayList<>(modIconFiles.length);
        long directoryFileSize = 0;
        for(File modIconFile : modIconFiles) {
            if(!modIconFile.isFile() || !modIconFile.canRead()) continue;
            directoryFileSize += modIconFile.length();
            if(!modIconFile.canWrite()) continue;
            writableModIconFiles.add(modIconFile);
        }
        if(directoryFileSize < CACHE_SIZE_LIMIT)  {
            Log.i("IconCacheJanitor", "Skipping cleanup because there's not enough to clean up");
            return;
        }
        Arrays.sort(modIconFiles,
                (x,y)-> Long.compare(y.lastModified(), x.lastModified())
        );
        int filesCleanedUp = 0;
        for(File modFile : writableModIconFiles) {
            if(directoryFileSize < CACHE_BRINGDOWN) break;
            long modFileSize = modFile.length();
            if(modFile.delete()) {
                directoryFileSize -= modFileSize;
                filesCleanedUp++;
            }
        }
        Log.i("IconCacheJanitor", "Cleaned up "+filesCleanedUp+ " files");
        synchronized (IconCacheJanitor.class) {
            sJanitorFuture = null;
            sJanitorRan = true;
        }
    }

    /**
     * Runs the janitor task, unless there was one running already or one has ran already
     */
    public static void runJanitor() {
        synchronized (IconCacheJanitor.class) {
            if (sJanitorFuture != null || sJanitorRan) return;
            sJanitorFuture = PojavApplication.sExecutorService.submit(new IconCacheJanitor());
        }
    }

    /**
     * Waits for the janitor task to finish, if there is one running already
     * Note that the thread waiting must not be interrupted.
     */
    public static void waitForJanitorToFinish() {
        synchronized (IconCacheJanitor.class) {
            if (sJanitorFuture == null) return;
            try {
                sJanitorFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException("Should not happen!", e);
            }
        }
    }
}
