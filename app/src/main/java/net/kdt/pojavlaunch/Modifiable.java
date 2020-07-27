package net.kdt.pojavlaunch;

import java.util.*;
import java.lang.reflect.*;

// This class simply get the modifiable original Collection from unmodifable one.
public class Modifiable
{
	public static <E> Collection<E> modifyCollection(Collection<? extends E> collection) {
        if (collection == null) {
            throw new NullPointerException("collection == null");
        } else if (collection.getClass().getSimpleName().startsWith("Unmodifiable")) {
			Field collectionField = null;
			try {
				collectionField = Class.forName("java.util.Collections$UnmodifiableCollection").getDeclaredField("c");
			} catch (Throwable th) {
				th.printStackTrace();
			}
			
			if (collectionField != null) {
				collectionField.setAccessible(true);
				try {
					return (Collection<E>) collectionField.get(collection);
				} catch (Throwable th) {
					th.printStackTrace();
				}
			}
		}
        return (Collection<E>) collection;
    }
	
	public static <E> Set<E> modifySet(Set<? extends E> set) {
		return (Set<E>) modifyCollection(set);
    }
}
