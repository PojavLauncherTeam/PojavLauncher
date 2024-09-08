/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import android.util.*;

import java.lang.reflect.*;
import java.nio.*;

import javax.annotation.*;

import org.lwjgl.*;
import org.lwjgl.system.*;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.APIUtil.*;
import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.JNI.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import java.util.*;

public class GLFW
{
    static FloatBuffer joystickData = (FloatBuffer)FloatBuffer.allocate(8).flip();
    static ByteBuffer buttonData = (ByteBuffer)ByteBuffer.allocate(8).flip();
    /** The major version number of the GLFW library. This is incremented when the API is changed in non-compatible ways. */
    public static final int GLFW_VERSION_MAJOR = 3;

    /** The minor version number of the GLFW library. This is incremented when features are added to the API but it remains backward-compatible. */
    public static final int GLFW_VERSION_MINOR = 4;

    /** The revision number of the GLFW library. This is incremented when a bug fix release is made that does not contain any API changes. */
    public static final int GLFW_VERSION_REVISION = 0;

    /** Boolean values. */
    public static final int
    GLFW_TRUE  = 1,
    GLFW_FALSE = 0;

    /** The key or button was released. */
    public static final int GLFW_RELEASE = 0;

    /** The key or button was pressed. */
    public static final int GLFW_PRESS = 1;

    /** The key was held down until it repeated. */
    public static final int GLFW_REPEAT = 2;

    /** Joystick hat states. */
    public static final int
    GLFW_HAT_CENTERED   = 0,
    GLFW_HAT_UP         = 1,
    GLFW_HAT_RIGHT      = 2,
    GLFW_HAT_DOWN       = 4,
    GLFW_HAT_LEFT       = 8,
    GLFW_HAT_RIGHT_UP   = (GLFW_HAT_RIGHT | GLFW_HAT_UP),
    GLFW_HAT_RIGHT_DOWN = (GLFW_HAT_RIGHT | GLFW_HAT_DOWN),
    GLFW_HAT_LEFT_UP    = (GLFW_HAT_LEFT  | GLFW_HAT_UP),
    GLFW_HAT_LEFT_DOWN  = (GLFW_HAT_LEFT  | GLFW_HAT_DOWN);

    /** The unknown key. */
    public static final int GLFW_KEY_UNKNOWN = -1;

    /** Printable keys. */
    public static final int
    GLFW_KEY_SPACE         = 32,
    GLFW_KEY_APOSTROPHE    = 39,
    GLFW_KEY_COMMA         = 44,
    GLFW_KEY_MINUS         = 45,
    GLFW_KEY_PERIOD        = 46,
    GLFW_KEY_SLASH         = 47,
    GLFW_KEY_0             = 48,
    GLFW_KEY_1             = 49,
    GLFW_KEY_2             = 50,
    GLFW_KEY_3             = 51,
    GLFW_KEY_4             = 52,
    GLFW_KEY_5             = 53,
    GLFW_KEY_6             = 54,
    GLFW_KEY_7             = 55,
    GLFW_KEY_8             = 56,
    GLFW_KEY_9             = 57,
    GLFW_KEY_SEMICOLON     = 59,
    GLFW_KEY_EQUAL         = 61,
    GLFW_KEY_A             = 65,
    GLFW_KEY_B             = 66,
    GLFW_KEY_C             = 67,
    GLFW_KEY_D             = 68,
    GLFW_KEY_E             = 69,
    GLFW_KEY_F             = 70,
    GLFW_KEY_G             = 71,
    GLFW_KEY_H             = 72,
    GLFW_KEY_I             = 73,
    GLFW_KEY_J             = 74,
    GLFW_KEY_K             = 75,
    GLFW_KEY_L             = 76,
    GLFW_KEY_M             = 77,
    GLFW_KEY_N             = 78,
    GLFW_KEY_O             = 79,
    GLFW_KEY_P             = 80,
    GLFW_KEY_Q             = 81,
    GLFW_KEY_R             = 82,
    GLFW_KEY_S             = 83,
    GLFW_KEY_T             = 84,
    GLFW_KEY_U             = 85,
    GLFW_KEY_V             = 86,
    GLFW_KEY_W             = 87,
    GLFW_KEY_X             = 88,
    GLFW_KEY_Y             = 89,
    GLFW_KEY_Z             = 90,
    GLFW_KEY_LEFT_BRACKET  = 91,
    GLFW_KEY_BACKSLASH     = 92,
    GLFW_KEY_RIGHT_BRACKET = 93,
    GLFW_KEY_GRAVE_ACCENT  = 96,
    GLFW_KEY_WORLD_1       = 161,
    GLFW_KEY_WORLD_2       = 162;

    /** Function keys. */
    public static final int
    GLFW_KEY_ESCAPE        = 256,
    GLFW_KEY_ENTER         = 257,
    GLFW_KEY_TAB           = 258,
    GLFW_KEY_BACKSPACE     = 259,
    GLFW_KEY_INSERT        = 260,
    GLFW_KEY_DELETE        = 261,
    GLFW_KEY_RIGHT         = 262,
    GLFW_KEY_LEFT          = 263,
    GLFW_KEY_DOWN          = 264,
    GLFW_KEY_UP            = 265,
    GLFW_KEY_PAGE_UP       = 266,
    GLFW_KEY_PAGE_DOWN     = 267,
    GLFW_KEY_HOME          = 268,
    GLFW_KEY_END           = 269,
    GLFW_KEY_CAPS_LOCK     = 280,
    GLFW_KEY_SCROLL_LOCK   = 281,
    GLFW_KEY_NUM_LOCK      = 282,
    GLFW_KEY_PRINT_SCREEN  = 283,
    GLFW_KEY_PAUSE         = 284,
    GLFW_KEY_F1            = 290,
    GLFW_KEY_F2            = 291,
    GLFW_KEY_F3            = 292,
    GLFW_KEY_F4            = 293,
    GLFW_KEY_F5            = 294,
    GLFW_KEY_F6            = 295,
    GLFW_KEY_F7            = 296,
    GLFW_KEY_F8            = 297,
    GLFW_KEY_F9            = 298,
    GLFW_KEY_F10           = 299,
    GLFW_KEY_F11           = 300,
    GLFW_KEY_F12           = 301,
    GLFW_KEY_F13           = 302,
    GLFW_KEY_F14           = 303,
    GLFW_KEY_F15           = 304,
    GLFW_KEY_F16           = 305,
    GLFW_KEY_F17           = 306,
    GLFW_KEY_F18           = 307,
    GLFW_KEY_F19           = 308,
    GLFW_KEY_F20           = 309,
    GLFW_KEY_F21           = 310,
    GLFW_KEY_F22           = 311,
    GLFW_KEY_F23           = 312,
    GLFW_KEY_F24           = 313,
    GLFW_KEY_F25           = 314,
    GLFW_KEY_KP_0          = 320,
    GLFW_KEY_KP_1          = 321,
    GLFW_KEY_KP_2          = 322,
    GLFW_KEY_KP_3          = 323,
    GLFW_KEY_KP_4          = 324,
    GLFW_KEY_KP_5          = 325,
    GLFW_KEY_KP_6          = 326,
    GLFW_KEY_KP_7          = 327,
    GLFW_KEY_KP_8          = 328,
    GLFW_KEY_KP_9          = 329,
    GLFW_KEY_KP_DECIMAL    = 330,
    GLFW_KEY_KP_DIVIDE     = 331,
    GLFW_KEY_KP_MULTIPLY   = 332,
    GLFW_KEY_KP_SUBTRACT   = 333,
    GLFW_KEY_KP_ADD        = 334,
    GLFW_KEY_KP_ENTER      = 335,
    GLFW_KEY_KP_EQUAL      = 336,
    GLFW_KEY_LEFT_SHIFT    = 340,
    GLFW_KEY_LEFT_CONTROL  = 341,
    GLFW_KEY_LEFT_ALT      = 342,
    GLFW_KEY_LEFT_SUPER    = 343,
    GLFW_KEY_RIGHT_SHIFT   = 344,
    GLFW_KEY_RIGHT_CONTROL = 345,
    GLFW_KEY_RIGHT_ALT     = 346,
    GLFW_KEY_RIGHT_SUPER   = 347,
    GLFW_KEY_MENU          = 348,
    GLFW_KEY_LAST          = GLFW_KEY_MENU;

