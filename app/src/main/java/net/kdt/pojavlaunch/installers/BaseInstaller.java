package net.kdt.pojavlaunch.installers;

import android.content.*;
import java.io.*;

public abstract class BaseInstaller {
    protected File mJarFile;

    public void setInput(File jarFile) {
        mJarFile = jarFile;
    }
    
    public abstract void install(Context ctx) throws IOException;
}
