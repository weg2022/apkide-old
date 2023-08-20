package com.apkide.apktool.engine.service;

import androidx.annotation.NonNull;

import com.apkide.apktool.engine.ApkToolConfig;

import cn.thens.okbinder2.AIDL;

/**
 * ApkTool 服务接口 {@link ApkToolService}
 */
@AIDL
public interface IApkToolService {
    
    /**
     * 更新配置
     *
     * @param config 配置
     */
    void configure(@NonNull ApkToolConfig config);
    
    /**
     * 构建
     *
     * @param rootPath   项目根路径
     * @param outputPath 输出文件路径
     * @param callback   处理回调
     */
    void build(@NonNull String rootPath, @NonNull String outputPath,@NonNull IProcessingCallback callback);
    
    /**
     * 反编译
     *
     * @param apkFilePath apk 文件路径
     * @param outputPath  输出文件路径
     * @param callback    处理回调
     */
    void decode(@NonNull String apkFilePath, @NonNull String outputPath, @NonNull IProcessingCallback callback);
    
    /**
     * 重启
     */
    void restart();
    
    /**
     * 关闭
     */
    void shutdown();
    
}
