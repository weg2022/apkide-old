package com.apkide.ui;

import static com.apkide.common.IOUtils.copyBytes;
import static com.apkide.common.IOUtils.safeClose;
import static java.io.File.separator;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import com.apkide.common.FileUtils;
import com.apkide.common.SafeRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import brut.util.AssetsProvider;
import brut.util.OSDetection;

public class IDEApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        AppPreferences.init(getApplicationContext());
        App.initApp(getApplicationContext());
        AssetsProvider.set(new AssetsProvider() {
            @Override
            public File foundBinary(String binaryName) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    return new File(getApplicationContext().getApplicationInfo().nativeLibraryDir
                            + separator + "lib" + binaryName + ".so");
                }
                String targetName = FileUtils.getFileName(binaryName);
                String arch = "";
                if (OSDetection.isAarch64())
                    arch = "aarch64";
                else if (OSDetection.isAarch32())
                    arch = "arm";
                else if (OSDetection.isX86_64())
                    arch = "x86-pie";
                else if (OSDetection.isX86())
                    arch = "x86";

                File targetFile = new File(getApplicationContext().getFilesDir(), targetName);
                if (targetFile.exists()) {
                    if (!targetFile.canExecute())
                        targetFile.setExecutable(true);
                    return targetFile;
                }

                String fullFileName = "bin" + separator + arch + separator + binaryName;

                SafeRunner.run(new SafeRunner.SafeRunnerCallback() {
                    @Override
                    public void handleException(@NonNull Throwable e) {
                        throw new RuntimeException(e);
                    }

                    @Override
                    public void run() throws Exception {
                        targetFile.createNewFile();
                        InputStream inputStream = getApplicationContext().getAssets().open(fullFileName);
                        FileOutputStream outputStream = new FileOutputStream(targetFile);
                        copyBytes(inputStream, outputStream);
                        safeClose(inputStream, outputStream);
                    }
                });

                if (!targetFile.canExecute())
                    targetFile.setExecutable(true);
                return targetFile;
            }

            @Override
            public File foundFile(String fileName) {
                String targetName = FileUtils.getFileName(fileName);
                File targetFile = new File(getApplicationContext().getExternalFilesDir(null), targetName);
                if (targetFile.exists()) {
                    //TODO: check size of file
                    return targetFile;
                }

                SafeRunner.run(new SafeRunner.SafeRunnerCallback() {
                    @Override
                    public void handleException(@NonNull Throwable e) {
                        throw new RuntimeException(e);
                    }

                    @Override
                    public void run() throws Exception {
                        targetFile.createNewFile();
                        InputStream inputStream = getApplicationContext().getAssets().open(fileName);
                        FileOutputStream outputStream = new FileOutputStream(targetFile);
                        copyBytes(inputStream, outputStream);
                        safeClose(inputStream, outputStream);
                    }
                });

                return targetFile;
            }

            @Override
            public File foundAndroidFrameworkFile() {
                return foundFile("android.jar");
            }

            @Override
            public File getTempDirectory() {
                return getApplicationContext().getExternalFilesDir(".temp");
            }

            @Override
            public String getString(int resId) {
                return getApplicationContext().getString(resId);
            }
        });

    }
}
