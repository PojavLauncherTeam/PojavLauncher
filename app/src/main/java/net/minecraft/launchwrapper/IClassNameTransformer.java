package net.minecraft.launchwrapper;

public interface IClassNameTransformer {
    String remapClassName(String str);

    String unmapClassName(String str);
}

