/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.security.fortress;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
/**
 * This class contains information about all registered providers and preferred
 * implementations for all "serviceName.algName".
 */
public class Services {
    /**
     * The HashMap that contains information about preferred implementations for
     * all serviceName.algName in the registered providers.
     * Set the initial size to 600 so we don't grow to 1024 by default because
     * initialization adds a few entries more than the growth threshold.
     */
    private static final HashMap<String, ArrayList<Provider.Service>> services
	= new HashMap<String, ArrayList<Provider.Service>>(600);
    /**
     * Save default SecureRandom service as well.
     * Avoids similar provider/services iteration in SecureRandom constructor.
     */
    private static Provider.Service cachedSecureRandomService;
    /**
     * Need refresh flag.
     */
    private static boolean needRefresh;
    /**
     * The cacheVersion is changed on every update of service
     * information. It is used by external callers to validate their
     * own caches of Service information.
     */
    private static int cacheVersion = 1;
    /**
     * Registered providers.
     */
    private static final ArrayList<Provider> providers = new ArrayList<Provider>(20);
    /**
     * Hash for quick provider access by name.
     */
    private static final HashMap<String, Provider> providersNames
	= new HashMap<String, Provider>(20);
    static {
        String providerClassName = null;
        int i = 1;
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        while ((providerClassName = Security.getProperty("security.provider." + i++)) != null) {
            try {
                Class<?> providerClass = Class.forName(providerClassName.trim(), true, cl);
                Provider p = (Provider) providerClass.newInstance();
                providers.add(p);
                providersNames.put(p.getName(), p);
                initServiceInfo(p);
            } catch (ClassNotFoundException ignored) {
            } catch (IllegalAccessException ignored) {
            } catch (InstantiationException ignored) {
            }
        }
        // Engine.door.renumProviders();
    }
    /**
     * Returns a copy of the registered providers as an array.
     */
    public static synchronized ArrayList<Provider> getProviders() {
        return providers;
    }
    /**
     * Returns the provider with the specified name.
     */
    public static synchronized Provider getProvider(String name) {
        if (name == null) {
            return null;
        }
        return providersNames.get(name);
    }
    /**
     * Inserts a provider at a specified 1-based position.
     */
    public static synchronized int insertProviderAt(Provider provider, int position) {
        int size = providers.size();
        if ((position < 1) || (position > size)) {
            position = size + 1;
        }
        providers.add(position - 1, provider);
        providersNames.put(provider.getName(), provider);
        setNeedRefresh();
        return position;
    }
    /**
     * Removes the provider at the specified 1-based position.
     */
    public static synchronized void removeProvider(int providerNumber) {
        Provider p = providers.remove(providerNumber - 1);
        providersNames.remove(p.getName());
        setNeedRefresh();
    }
    /**
     * Adds information about provider services into HashMap.
     */
    public static synchronized void initServiceInfo(Provider p) {
        for (Provider.Service service : p.getServices()) {
            String type = service.getType();
            if (cachedSecureRandomService == null && type.equals("SecureRandom")) {
                cachedSecureRandomService = service;
            }
            String key = type + "." + service.getAlgorithm().toUpperCase(Locale.US);
            appendServiceLocked(key, service);
			/*
            for (String alias : Engine.door.getAliases(service)) {
                key = type + "." + alias.toUpperCase(Locale.US);
                appendServiceLocked(key, service);
            }
			*/
        }
    }
    /**
     * Add or append the service to the key.
     */
    private static void appendServiceLocked(String key, Provider.Service service) {
        ArrayList<Provider.Service> serviceList = services.get(key);
        if (serviceList == null) {
            serviceList = new ArrayList<Provider.Service>(1);
            services.put(key, serviceList);
        }
        serviceList.add(service);
    }
    /**
     * Returns true if services does not contain any provider information.
     */
    public static synchronized boolean isEmpty() {
        return services.isEmpty();
    }
    /**
     * Looks up the requested service by type and algorithm. The
     * service key should be provided in the same format used when
     * registering a service with a provider, for example,
     * "KeyFactory.RSA".
     *
     * Callers can cache the returned service information but such
     * caches should be validated against the result of
     * Service.getCacheVersion() before use.
     */
    public static synchronized ArrayList<Provider.Service> getServices(String key) {
        return services.get(key);
    }
	
	public static synchronized ArrayList<Provider.Service> getServices(String type, String algorithm) {
		return null;
	}
	
    /**
     * Returns the default SecureRandom service description.
     */
    public static synchronized Provider.Service getSecureRandomService() {
        getCacheVersion();  // used for side effect of updating cache if needed
        return cachedSecureRandomService;
    }
    /**
     * In addition to being used here when the list of providers
     * changes, this method is also used by the Provider
     * implementation to indicate that a provides list of services has
     * changed.
     */
    public static synchronized void setNeedRefresh() {
        needRefresh = true;
    }
    /**
     * Returns the current cache version. This has the possible side
     * effect of updating the cache if needed.
     */
    public static synchronized int getCacheVersion() {
        if (needRefresh) {
            cacheVersion++;
            synchronized (services) {
                services.clear();
            }
            cachedSecureRandomService = null;
            for (Provider p : providers) {
                initServiceInfo(p);
            }
            needRefresh = false;
        }
        return cacheVersion;
    }
}
