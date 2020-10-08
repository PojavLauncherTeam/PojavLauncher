package com.kdt.filerapi;

import android.support.v7.app.*;
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

    public FileListView(Context context, AlertDialog build) {
        super(context);
        init(context);
        this.build = build;
    }

    public FileListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FileListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(final Context context) {
        //Main setup:
        this.context = context;

        LayoutParams layParam = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        setOrientation(VERTICAL);

        mainLv = new ListView(context);

        //listFileAt(Environment.getExternalStorageDirectory().getPath());

        mainLv.setOnItemClickListener(new OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
                {
                    // TODO: Implement this method
                    File mainFile = new File(p1.getItemAtPosition(p3).toString());
                    if (p3 == 0) {
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
        
        listFileAt(Environment.getExternalStorageDirectory().getAbsolutePath());
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
                        //fileAdapter.add(new File(path, "Path=\""+path+"\".noEquals(homePath=\""+homePath+"\")"));
                        fileAdapter.add(new File(path, ".."));
                    }

                    if(listFile.length != 0){
                        Arrays.sort(listFile, new SortFileName());
                        for(File file : listFile){
                            fileAdapter.add(file);
                        }
                    }
                    mainLv.setAdapter(fileAdapter);
                    build.setTitle(new File(path).getName());
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
