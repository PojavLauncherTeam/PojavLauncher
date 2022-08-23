package net.kdt.pojavlaunch.multirt;

import static org.apache.commons.io.FileUtils.listFiles;

import android.content.Context;
import android.system.Os;
import android.util.Log;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MultiRTUtils {
    public interface RuntimeProgressReporter {
        void reportStringProgress(int resId, Object ... stuff);
    }

    private static final HashMap<String,Runtime> sCache = new HashMap<>();

    private static final File RUNTIME_FOLDER = new File(Tools.MULTIRT_HOME);
    private static final String JAVA_VERSION_STR = "JAVA_VERSION=\"";
    private static final String OS_ARCH_STR = "OS_ARCH=\"";

    public static List<Runtime> getRuntimes() {
        if(!RUNTIME_FOLDER.exists()) RUNTIME_FOLDER.mkdirs();

        ArrayList<Runtime> runtimes = new ArrayList<>();
        System.out.println("Fetch runtime list");
        for(File f : RUNTIME_FOLDER.listFiles()) {
            runtimes.add(read(f.getName()));
        }

        return runtimes;
    }

    public static String getExactJreName(int majorVersion) {
        List<Runtime> runtimes = getRuntimes();
        for(Runtime r : runtimes)
            if(r.javaVersion == majorVersion)return r.name;

        return null;
    }

    public static String getNearestJreName(int majorVersion) {
        List<Runtime> runtimes = getRuntimes();
        int diff_factor = Integer.MAX_VALUE;
        String result = null;
        for(Runtime r : runtimes) {
            if(r.javaVersion < majorVersion) continue; // lower - not useful

            int currentFactor = r.javaVersion - majorVersion;
            if(diff_factor > currentFactor) {
                result = r.name;
                diff_factor = currentFactor;
            }
        }

        return result;
    }

    public static void installRuntimeNamed(String nativeLibDir, InputStream runtimeInputStream, String name, RuntimeProgressReporter progressReporter) throws IOException {
        File dest = new File(RUNTIME_FOLDER,"/"+name);
        if(dest.exists()) FileUtils.deleteDirectory(dest);
        dest.mkdirs();

        uncompressTarXZ(runtimeInputStream,dest,progressReporter);
        runtimeInputStream.close();
        unpack200(nativeLibDir,RUNTIME_FOLDER + "/" + name);
        read(name);
    }

    public static void postPrepare(Context ctx, String name) throws IOException {
        File dest = new File(RUNTIME_FOLDER,"/" + name);
        if(!dest.exists()) return;
        Runtime runtime = read(name);
        String libFolder = "lib";
        if(new File(dest,libFolder + "/" + runtime.arch).exists()) libFolder = libFolder + "/" + runtime.arch;
        File ftIn = new File(dest, libFolder + "/libfreetype.so.6");
        File ftOut = new File(dest, libFolder + "/libfreetype.so");
        if (ftIn.exists() && (!ftOut.exists() || ftIn.length() != ftOut.length())) {
            ftIn.renameTo(ftOut);
        }

        // Refresh libraries
        copyDummyNativeLib(ctx,"libawt_xawt.so", dest, libFolder);
    }

    public static Runtime installRuntimeNamedBinpack(String nativeLibDir, InputStream universalFileInputStream, InputStream platformBinsInputStream, String name, String binpackVersion, RuntimeProgressReporter thingy) throws IOException {
        File dest = new File(RUNTIME_FOLDER,"/"+name);
        if(dest.exists()) FileUtils.deleteDirectory(dest);
        dest.mkdirs();
        installRuntimeNamedNoRemove(universalFileInputStream,dest,thingy);
        installRuntimeNamedNoRemove(platformBinsInputStream,dest,thingy);
        File binpack_verfile = new File(RUNTIME_FOLDER,"/"+name+"/pojav_version");
        FileOutputStream fos = new FileOutputStream(binpack_verfile);
        fos.write(binpackVersion.getBytes());
        fos.close();

        unpack200(nativeLibDir,RUNTIME_FOLDER + "/" + name);

        sCache.remove(name); // Force reread
        return read(name);
    }


    public static String __internal__readBinpackVersion(String name) {
        File binpack_verfile = new File(RUNTIME_FOLDER,"/"+name+"/pojav_version");
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
        File dest = new File(RUNTIME_FOLDER,"/"+name);
        if(dest.exists()) {
            FileUtils.deleteDirectory(dest);
            sCache.remove(name);
        }
    }

    public static void setRuntimeNamed(Context ctx, String name) throws IOException {
        File dest = new File(RUNTIME_FOLDER,"/"+name);
        if((!dest.exists()) || MultiRTUtils.forceReread(name).versionString == null) throw new RuntimeException("Selected runtime is broken!");
        Tools.DIR_HOME_JRE = dest.getAbsolutePath();
        JREUtils.relocateLibPath(ctx);
    }

    public static Runtime forceReread(String name) {
        sCache.remove(name);
        return read(name);
    }

    public static Runtime read(String name) {
        if(sCache.containsKey(name)) return sCache.get(name);
        Runtime returnRuntime;
        File release = new File(RUNTIME_FOLDER,"/"+name+"/release");
        if(!release.exists()) {
            return new Runtime(name);
        }
        try {
            String content = Tools.read(release.getAbsolutePath());
            int javaVersionIndex = content.indexOf(JAVA_VERSION_STR);
            int osArchIndex = content.indexOf(OS_ARCH_STR);
            if(javaVersionIndex != -1 && osArchIndex != -1) {
                javaVersionIndex += JAVA_VERSION_STR.length();
                osArchIndex += OS_ARCH_STR.length();
                String javaVersion = content.substring(javaVersionIndex,content.indexOf('"', javaVersionIndex));
                    String[] javaVersionSplit = javaVersion.split("\\.");
                    int javaVersionInt;
                    if (javaVersionSplit[0].equals("1")) {
                        javaVersionInt = Integer.parseInt(javaVersionSplit[1]);
                    } else {
                        javaVersionInt = Integer.parseInt(javaVersionSplit[0]);
                    }
                    Runtime runtime = new Runtime(name);
                    runtime.arch = content.substring(osArchIndex,content.indexOf('"', osArchIndex));
                    runtime.javaVersion = javaVersionInt;
                    runtime.versionString = javaVersion;
                    returnRuntime = runtime;
            }else{
                returnRuntime =  new Runtime(name);
            }
        }catch(IOException e) {
            returnRuntime =  new Runtime(name);
        }
        sCache.put(name, returnRuntime);
        return returnRuntime;
    }

    /**
     * Unpacks all .pack files into .jar
     * @param nativeLibraryDir The native lib path, required to execute the unpack200 binary
     * @param runtimePath The path to the runtime to walk into
     */
    private static void unpack200(String nativeLibraryDir, String runtimePath) {

        File basePath = new File(runtimePath);
        Collection<File> files = listFiles(basePath, new String[]{"pack"}, true);

        File workdir = new File(nativeLibraryDir);

        ProcessBuilder processBuilder = new ProcessBuilder().directory(workdir);
        for(File jarFile : files){
            try{
                Process process = processBuilder.command("./libunpack200.so", "-r", jarFile.getAbsolutePath(), jarFile.getAbsolutePath().replace(".pack", "")).start();
                process.waitFor();
            }catch (InterruptedException | IOException e) {
                Log.e("MULTIRT", "Failed to unpack the runtime !");
            }
        }
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

    private static void installRuntimeNamedNoRemove(InputStream runtimeInputStream, File dest, RuntimeProgressReporter progressReporter) throws IOException {

        uncompressTarXZ(runtimeInputStream,dest,progressReporter);
        runtimeInputStream.close();
    }

    private static void uncompressTarXZ(final InputStream tarFileInputStream, final File dest, final RuntimeProgressReporter thingy) throws IOException {
        dest.mkdirs();

        TarArchiveInputStream tarIn = new TarArchiveInputStream(
                new XZCompressorInputStream(tarFileInputStream)
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
                    Log.e("MultiRT", e.toString());
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
