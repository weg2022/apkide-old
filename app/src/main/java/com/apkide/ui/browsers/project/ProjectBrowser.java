package com.apkide.ui.browsers.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.apkide.common.FileSystem;
import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.browsers.HeaderBrowserLayout;
import com.apkide.ui.databinding.BrowserProjectBinding;
import com.apkide.ui.services.project.ProjectServiceListener;
import com.apkide.ui.util.Entry;
import com.apkide.ui.util.ListAdapter;

import java.util.List;

public class ProjectBrowser extends HeaderBrowserLayout implements
        ProjectBrowserService.ProjectBrowserServiceListener,
        ListAdapter.ListEntryClickListener,
        ListAdapter.ListEntryLongPressListener,
        ProjectServiceListener {
    
    private final BrowserProjectBinding myBinding;
    private final ProjectEntryListAdapter myAdapter = new ProjectEntryListAdapter();
    
    public ProjectBrowser(@NonNull Context context) {
        super(context);
        myBinding = BrowserProjectBinding.inflate(
                LayoutInflater.from(context), this, false);
        addView(myBinding.getRoot());
        
        getHeaderIcon().setImageResource(R.drawable.project_properties);
        getHeaderLabel().setText(R.string.browser_label_project);
        getHeaderHelp().setOnClickListener(v -> {
        
        });
        
        myAdapter.setClickListener(this);
        myAdapter.setLongPressListener(this);
        myBinding.projectFileListView.setAdapter(myAdapter);
        myBinding.projectFileListView.setLayoutManager(new LinearLayoutManager(context));
        App.getProjectBrowserService().setListener(this);
        App.getProjectService().addListener(this);
        App.getProjectBrowserService().sync();
        getHeaderLabel().setText(FileSystem.getName(App.getProjectService().getProjectRootPath()));
    }
    
    @NonNull
    @Override
    public String getBrowserName() {
        return "ProjectBrowser";
    }
    
    @Override
    public void projectOpened(@NonNull String rootPath) {
        getHeaderLabel().setText(FileSystem.getName(rootPath));
        App.getProjectBrowserService().openProject(rootPath);
    }
    
    @Override
    public void projectClosed(@NonNull String rootPath) {
        App.getProjectBrowserService().closeProject();
        getHeaderLabel().setText(R.string.browser_label_project);
        myAdapter.clear();
        myAdapter.sync();
    }
    
    @Override
    public void projectFilesUpdate(@NonNull String visiblePath, @NonNull List<ProjectEntry> entries) {
        myAdapter.update(entries);
        myAdapter.sync();
    }
    
    @Override
    public void listEntryClick(View view, Entry entry, int position) {
        if (entry == null) return;
        if (entry instanceof ProjectEntry) {
            ProjectEntry projectEntry = (ProjectEntry) entry;
            if (projectEntry.isPrev()) {
                App.getProjectBrowserService().openProject(projectEntry.getFilePath());
                return;
            }
            
            if (projectEntry.isDirectory()) {
                App.getProjectBrowserService().openProject(projectEntry.getFilePath());
                return;
            }
            
            if (projectEntry.isFile()) {
                App.getFileService().openFile(projectEntry.getFilePath());
                return;
            }
        }
    }
    
    @Override
    public boolean listEntryLongPress(View view, Entry entry, int position) {
        return false;
    }
}
