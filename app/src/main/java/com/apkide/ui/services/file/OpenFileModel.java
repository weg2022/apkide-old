package com.apkide.ui.services.file;

import androidx.annotation.NonNull;

import java.io.InputStream;

public interface OpenFileModel {
	@NonNull
	String getName();
	
	@NonNull
	String getExtension();
	
	@NonNull
	byte[] getContents();
	
	@NonNull
	InputStream getInputStream();
	
	@NonNull
	String getEncoding();
	//boolean isExecutable();
	
	boolean isWritable();
	
	boolean isReadable();
	
	boolean isBinary();
	
	boolean exists();
	
	long getLastModified();
	
	long getSize();
}
