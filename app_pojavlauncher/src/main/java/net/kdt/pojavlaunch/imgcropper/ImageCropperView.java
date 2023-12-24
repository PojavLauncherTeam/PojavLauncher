package net.kdt.pojavlaunch.imgcropper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import net.kdt.pojavlaunch.Tools;

import top.defaults.checkerboarddrawable.CheckerboardDrawable;

public class ImageCropperView extends AppCompatImageView {

    private final Matrix mTranslateInverse = new Matrix();
    private final Matrix mTranslateMatrix = new Matrix();
    private final Matrix mPrescaleMatrix = new Matrix();
    private final Matrix mImageMatrix = new Matrix();
    private final Matrix mZoomMatrix = new Matrix();
    private final RectF mSelectionHighlight = new RectF();
    private final Rect mSelectionRect = new Rect();
    private boolean mTranslateInverseOutdated = true;
    public boolean horizontalLock, verticalLock;
    private float mLastTouchX, mLastTouchY;
    private float mHighlightThickness;
    private float mLastDistance = -1f;
    private int mLastTrackedPointer;
    private float mSelectionPadding;
    private Bitmap mOriginalBitmap;
    private Paint mSelectionPaint;

    public ImageCropperView(Context context) {
        super(context);
        init();
    }

    public ImageCropperView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageCropperView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackground(new CheckerboardDrawable.Builder().build());
        setScaleType(ScaleType.MATRIX);
        mSelectionPadding = Tools.dpToPx(24);
        mHighlightThickness = Tools.dpToPx(3);
        mSelectionPaint = new Paint();
        mSelectionPaint.setColor(Color.DKGRAY);
        mSelectionPaint.setStrokeWidth(mHighlightThickness);
        // Divide the thickness by 2 since we will be needing only half of it for
        // rect highlight correction.
        mHighlightThickness /= 2;
        mSelectionPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        float x1 = event.getX(0);
        float y1 = event.getY(0);
        if(event.getPointerCount() > 1) {
            // More than 1 pointer = pinching
            // Compute the distance and zoom the image with it
            float x2 = event.getX(1);
            float y2 = event.getY(1);
            float deltaXSquared = (x2 - x1) * (x2 - x1);
            float deltaYSquared = (y2 - y1) * (y2 - y1);
            float distance = (float) Math.sqrt(deltaXSquared + deltaYSquared);
            if(mLastDistance != -1) {
                float distanceDelta = distance - mLastDistance;
                float multiplier = 0.005f;
                float midpointX = (x1 + x2) / 2;
                float midpointY = (y1 + y2) / 2;
                zoom(1 + distanceDelta * multiplier, midpointX, midpointY);
            }
            mLastDistance = distance;
            return true;
        } else {
            // Reset lastDistance as it's fairly reliable to assume that when
            // there's less than 2 pointers on the screen, the zoom gesture is over
            mLastDistance = -1f;
        }

