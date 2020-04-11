package net.minecraft.launchwrapper;

import dalvik.system.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import java.util.jar.*;
import java.util.jar.Attributes.*;
import java.util.logging.*;
import net.kdt.pojavlaunch.*;

public class LaunchClassLoader extends BaseDexClassLoader {
    public static final int BUFFER_SIZE = 4096;
    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("legacy.debugClassLoading", "false"));
    private static final boolean DEBUG_FINER;
    private static final boolean DEBUG_SAVE;
    private static final Manifest EMPTY = new Manifest();
    private static final String[] RESERVED_NAMES = new String[]{"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
    private static File tempFolder = null;
    private Map<String, Class> cachedClasses = new HashMap<String, Class>(1000);
    private Set<String> classLoaderExceptions = new HashSet<String>();
    private Set<String> invalidClasses = new HashSet<String>(1000);
    private final ThreadLocal<byte[]> loadBuffer = new ThreadLocal<byte[]>();
    private Map<Package, Manifest> packageManifests = new HashMap<Package, Manifest>();
    //private ClassLoader parent = getClass().getClassLoader();
    private IClassNameTransformer renameTransformer;
    private List<URL> sources;
    private Set<String> transformerExceptions = new HashSet<String>();
    private List<IClassTransformer> transformers = new ArrayList<IClassTransformer>(2);

    static {
        boolean z;
        boolean z2 = true;
        if (DEBUG && Boolean.parseBoolean(System.getProperty("legacy.debugClassLoadingFiner", "false"))) {
            z = true;
        } else {
            z = false;
        }
        DEBUG_FINER = z;
        if (!(DEBUG && Boolean.parseBoolean(System.getProperty("legacy.debugClassLoadingSave", "false")))) {
            z2 = false;
        }
        DEBUG_SAVE = z2;
    }
	
	protected Package definePackage(String packageName, Manifest manifest,
                                    URL url) throws IllegalArgumentException {
        Attributes mainAttributes = manifest.getMainAttributes();
        String dirName = packageName.replace('.', '/') + "/";
        Attributes packageAttributes = manifest.getAttributes(dirName);
        boolean noEntry = false;
        if (packageAttributes == null) {
            noEntry = true;
            packageAttributes = mainAttributes;
        }
        String specificationTitle = packageAttributes
			.getValue(Attributes.Name.SPECIFICATION_TITLE);
        if (specificationTitle == null && !noEntry) {
            specificationTitle = mainAttributes
				.getValue(Attributes.Name.SPECIFICATION_TITLE);
        }
        String specificationVersion = packageAttributes
			.getValue(Attributes.Name.SPECIFICATION_VERSION);
        if (specificationVersion == null && !noEntry) {
            specificationVersion = mainAttributes
				.getValue(Attributes.Name.SPECIFICATION_VERSION);
        }
        String specificationVendor = packageAttributes
			.getValue(Attributes.Name.SPECIFICATION_VENDOR);
        if (specificationVendor == null && !noEntry) {
            specificationVendor = mainAttributes
				.getValue(Attributes.Name.SPECIFICATION_VENDOR);
        }
        String implementationTitle = packageAttributes
			.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
        if (implementationTitle == null && !noEntry) {
            implementationTitle = mainAttributes
				.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
        }
        String implementationVersion = packageAttributes
			.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        if (implementationVersion == null && !noEntry) {
            implementationVersion = mainAttributes
				.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        }
        String implementationVendor = packageAttributes
			.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
        if (implementationVendor == null && !noEntry) {
            implementationVendor = mainAttributes
				.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
        }
        return definePackage(packageName, specificationTitle,
							 specificationVersion, specificationVendor,
							 implementationTitle, implementationVersion, 
							 implementationVendor, //isSealed(manifest, dirName) ? url : null);
							 isSealed(dirName, manifest) ? url : null);
    }
	
	// ENDED modding part
	
    public LaunchClassLoader() { //String launchDexPath, String launchOptimizedDirectory, String launchLibrarySearchPath, ClassLoader parentClassLoader) {
        super(MainActivity.launchClassPath, new File(MainActivity.launchOptimizedDirectory), MainActivity.launchLibrarySearchPath, LaunchClassLoader.class.getClassLoader());
		
		//super(launchDexPath, launchOptimizedDirectory, launchLibrarySearchPath, parentClassLoader);
		//System.out.println("MinecraftLaunchWrapper: How did Minecraft generated it: " + LaunchClassLoaderAgruments.launchDexPath);
		
        this.sources = new ArrayList<URL>(); // Arrays.asList(sources));
        Thread.currentThread().setContextClassLoader(this);
        addClassLoaderExclusion("java.");
        addClassLoaderExclusion("sun.");
        addClassLoaderExclusion("org.lwjgl.");
        addClassLoaderExclusion("net.minecraft.launchwrapper.");
        addTransformerExclusion("javax.");
        addTransformerExclusion("argo.");
        addTransformerExclusion("org.objectweb.asm.");
        addTransformerExclusion("com.google.common.");
        addTransformerExclusion("org.bouncycastle.");
        addTransformerExclusion("net.minecraft.launchwrapper.injector.");
        if (DEBUG_SAVE) {
            int x = 1;
            tempFolder = new File(Launch.minecraftHome, "CLASSLOADER_TEMP");
            while (tempFolder.exists() && x <= 10) {
                int x2 = x + 1;
                tempFolder = new File(Launch.minecraftHome, "CLASSLOADER_TEMP" + x);
                x = x2;
            }
            if (tempFolder.exists()) {
                LogWrapper.info("DEBUG_SAVE enabled, but 10 temp directories already exist, clean them and try again.", new Object[0]);
                tempFolder = null;
                return;
            }
            LogWrapper.info("DEBUG_SAVE Enabled, saving all classes to \"%s\"", new Object[]{tempFolder.getAbsolutePath().replace('\\', '/')});
            tempFolder.mkdirs();
        }
    }

    public void registerTransformer(String transformerClassName) {
        try {
            IClassTransformer transformer = (IClassTransformer) loadClass(transformerClassName).newInstance();
            this.transformers.add(transformer);
            if ((transformer instanceof IClassNameTransformer) && this.renameTransformer == null) {
                this.renameTransformer = (IClassNameTransformer) transformer;
            }
        } catch (Exception e) {
            LogWrapper.log(Level.SEVERE, e, "A critical problem occurred registering the ASM transformer class %s", new Object[]{transformerClassName});
        }
    }
	
	public Class<?> findClass(String name) throws ClassNotFoundException {
		return super.findClass(name);
	}
	
/*
    public Class<?> findClass(String name) throws ClassNotFoundException {
        if (this.invalidClasses.contains(name)) {
            throw new ClassNotFoundException(name);
        }
        for (String exception : this.classLoaderExceptions) {
            if (name.startsWith(exception)) {
                try {
					return LaunchClassLoader.class.getClassLoader().loadClass(name);
				} catch (ClassNotFoundException e) {
					return loadClassAnyway(name);
				}
            }
        }
        if (this.cachedClasses.containsKey(name)) {
            return (Class<?>) this.cachedClasses.get(name);
        }
        for (String exception2 : this.transformerExceptions) {
            if (name.startsWith(exception2)) {
                try {
                    Class<?> clazz = super.findClass(name);
                    this.cachedClasses.put(name, clazz);
                    return clazz;
                } catch (ClassNotFoundException e) {
                    this.invalidClasses.add(name);
                    throw e;
                }
            }
        }
        try {
            String packageName;
            String transformedName = transformName(name);
            String untransformedName = untransformName(name);
            int lastDot = untransformedName.lastIndexOf(46);
            if (lastDot == -1) {
                packageName = "";
            } else {
                packageName = untransformedName.substring(0, lastDot);
            }
            String fileName = untransformedName.replace('.', '/').concat(".class");
            URLConnection urlConnection = findCodeSourceConnectionFor(fileName);
            CodeSigner[] signers = null;
            if (lastDot > -1) {
                if (!untransformedName.startsWith("net.minecraft.")) {
                    Package pkg;
                    if (urlConnection instanceof JarURLConnection) {
                        JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
                        JarFile jarFile = jarURLConnection.getJarFile();
                        if (!(jarFile == null || jarFile.getManifest() == null)) {
                            Manifest manifest = jarFile.getManifest();
                            JarEntry entry = jarFile.getJarEntry(fileName);
                            pkg = getPackage(packageName);
                            getClassBytes(untransformedName);
                            signers = entry.getCodeSigners();
                            if (pkg == null) {
                                this.packageManifests.put(definePackage(packageName, manifest, jarURLConnection.getJarFileURL()), manifest);
                            } else {//JarURLConnection aj;
                                if (pkg.isSealed()) {
                                    if (!pkg.isSealed(jarURLConnection.getJarFileURL())) {
                                        LogWrapper.severe("The jar file %s is trying to seal already secured path %s", new Object[]{jarFile.getName(), packageName});
                                    }
                                }
                                if (isSealed(packageName, manifest)) {
                                    LogWrapper.severe("The jar file %s has a security seal for path %s, but that path is defined and not secure", new Object[]{jarFile.getName(), packageName});
                                }
                            }
                        }
                    } else {
                        pkg = getPackage(packageName);
                        if (pkg == null) {
                            this.packageManifests.put(definePackage(packageName, null, null, null, null, null, null, null), EMPTY);
                        } else if (pkg.isSealed()) {
                            LogWrapper.severe("The URL %s is defining elements for sealed path %s", new Object[]{urlConnection.getURL(), packageName});
                        }
                    }
                }
            }
            byte[] transformedClass = runTransformers(untransformedName, transformedName, getClassBytes(untransformedName));
            if (DEBUG_SAVE) {
                saveTransformedClass(transformedClass, transformedName);
            }
            try {
				Class<?> clazz = defineClass(transformedName, transformedClass, 0, transformedClass.length); //, new ProtectionDomain(urlConnection == null ? null : new CodeSource(urlConnection.getURL(), signers), getClass().getProtectionDomain().getPermissions()));
				this.cachedClasses.put(transformedName, clazz);
				return clazz;
			} catch (Throwable th) {
				// MODDING HERE!
				Class<?> clazz = loadClassAnyway(name);
				this.cachedClasses.put(transformedName, clazz);
				return clazz;
			}
        } catch (Throwable e2) {
            this.invalidClasses.add(name);
            if (DEBUG) {
                LogWrapper.log(Level.FINEST, e2, "Exception encountered attempting classloading of %s", new Object[]{name});
            }
            //ClassNotFoundException classNotFoundException = new ClassNotFoundException(name, e2);
			throw new ClassNotFoundException(name, e2);
        }
    }

	private Class<?> loadClassAnyway(String pkg) {
		LogWrapper.log(Level.INFO, "Loading class " + pkg);
		try {
			return Class.forName(pkg);
		} catch (Throwable th) {}

		try {
			return getClass().forName(pkg);
		} catch (Throwable th) {}

		try {
			return super.findClass(pkg);
		} catch (Throwable th) {}

		try {
			return super.loadClass(pkg);
		} catch (Throwable th) {}

		try {
			return super.loadClass(pkg, true);
		} catch (Throwable th) {}

		try {
			return getParent().loadClass(pkg);
		} catch (Throwable th) {}

		try {
			return getSystemClassLoader().loadClass(pkg);
		} catch (Throwable th) {}

		try {
			return getClass().getClassLoader().loadClass(pkg);
		} catch (Throwable th) {}

		// throw new RuntimeException("Unable to find class, out of 8 times");
		
		return null;
	}
*/
    private void saveTransformedClass(byte[] data, String transformedName) {
        if (tempFolder != null) {
            File outFile = new File(tempFolder, transformedName.replace('.', File.separatorChar) + ".class");
            File outDir = outFile.getParentFile();
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
            if (outFile.exists()) {
                outFile.delete();
            }
            try {
                LogWrapper.fine("Saving transformed class \"%s\" to \"%s\"", new Object[]{transformedName, outFile.getAbsolutePath().replace('\\', '/')});
                OutputStream output = new FileOutputStream(outFile);
                output.write(data);
                output.close();
            } catch (IOException ex) {
                LogWrapper.log(Level.WARNING, ex, "Could not save transformed class \"%s\"", new Object[]{transformedName});
            }
        }
    }

    private String untransformName(String name) {
        if (this.renameTransformer != null) {
            return this.renameTransformer.unmapClassName(name);
        }
        return name;
    }

    private String transformName(String name) {
        if (this.renameTransformer != null) {
            return this.renameTransformer.remapClassName(name);
        }
        return name;
    }

    private boolean isSealed(String path, Manifest manifest) {
        Attributes attributes = manifest.getAttributes(path);
        String sealed = null;
        if (attributes != null) {
            sealed = attributes.getValue(Name.SEALED);
        }
        if (sealed == null) {
            attributes = manifest.getMainAttributes();
            if (attributes != null) {
                sealed = attributes.getValue(Name.SEALED);
            }
        }
        return "true".equalsIgnoreCase(sealed);
    }

    private URLConnection findCodeSourceConnectionFor(String name) {
        URL resource = findResource(name);
        if (resource == null) {
            return null;
        }
        try {
            return resource.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] runTransformers(String name, String transformedName, byte[] basicClass) {
        int i = 0;
        if (DEBUG_FINER) {
            String str = "Beginning transform of %s (%s) Start Length: %d";
            Object[] objArr = new Object[3];
            objArr[0] = name;
            objArr[1] = transformedName;
            objArr[2] = Integer.valueOf(basicClass == null ? 0 : basicClass.length);
            LogWrapper.finest(str, objArr);
            for (IClassTransformer transformer : this.transformers) {
                int i2;
                String transName = transformer.getClass().getName();
                str = "Before Transformer %s: %d";
                objArr = new Object[2];
                objArr[0] = transName;
                objArr[1] = Integer.valueOf(basicClass == null ? 0 : basicClass.length);
                LogWrapper.finest(str, objArr);
                basicClass = transformer.transform(name, transformedName, basicClass);
                str = "After  Transformer %s: %d";
                objArr = new Object[2];
                objArr[0] = transName;
                if (basicClass == null) {
                    i2 = 0;
                } else {
                    i2 = basicClass.length;
                }
                objArr[1] = Integer.valueOf(i2);
                LogWrapper.finest(str, objArr);
            }
            String str2 = "Ending transform of %s (%s) Start Length: %d";
            Object[] objArr2 = new Object[3];
            objArr2[0] = name;
            objArr2[1] = transformedName;
            if (basicClass != null) {
                i = basicClass.length;
            }
            objArr2[2] = Integer.valueOf(i);
            LogWrapper.finest(str2, objArr2);
        } else {
            for (IClassTransformer transformer2 : this.transformers) {
                basicClass = transformer2.transform(name, transformedName, basicClass);
            }
        }
        return basicClass;
    }

    public void addURL(URL url) {
        //super.addURL(url);
        this.sources.add(url);
    }

    public List<URL> getSources() {
        return this.sources;
    }

    private byte[] readFully(InputStream stream) {
        try {
            byte[] buffer = getOrCreateBuffer();
            int totalLength = 0;
            while (true) {
                int read = stream.read(buffer, totalLength, buffer.length - totalLength);
                if (read != -1) {
                    totalLength += read;
                    if (totalLength >= buffer.length - 1) {
                        byte[] newBuffer = new byte[(buffer.length + BUFFER_SIZE)];
                        net.kdt.pojavlaunch.SystemCrackResolver.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                        buffer = newBuffer;
                    }
                } else {
                    byte[] result = new byte[totalLength];
                    net.kdt.pojavlaunch.SystemCrackResolver.arraycopy(buffer, 0, result, 0, totalLength);
                    return result;
                }
            }
        } catch (Throwable t) {
            LogWrapper.log(Level.WARNING, t, "Problem loading class", new Object[0]);
            return new byte[0];
        }
    }

    private byte[] getOrCreateBuffer() {
        byte[] buffer = (byte[]) this.loadBuffer.get();
        if (buffer != null) {
            return buffer;
        }
        this.loadBuffer.set(new byte[BUFFER_SIZE]);
        return (byte[]) this.loadBuffer.get();
    }

    public List<IClassTransformer> getTransformers() {
        return Collections.unmodifiableList(this.transformers);
    }

    public void addClassLoaderExclusion(String toExclude) {
        this.classLoaderExceptions.add(toExclude);
    }

    public void addTransformerExclusion(String toExclude) {
        this.transformerExceptions.add(toExclude);
    }

    public byte[] getClassBytes(String name) throws IOException {
        byte[] data;
        if (name.indexOf(46) == -1) {
            for (String reservedName : RESERVED_NAMES) {
                if (name.toUpperCase(Locale.ENGLISH).startsWith(reservedName)) {
                    data = getClassBytes("_" + name);
                    if (data != null) {
                        return data;
                    }
                }
            }
        }
        try {
            URL classResource = findResource(name.replace('.', '/').concat(".class"));
            if (classResource == null) {
                if (DEBUG) {
                    LogWrapper.finest("Failed to find class resource"); // %s", new Object[]{resourcePath});
                }
                closeSilently(null);
                return null;
            }
            InputStream classStream = classResource.openStream();
            if (DEBUG) {
                LogWrapper.finest("Loading class %s from resource %s", new Object[]{name, classResource.toString()});
            }
            data = readFully(classStream);
            closeSilently(classStream);
            return data;
        } catch (Throwable th) {
            closeSilently(null);
			// Mod
			return null;
        }
    }

    private static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }
}

