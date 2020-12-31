package org.lwjgl.input;

import org.lwjgl.glfw.GLFW;

public class KeyCodes {
	
	public static int toLwjglKey(int glfwKeyCode) {
		
		switch(glfwKeyCode) {
		
		case GLFW.GLFW_KEY_ESCAPE	: return Keyboard.KEY_ESCAPE;
		case GLFW.GLFW_KEY_BACKSPACE: return Keyboard.KEY_BACK;
		case GLFW.GLFW_KEY_TAB		: return Keyboard.KEY_TAB;
		case GLFW.GLFW_KEY_ENTER	: return Keyboard.KEY_RETURN;
		case GLFW.GLFW_KEY_SPACE	: return Keyboard.KEY_SPACE;
		
		case GLFW.GLFW_KEY_LEFT_CONTROL	: return Keyboard.KEY_LCONTROL;
		case GLFW.GLFW_KEY_LEFT_SHIFT	: return Keyboard.KEY_LSHIFT;
		case GLFW.GLFW_KEY_LEFT_ALT		: return Keyboard.KEY_LMENU;
		case GLFW.GLFW_KEY_LEFT_SUPER	: return Keyboard.KEY_LMETA;
		
		case GLFW.GLFW_KEY_RIGHT_CONTROL: return Keyboard.KEY_RCONTROL;
		case GLFW.GLFW_KEY_RIGHT_SHIFT	: return Keyboard.KEY_RSHIFT;
		case GLFW.GLFW_KEY_RIGHT_ALT	: return Keyboard.KEY_RMENU;
		case GLFW.GLFW_KEY_RIGHT_SUPER	: return Keyboard.KEY_RMETA;
		
		case GLFW.GLFW_KEY_1		: return Keyboard.KEY_1;
		case GLFW.GLFW_KEY_2		: return Keyboard.KEY_2;
		case GLFW.GLFW_KEY_3		: return Keyboard.KEY_3;
		case GLFW.GLFW_KEY_4		: return Keyboard.KEY_4;
		case GLFW.GLFW_KEY_5		: return Keyboard.KEY_5;
		case GLFW.GLFW_KEY_6		: return Keyboard.KEY_6;
		case GLFW.GLFW_KEY_7		: return Keyboard.KEY_7;
		case GLFW.GLFW_KEY_8		: return Keyboard.KEY_8;
		case GLFW.GLFW_KEY_9		: return Keyboard.KEY_9;
		case GLFW.GLFW_KEY_0		: return Keyboard.KEY_0;
		
		case GLFW.GLFW_KEY_A		: return Keyboard.KEY_A;
		case GLFW.GLFW_KEY_B		: return Keyboard.KEY_B;
		case GLFW.GLFW_KEY_C		: return Keyboard.KEY_C;
		case GLFW.GLFW_KEY_D		: return Keyboard.KEY_D;
		case GLFW.GLFW_KEY_E		: return Keyboard.KEY_E;
		case GLFW.GLFW_KEY_F		: return Keyboard.KEY_F;
		case GLFW.GLFW_KEY_G		: return Keyboard.KEY_G;
		case GLFW.GLFW_KEY_H		: return Keyboard.KEY_H;
		case GLFW.GLFW_KEY_I		: return Keyboard.KEY_I;
		case GLFW.GLFW_KEY_J		: return Keyboard.KEY_J;
		case GLFW.GLFW_KEY_K		: return Keyboard.KEY_K;
		case GLFW.GLFW_KEY_L		: return Keyboard.KEY_L;
		case GLFW.GLFW_KEY_M		: return Keyboard.KEY_M;
		case GLFW.GLFW_KEY_N		: return Keyboard.KEY_N;
		case GLFW.GLFW_KEY_O		: return Keyboard.KEY_O;
		case GLFW.GLFW_KEY_P		: return Keyboard.KEY_P;
		case GLFW.GLFW_KEY_Q		: return Keyboard.KEY_Q;
		case GLFW.GLFW_KEY_R		: return Keyboard.KEY_R;
		case GLFW.GLFW_KEY_S		: return Keyboard.KEY_S;
		case GLFW.GLFW_KEY_T		: return Keyboard.KEY_T;
		case GLFW.GLFW_KEY_U		: return Keyboard.KEY_U;
		case GLFW.GLFW_KEY_V		: return Keyboard.KEY_V;
		case GLFW.GLFW_KEY_W		: return Keyboard.KEY_W;
		case GLFW.GLFW_KEY_X		: return Keyboard.KEY_X;
		case GLFW.GLFW_KEY_Y		: return Keyboard.KEY_Y;
		case GLFW.GLFW_KEY_Z		: return Keyboard.KEY_Z;
		
		case GLFW.GLFW_KEY_UP		: return Keyboard.KEY_UP;
		case GLFW.GLFW_KEY_DOWN		: return Keyboard.KEY_DOWN;
		case GLFW.GLFW_KEY_LEFT		: return Keyboard.KEY_LEFT;
		case GLFW.GLFW_KEY_RIGHT	: return Keyboard.KEY_RIGHT;
		
		case GLFW.GLFW_KEY_INSERT	: return Keyboard.KEY_INSERT;
		case GLFW.GLFW_KEY_DELETE	: return Keyboard.KEY_DELETE;
		case GLFW.GLFW_KEY_HOME		: return Keyboard.KEY_HOME;
		case GLFW.GLFW_KEY_END		: return Keyboard.KEY_END;
		case GLFW.GLFW_KEY_PAGE_UP	: return Keyboard.KEY_PRIOR;
		case GLFW.GLFW_KEY_PAGE_DOWN: return Keyboard.KEY_NEXT;
		
		case GLFW.GLFW_KEY_F1		: return Keyboard.KEY_F1;
		case GLFW.GLFW_KEY_F2		: return Keyboard.KEY_F2;
		case GLFW.GLFW_KEY_F3		: return Keyboard.KEY_F3;
		case GLFW.GLFW_KEY_F4		: return Keyboard.KEY_F4;
		case GLFW.GLFW_KEY_F5		: return Keyboard.KEY_F5;
		case GLFW.GLFW_KEY_F6		: return Keyboard.KEY_F6;
		case GLFW.GLFW_KEY_F7		: return Keyboard.KEY_F7;
		case GLFW.GLFW_KEY_F8		: return Keyboard.KEY_F8;
		case GLFW.GLFW_KEY_F9		: return Keyboard.KEY_F9;
		case GLFW.GLFW_KEY_F10		: return Keyboard.KEY_F10;
		case GLFW.GLFW_KEY_F11		: return Keyboard.KEY_F11;
		case GLFW.GLFW_KEY_F12		: return Keyboard.KEY_F12;
		case GLFW.GLFW_KEY_F13		: return Keyboard.KEY_F13;
		case GLFW.GLFW_KEY_F14		: return Keyboard.KEY_F14;
		case GLFW.GLFW_KEY_F15		: return Keyboard.KEY_F15;
		case GLFW.GLFW_KEY_F16		: return Keyboard.KEY_F16;
		case GLFW.GLFW_KEY_F17		: return Keyboard.KEY_F17;
		case GLFW.GLFW_KEY_F18		: return Keyboard.KEY_F18;
		case GLFW.GLFW_KEY_F19		: return Keyboard.KEY_F19;
		
		case GLFW.GLFW_KEY_KP_1		: return Keyboard.KEY_NUMPAD1;
		case GLFW.GLFW_KEY_KP_2		: return Keyboard.KEY_NUMPAD2;
		case GLFW.GLFW_KEY_KP_3		: return Keyboard.KEY_NUMPAD3;
		case GLFW.GLFW_KEY_KP_4		: return Keyboard.KEY_NUMPAD4;
		case GLFW.GLFW_KEY_KP_5		: return Keyboard.KEY_NUMPAD5;
		case GLFW.GLFW_KEY_KP_6		: return Keyboard.KEY_NUMPAD6;
		case GLFW.GLFW_KEY_KP_7		: return Keyboard.KEY_NUMPAD7;
		case GLFW.GLFW_KEY_KP_8		: return Keyboard.KEY_NUMPAD8;
		case GLFW.GLFW_KEY_KP_9		: return Keyboard.KEY_NUMPAD9;
		case GLFW.GLFW_KEY_KP_0		: return Keyboard.KEY_NUMPAD0;
		
		case GLFW.GLFW_KEY_KP_ADD	: return Keyboard.KEY_ADD;
		case GLFW.GLFW_KEY_KP_SUBTRACT	: return Keyboard.KEY_SUBTRACT;
		case GLFW.GLFW_KEY_KP_MULTIPLY	: return Keyboard.KEY_MULTIPLY;
		case GLFW.GLFW_KEY_KP_DIVIDE: return Keyboard.KEY_DIVIDE;
		case GLFW.GLFW_KEY_KP_DECIMAL	: return Keyboard.KEY_DECIMAL;
		case GLFW.GLFW_KEY_KP_EQUAL	: return Keyboard.KEY_NUMPADEQUALS;
		case GLFW.GLFW_KEY_KP_ENTER	: return Keyboard.KEY_NUMPADENTER;
		case GLFW.GLFW_KEY_NUM_LOCK	: return Keyboard.KEY_NUMLOCK;
		
		case GLFW.GLFW_KEY_SEMICOLON: return Keyboard.KEY_SEMICOLON;
		case GLFW.GLFW_KEY_BACKSLASH: return Keyboard.KEY_BACKSLASH;
		case GLFW.GLFW_KEY_COMMA	: return Keyboard.KEY_COMMA;
		case GLFW.GLFW_KEY_PERIOD	: return Keyboard.KEY_PERIOD;
		case GLFW.GLFW_KEY_SLASH	: return Keyboard.KEY_SLASH;
		case GLFW.GLFW_KEY_GRAVE_ACCENT	: return Keyboard.KEY_GRAVE;

		case GLFW.GLFW_KEY_CAPS_LOCK: return Keyboard.KEY_CAPITAL;
		case GLFW.GLFW_KEY_SCROLL_LOCK	: return Keyboard.KEY_SCROLL;
		
		case GLFW.GLFW_KEY_WORLD_1	: return Keyboard.KEY_CIRCUMFLEX; // TODO not sure if correct
		case GLFW.GLFW_KEY_PAUSE	: return Keyboard.KEY_PAUSE;
		
		case GLFW.GLFW_KEY_MINUS 	: return Keyboard.KEY_MINUS;
		case GLFW.GLFW_KEY_EQUAL 	: return Keyboard.KEY_EQUALS;
		case GLFW.GLFW_KEY_LEFT_BRACKET	: return Keyboard.KEY_LBRACKET;
		case GLFW.GLFW_KEY_RIGHT_BRACKET: return Keyboard.KEY_RBRACKET;
		case GLFW.GLFW_KEY_APOSTROPHE	: return Keyboard.KEY_APOSTROPHE;
//		public static final int KEY_AT              = 0x91; /*                     (NEC PC98) */
//		public static final int KEY_COLON           = 0x92; /*                     (NEC PC98) */
//		public static final int KEY_UNDERLINE       = 0x93; /*                     (NEC PC98) */

//		public static final int KEY_KANA            = 0x70; /* (Japanese keyboard)            */
//		public static final int KEY_CONVERT         = 0x79; /* (Japanese keyboard)            */
//		public static final int KEY_NOCONVERT       = 0x7B; /* (Japanese keyboard)            */
//		public static final int KEY_YEN             = 0x7D; /* (Japanese keyboard)            */
//		public static final int KEY_CIRCUMFLEX      = 0x90; /* (Japanese keyboard)            */
//		public static final int KEY_KANJI           = 0x94; /* (Japanese keyboard)            */
//		public static final int KEY_STOP            = 0x95; /*                     (NEC PC98) */
//		public static final int KEY_AX              = 0x96; /*                     (Japan AX) */
//		public static final int KEY_UNLABELED       = 0x97; /*                        (J3100) */
//		public static final int KEY_SECTION         = 0xA7; /* Section symbol (Mac) */
//		public static final int KEY_NUMPADCOMMA     = 0xB3; /* , on numeric keypad (NEC PC98) */
//		public static final int KEY_SYSRQ           = 0xB7;
//		public static final int KEY_FUNCTION        = 0xC4; /* Function (Mac) */
//		public static final int KEY_CLEAR           = 0xDA; /* Clear key (Mac) */

//		public static final int KEY_APPS            = 0xDD; /* AppMenu key */
//		public static final int KEY_POWER           = 0xDE;
//		public static final int KEY_SLEEP           = 0xDF;

		default: 	System.out.println("UNKNOWN GLFW KEY CODE: " + glfwKeyCode);
					return Keyboard.KEY_NONE;
		}
	}
	
