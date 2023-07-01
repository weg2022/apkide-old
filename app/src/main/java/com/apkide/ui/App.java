package com.apkide.ui;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.apkide.common.ApplicationProvider;
import com.apkide.ui.services.navigate.NavigateService;
import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;

import java.util.ArrayList;
import java.util.List;


public final class App {

    private static final List<StyledUI> sActivities = new ArrayList<>();
    private static App sApp;
    private static Handler sHandler;
    private static MainUI sMainUI;

    private final NavigateService navigateService = new NavigateService();

    private App() {

    }

    public static void initialize(MainUI mainUI) {
        sApp = new App();
        sHandler = new Handler(requireNonNull(Looper.myLooper()));
        sMainUI = mainUI;
    }


    public static void shutdown() {
        if (sApp != null) {

            sApp = null;
        }
    }

    public static boolean isShutdown() {
        return sApp == null;
    }


    public static NavigateService getNavigateService() {
        return sApp.navigateService;
    }

    public static boolean postExec(@NonNull Runnable runnable) {
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

    public static void putUI(StyledUI ui) {
        int index = sActivities.indexOf(ui);
        if (index != -1) {
            sActivities.remove(index);
        }
        sActivities.add(ui);
    }

    public static void removeUI(StyledUI ui) {
        sActivities.remove(ui);
    }

    public static void runOnBackgroundThread(@NonNull String label, @NonNull Runnable runTask, Runnable doneTask) {
        TipDialog.show(label, WaitDialog.TYPE.WARNING).setCancelable(false);
        new Thread(() -> {
            runTask.run();
            postExec(() -> {
                TipDialog.dismiss();
                if (doneTask != null)
                    doneTask.run();
            });
        }).start();
    }

    public static Context getContext() {
        return ApplicationProvider.get().getContext();
    }
}
