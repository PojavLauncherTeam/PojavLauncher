package net.kdt.pojavlaunch.settings;

import static net.kdt.pojavlaunchl.MainActivity.MainActivityContext;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.util.Log;

import com.fcl.plugin.mobileglues.MainActivity;
import com.fcl.plugin.mobileglues.utils.Constants;
import com.fcl.plugin.mobileglues.utils.FileUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MGConfig {

    private int enableANGLE;
    private int enableNoError;
    private int enableExtGL43;
    private int enableExtComputeShader;
    private int maxGlslCacheSize;

    public MGConfig(int enableANGLE, int enableNoError, int enableExtGL43, int enableExtComputeShader, int maxGlslCacheSize) {
        this.enableANGLE = enableANGLE;
        this.enableNoError = enableNoError;
        this.enableExtGL43 = enableExtGL43;
        this.enableExtComputeShader = enableExtComputeShader;
        this.maxGlslCacheSize = maxGlslCacheSize;
    }

    public void setMaxGlslCacheSize(int maxGlslCacheSize) throws IOException {
        if (maxGlslCacheSize < -1 || maxGlslCacheSize == 0)
            return;
        if (maxGlslCacheSize == -1)
            clearCacheFile();
        this.maxGlslCacheSize = maxGlslCacheSize;
        saveConfig();
    }
    
    public void setEnableANGLE(int enableANGLE) throws IOException {
        this.enableANGLE = enableANGLE;
        saveConfig();
    }

    public void setEnableNoError(int enableNoError) throws IOException {
        this.enableNoError = enableNoError;
        saveConfig();
    }

    public void setEnableExtGL43(int enableExtGL43) throws IOException {
        this.enableExtGL43 = enableExtGL43;
        saveConfig();
    }

    public void setEnableExtComputeShader(int enableExtComputeShader) throws IOException {
        this.enableExtComputeShader = enableExtComputeShader;
        saveConfig();
    }

    public int getEnableANGLE() {
        return enableANGLE;
    }

    public int getEnableNoError() {
        return enableNoError;
    }

    public int getEnableExtGL43() {
        return enableExtGL43;
    }

    public int getEnableExtComputeShader() {
        return enableExtComputeShader;
    }
    
    public int getMaxGlslCacheSize() { return maxGlslCacheSize; }

    private void clearCacheFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Uri cacheUri = DocumentsContract.buildDocumentUriUsingTree(MainActivity.MGDirectoryUri,
                    DocumentsContract.getTreeDocumentId(MainActivity.MGDirectoryUri) + "/glsl_cache.tmp");
            try {
                DocumentsContract.deleteDocument(MainActivityContext.getContentResolver(), cacheUri);
            } catch (IOException | RuntimeException ignored) { }
        } else {
            try {
                FileUtils.deleteFile(new File(Constants.GLSL_CACHE_FILE_PATH));
            } catch (IOException ignored) { }
        }
    }

    public void saveConfig() throws IOException {
        save(MainActivityContext);
    }
    
    public void saveConfig(Context context) {
        try {
            save(context);
        } catch (RuntimeException | IOException e) {
            Log.e("MG", "Failed to save the config file: " + e.getMessage());
        }
    }

    private void save(Context context) throws IOException {
        String configStr = new Gson().toJson(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (MainActivity.MGDirectoryUri == null) {
                throw new IOException("SAF directory not selected");
            }
            FileUtils.writeText(context, MainActivity.MGDirectoryUri, "config.json", configStr, "application/json");
        } else {
            FileUtils.writeText(new File(Constants.CONFIG_FILE_PATH), configStr);
        }
    }

    public static MGConfig loadConfig(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (MainActivity.MGDirectoryUri != null) {
                try {
                    Uri configUri = DocumentsContract.buildDocumentUriUsingTree(MainActivity.MGDirectoryUri,
                            DocumentsContract.getTreeDocumentId(MainActivity.MGDirectoryUri) + "/config.json");
                    String configStr = FileUtils.readText(context, configUri);
                    return new Gson().fromJson(configStr, MGConfig.class);
                } catch (RuntimeException | IOException e) {
                    return null;
                }
            }
            return null;
        } else {
            if (!Files.exists(new File(Constants.CONFIG_FILE_PATH).toPath())) {
                return null;
            }
            try {
                String configStr = FileUtils.readText(new File(Constants.CONFIG_FILE_PATH));
                return new Gson().fromJson(configStr, MGConfig.class);
            } catch (RuntimeException | IOException e) {
                return null; 
            }
        }
    }
}
