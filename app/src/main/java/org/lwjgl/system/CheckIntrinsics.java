/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system;

/**
 * Simple index checks.
 *
 * <p>On Java 9 these checks are replaced with the corresponding {@link java.util.Objects} methods, which perform better.</p>
 */
public final class CheckIntrinsics {

    private CheckIntrinsics() {
    }

    public static int checkIndex(int index, int length) {
        if (index < 0 || length <= index) {
            throw new IndexOutOfBoundsException();
        }
        return index;
    }

    public static int checkFromToIndex(int fromIndex, int toIndex, int length) {
        if (fromIndex < 0 || toIndex < fromIndex || length < toIndex) {
            throw new IndexOutOfBoundsException();
        }
        return fromIndex;
    }

    public static int checkFromIndexSize(int fromIndex, int size, int length) {
        if ((length | fromIndex | size) < 0 || length - fromIndex < size) {
            throw new IndexOutOfBoundsException();
        }
        return fromIndex;
    }

}