    /** If this bit is set one or more Shift keys were held down. */
    public static final int GLFW_MOD_SHIFT = 0x1;

    /** If this bit is set one or more Control keys were held down. */
    public static final int GLFW_MOD_CONTROL = 0x2;

    /** If this bit is set one or more Alt keys were held down. */
    public static final int GLFW_MOD_ALT = 0x4;

    /** If this bit is set one or more Super keys were held down. */
    public static final int GLFW_MOD_SUPER = 0x8;

    /** If this bit is set the Caps Lock key is enabled and the {@link #GLFW_LOCK_KEY_MODS LOCK_KEY_MODS} input mode is set. */
    public static final int GLFW_MOD_CAPS_LOCK = 0x10;

    /** If this bit is set the Num Lock key is enabled and the {@link #GLFW_LOCK_KEY_MODS LOCK_KEY_MODS} input mode is set. */
    public static final int GLFW_MOD_NUM_LOCK = 0x20;


    /** Mouse buttons. See <a target="_blank" href="http://www.glfw.org/docs/latest/input.html#input_mouse_button">mouse button input</a> for how these are used. */
    public static final int
    GLFW_MOUSE_BUTTON_1      = 0,
    GLFW_MOUSE_BUTTON_2      = 1,
    GLFW_MOUSE_BUTTON_3      = 2,
    GLFW_MOUSE_BUTTON_4      = 3,
    GLFW_MOUSE_BUTTON_5      = 4,
    GLFW_MOUSE_BUTTON_6      = 5,
    GLFW_MOUSE_BUTTON_7      = 6,
    GLFW_MOUSE_BUTTON_8      = 7,
    GLFW_MOUSE_BUTTON_LAST   = GLFW_MOUSE_BUTTON_8,
    GLFW_MOUSE_BUTTON_LEFT   = GLFW_MOUSE_BUTTON_1,
    GLFW_MOUSE_BUTTON_RIGHT  = GLFW_MOUSE_BUTTON_2,
    GLFW_MOUSE_BUTTON_MIDDLE = GLFW_MOUSE_BUTTON_3;

    /** Joysticks. See <a target="_blank" href="http://www.glfw.org/docs/latest/input.html#joystick">joystick input</a> for how these are used. */
    public static final int
    GLFW_JOYSTICK_1    = 0,
    GLFW_JOYSTICK_2    = 1,
    GLFW_JOYSTICK_3    = 2,
    GLFW_JOYSTICK_4    = 3,
    GLFW_JOYSTICK_5    = 4,
    GLFW_JOYSTICK_6    = 5,
    GLFW_JOYSTICK_7    = 6,
    GLFW_JOYSTICK_8    = 7,
    GLFW_JOYSTICK_9    = 8,
    GLFW_JOYSTICK_10   = 9,
    GLFW_JOYSTICK_11   = 10,
    GLFW_JOYSTICK_12   = 11,
    GLFW_JOYSTICK_13   = 12,
    GLFW_JOYSTICK_14   = 13,
    GLFW_JOYSTICK_15   = 14,
    GLFW_JOYSTICK_16   = 15,
    GLFW_JOYSTICK_LAST = GLFW_JOYSTICK_16;

    /** Gamepad buttons. See <a target="_blank" href="http://www.glfw.org/docs/latest/input.html#gamepad">gamepad</a> for how these are used. */
    public static final int
    GLFW_GAMEPAD_BUTTON_A            = 0,
    GLFW_GAMEPAD_BUTTON_B            = 1,
    GLFW_GAMEPAD_BUTTON_X            = 2,
    GLFW_GAMEPAD_BUTTON_Y            = 3,
    GLFW_GAMEPAD_BUTTON_LEFT_BUMPER  = 4,
    GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER = 5,
    GLFW_GAMEPAD_BUTTON_BACK         = 6,
    GLFW_GAMEPAD_BUTTON_START        = 7,
    GLFW_GAMEPAD_BUTTON_GUIDE        = 8,
    GLFW_GAMEPAD_BUTTON_LEFT_THUMB   = 9,
    GLFW_GAMEPAD_BUTTON_RIGHT_THUMB  = 10,
    GLFW_GAMEPAD_BUTTON_DPAD_UP      = 11,
    GLFW_GAMEPAD_BUTTON_DPAD_RIGHT   = 12,
    GLFW_GAMEPAD_BUTTON_DPAD_DOWN    = 13,
    GLFW_GAMEPAD_BUTTON_DPAD_LEFT    = 14,
    GLFW_GAMEPAD_BUTTON_LAST         = GLFW_GAMEPAD_BUTTON_DPAD_LEFT,
    GLFW_GAMEPAD_BUTTON_CROSS        = GLFW_GAMEPAD_BUTTON_A,
    GLFW_GAMEPAD_BUTTON_CIRCLE       = GLFW_GAMEPAD_BUTTON_B,
    GLFW_GAMEPAD_BUTTON_SQUARE       = GLFW_GAMEPAD_BUTTON_X,
    GLFW_GAMEPAD_BUTTON_TRIANGLE     = GLFW_GAMEPAD_BUTTON_Y;

    /** Gamepad axes. See <a target="_blank" href="http://www.glfw.org/docs/latest/input.html#gamepad">gamepad</a> for how these are used. */
    public static final int
    GLFW_GAMEPAD_AXIS_LEFT_X        = 0,
    GLFW_GAMEPAD_AXIS_LEFT_Y        = 1,
    GLFW_GAMEPAD_AXIS_RIGHT_X       = 2,
    GLFW_GAMEPAD_AXIS_RIGHT_Y       = 3,
    GLFW_GAMEPAD_AXIS_LEFT_TRIGGER  = 4,
    GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER = 5,
    GLFW_GAMEPAD_AXIS_LAST          = GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER;

    public static final int
    GLFW_NO_ERROR              = 0,
    GLFW_NOT_INITIALIZED       = 0x10001,
    GLFW_NO_CURRENT_CONTEXT    = 0x10002,
    GLFW_INVALID_ENUM          = 0x10003,
    GLFW_INVALID_VALUE         = 0x10004,
    GLFW_OUT_OF_MEMORY         = 0x10005,
    GLFW_API_UNAVAILABLE       = 0x10006,
    GLFW_VERSION_UNAVAILABLE   = 0x10007,
    GLFW_PLATFORM_ERROR        = 0x10008,
    GLFW_FORMAT_UNAVAILABLE    = 0x10009,
    GLFW_NO_WINDOW_CONTEXT     = 0x1000A,
    GLFW_CURSOR_UNAVAILABLE    = 0x1000B,
    GLFW_FEATURE_UNAVAILABLE   = 0x1000C,
    GLFW_FEATURE_UNIMPLEMENTED = 0x1000D;

    public static final int
    GLFW_FOCUSED                 = 0x20001,
    GLFW_ICONIFIED               = 0x20002,
    GLFW_RESIZABLE               = 0x20003,
    GLFW_VISIBLE                 = 0x20004,
    GLFW_DECORATED               = 0x20005,
    GLFW_AUTO_ICONIFY            = 0x20006,
    GLFW_FLOATING                = 0x20007,
    GLFW_MAXIMIZED               = 0x20008,
    GLFW_CENTER_CURSOR           = 0x20009,
    GLFW_TRANSPARENT_FRAMEBUFFER = 0x2000A,
    GLFW_HOVERED                 = 0x2000B,
    GLFW_FOCUS_ON_SHOW           = 0x2000C;

    /** Input options. */
    public static final int
    GLFW_CURSOR               = 0x33001,
    GLFW_STICKY_KEYS          = 0x33002,
    GLFW_STICKY_MOUSE_BUTTONS = 0x33003,
    GLFW_LOCK_KEY_MODS        = 0x33004,
    GLFW_RAW_MOUSE_MOTION     = 0x33005;

