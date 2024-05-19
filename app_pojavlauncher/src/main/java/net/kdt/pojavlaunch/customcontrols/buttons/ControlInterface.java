package net.kdt.pojavlaunch.customcontrols.buttons;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_BUTTONSIZE;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.core.math.MathUtils;

import net.kdt.pojavlaunch.GrabListener;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.customcontrols.ControlData;
import net.kdt.pojavlaunch.customcontrols.ControlLayout;
import net.kdt.pojavlaunch.customcontrols.handleview.EditControlPopup;

import org.lwjgl.glfw.CallbackBridge;

/**
 * Interface injecting custom behavior to a View.
 * Most of the injected behavior is editing behavior,
 * sending keys has to be implemented by sub classes.
 */
public interface ControlInterface extends View.OnLongClickListener, GrabListener {

    View getControlView();

    ControlData getProperties();

    default void setProperties(ControlData properties) {
        setProperties(properties, true);
    }

    /**
     * Remove the button presence from the CustomControl object
     * You need to use {getControlParent()} for this.
     */
    void removeButton();

    /**
     * Duplicate the data of the button and add a view with the duplicated data
     * Relies on the ControlLayout for the implementation.
     */
    void cloneButton();

    default void setVisible(boolean isVisible) {
        if(getProperties().isHideable)
            getControlView().setVisibility(isVisible ? VISIBLE : GONE);
    }

    void sendKeyPresses(boolean isDown);

    /**
     * Load the values and hide non useful forms
     */
    void loadEditValues(EditControlPopup editControlPopup);

    @Override
    default void onGrabState(boolean isGrabbing) {
        if (getControlLayoutParent() != null && getControlLayoutParent().getModifiable()) return; // Disable when edited
        setVisible(((getProperties().displayInGame && isGrabbing) || (getProperties().displayInMenu && !isGrabbing)) && getControlLayoutParent().areControlVisible());
    }

    default ControlLayout getControlLayoutParent() {
        return (ControlLayout) getControlView().getParent();
    }

    /**
     * Apply conversion steps for when the view is created
     */
    default ControlData preProcessProperties(ControlData properties, ControlLayout layout) {
        //Size
        properties.setWidth(properties.getWidth() / layout.getLayoutScale() * PREF_BUTTONSIZE);
        properties.setHeight(properties.getHeight() / layout.getLayoutScale() * PREF_BUTTONSIZE);

        //Visibility
        properties.isHideable = !properties.containsKeycode(ControlData.SPECIALBTN_TOGGLECTRL) && !properties.containsKeycode(ControlData.SPECIALBTN_VIRTUALMOUSE);

        return properties;
    }

    default void updateProperties() {
        setProperties(getProperties());
    }

    /* This function should be overridden to store the properties */
    @CallSuper
    default void setProperties(ControlData properties, boolean changePos) {
        if (changePos) {
            getControlView().setX(properties.insertDynamicPos(getProperties().dynamicX));
            getControlView().setY(properties.insertDynamicPos(getProperties().dynamicY));
        }

        // Recycle layout params
        ViewGroup.LayoutParams params = getControlView().getLayoutParams();
        if (params == null)
            params = new FrameLayout.LayoutParams((int) properties.getWidth(), (int) properties.getHeight());
        params.width = (int) properties.getWidth();
        params.height = (int) properties.getHeight();
        getControlView().setLayoutParams(params);
    }

    /**
     * Apply the background according to properties
     */
    default void setBackground() {
        GradientDrawable gd = getControlView().getBackground() instanceof GradientDrawable
                ? (GradientDrawable) getControlView().getBackground()
                : new GradientDrawable();
        gd.setColor(getProperties().bgColor);
        gd.setStroke((int) Tools.dpToPx(getProperties().strokeWidth * (getControlLayoutParent().getLayoutScale()/100f)), getProperties().strokeColor);
        gd.setCornerRadius(computeCornerRadius(getProperties().cornerRadius));

        getControlView().setBackground(gd);
    }

    /**
     * Apply the dynamic equation on the x axis.
     *
     * @param dynamicX The equation to compute the position from
     */
    default void setDynamicX(String dynamicX) {
        getProperties().dynamicX = dynamicX;
        getControlView().setX(getProperties().insertDynamicPos(dynamicX));
    }

    /**
     * Apply the dynamic equation on the y axis.
     *
     * @param dynamicY The equation to compute the position from
     */
    default void setDynamicY(String dynamicY) {
        getProperties().dynamicY = dynamicY;
        getControlView().setY(getProperties().insertDynamicPos(dynamicY));
    }

