package com.apkide.language.api;

import androidx.annotation.NonNull;

/**
 * 代码重构接口
 */
public interface CodeRefactor {
    
    /**
     * 准备重命名
     *
     * @param filePath 文件路径
     * @param line     标识符所在行
     * @param column   标识符所在列
     * @param newName  新的标识符名称
     * @param callback 重构回调
     */
    void prepareRename(@NonNull String filePath, int line, int column,
                       @NonNull String newName,
                       @NonNull CodeRefactorCallback callback);
    
    /**
     * 重命名
     *
     * @param filePath 文件路径
     * @param line     标识符所在行
     * @param column   标识符所在列
     * @param newName  新的标识符名称
     * @param callback 重构回调
     */
    void rename(@NonNull String filePath, int line, int column,
                @NonNull String newName,
                @NonNull CodeRefactorCallback callback);
    
    /**
     * 准备内联方法或变量
     *
     * @param filePath 文件路径
     * @param line     标识符所在行
     * @param column   标识符所在列
     * @param callback 重构回调
     */
    void prepareInline(@NonNull String filePath, int line, int column,
                       @NonNull CodeRefactorCallback callback);
    
    /**
     * 内联
     *
     * @param filePath 文件路径
     * @param line     标识符所在行
     * @param column   标识符所在列
     * @param callback 重构回调
     */
    void inline(@NonNull String filePath, int line, int column,
                @NonNull CodeRefactorCallback callback);
    
    /**
     * 注释
     *
     * @param filePath    文件路径
     * @param startLine   开始行
     * @param startColumn 开始行所在列
     * @param endLine     结束行
     * @param endColumn   结束行所在列
     * @param callback    重构回调
     */
    void commentLines(@NonNull String filePath, int startLine, int startColumn,
                      int endLine, int endColumn,
                      @NonNull CodeRefactorCallback callback);
    
    /**
     * 移除注释
     *
     * @param filePath    文件路径
     * @param startLine   开始行
     * @param startColumn 开始行所在列
     * @param endLine     结束行
     * @param endColumn   结束行所在列
     * @param callback    重构回调
     */
    void removeCommentLines(@NonNull String filePath, int startLine, int startColumn,
                            int endLine, int endColumn,
                            @NonNull CodeRefactorCallback callback);
    
    /**
     * 安全删除标识符
     *
     * @param filePath 文件路径
     * @param line     标识符所在行
     * @param column   标识符所在列
     * @param callback 重构回调
     */
    void safeDelete(@NonNull String filePath, int line, int column,
                    @NonNull CodeRefactorCallback callback);
    
    /**
     * 生成环绕（try...catch
     *
     * @param filePath 文件路径
     * @param line     行
     * @param column   列
     * @param callback 重构回调
     */
    void surroundWith(@NonNull String filePath, int line, int column,
                      @NonNull CodeRefactorCallback callback);
}
