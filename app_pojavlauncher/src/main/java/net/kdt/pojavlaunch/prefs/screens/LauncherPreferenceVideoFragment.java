package net.kdt.pojavlaunch.prefs.screens;

import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_NOTCH_SIZE;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.prefs.CustomSeekBarPreference;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

/**
 * Fragment for any settings video related
 */
public class LauncherPreferenceVideoFragment extends LauncherPreferenceFragment {
    private CustomSeekBarPreference seek5;
    private SwitchPreference sustainedPerfSwitch;
    private ListPreference rendererListPreference;
    private SwitchPreferenceCompat forceVSyncPreference;

    @Override
    public void onCreatePreferences(Bundle b, String str) {
        addPreferencesFromResource(R.xml.pref_video);

        // Desabilitar o comportamento de verificação de entalhe no Android 8.1 e abaixo.
        findPreference("ignoreNotch").setVisible(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && PREF_NOTCH_SIZE > 0);

        seek5 = findPreference("resolutionRatio");
        seek5.setMin(25);
        seek5.setSuffix(" %");

        // #724 bug fix
        if (seek5.getValue() < 25) {
            seek5.setValue(100);
        }

        // O desempenho sustentado só está disponível desde o Nougat
        sustainedPerfSwitch = findPreference("sustainedPerformance");
        sustainedPerfSwitch.setVisible(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N);

        rendererListPreference = findPreference("renderer");
        Tools.RenderersList renderersList = Tools.getCompatibleRenderers(getContext());
        rendererListPreference.setEntries(renderersList.rendererDisplayNames);
        rendererListPreference.setEntryValues(renderersList.rendererIds.toArray(new String[0]));

        forceVSyncPreference = findPreference("force_vsync");
        forceVSyncPreference.setVisible(LauncherPreferences.PREF_USE_ALTERNATE_SURFACE);

        // Cache o valor de algumas preferências para evitar chamadas desnecessárias ao SharedPreferences
        boolean useAlternateSurface = LauncherPreferences.get(getContext(), LauncherPreferences.PREF_USE_ALTERNATE_SURFACE, false);
        computeVisibility(useAlternateSurface);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences p, String s) {
        super.onSharedPreferenceChanged(p, s);

        // Compute visibility again only if the alternate surface preference changed
        if (s.equals(LauncherPreferences.PREF_USE_ALTERNATE_SURFACE)) {
            boolean useAlternateSurface = LauncherPreferences.get(getContext(), LauncherPreferences.PREF_USE_ALTERNATE_SURFACE, false);
            computeVisibility(useAlternateSurface);
        }
    }

    private void computeVisibility(boolean useAlternateSurface) {
        forceVSyncPreference.setVisible(useAlternateSurface);
    }
}
