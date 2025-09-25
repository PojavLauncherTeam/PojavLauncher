package net.kdt.pojavlaunch.prefs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

public class CustomListSummaryProvider implements Preference.SummaryProvider {
    @Nullable
    public CharSequence provideSummary(@NonNull Preference preference) {
        if (preference.hasKey())
            preference.setSummary(preference.getKey());
        else preference.setSummary("@string/mcl_setting_title_renderer_settings");
        return null;
    }
}
