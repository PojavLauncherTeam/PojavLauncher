package net.kdt.pojavlaunch;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGL15;
import android.os.Build;
import android.os.Bundle;
import android.system.ErrnoException;
import android.system.Os;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;

import net.kdt.pojavlaunch.utils.JREUtils;

import org.apache.commons.io.IOUtils;
import org.lwjgl.glfw.CallbackBridge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Formatter;

public class RenderingTestRunActivity extends LoggableActivity {
    private static final String TAG = "RTRA";
    private MinecraftGLView minecraftGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_with_customctrl);

        minecraftGLView = findViewById(R.id.main_game_render_view);

        minecraftGLView.setFocusable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            minecraftGLView.setDefaultFocusHighlightEnabled(false);
        }

        minecraftGLView.setSurfaceTextureListener(new STL());
    }

    @Override
    public void appendToLog(String text, boolean checkAllow) {
        Log.i(TAG, text);
    }

    public static void runJava(LoggableActivity act) {
        File jarPath = new File(Tools.DIR_GAME_HOME, "test.jar");

        try (OutputStream out = new FileOutputStream(jarPath)) {
            URL url = new URL("http://10.0.2.24:8001/GraphicsTest.jar");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn.getResponseCode() != 200)
                throw new IOException("Bad response code " + conn.getResponseCode());

            try (InputStream in = conn.getInputStream()) {
                IOUtils.copy(in, out);
            }
        } catch (IOException e) {
            Log.w(TAG, "Failed to download test.jar", e);
            return;
        }

        try {
            Os.setenv("QUESTCRAFT_TEST_NOVR", "true", true);
        } catch (ErrnoException e) {
            Log.w(TAG, "Failed to set QUESTCRAFT_TEST_NOVR env var", e);
            return;
        }

        // Set the width and height to be used by the client
        DisplayMetrics displayMetrics = Tools.getDisplayMetrics(act);
        float scaleFactor = 1;
        CallbackBridge.windowWidth = (int) ((float) displayMetrics.widthPixels * scaleFactor);
        CallbackBridge.windowHeight = (int) ((float) displayMetrics.heightPixels * scaleFactor);

        Log.i(TAG, "Start JVM");
        String launchClassPath = jarPath.getAbsolutePath();
        String mainClass = "xyz.znix.graphicstest.Main";
        List<String> launchArgs = new ArrayList<>(); // TODO

        ArrayList<String> javaArgList = new ArrayList<>();
        javaArgList.add("-cp");
        javaArgList.add(Tools.getLWJGL3ClassPath() + ":" + launchClassPath);

        javaArgList.add(mainClass);
        javaArgList.addAll(launchArgs);
        Log.i(TAG, "full args: " + javaArgList.toString());
        try {
            JREUtils.launchJavaVM(act, javaArgList);
        } catch (Throwable throwable) {
            Log.w(TAG, "JVM failed with exception", throwable);
        }

        // clean things up properly
        // will probably break on the quest, but hey it doesn't matter unless java exits
        act.runOnUiThread(BaseMainActivity::fullyExit);
    }

    private class STL implements TextureView.SurfaceTextureListener {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            Log.i(TAG, "Got surface texture");

            JREUtils.setupBridgeWindow(new Surface(surface));
            Thread thr = new Thread(() -> runJava(RenderingTestRunActivity.this));
            thr.setName("java-thread");
            thr.start();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            Log.i(TAG, "surface texture size: " + width + " " + height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            Log.i(TAG, "surface texture destroyed");
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            Log.v(TAG, "surface texture updated");
            // texture.setDefaultBufferSize(CallbackBridge.windowWidth,CallbackBridge.windowHeight);
        }
    }

    @Override
    public void onBackPressed() {
        BaseMainActivity.fullyExit();
    }
}
