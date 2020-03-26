package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.net.*;
import android.os.*;
import android.util.*;
import com.google.gson.*;
import dalvik.system.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;
import java.util.zip.*;
import net.kdt.pojavlaunch.util.*;
import net.kdt.pojavlaunch.value.*;
import org.apache.commons.codec.digest.*;
import android.widget.*;

public final class Tools
{
	public static boolean enableDevFeatures = true;
	
	public static String APP_NAME = "null";
	public static String MAIN_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/minecraft";
	public static String ASSETS_PATH = MAIN_PATH + "/assets";

	public static int usingVerCode = 1;
	public static String usingVerName = "2.0";
	public static String mhomeUrl = "http://mineup.eu5.net"; // "http://kdtjavacraft.eu5.net";
	public static String datapath = "/data/data/net.kdt.pojavlaunch";
	public static String worksDir = datapath + "/app_working_dir";
	
	// New since 2.4.2
	public static String versnDir = MAIN_PATH + "/versions";
	public static String libraries = MAIN_PATH + "/libraries";
	
	// Old since 2.4.2
	public static String oldGameDir = MAIN_PATH + "/gamedir";
	public static String oldVersionDir = worksDir + "/versions";
	public static String oldLibrariesDir = worksDir + "/libraries";
	
	public static String mpProfiles = datapath + "/Users";
	public static String crashPath = MAIN_PATH + "/crash-reports";

	public static String mpModEnable = datapath + "/ModsManager/✅Enabled";
	public static String mpModDisable = datapath + "/ModsManager/❌Disabled";
	public static String mpModAddNewMo = datapath + "/ModsManager/➕Add mod";

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

	// May useless
	public static boolean isOptifineInstalled(String version)
	{
		return new File(versnDir + "/" + version + "/optifine.jar").exists();
	}

	public static String generate(String version) throws IOException
	{
		StringBuilder libStr = new StringBuilder(); //versnDir + "/" + version + "/" + version + ".jar:";
		String[] classpath = generateLibClasspath(getVersionInfo(version).libraries);

		libStr.append(getPatchedFile(version));
		for (String perJar : classpath) {
			libStr.append(":" + perJar);
		}

		return libStr.toString();
	}
	
