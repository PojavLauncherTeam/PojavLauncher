package net.kdt.pojavlaunch;

import java.util.*;
import java.lang.reflect.*;
import java.security.*;

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
	
	public static <E> List<E> modifyList(List<? extends E> list) {
		return (List<E>) modifyCollection(list);
    }
	
	public static <E> Set<E> modifySet(Set<? extends E> set) {
		return (Set<E>) modifyCollection(set);
    }
	
	// Get modifiable list from ServiceList
	public static void resetServiceList(List<Provider.Service> list) throws Throwable {
		Class<?> listClass = Class.forName("sun.security.jca.ProviderList$ServiceList");
		Field servicesField = listClass.getDeclaredField("services");
		Field firstServiceField = listClass.getDeclaredField("firstService");
		
		servicesField.setAccessible(true);
		firstServiceField.setAccessible(true);
		
		List<Provider.Service> services = (List<Provider.Service>) servicesField.get(list);
		firstServiceField.set(list, null);
		if (services == null) {
			System.err.println("ServiceList is null, how to erase?");
		} else services.clear();
	}
}
