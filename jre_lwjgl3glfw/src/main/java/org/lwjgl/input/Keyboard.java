/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.lwjgl.input;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.InputImplementation;

/**
 * <br>
 * A raw Keyboard interface. This can be used to poll the current state of the
 * keys, or read all the keyboard presses / releases since the last read.
 *
 * @author cix_foo <cix_foo@users.sourceforge.net>
 * @author elias_naur <elias_naur@users.sourceforge.net>
 * @author Brian Matzon <brian@matzon.dk>
 * @version $Revision$
 * $Id$
 */
public class Keyboard {
	/** Internal use - event size in bytes */
	public static final int EVENT_SIZE = 4 + 1 + 4 + 8 + 1;

	/**
	 * The special character meaning that no
	 * character was translated for the event.
	 */
	public static final int CHAR_NONE          = '\0';

	/**
	 * The special keycode meaning that only the
	 * translated character is valid.
	 */
	public static final int KEY_NONE            = 0x00;

	public static final int KEY_ESCAPE          = 0x01;
	public static final int KEY_1               = 0x02;
	public static final int KEY_2               = 0x03;
	public static final int KEY_3               = 0x04;
	public static final int KEY_4               = 0x05;
	public static final int KEY_5               = 0x06;
	public static final int KEY_6               = 0x07;
	public static final int KEY_7               = 0x08;
	public static final int KEY_8               = 0x09;
	public static final int KEY_9               = 0x0A;
	public static final int KEY_0               = 0x0B;
	public static final int KEY_MINUS           = 0x0C; /* - on main keyboard */
	public static final int KEY_EQUALS          = 0x0D;
	public static final int KEY_BACK            = 0x0E; /* backspace */
	public static final int KEY_TAB             = 0x0F;
	public static final int KEY_Q               = 0x10;
	public static final int KEY_W               = 0x11;
	public static final int KEY_E               = 0x12;
	public static final int KEY_R               = 0x13;
	public static final int KEY_T               = 0x14;
	public static final int KEY_Y               = 0x15;
	public static final int KEY_U               = 0x16;
	public static final int KEY_I               = 0x17;
	public static final int KEY_O               = 0x18;
	public static final int KEY_P               = 0x19;
	public static final int KEY_LBRACKET        = 0x1A;
	public static final int KEY_RBRACKET        = 0x1B;
	public static final int KEY_RETURN          = 0x1C; /* Enter on main keyboard */
	public static final int KEY_LCONTROL        = 0x1D;
	public static final int KEY_A               = 0x1E;
	public static final int KEY_S               = 0x1F;
	public static final int KEY_D               = 0x20;
	public static final int KEY_F               = 0x21;
	public static final int KEY_G               = 0x22;
	public static final int KEY_H               = 0x23;
	public static final int KEY_J               = 0x24;
	public static final int KEY_K               = 0x25;
	public static final int KEY_L               = 0x26;
	public static final int KEY_SEMICOLON       = 0x27;
	public static final int KEY_APOSTROPHE      = 0x28;
	public static final int KEY_GRAVE           = 0x29; /* accent grave */
	public static final int KEY_LSHIFT          = 0x2A;
	public static final int KEY_BACKSLASH       = 0x2B;
	public static final int KEY_Z               = 0x2C;
	public static final int KEY_X               = 0x2D;
	public static final int KEY_C               = 0x2E;
	public static final int KEY_V               = 0x2F;
	public static final int KEY_B               = 0x30;
	public static final int KEY_N               = 0x31;
	public static final int KEY_M               = 0x32;
	public static final int KEY_COMMA           = 0x33;
	public static final int KEY_PERIOD          = 0x34; /* . on main keyboard */
	public static final int KEY_SLASH           = 0x35; /* / on main keyboard */
	public static final int KEY_RSHIFT          = 0x36;
	public static final int KEY_MULTIPLY        = 0x37; /* * on numeric keypad */
	public static final int KEY_LMENU           = 0x38; /* left Alt */
	public static final int KEY_SPACE           = 0x39;
	public static final int KEY_CAPITAL         = 0x3A;
	public static final int KEY_F1              = 0x3B;
	public static final int KEY_F2              = 0x3C;
	public static final int KEY_F3              = 0x3D;
	public static final int KEY_F4              = 0x3E;
	public static final int KEY_F5              = 0x3F;
	public static final int KEY_F6              = 0x40;
	public static final int KEY_F7              = 0x41;
	public static final int KEY_F8              = 0x42;
	public static final int KEY_F9              = 0x43;
	public static final int KEY_F10             = 0x44;
	public static final int KEY_NUMLOCK         = 0x45;
	public static final int KEY_SCROLL          = 0x46; /* Scroll Lock */
	public static final int KEY_NUMPAD7         = 0x47;
	public static final int KEY_NUMPAD8         = 0x48;
	public static final int KEY_NUMPAD9         = 0x49;
	public static final int KEY_SUBTRACT        = 0x4A; /* - on numeric keypad */
	public static final int KEY_NUMPAD4         = 0x4B;
	public static final int KEY_NUMPAD5         = 0x4C;
	public static final int KEY_NUMPAD6         = 0x4D;
	public static final int KEY_ADD             = 0x4E; /* + on numeric keypad */
	public static final int KEY_NUMPAD1         = 0x4F;
	public static final int KEY_NUMPAD2         = 0x50;
	public static final int KEY_NUMPAD3         = 0x51;
	public static final int KEY_NUMPAD0         = 0x52;
	public static final int KEY_DECIMAL         = 0x53; /* . on numeric keypad */
	public static final int KEY_F11             = 0x57;
	public static final int KEY_F12             = 0x58;
	public static final int KEY_F13             = 0x64; /*                     (NEC PC98) */
	public static final int KEY_F14             = 0x65; /*                     (NEC PC98) */
	public static final int KEY_F15             = 0x66; /*                     (NEC PC98) */
	public static final int KEY_F16             = 0x67; /* Extended Function keys - (Mac) */
	public static final int KEY_F17             = 0x68;
	public static final int KEY_F18             = 0x69;
	public static final int KEY_KANA            = 0x70; /* (Japanese keyboard)            */
	public static final int KEY_F19             = 0x71; /* Extended Function keys - (Mac) */
	public static final int KEY_CONVERT         = 0x79; /* (Japanese keyboard)            */
	public static final int KEY_NOCONVERT       = 0x7B; /* (Japanese keyboard)            */
	public static final int KEY_YEN             = 0x7D; /* (Japanese keyboard)            */
	public static final int KEY_NUMPADEQUALS    = 0x8D; /* = on numeric keypad (NEC PC98) */
	public static final int KEY_CIRCUMFLEX      = 0x90; /* (Japanese keyboard)            */
	public static final int KEY_AT              = 0x91; /*                     (NEC PC98) */
	public static final int KEY_COLON           = 0x92; /*                     (NEC PC98) */
	public static final int KEY_UNDERLINE       = 0x93; /*                     (NEC PC98) */
	public static final int KEY_KANJI           = 0x94; /* (Japanese keyboard)            */
	public static final int KEY_STOP            = 0x95; /*                     (NEC PC98) */
	public static final int KEY_AX              = 0x96; /*                     (Japan AX) */
	public static final int KEY_UNLABELED       = 0x97; /*                        (J3100) */
	public static final int KEY_NUMPADENTER     = 0x9C; /* Enter on numeric keypad */
	public static final int KEY_RCONTROL        = 0x9D;
	public static final int KEY_SECTION         = 0xA7; /* Section symbol (Mac) */
	public static final int KEY_NUMPADCOMMA     = 0xB3; /* , on numeric keypad (NEC PC98) */
	public static final int KEY_DIVIDE          = 0xB5; /* / on numeric keypad */
	public static final int KEY_SYSRQ           = 0xB7;
	public static final int KEY_RMENU           = 0xB8; /* right Alt */
	public static final int KEY_FUNCTION        = 0xC4; /* Function (Mac) */
	public static final int KEY_PAUSE           = 0xC5; /* Pause */
	public static final int KEY_HOME            = 0xC7; /* Home on arrow keypad */
	public static final int KEY_UP              = 0xC8; /* UpArrow on arrow keypad */
	public static final int KEY_PRIOR           = 0xC9; /* PgUp on arrow keypad */
	public static final int KEY_LEFT            = 0xCB; /* LeftArrow on arrow keypad */
	public static final int KEY_RIGHT           = 0xCD; /* RightArrow on arrow keypad */
	public static final int KEY_END             = 0xCF; /* End on arrow keypad */
	public static final int KEY_DOWN            = 0xD0; /* DownArrow on arrow keypad */
	public static final int KEY_NEXT            = 0xD1; /* PgDn on arrow keypad */
	public static final int KEY_INSERT          = 0xD2; /* Insert on arrow keypad */
	public static final int KEY_DELETE          = 0xD3; /* Delete on arrow keypad */
	public static final int KEY_CLEAR           = 0xDA; /* Clear key (Mac) */
	public static final int KEY_LMETA           = 0xDB; /* Left Windows/Option key */
	/**
	 * The left windows key, mapped to KEY_LMETA
	 *
	 * @deprecated Use KEY_LMETA instead
	 */
	public static final int KEY_LWIN            = KEY_LMETA; /* Left Windows key */
	public static final int KEY_RMETA            = 0xDC; /* Right Windows/Option key */
	/**
	 * The right windows key, mapped to KEY_RMETA
	 *
	 * @deprecated Use KEY_RMETA instead
	 */
	public static final int KEY_RWIN            = KEY_RMETA; /* Right Windows key */
	public static final int KEY_APPS            = 0xDD; /* AppMenu key */
	public static final int KEY_POWER           = 0xDE;
	public static final int KEY_SLEEP           = 0xDF;