    /** Cursor state. */
    public static final int
    GLFW_CURSOR_NORMAL   = 0x34001,
    GLFW_CURSOR_HIDDEN   = 0x34002,
    GLFW_CURSOR_DISABLED = 0x34003;

    /** The regular arrow cursor shape. */
    public static final int GLFW_ARROW_CURSOR = 0x36001;

    /** The text input I-beam cursor shape. */
    public static final int GLFW_IBEAM_CURSOR = 0x36002;

    /** The crosshair cursor shape. */
    public static final int GLFW_CROSSHAIR_CURSOR = 0x36003;

    /** The pointing hand cursor shape. */
    public static final int GLFW_POINTING_HAND_CURSOR = 0x36004;

    public static final int GLFW_RESIZE_EW_CURSOR = 0x36005;
    public static final int GLFW_RESIZE_NS_CURSOR = 0x36006;
    public static final int GLFW_RESIZE_NWSE_CURSOR = 0x36007;
    public static final int GLFW_RESIZE_NESW_CURSOR = 0x36008;

    /**
     * The omni-directional resize cursor/move shape.
     *
     * <p>This is usually either a combined horizontal and vertical double-headed arrow or a grabbing hand.</p>
     */
    public static final int GLFW_RESIZE_ALL_CURSOR = 0x36009;

    public static final int GLFW_NOT_ALLOWED_CURSOR = 0x3600A;

    /** Legacy name for compatibility. */
    public static final int GLFW_HRESIZE_CURSOR = GLFW_RESIZE_EW_CURSOR;

    /** Legacy name for compatibility. */
    public static final int GLFW_VRESIZE_CURSOR = GLFW_RESIZE_NS_CURSOR;

    /** Legacy name for compatibility. */
    public static final int GLFW_HAND_CURSOR = GLFW_POINTING_HAND_CURSOR;

    /** Monitor events. */
    public static final int
    GLFW_CONNECTED    = 0x40001,
    GLFW_DISCONNECTED = 0x40002;

    /** Init hints. */
    public static final int
    GLFW_JOYSTICK_HAT_BUTTONS  = 0x50001,
    GLFW_COCOA_CHDIR_RESOURCES = 0x51001,
    GLFW_COCOA_MENUBAR         = 0x51002;

    /** Hint value for {@link #GLFW_PLATFORM PLATFORM} that enables automatic platform selection. */
    public static final int
        GLFW_ANY_PLATFORM     = 0x60000,
        GLFW_PLATFORM_WIN32   = 0x60001,
        GLFW_PLATFORM_COCOA   = 0x60002,
        GLFW_PLATFORM_WAYLAND = 0x60003,
        GLFW_PLATFORM_X11     = 0x60004,
        GLFW_PLATFORM_NULL    = 0x60005;

    /** Don't care value. */
    public static final int GLFW_DONT_CARE = -1;

    /** PixelFormat hints. */
    public static final int
    GLFW_RED_BITS         = 0x21001,
    GLFW_GREEN_BITS       = 0x21002,
    GLFW_BLUE_BITS        = 0x21003,
    GLFW_ALPHA_BITS       = 0x21004,
    GLFW_DEPTH_BITS       = 0x21005,
    GLFW_STENCIL_BITS     = 0x21006,
    GLFW_ACCUM_RED_BITS   = 0x21007,
    GLFW_ACCUM_GREEN_BITS = 0x21008,
    GLFW_ACCUM_BLUE_BITS  = 0x21009,
    GLFW_ACCUM_ALPHA_BITS = 0x2100A,
    GLFW_AUX_BUFFERS      = 0x2100B,
    GLFW_STEREO           = 0x2100C,
    GLFW_SAMPLES          = 0x2100D,
    GLFW_SRGB_CAPABLE     = 0x2100E,
    GLFW_REFRESH_RATE     = 0x2100F,
    GLFW_DOUBLEBUFFER     = 0x21010;

    public static final int
    GLFW_CLIENT_API               = 0x22001,
    GLFW_CONTEXT_VERSION_MAJOR    = 0x22002,
    GLFW_CONTEXT_VERSION_MINOR    = 0x22003,
    GLFW_CONTEXT_REVISION         = 0x22004,
    GLFW_CONTEXT_ROBUSTNESS       = 0x22005,
    GLFW_OPENGL_FORWARD_COMPAT    = 0x22006,
    GLFW_OPENGL_DEBUG_CONTEXT     = 0x22007,
    GLFW_OPENGL_PROFILE           = 0x22008,
    GLFW_CONTEXT_RELEASE_BEHAVIOR = 0x22009,
    GLFW_CONTEXT_NO_ERROR         = 0x2200A,
    GLFW_CONTEXT_CREATION_API     = 0x2200B,
    GLFW_SCALE_TO_MONITOR         = 0x2200C;

    /** Specifies whether to use full resolution framebuffers on Retina displays. This is ignored on other platforms. */
    public static final int GLFW_COCOA_RETINA_FRAMEBUFFER = 0x23001;

    /**
     * Specifies the UTF-8 encoded name to use for autosaving the window frame, or if empty disables frame autosaving for the window. This is ignored on other
     * platforms. This is set with {@link #glfwWindowHintString WindowHintString}.
     */
    public static final int GLFW_COCOA_FRAME_NAME = 0x23002;

    /**
     * Specifies whether to enable Automatic Graphics Switching, i.e. to allow the system to choose the integrated GPU for the OpenGL context and move it
     * between GPUs if necessary or whether to force it to always run on the discrete GPU. This only affects systems with both integrated and discrete GPUs.
     * This is ignored on other platforms.
     */
    public static final int GLFW_COCOA_GRAPHICS_SWITCHING = 0x23003;

    /** The desired ASCII encoded class and instance parts of the ICCCM {@code WM_CLASS} window property. These are set with {@link #glfwWindowHintString WindowHintString}. */
    public static final int
    GLFW_X11_CLASS_NAME    = 0x24001,
    GLFW_X11_INSTANCE_NAME = 0x24002;

    /**
     * Specifies whether to allow access to the window menu via the Alt+Space and Alt-and-then-Space keyboard shortcuts.
     *
     * <p>This is ignored on other platforms.</p>
     */
    public static final int GLFW_WIN32_KEYBOARD_MENU = 0x25001;

    /** Values for the {@link #GLFW_CLIENT_API CLIENT_API} hint. */
    public static final int
    GLFW_NO_API        = 0,
    GLFW_OPENGL_API    = 0x30001,
    GLFW_OPENGL_ES_API = 0x30002;

    /** Values for the {@link #GLFW_CONTEXT_ROBUSTNESS CONTEXT_ROBUSTNESS} hint. */
    public static final int
    GLFW_NO_ROBUSTNESS         = 0,
    GLFW_NO_RESET_NOTIFICATION = 0x31001,
    GLFW_LOSE_CONTEXT_ON_RESET = 0x31002;

    /** Values for the {@link #GLFW_OPENGL_PROFILE OPENGL_PROFILE} hint. */
    public static final int
    GLFW_OPENGL_ANY_PROFILE    = 0,
    GLFW_OPENGL_CORE_PROFILE   = 0x32001,
    GLFW_OPENGL_COMPAT_PROFILE = 0x32002;

    /** Values for the {@link #GLFW_CONTEXT_RELEASE_BEHAVIOR CONTEXT_RELEASE_BEHAVIOR} hint. */
    public static final int
    GLFW_ANY_RELEASE_BEHAVIOR   = 0,
    GLFW_RELEASE_BEHAVIOR_FLUSH = 0x35001,
    GLFW_RELEASE_BEHAVIOR_NONE  = 0x35002;

    /** Values for the {@link #GLFW_CONTEXT_CREATION_API CONTEXT_CREATION_API} hint. */
    public static final int
    GLFW_NATIVE_CONTEXT_API = 0x36001,
    GLFW_EGL_CONTEXT_API    = 0x36002,
    GLFW_OSMESA_CONTEXT_API = 0x36003;

