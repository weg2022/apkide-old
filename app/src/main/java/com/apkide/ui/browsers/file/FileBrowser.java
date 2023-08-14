package com.apkide.ui.browsers.file;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.apkide.common.AppLog;
import com.apkide.common.EntryListAdapter;
import com.apkide.common.FileSystem;
import com.apkide.common.MessageBox;
import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.browsers.HeaderBrowserLayout;
import com.apkide.ui.databinding.BrowserFileBinding;
import com.apkide.ui.dialogs.DeleteFileDialog;
import com.apkide.ui.dialogs.NewFileDialog;

import java.util.ArrayList;
import java.util.List;

public class FileBrowser extends HeaderBrowserLayout implements FileBrowserService.FileBrowserServiceListener, EntryListAdapter.OnEntryClickListener, EntryListAdapter.OnEntryLongPressListener {

	private final BrowserFileBinding myBinding;
	private final FileBrowserAdapter myAdapter;

	public FileBrowser(@NonNull Context context) {
		super(context);
		myBinding = BrowserFileBinding.inflate(LayoutInflater.from(getContext()), this, false);
		addView(myBinding.getRoot());

		getHeaderIcon().setImageResource(R.drawable.folder);
		getHeaderLabel().setText(R.string.browser_label_file);
		getHeaderHelp().setOnClickListener(this::showOptions);

		myAdapter = new FileBrowserAdapter();
		myAdapter.setClickListener(this);
		myAdapter.setLongPressListener(this);
		myBinding.fileListView.setLayoutManager(new LinearLayoutManager(getContext()));
		myBinding.fileListView.setNestedScrollingEnabled(false);
		myBinding.fileListView.setAdapter(myAdapter);
		App.getFileBrowserService().setListener(this);
		App.getFileBrowserService().sync();
	}

	@NonNull
	@Override
	public String getBrowserName() {
		return "FileBrowser";
	}

	@Override
	public void fileBrowserFolderChanged(@NonNull String folderPath) {
		long startTime = System.currentTimeMillis();
		List<String> files = FileSystem.getChildEntries(folderPath);

		List<EntryListAdapter.Entry> entities = new ArrayList<>();

		if (!folderPath.equals(App.getFileBrowserService().getDefaultFolder())) {
			String parent = FileSystem.getParentDirPath(folderPath);
			if (parent == null || !FileSystem.exists(parent))
				parent = App.getFileBrowserService().getDefaultFolder();

			entities.add(new FileEntry(parent));
		}

		getHeaderLabel().setText(folderPath);

		for (String filePath : files) {
			String name = FileSystem.getName(filePath);
			boolean isDir = FileSystem.isDirectory(filePath);
			entities.add(new FileEntry(filePath, name, isDir));
		}
		entities.sort((o1, o2) -> {
			if (o1 instanceof FileEntry && o2 instanceof FileEntry) {
				return ((FileEntry) o1).compareTo((FileEntry) o2);
			}
			return 0;
		});

		myAdapter.updateEntries(entities);
		long endTime = System.currentTimeMillis();
		AppLog.s(this, "Loading files done in " + (endTime - startTime) + " ms");
	}

	@Override
	public void onEntryClicked(EntryListAdapter.Entry entry, View view, int position) {
		if (entry instanceof FileEntry) {
			if (((FileEntry) entry).isPrev()) {
				App.getFileBrowserService().openFolder(((FileEntry) entry).getFilePath());
			} else if (((FileEntry) entry).isDirectory()) {
				showOpenFolder((FileEntry) entry, view);

			} else if (((FileEntry) entry).isFile()) {

			}
		}
	}

	private void showOpenFolder(FileEntry entry, View view) {
		if (App.getProjectService().isSupportedProject(entry.getFilePath())) {
			MessageBox.showInfo(App.getMainUI(),
					"Open Project",
					"The current directory is a project, do you want to open it ?",
					false,
					"Open Project", () -> {
						if (App.getProjectService().openProject(entry.getFilePath())) {
							//Do action
						}
						App.getFileBrowserService().openFolder(entry.getFilePath());
					}, "Open Folder", () -> App.getFileBrowserService().openFolder(entry.getFilePath()));
		} else {
			App.getFileBrowserService().openFolder(entry.getFilePath());
		}
	}

	@Override
	public boolean onEntryLongPressed(EntryListAdapter.Entry entry, View view, int position) {
		if (entry instanceof FileEntry) {
			if (((FileEntry) entry).isPrev()) {
				//Ignore
				return true;
			} else if (((FileEntry) entry).isDirectory()) {
				showEntryOptions((FileEntry) entry, view);
				return true;
			} else if (((FileEntry) entry).isFile()) {
				showEntryOptions((FileEntry) entry, view);
				return true;
			}
		}
		return false;
	}

	private void showOptions(View v) {
		PopupMenu popupMenu = new PopupMenu(getContext(), v);
		popupMenu.inflate(R.menu.filebrowser_options);
		popupMenu.setOnMenuItemClickListener(item -> {
			if (item.getItemId() == R.id.fileBrowserCommandSyncWithDisk) {
				App.getFileBrowserService().sync();
				return true;
			}

			if (item.getItemId() == R.id.fileBrowserCommandShowHome) {
				App.getFileBrowserService().openFolder(App.getFileBrowserService().getDefaultFolder());
				return true;
			}

			if (item.getItemId() == R.id.fileBrowserCommandShowCurrent) {

				return true;
			}

			if (item.getItemId() == R.id.fileBrowserCommandNewFile) {
				String rootPath = App.getFileBrowserService().getCurrentFolder();
				MessageBox.showDialog(App.getMainUI(), new NewFileDialog(rootPath, false));
				return true;
			}

			if (item.getItemId() == R.id.fileBrowserCommandNewFolder) {
				String rootPath = App.getFileBrowserService().getCurrentFolder();
				MessageBox.showDialog(App.getMainUI(), new NewFileDialog(rootPath, true));
				return true;
			}

			return false;
		});
		popupMenu.show();
	}

	private void showEntryOptions(FileEntry entry, View v) {
		PopupMenu popupMenu = new PopupMenu(getContext(), v);
		popupMenu.inflate(R.menu.filebrowser_entry_options);
		popupMenu.setOnMenuItemClickListener(item -> {
			if (item.getItemId() == R.id.fileBrowserCommandSyncWithDisk) {
				App.getFileBrowserService().sync();
				return true;
			}
			if (item.getItemId() == R.id.fileBrowserCommandCopyPath) {

			}

			if (item.getItemId() == R.id.fileBrowserCommandCopyName) {

			}

			if (item.getItemId() == R.id.fileBrowserCommandDelete) {
				MessageBox.showDialog(App.getMainUI(), new DeleteFileDialog(entry.getFilePath()));
				return true;
			}
			return false;
		});
		popupMenu.show();
	}
}
