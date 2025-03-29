package net.kdt.pojavlaunch;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.sql.Types.NULL;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fcl.plugin.mobileglues.databinding.ActivityMainBinding;
import com.fcl.plugin.mobileglues.settings.FolderPermissionManager;
import com.fcl.plugin.mobileglues.settings.MGConfig;
import com.fcl.plugin.mobileglues.utils.Constants;
import com.fcl.plugin.mobileglues.utils.ResultListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    private static final int REQUEST_CODE_SAF = 2000;
    public static Uri MGDirectoryUri;
    public static Context MainActivityContext;
    private ActivityMainBinding binding;
    private MGConfig config = null;
    private FolderPermissionManager folderPermissionManager;
    private Boolean State = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        folderPermissionManager = new FolderPermissionManager(this);
        MainActivityContext = this;

        ArrayList<String> angleOptions = new ArrayList<>();
        angleOptions.add(getString(R.string.option_angle_disable_if_possible));
        angleOptions.add(getString(R.string.option_angle_enable_if_possible));
        angleOptions.add(getString(R.string.option_angle_disable));
        angleOptions.add(getString(R.string.option_angle_enable));
        ArrayAdapter<String> angleAdapter = new ArrayAdapter<>(this, R.layout.spinner, angleOptions);
        binding.spinnerAngle.setAdapter(angleAdapter);

        ArrayList<String> noErrorOptions = new ArrayList<>();
        noErrorOptions.add(getString(R.string.option_no_error_auto));
        noErrorOptions.add(getString(R.string.option_no_error_enable));
        noErrorOptions.add(getString(R.string.option_no_error_disable_pri));
        noErrorOptions.add(getString(R.string.option_no_error_disable_sec));
        ArrayAdapter<String> noErrorAdapter = new ArrayAdapter<>(this, R.layout.spinner, noErrorOptions);
        binding.spinnerNoError.setAdapter(noErrorAdapter);

        binding.info.setOnClickListener(view -> new AppInfoDialogBuilder(MainActivityContext).create().show());

        binding.openOptions.setOnClickListener(view -> {
            if (State) {
                checkPermission();
            } else {
                folderPermissionManager.clearAllPermissions();
                checkPermissionSilently();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissionSilently();
    }

    private void showOptions() {
        try {
            config = MGConfig.loadConfig(this);

            if (config == null) {
                config = new MGConfig(0, 0, 0, 0, 24);
            }
            if (config.getEnableANGLE() > 3 || config.getEnableANGLE() < 0)
                config.setEnableANGLE(0);
            if (config.getEnableNoError() > 3 || config.getEnableNoError() < 0)
                config.setEnableNoError(0);

            if (config.getMaxGlslCacheSize() == NULL)
                config.setMaxGlslCacheSize(24);

            binding.inputMaxGlslCacheSize.setText(String.valueOf(config.getMaxGlslCacheSize()));
            binding.spinnerAngle.setSelection(config.getEnableANGLE());
            binding.spinnerNoError.setSelection(config.getEnableNoError());
            binding.switchExtGl43.setChecked(config.getEnableExtGL43() == 1);
            binding.switchExtCs.setChecked(config.getEnableExtComputeShader() == 1);

            binding.spinnerAngle.setOnItemSelectedListener(this);
            binding.spinnerNoError.setOnItemSelectedListener(this);
            binding.switchExtGl43.setOnCheckedChangeListener(this);
            binding.switchExtCs.setOnCheckedChangeListener(this);
            binding.inputMaxGlslCacheSize.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    String text = s.toString().trim();
                    if (!text.isEmpty()) {
                        try {
                            int number = Integer.parseInt(text);
                            if (number < -1 || number == 0) {
                                binding.inputMaxGlslCacheSizeLayout.setError(getString(R.string.option_glsl_cache_error_range));
                            } else {
                                binding.inputMaxGlslCacheSizeLayout.setError(null);
                                config.setMaxGlslCacheSize(number);
                            }
                        } catch (NumberFormatException e) {
                            binding.inputMaxGlslCacheSizeLayout.setError(getString(R.string.option_glsl_cache_error_invalid));
                        } catch (IOException e) {
                            binding.inputMaxGlslCacheSizeLayout.setError(getString(R.string.option_glsl_cache_error_unexpected));
                            throw new RuntimeException(e);
                        }
                    } else {
                        binding.inputMaxGlslCacheSizeLayout.setError(null);
                        try {
                            config.setMaxGlslCacheSize(24);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                }
            });
            State = false;
            binding.openOptions.setText(getString(R.string.clear_permission));
            binding.optionLayout.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            Logger.getLogger("MG").log(Level.SEVERE, "Failed to load config! Exception: ", e.getCause());
            Toast.makeText(this, getString(R.string.warning_load_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void hideOptions() {
        State = true;
        binding.openOptions.setText(getString(R.string.open_options));
        binding.optionLayout.setVisibility(View.GONE);
    }

    private void checkPermissionSilently() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MGDirectoryUri = folderPermissionManager.getMGFolderUri();

            MGConfig config = MGConfig.loadConfig(this);
            if (config != null && MGDirectoryUri != null) {
                showOptions();
            } else {
                hideOptions();
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showOptions();
            } else {
                hideOptions();
            }
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.dialog_permission_msg_android_Q, Constants.MG_DIRECTORY))
                .setPositiveButton(R.string.dialog_positive, (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(Environment.getExternalStorageDirectory() + "/MG"));
                    ResultListener.startActivityForResult(this, intent, REQUEST_CODE_SAF, (requestCode, resultCode, data) -> {
                        if (requestCode == REQUEST_CODE_SAF && resultCode == RESULT_OK && data != null) {
                            Uri treeUri = data.getData();
                            if (treeUri == null) {
                                hideOptions();
                                return;
                            }

                            if (!folderPermissionManager.isUriMatchingFilePath(treeUri, new File(Constants.MG_DIRECTORY))) {
                                new MaterialAlertDialogBuilder(this)
                                        .setTitle(R.string.app_name)
                                        .setMessage(getString(R.string.warning_path_selection_error, Constants.MG_DIRECTORY, folderPermissionManager.getFileByUri(treeUri)))
                                        .setPositiveButton(R.string.dialog_positive, null)
                                        .show();
                                hideOptions();
                                return;
                            }

                            getContentResolver().takePersistableUriPermission(treeUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                            MGDirectoryUri = treeUri;
                            MGConfig config = MGConfig.loadConfig(this);
                            if (config == null) config = new MGConfig(0, 0, 0, 0, 24);
                            config.saveConfig(this);
                            showOptions();
                        }
                    });
                })
                .setNegativeButton(R.string.dialog_negative, (dialog, which) -> dialog.dismiss())
                .show();
        else {
            if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showOptions();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 1000);
                hideOptions();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == binding.spinnerAngle && config != null) {
            try {
                if (i == 3 && isAdreno740()) {
                    new MaterialAlertDialogBuilder(this)
                            .setTitle(getString(R.string.dialog_title_warning))
                            .setMessage(getString(R.string.warning_adreno_740_angle))
                            .setPositiveButton(getString(R.string.dialog_positive), (dialog, which) -> {
                                try {
                                    config.setEnableANGLE(i);
                                } catch (IOException e) {
                                    Logger.getLogger("MG").log(Level.SEVERE, "Failed to save config! Exception: ", e);
                                    Toast.makeText(MainActivity.this, getString(R.string.warning_save_failed), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(getString(R.string.dialog_negative), (dialog, which) -> binding.spinnerAngle.setSelection(config.getEnableANGLE()))
                            .show();
                } else {
                    config.setEnableANGLE(i);
                }
            } catch (IOException e) {
                Logger.getLogger("MG").log(Level.SEVERE, "Failed to save config! Exception: ", e.getCause());
                Toast.makeText(this, getString(R.string.warning_save_failed), Toast.LENGTH_SHORT).show();
            }
        }

        if (adapterView == binding.spinnerNoError && config != null) {
            try {
                config.setEnableNoError(i);
            } catch (IOException e) {
                Logger.getLogger("MG").log(Level.SEVERE, "Failed to save config! Exception: ", e.getCause());
                Toast.makeText(this, getString(R.string.warning_save_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCheckedChanged(final CompoundButton compoundButton, final boolean isChecked) {
        if (compoundButton == binding.switchExtGl43 && config != null) {
            if (isChecked) {
                new MaterialAlertDialogBuilder(MainActivity.this)
                        .setTitle(getString(R.string.dialog_title_warning))
                        .setMessage(getString(R.string.warning_ext_gl43_enable))
                        .setCancelable(false)
                        .setOnKeyListener((dialog, keyCode, event) -> keyCode == KeyEvent.KEYCODE_BACK)
                        .setPositiveButton(getString(R.string.dialog_positive), (dialog, which) -> {
                            try {
                                config.setEnableExtGL43(1);
                            } catch (IOException e) {
                                Logger.getLogger("MG").log(Level.SEVERE, "Failed to save config! Exception: ", e);
                                Toast.makeText(MainActivity.this, getString(R.string.warning_save_failed), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_negative), (dialog, which) -> binding.switchExtGl43.setChecked(false))
                        .show();
            } else {
                try {
                    config.setEnableExtGL43(0);
                } catch (IOException e) {
                    Logger.getLogger("MG").log(Level.SEVERE, "Failed to save config! Exception: ", e);
                    Toast.makeText(MainActivity.this, getString(R.string.warning_save_failed), Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (compoundButton == binding.switchExtCs && config != null) {
            if (isChecked) {
                new MaterialAlertDialogBuilder(MainActivity.this)
                        .setTitle(getString(R.string.dialog_title_warning))
                        .setMessage(getString(R.string.warning_ext_cs_enable)).setCancelable(false)
                        .setOnKeyListener((dialog, keyCode, event) -> keyCode == KeyEvent.KEYCODE_BACK)
                        .setPositiveButton(getString(R.string.dialog_positive), (dialog, which) -> {
                            try {
                                config.setEnableExtComputeShader(1);
                            } catch (IOException e) {
                                Logger.getLogger("MG").log(Level.SEVERE, "Failed to save config! Exception: ", e);
                                Toast.makeText(MainActivity.this, getString(R.string.warning_save_failed), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_negative), (dialog, which) -> binding.switchExtCs.setChecked(false))
                        .show();
            } else {
                try {
                    config.setEnableExtComputeShader(0);
                } catch (IOException e) {
                    Logger.getLogger("MG").log(Level.SEVERE, "Failed to save config! Exception: ", e);
                    Toast.makeText(MainActivity.this, getString(R.string.warning_save_failed), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ResultListener.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showOptions();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || !ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    ResultListener.startActivityForResult(this, intent, 1000, (requestCode1, resultCode, data) -> {
                        if (requestCode1 == 1000) {
                            if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                showOptions();
                            } else {
                                onRequestPermissionsResult(requestCode1, permissions, grantResults);
                            }
                        }
                    });
                } else {
                    checkPermission();
                }
            }
        }
    }

    private String getGPUName() {
        EGLDisplay eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
            return null;
        }

        int[] version = new int[2];
        if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
            return null;
        }

        int[] configAttributes = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_NONE
        };
        android.opengl.EGLConfig[] eglConfigs = new android.opengl.EGLConfig[1];
        int[] numConfigs = new int[1];
        if (!EGL14.eglChooseConfig(eglDisplay, configAttributes, 0, eglConfigs, 0, eglConfigs.length, numConfigs, 0)) {
            EGL14.eglTerminate(eglDisplay);
            return null;
        }

        int[] contextAttributes = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        EGLContext eglContext = EGL14.eglCreateContext(eglDisplay, eglConfigs[0], EGL14.EGL_NO_CONTEXT, contextAt
