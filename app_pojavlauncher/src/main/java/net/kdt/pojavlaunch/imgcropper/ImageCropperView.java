package net.kdt.pojavlaunch.imgcropper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import net.kdt.pojavlaunch.Tools;

import top.defaults.checkerboarddrawable.CheckerboardDrawable;

public class ImageCropperView extends AppCompatImageView {
    private final Matrix mZoomMatrix = new Matrix();
    private final Matrix mTranslateMatrix = new Matrix();
    private final Matrix mTranslateInverse = new Matrix();
    private final Matrix mImageMatrix = new Matrix();
    private float mLastTouchX, mLastTouchY;
    private float mLastDistance = -1f;
    private int mLastTrackedPointer;
    private final Rect mSelectionFrameRect = new Rect();
    private Paint mSelectionPaint;
    private float mSelectionPadding;
    private Bitmap mOriginalBitmap;
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
        mSelectionPaint = new Paint();
        mSelectionPaint.setColor(Color.RED);
        mSelectionPaint.setStrokeWidth(Tools.dpToPx(1));
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
                    pan((int)(x1 - mLastTouchX), (int)(y1 - mLastTouchY));
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
        canvas.drawRect(mSelectionFrameRect, mSelectionPaint);
    }

    private int findPointerIndex(MotionEvent event, int id)  {
        for(int i = 0; i < event.getPointerCount(); i++) {
            if(event.getPointerId(i) == id) return i;
        }
        return -1;
    }

    private void pan(int panX, int panY) {
        mTranslateMatrix.postTranslate(panX, panY);
        computeImageMatrix();
    }

    private void zoom(float zoomLevel, float midpointX, float midpointY) {
        inverse(mTranslateMatrix, mTranslateInverse);
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
        mImageMatrix.reset();
        mImageMatrix.preConcat(mTranslateMatrix);
        mImageMatrix.preConcat(mZoomMatrix);
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
                mSelectionFrameRect.left,
                mSelectionFrameRect.top,
                mSelectionFrameRect.right,
                mSelectionFrameRect.bottom
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
        mSelectionFrameRect.left = centerShiftX;
        mSelectionFrameRect.top = centerShiftY;
        mSelectionFrameRect.right = centerShiftX + lesserDimension;
        mSelectionFrameRect.bottom = centerShiftY + lesserDimension;
    }

    private void reset() {
        mTranslateMatrix.reset();
        mZoomMatrix.reset();
        mTranslateInverse.reset();
        mImageMatrix.reset();
        setImageMatrix(mImageMatrix);
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
        float[] matrixSource = new float[9];
        float[] matrixDest = new float[9];
        source.getValues(matrixSource);
        inverseMatrix(matrixSource, matrixDest);
        destination.setValues(matrixDest);
    }

    // This was made by ChatGPT and i have no clue what's happening here, but it works so eh
    public static void inverseMatrix(float[] matrix, float[] inverse) {
        float determinant = matrix[0] * (matrix[4] * matrix[8] - matrix[5] * matrix[7])
                - matrix[1] * (matrix[3] * matrix[8] - matrix[5] * matrix[6])
                + matrix[2] * (matrix[3] * matrix[7] - matrix[4] * matrix[6]);

        if (determinant == 0) {
            throw new IllegalArgumentException("Matrix is not invertible");
        }

        float invDet = 1 / determinant;

        inverse[0] = (matrix[4] * matrix[8] - matrix[5] * matrix[7]) * invDet;
        inverse[1] = (matrix[2] * matrix[7] - matrix[1] * matrix[8]) * invDet;
        inverse[2] = (matrix[1] * matrix[5] - matrix[2] * matrix[4]) * invDet;
        inverse[3] = (matrix[5] * matrix[6] - matrix[3] * matrix[8]) * invDet;
        inverse[4] = (matrix[0] * matrix[8] - matrix[2] * matrix[6]) * invDet;
        inverse[5] = (matrix[2] * matrix[3] - matrix[0] * matrix[5]) * invDet;
        inverse[6] = (matrix[3] * matrix[7] - matrix[4] * matrix[6]) * invDet;
        inverse[7] = (matrix[1] * matrix[6] - matrix[0] * matrix[7]) * invDet;
        inverse[8] = (matrix[0] * matrix[4] - matrix[1] * matrix[3]) * invDet;
    }
}
