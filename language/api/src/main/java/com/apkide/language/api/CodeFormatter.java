package com.apkide.language.api;

import androidx.annotation.NonNull;

/**
 * 格式化接口
 */
public interface CodeFormatter {
    
    
    /**
     * 缩进
     *
     * @param filePath    文件路径
     * @param startLine   开始行
     * @param startColumn 开始行所在列
     * @param endLine     结束行
     * @param endColumn   结束行所在列
     * @param callback    格式化回调
     */
    void indentLines(@NonNull String filePath, int startLine, int startColumn,
                     int endLine, int endColumn,
                     @NonNull CodeFormatterCallback callback);
    
    /**
     * 格式化
     *
     * @param filePath    文件路径
     * @param startLine   开始行
     * @param startColumn 开始行所在列
     * @param endLine     结束行
     * @param endColumn   结束行所在列
     * @param callback    格式化回调
     */
    void formatLines(@NonNull String filePath, int startLine, int startColumn,
                     int endLine, int endColumn,
                     @NonNull CodeFormatterCallback callback);
    
}
