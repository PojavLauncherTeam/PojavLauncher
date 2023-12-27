package net.kdt.pojavlaunch.imgcropper;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public interface CropperBehaviour {
    int getLargestImageSide();
    void drawPreHighlight(Canvas canvas);
    void onSelectionRectUpdated();
    void resetTransforms();
    void pan(float dx, float dy);
    void zoom(float dz, float originX, float originY);
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
