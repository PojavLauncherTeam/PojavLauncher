package net.kdt.pojavlaunch.utils;

import android.util.Log;

import androidx.collection.ArrayMap;

import net.fornwall.jelf.ElfDynamicSection;
import net.fornwall.jelf.ElfFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElfSequencer {
    private final List<String> alreadyLoadedLibs = new ArrayList<>(Collections.singletonList( // Automatically include ld-android.so
            "ld-android.so" //Most system libraries, for some reason, have this file in DYNAMIC, yet it's not loadable due to Android loader namespaces
    )); // Since it's technically already loaded, we add it here by default
    private final List<ArrayMap<String, File>> dirCache = new ArrayList<>(); //Directory cache - here are all the files in loader path. Well, they would appear there after class' construction
    /*
     * Constructs an ElfSequencer with specified search paths. Search paths should be a list of directory paths separated by :
     */
    public ElfSequencer(String libraryPaths) {
        for(String path : libraryPaths.split(":")) {
            ArrayMap<String, File> caching = new ArrayMap<>();
            File[] filesInDir = new File(path).listFiles((pathname)->pathname.isFile() && pathname.exists());
            if(filesInDir != null) {
                for (File file : filesInDir) {
                    caching.put(file.getName(), file);
                }
                dirCache.add(caching);
            }else{
                Log.w("ElfLoader","Omitted directory during initialization: "+path);
            }
        }
    }
    /*
     * Loads a library by it's name, searching in the specified search paths.
     */
    public void loadLib(String libName) throws IOException {
        if(alreadyLoadedLibs.contains(libName))
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
        ElfDynamicSection section = file.firstSectionByType(ElfDynamicSection.class);
        List<String> needed = section.getNeededLibraries();
        for(String neededLibrary : needed) {
            loadLib(neededLibrary);
        }
        JREUtils.dlopen(library.getAbsolutePath());
        alreadyLoadedLibs.add(libName);
    }
}