        // When not pinching, pan around. Simultaneous panning and zooming proved to be confusing in my testing.
        // Lots of code there to allow seamless finger changing while panning.
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchX = x1;
                mLastTouchY = y1;
                // Remember the pointer index from the start of the gesture.
                // We will be tracking it for the rest of the gesture unless it gets released.
                mLastTrackedPointer = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                // Fond the pointer we should be tracking
                int trackedIndex = findPointerIndex(event, mLastTrackedPointer);
                // By default, we query the X/Y coordinates of pointer index 0. If our tracked
                // pointer is no longer at index 0 and is still tracked, overwrite the coordinates
                // with the expected ones
                if(trackedIndex > 0) {
                    x1 = event.getX(trackedIndex);
                    y1 = event.getY(trackedIndex);
                }
                if(trackedIndex != -1) {
                    // If we still track out current pointer, pan the image by the movement delta
                    pan(x1 - mLastTouchX, y1 - mLastTouchY);
                } else {
                    // Otherwise, mark the new tracked pointer without panning.
                    mLastTrackedPointer = event.getPointerId(0);
                }
                mLastTouchX = x1;
                mLastTouchY = y1;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return dispatchGenericMotionEvent(event);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(mSelectionHighlight, mSelectionPaint);
    }

    private int findPointerIndex(MotionEvent event, int id)  {
        for(int i = 0; i < event.getPointerCount(); i++) {
            if(event.getPointerId(i) == id) return i;
        }
        return -1;
    }

    private void pan(float panX, float panY) {
        if(horizontalLock) panX = 0;
        if(verticalLock) panY = 0;
        mTranslateMatrix.postTranslate(panX, panY);
        if(panX != 0 || panY != 0) {
            // Only mark the matrix as outdated if any amount of panning has occured
            mTranslateInverseOutdated = true;
        }
        computeImageMatrix();
    }

    private void zoom(float zoomLevel, float midpointX, float midpointY) {
        // Do this to avoid constantly inverting the same matrix on each touch event.
        if(mTranslateInverseOutdated) {
            inverse(mTranslateMatrix, mTranslateInverse);
            mTranslateInverseOutdated = false;
        }
        float[] zoomCenter = new float[] {
                midpointX,
                midpointY
        };
        float[] realZoomCenter = new float[2];
        mTranslateInverse.mapPoints(realZoomCenter, 0, zoomCenter, 0, 1);
        mZoomMatrix.postScale(zoomLevel, zoomLevel, realZoomCenter[0], realZoomCenter[1]);
        computeImageMatrix();
    }

    private void computeImageMatrix() {
        mImageMatrix.set(mPrescaleMatrix);
        mImageMatrix.postConcat(mZoomMatrix);
        mImageMatrix.postConcat(mTranslateMatrix);
        setImageMatrix(mImageMatrix);
    }

    public void loadBitmap(Bitmap bitmap) {
        setImageBitmap(bitmap);
        mOriginalBitmap = bitmap;
        reset();
    }

    public Bitmap crop(int targetMaxSide) {
        Matrix imageInverse = new Matrix();
        inverse(mImageMatrix, imageInverse);
        // By inverting the matrix we will effectively "divide" our rectangle by it, thus getting
        // its two points on the bitmap's surface. Math be cool indeed.
        float[] src = new float[] {
                mSelectionRect.left,
                mSelectionRect.top,
                mSelectionRect.right,
                mSelectionRect.bottom
        };
        float[] dst = new float[4];
        imageInverse.mapPoints(dst, 0, src, 0, 2);
        Rect originalBitmapRect = new Rect(
                (int)dst[0], (int)dst[1],
                (int)dst[2], (int)dst[3]
        );
        // Pick the best dimensions for the crop result, shrinking the target if necessary.
        int targetWidth, targetHeight;
        int targetMinDimension = Math.min(originalBitmapRect.width(), originalBitmapRect.height());
        if(targetMinDimension > targetMaxSide) {
            float ratio = (float) targetMinDimension / targetMaxSide;
            targetWidth = (int) (originalBitmapRect.width() * ratio);
            targetHeight = (int) (originalBitmapRect.height() * ratio);
        }else {
            targetWidth = originalBitmapRect.width();
            targetHeight = originalBitmapRect.height();
        }
        Bitmap croppedBitmap = Bitmap.createBitmap(
                targetWidth, targetHeight,
                mOriginalBitmap.getConfig()
        );
        // Draw the bitmap on the target. Doing this allows us to not bother with making sure
        // that originalBitmapRect is fully contained within image bounds.
        Canvas drawCanvas = new Canvas(croppedBitmap);
        drawCanvas.drawBitmap(
                mOriginalBitmap,
                originalBitmapRect,
                new Rect(0, 0, targetWidth, targetHeight),
                null
        );

        return croppedBitmap;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int lesserDimension = (int)(Math.min(w, h) - mSelectionPadding);
        // Calculate the corners of the new selection frame. It should always appear at the center of the view.
        int centerShiftX = (w - lesserDimension) / 2;
        int centerShiftY = (h - lesserDimension) / 2;
        mSelectionRect.left = centerShiftX;
        mSelectionRect.top = centerShiftY;
        mSelectionRect.right = centerShiftX + lesserDimension;
        mSelectionRect.bottom = centerShiftY + lesserDimension;
        // Adjust the selection highlight rectangle to be bigger than the selection area
        // by the highlight thickness, to make sure that the entire inside of the selection highlight
        // will fit into the image
        mSelectionHighlight.left = mSelectionRect.left - mHighlightThickness;
        mSelectionHighlight.top = mSelectionRect.top + mHighlightThickness;
        mSelectionHighlight.right = mSelectionRect.right + mHighlightThickness;
        mSelectionHighlight.bottom = mSelectionRect.bottom - mHighlightThickness;
        computePrescaleMatrix();
    }

    /**
     * Computes a prescale matrix.
     * This matrix basically centers the source image in the selection rect.
     * Mainly intended for convenience of implementing a "Reset" button.
     */
    private void computePrescaleMatrix() {
        if(mOriginalBitmap == null) return;
        int selectionRectWidth = mSelectionRect.width();
        int selectionRectHeight = mSelectionRect.height();
        int imageWidth = mOriginalBitmap.getWidth();
        int imageHeight = mOriginalBitmap.getHeight();
        // A basic "scale to fit while preserving aspect ratio" I have taken from
        // https://stackoverflow.com/a/23105310
        float hRatio =  (float)selectionRectWidth / imageWidth ;
        float vRatio =  (float)selectionRectHeight / imageHeight;
        float ratio  = Math.min (hRatio, vRatio);
        float centerShift_x = (selectionRectWidth - imageWidth*ratio) / 2;
        float centerShift_y = (selectionRectWidth - imageHeight*ratio) / 2;
        centerShift_x += mSelectionRect.left;
        centerShift_y += mSelectionRect.top;
        // By doing setScale() we don't have to reset() the matrix beforehand saving us a
        // JNI transition
        mPrescaleMatrix.setScale(ratio, ratio);
        mPrescaleMatrix.postTranslate(centerShift_x, centerShift_y);
        computeImageMatrix();
    }

    public void resetTransforms() {
        // Don't set the mTranslateInverseOutdated flag to true here as
        // the inverse of an identity matrix (aka the matrix we're setting ours to on reset())
        // is an identity matrix, which technically means that mTranslateInverse gets up-to-date there
        mTranslateMatrix.reset();
        mTranslateInverse.reset();
        mZoomMatrix.reset();
        computeImageMatrix();
    }

    private void reset() {
        computePrescaleMatrix();
        resetTransforms();
        mLastDistance = -1f;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int widthMode = MeasureSpec.getMode(widthSpec), widthSize = MeasureSpec.getSize(widthSpec);
        int heightMode = MeasureSpec.getMode(heightSpec), heightSize = MeasureSpec.getSize(heightSpec);
        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            // No leeway. Size to spec.
            setMeasuredDimension(widthSize, heightSize);
            return;
        }
        int biggestAllowedDimension = Math.max(mOriginalBitmap.getWidth(), mOriginalBitmap.getHeight());
        if(widthMode == MeasureSpec.EXACTLY) biggestAllowedDimension = widthSize;
        if(heightMode == MeasureSpec.EXACTLY) biggestAllowedDimension = heightSize;
        setMeasuredDimension(
                pickDesiredDimension(widthMode, widthSize, biggestAllowedDimension),
                pickDesiredDimension(heightMode, heightSize, biggestAllowedDimension)
        );

    }

    private int pickDesiredDimension(int mode, int size, int desired) {
        switch (mode) {
            case MeasureSpec.EXACTLY:
                return size;
            case MeasureSpec.AT_MOST:
                return Math.min(size, desired);
            case MeasureSpec.UNSPECIFIED:
                return desired;
        }
        return desired;
    }

    /**
     * Android's conditions for matrix inversion are wacky, and sometimes it just stops working out
     * of the blue. So, when Android's accelerated matrix inverse dies, just invert by hand.
     * @param source Source matrix
     * @param destination The inverse of the source matrix
     */
    private void inverse(Matrix source, Matrix destination) {
        if(source.invert(destination)) return;
        float[] matrix = new float[9];
        source.getValues(matrix);
        inverseMatrix(matrix);
        destination.setValues(matrix);
    }

    // This was made by ChatGPT and i have no clue what's happening here, but it works so eh
    public static void inverseMatrix(float[] matrix) {
        float determinant = matrix[0] * (matrix[4] * matrix[8] - matrix[5] * matrix[7])
                - matrix[1] * (matrix[3] * matrix[8] - matrix[5] * matrix[6])
                + matrix[2] * (matrix[3] * matrix[7] - matrix[4] * matrix[6]);

        if (determinant == 0) {
            throw new IllegalArgumentException("Matrix is not invertible");
        }

        float invDet = 1 / determinant;

        float temp0 = (matrix[4] * matrix[8] - matrix[5] * matrix[7]);
        float temp1 = (matrix[2] * matrix[7] - matrix[1] * matrix[8]);
        float temp2 = (matrix[1] * matrix[5] - matrix[2] * matrix[4]);
        float temp3 = (matrix[5] * matrix[6] - matrix[3] * matrix[8]);
        float temp4 = (matrix[0] * matrix[8] - matrix[2] * matrix[6]);
        float temp5 = (matrix[2] * matrix[3] - matrix[0] * matrix[5]);
        float temp6 = (matrix[3] * matrix[7] - matrix[4] * matrix[6]);
        float temp7 = (matrix[1] * matrix[6] - matrix[0] * matrix[7]);
        float temp8 = (matrix[0] * matrix[4] - matrix[1] * matrix[3]);
        matrix[0] = temp0 * invDet;
        matrix[1] = temp1 * invDet;
        matrix[2] = temp2 * invDet;
        matrix[3] = temp3 * invDet;
        matrix[4] = temp4 * invDet;
        matrix[5] = temp5 * invDet;
        matrix[6] = temp6 * invDet;
        matrix[7] = temp7 * invDet;
        matrix[8] = temp8 * invDet;
    }
}
