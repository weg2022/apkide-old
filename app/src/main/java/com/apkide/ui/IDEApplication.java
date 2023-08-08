package com.apkide.ui;

import static com.apkide.common.io.IOUtils.copyBytes;
import static com.apkide.common.io.IOUtils.safeClose;
import static java.io.File.separator;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import com.apkide.common.FileSystem;
import com.apkide.common.SafeRunner;
import com.apkide.common.app.AppLog;
import com.apkide.common.app.Application;
import com.apkide.common.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;


public class IDEApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Application.set(new Application() {

            @NonNull
            @Override
            public File foundBinary(@NonNull String binaryName) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    String binaryPath = getContext().getApplicationInfo().nativeLibraryDir
                            + separator + "lib" + binaryName + ".so";
                    AppLog.s(String.format("%s binary file found.", binaryPath));
                    return new File(binaryPath);
                }
                String targetName = FileUtils.getFileName(binaryName);
                String arch = "";
                String[] abis=Build.SUPPORTED_ABIS;
                label:
                for (String abi : abis) {
                    switch (abi) {
                        case "armeabi-v7a":
                            arch = "arm";
                            break label;
                        case "arm64-v8a":
                            arch = "arm64";
                            break label;
                        case "x86":
                        case "x86_64":
                            arch = "x86";
                            break label;
                    }
                }

                File targetFile = new File(getContext().getFilesDir(), targetName);
                if (TextUtils.isEmpty(arch)) {
                    AppLog.e(String.format("Unsupported processor architecture %s.",
                            Arrays.toString(abis)));
                    return targetFile;
                }

                if (targetFile.exists()) {
                    AppLog.s(String.format("%s binary file found.", targetFile.getAbsolutePath()));
                    return FileUtils.setExecutable(targetFile);
                }

                String fullFileName = "bin" + separator + arch + separator + binaryName;

                AppLog.s(String.format("Extract binary file %s to %s from assets.", fullFileName, targetFile.getAbsolutePath()));
                SafeRunner.run(() -> {
                    FileSystem.createFile(targetFile.getAbsolutePath());
                    InputStream inputStream = getContext().getAssets().open(fullFileName);
                    FileOutputStream outputStream = new FileOutputStream(targetFile);
                    copyBytes(inputStream, outputStream);
                    safeClose(inputStream, outputStream);
                });

                AppLog.s(String.format("%s binary found.", targetFile.getAbsolutePath()));
                return FileUtils.setExecutable(targetFile);
            }

            @NonNull
            @Override
            public File foundFile(@NonNull String fileName) {
                String targetName = FileUtils.getFileName(fileName);
                File targetFile = new File(getContext().getFilesDir(), targetName);
                if (targetFile.exists()) {
                    AppLog.s(String.format("%s file found.", targetFile.getAbsolutePath()));
                    return targetFile;
                }

                AppLog.s(String.format("Extract file %s to %s.",
                        fileName, targetFile.getAbsolutePath()));
                SafeRunner.run(() -> {
                    InputStream inputStream = getContext().getAssets().open(fileName);
                    if (inputStream == null)
                        inputStream = this.getClass().getResourceAsStream(fileName);
                    if (inputStream != null) {
                        FileOutputStream outputStream = new FileOutputStream(targetFile);
                        copyBytes(inputStream, outputStream);
                        safeClose(inputStream, outputStream);
                    }
                });

                return targetFile;
            }

            @NonNull
            @Override
            public File getTempDirectory() {
                return getApplicationContext().getExternalFilesDir(".temp");
            }

            @NonNull
            @Override
            public Context getContext() {
                return getApplicationContext();
            }
        });

        AppPreferences.init(getApplicationContext());
    }
}
