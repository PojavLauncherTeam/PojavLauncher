package net.kdt.pojavlaunch.multirt;

import android.content.Context;
import android.system.Os;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.utils.JREUtils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MultiRTUtils {
    public static HashMap<String,Runtime> cache = new HashMap<>();
    public static class Runtime {
        public Runtime(String name) {
            this.name = name;
        }
        public String name;
        public String versionString;
        public String arch;
        public int javaVersion;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Runtime runtime = (Runtime) o;
            return name.equals(runtime.name);
        }
        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
    public static interface ProgressReporterThingy {
        void reportStringProgress(int resid, Object ... stuff);
    }
    private static final File runtimeFolder = new File(Tools.MULTIRT_HOME);
    private static final String JAVA_VERSION_str = "JAVA_VERSION=\"";
    private static final String OS_ARCH_str = "OS_ARCH=\"";
    public static List<Runtime> getRuntimes() {
        if(!runtimeFolder.exists()) runtimeFolder.mkdirs();
        ArrayList<Runtime> ret = new ArrayList<>();
        System.out.println("Fetch runtime list");
        for(File f : runtimeFolder.listFiles()) {
            ret.add(read(f.getName()));
        }

        return ret;
    }
    public static String getNearestJREName(int majorVersion) {
        List<Runtime> runtimes = getRuntimes();
        int diff_factor = Integer.MAX_VALUE;
        String result = null;
        for(Runtime r : runtimes) {
            if(r.javaVersion >= majorVersion) { // lower - not useful
                int currentFactor = r.javaVersion - majorVersion;
                if(diff_factor > currentFactor) {
                    result = r.name;
                    diff_factor = currentFactor;
                }
            }
        }
        return result;
    }
    public static void installRuntimeNamed(InputStream runtimeInputStream, String name, ProgressReporterThingy thingy) throws IOException {
        File dest = new File(runtimeFolder,"/"+name);
        File tmp = new File(dest,"temporary");
        if(dest.exists()) FileUtils.deleteDirectory(dest);
        dest.mkdirs();
        FileOutputStream fos = new FileOutputStream(tmp);
        thingy.reportStringProgress(R.string.multirt_progress_caching);
        IOUtils.copy(runtimeInputStream,fos);
        fos.close();
        runtimeInputStream.close();
        uncompressTarXZ(tmp,dest,thingy);
        tmp.delete();
        read(name);
    }
    private static void __installRuntimeNamed__NoRM(InputStream runtimeInputStream, File dest, ProgressReporterThingy thingy) throws IOException {
        File tmp = new File(dest,"temporary");
        FileOutputStream fos = new FileOutputStream(tmp);
        thingy.reportStringProgress(R.string.multirt_progress_caching);
        IOUtils.copy(runtimeInputStream,fos);
        fos.close();
        runtimeInputStream.close();
        uncompressTarXZ(tmp,dest,thingy);
        tmp.delete();
    }
    public static void postPrepare(Context ctx, String name) throws IOException {
        File dest = new File(runtimeFolder,"/"+name);
        if(!dest.exists()) return;
        Runtime r = read(name);
        String libFolder = "lib";
        if(new File(dest,libFolder+"/"+r.arch).exists()) libFolder = libFolder+"/"+r.arch;
        File ftIn = new File(dest, libFolder+ "/libfreetype.so.6");
        File ftOut = new File(dest, libFolder + "/libfreetype.so");
        if (ftIn.exists() && (!ftOut.exists() || ftIn.length() != ftOut.length())) {
            ftIn.renameTo(ftOut);
        }

        // Refresh libraries
        copyDummyNativeLib(ctx,"libawt_xawt.so",dest,libFolder);
    }
    private static void copyDummyNativeLib(Context ctx, String name, File dest, String libFolder) throws IOException {

        File fileLib = new File(dest, "/"+libFolder + "/" + name);
        fileLib.delete();
        FileInputStream is = new FileInputStream(new File(ctx.getApplicationInfo().nativeLibraryDir, name));
        FileOutputStream os = new FileOutputStream(fileLib);
        IOUtils.copy(is, os);
        is.close();
        os.close();
    }
    public static Runtime installRuntimeNamedBinpack(InputStream universalFileInputStream, InputStream platformBinsInputStream, String name, String binpackVersion, ProgressReporterThingy thingy) throws IOException {
        File dest = new File(runtimeFolder,"/"+name);
        if(dest.exists()) FileUtils.deleteDirectory(dest);
        dest.mkdirs();
        __installRuntimeNamed__NoRM(universalFileInputStream,dest,thingy);
        __installRuntimeNamed__NoRM(platformBinsInputStream,dest,thingy);
        File binpack_verfile = new File(runtimeFolder,"/"+name+"/pojav_version");
        FileOutputStream fos = new FileOutputStream(binpack_verfile);
        fos.write(binpackVersion.getBytes());
        fos.close();
        cache.remove(name); // Force reread
        return read(name);
    }
    public static String __internal__readBinpackVersion(String name) {
        File binpack_verfile = new File(runtimeFolder,"/"+name+"/pojav_version");
        try {
            if (binpack_verfile.exists()) {
                return Tools.read(binpack_verfile.getAbsolutePath());
            }else{
                return null;
            }
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void removeRuntimeNamed(String name) throws IOException {
        File dest = new File(runtimeFolder,"/"+name);
        if(dest.exists()) {
            FileUtils.deleteDirectory(dest);
            cache.remove(name);
        }
    }
    public static void setRuntimeNamed(Context ctx, String name) throws IOException {
        File dest = new File(runtimeFolder,"/"+name);
        if((!dest.exists()) || MultiRTUtils.forceReread(name).versionString == null) throw new RuntimeException("Selected runtime is broken!");
        Tools.DIR_HOME_JRE = dest.getAbsolutePath();
        JREUtils.relocateLibPath(ctx);
    }
    public static Runtime forceReread(String name) {
        cache.remove(name);
        return read(name);
    }
    public static Runtime read(String name) {
        if(cache.containsKey(name)) return cache.get(name);
        Runtime retur;
        File release = new File(runtimeFolder,"/"+name+"/release");
        if(!release.exists()) {
            return new Runtime(name);
        }
        try {
            String content = Tools.read(release.getAbsolutePath());
            int _JAVA_VERSION_index = content.indexOf(JAVA_VERSION_str);
            int _OS_ARCH_index = content.indexOf(OS_ARCH_str);
            if(_JAVA_VERSION_index != -1 && _OS_ARCH_index != -1) {
                _JAVA_VERSION_index += JAVA_VERSION_str.length();
                _OS_ARCH_index += OS_ARCH_str.length();
                String javaVersion = content.substring(_JAVA_VERSION_index,content.indexOf('"',_JAVA_VERSION_index));
                    String[] javaVersionSplit = javaVersion.split("\\.");
                    int javaVersionInt;
                    if (javaVersionSplit[0].equals("1")) {
                        javaVersionInt = Integer.parseInt(javaVersionSplit[1]);
                    } else {
                        javaVersionInt = Integer.parseInt(javaVersionSplit[0]);
                    }
                    Runtime r = new Runtime(name);
                    r.arch = content.substring(_OS_ARCH_index,content.indexOf('"',_OS_ARCH_index));
                    r.javaVersion = javaVersionInt;
                    r.versionString = javaVersion;
                    retur = r;
            }else{
                retur =  new Runtime(name);
            }
        }catch(IOException e) {
            retur =  new Runtime(name);
        }
        cache.put(name,retur);
        return retur;
    }
    private static void uncompressTarXZ(final File tarFile, final File dest, final ProgressReporterThingy thingy) throws IOException {
        dest.mkdirs();
        TarArchiveInputStream tarIn = null;

        tarIn = new TarArchiveInputStream(
                new XZCompressorInputStream(
                        new BufferedInputStream(
                                new FileInputStream(tarFile)
                        )
                )
        );
        TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
        // tarIn is a TarArchiveInputStream
        while (tarEntry != null) {
            /*
             * Unpacking very small files in short time cause
             * application to ANR or out of memory, so delay
             * a little if size is below than 20kb (20480 bytes)
             */
            if (tarEntry.getSize() <= 20480) {
                try {
                    // 40 small files per second
                    Thread.sleep(25);
                } catch (InterruptedException ignored) {}
            }
            final String tarEntryName = tarEntry.getName();
            // publishProgress(null, "Unpacking " + tarEntry.getName());
            thingy.reportStringProgress(R.string.global_unpacking,tarEntryName);
            File destPath = new File(dest, tarEntry.getName());
            if (tarEntry.isSymbolicLink()) {
                destPath.getParentFile().mkdirs();
                try {
                    // android.system.Os
                    // Libcore one support all Android versions
                    Os.symlink(tarEntry.getName(), tarEntry.getLinkName());
                } catch (Throwable e) {
                    e.printStackTrace();
                }

            } else if (tarEntry.isDirectory()) {
                destPath.mkdirs();
                destPath.setExecutable(true);
            } else if (!destPath.exists() || destPath.length() != tarEntry.getSize()) {
                destPath.getParentFile().mkdirs();
                destPath.createNewFile();

                FileOutputStream os = new FileOutputStream(destPath);
                IOUtils.copy(tarIn, os);
                os.close();

            }
            tarEntry = tarIn.getNextTarEntry();
        }
        tarIn.close();
    }
}
