// 
// Decompiled by Procyon v0.5.36
// 

package ../optifine;

import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.io.File;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.zip.ZipFile;
import net.minecraft.launchwrapper.IClassTransformer;

public class OptiFineClassTransformer implements IClassTransformer, IResourceProvider
{
    private ZipFile ofZipFile;
    private Map<String, String> patchMap;
    private Pattern[] patterns;
    public static OptiFineClassTransformer instance;
    
    static {
        OptiFineClassTransformer.instance = null;
    }
    
    public OptiFineClassTransformer() {
        this.ofZipFile = null;
        this.patchMap = null;
        this.patterns = null;
        OptiFineClassTransformer.instance = this;
        try {
            dbg("OptiFine ClassTransformer");
            final URL url = OptiFineClassTransformer.class.getProtectionDomain().getCodeSource().getLocation();
            final URI uri = url.toURI();
            final File file = new File(uri);
            this.ofZipFile = new ZipFile(file);
            dbg("OptiFine ZIP file: " + file);
            this.patchMap = Patcher.getConfigurationMap(this.ofZipFile);
            this.patterns = Patcher.getConfigurationPatterns(this.patchMap);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (this.ofZipFile == null) {
            dbg("*** Can not find the OptiFine JAR in the classpath ***");
            dbg("*** OptiFine will not be loaded! ***");
        }
    }
    
    public byte[] transform(final String name, final String transformedName, final byte[] bytes) {
        final String nameClass = String.valueOf(name) + ".class";
        final byte[] ofBytes = this.getOptiFineResource(nameClass);
        if (ofBytes != null) {
            return ofBytes;
        }
        return bytes;
    }
    
    public InputStream getResourceStream(String path) {
        path = Utils.ensurePrefix(path, "/");
        return OptiFineClassTransformer.class.getResourceAsStream(path);
    }
    
    public synchronized byte[] getOptiFineResource(String name) {
        name = Utils.removePrefix(name, "/");
        byte[] bytes = this.getOptiFineResourceZip(name);
        if (bytes != null) {
            return bytes;
        }
        bytes = this.getOptiFineResourcePatched(name, this);
        if (bytes != null) {
            return bytes;
        }
        return null;
    }
    
    public synchronized byte[] getOptiFineResourceZip(String name) {
        if (this.ofZipFile == null) {
            return null;
        }
        name = Utils.removePrefix(name, "/");
        final ZipEntry ze = this.ofZipFile.getEntry(name);
        if (ze == null) {
            return null;
        }
        try {
            final InputStream in = this.ofZipFile.getInputStream(ze);
            final byte[] bytes = readAll(in);
            in.close();
            if (bytes.length != ze.getSize()) {
                dbg("Invalid size, name: " + name + ", zip: " + ze.getSize() + ", stream: " + bytes.length);
                return null;
            }
            return bytes;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public synchronized byte[] getOptiFineResourcePatched(String name, final IResourceProvider resourceProvider) {
        if (this.patterns == null || this.patchMap == null || resourceProvider == null) {
            return null;
        }
        name = Utils.removePrefix(name, "/");
        final String patchName = "patch/" + name + ".xdelta";
        final byte[] bytes = this.getOptiFineResourceZip(patchName);
        if (bytes == null) {
            return null;
        }
        try {
            final byte[] bytesPatched = Patcher.applyPatch(name, bytes, this.patterns, this.patchMap, resourceProvider);
            final String fullMd5Name = "patch/" + name + ".md5";
            final byte[] bytesMd5 = this.getOptiFineResourceZip(fullMd5Name);
            if (bytesMd5 != null) {
                final String md5Str = new String(bytesMd5, "ASCII");
                final byte[] md5Mod = HashUtils.getHashMd5(bytesPatched);
                final String md5ModStr = HashUtils.toHexString(md5Mod);
                if (!md5Str.equals(md5ModStr)) {
                    throw new IOException("MD5 not matching, name: " + name + ", saved: " + md5Str + ", patched: " + md5ModStr);
                }
            }
            return bytesPatched;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static byte[] readAll(final InputStream is) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] buf = new byte[1024];
        while (true) {
            final int len = is.read(buf);
            if (len < 0) {
                break;
            }
            baos.write(buf, 0, len);
        }
        is.close();
        final byte[] bytes = baos.toByteArray();
        return bytes;
    }
    
    private static void dbg(final String str) {
        System.out.println(str);
    }
}
