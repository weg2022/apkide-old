package com.apkide.ui.browsers.file;

import static com.apkide.ui.browsers.file.FileBrowserService.FileBrowserServiceListener;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.browsers.HeaderBrowserLayout;
import com.apkide.ui.databinding.BrowserFileBinding;
import com.apkide.ui.util.Entry;
import com.apkide.ui.util.ListAdapter;

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
    public void filesUpdate(@NonNull String visiblePath,@NonNull List<FileEntry> entries) {
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
            
            
            return true;
        }
        return false;
    }
}
