package net.kdt.pojavlaunch.multirt;

import android.content.Context;
import android.system.Os;
import android.util.Log;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.extra.ExtraCore;
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


	/** @return The name of the runtime with the nearest version  */
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


    /** Increment the amount of runtimes being installed  */
    private static void incrementRuntimeBeingInstalled(){
        Integer status =  (Integer) ExtraCore.getValue("runtime_status");
        if(status == null) status = 0;
        ExtraCore.setValue("runtime_status", status + 1);
    }

    /** Decrement the amount of runtimes being installed */
    private static void decrementRuntimeBeingInstalled(){
        Integer status =  (Integer) ExtraCore.getValue("runtime_status");
        if(status == null) status = 1;
        ExtraCore.setValue("runtime_status", status - 1);
    }

    // TODO make a function to deal with checking installation in progress ?

    /** Install the runtime in its respective folder, removing previous install if on the same folder */
    public static void installRuntimeNamed(InputStream runtimeInputStream, String name, String libraryPath) throws IOException {
        // Notify ExtraCore
        incrementRuntimeBeingInstalled();

        File dest = new File(runtimeFolder,"/"+name);
        File tmp = new File(dest,"temporary");
        if(dest.exists()) FileUtils.deleteDirectory(dest);
        dest.mkdirs();
        FileOutputStream fos = new FileOutputStream(tmp);

        IOUtils.copy(runtimeInputStream,fos);
        fos.close();
        runtimeInputStream.close();
        uncompressTarXZ(tmp,dest);
        tmp.delete();
        read(name);

        postPrepare(libraryPath, name);

        decrementRuntimeBeingInstalled();
    }

    /** Same as installRuntimeNamed, but without removal of the dest file if it exists */
    private static void __installRuntimeNamed__NoRM(InputStream runtimeInputStream, File dest) throws IOException {
        File tmp = new File(dest,"temporary");
        FileOutputStream fos = new FileOutputStream(tmp);

        IOUtils.copy(runtimeInputStream,fos);
        fos.close();
        runtimeInputStream.close();
        uncompressTarXZ(tmp,dest);
        tmp.delete();
    }

    /**
     * Setup libraries in the runtime after installation
     * @param libraryPath library path to the dummy library
     * @param name The name of the runtime
     * @throws IOException
     */
    private static void postPrepare(String libraryPath, String name) throws IOException {
        File dest = new File(runtimeFolder,"/" + name);
        if(!dest.exists()) return;

        Runtime runtime = read(name);
        String libFolder = "lib";
        if(new File(dest,libFolder + "/" + runtime.arch).exists())
            libFolder = libFolder + "/" + runtime.arch;
        File ftIn = new File(dest, libFolder+ "/libfreetype.so.6");
        File ftOut = new File(dest, libFolder + "/libfreetype.so");
        if (ftIn.exists() && (!ftOut.exists() || ftIn.length() != ftOut.length())) {
            ftIn.renameTo(ftOut);
        }

        // Refresh libraries
        copyDummyNativeLib(libraryPath,"libawt_xawt.so",dest,libFolder);
    }


    private static void copyDummyNativeLib(String libraryPath, String name, File dest, String libFolder) throws IOException {
        File fileLib = new File(dest, "/"+libFolder + "/" + name);
        fileLib.delete();
        FileInputStream is = new FileInputStream(new File(libraryPath, name));
        FileOutputStream os = new FileOutputStream(fileLib);
        IOUtils.copy(is, os);
        is.close();
        os.close();
    }

    /** Installs the internal runtime in 2 parts
     * @param universalFileInputStream The universal part
     * @param platformBinsInputStream The platform specific part
     * @param name The name the installed runtime will get
     * @param binpackVersion The version on the runtime
     * @return The Runtime representation
     * @throws IOException
     */
    public static Runtime installRuntimeNamedBinpack(InputStream universalFileInputStream,
                                                     InputStream platformBinsInputStream,
                                                     String name,
                                                     String binpackVersion,
                                                     String libraryPath) throws IOException {
        // Notify ExtraCore
        incrementRuntimeBeingInstalled();

        File dest = new File(runtimeFolder,"/"+name);
        if(dest.exists()) FileUtils.deleteDirectory(dest);
        dest.mkdirs();
        __installRuntimeNamed__NoRM(universalFileInputStream,dest);
        __installRuntimeNamed__NoRM(platformBinsInputStream,dest);
        File binpack_verfile = new File(runtimeFolder,"/" + name + "/pojav_version");
        FileOutputStream fos = new FileOutputStream(binpack_verfile);
        fos.write(binpackVersion.getBytes());
        fos.close();
        cache.remove(name); // Force reread

        // Install libraries
        postPrepare(libraryPath, "Internal");

        decrementRuntimeBeingInstalled();
        return read(name);
    }

    public static String __internal__readBinpackVersion(String name) {
        File binpack_version_file = new File(runtimeFolder,"/"+name+"/pojav_version");
        try {
            if (binpack_version_file.exists()) {
                return Tools.read(binpack_version_file.getAbsolutePath());
            }
        }catch (IOException e) {e.printStackTrace();}

        return null;
    }

    /** Delete the runtime, and its cached representation */
    public static void removeRuntimeNamed(String name) throws IOException {
        File dest = new File(RUNTIME_FOLDER,"/"+name);
        if(dest.exists()) {
            FileUtils.deleteDirectory(dest);
            sCache.remove(name);
        }
    }


    /** Set the default runtime to be the selected one */
    public static void setRuntimeNamed(Context ctx, String name) throws IOException {
        File dest = new File(runtimeFolder,"/"+name);
        if((!dest.exists()) || MultiRTUtils.forceReread(name).versionString == null)
            throw new RuntimeException("Selected runtime is broken!");

        Tools.DIR_HOME_JRE = dest.getAbsolutePath();
        JREUtils.relocateLibPath(ctx);
    }


    /** @return Runtime, as fresh as it could be */
    public static Runtime forceReread(String name) {
        sCache.remove(name);
        return read(name);
    }


    /** @return Runtime, maybe cached */
    public static Runtime read(String name) {
        // Cached runtime verification
        if(cache.containsKey(name)) return cache.get(name);

        // Not cached, read from scratch
        Runtime runtime;
        File release = new File(runtimeFolder,"/" + name + "/release");
>>>>>>> W.I.P runtime installation and component unpacking
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
                    Runtime r = new Runtime(name);
                    r.arch = content.substring(_OS_ARCH_index,content.indexOf('"',_OS_ARCH_index));
                    r.javaVersion = javaVersionInt;
                    r.versionString = javaVersion;
                    runtime = r;
            }else{
                runtime =  new Runtime(name);
            }
        }catch(IOException e) {
            runtime =  new Runtime(name);
        }
        cache.put(name,runtime);
        return runtime;
    }


    private static void uncompressTarXZ(final File tarFile, final File dest) throws IOException {
        dest.mkdirs();

        TarArchiveInputStream tarIn = new TarArchiveInputStream(
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
