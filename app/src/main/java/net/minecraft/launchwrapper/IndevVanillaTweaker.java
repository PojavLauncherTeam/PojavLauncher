package net.minecraft.launchwrapper;

import java.util.List;

public class IndevVanillaTweaker implements ITweaker {
  private List<String> args;
  
  public IndevVanillaTweaker() {}
  
  public void acceptOptions(List<String> args, java.io.File gameDir, java.io.File assetsDir, String profile) {
    this.args = args;
  }
  
  public void injectIntoClassLoader(LaunchClassLoader classLoader)
  {
    classLoader.registerTransformer("net.minecraft.launchwrapper.injector.IndevVanillaTweakInjector");
  }
  
  public String getLaunchTarget()
  {
    return "net.minecraft.launchwrapper.injector.AlphaVanillaTweakInjector";
  }
  
  public String[] getLaunchArguments()
  {
    return (String[])args.toArray(new String[args.size()]);
  }
}
