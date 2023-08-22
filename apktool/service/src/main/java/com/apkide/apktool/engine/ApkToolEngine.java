package com.apkide.apktool.engine;

import androidx.annotation.NonNull;

import com.apkide.apktool.androlib.AaptInvoker;
import com.apkide.apktool.androlib.ApkBuilder;
import com.apkide.apktool.androlib.ApkDecoder;
import com.apkide.apktool.androlib.Config;
import com.apkide.apktool.androlib.exceptions.AndrolibException;
import com.apkide.apktool.androlib.res.Framework;
import com.apkide.apktool.androlib.res.ResourcesDecoder;
import com.apkide.apktool.androlib.res.decoder.ARSCDecoder;
import com.apkide.apktool.common.BrutException;
import com.apkide.apktool.directory.DirUtil;
import com.apkide.apktool.directory.DirectoryException;
import com.apkide.apktool.directory.ExtFile;
import com.apkide.apktool.util.OS;
import com.apkide.common.AppLog;
import com.apkide.common.logger.Logger;
import com.apkide.common.logger.LoggerListener;

import java.io.File;
import java.io.IOException;

/**
 * ApkTool 引擎
 */
public final class ApkToolEngine {
    private final Object myLock = new Object();
    private boolean myDestroyed;
    private boolean myShutdown;
    private Mode myMode = Mode.None;
    private final Config myConfig = Config.getDefaultConfig();
    private ApkToolConfig myApkToolConfig;
    
    private String mySourcePath;
    private String myOutputPath;
    private ProcessingCallback myCallback;
    
    private enum Mode {
        /**
         * 无操作模式
         */
        None,
        /**
         * 构建模式
         */
        Build,
        /**
         * 反编译模式
         */
        Decode
    }
    
    public ApkToolEngine() {
        LoggerListener loggerListener = (name, level, msg) -> {
            if (myMode != Mode.None && myCallback != null) {
                myCallback.processing(level.prefix + ":" + msg);
            }
        };
        Logger.getLogger(AaptInvoker.class.getName()).addListener(loggerListener);
        Logger.getLogger(ApkBuilder.class.getName()).addListener(loggerListener);
        Logger.getLogger(ApkDecoder.class.getName()).addListener(loggerListener);
        Logger.getLogger(Framework.class.getName()).addListener(loggerListener);
        Logger.getLogger(Config.class.getName()).addListener(loggerListener);
        Logger.getLogger(ResourcesDecoder.class.getName()).addListener(loggerListener);
        Logger.getLogger(ARSCDecoder.class.getName()).addListener(loggerListener);
        Logger.getLogger(DirUtil.class.getName()).addListener(loggerListener);
        Logger.getLogger(OS.class.getName()).addListener(loggerListener);
        
        Thread thread = new Thread(null, () -> {
            try {
                synchronized (myLock) {
                    while (!myDestroyed) {
                        if (!myShutdown) {
                            if (myApkToolConfig != null)
                                myApkToolConfig.configure(myConfig);
                            switch (myMode) {
                                case Build:
                                    if (mySourcePath != null && myOutputPath != null) {
                                        if (myCallback != null)
                                            myCallback.processPrepare(mySourcePath);
                                        
                                        ApkBuilder builder = new ApkBuilder(myConfig, new ExtFile(mySourcePath));
                                        File outDir = new File(myOutputPath);
                                        Throwable err = null;
                                        
                                        
                                        try {
                                            builder.build(outDir);
                                        } catch (BrutException e) {
                                            AppLog.e(e);
                                            err = e;
                                        }
                                        
                                        if (myCallback != null) {
                                            if (err != null)
                                                myCallback.processError(err);
                                            else
                                                myCallback.processDone(myOutputPath);
                                        }
                                        
                                        mySourcePath = null;
                                        myOutputPath = null;
                                    }
                                    break;
                                case Decode:
                                    if (mySourcePath != null && myOutputPath != null) {
                                        if (myCallback != null)
                                            myCallback.processPrepare(mySourcePath);
                                        ApkDecoder decoder = new ApkDecoder(myConfig, new ExtFile(mySourcePath));
                                        File outDir = new File(myOutputPath);
                                        Throwable err = null;
                                        try {
                                            decoder.decode(outDir);
                                        } catch (AndrolibException | DirectoryException |
                                                 IOException e) {
                                            AppLog.e(e);
                                            err = e;
                                        }
                                        
                                        if (myCallback != null) {
                                            if (err != null)
                                                myCallback.processError(err);
                                            else
                                                myCallback.processDone(myOutputPath);
                                        }
                                        
                                        mySourcePath = null;
                                        myOutputPath = null;
                                    }
                                    break;
                                default:
                                    break;
                            }
                            
                            myMode = Mode.None;
                        }
                        myLock.wait();
                    }
                }
            } catch (InterruptedException ignored) {
            
            }
        }, "APK-Tool");
        thread.setPriority(Thread.MIN_PRIORITY + 2);
        thread.start();
    }
    
    
    /**
     * 更新配置
     *
     * @param config 配置
     */
    public void configureConfig(@NonNull ApkToolConfig config) {
        synchronized (myLock) {
            myApkToolConfig = config;
        }
    }
    
    /**
     * 反编译
     *
     * @param apkFilePath apk 文件路径
     * @param outputPath  输出文件路径
     * @param callback    处理回调
     */
    public void decode(@NonNull String apkFilePath, @NonNull String outputPath, @NonNull ProcessingCallback callback) {
        synchronized (myLock) {
            mySourcePath = apkFilePath;
            myOutputPath = outputPath;
            myMode = Mode.Decode;
            myCallback = callback;
            myLock.notify();
        }
    }
    
    
    /**
     * 构建
     *
     * @param rootPath   项目根路径
     * @param outputPath 输出文件路径
     * @param callback   处理回调
     */
    public void build(@NonNull String rootPath, @NonNull String outputPath, @NonNull ProcessingCallback callback) {
        synchronized (myLock) {
            mySourcePath = rootPath;
            myOutputPath = outputPath;
            myMode = Mode.Build;
            myCallback = callback;
            myLock.notify();
        }
    }
    
    /**
     * 重启
     */
    public void restart() {
        synchronized (myLock) {
            myShutdown = false;
        }
    }
    
    /**
     * 关闭
     */
    public void shutdown() {
        synchronized (myLock) {
            myShutdown = false;
        }
    }
    
    /**
     * 销毁.
     */
    public void destroy() {
        synchronized (myLock) {
            myDestroyed = true;
        }
    }
    
    
}
