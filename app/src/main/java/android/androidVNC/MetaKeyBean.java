/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package android.androidVNC;

import android.util.*;
import android.view.*;
import com.antlersoft.android.dbimpl.*;
import java.util.*;

/**
 * @author Michael A. MacDonald
 *
 */
public class MetaKeyBean extends AbstractMetaKeyBean implements Comparable<MetaKeyBean> {
	static ArrayList<MetaKeyBase> allKeys;
	static String[] allKeysNames;
	public static ArrayMap<Integer,MetaKeyBase> keysByKeyCode;
	static ArrayMap<Integer,MetaKeyBase> keysByMouseButton;
	static MetaKeyBean keyCtrlAltDel;
	static MetaKeyBean keyArrowLeft;
	static MetaKeyBean keyArrowRight;
	static MetaKeyBean keyArrowUp;
	static MetaKeyBean keyArrowDown;
	
	static NewInstance<MetaKeyBean> NEW;
	
	static {
		initStatic();
	}
	
	public static void initStatic() {
		allKeys = new ArrayList<MetaKeyBase>();

		allKeys.add(new MetaKeyBase("Hangul", 0xff31));
		allKeys.add(new MetaKeyBase("Hangul_Start", 0xff32));
		allKeys.add(new MetaKeyBase("Hangul_End", 0xff33));
		allKeys.add(new MetaKeyBase("Hangul_Hanja", 0xff34));
		allKeys.add(new MetaKeyBase("Kana_Shift", 0xff2e, KeyEvent.KEYCODE_KANA));
		allKeys.add(new MetaKeyBase("Right_Alt", 0xffea, KeyEvent.KEYCODE_ALT_RIGHT));
		
		allKeys.add(new MetaKeyBase(VncCanvas.MOUSE_BUTTON_LEFT,"Mouse Left"));
		allKeys.add(new MetaKeyBase(VncCanvas.MOUSE_BUTTON_MIDDLE,"Mouse Middle"));
		allKeys.add(new MetaKeyBase(VncCanvas.MOUSE_BUTTON_RIGHT,"Mouse Right"));
		allKeys.add(new MetaKeyBase(VncCanvas.MOUSE_BUTTON_SCROLL_DOWN, "Mouse Scroll Down"));
		allKeys.add(new MetaKeyBase(VncCanvas.MOUSE_BUTTON_SCROLL_UP, "Mouse Scroll Up"));
		
		allKeys.add(new MetaKeyBase("Home", 0xFF50, KeyEvent.KEYCODE_HOME));
		allKeys.add(new MetaKeyBase("Arrow Left", 0xFF51, KeyEvent.KEYCODE_DPAD_LEFT));
		allKeys.add(new MetaKeyBase("Arrow Up", 0xFF52, KeyEvent.KEYCODE_DPAD_UP));
		allKeys.add(new MetaKeyBase("Arrow Right", 0xFF53, KeyEvent.KEYCODE_DPAD_RIGHT));
		allKeys.add(new MetaKeyBase("Arrow Down", 0xFF54, KeyEvent.KEYCODE_DPAD_DOWN));
		allKeys.add(new MetaKeyBase("Page Up", 0xFF55, KeyEvent.KEYCODE_PAGE_UP));
		allKeys.add(new MetaKeyBase("Page Down", 0xFF56, KeyEvent.KEYCODE_PAGE_DOWN));
		allKeys.add(new MetaKeyBase("End", 0xFF57 /* ??? HELPME */));
		allKeys.add(new MetaKeyBase("Insert", 0xFF63, KeyEvent.KEYCODE_INSERT));
		allKeys.add(new MetaKeyBase("Delete", 0xFFFF, KeyEvent.KEYCODE_DEL));
		allKeys.add(new MetaKeyBase("Num Lock", 0xFF7F, KeyEvent.KEYCODE_NUM_LOCK));
		allKeys.add(new MetaKeyBase("Break", 0xFF6b, KeyEvent.KEYCODE_BREAK));
		allKeys.add(new MetaKeyBase("Scroll Lock", 0xFF14, KeyEvent.KEYCODE_SCROLL_LOCK));
		allKeys.add(new MetaKeyBase("Print Scrn", 0xFF15 /* ??? HELPME */));
		allKeys.add(new MetaKeyBase("Escape", 0xFF1B, KeyEvent.KEYCODE_ESCAPE));
		allKeys.add(new MetaKeyBase("Enter", 0xFF0D, KeyEvent.KEYCODE_ENTER));
		allKeys.add(new MetaKeyBase("Tab", 0xFF09, KeyEvent.KEYCODE_TAB));
		allKeys.add(new MetaKeyBase("BackSpace", 0xFF08, KeyEvent.KEYCODE_ESCAPE));
		allKeys.add(new MetaKeyBase("Space", 0x020, KeyEvent.KEYCODE_SPACE));
		
		StringBuilder sb = new StringBuilder(" ");
		for (int i=0; i<26; i++)
		{
			sb.setCharAt(0, (char)('A' + i));
			allKeys.add(new MetaKeyBase(sb.toString(), 'a' + i, KeyEvent.KEYCODE_A + i));
		}
		
		for (int i=0; i<10; i++)
		{
			sb.setCharAt(0, (char)('0' + i));
			allKeys.add(new MetaKeyBase(sb.toString(), '0' + i, KeyEvent.KEYCODE_0 + i));
		}
		
		for (int i=0; i<12; i++)
		{
			sb.setLength(0);
			sb.append('F');
			if (i<9)
				sb.append(' ');
			sb.append(Integer.toString(i+1));
			allKeys.add(new MetaKeyBase(sb.toString(), 0xFFBE + i, KeyEvent.KEYCODE_F1 + i));
		}
		
		java.util.Collections.sort(allKeys);
		allKeysNames = new String[allKeys.size()];
		keysByKeyCode = new ArrayMap<Integer,MetaKeyBase>();
		keysByMouseButton = new ArrayMap<Integer,MetaKeyBase>();
		for (int i=0; i<allKeysNames.length; ++i)
		{
			MetaKeyBase b=allKeys.get(i);
			allKeysNames[i] = b.name;
			if (b.isKeyEvent)
				keysByKeyCode.put(b.keyEvent,b);
			if (b.isMouse)
				keysByMouseButton.put(b.mouseButtons,b);
			// else
				// keysByKeySym.put(b.keySym,b);
		}
		NEW = new NewInstance<MetaKeyBean>() {

				/* (non-Javadoc)
				 * @see com.antlersoft.android.dbimpl.NewInstance#get()
				 */
				@Override
				public MetaKeyBean get() {
					return new MetaKeyBean();
				}
			};
		keyCtrlAltDel = new MetaKeyBean(0,VncCanvas.CTRL_MASK|VncCanvas.ALT_MASK,keysByKeyCode.get(KeyEvent.KEYCODE_DEL));
		keyArrowLeft = new MetaKeyBean(0,0,keysByKeyCode.get(KeyEvent.KEYCODE_DPAD_LEFT));
		keyArrowUp = new MetaKeyBean(0,0,keysByKeyCode.get(KeyEvent.KEYCODE_DPAD_UP));
		keyArrowRight = new MetaKeyBean(0,0,keysByKeyCode.get(KeyEvent.KEYCODE_DPAD_RIGHT));
		keyArrowDown = new MetaKeyBean(0,0,keysByKeyCode.get(KeyEvent.KEYCODE_DPAD_DOWN));
	}
	
