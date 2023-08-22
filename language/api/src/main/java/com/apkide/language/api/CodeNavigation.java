package com.apkide.language.api;

import androidx.annotation.NonNull;

/**
 * 代码导航接口
 */
public interface CodeNavigation {
    
    /**
     * 查找用法
     *
     * @param filePath           文件路径
     * @param line               标识符所在行
     * @param column             标识符所在列
     * @param callback           导航回调
     */
    void searchUsages(@NonNull String filePath, int line, int column,
                      @NonNull CodeNavigationCallback callback);
    
    /**
     * 查找符号
     *
     * @param filePath           文件路径
     * @param line               标识符所在行
     * @param column             标识符所在列
     * @param includeDeclaration 包含弃用
     * @param callback           导航回调
     */
    void searchSymbol(@NonNull String filePath, int line, int column, boolean includeDeclaration,
                      @NonNull CodeNavigationCallback callback);
    
}
