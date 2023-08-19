package com.apkide.apktool.engine;

import androidx.annotation.NonNull;

import com.apkide.apktool.androlib.AaptInvoker;
import com.apkide.apktool.androlib.ApkBuilder;
import com.apkide.apktool.androlib.ApkDecoder;
import com.apkide.apktool.androlib.Config;
import com.apkide.apktool.androlib.exceptions.AndrolibException;
import com.apkide.apktool.androlib.res.Framework;
import com.apkide.apktool.androlib.res.ResourcesDecoder;
import com.apkide.apktool.androlib.res.data.ResTable;
import com.apkide.apktool.androlib.res.decoder.ARSCDecoder;
import com.apkide.apktool.androlib.res.decoder.AXmlResourceParser;
import com.apkide.apktool.androlib.res.decoder.StringBlock;
import com.apkide.apktool.androlib.res.decoder.StyledString;
import com.apkide.apktool.androlib.res.xml.ResXmlPatcher;
import com.apkide.apktool.androlib.src.SmaliBuilder;
import com.apkide.apktool.androlib.src.SmaliDecoder;
import com.apkide.apktool.common.BrutException;
import com.apkide.apktool.directory.DirUtil;
import com.apkide.apktool.directory.DirectoryException;
import com.apkide.apktool.directory.ExtFile;
import com.apkide.common.AppLog;
import com.apkide.common.Logger;

import java.io.File;
import java.io.IOException;

public final class ApkToolEngine {
    private final Object myLock = new Object();
    private boolean myDestroyed;
    private boolean myShutdown;
    private Mode myMode = Mode.None;
    private String myApkFilePath;
    private String myDecodeDestPath;
    private String myRootPath;
    private String myOutputPath;
    private final Config myConfig = Config.getDefaultConfig();
    private ApkToolConfig myApkToolConfig;
    private ApkBuildingListener myBuildingListener;
    private ApkDecodingListener myDecodingListener;
    
    
    private enum Mode {
        None,
        Build,
        Decode
    }
    
