package net.minecraft.launchwrapper;

import java.io.File;
import java.util.List;

public interface ITweaker {
    void acceptOptions(List<String> list, File file, File file2, String str);

    String[] getLaunchArguments();

    String getLaunchTarget();

    void injectIntoClassLoader(LaunchClassLoader launchClassLoader);
}

