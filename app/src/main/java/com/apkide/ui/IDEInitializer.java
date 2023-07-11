package com.apkide.ui;

import static com.apkide.common.IOUtils.copyBytes;
import static com.apkide.common.IOUtils.safeClose;
import static java.io.File.separator;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.AppLog;
import com.apkide.common.ApplicationProvider;
import com.apkide.common.FileUtils;
import com.apkide.common.SafeRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;

import brut.util.OSDetection;

@Keep
public class IDEInitializer extends ContentProvider {
    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    @Override
    public boolean onCreate() {
        sContext = getContext();
        init();
        return true;
    }

    private void init() {
        ApplicationProvider.set(new ApplicationProvider() {

            @Override
            public File foundBinary(String binaryName) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    String binaryPath = sContext.getApplicationInfo().nativeLibraryDir
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
                else if (OSDetection.isX86_64() || OSDetection.isX86())
                    arch = "x86";

                File targetFile = new File(sContext.getFilesDir(), targetName);
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
                    InputStream inputStream = sContext.getAssets().open(fullFileName);
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
                File targetFile = new File(sContext.getFilesDir(), targetName);
                if (targetFile.exists()) {
                    AppLog.s(String.format("%s file found.", targetFile.getAbsolutePath()));
                    return targetFile;
                }

                AppLog.s(String.format("Extract file %s to %s from assets.", fileName, targetFile.getAbsolutePath()));
                SafeRunner.run(() -> {
                    InputStream inputStream = sContext.getAssets().open(fileName);
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
                return sContext.getExternalFilesDir(".temp");
            }

            @Override
            public Context getContext() {
                return sContext;
            }
        });

        AppPreferences.init(sContext);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
