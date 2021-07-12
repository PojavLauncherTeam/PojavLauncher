package net.kdt.pojavlaunch.utils;

import android.annotation.SuppressLint;
import android.system.Os;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;

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
    public static HashMap<String,Runtime> cache = new HashMap();
    public static class Runtime {
        public Runtime(String name) {
            this.name = name;
        }
        public String name;
        public String versionString;
        public int javaVersion;
    }
    private static File runtimeFolder = new File(Tools.MULTIRT_HOME);
    private static final String JAVA_VERSION_str = "JAVA_VERSION=\"";
    public static List<Runtime> getRuntimes() {
        ArrayList<Runtime> ret = new ArrayList<>();
        for(File f : runtimeFolder.listFiles()) {
            ret.add(read(f.getName()));
        }
        return ret;
    }
    public static Runtime installRuntimeNamed(InputStream runtimeInputStream, String name) throws IOException {
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
        return read(name);
    }
    public static void removeRuntimeNamed(String name) throws IOException {
        File dest = new File(runtimeFolder,"/"+name);
        if(dest.exists()) {
            FileUtils.deleteDirectory(dest);
            cache.remove(name);
        }
    }
    private static Runtime read(String name) {
        if(cache.containsKey(name)) return cache.get(name);
        Runtime retur;
        File release = new File(runtimeFolder,"/"+name+"/release");
        if(!release.exists()) {
            return null;
        }
        try {
            String content = Tools.read(release.getAbsolutePath());
            int _JAVA_VERSION_index = content.indexOf(JAVA_VERSION_str);
            if(_JAVA_VERSION_index != -1) {
                _JAVA_VERSION_index += JAVA_VERSION_str.length();
                String javaVersion = content.substring(_JAVA_VERSION_index,content.indexOf('"',_JAVA_VERSION_index));
                String[] javaVersionSplit = javaVersion.split(".");
                int javaVersionInt;
                if(javaVersionSplit[0].equals("1")) {
                    javaVersionInt = Integer.parseInt(javaVersionSplit[1]);
                }else{
                    javaVersionInt = Integer.parseInt(javaVersionSplit[0]);
                }
                Runtime r = new Runtime(name);
                r.javaVersion = javaVersionInt;
                r.versionString = javaVersion;
                retur =  r;
            }else{
                retur =  new Runtime(name);
            }
        }catch(IOException e) {
            retur =  new Runtime(name);
        }
        cache.put(name,retur);
        return retur;
    }
    private static void uncompressTarXZ(final File tarFile, final File dest) throws IOException {
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
                } catch (InterruptedException e) {}
            }
            final String tarEntryName = tarEntry.getName();
            // publishProgress(null, "Unpacking " + tarEntry.getName());
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
