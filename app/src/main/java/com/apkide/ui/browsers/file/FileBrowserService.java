package com.apkide.ui.browsers.file;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.apkide.common.AppLog;
import com.apkide.common.Application;
import com.apkide.common.FileSystem;
import com.apkide.ui.App;
import com.apkide.ui.services.AppService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileBrowserService implements AppService {
    
    public interface FileBrowserServiceListener {
        
        void filesUpdate(@NonNull String visiblePath, @NonNull List<FileEntry> entries);
    }
    
    private SharedPreferences myPreferences;
    
    private FileBrowserServiceListener myListener;
    
    private final List<FileEntry> myFileEntries = new ArrayList<>();
    private final Object myLock = new Object();
    private boolean myShutdown;
    private String myVisiblePath;
    private boolean myHiddenVisible;
    private boolean myRunning;
    
    @Override
    public void initialize() {
        Thread thread = new Thread(null, () -> {
            try {
                synchronized (myLock) {
                    while (!myShutdown) {
                        if (myRunning && myVisiblePath != null) {
                            myFileEntries.clear();
                            
                            List<String> childList = FileSystem.getChildEntries(myVisiblePath);
                            
                            String parent = FileSystem.getParentPath(myVisiblePath);
                            
                            if (parent != null && new File(parent).exists())
                                myFileEntries.add(new FileEntry(parent));
                            
                            if (!new File(myVisiblePath).exists() && parent != null)
                                myFileEntries.add(new FileEntry(parent));
                            
                            for (String path : childList) {
                                String name = FileSystem.getName(path);
                                boolean isDir = FileSystem.isDirectory(path);
                                FileEntry fileEntry = new FileEntry(path, name, isDir);
                                if (!myHiddenVisible && fileEntry.isHidden()) {
                                    //...
                                } else {
                                    myFileEntries.add(new FileEntry(path, name, isDir));
                                }
                            }
                            
                            Collections.sort(myFileEntries);
                            
                            if (myListener != null) {
                                App.postRun(() -> myListener.filesUpdate(myVisiblePath, new ArrayList<>(myFileEntries)));
                            }
                            
                            
                            myRunning = false;
                        }
                        myLock.wait();
                    }
                }
            } catch (InterruptedException e) {
                AppLog.e(e);
            }
        }, "FileBrowserService");
        thread.start();
        
        myHiddenVisible = getPreferences().getBoolean("hidden.visible", false);
        myVisiblePath = getLastRootPath();
    }
    
    @Override
    public void shutdown() {
        synchronized (myLock) {
            myShutdown = true;
            myLock.notify();
        }
    }
    
    public void setListener(FileBrowserServiceListener listener) {
        myListener = listener;
    }
    
    private String getLastRootPath() {
        String rootPath = getPreferences().getString("open.rootPath", null);
        if (rootPath != null) {
            File file = new File(rootPath);
            if (file.exists() && file.isDirectory()) {
                return rootPath;
            }
        }
        rootPath = Application.get().getExternalDir().getAbsolutePath();
        return rootPath;
    }
    
    public void open(@NonNull String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            return;
        }
        getPreferences().edit().putString("open.rootPath", path).apply();
        synchronized (myLock) {
            myVisiblePath = path;
            myRunning = true;
            myLock.notify();
        }
    }
    
    public void setHiddenVisible(boolean visible) {
        if (myHiddenVisible == visible) {
            return;
        }
        getPreferences().edit().putBoolean("hidden.visible", visible).apply();
        synchronized (myLock) {
            myRunning = true;
            myHiddenVisible = visible;
            myLock.notify();
        }
    }
    
    public boolean isHiddenVisible() {
        return myHiddenVisible;
    }
    
    public void sync() {
        synchronized (myLock) {
            myRunning = true;
            myLock.notify();
        }
    }
    
    public String getVisiblePath() {
        return myVisiblePath;
    }
    
    @NonNull
    protected SharedPreferences getPreferences() {
        if (myPreferences == null)
            myPreferences = App.getPreferences("FileBrowserService");
        return myPreferences;
    }
}
