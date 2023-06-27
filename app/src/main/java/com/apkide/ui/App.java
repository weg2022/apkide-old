package com.apkide.ui;

import static com.apkide.common.IOUtils.copyBytes;
import static com.apkide.common.IOUtils.safeClose;
import static java.io.File.separator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;

import com.apkide.common.AppLog;
import com.apkide.common.AssetsProvider;
import com.apkide.common.FileUtils;
import com.apkide.common.SafeRunner;
import com.apkide.ui.services.FileSystem;
import com.apkide.ui.services.navigate.NavigateService;
import com.apkide.ui.services.openfile.OpenFileService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;

import brut.util.OSDetection;

@SuppressLint("StaticFieldLeak")
public final class App {

    private static Context context;
    private static App app;
    private static Handler handler;
    private static MainUI mainUI;


    private final OpenFileService openFileService;
    private final NavigateService navigateService;

    private App() {
        openFileService = new OpenFileService();
        navigateService = new NavigateService();
    }

    public static void initApp(Context context) {
        App.context = context;
        AppPreferences.init(context);
        AssetsProvider.set(new AssetsProvider() {

            @Override
            public File foundBinary(String binaryName) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    String binaryPath = context.getApplicationInfo().nativeLibraryDir
                            + separator + "lib" + binaryName + ".so";
                    AppLog.s(String.format("%s binary file found.", binaryPath));
                    return new File(binaryPath);
                }
                String targetName = FileUtils.getFileName(binaryName);
                String arch = "";
                if (OSDetection.isAarch64())
                    arch = "arm64";
                else if (OSDetection.isAarch32())
                    arch = "arm";
                else if (OSDetection.isX86_64())
                    arch = "x86_64";
                else if (OSDetection.isX86())
                    arch = "x86";

                File targetFile = new File(context.getFilesDir(), targetName);
                if (TextUtils.isEmpty(arch)) {
                    AppLog.e(String.format("Unsupported processor architecture %s.", Arrays.toString(OSDetection.getArchitectures())));
                    return null;
                }

                if (targetFile.exists()) {
                    AppLog.s(String.format("%s binary file found.", targetFile.getAbsolutePath()));
                    return FileUtils.setExecutable(targetFile);
                }

                String fullFileName = "bin" + separator + arch + separator + binaryName;

                AppLog.s(String.format("Extract binary file %s to %s from assets.", fullFileName, targetFile.getAbsolutePath()));
                SafeRunner.run(() -> {
                    FileSystem.createFile(targetFile.getAbsolutePath());
                    InputStream inputStream = context.getAssets().open(fullFileName);
                    FileOutputStream outputStream = new FileOutputStream(targetFile);
                    copyBytes(inputStream, outputStream);
                    safeClose(inputStream, outputStream);
                });

                AppLog.s(String.format("%s binary found.", targetFile.getAbsolutePath()));
                return FileUtils.setExecutable(targetFile);
            }

            @Override
            public File foundFile(String fileName) {
                String targetName = FileUtils.getFileName(fileName);
                File targetFile = new File(context.getExternalFilesDir(null), targetName);
                if (targetFile.exists()) {
                    AppLog.s(String.format("%s file found.", targetFile.getAbsolutePath()));
                    return targetFile;
                }

                AppLog.s(String.format("Extract file %s to %s from assets.", fileName, targetFile.getAbsolutePath()));
                SafeRunner.run(() -> {
                    // FileSystem.createFile(targetFile.getAbsolutePath());
                    InputStream inputStream = context.getAssets().open(fileName);
                    FileOutputStream outputStream = new FileOutputStream(targetFile);
                    copyBytes(inputStream, outputStream);
                    safeClose(inputStream, outputStream);
                });

                return targetFile;
            }

            @Override
            public File foundAndroidFrameworkFile() {
                return foundFile("android.jar");
            }

            @Override
            public File getTempDirectory() {
                return context.getExternalFilesDir(".temp");
            }

            @Override
            public String getString(int resId) {
                return context.getString(resId);
            }
        });
    }

    public static Context getContext() {
        return context;
    }

    public static void init(MainUI mainUI) {
        AppLog.s("App.init");
        app = new App();
        App.mainUI = mainUI;
        App.handler = new Handler();
        initServices();
    }

    private static void initServices() {

    }

    public static void shutdown() {
        if (app != null) {
            AppLog.s("App.shutdown");

            app = null;
        }
    }


    public static boolean isShutdown() {
        return app != null;
    }

    public static OpenFileService getOpenFileService() {
        return app.openFileService;
    }

    public static NavigateService getNavigateService() {
        return app.navigateService;
    }


}
