package net.minecraft.launchwrapper;

import java.io.*;
import java.lang.reflect.*;
import java.util.logging.*;
import java.util.logging.Level;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Launch {
    private static final String DEFAULT_TWEAK = "net.minecraft.launchwrapper.VanillaTweaker";
    public static File assetsDir;
    public static LaunchClassLoader classLoader;
    public static File minecraftHome;

    public static void main(String[] args) {
        new Launch().launch(args);
    }

    private Launch() {
        classLoader = (LaunchClassLoader) Thread.currentThread().getContextClassLoader(); // getClass().getClassLoader(); //((URLClassLoader) getClass().getClassLoader()).getURLs());
    }

    private void launch(String[] args) {
        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        OptionSpec<String> profileOption = parser.accepts("version", "The version we launched with").withRequiredArg();
        OptionSpec<File> gameDirOption = parser.accepts("gameDir", "Alternative game directory").withRequiredArg().ofType(File.class);
        OptionSpec<File> assetsDirOption = parser.accepts("assetsDir", "Assets directory").withRequiredArg().ofType(File.class);
        OptionSpec<String> tweakClassOption = parser.accepts("tweakClass", "Tweak class to load").withRequiredArg().defaultsTo(DEFAULT_TWEAK, new String[0]);
        OptionSpec<String> nonOption = parser.nonOptions();
        OptionSet options = parser.parse(args);
        minecraftHome = (File) options.valueOf(gameDirOption);
        assetsDir = (File) options.valueOf(assetsDirOption);
        String profileName = (String) options.valueOf(profileOption);
        String tweakClassName = (String) options.valueOf(tweakClassOption);
        try {
            LogWrapper.log(Level.INFO, "Using tweak class name %s", new Object[]{tweakClassName});
            classLoader.addClassLoaderExclusion(tweakClassName.substring(0, tweakClassName.lastIndexOf(46)));
            ITweaker tweaker = (ITweaker) Class.forName(tweakClassName, true, classLoader).newInstance();
            tweaker.acceptOptions(options.valuesOf(nonOption), minecraftHome, assetsDir, profileName);
            tweaker.injectIntoClassLoader(classLoader);
            Method mainMethod = Class.forName(tweaker.getLaunchTarget(), false, classLoader).getMethod("main", new Class[]{String[].class});
            LogWrapper.info("Launching wrapped minecraft", new Object[0]);
            mainMethod.invoke(null, new Object[]{tweaker.getLaunchArguments()});
        } catch (Exception e) {
            LogWrapper.log(Level.SEVERE, e, "Unable to launch", new Object[0]);
        }
    }
}

