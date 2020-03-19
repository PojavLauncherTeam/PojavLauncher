package com.kdt.filermod;

import java.io.File;

public interface MFileSelectedListener
{
	public void onFileSelected(File file, String path, String nane, String extension);
	public void onFileLongClick(File file, String path, String nane, String extension);
}
