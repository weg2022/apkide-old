package com.apkide.ui.browsers.file;

import static com.apkide.ui.browsers.file.FileBrowserService.FileBrowserServiceListener;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.apkide.common.FileSystem;
import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.browsers.HeaderBrowserLayout;
import com.apkide.ui.databinding.BrowserFileBinding;
import com.apkide.ui.dialogs.DeleteFileDialog;
import com.apkide.ui.dialogs.NewFileDialog;
import com.apkide.ui.dialogs.RenameFileDialog;
import com.apkide.ui.util.Entry;
import com.apkide.ui.util.ListAdapter;
import com.apkide.ui.util.MessageBox;

import java.util.List;

public class FileBrowser extends HeaderBrowserLayout implements
        FileBrowserServiceListener,
        ListAdapter.ListEntryClickListener,
        ListAdapter.ListEntryLongPressListener {
    
    private final BrowserFileBinding myBinding;
    private final FileEntryListAdapter myAdapter = new FileEntryListAdapter();
    
    
    public FileBrowser(@NonNull Context context) {
        super(context);
        myBinding = BrowserFileBinding.inflate(
                LayoutInflater.from(getContext()), this, false);
        
        addView(myBinding.getRoot());
        
        getHeaderIcon().setImageResource(R.drawable.folder);
        getHeaderLabel().setText(R.string.browser_label_file);
        getHeaderHelp().setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(getContext(), v);
            menu.inflate(R.menu.browser_file_options);
            menu.getMenu().findItem(R.id.commandHiddenFileVisible).setVisible(!App.getFileBrowserService().isHiddenVisible());
            menu.getMenu().findItem(R.id.commandHiddenFileInVisible).setVisible(App.getFileBrowserService().isHiddenVisible());
            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.commandSyncWithDisk) {
                    App.getFileBrowserService().sync();
                    return true;
                } else if (item.getItemId() == R.id.commandHiddenFileVisible) {
                    App.getFileBrowserService().setHiddenVisible(true);
                    return true;
                } else if (item.getItemId() == R.id.commandHiddenFileInVisible) {
                    App.getFileBrowserService().setHiddenVisible(false);
                    return true;
                }else if (item.getItemId()==R.id.commandNewFile){
                    MessageBox.show(App.getMainUI(), new NewFileDialog(
                                    App.getFileBrowserService().getVisiblePath(),
                                    false));
                    return true;
                }else if (item.getItemId()==R.id.commandNewDir){
                    MessageBox.show(App.getMainUI(), new NewFileDialog(
                            App.getFileBrowserService().getVisiblePath(),
                            true));
                    return true;
                }
                return false;
            });
            menu.show();
        });
        
        myAdapter.setClickListener(this);
        myAdapter.setLongPressListener(this);
        myBinding.fileListView.setAdapter(myAdapter);
        myBinding.fileListView.setLayoutManager(new LinearLayoutManager(getContext()));
        App.getFileBrowserService().setListener(this);
        App.getFileBrowserService().sync();
    }
    
    @NonNull
    @Override
    public String getBrowserName() {
        return "FileBrowser";
    }
    
    @Override
    public void filesUpdate(@NonNull String visiblePath, @NonNull List<FileEntry> entries) {
        getHeaderLabel().setText(visiblePath);
        myAdapter.update(entries);
        myAdapter.sync();
    }
    
    @Override
    public void listEntryClick(View view, Entry entry, int position) {
        if (entry == null) return;
        if (entry instanceof FileEntry) {
            FileEntry fileEntry = (FileEntry) entry;
            if (fileEntry.isPrev()) {
                App.getFileBrowserService().open(fileEntry.getFilePath());
                return;
            }
            
            if (fileEntry.isDirectory()) {
                App.getFileBrowserService().open(fileEntry.getFilePath());
                return;
            }
            
            if (fileEntry.isFile()) {
                App.getFileService().openFile(fileEntry.getFilePath());
                return;
            }
            
        }
    }
    
    @Override
    public boolean listEntryLongPress(View view, Entry entry, int position) {
        if (entry == null) return false;
        if (entry instanceof FileEntry) {
            FileEntry fileEntry = (FileEntry) entry;
            if (fileEntry.isPrev()) {
                return false;
            }
            
            PopupMenu menu = new PopupMenu(getContext(), view);
            menu.inflate(R.menu.browser_file_entry_options);
            
            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.commandSyncWithDisk) {
                    App.getProjectBrowserService().sync();
                    
                    return true;
                } else if (item.getItemId() == R.id.commandCopyFilePath) {
                    ClipboardManager clipboardManager = (ClipboardManager) getContext().
                            getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboardManager.setPrimaryClip(
                            ClipData.newPlainText(null, fileEntry.getFilePath()));
                    return true;
                } else if (item.getItemId() == R.id.commandCopyFileName) {
                    String name = FileSystem.getName(fileEntry.getFilePath());
                    ClipboardManager clipboardManager = (ClipboardManager) getContext().
                            getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboardManager.setPrimaryClip(
                            ClipData.newPlainText(null, name));
                    
                    return true;
                } else if (item.getItemId() == R.id.commandRenameFile) {
                    MessageBox.show(App.getMainUI(), new RenameFileDialog(
                            App.getFileBrowserService().getVisiblePath()));
                    
                    return true;
                } else if (item.getItemId() == R.id.commandDeleteFile) {
    
                    MessageBox.show(App.getMainUI(), new DeleteFileDialog(
                            App.getFileBrowserService().getVisiblePath()));
                    return true;
                }
                
                return false;
            });
            menu.show();
            
            return true;
        }
        return false;
    }
    
}
