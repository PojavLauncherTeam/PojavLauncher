package git.artdeell.arcdns;
import static git.artdeell.arcdns.CacheUtilCommons.NEVER_EXPIRATION;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
public class CacheUtil_J9 {
    public static void setInetAddressCache(String host, String[] ips, long expireMillis)
            throws UnknownHostException, IllegalAccessException, InstantiationException,
            InvocationTargetException, ClassNotFoundException, NoSuchFieldException {
        long expiration = expireMillis == NEVER_EXPIRATION ? NEVER_EXPIRATION : System.nanoTime() + expireMillis * 1_000_000;
        Object cachedAddresses = newCachedAddresses(host, ips, expiration);

        getCacheOfInetAddress().put(host, cachedAddresses);
        getExpirySetOfInetAddress().add(cachedAddresses);
    }

    private static Object newCachedAddresses(String host, String[] ips, long expiration)
            throws ClassNotFoundException, UnknownHostException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        // InetAddress.CachedAddresses has only one constructor
        return getConstructorOfInetAddress$CachedAddresses().newInstance(host, CacheUtilCommons.toInetAddressArray(host, ips), expiration);
    }

    private static volatile Constructor<?> constructorOfInetAddress$CachedAddresses = null;

    private static Constructor<?> getConstructorOfInetAddress$CachedAddresses() throws ClassNotFoundException {
        if (constructorOfInetAddress$CachedAddresses != null) return constructorOfInetAddress$CachedAddresses;

        synchronized (CacheUtilCommons.class) {
            // double check
            if (constructorOfInetAddress$CachedAddresses != null) return constructorOfInetAddress$CachedAddresses;

            final Class<?> clazz = Class.forName(inetAddress$CachedAddresses_ClassName);

            // InetAddress.CacheEntry has only one constructor:
            //
            // - for jdk 9-jdk12, constructor signature is CachedAddresses(String host, InetAddress[] inetAddresses, long expiryTime)
            // code in jdk 9:
            //   https://hg.openjdk.java.net/jdk9/jdk9/jdk/file/65464a307408/src/java.base/share/classes/java/net/InetAddress.java#l783
            // code in jdk 11:
            //   https://hg.openjdk.java.net/jdk/jdk11/file/1ddf9a99e4ad/src/java.base/share/classes/java/net/InetAddress.java#l787
            final Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
            constructor.setAccessible(true);

            constructorOfInetAddress$CachedAddresses = constructor;
            return constructor;
        }
    }

    public static void removeInetAddressCache(String host) throws NoSuchFieldException, IllegalAccessException {
        getCacheOfInetAddress().remove(host);
        removeHostFromExpirySetOfInetAddress(host);
    }

    /**
     * @see #getExpirySetOfInetAddress()
     */
    private static void removeHostFromExpirySetOfInetAddress(String host)
            throws NoSuchFieldException, IllegalAccessException {
        for (Iterator<Object> iterator = getExpirySetOfInetAddress().iterator(); iterator.hasNext(); ) {
            Object cachedAddresses = iterator.next();
            if (getHostOfInetAddress$CacheAddress(cachedAddresses).equals(host)) {
                iterator.remove();
            }
        }
    }

    private static volatile Field hostFieldOfInetAddress$CacheAddress = null;

    private static String getHostOfInetAddress$CacheAddress(Object cachedAddresses)
            throws NoSuchFieldException, IllegalAccessException {
        if (hostFieldOfInetAddress$CacheAddress == null) {
            synchronized (CacheUtil_J9.class) {
                if (hostFieldOfInetAddress$CacheAddress == null) { // double check
                    final Field f = cachedAddresses.getClass().getDeclaredField("host");
                    f.setAccessible(true);
                    hostFieldOfInetAddress$CacheAddress = f;
                }
            }
        }
        return (String) hostFieldOfInetAddress$CacheAddress.get(cachedAddresses);
    }


    //////////////////////////////////////////////////////////////////////////////
    // getters of static cache related fields of InetAddress
    //////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    private static ConcurrentMap<String, Object> getCacheOfInetAddress()
            throws NoSuchFieldException, IllegalAccessException {
        return (ConcurrentMap<String, Object>) getCacheAndExpirySetOfInetAddress0()[0];
    }

    @SuppressWarnings("unchecked")
    private static ConcurrentSkipListSet<Object> getExpirySetOfInetAddress()
            throws NoSuchFieldException, IllegalAccessException {
        return (ConcurrentSkipListSet<Object>) getCacheAndExpirySetOfInetAddress0()[1];
    }

    private static volatile Object[] ADDRESS_CACHE_AND_EXPIRY_SET = null;

    private static Object[] getCacheAndExpirySetOfInetAddress0()
            throws NoSuchFieldException, IllegalAccessException {
        if (ADDRESS_CACHE_AND_EXPIRY_SET != null) return ADDRESS_CACHE_AND_EXPIRY_SET;

        synchronized (CacheUtil_J9.class) {
            if (ADDRESS_CACHE_AND_EXPIRY_SET != null) return ADDRESS_CACHE_AND_EXPIRY_SET;

            final Field cacheField = InetAddress.class.getDeclaredField("cache");
            cacheField.setAccessible(true);

            final Field expirySetField = InetAddress.class.getDeclaredField("expirySet");
            expirySetField.setAccessible(true);

            ADDRESS_CACHE_AND_EXPIRY_SET = new Object[]{
                    cacheField.get(InetAddress.class),
                    expirySetField.get(InetAddress.class)
            };

            return ADDRESS_CACHE_AND_EXPIRY_SET;
        }
    }

    //////////////////////////////////////////////////////////////////////////////

    private static final String inetAddress$CachedAddresses_ClassName = "java.net.InetAddress$CachedAddresses";
    public static void clearInetAddressCache() throws NoSuchFieldException, IllegalAccessException {
        getCacheOfInetAddress().clear();
        getExpirySetOfInetAddress().clear();
    }

    private CacheUtil_J9() {
    }

}
