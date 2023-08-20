package com.apkide.language.api.editor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 用于表示编辑器模式接口
 */
public interface EditorMode {
    
    /**
     * 获取名称
     *
     * @return 名称
     */
    @NonNull
    String getName();
    
    /**
     * 获取格式化器
     *
     * @return 格式化器
     */
    @Nullable
    Formatter getFormatter();
    
    /**
     * 获取高亮器
     *
     * @return 高亮器
     */
    @Nullable
    Highlighter getHighlighter();
}
