package net.minecraft.launchwrapper;

import java.io.File;
import java.util.List;

public class AlphaVanillaTweaker implements ITweaker {
    private List<String> args;

    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.args = args;
    }

    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        classLoader.registerTransformer("net.minecraft.launchwrapper.injector.AlphaVanillaTweakInjector");
    }

    public String getLaunchTarget() {
        return "net.minecraft.launchwrapper.injector.AlphaVanillaTweakInjector";
    }

    public String[] getLaunchArguments() {
        return (String[]) this.args.toArray(new String[this.args.size()]);
    }
}