	public static int toGlfwKey(int lwjglKeyCode) {
		
		switch(lwjglKeyCode) {
		
		case Keyboard.KEY_ESCAPE	: return GLFW.GLFW_KEY_ESCAPE;
		case Keyboard.KEY_BACK		: return GLFW.GLFW_KEY_BACKSPACE;
		case Keyboard.KEY_TAB		: return GLFW.GLFW_KEY_TAB;
		case Keyboard.KEY_RETURN	: return GLFW.GLFW_KEY_ENTER;
		case Keyboard.KEY_SPACE		: return GLFW.GLFW_KEY_SPACE;
		
		case Keyboard.KEY_LCONTROL	: return GLFW.GLFW_KEY_LEFT_CONTROL;
		case Keyboard.KEY_LSHIFT	: return GLFW.GLFW_KEY_LEFT_SHIFT;
		case Keyboard.KEY_LMENU		: return GLFW.GLFW_KEY_LEFT_ALT;
		case Keyboard.KEY_LMETA		: return GLFW.GLFW_KEY_LEFT_SUPER;
		
		case Keyboard.KEY_RCONTROL	: return GLFW.GLFW_KEY_RIGHT_CONTROL;
		case Keyboard.KEY_RSHIFT	: return GLFW.GLFW_KEY_RIGHT_SHIFT;
		case Keyboard.KEY_RMENU		: return GLFW.GLFW_KEY_RIGHT_ALT;
		case Keyboard.KEY_RMETA		: return GLFW.GLFW_KEY_RIGHT_SUPER;
		
		case Keyboard.KEY_1			: return GLFW.GLFW_KEY_1;
		case Keyboard.KEY_2			: return GLFW.GLFW_KEY_2;
		case Keyboard.KEY_3			: return GLFW.GLFW_KEY_3;
		case Keyboard.KEY_4			: return GLFW.GLFW_KEY_4;
		case Keyboard.KEY_5			: return GLFW.GLFW_KEY_5;
		case Keyboard.KEY_6			: return GLFW.GLFW_KEY_6;
		case Keyboard.KEY_7			: return GLFW.GLFW_KEY_7;
		case Keyboard.KEY_8			: return GLFW.GLFW_KEY_8;
		case Keyboard.KEY_9			: return GLFW.GLFW_KEY_9;
		case Keyboard.KEY_0			: return GLFW.GLFW_KEY_0;
		
		case Keyboard.KEY_A			: return GLFW.GLFW_KEY_A;
		case Keyboard.KEY_B			: return GLFW.GLFW_KEY_B;
		case Keyboard.KEY_C			: return GLFW.GLFW_KEY_C;
		case Keyboard.KEY_D			: return GLFW.GLFW_KEY_D;
		case Keyboard.KEY_E			: return GLFW.GLFW_KEY_E;
		case Keyboard.KEY_F			: return GLFW.GLFW_KEY_F;
		case Keyboard.KEY_G			: return GLFW.GLFW_KEY_G;
		case Keyboard.KEY_H			: return GLFW.GLFW_KEY_H;
		case Keyboard.KEY_I			: return GLFW.GLFW_KEY_I;
		case Keyboard.KEY_J			: return GLFW.GLFW_KEY_J;
		case Keyboard.KEY_K			: return GLFW.GLFW_KEY_K;
		case Keyboard.KEY_L			: return GLFW.GLFW_KEY_L;
		case Keyboard.KEY_M			: return GLFW.GLFW_KEY_M;
		case Keyboard.KEY_N			: return GLFW.GLFW_KEY_N;
		case Keyboard.KEY_O			: return GLFW.GLFW_KEY_O;
		case Keyboard.KEY_P			: return GLFW.GLFW_KEY_P;
		case Keyboard.KEY_Q			: return GLFW.GLFW_KEY_Q;
		case Keyboard.KEY_R			: return GLFW.GLFW_KEY_R;
		case Keyboard.KEY_S			: return GLFW.GLFW_KEY_S;
		case Keyboard.KEY_T			: return GLFW.GLFW_KEY_T;
		case Keyboard.KEY_U			: return GLFW.GLFW_KEY_U;
		case Keyboard.KEY_V			: return GLFW.GLFW_KEY_V;
		case Keyboard.KEY_W			: return GLFW.GLFW_KEY_W;
		case Keyboard.KEY_X			: return GLFW.GLFW_KEY_X;
		case Keyboard.KEY_Y			: return GLFW.GLFW_KEY_Y;
		case Keyboard.KEY_Z			: return GLFW.GLFW_KEY_Z;
		
		case Keyboard.KEY_UP		: return GLFW.GLFW_KEY_UP;
		case Keyboard.KEY_DOWN		: return GLFW.GLFW_KEY_DOWN;
		case Keyboard.KEY_LEFT		: return GLFW.GLFW_KEY_LEFT;
		case Keyboard.KEY_RIGHT		: return GLFW.GLFW_KEY_RIGHT;
		
		case Keyboard.KEY_INSERT	: return GLFW.GLFW_KEY_INSERT;
		case Keyboard.KEY_DELETE	: return GLFW.GLFW_KEY_DELETE;
		case Keyboard.KEY_HOME		: return GLFW.GLFW_KEY_HOME;
		case Keyboard.KEY_END		: return GLFW.GLFW_KEY_END;
		case Keyboard.KEY_PRIOR		: return GLFW.GLFW_KEY_PAGE_UP;
		case Keyboard.KEY_NEXT		: return GLFW.GLFW_KEY_PAGE_DOWN;
		
		case Keyboard.KEY_F1		: return GLFW.GLFW_KEY_F1;
		case Keyboard.KEY_F2		: return GLFW.GLFW_KEY_F2;
		case Keyboard.KEY_F3		: return GLFW.GLFW_KEY_F3;
		case Keyboard.KEY_F4		: return GLFW.GLFW_KEY_F4;
		case Keyboard.KEY_F5		: return GLFW.GLFW_KEY_F5;
		case Keyboard.KEY_F6		: return GLFW.GLFW_KEY_F6;
		case Keyboard.KEY_F7		: return GLFW.GLFW_KEY_F7;
		case Keyboard.KEY_F8		: return GLFW.GLFW_KEY_F8;
		case Keyboard.KEY_F9		: return GLFW.GLFW_KEY_F9;
		case Keyboard.KEY_F10		: return GLFW.GLFW_KEY_F10;
		case Keyboard.KEY_F11		: return GLFW.GLFW_KEY_F11;
		case Keyboard.KEY_F12		: return GLFW.GLFW_KEY_F12;
		case Keyboard.KEY_F13		: return GLFW.GLFW_KEY_F13;
		case Keyboard.KEY_F14		: return GLFW.GLFW_KEY_F14;
		case Keyboard.KEY_F15		: return GLFW.GLFW_KEY_F15;
		case Keyboard.KEY_F16		: return GLFW.GLFW_KEY_F16;
		case Keyboard.KEY_F17		: return GLFW.GLFW_KEY_F17;
		case Keyboard.KEY_F18		: return GLFW.GLFW_KEY_F18;
		case Keyboard.KEY_F19		: return GLFW.GLFW_KEY_F19;
		
		case Keyboard.KEY_NUMPAD1	: return GLFW.GLFW_KEY_KP_1;
		case Keyboard.KEY_NUMPAD2	: return GLFW.GLFW_KEY_KP_2;
		case Keyboard.KEY_NUMPAD3	: return GLFW.GLFW_KEY_KP_3;
		case Keyboard.KEY_NUMPAD4	: return GLFW.GLFW_KEY_KP_4;
		case Keyboard.KEY_NUMPAD5	: return GLFW.GLFW_KEY_KP_5;
		case Keyboard.KEY_NUMPAD6	: return GLFW.GLFW_KEY_KP_6;
		case Keyboard.KEY_NUMPAD7	: return GLFW.GLFW_KEY_KP_7;
		case Keyboard.KEY_NUMPAD8	: return GLFW.GLFW_KEY_KP_8;
		case Keyboard.KEY_NUMPAD9	: return GLFW.GLFW_KEY_KP_9;
		case Keyboard.KEY_NUMPAD0	: return GLFW.GLFW_KEY_KP_0;
		
		case Keyboard.KEY_ADD		: return GLFW.GLFW_KEY_KP_ADD;
		case Keyboard.KEY_SUBTRACT	: return GLFW.GLFW_KEY_KP_SUBTRACT;
		case Keyboard.KEY_MULTIPLY	: return GLFW.GLFW_KEY_KP_MULTIPLY;
		case Keyboard.KEY_DIVIDE	: return GLFW.GLFW_KEY_KP_DIVIDE;
		case Keyboard.KEY_DECIMAL	: return GLFW.GLFW_KEY_KP_DECIMAL;
		case Keyboard.KEY_NUMPADEQUALS	: return GLFW.GLFW_KEY_KP_EQUAL;
		case Keyboard.KEY_NUMPADENTER	: return GLFW.GLFW_KEY_KP_ENTER;
		case Keyboard.KEY_NUMLOCK	: return GLFW.GLFW_KEY_NUM_LOCK;
		
		case Keyboard.KEY_SEMICOLON	: return GLFW.GLFW_KEY_SEMICOLON;
		case Keyboard.KEY_BACKSLASH	: return GLFW.GLFW_KEY_BACKSLASH;
		case Keyboard.KEY_COMMA		: return GLFW.GLFW_KEY_COMMA;
		case Keyboard.KEY_PERIOD	: return GLFW.GLFW_KEY_PERIOD;
		case Keyboard.KEY_SLASH		: return GLFW.GLFW_KEY_SLASH;
		case Keyboard.KEY_GRAVE		: return GLFW.GLFW_KEY_GRAVE_ACCENT;

		case Keyboard.KEY_CAPITAL	: return GLFW.GLFW_KEY_CAPS_LOCK;
		case Keyboard.KEY_SCROLL	: return GLFW.GLFW_KEY_SCROLL_LOCK;
		
		case Keyboard.KEY_PAUSE		: return GLFW.GLFW_KEY_PAUSE;
		case Keyboard.KEY_CIRCUMFLEX: return GLFW.GLFW_KEY_WORLD_1; // TODO not sure if correct

		case Keyboard.KEY_MINUS		: return GLFW.GLFW_KEY_MINUS;
		case Keyboard.KEY_EQUALS	: return GLFW.GLFW_KEY_EQUAL;
		case Keyboard.KEY_LBRACKET	: return GLFW.GLFW_KEY_LEFT_BRACKET;
		case Keyboard.KEY_RBRACKET	: return GLFW.GLFW_KEY_RIGHT_BRACKET;
		case Keyboard.KEY_APOSTROPHE: return GLFW.GLFW_KEY_APOSTROPHE;
//		public static final int KEY_AT              = 0x91; /*                     (NEC PC98) */
//		public static final int KEY_COLON           = 0x92; /*                     (NEC PC98) */
//		public static final int KEY_UNDERLINE       = 0x93; /*                     (NEC PC98) */

//		public static final int KEY_KANA            = 0x70; /* (Japanese keyboard)            */
//		public static final int KEY_CONVERT         = 0x79; /* (Japanese keyboard)            */
//		public static final int KEY_NOCONVERT       = 0x7B; /* (Japanese keyboard)            */
//		public static final int KEY_YEN             = 0x7D; /* (Japanese keyboard)            */
		
//		public static final int KEY_CIRCUMFLEX      = 0x90; /* (Japanese keyboard)            */
//		public static final int KEY_KANJI           = 0x94; /* (Japanese keyboard)            */
//		public static final int KEY_STOP            = 0x95; /*                     (NEC PC98) */
//		public static final int KEY_AX              = 0x96; /*                     (Japan AX) */
//		public static final int KEY_UNLABELED       = 0x97; /*                        (J3100) */
//		public static final int KEY_SECTION         = 0xA7; /* Section symbol (Mac) */
//		public static final int KEY_NUMPADCOMMA     = 0xB3; /* , on numeric keypad (NEC PC98) */
//		public static final int KEY_SYSRQ           = 0xB7;
//		public static final int KEY_FUNCTION        = 0xC4; /* Function (Mac) */
		
//		public static final int KEY_CLEAR           = 0xDA; /* Clear key (Mac) */

//		public static final int KEY_APPS            = 0xDD; /* AppMenu key */
//		public static final int KEY_POWER           = 0xDE;
//		public static final int KEY_SLEEP           = 0xDF;

		default: 	System.out.println("UNKNOWN LWJGL KEY CODE: " + lwjglKeyCode);
					return GLFW.GLFW_KEY_UNKNOWN;
		}
	}

}