    /**
     * Generate a dynamic equation from an absolute position, used to scale properly across devices
     *
     * @param x The absolute position on the horizontal axis
     * @return The equation as a String
     */
    default String generateDynamicX(float x) {
        if (x + (getProperties().getWidth() / 2f) > CallbackBridge.physicalWidth / 2f) {
            return (x + getProperties().getWidth()) / CallbackBridge.physicalWidth + " * ${screen_width} - ${width}";
        } else {
            return x / CallbackBridge.physicalWidth + " * ${screen_width}";
        }
    }

    /**
     * Generate a dynamic equation from an absolute position, used to scale properly across devices
     *
     * @param y The absolute position on the vertical axis
     * @return The equation as a String
     */
    default String generateDynamicY(float y) {
        if (y + (getProperties().getHeight() / 2f) > CallbackBridge.physicalHeight / 2f) {
            return (y + getProperties().getHeight()) / CallbackBridge.physicalHeight + " * ${screen_height} - ${height}";
        } else {
            return y / CallbackBridge.physicalHeight + " * ${screen_height}";
        }
    }

    /**
     * Regenerate and apply coordinates with supposedly modified properties
     */
    default void regenerateDynamicCoordinates() {
        getProperties().dynamicX = generateDynamicX(getControlView().getX());
        getProperties().dynamicY = generateDynamicY(getControlView().getY());
        updateProperties();
    }

    /**
     * Do a pre-conversion of an equation using values from a button,
     * so the variables can be used for another button
     * <p>
     * Internal use only.
     *
     * @param equation The dynamic position as a String
     * @param button   The button to get the values from.
     * @return The pre-processed equation as a String.
     */
    default String applySize(String equation, ControlInterface button) {
        return equation
                .replace("${right}", "(${screen_width} - ${width})")
                .replace("${bottom}", "(${screen_height} - ${height})")
                .replace("${height}", "(px(" + Tools.pxToDp(button.getProperties().getHeight()) + ") /" + PREF_BUTTONSIZE + " * ${preferred_scale})")
                .replace("${width}", "(px(" + Tools.pxToDp(button.getProperties().getWidth()) + ") / " + PREF_BUTTONSIZE + " * ${preferred_scale})");
    }


    /**
     * Convert a corner radius percentage into a px corner radius
     */
    default float computeCornerRadius(float radiusInPercent) {
        float minSize = Math.min(getProperties().getWidth(), getProperties().getHeight());
        return (minSize / 2) * (radiusInPercent / 100);
    }

    /**
     * Passe a series of checks to determine if the ControlButton isn't available to be snapped on.
     *
     * @param button The button to check
     * @return whether or not the button
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean canSnap(ControlInterface button) {
        float MIN_DISTANCE = Tools.dpToPx(8);

        if (button == this) return false;
        return !(net.kdt.pojavlaunch.utils.MathUtils.dist(
                button.getControlView().getX() + button.getControlView().getWidth() / 2f,
                button.getControlView().getY() + button.getControlView().getHeight() / 2f,
                getControlView().getX() + getControlView().getWidth() / 2f,
                getControlView().getY() + getControlView().getHeight() / 2f)
                > Math.max(button.getControlView().getWidth() / 2f + getControlView().getWidth() / 2f,
                button.getControlView().getHeight() / 2f + getControlView().getHeight() / 2f) + MIN_DISTANCE);
    }

    /**
     * Try to snap, then align to neighboring buttons, given the provided coordinates.
     * The new position is automatically applied to the View,
     * regardless of if the View snapped or not.
     * <p>
     * The new position is always dynamic, thus replacing previous dynamic positions
     *
     * @param x Coordinate on the x axis
     * @param y Coordinate on the y axis
     */
    default void snapAndAlign(float x, float y) {
        float MIN_DISTANCE = Tools.dpToPx(8);
        String dynamicX = generateDynamicX(x);
        String dynamicY = generateDynamicY(y);

        getControlView().setX(x);
        getControlView().setY(y);

        for (ControlInterface button : ((ControlLayout) getControlView().getParent()).getButtonChildren()) {
            //Step 1: Filter unwanted buttons
            if (!canSnap(button)) continue;

            //Step 2: Get Coordinates
            float button_top = button.getControlView().getY();
            float button_bottom = button_top + button.getControlView().getHeight();
            float button_left = button.getControlView().getX();
            float button_right = button_left + button.getControlView().getWidth();

            float top = getControlView().getY();
            float bottom = getControlView().getY() + getControlView().getHeight();
            float left = getControlView().getX();
            float right = getControlView().getX() + getControlView().getWidth();

            //Step 3: For each axis, we try to snap to the nearest
            if (Math.abs(top - button_bottom) < MIN_DISTANCE) { // Bottom snap
                dynamicY = applySize(button.getProperties().dynamicY, button) + applySize(" + ${height}", button) + " + ${margin}";
            } else if (Math.abs(button_top - bottom) < MIN_DISTANCE) { //Top snap
                dynamicY = applySize(button.getProperties().dynamicY, button) + " - ${height} - ${margin}";
            }
            if (!dynamicY.equals(generateDynamicY(getControlView().getY()))) { //If we snapped
                if (Math.abs(button_left - left) < MIN_DISTANCE) { //Left align snap
                    dynamicX = applySize(button.getProperties().dynamicX, button);
                } else if (Math.abs(button_right - right) < MIN_DISTANCE) { //Right align snap
                    dynamicX = applySize(button.getProperties().dynamicX, button) + applySize(" + ${width}", button) + " - ${width}";
                }
            }

            if (Math.abs(button_left - right) < MIN_DISTANCE) { //Left snap
                dynamicX = applySize(button.getProperties().dynamicX, button) + " - ${width} - ${margin}";
            } else if (Math.abs(left - button_right) < MIN_DISTANCE) { //Right snap
                dynamicX = applySize(button.getProperties().dynamicX, button) + applySize(" + ${width}", button) + " + ${margin}";
            }
            if (!dynamicX.equals(generateDynamicX(getControlView().getX()))) { //If we snapped
                if (Math.abs(button_top - top) < MIN_DISTANCE) { //Top align snap
                    dynamicY = applySize(button.getProperties().dynamicY, button);
                } else if (Math.abs(button_bottom - bottom) < MIN_DISTANCE) { //Bottom align snap
                    dynamicY = applySize(button.getProperties().dynamicY, button) + applySize(" + ${height}", button) + " - ${height}";
                }
            }

        }

        setDynamicX(dynamicX);
        setDynamicY(dynamicY);
    }