	/*	public static final int STATE_ON							= 0;
        public static final int STATE_OFF						 = 1;
        public static final int STATE_UNKNOWN				 = 2;
    */
	public static final int KEYBOARD_SIZE = 256;

	/** Buffer size in events */
	private static final int BUFFER_SIZE = 50;

	/** Key names */
	private static final String[] keyName = new String[KEYBOARD_SIZE];
	private static final Map<String, Integer> keyMap = new HashMap<String, Integer>(253);
	private static int counter;

	static {
		// Use reflection to find out key names
		Field[] fields = Keyboard.class.getFields();
		try {
			for ( Field field : fields ) {
				if ( Modifier.isStatic(field.getModifiers())
						&& Modifier.isPublic(field.getModifiers())
						&& Modifier.isFinal(field.getModifiers())
						&& field.getType().equals(int.class)
						&& field.getName().startsWith("KEY_")
						&& !field.getName().endsWith("WIN") ) { /* Don't use deprecated names */

					int key = field.getInt(null);
					String name = field.getName().substring(4);
					keyName[key] = name;
					keyMap.put(name, key);
					counter++;
				}

			}
		} catch (Exception e) {
		}

	}

	/** The number of keys supported */
	private static final int keyCount = counter;

	/** Has the keyboard been created? */
	private static boolean created;

