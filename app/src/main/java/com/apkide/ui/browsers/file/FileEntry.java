package com.apkide.ui.browsers.file;


import com.apkide.common.EntryListAdapter;
import com.apkide.common.FileSystem;
import com.apkide.ui.App;
import com.apkide.ui.AppCommands;
import com.apkide.ui.R;

public class FileEntry implements EntryListAdapter.Entry {

	private String filePath;
	private String label;
	private int icon;
	private boolean isFile;
	private AppCommands.BrowserCommand command;

	public FileEntry(String filePath, String label, boolean isFile) {
		this.filePath = filePath;
		this.label = label;
		if (isFile) {
			this.icon = FileIcons.getIcon(filePath);
		} else if (!isBackEntry()) {
			if (isHidden(filePath)) {
				this.icon = R.mipmap.folder_hidden;
			} else {
				this.icon = R.mipmap.folder;
			}
		} else {
			this.icon = R.mipmap.folder_open;
		}
		this.isFile = isFile;
	}

	public FileEntry(AppCommands.BrowserCommand command) {
		this.command = command;

		icon = command.getIcon();
		int label = command.getLabel();
		if (label != 0) {
			this.label = App.getContext().getResources().getString(label);
		}
	}

	public static boolean isHidden(String filePath) {
		String name = FileSystem.getName(filePath);
		return name.startsWith(".") ||
				name.startsWith("bin") ||
				name.startsWith(".obj") ||
				name.startsWith("build") ||
				name.startsWith("gradle");
	}

	public boolean isBackEntry() {
		if (isFile) return false;
		return label.equals("...");
	}

	public boolean isDir() {
		if (isFile) return false;
		return !isBackEntry();
	}

	public boolean isFile() {
		return isFile;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getLabel() {
		return label;
	}

	public int getIcon() {
		return icon;
	}

	public AppCommands.BrowserCommand getCommand() {
		return command;
	}
}
