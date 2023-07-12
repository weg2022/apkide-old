package com.apkide.ui.services.project;

import static java.util.Objects.requireNonNull;

import android.content.SharedPreferences;
import android.widget.Toast;

import com.apkide.common.ApplicationProvider;
import com.apkide.ui.App;
import com.apkide.ui.FileSystem;
import com.apkide.ui.project.ApkToolProject;

public class ProjectService {

    private SharedPreferences myPreferences;
    private String myAndroidJar;
    private String myRootPath;

    public ProjectService() {

    }

    public void init(String filePath) {
        myAndroidJar = getPreferences().getString("android.sdk", null);
        if (myAndroidJar != null && !FileSystem.exists(myAndroidJar))
            myAndroidJar = null;

        if (myAndroidJar == null)
            myAndroidJar = ApplicationProvider.get().foundFile("android.jar").getAbsolutePath();

        if (filePath != null) {
            openProject(requireNonNull(getRootPath(filePath)));
        } else {
            myRootPath = getPreferences().getString("project.root", null);
            if (myRootPath != null && !isProjectRoot(myRootPath)) {
                myRootPath = null;
            }
        }

        if (myRootPath != null) {
            showOpenOrCloseTip();
        }
    }

    private void showOpenOrCloseTip() {
        if (myRootPath == null) {
            Toast.makeText(App.getContext(), "Opening project " + myRootPath, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(App.getContext(), "Closing project", Toast.LENGTH_SHORT).show();
        }
    }

    public void openProject(String rootPath) {
        if (rootPath.equals(getRootPath())) {
            Toast.makeText(App.getContext(), "Project " + rootPath + " is open", Toast.LENGTH_SHORT).show();
        } else {
            doOpenProject(rootPath);
        }
    }

    private void doOpenProject(String rootPath) {
        if (rootPath != null && !rootPath.equals(getRootPath())) {
            commitProjectRoot(rootPath);
            showOpenOrCloseTip();
            updateProject();
        }
    }

    private void updateProject() {
        if (myRootPath != null) {
            return;
        }
    }

    private void commitProjectRoot(String rootPath) {
        myRootPath = rootPath;
        getPreferences().edit().putString("project.root", rootPath).apply();
    }


    private String getRootPath(String filePath) {
        if (!FileSystem.isJarFileEntry(filePath)) {
            while (filePath != null && !FileSystem.isRoot(filePath)) {
                if (isProjectRoot(filePath)) {
                    return filePath;
                }
                filePath = FileSystem.getParentDirPath(filePath);
            }
        }
        return null;
    }

    public String getRootPath() {
        return myRootPath;
    }


    public boolean isOpened() {
        return myRootPath != null;
    }

    public boolean isProjectRoot(String filePath) {
        return ApkToolProject.isProjectRoot(filePath);
    }

    public void setAndroidJar(String androidJarPath) {
        if (FileSystem.exists(androidJarPath)) {
            getPreferences().edit().putString("android.sdk", androidJarPath).apply();

        }
    }

    public String getAndroidJarPath() {
        return myAndroidJar;
    }

    public SharedPreferences getPreferences() {
        if (myPreferences == null)
            myPreferences = App.getPreferences("ProjectService");
        return myPreferences;
    }
}
