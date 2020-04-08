package java.awt;

public class Container extends Component
{

	@Override
	public void remove(MenuComponent c)
	{
	}

	@Override
	public Font getFont()
	{
		return Font.decode(null);
	}
	
	private java.util.List<Component> component = new java.util.ArrayList<Component>();
    LayoutManager layoutMgr;
	
	public void setLayout(LayoutManager mgr) {
		layoutMgr = mgr;
    }
	
	public void doLayout() {
        // layout();
    }
	

    public Component getComponent(int n) {
        // This method is not synchronized under AWT tree lock.
        // Instead, the calling code is responsible for the
        // synchronization. See 6784816 for details.
        try {
            return component.get(n);
        } catch (IndexOutOfBoundsException z) {
            throw new ArrayIndexOutOfBoundsException("No such child: " + n);
        }
    }
	
	public int getComponentCount() {
        return countComponents();
    }

    @Deprecated
    public int countComponents() {
        // This method is not synchronized under AWT tree lock.
        // Instead, the calling code is responsible for the
        // synchronization. See 6784816 for details.
        return component.size();
    }
   
    public void add(Component comp, Object constraints) {
        addImpl(comp, constraints, -1);
    }
	
	private void checkAddToSelf(Component comp){
        if (comp instanceof Container) {
            for (Container cn = this; cn != null; cn=cn.parent) {
                if (cn == comp) {
                    throw new IllegalArgumentException("adding container's parent to itself");
                }
            }
        }
    }
    
    private void checkNotAWindow(Component comp){
        if (comp instanceof Window) {
            throw new IllegalArgumentException("adding a window to a container");
        }
    }
    
    public void add(Component comp, Object constraints, int index) {
		addImpl(comp, constraints, index);
    }
	
	protected void addImpl(Component comp, Object constraints, int index) {
        /* Check for correct arguments:  index in bounds,
		 * comp cannot be one of this container's parents,
		 * and comp cannot be a window.
		 * comp and container must be on the same GraphicsDevice.
		 * if comp is container, all sub-components must be on
		 * same GraphicsDevice.
		 */
		// GraphicsConfiguration thisGC = this.getGraphicsConfiguration();
		if (index > component.size() || (index < 0 && index != -1)) {
			throw new IllegalArgumentException(
				"illegal component position");
		}
		checkAddToSelf(comp);
		checkNotAWindow(comp);
		/*
		 if (thisGC != null) {
		 comp.checkGD(thisGC.getDevice().getIDstring());
		 }
		 */
		/* Reparent the component and tidy up the tree's state. */
		if (comp.parent != null) {
			comp.parent.remove(comp);
			if (index > component.size()) {
				throw new IllegalArgumentException("illegal component position");
			}
		}
		//index == -1 means add to the end.
		if (index == -1) {
			component.add(comp);
		} else {
			component.add(index, comp);
		}
		comp.parent = this;
		/*
		 comp.setGraphicsConfiguration(thisGC);
		 adjustListeningChildren(AWTEvent.HIERARCHY_EVENT_MASK,
		 comp.numListening(AWTEvent.HIERARCHY_EVENT_MASK));
		 adjustListeningChildren(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK,
		 comp.numListening(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK));
		 adjustDescendants(comp.countHierarchyMembers());
		 invalidateIfValid();
		 if (peer != null) {
		 comp.addNotify();
		 }
		 */
		/* Notify the layout manager of the added component. */
		if (layoutMgr != null) {
			if (layoutMgr instanceof LayoutManager2) {
				((LayoutManager2)layoutMgr).addLayoutComponent(comp, constraints);
			} else if (constraints instanceof String) {
				layoutMgr.addLayoutComponent((String)constraints, comp);
			}
		}
		/*
		 if (containerListener != null ||
		 (eventMask & AWTEvent.CONTAINER_EVENT_MASK) != 0 ||
		 Toolkit.enabledOnToolkit(AWTEvent.CONTAINER_EVENT_MASK)) {
		 ContainerEvent e = new ContainerEvent(this,
		 ContainerEvent.COMPONENT_ADDED,
		 comp);
		 dispatchEvent(e);
		 }
		 comp.createHierarchyEvents(HierarchyEvent.HIERARCHY_CHANGED, comp,
		 this, HierarchyEvent.PARENT_CHANGED,
		 Toolkit.enabledOnToolkit(AWTEvent.HIERARCHY_EVENT_MASK));
		 if (peer != null && layoutMgr == null && isVisible()) {
		 updateCursorImmediately();
		 }
		 */
    }
	
