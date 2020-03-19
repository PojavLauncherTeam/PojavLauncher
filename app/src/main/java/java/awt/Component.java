package java.awt;
import java.beans.*;
import org.apache.harmony.awt.wtk.*;

public class Component {
	
    int x;
    int y;
    int w = 0;
    int h = 0;
	
	protected boolean enabled = true;
    private boolean inputMethodsEnabled = true;
    transient boolean dispatchToIM = true;
    private boolean focusable = true; // By default, all Components return
    boolean visible = true;
    private boolean wasShowing;
    private boolean wasDisplayable;
    private boolean valid;
	private Dimension defaultMinimumSize;
	private ComponentOrientation orientation;
	private PropertyChangeSupport propertyChangeSupport;
	private Dimension maximumSize;
    private Dimension minimumSize;
	private Dimension preferredSize;
    private Color backColor = Color.WHITE;
    private Color foreColor = Color.BLACK;
	
	public Container parent = null;

	private int boundsMaskParam = 0;
	private Cursor cursor;

	private Font font;

	private boolean calledSetFocusable;
	
    public Cursor getCursor() {
        if (cursor != null) {
			return cursor;
			// ???AWT
		} else if (parent != null) {
			return parent.getCursor();
		}
		return Cursor.getDefaultCursor();
    }
	
	public void setCursor(Cursor cursor) {
        this.cursor = cursor;
		setCursor();
    }

    void setCursor() {
        if (isDisplayable()) { // && isShowing()) {
            // Rectangle absRect = new Rectangle(getLocationOnScreen(), getSize());
            //Point absPointerPos = toolkit.dispatcher.mouseDispatcher.getPointerPos();
            // ???AWT
            /*
             * if (absRect.contains(absPointerPos)) { // set Cursor only on
             * top-level Windows(on X11) Window topLevelWnd =
             * getWindowAncestor(); if (topLevelWnd != null) { Point pointerPos
             * = MouseDispatcher.convertPoint(null, absPointerPos, topLevelWnd);
             * Component compUnderCursor =
             * topLevelWnd.findComponentAt(pointerPos); // if (compUnderCursor
             * == this || // compUnderCursor.getCursorAncestor() == this) {
             * NativeWindow wnd = topLevelWnd.getNativeWindow(); if
             * (compUnderCursor != null && wnd != null) {
             * compUnderCursor.getRealCursor().getNativeCursor()
             * .setCursor(wnd.getId()); } // } } }
             */
        }
    }
	
	public void setFocusable(boolean focusable) { boolean oldFocusable;
		calledSetFocusable = true;
		oldFocusable = this.focusable;
		this.focusable = focusable;
		if (!focusable) {
			moveFocus();
		}
		firePropertyChange("focusable", oldFocusable, focusable); //$NON-NLS-1$
	}
	
	void moveFocus() {
		// Fake method
	}
	
	
	public void setFont(Font f) {
        Font oldFont;
        oldFont = font;
		setFontImpl(f);
        firePropertyChange("font", oldFont, font); //$NON-NLS-1$
    }

    void setFontImpl(Font f) {
        font = f;
        invalidate();
		/*
        if (isShowing()) {
            repaint();
        }
		*/
    }
	
