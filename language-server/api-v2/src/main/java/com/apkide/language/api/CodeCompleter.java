package com.apkide.language.api;

import androidx.annotation.NonNull;


/**
 * 代码补全接口
 */
public interface CodeCompleter {
    
    /**
     * 请求补全
     *
     * @param filePath   文件路径
     * @param line       标识符所在行
     * @param column     标识符所在列
     * @param allowTypes 包括类型
     * @param callback   补全回调
     */
    void completion(@NonNull String filePath, int line, int column, boolean allowTypes,
                    @NonNull CodeCompleterCallback callback);
    
}
