package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.net.*;
import android.os.*;
import android.system.*;
import android.util.*;
import com.google.gson.*;
import com.oracle.dalvik.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;
import java.util.zip.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.util.*;
import net.kdt.pojavlaunch.value.*;
import org.lwjgl.glfw.*;

public final class Tools
{
	public static boolean enableDevFeatures = BuildConfig.DEBUG;
	
	public static String APP_NAME = "null";
	public static String MAIN_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/.minecraft";
	public static String ASSETS_PATH = MAIN_PATH + "/assets";
	public static String CTRLMAP_PATH = MAIN_PATH + "/controlmap";
	public static String CTRLDEF_FILE = MAIN_PATH + "/controlmap/default.json";
	
	public static int usingVerCode = 1;
	public static String usingVerName = "2.4.2";
	public static String mhomeUrl = "https://pojavlauncherteam.github.io/PojavLauncher"; // "http://kdtjavacraft.eu5.net";
	public static String datapath = "/data/data/net.kdt.pojavlaunch";
	public static String worksDir = datapath + "/app_working_dir";
	
	// New since 3.0.0
	public static String homeJreDir = datapath + "/jre_runtime";
	
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
	

	public static final int LTYPE_PROCESS = 0;
	public static final int LTYPE_INVOCATION = 1;
	public static final int LTYPE_CREATEJAVAVM = 2;
	public static final int LAUNCH_TYPE = LTYPE_INVOCATION;
	