	/** Are repeat events enabled? */
	private static boolean repeat_enabled;

	/** The keys status from the last poll */
	private static final ByteBuffer keyDownBuffer = BufferUtils.createByteBuffer(KEYBOARD_SIZE);

	/**
	 * The key events from the last read: a sequence of pairs of key number,
	 * followed by state. The state is followed by
	 * a 4 byte code point representing the translated character.
	 */
	private static ByteBuffer readBuffer;

	/** current event */
	private static KeyEvent current_event = new KeyEvent();

	/** scratch event */
	private static KeyEvent tmp_event = new KeyEvent();

	/** One time initialization */
	private static boolean initialized;

	private static InputImplementation implementation;

	/**
	 * Keyboard cannot be constructed.
	 */
	private Keyboard() {
	}

	/**
	 * Static initialization
	 */
	private static void initialize() {
		if (initialized)
			return;
		Sys.initialize();
		initialized = true;
	}

	/**
	 * "Create" the keyboard with the given implementation. This is used
	 * reflectively from AWTInputAdapter.
	 *
	 * @throws LWJGLException if the keyboard could not be created for any reason
	 */
	private static void create(InputImplementation impl) throws LWJGLException {
		if (created)
			return;
		if (!initialized)
			initialize();
		implementation = impl;
		implementation.createKeyboard();
		created = true;
		readBuffer = ByteBuffer.allocate(EVENT_SIZE*BUFFER_SIZE);
		reset();
	}

