package com.apkide.ui.services.project.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class ConfigureFile implements Serializable {

	private static final long serialVersionUID = -7884062718817046201L;

	private static final Map<String, CacheEntry> sCacheMap = new HashMap<>(5);

	private final String filePath;

	public ConfigureFile(@NonNull String filePath) {
		this.filePath = filePath;
	}

	@NonNull
	public String getFilePath() {
		return filePath;
	}

	@Nullable
	public static <T extends ConfigureFile>T getFile(@NonNull String filePath){
		if (sCacheMap.containsKey(filePath)){
			CacheEntry entry=sCacheMap.get(filePath);
			if (entry!=null && entry.isInvalid()){
				return (T) entry.file;
			}
		}
		return null;
	}

	public void sync() throws ConfigureFileException {
		File file = new File(getFilePath());
		if (!file.exists())
			throw new ConfigureFileException(getFilePath() + " file not exists.");
		if (file.isDirectory())
			throw new ConfigureFileException(getFilePath() + " file is directory.");

		if (sCacheMap.containsKey(getFilePath())) {
			CacheEntry entry = sCacheMap.get(getFilePath());
			if (entry != null && entry.isInvalid()) {
				if (file.lastModified() != entry.lastModified) {
					entry.file.onSync();
					entry.lastModified = file.lastModified();
				}
				return;
			}
		}
		onSync();
		sCacheMap.put(getFilePath(),
				new CacheEntry(this, new File(getFilePath()).lastModified()));
	}

	public void destroy() {
		if (sCacheMap.containsKey(getFilePath())) {
			CacheEntry entry = sCacheMap.get(getFilePath());
			if (entry != null && entry.isInvalid()) {
				entry.file.onDestroy();
				entry.lastModified = -1;
				entry.file = null;
				sCacheMap.remove(getFilePath());
				return;
			}
		}
		onDestroy();
		sCacheMap.remove(getFilePath());
	}

	protected abstract void onSync() throws ConfigureFileException;

	protected abstract void onDestroy();

	private static class CacheEntry {
		public ConfigureFile file;
		public long lastModified;

		public CacheEntry() {
		}

		public CacheEntry(ConfigureFile configureFile, long lastModified) {
			this.file = configureFile;
			this.lastModified = lastModified;
		}

		public boolean isInvalid() {
			return lastModified != -1 && file != null;
		}
	}
}