	public static ShellProcessOperation mLaunchShell;
	public static void launchMinecraft(final Activity ctx, MCProfile.Builder profile, JMinecraftVersionList.Version versionInfo) throws Throwable {
		String[] launchArgs = getMinecraftArgs(profile, versionInfo);

		List<String> javaArgList = new ArrayList<String>();
	/*
		if (LAUNCH_TYPE == LTYPE_PROCESS || LAUNCH_TYPE == LTYPE_BINARYEXEC) javaArgList.add(Tools.homeJreDir + "/bin/java");
		else javaArgList.add("java");
	*/
		javaArgList.add(Tools.homeJreDir + "/bin/java");
	
        List<String> overrideableArgList = new ArrayList<String>();
		
		overrideableArgList.add("-Djava.home=" + Tools.homeJreDir);
		overrideableArgList.add("-Djava.io.tmpdir=" + ctx.getCacheDir().getAbsolutePath());
		overrideableArgList.add("-Dos.name=Linux");
		
		// javaArgList.add("-Dorg.lwjgl.libname=liblwjgl3.so");
		// javaArgList.add("-Dorg.lwjgl.system.jemalloc.libname=libjemalloc.so");
		overrideableArgList.add("-Dorg.lwjgl.opengl.libname=libgl04es.so");
		// javaArgList.add("-Dorg.lwjgl.opengl.libname=libRegal.so");
			
		// Enable LWJGL3 debug
		overrideableArgList.add("-Dorg.lwjgl.util.Debug=true");
		// overrideableArgList.add("-Dorg.lwjgl.util.DebugFunctions=true");
		overrideableArgList.add("-Dorg.lwjgl.util.DebugLoader=true");
		
		// GLFW Stub width height
		overrideableArgList.add("-Dglfwstub.windowWidth=" + CallbackBridge.windowWidth);
		overrideableArgList.add("-Dglfwstub.windowHeight=" + CallbackBridge.windowHeight);
		
		overrideableArgList.add("-Dglfwstub.initEgl=false");
		
		if (versionInfo.arguments != null) {
			// Minecraft 1.13+

			overrideableArgList.add("-Dminecraft.launcher.brand=" + Tools.APP_NAME);
			overrideableArgList.add("-Dminecraft.launcher.version=" + ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName);
		}
		
		String launchClassPath = generateLaunchClassPath(profile.getVersion());
        System.out.println("Java Classpath: " + launchClassPath);
		if (LAUNCH_TYPE == LTYPE_CREATEJAVAVM) {
			javaArgList.add("-Djava.library.path=" + launchClassPath);
		} else {
        /*
			if (LAUNCH_TYPE == LTYPE_PROCESS) {
				javaArgList.add("-Dglfwstub.eglContext=" + Tools.getEGLAddress("Context", AndroidContextImplementation.context));
				String eglDisplay = Tools.getEGLAddress("Display", AndroidContextImplementation.display);
				if (eglDisplay.equals("1")) {
					eglDisplay = Tools.getEGLAddress("Display", ((EGL10) EGLContext.getEGL()).eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY));
				}
				javaArgList.add("-Dglfwstub.eglDisplay=" + eglDisplay);

				javaArgList.add("-Dglfwstub.eglSurfaceRead=" + Tools.getEGLAddress("Surface", AndroidContextImplementation.read));
				javaArgList.add("-Dglfwstub.eglSurfaceDraw=" + Tools.getEGLAddress("Surface", AndroidContextImplementation.draw));
			}
        */
            // Override args
            // TODO fix duplicate args
            for (String argOverride : LauncherPreferences.PREF_CUSTOM_JAVA_ARGS.split(" ")) {
                for (int i = 0; i < overrideableArgList.size(); i++) {
                    String arg = overrideableArgList.get(i);
                    if (arg.startsWith("-D") && argOverride.startsWith(arg.substring(0, arg.indexOf('=') + 1))) {
                        overrideableArgList.set(i, argOverride);
                        break;
                    } else if (i+1 == overrideableArgList.size()) {
                        javaArgList.add(argOverride);
                    }
                }
            }
            
            javaArgList.addAll(overrideableArgList);
            
			javaArgList.add("-cp");
			javaArgList.add(launchClassPath);
			javaArgList.add(versionInfo.mainClass);
			javaArgList.addAll(Arrays.asList(launchArgs));
		}
		
		if (LAUNCH_TYPE == LTYPE_PROCESS) {
			mLaunchShell = new ShellProcessOperation(new ShellProcessOperation.OnPrintListener(){

					@Override
					public void onPrintLine(String text) {
						// ctx.appendToLog(text, false);
					}
				});
			mLaunchShell.initInputStream(ctx);
		}

		// can fix java?
		// setEnvironment("ORIGIN", Tools.homeJreDir + "/lib");
		
		JREUtils.setJavaEnvironment(ctx);
		
		if (LAUNCH_TYPE == LTYPE_PROCESS) {
			mLaunchShell.writeToProcess("cd $HOME");

			mLaunchShell.writeToProcess(javaArgList.toArray(new String[0]));
			int exitCode = mLaunchShell.waitFor();
			if (exitCode != 0) {
				Tools.showError(ctx, new ErrnoException("java", exitCode), false);
			}
		} else { // Type Invocation
			// Is it need?
		/*
			Os.dup2(FileDescriptor.err, OsConstants.STDERR_FILENO);
			Os.dup2(FileDescriptor.out, OsConstants.STDOUT_FILENO);
		*/

			JREUtils.initJavaRuntime();
			JREUtils.chdir(Tools.MAIN_PATH);
			
			if (new File(Tools.MAIN_PATH, "strace.txt").exists()) {
				startStrace(android.os.Process.myTid());
			}
			
			if (LAUNCH_TYPE == LTYPE_CREATEJAVAVM) {
				VMLauncher.createLaunchMainJVM(javaArgList.toArray(new String[0]), versionInfo.mainClass, launchArgs);
			} else {
				// Test
			/*
				VMLauncher.launchJVM(new String[]{
					Tools.homeJreDir + "/bin/java",
					"-invalidarg"
				});
			*/
				
				VMLauncher.launchJVM(javaArgList.toArray(new String[0]));
			}
		}
		
		ctx.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
					dialog.setMessage(R.string.mcn_javaexit_title);
					dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2){
								ctx.finish();
							}
						});
				}
			});
	}
	
	public static String[] getMinecraftArgs(MCProfile.Builder profile, JMinecraftVersionList.Version versionInfo)
	{
		String username = profile.getUsername();
		String versionName = profile.getVersion();
		String mcAssetsDir = Tools.ASSETS_PATH;
		String userType = "mojang";

		File gameDir = new File(Tools.MAIN_PATH);
		gameDir.mkdirs();

		Map<String, String> varArgMap = new ArrayMap<String, String>();
		varArgMap.put("auth_player_name", username);
		varArgMap.put("version_name", versionName);
		varArgMap.put("game_directory", gameDir.getAbsolutePath());
		varArgMap.put("assets_root", mcAssetsDir);
		varArgMap.put("assets_index_name", versionInfo.assets);
		varArgMap.put("auth_uuid", profile.getProfileID());
		varArgMap.put("auth_access_token", profile.getAccessToken());
		varArgMap.put("user_properties", "{}");
		varArgMap.put("user_type", userType);
		varArgMap.put("version_type", versionInfo.type);
		varArgMap.put("game_assets", Tools.ASSETS_PATH);

		List<String> minecraftArgs = new ArrayList<String>();
		if (versionInfo.arguments != null) {
			// Support Minecraft 1.13+
			for (Object arg : versionInfo.arguments.game) {
				if (arg instanceof String) {
					minecraftArgs.add((String) arg);
				} else {
					/*
					 for (JMinecraftVersionList.Arguments.ArgValue.ArgRules rule : arg.rules) {
					 // rule.action = allow
					 // TODO implement this
					 }
					 */
				}
			}
		}

		String[] argsFromJson = insertVariableArgument(
			splitAndFilterEmpty(
				versionInfo.minecraftArguments == null ?
				fromStringArray(minecraftArgs.toArray(new String[0])):
				versionInfo.minecraftArguments
			), varArgMap
		);
		// Tools.dialogOnUiThread(this, "Result args", Arrays.asList(argsFromJson).toString());
		return argsFromJson;
	}

	private static void startStrace(int pid) throws Exception {
		String[] straceArgs = new String[] {"/system/bin/strace",
			"-o", new File(Tools.MAIN_PATH, "strace.txt").getAbsolutePath(), "-f", "-p", "" + pid};
		System.out.println("strace args: " + Arrays.toString(straceArgs));
		Runtime.getRuntime().exec(straceArgs);
	}
	
	public static String fromStringArray(String[] strArr) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < strArr.length; i++) {
			if (i > 0) builder.append(" ");
			builder.append(strArr[i]);
		}

		return builder.toString();
	}

	private static String[] splitAndFilterEmpty(String argStr) {
		List<String> strList = new ArrayList<String>();
		strList.add("--fullscreen");
		for (String arg : argStr.split(" ")) {
			if (!arg.isEmpty()) {
				strList.add(arg);
			}
		}
		return strList.toArray(new String[0]);
	}

	private static String[] insertVariableArgument(String[] args, Map<String, String> keyValueMap) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			String argVar = null;
			if (arg.startsWith("${") && arg.endsWith("}")) {
				argVar = arg.substring(2, arg.length() - 1);
				for (Map.Entry<String, String> keyValue : keyValueMap.entrySet()) {
					if (argVar.equals(keyValue.getKey())) {
						args[i] = keyValue.getValue();
					}
				}
			}
		}
		return args;
	}

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
		File lwjgl2Folder = new File(Tools.MAIN_PATH, "lwjgl2");
		File lwjgl3Folder = new File(Tools.MAIN_PATH, "lwjgl3");
		if (info.arguments != null && lwjgl3Folder.exists()) {
			for (File file: lwjgl3Folder.listFiles()) {
				if (file.getName().endsWith(".jar")) {
					libStr.append(file.getAbsolutePath() + ":");
				}
			}
		} else if (lwjgl2Folder.exists()) {
			for (File file: lwjgl2Folder.listFiles()) {
				if (file.getName().endsWith(".jar")) {
					libStr.append(file.getAbsolutePath() + ":");
				}
			}
		}
		
		if (isClientFirst) {
			libStr.append(getPatchedFile(version));
		}
		for (String perJar : classpath) {
            if (!new File(perJar).exists()) {
                System.out.println("ClassPathGen: ignored non-exists file: " + perJar);
                continue;
            }
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
	
	public static String getEGLAddress(String type, Object obj) {
		try {
			Field addrField = obj.getClass().getDeclaredField("mEGL" + type);
			addrField.setAccessible(true);
			return Long.toString((long) addrField.get(obj));
		} catch (Throwable th) {
			th.printStackTrace();
			return "0l";
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
							android.content.ClipboardManager mgr = (android.content.ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
							mgr.setPrimaryClip(ClipData.newPlainText("error", Log.getStackTraceString(e)));
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
                    
                    inheritsVer.arguments.game = totalArgList.toArray(new Object[0]);
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
				if (((value instanceof String) && !((String) value).isEmpty()) || value != null) {
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

		String out = "";
		int len;
		byte[] buf = new byte[512];
		while((len = inputStream.read(buf))!=-1) {
			out += new String(buf,0,len,charset);
		}
		return out;
	}
	
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


	public static String read(InputStream is) throws Exception {
		String out = "";
		int len;
		byte[] buf = new byte[512];
		while((len = is.read(buf))!=-1) {
			out += new String(buf,0,len);
		}
		return out;
	}
	
	public static String read(String path) throws Exception {
		return read(new FileInputStream(path));
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
	
	public static void downloadFile(String urlInput, String nameOutput, boolean requireNonExist) throws Throwable {
		File fileDDD = new File(nameOutput);
		if(requireNonExist && !fileDDD.exists()) {
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