    // GLFW Callbacks
    /* volatile */ public static GLFWCharCallback mGLFWCharCallback;
    /* volatile */ public static GLFWCharModsCallback mGLFWCharModsCallback;
    /* volatile */ public static GLFWCursorEnterCallback mGLFWCursorEnterCallback;
    /* volatile */ public static GLFWCursorPosCallback mGLFWCursorPosCallback;
    /* volatile */ public static GLFWDropCallback mGLFWDropCallback;
    /* volatile */ public static GLFWErrorCallback mGLFWErrorCallback;
    /* volatile */ public static GLFWFramebufferSizeCallback mGLFWFramebufferSizeCallback;
    /* volatile */ public static GLFWJoystickCallback mGLFWJoystickCallback;
    /* volatile */ public static GLFWKeyCallback mGLFWKeyCallback;
    /* volatile */ public static GLFWMonitorCallback mGLFWMonitorCallback;
    /* volatile */ public static GLFWMouseButtonCallback mGLFWMouseButtonCallback;
    /* volatile */ public static GLFWScrollCallback mGLFWScrollCallback;
    /* volatile */ public static GLFWWindowCloseCallback mGLFWWindowCloseCallback;
    /* volatile */ public static GLFWWindowContentScaleCallback mGLFWWindowContentScaleCallback;
    /* volatile */ public static GLFWWindowFocusCallback mGLFWWindowFocusCallback;
    /* volatile */ public static GLFWWindowIconifyCallback mGLFWWindowIconifyCallback;
    /* volatile */ public static GLFWWindowMaximizeCallback mGLFWWindowMaximizeCallback;
    /* volatile */ public static GLFWWindowPosCallback mGLFWWindowPosCallback;
    /* volatile */ public static GLFWWindowRefreshCallback mGLFWWindowRefreshCallback;
    /* volatile */ public static GLFWWindowSizeCallback mGLFWWindowSizeCallback;

    volatile public static int mGLFWWindowWidth, mGLFWWindowHeight;

    private static GLFWGammaRamp mGLFWGammaRamp;
    private static Map<Integer, String> mGLFWKeyCodes;
    private static GLFWVidMode mGLFWVideoMode;
    private static long mGLFWWindowMonitor;

    private static double mGLFWInitialTime;

    private static ArrayMap<Long, GLFWWindowProperties> mGLFWWindowMap;
    public static boolean mGLFWIsInputReady;
    private static boolean mGLFWInputPumping;
    public static final ByteBuffer keyDownBuffer = ByteBuffer.allocateDirect(317);
    public static final ByteBuffer mouseDownBuffer = ByteBuffer.allocateDirect(8);

    private static final String PROP_WINDOW_WIDTH = "glfwstub.windowWidth";
    private static final String PROP_WINDOW_HEIGHT= "glfwstub.windowHeight";
    public static long mainContext = 0;

    static {
        String windowWidth = System.getProperty(PROP_WINDOW_WIDTH);
        String windowHeight = System.getProperty(PROP_WINDOW_HEIGHT);
        if (windowWidth == null || windowHeight == null) {
            System.err.println("Warning: Property " + PROP_WINDOW_WIDTH + " or " + PROP_WINDOW_HEIGHT + " not set, defaulting to 1280 and 720");

            mGLFWWindowWidth = 1280;
            mGLFWWindowHeight = 720;
        } else {
            mGLFWWindowWidth = Integer.parseInt(windowWidth);
            mGLFWWindowHeight = Integer.parseInt(windowHeight);
        }

        // Minecraft triggers a glfwPollEvents() on splash screen, so update window size there.
        // CallbackBridge.receiveCallback(CallbackBridge.EVENT_TYPE_FRAMEBUFFER_SIZE, mGLFWWindowWidth, mGLFWWindowHeight, 0, 0);
        // CallbackBridge.receiveCallback(CallbackBridge.EVENT_TYPE_WINDOW_SIZE, mGLFWWindowWidth, mGLFWWindowHeight, 0, 0);

        try {
            System.loadLibrary("pojavexec");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
        mGLFWErrorCallback = GLFWErrorCallback.createPrint();
        mGLFWKeyCodes = new ArrayMap<>();

        mGLFWWindowMap = new ArrayMap<>();

        mGLFWVideoMode = new GLFWVidMode(ByteBuffer.allocateDirect(GLFWVidMode.SIZEOF));
        memPutInt(mGLFWVideoMode.address() + mGLFWVideoMode.WIDTH, mGLFWWindowWidth);
        memPutInt(mGLFWVideoMode.address() + mGLFWVideoMode.HEIGHT, mGLFWWindowHeight);
        memPutInt(mGLFWVideoMode.address() + mGLFWVideoMode.REDBITS, 8);
        memPutInt(mGLFWVideoMode.address() + mGLFWVideoMode.GREENBITS, 8);
        memPutInt(mGLFWVideoMode.address() + mGLFWVideoMode.BLUEBITS, 8);
        memPutInt(mGLFWVideoMode.address() + mGLFWVideoMode.REFRESHRATE, 60);

        // A way to generate key code names
        Field[] thisFieldArr = GLFW.class.getFields();
        try {
            for (Field thisField : thisFieldArr) {
                if (thisField.getName().startsWith("GLFW_KEY_")) {
                    mGLFWKeyCodes.put(
                    (int) thisField.get(null),
                    thisField.getName().substring(9, 10).toUpperCase() +
                    thisField.getName().substring(10).replace("_", " ").toLowerCase()
                    );
                }
            }
        } catch (IllegalAccessException e) {
            // This will never happen since this is accessing itself
        }

		/*
		 mGLFWMonitorCallback = new GLFWMonitorCallback(){

		 // Fake one!!!
		 @Override
		 public void free() {}

		 @Override
		 public void callback(long args) {
		 // TODO: Implement this method
		 }
		 };
		 */
    }

    private static native long nglfwSetCharCallback(long window, long ptr);
    private static native long nglfwSetCharModsCallback(long window, long ptr);
    private static native long nglfwSetCursorEnterCallback(long window, long ptr);
    private static native long nglfwSetCursorPosCallback(long window, long ptr);
    private static native long nglfwSetFramebufferSizeCallback(long window, long ptr);
    private static native long nglfwSetKeyCallback(long window, long ptr);
    private static native long nglfwSetMouseButtonCallback(long window, long ptr);
    private static native long nglfwSetScrollCallback(long window, long ptr);
    private static native long nglfwSetWindowSizeCallback(long window, long ptr);
    // private static native void nglfwSetInputReady();
    private static native void nglfwSetShowingWindow(long window);

    /*
     private static void priGlfwSetError(int error) {
     mGLFW_currentError = error;
     if (error != GLFW_NO_ERROR && mGLFWErrorCallback != null) {
     mGLFWErrorCallback.invoke(error, 0);
     }
     }

     private static void priGlfwNoError() {
     priGlfwSetError(GLFW_NO_ERROR);
     }
     */
    protected GLFW() {
        throw new UnsupportedOperationException();
    }

    private static final SharedLibrary GLFW = Library.loadNative(GLFW.class, "org.lwjgl.glfw", "libpojavexec.so", true);

    /** Contains the function pointers loaded from the glfw {@link SharedLibrary}. */
    public static final class Functions {

        private Functions() {}

        /** Function address. */
        public static final long
        Init = apiGetFunctionAddress(GLFW, "pojavInit"),
        CreateContext = apiGetFunctionAddress(GLFW, "pojavCreateContext"),
        GetCurrentContext = apiGetFunctionAddress(GLFW, "pojavGetCurrentContext"),
        //DetachOnCurrentThread = apiGetFunctionAddress(GLFW, "pojavDetachOnCurrentThread"),
        MakeContextCurrent = apiGetFunctionAddress(GLFW, "pojavMakeCurrent"),
        Terminate = apiGetFunctionAddress(GLFW, "pojavTerminate"),
        SetWindowHint = apiGetFunctionAddress(GLFW, "pojavSetWindowHint"),
        SwapBuffers = apiGetFunctionAddress(GLFW, "pojavSwapBuffers"),
        SwapInterval = apiGetFunctionAddress(GLFW, "pojavSwapInterval"),
        PumpEvents = apiGetFunctionAddress(GLFW, "pojavPumpEvents"),
        StopPumping = apiGetFunctionAddress(GLFW, "pojavStopPumping"),
        StartPumping = apiGetFunctionAddress(GLFW, "pojavStartPumping");
    }

    public static SharedLibrary getLibrary() {
        return GLFW;
    }

    public static void internalChangeMonitorSize(int width, int height) {
        mGLFWWindowWidth = width;
        mGLFWWindowHeight = height;
        if (mGLFWVideoMode == null) return;
        memPutInt(mGLFWVideoMode.address() + (long) mGLFWVideoMode.WIDTH, mGLFWWindowWidth);
        memPutInt(mGLFWVideoMode.address() + (long) mGLFWVideoMode.HEIGHT, mGLFWWindowHeight);
    }

    public static GLFWWindowProperties internalGetWindow(long window) {
        GLFWWindowProperties win = mGLFWWindowMap.get(window);
        if (win == null) {
            throw new IllegalArgumentException("No window pointer found: " + window);
        }
        return win;
    }

    // Generated stub callback methods
    public static GLFWCharCallback glfwSetCharCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWcharfun") GLFWCharCallbackI cbfun) {
        GLFWCharCallback lastCallback = mGLFWCharCallback;
        if (cbfun == null) mGLFWCharCallback = null;
        else mGLFWCharCallback = GLFWCharCallback.createSafe(nglfwSetCharCallback(window, memAddressSafe(cbfun)));

        return lastCallback;
    }

