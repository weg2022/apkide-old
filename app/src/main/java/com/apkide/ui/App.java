package com.apkide.ui;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.apkide.common.Application;
import com.apkide.ui.browsers.file.FileBrowserService;
import com.apkide.ui.services.CodeEngineService;
import com.apkide.ui.services.apktool.ApkEngineService;
import com.apkide.ui.services.openfile.OpenFileService;
import com.apkide.ui.services.project.ProjectService;

import java.util.ArrayList;
import java.util.List;

public final class App {
    
    private static final List<StyledUI> sActivities = new ArrayList<>();
    private static App sApp;
    private static Handler sHandler;
    private static MainUI sMainUI;
    
    private final FileBrowserService myFileBrowserService = new FileBrowserService();
    private final OpenFileService myOpenFileService = new OpenFileService();
    private final ProjectService myProjectService = new ProjectService();
    private final CodeEngineService myCodeEngineService = new CodeEngineService();
    
    private final ApkEngineService myApkEngineService = new ApkEngineService();
    
    private App() {
    }
    
    public static ApkEngineService getApkEngineService() {
        return sApp.myApkEngineService;
    }
    
    public static CodeEngineService getCodeEngineService() {
        return sApp.myCodeEngineService;
    }
    
    public static FileBrowserService getFileBrowserService() {
        return sApp.myFileBrowserService;
    }
    
    public static OpenFileService getOpenFileService() {
        return sApp.myOpenFileService;
    }
    
    public static ProjectService getProjectService() {
        return sApp.myProjectService;
    }
    
    public static void init(@NonNull MainUI mainUI) {
        sApp = new App();
        sHandler = new Handler(requireNonNull(Looper.myLooper()));
        sMainUI = mainUI;
        sApp.myApkEngineService.initialize();
        sApp.myCodeEngineService.initialize();
        sApp.myFileBrowserService.initialize();
        sApp.myProjectService.initialize();
        sApp.myOpenFileService.initialize();
    }
    
    
    public static void shutdown() {
        if (sApp != null) {
            
            sApp.myOpenFileService.shutdown();
            sApp.myFileBrowserService.shutdown();
            sApp.myProjectService.shutdown();
            sApp.myCodeEngineService.shutdown();
            sApp.myApkEngineService.shutdown();
            sActivities.clear();
            sMainUI = null;
            sHandler = null;
            sApp = null;
        }
    }
    
    public static boolean isShutdown() {
        return sApp == null;
    }
    
    public static boolean postRun(@NonNull Runnable runnable) {
        return Application.get().postExec(runnable);
    }
    
    public static boolean postRun(@NonNull Runnable runnable, long delayMillis) {
        return Application.get().postExec(runnable, delayMillis);
    }
    
    public static MainUI getMainUI() {
        return sMainUI;
    }
    
    @NonNull
    public static StyledUI getUI() {
        return sActivities.isEmpty() ? sMainUI : sActivities.get(sActivities.size() - 1);
    }
    
    public static void startUI(@NonNull StyledUI ui) {
        int index = sActivities.indexOf(ui);
        if (index != -1)
            sActivities.remove(index);
        
        sActivities.add(ui);
    }
    
    public static void stopUI(@NonNull StyledUI ui) {
        sActivities.remove(ui);
    }
    
    public static void runOnBackground(@NonNull Runnable backgroundRun) {
        Application.get().syncExec(backgroundRun);
    }
    
    public static void runOnBackground(@NonNull Runnable backgroundRun, @Nullable Runnable uiRun) {
        Application.get().syncExec(backgroundRun, uiRun);
    }
    
    @NonNull
    public static Context getContext() {
        if (sMainUI != null) {
            return sMainUI;
        }
        if (sActivities.isEmpty()) {
            return Application.get().getContext();
        }
        return sActivities.get(sActivities.size() - 1);
    }
    
    
    @NonNull
    public static String getString(@StringRes int id) {
        return getContext().getString(id);
    }
    
    @NonNull
    public static SharedPreferences getPreferences(@NonNull String name) {
        return getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
    }
    
}
