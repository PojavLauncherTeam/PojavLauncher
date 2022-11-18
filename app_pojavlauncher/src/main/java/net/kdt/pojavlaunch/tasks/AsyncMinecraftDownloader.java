package net.kdt.pojavlaunch.tasks;

import android.app.Activity;
import android.util.Log;
import androidx.annotation.NonNull;
import com.kdt.mcgui.ProgressLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.kdt.pojavlaunch.JAssetInfo;
import net.kdt.pojavlaunch.JAssets;
import net.kdt.pojavlaunch.JMinecraftVersionList;
import net.kdt.pojavlaunch.JRE17Util;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.download.MirrorsChanger;
import net.kdt.pojavlaunch.download.providers.MirrorsProviders;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.utils.DownloadUtils;
import net.kdt.pojavlaunch.value.DependentLibrary;
import net.kdt.pojavlaunch.value.MinecraftClientInfo;
import net.kdt.pojavlaunch.value.MinecraftLibraryArtifact;
import org.apache.commons.io.IOUtils;

import static net.kdt.pojavlaunch.PojavApplication.sExecutorService;
import static net.kdt.pojavlaunch.utils.DownloadUtils.downloadFileMonitored;
import android.widget.Toast;

public class AsyncMinecraftDownloader {
	
    private String mcResUrl;
	private String mcLibrariesUrl;
	
    /* Allows each downloading thread to have its own RECYCLED buffer */
    private final ConcurrentHashMap<Thread, byte[]> mThreadBuffers = new ConcurrentHashMap<>(5);

	public AsyncMinecraftDownloader(@NonNull Activity activity, JMinecraftVersionList.Version version, String realVersion,
                                    
									@NonNull DoneListener listener) {
		this(activity, version, realVersion, listener, true);
	}
	
    public AsyncMinecraftDownloader(@NonNull Activity activity, JMinecraftVersionList.Version version, String realVersion,
                                    @NonNull DoneListener listener, boolean changeMirror) { // this was there for a reason
		//here change main jar and so on 
		if (changeMirror) 
			MirrorsChanger.getInstance().inject(version);
			
		//use default when changeMirror is false
		mcResUrl = addMissingInUrl(changeMirror ? MirrorsChanger.getInstance().getMinecraftResourceUrl(): 
		   MirrorsProviders.DEFAULT_PROVIDER.getAssetsURL());
		
		mcLibrariesUrl = addMissingInUrl(changeMirror ? MirrorsChanger.getInstance().getMinecraftLibrariesUrl(): 
			MirrorsProviders.DEFAULT_PROVIDER.getLibrariesURL());
		   
        sExecutorService.execute(() -> {
            if(downloadGame(activity, version, realVersion))
                listener.onDownloadDone();
        });
    }

    private boolean downloadGame(@NonNull Activity activity, JMinecraftVersionList.Version verInfo, String versionName){
        final String downVName = "/" + versionName + "/" + versionName;

        //Downloading libraries
        String minecraftMainJar = Tools.DIR_HOME_VERSION + downVName + ".jar";
        JAssets assets = null;
        try {
            File verJsonDir = new File(Tools.DIR_HOME_VERSION + downVName + ".json");

            if (verInfo != null && verInfo.url != null) {
                if(!LauncherPreferences.PREF_CHECK_LIBRARY_SHA)  Log.w("Chk","Checker is off");

                boolean isManifestGood = verJsonDir.exists()
                        && (!LauncherPreferences.PREF_CHECK_LIBRARY_SHA
                        || verInfo.sha1 == null
                        || Tools.compareSHA1(verJsonDir, verInfo.sha1));

                if(!isManifestGood) {
                    ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT, 0, R.string.mcl_launch_downloading, versionName + ".json");
                    verJsonDir.delete();
					
                    downloadFileMonitored(verInfo.url, verJsonDir, getByteBuffer(),
                            (curr, max) -> ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT,
                                    (int) Math.max((float)curr/max*100,0), R.string.mcl_launch_downloading, versionName + ".json")
                    );
                }
            }

            verInfo = Tools.getVersionInfo(versionName);

            // THIS one function need the activity in the case of an error
            if(!JRE17Util.installNewJreIfNeeded(activity, verInfo)){
                return false;
            }

            try {
                assets = downloadIndex(verInfo, new File(Tools.ASSETS_PATH, "indexes/" + verInfo.assets + ".json"));
            } catch (IOException e) {
                Log.e("AsyncMcDownloader", e.toString(), e);
            }