    /**
     * Wrapper for multiple injections at once
     */
    default void injectBehaviors() {
        injectProperties();
        injectTouchEventBehavior();
        injectLayoutParamBehavior();
        injectGrabListenerBehavior();
    }

    /**
     * Inject the grab listener, remove it when the view is gone
     */
    default void injectGrabListenerBehavior() {
        if (getControlView() == null) {
            Log.e(ControlInterface.class.toString(), "Failed to inject grab listener behavior !");
            return;
        }


        getControlView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                CallbackBridge.addGrabListener(ControlInterface.this);
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
                getControlView().removeOnAttachStateChangeListener(this);
                CallbackBridge.removeGrabListener(ControlInterface.this);
            }
        });


    }

    default void injectProperties() {
        getControlView().post(() -> getControlView().setTranslationZ(10));
    }

    /**
     * Inject a touch listener on the view to make editing controls straight forward
     */
    default void injectTouchEventBehavior() {
        getControlView().setOnTouchListener(new View.OnTouchListener() {
            private boolean mCanTriggerLongClick = true;
            private float downX, downY;
            private float downRawX, downRawY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (!getControlLayoutParent().getModifiable()) {
                    // Basically, editing behavior is forced while in game behavior is specific
                    view.onTouchEvent(event);
                    return true;
                }

                /* If the button can be modified/moved */
                //Instantiate the gesture detector only when needed

                if (event.getActionMasked() == MotionEvent.ACTION_UP && mCanTriggerLongClick) {
                    //TODO change this.
                    onLongClick(view);
                }

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        mCanTriggerLongClick = true;
                        downRawX = event.getRawX();
                        downRawY = event.getRawY();
                        downX = downRawX - view.getX();
                        downY = downRawY - view.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(event.getRawX() - downRawX) > 8 || Math.abs(event.getRawY() - downRawY) > 8)
                            mCanTriggerLongClick = false;
                        getControlLayoutParent().adaptPanelPosition();
                        snapAndAlign(
                                MathUtils.clamp(event.getRawX() - downX, 0, CallbackBridge.physicalWidth - view.getWidth()),
                                MathUtils.clamp(event.getRawY() - downY, 0, CallbackBridge.physicalHeight - view.getHeight())
                        );
                        break;
                }

                return true;
            }
        });
    }

    default void injectLayoutParamBehavior() {
        getControlView().addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            getProperties().setWidth(right - left);
            getProperties().setHeight(bottom - top);
            setBackground();

            // Re-calculate position
            getControlView().setX(getControlView().getX());
            getControlView().setY(getControlView().getY());
        });
    }

    @Override
    default boolean onLongClick(View v) {
        if (getControlLayoutParent().getModifiable()) {
            getControlLayoutParent().editControlButton(this);
            getControlLayoutParent().mActionRow.setFollowedButton(this);
        }

        return true;
    }
}
