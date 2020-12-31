package org.lwjgl.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;

public class Mouse {

	private static boolean grabbed = false;

	private static int lastX = 0;
	private static int lastY = 0;

	private static int latestX = 0;
	private static int latestY = 0;

	private static int x = 0;
	private static int y = 0;

	private static int lastDWheel = 0;

	private static EventQueue queue = new EventQueue(32);

	private static int[] buttonEvents = new int[queue.getMaxEvents()];
	private static int[] wheelEvents = new int[queue.getMaxEvents()];
	private static boolean[] buttonEventStates = new boolean[queue.getMaxEvents()];
	private static int[] xEvents = new int[queue.getMaxEvents()];
	private static int[] yEvents = new int[queue.getMaxEvents()];
	private static int[] lastxEvents = new int[queue.getMaxEvents()];
	private static int[] lastyEvents = new int[queue.getMaxEvents()];
	private static long[] nanoTimeEvents = new long[queue.getMaxEvents()];

	private static boolean clipPostionToDisplay = true;

	private static boolean isMouseInsideWindow = true;

	private static Cursor currentCursor = null;

	public static void addMoveEvent(double mouseX, double mouseY) {
		latestX = (int) mouseX;
		latestY = Display.getHeight() - (int) mouseY;

		lastxEvents[queue.getNextPos()] = xEvents[queue.getNextPos()];
		lastyEvents[queue.getNextPos()] = yEvents[queue.getNextPos()];

		xEvents[queue.getNextPos()] = latestX;
		yEvents[queue.getNextPos()] = latestY;

		buttonEvents[queue.getNextPos()] = -1;
		buttonEventStates[queue.getNextPos()] = false;

		wheelEvents[queue.getNextPos()] = 0;

		nanoTimeEvents[queue.getNextPos()] = Sys.getNanoTime();

		queue.add();
	}

	public static void addButtonEvent(int button, boolean pressed) {
		lastxEvents[queue.getNextPos()] = xEvents[queue.getNextPos()];
		lastyEvents[queue.getNextPos()] = yEvents[queue.getNextPos()];

		xEvents[queue.getNextPos()] = latestX;
		yEvents[queue.getNextPos()] = latestY;

		buttonEvents[queue.getNextPos()] = button;
		buttonEventStates[queue.getNextPos()] = pressed;

		wheelEvents[queue.getNextPos()] = 0;

		nanoTimeEvents[queue.getNextPos()] = Sys.getNanoTime();

		queue.add();
	}

	public static void addWheelEvent(int wheel) {
		lastxEvents[queue.getNextPos()] = xEvents[queue.getNextPos()];
		lastyEvents[queue.getNextPos()] = yEvents[queue.getNextPos()];

		xEvents[queue.getNextPos()] = latestX;
		yEvents[queue.getNextPos()] = latestY;

		buttonEvents[queue.getNextPos()] = -1;
		buttonEventStates[queue.getNextPos()] = false;

		wheelEvents[queue.getNextPos()] = wheel;
		lastDWheel = wheel;

		nanoTimeEvents[queue.getNextPos()] = Sys.getNanoTime();

		queue.add();
	}

	public static void setMouseInsideWindow(boolean mouseInsideWindow) {
		isMouseInsideWindow = mouseInsideWindow;
	}

	public static void poll() {
		lastX = x;
		lastY = y;

		if (!grabbed && clipPostionToDisplay) {
			if (latestX < 0)
				latestX = 0;
			if (latestY < 0)
				latestY = 0;
			if (latestX > Display.getWidth() - 1)
				latestX = Display.getWidth() - 1;
			if (latestY > Display.getHeight() - 1)
				latestY = Display.getHeight() - 1;
		}

		x = latestX;
		y = latestY;
	}

	public static void create() throws LWJGLException {

	}

	public static boolean isCreated() {
		return Display.isCreated();
	}

	public static void setGrabbed(boolean grab) {
		GLFW.glfwSetInputMode(Display.getWindow(), GLFW.GLFW_CURSOR,
				grab ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
		grabbed = grab;
	}

	public static boolean isGrabbed() {
		return grabbed;
	}

	public static boolean isButtonDown(int button) {
		return GLFW.glfwGetMouseButton(Display.getWindow(), button) == GLFW.GLFW_PRESS;
	}

	public static boolean next() {
		return queue.next();
	}

	public static int getEventX() {
		return xEvents[queue.getCurrentPos()];
	}

	public static int getEventY() {
		return yEvents[queue.getCurrentPos()];
	}

	public static int getEventDX() {
		return xEvents[queue.getCurrentPos()] - lastxEvents[queue.getCurrentPos()];
	}

	public static int getEventDY() {
		return yEvents[queue.getCurrentPos()] - lastyEvents[queue.getCurrentPos()];
	}

	public static long getEventNanoseconds() {
		return nanoTimeEvents[queue.getCurrentPos()];
	}

	public static int getEventButton() {
		return buttonEvents[queue.getCurrentPos()];
	}

	public static boolean getEventButtonState() {
		return buttonEventStates[queue.getCurrentPos()];
	}

	public static int getEventDWheel() {
		return wheelEvents[queue.getCurrentPos()];
	}

	public static int getX() {
		return x;
	}

	public static int getY() {
		return y;
	}

	public static int getDX() {
		return x - lastX;
	}

	public static int getDY() {
		return y - lastY;
	}

	public static int getDWheel() {
		int dwheel = lastDWheel;
		lastDWheel = 0;
		return dwheel;
	}

	public static int getButtonCount() {
		return 8; // max mouse buttons supported by GLFW
	}

	public static boolean isInsideWindow() {
		return isMouseInsideWindow;
	}

	public static void setClipMouseCoordinatesToWindow(boolean clip) {
		clipPostionToDisplay = clip;
	}

	public static void setCursorPosition(int new_x, int new_y) {
		GLFW.glfwSetCursorPos(Display.getWindow(), new_x, new_y);
	}

	public static Cursor setNativeCursor(Cursor cursor) throws LWJGLException {
		if (cursor == null) {
			GLFW.glfwSetCursor(Display.getWindow(), MemoryUtil.NULL);
			return null;
		}

		GLFW.glfwSetCursor(Display.getWindow(), cursor.getHandle());
		currentCursor = cursor;
		return cursor;
	}

	public static Cursor getCurrentCursor() {
		return currentCursor;
	}
	
    public static Cursor getNativeCursor() {
        return currentCursor;
    }
    
    public static boolean hasWheel() {
        return true;
    }

	public static void destroy() {

	}
}
