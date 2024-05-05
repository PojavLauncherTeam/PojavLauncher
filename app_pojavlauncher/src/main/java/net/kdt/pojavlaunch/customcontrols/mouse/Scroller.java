package net.kdt.pojavlaunch.customcontrols.mouse;

import org.lwjgl.glfw.CallbackBridge;

public class Scroller {

    private float mScrollOvershootH, mScrollOvershootV;
    private final float mScrollThreshold;

    public Scroller(float mScrollThreshold) {
        this.mScrollThreshold = mScrollThreshold;
    }

    /**
     * Perform a scrolling gesture.
     * @param dx the X coordinate of the primary pointer's vector
     * @param dy the Y coordinate of the primary pointer's vector
     */
    public void performScroll(float dx, float dy) {
        float hScroll = (dx / mScrollThreshold) + mScrollOvershootH;
        float vScroll = (dy / mScrollThreshold) + mScrollOvershootV;
        int hScrollRound = (int) hScroll, vScrollRound = (int) vScroll;
        if(hScrollRound != 0 || vScrollRound != 0) CallbackBridge.sendScroll(hScroll, vScroll);
        mScrollOvershootH = hScroll - hScrollRound;
        mScrollOvershootV = vScroll - vScrollRound;
    }

    /**
     * Perform a scrolling gesture.
     * @param vector a 2-component vector that stores the relative position of the primary pointer.
     */
    public void performScroll(float[] vector) {
        performScroll(vector[0], vector[1]);
    }

    /**
     * Reset scroll overshoot values. Scroll overshoot makes the scrolling feel less
     * choppy, but will cause anomailes if not reset on the end of a scrolling gesture.
     */
    public void resetScrollOvershoot() {
        mScrollOvershootH = mScrollOvershootV = 0f;
    }
}
