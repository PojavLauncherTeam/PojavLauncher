package net.kdt.pojavlaunch.imgcropper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;

import net.kdt.pojavlaunch.PojavApplication;
import net.kdt.pojavlaunch.modloaders.modpacks.SelfReferencingFuture;

import java.util.concurrent.Future;

public class RegionDecoderCropBehaviour extends BitmapCropBehaviour {
    private BitmapRegionDecoder mBitmapDecoder;
    private Bitmap mOverlayBitmap;
    private final RectF mOverlayDst = new RectF(0, 0, 0, 0);
    private boolean mRequiresOverlayBitmap;
    private final Matrix mDecoderPrescaleMatrix = new Matrix();
    private final Handler mHiresLoadHandler = new Handler();
    private Future<?> mDecodeFuture;
    private final Runnable mHiresLoadRunnable = ()->{
        RectF subsectionRect = new RectF(0,0, mHostView.getWidth(), mHostView.getHeight());
        RectF overlayDst = new RectF();
        discardDecodeFuture();
        mDecodeFuture = new SelfReferencingFuture(myFuture -> {
            Bitmap overlayBitmap = decodeRegionBitmap(overlayDst, subsectionRect);
            mHiresLoadHandler.post(()->{
                if(myFuture.isCancelled()) return;
                mOverlayBitmap = overlayBitmap;
                mOverlayDst.set(overlayDst);
                mHostView.invalidate();
            });
        }).startOnExecutor(PojavApplication.sExecutorService);
    };

    /**
     * Decoade a region from this Bitmap based on a subsection in the View coordinate space.
     * @param targetDrawRect an output Rect. This Rect is the position at which the region must
     *                       be rendered within subsectionRect.
     * @param subsectionRect the subsection in View coordinate space. Note that this Rect is modified
     *                       by this function and shouldn't be re-used.
     * @return null if the resulting region is bigger than the original image
     *         null if the resulting region is completely out of the original image bounds
     *         null if the resulting region is smaller than 16x16 pixels
     *         null if a region decoding error has occured
     *         the resulting Bitmap region otherwise.
     */
    private Bitmap decodeRegionBitmap(RectF targetDrawRect, RectF subsectionRect) {
        RectF decoderRect = new RectF(0, 0, mBitmapDecoder.getWidth(), mBitmapDecoder.getHeight());
        Matrix matrix = createDecoderImageMatrix();
        Matrix inverse = new Matrix();
        inverse(matrix, inverse);
        transformRect(subsectionRect, inverse);
        // If our current sub-section is bigger than the decoder rect, skip.
        // We do this to avoid unnecessarily loading the image at full resolution.
        if(subsectionRect.width() > decoderRect.width()
                || subsectionRect.height() > decoderRect.height()) return null;
        // If our current sub-section doesn't even intersect the decoder rect, we won't even
        // be able to create an overlay. So, skip.
        if(!subsectionRect.setIntersect(decoderRect, subsectionRect)) return null;
        // In my testing, decoding a region smaller than that breaks the current region decoder instance.
        // So, if it is smaller, skip.
        if(subsectionRect.width() < 16 || subsectionRect.height() < 16) return null;
        // We can't really create a floating-point subsection from a bitmap, so convert the intersected
        // rectangle that we want to get from the decoder into an integer Rect.
        Rect bitmapRegionRect = new Rect(
                (int) subsectionRect.left,
                (int) subsectionRect.top,
                (int) subsectionRect.right,
                (int) subsectionRect.bottom
        );
        transformRect(subsectionRect, matrix);
        targetDrawRect.set(subsectionRect);
        return mBitmapDecoder.decodeRegion(bitmapRegionRect, null);
    }

    private void discardDecodeFuture() {
        if(mDecodeFuture != null) {
            // Putting false here as I don't know how BitmapRegionDecoder will behave when interrupted
            mDecodeFuture.cancel(false);
        }
    }

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
        if (mOverlayBitmap != null) {
            canvas.drawBitmap(mOverlayBitmap, null, mOverlayDst, null);
        } else {
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
        discardDecodeFuture();
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
    private void transformRect(RectF rect, Matrix regionImageInverse) {
        if(regionImageInverse.isIdentity()) return;
        float[] inOutDecodeRect = new float[8];
        inOutDecodeRect[0] = rect.left;
        inOutDecodeRect[1] = rect.top;
        inOutDecodeRect[2] = rect.right;
        inOutDecodeRect[3] = rect.bottom;
        regionImageInverse.mapPoints(inOutDecodeRect, 4, inOutDecodeRect, 0, 2);
        rect.left = inOutDecodeRect[4];
        rect.top = inOutDecodeRect[5];
        rect.right = inOutDecodeRect[6];
        rect.bottom = inOutDecodeRect[7];
    }

    @Override
    public Bitmap crop(int targetMaxSide) {
        RectF drawRect = new RectF();
        Bitmap regionBitmap = decodeRegionBitmap(drawRect, new RectF(mHostView.mSelectionRect));
        if(regionBitmap == null) {
            // If we can't decode a hi-res region, just crop out of the low-res preview. Yes, this will in fact
            // cause the image to be low res, but we can't really avoid that in this case.
            return super.crop(targetMaxSide);
        }

        int targetDimension = targetMaxSide;
        // Use Math.max here as the region bitmap may not always be a square, and we need to make it one without
        // losing detail.
        int regionBitmapSide = Math.max(regionBitmap.getWidth(), regionBitmap.getHeight());
        if(regionBitmapSide < targetDimension) targetDimension = regionBitmapSide;
        // The drawRect will be a subsection of the selectionRect, so we will need to scale it
        // down in order to fit it into the targetDimension x targetDimension bitmap
        // that we will return.
        float scaleRatio = (float)targetDimension / mHostView.mSelectionRect.width();
        Matrix drawRectScaleMatrix = new Matrix();
        drawRectScaleMatrix.setScale(scaleRatio, scaleRatio);
        transformRect(drawRect, drawRectScaleMatrix);

        Bitmap returnBitmap = Bitmap.createBitmap(targetDimension, targetDimension, regionBitmap.getConfig());
        Canvas canvas = new Canvas(returnBitmap);
        canvas.drawBitmap(regionBitmap, null, drawRect, null);
        return returnBitmap;
    }
}
