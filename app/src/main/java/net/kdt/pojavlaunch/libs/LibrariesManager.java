package net.kdt.pojavlaunch.libs;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.*;
import net.kdt.pojavlaunch.value.*;
import net.kdt.pojavlaunch.*;

public class LibrariesManager
{
	public static MinecraftVersion getVersionInfo(String versionJsonDir) throws Exception {
        String versionStr = Tools.read(versionJsonDir);
        return new Gson().fromJson(versionStr, MinecraftVersion.class);
    }
}
