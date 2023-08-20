package com.apkide.ui;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;
import static com.apkide.common.IoUtils.copyAllBytes;
import static com.apkide.common.IoUtils.safeClose;
import static java.io.File.separator;
import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import com.apkide.codeanalysis.LanguageServerProvider;
import com.apkide.common.AppLog;
import com.apkide.common.Application;
import com.apkide.common.FileSystem;
import com.apkide.common.FileUtils;
import com.apkide.common.SafeRunner;
import com.apkide.ls.api.LanguageServer;
import com.apkide.ls.java.JavaLanguageServer;
import com.apkide.ls.smali.SmaliLanguageServer;
import com.apkide.ls.xml.XmlLanguageServer;
import com.apkide.ui.util.JarFileArchiveReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class IDEApplication extends MultiDexApplication {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        Application.set(new Application() {
            private SharedPreferences myPreferences;
            
            private SharedPreferences getPreferences() {
                if (myPreferences == null) {
                    myPreferences = getContext().getSharedPreferences("IDEApp", Context.MODE_PRIVATE);
                }
                return myPreferences;
            }
            
            private String[] getSupportedABI() {
                String[] list = Build.SUPPORTED_ABIS;
                for (String abi : list) {
                    switch (abi) {
                        case "armeabi":
                            return new String[]{"arm"};
                        case "arm64-v8a":
                            return new String[]{"arm64", "arm-pie", "arm"};
                        case "armeabi-v7a":
                            return new String[]{"arm-pie", "arm"};
                        case "x86":
                            return new String[]{"x86", "arm"};
                        case "x86_64":
                            return new String[]{"x86-pie", "x86", "arm"};
                    }
                }
                return new String[0];
            }
            
            @NonNull
            @Override
            public File foundBinary(@NonNull String binaryName) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    String binaryPath = getContext().getApplicationInfo().nativeLibraryDir
                            + separator + "lib" + binaryName + ".so";
                    return new File(binaryPath);
                }
                String targetName = FileUtils.getFileName(binaryName);
                String[] arch = getSupportedABI();
                File targetFile = new File(getDataDir(), targetName);
                if (arch.length == 0) {
                    AppLog.e(String.format("Unsupported processor architecture %s.",
                            Arrays.toString(Build.SUPPORTED_ABIS)));
                    return targetFile;
                }
                
                if (targetFile.exists() &&
                        getPreferences().getLong(binaryName, -1)
                                == targetFile.length()) {
                    return FileUtils.setExecutable(targetFile);
                }
                
                
                final long[] version = {-1};
                final InputStream[] inputStreams = new InputStream[1];
                
                for (String a : arch) {
                    String fullFileName = "bin" + separator + a + separator + binaryName;
                    try {
                        InputStream inputStream = getContext().getAssets().open(fullFileName);
                        inputStreams[0] = inputStream;
                    } catch (IOException ignored) {
                    
                    }
                    
                    if (inputStreams[0] != null)
                        break;
                }
                
                if (inputStreams[0] != null) {
                    SafeRunner.run(() -> {
                        InputStream inputStream = inputStreams[0];
                        FileOutputStream outputStream = new FileOutputStream(targetFile);
                        version[0] = inputStream.available();
                        copyAllBytes(inputStream, outputStream);
                        safeClose(inputStream, outputStream);
                    });
                }
                
                getPreferences().edit().putLong(binaryName, version[0]).apply();
                return FileUtils.setExecutable(targetFile);
            }
            
            @NonNull
            @Override
            public File foundFile(@NonNull String fileName) {
                File targetFile = new File(getDataDir(), fileName);
                if (targetFile.exists() &&
                        getPreferences().getLong(fileName, -1)
                                == targetFile.length()) {
                    return targetFile;
                }
                
                long[] version = new long[]{-1};
                SafeRunner.run(() -> {
                    InputStream inputStream = getContext().getAssets().open(fileName);
                    FileOutputStream outputStream = new FileOutputStream(targetFile);
                    version[0] = inputStream.available();
                    copyAllBytes(inputStream, outputStream);
                    safeClose(inputStream, outputStream);
                });
                
                getPreferences().edit().putLong(fileName, version[0]).apply();
                return targetFile;
            }
            
            @NonNull
            @Override
            public File getDataDir() {
                return getContext().getFilesDir();
            }
            
            @NonNull
            @Override
            public File getCacheDir() {
                return requireNonNull(getContext().getExternalFilesDir(".cache"));
            }
            
            @NonNull
            @Override
            public File getTempDir() {
                return requireNonNull(getContext().getExternalFilesDir(".temp"));
            }
            
            @NonNull
            @Override
            public File getExternalStorageDir() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    return Environment.getExternalStorageDirectory();
                } else {
                    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                }
            }
            
            @NonNull
            @Override
            public Context getContext() {
                return getApplicationContext();
            }
            
            private final Handler myHandler = new Handler(requireNonNull(Looper.myLooper()));
            private final ExecutorService myService = Executors.newFixedThreadPool(8);
            
            @Override
            public boolean postExec(@NonNull Runnable runnable, long delayMillis) {
                if (delayMillis <= 0) {
                    return myHandler.post(runnable);
                }
                return myHandler.postDelayed(runnable, delayMillis);
            }
            
            @Override
            public void syncExec(@NonNull Runnable workRun, @Nullable Runnable doneRun) {
                myService.execute(() -> {
                    workRun.run();
                    if (doneRun != null)
                        postExec(doneRun);
                });
            }
        });
        
        AppPreferences.initialize(getApplicationContext());
        if (AppPreferences.isFollowSystemTheme()) {
            if (AppCompatDelegate.getDefaultNightMode() != MODE_NIGHT_FOLLOW_SYSTEM)
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM);
            else
                setDefaultNightMode(AppPreferences.isNightTheme() ? MODE_NIGHT_YES : MODE_NIGHT_NO);
        } else
            setDefaultNightMode(AppPreferences.isNightTheme() ? MODE_NIGHT_YES : MODE_NIGHT_NO);
        
        FileSystem.setArchiveReaders(new FileSystem.FileArchiveReader[]{
                new JarFileArchiveReader(),
                //TODO: zip & apk support
        });
        
        LanguageServerProvider.set(new LanguageServerProvider() {
            @NonNull
            @Override
            public LanguageServer[] getLanguageServers() {
                return new LanguageServer[]{
                        new SmaliLanguageServer(),
                        new JavaLanguageServer(),
                        new XmlLanguageServer(),
                };
            }
        });
    }
}
