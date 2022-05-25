package com.kdt.pickafile;

import androidx.appcompat.app.*;
import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.ipaulpro.afilechooser.*;
import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import android.os.*;

public class FileListView extends LinearLayout
{
    //For list view:
    private String fullPath;
    private ListView mainLv;
    private Context context;

    //For File selected listener:
    private FileSelectedListener listener;
    private AlertDialog build;
    private String lockPath = "/";

    //For filtering by file types:
    private final String[] fileSuffixes;

    public FileListView(AlertDialog build) {
        this(build.getContext(), null, new String[0]);
        this.build = build;
    }

    public FileListView(AlertDialog build, String fileSuffix) {
        this(build.getContext(), null, new String[]{fileSuffix});
        this.build = build;
    }

    public FileListView(AlertDialog build, String[] fileSuffixes){
        this(build.getContext(), null, fileSuffixes);
        this.build = build;
    }

    public FileListView(Context context, AttributeSet attrs, String[] fileSuffixes) {
        this(context, attrs, 0, fileSuffixes);
    }

    public FileListView(Context context, AttributeSet attrs, int defStyle, String[] fileSuffixes) {
        super(context, attrs, defStyle);
        this.fileSuffixes = fileSuffixes;
        init(context);
    }

    public void init(final Context context) {
        //Main setup:
        this.context = context;

        LayoutParams layParam = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        setOrientation(VERTICAL);

        mainLv = new ListView(context);

        mainLv.setOnItemClickListener(new OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
                {
                    // TODO: Implement this method
                    File mainFile = new File(p1.getItemAtPosition(p3).toString());
                    if (p3 == 0 && !lockPath.equals(fullPath)) {
                        parentDir();
                    } else {
                        listFileAt(mainFile.getAbsolutePath());
                    }
                }
            });
        mainLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
                @Override
                public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
                {
                    // TODO: Implement this method
                    File mainFile = new File(p1.getItemAtPosition(p3).toString());
                    if (mainFile.isFile()) {
                        listener.onFileLongClick(mainFile, mainFile.getAbsolutePath());
                        return true;
                    }
                    return false;
                }
            });
        addView(mainLv, layParam);

        try {
            listFileAt(Environment.getExternalStorageDirectory().getAbsolutePath());
        } catch (NullPointerException e) {} // Android 10+ disallows access to sdcard
    }
    public void setFileSelectedListener(FileSelectedListener listener)
    {
        this.listener = listener;
    }

    public void listFileAt(final String path)
    {
        try{
            final File mainPath = new File(path);
            if(mainPath.exists()){
                if(mainPath.isDirectory()){
                    fullPath = path;

                    File[] listFile = mainPath.listFiles();
                    FileListAdapter fileAdapter = new FileListAdapter(context);
                    if(!path.equals(lockPath)){
                        fileAdapter.add(new File(path, ".."));
                    }

                    if(listFile != null && listFile.length != 0){
                        Arrays.sort(listFile, new SortFileName());
                        if(fileSuffixes.length > 0){ //Meaning we want only specific files
                            for(File file : listFile){
                                if(file.isDirectory()){
                                    if((!file.getName().startsWith(".")) || file.getName().equals(".minecraft"))
                                    fileAdapter.add(file);
                                    continue;
                                }

                                for(String suffix : fileSuffixes){
                                    if(file.getName().endsWith("." + suffix)){
                                        fileAdapter.add(file);
                                        break;
                                    }
                                }
                            }
                        }else{ //We get every file
                            for(File file : listFile){
                                fileAdapter.add(file);
                            }
                        }

                    }
                    mainLv.setAdapter(fileAdapter);
                    if (build != null) build.setTitle(new File(path).getName());
                } else {
                    listener.onFileSelected(mainPath, path);
                }
            } else {
                Toast.makeText(context, "This folder (or file) doesn't exist", Toast.LENGTH_SHORT).show();
                refreshPath();
            }
        } catch (Exception e){
            Tools.showError(context, e);
        }
    }

    public String getFullPath(){
        return fullPath;
    }

    public void refreshPath() {
        listFileAt(getFullPath());
    }
    
    public void parentDir() {
        File pathFile = new File(fullPath);
        if(!pathFile.getAbsolutePath().equals("/")){
            listFileAt(pathFile.getParent());
        }
    }
    
    public void lockPathAt(String path) {
        lockPath = path;
        listFileAt(path);
    }
}
