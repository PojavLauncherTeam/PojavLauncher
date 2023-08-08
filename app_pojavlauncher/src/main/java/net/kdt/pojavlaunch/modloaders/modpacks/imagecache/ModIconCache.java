package net.kdt.pojavlaunch.modloaders.modpacks.imagecache;

import android.util.Base64;
import android.util.Log;

import net.kdt.pojavlaunch.Tools;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ModIconCache {
    ThreadPoolExecutor cacheLoaderPool = new ThreadPoolExecutor(10,
            10,
            1000,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());
    File cachePath;
    private final List<WeakReference<ImageReceiver>> mCancelledReceivers = new ArrayList<>();
    public ModIconCache() {
        cachePath = new File(Tools.DIR_CACHE, "mod_icons");
        if(!cachePath.exists() && !cachePath.isFile() && Tools.DIR_CACHE.canWrite()) {
            if(!cachePath.mkdirs())
                throw new RuntimeException("Failed to create icon cache directory");
        }

    }

    /**
     * Get an image for a mod with the associated tag and URL to download it in case if its not cached
     * @param imageReceiver the receiver interface that would get called when the image loads
     * @param imageTag the tag of the image to keep track of it
     * @param imageUrl the URL of the image in case if it's not cached
     */
    public void getImage(ImageReceiver imageReceiver, String imageTag, String imageUrl) {
        cacheLoaderPool.execute(new ReadFromDiskTask(this, imageReceiver, imageTag, imageUrl));
    }

    /**
     * Mark the image obtainment task requested with this receiver as "cancelled". This means that
     * this receiver will not be called back and that some tasks related to this image may be
     * prevented from happening or interrupted.
     * @param imageReceiver the receiver to cancel
     */
    public void cancelImage(ImageReceiver imageReceiver) {
        synchronized (mCancelledReceivers) {
            mCancelledReceivers.add(new WeakReference<>(imageReceiver));
        }
    }

    boolean checkCancelled(ImageReceiver imageReceiver) {
        boolean isCanceled = false;
        synchronized (mCancelledReceivers) {
            Iterator<WeakReference<ImageReceiver>> iterator = mCancelledReceivers.iterator();
            while (iterator.hasNext()) {
                WeakReference<ImageReceiver> reference = iterator.next();
                if (reference.get() == null) {
                    iterator.remove();
                    continue;
                }
                if(reference.get() == imageReceiver) {
                    isCanceled = true;
                }
            }
        }
        if(isCanceled) Log.i("IconCache", "checkCancelled("+imageReceiver.hashCode()+") == true");
        return isCanceled;
    }

    /**
     * Get the base64-encoded version of a cached icon by its tag.
     * Note: this functions performs I/O operations, and should not be called on the UI
     * thread.
     * @param imageTag the icon tag
     * @return the base64 encoded image or null if not cached
     */

    public static String getBase64Image(String imageTag) {
        File imagePath = new File(Tools.DIR_CACHE, "mod_icons/"+imageTag+".ca");
        Log.i("IconCache", "Creating base64 version of icon "+imageTag);
        if(!imagePath.canRead() || !imagePath.isFile()) {
            Log.i("IconCache", "Icon does not exist");
            return null;
        }
        try {
            try(FileInputStream fileInputStream = new FileInputStream(imagePath)) {
                byte[] imageBytes = IOUtils.toByteArray(fileInputStream);
                // reencode to png? who cares! our profile icon cache is an omnivore!
                // if some other launcher parses this and dies it is not our problem :troll:
                return "data:image/png;base64,"+ Base64.encodeToString(imageBytes, Base64.DEFAULT);
            }
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