	public void removeAll() {
        /*
		 adjustListeningChildren(AWTEvent.HIERARCHY_EVENT_MASK,
		 -listeningChildren);
		 adjustListeningChildren(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK,
		 -listeningBoundsChildren);
		 adjustDescendants(-descendantsCount);
		 */
		while (!component.isEmpty()) {
			Component comp = component.remove(component.size()-1);

			if (getPeer() != null) {
				comp.removeNotify();
			}
			if (layoutMgr != null) {
				layoutMgr.removeLayoutComponent(comp);
			}
			comp.parent = null;
			/*
			 comp.setGraphicsConfiguration(null);
			 if (containerListener != null ||
			 (eventMask & AWTEvent.CONTAINER_EVENT_MASK) != 0 ||
			 Toolkit.enabledOnToolkit(AWTEvent.CONTAINER_EVENT_MASK)) {
			 ContainerEvent e = new ContainerEvent(this,
			 ContainerEvent.COMPONENT_REMOVED,
			 comp);
			 dispatchEvent(e);
			 }

			 comp.createHierarchyEvents(HierarchyEvent.HIERARCHY_CHANGED,
			 comp, this,
			 HierarchyEvent.PARENT_CHANGED,
			 Toolkit.enabledOnToolkit(AWTEvent.HIERARCHY_EVENT_MASK));
			 */
		}
		/*
		 if (peer != null && layoutMgr == null && isVisible()) {
		 updateCursorImmediately();
		 }
		 
		invalidateIfValid();
		*/
    }
	
	public void remove(Component comp) {
        if (comp.parent == this)  {
			int index = component.indexOf(comp);
			if (index >= 0) {
				remove(index);
			}
		}
    }
	
	public void remove(int index) {
        
            if (index < 0  || index >= component.size()) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            Component comp = component.get(index);
			/*
            if (peer != null) {
                comp.removeNotify();
            }
			*/
            if (layoutMgr != null) {
                layoutMgr.removeLayoutComponent(comp);
            }
			/*
            adjustListeningChildren(AWTEvent.HIERARCHY_EVENT_MASK,
									-comp.numListening(AWTEvent.HIERARCHY_EVENT_MASK));
            adjustListeningChildren(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK,
									-comp.numListening(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK));
            adjustDescendants(-(comp.countHierarchyMembers()));
			*/
            comp.parent = null;
            component.remove(index);
			/*
            comp.setGraphicsConfiguration(null);
            invalidateIfValid();
            if (containerListener != null ||
                (eventMask & AWTEvent.CONTAINER_EVENT_MASK) != 0 ||
                Toolkit.enabledOnToolkit(AWTEvent.CONTAINER_EVENT_MASK)) {
                ContainerEvent e = new ContainerEvent(this,
													  ContainerEvent.COMPONENT_REMOVED,
													  comp);
                dispatchEvent(e);
            }
            comp.createHierarchyEvents(HierarchyEvent.HIERARCHY_CHANGED, comp,
                                       this, HierarchyEvent.PARENT_CHANGED,
                                       Toolkit.enabledOnToolkit(AWTEvent.HIERARCHY_EVENT_MASK));
            if (peer != null && layoutMgr == null && isVisible()) {
                updateCursorImmediately();
            }
			*/
        }
  
}
