package org.lwjgl.input;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.LWJGLException;

public class Cursor {

	/** 1 bit transparency for native cursor */
	public static final int CURSOR_ONE_BIT_TRANSPARENCY = 1;

	/** 8 bit alpha native cursor */
	public static final int CURSOR_8_BIT_ALPHA = 2;

	/** Animation native cursor */
	public static final int CURSOR_ANIMATION = 4;

	/** Elements to display */
	private final CursorElement[] cursors;

	/** Index into list of cursors */
	private int index;

	/** Flag set when the cursor has been destroyed */
	private boolean destroyed;

	/** Flag set if the cursor is empty */
	private boolean isEmpty;

	/**
	 * Constructs a new Cursor, with the given parameters. Mouse must have been
	 * created before you can create Cursor objects. Cursor images are in ARGB
	 * format, but only one bit transparency is guaranteed to be supported. So
	 * to maximize portability, LWJGL applications should only create cursor
	 * images with 0x00 or 0xff as alpha values. The constructor will copy the
	 * images and delays, so there's no need to keep them around.
	 *
	 * @param width
	 *            cursor image width
	 * @param height
	 *            cursor image height
	 * @param xHotspot
	 *            the x coordinate of the cursor hotspot
	 * @param yHotspot
	 *            the y coordinate of the cursor hotspot
	 * @param numImages
	 *            number of cursor images specified. Must be 1 if animations are
	 *            not supported.
	 * @param images
	 *            A buffer containing the images. The origin is at the lower
	 *            left corner, like OpenGL.
	 * @param delays
	 *            An int buffer of animation frame delays, if numImages is
	 *            greater than 1, else null
	 * @throws LWJGLException
	 *             if the cursor could not be created for any reason
	 */
	public Cursor(int width, int height, int xHotspot, int yHotspot, int numImages, IntBuffer images, IntBuffer delays)
			throws LWJGLException {
		cursors = new CursorElement[numImages];

		IntBuffer flippedImages = BufferUtils.createIntBuffer(images.limit());
		flipImages(width, height, numImages, images, flippedImages);

		ByteBuffer pixels = convertARGBIntBuffertoRGBAByteBuffer(width, height, flippedImages);
		if(numImages == 1) {
			isEmpty = true;
			for(int i = 0; i < width*height; i++) if(pixels.get(i) != 0) {
				System.out.println("Encountered non-zero byte at "+i+", custom cursor is not empty!");
				isEmpty = false;
			}
		}
		for (int i = 0; i < numImages; i++) {
			int size = width * height;
			ByteBuffer image = BufferUtils.createByteBuffer(size);
			for (int j = 0; j < size; j++)
				image.put(pixels.get());

			GLFWImage cursorImage = GLFWImage.malloc();
			cursorImage.width(width);
			cursorImage.height(height);
			cursorImage.pixels(image);

			long delay = (delays != null) ? delays.get(i) : 0;
			long timeout = GLFW.glfwGetTimerValue();
			cursors[i] = new CursorElement(xHotspot, yHotspot, delay, timeout, cursorImage);
		}
	}

	private static ByteBuffer convertARGBIntBuffertoRGBAByteBuffer(int width, int height, IntBuffer imageBuffer) {
		ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);

		for (int i = 0; i < imageBuffer.limit(); i++) {
			int argbColor = imageBuffer.get(i);

			byte alpha = (byte) (argbColor >>> 24);
			byte blue = (byte) (argbColor >>> 16);
			byte green = (byte) (argbColor >>> 8);
			byte red = (byte) argbColor;

			pixels.put(red);
			pixels.put(green);
			pixels.put(blue);
			pixels.put(alpha);
		}

		pixels.flip();

