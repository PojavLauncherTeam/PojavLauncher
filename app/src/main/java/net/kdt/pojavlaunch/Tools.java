package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import com.google.gson.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import net.kdt.pojavlaunch.util.*;
import net.kdt.pojavlaunch.value.*;
import org.apache.commons.codec.digest.*;
import net.kdt.pojavlaunch.patcher.*;
import java.lang.reflect.*;
import dalvik.system.*;
import optifine.*;
import android.text.*;
import java.awt.*;
import javax.xml.transform.*;
import java.awt.datatransfer.*;

public final class Tools
{
	public static boolean enableDevFeatures = BuildConfig.DEBUG;
	
	public static String APP_NAME = "null";
	public static String MAIN_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/minecraft";
	public static String ASSETS_PATH = MAIN_PATH + "/assets";
	public static String CTRLMAP_PATH = MAIN_PATH + "/controlmap";
	public static String CTRLDEF_FILE = MAIN_PATH + "/controlmap/default.json";
	
	public static int usingVerCode = 1;
	public static String usingVerName = "2.4.2";
	public static String mhomeUrl = "https://pojavlauncherteam.github.io/PojavLauncher"; // "http://kdtjavacraft.eu5.net";
	public static String datapath = "/data/data/net.kdt.pojavlaunch";
	public static String worksDir = datapath + "/app_working_dir";
	
	// New since 2.4.2
	public static String versnDir = MAIN_PATH + "/versions";
	public static String libraries = MAIN_PATH + "/libraries";
	public static String optifineDir = MAIN_PATH + "/optifine";
	
	// Old since 2.4.2
	public static String oldGameDir = MAIN_PATH + "/gamedir";
	public static String oldVersionDir = worksDir + "/versions";
	public static String oldLibrariesDir = worksDir + "/libraries";
	
	public static String mpProfiles = datapath + "/Users";
	public static String crashPath = MAIN_PATH + "/crash-reports";

	public static String mpModEnable = datapath + "/ModsManager/✅Enabled";
	public static String mpModDisable = datapath + "/ModsManager/❌Disabled";
	public static String mpModAddNewMo = datapath + "/ModsManager/➕Add mod";

	public static String optifineLib = "optifine:OptiFine";
	
	public static String[] versionList = {
		"1.7.3",
		"1.7.10",
		"1.8",
		"1.9"
	};

	public static String artifactToPath(String group, String artifact, String version) {
		return group.replaceAll("\\.", "/") + "/" + artifact + "/" + version + "/" + artifact + "-" + version + ".jar";
	}

	public static String getPatchedFile(String version) {
		return versnDir + "/" + version + "/" + version + ".jar";
	}

	private static boolean isClientFirst = false;
	public static String generateLaunchClassPath(String version) throws IOException
	{
		StringBuilder libStr = new StringBuilder(); //versnDir + "/" + version + "/" + version + ".jar:";
		
		JMinecraftVersionList.Version info = getVersionInfo(version);
		String[] classpath = generateLibClasspath(info);

		// Debug: LWJGL 3 override
		File lwjgl3Folder = new File(Tools.MAIN_PATH, "lwjgl3");
		if (info.arguments != null && lwjgl3Folder.exists()) {
			for (File file: lwjgl3Folder.listFiles()) {
				if (file.getName().endsWith(".jar")) {
					libStr.append(file.getAbsolutePath() + ":");
				}
			}
		}
		
		if (isClientFirst) {
			libStr.append(getPatchedFile(version));
		}
		for (String perJar : classpath) {
			libStr.append((isClientFirst ? ":" : "") + perJar + (!isClientFirst ? ":" : ""));
		}
		if (!isClientFirst) {
			libStr.append(getPatchedFile(version));
		}
		
		return libStr.toString();
	}
	
