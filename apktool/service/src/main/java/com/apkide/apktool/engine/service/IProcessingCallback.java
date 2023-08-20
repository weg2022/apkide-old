package com.apkide.apktool.engine.service;

import androidx.annotation.NonNull;

import cn.thens.okbinder2.AIDL;

/**
 * 处理回调
 */
@AIDL
public interface IProcessingCallback {
    
    /**
     * 准备处理
     *
     * @param sourcePath 源文件路径
     */
    void processPrepare(@NonNull String sourcePath);
    
    /**
     * 处理中
     *
     * @param msg 处理输出信息
     */
    void processing(@NonNull String msg);
    
    /**
     * 处理错误
     *
     * @param error 错误
     */
    void processError(@NonNull Throwable error);
    
    /**
     * 处理完成
     *
     * @param outputPath 输出文件路径
     */
    void processDone(@NonNull String outputPath);
    
}
