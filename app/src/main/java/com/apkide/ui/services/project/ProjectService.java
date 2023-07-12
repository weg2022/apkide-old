package com.apkide.ui.services.project;

import android.content.SharedPreferences;

import com.apkide.common.ApplicationProvider;
import com.apkide.ui.App;
import com.apkide.ui.FileSystem;

public class ProjectService {

    private SharedPreferences myPreferences;
    private String myAndroidJar;

    public ProjectService() {

    }

    public void init(String filePath) {
        myAndroidJar= getPreferences().getString("currentAndroidJar", null);
        if (myAndroidJar != null && !FileSystem.exists(myAndroidJar))
            myAndroidJar = null;

        if (myAndroidJar == null)
            myAndroidJar = ApplicationProvider.get().foundFile("android.jar").getAbsolutePath();


    }

    public void openProject(String rootPath){

    }


    public SharedPreferences getPreferences() {
        if (myPreferences == null)
            myPreferences = App.getPreferences("ProjectService");
        return myPreferences;
    }
}
