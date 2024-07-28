package net.kdt.pojavlaunch;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;

import com.kdt.LoggerView;

import net.kdt.pojavlaunch.customcontrols.keyboard.AwtCharSender;
import net.kdt.pojavlaunch.customcontrols.keyboard.TouchCharInput;
import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.multirt.Runtime;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.utils.JREUtils;
import net.kdt.pojavlaunch.utils.MathUtils;

import org.apache.commons.io.IOUtils;
import org.lwjgl.glfw.CallbackBridge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JavaGUILauncherActivity extends BaseActivity implements View.OnTouchListener {

    private AWTCanvasView mTextureView;
    private LoggerView mLoggerView;
    private TouchCharInput mTouchCharInput;

    private LinearLayout mTouchPad;
    private ImageView mMousePointerImageView;
    private GestureDetector mGestureDetector;

    private boolean mIsVirtualMouseEnabled;
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_gui_launcher);

        try {
            File latestLogFile = new File(Tools.DIR_GAME_HOME, "latestlog.txt");
            if (!latestLogFile.exists() && !latestLogFile.createNewFile())
                throw new IOException("Failed to create a new log file");
            Logger.begin(latestLogFile.getAbsolutePath());
        }catch (IOException e) {
            Tools.showError(this, e, true);
        }
        MainActivity.GLOBAL_CLIPBOARD = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mTouchCharInput = findViewById(R.id.awt_touch_char);
        mTouchCharInput.setCharacterSender(new AwtCharSender());

        mTouchPad = findViewById(R.id.main_touchpad);
        mLoggerView = findViewById(R.id.launcherLoggerView);
        mMousePointerImageView = findViewById(R.id.main_mouse_pointer);
        mTextureView = findViewById(R.id.installmod_surfaceview);
        mGestureDetector = new GestureDetector(this, new SingleTapConfirm());
        mTouchPad.setFocusable(false);
        mTouchPad.setVisibility(View.GONE);

        findViewById(R.id.installmod_mouse_pri).setOnTouchListener(this);
        findViewById(R.id.installmod_mouse_sec).setOnTouchListener(this);
        findViewById(R.id.installmod_window_moveup).setOnTouchListener(this);
        findViewById(R.id.installmod_window_movedown).setOnTouchListener(this);
        findViewById(R.id.installmod_window_moveleft).setOnTouchListener(this);
        findViewById(R.id.installmod_window_moveright).setOnTouchListener(this);

        mMousePointerImageView.post(() -> {
            ViewGroup.LayoutParams params = mMousePointerImageView.getLayoutParams();
            params.width = (int) (36 / 100f * LauncherPreferences.PREF_MOUSESCALE);
            params.height = (int) (54 / 100f * LauncherPreferences.PREF_MOUSESCALE);
        });

        mTouchPad.setOnTouchListener(new View.OnTouchListener() {
            float prevX = 0, prevY = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // MotionEvent reports input details from the touch screen
                // and other input controls. In this case, you are only
                // interested in events where the touch position changed.
                // int index = event.getActionIndex();
                int action = event.getActionMasked();

                float x = event.getX();
                float y = event.getY();
                float mouseX, mouseY;

                mouseX = mMousePointerImageView.getX();
                mouseY = mMousePointerImageView.getY();

                if (mGestureDetector.onTouchEvent(event)) {
                    sendScaledMousePosition(mouseX,mouseY);
                    AWTInputBridge.sendMousePress(AWTInputEvent.BUTTON1_DOWN_MASK);
                } else {
                    if (action == MotionEvent.ACTION_MOVE) { // 2
                        mouseX = Math.max(0, Math.min(CallbackBridge.physicalWidth, mouseX + x - prevX));
                        mouseY = Math.max(0, Math.min(CallbackBridge.physicalHeight, mouseY + y - prevY));
                        placeMouseAt(mouseX, mouseY);
                        sendScaledMousePosition(mouseX, mouseY);
                    }
                }

                prevY = y;
                prevX = x;
                return true;
            }
        });

        mTextureView.setOnTouchListener((v, event) -> {
            float x = event.getX();
            float y = event.getY();
            if (mGestureDetector.onTouchEvent(event)) {
                sendScaledMousePosition(x + mTextureView.getX(), y);
                AWTInputBridge.sendMousePress(AWTInputEvent.BUTTON1_DOWN_MASK);
                return true;
            }

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_UP: // 1
                case MotionEvent.ACTION_CANCEL: // 3
                case MotionEvent.ACTION_POINTER_UP: // 6
                    break;
                case MotionEvent.ACTION_MOVE: // 2
                    sendScaledMousePosition(x + mTextureView.getX(), y);
                    break;
            }
            return true;
        });

        try {

            placeMouseAt(CallbackBridge.physicalWidth / 2f, CallbackBridge.physicalHeight / 2f);
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                finish();
                return;
            }
            final String javaArgs = extras.getString("javaArgs");
            final Uri resourceUri = (Uri) extras.getParcelable("modUri");
            if(extras.getBoolean("openLogOutput", false)) openLogOutput(null);
            if (javaArgs != null) {
                startModInstaller(null, javaArgs);
            }else if(resourceUri != null) {
                ProgressDialog barrierDialog = Tools.getWaitingDialog(this, R.string.multirt_progress_caching);
                PojavApplication.sExecutorService.execute(()->{
                    startModInstallerWithUri(resourceUri);
                    runOnUiThread(barrierDialog::dismiss);
                });
            }
        } catch (Throwable th) {
            Tools.showError(this, th, true);
        }


        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MainActivity.dialogForceClose(JavaGUILauncherActivity.this);
            }
        });
    }

    private void startModInstallerWithUri(Uri uri) {
        try {
            File cacheFile = new File(getCacheDir(), "mod-installer-temp");
            InputStream contentStream = getContentResolver().openInputStream(uri);
            if(contentStream == null) throw new IOException("Failed to open content stream");
            try (FileOutputStream fileOutputStream = new FileOutputStream(cacheFile)) {
                IOUtils.copy(contentStream, fileOutputStream);
            }
            contentStream.close();
            startModInstaller(cacheFile, null);
        }catch (IOException e) {
            Tools.showError(this, e, true);
        }
    }

    public Runtime selectRuntime(File modFile) {
        int javaVersion = getJavaVersion(modFile);
        if(javaVersion == -1) {
            finalErrorDialog(getString(R.string.execute_jar_failed_to_read_file));
            return null;
        }
        String nearestRuntime = MultiRTUtils.getNearestJreName(javaVersion);
        if(nearestRuntime == null) {
            finalErrorDialog(getString(R.string.multirt_nocompatiblert, javaVersion));
            return null;
        }
        Runtime selectedRuntime = MultiRTUtils.forceReread(nearestRuntime);
        int selectedJavaVersion = Math.max(javaVersion, selectedRuntime.javaVersion);
        // Don't allow versions higher than Java 17 because our caciocavallo implementation does not allow for it
        if(selectedJavaVersion > 17) {
            finalErrorDialog(getString(R.string.execute_jar_incompatible_runtime, selectedJavaVersion));
            return null;
        }
        return selectedRuntime;
    }

    private File findModPath(List<String> argList) {
        int argsSize = argList.size();
        for(int i = 0; i < argsSize; i++) {
            // Look for the -jar argument
            if(!argList.get(i).equals("-jar")) continue;
            int pathIndex = i+1;
            // Check if the supposed path is out of the argument bounds
            if(pathIndex >= argsSize) return null;
            // Use the path as a file
            return new File(argList.get(pathIndex));
        }
        return null;
    }

    private void startModInstaller(File modFile, String javaArgs) {
        new Thread(() -> {
            // Maybe replace with more advanced arg parsing logic later
            List<String> argList = javaArgs != null ? Arrays.asList(javaArgs.split(" ")) : null;
            File selectedMod = modFile;
            if(selectedMod == null && argList != null) {
                // If modFile is not specified directly, try to extract the -jar argument from the javaArgs
                selectedMod = findModPath(argList);
            }
            Runtime selectedRuntime;
            if(selectedMod == null) {
                // We were unable to find out the path to the mod. In that case, use the default runtime.
                selectedRuntime = MultiRTUtils.forceReread(LauncherPreferences.PREF_DEFAULT_RUNTIME);
            }else {
                // Autoselect it properly in the other case.
                selectedRuntime = selectRuntime(selectedMod);
                // If the selection failed, just return. The autoselect function has already shown the dialog.
                if(selectedRuntime == null) return;
            }
            launchJavaRuntime(selectedRuntime, modFile, argList);
        }, "JREMainThread").start();
    }

    private void finalErrorDialog(CharSequence msg) {
        runOnUiThread(()-> new AlertDialog.Builder(this)
                .setTitle(R.string.global_error)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, (d,w)->this.finish())
                .setCancelable(false)
                .show());
    }

    @Override
    public void onResume() {
        super.onResume();
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent e) {
        boolean isDown;
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: // 0
            case MotionEvent.ACTION_POINTER_DOWN: // 5
                isDown = true;
                break;
            case MotionEvent.ACTION_UP: // 1
            case MotionEvent.ACTION_CANCEL: // 3
            case MotionEvent.ACTION_POINTER_UP: // 6
                isDown = false;
                break;
            default:
                return false;
        }
        
        switch (v.getId()) {
            case R.id.installmod_mouse_pri:
                AWTInputBridge.sendMousePress(AWTInputEvent.BUTTON1_DOWN_MASK, isDown);
                break;
                
            case R.id.installmod_mouse_sec:
                AWTInputBridge.sendMousePress(AWTInputEvent.BUTTON3_DOWN_MASK, isDown);
                break;
        }
        if(isDown) switch(v.getId()) {
            case R.id.installmod_window_moveup:
                AWTInputBridge.nativeMoveWindow(0, -10);
                break;
            case R.id.installmod_window_movedown:
                AWTInputBridge.nativeMoveWindow(0, 10);
                break;
            case R.id.installmod_window_moveleft:
                AWTInputBridge.nativeMoveWindow(-10, 0);
                break;
            case R.id.installmod_window_moveright:
                AWTInputBridge.nativeMoveWindow(10, 0);
                break;
        }
        return true;
    }

    public void placeMouseAt(float x, float y) {
        mMousePointerImageView.setX(x);
        mMousePointerImageView.setY(y);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    void sendScaledMousePosition(float x, float y){
        // Clamp positions to the borders of the usable view, then scale them
        x = androidx.core.math.MathUtils.clamp(x, mTextureView.getX(), mTextureView.getX() + mTextureView.getWidth());
        y = androidx.core.math.MathUtils.clamp(y, mTextureView.getY(), mTextureView.getY() + mTextureView.getHeight());

        AWTInputBridge.sendMousePos(
                (int) MathUtils.map(x, mTextureView.getX(), mTextureView.getX() + mTextureView.getWidth(), 0, AWTCanvasView.AWT_CANVAS_WIDTH),
                (int) MathUtils.map(y, mTextureView.getY(), mTextureView.getY() + mTextureView.getHeight(), 0, AWTCanvasView.AWT_CANVAS_HEIGHT)
                );
    }

    public void forceClose(View v) {
        MainActivity.dialogForceClose(this);
    }

    public void openLogOutput(View v) {
        mLoggerView.setVisibility(View.VISIBLE);
    }

    public void toggleVirtualMouse(View v) {
        mIsVirtualMouseEnabled = !mIsVirtualMouseEnabled;
        mTouchPad.setVisibility(mIsVirtualMouseEnabled ? View.VISIBLE : View.GONE);
        Toast.makeText(this,
                mIsVirtualMouseEnabled ? R.string.control_mouseon : R.string.control_mouseoff,
                Toast.LENGTH_SHORT).show();
    }

    public void launchJavaRuntime(Runtime runtime, File modFile, List<String> javaArgs) {
        JREUtils.redirectAndPrintJRELog();
        try {
            List<String> javaArgList = new ArrayList<>();

            // Enable Caciocavallo
            Tools.getCacioJavaArgs(javaArgList,runtime.javaVersion == 8);
            if(javaArgs != null) {
                javaArgList.addAll(javaArgs);
            }
            if(modFile != null) {
                javaArgList.add("-jar");
                javaArgList.add(modFile.getAbsolutePath());
            }
            
            if (LauncherPreferences.PREF_JAVA_SANDBOX) {
                Collections.reverse(javaArgList);
                javaArgList.add("-Xbootclasspath/a:" + Tools.DIR_DATA + "/security/pro-grade.jar");
                javaArgList.add("-Djava.security.manager=net.sourceforge.prograde.sm.ProGradeJSM");
                javaArgList.add("-Djava.security.policy=" + Tools.DIR_DATA + "/security/java_sandbox.policy");
                Collections.reverse(javaArgList);
            }

            Logger.appendToLog("Info: Java arguments: " + Arrays.toString(javaArgList.toArray(new String[0])));

            JREUtils.launchJavaVM(this, runtime,null,javaArgList, LauncherPreferences.PREF_CUSTOM_JAVA_ARGS);
        } catch (Throwable th) {
            Tools.showError(this, th, true);
        }
    }

    public void toggleKeyboard(View view) {
        mTouchCharInput.switchKeyboardState();
    }
    public void performCopy(View view) {
        AWTInputBridge.sendKey(' ', AWTInputEvent.VK_CONTROL, 1);
        AWTInputBridge.sendKey(' ', AWTInputEvent.VK_C);
        AWTInputBridge.sendKey(' ', AWTInputEvent.VK_CONTROL, 0);
    }

    public void performPaste(View view) {
        AWTInputBridge.sendKey(' ', AWTInputEvent.VK_CONTROL, 1);
        AWTInputBridge.sendKey(' ', AWTInputEvent.VK_V);
        AWTInputBridge.sendKey(' ', AWTInputEvent.VK_CONTROL, 0);
    }

    public int getJavaVersion(File modFile) {
        try (ZipFile zipFile = new ZipFile(modFile)){
            ZipEntry manifest = zipFile.getEntry("META-INF/MANIFEST.MF");
            if(manifest == null) return -1;

            String manifestString = Tools.read(zipFile.getInputStream(manifest));
            String mainClass = Tools.extractUntilCharacter(manifestString, "Main-Class:", '\n');
            if(mainClass == null) return -1;

            mainClass = mainClass.trim().replace('.', '/') + ".class";
            ZipEntry mainClassFile = zipFile.getEntry(mainClass);
            if(mainClassFile == null) return -1;

            InputStream classStream = zipFile.getInputStream(mainClassFile);
            byte[] bytesWeNeed = new byte[8];
            int readCount = classStream.read(bytesWeNeed);
            classStream.close();
            if(readCount < 8) return -1;

            ByteBuffer byteBuffer = ByteBuffer.wrap(bytesWeNeed);
            if(byteBuffer.getInt() != 0xCAFEBABE) return -1;
            short minorVersion = byteBuffer.getShort();
            short majorVersion = byteBuffer.getShort();
            Log.i("JavaGUILauncher", majorVersion+","+minorVersion);
            return classVersionToJavaVersion(majorVersion);
        }catch (Exception e) {
            Log.e("JavaVersion", "Exception thrown", e);
            return -1;
        }
    }
    public static int classVersionToJavaVersion(int majorVersion) {
        if(majorVersion < 46) return 2; // there isn't even an arm64 port of jre 1.1 (or anything before 1.8 in fact)
        return majorVersion - 44;
    }
}