            File outLib;

            // Patch the Log4J RCE (CVE-2021-44228)
            if (verInfo.logging != null) {
                outLib = new File(Tools.DIR_DATA + "/security", verInfo.logging.client.file.id.replace("client", "log4j-rce-patch"));
                boolean useLocal = outLib.exists();
                if (!useLocal) {
                    outLib = new File(Tools.DIR_GAME_NEW, verInfo.logging.client.file.id);
                }
                if (outLib.exists() && !useLocal) {
                    if(LauncherPreferences.PREF_CHECK_LIBRARY_SHA) {
                        if(!Tools.compareSHA1(outLib,verInfo.logging.client.file.sha1)) {
                            outLib.delete();
                            ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT, 0, R.string.dl_library_sha_fail,verInfo.logging.client.file.id);
                        }else{
                            ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT, 0, R.string.dl_library_sha_pass,verInfo.logging.client.file.id);
                        }
                    } else if (outLib.length() != verInfo.logging.client.file.size) {
                        // force updating anyways
                        outLib.delete();
                    }
                }
                if (!outLib.exists()) {
                    ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT, 0, R.string.mcl_launch_downloading, verInfo.logging.client.file.id);
                    JMinecraftVersionList.Version finalVerInfo = verInfo;
                    downloadFileMonitored(
                            verInfo.logging.client.file.url, outLib, getByteBuffer(),
                            (curr, max) -> ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT,
                                    (int) Math.max((float)curr/max*100,0), R.string.mcl_launch_downloading, finalVerInfo.logging.client.file.id)
                    );
                }
            }


            for (final DependentLibrary libItem : verInfo.libraries) {
                if(libItem.name.startsWith("org.lwjgl")){ //Black list
                    ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT, 0, "Ignored " + libItem.name);
                    continue;
                }

                String libArtifact = Tools.artifactToPath(libItem.name);
                outLib = new File(Tools.DIR_HOME_LIBRARY + "/" + libArtifact);
                outLib.getParentFile().mkdirs();

                if (!outLib.exists()) {
                    downloadLibrary(libItem,libArtifact,outLib);
                }else{
                    if(libItem.downloads != null && libItem.downloads.artifact != null && libItem.downloads.artifact.sha1 != null && !libItem.downloads.artifact.sha1.isEmpty()) {
                        if(!Tools.compareSHA1(outLib,libItem.downloads.artifact.sha1)) {
                            outLib.delete();
                            ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT, 0, R.string.dl_library_sha_fail,libItem.name);
                            downloadLibrary(libItem,libArtifact,outLib);
                        }else{
                            ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT, 0, R.string.dl_library_sha_pass,libItem.name);
                        }
                    }else{
                        ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT, 0, R.string.dl_library_sha_unknown,libItem.name);
                    }
                }
            }
            File minecraftMainFile = new File(minecraftMainJar);
			//wait for
            JMinecraftVersionList.Version originalVersion = Tools.getVersionInfo(versionName, true);
            Log.i("Downloader", "originalVersion.inheritsFrom="+originalVersion.inheritsFrom);
            Log.i("Downloader", "originalVersion.downloads="+originalVersion.downloads);
            MinecraftClientInfo originalClientInfo;
            if(originalVersion.inheritsFrom == null) {
                if (originalVersion.downloads != null && (originalClientInfo = originalVersion.downloads.get("client")) != null) {
                    verifyAndDownloadMainJar(originalClientInfo.url, originalClientInfo.sha1, minecraftMainFile);
                }
            }else if(!minecraftMainFile.exists() || minecraftMainFile.length() == 0) {
                File minecraftSourceFile = new File(Tools.DIR_HOME_VERSION + "/" + verInfo.id + "/" + verInfo.id + ".jar");
                MinecraftClientInfo inheritedClientInfo;
                if(verInfo.downloads != null && (inheritedClientInfo = verInfo.downloads.get("client")) != null) {
                    verifyAndDownloadMainJar(inheritedClientInfo.url, inheritedClientInfo.sha1, minecraftSourceFile);
                }
                if(minecraftSourceFile.exists()) {
                    FileInputStream is = new FileInputStream(minecraftSourceFile);
                    FileOutputStream os = new FileOutputStream(minecraftMainFile);
                    IOUtils.copy(is, os);
                    is.close();
                    os.close();
                }
            }
        } catch (Throwable e) {
            Log.e("AsyncMcDownloader", e.toString(),e );
        }

        ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT, 0, R.string.mcl_launch_cleancache);
        new File(Tools.DIR_HOME_VERSION).mkdir();
        for (File f : new File(Tools.DIR_HOME_VERSION).listFiles()) {
            if(f.getName().endsWith(".part")) {
                Log.d(Tools.APP_NAME, "Cleaning cache: " + f);
                f.delete();
            }
        }


        try {
            downloadAssets(assets, verInfo.assets, assets.mapToResources ? new File(Tools.OBSOLETE_RESOURCES_PATH) : new File(Tools.ASSETS_PATH));
        } catch (Exception e) {
            Log.e("AsyncMcDownloader", e.toString(), e);
        }

        return true;
    }

    public void verifyAndDownloadMainJar(String url, String sha1, File destination) throws Exception{
        while(!destination.exists() || (destination.exists() && !Tools.compareSHA1(destination, sha1))) downloadFileMonitored(
                url,
                destination, getByteBuffer(),
                (curr, max) -> ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT,
                        (int) Math.max((float)curr/max*100,0), R.string.mcl_launch_downloading, destination.getName()));
    }

    public void downloadAssets(final JAssets assets, String assetsVersion, final File outputDir) throws IOException {
        LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 500, TimeUnit.MILLISECONDS, workQueue);


        Log.i("AsyncMcDownloader","Assets begin time: " + System.currentTimeMillis());
        ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT, 0, R.string.mcl_launch_download_assets);

        Map<String, JAssetInfo> assetsObjects = assets.objects;
        int assetsSizeBytes = 0;
        AtomicInteger downloadedSize = new AtomicInteger(0);
        AtomicBoolean localInterrupt = new AtomicBoolean(false);

        File objectsDir = new File(outputDir, "objects");
        ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT, 0, R.string.mcl_launch_downloading, "assets");

        for(String assetKey : assetsObjects.keySet()) {
            JAssetInfo asset = assetsObjects.get(assetKey);
            assetsSizeBytes += asset.size;
            String assetPath = asset.hash.substring(0, 2) + "/" + asset.hash;
            File outFile = assets.mapToResources ? new File(outputDir,"/"+assetKey) : new File(objectsDir, assetPath);
            boolean skip = outFile.exists();// skip if the file exists

            if(LauncherPreferences.PREF_CHECK_LIBRARY_SHA && skip)
                skip = Tools.compareSHA1(outFile, asset.hash);

            if(skip) {
                downloadedSize.addAndGet(asset.size);
                continue;
            }

            if(outFile.exists())
                ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT, 0, R.string.dl_library_sha_fail, assetKey);

            executor.execute(()->{
                try {
                    if (!assets.mapToResources) downloadAsset(asset, objectsDir, downloadedSize);
                    else downloadAssetMapped(asset, assetKey, outputDir, downloadedSize);
                }catch (IOException e) {
                    Log.e("AsyncMcManager", e.toString());
                    localInterrupt.set(true);
                }
            });
        }
        executor.shutdown();

        try {
            Log.i("AsyncMcDownloader","Queue size: " + workQueue.size());
            while ((!executor.awaitTermination(1000, TimeUnit.MILLISECONDS))&&(!localInterrupt.get()) /*&&mActivity.mIsAssetsProcessing*/) {
                int DLSize = downloadedSize.get();
                ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT,(int) Math.max((float) DLSize/assetsSizeBytes*100, 0),
                        R.string.mcl_launch_downloading, "assets");
            }


            executor.shutdownNow();
            while (!executor.awaitTermination(250, TimeUnit.MILLISECONDS)) {}
            Log.i("AsyncMcDownloader","Fully shut down!");
        }catch(InterruptedException e) {
            Log.e("AsyncMcDownloader", e.toString());
        }
        Log.i("AsyncMcDownloader", "Assets end time: " + System.currentTimeMillis());
    }


    public void downloadAsset(JAssetInfo asset, File objectsDir, AtomicInteger downloadCounter) throws IOException {
        String assetPath = asset.hash.substring(0, 2) + "/" + asset.hash;
        File outFile = new File(objectsDir, assetPath);
        downloadFileMonitored(mcResUrl + assetPath, outFile, getByteBuffer(),
                new Tools.DownloaderFeedback() {
                    int prevCurr;
                    @Override
                    public void updateProgress(int curr, int max) {
                        downloadCounter.addAndGet(curr - prevCurr);
                        prevCurr = curr;
                    }
                });
    }

    public void downloadAssetMapped(JAssetInfo asset, String assetName, File resDir, AtomicInteger downloadCounter) throws IOException {
        String assetPath = asset.hash.substring(0, 2) + "/" + asset.hash;
        File outFile = new File(resDir,"/"+assetName);
		
        downloadFileMonitored(mcResUrl + assetPath, outFile, getByteBuffer(),
                new Tools.DownloaderFeedback() {
                    int prevCurr;
                    @Override
                    public void updateProgress(int curr, int max) {
                        downloadCounter.addAndGet(curr - prevCurr);
                        prevCurr = curr;
                    }
                });
    }

    protected void downloadLibrary(DependentLibrary libItem, String libArtifact, File outLib) throws Throwable{
        String libPathURL;
        boolean skipIfFailed = false;

        if (libItem.downloads == null || libItem.downloads.artifact == null) {
            MinecraftLibraryArtifact artifact = new MinecraftLibraryArtifact();
            artifact.url = (libItem.url == null
                    ? mcLibrariesUrl
                    : libItem.url.replace("http://","https://")) + libArtifact;
            libItem.downloads = new DependentLibrary.LibraryDownloads(artifact);

            skipIfFailed = true;
        }

        try {
            libPathURL = libItem.downloads.artifact.url;
            boolean isFileGood = false;
            byte timesChecked=0;
            while(!isFileGood) {
                timesChecked++;
                if(timesChecked > 5) throw new RuntimeException("Library download failed after 5 retries");

                downloadFileMonitored(libPathURL, outLib, getByteBuffer(),
                        (curr, max) -> ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT,
                                (int) Math.max((float)curr/max*100,0), R.string.mcl_launch_downloading, outLib.getName())
                );

                isFileGood = (libItem.downloads.artifact.sha1 == null
                        || LauncherPreferences.PREF_CHECK_LIBRARY_SHA)
                        || Tools.compareSHA1(outLib,libItem.downloads.artifact.sha1);

                ProgressLayout.setProgress(ProgressLayout.DOWNLOAD_MINECRAFT, 0,
                        isFileGood ? R.string.dl_library_sha_pass : R.string.dl_library_sha_unknown
                        ,outLib.getName());
            }
        } catch (Throwable th) {
            Log.e("AsyncMcDownloader", th.toString(), th);
            if (!skipIfFailed) {
                throw th;
            } else {
                th.printStackTrace();
            }
        }
    }

    public JAssets downloadIndex(JMinecraftVersionList.Version version, File output) throws IOException {
        if (!output.exists()) {
            output.getParentFile().mkdirs();
            DownloadUtils.downloadFile(version.assetIndex != null
                    ? version.assetIndex.url
                    : "https://s3.amazonaws.com/Minecraft.Download/indexes/" + version.assets + ".json", output);
        }

        return Tools.GLOBAL_GSON.fromJson(Tools.read(output.getAbsolutePath()), JAssets.class);
    }

    public static String normalizeVersionId(String versionString) {
        JMinecraftVersionList versionList = (JMinecraftVersionList) ExtraCore.getValue(ExtraConstants.RELEASE_TABLE);
        if(versionList == null || versionList.versions == null) return versionString;
        if("latest-release".equals(versionString)) versionString = versionList.latest.get("release");
        if("latest-snapshot".equals(versionString)) versionString = versionList.latest.get("snapshot");
        return versionString;
    }

    public static JMinecraftVersionList.Version getListedVersion(String normalizedVersionString) {
        JMinecraftVersionList versionList = (JMinecraftVersionList) ExtraCore.getValue(ExtraConstants.RELEASE_TABLE);
        if(versionList == null || versionList.versions == null) return null; // can't have listed versions if there's no list
        for(JMinecraftVersionList.Version version : versionList.versions) {
            if(version.id.equals(normalizedVersionString)) return version;
        }
        return null;
    }


    /**@return A byte buffer bound to a thread, useful to recycle it across downloads */
    private byte[] getByteBuffer(){
        byte[] buffer = mThreadBuffers.get(Thread.currentThread());
        if (buffer == null){
            buffer = new byte[8192];
            mThreadBuffers.put(Thread.currentThread(), buffer);
        }

        return buffer;
    }
	
	private String addMissingInUrl(String needle) {
		return needle.endsWith("/") ? needle : needle + "/";
	}

    public interface DoneListener{
        void onDownloadDone();
    }

}
