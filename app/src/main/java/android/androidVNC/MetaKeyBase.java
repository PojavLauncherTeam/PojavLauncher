/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package android.androidVNC;

/**
 * @author Michael A. MacDonald
 *
 */
class MetaKeyBase implements Comparable<MetaKeyBase> {
	int keySym;
	int mouseButtons;
	int keyEvent;
	String name;
	boolean isMouse;
	boolean isKeyEvent;
	
	MetaKeyBase(int mouseButtons, String name)
	{
		this.mouseButtons = mouseButtons;
		this.name = name;
		this.isMouse = true;
		this.isKeyEvent = false;
	}
	
	MetaKeyBase(String name, int keySym, int keyEvent)
	{
		this.name = name;
		this.keySym = keySym;
		this.keyEvent = keyEvent;
		this.isMouse = false;
		this.isKeyEvent = true;
	}
	
	MetaKeyBase(String name, int keySym)
	{
		this.name = name;
		this.keySym = keySym;
		this.isMouse = false;
		this.isKeyEvent = false;		
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MetaKeyBase another) {
		return name.compareTo(another.name);
	}
}
