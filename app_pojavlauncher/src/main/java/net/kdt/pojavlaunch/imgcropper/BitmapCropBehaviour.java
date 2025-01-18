package net.kdt.pojavlaunch.imgcropper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

import net.kdt.pojavlaunch.utils.MatrixUtils;

public class BitmapCropBehaviour implements CropperBehaviour{
    private final Matrix mTranslateInverse = new Matrix();
    protected final Matrix mTranslateMatrix = new Matrix();
    private final Matrix mPrescaleMatrix = new Matrix();
    private final Matrix mImageMatrix = new Matrix();
    protected final Matrix mZoomMatrix = new Matrix();
    private boolean mTranslateInverseOutdated = true;
    protected Bitmap mOriginalBitmap;
    protected CropperView mHostView;
    public BitmapCropBehaviour(CropperView hostView) {
        mHostView = hostView;
    }
    @Override
    public void pan(float panX, float panY) {
        if(mHostView.horizontalLock) panX = 0;
        if(mHostView.verticalLock) panY = 0;
        if(panX != 0 || panY != 0) {
            // Actually translate and refresh only if either of the pan deltas are nonzero
            mTranslateMatrix.postTranslate(panX, panY);
            mTranslateInverseOutdated = true;
            refresh();
        }
    }

    public void zoom(float zoomLevel, float midpointX, float midpointY) {
        // Do this to avoid constantly inverting the same matrix on each touch event.
        if(mTranslateInverseOutdated) {
            MatrixUtils.inverse(mTranslateMatrix, mTranslateInverse);
            mTranslateInverseOutdated = false;
        }
        float[] zoomCenter = new float[] {
                midpointX,
                midpointY
        };
        float[] realZoomCenter = new float[2];
        mTranslateInverse.mapPoints(realZoomCenter, 0, zoomCenter, 0, 1);
        mZoomMatrix.postScale(zoomLevel, zoomLevel, realZoomCenter[0], realZoomCenter[1]);
        refresh();
    }

    public int getLargestImageSide() {
        if(mOriginalBitmap == null) return 0;
        return Math.max(mOriginalBitmap.getWidth(), mOriginalBitmap.getHeight());
    }

    @Override
    public void drawPreHighlight(Canvas canvas) {
        canvas.drawBitmap(mOriginalBitmap, mImageMatrix, null);
    }

    @Override
    public void onSelectionRectUpdated() {
        computeLocalPrescaleMatrix();
    }

    public void applyImage() {
        mHostView.reset();
        computeLocalPrescaleMatrix();
        resetTransforms();
        refresh();
    }

    public void setBitmap(Bitmap bitmap) {
        mOriginalBitmap = bitmap;
    }

    protected void refresh() {
        mImageMatrix.set(mPrescaleMatrix);
        mImageMatrix.postConcat(mZoomMatrix);
        mImageMatrix.postConcat(mTranslateMatrix);
        mHostView.invalidate();
    }

    public Bitmap crop(int targetMaxSide) {
        Matrix imageInverse = new Matrix();
        MatrixUtils.inverse(mImageMatrix, imageInverse);
        // By inverting the matrix we will effectively "divide" our rectangle by it, thus getting
        // its two points on the surface of the bitmap. Math be cool indeed.
        Rect targetRect = new Rect();
        MatrixUtils.transformRect(mHostView.mSelectionRect, targetRect, imageInverse);
        // Pick the best dimensions for the crop result, shrinking the target if necessary.
        int targetWidth, targetHeight;
        int targetMinDimension = Math.min(targetRect.width(), targetRect.height());
        if(targetMaxSide < targetMinDimension) {
            float ratio = (float) targetMaxSide / targetMinDimension;
            targetWidth = (int) (targetRect.width() * ratio);
            targetHeight = (int) (targetRect.height() * ratio);
        }else {
            targetWidth = targetRect.width();
            targetHeight = targetRect.height();
        }
        Bitmap croppedBitmap = Bitmap.createBitmap(
                targetWidth, targetHeight,
                mOriginalBitmap.getConfig()
        );
        // Draw the bitmap on the target. Doing this allows us to not bother with making sure
        // that targetRect is fully contained within image bounds.
        Canvas drawCanvas = new Canvas(croppedBitmap);
        drawCanvas.drawBitmap(
                mOriginalBitmap,
                targetRect,
                new Rect(0, 0, targetWidth, targetHeight),
                null
        );

        return croppedBitmap;
    }

    /**
     * Computes a prescale matrix.
     * This matrix basically centers the source image in the selection rect.
     * Mainly intended for convenience of implementing a "Reset" button.
     */
    protected void computePrescaleMatrix(Matrix inMatrix, int imageWidth, int imageHeight) {
        if(mOriginalBitmap == null) return;
        int selectionRectWidth = mHostView.mSelectionRect.width();
        int selectionRectHeight = mHostView.mSelectionRect.height();
        // A basic "scale to fit while preserving aspect ratio" I have taken from
        // https://stackoverflow.com/a/23105310
        float hRatio =  (float)selectionRectWidth / imageWidth ;
        float vRatio =  (float)selectionRectHeight / imageHeight;
        float ratio  = Math.min (hRatio, vRatio);
        float centerShift_x = (selectionRectWidth - imageWidth*ratio) / 2;
        float centerShift_y = (selectionRectWidth - imageHeight*ratio) / 2;
        centerShift_x += mHostView.mSelectionRect.left;
        centerShift_y += mHostView.mSelectionRect.top;
        // By doing setScale() we don't have to reset() the matrix beforehand saving us a
        // JNI transition
        inMatrix.setScale(ratio, ratio);
        inMatrix.postTranslate(centerShift_x, centerShift_y);
        refresh();
    }

    private void computeLocalPrescaleMatrix() {
        computePrescaleMatrix(
                mPrescaleMatrix,
                mOriginalBitmap.getWidth(),
                mOriginalBitmap.getHeight()
        );
    }

    public void resetTransforms() {
        // Don't set the mTranslateInverseOutdated flag to true here as
        // the inverse of an identity matrix (aka the matrix we're setting ours to on reset())
        // is an identity matrix, which technically means that mTranslateInverse gets up-to-date there
        mTranslateMatrix.reset();
        mTranslateInverse.reset();
        mZoomMatrix.reset();
        refresh();
    }
}