		return pixels;
	}

	/**
	 * Gets the minimum size of a native cursor. Can only be called if The Mouse
	 * is created and cursor caps includes at least CURSOR_ONE_BIT_TRANSPARANCY.
	 *
	 * @return the minimum size of a native cursor
	 */
	public static int getMinCursorSize() {
		return 1;
	}

	/**
	 * Gets the maximum size of a native cursor. Can only be called if the
	 * cursor caps includes at least {@link #CURSOR_ONE_BIT_TRANSPARENCY}.
	 *
	 * @return the maximum size of a native cursor
	 */
	public static int getMaxCursorSize() {
		return 512;
	}

	/**
	 * Get the capabilities of the native cursor. Return a bit mask of the
	 * native cursor capabilities.
	 * <ul>
	 * <li><code>CURSOR_ONE_BIT_TRANSPARENCY</code> indicates support for
	 * cursors with one bit transparency.</li>
	 * 
	 * <li><code>CURSOR_8_BIT_ALPHA</code> indicates support for 8 bit
	 * alpha.</li>
	 * 
	 * <li><code>CURSOR_ANIMATION</code> indicates support for cursor
	 * animations.</li>
	 * </ul>
	 * 
	 * @return A bit mask with native cursor capabilities.
	 */
	public static int getCapabilities() {
		return CURSOR_8_BIT_ALPHA | CURSOR_ANIMATION;
	}

	/**
	 * Flips the images so they're oriented according to OpenGL
	 *
	 * @param width
	 *            Width of image
	 * @param height
	 *            Height of images
	 * @param numImages
	 *            How many images to flip
	 * @param images
	 *            Source images
	 * @param images_copy
	 *            Destination images
	 */
	private static void flipImages(int width, int height, int numImages, IntBuffer images, IntBuffer images_copy) {
		for (int i = 0; i < numImages; i++) {
			int start_index = i * width * height;
			flipImage(width, height, start_index, images, images_copy);
		}
	}

	/**
	 * @param width
	 *            Width of image
	 * @param height
	 *            Height of images
	 * @param start_index
	 *            index into source buffer to copy to
	 * @param images
	 *            Source images
	 * @param images_copy
	 *            Destination images
	 */
	private static void flipImage(int width, int height, int start_index, IntBuffer images, IntBuffer images_copy) {
		for (int y = 0; y < height >> 1; y++) {
			int index_y_1 = y * width + start_index;
			int index_y_2 = (height - y - 1) * width + start_index;
			for (int x = 0; x < width; x++) {
				int index1 = index_y_1 + x;
				int index2 = index_y_2 + x;
				int temp_pixel = images.get(index1 + images.position());
				images_copy.put(index1, images.get(index2 + images.position()));
				images_copy.put(index2, temp_pixel);
			}
		}
	}

	/**
	 * Gets the native handle associated with the cursor object.
	 */
	long getHandle() {
		checkValid();
		return cursors[index].cursorHandle;
	}

	/**
	 * Checks whether the cursor is still active and not yet destroyed.
	 */
	private void checkValid() {
		if (destroyed)
			throw new IllegalStateException("The cursor is already destroyed");
	}

	/**
	 * Destroy the current cursor. If the cursor is current, the current native
	 * cursor is set to null (the default OS cursor)
	 */
	public void destroy() {
		for (CursorElement cursor : cursors)
			GLFW.glfwDestroyCursor(cursor.cursorHandle);

		destroyed = true;
	}

	/**
	 * Sets the timout property to the time it should be changed
	 */

	protected void setTimeout() {
		checkValid();
		cursors[index].timeout = GLFW.glfwGetTimerValue() + cursors[index].delay;
	}

	/**
	 * Determines whether this cursor has timed out
	 * 
	 * @return true if the this cursor has timed out, false if not
	 */

	protected boolean hasTimedOut() {
		checkValid();
		return cursors.length > 1 && cursors[index].timeout < GLFW.glfwGetTimerValue();
	}

	/**
	 * Changes to the next cursor
	 */
	protected void nextCursor() {
		checkValid();
		index = ++index % cursors.length;
	}

	/**
	/* Returns wheteher the cursor image is empty or not
	 */

	/*package-private*/ boolean isEmpty() {
		return isEmpty;
	}

	/**
	 * A single cursor element, used when animating
	 */
	private static class CursorElement {

		final long cursorHandle;
		long delay;
		long timeout;

		CursorElement(int xHotspot, int yHotspot, long delay, long timeout, GLFWImage image) {
			this.delay = delay;
			this.timeout = timeout;

			this.cursorHandle = GLFW.glfwCreateCursor(image, xHotspot, yHotspot);
			if (cursorHandle == MemoryUtil.NULL)
				throw new RuntimeException("Error creating GLFW cursor");
		}
	}
}
