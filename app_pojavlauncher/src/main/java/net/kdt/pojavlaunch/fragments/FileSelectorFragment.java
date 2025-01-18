package net.kdt.pojavlaunch.fragments;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kdt.pickafile.FileListView;
import com.kdt.pickafile.FileSelectedListener;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;

import java.io.File;
public class FileSelectorFragment extends Fragment {
    public static final String TAG = "FileSelectorFragment";
    public static final String BUNDLE_SELECT_FOLDER = "select_folder";
    public static final String BUNDLE_SELECT_FILE = "select_file";
    public static final String BUNDLE_SHOW_FILE = "show_file";
    public static final String BUNDLE_SHOW_FOLDER = "show_folder";
    public static final String BUNDLE_ROOT_PATH = "root_path";

    private Button mSelectFolderButton, mCreateFolderButton;
    private FileListView mFileListView;
    private TextView mFilePathView;

    private boolean mSelectFolder = true;
    private boolean mShowFiles = true;
    private boolean mShowFolders = true;
    private String mRootPath = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
            ? Tools.DIR_GAME_NEW
            : Environment.getExternalStorageDirectory().getAbsolutePath();


    public FileSelectorFragment(){
        super(R.layout.fragment_file_selector);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindViews(view);
        parseBundle();
        if(!mSelectFolder) mSelectFolderButton.setVisibility(View.GONE);
        else mSelectFolderButton.setVisibility(View.VISIBLE);

        mFileListView.setShowFiles(mShowFiles);
        mFileListView.setShowFolders(mShowFolders);
        mFileListView.lockPathAt(new File(mRootPath));
        mFileListView.setDialogTitleListener((title)->mFilePathView.setText(removeLockPath(title)));
        mFileListView.refreshPath();

        mCreateFolderButton.setOnClickListener(v -> {
            final EditText editText = new EditText(getContext());
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.folder_dialog_insert_name)
                    .setView(editText)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(R.string.folder_dialog_create, (dialog, which) -> {
                        File folder = new File(mFileListView.getFullPath(), editText.getText().toString());
                        boolean success = folder.mkdir();
                        if(success){
                            mFileListView.listFileAt(new File(mFileListView.getFullPath(),editText.getText().toString()));
                        }else{
                            mFileListView.refreshPath();
                        }
                    }).show();
        });

        mSelectFolderButton.setOnClickListener(v -> {
            ExtraCore.setValue(ExtraConstants.FILE_SELECTOR, removeLockPath(mFileListView.getFullPath().getAbsolutePath()));
            Tools.removeCurrentFragment(requireActivity());
        });

        mFileListView.setFileSelectedListener(new FileSelectedListener() {
            @Override
            public void onFileSelected(File file, String path) {
                ExtraCore.setValue(ExtraConstants.FILE_SELECTOR, removeLockPath(path));
                Tools.removeCurrentFragment(requireActivity());
            }
        });
    }

    private String removeLockPath(String path){
        return path.replace(mRootPath, ".");
    }

    private void parseBundle(){
        Bundle bundle = getArguments();
        if(bundle == null) return;
        mSelectFolder = bundle.getBoolean(BUNDLE_SELECT_FOLDER, mSelectFolder);
        mShowFiles = bundle.getBoolean(BUNDLE_SHOW_FILE, mShowFiles);
        mShowFolders = bundle.getBoolean(BUNDLE_SHOW_FOLDER, mShowFolders);
        mRootPath = bundle.getString(BUNDLE_ROOT_PATH, mRootPath);
    }

    private void bindViews(@NonNull View view){
        mSelectFolderButton = view.findViewById(R.id.file_selector_select_folder);
        mCreateFolderButton = view.findViewById(R.id.file_selector_create_folder);
        mFileListView = view.findViewById(R.id.file_selector);
        mFilePathView = view.findViewById(R.id.file_selector_current_path);
    }
}
