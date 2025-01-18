package com.kdt;

import static net.kdt.pojavlaunch.Tools.currentDisplayMetrics;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;

/**
 * The base class for side dialog views
 * A side dialog is a dialog appearing from one side of the screen
 */
public abstract class SideDialogView {

    private final ViewGroup mParent;
    private final @LayoutRes int mLayoutId;
    private ViewGroup mDialogLayout;
    private DefocusableScrollView mScrollView;
    protected View mDialogContent;

    protected final int mMargin;
    private ObjectAnimator mSideDialogAnimator;
    protected boolean mDisplaying = false;
    /* Whether the layout is built */
    private boolean mIsInstantiated = false;

    /* UI elements */
    private Button mStartButton, mEndButton;
    private TextView mTitleTextview;
    private View mTitleDivider;

    /* Data to store when the UI element has yet to be inflated */
    private @StringRes int mStartButtonStringId, mEndButtonStringId, mTitleStringId;
    private View.OnClickListener mStartButtonListener, mEndButtonListener;


    public SideDialogView(Context context, ViewGroup parent, @LayoutRes int layoutId) {
        mMargin = context.getResources().getDimensionPixelOffset(R.dimen._20sdp);
        mParent = parent;
        mLayoutId = layoutId;
    }

    public void setTitle(@StringRes int textId) {
        mTitleStringId = textId;
        if (mIsInstantiated) {
            mTitleTextview.setText(textId);
            mTitleTextview.setVisibility(View.VISIBLE);
            mTitleDivider.setVisibility(View.VISIBLE);
        }
    }

    public final void setStartButtonListener(@StringRes int textId, @Nullable View.OnClickListener listener) {
        mStartButtonStringId = textId;
        mStartButtonListener = listener;
        if (mIsInstantiated) setButton(mStartButton, textId, listener);
    }

    public final void setEndButtonListener(@StringRes int textId, @Nullable View.OnClickListener listener) {
        mEndButtonStringId = textId;
        mEndButtonListener = listener;
        if (mIsInstantiated) setButton(mEndButton, textId, listener);
    }

    private void setButton(@NonNull Button button, @StringRes int textId, @Nullable View.OnClickListener listener) {
        button.setText(textId);
        button.setOnClickListener(listener);
        button.setVisibility(View.VISIBLE);
    }


    private void inflateLayout() {
        if(mIsInstantiated) {
            Log.w("SideDialogView", "Layout already inflated");
            return;
        }

        // Inflate layouts
        mDialogLayout = (ViewGroup) LayoutInflater.from(mParent.getContext()).inflate(R.layout.dialog_side_dialog, mParent, false);
        mScrollView = mDialogLayout.findViewById(R.id.side_dialog_scrollview);
        mStartButton = mDialogLayout.findViewById(R.id.side_dialog_start_button);
        mEndButton = mDialogLayout.findViewById(R.id.side_dialog_end_button);
        mTitleTextview = mDialogLayout.findViewById(R.id.side_dialog_title_textview);
        mTitleDivider = mDialogLayout.findViewById(R.id.side_dialog_title_divider);

        LayoutInflater.from(mParent.getContext()).inflate(mLayoutId, mScrollView, true);
        mDialogContent = mScrollView.getChildAt(0);

        // Attach layouts
        mParent.addView(mDialogLayout);

        mSideDialogAnimator = ObjectAnimator.ofFloat(mDialogLayout, "x", 0).setDuration(600);
        mSideDialogAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        mDialogLayout.setElevation(10);
        mDialogLayout.setTranslationZ(10);

        mDialogLayout.setVisibility(View.VISIBLE);
        mDialogLayout.setBackground(ResourcesCompat.getDrawable(mDialogLayout.getResources(), R.drawable.background_control_editor, null));

        //TODO offset better according to view width
        mDialogLayout.setX(-mDialogLayout.getResources().getDimensionPixelOffset(R.dimen._280sdp));
        mIsInstantiated = true;

        // Set up UI elements
        if (mTitleStringId != 0) setTitle(mTitleStringId);
        if (mStartButtonStringId != 0) setStartButtonListener(mStartButtonStringId, mStartButtonListener);
        if (mEndButtonStringId != 0) setEndButtonListener(mEndButtonStringId, mEndButtonListener);
    }

    /** Destroy the layout, cleanup variables */
    private void deflateLayout() {
        if(!mIsInstantiated) {
            Log.w("SideDialogView", "Layout not inflated");
            return;
        }

        mParent.removeView(mDialogLayout);
        mIsInstantiated = false;

        mScrollView = null;
        mSideDialogAnimator = null;
        mDialogLayout = null;
        mDialogContent = null;
        mTitleTextview = null;
        mTitleDivider = null;
        mStartButton = null;
        mEndButton = null;
    }


    /**
     * Slide the layout into the visible screen area
     */
    @CallSuper
    public final void appear(boolean fromRight) {
        if (!mIsInstantiated) {
            inflateLayout();
            onInflate();
        }

        // To avoid UI sizing issue when the dialog is not fully inflated
        onAppear();
        Tools.runOnUiThread(() -> {
            if (fromRight) {
                if (!mDisplaying || !isAtRight()) {
                    mSideDialogAnimator.setFloatValues(currentDisplayMetrics.widthPixels, currentDisplayMetrics.widthPixels - mScrollView.getWidth() - mMargin);
                    mSideDialogAnimator.start();
                    mDisplaying = true;
                }
            } else {
                if (!mDisplaying || isAtRight()) {
                    mSideDialogAnimator.setFloatValues(-mDialogLayout.getWidth(), mMargin);
                    mSideDialogAnimator.start();
                    mDisplaying = true;
                }
            }
        });
    }

    protected final boolean isAtRight() {
        return mDialogLayout.getX() > currentDisplayMetrics.widthPixels / 2f;
    }

    /**
     * Slide out the layout
     * @param destroy Whether the layout should be destroyed after disappearing.
     *                Recommended to be true if the layout is not going to be used anymore
     */
    @CallSuper
    public final void disappear(boolean destroy) {
        if(!mIsInstantiated) {
            Log.w("SideDialogView", "Layout not inflated");
            return;
        }

        if (!mDisplaying) {
            if(destroy) {
                onDisappear();
                onDestroy();
                deflateLayout();
            }
            return;
        }

        mDisplaying = false;
        if (isAtRight())
            mSideDialogAnimator.setFloatValues(currentDisplayMetrics.widthPixels - mDialogLayout.getWidth() - mMargin, currentDisplayMetrics.widthPixels);
        else
            mSideDialogAnimator.setFloatValues(mMargin, -mDialogLayout.getWidth());

        if(destroy) {
            onDisappear();
            onDestroy();
            mSideDialogAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSideDialogAnimator.removeListener(this);
                    deflateLayout();
                }
            });
        }

        mSideDialogAnimator.start();
    }

    /** @return Whether the dialog is currently displaying */
    public final boolean isDisplaying(){
        return mDisplaying;
    }

    /**
     * Called when the dialog is inflated, ideal for setting up UI elements bindings
     */
    protected void onInflate() {}

    /**
     * Called after the dialog has appeared
     */
    protected void onAppear() {}

    /**
     * Called after the dialog has disappeared
     */
    protected void onDisappear() {}

    /**
     * Called before the dialog gets destroyed (removing views from parent)
     * Ideal for cleaning up resources
     */
    protected void onDestroy() {}


}
