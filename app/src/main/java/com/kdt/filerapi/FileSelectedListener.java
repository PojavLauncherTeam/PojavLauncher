package com.kdt.filerapi;

import java.io.File;

public interface FileSelectedListener
{
	public void onFileSelected(File file, String path, String nane, String extension);
}
