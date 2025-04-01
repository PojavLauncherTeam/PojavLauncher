package net.kdt.pojavlaunch.prefs.screens;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.view.LayoutInflater;

import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;
import androidx.preference.Preference;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.prefs.CustomSeekBarPreference;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_RENDERER;

/**
 * Fragment for any settings video related
 */
public class LauncherPreferenceVideoFragment extends LauncherPreferenceFragment {
    @Override
    public void onCreatePreferences(Bundle b, String str) {
        addPreferencesFromResource(R.xml.pref_video);
        int resolution = (int) (LauncherPreferences.PREF_SCALE_FACTOR * 100);

        // Disable notch checking behavior on android 8.1 and below.
        requirePreference("ignoreNotch").setVisible(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && LauncherPreferences.PREF_NOTCH_SIZE > 0);

        CustomSeekBarPreference resolutionSeekbar = requirePreference("resolutionRatio",
                CustomSeekBarPreference.class);
        resolutionSeekbar.setSuffix(" %");

        // #724 bug fix
        if (resolution < 25) {
            resolutionSeekbar.setValue(100);
        } else {
            resolutionSeekbar.setValue(resolution);
        }

        // Sustained performance is only available since Nougat
        SwitchPreference sustainedPerfSwitch = requirePreference("sustainedPerformance",
                SwitchPreference.class);
        sustainedPerfSwitch.setVisible(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N);
        sustainedPerfSwitch.setChecked(LauncherPreferences.PREF_SUSTAINED_PERFORMANCE);

        requirePreference("alternate_surface", SwitchPreferenceCompat.class).setChecked(LauncherPreferences.PREF_USE_ALTERNATE_SURFACE);
        requirePreference("force_vsync", SwitchPreferenceCompat.class).setChecked(LauncherPreferences.PREF_FORCE_VSYNC);

        ListPreference rendererListPreference = requirePreference("renderer",
                ListPreference.class);
        Tools.RenderersList renderersList = Tools.getCompatibleRenderers(getContext());
        rendererListPreference.setEntries(renderersList.rendererDisplayNames);
        rendererListPreference.setEntryValues(renderersList.rendererIds.toArray(new String[0]));

        // Initialize mgRendererSettingsPref before usage
        Preference mgRendererSettingsPref = requirePreference("renderer_mobileglues_settings", Preference.class);
        rendererListPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            String currentRenderer = (String) newValue;
            Tools.LOCAL_RENDERER = currentRenderer;
            mgRendererSettingsPref.setVisible(currentRenderer.equals("opengles3_mges"));
            return true;
        });

        mgRendererSettingsPref.setVisible(PREF_RENDERER.equals("opengles3_mges"));
        mgRendererSettingsPref.setOnPreferenceClickListener(preference -> {
            mgRendererSettings();
            return true;
        });

        computeVisibility();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences p, String s) {
        super.onSharedPreferenceChanged(p, s);
        computeVisibility();
    }

    private void computeVisibility(){
        requirePreference("force_vsync", SwitchPreferenceCompat.class)
                .setVisible(LauncherPreferences.PREF_USE_ALTERNATE_SURFACE);
    }

    // MobileGlues Renderer Settings
    private void mgRendererSettings() {
        // Layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_mgrenderer_settings, null);
        EditText maxGlslCacheSize = view.findViewById(R.id.mg_input_max_glsl_cache_size);
        Spinner enableANGLE = view.findViewById(R.id.mg_spinner_angle);
        Spinner enableNoError = view.findViewById(R.id.mg_spinner_no_error);
        Switch enableExtGL43 = view.findViewById(R.id.mg_switch_ext_gl43);
        Switch enableExtComputeShader = view.findViewById(R.id.mg_switch_ext_cs);

        // Max glsl cache size
        maxGlslCacheSize.setText(LauncherPreferences.MG_GLSL_CACHE_SIZE);

        // Angle Settings
        ArrayList<String> angleOptions = new ArrayList<>();
        angleOptions.add(getString(R.string.mg_option_angle_disable_if_possible));
        angleOptions.add(getString(R.string.mg_option_angle_enable_if_possible));
        angleOptions.add(getString(R.string.mg_option_angle_disable));
        angleOptions.add(getString(R.string.mg_option_angle_enable));
        ArrayAdapter<String> angleAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner, angleOptions);
        enableANGLE.setAdapter(angleAdapter);
        enableANGLE.setSelection(Integer.parseInt(LauncherPreferences.MG_ANGLE_OPTION));

        // No error Settings
        ArrayList<String> noErrorOptions = new ArrayList<>();
        noErrorOptions.add(getString(R.string.mg_option_no_error_auto));
        noErrorOptions.add(getString(R.string.mg_option_no_error_enable));
        noErrorOptions.add(getString(R.string.mg_option_no_error_disable_pri));
        noErrorOptions.add(getString(R.string.mg_option_no_error_disable_sec));
        ArrayAdapter<String> noErrorAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner, noErrorOptions);
        enableNoError.setAdapter(noErrorAdapter);
        enableNoError.setSelection(Integer.parseInt(LauncherPreferences.MG_NOERROR_OPTION));

        enableExtGL43.setChecked(LauncherPreferences.MG_EXT_GL43.equals("1"));
        enableExtComputeShader.setChecked(LauncherPreferences.MG_EXT_CS.equals("1"));

        new AlertDialog.Builder(getContext())
            .setTitle("Dialog Title")
            .setMessage("Dialog Message")
            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                String cacheSize = maxGlslCacheSize.getText().toString();

                LauncherPreferences.MG_GLSL_CACHE_SIZE = cacheSize;
                LauncherPreferences.MG_ANGLE_OPTION = Integer.toString(enableANGLE.getSelectedItemPosition());
                LauncherPreferences.MG_NOERROR_OPTION = Integer.toString(enableNoError.getSelectedItemPosition());
                LauncherPreferences.MG_EXT_GL43 = enableExtGL43.isChecked() ? "1" : "0";
                LauncherPreferences.MG_EXT_CS = enableExtComputeShader.isChecked() ? "1" : "0";
                LauncherPreferences.DEFAULT_PREF.edit()
                        .putString("mg_glsl_cache_size", LauncherPreferences.MG_GLSL_CACHE_SIZE)
                        .putString("mg_angle_option", LauncherPreferences.MG_ANGLE_OPTION)
                        .putString("mg_noerror_option", LauncherPreferences.MG_NOERROR_OPTION)
                        .putString("mg_ext_gl43", LauncherPreferences.MG_EXT_GL43)
                        .putString("mg_ext_compute_shader", LauncherPreferences.MG_EXT_CS)
                        .apply();
            })
            .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                dialog.dismiss(); // Properly dismiss the dialog on cancel
            })
            .show();
    }
}
