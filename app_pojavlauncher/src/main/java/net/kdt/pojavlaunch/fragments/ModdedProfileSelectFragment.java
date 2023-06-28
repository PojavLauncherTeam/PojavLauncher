package net.kdt.pojavlaunch.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;

public class ModdedProfileSelectFragment extends Fragment {
    public static final String TAG = "ModdedProfileSelectFragment";
    public ModdedProfileSelectFragment() {
        super(R.layout.fragment_mod_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.modded_profile_fabric).setOnClickListener((v)->
                Tools.swapFragment(requireActivity(), FabricInstallFragment.class, FabricInstallFragment.TAG, false, null));
        view.findViewById(R.id.modded_profile_forge).setOnClickListener((v)->
                Tools.swapFragment(requireActivity(), ForgeInstallFragment.class, ForgeInstallFragment.TAG, false, null));
    }
}
