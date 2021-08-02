package net.kdt.pojavlaunch.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.tasks.MinecraftDownloaderTask;
import net.kdt.pojavlaunch.value.PerVersionConfig;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
        String ret = "["+packs.get(0);
        for(int i = 1; i < packs.size(); i++) {
            ret += ","+packs.get(i);
        }
        ret += "]";
        return ret;
    }
    public static void runCheck(String version, Activity ctx) throws Exception{

        PerVersionConfig.VersionConfig cfg = PerVersionConfig.configMap.get(version);
        MCOptionUtils.load();

        List<String> packList =getTexturePackList(MCOptionUtils.get("resourcePacks"));
        String renderer = cfg != null && cfg.renderer != null?cfg.renderer:LauncherPreferences.PREF_RENDERER;

        if(renderer.equals("vulkan_zink")) return; //don't install for zink users;
        if(packList.contains("\"assets-v0.zip\"") && renderer.equals("opengles2_5")) return;

        Object lock = new Object();
        AtomicInteger proceed = new AtomicInteger(0);
        ctx.runOnUiThread(() -> {
            AlertDialog.Builder bldr = new AlertDialog.Builder(ctx);
            bldr.setTitle(R.string.global_warinng);
            bldr.setMessage(R.string.compat_117_message);
            bldr.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                proceed.set(1);
                synchronized (lock) { lock.notifyAll(); }
                dialog.dismiss();
            });
            bldr.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                synchronized (lock) { lock.notifyAll(); }
                dialog.dismiss();
            });
            bldr.setNeutralButton(R.string.compat_11x_playanyway, (dialog, which) -> {
                proceed.set(2);
                synchronized (lock) { lock.notifyAll(); }
                dialog.dismiss();
            });
            bldr.setCancelable(false);
            bldr.show();
        });

        synchronized (lock) {
            lock.wait();
        }
        switch(proceed.get()) {
            case 1:
                if (cfg == null) {
                    cfg = new PerVersionConfig.VersionConfig();
                    PerVersionConfig.configMap.put(version, cfg);
                }
                cfg.renderer = "opengles2_5";
                String path = Tools.DIR_GAME_NEW;
                if(cfg.gamePath != null && !cfg.gamePath.isEmpty()) path = cfg.gamePath;
                copyResourcePack(path,ctx.getAssets());
                if(!packList.contains("\"assets-v0.zip\"")) packList.add(0,"\"assets-v0.zip\"");
                MCOptionUtils.set("resourcePacks",regenPackList(packList));
                MCOptionUtils.save();
                PerVersionConfig.update();
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
