package net.minecraft.launchwrapper;

public abstract interface IClassNameTransformer
{
  public abstract String unmapClassName(String paramString);
  
  public abstract String remapClassName(String paramString);
}
