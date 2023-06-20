package com.apkide.ui;

import static com.apkide.common.IOUtils.copyBytes;
import static com.apkide.common.IOUtils.safeClose;
import static java.io.File.separator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.apkide.common.FileUtils;
import com.apkide.common.SafeRunner;
import com.apkide.ui.services.build.BuildService;
import com.apkide.ui.services.file.FileSystem;
import com.apkide.ui.services.file.OpenFileService;
import com.apkide.ui.services.project.ProjectService;
import com.apkide.ui.services.scm.GitService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import brut.util.AssetsProvider;
import brut.util.OSDetection;

@SuppressLint("StaticFieldLeak")
public final class App {


    private static Context context;
    private static App app;
    private static Handler handler;
    private static MainUI mainUI;

    private final OpenFileService openFileService = new OpenFileService();
    private final ProjectService projectService = new ProjectService();
    private final BuildService buildService = new BuildService();
    private final GitService gitService = new GitService();

    private App() {
    }

    public static void initApp(Context context) {
        App.context = context;
        AssetsProvider.set(new AssetsProvider() {


            @Override
            public File foundBinary(String binaryName) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    return new File(context.getApplicationInfo().nativeLibraryDir
                            + separator + "lib" + binaryName + ".so");
                }
                String targetName = FileUtils.getFileName(binaryName);
                String arch = "";
                if (OSDetection.isAarch64())
                    arch = "arm64";
                else if (OSDetection.isAarch32())
                    arch = "arm-pie";
                else if (OSDetection.isArm())
                    arch = "arm";
                else if (OSDetection.isX86_64())
                    arch = "x86-pie";
                else if (OSDetection.isX86())
                    arch = "x86";

                File targetFile = new File(context.getFilesDir(), targetName);
                if (targetFile.exists())
                    return FileUtils.setExecutable(targetFile);

                String fullFileName = "bin" + separator + arch + separator + binaryName;

                SafeRunner.run(() -> {
                    FileSystem.createFile(targetFile.getAbsolutePath());
                    InputStream inputStream = context.getAssets().open(fullFileName);
                    FileOutputStream outputStream = new FileOutputStream(targetFile);
                    copyBytes(inputStream, outputStream);
                    safeClose(inputStream, outputStream);
                });

                return FileUtils.setExecutable(targetFile);
            }

            @Override
            public File foundFile(String fileName) {
                String targetName = FileUtils.getFileName(fileName);
                File targetFile = new File(context.getExternalFilesDir(null), targetName);
                if (targetFile.exists()) {
                    return targetFile;
                }

                SafeRunner.run(() -> {
                    FileSystem.createFile(targetFile.getAbsolutePath());
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
        app = new App();
        App.mainUI = mainUI;
        App.handler = new Handler();
        initServices();
    }

    private static void initServices() {
        app.projectService.init();
    }

    public static void shutdown() {
        if (app != null) {


            app = null;
        }
    }

    public static OpenFileService getOpenFileService() {
        return app.openFileService;
    }

    public static ProjectService getProjectService() {
        return app.projectService;
    }

    public static BuildService getBuildService() {
        return app.buildService;
    }

    public static GitService getGitService() {
        return app.gitService;
    }

    public static boolean isShutdown() {
        return app != null;
    }


}
