package net.kdt.pojavlaunch;

import android.*;
import android.support.v7.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import android.widget.CompoundButton.*;
import com.kdt.filerapi.*;
import com.kdt.mojangauth.*;
import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.update.*;
import net.kdt.pojavlaunch.value.customcontrols.*;
import org.apache.commons.compress.archivers.tar.*;
import org.apache.commons.compress.compressors.xz.*;
import org.apache.commons.io.*;

import android.support.v7.app.AlertDialog;
import android.system.*;

public class PojavLoginActivity extends AppCompatActivity
// MineActivity
{
    private EditText edit2, edit3;
    private int REQUEST_STORAGE_REQUEST_CODE = 1;
    private ProgressBar prb;
    private CheckBox sRemember, sOffline;
    private LinearLayout loginLayout;
    private ImageView imageLogo;
    private TextView startupTextView;
    
    private boolean isPromptingGrant = false;
    // private boolean isPermGranted = false;
    
    private SharedPreferences firstLaunchPrefs;
    // private final String PREF_IS_DONOTSHOWAGAIN_WARN = "isWarnDoNotShowAgain";
    public static final String PREF_IS_INSTALLED_JAVARUNTIME = "isJavaRuntimeInstalled";
    
    private boolean isInitCalled = false;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState); // false);

        Tools.updateWindowSize(this);
        ControlButton.pixelOf2dp = (int) Tools.dpToPx(this, 2);
        ControlButton.pixelOf30dp = (int) Tools.dpToPx(this, 30);
        ControlButton.pixelOf50dp = (int) Tools.dpToPx(this, 50);
        ControlButton.pixelOf80dp = (int) Tools.dpToPx(this, 80);
        ControlButton[] specialButtons = ControlButton.getSpecialButtons();
        specialButtons[0].name = getString(R.string.control_keyboard);
        specialButtons[1].name = getString(R.string.control_toggle);
        specialButtons[2].name = getString(R.string.control_primary);
        specialButtons[3].name = getString(R.string.control_secondary);
        specialButtons[4].name = getString(R.string.control_mouse);
        
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                    }
                }
            });
            
        if (!isInitCalled) {
            init();
            isInitCalled = true;
        }
    }
    
    private void init() {
        firstLaunchPrefs = getSharedPreferences("pojav_extract", MODE_PRIVATE);
        new InitTask().execute();
    }

    private class InitTask extends AsyncTask<Void, String, Integer>{
        private AlertDialog startAle;
        private ProgressBar progress;

        private ProgressBar progressSpin;
        // private EditText progressLog;
        private AlertDialog progDlg;

        @Override
        protected void onPreExecute()
        {
            LinearLayout startScr = new LinearLayout(PojavLoginActivity.this);
            LayoutInflater.from(PojavLoginActivity.this).inflate(R.layout.start_screen, startScr);

            FontChanger.changeFonts(startScr);

            progress = (ProgressBar) startScr.findViewById(R.id.startscreenProgress);
            startupTextView = (TextView) startScr.findViewById(R.id.startscreen_text);
            //startScr.addView(progress);

            AlertDialog.Builder startDlg = new AlertDialog.Builder(PojavLoginActivity.this, R.style.AppTheme);
            startDlg.setView(startScr);
            startDlg.setCancelable(false);

            startAle = startDlg.create();
            startAle.show();
            startAle.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
        
        private int revokeCount = -1;
        
        @Override
        protected Integer doInBackground(Void[] p1)
        {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}

            publishProgress("visible");

            while (Build.VERSION.SDK_INT >= 23 && !isStorageAllowed()){
                try {
                    revokeCount++;
                    if (revokeCount >= 3) {
                        Toast.makeText(PojavLoginActivity.this, R.string.toast_permission_denied, Toast.LENGTH_LONG).show();
                        finish();
                    }
                    isPromptingGrant = true;
                    requestStoragePermission();
                    while (isPromptingGrant) {
                        Thread.sleep(200);
                    }
                    
                } catch (InterruptedException e) {}
            }

            initMain();

            return 0;
        }

        @Override
        protected void onProgressUpdate(String... obj)
        {
            if (obj[0].equals("visible")) {
                progress.setVisibility(View.VISIBLE);
            } /* else if (obj.length == 2 && obj[1] != null) {
                progressLog.append(obj[1]);
            } */
        }

        @Override
        protected void onPostExecute(Integer obj) {
            startAle.dismiss();
            if (progressSpin != null) progressSpin.setVisibility(View.GONE);
            if (obj == 0) {
                if (progDlg != null) progDlg.dismiss();
                uiInit();
            } /* else if (progressLog != null) {
                progressLog.setText(getResources().getString(R.string.error_checklog, "\n\n" + progressLog.getText()));
            } */
        }
/*
        private void appendlnToLog(String txt) {
            publishProgress("", txt + "\n");
        }
        
         private void execCmd(String cmd) throws Exception {
         appendlnToLog("> " + cmd);
         ShellProcessOperation mainProcess = new ShellProcessOperation(new ShellProcessOperation.OnPrintListener(){

         @Override
         public void onPrintLine(String text)
         {
         publishProgress(text);
         }
         }, cmd);
         mainProcess.initInputStream(MCLoginActivity.this);
         String msgExit = cmd.split(" ")[0] + " has exited with code " + mainProcess.waitFor();
         if (mainProcess.exitCode() != 0) {
         throw new Error("(ERROR) " + msgExit);
         } else {
         appendlnToLog("(SUCCESS) " + msgExit);
         }
         }
         */
    }
    
    private void uiInit() {
        setContentView(R.layout.launcher_login_v2);

        loginLayout = findViewById(R.id.login_layout_linear);
        imageLogo = findViewById(R.id.login_image_logo);
        loginLayout.postDelayed(new Runnable(){
                @Override
                public void run(){
                    imageLogo.setTranslationY(loginLayout.getY() - (imageLogo.getHeight() / 2f));
                }
            }, 100);
            
        edit2 = (EditText) findViewById(R.id.login_edit_email);
        edit3 = (EditText) findViewById(R.id.login_edit_password);
        if(prb == null) prb = (ProgressBar) findViewById(R.id.launcherAccProgress);
        
        sRemember = findViewById(R.id.login_switch_remember);
        sOffline  = findViewById(R.id.login_switch_offline);
        sOffline.setOnCheckedChangeListener(new OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(CompoundButton p1, boolean p2) {
                    // May delete later
                    edit3.setEnabled(!p2);
                }
            });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        Tools.updateWindowSize(this);
        
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
        
        if (loginLayout != null && imageLogo != null) {
            imageLogo.setTranslationY(loginLayout.getY() - (imageLogo.getHeight() / 2f));
        }
        
        // Clear current profile
        PojavProfile.setCurrentProfile(this, null);
    }

    private boolean isJavaRuntimeInstalled() {
        return firstLaunchPrefs.getBoolean(PREF_IS_INSTALLED_JAVARUNTIME, false);
    }
    
    private boolean setPref(String prefName, boolean value) {
        return firstLaunchPrefs.edit().putBoolean(prefName, value).commit();
    }
    
    private void initMain()
    {
        mkdirs(Tools.worksDir);
        mkdirs(Tools.versnDir);
        mkdirs(Tools.libraries);
        mkdirs(Tools.mpProfiles);
        
        mkdirs(Tools.MAIN_PATH);
        mkdirs(Tools.MAIN_PATH + "/lwjgl3");
        mkdirs(Tools.MAIN_PATH + "/mods");
        
        mkdirs(Tools.CTRLMAP_PATH);
        
        try {
            new CustomControls(this).save(Tools.CTRLDEF_FILE);
            
            Tools.copyAssetFile(this, "options.txt", Tools.MAIN_PATH, false);
            
            // Extract launcher_profiles.json
            // TODO: Remove after implement.
            Tools.copyAssetFile(this, "launcher_profiles.json", Tools.MAIN_PATH, false);
            
            Tools.copyAssetFile(this, "ClassWrapper.jar", Tools.MAIN_PATH + "/lwjgl3", false);
            
            // Yep, the codebase from v1.0.3:
            //FileAccess.copyAssetToFolderIfNonExist(this, "1.0.jar", Tools.versnDir + "/1.0");
            //FileAccess.copyAssetToFolderIfNonExist(this, "1.7.3.jar", Tools.versnDir + "/1.7.3");
            //FileAccess.copyAssetToFolderIfNonExist(this, "1.7.10.jar", Tools.versnDir + "/1.7.10");
            
            UpdateDataChanger.changeDataAuto("2.4", "2.4.2");
            
            if (!isJavaRuntimeInstalled()) {
                File jreTarFile = selectJreTarFile();
                uncompressTarXZ(jreTarFile, new File(Tools.homeJreDir));
                
                setPref(PREF_IS_INSTALLED_JAVARUNTIME, true);
            }
            
            for (String arch : Tools.currentArch.split("/")) {
                File f = new File(Tools.homeJreDir, "lib/" + arch);
                if (f.exists() && f.isDirectory()) {
                    Tools.homeJreLib = "lib/" + arch;
                    break;
                }
            }

            File ftIn = new File(Tools.homeJreDir, Tools.homeJreLib + "/libfreetype.so.6");
            File ftOut = new File(Tools.homeJreDir, Tools.homeJreLib + "/libfreetype.so");
            if (ftIn.exists() && (!ftOut.exists() || ftIn.length() != ftOut.length())) {
                ftIn.renameTo(ftOut);
            }
            
            // Refresh libraries
            copyDummyNativeLib("libawt_xawt.so");
            copyDummyNativeLib("libfontconfig.so");
        }
        catch(Throwable e){
            Tools.showError(this, e);
        }
    }
    
    private void copyDummyNativeLib(String name) throws Throwable {
        File fileLib = new File(Tools.homeJreDir, Tools.homeJreLib + "/" + name);
        fileLib.delete();
        IOUtils.copy(
            new FileInputStream(new File(getApplicationInfo().nativeLibraryDir, name)),
            new FileOutputStream(fileLib)
        );
    }
    
    private File selectJreTarFile() throws InterruptedException {
        final StringBuilder selectedFile = new StringBuilder();
        
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(PojavLoginActivity.this);
                builder.setTitle(getString(R.string.alerttitle_install_jre, Tools.currentArch));
                builder.setCancelable(false);

                final AlertDialog dialog = builder.create();
                FileListView flv = new FileListView(dialog);
                flv.setFileSelectedListener(new FileSelectedListener(){

                        @Override
                        public void onFileSelected(File file, String path) {
                            if (file.getName().endsWith(".tar.xz")) {
                                selectedFile.append(path);
                                dialog.dismiss();
                            }
                        }
                    });
                dialog.setView(flv);
                dialog.show();
            }
        });
        
        while (selectedFile.length() == 0) {
            Thread.sleep(500);
        }
        
        return new File(selectedFile.toString());
    }

    private void uncompressTarXZ(final File tarFile, final File dest) throws IOException {

        dest.mkdir();
        TarArchiveInputStream tarIn = null;

        tarIn = new TarArchiveInputStream(
            new XZCompressorInputStream(
                new BufferedInputStream(
                    new FileInputStream(tarFile)
                )
            )
        );

        TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
        // tarIn is a TarArchiveInputStream
        while (tarEntry != null) {
            /*
             * Unpacking very small files in short time cause
             * application to ANR or out of memory, so delay
             * a little if size is below than 20kb (20480 bytes)
             */
            if (tarEntry.getSize() <= 20480) {
                try {
                    // 40 small files per second
                    Thread.sleep(25);
                } catch (InterruptedException e) {}
            }
            final String tarEntryName = tarEntry.getName();
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    startupTextView.setText(getString(R.string.global_unpacking, tarEntryName));
                }
            });
            // publishProgress(null, "Unpacking " + tarEntry.getName());
            File destPath = new File(dest, tarEntry.getName()); 
            if (tarEntry.isSymbolicLink()) {
                destPath.getParentFile().mkdirs();
                try {
                    // android.system.Os
                    // Libcore one support all Android versions
                    Os.symlink(tarEntry.getName(), tarEntry.getLinkName());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                // unpackShell.writeToProcess("ln -s " + tarEntry.getName() + " " + tarEntry.getLinkName());
            } else if (tarEntry.isDirectory()) {
                destPath.mkdirs();
                destPath.setExecutable(true);
            } else if (!destPath.exists() || destPath.length() != tarEntry.getSize()) {
                destPath.getParentFile().mkdirs();
                destPath.createNewFile();
                // destPath.setExecutable(true);
                
                IOUtils.copy(tarIn, new FileOutputStream(destPath));

/*
                byte[] btoRead = new byte[2048];
                BufferedOutputStream bout = 
                    new BufferedOutputStream(new FileOutputStream(destPath));
                int len = 0;

                while((len = tarIn.read(btoRead)) != -1) {
                    bout.write(btoRead,0,len);
                }

                bout.close();
                btoRead = null;
*/
            }
            tarEntry = tarIn.getNextTarEntry();
        }
        tarIn.close();
    }
    
    private boolean mkdirs(String path)
    {
        File file = new File(path);
        if(file.getParentFile().exists())
             return file.mkdir();
        else return file.mkdirs();
    }
    
    /*
    public void loginUsername(View view)
    {
        LinearLayout mainLaun = new LinearLayout(this);
        LayoutInflater.from(this).inflate(R.layout.launcher_user, mainLaun, true);
        replaceFonts(mainLaun);
        
        //edit1 = mainLaun.findViewById(R.id.launcherAccUsername);
        
        new AlertDialog.Builder(this)
            .setTitle("Register with username")
            .setView(mainLaun)
            .show();
        
    }
    */
    
    // developer methods
    // end dev methods
    public void loginSavedAcc(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (Tools.enableDevFeatures) {
            builder.setNegativeButton("Toggle UI v2", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {
                        int ver = PojavV2ActivityManager.getLauncherRemakeInt(PojavLoginActivity.this) == 0 ? 1 : 0;
                        PojavV2ActivityManager.setLauncherRemakeVer(PojavLoginActivity.this, ver);
                        Toast.makeText(PojavLoginActivity.this, "Changed to use v" + (ver + 1), Toast.LENGTH_SHORT).show();
                    }
                });
        }
        
        builder.setPositiveButton(android.R.string.cancel, null);
        builder.setTitle(this.getString(R.string.login_select_account));
        final AlertDialog dialog = builder.create();

        /*
        LinearLayout.LayoutParams lpHint, lpFlv;
        
        lpHint = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpFlv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpHint.weight = 1;
        lpFlv.weight = 1;
        */
        dialog.setTitle(this.getString(R.string.login_select_account));
        System.out.println("Setting title...");
        LinearLayout dialay = new LinearLayout(this);
        dialay.setOrientation(LinearLayout.VERTICAL);
        TextView fhint = new TextView(this);
        fhint.setText(R.string.hint_select_account);
        // fhint.setLayoutParams(lpHint);
        
        final FileListView flv = new FileListView(dialog);
        // flv.setLayoutParams(lpFlv);
        
        flv.lockPathAt(Tools.mpProfiles);
        flv.setFileSelectedListener(new FileSelectedListener(){

                @Override
                public void onFileLongClick(final File file, String path)
                {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(PojavLoginActivity.this);
                    builder2.setTitle(file.getName());
                    builder2.setMessage(R.string.warning_remove_account);
                    builder2.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                file.delete();
                                flv.refreshPath();
                            }
                        });
                    builder2.setNegativeButton(android.R.string.cancel, null);
                    builder2.show();
                }
                @Override
                public void onFileSelected(File file, final String path) {
                    try {
                        if (MCProfile.load(path).isMojangAccount()){
                            MCProfile.updateTokens(PojavLoginActivity.this, path, new RefreshListener(){

                                    @Override
                                    public void onFailed(Throwable e)
                                    {
                                        Tools.showError(PojavLoginActivity.this, e);
                                    }

                                    @Override
                                    public void onSuccess()
                                    {
                                        MCProfile.launch(PojavLoginActivity.this, path);
                                    }
                                });
                        } else {
                            MCProfile.launch(PojavLoginActivity.this, path);
                        }
                        
                        dialog.hide();
                        //Tools.throwError(MCLoginActivity.this, new Exception(builder.getAccessToken() + "," + builder.getUUID() + "," + builder.getNickname() + "," + builder.getEmail() + "," + builder.getPassword()));
                    }
                    catch (Exception e)
                    {
                        Tools.showError(PojavLoginActivity.this, e);
                    }
                }
            });
        dialay.addView(fhint);
        dialay.addView(flv);
        
        dialog.setView(dialay);
        dialog.setTitle(this.getString(R.string.login_select_account));
        dialog.show();
    }
    
    private MCProfile.Builder loginOffline() {
        new File(Tools.mpProfiles).mkdir();
        
        String text = edit2.getText().toString();
        if(text.isEmpty()){
            edit2.setError(getResources().getString(R.string.global_error_field_empty));
        } else if(text.length() <= 2){
            edit2.setError(getResources().getString(R.string.login_error_short_username));
        } else if(new File(Tools.mpProfiles + "/" + text).exists()){
            edit2.setError(getResources().getString(R.string.login_error_exist_username));
        } else{
            MCProfile.Builder builder = new MCProfile.Builder();
            builder.setIsMojangAccount(false);
            builder.setUsername(text);
            
            return builder;
        }
        return null;
    }
    
    private MCProfile.Builder mProfile = null;
    public void loginMC(final View v)
    {
        /*skip it

        String proFilePath = MCProfile.build(builder);
        MCProfile.launchWithProfile(this, proFilePath);
        end skip*/
        
        if (sOffline.isChecked()) {
            mProfile = loginOffline();
            playProfile();
        } else {
            new LoginTask().setLoginListener(new LoginListener(){

                    @Override
                    public void onBeforeLogin()
                    {
                        // TODO: Implement this method
                        v.setEnabled(false);
                        prb.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoginDone(String[] result)
                    {
                        // TODO: Implement this method
                        if(result[0].equals("ERROR")){
                            Tools.dialogOnUiThread(PojavLoginActivity.this, getResources().getString(R.string.global_error), strArrToString(result));
                        } else{
                            MCProfile.Builder builder = new MCProfile.Builder();
                            builder.setAccessToken(result[1]);
                            builder.setClientID(result[2]);
                            builder.setProfileID(result[3]);
                            builder.setUsername(result[4]);
                            builder.setVersion("1.7.10");

                            mProfile = builder;
                        }
                        v.setEnabled(true);
                        prb.setVisibility(View.GONE);
                        
                        playProfile();
                    }
                }).execute(edit2.getText().toString(), edit3.getText().toString());
        }
    }
    
    private void playProfile() {
        if (mProfile != null) {
            String profilePath = null;
            if (sRemember.isChecked()) {
                profilePath = MCProfile.build(mProfile);
            }
            
            MCProfile.launch(PojavLoginActivity.this, profilePath == null ? mProfile : profilePath);
        }
    }
    
    public static String strArrToString(String[] strArr)
    {
        String[] strArrEdit = strArr;
        strArrEdit[0] = "";
        
        String str = Arrays.toString(strArrEdit);
        str = str.substring(1, str.length() - 1).replace(",", "\n");
        
        return str;
    }
    //We are calling this method to check the permission status
    private boolean isStorageAllowed() {
        //Getting the permission status
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        //If permission is granted returning true
        return result1 == PackageManager.PERMISSION_GRANTED &&
            result2 == PackageManager.PERMISSION_GRANTED;
    }

    //Requesting permission
    private void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_REQUEST_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == REQUEST_STORAGE_REQUEST_CODE){
            isPromptingGrant = false;
            // isPermGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED;
        }
    }
}
