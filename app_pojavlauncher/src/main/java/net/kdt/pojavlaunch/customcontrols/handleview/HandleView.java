/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This class has been splited from android/widget/Editor$HandleView.java
 */
package net.kdt.pojavlaunch.customcontrols.handleview;

import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.customcontrols.buttons.ControlButton;


public abstract class HandleView extends View implements ViewPositionListener, View.OnLongClickListener
 {
    protected Drawable mDrawable;
    protected Drawable mDrawableLtr;
    protected Drawable mDrawableRtl;
    private final PopupWindow mContainer;
    // Position with respect to the parent TextView
    private int mPositionX, mPositionY;
    private boolean mIsDragging;
    // Offset from touch position to mPosition
    private float mTouchToWindowOffsetX, mTouchToWindowOffsetY;
    protected int mHotspotX;
    protected int mHorizontalGravity;
    // Offsets the hotspot point up, so that cursor is not hidden by the finger when moving up
    private float mTouchOffsetY;
    // Where the touch position should be on the handle to ensure a maximum cursor visibility
    private float mIdealVerticalOffset;
    // Parent's (TextView) previous position in window
    private int mLastParentX, mLastParentY;
    // Transient action popup window for Paste and Replace actions
    protected ActionPopupWindow mActionPopupWindow;
    // Previous text character offset
    private int mPreviousOffset = -1;
    // Previous text character offset
    private boolean mPositionHasChanged = true;
    // Used to delay the appearance of the action popup window
    private Runnable mActionPopupShower;
    // Minimum touch target size for handles
    private int mMinSize;
    protected ControlButton mView;
    
    // MOD: Addition. Save old size of parent
    private int mDownWidth, mDownHeight;
    // int mWindowPosX, mWindowPosY;

    private PositionListener mPositionListener;
    public PositionListener getPositionListener() {
        if (mPositionListener == null) {
            mPositionListener = new PositionListener(mView);
        }
        return mPositionListener;
    }

    public HandleView(ControlButton view) {
        super(view.getContext());
        
        mView = view;
        
        mDownWidth = view.getLayoutParams().width;
        mDownHeight = view.getLayoutParams().height;
        
        mContainer = new PopupWindow(view.getContext(), null, android.R.attr.textSelectHandleWindowStyle);
        mContainer.setSplitTouchEnabled(true);
        mContainer.setClippingEnabled(false);
        mContainer.setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL);
        mContainer.setContentView(this);

        mDrawableLtr = view.getContext().getDrawable(R.drawable.text_select_handle_left_material);
        mDrawableRtl = view.getContext().getDrawable(R.drawable.text_select_handle_right_material);
        mMinSize = view.getContext().getResources().getDimensionPixelSize(R.dimen.text_handle_min_size);

        setOnLongClickListener(this);
            
        updateDrawable();

        final int handleHeight = getPreferredHeight();
        mTouchOffsetY = -0.3f * handleHeight;
        mIdealVerticalOffset = 0.7f * handleHeight;
    }


    protected void updateDrawable() {
        // final int offset = getCurrentCursorOffset();
        final boolean isRtlCharAtOffset = true; // mView.getLayout().isRtlCharAt(offset);
        mDrawable = isRtlCharAtOffset ? mDrawableRtl : mDrawableLtr;
        mHotspotX = getHotspotX(mDrawable, isRtlCharAtOffset);
        mHorizontalGravity = getHorizontalGravity(isRtlCharAtOffset);
    }

    protected abstract int getHotspotX(Drawable drawable, boolean isRtlRun);
    protected abstract int getHorizontalGravity(boolean isRtlRun);

    // Touch-up filter: number of previous positions remembered
    private static final int HISTORY_SIZE = 5;
    private static final int TOUCH_UP_FILTER_DELAY_AFTER = 150;
    private static final int TOUCH_UP_FILTER_DELAY_BEFORE = 350;
    private final long[] mPreviousOffsetsTimes = new long[HISTORY_SIZE];
    private final int[] mPreviousOffsets = new int[HISTORY_SIZE];
    private int mPreviousOffsetIndex = 0;
    private int mNumberPreviousOffsets = 0;

    private void startTouchUpFilter(int offset) {
        mNumberPreviousOffsets = 0;
        addPositionToTouchUpFilter(offset);
    }

    private void addPositionToTouchUpFilter(int offset) {
        mPreviousOffsetIndex = (mPreviousOffsetIndex + 1) % HISTORY_SIZE;
        mPreviousOffsets[mPreviousOffsetIndex] = offset;
        mPreviousOffsetsTimes[mPreviousOffsetIndex] = SystemClock.uptimeMillis();
        mNumberPreviousOffsets++;
    }

    private void filterOnTouchUp() {
        final long now = SystemClock.uptimeMillis();
        int i = 0;
        int index = mPreviousOffsetIndex;
        final int iMax = Math.min(mNumberPreviousOffsets, HISTORY_SIZE);
        while (i < iMax && (now - mPreviousOffsetsTimes[index]) < TOUCH_UP_FILTER_DELAY_AFTER) {
            i++;
            index = (mPreviousOffsetIndex - i + HISTORY_SIZE) % HISTORY_SIZE;
        }

        if (i > 0 && i < iMax &&
            (now - mPreviousOffsetsTimes[index]) > TOUCH_UP_FILTER_DELAY_BEFORE) {
            positionAtCursorOffset(mPreviousOffsets[index], false);
        }
    }

    public boolean offsetHasBeenChanged() {
        return mNumberPreviousOffsets > 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getPreferredWidth(), getPreferredHeight());
    }

    private int getPreferredWidth() {
        return Math.max(mDrawable.getIntrinsicWidth(), mMinSize);
    }

    private int getPreferredHeight() {
        return Math.max(mDrawable.getIntrinsicHeight(), mMinSize);
    }

    public void show() {
        if (isShowing()) return;

        getPositionListener().addSubscriber(this, true /* local position may change */);

        // Make sure the offset is always considered new, even when focusing at same position
        mPreviousOffset = -1;
        positionAtCursorOffset(getCurrentCursorOffset(), false);

        hideActionPopupWindow();
    }

    protected void dismiss() {
        mIsDragging = false;
        mContainer.dismiss();
        onDetached();
    }

    public void hide() {
        dismiss();

        getPositionListener().removeSubscriber(this);
    }

    void showActionPopupWindow(int delay, Object object) {
        if (mActionPopupWindow == null) {
            mActionPopupWindow = new ActionPopupWindow(this, object);
        }
        if (mActionPopupShower == null) {
            mActionPopupShower = new Runnable() {
                public void run() {
                    try {
                        mActionPopupWindow.show();
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            };
        } else {
            mView.removeCallbacks(mActionPopupShower);
        }
        mView.postDelayed(mActionPopupShower, delay);
    }

    protected void hideActionPopupWindow() {
        if (mActionPopupShower != null) {
            mView.removeCallbacks(mActionPopupShower);
        }
        if (mActionPopupWindow != null) {
            mActionPopupWindow.hide();
        }
    }

    public boolean isShowing() {
        return mContainer.isShowing();
    }

    private boolean isVisible() {
        // Always show a dragging handle.
        if (mIsDragging) {
            return true;
        }

        return mView.getVisibility() == View.VISIBLE;
    }

    public abstract int getCurrentCursorOffset();

    protected abstract void updateSelection(int offset);

    public abstract void updatePosition(float x, float y);

    protected void positionAtCursorOffset(int offset, boolean parentScrolled) {
        mPositionX = mView.getWidth();
        mPositionY = mView.getHeight();
        
        mPositionHasChanged = true;
    }

    public void updatePosition(int parentPositionX, int parentPositionY,
                               boolean parentPositionChanged, boolean parentScrolled) {
        positionAtCursorOffset(getCurrentCursorOffset(), parentScrolled);
        if (parentPositionChanged || mPositionHasChanged) {
            if (mIsDragging) {
                // Update touchToWindow offset in case of parent scrolling while dragging
                if (parentPositionX != mLastParentX || parentPositionY != mLastParentY) {
                    mTouchToWindowOffsetX += parentPositionX - mLastParentX;
                    mTouchToWindowOffsetY += parentPositionY - mLastParentY;
                    mLastParentX = parentPositionX;
                    mLastParentY = parentPositionY;
                }

                onHandleMoved();
            }

            if (isVisible()) {
                final int positionX = parentPositionX + mPositionX;
                final int positionY = parentPositionY + mPositionY;
                /*
                mWindowPosX = positionX;
                mWindowPosY = positionY;
                */
                if (isShowing()) {
                    mContainer.update(positionX, positionY, -1, -1);
                } else {
                    mContainer.showAtLocation(mView, Gravity.NO_GRAVITY,
                                              positionX, positionY);
                }
            } else {
                if (isShowing()) {
                    dismiss();
                }
            }

            mPositionHasChanged = false;
        }
    }

    @Override
    protected void onDraw(Canvas c) {
        final int drawWidth = mDrawable.getIntrinsicWidth();
        final int left = getHorizontalOffset();

        mDrawable.setBounds(left, 0, left + drawWidth, mDrawable.getIntrinsicHeight());
        mDrawable.draw(c);
    }

    private int getHorizontalOffset() {
        final int width = getPreferredWidth();
        final int drawWidth = mDrawable.getIntrinsicWidth();
        final int left;
        switch (mHorizontalGravity) {
            case Gravity.LEFT:
                left = 0;
                break;
            default:
            case Gravity.CENTER:
                left = (width - drawWidth) / 2;
                break;
            case Gravity.RIGHT:
                left = width - drawWidth;
                break;
        }
        return left;
    }

    protected int getCursorOffset() {
        return 0;  
    }

    // Addition
    private float mDownX, mDownY;
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        ViewGroup.LayoutParams params = mView.getLayoutParams();
        
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                    startTouchUpFilter(getCurrentCursorOffset());
                    mTouchToWindowOffsetX = ev.getRawX() - mPositionX;
                    mTouchToWindowOffsetY = ev.getRawY() - mPositionY;
                    
                    final PositionListener positionListener = getPositionListener();
                    mLastParentX = positionListener.getPositionX();
                    mLastParentY = positionListener.getPositionY();
                    mIsDragging = true;
                    
                    // MOD: Addition
                    mDownX = ev.getRawX();
                    mDownY = ev.getRawY();
                    mDownWidth = params.width;
                    mDownHeight = params.height;
                    
                    break;
                }

            case MotionEvent.ACTION_MOVE: {
                    final float rawX = ev.getRawX();
                    final float rawY = ev.getRawY();

                    // Vertical hysteresis: vertical down movement tends to snap to ideal offset
                    final float previousVerticalOffset = mTouchToWindowOffsetY - mLastParentY;
                    final float currentVerticalOffset = rawY - mPositionY - mLastParentY;
                    float newVerticalOffset;
                    if (previousVerticalOffset < mIdealVerticalOffset) {
                        newVerticalOffset = Math.min(currentVerticalOffset, mIdealVerticalOffset);
                        newVerticalOffset = Math.max(newVerticalOffset, previousVerticalOffset);
                    } else {
                        newVerticalOffset = Math.max(currentVerticalOffset, mIdealVerticalOffset);
                        newVerticalOffset = Math.min(newVerticalOffset, previousVerticalOffset);
                    }
                    mTouchToWindowOffsetY = newVerticalOffset + mLastParentY;

                    final float newPosX = rawX - mTouchToWindowOffsetX + mHotspotX;
                    final float newPosY = rawY - mTouchToWindowOffsetY + mTouchOffsetY;

                    int newWidth = (int) (mDownWidth + (rawX - mDownX));
                    int newHeight = (int) (mDownHeight + (rawY - mDownY));
                    
                    // mDownX = rawX;
                    // mDownY = rawY;
                    
                    params.width = Math.max(50, newWidth);
                    params.height = Math.max(50, newHeight);
                    
                    mView.setLayoutParams(params);
               
                    updatePosition(newPosX, newPosY);
                    // break;
                    return true;
                }

            case MotionEvent.ACTION_UP:
                filterOnTouchUp();
                mIsDragging = false;
                break;

            case MotionEvent.ACTION_CANCEL:
                mIsDragging = false;
                break;
        }
        return true;
    }

    public boolean isDragging() {
        return mIsDragging;
    }

    void onHandleMoved() {
        hideActionPopupWindow();
    }

    public void onDetached() {
        hideActionPopupWindow();
    }
}

