package git.artdeell.arcdns;

import static git.artdeell.arcdns.CacheUtilCommons.NEVER_EXPIRATION;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;


public final class CacheUtil_J8 {

    public static void setInetAddressCache(String host, String[] ips, long expireMillis)
            throws UnknownHostException, IllegalAccessException, InstantiationException,
            InvocationTargetException, ClassNotFoundException, NoSuchFieldException {
        host = host.toLowerCase();
        long expiration = expireMillis == NEVER_EXPIRATION ? NEVER_EXPIRATION : System.currentTimeMillis() + expireMillis;
        Object entry = newCacheEntry(host, ips, expiration);

        synchronized (getAddressCacheOfInetAddress()) {
            getCache().put(host, entry);
            getNegativeCache().remove(host);
        }
    }

    private static Object newCacheEntry(String host, String[] ips, long expiration)
            throws UnknownHostException, ClassNotFoundException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        // InetAddress.CacheEntry has only one constructor
        return getConstructorOfInetAddress$CacheEntry().newInstance(CacheUtilCommons.toInetAddressArray(host, ips), expiration);
    }

    private static volatile Constructor<?> constructorOfInetAddress$CacheEntry = null;

    private static Constructor<?> getConstructorOfInetAddress$CacheEntry() throws ClassNotFoundException {
        if (constructorOfInetAddress$CacheEntry != null) return constructorOfInetAddress$CacheEntry;

        synchronized (CacheUtilCommons.class) {
            // double check
            if (constructorOfInetAddress$CacheEntry != null) return constructorOfInetAddress$CacheEntry;

            final String className = "java.net.InetAddress$CacheEntry";
            final Class<?> clazz = Class.forName(className);

            // InetAddress.CacheEntry has only one constructor:
            // - for jdk 6, constructor signature is CacheEntry(Object address, long expiration)
            // - for jdk 7/8, constructor signature is CacheEntry(InetAddress[] addresses, long expiration)
            //
            // code in jdk 6:
            //   https://hg.openjdk.java.net/jdk6/jdk6/jdk/file/8deef18bb749/src/share/classes/java/net/InetAddress.java#l739
            // code in jdk 7:
            //   https://hg.openjdk.java.net/jdk7u/jdk7u/jdk/file/4dd5e486620d/src/share/classes/java/net/InetAddress.java#l742
            // code in jdk 8:
            //   https://hg.openjdk.java.net/jdk8u/jdk8u/jdk/file/45e4e636b757/src/share/classes/java/net/InetAddress.java#l748
            final Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
            constructor.setAccessible(true);

            constructorOfInetAddress$CacheEntry = constructor;
            return constructor;
        }
    }

    public static void removeInetAddressCache(String host)
            throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        host = host.toLowerCase();

        synchronized (getAddressCacheOfInetAddress()) {
            getCache().remove(host);
            getNegativeCache().remove(host);
        }
    }

    private static Map<String, Object> getCache()
            throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        return getCacheOfInetAddress$Cache0(getAddressCacheOfInetAddress());
    }

    private static Map<String, Object> getNegativeCache()
            throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        return getCacheOfInetAddress$Cache0(getNegativeCacheOfInetAddress());
    }


    private static volatile Field cacheMapFieldOfInetAddress$Cache = null;

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getCacheOfInetAddress$Cache0(Object inetAddressCache)
            throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        if (cacheMapFieldOfInetAddress$Cache == null) {
            synchronized (CacheUtil_J8.class) {
                if (cacheMapFieldOfInetAddress$Cache == null) { // double check
                    final Class<?> clazz = Class.forName("java.net.InetAddress$Cache");
                    final Field f = clazz.getDeclaredField("cache");
                    f.setAccessible(true);
                    cacheMapFieldOfInetAddress$Cache = f;
                }
            }
        }

        return (Map<String, Object>) cacheMapFieldOfInetAddress$Cache.get(inetAddressCache);
    }

    private static Object getAddressCacheOfInetAddress()
            throws NoSuchFieldException, IllegalAccessException {
        return getAddressCacheAndNegativeCacheOfInetAddress0()[0];
    }

    private static Object getNegativeCacheOfInetAddress()
            throws NoSuchFieldException, IllegalAccessException {
        return getAddressCacheAndNegativeCacheOfInetAddress0()[1];
    }

    private static volatile Object[] ADDRESS_CACHE_AND_NEGATIVE_CACHE = null;

    private static Object[] getAddressCacheAndNegativeCacheOfInetAddress0()
            throws NoSuchFieldException, IllegalAccessException {
        if (ADDRESS_CACHE_AND_NEGATIVE_CACHE != null) return ADDRESS_CACHE_AND_NEGATIVE_CACHE;

        synchronized (CacheUtil_J8.class) {
            // double check
            if (ADDRESS_CACHE_AND_NEGATIVE_CACHE != null) return ADDRESS_CACHE_AND_NEGATIVE_CACHE;

            final Field cacheField = InetAddress.class.getDeclaredField("addressCache");
            cacheField.setAccessible(true);

            final Field negativeCacheField = InetAddress.class.getDeclaredField("negativeCache");
            negativeCacheField.setAccessible(true);

            ADDRESS_CACHE_AND_NEGATIVE_CACHE = new Object[]{
                    cacheField.get(InetAddress.class),
                    negativeCacheField.get(InetAddress.class)
            };
            return ADDRESS_CACHE_AND_NEGATIVE_CACHE;
        }
    }

    private static boolean isDnsCacheEntryExpired(String host) {
        return null == host || "0.0.0.0".equals(host);
    }

    public static void clearInetAddressCache()
            throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        synchronized (getAddressCacheOfInetAddress()) {
            getCache().clear();
            getNegativeCache().clear();
        }
    }

    private CacheUtil_J8() {
    }
}
