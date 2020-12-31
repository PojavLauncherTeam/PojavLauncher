package net.kdt.pojavlaunch;

import android.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.net.*;
import android.os.*;

import androidx.annotation.NonNull;
import androidx.core.app.*;
import androidx.core.content.*;
import androidx.appcompat.app.*;
import android.system.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.CompoundButton.*;
import com.kdt.pickafile.*;
import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.authenticator.microsoft.*;
import net.kdt.pojavlaunch.authenticator.mojang.*;
import net.kdt.pojavlaunch.customcontrols.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.utils.*;
import org.apache.commons.compress.archivers.tar.*;
import org.apache.commons.compress.compressors.xz.*;
import org.apache.commons.io.*;

import org.apache.commons.io.FileUtils;
import net.kdt.pojavlaunch.value.*;
import com.google.gson.*;

public class PojavLoginActivity extends BaseActivity
// MineActivity
{
    private Object mLockStoragePerm = new Object(),
        mLockSelectJRE = new Object();
    
    private EditText edit2, edit3;
    private int REQUEST_STORAGE_REQUEST_CODE = 1;
    private ProgressBar prb;
    private CheckBox sRemember, sOffline;
    private LinearLayout loginLayout;
    private Spinner spinnerChgLang;
    private ImageView imageLogo;
    private TextView startupTextView;
    
    private SharedPreferences firstLaunchPrefs;
    
    private static boolean isSkipInit = false;
    
    // private final String PREF_IS_DONOTSHOWAGAIN_WARN = "isWarnDoNotShowAgain";
    public static final String PREF_IS_INSTALLED_JAVARUNTIME = "isJavaRuntimeInstalled";
    public static final String PREF_JAVARUNTIME_VER = "javaRuntimeVersion";
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState); // false);
        
        Tools.updateWindowSize(this);
        
        ControlData.pixelOf2dp = (int) Tools.dpToPx(2);
        ControlData.pixelOf30dp = (int) Tools.dpToPx(30);
        ControlData.pixelOf50dp = (int) Tools.dpToPx(50);
        ControlData.pixelOf80dp = (int) Tools.dpToPx(80);
        ControlData[] specialButtons = ControlData.getSpecialButtons();
        specialButtons[0].name = getString(R.string.control_keyboard);
        specialButtons[1].name = getString(R.string.control_toggle);
        specialButtons[2].name = getString(R.string.control_primary);
        specialButtons[3].name = getString(R.string.control_secondary);
        specialButtons[4].name = getString(R.string.control_mouse);
        
        firstLaunchPrefs = getSharedPreferences("pojav_extract", MODE_PRIVATE);
        new InitTask().execute(isSkipInit);
    }

    private class InitTask extends AsyncTask<Boolean, String, Integer>{
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
        protected Integer doInBackground(Boolean[] params) {
            // If trigger a quick restart
            if (params[0] == true) {
                return 0;
            }
            
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
                        return 0;
                    }
                    
                    requestStoragePermission();
                    
                    synchronized (mLockStoragePerm) {
                        mLockStoragePerm.wait();
                    }
                } catch (InterruptedException e) {}
            }

            try {
                initMain();
            } catch (Throwable th) {
                Tools.showError(PojavLoginActivity.this, th, true);
                return 1;
            }

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
    }
    
    private void uiInit() {
        setContentView(R.layout.launcher_login_v2);

        loginLayout = findViewById(R.id.login_layout_linear);
        spinnerChgLang = findViewById(R.id.login_spinner_language);
        imageLogo = findViewById(R.id.login_image_logo);
        loginLayout.postDelayed(new Runnable(){
                @Override
                public void run(){
                    imageLogo.setTranslationY(loginLayout.getY() - (imageLogo.getHeight() / 2f));
                }
            }, 100);

        String defaultLang = LocaleUtils.DEFAULT_LOCALE.getDisplayName();
        SpannableString defaultLangChar = new SpannableString(defaultLang);
        defaultLangChar.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, defaultLang.length(), 0);
        
        final ArrayAdapter<DisplayableLocale> langAdapter = new ArrayAdapter<DisplayableLocale>(this, android.R.layout.simple_spinner_item);
        langAdapter.add(new DisplayableLocale(LocaleUtils.DEFAULT_LOCALE, defaultLangChar));
        langAdapter.add(new DisplayableLocale(Locale.ENGLISH));
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("language_list.txt")));
            String line;
            while ((line = reader.readLine()) != null) {
                File currFile = new File("/" + line);
                // System.out.println(currFile.getAbsolutePath());
                if (currFile.getAbsolutePath().contains("/values-") || currFile.getName().startsWith("values-")) {
                    // TODO use regex(?)
                    langAdapter.add(new DisplayableLocale(currFile.getName().replace("values-", "").replace("-r", "-")));
                }
            }
        } catch (IOException e) {
            Tools.showError(this, e);
        }
        
        langAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        
        int selectedLang = 0;
        for (int i = 0; i < langAdapter.getCount(); i++) {
            if (Locale.getDefault().getDisplayLanguage().equals(langAdapter.getItem(i).mLocale.getDisplayLanguage())) {
                selectedLang = i;
                break;
            }
        }
        
        spinnerChgLang.setAdapter(langAdapter);
        spinnerChgLang.setSelection(selectedLang);
        spinnerChgLang.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            private boolean isInitCalled;
            @Override
            public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
                if (!isInitCalled) {
                    isInitCalled = true;
                    return;
                }
                
                Locale locale;
                if (position == 0) {
                    locale = LocaleUtils.DEFAULT_LOCALE;
                } else if (position == 1) {
                    locale = Locale.ENGLISH;
                } else {
                    locale = langAdapter.getItem(position).mLocale;
                }
                
                LauncherPreferences.PREF_LANGUAGE = locale.getLanguage();
                LauncherPreferences.DEFAULT_PREF.edit().putString("language", LauncherPreferences.PREF_LANGUAGE).commit();
                
                // Restart to apply language change
                finish();
                startActivity(getIntent());
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {}
        });
            
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
            
        isSkipInit = true;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        Tools.updateWindowSize(this);
        
        if (loginLayout != null && imageLogo != null) {
            imageLogo.setTranslationY(loginLayout.getY() - (imageLogo.getHeight() / 2f));
        }
        
        // Clear current profile
        PojavProfile.setCurrentProfile(this, null);
    }

    private boolean isJavaRuntimeInstalled(AssetManager am) {
        boolean prefValue = firstLaunchPrefs.getBoolean(PREF_IS_INSTALLED_JAVARUNTIME, false);
        try {
            return prefValue && (
                am.open("components/jre/bin-" + Tools.CURRENT_ARCHITECTURE.split("/")[0] + ".tar.xz") == null ||
                Tools.read(new FileInputStream(Tools.DIR_HOME_JRE+"/version")).equals(Tools.read(am.open("components/jre/version"))));
        } catch(IOException e) {
            Log.e("JVMCtl","failed to read file",e);
            return prefValue;
        }
    }
   
    private void initMain() throws Throwable {
        mkdirs(Tools.DIR_ACCOUNT_NEW);
        PojavMigrator.migrateAccountData(this);
        
        mkdirs(Tools.DIR_GAME_HOME);
        if (!PojavMigrator.migrateGameDir()) {
            mkdirs(Tools.DIR_GAME_NEW);
            mkdirs(Tools.DIR_GAME_NEW + "/config");
            mkdirs(Tools.DIR_GAME_NEW + "/lwjgl3");
            mkdirs(Tools.DIR_GAME_NEW + "/mods");
            mkdirs(Tools.DIR_HOME_VERSION);
            mkdirs(Tools.DIR_HOME_LIBRARY);
        }
        
        File forgeSplashFile = new File(Tools.DIR_GAME_NEW, "config/splash.properties");
        String forgeSplashContent = "enabled=true";
        try {
            if (forgeSplashFile.exists()) {
                forgeSplashContent = Tools.read(forgeSplashFile.getAbsolutePath());
            }
            if (forgeSplashContent.contains("enabled=true")) {
                Tools.write(forgeSplashFile.getAbsolutePath(),
                    forgeSplashContent.replace("enabled=true", "enabled=false"));
            }
        } catch (IOException e) {
            Log.w(Tools.APP_NAME, "Could not disable Forge 1.12.2 and below splash screen!", e);
        }
        
        mkdirs(Tools.CTRLMAP_PATH);
        
        try {
            new CustomControls(this).save(Tools.CTRLDEF_FILE);

            Tools.copyAssetFile(this, "components/ForgeInstallerHeadless/forge-installer-headless-1.0.1.jar",
                Tools.DIR_GAME_NEW + "/config", "forge-installer-headless.jar", true);
            Tools.copyAssetFile(this, "components/OptiInst.jar",
                    Tools.DIR_GAME_NEW + "/config", "OptiInst.jar", true);
            Tools.copyAssetFile(this, "components/security/pro-grade.jar", Tools.DIR_DATA, true);
            Tools.copyAssetFile(this, "components/security/java_sandbox.policy", Tools.DIR_DATA, true);
            Tools.copyAssetFile(this, "options.txt", Tools.DIR_GAME_NEW, false);
            // TODO: Remove after implement.
            Tools.copyAssetFile(this, "launcher_profiles.json", Tools.DIR_GAME_NEW, false);

            AssetManager am = this.getAssets();
            
            InputStream is = am.open("components/lwjgl3/version");
            if(!new File(Tools.DIR_GAME_NEW + "/lwjgl3/version").exists()) {
                Log.i("LWJGL3Prep","Pack was installed manually, or does not exist, unpacking new...");
                String[] lwjglFileList = am.list("components/lwjgl3");
                // FileOutputStream fos;
                // InputStream iis;
                for(String s : lwjglFileList) {
                    Tools.copyAssetFile(this, "components/lwjgl3/" + s, Tools.DIR_GAME_NEW + "/lwjgl3", true);
                    /*
                    iis = am.open("components/lwjgl3/"+s);
                    fos = new FileOutputStream(new File(Tools.DIR_GAME_NEW+"/lwjgl3/"+s));
                    IOUtils.copy(iis,fos);
                    fos.close();
                    iis.close();
                    */
                }
            } else {
                FileInputStream fis = new FileInputStream(new File(Tools.DIR_GAME_NEW + "/lwjgl3/version"));
                String release1 = Tools.read(is);
                String release2 = Tools.read(fis);
                if (!release1.equals(release2)) {
                    String[] lwjglFileList = am.list("components/lwjgl3");
                    for (String s : lwjglFileList) {
                        Tools.copyAssetFile(this, "components/lwjgl3/" + s, Tools.DIR_GAME_NEW + "/lwjgl3", true);
                    }
                } else {
                    Log.i("LWJGL3Prep","Pack is up-to-date with the launcher, continuing...");
                }
            }
            if (!isJavaRuntimeInstalled(am)) {
                if(!installRuntimeAutomatically(am)) {
                    File jreTarFile = selectJreTarFile();
                    uncompressTarXZ(jreTarFile, new File(Tools.DIR_HOME_JRE));
                } else {
                    Tools.copyAssetFile(this, "components/jre/version", Tools.DIR_HOME_JRE + "/","version", true);
                }
                firstLaunchPrefs.edit().putBoolean(PREF_IS_INSTALLED_JAVARUNTIME, true).commit();
            }
            
            JREUtils.relocateLibPath(this);

            File ftIn = new File(Tools.DIR_HOME_JRE, Tools.DIRNAME_HOME_JRE + "/libfreetype.so.6");
            File ftOut = new File(Tools.DIR_HOME_JRE, Tools.DIRNAME_HOME_JRE + "/libfreetype.so");
            if (ftIn.exists() && (!ftOut.exists() || ftIn.length() != ftOut.length())) {
                ftIn.renameTo(ftOut);
            }
            
            // Refresh libraries
            copyDummyNativeLib("libawt_xawt.so");
            // copyDummyNativeLib("libfontconfig.so");
        }
        catch(Throwable e){
            Tools.showError(this, e);
        }
    }
    
    private boolean installRuntimeAutomatically(AssetManager am) {
        try {
            am.open("components/jre/version");
        } catch (IOException e) {
            Log.e("JREAuto", "JRE was not included on this APK.", e);
            return false;
        }
        
        File rtUniversal = new File(Tools.DIR_HOME_JRE+"/universal.tar.xz");
        File rtPlatformDependent = new File(Tools.DIR_HOME_JRE+"/cust-bin.tar.xz");
        if(!new File(Tools.DIR_HOME_JRE).exists()) new File(Tools.DIR_HOME_JRE).mkdirs(); else {
            //SANITY: remove the existing files
            for (File f : new File(Tools.DIR_HOME_JRE).listFiles()) {
                if (f.isDirectory()){
                    try {
                        FileUtils.deleteDirectory(f);
                    } catch(IOException e1) {
                        Log.e("JREAuto","da fuq is wrong wit ur device? n2",e1);
                    }
                } else{
                    f.delete();
                }
            }
        }
        InputStream is;
        FileOutputStream os;
        try {
            is = am.open("components/jre/universal.tar.xz");
            os = new FileOutputStream(rtUniversal);
            IOUtils.copy(is,os);
            is.close();
            os.close();
            uncompressTarXZ(rtUniversal, new File(Tools.DIR_HOME_JRE));
        } catch (IOException e){
            Log.e("JREAuto","Failed to unpack universal. Custom embedded-less build?",e);
            return false;
        }
        try {
            is = am.open("components/jre/bin-" + Tools.CURRENT_ARCHITECTURE.split("/")[0] + ".tar.xz");
            os = new FileOutputStream(rtPlatformDependent);
            IOUtils.copy(is, os);
            is.close();
            os.close();
            uncompressTarXZ(rtPlatformDependent, new File(Tools.DIR_HOME_JRE));
        } catch (IOException e) {
            // Something's very wrong, or user's using an unsupported arch (MIPS phone? ARMv6 phone?),
            // in both cases, redirecting to manual install, and removing the universal stuff
            for (File f : new File(Tools.DIR_HOME_JRE).listFiles()) {
                if (f.isDirectory()){
                    try {
                        FileUtils.deleteDirectory(f);
                    } catch(IOException e1) {
                        Log.e("JREAuto","da fuq is wrong wit ur device?",e1);
                    }
                } else{
                    f.delete();
                }
            }
            return false;
        }
        return true;
    }
    private void copyDummyNativeLib(String name) throws Throwable {
        File fileLib = new File(Tools.DIR_HOME_JRE, Tools.DIRNAME_HOME_JRE + "/" + name);
        fileLib.delete();
        FileInputStream is = new FileInputStream(new File(getApplicationInfo().nativeLibraryDir, name));
        FileOutputStream os = new FileOutputStream(fileLib);
        IOUtils.copy(is, os);
        is.close();
        os.close();
    }
    
    private File selectJreTarFile() throws InterruptedException {
        final StringBuilder selectedFile = new StringBuilder();
        
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(PojavLoginActivity.this);
                builder.setTitle(getString(R.string.alerttitle_install_jre, Tools.CURRENT_ARCHITECTURE));
                builder.setCancelable(false);

                final AlertDialog dialog = builder.create();
                FileListView flv = new FileListView(dialog, "tar.xz");
                flv.setFileSelectedListener(new FileSelectedListener(){

                        @Override
                        public void onFileSelected(File file, String path) {
                            selectedFile.append(path);
                            dialog.dismiss();

                            synchronized (mLockSelectJRE) {
                                mLockSelectJRE.notifyAll();
                            }

                        }
                    });
                dialog.setView(flv);
                dialog.show();
            }
        });
        
        synchronized (mLockSelectJRE) {
            mLockSelectJRE.wait();
        }
        
        return new File(selectedFile.toString());
    }

    private void uncompressTarXZ(final File tarFile, final File dest) throws IOException {

        dest.mkdirs();
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
                
                FileOutputStream os = new FileOutputStream(destPath);
                IOUtils.copy(tarIn, os);
                os.close();

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
        // check necessary???
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
    
    public void loginMicrosoft(View view) {
        CustomTabs.openTab(this,
            "https://login.live.com/oauth20_authorize.srf" + 
            "?client_id=00000000402b5328" +
            "&response_type=code" +
            "&scope=service%3A%3Auser.auth.xboxlive.com%3A%3AMBI_SSL" +
            "&redirect_url=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf");
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        
        Uri data = intent.getData();
        Log.i("MicroAuth", data.toString());
        if (data != null && data.getScheme().equals("ms-xal-00000000402b5328") && data.getHost().equals("auth")) {
            String error = data.getQueryParameter("error");
            String error_description = data.getQueryParameter("error_description");
            if (error != null) {
                // "The user has denied access to the scope requested by the client application": user pressed Cancel button, skip it
                if (!error_description.startsWith("The user has denied access to the scope requested by the client application")) {
                    Toast.makeText(this, "Error: " + error + ": " + error_description, Toast.LENGTH_LONG).show();
                }
            } else {
                String code = data.getQueryParameter("code");
                new MicrosoftAuthTask(this, new RefreshListener(){
                        @Override
                        public void onFailed(Throwable e) {
                            Tools.showError(PojavLoginActivity.this, e);
                        }

                        @Override
                        public void onSuccess(MinecraftAccount b) {
                            mProfile = b;
                            playProfile(false);
                        }
                    }).execute("false", code);
                // Toast.makeText(this, "Logged in to Microsoft account, but NYI", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private View getViewFromList(int pos, ListView listView) {
        final int firstItemPos = listView.getFirstVisiblePosition();
        final int lastItemPos = firstItemPos + listView.getChildCount() - 1;

        if (pos < firstItemPos || pos > lastItemPos ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstItemPos;
            return listView.getChildAt(childIndex);
        }
    }
    
    public void loginSavedAcc(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (Tools.ENABLE_DEV_FEATURES) {
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
        
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        for (String s : new File(Tools.DIR_ACCOUNT_NEW).list()) {
            listAdapter.add(s.substring(0, s.length() - 5));
        }
        
        builder.setPositiveButton(android.R.string.cancel, null);
        builder.setTitle(this.getString(R.string.login_select_account));
        builder.setSingleChoiceItems(listAdapter, 0, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(final DialogInterface di, final int selectedIndex) {
                    PopupMenu popup = new PopupMenu(PojavLoginActivity.this, getViewFromList(selectedIndex, ((AlertDialog) di).getListView()));
                    popup.inflate(R.menu.menu_options_account);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                final String selectedAccName = listAdapter.getItem(selectedIndex);
                                switch (item.getItemId()) {
                                    case R.id.menu_account_select:
                                        try {
                                            RefreshListener authListener = new RefreshListener(){
                                                @Override
                                                public void onFailed(Throwable e) {
                                                    Tools.showError(PojavLoginActivity.this, e);
                                                }

                                                @Override
                                                public void onSuccess(MinecraftAccount out) {
                                                    di.dismiss();
                                                    mProfile = out;
                                                    playProfile(true);
                                                }
                                            };
                                            
                                            MinecraftAccount acc = MinecraftAccount.load(selectedAccName);
                                            if (acc.isMicrosoft){
                                                new MicrosoftAuthTask(PojavLoginActivity.this, authListener)
                                                    .execute("true", acc.msaRefreshToken);
                                            } else if (acc.accessToken.length() >= 5) {
                                                PojavProfile.updateTokens(PojavLoginActivity.this, selectedAccName, authListener);
                                            } else {
                                                di.dismiss();
                                                PojavProfile.launch(PojavLoginActivity.this, selectedAccName);
                                            }
                                        } catch (Exception e) {
                                            Tools.showError(PojavLoginActivity.this, e);
                                        }
                                        break;
                                    
                                    case R.id.menu_account_remove:
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(PojavLoginActivity.this);
                                        builder2.setTitle(selectedAccName);
                                        builder2.setMessage(R.string.warning_remove_account);
                                        builder2.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

                                                @Override
                                                public void onClick(DialogInterface p1, int p2) {
                                                    new InvalidateTokenTask(PojavLoginActivity.this)
                                                        .execute(selectedAccName);
                                                    listAdapter.remove(selectedAccName);
                                                    listAdapter.notifyDataSetChanged();
                                                }
                                            });
                                        builder2.setNegativeButton(android.R.string.cancel, null);
                                        builder2.show();
                                        break;
                                }
                                return true;
                            }
                        });
                    popup.show();
                }
            });
        builder.show();
    }
    
    private MinecraftAccount loginOffline() {
        new File(Tools.DIR_ACCOUNT_OLD).mkdir();
        
        String text = edit2.getText().toString();
        if(text.isEmpty()){
            edit2.setError(getResources().getString(R.string.global_error_field_empty));
        } else if(text.length() <= 2){
            edit2.setError(getResources().getString(R.string.login_error_short_username));
        } else if(new File(Tools.DIR_ACCOUNT_NEW + "/" + text + ".json").exists()){
            edit2.setError(getResources().getString(R.string.login_error_exist_username));
        } else{
            MinecraftAccount builder = new MinecraftAccount();
            builder.isMicrosoft = false;
            builder.username = text;
            
            return builder;
        }
        return null;
    }
    
    private MinecraftAccount mProfile = null;
    public void loginMC(final View v)
    {
        /*skip it

        String proFilePath = MCProfile.build(builder);
        MCProfile.launchWithProfile(this, proFilePath);
        end skip*/
        
        if (sOffline.isChecked()) {
            mProfile = loginOffline();
            playProfile(false);
        } else {
            new LoginTask().setLoginListener(new LoginListener(){

                    @Override
                    public void onBeforeLogin() {
                        v.setEnabled(false);
                        prb.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoginDone(String[] result) {
                        if(result[0].equals("ERROR")){
                            Tools.dialogOnUiThread(PojavLoginActivity.this,
                                getResources().getString(R.string.global_error), strArrToString(result));
                        } else{
                            MinecraftAccount builder = new MinecraftAccount();
                            builder.accessToken = result[1];
                            builder.clientToken = result[2];
                            builder.profileId = result[3];
                            builder.username = result[4];
                            builder.selectedVersion = "1.12.2";
                            mProfile = builder;
                        }
                        v.setEnabled(true);
                        prb.setVisibility(View.GONE);
                        playProfile(false);
                    }
                }).execute(edit2.getText().toString(), edit3.getText().toString());
        }
    }
    
    private void playProfile(boolean notOnLogin) {
        if (mProfile != null) {
            try {
                String profileName = null;
                if (sRemember.isChecked() || notOnLogin) {
                    profileName = mProfile.save();
                }
                
                PojavProfile.launch(PojavLoginActivity.this, profileName == null ? mProfile : profileName);
            } catch (IOException e) {
                Tools.showError(this, e);
            }
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
        ActivityCompat.requestPermissions(this, new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_REQUEST_CODE);
    }

    // This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_REQUEST_CODE){
            synchronized (mLockStoragePerm) {
                mLockStoragePerm.notifyAll();
            }
        }
    }
}