	public int getBaseline(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException(
				"Width and height must be >= 0");
        }
        return -1;
    }
	
	public void setLocation(Point p) {
        setLocation(p.x, p.y);
    }

    public void setLocation(int x, int y) {
        move(x, y);
    }
	
	public void move(int x, int y) {
        boundsMaskParam = NativeWindow.BOUNDS_NOSIZE;
		setBounds(x, y, w, h);
    }
	
	public void repaint() {
		// Android implementation, don't need to paint.
	}

    public void validate() {
        validateImpl();
    }

    void validateImpl() {
        valid = true;
    }

    public void addNotify() {
		/*
        toolkit.lockAWT();
        try {
            prepare4HierarchyChange();
            //behaviour.addNotify();
            // ???AWT
            // finishHierarchyChange(this, parent, 0);
            // if (dropTarget != null) {
            // dropTarget.addNotify(peer);
            // }
        } finally {
            toolkit.unlockAWT();
        }
		*/
    }
	
	Insets getNativeInsets() {
        return new Insets(0, 0, 0, 0);
    }

    Insets getInsets() {
        return new Insets(0, 0, 0, 0);
    }
    
    Color getDefaultBackground() {
        // ???AWT: return getWindowAncestor().getDefaultBackground();
        return getBackground();
    }

    Color getDefaultForeground() {
        // ???AWT return getWindowAncestor().getDefaultForeground();
        return getForeground();
    }
	
	public Color getBackground() {
		if (backColor == null && parent != null) {
			return parent.getBackground();
		}

		return backColor;
	}
	
	public Color getForeground() {
        if (foreColor == null && parent != null) {
			return parent.getForeground();
		}

		return foreColor;
    }
	
	public boolean isBackgroundSet() {
		return backColor != null;
	}

	public Color getTextColor() {
		Color c = getForeground();
		return (c != null) ? c : getDefaultForeground();
	}

    public void setForeground(Color c) {
        Color oldFgColor;
        oldFgColor = foreColor;
		foreColor = c;
        firePropertyChange("foreground", oldFgColor, foreColor); //$NON-NLS-1$
        repaint();
    }

    public void setBackground(Color c) {
        Color oldBkColor;
        oldBkColor = backColor;
		backColor = c;
        firePropertyChange("background", oldBkColor, backColor); //$NON-NLS-1$
        repaint();
    }
	
    public boolean isMaximumSizeSet() {
        return maximumSize != null;
    }

    public boolean isMinimumSizeSet() {
		return minimumSize != null;
    }

    public boolean isPreferredSizeSet() {
        return preferredSize != null;
    }

    public Dimension getMaximumSize() {
        return isMaximumSizeSet() ? new Dimension(maximumSize) : new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
    }

    public Dimension getMinimumSize() {
        return minimumSize();
    }

    @Deprecated
    public Dimension minimumSize() {
        if (isMinimumSizeSet()) {
			return (Dimension)minimumSize.clone();
		}
		Dimension defSize = getDefaultMinimumSize();
		if (defSize != null) {
			return (Dimension)defSize.clone();
		}
		return isDisplayable() ? new Dimension(1, 1) : new Dimension(w, h);
    }

    public Dimension getPreferredSize() {
        return preferredSize();
    }

    @Deprecated
    public Dimension preferredSize() {
        if (isPreferredSizeSet()) {
			return new Dimension(preferredSize);
		}
		Dimension defSize = getDefaultPreferredSize();
		if (defSize != null) {
			return new Dimension(defSize);
		}
		return new Dimension(getMinimumSize());
    }

    public void setMaximumSize(Dimension maximumSize) {
        Dimension oldMaximumSize;
        oldMaximumSize = this.maximumSize;
		if (oldMaximumSize != null) {
			oldMaximumSize = oldMaximumSize.getSize();
		}
		if (this.maximumSize == null) {
			if (maximumSize != null) {
				this.maximumSize = new Dimension(maximumSize);
			}
		} else {
			if (maximumSize != null) {
				this.maximumSize.setSize(maximumSize);
			} else {
				this.maximumSize = null;
			}
		}
        firePropertyChange("maximumSize", oldMaximumSize, this.maximumSize); //$NON-NLS-1$
    }

    public void setMinimumSize(Dimension minimumSize) {
        Dimension oldMinimumSize;
        oldMinimumSize = this.minimumSize;
		if (oldMinimumSize != null) {
			oldMinimumSize = oldMinimumSize.getSize();
		}
		if (this.minimumSize == null) {
			if (minimumSize != null) {
				this.minimumSize = new Dimension(minimumSize);
			}
		} else {
			if (minimumSize != null) {
				this.minimumSize.setSize(minimumSize);
			} else {
				this.minimumSize = null;
			}
		}
        firePropertyChange("minimumSize", oldMinimumSize, this.minimumSize); //$NON-NLS-1$
    }

    public void setPreferredSize(Dimension preferredSize) {
        Dimension oldPreferredSize;
        oldPreferredSize = this.preferredSize;
		if (oldPreferredSize != null) {
			oldPreferredSize = oldPreferredSize.getSize();
		}
		if (this.preferredSize == null) {
			if (preferredSize != null) {
				this.preferredSize = new Dimension(preferredSize);
			}
		} else {
			if (preferredSize != null) {
				this.preferredSize.setSize(preferredSize);
			} else {
				this.preferredSize = null;
			}
		}
        firePropertyChange("preferredSize", oldPreferredSize, this.preferredSize); //$NON-NLS-1$
    }
	
    Dimension getDefaultMinimumSize() {
        return null;
    }

    Dimension getDefaultPreferredSize() {
        return null;
    }

    void resetDefaultSize() {
    }

	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
    public boolean isDisplayable() {
        return isVisible(); //behaviour.isDisplayable();
    }
	
	public void setVisible(boolean b) {
        // show() & hide() are not deprecated for Window,
        // so have to call them from setVisible()
        show(b);
    }

    /**
     * Deprecated: replaced by setVisible(boolean) method.
     * 
     * @deprecated Replaced by setVisible(boolean) method.
     */
    @Deprecated
    public void show() {
        if (visible) {
			return;
		}
		/*
		prepare4HierarchyChange();
		mapToDisplay(true);
		validate();
		*/
		visible = true;
		/*
		//behaviour.setVisible(true);
		postEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_SHOWN));
		// ???AWT: finishHierarchyChange(this, parent, 0);
		notifyInputMethod(new Rectangle(x, y, w, h));
		// ???AWT: invalidateRealParent();
		*/
    }

	public void hide() {
        if (!visible) {
			return;
		}
		// prepare4HierarchyChange();
		visible = false;
		
		/*
		moveFocusOnHide();
		//behaviour.setVisible(false);
		postEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_HIDDEN));
		// ???AWT: finishHierarchyChange(this, parent, 0);
		notifyInputMethod(null);
		// ???AWT: invalidateRealParent();
		*/
		
    }
	
    @Deprecated
    public void show(boolean b) {
        if (b) {
            show();
        } else {
            hide();
        }
    }
	
	public void applyComponentOrientation(ComponentOrientation orientation) {
        if (orientation == null) {
            throw new NullPointerException();
        }
        setComponentOrientation(orientation);
    }
	
	public ComponentOrientation getComponentOrientation() {
        return orientation;
	}

    public void setComponentOrientation(ComponentOrientation o) {
        ComponentOrientation oldOrientation;
        oldOrientation = orientation;
		orientation = o;
        firePropertyChange("componentOrientation", oldOrientation, orientation); //$NON-NLS-1$
        invalidate();
    }
