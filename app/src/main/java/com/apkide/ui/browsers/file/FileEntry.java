package com.apkide.ui.browsers.file;

import static com.apkide.common.FileSystem.getEnclosingDir;
import static com.apkide.common.FileSystem.getEnclosingDirPrefix;
import static com.apkide.common.FileSystem.getName;

import androidx.annotation.NonNull;

import com.apkide.common.EntryListAdapter;
import com.apkide.ui.R;

public class FileEntry implements EntryListAdapter.Entry, Comparable<FileEntry> {

	private static final long serialVersionUID = -4421092652929488640L;
	private static final String PRV = "...";
	private final String filePath;
	private final String label;
	private final int icon;
	private final boolean isPrev;
	private final boolean isDirectory;
	private final boolean isHidden;

	public FileEntry(@NonNull String filePath) {
		this.filePath = filePath;
		this.label = PRV;
		this.icon = R.drawable.folder_open;
		this.isPrev = true;
		this.isDirectory = false;
		this.isHidden = false;
	}

	public FileEntry(@NonNull String filePath, @NonNull String label, boolean isDirectory) {
		this.filePath = filePath;
		this.label = label;
		this.isDirectory = isDirectory;
		this.isPrev = false;
		if (isDirectory) {
			this.isHidden = isHidden(filePath);
			icon = isHidden ? R.drawable.folder_hidden : R.drawable.folder;
		} else {
			this.isHidden = false;
			icon = FileIcons.getIcon(filePath);
		}
	}

	public static boolean isHidden(String filePath) {
		String name = getName(filePath);
		if (name.startsWith(".") || name.equals("build") || name.equals("bin")) {
			return true;
		}
		if (getEnclosingDirPrefix(filePath, ".") != null ||
				getEnclosingDir(filePath, "build") != null ||
				getEnclosingDir(filePath, "bin") != null) {
			return true;
		}
		return false;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public int getIcon() {
		return icon;
	}

	@NonNull
	public String getLabel() {
		return label;
	}

	@NonNull
	public String getFilePath() {
		return filePath;
	}

	public boolean isPrev() {
		return isPrev;
	}

	public boolean isDirectory() {
		if (isPrev) {
			return false;
		}
		return isDirectory;
	}

	public boolean isFile() {
		if (isPrev)
			return false;
		return !isDirectory;
	}


	@Override
	public int compareTo(FileEntry o) {
		if (isPrev() && !o.isPrev()) {
			return -1;
		}

		if (!isPrev() && o.isPrev()) {
			return 1;
		}

		if (isDirectory() && o.isFile()) {
			return -1;
		}

		if (isFile() && o.isDirectory()) {
			return 1;
		}

		return getLabel().compareTo(o.getLabel());
	}
}
