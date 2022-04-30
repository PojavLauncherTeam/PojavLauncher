package net.kdt.pojavlaunch.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import net.kdt.pojavlaunch.BaseLauncherActivity;
import net.kdt.pojavlaunch.CustomControlsActivity;
import net.kdt.pojavlaunch.JavaGUILauncherActivity;
import net.kdt.pojavlaunch.PojavLauncherActivity;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceFragment;

public class MainFragment extends Fragment{
    private View mRootView;
    private Button mButton1;
    private Button mButton2;
    private Button mButton3;
    private Button mButton4;
    private Button mCustomControlsButton;
    private Button mInstallJarButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(!(getActivity() instanceof BaseLauncherActivity))
            throw new RuntimeException("Fragment running from wrong Activity");
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mButton1 = view.findViewById(R.id.btnTab1);
        mButton2 = view.findViewById(R.id.btnTab2);
        mButton3 = view.findViewById(R.id.btnTab3);
        mButton4 = view.findViewById(R.id.btnTab4);
        mCustomControlsButton = view.findViewById(R.id.customControlsButton);
        mInstallJarButton = view.findViewById(R.id.installJarButton);
        mButton1.setOnClickListener(v->swapFragment(LauncherFragment.class));
        mButton2.setOnClickListener(v->swapFragment(ConsoleFragment.class));
        mButton3.setOnClickListener(v->swapFragment(CrashFragment.class));
        mButton4.setOnClickListener(v->swapFragment(LauncherPreferenceFragment.class));
        mCustomControlsButton.setOnClickListener(v->startActivity(new Intent(getContext(), CustomControlsActivity.class)));
        mInstallJarButton.setOnClickListener(v->installMod(false));
        mInstallJarButton.setOnLongClickListener(v -> {
            installMod(true);
            return true;
        });
    }


    public void swapFragment(Class<? extends Fragment> fragmentClass) {
        assert(getActivity() != null);
        getActivity().getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .replace(R.id.launchermainFragmentContainer, fragmentClass, null)
                .commit();
        Log.i("Backstack",getActivity().getSupportFragmentManager().getBackStackEntryCount()+"");
    }

    private void installMod(boolean customJavaArgs) {
        Context context = getContext();
        if (MultiRTUtils.getExactJreName(8) == null) {
            Toast.makeText(context, R.string.multirt_nojava8rt, Toast.LENGTH_LONG).show();
            return;
        }
        if (customJavaArgs) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.alerttitle_installmod);
            builder.setNegativeButton(android.R.string.cancel, null);
            final AlertDialog dialog;
            final EditText edit = new EditText(context);
            edit.setSingleLine();
            edit.setHint("-jar/-cp /path/to/file.jar ...");
            builder.setPositiveButton(android.R.string.ok, (di, i) -> {
                Intent intent = new Intent(context, JavaGUILauncherActivity.class);
                intent.putExtra("skipDetectMod", true);
                intent.putExtra("javaArgs", edit.getText().toString());
                startActivity(intent);
            });
            dialog = builder.create();
            dialog.setView(edit);
            dialog.show();
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("jar");
            if(mimeType == null) mimeType = "*/*";
            intent.setType(mimeType);
            startActivityForResult(intent,BaseLauncherActivity.RUN_MOD_INSTALLER);
        }

    }
}
