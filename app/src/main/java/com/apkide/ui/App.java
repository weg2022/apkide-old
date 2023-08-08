package com.apkide.ui;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.apkide.common.app.Application;
import com.apkide.ui.browsers.file.FileBrowserService;
import com.apkide.ui.services.file.OpenFileService;
import com.apkide.ui.services.keybinding.KeyBindingService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public final class App {

    private static final List<StyledUI> sActivities = new ArrayList<>();
    private static App sApp;
    private static Handler sHandler;
    private static MainUI sMainUI;

    private final FileBrowserService myFileBrowserService=new FileBrowserService();
    private final OpenFileService myOpenFileService=new OpenFileService();
    private final KeyBindingService myKeyBindingService=new KeyBindingService();
    private App() {

    }

    public static void initialize(MainUI mainUI) {
        sApp = new App();
        sHandler = new Handler(requireNonNull(Looper.getMainLooper()));
        sMainUI = mainUI;
        sApp.myOpenFileService.init();
    }


    public static void shutdown() {
        if (sApp != null) {

            sApp = null;
        }
    }

    public static boolean isShutdown() {
        return sApp == null;
    }


    public static FileBrowserService getFileBrowserService(){
        return sApp.myFileBrowserService;
    }

    public static OpenFileService getOpenFileService(){
        return sApp.myOpenFileService;
    }

    public static KeyBindingService getKeyBindingService(){
        return sApp.myKeyBindingService;
    }


    public static boolean runOnUIThread(@NonNull Runnable runnable) {
        return sHandler.post(() -> {
            if (isShutdown())
                return;
            runnable.run();
        });
    }

    public static MainUI getMainUI() {
        return sMainUI;
    }

    public static StyledUI getUI() {
        return sActivities.isEmpty() ? sMainUI : sActivities.get(sActivities.size() - 1);
    }

    public static void startUI(StyledUI ui) {
        int index = sActivities.indexOf(ui);
        if (index != -1)
            sActivities.remove(index);

        sActivities.add(ui);
    }

    public static void stopUI(StyledUI ui) {
        sActivities.remove(ui);
    }

    public static void runOnBackgroundThread(@NonNull Runnable backgroundRun, Runnable uiRun) {
        new Thread(() -> {
            backgroundRun.run();
            runOnUIThread(() -> {
                if (uiRun != null)
                    uiRun.run();
            });
        }).start();
    }

    public static SharedPreferences getPreferences(String name) {
        return getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static Context getContext() {
        return Application.get().getContext();
    }

    public static File getHomeDir(){
        return getContext().getExternalFilesDir(null).getParentFile();
    }
}
