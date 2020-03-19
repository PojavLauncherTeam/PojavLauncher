/*
 * Copyright (c) 1997, 1999, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package javax.accessibility;

/**
 * Interface Accessible is the main interface for the accessibility package.
 * All components that support
 * the accessibility package must implement this interface.
 * It contains a single method, {@link #getAccessibleContext}, which
 * returns an instance of the class {@link AccessibleContext}.
 *
 * @author      Peter Korn
 * @author      Hans Muller
 * @author      Willie Walker
 */
public interface Accessible {

    /**
     * Returns the AccessibleContext associated with this object.  In most
     * cases, the return value should not be null if the object implements
     * interface Accessible.  If a component developer creates a subclass
     * of an object that implements Accessible, and that subclass
     * is not Accessible, the developer should override the
     * getAccessibleContext method to return null.
     */
    public AccessibleContext getAccessibleContext();
}
