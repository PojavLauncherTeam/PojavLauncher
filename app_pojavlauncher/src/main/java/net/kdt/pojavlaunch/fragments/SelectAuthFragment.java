package net.kdt.pojavlaunch.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.accounts.LocalAccountUtils;

public class SelectAuthFragment extends Fragment {
    public static final String TAG = "AUTH_SELECT_FRAGMENT";

    public SelectAuthFragment(){
        super(R.layout.fragment_select_auth_method);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button mMicrosoftButton = view.findViewById(R.id.button_microsoft_authentication);
        Button mLocalButton = view.findViewById(R.id.button_local_authentication);

        FragmentActivity fragmentActivity = requireActivity();

        mMicrosoftButton.setOnClickListener(v -> Tools.swapFragment(fragmentActivity, MicrosoftLoginFragment.class, MicrosoftLoginFragment.TAG, null));
        mLocalButton.setOnClickListener(v -> LocalAccountUtils.checkUsageAllowed(new LocalAccountUtils.CheckResultListener() {
            @Override
            public void onUsageAllowed() {
                Tools.swapFragment(fragmentActivity, LocalLoginFragment.class, LocalLoginFragment.TAG, null);
            }

            @Override
            public void onUsageDenied() {
                LocalAccountUtils.openDialog(fragmentActivity,
                        getString(R.string.reject_local_account) + "\r\n" + getString(R.string.purchase_minecraft_account_tip));
            }
        }));
    }
}
