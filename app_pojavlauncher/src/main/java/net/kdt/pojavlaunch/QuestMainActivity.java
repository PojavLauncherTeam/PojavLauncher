package net.kdt.pojavlaunch;

import android.app.Activity;
import android.app.NativeActivity;
import android.util.Log;

import net.kdt.pojavlaunch.utils.JREUtils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class QuestMainActivity extends NativeActivity implements ILoggableActivity {
    private static final String TAG = "QuestActivity";

    private static final File APP_JAR = new File(Tools.DIR_GAME_HOME, "test.jar");

    // Called from native code
    @SuppressWarnings("unused")
    public void setup() {
        try (OutputStream out = new FileOutputStream(APP_JAR)) {
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
            JREUtils.relocateLibPath(this);
            JREUtils.setJavaEnvironment(this, null);
        } catch (Throwable throwable) {
            throw new RuntimeException("Could not setup Java environment", throwable);
        }

        Log.e(TAG, "Calling Java setup");
        JREUtils.initJavaRuntime();
    }

    /**
     * Get the list of arguments to be supplied to the JVM.
     * <p>
     * Called from native code.
     */
    @SuppressWarnings("unused")
    public String[] getJavaArgs() {
        ArrayList<String> args = new ArrayList<>();
        args.add("java");
        args.add("-cp");
        args.add(Tools.getLWJGL3ClassPath() + ":" + APP_JAR);
        Tools.getJavaArgs(this, args);

        args.add("xyz.znix.graphicstest.Main");

        return args.toArray(new String[0]);
    }

    @Override
    public Activity asActivity() {
        return this;
    }

    @Override
    public void appendToLog(String text, boolean checkAllow) {
        Log.i(TAG, text);
    }
}
