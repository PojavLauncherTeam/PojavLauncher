package com.kdt.pickafile;

import androidx.appcompat.app.*;
import android.content.*;
import android.util.*;
import android.widget.*;

import com.ipaulpro.afilechooser.*;
import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import android.os.*;

public class FileListView extends LinearLayout
{
    //For list view:
    private File fullPath;
    private ListView mainLv;
    private Context context;

    //For File selected listener:
    private FileSelectedListener fileSelectedListener;
    private DialogTitleListener dialogTitleListener;
    private File lockPath = new File("/");

    //For filtering by file types:
    private final String[] fileSuffixes;
    private boolean showFiles = true;
    private boolean showFolders = true;

    public FileListView(AlertDialog build) {
        this(build.getContext(), null, new String[0]);
        dialogToTitleListener(build);
    }

    public FileListView(AlertDialog build, String fileSuffix) {
        this(build.getContext(), null, new String[]{fileSuffix});
        dialogToTitleListener(build);
    }

    public FileListView(AlertDialog build, String[] fileSuffixes){
        this(build.getContext(), null, fileSuffixes);
        dialogToTitleListener(build);
    }

    public FileListView(Context context){
        this(context, null);
    }

    public FileListView(Context context, AttributeSet attrs){
        this(context, attrs, new String[0]);
    }

    public FileListView(Context context, AttributeSet attrs, String[] fileSuffixes) {
        this(context, attrs, 0, fileSuffixes);
    }

    public FileListView(Context context, AttributeSet attrs, int defStyle, String[] fileSuffixes) {
        super(context, attrs, defStyle);
        this.fileSuffixes = fileSuffixes;
        init(context);
    }

    private void dialogToTitleListener(AlertDialog dialog) {
        if(dialog != null) dialogTitleListener = dialog::setTitle;
    }

    public void init(final Context context) {
        //Main setup:
        this.context = context;

        LayoutParams layParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        setOrientation(VERTICAL);

        mainLv = new ListView(context);

        mainLv.setOnItemClickListener((p1, p2, p3, p4) -> {
            // TODO: Implement this method
            File mainFile = new File(p1.getItemAtPosition(p3).toString());
            if (p3 == 0 && !lockPath.equals(fullPath)) {
                parentDir();
            } else {
                listFileAt(mainFile);
            }
        });

        mainLv.setOnItemLongClickListener((p1, p2, p3, p4) -> {
            // TODO: Implement this method
            File mainFile = new File(p1.getItemAtPosition(p3).toString());
            if (mainFile.isFile()) {
                fileSelectedListener.onFileLongClick(mainFile, mainFile.getAbsolutePath());
                return true;
            }
            return false;
        });
        addView(mainLv, layParam);

        try {
            listFileAt(Environment.getExternalStorageDirectory());
        } catch (NullPointerException e) {} // Android 10+ disallows access to sdcard
    }
    public void setFileSelectedListener(FileSelectedListener listener)
    {
        this.fileSelectedListener = listener;
    }
    public void setDialogTitleListener(DialogTitleListener listener) {
        this.dialogTitleListener = listener;
    }

    public void listFileAt(final File path) {
        try{
            if(path.exists()){
                if(path.isDirectory()){
                    fullPath = path;

                    File[] listFile = path.listFiles();
                    FileListAdapter fileAdapter = new FileListAdapter(context);
                    if(!path.equals(lockPath)){
                        fileAdapter.add(new File(path, ".."));
                    }

                    if(listFile != null && listFile.length != 0){
                        Arrays.sort(listFile, new SortFileName());

                        for(File file : listFile){
                            if(file.isDirectory()){
                                if(showFolders && ((!file.getName().startsWith(".")) || file.getName().equals(".minecraft")))
                                    fileAdapter.add(file);
                                continue;
                            }

                            if(showFiles){
                                if(fileSuffixes.length > 0){
                                    for(String suffix : fileSuffixes){
                                        if(file.getName().endsWith("." + suffix)){
                                            fileAdapter.add(file);
                                            break;
                                        }
                                    }
                                }else {
                                    fileAdapter.add(file);
                                }
                            }
                        }
                    }
                    mainLv.setAdapter(fileAdapter);
                    if(dialogTitleListener != null) dialogTitleListener.onChangeDialogTitle(path.getAbsolutePath());
                } else {
                    fileSelectedListener.onFileSelected(path, path.getAbsolutePath());
                }
            } else {
                Toast.makeText(context, "This folder (or file) doesn't exist", Toast.LENGTH_SHORT).show();
                refreshPath();
            }
        } catch (Exception e){
            Tools.showError(context, e);
        }
    }

    public File getFullPath(){
        return fullPath;
    }

    public void refreshPath() {
        listFileAt(getFullPath());
    }

    public void parentDir() {
        if(!fullPath.getAbsolutePath().equals("/")){
            listFileAt(fullPath.getParentFile());
        }
    }

    public void lockPathAt(File path) {
        lockPath = path;
        listFileAt(path);
    }

    public void setShowFiles(boolean showFiles){
        this.showFiles = showFiles;
    }

    public void setShowFolders(boolean showFolders){
        this.showFolders = showFolders;
    }
}