	public static DisplayMetrics getDisplayMetrics(Activity ctx) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		ctx.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics;
	}
	
	public static float pxToDp(Context ctx, float px) {
		return (px / ctx.getResources().getDisplayMetrics().density);
	}

	public static float dpToPx(Context ctx, float dp) {
		return (dp * ctx.getResources().getDisplayMetrics().density);
	}

	public static void copyAssetFile(Context ctx, String fileName, String output, boolean overwrite) throws Exception
	{
		copyAssetFile(ctx, fileName, output, fileName, overwrite);
	}

	public static void copyAssetFile(Context ctx, String fileName, String output, String outputName, boolean overwrite) throws Exception
	{
		try {
			File file = new File(output);
			if(!file.exists()) file.mkdirs();
			File file2 = new File(output, outputName);
			if(!file2.exists() || overwrite){
				write(file2.getAbsolutePath(), loadFromAssetToByte(ctx, fileName));
			}
		} catch (Throwable th) {
			throw new RuntimeException("Unable to copy " + fileName + " to " + output + "/" + outputName, th);
		}
	}
	
	public static void extractAssetFolder(Activity ctx, String path, String output) throws Exception {
		extractAssetFolder(ctx, path, output, false);
	}
	
	public static void extractAssetFolder(Activity ctx, String path, String output, boolean overwrite) throws Exception {
		AssetManager assetManager = ctx.getAssets();
		String assets[] = null;
		try {
			assets = assetManager.list(path);
			if (assets.length == 0) {
				Tools.copyAssetFile(ctx, path, output, overwrite);
			} else {
				File dir = new File(output, path);
				if (!dir.exists())
					dir.mkdirs();
				for (String sub : assets) {
					extractAssetFolder(ctx, path + "/" + sub, output, overwrite);
				}
			}
		} catch (Exception e) {
			showError(ctx, e);
		}
	}

	public static void showError(Context ctx, Throwable e)
	{
		showError(ctx, e, false);
	}

	public static void showError(final Context ctx, final Throwable e, final boolean exitIfOk)
	{
		showError(ctx, R.string.global_error, e, exitIfOk, false);
	}

	public static void showError(final Context ctx, final int titleId, final Throwable e, final boolean exitIfOk)
	{
		showError(ctx, titleId, e, exitIfOk, false);
	}
	
	private static void showError(final Context ctx, final int titleId, final Throwable e, final boolean exitIfOk, final boolean showMore)
	{
		Runnable runnable = new Runnable(){

			@Override
			public void run()
			{
				final String errMsg = showMore ? Log.getStackTraceString(e): e.getMessage();
				new AlertDialog.Builder((Context) ctx)
					.setTitle(titleId)
					.setMessage(errMsg)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							if(exitIfOk) {
								if (ctx instanceof MainActivity) {
									MainActivity.fullyExit();
								} else if (ctx instanceof Activity) {
									((Activity) ctx).finish();
								}
							}
						}
					})
					.setNegativeButton(showMore ? R.string.error_show_less : R.string.error_show_more, new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							showError(ctx, titleId, e, exitIfOk, !showMore);
						}
					})
					.setNeutralButton(android.R.string.copy, new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							StringSelection errData = new StringSelection(errMsg);
							Toolkit.getDefaultToolkit().getSystemClipboard().setContents(errData, null);

							if(exitIfOk) {
								if (ctx instanceof MainActivity) {
									MainActivity.fullyExit();
								} else {
									((Activity) ctx).finish();
								}
							}
						}
					})
					//.setNegativeButton("Report (not available)", null)
					.setCancelable(!exitIfOk)
					.show();
			}
		};
		
		if (ctx instanceof Activity) {
			((Activity) ctx).runOnUiThread(runnable);
		} else {
			runnable.run();
		}
	}

	public static void dialogOnUiThread(final Activity ctx, final CharSequence title, final CharSequence message) {
		ctx.runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					// TODO: Implement this method
					new AlertDialog.Builder(ctx)
						.setTitle(title)
						.setMessage(message)
						.setPositiveButton(android.R.string.ok, null)
						.show();
				}
			});

	}
	
	public static void moveInside(String from, String to) {
		File fromFile = new File(from);
		for (File fromInside : fromFile.listFiles()) {
			moveRecursive(fromInside.getAbsolutePath(), to);
		}
		fromFile.delete();
	}
	
	public static void moveRecursive(String from, String to) {
		moveRecursive(new File(from), new File(to));
	}
	
	public static void moveRecursive(File from, File to) {
		File toFrom = new File(to, from.getName());
		try {
			if (from.isDirectory()) {
				for (File child : from.listFiles()) {
					moveRecursive(child, toFrom);
				}
			}
		} finally {
			from.getParentFile().mkdirs();
			from.renameTo(toFrom);
		}
	}
	
	public static void openURL(Activity act, String url) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		act.startActivity(browserIntent);
	}
	/*
	public static void clearDuplicateFiles(File f) throws IOException {
		List<File> list = Arrays.asList(f.listFiles());
		for (File file : list) {
			if (!file.exists()) {
				// The file was deleted by duplicate
				list.remove(file);
				continue;
			}
			
			String md5 = Md5Crypt.md5Crypt(read(file));
			list.remove(file);
			clearDuplicateFilesByMD5(list.toArray(new File[0]), md5);
		}
	}
	
	public static void clearDuplicateFilesByMD5(File[] list, String md5Find) throws IOException {
		for (File file : list) {
			String md5Other = DigestUtils.md5Hex(new FileInputStream(file));
			if (md5Find.equals(md5Other)) {
				file.delete();
			}
		}
	}
	*/
	public static String[] generateLibClasspath(JMinecraftVersionList.Version info) {
		List<String> libDir = new ArrayList<String>();
		
		for (DependentLibrary libItem: info.libraries) {
			String[] libInfos = libItem.name.split(":");
			libDir.add(Tools.libraries + "/" + Tools.artifactToPath(libInfos[0], libInfos[1], libInfos[2]));
		}
		return libDir.toArray(new String[0]);
	}
	
	public static String[] patchOptifineInstaller(Activity ctx, File inFile) throws Exception {
		File optifineDirFile = new File(optifineDir);
		optifineDirFile.mkdir();
		
		return new OptiFinePatcher(inFile).saveInstaller(optifineDirFile);
	}
	
	private static int selectCompatibleSdkInt() {
		int currSdkInt = Build.VERSION.SDK_INT;
		if (currSdkInt < 23) {
			return 13;
		} else if (currSdkInt < 26) {
			return 24;
		} else if (currSdkInt < 28) {
			return 26;
		} else {
			return currSdkInt;
		}
	}
	
	public static void runDx(final Activity ctx, String fileIn, String fileOut, PojavDXManager.Listen listener) throws Exception
	{
		runDx(ctx, fileIn, fileOut, false, listener);
	}
	
	public static void runDx(final Activity ctx, String fileIn, String fileOut, boolean keepClass, PojavDXManager.Listen listener) throws Exception
	{
		PojavDXManager.setListener(listener);
		
		File optDir = ctx.getDir("dalvik-cache", 0);
		optDir.mkdirs();
		
		com.pojavdx.dx.command.Main.main(new String[]{"--dex", (keepClass ? "--keep-classes" : "--verbose"), "--verbose", "--min-sdk-version=" + selectCompatibleSdkInt() , "--multi-dex", "--no-optimize", "--num-threads=4", "--output", fileOut, fileIn});
		
		//return Runtime.getRuntime().exec("echo IN:" + fileIn + ";OUT:" + fileOut);
	}
	
	public static JMinecraftVersionList.Version getVersionInfo(String versionName) {
        try {
			JMinecraftVersionList.Version customVer = new Gson().fromJson(read(versnDir + "/" + versionName + "/" + versionName + ".json"), JMinecraftVersionList.Version.class);
			for (DependentLibrary lib : customVer.libraries) {
				if (lib.name.startsWith(optifineLib)) {
					customVer.optifineLib = lib;
					break;
				}
			}
			if (customVer.inheritsFrom == null) {
				return customVer;
			} else {
				JMinecraftVersionList.Version inheritsVer = new Gson().fromJson(read(versnDir + "/" + customVer.inheritsFrom + "/" + customVer.inheritsFrom + ".json"), JMinecraftVersionList.Version.class);

				insertSafety(inheritsVer, customVer,
							 "assetIndex", "assets",
					"id", "mainClass", "minecraftArguments",
					"optifineLib", "releaseTime", "time", "type"
				);
				
				List<DependentLibrary> libList = new ArrayList<DependentLibrary>(Arrays.asList(inheritsVer.libraries));
				try {
					for (DependentLibrary lib : customVer.libraries) {
						libList.add(lib);
					}
				} finally {
					inheritsVer.libraries = libList.toArray(new DependentLibrary[0]);
				}

                // Inheriting Minecraft 1.13+ with append custom args
                if (inheritsVer.arguments != null && customVer.arguments != null) {
                    List totalArgList = new ArrayList();
                    totalArgList.addAll(Arrays.asList(inheritsVer.arguments.game));
                    totalArgList.addAll(Arrays.asList(customVer.arguments.game));
                    
                    customVer.arguments.game = totalArgList.toArray(new Object[0]);
                }
                
				return inheritsVer;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
	
	// Prevent NullPointerException
	private static void insertSafety(JMinecraftVersionList.Version targetVer, JMinecraftVersionList.Version fromVer, String... keyArr) {
		for (String key : keyArr) {
			Object value = null;
			try {
				Field fieldA = fromVer.getClass().getDeclaredField(key);
				value = fieldA.get(fromVer);
				if (value != null || ((value instanceof String) && !((String) value).isEmpty())) {
					Field fieldB = targetVer.getClass().getDeclaredField(key);
					fieldB.set(targetVer, value);
				}
			} catch (Throwable th) {
				System.err.println("Unable to insert " + key + "=" + value);
				th.printStackTrace();
			}
		}
	}
	
	public static String convertStream(InputStream inputStream, Charset charset) throws IOException {

		StringBuilder stringBuilder = new StringBuilder();
		String line = null;

		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charset))) {	
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
			}
		}

		return stringBuilder.toString();
	}
	
	// Current Useless below but keep it for future usage.
	public static void deleteRecursive(File fileOrDirectory) {
		try {
			if (fileOrDirectory.isDirectory()) {
				for (File child : fileOrDirectory.listFiles()) {
					deleteRecursive(child);
				}
			}
		} finally {
			fileOrDirectory.delete();
		}
	}
	
	public static File lastFileModified(String dir) {
		File fl = new File(dir);
		
		File[] files = fl.listFiles(new FileFilter() {          
				public boolean accept(File file) {
					return file.isFile();
				}
			});
			
		long lastMod = Long.MIN_VALUE;
		File choice = null;
		for (File file : files) {
			if (file.lastModified() > lastMod) {
				choice = file;
				lastMod = file.lastModified();
			}
		}
		
		return choice;
	}
	
	public static byte[] getByteArray(String filePath) throws Exception
	{
		return getByteArray(new FileInputStream(filePath));
	}
	
	public static byte[] getByteArray(InputStream stream) throws IOException
	{
		byte[] bytes = new byte[stream.available()];
		BufferedInputStream buf = new BufferedInputStream(stream);
		buf.read(bytes, 0, bytes.length);
		buf.close();

		return bytes;
	}

	public static String read(InputStream is) throws Exception
	{
		return new String(getByteArray(is));
	}
	
	public static String read(String path) throws Exception
	{
		return new String(getByteArray(path));
	}
	
	public static void write(String path, byte[] content) throws Exception
	{
		File outPath = new File(path);
		outPath.getParentFile().mkdirs();
		outPath.createNewFile();
		
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(content);
		fos.close();
	}
	
	public static void write(String path, String content) throws Exception
	{
		write(path, content.getBytes());
	}
	
	public static byte[] loadFromAssetToByte(Context ctx, String inFile) {
        byte[] buffer = null;

		try {
			InputStream stream = ctx.getAssets().open(inFile);

			int size = stream.available();
			buffer = new byte[size];
			stream.read(buffer);
			stream.close();
		} catch (IOException e) {
			// Handle exceptions here
			e.printStackTrace();
		}
		return buffer;
	}
	
	public static void downloadFile(String urlInput, String nameOutput, boolean requireNonExist) throws Throwable
	{
		File fileDDD = new File(nameOutput);
		if(requireNonExist && !fileDDD.exists())
		{
			DownloadUtils.downloadFile(urlInput, fileDDD);
		}
	}
	public static class ZipTool
	{
		private ZipTool(){}
		public static void zip(List<File> files, File zipFile) throws IOException {
			final int BUFFER_SIZE = 2048;

			BufferedInputStream origin = null;
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));

			try {
				byte data[] = new byte[BUFFER_SIZE];

				for (File file : files) {
					FileInputStream fileInputStream = new FileInputStream( file );

					origin = new BufferedInputStream(fileInputStream, BUFFER_SIZE);

					try {
						ZipEntry entry = new ZipEntry(file.getName());

						out.putNextEntry(entry);

						int count;
						while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
							out.write(data, 0, count);
						}
					}
					finally {
						origin.close();
					}
				}
			} finally {
				out.close();
			}
		}
		public static void unzip(File zipFile, File targetDirectory) throws IOException {
			final int BUFFER_SIZE = 1024;
			ZipInputStream zis = new ZipInputStream(
				new BufferedInputStream(new FileInputStream(zipFile)));
			try {
				ZipEntry ze;
				int count;
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((ze = zis.getNextEntry()) != null) {
					File file = new File(targetDirectory, ze.getName());
					File dir = ze.isDirectory() ? file : file.getParentFile();
					if (!dir.isDirectory() && !dir.mkdirs())
						throw new FileNotFoundException("Failed to ensure directory: " +
														dir.getAbsolutePath());
					if (ze.isDirectory())
						continue;
					FileOutputStream fout = new FileOutputStream(file);
					try {
						while ((count = zis.read(buffer)) != -1)
							fout.write(buffer, 0, count);
					} finally {
						fout.close();
					}
					/* if time should be restored as well
					 long time = ze.getTime();
					 if (time > 0)
					 file.setLastModified(time);
					 */
				}
			} finally {
				zis.close();
			}
		}
	}
}