/*
	final boolean canBeFocusOwner() {
        // It is enabled, visible, focusable.
        if (isEnabled() && isDisplayable() && isVisible() && isFocusable()) {
            return true;
        }
        return false;
    }
*/
	public boolean inside(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }
	

    public boolean isValid() {
		return valid; //&& behaviour.isDisplayable();
    }

    public void invalidate() {
        valid = false;
		resetDefaultSize();
    }

	void invalidateParent() {
        if (parent != null) {
            parent.invalidateIfValid();
        }
    }
	
    final void invalidateIfValid() {
        if (isValid()) {
            invalidate();
        }
    }

	public void addPropertyChangeListener(//String name,
		PropertyChangeListener listener) {
		if (listener == null) {
			return;
		}
		if (propertyChangeSupport == null) {
			propertyChangeSupport = new PropertyChangeSupport(this);
		}
		propertyChangeSupport.addPropertyChangeListener(listener);
    }

	public void removePropertyChangeListener(
		PropertyChangeListener listener) {
        if (listener == null || propertyChangeSupport == null) {
			return;
		}
		propertyChangeSupport.removePropertyChangeListener(listener);
    }

	private void firePropertyChangeImpl(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeSupport pcs;
        if (propertyChangeSupport == null) {
			return;
		}
		pcs = propertyChangeSupport;
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
        firePropertyChangeImpl(propertyName, new Integer(oldValue), new Integer(newValue));
    }

    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        firePropertyChangeImpl(propertyName, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
    }

    protected void firePropertyChange(final String propertyName, final Object oldValue,
									  final Object newValue) {
        firePropertyChangeImpl(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
        firePropertyChangeImpl(propertyName, new Byte(oldValue), new Byte(newValue));
    }

    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
        firePropertyChangeImpl(propertyName, new Character(oldValue), new Character(newValue));
    }

    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
        firePropertyChangeImpl(propertyName, new Short(oldValue), new Short(newValue));
    }

    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
        firePropertyChangeImpl(propertyName, new Long(oldValue), new Long(newValue));
    }

    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
        firePropertyChangeImpl(propertyName, new Float(oldValue), new Float(newValue));
    }

    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
        firePropertyChangeImpl(propertyName, new Double(oldValue), new Double(newValue));
    }
	
	public void setSize(int width, int height) {
        // toolkit.lockAWT();
        try {
            resize(width, height);
        } finally {
            // toolkit.unlockAWT();
        }
    }

    /**
     * Sets the size of the Component specified by Dimension object.
     * 
     * @param d
     *            the new size of the Component.
     */
    public void setSize(Dimension d) {
        resize(d);
    }

    public void resize(int width, int height) {
        boundsMaskParam = NativeWindow.BOUNDS_NOMOVE;
		setBounds(x, y, width, height);

		// MOD: impl
		// graphics.resize(width, height);
    }
	
	public void reshape(int x, int y, int w, int h) {
        setBounds(x, y, w, h, boundsMaskParam, true);
		boundsMaskParam = 0;
    }
	
	public void setBounds(int x, int y, int w, int h) {
        reshape(x, y, w, h);
    }

    void setBounds(int x, int y, int w, int h, int bMask, boolean updateBehavior) {
        int oldX = this.x;
        int oldY = this.y;
        int oldW = this.w;
        int oldH = this.h;
        setBoundsFields(x, y, w, h, bMask);
        // Moved
		/*
        if ((oldX != this.x) || (oldY != this.y)) {
            // ???AWT: invalidateRealParent();
            postEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_MOVED));
            spreadHierarchyBoundsEvents(this, HierarchyEvent.ANCESTOR_MOVED);
        }
        // Resized
        if ((oldW != this.w) || (oldH != this.h)) {
            invalidate();
            postEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
            spreadHierarchyBoundsEvents(this, HierarchyEvent.ANCESTOR_RESIZED);
        }
        if (updateBehavior) {
            //behaviour.setBounds(this.x, this.y, this.w, this.h, bMask);
        }
        notifyInputMethod(new Rectangle(x, y, w, h));
		*/
    }

    private void setBoundsFields(int x, int y, int w, int h, int bMask) {
        if ((bMask & NativeWindow.BOUNDS_NOSIZE) == 0) {
            this.w = w;
            this.h = h;
        }
        if ((bMask & NativeWindow.BOUNDS_NOMOVE) == 0) {
            this.x = x;
            this.y = y;
        }
    }
	
    public void resize(Dimension size) {
        setSize(size.width, size.height);
    }
	
    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }
}

