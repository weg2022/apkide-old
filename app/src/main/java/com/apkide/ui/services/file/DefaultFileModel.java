package com.apkide.ui.services.file;

import androidx.annotation.NonNull;

import com.apkide.common.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class DefaultFileModel implements OpenFileModel {
	private final String filePath;
	
	public DefaultFileModel(String filePath) {
		this.filePath = filePath;
	}
	
	
	@NonNull
	@Override
	public String getName() {
		int index = filePath.lastIndexOf(File.separator);
		if (index >= 0)
			return filePath.substring(index + 1);
		return filePath;
	}
	
	@NonNull
	@Override
	public String getExtension() {
		int index = filePath.lastIndexOf('.');
		if (index >= 0)
			return filePath.substring(index + 1);
		return filePath;
	}
	
	@NonNull
	@Override
	public byte[] getContents() {
		try {
			return IOUtils.readBytes(getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@NonNull
	@Override
	public InputStream getInputStream() {
		try {
			return new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	@NonNull
	@Override
	public String getEncoding() {
		return "UTF-8";//Ignore encoding
	}
	
	@Override
	public boolean isWritable() {
		return new File(filePath).canWrite();
	}
	
	@Override
	public boolean isReadable() {
		return new File(filePath).canRead();
	}
	
	@Override
	public boolean isBinary() {
		return false;
	}
	
	@Override
	public boolean exists() {
		File file = new File(filePath);
		return file.exists() && (!file.isDirectory());
	}
	
	@Override
	public long getLastModified() {
		return new File(filePath).lastModified();
	}
	
	@Override
	public long getSize() {
		return new File(filePath).length();
	}
}
