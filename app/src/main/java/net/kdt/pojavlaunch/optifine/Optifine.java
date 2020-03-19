package net.kdt.pojavlaunch.optifine;

import com.google.gson.*;
import java.nio.charset.*;
import net.kdt.pojavlaunch.value.*;
import net.kdt.pojavlaunch.util.*;
import java.io.*;
import net.kdt.pojavlaunch.*;

public class Optifine
{
	public static OptifineVersion.VersionList getList() throws Exception
	{
		String optifineStr = DownloadUtils.downloadString(Tools.mhomeUrl + "/optifine.json");
		return new Gson().fromJson(optifineStr, OptifineVersion.VersionList.class);
	}
}
