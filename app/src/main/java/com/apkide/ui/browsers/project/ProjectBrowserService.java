package com.apkide.ui.browsers.project;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.apkide.common.AppLog;
import com.apkide.common.FileSystem;
import com.apkide.ui.App;
import com.apkide.ui.services.AppService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProjectBrowserService implements AppService {
    
    public interface ProjectBrowserServiceListener {
        void projectFilesUpdate(@NonNull String visiblePath, @NonNull List<ProjectEntry> entries);
    }
    
    private final Object myLock = new Object();
    private boolean myShutdown;
    private boolean myRunning;
    private String myVisiblePath;
    private SharedPreferences myPreferences;
    private final List<ProjectEntry> myProjectEntries = new ArrayList<>();
    private ProjectBrowserServiceListener myListener;
    
    @Override
    public void initialize() {
        Thread thread = new Thread(null, () -> {
            try {
                synchronized (myLock) {
                    while (!myShutdown) {
                        if (myRunning && myVisiblePath != null) {
                            myProjectEntries.clear();
                            if (App.getProjectService().isProjectOpened()) {
                                List<String> childList = FileSystem.getChildEntries(myVisiblePath);
                                
                                String parent = FileSystem.getParentPath(myVisiblePath);
                                
                                if (parent != null && new File(parent).exists()) {
                                    if (!myVisiblePath.equals(App.getProjectService().getProjectRootPath()))
                                        myProjectEntries.add(new ProjectEntry(parent));
                                }
                                
                                for (String path : childList) {
                                    String name = FileSystem.getName(path);
                                    boolean isDir = FileSystem.isDirectory(path);
                                    myProjectEntries.add(new ProjectEntry(path, name, isDir));
                                }
                                
                                Collections.sort(myProjectEntries);
                            }
                            
                            if (myListener != null) {
                                String path = myVisiblePath;
                                App.postRun(() -> myListener.projectFilesUpdate(path, new ArrayList<>(myProjectEntries)));
                            }
                            
                            myRunning = false;
                        }
                        myLock.wait();
                    }
                }
            } catch (InterruptedException e) {
                AppLog.e(e);
            }
        }, "ProjectBrowserService");
        thread.start();
        
        myVisiblePath = getPreferences().getString("visible.path", null);
    }
    
    public void setListener(ProjectBrowserServiceListener listener) {
        myListener = listener;
    }
    
    
    public void openProject(@NonNull String filePath) {
        
        getPreferences().edit().putString("visible.path", filePath).apply();
        synchronized (myLock) {
            myVisiblePath = filePath;
            myRunning = true;
            myLock.notify();
        }
    }
    
    public void sync() {
        if (myVisiblePath == null) {
            if (App.getProjectService().isProjectOpened()) {
                myVisiblePath = App.getProjectService().getProjectRootPath();
                getPreferences().edit().putString("visible.path", myVisiblePath).apply();
            }
        }
        
        synchronized (myLock) {
            myRunning = true;
            myLock.notify();
        }
    }
    
    public String getVisiblePath() {
        return myVisiblePath;
    }
    
    
    public void closeProject() {
        getPreferences().edit().putString("visible.path", null).apply();
    }
    
    private SharedPreferences getPreferences() {
        if (myPreferences == null)
            myPreferences = App.getPreferences("ProjectBrowserService");
        return myPreferences;
    }
    
    @Override
    public void shutdown() {
        synchronized (myLock) {
            myShutdown = true;
            myLock.notify();
        }
    }
    
}
