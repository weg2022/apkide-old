package com.apkide.ui.browsers.file;

import static android.view.LayoutInflater.from;
import static com.apkide.ui.browsers.file.FileBrowserService.FileBrowserServiceListener;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.apkide.common.EntryListAdapter;
import com.apkide.common.FileSystem;
import com.apkide.common.app.AppLog;
import com.apkide.ui.App;
import com.apkide.ui.AppCommands;
import com.apkide.ui.R;
import com.apkide.ui.browsers.BrowserLayout;
import com.apkide.ui.databinding.BrowserFileBinding;

import java.util.ArrayList;
import java.util.List;

public class FileBrowser extends BrowserLayout implements
		FileBrowserServiceListener,
		EntryListAdapter.OnEntryClickListener,
		EntryListAdapter.OnEntryLongPressListener {

	private BrowserFileBinding myBinding;
	private EntryListAdapter myListAdapter;

	public FileBrowser(Context context) {
		super(context);
		LayoutInflater inflater = from(getContext());
		myBinding = BrowserFileBinding.inflate(inflater, this, false);
		addView(myBinding.getRoot(),new ViewGroup.LayoutParams(-1,-1));
		getHeader().browserHeaderLabel.setText("Files");
		getHeader().browserHeaderIcon.setImageResource(R.mipmap.folder);
		getHeader().browserHeaderHelp.setOnClickListener(v -> {

		});
		myListAdapter = new FileEntryAdapter();
		myListAdapter.setClickListener(this);
		myListAdapter.setLongPressListener(this);
		myBinding.fileListView.setLayoutManager(new LinearLayoutManager(getContext()));
		myBinding.fileListView.setNestedScrollingEnabled(false);
		myBinding.fileListView.setAdapter(myListAdapter);
		App.getFileBrowserService().setFileBrowserServiceListener(this);
		syncFormDisk();
	}

	@NonNull
	@Override
	public String getPreferencesName() {
		return "FileBrowser";
	}

	@Override
	public void folderOpened(String folderPath) {
		AppLog.d(folderPath);
		syncFormDisk();
	}

	public void syncFormDisk() {
		App.getMainUI().runOnStorage(() -> {

			String dir = App.getFileBrowserService().getFolderPath();

			getHeader().browserHeaderLabel.setText(dir);
			List<FileEntry> entries = new ArrayList<>();
			String parentDir = FileSystem.getParentDirPath(dir);
			if (parentDir != null) {
				if (!parentDir.endsWith(App.getFileBrowserService().getDefaultFolderPath()))
					entries.add(new FileEntry(parentDir, "...", false));
			}

			for (AppCommands.BrowserCommand command : AppCommands.getBrowserCommands()) {
				if (command.getVisible(false)) {
					entries.add(new FileEntry(command));
				}
			}

			List<String> files = FileSystem.getChildEntries(dir);
			files.sort((o1, o2) -> {
				boolean isDir = FileSystem.isDirectory(o1);
				boolean isDir2 = FileSystem.isDirectory(o2);
				String fileName = FileSystem.getName(o1);
				String fileName2 = FileSystem.getName(o2);
				if (isDir && !isDir2) {
					return -1;
				}

				if (isDir || !isDir2) {
					if (isDir) {
						boolean isHidden = FileEntry.isHidden(o1);
						boolean isHidden2 = FileEntry.isHidden(o2);
						if (isHidden && !isHidden2) return -1;
						return fileName.compareTo(fileName2);
					}
					return fileName.compareTo(fileName2);
				}
				return 1;

			});

			for (String file : files) {
				entries.add(new FileEntry(file, FileSystem.getName(file), FileSystem.isFile(file)));
			}
			myListAdapter.updateEntries(entries);
		});
	}

	@Override
	public void onEntryClicked(EntryListAdapter.Entry entry, View view, int position) {
		if (entry == null) return;

		if (entry instanceof FileEntry) {
			FileEntry fileEntry = (FileEntry) entry;
			if (fileEntry.getCommand() != null) {
				if (fileEntry.getCommand().getVisible(false)) {
					fileEntry.getCommand().run();
				}
			} else if (fileEntry.isFile()) {
				String filePath = fileEntry.getFilePath();
				App.getOpenFileService().openFile(filePath,true);
					//App.getMainUI().openFile(filePath);
			} else if (fileEntry.isBackEntry()) {
				String dir = fileEntry.getFilePath();
				if (dir != null)
					App.getFileBrowserService().folderOpen(dir);

			} else {
				String path = fileEntry.getFilePath();
				if (path != null) {

					App.getFileBrowserService().folderOpen(path);
				}
			}
		}
	}

	@Override
	public boolean onEntryLongPressed(EntryListAdapter.Entry entry, View view, int position) {
		return false;
	}
}
