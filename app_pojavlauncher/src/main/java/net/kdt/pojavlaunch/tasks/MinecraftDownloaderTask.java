package net.kdt.pojavlaunch.tasks;

import static net.kdt.pojavlaunch.Tools.ENABLE_DEV_FEATURES;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.multirt.Runtime;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.utils.*;
import net.kdt.pojavlaunch.value.*;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import org.apache.commons.io.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MinecraftDownloaderTask extends AsyncTask<String, String, Throwable>
 {
    private BaseLauncherActivity mActivity;
    private boolean launchWithError = false;
    MinecraftDownloaderTask thiz = this;
    public MinecraftDownloaderTask(BaseLauncherActivity activity) {
        mActivity = activity;
    }
    
    @Override
    protected void onPreExecute() {
        mActivity.mLaunchProgress.setMax(1);
        mActivity.statusIsLaunching(true);
    }

    private JMinecraftVersionList.Version verInfo;
    @Override
    protected Throwable doInBackground(final String[] p1) {
        Throwable throwable = null;
        try {
            final String downVName = "/" + p1[0] + "/" + p1[0];
            //Downloading libraries
            String minecraftMainJar = Tools.DIR_HOME_VERSION + downVName + ".jar";
            JAssets assets = null;
            try {
                File verJsonDir = new File(Tools.DIR_HOME_VERSION + downVName + ".json");

                verInfo = findVersion(p1[0]);

                if (verInfo.url != null) {
                    boolean isManifestGood = true; // assume it is dy default
                    if(verJsonDir.exists()) {//if the file exists
                        if(LauncherPreferences.PREF_CHECK_LIBRARY_SHA) {// check if the checker is on
                            if (verInfo.sha1 != null) {//check if the SHA is available
                                if (Tools.compareSHA1(verJsonDir, verInfo.sha1)) // check the SHA
                                    publishProgress("0", mActivity.getString(R.string.dl_library_sha_pass, p1[0] + ".json")); // and say that SHA was verified
                                else{
                                    verJsonDir.delete(); // if it wasn't, delete the old one
                                    publishProgress("0", mActivity.getString(R.string.dl_library_sha_fail, p1[0] + ".json")); // put it into log
                                    isManifestGood = false;  // and mark manifest as bad
                                }
                            }else{
                                publishProgress("0", mActivity.getString(R.string.dl_library_sha_unknown, p1[0] + ".json")); // say that the sha is unknown, but assume that the manifest is good
                            }
                        }else{
                            Log.w("Chk","Checker is off");// if the checker is off, manifest will be always good, unless it doesn't exist
                        }
                    } else {
                         isManifestGood = false;
                    }
                    if(!isManifestGood) {
                        publishProgress("1", mActivity.getString(R.string.mcl_launch_downloading, p1[0] + ".json"));
                        Tools.downloadFileMonitored(
                                verInfo.url,
                                verJsonDir.getAbsolutePath(),
                                new Tools.DownloaderFeedback() {
                                    @Override
                                    public void updateProgress(int curr, int max) {
                                        publishDownloadProgress(p1[0] + ".json", curr, max);
                                    }
                                }
                        );
                    }
                }

                verInfo = Tools.getVersionInfo(mActivity,p1[0]);

                //Now we have the reliable information to check if our runtime settings are good enough
                if(verInfo.javaVersion != null && !verInfo.javaVersion.component.equalsIgnoreCase("jre-legacy")) { //1.17+
                    LauncherProfiles.update();
                    MinecraftProfile minecraftProfile = LauncherProfiles.mainProfileJson.profiles.get(LauncherPreferences.DEFAULT_PREF.getString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE,""));
                    if(minecraftProfile == null) throw new SilentException();
                    String selectedRuntime = null;
                    if(minecraftProfile.javaDir != null && minecraftProfile.javaDir.startsWith(Tools.LAUNCHERPROFILES_RTPREFIX)) {
                        selectedRuntime = minecraftProfile.javaDir.substring(Tools.LAUNCHERPROFILES_RTPREFIX.length());
                    }
                    Runtime runtime = selectedRuntime != null?MultiRTUtils.read(selectedRuntime):MultiRTUtils.read(LauncherPreferences.PREF_DEFAULT_RUNTIME);
                    if(runtime.javaVersion < verInfo.javaVersion.majorVersion) {
                         String appropriateRuntime = MultiRTUtils.getNearestJreName(verInfo.javaVersion.majorVersion);
                         if(appropriateRuntime != null) {
                             if(JRE17Util.isInternalNewJRE(appropriateRuntime)) {
                                 JRE17Util.checkInternalNewJre(mActivity, ((resId, stuff) -> publishProgress("0",mActivity.getString(resId,stuff))));
                             }
                             minecraftProfile.javaDir = Tools.LAUNCHERPROFILES_RTPREFIX+appropriateRuntime;
                             LauncherProfiles.update();
                         }else{
                             if(verInfo.javaVersion.majorVersion <= 17) { // there's a chance we have an internal one for this case
                                 if(!JRE17Util.checkInternalNewJre(mActivity, ((resId, stuff) -> publishProgress("0",mActivity.getString(resId,stuff)))))
                                     showRuntimeFail();
                                 else {
                                     minecraftProfile.javaDir = Tools.LAUNCHERPROFILES_RTPREFIX+JRE17Util.NEW_JRE_NAME;
                                     LauncherProfiles.update();
                                 }
                             }else showRuntimeFail();
                         }
                     } //if else, we are satisfied
                }

                try {
                    assets = downloadIndex(verInfo.assets, new File(Tools.ASSETS_PATH, "indexes/" + verInfo.assets + ".json"));
                } catch (IOException e) {
                    publishProgress("0", Log.getStackTraceString(e));
                }

                File outLib;

                // Patch the Log4J RCE (CVE-2021-44228)
                if (verInfo.logging != null) {
                    outLib = new File(Tools.DIR_DATA, verInfo.logging.client.file.id.replace("client", "log4j-rce-patch"));
                    boolean useLocal = outLib.exists();
                    if (!useLocal) {
                        outLib = new File(Tools.DIR_GAME_NEW, verInfo.logging.client.file.id);
                    }
                    if (outLib.exists() && !useLocal) {
                        if(LauncherPreferences.PREF_CHECK_LIBRARY_SHA) {
                            if(!Tools.compareSHA1(outLib,verInfo.logging.client.file.sha1)) {
                                outLib.delete();
                                publishProgress("0", mActivity.getString(R.string.dl_library_sha_fail,verInfo.logging.client.file.id));
                            }else{
                                publishProgress("0", mActivity.getString(R.string.dl_library_sha_pass,verInfo.logging.client.file.id));
                            }
                        } else if (outLib.length() != verInfo.logging.client.file.size) {
                            // force updating anyways
                            outLib.delete();
                        }
                    }
                    if (!outLib.exists()) {
                        publishProgress("0", mActivity.getString(R.string.mcl_launch_downloading, verInfo.logging.client.file.id));
                        Tools.downloadFileMonitored(
                            verInfo.logging.client.file.url,
                            outLib.getAbsolutePath(),
                            new Tools.DownloaderFeedback() {
                                @Override
                                public void updateProgress(int curr, int max) {
                                    publishDownloadProgress(verInfo.logging.client.file.id, curr, max);
                                }
                            }
                        );
                    }
                }

                setMax(verInfo.libraries.length);
                zeroProgress();
                for (final DependentLibrary libItem : verInfo.libraries) {

                    if (
                        // libItem.name.startsWith("net.java.jinput") ||
                        libItem.name.startsWith("org.lwjgl")
                    ) { // Black list
                        publishProgress("1", "Ignored " + libItem.name);
                        //Thread.sleep(100);
                    } else {
                        String libArtifact = Tools.artifactToPath(libItem.name);
                        outLib = new File(Tools.DIR_HOME_LIBRARY + "/" + libArtifact);
                        outLib.getParentFile().mkdirs();

                        if (!outLib.exists()) {
                            downloadLibrary(libItem,libArtifact,outLib);
                        }else{
                            if(libItem.downloads != null && libItem.downloads.artifact != null && libItem.downloads.artifact.sha1 != null && !libItem.downloads.artifact.sha1.isEmpty()) {
                                if(!Tools.compareSHA1(outLib,libItem.downloads.artifact.sha1)) {
                                    outLib.delete();
                                    publishProgress("0", mActivity.getString(R.string.dl_library_sha_fail,libItem.name));
                                    downloadLibrary(libItem,libArtifact,outLib);
                                }else{
                                    publishProgress("0", mActivity.getString(R.string.dl_library_sha_pass,libItem.name));
                                }
                            }else{
                                publishProgress("0", mActivity.getString(R.string.dl_library_sha_unknown,libItem.name));
                            }
                        }
                    }
                }
                setMax(3);
                zeroProgress();
                publishProgress("1", mActivity.getString(R.string.mcl_launch_downloading, p1[0] + ".jar"));
                File minecraftMainFile = new File(minecraftMainJar);
                if ((!minecraftMainFile.exists() || minecraftMainFile.length() == 0l) &&
                  verInfo.downloads != null) {
                    try {
                        Tools.downloadFileMonitored(
                            verInfo.downloads.values().toArray(new MinecraftClientInfo[0])[0].url,
                            minecraftMainJar,
                                new Tools.DownloaderFeedback() {
                                    @Override
                                    public void updateProgress(int curr, int max) {
                                        publishDownloadProgress(p1[0] + ".jar", curr, max);
                                    }
                                }
                        );
                    } catch (Throwable th) {
                        if (verInfo.inheritsFrom != null) {
                            minecraftMainFile.delete();
                            FileInputStream is = new FileInputStream(new File(Tools.DIR_HOME_VERSION, verInfo.inheritsFrom + "/" + verInfo.inheritsFrom + ".jar"));
                            FileOutputStream os = new FileOutputStream(minecraftMainFile);
                            IOUtils.copy(is, os);
                            is.close();
                            os.close();
                        } else {
                            throw th;
                        }
                    }
                }
            } catch (Throwable e) {
                launchWithError = true;
                throw e;
            }

            publishProgress("1", mActivity.getString(R.string.mcl_launch_cleancache));
            // new File(inputPath).delete();

            for (File f : new File(Tools.DIR_HOME_VERSION).listFiles()) {
                if(f.getName().endsWith(".part")) {
                    Log.d(Tools.APP_NAME, "Cleaning cache: " + f);
                    f.delete();
                }
            }

            mActivity.mIsAssetsProcessing = true;
            if(ENABLE_DEV_FEATURES){
                mActivity.mPlayButton.post(new Runnable(){

                    @Override
                    public void run()
                    {
                        mActivity.mPlayButton.setText("Skip");
                        mActivity.mPlayButton.setEnabled(true);
                    }
                });
            }

                
            if (assets == null) {
                return null;
            }
            publishProgress("1", mActivity.getString(R.string.mcl_launch_download_assets));
            setMax(assets.objects.size());
            zeroProgress();
            try {
                downloadAssets(assets, verInfo.assets, assets.mapToResources ? new File(Tools.OBSOLETE_RESOURCES_PATH) : new File(Tools.ASSETS_PATH));
            } catch (Exception e) {
                e.printStackTrace();

                // Ignore it
                launchWithError = false;
            } finally {
                mActivity.mIsAssetsProcessing = false;
            }
        } catch (Throwable th){
            throwable = th;
        } finally {
            return throwable;
        }
    }
     private void showRuntimeFail() throws SilentException{
         mActivity.runOnUiThread(()->{
             AlertDialog.Builder bldr = new AlertDialog.Builder(mActivity);
             bldr.setTitle(R.string.global_error);
             bldr.setMessage(mActivity.getString(R.string.multirt_nocompartiblert, verInfo.javaVersion.majorVersion));
             bldr.setPositiveButton(android.R.string.ok,(dialog, which)->{
                 dialog.dismiss();
             });
             bldr.show();
         });
         throw new SilentException();
     }
    private int addProgress = 0;
    public static class SilentException extends Exception{}
    public void zeroProgress() {
        addProgress = 0;
    }
    protected void downloadLibrary(DependentLibrary libItem,String libArtifact,File outLib) throws Throwable{
        publishProgress("1", mActivity.getString(R.string.mcl_launch_downloading, libItem.name));
        String libPathURL;
        boolean skipIfFailed = false;

        if (libItem.downloads == null || libItem.downloads.artifact == null) {
            System.out.println("UnkLib:"+libArtifact);
            MinecraftLibraryArtifact artifact = new MinecraftLibraryArtifact();
            artifact.url = (libItem.url == null ? "https://libraries.minecraft.net/" : libItem.url.replace("http://","https://")) + libArtifact;
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
                Tools.downloadFileMonitored(
                        libPathURL,
                        outLib.getAbsolutePath(),
                        new Tools.DownloaderFeedback() {
                            @Override
                            public void updateProgress(int curr, int max) {
                                publishDownloadProgress(libItem.name, curr, max);
                            }
                        }
                );
                if(libItem.downloads.artifact.sha1 != null) {
                    isFileGood = LauncherPreferences.PREF_CHECK_LIBRARY_SHA ? Tools.compareSHA1(outLib,libItem.downloads.artifact.sha1) : true;
                    if(!isFileGood) publishProgress("0", mActivity.getString(R.string.dl_library_sha_fail,libItem.name));
                    else publishProgress("0", mActivity.getString(R.string.dl_library_sha_pass,libItem.name));
                }else{
                    publishProgress("0", mActivity.getString(R.string.dl_library_sha_unknown,libItem.name));
                    isFileGood = true;
                }
            }
        } catch (Throwable th) {
            if (!skipIfFailed) {
                throw th;
            } else {
                th.printStackTrace();
                publishProgress("0", th.getMessage());
            }
        }
    }
    public void setMax(final int value)
    {
        mActivity.mLaunchProgress.post(new Runnable(){

                @Override
                public void run()
                {
                    mActivity.mLaunchProgress.setMax(value);
                }
            });
    }
    
    private void publishDownloadProgress(String target, int curr, int max) {
        // array length > 2 ignores append log on dev console
        publishProgress("0", mActivity.getString(R.string.mcl_launch_downloading_progress, target,
            curr / 1024d / 1024d, max / 1024d / 1024d), "");
    }

    @Override
    protected void onProgressUpdate(String... p1)
    {
        int addedProg = Integer.parseInt(p1[0]);
        if (addedProg != -1) {
            addProgress = addProgress + addedProg;
            mActivity.mLaunchProgress.setProgress(addProgress);
            if(p1[1] != null) mActivity.mLaunchTextStatus.setText(p1[1]);
        }

        if (p1.length < 3) {
            //mActivity.mConsoleView.putLog(p1[1] + "\n");
        }
    }

    @Override
    public void onPostExecute(Throwable p1)
    {
        mActivity.mPlayButton.setText("Play");
        mActivity.mPlayButton.setEnabled(true);
        mActivity.mLaunchProgress.setMax(100);
        mActivity.mLaunchProgress.setProgress(0);
        mActivity.statusIsLaunching(false);
        if(p1 != null && !(p1 instanceof SilentException)) {
            p1.printStackTrace();
            Tools.showError(mActivity, p1);
        }
        if(!launchWithError) {
            //mActivity.mCrashView.setLastCrash("");

            try {
                Intent mainIntent = new Intent(mActivity, MainActivity.class /* MainActivity.class */);
                // mainIntent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                mActivity.startActivity(mainIntent);
                mActivity.finish();
                Log.i("ActCheck","mainActivity finishing="+mActivity.isFinishing()+", destroyed="+mActivity.isDestroyed());
            }
            catch (Throwable e) {
                Tools.showError(mActivity, e);
            }
        }

        mActivity.mTask = null;
    }

    public static final String MINECRAFT_RES = "https://resources.download.minecraft.net/";

    public JAssets downloadIndex(String versionName, File output) throws IOException {
        if (!output.exists()) {
            output.getParentFile().mkdirs();
            DownloadUtils.downloadFile(verInfo.assetIndex != null ? verInfo.assetIndex.url : "https://s3.amazonaws.com/Minecraft.Download/indexes/" + versionName + ".json", output);
        }

        return Tools.GLOBAL_GSON.fromJson(Tools.read(output.getAbsolutePath()), JAssets.class);
    }

    public void downloadAsset(JAssetInfo asset, File objectsDir, AtomicInteger downloadCounter) throws IOException {
        String assetPath = asset.hash.substring(0, 2) + "/" + asset.hash;
        File outFile = new File(objectsDir, assetPath);
        Tools.downloadFileMonitored(MINECRAFT_RES + assetPath, outFile.getAbsolutePath(), new Tools.DownloaderFeedback() {
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
        Tools.downloadFileMonitored(MINECRAFT_RES + assetPath, outFile.getAbsolutePath(), new Tools.DownloaderFeedback() {
            int prevCurr;
            @Override
            public void updateProgress(int curr, int max) {
                downloadCounter.addAndGet(curr - prevCurr);
                prevCurr = curr;
            }
        });
    }
    public void downloadAssets(final JAssets assets, String assetsVersion, final File outputDir) throws IOException {
        LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 500, TimeUnit.MILLISECONDS, workQueue);
        mActivity.mIsAssetsProcessing = true;
        //File hasDownloadedFile = new File(outputDir, "downloaded/" + assetsVersion + ".downloaded");
        if (true) { //(!hasDownloadedFile.exists()) {
            System.out.println("Assets begin time: " + System.currentTimeMillis());
            Map<String, JAssetInfo> assetsObjects = assets.objects;
            int assetsSizeBytes=0;
            AtomicInteger downloadedSize = new AtomicInteger(0);
            AtomicBoolean localInterrupt = new AtomicBoolean(false);
            File objectsDir = new File(outputDir, "objects");
            zeroProgress();
            for(String assetKey : assetsObjects.keySet()) {
                if(!mActivity.mIsAssetsProcessing) break;
                JAssetInfo asset = assetsObjects.get(assetKey);
                assetsSizeBytes+=asset.size;
                String assetPath = asset.hash.substring(0, 2) + "/" + asset.hash;
                File outFile = assets.mapToResources ?new File(outputDir,"/"+assetKey):new File(objectsDir, assetPath);
                boolean skip = outFile.exists();// skip if the file exists
                if(LauncherPreferences.PREF_CHECK_LIBRARY_SHA && skip){
                    //if sha checking is enabled
                    skip = Tools.compareSHA1(outFile, asset.hash); //check hash
                }

                if(skip) {
                    downloadedSize.addAndGet(asset.size);
                }else{
                    if(outFile.exists()) publishProgress("0",mActivity.getString(R.string.dl_library_sha_fail,assetKey));
                    executor.execute(()->{
                        try {
                            if (!assets.mapToResources) {
                                downloadAsset(asset, objectsDir, downloadedSize);
                            } else {
                                downloadAssetMapped(asset, assetKey, outputDir, downloadedSize);
                            }
                        }catch (IOException e) {
                            e.printStackTrace();
                            localInterrupt.set(true);
                        }
                    });
                }
            }
            mActivity.mLaunchProgress.setMax(assetsSizeBytes);
            executor.shutdown();
            try {
                int prevDLSize=0;
                System.out.println("Queue size: "+workQueue.size());
                while ((!executor.awaitTermination(250, TimeUnit.MILLISECONDS))&&(!localInterrupt.get())&&mActivity.mIsAssetsProcessing) {
                    int DLSize = downloadedSize.get();
                    publishProgress(Integer.toString(DLSize-prevDLSize),null,"");
                    publishDownloadProgress("assets", DLSize, assetsSizeBytes);
                    prevDLSize = downloadedSize.get();
                }
                if(mActivity.mIsAssetsProcessing) {
                    System.out.println("Unskipped download done!");
                    //if(!hasDownloadedFile.getParentFile().exists())hasDownloadedFile.getParentFile().mkdirs();
                    //hasDownloadedFile.createNewFile();
                }else{
                    System.out.println("Skipped!");
                }
                executor.shutdownNow();
                while (!executor.awaitTermination(250, TimeUnit.MILLISECONDS)) {}
                System.out.println("Fully shut down!");
            }catch(InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Assets end time: " + System.currentTimeMillis());
        }
    }

    private JMinecraftVersionList.Version findVersion(String version) {
        if (mActivity.mVersionList != null) {
            for (JMinecraftVersionList.Version valueVer: mActivity.mVersionList.versions) {
                if (valueVer.id.equals(version)) {
                    return valueVer;
                }
            }
        }

        // Custom version, inherits from base.
        return Tools.getVersionInfo(mActivity,version);
    }
}
