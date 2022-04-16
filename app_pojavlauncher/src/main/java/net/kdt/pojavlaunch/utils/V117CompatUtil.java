package net.kdt.pojavlaunch.utils;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import net.kdt.pojavlaunch.BaseLauncherActivity;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.tasks.MinecraftDownloaderTask;
import net.kdt.pojavlaunch.value.PerVersionConfig;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class V117CompatUtil {
    /*
    /*
                        New rendering engine was added in snapshot 21w10a
                        21a08b (FP (GL2) engine): 20210225
                        21w10a (non-FP engine): 20210310

    boolean skipResDialog = false;
                    if(mcReleaseDate > 20210225) skipResDialog = true;
                    PerVersionConfig.update(); //Prepare the PVC
    PerVersionConfig.VersionConfig cfg = PerVersionConfig.configMap.get(p1[0]); //Get the version config...
                    if (cfg == null) {
        cfg = new PerVersionConfig.VersionConfig();//or create a new one!
        PerVersionConfig.configMap.put(p1[0], cfg);//and put it into the base
    }
                    MCOptionUtils.load();

                    if(!skipResDialog) {
        AtomicBoolean proceed = new AtomicBoolean(false);
        Object lock = new Object();
        mActivity.runOnUiThread(() -> {
            AlertDialog.Builder bldr = new AlertDialog.Builder(mActivity);
            bldr.setTitle(R.string.global_warinng);
            bldr.setMessage(R.string.compat_117_message);
            bldr.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                proceed.set(true);
                synchronized (lock) { lock.notifyAll(); }
                dialog.dismiss();
            });
            bldr.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                synchronized (lock) { lock.notifyAll(); }
                dialog.dismiss();
            });
            bldr.setCancelable(false);
            bldr.show();
        });
        synchronized (lock) {
            lock.wait();
        }
        if(proceed.get()) {
            File resourcepacksDir = new File(cfg.gamePath==null? Tools.DIR_GAME_NEW:cfg.gamePath,"resourcepacks");
            if(!resourcepacksDir.exists()) resourcepacksDir.mkdirs();
            FileOutputStream fos = new FileOutputStream(new File(resourcepacksDir,"assets-v0.zip"));
            InputStream is = this.mActivity.getAssets().open("assets-v0.zip");
            IOUtils.copy(is,fos);
            is.close();fos.close();
            String resourcepacks = MCOptionUtils.get("resourcePacks");
            if(resourcepacks == null || !resourcepacks.contains("assets-v0.zip")) {
                List<String> resPacksArray = resourcepacks == null ? new ArrayList(): Arrays.asList()
            }
        }else throw new MinecraftDownloaderTask.SilentException();
    }
     */
    private static List<String> getTexturePackList(String param) {
        if (param == null) {
            Log.i("V117CompatDebug","null, defaulting to empty");
            return new ArrayList<>();
        }
        Log.i("V117CompatDebug",param);
        if("[]".equals(param)) return new ArrayList<>();
        Log.i("V117CompatDebug","ph2");
        if(param == null) return new ArrayList<>();
        Log.i("V117CompatDebug","ph3");
        String rawList = param.substring(1,param.length()-1);
        Log.i("V117CompatDebug",rawList);
        return new ArrayList<>(Arrays.asList(rawList.split(",")));
    }

    private static String regenPackList(List<String> packs) {
        if(packs.size()==0) return "[]";
        StringBuilder ret = new StringBuilder("[" + packs.get(0));
        for(int i = 1; i < packs.size(); i++) {
            ret.append(",").append(packs.get(i));
        }
        ret.append("]");
        return ret.toString();
    }

    public static void runCheck(String version, Activity activity) throws Exception{


        MCOptionUtils.load();

        List<String> packList =getTexturePackList(MCOptionUtils.get("resourcePacks"));
        String renderer;
        String gamePath;
        LauncherProfiles.update();
        MinecraftProfile prof = LauncherProfiles.mainProfileJson.profiles.get(((BaseLauncherActivity)ctx).mProfile.selectedProfile);
        if(prof == null) throw new MinecraftDownloaderTask.SilentException();
        renderer = prof.pojavRendererName != null ? prof.pojavRendererName : LauncherPreferences.PREF_RENDERER;
        gamePath = prof.gameDir != null && prof.gameDir.startsWith(Tools.LAUNCHERPROFILES_RTPREFIX) ? prof.gameDir.replace(Tools.LAUNCHERPROFILES_RTPREFIX,Tools.DIR_GAME_HOME + "/") : Tools.DIR_GAME_NEW;

        //String

        if(renderer.equals("vulkan_zink") || renderer.equals("opengles3_virgl")) return; //don't install for zink/virgl users;
        if(packList.contains("\"assets-v0.zip\"")) return;
        if(JREUtils.getDetectedVersion() >= 3) return; // GL4ES_extra supports 1.17+

        Object lock = new Object();
        AtomicInteger proceed = new AtomicInteger(0);
        activity.runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.global_warinng);
            builder.setMessage(R.string.compat_117_message);
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                proceed.set(1);
                synchronized (lock) { lock.notifyAll(); }
            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                synchronized (lock) { lock.notifyAll(); }
            });
            builder.setNeutralButton(R.string.compat_11x_playanyway, (dialog, which) -> {
                proceed.set(2);
                synchronized (lock) { lock.notifyAll(); }
            });
            builder.setCancelable(false);
            builder.show();
        });

        synchronized (lock) {
            lock.wait();
        }
        switch(proceed.get()) {
            case 1:
                MinecraftProfile minecraftProfile = LauncherProfiles.mainProfileJson.profiles.get(((BaseLauncherActivity)activity).mProfile.selectedProfile);
                if(minecraftProfile == null) throw new MinecraftDownloaderTask.SilentException();
                minecraftProfile.pojavRendererName = "opengles2";
                LauncherProfiles.update();
                copyResourcePack(gamePath,activity.getAssets());
                if(!packList.contains("\"assets-v0.zip\"")) packList.add(0,"\"assets-v0.zip\"");
                MCOptionUtils.set("resourcePacks",regenPackList(packList));
                MCOptionUtils.save();
                break;
            case 0:
                throw new MinecraftDownloaderTask.SilentException();
        }
    }

    public static void copyResourcePack(String gameDir, AssetManager am) throws IOException {
        File resourcepacksDir = new File(gameDir,"resourcepacks");
        if(!resourcepacksDir.exists()) resourcepacksDir.mkdirs();
        FileOutputStream fos = new FileOutputStream(new File(resourcepacksDir,"assets-v0.zip"));
        InputStream is = am.open("assets-v0.zip");
        IOUtils.copy(is,fos);
        is.close();fos.close();
    }
}