	/**
	 * "Create" the keyboard. The display must first have been created. The
	 * reason for this is so the keyboard has a window to "focus" in.
	 *
	 * @throws LWJGLException if the keyboard could not be created for any reason
	 */
	public static void create() throws LWJGLException {
			if (!Display.isCreated()) throw new IllegalStateException("Display must be created.");

			create((InputImplementation) GLFWInputImplementation.singleton);
	}

	private static void reset() {
		readBuffer.limit(0);
		for (int i = 0; i < keyDownBuffer.remaining(); i++)
			keyDownBuffer.put(i, (byte)0);
		current_event.reset();
	}

	/**
	 * @return true if the keyboard has been created
	 */
	public static boolean isCreated() {
			return created;
	}

	/**
	 * "Destroy" the keyboard
	 */
	public static void destroy() {
			if (!created)
				return;
			created = false;
			implementation.destroyKeyboard();
			reset();
	}

	/**
	 * Polls the keyboard for its current state. Access the polled values using the
	 * <code>isKeyDown</code> method.
	 * By using this method, it is possible to "miss" keyboard keys if you don't
	 * poll fast enough.
	 *
	 * To use buffered values, you have to call <code>next</code> for each event you
	 * want to read. You can query which key caused the event by using
	 * <code>getEventKey</code>. To get the state of that key, for that event, use
	 * <code>getEventKeyState</code> - finally use <code>getEventCharacter</code> to get the
	 * character for that event.
	 *
	 * NOTE: This method does not query the operating system for new events. To do that,
	 * Display.processMessages() (or Display.update()) must be called first.
	 *
	 * @see org.lwjgl.input.Keyboard#isKeyDown(int key)
	 * @see org.lwjgl.input.Keyboard#next()
	 * @see org.lwjgl.input.Keyboard#getEventKey()
	 * @see org.lwjgl.input.Keyboard#getEventKeyState()
	 * @see org.lwjgl.input.Keyboard#getEventCharacter()
	 */
	public static void poll() {
			if (!created)
				throw new IllegalStateException("Keyboard must be created before you can poll the device");
			implementation.pollKeyboard(keyDownBuffer);
			read();
	}

	private static void read() {
		readBuffer.compact();
		implementation.readKeyboard(readBuffer);
		readBuffer.flip();
	}

	/**
	 * Checks to see if a key is down.
	 * @param key Keycode to check
	 * @return true if the key is down according to the last poll()
	 */
	public static boolean isKeyDown(int key) {
			if (!created)
				throw new IllegalStateException("Keyboard must be created before you can query key state");
			return keyDownBuffer.get(key) != 0;
	}

	/**
	 * Checks whether one of the state keys are "active"
	 *
	 * @param key State key to test (KEY_CAPITAL | KEY_NUMLOCK | KEY_SYSRQ)
	 * @return STATE_ON if on, STATE_OFF if off and STATE_UNKNOWN if the state is unknown
	 */
/*	public static int isStateKeySet(int key) {
		if (!created)
			throw new IllegalStateException("Keyboard must be created before you can query key state");
		return implementation.isStateKeySet(key);
	}
*/
	/**
	 * Gets a key's name
	 * @param key The key
	 * @return a String with the key's human readable name in it or null if the key is unnamed
	 */
	public static synchronized String getKeyName(int key) {
		return keyName[key];
	}

	/**
	 * Get's a key's index. If the key is unrecognised then KEY_NONE is returned.
	 * @param keyName The key name
	 */
	public static synchronized int getKeyIndex(String keyName) {
		Integer ret = keyMap.get(keyName);
		if (ret == null)
			return KEY_NONE;
		else
			return ret;
	}