	private boolean _regenDesc;
	
	MetaKeyBean()
	{
	}
	
	MetaKeyBean(MetaKeyBean toCopy)
	{
		_regenDesc = true;
		if (toCopy.isMouseClick())
			setMouseButtons(toCopy.getMouseButtons());
		else {
			setKeyEvent(toCopy.getKeyEvent());
			setKeySym(toCopy.getKeySym());
		}
		setMetaListId(toCopy.getMetaListId());
		setMetaFlags(toCopy.getMetaFlags());
	}
	
	MetaKeyBean(long listId, int metaFlags, MetaKeyBase base)
	{
		setMetaListId(listId);
		setKeyBase(base);
		setMetaFlags(metaFlags);
		_regenDesc = true;
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractMetaKeyBean#getKeyDesc()
	 */
	@Override
	public String getKeyDesc() {
		if (_regenDesc)
		{
			synchronized(this)
			{
				if (_regenDesc)
				{
					StringBuilder sb=new StringBuilder();
					int meta=getMetaFlags();
					if (0 != (meta & VncCanvas.SHIFT_MASK))
					{
						sb.append("Shift");
					}
					if (0 != (meta & VncCanvas.CTRL_MASK))
					{
						if (sb.length()>0)
							sb.append('-');
						sb.append("Ctrl");
					}
					if (0 != (meta & VncCanvas.ALT_MASK))
					{
						if (sb.length()>0)
							sb.append('-');
						sb.append("Alt");
					}
					if (sb.length()>0)
						sb.append(' ');
					MetaKeyBase base;
					if (isMouseClick())
						base=keysByMouseButton.get(getMouseButtons());
					else
						base=keysByKeyCode.get(getKeyEvent());
					sb.append(base.name);
					setKeyDesc(sb.toString());
				}
			}
		}
		return super.getKeyDesc();
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractMetaKeyBean#setKeyDesc(java.lang.String)
	 */
	@Override
	public void setKeyDesc(String arg_keyDesc) {
		super.setKeyDesc(arg_keyDesc);
		_regenDesc = false;
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractMetaKeyBean#setKeySym(int)
	 */
	@Override
	public void setKeyEvent(int arg_keyEvent) {
		if (arg_keyEvent!=getKeyEvent() || isMouseClick())
		{
			setMouseClick(false);
			_regenDesc=true;
			super.setKeyEvent(arg_keyEvent);
		}
	}
	
	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractMetaKeyBean#setKeySym(int)
	 */
	@Override
	public void setKeySym(int arg_keySym) {
		if (arg_keySym!=getKeySym() || isMouseClick())
		{
			setMouseClick(false);
			_regenDesc=true;
			super.setKeySym(arg_keySym);
		}
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractMetaKeyBean#setMetaFlags(int)
	 */
	@Override
	public void setMetaFlags(int arg_metaFlags) {
		if (arg_metaFlags != getMetaFlags())
		{
			_regenDesc = true;
			super.setMetaFlags(arg_metaFlags);
		}
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractMetaKeyBean#setMouseButtons(int)
	 */
	@Override
	public void setMouseButtons(int arg_mouseButtons) {
		if (arg_mouseButtons!=getMouseButtons() || ! isMouseClick())
		{
			setMouseClick(true);
			_regenDesc = true;
			super.setMouseButtons(arg_mouseButtons);
		}
	}
	
	void setKeyBase(MetaKeyBase base)
	{
		if (base.isMouse) {
			setMouseButtons(base.mouseButtons);
		} else {
			setKeyEvent(base.keyEvent);
			setKeySym(base.keySym);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof MetaKeyBean)
		{
			return getKeyDesc().equals(((MetaKeyBean)o).getKeyDesc());
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MetaKeyBean another) {
		return getKeyDesc().compareTo(another.getKeyDesc());
	}
}
