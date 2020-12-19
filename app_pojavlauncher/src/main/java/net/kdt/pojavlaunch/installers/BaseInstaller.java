package net.kdt.pojavlaunch.installers;

import java.io.*;
import java.util.zip.*;
import net.kdt.pojavlaunch.*;

public class BaseInstaller {
    protected File mFile;
    protected ZipFile mJarFile;

    public void setInput(File file) throws IOException {
        mFile = file;
        mJarFile = new ZipFile(file);
    }
    
    public int install(JavaGUILauncherActivity ctx) throws IOException {
        return 0;
    }
    
    public void from(BaseInstaller base) {
        mFile = base.mFile;
        mJarFile = base.mJarFile;
    }
}