	/**
	 * Gets the number of keyboard events waiting after doing a buffer enabled poll().
	 * @return the number of keyboard events
	 */
	public static int getNumKeyboardEvents() {
			if (!created)
				throw new IllegalStateException("Keyboard must be created before you can read events");
			int old_position = readBuffer.position();
			int num_events = 0;
			while (readNext(tmp_event) && (!tmp_event.repeat || repeat_enabled))
				num_events++;
			readBuffer.position(old_position);
			return num_events;
	}

	/**
	 * Gets the next keyboard event. You can query which key caused the event by using
	 * <code>getEventKey</code>. To get the state of that key, for that event, use
	 * <code>getEventKeyState</code> - finally use <code>getEventCharacter</code> to get the
	 * character for that event.
	 *
	 * @see org.lwjgl.input.Keyboard#getEventKey()
	 * @see org.lwjgl.input.Keyboard#getEventKeyState()
	 * @see org.lwjgl.input.Keyboard#getEventCharacter()
	 * @return true if a keyboard event was read, false otherwise
	 */
	public static boolean next() {
			if (!created)
				throw new IllegalStateException("Keyboard must be created before you can read events");

			boolean result;
			while ((result = readNext(current_event)) && current_event.repeat && !repeat_enabled)
				;
			return result;
	}

	/**
	 * Controls whether repeat events are reported or not. If repeat events
	 * are enabled, key down events are reported when a key is pressed and held for
	 * a OS dependent amount of time. To distinguish a repeat event from a normal event,
	 * use isRepeatEvent().
	 *
	 * @see org.lwjgl.input.Keyboard#getEventKey()
	 */
	public static void enableRepeatEvents(boolean enable) {
			repeat_enabled = enable;
	}

	/**
	 * Check whether repeat events are currently reported or not.
	 *
	 * @return true is repeat events are reported, false if not.
	 * @see org.lwjgl.input.Keyboard#getEventKey()
	 */
	public static boolean areRepeatEventsEnabled() {
			return repeat_enabled;
	}

	private static boolean readNext(KeyEvent event) {
		if (readBuffer.hasRemaining()) {
			event.key = readBuffer.getInt() & 0xFF;
			event.state = readBuffer.get() != 0;
			event.character = readBuffer.getInt();
			event.nanos = readBuffer.getLong();
			event.repeat = readBuffer.get() == 1;
			return true;
		} else
			return false;
	}

	/**
	 * @return Number of keys on this keyboard
	 */
	public static int getKeyCount() {
		return keyCount;
	}

	/**
	 * @return The character from the current event
	 */
	public static char getEventCharacter() {
			return (char)current_event.character;
	}

	/**
	 * Please note that the key code returned is NOT valid against the
	 * current keyboard layout. To get the actual character pressed call
	 * getEventCharacter
	 *
	 * @return The key from the current event
	 */
	public static int getEventKey() {
			return current_event.key;
	}

	/**
	 * Gets the state of the key that generated the
	 * current event
	 *
	 * @return True if key was down, or false if released
	 */
	public static boolean getEventKeyState() {
			return current_event.state;
	}

	/**
	 * Gets the time in nanoseconds of the current event.
	 * Only useful for relative comparisons with other
	 * Keyboard events, as the absolute time has no defined
	 * origin.
	 * @return The time in nanoseconds of the current event
	 */
	public static long getEventNanoseconds() {
			return current_event.nanos;
	}

	/**
	 * @see org.lwjgl.input.Keyboard#enableRepeatEvents(boolean)
	 * @return true if the current event is a repeat event, false if
	 * the current event is not a repeat even or if repeat events are disabled.
	 */
	public static boolean isRepeatEvent() {
			return current_event.repeat;
	}

	private static final class KeyEvent {
		/** The current keyboard character being examined */
		private int character;

		/** The current keyboard event key being examined */
		private int key;

		/** The current state of the key being examined in the event queue */
		private boolean state;

		/** The current event time */
		private long nanos;

		/** Is the current event a repeated event? */
		private boolean repeat;

		private void reset() {
			character = 0;
			key = 0;
			state = false;
			repeat = false;
		}
	}
}
