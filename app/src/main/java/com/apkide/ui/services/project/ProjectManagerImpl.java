package com.apkide.ui.services.project;

import static com.apkide.common.FileSystem.getEnclosingParent;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.FileSystem;
import com.apkide.ui.App;
import com.apkide.ui.R;

import java.io.File;
import java.io.IOException;

public class ProjectManagerImpl implements ProjectManager {
	public static final String APKTOOL_YML = "apktool.yml";
	public static final String ANDROID_MANIFEST="AndroidManifest.xml";
    private String myRootPath;
    private ConfigureFileImpl myConfigureFile;
    
    @NonNull
    @Override
    public String getName() {
        return "APK-Tool";
    }
    
    @NonNull
    @Override
    public String[] getSupportedLanguages() {
        return new String[]{"Smali", "Java", "XML"};
    }
    
    @Override
    public boolean checkIsSupportedProjectRootPath(@NonNull String rootPath) {
        return new File(rootPath, APKTOOL_YML).exists() &&
                new File(rootPath, ANDROID_MANIFEST).exists();
    }
    
    @Override
    public boolean checkIsSupportedProjectPath(@NonNull String path) {
        String workspace = getEnclosingParent(path, s -> {
            return new File(s, APKTOOL_YML).exists() &&
                    new File(s, ANDROID_MANIFEST).exists();
        });
        return workspace != null;
    }
    
    @Override
    public boolean isProjectFilePath(@NonNull String path) {
        if (isOpen()) {
            return getEnclosingParent(path, s -> s.equals(myRootPath)) != null;
        }
        return false;
    }
    
    @Override
    public void open(@NonNull String rootPath) throws IOException {
        String workspace;
        if (checkIsSupportedProjectRootPath(rootPath)) {
            workspace = rootPath;
        } else {
            workspace = getEnclosingParent(rootPath, s -> {
                return new File(s, APKTOOL_YML).exists() &&
                        new File(s, ANDROID_MANIFEST).exists();
            });
        }
        if (workspace == null) {
            throw new IOException("rootPath is null");
        }
        myRootPath = workspace;
        myConfigureFile = new ConfigureFileImpl(myRootPath + File.separator + APKTOOL_YML);
    }
    
    @Override
    public void close() {
        
        myRootPath = null;
        if (myConfigureFile != null)
            myConfigureFile.destroy();
    }
    
    @Override
    public void sync() {
        try {
            myConfigureFile.sync();
        } catch (ConfigureFileException e) {
            Toast.makeText(App.getUI(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public boolean isOpen() {
        return myRootPath != null;
    }
    
    @Nullable
    @Override
    public String getRootPath() {
        return myRootPath;
    }
    
    
    @Nullable
    @Override
    public String resolvePath(@NonNull String path) {
        if (!isOpen()) return null;
        return FileSystem.resolvePath(myRootPath, path);
    }
    
    @Override
    public int getIcon() {
        return R.drawable.project_properties;
    }
}