	public static DisplayMetrics getDisplayMetrics(Activity ctx) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		ctx.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics;
	}
	
	public static void extractAssetFolder(Activity ctx, String path, String output) throws Exception
	{
		AssetManager assetManager = ctx.getAssets();
		String assets[] = null;
		try {
			assets = assetManager.list(path);
			if (assets.length == 0) {
				Tools.copyAssetOptional(ctx, path, output);
			} else {
				String fullPath = output + "/" + path;
				File dir = new File(fullPath);
				if (!dir.exists())
					dir.mkdir();
				for (String sub : assets) {
					extractAssetFolder(ctx, path + "/" + sub, output);
				}
			}
		} catch (Exception e) {
			showError(ctx, e);
		}
	}

	/*
	 public static void extractLibraries(Activity ctx) throws Exception
	 {
	 extractAssetFolder(ctx, "libraries", worksDir);
	 }
	 */

	public static void showError(Activity ctx, Throwable e)
	{
		showError(ctx, e, false);
	}

	public static void showError(final Activity ctx, final Throwable e, final boolean exitIfOk)
	{
		showError(ctx, e, exitIfOk, false);
	}

	private static void showError(final Activity ctx, final Throwable e, final boolean exitIfOk, final boolean showMore)
	{
		ctx.runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					final String errMsg = showMore ? Log.getStackTraceString(e): e.getMessage();
					new AlertDialog.Builder((Context) ctx)
						.setTitle(R.string.error_title)
						.setMessage(errMsg)
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								if(exitIfOk) MainActivity.fullyExit();
							}
						})
						.setNegativeButton(showMore ? R.string.error_show_less : R.string.error_show_more, new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								showError(ctx, e, exitIfOk, !showMore);
							}
						})
						.setNeutralButton(android.R.string.copy, new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE); 
								ClipData clip = ClipData.newPlainText("Error", errMsg);
								clipboard.setPrimaryClip(clip);
								
								Toast.makeText(ctx, "Copied to clipboard", Toast.LENGTH_SHORT).show();
								
								if(exitIfOk) MainActivity.fullyExit();
							}
						})
						//.setNegativeButton("Report (not available)", null)
						.setCancelable(!exitIfOk)
						.show();
				}
			});
	}

	public static void dialogOnUiThread(final Activity ctx, final CharSequence title, final CharSequence message)
	{
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
	
	public static void clearDuplicateFiles(File f) throws IOException {
		List<File> list = Arrays.asList(f.listFiles());
		for (File file : list) {
			if (!file.exists()) {
				// The file was deleted by duplicate
				list.remove(file);
				continue;
			}
			
			String md5 = DigestUtils.md5Hex(new FileInputStream(file));
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
	
	public static String[] generateLibClasspath(DependentLibrary[] libs)
	{
		List<String> libDir = new ArrayList<String>();
		for (DependentLibrary libItem: libs) {
			String[] libInfos = libItem.name.split(":");
			libDir.add(Tools.libraries + "/" + Tools.artifactToPath(libInfos[0], libInfos[1], libInfos[2]));
		}
		return libDir.toArray(new String[0]);
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
		PojavDXManager.setListener(listener);
		
		File optDir = ctx.getDir("dalvik-cache", 0);
		optDir.mkdirs();
		
		com.pojavdx.dx.command.Main.main(new String[]{"--dex", "--verbose", "--min-sdk-version=" + selectCompatibleSdkInt() , "--multi-dex", "--no-optimize", "--num-threads=4", "--output", fileOut, fileIn});
		
		//return Runtime.getRuntime().exec("echo IN:" + fileIn + ";OUT:" + fileOut);
	}
	
	public static JMinecraftVersionList.Version getVersionInfo(String versionName) {
        try {
			JMinecraftVersionList.Version customVer = new Gson().fromJson(read(versnDir + "/" + versionName + "/" + versionName + ".json"), JMinecraftVersionList.Version.class);
			if (customVer.inheritsFrom == null) {
				return customVer;
			} else {
				JMinecraftVersionList.Version inheritsVer = new Gson().fromJson(read(versnDir + "/" + customVer.inheritsFrom + "/" + customVer.inheritsFrom + ".json"), JMinecraftVersionList.Version.class);

				inheritsVer.id = customVer.id;
				inheritsVer.mainClass = customVer.mainClass;
				inheritsVer.minecraftArguments = customVer.minecraftArguments;
				inheritsVer.releaseTime = customVer.releaseTime;
				inheritsVer.time = customVer.time;
				inheritsVer.type = customVer.type;

				List<DependentLibrary> libList = new ArrayList<DependentLibrary>(Arrays.asList(inheritsVer.libraries));
				try {
					for (DependentLibrary lib : customVer.libraries) {
						libList.add(lib);
					}
				} finally {
					inheritsVer.libraries = libList.toArray(new DependentLibrary[0]);
				}

				return inheritsVer;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
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
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(content);
		fos.close();
	}
	
	public static void write(String path, String content) throws Exception
	{
		write(path, content.getBytes());
	}
	
	public static byte[] loadFromAssetToByte(Context ctx, String inFile) {
        byte[] tContents = {};

		try {
			InputStream stream = ctx.getAssets().open(inFile);

			int size = stream.available();
			byte[] buffer = new byte[size];
			stream.read(buffer);
			stream.close();
			tContents = buffer;
		} catch (IOException e) {
			// Handle exceptions here
			e.printStackTrace();
		}
		return tContents;
	}
	
	public static void copyAssetOptional(Context ctx, String fileName, String output) throws Exception
	{
		copyAssetOptional(ctx, fileName, output, fileName);
	}
	
	public static void copyAssetOptional(Context ctx, String fileName, String output, String outputName) throws Exception
	{
		try {
			File file = new File(output);
			if(!file.exists()) file.mkdirs();
			File file2 = new File(output + "/" + outputName);
			if(!file2.exists()){
				if (!file2.createNewFile()) throw new RuntimeException("Unable to write " + output + "/" + outputName);
				write(file2.getAbsolutePath(), loadFromAssetToByte(ctx, fileName));
			}
		} catch (Throwable th) {
			throw new RuntimeException("Unable to copy " + fileName + " to " + output + "/" + outputName, th);
		}
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
