package com.kdt.filerapi;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.annotation.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.io.*;
import java.util.*;

public class FileListView extends LinearLayout
{
	//For list view:
	private String fullPath;
	private TextView mainTv;
	private ListView mainLv;
	private Context context;
	
	//For message empty:
	private WindowManager emptyWm;
	private TextView emptyTv;
	
	//For File selected listener:
	private FileSelectedListener listener;
	
	public FileListView(Context context)
	{
		super(context);
		init(context);
	}
	public FileListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}
	public FileListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}
	public void init(final Context context)
	{
		//Empty message setup:
		WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.gravity = Gravity.CENTER;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		
		emptyWm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
		
		emptyTv = new TextView(context);
		emptyTv.setText("This folder is empty");
		
		emptyWm.addView(emptyTv, mParams);
		
		//Main setup:
		this.context = context;
		
		LayoutParams layParam = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		setOrientation(VERTICAL);
		
		mainTv = new TextView(context);
		mainTv.setText("Path: null");
		
		mainLv = new ListView(context);
		
		listFileAt(Environment.getExternalStorageDirectory().getPath());
		
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
		
		addView(mainTv, layParam);
		addView(mainLv, layParam);
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
					fileAdapter.add(new File(path, "..           "));

					if(listFile.length != 0){
						Arrays.sort(listFile, new SortFileName());
						for(File file : listFile){
							fileAdapter.add(file);
						}
						emptyTv.setVisibility(View.GONE);
					}
					else{
						emptyTv.setVisibility(View.VISIBLE);
					}
					mainLv.setAdapter(fileAdapter);
					mainTv.setText("Path: " + path);
				}
				else{
					String name = mainPath.getName();
					// String extension = getExtension(path);
					listener.onFileSelected(mainPath, path, name);
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
