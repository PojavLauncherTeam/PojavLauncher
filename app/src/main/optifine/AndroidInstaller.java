package ../optifine;

import java.io.*;
import ../optifine.*;
import java.lang.reflect.*;
import Config;
import javax.swing.*;
import java.net.*;
import android.os.*;

public class AndroidInstaller
{
    public static void doAndroidInstall(File file) throws Exception {
        Utils.dbg("Dir minecraft: " + file);
        File file2 = new File(file, "libraries");
        Utils.dbg("Dir libraries: " + file2);
        File file3 = new File(file, "versions");
        Utils.dbg("Dir versions: " + file3);
        String optiFineVersion = Config.VERSION;
        Utils.dbg("OptiFine Version: " + optiFineVersion);
        String str = Config.MC_VERSION;
        Utils.dbg("Minecraft Version: " + str);
        optiFineVersion = Config.OF_EDITION;
        Utils.dbg("OptiFine Edition: " + optiFineVersion);
        String stringBuilder = str + "-OptiFine_" + optiFineVersion;
        Utils.dbg("Minecraft_OptiFine Version: " + stringBuilder);
		
        try {
			invokeStatic("copyMinecraftVersion", str, stringBuilder, file3);
			installOptiFineLibrary(str, optiFineVersion, file2);
			invokeStatic("updateJson", file3, stringBuilder, file2, str, optiFineVersion);
			invokeStatic("updateLauncherJson", file, stringBuilder);
		} catch (Throwable th) {
			throw new RuntimeException(th);
		}
    }
	
	private static boolean installOptiFineLibrary(String str, String str2, File file) throws Exception {
        File optiFineZipFile = getOptiFineZipFile();
        File file2 = new File(new File(file, "optifine/OptiFine/" + str + "_" + str2), "OptiFine-" + str + "_" + str2 + "_orig.jar");
        if (file2.equals(optiFineZipFile)) {
            JOptionPane.showMessageDialog(null, "Source and target file are the same.", "Save", 0);
            return false;
        }
        Utils.dbg("Source: " + optiFineZipFile);
        Utils.dbg("Dest: " + file2);
        File file3 = new File(file.getParentFile(), "versions/" + str + "/" + str + "_orig.jar");
		Utils.dbg("Minecraft: " + file3);
        if (file3.exists()) {
            if (file2.getParentFile() != null) {
                file2.getParentFile().mkdirs();
            }
            Patcher.process(file3, optiFineZipFile, file2);
            return true;
        }
        invokeStatic("showMessageVersionNotFound", str);
        throw new RuntimeException("QUIET");
    }
	
	public static File getOptiFineZipFile() throws Exception {
        // URL location = Installer.class.getProtectionDomain().getCodeSource().getLocation();
        URL location = new File(SwingScreen.MINECRAFT_DIR, "OptiFine_1.7.10_HD_U_E7.jar").toURL();
		Utils.dbg("URL: " + location);
        return new File(location.toURI());
    }
	
}