    public ApkToolEngine() {
        
        Logger.LoggerListener buildLoggingListener = (name, level, msg) -> {
                if (myMode == Mode.Build) {
                    if (myBuildingListener != null)
                        myBuildingListener.apkBuildProgressing(level, msg);
            }
        };
        
        Logger.getLogger(ApkBuilder.class.getName()).addListener(buildLoggingListener);
        Logger.getLogger(Framework.class.getName()).addListener(buildLoggingListener);
        Logger.getLogger(ResXmlPatcher.class.getName()).addListener(buildLoggingListener);
        Logger.getLogger(AaptInvoker.class.getName()).addListener(buildLoggingListener);
        Logger.getLogger(SmaliBuilder.class.getName()).addListener(buildLoggingListener);
        Logger.getLogger(Config.class.getName()).addListener(buildLoggingListener);
        Logger.getLogger(DirUtil.class.getName()).addListener(buildLoggingListener);
        
        Logger.LoggerListener compileLoggingListener = (name, level, msg) -> {
                if (myMode == Mode.Decode) {
                    if (myDecodingListener != null)
                        myDecodingListener.apkDecodeProgressing(level, msg);
                }
        };
        
        Logger.getLogger(ApkDecoder.class.getName()).addListener(compileLoggingListener);
        Logger.getLogger(SmaliDecoder.class.getName()).addListener(compileLoggingListener);
        Logger.getLogger(Framework.class.getName()).addListener(compileLoggingListener);
        Logger.getLogger(ResTable.class.getName()).addListener(compileLoggingListener);
        Logger.getLogger(ResourcesDecoder.class.getName()).addListener(compileLoggingListener);
        Logger.getLogger(ARSCDecoder.class.getName()).addListener(compileLoggingListener);
        Logger.getLogger(AXmlResourceParser.class.getName()).addListener(compileLoggingListener);
        Logger.getLogger(StringBlock.class.getName()).addListener(compileLoggingListener);
        Logger.getLogger(StyledString.class.getName()).addListener(compileLoggingListener);
        Logger.getLogger(DirUtil.class.getName()).addListener(compileLoggingListener);
        
        
        Thread thread = new Thread(null, () -> {
            try {
                synchronized (myLock) {
                    while (!myDestroyed) {
                        if (!myShutdown) {
                            if (myApkToolConfig != null)
                                myApkToolConfig.configure(myConfig);
                            switch (myMode) {
                                case Build:
                                    if (myRootPath != null && myOutputPath != null) {
                                        if (myBuildingListener != null)
                                            myBuildingListener.apkBuildStarted(myRootPath);
                                        
                                        ApkBuilder builder = new ApkBuilder(myConfig, new ExtFile(myRootPath));
                                        File outDir = new File(myOutputPath);
                                        Throwable err = null;
                                        try {
                                            builder.build(outDir);
                                        } catch (BrutException e) {
                                            AppLog.e(e);
                                            err = e;
                                        }
                                        
                                        if (myBuildingListener != null) {
                                            if (err != null)
                                                myBuildingListener.apkBuildFailed(err);
                                            else
                                                myBuildingListener.apkBuildFinished(myOutputPath);
                                        }
                                        myRootPath = null;
                                        myDecodeDestPath = null;
                                    }
                                    break;
                                case Decode:
                                    if (myApkFilePath != null && myDecodeDestPath != null) {
                                        if (myDecodingListener != null)
                                            myDecodingListener.apkDecodeStarted(myApkFilePath);
                                        ApkDecoder decoder = new ApkDecoder(myConfig, new ExtFile(myApkFilePath));
                                        File outDir = new File(myDecodeDestPath);
                                        Throwable err = null;
                                        try {
                                            decoder.decode(outDir);
                                        } catch (AndrolibException | DirectoryException |
                                                 IOException e) {
                                            AppLog.e(e);
                                            err = e;
                                        }
                                        if (myDecodingListener != null) {
                                            if (err != null)
                                                myDecodingListener.apkDecodeFailed(err);
                                            else
                                                myDecodingListener.apkDecodeFinish(myDecodeDestPath);
                                        }
                                        myApkFilePath = null;
                                        myDecodeDestPath = null;
                                    }
                                    break;
                                default:
                                    break;
                            }
                            
                            myMode = Mode.None;
                            myLock.wait();
                        }
                    }
                }
            } catch (InterruptedException ignored) {
            
            }
        }, "APK-Tool");
        thread.setPriority(Thread.MIN_PRIORITY + 2);
        thread.start();
    }
    
    
    public void configureConfig(@NonNull ApkToolConfig config) {
        synchronized (myLock) {
            myApkToolConfig = config;
        }
    }
    
    public void decode(@NonNull String apkFilePath, @NonNull String outputPath) {
        synchronized (myLock) {
            myApkFilePath = apkFilePath;
            myDecodeDestPath = outputPath;
            myMode = Mode.Decode;
            myLock.notify();
        }
    }
    
    public void build(@NonNull String rootPath, @NonNull String outputPath) {
        synchronized (myLock) {
            myRootPath = rootPath;
            myOutputPath = outputPath;
            myMode = Mode.Build;
            myLock.notify();
        }
    }
    
    public void setApkBuildingListener(@NonNull ApkBuildingListener listener) {
        synchronized (myLock) {
            myBuildingListener = listener;
        }
    }
    
    public void setApkDecodingListener(@NonNull ApkDecodingListener listener) {
        synchronized (myLock) {
            myDecodingListener = listener;
        }
    }
    
    public void restart() {
        synchronized (myLock) {
            myShutdown = false;
        }
    }
    
    public void shutdown() {
        synchronized (myLock) {
            myShutdown = false;
        }
    }
    
    public void destroy() {
        synchronized (myLock) {
            myDestroyed = true;
        }
    }
    
    
}
