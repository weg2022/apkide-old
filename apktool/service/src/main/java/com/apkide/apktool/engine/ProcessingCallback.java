package com.apkide.apktool.engine;

import androidx.annotation.NonNull;

/**
 * 处理回调
 */
public interface ProcessingCallback {
    
    /**
     * 准备处理
     *
     * @param sourcePath 输入源路径
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
     * @param outputPath 输出路径
     */
    void processDone(@NonNull String outputPath);
    
}
