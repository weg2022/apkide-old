package com.apkide.ui.browsers.file;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.apkide.common.EntryListAdapter;
import com.apkide.common.FileSystem;
import com.apkide.common.MessageBox;
import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.browsers.HeaderBrowserLayout;
import com.apkide.ui.databinding.BrowserFileBinding;
import com.apkide.ui.dialogs.DeleteFileDialog;
import com.apkide.ui.dialogs.NewFileDialog;
import com.apkide.ui.dialogs.RenameFileDialog;

import java.util.ArrayList;
import java.util.List;

public class FileBrowser extends HeaderBrowserLayout implements FileBrowserService.FileBrowserServiceListener, EntryListAdapter.OnEntryClickListener, EntryListAdapter.OnEntryLongPressListener {
    
    private final BrowserFileBinding myBinding;
    private final FileBrowserAdapter myAdapter;
    private final Thread myThread;
    private boolean myShutdown;
    private final Object myLock = new Object();
    private String myOpenFolder;
    
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
        
        myThread = new Thread(null, () -> {
            synchronized (myLock) {
                while (true) {
                    if (!myShutdown) {
                        if (myOpenFolder != null) {
                            List<String> files = FileSystem.getChildEntries(myOpenFolder);
                            
                            List<EntryListAdapter.Entry> entities = new ArrayList<>();
                            
                            if (!myOpenFolder.equals(App.getFileBrowserService().getDefaultFolder())) {
                                String parent = FileSystem.getParentDirPath(myOpenFolder);
                                if (parent == null || !FileSystem.exists(parent))
                                    parent = App.getFileBrowserService().getDefaultFolder();
                                
                                entities.add(new FileEntry(parent));
                            }
                            
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
                            
                            post(() -> {
                                myBinding.fileProgressBar.setVisibility(GONE);
                                String label = myOpenFolder;
                                if (FileSystem.isArchiveFile(label) || FileSystem.isArchiveDirectoryEntry(label)) {
                                    String pre = FileSystem.getEnclosingArchivePath(label);
                                    if (pre != null) {
                                        String dir = FileSystem.getParentDirPath(pre);
                                        if (dir != null) {
                                            String parentDir = FileSystem.getParentDirPath(dir);
                                            if (parentDir != null)
                                                label = "~" + label.substring(parentDir.length());
                                        }
                                    }
                                }
                                getHeaderLabel().setText(label);
                                myAdapter.updateEntries(entities);
                            });
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    myBinding.fileListScrollView.requestLayout();
                                    //     myBinding.fileListScrollView.invalidate();
                                    myBinding.fileListView.requestFocus();
                                }
                            }, 50L);
                        }
                        try {
                            myLock.wait();
                        } catch (InterruptedException ignored) {
                        
                        }
                    }
                }
            }
        }, getBrowserName());
        myThread.start();
        
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
        myBinding.fileProgressBar.setVisibility(VISIBLE);
        App.getMainUI().runOnStorage(() -> {
            synchronized (myLock) {
                myOpenFolder = folderPath;
                myLock.notify();
            }
        });
        
    }
    
    @Override
    public void onEntryClicked(EntryListAdapter.Entry entry, View view, int position) {
        if (entry instanceof FileEntry) {
            if (((FileEntry) entry).isPrev()) {
                App.getFileBrowserService().openFolder(((FileEntry) entry).getFilePath());
            } else if (((FileEntry) entry).isDirectory()) {
                showOpenFolder((FileEntry) entry, view);
            } else if (((FileEntry) entry).isFile()) {
                showOpenFile((FileEntry) entry, view);
            }
        }
    }
    
    private void showOpenFolder(FileEntry entry, View view) {
        if (App.getProjectService().checkIsSupportedProjectRootPath(entry.getFilePath())) {
            MessageBox.showInfo(App.getMainUI(),
                    "Open Project",
                    "This directory is a supported project,\n" +
                            " do I need to open this directory according to the project?",
                    false, getContext().getString(android.R.string.ok), () -> {
                        if (App.getProjectService().openProject(entry.getFilePath())) {
                            //Do action
                        }
                    }, getContext().getString(android.R.string.cancel), () -> {
                    
                    });
        }
        App.getFileBrowserService().openFolder(entry.getFilePath());
    }
    
    private void showOpenFile(FileEntry entry, View v) {
        String path = entry.getFilePath();
        if (FileSystem.isArchiveFile(path)) {
            App.getFileBrowserService().openFolder(path);
        } else {
            App.getOpenFileService().openFile(entry.getFilePath());
            if (App.getProjectService().checkIsSupportedProjectPath(entry.getFilePath())) {
                MessageBox.showInfo(App.getMainUI(),
                        "Open Project",
                        "The current file is in the project file, open the project?",
                        false, getContext().getString(android.R.string.ok), () -> {
                            String parent = FileSystem.getParentDirPath(entry.getFilePath());
                            if (parent != null && App.getProjectService().openProject(parent)) {
                                //Do action
                            }
                        }, getContext().getString(android.R.string.cancel), () -> {
                        
                        });
            }
        }
    }
    
    @Override
    public boolean onEntryLongPressed(EntryListAdapter.Entry entry, View view, int position) {
        if (entry instanceof FileEntry) {
            if (((FileEntry) entry).isPrev()) {
                //Ignore
                return true;
            } else {
                showEntryOptions((FileEntry) entry, view);
                return true;
            }
        }
        return false;
    }
    
    private void showOptions(View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.inflate(R.menu.filebrowser_options);
        String filePath = App.getFileBrowserService().getCurrentFolder();
        if (FileSystem.isArchiveFile(filePath) || FileSystem.isArchiveEntry(filePath)) {
            popupMenu.getMenu().findItem(R.id.fileBrowserCommandNew).setVisible(false);
            popupMenu.getMenu().findItem(R.id.fileBrowserCommandNewFile).setVisible(false);
            popupMenu.getMenu().findItem(R.id.fileBrowserCommandNewFolder).setVisible(false);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.fileBrowserCommandSyncWithDisk) {
                App.getFileBrowserService().sync();
                return true;
            }
            
            if (item.getItemId() == R.id.fileBrowserCommandShowHome) {
                App.getFileBrowserService().openFolder(
                        App.getFileBrowserService().getDefaultFolder());
                return true;
            }
            
            
            if (item.getItemId() == R.id.fileBrowserCommandNewFile) {
                String rootPath = App.getFileBrowserService().getCurrentFolder();
                MessageBox.showDialog(App.getMainUI(),
                        new NewFileDialog(rootPath, false));
                return true;
            }
            
            if (item.getItemId() == R.id.fileBrowserCommandNewFolder) {
                String rootPath = App.getFileBrowserService().getCurrentFolder();
                MessageBox.showDialog(App.getMainUI(),
                        new NewFileDialog(rootPath, true));
                return true;
            }
            
            return false;
        });
        popupMenu.show();
    }
    
    private void showEntryOptions(FileEntry entry, View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.inflate(R.menu.filebrowser_entry_options);
        
        if (FileSystem.isArchiveEntry(entry.getFilePath())) {
            popupMenu.getMenu().findItem(R.id.fileBrowserCommandRename).setVisible(false);
            popupMenu.getMenu().findItem(R.id.fileBrowserCommandDelete).setVisible(false);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.fileBrowserCommandSyncWithDisk) {
                App.getFileBrowserService().sync();
                return true;
            }
            if (item.getItemId() == R.id.fileBrowserCommandCopyPath) {
            
            }
            
            if (item.getItemId() == R.id.fileBrowserCommandCopyName) {
            
            }
            
            if (item.getItemId() == R.id.fileBrowserCommandRename) {
                MessageBox.showDialog(App.getMainUI(),
                        new RenameFileDialog(entry.getFilePath()));
                return true;
            }
            
            if (item.getItemId() == R.id.fileBrowserCommandDelete) {
                MessageBox.showDialog(App.getMainUI(),
                        new DeleteFileDialog(entry.getFilePath()));
                return true;
            }
            return false;
        });
        popupMenu.show();
    }
}