    public static GLFWCharModsCallback glfwSetCharModsCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWcharmodsfun") GLFWCharModsCallbackI cbfun) {
        GLFWCharModsCallback lastCallback = mGLFWCharModsCallback;
        if (cbfun == null) mGLFWCharModsCallback = null;
        else mGLFWCharModsCallback = GLFWCharModsCallback.createSafe(nglfwSetCharModsCallback(window, memAddressSafe(cbfun)));

        return lastCallback;
    }

    public static GLFWCursorEnterCallback glfwSetCursorEnterCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWcursorenterfun") GLFWCursorEnterCallbackI cbfun) {
        GLFWCursorEnterCallback lastCallback = mGLFWCursorEnterCallback;
        if (cbfun == null) mGLFWCursorEnterCallback = null;
        else mGLFWCursorEnterCallback = GLFWCursorEnterCallback.createSafe(nglfwSetCursorEnterCallback(window, memAddressSafe(cbfun)));

        return lastCallback;
    }

    public static GLFWCursorPosCallback glfwSetCursorPosCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWcursorposfun") GLFWCursorPosCallbackI cbfun) {
        GLFWCursorPosCallback lastCallback = mGLFWCursorPosCallback;
        if (cbfun == null) mGLFWCursorPosCallback = null;
        else mGLFWCursorPosCallback = GLFWCursorPosCallback.createSafe(nglfwSetCursorPosCallback(window, memAddressSafe(cbfun)));

        return lastCallback;
    }

    public static GLFWDropCallback glfwSetDropCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWdropfun") GLFWDropCallbackI cbfun) {
        GLFWDropCallback lastCallback = mGLFWDropCallback;
        if (cbfun == null) mGLFWDropCallback = null;
        else mGLFWDropCallback = GLFWDropCallback.create(cbfun);

        return lastCallback;
    }

    public static GLFWErrorCallback glfwSetErrorCallback(@Nullable @NativeType("GLFWerrorfun") GLFWErrorCallbackI cbfun) {
        GLFWErrorCallback lastCallback = mGLFWErrorCallback;
        if (cbfun == null) mGLFWErrorCallback = null;
        else mGLFWErrorCallback = GLFWErrorCallback.create(cbfun);

        return lastCallback;
    }

    public static GLFWFramebufferSizeCallback glfwSetFramebufferSizeCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWframebuffersizefun") GLFWFramebufferSizeCallbackI cbfun) {
        GLFWFramebufferSizeCallback lastCallback = mGLFWFramebufferSizeCallback;
        if (cbfun == null) mGLFWFramebufferSizeCallback = null;
        else mGLFWFramebufferSizeCallback = GLFWFramebufferSizeCallback.createSafe(nglfwSetFramebufferSizeCallback(window, memAddressSafe(cbfun)));

        return lastCallback;
    }

    public static GLFWJoystickCallback glfwSetJoystickCallback(/* @NativeType("GLFWwindow *") long window, */ @Nullable @NativeType("GLFWjoystickfun") GLFWJoystickCallbackI cbfun) {
        GLFWJoystickCallback lastCallback = mGLFWJoystickCallback;
        if (cbfun == null) mGLFWJoystickCallback = null;
        else mGLFWJoystickCallback = GLFWJoystickCallback.create(cbfun);

        return lastCallback;
    }

    public static GLFWKeyCallback glfwSetKeyCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWkeyfun") GLFWKeyCallbackI cbfun) {
        GLFWKeyCallback lastCallback = mGLFWKeyCallback;
        if (cbfun == null) mGLFWKeyCallback = null;
        else mGLFWKeyCallback = GLFWKeyCallback.createSafe(nglfwSetKeyCallback(window, memAddressSafe(cbfun)));

        return lastCallback;
    }

    public static GLFWMonitorCallback glfwSetMonitorCallback(@Nullable @NativeType("GLFWmonitorfun") GLFWMonitorCallbackI cbfun) {
        GLFWMonitorCallback lastCallback = mGLFWMonitorCallback;
        if (cbfun == null) mGLFWMonitorCallback = null;
        else mGLFWMonitorCallback = GLFWMonitorCallback.create(cbfun);

        return lastCallback;
    }

    public static GLFWMouseButtonCallback glfwSetMouseButtonCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWmousebuttonfun") GLFWMouseButtonCallbackI cbfun) {
        GLFWMouseButtonCallback lastCallback = mGLFWMouseButtonCallback;
        if (cbfun == null) mGLFWMouseButtonCallback = null;
        else mGLFWMouseButtonCallback = GLFWMouseButtonCallback.createSafe(nglfwSetMouseButtonCallback(window, memAddressSafe(cbfun)));

        return lastCallback;
    }

    public static GLFWScrollCallback glfwSetScrollCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWscrollfun") GLFWScrollCallbackI cbfun) {
        GLFWScrollCallback lastCallback = mGLFWScrollCallback;
        if (cbfun == null) mGLFWScrollCallback = null;
        else mGLFWScrollCallback = GLFWScrollCallback.createSafe(nglfwSetScrollCallback(window, memAddressSafe(cbfun)));

        return lastCallback;
    }

    public static GLFWWindowCloseCallback glfwSetWindowCloseCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWwindowclosefun") GLFWWindowCloseCallbackI cbfun) {
        GLFWWindowCloseCallback lastCallback = mGLFWWindowCloseCallback;
        if (cbfun == null) mGLFWWindowCloseCallback = null;
        else mGLFWWindowCloseCallback = GLFWWindowCloseCallback.create(cbfun);

        return lastCallback;
    }

    public static GLFWWindowContentScaleCallback glfwSetWindowContentScaleCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWwindowcontentscalefun") GLFWWindowContentScaleCallbackI cbfun) {
        GLFWWindowContentScaleCallback lastCallback = mGLFWWindowContentScaleCallback;
        if (cbfun == null) mGLFWWindowContentScaleCallback = null;
        else mGLFWWindowContentScaleCallback = GLFWWindowContentScaleCallback.create(cbfun);

        return lastCallback;
    }

    public static GLFWWindowFocusCallback glfwSetWindowFocusCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWwindowfocusfun") GLFWWindowFocusCallbackI cbfun) {
        GLFWWindowFocusCallback lastCallback = mGLFWWindowFocusCallback;
        if (cbfun == null) mGLFWWindowFocusCallback = null;
        else mGLFWWindowFocusCallback = GLFWWindowFocusCallback.create(cbfun);
        return lastCallback;
    }

    public static GLFWWindowIconifyCallback glfwSetWindowIconifyCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWwindowiconifyfun") GLFWWindowIconifyCallbackI cbfun) {
        GLFWWindowIconifyCallback lastCallback = mGLFWWindowIconifyCallback;
        if (cbfun == null) mGLFWWindowIconifyCallback = null;
        else mGLFWWindowIconifyCallback = GLFWWindowIconifyCallback.create(cbfun);

        return lastCallback;
    }

    public static GLFWWindowMaximizeCallback glfwSetWindowMaximizeCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWwindowmaximizefun") GLFWWindowMaximizeCallbackI cbfun) {
        GLFWWindowMaximizeCallback lastCallback = mGLFWWindowMaximizeCallback;
        if (cbfun == null) mGLFWWindowMaximizeCallback = null;
        else mGLFWWindowMaximizeCallback = GLFWWindowMaximizeCallback.create(cbfun);

        return lastCallback;
    }

    public static GLFWWindowPosCallback glfwSetWindowPosCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWwindowposfun") GLFWWindowPosCallbackI cbfun) {
        GLFWWindowPosCallback lastCallback = mGLFWWindowPosCallback;
        if (cbfun == null) mGLFWWindowPosCallback = null;
        else mGLFWWindowPosCallback = GLFWWindowPosCallback.create(cbfun);

        return lastCallback;
    }

    public static GLFWWindowRefreshCallback glfwSetWindowRefreshCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWwindowrefreshfun") GLFWWindowRefreshCallbackI cbfun) {
        GLFWWindowRefreshCallback lastCallback = mGLFWWindowRefreshCallback;
        if (cbfun == null) mGLFWWindowRefreshCallback = null;
        else mGLFWWindowRefreshCallback = GLFWWindowRefreshCallback.create(cbfun);

        return lastCallback;
    }

    public static GLFWWindowSizeCallback glfwSetWindowSizeCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWwindowsizefun") GLFWWindowSizeCallbackI cbfun) {
        GLFWWindowSizeCallback lastCallback = mGLFWWindowSizeCallback;
        if (cbfun == null) mGLFWWindowSizeCallback = null;
        else mGLFWWindowSizeCallback = GLFWWindowSizeCallback.createSafe(nglfwSetWindowSizeCallback(window, memAddressSafe(cbfun)));

        return lastCallback;
    }

    static boolean isGLFWReady;
    public static boolean glfwInit() {
        if (!isGLFWReady) {
            //CallbackBridge.nativeAttachThreadToOther(false, false);
            mGLFWInitialTime = (double) System.nanoTime();
            long __functionAddress = Functions.Init;
            isGLFWReady = invokeI(__functionAddress) != 0;
        }
        return isGLFWReady;
    }

    public static void glfwTerminate() {
        mGLFWIsInputReady = false;
        CallbackBridge.nativeSetInputReady(false);

        long __functionAddress = Functions.Terminate;
        invokeV(__functionAddress);
    }

    public static void glfwInitHint(int hint, int value) { }

    public static int glfwGetPlatform() {
        return GLFW_PLATFORM_X11;
    }

    @NativeType("GLFWwindow *")
    public static long glfwGetCurrentContext() {
        long __functionAddress = Functions.GetCurrentContext;
        return invokeP(__functionAddress);
    }

    public static void glfwGetFramebufferSize(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("int *") IntBuffer width, @Nullable @NativeType("int *") IntBuffer height) {
        if (CHECKS) {
            checkSafe(width, 1);
            checkSafe(height, 1);
        }
        width.put(internalGetWindow(window).width);
        height.put(internalGetWindow(window).height);
    }

    @Nullable
    @NativeType("GLFWmonitor **")
    public static PointerBuffer glfwGetMonitors() {
        PointerBuffer pBuffer = PointerBuffer.allocateDirect(1);
        pBuffer.put(glfwGetPrimaryMonitor());
        return pBuffer;
    }

    public static long glfwGetPrimaryMonitor() {
        // Prevent NULL check
        return 1L;
    }

    public static void glfwGetMonitorPos(@NativeType("GLFWmonitor *") long monitor, @Nullable @NativeType("int *") IntBuffer xpos, @Nullable @NativeType("int *") IntBuffer ypos) {
        if (CHECKS) {
            checkSafe(xpos, 1);
            checkSafe(ypos, 1);
        }

        xpos.put(0);
        ypos.put(0);
    }

    public static void glfwGetMonitorWorkarea(@NativeType("GLFWmonitor *") long monitor, @Nullable @NativeType("int *") IntBuffer xpos, @Nullable @NativeType("int *") IntBuffer ypos, @Nullable @NativeType("int *") IntBuffer width, @Nullable @NativeType("int *") IntBuffer height) {
        if (CHECKS) {
            checkSafe(xpos, 1);
            checkSafe(ypos, 1);
            checkSafe(width, 1);
            checkSafe(height, 1);
        }

        xpos.put(0);
        ypos.put(0);
        width.put(mGLFWWindowWidth);
        height.put(mGLFWWindowHeight);
    }

    @NativeType("GLFWmonitor *")
    public static long glfwGetWindowMonitor(@NativeType("GLFWwindow *") long window) {
        return mGLFWWindowMonitor;
    }

    public static void glfwSetWindowMonitor(@NativeType("GLFWwindow *") long window, @NativeType("GLFWmonitor *") long monitor, int xpos, int ypos, int width, int height, int refreshRate) {
        // weird calculation to fake pointer
        mGLFWWindowMonitor = window * monitor;
    }

    public static int glfwGetWindowAttrib(@NativeType("GLFWwindow *") long window, int attrib) {
        return internalGetWindow(window).windowAttribs.getOrDefault(attrib, 0);
    }

    public static void glfwSetWindowAttrib(@NativeType("GLFWwindow *") long window, int attrib, int value) {
        internalGetWindow(window).windowAttribs.put(attrib, value);
    }

    public static void glfwGetVersion(IntBuffer major, IntBuffer minor, IntBuffer rev) {
        if (CHECKS) {
            checkSafe(major, 1);
            checkSafe(minor, 1);
            checkSafe(rev, 1);
        }

        major.put(GLFW_VERSION_MAJOR);
        minor.put(GLFW_VERSION_MINOR);
        rev.put(GLFW_VERSION_REVISION);
    }

    public static String glfwGetVersionString() {
        return GLFW_VERSION_MAJOR + "." + GLFW_VERSION_MINOR + "." + GLFW_VERSION_REVISION;
    }

    public static int glfwGetError(@Nullable PointerBuffer description) {
        return GLFW_NO_ERROR;
    }

    @Nullable
    @NativeType("GLFWvidmode const *")
    public static GLFWVidMode.Buffer glfwGetVideoModes(@NativeType("GLFWmonitor *") long monitor) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            // long __result = nglfwGetVideoModes(monitor, memAddress(count));
            long __result = glfwGetVideoMode(monitor).address();
            return GLFWVidMode.createSafe(__result, 1);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    @Nullable
    public static GLFWVidMode glfwGetVideoMode(long monitor) {
        return mGLFWVideoMode;
    }

    public static GLFWGammaRamp glfwGetGammaRamp(@NativeType("GLFWmonitor *") long monitor) {
        return mGLFWGammaRamp;
    }
    public static void glfwSetGammaRamp(@NativeType("GLFWmonitor *") long monitor, @NativeType("const GLFWgammaramp *") GLFWGammaRamp ramp) {
        mGLFWGammaRamp = ramp;
    }

    public static void glfwMakeContextCurrent(@NativeType("GLFWwindow *") long window) {
        long __functionAddress = Functions.MakeContextCurrent;
        invokePV(window, __functionAddress);
    }

    public static void glfwSwapBuffers(@NativeType("GLFWwindow *") long window) {
        long __functionAddress = Functions.SwapBuffers;
        invokePV(window, __functionAddress);
    }

    public static void glfwSwapInterval(int interval) {
        long __functionAddress = Functions.SwapInterval;
        invokeV(interval, __functionAddress);
    }

    // private static double mTime = 0d;
    public static double glfwGetTime() {
        // Boardwalk: just use system timer
        // System.out.println("glfwGetTime");
        return (System.nanoTime() - mGLFWInitialTime) / 1.e9;
    }

    public static void glfwSetTime(double time) {
        mGLFWInitialTime = System.nanoTime() - (long) time;
    }

    public static long glfwGetTimerValue() {
        return System.currentTimeMillis();
    }

    public static long glfwGetTimerFrequency() {
        // FIXME set correct value!!
        return 60;
    }

    // GLFW Window functions
    public static long nglfwCreateContext(long share) {
        return invokePP(share, Functions.CreateContext);
    }
    public static long glfwCreateWindow(int width, int height, CharSequence title, long monitor, long share) {
        // Create an ACTUAL EGL context
        long ptr = nglfwCreateContext(share);
        //nativeEglMakeCurrent(ptr);
        GLFWWindowProperties win = new GLFWWindowProperties();
        // win.width = width;
        // win.height = height;

        win.width = mGLFWWindowWidth;
        win.height = mGLFWWindowHeight;
        win.title = title;

        win.windowAttribs.put(GLFW_HOVERED, 1);
        win.windowAttribs.put(GLFW_VISIBLE, 1);

        mGLFWWindowMap.put(ptr, win);
        mainContext = ptr;
        return ptr;
        //Return our context
    }

    public static void glfwDestroyWindow(long window) {
        // Check window exists
        try {
            internalGetWindow(window);
            mGLFWWindowMap.remove(window);
        } catch (IllegalArgumentException e) {
            System.out.println("GLFW: Warning: failed to remove window " + window);
            e.printStackTrace();
        }
        nglfwSetShowingWindow(mGLFWWindowMap.size() == 0 ? 0 : mGLFWWindowMap.keyAt(mGLFWWindowMap.size() - 1));
    }

    public static void glfwDefaultWindowHints() {}

    public static void glfwGetWindowSize(long window, IntBuffer width, IntBuffer height) {
        if (width != null) width.put(internalGetWindow(window).width);
        if (height != null) height.put(internalGetWindow(window).height);
    }

    public static void glfwSetWindowSizeLimits(@NativeType("GLFWwindow *") long window, int minwidth, int minheight, int maxwidth, int maxheight) {
    }

    public static void glfwSetWindowPos(long window, int x, int y) {
        internalGetWindow(window).x = x;
        internalGetWindow(window).y = y;
    }

    public static void glfwSetWindowSize(long window, int width, int height) {
        internalGetWindow(window).width = width;
        internalGetWindow(window).height = height;

        System.out.println("GLFW: Set size for window " + window + ", width=" + width + ", height=" + height);
    }

    public static void glfwShowWindow(long window) {
        nglfwSetShowingWindow(window);
    }

    public static void glfwWindowHint(int hint, int value) {
        long __functionAddress = Functions.SetWindowHint;
        invokeV(hint, value, __functionAddress);
    }

    public static void glfwWindowHintString(int hint, @NativeType("const char *") ByteBuffer value) {}
    public static void glfwWindowHintString(int hint, @NativeType("const char *") CharSequence value) {}

    public static boolean glfwWindowShouldClose(long window) {
        return internalGetWindow(window).shouldClose;
    }

    public static void glfwSetWindowShouldClose(long window, boolean close) {
        internalGetWindow(window).shouldClose = close;
    }


    public static void glfwSetWindowTitle(@NativeType("GLFWwindow *") long window, @NativeType("char const *") ByteBuffer title) {

    }
    public static void glfwSetWindowTitle(@NativeType("GLFWwindow *") long window, @NativeType("char const *") CharSequence title) {
        internalGetWindow(window).title = title;
    }

    public static void glfwSetWindowIcon(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWimage const *") GLFWImage.Buffer images) {}

    public static void glfwPollEvents() {
        if (!mGLFWIsInputReady) {
            mGLFWIsInputReady = true;
            CallbackBridge.nativeSetInputReady(true);
        }
        // During interactions with UI elements, Minecraft likes to update the screen as events related to those inputs arrive.
        // This leads to calls to glfwPollEvents within glfwPollEvents, which is not good for our queue system.
        // Prevent these with this code.
        if(mGLFWInputPumping) return;
        mGLFWInputPumping = true;
        callV(Functions.StartPumping);
        for (Long ptr : mGLFWWindowMap.keySet()) callJV(ptr, Functions.PumpEvents);
        callV(Functions.StopPumping);
        mGLFWInputPumping = false;
    }

    public static void internalWindowSizeChanged(long window, int w, int h) {
        try {
            internalChangeMonitorSize(w, h);
            glfwSetWindowSize(window, mGLFWWindowWidth, mGLFWWindowHeight);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void glfwWaitEvents() {}

    public static void glfwWaitEventsTimeout(double timeout) {
        // Boardwalk: this isn't how you do a frame limiter, but oh well
        // System.out.println("Frame limiter");
    /*
        try {
            Thread.sleep((long)(timeout * 1000));
        } catch (InterruptedException ie) {
        }
    */
        // System.out.println("Out of the frame limiter");

    }

    public static void glfwPostEmptyEvent() {}

    public static int glfwGetInputMode(@NativeType("GLFWwindow *") long window, int mode) {
        return internalGetWindow(window).inputModes.get(mode);
    }

    public static void glfwSetInputMode(@NativeType("GLFWwindow *") long window, int mode, int value) {
        if (mode == GLFW_CURSOR) {
            switch (value) {
                case GLFW_CURSOR_DISABLED:
                    CallbackBridge.nativeSetGrabbing(true);
                    break;
                default: CallbackBridge.nativeSetGrabbing(false);
            }
        }

        internalGetWindow(window).inputModes.put(mode, value);
    }
    public static String glfwGetKeyName(int key, int scancode) {
        // TODO keyname list from GLFW
        return mGLFWKeyCodes.get(key);
    }

    public static int glfwGetKeyScancode(int key) {
        return 0;
    }

    public static int glfwGetKey(@NativeType("GLFWwindow *") long window, int key) {
        return keyDownBuffer.get(Math.max(0, key-31));
    }

    public static int glfwGetMouseButton(@NativeType("GLFWwindow *") long window, int button) {
        return mouseDownBuffer.get(button);
    }
    public static void glfwGetCursorPos(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("double *") DoubleBuffer xpos, @Nullable @NativeType("double *") DoubleBuffer ypos) {
        if (CHECKS) {
            checkSafe(xpos, 1);
            checkSafe(ypos, 1);
        }
        nglfwGetCursorPos(window, xpos, ypos);
    }


    public static native void nglfwGetCursorPos(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("double *") DoubleBuffer xpos, @Nullable @NativeType("double *") DoubleBuffer ypos);
    public static native void nglfwGetCursorPosA(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("double *") double[] xpos, @Nullable @NativeType("double *") double[] ypos);

    public static native void glfwSetCursorPos(@NativeType("GLFWwindow *") long window, double xpos, double ypos); /*{
        mGLFWCursorX = mGLFWCursorLastX = xpos;
        mGLFWCursorY = mGLFWCursorLastY = ypos;

        CallbackBridge.sendGrabbing(mGLFWIsGrabbing, (int) xpos, (int) ypos);
    }*/

    public static long glfwCreateCursor(@NativeType("const GLFWimage *") GLFWImage image, int xhot, int yhot) {
        return 4L;
    }
    public static long glfwCreateStandardCursor(int shape) {
        return 4L;
    }
    public static void glfwDestroyCursor(@NativeType("GLFWcursor *") long cursor) {}
    public static void glfwSetCursor(@NativeType("GLFWwindow *") long window, @NativeType("GLFWcursor *") long cursor) {}

    public static boolean glfwRawMouseMotionSupported() {
        // Should be not supported?
        return false;
    }

    public static void glfwSetClipboardString(@NativeType("GLFWwindow *") long window, @NativeType("char const *") ByteBuffer string) {
        byte[] arr = new byte[string.remaining()];
        string.get(arr);
        CallbackBridge.nativeClipboard(CallbackBridge.CLIPBOARD_COPY, arr);
    }

    public static void glfwSetClipboardString(@NativeType("GLFWwindow *") long window, @NativeType("char const *") CharSequence string) {
        glfwSetClipboardString(window, memUTF8Safe(string));
    }

    public static String glfwGetClipboardString(@NativeType("GLFWwindow *") long window) {
        return CallbackBridge.nativeClipboard(CallbackBridge.CLIPBOARD_PASTE, null);
    }

    public static void glfwRequestWindowAttention(@NativeType("GLFWwindow *") long window) {
    }

    public static boolean glfwJoystickPresent(int jid) {
        if(jid == 0) {
            return true;
        }else return false;
    }
    public static String glfwGetJoystickName(int jid) {
        if(jid == 0) {
            return "AIC event bus controller";
        }else return null;
    }
    public static FloatBuffer glfwGetJoystickAxes(int jid) {
        if(jid == 0) {
            return joystickData;
        }else return null;
    }
    public static ByteBuffer glfwGetJoystickButtons(int jid) {
        if(jid == 0) {
            return buttonData;
        }else return null;
    }
    public static ByteBuffer glfwGetjoystickHats(int jid) {
        return null;
    }
    public static boolean glfwJoystickIsGamepad(int jid) {
        if(jid == 0) return true;
        else return false;
    }
    public static String glfwGetJoystickGUID(int jid) {
        if(jid == 0) return "aio0";
        else return null;
    }
    public static long glfwGetJoystickUserPointer(int jid) {
        return 0;
    }
    public static void glfwSetJoystickUserPointer(int jid, long pointer) {

    }
    public static boolean glfwUpdateGamepadMappings(ByteBuffer string) {
        return false;
    }
    public static String glfwGetGamepadName(int jid) {
        return null;
    }
    public static boolean glfwGetGamepadState(int jid, GLFWGamepadState state) {
        return false;
    }

    /** Array version of: {@link #glfwGetVersion GetVersion} */
    public static void glfwGetVersion(@Nullable @NativeType("int *") int[] major, @Nullable @NativeType("int *") int[] minor, @Nullable @NativeType("int *") int[] rev) {
        if (CHECKS) {
            checkSafe(major, 1);
            checkSafe(minor, 1);
            checkSafe(rev, 1);
        }
        major[0] = GLFW_VERSION_MAJOR;
        minor[0] = GLFW_VERSION_MINOR;
        rev[0] = GLFW_VERSION_REVISION;
    }

    /** Array version of: {@link #glfwGetMonitorPos GetMonitorPos} */
    public static void glfwGetMonitorPos(@NativeType("GLFWmonitor *") long monitor, @Nullable @NativeType("int *") int[] xpos, @Nullable @NativeType("int *") int[] ypos) {
        if (CHECKS) {
            // check(monitor);
            checkSafe(xpos, 1);
            checkSafe(ypos, 1);
        }
        xpos[0] = 0;
        ypos[0] = 0;
    }

    /** Array version of: {@link #glfwGetMonitorWorkarea GetMonitorWorkarea} */
    public static void glfwGetMonitorWorkarea(@NativeType("GLFWmonitor *") long monitor, @Nullable @NativeType("int *") int[] xpos, @Nullable @NativeType("int *") int[] ypos, @Nullable @NativeType("int *") int[] width, @Nullable @NativeType("int *") int[] height) {
        if (CHECKS) {
            // check(monitor);
            checkSafe(xpos, 1);
            checkSafe(ypos, 1);
            checkSafe(width, 1);
            checkSafe(height, 1);
        }

        xpos[0] = 0;
        ypos[0] = 0;
        width[0] = mGLFWWindowWidth;
        height[0] = mGLFWWindowHeight;
    }

    /** Array version of: {@link #glfwGetMonitorPhysicalSize GetMonitorPhysicalSize} */
/*
    public static void glfwGetMonitorPhysicalSize(@NativeType("GLFWmonitor *") long monitor, @Nullable @NativeType("int *") int[] widthMM, @Nullable @NativeType("int *") int[] heightMM) {
        long __functionAddress = Functions.GetMonitorPhysicalSize;
        if (CHECKS) {
            // check(monitor);
            checkSafe(widthMM, 1);
            checkSafe(heightMM, 1);
        }
        invokePPPV(monitor, widthMM, heightMM, __functionAddress);
    }
*/

    /** Array version of: {@link #glfwGetMonitorContentScale GetMonitorContentScale} */
/*
    public static void glfwGetMonitorContentScale(@NativeType("GLFWmonitor *") long monitor, @Nullable @NativeType("float *") float[] xscale, @Nullable @NativeType("float *") float[] yscale) {
        long __functionAddress = Functions.GetMonitorContentScale;
        if (CHECKS) {
            // check(monitor);
            checkSafe(xscale, 1);
            checkSafe(yscale, 1);
        }
        invokePPPV(monitor, xscale, yscale, __functionAddress);
    }
*/

    /** Array version of: {@link #glfwGetWindowPos GetWindowPos} */
    public static void glfwGetWindowPos(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("int *") int[] xpos, @Nullable @NativeType("int *") int[] ypos) {
        if (CHECKS) {
            // check(window);
            checkSafe(xpos, 1);
            checkSafe(ypos, 1);
        }
        xpos[0] = 0;
        ypos[0] = 0;
    }

    /** Array version of: {@link #glfwGetWindowSize GetWindowSize} */
    public static void glfwGetWindowSize(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("int *") int[] width, @Nullable @NativeType("int *") int[] height) {
        if (CHECKS) {
            // check(window);
            checkSafe(width, 1);
            checkSafe(height, 1);
        }
        width[0] = internalGetWindow(window).width;
        height[0] = internalGetWindow(window).height;
    }

    /** Array version of: {@link #glfwGetFramebufferSize GetFramebufferSize} */
    public static void glfwGetFramebufferSize(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("int *") int[] width, @Nullable @NativeType("int *") int[] height) {
        if (CHECKS) {
            // check(window);
            checkSafe(width, 1);
            checkSafe(height, 1);
        }
        width[0] = internalGetWindow(window).width;
        height[0] = internalGetWindow(window).height;
    }

    /** Array version of: {@link #glfwGetWindowFrameSize GetWindowFrameSize} */
    public static void glfwGetWindowFrameSize(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("int *") int[] left, @Nullable @NativeType("int *") int[] top, @Nullable @NativeType("int *") int[] right, @Nullable @NativeType("int *") int[] bottom) {
        if (CHECKS) {
            // check(window);
            checkSafe(left, 1);
            checkSafe(top, 1);
            checkSafe(right, 1);
            checkSafe(bottom, 1);
        }
        left[0] = top[0] = 0;
        right[0] = internalGetWindow(window).width;
        bottom[0] = internalGetWindow(window).height;
    }

    /** Array version of: {@link #glfwGetWindowContentScale GetWindowContentScale} */
/*
    public static void glfwGetWindowContentScale(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("float *") float[] xscale, @Nullable @NativeType("float *") float[] yscale) {
        long __functionAddress = Functions.GetWindowContentScale;
        if (CHECKS) {
            // check(window);
            checkSafe(xscale, 1);
            checkSafe(yscale, 1);
        }
        invokePPPV(window, xscale, yscale, __functionAddress);
    }
*/

    /** Array version of: {@link #glfwGetCursorPos GetCursorPos} */
    public static void glfwGetCursorPos(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("double *") double[] xpos, @Nullable @NativeType("double *") double[] ypos) {
        if (CHECKS) {
            // check(window);
            checkSafe(xpos, 1);
            checkSafe(ypos, 1);
        }
        nglfwGetCursorPosA(window, xpos, ypos);
    }

    @NativeType("int")
    public static boolean glfwExtensionSupported(@NativeType("char const *") CharSequence ext) {
        //return Arrays.stream(glGetString(GL_EXTENSIONS).split(" ")).anyMatch(ext::equals);
        // Fast path, but will return true if one has the same prefix
        return glGetString(GL_EXTENSIONS).contains(ext);
    }
}
