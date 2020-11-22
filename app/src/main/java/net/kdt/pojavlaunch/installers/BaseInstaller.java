package net.kdt.pojavlaunch.installers;

import java.io.*;
import java.util.jar.*;
import net.kdt.pojavlaunch.*;

public class BaseInstaller {
    protected File mFile;
    protected JarFile mJarFile;

    public void setInput(File file) throws IOException {
        mFile = file;
        mJarFile = new JarFile(file);
    }
    
    public void install(LoggableActivity ctx) throws IOException {}
    
    public void from(BaseInstaller base) {
        mFile = base.mFile;
        mJarFile = base.mJarFile;
    }
}
