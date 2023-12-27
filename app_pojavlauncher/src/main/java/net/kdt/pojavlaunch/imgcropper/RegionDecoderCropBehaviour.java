package net.kdt.pojavlaunch.imgcropper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;

public class RegionDecoderCropBehaviour extends BitmapCropBehaviour {
    private BitmapRegionDecoder mBitmapDecoder;
    private Bitmap mOverlayBitmap;
    private final Rect mOverlayDst = new Rect(0, 0, 0, 0);
    private boolean mRequiresOverlayBitmap;
    private final Matrix mDecoderPrescaleMatrix = new Matrix();
    private final Handler mHiresLoadHandler = new Handler();
    private final Runnable mHiresLoadRunnable = ()->{
        Rect subsectionRect = new Rect(0,0, mHostView.getWidth(), mHostView.getHeight());
        Rect decoderRect = new Rect(0, 0, mBitmapDecoder.getWidth(), mBitmapDecoder.getHeight());
        transformRectToDecoderCoords(subsectionRect);
        if(!decoderRect.contains(subsectionRect)) return;
        mOverlayBitmap = mBitmapDecoder.decodeRegion(subsectionRect, null);
        mHostView.invalidate();
    };

    public RegionDecoderCropBehaviour(CropperView hostView) {
        super(hostView);
    }

    public void loadRegionDecoder(BitmapRegionDecoder bitmapRegionDecoder) {
        mBitmapDecoder = bitmapRegionDecoder;
        reset();
    }

    @Override
    public int getLargestImageSide() {
        if(mBitmapDecoder == null) return 0;
        return Math.max(mBitmapDecoder.getWidth(), mBitmapDecoder.getHeight());
    }

    @Override
    public void drawPreHighlight(Canvas canvas) {
        if(mOverlayBitmap != null) {
            mOverlayDst.right = mHostView.getWidth();
            mOverlayDst.bottom = mHostView.getHeight();
            canvas.drawBitmap(mOverlayBitmap, null, mOverlayDst, null);
        }else {
            super.drawPreHighlight(canvas);
        }
    }

    @Override
    protected void refresh() {
        if(mOverlayBitmap != null) {
            mOverlayBitmap.recycle();
            mOverlayBitmap = null;
        }
        mHiresLoadHandler.removeCallbacks(mHiresLoadRunnable);
        if(mRequiresOverlayBitmap) {
            mHiresLoadHandler.postDelayed(mHiresLoadRunnable, 200);
        }
        super.refresh();
    }

    @Override
    public void reset() {
        createScaledSourceBitmap();
        computeDecoderPrescaleMatrix();
        super.reset();
    }

    @Override
    public void onSelectionRectUpdated() {
        createScaledSourceBitmap();
        computeDecoderPrescaleMatrix();
        super.onSelectionRectUpdated();
    }

    /**
     * Load a scaled down version of the Bitmap that will be used for zooming and panning in the view.
     * BitmapCropBehaviour will base its prescale matrix off of this Bitmap.
     */
    private void createScaledSourceBitmap() {
        if(mBitmapDecoder == null) return;
        int width = mHostView.getWidth();
        int height = mHostView.getHeight();
        int imageWidth = mBitmapDecoder.getWidth();
        int imageHeight = mBitmapDecoder.getHeight();
        float hRatio =  (float)width / imageWidth ;
        float vRatio =  (float)height / imageHeight;
        float ratio = Math.max(hRatio, vRatio);
        BitmapFactory.Options options = new BitmapFactory.Options();
        if(ratio < 1 && ratio != 0) {
            ratio = 1 / ratio;
            options.inSampleSize = (int)Math.floor(ratio);
            mRequiresOverlayBitmap = true;
        }else {
            mRequiresOverlayBitmap = false;
        }
        mOriginalBitmap = mBitmapDecoder.decodeRegion(
                new Rect(0, 0, imageWidth, imageHeight),
                options
        );
    }

    /**
     * Compute the prescale matrix for the image bounds of the BitmapRegionDecoder. Used to
     * align the transforms done on the scaled source bitmap with the bitmap region decoder.
     */
    private void computeDecoderPrescaleMatrix() {
        computePrescaleMatrix(
                mDecoderPrescaleMatrix,
                mBitmapDecoder.getWidth(),
                mBitmapDecoder.getHeight()
        );
    }

    /**
     * Create a Matrix that can be used to transform points from the View coordinate space to the
     * BitmapRegionDecoder coordinate space based on current pan and zoom transforms.
     * @return the newly allocated Matrix for these operations
     */
    private Matrix createDecoderImageInverse() {
        Matrix decoderImageMatrix = new Matrix(mDecoderPrescaleMatrix);
        decoderImageMatrix.postConcat(mZoomMatrix);
        decoderImageMatrix.postConcat(mTranslateMatrix);
        inverse(decoderImageMatrix, decoderImageMatrix);
        return decoderImageMatrix;
    }

    /**
     * Transform the coordinates of the Rect into the coordinate space of RegionImageDecoder
     * based on currently applied pan/zoom transforms, and write them back into the current
     * Rect.
     * @param rect the input/ouput Rect for this operation
     */
    private void transformRectToDecoderCoords(Rect rect) {
        Matrix regionImageInverse = createDecoderImageInverse();
        float[] inOutDecodeRect = new float[8];
        inOutDecodeRect[0] = rect.left;
        inOutDecodeRect[1] = rect.top;
        inOutDecodeRect[2] = rect.right;
        inOutDecodeRect[3] = rect.bottom;
        regionImageInverse.mapPoints(inOutDecodeRect, 4, inOutDecodeRect, 0, 2);
        rect.left = (int)inOutDecodeRect[4];
        rect.top = (int)inOutDecodeRect[5];
        rect.right = (int)inOutDecodeRect[6];
        rect.bottom = (int)inOutDecodeRect[7];
    }

    @Override
    public Bitmap crop(int targetMaxSide) {
        return null;
    }
}
