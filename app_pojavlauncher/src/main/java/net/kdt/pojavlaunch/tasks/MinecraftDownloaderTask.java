package net.kdt.pojavlaunch.tasks;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import com.google.gson.*;
import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.utils.*;
import net.kdt.pojavlaunch.value.*;
import org.apache.commons.io.*;

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
                String verJsonDir = Tools.DIR_HOME_VERSION + downVName + ".json";

                verInfo = findVersion(p1[0]);

                if (verInfo.url != null && !new File(verJsonDir).exists()) {
                    publishProgress("1", mActivity.getString(R.string.mcl_launch_downloading, p1[0] + ".json"));

                    Tools.downloadFileMonitored(
                        verInfo.url,
                        verJsonDir,
                            new Tools.DownloaderFeedback(){
                                @Override
                                public void updateProgress(int curr, int max) {
                                    publishDownloadProgress(p1[0] + ".json", curr, max);
                                }
                            }
                    );
                }

                verInfo = Tools.getVersionInfo(mActivity,p1[0]);
                try {
                    assets = downloadIndex(verInfo.assets, new File(Tools.ASSETS_PATH, "indexes/" + verInfo.assets + ".json"));
                } catch (IOException e) {
                    publishProgress("0", Log.getStackTraceString(e));
                }

                File outLib;
                String libPathURL;

                setMax(verInfo.libraries.length);
                zeroProgress();
                for (final DependentLibrary libItem : verInfo.libraries) {

                    if (
                        libItem.name.startsWith("net.java.jinput") ||
                        libItem.name.startsWith("org.lwjgl")
                    ) { // Black list
                        publishProgress("1", "Ignored " + libItem.name);
                        //Thread.sleep(100);
                    } else {
                        String[] libInfo = libItem.name.split(":");
                        String libArtifact = Tools.artifactToPath(libInfo[0], libInfo[1], libInfo[2]);
                        outLib = new File(Tools.DIR_HOME_LIBRARY + "/" + libArtifact);
                        outLib.getParentFile().mkdirs();

                        if (!outLib.exists()) {
                            publishProgress("1", mActivity.getString(R.string.mcl_launch_downloading, libItem.name));

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
                            } catch (Throwable th) {
                                if (!skipIfFailed) {
                                    throw th;
                                } else {
                                    th.printStackTrace();
                                    publishProgress("0", th.getMessage());
                                }
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
            mActivity.mPlayButton.post(new Runnable(){

                    @Override
                    public void run()
                    {
                        mActivity.mPlayButton.setText("Skip");
                        mActivity.mPlayButton.setEnabled(true);
                    }
                });
                
            if (assets == null) {
                return null;
            }
            publishProgress("1", mActivity.getString(R.string.mcl_launch_download_assets));
            setMax(assets.objects.size());
            zeroProgress();
            try {
                downloadAssets(assets, verInfo.assets, assets.map_to_resources ? new File(Tools.OBSOLETE_RESOURCES_PATH) : new File(Tools.ASSETS_PATH));
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
    private int addProgress = 0;

    public void zeroProgress() {
        addProgress = 0;
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

            mActivity.mLaunchTextStatus.setText(p1[1]);
        }

        if (p1.length < 3) {
            mActivity.mConsoleView.putLog(p1[1] + "\n");
        }
    }

    @Override
    protected void onPostExecute(Throwable p1)
    {
        mActivity.mPlayButton.setText("Play");
        mActivity.mPlayButton.setEnabled(true);
        mActivity.mLaunchProgress.setMax(100);
        mActivity.mLaunchProgress.setProgress(0);
        mActivity.statusIsLaunching(false);
        if(p1 != null) {
            p1.printStackTrace();
            Tools.showError(mActivity, p1);
        }
        if(!launchWithError) {
            mActivity.mCrashView.setLastCrash("");

            try {
                /*
                 List<String> jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
                 jvmArgs.add("-Xms128M");
                 jvmArgs.add("-Xmx1G");
                 */

                Intent mainIntent = new Intent(mActivity, MainActivity.class /* MainActivity.class */);
                // mainIntent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                if (LauncherPreferences.PREF_FREEFORM) {
                    DisplayMetrics dm = new DisplayMetrics();
                    mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);

                    ActivityOptions options = (ActivityOptions) ActivityOptions.class.getMethod("makeBasic").invoke(null);
                    Rect freeformRect = new Rect(0, 0, dm.widthPixels / 2, dm.heightPixels / 2);
                    options.getClass().getDeclaredMethod("setLaunchBounds", Rect.class).invoke(options, freeformRect);
                    mActivity.startActivity(mainIntent, options.toBundle());
                } else {
                    mActivity.startActivity(mainIntent);
                }
            }
            catch (Throwable e) {
                Tools.showError(mActivity, e);
            }

            /*
             FloatingIntent maini = new FloatingIntent(PojavLauncherActivity.this, MainActivity.class);
             maini.startFloatingActivity();
             */
        }

        mActivity.mTask = null;
    }

    public static final String MINECRAFT_RES = "https://resources.download.minecraft.net/";

    public JAssets downloadIndex(String versionName, File output) throws Throwable {
        if (!output.exists()) {
            output.getParentFile().mkdirs();
            DownloadUtils.downloadFile(verInfo.assetIndex != null ? verInfo.assetIndex.url : "https://s3.amazonaws.com/Minecraft.Download/indexes/" + versionName + ".json", output);
        }

        return Tools.GLOBAL_GSON.fromJson(Tools.read(output.getAbsolutePath()), JAssets.class);
    }

    public void downloadAsset(JAssetInfo asset, File objectsDir) throws IOException, Throwable {
        String assetPath = asset.hash.substring(0, 2) + "/" + asset.hash;
        File outFile = new File(objectsDir, assetPath);
        if (!outFile.exists()) {
            DownloadUtils.downloadFile(MINECRAFT_RES + assetPath, outFile);
        }
    }
    public void downloadAssetMapped(JAssetInfo asset, String assetName, File resDir) throws Throwable {
        String assetPath = asset.hash.substring(0, 2) + "/" + asset.hash;
        File outFile = new File(resDir,"/"+assetName);
        if (!outFile.exists()) {
            DownloadUtils.downloadFile(MINECRAFT_RES + assetPath, outFile);
        }
    }

    public void downloadAssets(JAssets assets, String assetsVersion, File outputDir) throws IOException, Throwable {
        File hasDownloadedFile = new File(outputDir, "downloaded/" + assetsVersion + ".downloaded");
        if (!hasDownloadedFile.exists()) {
            System.out.println("Assets begin time: " + System.currentTimeMillis());
            Map<String, JAssetInfo> assetsObjects = assets.objects;
            mActivity.mLaunchProgress.setMax(assetsObjects.size());
            zeroProgress();
            File objectsDir = new File(outputDir, "objects");
            int downloadedSs = 0;
            for (JAssetInfo asset : assetsObjects.values()) {
                if (!mActivity.mIsAssetsProcessing) {
                    return;
                }

                if(!assets.map_to_resources) downloadAsset(asset, objectsDir);
                else downloadAssetMapped(asset,(assetsObjects.keySet().toArray(new String[0])[downloadedSs]),outputDir);
                publishProgress("1", mActivity.getString(R.string.mcl_launch_downloading, assetsObjects.keySet().toArray(new String[0])[downloadedSs]));
                downloadedSs++;
            }
            hasDownloadedFile.getParentFile().mkdirs();
            hasDownloadedFile.createNewFile();
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
