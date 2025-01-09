package net.kdt.pojavlaunch.customcontrols.mouse;

import android.os.Build;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.RequiresApi;

import net.kdt.pojavlaunch.MinecraftGLSurface;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import org.lwjgl.glfw.CallbackBridge;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AndroidPointerCapture implements ViewTreeObserver.OnWindowFocusChangeListener, View.OnCapturedPointerListener {
    private static final float TOUCHPAD_SCROLL_THRESHOLD = 1;
    private final AbstractTouchpad mTouchpad;
    private final View mHostView;
    private final float mMousePrescale = Tools.dpToPx(1);
    private final PointerTracker mPointerTracker = new PointerTracker();
    private final Scroller mScroller = new Scroller(TOUCHPAD_SCROLL_THRESHOLD);
    private final float[] mVector = mPointerTracker.getMotionVector();

    private int mInputDeviceIdentifier;
    private boolean mDeviceSupportsRelativeAxis;

    public AndroidPointerCapture(AbstractTouchpad touchpad, View hostView) {
        this.mTouchpad = touchpad;
        this.mHostView = hostView;
        hostView.setOnCapturedPointerListener(this);
        hostView.getViewTreeObserver().addOnWindowFocusChangeListener(this);
    }

    private void enableTouchpadIfNecessary() {
        if(!mTouchpad.getDisplayState()) mTouchpad.enable(true);
    }

    public void handleAutomaticCapture() {
        if(!mHostView.hasWindowFocus()) {
            mHostView.requestFocus();
        } else {
            mHostView.requestPointerCapture();
        }
    }

    @Override
    public boolean onCapturedPointer(View view, MotionEvent event) {
        checkSameDevice(event.getDevice());
        // Yes, we actually not only receive relative mouse events here, but also absolute touchpad ones!
        // Therefore, we need to know when it's a touchpad and when it's a mouse.

        if((event.getSource() & InputDevice.SOURCE_CLASS_TRACKBALL) != 0) {
            // If the source claims to be a relative device by belonging to the trackball class,
            // use its coordinates directly.
            if(mDeviceSupportsRelativeAxis) {
                // If some OEM decides to do a funny and make an absolute touchpad report itself as
                // a trackball, we will at least have semi-valid relative positions
                mVector[0] = event.getAxisValue(MotionEvent.AXIS_RELATIVE_X);
                mVector[1] = event.getAxisValue(MotionEvent.AXIS_RELATIVE_Y);
            }else {
                // Otherwise trust the OS, i guess??
                mVector[0] = event.getX();
                mVector[1] = event.getY();
            }
        }else {
            // If it's not a trackball, it's likely a touchpad and needs tracking like a touchscreen.
            mPointerTracker.trackEvent(event);
            // The relative position will already be written down into the mVector variable.
        }

        if(!CallbackBridge.isGrabbing()) {
            enableTouchpadIfNecessary();
            // Yes, if the user's touchpad is multi-touch we will also receive events for that.
            // So, handle the scrolling gesture ourselves.
            mVector[0] *= mMousePrescale;
            mVector[1] *= mMousePrescale;
            if(event.getPointerCount() < 2) {
                mTouchpad.applyMotionVector(mVector);
                mScroller.resetScrollOvershoot();
            } else {
                mScroller.performScroll(mVector);
            }
        } else {
            // Position is updated by many events, hence it is send regardless of the event value
            CallbackBridge.mouseX += (mVector[0] * LauncherPreferences.PREF_SCALE_FACTOR);
            CallbackBridge.mouseY += (mVector[1] * LauncherPreferences.PREF_SCALE_FACTOR);
            CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_BUTTON_PRESS:
                return MinecraftGLSurface.sendMouseButtonUnconverted(event.getActionButton(), true);
            case MotionEvent.ACTION_BUTTON_RELEASE:
                return MinecraftGLSurface.sendMouseButtonUnconverted(event.getActionButton(), false);
            case MotionEvent.ACTION_SCROLL:
                CallbackBridge.sendScroll(
                        event.getAxisValue(MotionEvent.AXIS_HSCROLL),
                        event.getAxisValue(MotionEvent.AXIS_VSCROLL)
                );
                return true;
            case MotionEvent.ACTION_UP:
                mPointerTracker.cancelTracking();
                return true;
            default:
                return false;
        }
    }

    private void checkSameDevice(InputDevice inputDevice) {
        int newIdentifier = inputDevice.getId();
        if(mInputDeviceIdentifier != newIdentifier) {
            reinitializeDeviceSpecificProperties(inputDevice);
            mInputDeviceIdentifier = newIdentifier;
        }
    }

    private void reinitializeDeviceSpecificProperties(InputDevice inputDevice) {
        mPointerTracker.cancelTracking();
        boolean relativeXSupported = inputDevice.getMotionRange(MotionEvent.AXIS_RELATIVE_X) != null;
        boolean relativeYSupported = inputDevice.getMotionRange(MotionEvent.AXIS_RELATIVE_Y) != null;
        mDeviceSupportsRelativeAxis = relativeXSupported && relativeYSupported;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus && Tools.isAndroid8OrHigher()) mHostView.requestPointerCapture();
    }

    public void detach() {
        mHostView.setOnCapturedPointerListener(null);
        mHostView.getViewTreeObserver().removeOnWindowFocusChangeListener(this);
    }
}
