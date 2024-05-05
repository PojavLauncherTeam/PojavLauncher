package net.kdt.pojavlaunch.customcontrols.mouse;

public interface AbstractTouchpad {
    /**
     * Get the supposed display state of the mouse (whether it should be shown when the user is in a GUI)
     * Note that this does *not* reflect the actual visibility state of the mouse
     * @return current supposed enabled state
     */
    boolean getDisplayState();

    /**
     * Apply a motion vector to the mouse in form of a two-entry float array. This will move the mouse
     * on the screen and send the new cursor position to the game.
     * @param vector the array that contains the vector
     */
    default void applyMotionVector(float[] vector) {
        applyMotionVector(vector[0], vector[1]);
    }

    /**
     * Apply a motion vector to the mouse in form of the separate X/Y coordinates. This will move the mouse
     * on the screen and send the new cursor position to the game.
     * @param x the relative X coordinate of the vector
     * @param y the relative Y coordinate for the vector
     */
    void applyMotionVector(float x, float y);

    /**
     * Sets the state of the touchpad to "enabled"
     * @param supposed if set to true, this will set the supposed display state to enabled but may not
     *                 affect the touchpad until internal conditions are met
     *                 if set to false it will turn the touchpad on regardless of internal conditions
     */
    void enable(boolean supposed);
    /**
     * Sets the state of the touchpad to "disabled".
     */
    void disable();
}
