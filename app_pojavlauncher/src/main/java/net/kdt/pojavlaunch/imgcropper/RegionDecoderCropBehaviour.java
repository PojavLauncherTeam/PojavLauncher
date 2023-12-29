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
    private boolean mRenderLowResBackground;
    private final Matrix mDecoderPrescaleMatrix = new Matrix();
    private final Handler mHiresLoadHandler = new Handler();
    private final Runnable mHiresLoadRunnable = ()->{
        Rect subsectionRect = new Rect(0,0, mHostView.getWidth(), mHostView.getHeight());
        Rect decoderRect = new Rect(0, 0, mBitmapDecoder.getWidth(), mBitmapDecoder.getHeight());
        Rect subsectionIntersection = new Rect();
        Matrix matrix = createDecoderImageMatrix();
        Matrix inverse = new Matrix();
        inverse(matrix, inverse);
        transformRect(subsectionRect, inverse);
        // If our current sub-section is bigger than the decoder rect, skip.
        // We do this to avoid unnecerssarily loading the image at full resolution.
        if(subsectionRect.width() > decoderRect.width()
                || subsectionRect.height() > decoderRect.height()) return;
        // If our current sub-section doesn't even intersect the decoder rect, we won't even
        // be able to create an overlay. So, skip.
        if(!subsectionIntersection.setIntersect(decoderRect, subsectionRect)) return;
        // In my testing, decoding a region smaller than that breaks the current region decoder instance.
        // So, if it is smaller, skip.
        if(subsectionIntersection.width() < 16 || subsectionRect.width() < 16) return;
        mOverlayBitmap = mBitmapDecoder.decodeRegion(subsectionIntersection, null);
        if(decoderRect.contains(subsectionRect)) {
            // Doing the matrix approach when the subsection is fully contained within the
            // decoder rect causes weird issues with width/height, so just force the full View
            // width/height there
            mOverlayDst.top = mOverlayDst.left = 0;
            mOverlayDst.right = mHostView.getWidth();
            mOverlayDst.bottom = mHostView.getHeight();
            // DIsable the low-res rendering as the overlay completely fills the view.
            mRenderLowResBackground = false;
        } else {
            // When not fully containing the image we still need a hi-res version, so transform the
            // intersection back into View coordinate space to use as the destination.
            // Sadly this causes weird issues with the resolution. Have no idea how to resolve yet.
            transformRect(subsectionIntersection, matrix);
            mOverlayDst.set(subsectionIntersection);
            // Render the low-res original image in the background to avoid the messy corners.
            mRenderLowResBackground = true;
        }
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
        if(mOverlayBitmap == null || mRenderLowResBackground) super.drawPreHighlight(canvas);
        if(mOverlayBitmap != null) canvas.drawBitmap(mOverlayBitmap, null, mOverlayDst, null);
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
     * Create a Matrix that can be used to transform points from the bitmap coordinate space into the
     * View coordinate space.
     */
    private Matrix createDecoderImageMatrix() {
        Matrix decoderImageMatrix = new Matrix(mDecoderPrescaleMatrix);
        decoderImageMatrix.postConcat(mZoomMatrix);
        decoderImageMatrix.postConcat(mTranslateMatrix);
        return decoderImageMatrix;
    }

    /**
     * Transform the coordinates of the Rect using the supplied Matrix.
     * @param rect the input/ouput Rect for this operation
     * @param regionImageInverse the Matrix for transforming the Rect.
     */
    private void transformRect(Rect rect, Matrix regionImageInverse) {
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
