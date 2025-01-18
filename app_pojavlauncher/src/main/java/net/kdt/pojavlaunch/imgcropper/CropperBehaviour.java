package net.kdt.pojavlaunch.imgcropper;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public interface CropperBehaviour {
    /**
     * Get the largest side of the image currently loaded into this CropperBehaviour.
     * @return the largest side of the loaded image
     */
    int getLargestImageSide();

    /**
     * This method is called by CropperView for the CropperBehaviour to draw its image with all
     * the transforms applied, It is called before the selection rectangle is drawn.
     * @param canvas the canvas to draw the image on
     */
    void drawPreHighlight(Canvas canvas);

    /**
     * This method is called by CropperView to let the behaviour know that the selection rect
     * dimensions were updated.
     */
    void onSelectionRectUpdated();

    /**
     * This method is called by CropperView or by the programmer to reset all current transforms
     * applied to the image loaded within this CropperBehaviour
     */
    void resetTransforms();

    /**
     * Prepares this behaviour for being rendered in CropperView.
     */
    void applyImage();

    /**
     * This method is called by CropperView to pan the image
     * @param dx pan delta-X
     * @param dy pan delta-Y
     */
    void pan(float dx, float dy);

    /**
     * This method is called by CropperView to zoom the image
     * @param dz zoom delta-Z
     * @param originX the X coordinate of a point at which the image should be zoomed
     * @param originY the Y coordinate of a point at which the image should be zoomed
     */
    void zoom(float dz, float originX, float originY);

    /**
     * Crop the image according to current transforms, with the targetMaxSide specifying the
     * maximum side of the resulting 1:1 bitmap.
     * @param targetMaxSide the maximum side of the 1:1 bitmap
     * @return the crop of the behaviour's image
     */
    Bitmap crop(int targetMaxSide);
    CropperBehaviour DUMMY = new CropperBehaviour() {
        @Override
        public int getLargestImageSide() {
            return 0;
        }

        @Override
        public void drawPreHighlight(Canvas canvas) {

        }

        @Override
        public void onSelectionRectUpdated() {

        }

        @Override
        public void resetTransforms() {

        }

        @Override
        public void applyImage() {

        }

        @Override
        public void pan(float dx, float dy) {

        }

        @Override
        public void zoom(float dz, float originX, float originY) {

        }

        @Override
        public Bitmap crop(int targetMaxSide) {
            return null;
        }
    };
}
