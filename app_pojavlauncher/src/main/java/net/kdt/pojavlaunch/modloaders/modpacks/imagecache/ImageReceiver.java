package net.kdt.pojavlaunch.modloaders.modpacks.imagecache;

import android.graphics.Bitmap;

/**
 * ModIconCache will call your view back when the image becomes available with this interface
 */
public interface ImageReceiver {
    void onImageAvailable(Bitmap image);
}
