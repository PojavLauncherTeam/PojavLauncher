package net.minecraft.launchwrapper;

import java.io.File;
import java.util.List;

public abstract interface ITweaker
{
  public abstract void acceptOptions(List<String> paramList, File paramFile1, File paramFile2, String paramString);
  
  public abstract void injectIntoClassLoader(LaunchClassLoader paramLaunchClassLoader);
  
  public abstract String getLaunchTarget();
  
  public abstract String[] getLaunchArguments();
}
