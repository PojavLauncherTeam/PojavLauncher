package net.kdt.pojavlaunch.utils;

import android.util.Log;

import androidx.collection.ArrayMap;

import net.fornwall.jelf.ElfDynamicSection;
import net.fornwall.jelf.ElfFile;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ElfSequencer {
    static final List<String> EXCLUDED_LIBRARIES = Arrays.asList(
            "ld-android.so"
    );

    List<String> alreadyLoadedLibs = new ArrayList<>();
    List<ArrayMap<String, File>> dirCache = new ArrayList<>();
    public ElfSequencer(String libraryPaths) {
        for(String s : libraryPaths.split(":")) {
            ArrayMap<String, File> caching = new ArrayMap<>();
            File[] filesInDir = new File(s).listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile() && pathname.exists();
                }
            });
            if(filesInDir != null) {
                for (File f : filesInDir) {
                    caching.put(f.getName(), f);
                }
                dirCache.add(caching);
            }else{
                Log.w("ElfLoader","Omitted directory during initialization: "+s);
            }
        }
    }
    public void loadLib(String libName) throws IOException {
        if(alreadyLoadedLibs.contains(libName) || EXCLUDED_LIBRARIES.contains(libName))
            return;
        Log.i("ElfLoader", "Loading library "+libName);
        File library = null;
        for(ArrayMap<String, File> fileCache : dirCache) {
            if(fileCache.containsKey(libName)) {
                library = fileCache.get(libName);
                break;
            }
        }
        if(library == null) {
            Log.w("ElfSequencer","Library " +libName + " not found in search paths");
            return;
        }
        ElfFile file = ElfFile.from(library);
        ElfDynamicSection section = (ElfDynamicSection) file.firstSectionByType(ElfDynamicSection.class);
        List<String> needed = section.getNeededLibraries();
        for(String _libName : needed) {
            loadLib(_libName);
        }
        JREUtils.dlopen(library.getAbsolutePath());
        alreadyLoadedLibs.add(libName);
    }
}
