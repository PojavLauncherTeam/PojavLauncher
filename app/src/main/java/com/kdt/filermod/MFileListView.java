package com.kdt.filermod;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.*;

public class MFileListView extends LinearLayout
{
	//For list view:
	private String fullPath;
	private ListView mainLv;
	private Context context;
	private boolean lockedHome = false;
	
	//For File selected listener:
	private MFileSelectedListener listener;
	private AlertDialog build;
	private String homePath;
	
	public MFileListView(Context context, AlertDialog build)
	{
		super(context);
		init(context);
		this.build = build;
	}
	public MFileListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}
	public MFileListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}
	public void init(final Context context)
	{
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
					String name = mainFile.getName();
					//Toast.makeText(MainActivity.this, name, Toast.LENGTH_LONG).show();
					if(name.equals("..           ")){
						parentDir();
					}
					else{
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
					String name = mainFile.getName();
					//Toast.makeText(MainActivity.this, name, Toast.LENGTH_LONG).show();
					if(mainFile.isFile()){
						String extension = getExtension(mainFile.getAbsolutePath());
						listener.onFileLongClick(mainFile, mainFile.getAbsolutePath(), name, extension);
						return true;
					}
					return false;
				}
			});
		addView(mainLv, layParam);
	}
	public void setFileSelectedListener(MFileSelectedListener listener)
	{
		this.listener = listener;
	}
	public void listFileAt(final String path)
	{
		try{
			final File mainPath = new File(path);
			if(mainPath.exists()){
				if(mainPath.isDirectory()){
					if(!lockedHome){
						homePath = path;
						lockedHome = true;
					}
					fullPath = path;
					
					File[] listFile = mainPath.listFiles();
					MFileListAdapter fileAdapter = new MFileListAdapter(context);
					if(!path.equals(homePath)){
						//fileAdapter.add(new File(path, "Path=\""+path+"\".noEquals(homePath=\""+homePath+"\")"));
						fileAdapter.add(new File(path, "..           "));
					}
					
					if(listFile.length != 0){
						Arrays.sort(listFile, new MSortFileName());
						for(File file : listFile){
							fileAdapter.add(file);
						}
					}
					mainLv.setAdapter(fileAdapter);
					build.setTitle(new File(path).getName());
				}
				else{
					String name = mainPath.getName();
					String extension = getExtension(path);
					listener.onFileSelected(mainPath, path, name, extension);
				}
			}
			else{
				Toast.makeText(context, "This folder (or file) doesn't exist", Toast.LENGTH_SHORT).show();
				refreshPath();
			}
		}
		catch(Exception e){
			/*
			new AlertDialog.Builder(context)
				.setTitle("Error")
				.setMessage(Log.getStackTraceString(e))
				.setPositiveButton("OK", null)
				.show();
			*/
		}
	}
	public static String getExtension(String path)
	{
		return getExtension(new File(path));
	}
	public static String getExtension(File file)
	{
		return file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
	}
	public String getFullPath()
	{
		return fullPath;
	}
	public void refreshPath()
	{
		listFileAt(getFullPath());
	}
	public void parentDir()
	{
		File pathFile = new File(fullPath);
		if(!pathFile.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())){
			listFileAt(pathFile.getParent());
		}
	}
}
